package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.constant.ConstantsWorkbench;
import com.platform.ems.domain.AssAssetRecord;
import com.platform.ems.domain.AssAssetRecordAttach;
import com.platform.ems.domain.BasCompany;
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.AssAssetRecordAttachMapper;
import com.platform.ems.mapper.AssAssetRecordMapper;
import com.platform.ems.mapper.BasCompanyMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.plug.domain.ConMeasureUnit;
import com.platform.ems.plug.mapper.ConMeasureUnitMapper;
import com.platform.ems.service.IAssAssetRecordService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.ExcelStyleUtil;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.workflow.domain.Submit;
import com.platform.ems.workflow.service.IWorkFlowService;
import com.platform.api.service.RemoteMenuService;
import com.platform.common.core.domain.entity.SysMenu;
import com.platform.flowable.domain.vo.FormParameter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 资产台账Service业务层处理
 *
 * @author chenkw
 * @date 2022-03-01
 */
@Service
@SuppressWarnings("all")
public class AssAssetRecordServiceImpl extends ServiceImpl<AssAssetRecordMapper, AssAssetRecord> implements IAssAssetRecordService {
    @Autowired
    private AssAssetRecordMapper assAssetRecordMapper;
    @Autowired
    private AssAssetRecordAttachMapper assAssetRecordAttachMapper;
    @Autowired
    private BasCompanyMapper companyMapper;
    @Autowired
    private IWorkFlowService workflowService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RemoteMenuService remoteMenuService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private ConMeasureUnitMapper conMeasureUnitMapper;
    private static final String TITLE = "资产台账";

    /**
     * 查询资产台账
     *
     * @param assetSid 资产台账ID
     * @return 资产台账
     */
    @Override
    public AssAssetRecord selectAssAssetRecordById(Long assetSid) {
        AssAssetRecord assAssetRecord = assAssetRecordMapper.selectAssAssetRecordById(assetSid);
        // 更新图片
        getData(assAssetRecord);
        List<AssAssetRecordAttach> attachList = assAssetRecordAttachMapper.selectAssAssetRecordAttachList(new AssAssetRecordAttach().setAssetSid(assetSid));
        assAssetRecord.setAttachmentList(attachList);
        MongodbUtil.find(assAssetRecord);
        return assAssetRecord;
    }

    /**
     * 数据字段处理
     *
     * @param prjTask 任务节点
     * @return 结果
     */
    private void setData(AssAssetRecord assAssetRecord) {
        // 图片
        String picture = null;
        if (ArrayUtil.isNotEmpty(assAssetRecord.getPicturePathList())) {
            picture = "";
            for (int i = 0; i < assAssetRecord.getPicturePathList().length; i++) {
                picture = picture + assAssetRecord.getPicturePathList()[i] + ";";
            }
        }
        assAssetRecord.setPicturePath(picture);
        // 公司
        assAssetRecord.setCompanyCode(null);
        if (assAssetRecord.getCompanySid() != null) {
            BasCompany company = companyMapper.selectById(assAssetRecord.getCompanySid());
            if (company != null) {
                assAssetRecord.setCompanyCode(company.getCompanyCode());
            }
        }
    }

    /**
     * 获取岗位名称
     *
     * @param projectTask 任务节点
     * @return 结果
     */
    public void getData(AssAssetRecord assAssetRecord) {
        if (assAssetRecord == null) {
            return;
        }
        // 图片
        if (StrUtil.isNotBlank(assAssetRecord.getPicturePath())) {
            String[] picture = assAssetRecord.getPicturePath().split(";");
            assAssetRecord.setPicturePathList(picture);
        }
    }

    /**
     * 查询资产台账列表
     *
     * @param assAssetRecord 资产台账
     * @return 资产台账
     */
    @Override
    public List<AssAssetRecord> selectAssAssetRecordList(AssAssetRecord assAssetRecord) {
        List<AssAssetRecord> list = assAssetRecordMapper.selectAssAssetRecordList(assAssetRecord);
        if (CollectionUtil.isNotEmpty(list)) {
            for (AssAssetRecord record : list) {
                // 更新图片
                getData(record);
            }
        }
        return list;
    }

    /**
     * 查询资产统计台账列表
     *
     * @param assAssetRecord 资产台账
     * @return 资产台账
     */
    @Override
    public List<AssAssetRecord> selectAssAssetStatisticalRecordList(AssAssetRecord assAssetRecord) {
        List<AssAssetRecord> list = assAssetRecordMapper.selectAssAssetStatisticalRecordList(assAssetRecord);
        if (CollectionUtil.isNotEmpty(list)) {
            for (AssAssetRecord record : list) {
                // 更新图片
                getData(record);
            }
        }
        return list;
    }

    /**
     * 查询资产统计台账明细
     *
     * @param assAssetRecord 资产台账
     * @return 资产台账
     */
    @Override
    public List<AssAssetRecord> selectAssAssetStatisticalRecordListDetail(AssAssetRecord assAssetRecord) {
        List<AssAssetRecord> list = assAssetRecordMapper.selectAssAssetStatisticalRecordListDetail(assAssetRecord);
        if (CollectionUtil.isNotEmpty(list)) {
            for (AssAssetRecord record : list) {
                // 更新图片
                getData(record);
            }
        }
        return list;
    }

    /**
     * 新增资产台账
     * 需要注意编码重复校验
     *
     * @param assAssetRecord 资产台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertAssAssetRecord(AssAssetRecord assAssetRecord) {
        checkOut(assAssetRecord);
        checkName(assAssetRecord);
        assAssetRecord.setUseTimeUnit("YE").setMaintenanceCycleUnit("DA");
        assAssetRecord.setCurrencyUnit(ConstantsEms.YUAN);
        // 更新图片
        setData(assAssetRecord);
        int row = assAssetRecordMapper.insert(assAssetRecord);
        if (row > 0) {
            if (CollectionUtil.isNotEmpty(assAssetRecord.getAttachmentList())) {
                assAssetRecord.getAttachmentList().forEach(item -> {
                    item.setAssetSid((assAssetRecord.getAssetSid()));
                });
                assAssetRecordAttachMapper.inserts(assAssetRecord.getAttachmentList());
            }
            AssAssetRecord one = assAssetRecordMapper.selectById(assAssetRecord.getAssetSid());
            insertTodo(one);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new AssAssetRecord(), assAssetRecord);
            MongodbDeal.insert(assAssetRecord.getAssetSid(), assAssetRecord.getHandleStatus(), msgList, TITLE, null, assAssetRecord.getImportStatus());
        }
        return row;
    }

    /**
     * 修改资产台账
     *
     * @param assAssetRecord 资产台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateAssAssetRecord(AssAssetRecord assAssetRecord) {
        checkOut(assAssetRecord);
        checkName(assAssetRecord);
        // 更新图片
        setData(assAssetRecord);
        AssAssetRecord response = assAssetRecordMapper.selectAssAssetRecordById(assAssetRecord.getAssetSid());
        int row = assAssetRecordMapper.updateAllById(assAssetRecord);
        if (row > 0) {
            addAttach(assAssetRecord);
            //非保存状态删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(assAssetRecord.getHandleStatus())) {
                sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().eq(SysTodoTask::getDocumentSid, assAssetRecord.getAssetSid())
                        .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB));
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, assAssetRecord);
            MongodbDeal.update(assAssetRecord.getAssetSid(), response.getHandleStatus(), assAssetRecord.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更资产台账
     *
     * @param assAssetRecord 资产台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeAssAssetRecord(AssAssetRecord assAssetRecord) {
        checkOut(assAssetRecord);
        checkName(assAssetRecord);
        // 更新图片
        setData(assAssetRecord);
        AssAssetRecord response = assAssetRecordMapper.selectAssAssetRecordById(assAssetRecord.getAssetSid());
        int row = assAssetRecordMapper.updateAllById(assAssetRecord);
        if (row > 0) {
            addAttach(assAssetRecord);
            if (HandleStatus.SUBMIT.getCode().equals(assAssetRecord.getHandleStatus())) {
                Submit submit = new Submit();
                submit.setStartUserId(ApiThreadLocalUtil.get().getUserid().toString());
                submit.setFormType(FormType.AssetRecord_BG.getCode());
                List<FormParameter> list = new ArrayList();
                FormParameter formParameter = new FormParameter();
                formParameter.setParentId(assAssetRecord.getAssetSid().toString());
                formParameter.setFormId(assAssetRecord.getAssetSid().toString());
                formParameter.setFormCode(assAssetRecord.getAssetCode().toString());
                list.add(formParameter);
                submit.setFormParameters(list);
                workflowService.change(submit);
            }
            //插入日志
            MongodbUtil.insertUserLog(assAssetRecord.getAssetSid(), BusinessType.CHANGE.getValue(), response, assAssetRecord, TITLE);
        }
        return row;
    }

    /**
     * 批量删除资产台账
     *
     * @param assetSids 需要删除的资产台账ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAssAssetRecordByIds(List<Long> assetSids) {
        assetSids.forEach(sid -> {
            AssAssetRecord assAssetRecord = assAssetRecordMapper.selectById(sid);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(assAssetRecord, new AssAssetRecord());
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        });
        assAssetRecordAttachMapper.delete(new QueryWrapper<AssAssetRecordAttach>().lambda()
                .in(AssAssetRecordAttach::getAssetSid, assetSids));
        sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().in(SysTodoTask::getDocumentSid, assetSids));
        return assAssetRecordMapper.deleteBatchIds(assetSids);
    }

    /**
     * 更改确认状态
     *
     * @param assAssetRecord
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(AssAssetRecord assAssetRecord) {
        int row = 0;
        Long[] sids = assAssetRecord.getAssetSidList();
        if (sids != null && sids.length > 0) {
            row = assAssetRecordMapper.update(null, new UpdateWrapper<AssAssetRecord>().lambda()
                    .set(AssAssetRecord::getHandleStatus, assAssetRecord.getHandleStatus())
                    .set(AssAssetRecord::getConfirmDate, new Date())
                    .set(AssAssetRecord::getConfirmerAccount, ApiThreadLocalUtil.get().getSysUser().getUserName())
                    .in(AssAssetRecord::getAssetSid, sids));
            if (ConstantsEms.CHECK_STATUS.equals(assAssetRecord.getHandleStatus())) {
                sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().in(SysTodoTask::getDocumentSid, sids));
            }
            if (row > 0) {
                for (int i = 0; i < sids.length; i++) {
                    //插入日志
                    MongodbUtil.insertUserLog(sids[i], BusinessType.CHECK.getValue(), null, TITLE, null);
                }
            }
        }
        return row;
    }

    /**
     * 作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancel(AssAssetRecord assAssetRecord) {
        int row = 0;
        Long[] sids = assAssetRecord.getAssetSidList();
        if (sids != null && sids.length > 0) {
            row = assAssetRecordMapper.update(null, new UpdateWrapper<AssAssetRecord>().lambda()
                    .set(AssAssetRecord::getHandleStatus, HandleStatus.INVALID.getCode())
                    .in(AssAssetRecord::getAssetSid, sids));
            for (int i = 0; i < sids.length; i++) {
                //插入日志
                MongodbUtil.insertUserLog(sids[i], BusinessType.CANCEL.getValue(), null, TITLE, assAssetRecord.getComment());
            }
        }
        return row;
    }

    /**
     * 处理附件
     *
     * @param assAssetRecord
     * @return
     */
    public void addAttach(AssAssetRecord assAssetRecord) {
        assAssetRecordAttachMapper.delete(new QueryWrapper<AssAssetRecordAttach>().lambda()
                .eq(AssAssetRecordAttach::getAssetSid, assAssetRecord.getAssetSid()));
        if (CollectionUtil.isNotEmpty(assAssetRecord.getAttachmentList())) {
            assAssetRecord.getAttachmentList().forEach(item -> {
                item.setAssetSid((assAssetRecord.getAssetSid()));
            });
            assAssetRecordAttachMapper.inserts(assAssetRecord.getAttachmentList());
        }
    }

    /**
     * 金额等数字类型的校验
     *
     * @param assAssetRecord
     * @return
     */
    private void checkOut(AssAssetRecord assAssetRecord) {
        if (assAssetRecord.getCurrencyAmount() != null && BigDecimal.ZERO.compareTo(assAssetRecord.getCurrencyAmount()) > 0) {
            throw new CustomException("采购金额不可以填负数");
        }
        if (assAssetRecord.getCurrentNetValue() != null && BigDecimal.ZERO.compareTo(assAssetRecord.getCurrentNetValue()) > 0) {
            throw new CustomException("当前净值不可以填负数");
        }
        if (assAssetRecord.getQuantity() != null && new Long("0").compareTo(assAssetRecord.getQuantity()) >= 0) {
            throw new CustomException("数量不可以填负数或0");
        }
        if (assAssetRecord.getEstimatedRemainYears() != null && BigDecimal.ZERO.compareTo(assAssetRecord.getEstimatedRemainYears()) > 0) {
            throw new CustomException("预计可使用年限不可以填负数");
        }
        if (assAssetRecord.getHasUsedYears() != null && BigDecimal.ZERO.compareTo(assAssetRecord.getHasUsedYears()) > 0) {
            throw new CustomException("投用时已用年限不可以填负数");
        }
        if (assAssetRecord.getMaintenanceCycle() != null && BigDecimal.ZERO.compareTo(assAssetRecord.getMaintenanceCycle()) > 0) {
            throw new CustomException("保养周期不可以填负数");
        }
        if (assAssetRecord.getInspectionCycle() != null && BigDecimal.ZERO.compareTo(assAssetRecord.getInspectionCycle()) > 0) {
            throw new CustomException("巡检周期不可以填负数");
        }
    }

    /**
     * 资产卡片编号不能重复
     *
     * @param assAssetRecord
     * @return
     */
    private void checkName(AssAssetRecord assAssetRecord) {
        if (StrUtil.isNotBlank(assAssetRecord.getAssetCardNumber())) {
            QueryWrapper<AssAssetRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(AssAssetRecord::getAssetCardNumber, assAssetRecord.getAssetCardNumber());
            //sid不为空，就是编辑或者变更的情况
            if (assAssetRecord.getAssetSid() != null) {
                queryWrapper.lambda().ne(AssAssetRecord::getAssetSid, assAssetRecord.getAssetSid());
            }
            List<AssAssetRecord> recordList = assAssetRecordMapper.selectList(queryWrapper);
            if (CollectionUtil.isNotEmpty(recordList)) {
                throw new CustomException("资产卡片编号已存在，请核实");
            }
        }
    }

    /**
     * 新增待办
     *
     * @param assAssetRecord
     * @return
     */
    private void insertTodo(AssAssetRecord assAssetRecord) {
        //待办通知
        SysTodoTask sysTodoTask = new SysTodoTask();
        sysTodoTask.setTableName("s_ass_asset_record")
                .setDocumentSid(assAssetRecord.getAssetSid());
        sysTodoTask.setDocumentCode(String.valueOf(assAssetRecord.getAssetCode()))
                .setNoticeDate(new Date())
                .setUserId(ApiThreadLocalUtil.get().getUserid());
        // 获取菜单id
        SysMenu menu = new SysMenu();
        menu.setMenuName(ConstantsWorkbench.TODO_ASSET_RECORD_MENU_NAME);
        menu = remoteMenuService.getInfoByName(menu).getData();
        if (menu != null && menu.getMenuId() != null) {
            sysTodoTask.setMenuId(menu.getMenuId());
        }
        if (ConstantsEms.SAVA_STATUS.equals(assAssetRecord.getHandleStatus())) {
            sysTodoTask.setTitle("资产台账 " + assAssetRecord.getAssetCode() + " 当前是保存状态，请及时处理！")
                    .setTaskCategory(ConstantsEms.TODO_TASK_DB);
            sysTodoTaskMapper.insert(sysTodoTask);
        }
    }

    /**
     * 导入
     *
     * @param file
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object importData(MultipartFile file) {
        int num = 0;
        try {
            File toFile = null;
            try {
                toFile = FileUtils.multipartFileToFile(file);
            } catch (Exception e) {
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            //数据字典Map
            List<DictData> assetTypeDict = sysDictDataService.selectDictData("s_asset_type"); //资产类型
            assetTypeDict = assetTypeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> assetTypeMaps = assetTypeDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> assetStateDict = sysDictDataService.selectDictData("s_asset_status"); //资产状态
            assetStateDict = assetStateDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> assetStateMaps = assetStateDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> isNeedMaintenanceDict = sysDictDataService.selectDictData("s_yesno_flag"); //是否需保养
            isNeedMaintenanceDict = isNeedMaintenanceDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> isNeedMaintenanceMaps = isNeedMaintenanceDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> assetSourceDict = sysDictDataService.selectDictData("s_asset_source"); //资产来源
            assetSourceDict = assetSourceDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> assetSourceMaps = assetSourceDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> maintenanceLevelDict = sysDictDataService.selectDictData("s_maintenance_level"); //保养等级
            maintenanceLevelDict = maintenanceLevelDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> maintenanceLevelMaps = maintenanceLevelDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> isNeedInspectionDict = sysDictDataService.selectDictData("s_yesno_flag"); //是否需巡检
            isNeedInspectionDict = isNeedInspectionDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> isNeedInspectionMaps = isNeedInspectionDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            //每行对象
            List<AssAssetRecord> recordList = new ArrayList<>();
            CommonErrMsgResponse errMsg = null;
            //错误信息
            List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
            //读excel行和列
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
                //填充总列数
                copy(objects, readAll);
                num = i + 1;

                /**
                 * 资产名称 必填
                 */
                String assetName = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                if (StrUtil.isBlank(assetName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("资产名称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (assetName.length() > 200) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("资产名称不能超过200个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 资产类型 必填
                 */
                String assetType = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                if (StrUtil.isBlank(assetType)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("资产类型不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    assetType = assetTypeMaps.get(assetType); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(assetType)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("资产类型填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 公司简称 必填 （配置档案）
                 */
                Long companySid = null; // 公司
                String companyName = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                if (StrUtil.isBlank(companyName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("公司不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    //获取档案信息校验 公司XXXX不存在 、公司XXXX必须为确认且启用的数据
                    BasCompany basCompany = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>().lambda()
                            .eq(BasCompany::getShortName, companyName));
                    if (basCompany == null || basCompany.getCompanySid() == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("公司" + companyName + "不存在，导入失败！");
                        errMsgList.add(errMsg);
                    } else if (!basCompany.getStatus().equals("1") || !basCompany.getHandleStatus().equals("5")) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("公司" + companyName + "必须为确认且启用的数据，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        companySid = basCompany.getCompanySid();
                    }
                }

                /**
                 * 资产卡片编号 必填
                 */
                String assetCardNumber = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                if (StrUtil.isBlank(assetCardNumber)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("资产卡片编号不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    AssAssetRecord assAssetRecord = assAssetRecordMapper.selectOne(new QueryWrapper<AssAssetRecord>().lambda()
                            .eq(AssAssetRecord::getAssetCardNumber, assetCardNumber));
                    if (assAssetRecord != null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("资产卡片编号" + assetCardNumber + "已存在，导入失败！");
                        errMsgList.add(errMsg);
                    } else if (assetCardNumber.length() > 20) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("资产卡片编号不能超过20个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }


                /**
                 * 销售方 选填
                 */
                String seller = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                if (StrUtil.isNotBlank(seller)) {
                    if (seller.length() > 300) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("销售方不能超过300个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 厂家 选填
                 */
                String factory = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                if (StrUtil.isNotBlank(factory)) {
                    if (factory.length() > 300) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("厂家不能超过300个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 品牌 选填
                 */
                String brand = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                if (StrUtil.isNotBlank(brand)) {
                    if (brand.length() > 300) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("品牌不能超过120个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 规格型号 选填
                 */
                String specification = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();
                if (StrUtil.isNotBlank(specification)) {
                    if (specification.length() > 8) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("规格型号不能超过8个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 采购员 选填
                 */
                String buyer = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();
                if (StrUtil.isNotBlank(buyer)) {
                    if (buyer.length() > 8) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("采购员不能超过8个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 采购日期 选填
                 */
                String purchaseDate_s = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString();
                Date purchaseDate = null;
                if (StrUtil.isNotBlank(purchaseDate_s)) {
                    if (!JudgeFormat.isValidDate(purchaseDate_s)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("采购日期格式错误，导入失败");
                        errMsgList.add(errMsg);
                    } else {
                        purchaseDate = DateUtil.parse(purchaseDate_s);
                    }
                }

                /**
                 * 采购金额(元) 选填
                 */
                String currencyAmount_s = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString();
                BigDecimal currencyAmount = null;
                if (StrUtil.isNotBlank(currencyAmount_s)) {
                    if (!JudgeFormat.isValidDouble(currencyAmount_s, 10, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("采购金额(元)格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        currencyAmount = new BigDecimal(currencyAmount_s);
                        currencyAmount = currencyAmount.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP);
                    }
                }
                if (currencyAmount != null && BigDecimal.ZERO.compareTo(currencyAmount) >= 0) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("采购金额(元)必须大于0，导入失败！");
                    errMsgList.add(errMsg);
                }

                /**
                 * 当前净值(元) 选填
                 */
                String currentNetValue_s = objects.get(11) == null || objects.get(11) == "" ? null : objects.get(11).toString();
                BigDecimal currentNetValue = null;
                if (StrUtil.isNotBlank(currentNetValue_s)) {
                    if (!JudgeFormat.isValidDouble(currentNetValue_s, 10, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("当前净值(元)格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        currentNetValue = new BigDecimal(currentNetValue_s);
                        currentNetValue = currentNetValue.divide(BigDecimal.ONE, 2, BigDecimal.ROUND_HALF_UP);
                    }
                }
                if (currentNetValue != null && BigDecimal.ZERO.compareTo(currentNetValue) >= 0) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("当前净值(元)必须大于0，导入失败！");
                    errMsgList.add(errMsg);
                }

                /**
                 * 资产管理员 选填
                 */
                String assetAdministrator = objects.get(12) == null || objects.get(12) == "" ? null : objects.get(12).toString();
                if (StrUtil.isNotBlank(assetAdministrator)) {
                    if (assetAdministrator.length() > 30) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("银行支行名称不能超过30个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 投用日期 选填
                 */
                String enableDate_s = objects.get(13) == null || objects.get(13) == "" ? null : objects.get(13).toString();
                Date enableDate = null;
                if (StrUtil.isNotBlank(enableDate_s)) {
                    if (!JudgeFormat.isValidDate(enableDate_s)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("投用日期格式错误，导入失败");
                        errMsgList.add(errMsg);
                    } else {
                        enableDate = DateUtil.parse(enableDate_s);
                    }
                }

                /**
                 * 报废/处置日期 选填
                 */
                String scrapDate_s = objects.get(14) == null || objects.get(14) == "" ? null : objects.get(14).toString();
                Date scrapDate = null;
                if (StrUtil.isNotBlank(scrapDate_s)) {
                    if (!JudgeFormat.isValidDate(scrapDate_s)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("报废/处置日期格式错误，导入失败");
                        errMsgList.add(errMsg);
                    } else {
                        scrapDate = DateUtil.parse(scrapDate_s);
                    }
                }

                /**
                 * 资产状态 选填 (数据字典）
                 */
                String assetState = objects.get(15) == null || objects.get(15) == "" ? null : objects.get(15).toString();
                if (StrUtil.isNotBlank(assetState)) {
                    if (assetState.length() > 200) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("资产状态不能超过200个字符，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        assetState = assetStateMaps.get(assetState); //通过数据字典标签获取数据字典的值
                        if (StrUtil.isBlank(assetState)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("资产状态填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }

                /**
                 * 资产来源 选填 （数据字典）
                 */
                String assetSource = objects.get(16) == null || objects.get(16) == "" ? null : objects.get(16).toString();
                if (StrUtil.isNotBlank(assetSource)) {
                    if (assetSource.length() > 200) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("资产来源不能超过200个字符，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        assetSource = assetSourceMaps.get(assetSource); //通过数据字典标签获取数据字典的值
                        if (StrUtil.isBlank(assetSource)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("资产来源填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }

                /**
                 * 计量单位 选填 （配置档案）
                 */
                String unitBase = null; // 计量单位
                String unitBaseName = objects.get(17) == null || objects.get(17) == "" ? null : objects.get(17).toString();
                if (StrUtil.isNotBlank(unitBaseName)) {
                    //获取档案信息校验  s_con_measure_unit
                    ConMeasureUnit conMeasureUnit = conMeasureUnitMapper.selectOne(new QueryWrapper<ConMeasureUnit>().lambda()
                            .eq(ConMeasureUnit::getName, unitBaseName));
                    if (conMeasureUnit == null || conMeasureUnit.getCode() == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("计量单位" + unitBaseName + "不存在，导入失败！");
                        errMsgList.add(errMsg);
                    } else if (!conMeasureUnit.getStatus().equals("1") || !conMeasureUnit.getHandleStatus().equals("5")) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("计量单位" + unitBaseName + "必须为确认且启用的数据，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        unitBase = conMeasureUnit.getCode();
                    }
                }

                /**
                 * 当前使用部门 选填
                 */
                String departmentName = objects.get(18) == null || objects.get(18) == "" ? null : objects.get(18).toString();
                if (StrUtil.isNotBlank(departmentName)) {
                    if (departmentName.length() > 255) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("当前使用部门不能超过255个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 年折旧率(%) 选填
                 */
                String annualDepreciationRate_s = objects.get(19) == null || objects.get(19) == "" ? null : objects.get(19).toString();
                BigDecimal annualDepreciationRate = null;
                if (StrUtil.isNotBlank(annualDepreciationRate_s)) {
                    if (!JudgeFormat.isValidDouble(annualDepreciationRate_s, 10, 2)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("年折旧率(%)格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        annualDepreciationRate = new BigDecimal(annualDepreciationRate_s);
                        annualDepreciationRate = annualDepreciationRate.multiply(BigDecimal.valueOf(0.01));
                        annualDepreciationRate = annualDepreciationRate.setScale(4, BigDecimal.ROUND_HALF_UP);
                    }
                }
                if (annualDepreciationRate != null && BigDecimal.ZERO.compareTo(annualDepreciationRate) >= 0) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("年折旧率(%)必须大于0，导入失败！");
                    errMsgList.add(errMsg);
                }

                /**
                 * 预计可使用年限(年)
                 */
                String estimatedRemainYears_s = objects.get(20) == null || objects.get(20) == "" ? null : objects.get(20).toString();
                BigDecimal estimatedRemainYears = null;
                if (StrUtil.isNotBlank(estimatedRemainYears_s)) {
                    if (!JudgeFormat.isValidDouble(estimatedRemainYears_s, 10, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("预计可使用年限(年)格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        estimatedRemainYears = new BigDecimal(estimatedRemainYears_s);
                        estimatedRemainYears = estimatedRemainYears.divide(BigDecimal.ONE, 1, BigDecimal.ROUND_HALF_UP);
                    }
                }
                if (estimatedRemainYears != null && BigDecimal.ZERO.compareTo(estimatedRemainYears) >= 0) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("预计可使用年限(年)必须大于0，导入失败！");
                    errMsgList.add(errMsg);
                }

                /**
                 * 投用时已用年限(年)
                 */
                String hasUsedYears_s = objects.get(21) == null || objects.get(21) == "" ? null : objects.get(21).toString();
                BigDecimal hasUsedYears = null;
                if (StrUtil.isNotBlank(hasUsedYears_s)) {
                    if (!JudgeFormat.isValidDouble(hasUsedYears_s, 10, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("投用时已用年限(年)格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        hasUsedYears = new BigDecimal(hasUsedYears_s);
                        hasUsedYears = hasUsedYears.divide(BigDecimal.ONE, 1, BigDecimal.ROUND_HALF_UP);
                    }
                }
                if (hasUsedYears != null && BigDecimal.ZERO.compareTo(hasUsedYears) >= 0) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("投用时已用年限(年)必须大于0，导入失败！");
                    errMsgList.add(errMsg);
                }

                /**
                 * 是否需保养 选填 (数据字典）
                 */
                String isNeedMaintenance = objects.get(22) == null || objects.get(22) == "" ? null : objects.get(22).toString();
                if (StrUtil.isNotBlank(isNeedMaintenance)) {
                    if (isNeedMaintenance.length() > 200) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否需保养不能超过200个字符，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        isNeedMaintenance = isNeedMaintenanceMaps.get(isNeedMaintenance); //通过数据字典标签获取数据字典的值
                        if (StrUtil.isBlank(isNeedMaintenance)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("是否需保养填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }

                /**
                 * 保养周期(天)
                 */
                String maintenanceCycle_s = objects.get(23) == null || objects.get(23) == "" ? null : objects.get(23).toString();
                BigDecimal maintenanceCycle = null;
                if (StrUtil.isNotBlank(maintenanceCycle_s)) {
                    if (!JudgeFormat.isValidDouble(maintenanceCycle_s, 4, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("保养周期(天)格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        maintenanceCycle = new BigDecimal(maintenanceCycle_s);
                        maintenanceCycle = maintenanceCycle.divide(BigDecimal.ONE, 1, BigDecimal.ROUND_HALF_UP);
                    }
                }
                if (maintenanceCycle != null && BigDecimal.ZERO.compareTo(maintenanceCycle) >= 0) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("保养周期(天)必须大于0，导入失败！");
                    errMsgList.add(errMsg);
                }

                /**
                 * 保养等级 选填 (数据字典）
                 */
                String maintenanceLevel = objects.get(24) == null || objects.get(24) == "" ? null : objects.get(24).toString();
                if (StrUtil.isNotBlank(maintenanceLevel)) {
                    if (maintenanceLevel.length() > 200) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("保养等级不能超过200个字符，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        maintenanceLevel = maintenanceLevelMaps.get(maintenanceLevel); //通过数据字典标签获取数据字典的值
                        if (StrUtil.isBlank(maintenanceLevel)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("保养等级填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }

                /**
                 * 最近保养日期 选填
                 */
                String lastestMaintenanceDate_s = objects.get(25) == null || objects.get(25) == "" ? null : objects.get(25).toString();
                Date lastestMaintenanceDate = null;
                if (StrUtil.isNotBlank(lastestMaintenanceDate_s)) {
                    if (!JudgeFormat.isValidDate(lastestMaintenanceDate_s)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("最近保养日期格式错误，导入失败");
                        errMsgList.add(errMsg);
                    } else {
                        lastestMaintenanceDate = DateUtil.parse(lastestMaintenanceDate_s);
                    }
                }

                /**
                 * 是否需巡检 选填 (数据字典）
                 */
                String isNeedInspection = objects.get(26) == null || objects.get(26) == "" ? null : objects.get(26).toString();
                if (StrUtil.isNotBlank(isNeedInspection)) {
                    if (isNeedInspection.length() > 200) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("是否需巡检不能超过200个字符，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        isNeedInspection = isNeedInspectionMaps.get(isNeedInspection); //通过数据字典标签获取数据字典的值
                        if (StrUtil.isBlank(isNeedInspection)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("是否需巡检填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }

                /**
                 * 巡检周期(天) 选填
                 */
                String inspectionCycle_s = objects.get(27) == null || objects.get(27) == "" ? null : objects.get(27).toString();
                BigDecimal inspectionCycle = null;
                if (StrUtil.isNotBlank(inspectionCycle_s)) {
                    if (!JudgeFormat.isValidDouble(inspectionCycle_s, 4, 1)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("巡检周期(天)格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        inspectionCycle = new BigDecimal(inspectionCycle_s);
                        inspectionCycle = inspectionCycle.divide(BigDecimal.ONE, 1, BigDecimal.ROUND_HALF_UP);
                    }
                }
                if (inspectionCycle != null && BigDecimal.ZERO.compareTo(inspectionCycle) >= 0) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("巡检周期(天)必须大于0，导入失败！");
                    errMsgList.add(errMsg);
                }

                /**
                 * 最近巡检日期 选填
                 */
                String lastestInspectionDate_s = objects.get(28) == null || objects.get(28) == "" ? null : objects.get(28).toString();
                Date lastestInspectionDate = null;
                if (StrUtil.isNotBlank(lastestInspectionDate_s)) {
                    if (!JudgeFormat.isValidDate(lastestInspectionDate_s)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("最近巡检日期格式错误，导入失败");
                        errMsgList.add(errMsg);
                    } else {
                        lastestInspectionDate = DateUtil.parse(lastestInspectionDate_s);
                    }
                }

                /**
                 * 接收人 选填
                 */
                String recipient = objects.get(29) == null || objects.get(29) == "" ? null : objects.get(29).toString();
                if (StrUtil.isNotBlank(recipient)) {
                    if (recipient.length() > 30) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("接收人不能超过30个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 存放位置 选填
                 */
                String location = objects.get(30) == null || objects.get(30) == "" ? null : objects.get(30).toString();
                if (StrUtil.isNotBlank(location)) {
                    if (location.length() > 600) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("存放位置不能超过600个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 供货方联系方式（联系人/电话/地址） 选填
                 */
                String supplierPhone = objects.get(31) == null || objects.get(31) == "" ? null : objects.get(31).toString();
                if (StrUtil.isNotBlank(supplierPhone)) {
                    if (supplierPhone.length() > 600) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("供货方联系方式（联系人/电话/地址）不能超过600个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 备注 选填
                 */
                String remark = objects.get(32) == null || objects.get(32) == "" ? null : objects.get(32).toString();
                if (StrUtil.isNotBlank(remark)) {
                    if (remark.length() > 600) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("备注不能超过600个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                AssAssetRecord record = new AssAssetRecord();
                record.setAssetName(assetName).setAssetType(assetType).setCompanySid(companySid).setAssetCardNumber(assetCardNumber)
                        .setSeller(seller).setFactory(factory).setBrand(brand).setSpecification(specification).setBuyer(buyer)
                        .setPurchaseDate(purchaseDate).setCurrencyAmount(currencyAmount).setCurrentNetValue(currentNetValue)
                        .setAssetAdministrator(assetAdministrator).setEnableDate(enableDate).setScrapDate(scrapDate)
                        .setAssetState(assetState).setAssetSource(assetSource).setUnitBase(unitBase).setDepartmentName(departmentName)
                        .setAnnualDepreciationRate(annualDepreciationRate).setEstimatedRemainYears(estimatedRemainYears)
                        .setHasUsedYears(hasUsedYears).setIsNeedMaintenance(isNeedMaintenance).setMaintenanceCycle(maintenanceCycle)
                        .setMaintenanceLevel(maintenanceLevel).setLastestMaintenanceDate(lastestMaintenanceDate)
                        .setIsNeedInspection(isNeedInspection).setInspectionCycle(inspectionCycle)
                        .setLastestInspectionDate(lastestInspectionDate).setRecipient(recipient)
                        .setLocation(location).setSupplierPhone(supplierPhone).setRemark(remark).setQuantity(1L);
                record.setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN).setCurrency(ConstantsFinance.CURRENCY_CNY)
                        .setHandleStatus(ConstantsEms.SAVA_STATUS).setImportStatus(BusinessType.IMPORT.getValue());
                recordList.add(record);

            }
            //检查有没有报错
            if (CollectionUtil.isNotEmpty(errMsgList)) {
                return errMsgList;
            }
            //调用新增方法写入
            if (CollectionUtil.isNotEmpty(recordList)) {
                recordList.forEach(item -> {
                    insertAssAssetRecord(item);
                });
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return num - 2;
    }

    /**
     * 导出资产卡片
     */
    @Override
    public void exportAssetCardList(HttpServletResponse response, AssAssetRecord assAssetRecord) {
        try {
            List<AssAssetRecord> assAssetRecordList = assAssetRecordMapper.selectAssAssetRecordList(assAssetRecord);
            // 遍历每个模块
            XSSFWorkbook workbook = new XSSFWorkbook();
            // 创建一个列表来存储导出的数据
            List<String> exportData = new ArrayList<>();
            int size = 0;
            for (AssAssetRecord record : assAssetRecordList) {
                // 添加模块内容
                exportData.add("资产编号: " + record.getAssetCode());
                exportData.add("资产名称: " + record.getAssetName());
                exportData.add("规格型号: " + record.getSpecification());
                exportData.add("存放位置: " + record.getLocation());
                exportData.add("管理员: " + record.getAssetAdministrator());
                exportData.add(""); // 添加空行
                exportData.add(record.getCompanyName());

                // 添加空行
                exportData.add("");
                exportData.add("");
            }
            // 导出到 Excel 文件
            size = exportData.size();
            printExcelAssetCardList(workbook,exportData,size);
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment; filename=" + new String("导出资产卡片".getBytes("gbk"), "iso8859-1") + ".xlsx");
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            throw new CustomException("导出失败");
        }
    }

    /**
     * 资产卡片导出成 excel
     */
    public void printExcelAssetCardList(XSSFWorkbook workbook, List<String> exportReport, int size) {
        // 绘制excel表格
        Sheet sheet = workbook.createSheet("资产卡片");
        sheet.setDefaultColumnWidth(20);
        // 单元格格式
        CellStyle defaultCellStyle = ExcelStyleUtil.getDefaultCellStyle(workbook);
        CellStyle defaultCellStyleLeft = ExcelStyleUtil.getDefaultCellStyle(workbook);
        defaultCellStyleLeft.setAlignment(HorizontalAlignment.LEFT);
        CellStyle defaultCellStyleNo = ExcelStyleUtil.getDefaultCellStyle(workbook);
        // 遍历数据，创建行和单元格
        int rowNum = 0;
        for (String data : exportReport) {
            Row row = sheet.createRow(rowNum++);
            Cell cell = row.createCell(0); // 第一列
            cell.setCellValue(data);
        }
    }

    //填充-主表
    public void copy(List<Object> objects, List<List<Object>> readAll) {
        //获取第一行的列数
        int size = readAll.get(0).size();
        //当前行的列数
        int lineSize = objects.size();
        ArrayList<Object> all = new ArrayList<>();
        for (int i = lineSize; i < size; i++) {
            Object o = new Object();
            o = null;
            objects.add(o);
        }
    }

}
