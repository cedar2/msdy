package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.utils.file.FileUtils;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.mapper.*;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.service.ISysDictDataService;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.common.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.service.IQuaPurInspectionRecordService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 采购验货问题台账Service业务层处理
 *
 * @author platform
 * @date 2024-09-20
 */
@Service
@SuppressWarnings("all" )
public class QuaPurInspectionRecordServiceImpl extends ServiceImpl<QuaPurInspectionRecordMapper,QuaPurInspectionRecord> implements IQuaPurInspectionRecordService {
    @Autowired
    private QuaPurInspectionRecordMapper quaPurInspectionRecordMapper;
    @Autowired
    private QuaPurInspectionRecordAttachMapper attachMapper;
    @Autowired
    private BasProductSeasonMapper basProductSeasonMapper;
    @Autowired
    private BasPlantMapper basPlantMapper;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private ISysDictDataService sysDictDataService;


    private static final String TITLE = "采购验货问题台账" ;

    /**
     * 查询采购验货问题台账
     *
     * @param purInspectionRecordSid 采购验货问题台账ID
     * @return 采购验货问题台账
     */
    @Override
    public QuaPurInspectionRecord selectQuaPurInspectionRecordById(Long purInspectionRecordSid) {
        QuaPurInspectionRecord quaPurInspectionRecord =quaPurInspectionRecordMapper.selectQuaPurInspectionRecordById(purInspectionRecordSid);
        getData(quaPurInspectionRecord);
        // 附件清单
        quaPurInspectionRecord.setAttachmentList(new ArrayList<>());
        List<QuaPurInspectionRecordAttach> attachList = attachMapper.selectQuaPurInspectionRecordAttachList(new QuaPurInspectionRecordAttach()
                .setPurInspectionRecordSid(purInspectionRecordSid));
        if (CollectionUtil.isNotEmpty(attachList)) {
            quaPurInspectionRecord.setAttachmentList(attachList);
        }
        MongodbUtil.find(quaPurInspectionRecord);
        return quaPurInspectionRecord;
    }

    /**
     * 查询采购验货问题台账列表
     *
     * @param quaPurInspectionRecord 采购验货问题台账
     * @return 采购验货问题台账
     */
    @Override
    public List<QuaPurInspectionRecord> selectQuaPurInspectionRecordList(QuaPurInspectionRecord quaPurInspectionRecord) {
        List<QuaPurInspectionRecord> list = quaPurInspectionRecordMapper.selectQuaPurInspectionRecordList(quaPurInspectionRecord);
        // 图片视频字段查询页面
        if (CollectionUtil.isNotEmpty(list)) {
            for (QuaPurInspectionRecord record : list) {
                getData(record);
            }
        }
        return list;
    }

    /**
     * 新增采购验货问题台账
     * 需要注意编码重复校验
     * @param quaPurInspectionRecord 采购验货问题台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertQuaPurInspectionRecord(QuaPurInspectionRecord quaPurInspectionRecord) {

        // 写默认值
        setData(quaPurInspectionRecord);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(quaPurInspectionRecord.getHandleStatus())) {
            quaPurInspectionRecord.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = quaPurInspectionRecordMapper.insert(quaPurInspectionRecord);
        if (row > 0){
            // 附件清单
            if (CollectionUtil.isNotEmpty(quaPurInspectionRecord.getAttachmentList())) {
                quaPurInspectionRecord.getAttachmentList().forEach(item->{
                    item.setPurInspectionRecordSid(quaPurInspectionRecord.getPurInspectionRecordSid());
                });
                attachMapper.inserts(quaPurInspectionRecord.getAttachmentList());
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new QuaPurInspectionRecord(), quaPurInspectionRecord);
            MongodbDeal.insert(quaPurInspectionRecord.getPurInspectionRecordSid(), quaPurInspectionRecord.getHandleStatus(), msgList, TITLE, null, BusinessType.IMPORT.getValue());
        }
        return row;
    }

    /**
     * 批量修改附件信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAttach(QuaPurInspectionRecord record) {
        // 先删后加
        attachMapper.delete(new QueryWrapper<QuaPurInspectionRecordAttach>().lambda()
                .eq(QuaPurInspectionRecordAttach::getPurInspectionRecordSid, record.getPurInspectionRecordSid()));
        if (CollectionUtil.isNotEmpty(record.getAttachmentList())) {
            record.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getPurInspectionRecordAttachSid() == null) {
                    att.setPurInspectionRecordSid(record.getPurInspectionRecordSid());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            attachMapper.inserts(record.getAttachmentList());
        }
    }

    /**
     * 修改采购验货问题台账
     *
     * @param quaPurInspectionRecord 采购验货问题台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateQuaPurInspectionRecord(QuaPurInspectionRecord quaPurInspectionRecord) {
        QuaPurInspectionRecord original = quaPurInspectionRecordMapper.selectQuaPurInspectionRecordById(quaPurInspectionRecord.getPurInspectionRecordSid());
        // 写默认值
        setData(quaPurInspectionRecord);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(quaPurInspectionRecord.getHandleStatus())) {
            quaPurInspectionRecord.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, quaPurInspectionRecord);
        if (CollectionUtil.isNotEmpty(msgList)) {
            quaPurInspectionRecord.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = quaPurInspectionRecordMapper.updateAllById(quaPurInspectionRecord);
        if (row > 0){
            // 附件清单
            updateAttach(quaPurInspectionRecord);
            //插入日志
            MongodbDeal.update(quaPurInspectionRecord.getPurInspectionRecordSid(), original.getHandleStatus(),
                    quaPurInspectionRecord.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更采购验货问题台账
     *
     * @param quaPurInspectionRecord 采购验货问题台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeQuaPurInspectionRecord(QuaPurInspectionRecord quaPurInspectionRecord) {
        QuaPurInspectionRecord response = quaPurInspectionRecordMapper.selectQuaPurInspectionRecordById(quaPurInspectionRecord.getPurInspectionRecordSid());
        // 写默认值
        setData(quaPurInspectionRecord);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, quaPurInspectionRecord);
        if (CollectionUtil.isNotEmpty(msgList)) {
            quaPurInspectionRecord.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = quaPurInspectionRecordMapper.updateAllById(quaPurInspectionRecord);
        if (row > 0){
            // 附件清单
            updateAttach(quaPurInspectionRecord);
            //插入日志
            MongodbUtil.insertUserLog(quaPurInspectionRecord.getPurInspectionRecordSid(), BusinessType.CHANGE.getValue(), response, quaPurInspectionRecord, TITLE);
        }
        return row;
    }

    /**
     * 批量删除采购验货问题台账
     *
     * @param purInspectionRecordSids 需要删除的采购验货问题台账ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteQuaPurInspectionRecordByIds(List<Long> purInspectionRecordSids) {
        List<QuaPurInspectionRecord> list = quaPurInspectionRecordMapper.selectList(new QueryWrapper<QuaPurInspectionRecord>()
                .lambda().in(QuaPurInspectionRecord::getPurInspectionRecordSid, purInspectionRecordSids));
        int row = quaPurInspectionRecordMapper.deleteBatchIds(purInspectionRecordSids);
        if (row > 0){
            // 附件清单
            attachMapper.delete(new QueryWrapper<QuaPurInspectionRecordAttach>()
                    .lambda().in(QuaPurInspectionRecordAttach::getPurInspectionRecordSid, purInspectionRecordSids));
            //操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new QuaPurInspectionRecord());
                MongodbUtil.insertUserLog(o.getPurInspectionRecordSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }


    /**
     *更改确认状态
     * @param quaPurInspectionRecord
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(QuaPurInspectionRecord quaPurInspectionRecord) {
        Long[] sids =quaPurInspectionRecord.getPurInspectionRecordSidList();
        if (ArrayUtil.isEmpty(sids)) {
            return 0;
        }
        LambdaUpdateWrapper<QuaPurInspectionRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(QuaPurInspectionRecord::getPurInspectionRecordSid, sids);
        updateWrapper.set(QuaPurInspectionRecord::getHandleStatus, quaPurInspectionRecord.getHandleStatus());
        if (ConstantsEms.CHECK_STATUS.equals(quaPurInspectionRecord.getHandleStatus())) {
            updateWrapper.set(QuaPurInspectionRecord::getConfirmDate, new Date());
            updateWrapper.set(QuaPurInspectionRecord::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        int row = quaPurInspectionRecordMapper.update(null, updateWrapper);
        if (row > 0) {
            for (Long id : sids) {
                //插入日志
                MongodbDeal.check(id, quaPurInspectionRecord.getHandleStatus(), null, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 导入
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity importRecord(MultipartFile file) {
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

            // 数据字典问题类型
            List<DictData> defectTypeList = sysDictDataService.selectDictData("s_defect_type");
            defectTypeList = defectTypeList.stream().filter(o -> o.getStatus().equals(com.platform.ems.constant.ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> defectTypeMaps = defectTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典解决状态
            List<DictData> resolveStatusList = sysDictDataService.selectDictData("s_resolve_status");
            resolveStatusList = resolveStatusList.stream().filter(o -> o.getStatus().equals(com.platform.ems.constant.ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> resolveStatusMaps = resolveStatusList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典验货方式
            List<DictData> inspectionMethodList = sysDictDataService.selectDictData("s_inspection_method");
            inspectionMethodList = inspectionMethodList.stream().filter(o -> o.getStatus().equals(com.platform.ems.constant.ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> inspectionMethodMaps = inspectionMethodList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典验货结果
            List<DictData> resultList = sysDictDataService.selectDictData("s_inspection_result");
            resultList = resultList.stream().filter(o -> o.getStatus().equals(com.platform.ems.constant.ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> resultMaps = resultList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典验货方类型
            List<DictData> inspectionPartnerTypeList = sysDictDataService.selectDictData("s_inspection_partner_type");
            inspectionPartnerTypeList = inspectionPartnerTypeList.stream().filter(o -> o.getStatus().equals(com.platform.ems.constant.ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> inspectionPartnerTypeMaps = inspectionPartnerTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 错误信息
            CommonErrMsgResponse errMsg = null;
            List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
            // 警告信息
            CommonErrMsgResponse warnMsg = null;
            List<CommonErrMsgResponse> warnMsgList = new ArrayList<>();
            // 提示信息
            CommonErrMsgResponse infoMsg = null;
            List<CommonErrMsgResponse> infoMsgList = new ArrayList<>();
            // 列表
            List<QuaPurInspectionRecord> recordList = new ArrayList<>();

            if (readAll.size() > 100) {
                throw new BaseException("导入表格中数据请不要超过100行！");
            }

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

                // 主表

                QuaPurInspectionRecord record = new QuaPurInspectionRecord();
                /*
                 * 供应商简称 必填
                 */
                String vendorShortName = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString().trim();
                /*
                 * 公司简称 必填
                 */
                String companyShortName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString().trim();
                /*
                 * 商品/物料编码 必填
                 */
                String materialCode = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString().trim();
                /*
                 * 验货方式  数据字典 必填
                 */
                String inspectionMethodName = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString().trim();
                /*
                 * 验货结果 数据字典 必填
                 */
                String resultName = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString().trim();
                /*
                 * 问题类型 数据字典 必填
                 */
                String defectTypeName = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString().trim();
                /*
                 * 问题描述 必填
                 */
                String defectDescription = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString().trim();
                /*
                 * 解决状态 字典 必填
                 */
                String resolveStatusName = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString().trim();
                /*
                 * 解决说明 选填
                 */
                String solutionRemark = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString().trim();
                /*
                 * 产品季名称 选填
                 */
                String productSeasonName = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString().trim();
                /*
                 * 工厂简称 选填
                 */
                String plantShortName = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString().trim();
                /*
                 * 验货方类型 选填
                 */
                String inspectionPartnerType = objects.get(11) == null || objects.get(11) == "" ? null : objects.get(11).toString().trim();
                /*
                 * 验货批次 选填
                 */
                String inspectionBatchS = objects.get(12) == null || objects.get(12) == "" ? null : objects.get(12).toString().trim();
                /*
                 * 验货数量 选填
                 */
                String quantityS = objects.get(13) == null || objects.get(13) == "" ? null : objects.get(13).toString().trim();
                /*
                 * 不合格数量 选填
                 */
                String bhgQuantityS = objects.get(14) == null || objects.get(14) == "" ? null : objects.get(14).toString().trim();
                /*
                 * 重验次数 选填
                 */
                String repeatInspectionNumS = objects.get(15) == null || objects.get(15) == "" ? null : objects.get(15).toString().trim();
                /*
                 * 验货说明 选填
                 */
                String inspectionRemark = objects.get(16) == null || objects.get(16) == "" ? null : objects.get(16).toString().trim();
                /*
                 * 验货人 选填
                 */
                String inspector = objects.get(17) == null || objects.get(17) == "" ? null : objects.get(17).toString().trim();
                /*
                 * 验货日期 选填
                 */
                String inspectionDateS = objects.get(18) == null || objects.get(18) == "" ? null : objects.get(18).toString().trim();
                /*
                 * 采购订单号 选填
                 */
                String purchaseOrderCode = objects.get(19) == null || objects.get(19) == "" ? null : objects.get(19).toString().trim();
                /*
                 * 采购合同号 选填
                 */
                String purchaseContractCode = objects.get(20) == null || objects.get(20) == "" ? null : objects.get(20).toString().trim();
                /*
                 * 承运方 选填
                 */
                String carrierName = objects.get(21) == null || objects.get(21) == "" ? null : objects.get(21).toString().trim();
                /*
                 * 司机 选填
                 */
                String driver = objects.get(22) == null || objects.get(22) == "" ? null : objects.get(22).toString().trim();
                /*
                 * 联系电话(司机) 选填
                 */
                String driverPhone = objects.get(23) == null || objects.get(23) == "" ? null : objects.get(23).toString().trim();
                /*
                 * 到货日期 选填
                 */
                String arrivalDateS = objects.get(24) == null || objects.get(24) == "" ? null : objects.get(24).toString().trim();
                /*
                 * 货运单号 选填
                 */
                String carrierNoteCode = objects.get(25) == null || objects.get(25) == "" ? null : objects.get(25).toString().trim();
                /*
                 * 备注 选填
                 */
                String remark = objects.get(26) == null || objects.get(26) == "" ? null : objects.get(26).toString().trim();

                //进行必填和格式的校验

                //供应商
                Long vendorSid = null;
                Long vendorCode = null;
                if(StrUtil.isBlank(vendorShortName)){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("供应商简称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else{
                    //获取档案信息校验
                    BasVendor basVendor = basVendorMapper.selectOne(new QueryWrapper<BasVendor>().lambda()
                            .eq(BasVendor::getShortName, vendorShortName));
                    if(basVendor == null || basVendor.getVendorSid() == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("供应商简称填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else if (!basVendor.getStatus().equals("1") || !basVendor.getHandleStatus().equals("5")) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("供应商简称填写错误，导入失败!");
                        errMsgList.add(errMsg);
                    } else {
                        vendorSid = basVendor.getVendorSid();
                        vendorCode = basVendor.getVendorCode();
                    }
                }

                //公司
                Long companySid = null;
                String companyCode = null;
                if (StrUtil.isBlank(companyShortName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("公司简称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else{
                    //获取档案信息校验 公司XXXX不存在 、公司XXXX必须为确认且启用的数据
                    BasCompany basCompany = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>().lambda()
                            .eq(BasCompany::getShortName, companyShortName));
                    if (basCompany == null || basCompany.getCompanySid() == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("公司简称填写错误，导入失败!");
                        errMsgList.add(errMsg);
                    } else if (!basCompany.getStatus().equals("1") || !basCompany.getHandleStatus().equals("5")) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("公司简称填写错误，导入失败!");
                        errMsgList.add(errMsg);
                    } else {
                        companySid = basCompany.getCompanySid();
                        companyCode = basCompany.getCompanyCode();
                    }
                }


                //商品/物料编码
                Long materialSid = null;
                if (StrUtil.isBlank(materialCode)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("商品/物料编码不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else{
                    try {
                        BasMaterial material = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                                .eq(BasMaterial::getMaterialCode, materialCode)
                                .eq(BasMaterial::getMaterialCategory, com.platform.ems.constant.ConstantsEms.MATERIAL_CATEGORY_SP)
                                .eq(BasMaterial::getStatus, com.platform.ems.constant.ConstantsEms.ENABLE_STATUS)
                                .eq(BasMaterial::getHandleStatus, com.platform.ems.constant.ConstantsEms.CHECK_STATUS));
                        if (material == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("商品/物料编码填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            materialSid = material.getMaterialSid();
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品编码“" + materialCode + "”系统存在多值，导入失败！");
                        errMsgList.add(errMsg);
                        log.error("采购验货问题台账Service业务层处理->导入功能->商品编码查询报错");
                        e.printStackTrace();
                    }
                }

                // 验货方式
                String inspectionMethod = null;
                if (StrUtil.isBlank(inspectionMethodName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("验货方式不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    // 通过数据字典标签获取数据字典的值
                    inspectionMethod = inspectionMethodMaps.get(inspectionMethodName);
                    if (StrUtil.isBlank(inspectionMethod)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("验货方式填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 验货结果
                String result = null;
                if (StrUtil.isBlank(resultName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("验货结果不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    // 通过数据字典标签获取数据字典的值
                    result = resultMaps.get(resultName);
                    if (StrUtil.isBlank(result)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("验货结果填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 问题类型编码
                String defectType = null;
                if (StrUtil.isBlank(defectTypeName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("问题类型不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    // 通过数据字典标签获取数据字典的值
                    defectType = defectTypeMaps.get(defectTypeName);
                    if (StrUtil.isBlank(defectType)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("问题类型填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 问题描述
                if (StrUtil.isBlank(defectDescription)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("问题描述不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    if (defectDescription.length() > 600) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("问题描述最大只能输入600位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 解决状态
                String resolveStatus = null;
                if (StrUtil.isBlank(resolveStatusName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("解决状态不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    // 通过数据字典标签获取数据字典的值
                    resolveStatus = resolveStatusMaps.get(resolveStatusName);
                    if (StrUtil.isBlank(resolveStatus)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("解决状态填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                if (StrUtil.isNotBlank(solutionRemark)) {
                    if (solutionRemark.length() > 600) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("解决说明最大只能输入600位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 产品季
                Long productSeasonSid = null;
                String productSeasonCode = null;
                if (StrUtil.isNotBlank(productSeasonName)) {
                    try {
                        BasProductSeason productSeason = basProductSeasonMapper.selectOne(new QueryWrapper<BasProductSeason>().lambda()
                                .eq(BasProductSeason::getProductSeasonName, productSeasonName)
                                .eq(BasProductSeason::getStatus, com.platform.ems.constant.ConstantsEms.ENABLE_STATUS)
                                .eq(BasProductSeason::getHandleStatus, com.platform.ems.constant.ConstantsEms.CHECK_STATUS));
                        if (productSeason == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("产品季名称填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            productSeasonSid = productSeason.getProductSeasonSid();
                            productSeasonCode = productSeason.getProductSeasonCode();
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("产品季“" + productSeasonName + "”系统存在多值，导入失败！");
                        errMsgList.add(errMsg);
                        log.error("采购验货问题台账Service业务层处理->导入功能->产品季名称查询报错");
                        e.printStackTrace();
                    }
                }

                // 工厂
                Long plantSid = null;
                String plantCode = null;
                if (StrUtil.isNotBlank(plantShortName)) {
                    try {
                        BasPlant plant = basPlantMapper.selectOne(new QueryWrapper<BasPlant>().lambda()
                                .eq(BasPlant::getShortName, plantShortName)
                                .eq(BasPlant::getStatus, com.platform.ems.constant.ConstantsEms.ENABLE_STATUS)
                                .eq(BasPlant::getHandleStatus, com.platform.ems.constant.ConstantsEms.CHECK_STATUS));
                        if (plant == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("工厂简称填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            plantSid = Long.parseLong(plant.getPlantSid());
                            plantCode = plant.getPlantCode();
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("工厂“" + productSeasonName + "”系统存在多值，导入失败！");
                        errMsgList.add(errMsg);
                        log.error("采购验货问题台账Service业务层处理->导入功能->工厂简称查询报错");
                        e.printStackTrace();
                    }
                }

                // 验货批次
                Long inspectionBatch = null;
                if (StrUtil.isNotBlank(inspectionBatchS)) {
                    if (!JudgeFormat.isPositiveInteger(inspectionBatchS)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("验货批次格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        inspectionBatch = Long.parseLong(inspectionBatchS);
                    }
                }

                // 验货数量
                Long quantity = null;
                if (StrUtil.isNotBlank(quantityS)) {
                    if (!JudgeFormat.isPositiveInteger(quantityS)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("验货数量格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        quantity = Long.parseLong(quantityS);
                    }
                }
                // 不合格数量
                Long bhgQuantity = null;
                if (StrUtil.isNotBlank(bhgQuantityS)) {
                    if (!JudgeFormat.isPositiveInteger(bhgQuantityS)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("不合格数量格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        bhgQuantity = Long.parseLong(bhgQuantityS);
                    }
                }

                // 重验次数
                Long repeatInspectionNum = null;
                if (StrUtil.isNotBlank(repeatInspectionNumS)) {
                    if (!JudgeFormat.isPositiveInteger(repeatInspectionNumS)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("重验次数格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        repeatInspectionNum = Long.parseLong(repeatInspectionNumS);
                    }
                }
                if (StrUtil.isNotBlank(inspectionRemark)) {
                    if (inspectionRemark.length() > 600) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("验货说明最大只能输入600位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                if (StrUtil.isNotBlank(inspector)) {
                    if (inspector.length() > 30) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("验货人最大只能输入30位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                // 验货日期
                Date inspectionDate = null;
                if (StrUtil.isNotBlank(inspectionDateS)) {
                    if (!JudgeFormat.isValidDate(inspectionDateS)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("验货日期格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        inspectionDate = new Date();
                        try {
                            inspectionDate = DateUtil.parse(inspectionDateS);
                        } catch (Exception e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("验货日期格式错误，导入失败！");
                            errMsgList.add(errMsg);
                            inspectionDate = null;
                        }
                    }
                }


                if (StrUtil.isNotBlank(inspectionPartnerType)) {
                    inspectionPartnerType = inspectionPartnerTypeMaps.get(inspectionPartnerType);
                    if (StrUtil.isBlank(inspectionPartnerType)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("验货方类型填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }


                if (StrUtil.isNotBlank(purchaseOrderCode)) {
                    if (purchaseOrderCode.length() > 30) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("采购订单号最大只能输入30位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                if (StrUtil.isNotBlank(purchaseContractCode)) {
                    if (purchaseContractCode.length() > 30) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("采购合同号最大只能输入30位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }


                if (StrUtil.isNotBlank(carrierName)) {
                    if (carrierName.length() > 300) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("承运方最大只能输入300位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                if (StrUtil.isNotBlank(driver)) {
                    if (driver.length() > 20) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("司机最大只能输入20位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                if (StrUtil.isNotBlank(driverPhone)) {
                    if (driverPhone.length() > 60) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("联系方式（司机）不能超过60个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 到货日期
                Date arrivalDate = null;
                if (StrUtil.isNotBlank(arrivalDateS)) {
                    if (!JudgeFormat.isValidDate(arrivalDateS)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("到货日期格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        arrivalDate = new Date();
                        try {
                            arrivalDate = DateUtil.parse(arrivalDateS);
                        } catch (Exception e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("到货日期格式错误，导入失败！");
                            errMsgList.add(errMsg);
                            arrivalDate = null;
                        }
                    }
                }

                if (StrUtil.isNotBlank(carrierNoteCode)) {
                    if (carrierNoteCode.length() > 20) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("货运单号最大只能输入20位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                if (StrUtil.isNotBlank(remark)) {
                    if (remark.length() > 600) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("备注只能输入600位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 写入数据
                if(CollectionUtil.isEmpty(errMsgList)){
                    record.setVendorSid(vendorSid).setVendorCode(vendorCode).setCompanySid(companySid).setCompanyCode(companyCode)
                            .setMaterialCode(materialCode).setMaterialSid(materialSid).setInspectionMethod(inspectionMethod)
                            .setResult(result).setDefectType(defectType).setDefectDescription(defectDescription)
                            .setResolveStatus(resolveStatus).setSolutionRemark(solutionRemark)
                            .setProductSeasonSid(productSeasonSid).setProductSeasonCode(productSeasonCode)
                            .setPlantSid(plantSid).setPlantCode(plantCode).setInspectionPartnerType(inspectionPartnerType)
                            .setInspectionBatch(inspectionBatch).setQuantity(quantity).setBhgQuantity(bhgQuantity)
                            .setRepeatInspectionNum(repeatInspectionNum).setInspectionRemark(inspectionRemark)
                            .setInspector(inspector).setInspectionDate(inspectionDate).setPurchaseContractCode(purchaseContractCode)
                            .setPurchaseOrderCode(purchaseOrderCode).setCarrierName(carrierName).setHandleStatus(com.platform.ems.constant.ConstantsEms.SAVA_STATUS)
                            .setDriver(driver).setDriverPhone(driverPhone).setArrivalDate(arrivalDate)
                            .setCarrierNoteCode(carrierNoteCode).setRemark(remark);
                    recordList.add(record);
                }
            }

            // 报错
            if (CollectionUtil.isNotEmpty(errMsgList)) {
                return EmsResultEntity.error(errMsgList);
            }
            else if (CollectionUtil.isNotEmpty(warnMsgList)) {
                String message = null;
                if (CollectionUtil.isNotEmpty(infoMsgList)) {
                    message = "";
                }
                return EmsResultEntity.warning(recordList, warnMsgList, infoMsgList, message);
            }
            else if (CollectionUtil.isNotEmpty(recordList)) {
                for (QuaPurInspectionRecord record : recordList) {
                    insertQuaPurInspectionRecord(record);
                }
                String message = null;
                if (CollectionUtil.isNotEmpty(infoMsgList)) {
                    message = "";
                }
                return EmsResultEntity.success(recordList.size(), null, infoMsgList, message);
            }

        }catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
            return EmsResultEntity.success();
    }


    /**
     * 读取数据字段处理
     */
    private void getData(QuaPurInspectionRecord record) {
        if (record == null) {
            return;
        }
        // 图片
        if (StrUtil.isNotBlank(record.getPicturePath())) {
            record.setPicturePathList(record.getPicturePath().split(";"));
        }
        // 视频
        if (StrUtil.isNotBlank(record.getVideoPath())) {
            record.setVideoPathList(record.getVideoPath().split(";"));
        }
    }

    /**
     * 写入数据字段处理
     */
    private void setData(QuaPurInspectionRecord record) {
        // 图片
        String picture = null;
        if (ArrayUtil.isNotEmpty(record.getPicturePathList())) {
            picture = "";
            for (int i = 0; i < record.getPicturePathList().length; i++) {
                picture = picture + record.getPicturePathList()[i] + ";";
            }
        }
        record.setPicturePath(picture);
        // 视频
        String video = null;
        if (ArrayUtil.isNotEmpty(record.getVideoPathList())) {
            video = "";
            for (int i = 0; i < record.getVideoPathList().length; i++) {
                video = video + record.getVideoPathList()[i] + ";";
            }
        }
        record.setVideoPath(video);
        //
        record.setProductSeasonCode(null);
        if (record.getProductSeasonSid() != null) {
            BasProductSeason season = basProductSeasonMapper.selectById(record.getProductSeasonSid());
            if (season != null) {
                record.setProductSeasonCode(season.getProductSeasonCode());
            }
        }
        record.setVendorCode(null);
        if (record.getVendorSid() != null) {
            BasVendor vendor = basVendorMapper.selectById(record.getVendorSid());
            if (vendor != null) {
                record.setVendorCode(vendor.getVendorCode());
            }
        }
        record.setPlantCode(null);
        if (record.getPlantSid() != null) {
            BasPlant plant = basPlantMapper.selectById(record.getPlantSid());
            if (plant != null) {
                record.setPlantCode(String.valueOf(plant.getPlantCode()));
            }
        }

        record.setCompanyCode(null);
        if (record.getPlantSid() != null) {
            BasCompany company = basCompanyMapper.selectById(record.getCompanySid());
            if (company != null) {
                record.setCompanyCode(String.valueOf(company.getCompanyCode()));
            }
        }
    }

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
