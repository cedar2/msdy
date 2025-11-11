package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IDevMarketSurveyService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 市场调研Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-08
 */
@Service
@SuppressWarnings("all")
public class DevMarketSurveyServiceImpl extends ServiceImpl<DevMarketSurveyMapper, DevMarketSurvey> implements IDevMarketSurveyService {
    @Autowired
    private DevMarketSurveyMapper devMarketSurveyMapper;
    @Autowired
    private DevMarketSurveyAttachMapper devMarketSurveyAttachMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private BasCompanyBrandMapper basCompanyBrandMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;

    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static final String TITLE = "市场调研";

    /**
     * 查询市场调研
     *
     * @param marketSurveySid 市场调研ID
     * @return 市场调研
     */
    @Override
    public DevMarketSurvey selectDevMarketSurveyById(Long marketSurveySid) {
        DevMarketSurvey devMarketSurvey = devMarketSurveyMapper.selectDevMarketSurveyById(marketSurveySid);
        devMarketSurvey.setAttachmentList(new ArrayList<>());
        // 附件
        List<DevMarketSurveyAttach> attachmentList = devMarketSurveyAttachMapper.selectDevMarketSurveyAttachList(
                new DevMarketSurveyAttach().setMarketSurveySid(marketSurveySid));
        if (CollectionUtil.isNotEmpty(attachmentList)) {
            devMarketSurvey.setAttachmentList(attachmentList);
        }
        // 操作日志
        MongodbUtil.find(devMarketSurvey);
        return devMarketSurvey;
    }

    /**
     * 复制市场调研
     *
     * @param marketSurveySid 市场调研ID
     * @return 市场调研
     */
    @Override
    public DevMarketSurvey copyDevMarketSurveyById(Long marketSurveySid) {
        DevMarketSurvey devMarketSurvey = devMarketSurveyMapper.selectDevMarketSurveyById(marketSurveySid);
        if (devMarketSurvey != null) {
            devMarketSurvey.setCreateDate(null).setCreatorAccount(null).setCreatorAccountName(null)
                           .setUpdateDate(null).setUpdaterAccount(null).setUpdaterAccountName(null);
            devMarketSurvey.setMarketSurveySid(null);
            devMarketSurvey.setHandleStatus(ConstantsEms.SAVA_STATUS).setStatus(ConstantsEms.ENABLE_STATUS);
        }
        return devMarketSurvey;
    }

    /**
     * 查询市场调研列表
     *
     * @param devMarketSurvey 市场调研
     * @return 市场调研
     */
    @Override
    public List<DevMarketSurvey> selectDevMarketSurveyListOrderByDesc(DevMarketSurvey devMarketSurvey) {
        return devMarketSurveyMapper.selectDevMarketSurveyListOrderByDesc(devMarketSurvey);
    }

    /**
     * 设置部分字段的编码
     *
     * @param old 表中数据，new修改后数据
     * @return 结果
     */
    private void setData(DevMarketSurvey oldSurvey, DevMarketSurvey newSurvey) {
        // 公司
        if (newSurvey.getCompanySid() != null && !newSurvey.getCompanySid().equals(oldSurvey.getCompanySid())) {
            BasCompany company = basCompanyMapper.selectById(newSurvey.getCompanySid());
            if (company != null) {
                newSurvey.setCompanyCode(company.getCompanyCode());
            } else {
                newSurvey.setCompanyCode(null);
            }
        } else if (newSurvey.getCompanySid() == null) {
            newSurvey.setCompanyCode(null);
        }
    }

    /**
     * 新增市场调研
     * 需要注意编码重复校验
     *
     * @param devMarketSurvey 市场调研
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDevMarketSurvey(DevMarketSurvey devMarketSurvey) {
        ensureMarketSurveyIsUnique(devMarketSurvey);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(devMarketSurvey.getHandleStatus())) {
            devMarketSurvey.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 写入部分字段的code
        this.setData(new DevMarketSurvey(), devMarketSurvey);
        int row = devMarketSurveyMapper.insert(devMarketSurvey);
        if (row > 0) {
            DevMarketSurvey survey = devMarketSurveyMapper.selectById(devMarketSurvey.getMarketSurveySid());
            // 写入附件
            if (CollectionUtil.isNotEmpty(devMarketSurvey.getAttachmentList())) {
                devMarketSurvey.getAttachmentList().forEach(item -> {
                    item.setMarketSurveySid(devMarketSurvey.getMarketSurveySid());
                    item.setMarketSurveyCode(survey.getMarketSurveyCode());
                });
                devMarketSurveyAttachMapper.inserts(devMarketSurvey.getAttachmentList());
            }
            // 待办
            if (ConstantsEms.SAVA_STATUS.equals(devMarketSurvey.getHandleStatus())) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                           .setTableName(ConstantsTable.TABLE_DEV_MARKET_SURVEY)
                           .setDocumentSid(devMarketSurvey.getMarketSurveySid());
                sysTodoTask.setTitle("市场调研" + survey.getMarketSurveyCode() + "当前是保存状态，请及时处理！")
                           .setDocumentCode(survey.getMarketSurveyCode().toString())
                           .setNoticeDate(new Date())
                           .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            // 插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new DevMarketSurvey(), devMarketSurvey);
            MongodbDeal.insert(devMarketSurvey.getMarketSurveySid(),
                               devMarketSurvey.getHandleStatus(),
                               msgList,
                               TITLE,
                               null);
        }
        return row;
    }

    /**
     * 修改市场调研
     *
     * @param devMarketSurvey 市场调研
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDevMarketSurvey(DevMarketSurvey devMarketSurvey) {
        ensureMarketSurveyIsUnique(devMarketSurvey);
        DevMarketSurvey original = devMarketSurveyMapper.selectDevMarketSurveyById(devMarketSurvey.getMarketSurveySid());
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(devMarketSurvey.getHandleStatus())) {
            devMarketSurvey.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, devMarketSurvey);
        if (CollectionUtil.isNotEmpty(msgList)) {
            devMarketSurvey.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 写入部分字段的code
        this.setData(original, devMarketSurvey);
        // 更新主表
        int row = devMarketSurveyMapper.updateAllById(devMarketSurvey);
        if (row > 0) {
            // 修改附件
            this.updateDevMarketSurveyAttach(devMarketSurvey);
            // 不是保存状态删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(devMarketSurvey.getHandleStatus())) {
                sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                                                                        .eq(SysTodoTask::getDocumentSid,
                                                                            devMarketSurvey.getMarketSurveySid())
                                                                        .eq(SysTodoTask::getTaskCategory,
                                                                            ConstantsEms.TODO_TASK_DB)
                                                                        .eq(SysTodoTask::getTableName,
                                                                            ConstantsTable.TABLE_DEV_MARKET_SURVEY));
            }
            // 插入日志
            MongodbDeal.update(devMarketSurvey.getMarketSurveySid(),
                               original.getHandleStatus(),
                               devMarketSurvey.getHandleStatus(),
                               msgList,
                               TITLE,
                               null);
        }
        return row;
    }

    /**
     * 变更市场调研
     *
     * @param devMarketSurvey 市场调研
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeDevMarketSurvey(DevMarketSurvey devMarketSurvey) {
        ensureMarketSurveyIsUnique(devMarketSurvey);
        DevMarketSurvey response = devMarketSurveyMapper.selectDevMarketSurveyById(devMarketSurvey.getMarketSurveySid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, devMarketSurvey);
        if (CollectionUtil.isNotEmpty(msgList)) {
            devMarketSurvey.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 写入部分字段的code
        this.setData(response, devMarketSurvey);
        // 更新主表
        int row = devMarketSurveyMapper.updateAllById(devMarketSurvey);
        if (row > 0) {
            // 修改附件
            this.updateDevMarketSurveyAttach(devMarketSurvey);
            // 插入日志
            MongodbUtil.insertUserLog(devMarketSurvey.getMarketSurveySid(),
                                      BusinessType.CHANGE.getValue(),
                                      msgList,
                                      TITLE);
        }
        return row;
    }

    /**
     * 批量修改附件信息
     *
     * @param devMarketSurvey 市场调研
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDevMarketSurveyAttach(DevMarketSurvey devMarketSurvey) {
        // 先删后加
        devMarketSurveyAttachMapper.delete(new QueryWrapper<DevMarketSurveyAttach>().lambda()
                                                                                    .eq(DevMarketSurveyAttach::getMarketSurveySid,
                                                                                        devMarketSurvey.getMarketSurveySid()));
        if (CollectionUtil.isNotEmpty(devMarketSurvey.getAttachmentList())) {
            devMarketSurvey.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getMarketSurveyAttachSid() == null) {
                    att.setMarketSurveySid(devMarketSurvey.getMarketSurveySid());
                    att.setMarketSurveyCode(devMarketSurvey.getMarketSurveyCode());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            devMarketSurveyAttachMapper.inserts(devMarketSurvey.getAttachmentList());
        }
    }

    /**
     * 批量删除市场调研
     *
     * @param marketSurveySids 需要删除的市场调研ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDevMarketSurveyByIds(List<Long> marketSurveySids) {
        List<DevMarketSurvey> list = devMarketSurveyMapper.selectList(new QueryWrapper<DevMarketSurvey>()
                                                                              .lambda().in(DevMarketSurvey::getMarketSurveySid,
                                                                                           marketSurveySids));
        // 删除校验
        list = list.stream().filter(o -> !HandleStatus.SAVE.getCode().equals(o.getHandleStatus()) && !HandleStatus.RETURNED.getCode().equals(
                           o.getHandleStatus()))
                   .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(list)) {
            throw new BaseException("只有保存或者已退回状态才允许删除！");
        }
        int row = devMarketSurveyMapper.deleteBatchIds(marketSurveySids);
        if (row > 0) {
            // 删除附件
            devMarketSurveyAttachMapper.delete(new QueryWrapper<DevMarketSurveyAttach>().lambda()
                                                                                        .in(DevMarketSurveyAttach::getMarketSurveySid,
                                                                                            marketSurveySids));
            // 删除待办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                                                                    .in(SysTodoTask::getDocumentSid, marketSurveySids)
                                                                    .eq(SysTodoTask::getTaskCategory,
                                                                        ConstantsEms.TODO_TASK_DB)
                                                                    .eq(SysTodoTask::getTableName,
                                                                        ConstantsTable.TABLE_DEV_MARKET_SURVEY));
            // 操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new DevMarketSurvey());
                MongodbUtil.insertUserLog(o.getMarketSurveySid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 启用/停用
     *
     * @param devMarketSurvey
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(DevMarketSurvey devMarketSurvey) {
        int row = 0;
        Long[] sids = devMarketSurvey.getMarketSurveySidList();
        if (sids != null && sids.length > 0) {
            row = devMarketSurveyMapper.update(null,
                                               new UpdateWrapper<DevMarketSurvey>().lambda().set(DevMarketSurvey::getStatus,
                                                                                                 devMarketSurvey.getStatus())
                                                                                   .in(DevMarketSurvey::getMarketSurveySid,
                                                                                       sids));
            if (row == 0) {
                throw new BaseException("更改状态失败,请联系管理员");
            }
            for (Long id : sids) {
                // 插入日志
                MongodbDeal.status(id, devMarketSurvey.getStatus(), null, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param devMarketSurvey
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(DevMarketSurvey devMarketSurvey) {
        int row = 0;
        Long[] sids = devMarketSurvey.getMarketSurveySidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<DevMarketSurvey> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(DevMarketSurvey::getMarketSurveySid, sids);
            updateWrapper.set(DevMarketSurvey::getHandleStatus, devMarketSurvey.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(devMarketSurvey.getHandleStatus())) {
                updateWrapper.set(DevMarketSurvey::getConfirmDate, new Date());
                updateWrapper.set(DevMarketSurvey::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            row = devMarketSurveyMapper.update(null, updateWrapper);
            if (row > 0) {
                // 删除待办
                if (ConstantsEms.CHECK_STATUS.equals(devMarketSurvey.getHandleStatus())) {
                    sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                                                                            .in(SysTodoTask::getDocumentSid, sids)
                                                                            .eq(SysTodoTask::getTaskCategory,
                                                                                ConstantsEms.TODO_TASK_DB)
                                                                            .eq(SysTodoTask::getTableName,
                                                                                ConstantsTable.TABLE_DEV_MARKET_SURVEY));
                }
                for (Long id : sids) {
                    // 插入日志
                    MongodbDeal.check(id, devMarketSurvey.getHandleStatus(), null, TITLE, null);
                }
            }
        }
        return row;
    }

    /**
     * 校验 唯一性
     * 校验的查询条件相当于【人名】
     *
     * @param givenCase 给定市场调研
     */
    private void ensureMarketSurveyIsUnique(DevMarketSurvey givenCase) {
        if (givenCase.getYear() == null) {
            throw new BaseException("年份不能为空");
        }
        List<DevMarketSurvey> result = devMarketSurveyMapper.selectByYearAndCompanySidAndBrandCode(
                givenCase.getYear(),
                givenCase.getCompanySid(),
                givenCase.getBrandCode(),
                givenCase.getGroupType()
        );
        if (result.isEmpty()) {
            // 不存在这个人，说明是新建的
            return;
        }
        if (result.size() != 1) {
            // 存在多个这个 叫这个名字的人，有问题
            throw new BaseException("该市场调研组合已存在！");
        }
        DevMarketSurvey onlyOne = result.get(0);
        if (onlyOne.getMarketSurveySid().equals(givenCase.getMarketSurveySid())) {
            // 这两个市场调研是同一个人，这没问题。
            return;
        }
        // 这两个市场调研不是同一个人，但叫了同样名字，这是有问题的。
        throw new BaseException("该市场调研组合已存在！");
    }

    /**
     * 导入
     * @param file
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity importData(MultipartFile file) {
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
            List<DictData> yearDict = sysDictDataService.selectDictData("s_year"); // 年度
            yearDict = yearDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> yearMaps = yearDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            // 错误信息
            CommonErrMsgResponse errMsg = null;
            List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
            HashMap<String, String> codeMap = new HashMap<>();
            HashMap<String, String> nameMap = new HashMap<>();
            // 基本
            DevMarketSurvey marketSurvey = null;
            List<DevMarketSurvey> marketSurveyList = new ArrayList<>();
            Map<String, String> marketSurveyHasMap = new HashMap<>();
            // 循环文件
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
                 * 年度 必填
                 */
                String yearName = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                String year = null;
                if (StrUtil.isBlank(yearName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("年度不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    year = yearMaps.get(yearName); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(year)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("年度填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 公司简称 必填
                 */
                String companyShortName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                Long companySid = null;
                String companyCode = null;
                String companyName = null;
                if (StrUtil.isNotBlank(companyShortName)) {
                    try {
                        BasCompany basCompany = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>().lambda().eq(BasCompany::getShortName,companyShortName));
                        if (basCompany == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("简称为"+ companyShortName +"没有对应的公司，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(basCompany.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basCompany.getHandleStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的公司必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            companySid = basCompany.getCompanySid();
                            companyCode = basCompany.getCompanyCode();
                            companyName = basCompany.getCompanyName();
                        }
                    }catch (TooManyResultsException e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(companyShortName + "公司档案存在重复，请先检查该公司，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 品牌 必填
                 */
                String brandName = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                Long companyBrandSid = null;
                String brandCode = null;
                if (StrUtil.isNotBlank(brandName)) {
                    if (companySid == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("请填写品牌对应的公司，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        try {
                            BasCompanyBrand basCompanyBrand = basCompanyBrandMapper.selectOne(new
                                    QueryWrapper<BasCompanyBrand>().lambda().eq(BasCompanyBrand::getBrandName,brandName)
                                    .eq(BasCompanyBrand::getCompanySid, companySid));
                            if (basCompanyBrand == null){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("品牌" + brandName + "不存在，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            else {
                                if (ConstantsEms.DISENABLE_STATUS.equals(basCompanyBrand.getStatus())){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("品牌" + brandName + "必须是已启用的状态，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                                companyBrandSid = basCompanyBrand.getCompanyBrandSid();
                                brandCode = basCompanyBrand.getBrandCode();
                            }
                        }catch (TooManyResultsException e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("品牌" + brandName + "存在重复，请先检查该品牌，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }

                /**
                 * 校验重复
                 */
                if (StrUtil.isNotBlank(year)) {
                    String key = "";
                    key = String.valueOf(year) + String.valueOf(companySid.toString()) + String.valueOf(brandCode);
                    // 表格中
                    if (marketSurveyHasMap.containsKey(key)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("表格中，该“年度X公司X品牌”组合已存在！");
                        errMsgList.add(errMsg);
                    } else {
                        // 存入map
                        marketSurveyHasMap.put(key, "1");
                        // 数据库中
                        QueryWrapper<DevMarketSurvey> queryWrapper = new QueryWrapper<>();
                        queryWrapper.lambda().eq(DevMarketSurvey::getYear, year);
                        if (companySid == null)  {
                            queryWrapper.lambda().isNull(DevMarketSurvey::getCompanySid);
                        } else {
                            queryWrapper.lambda().eq(DevMarketSurvey::getCompanySid, companySid);
                        }
                        if (StrUtil.isBlank(brandCode))  {
                            queryWrapper.lambda().isNull(DevMarketSurvey::getBrandCode);
                        } else {
                            queryWrapper.lambda().eq(DevMarketSurvey::getBrandCode, brandCode);
                        }
                        List<DevMarketSurvey> marketSurveys = devMarketSurveyMapper.selectList(queryWrapper);
                        if (CollectionUtil.isNotEmpty(marketSurveys)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("系统中，该“年度X公司X品牌”组合已存在！");
                            errMsgList.add(errMsg);
                        }
                    }
                }

                /**
                 * 备注 选填
                 */
                String remark = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                if (StrUtil.isNotBlank(remark) && remark.length() > 600){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("备注长度不能超过600个字符，导入失败！");
                    errMsgList.add(errMsg);
                }

                // 写入数据
                if (CollectionUtil.isEmpty(errMsgList)){
                    marketSurvey = new DevMarketSurvey();
                    marketSurvey.setYear(year).setCompanySid(companySid).setCompanyCode(companyCode)
                            .setBrandCode(brandCode).setRemark(remark);
                    marketSurvey.setStatus(ConstantsEms.ENABLE_STATUS).setHandleStatus(ConstantsEms.CHECK_STATUS);
                    marketSurveyList.add(marketSurvey);
                }
            }

            if (CollectionUtil.isNotEmpty(errMsgList)){
                return EmsResultEntity.error(errMsgList);
            }
            if (CollectionUtil.isNotEmpty(marketSurveyList)){
                marketSurveyList.forEach(item->{
                    this.insertDevMarketSurvey(item);
                });
            }
        }catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return EmsResultEntity.success(num-2);
    }

    //填充-主表
    public void copy(List<Object> objects, List<List<Object>> readAll){
        //获取第一行的列数
        int size = readAll.get(0).size();
        //当前行的列数
        int lineSize = objects.size();
        ArrayList<Object> all = new ArrayList<>();
        for (int i=lineSize;i<size;i++){
            Object o = new Object();
            o=null;
            objects.add(o);
        }
    }
}
