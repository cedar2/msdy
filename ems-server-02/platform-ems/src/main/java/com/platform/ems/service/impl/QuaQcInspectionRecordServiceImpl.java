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
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.mapper.*;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.service.ISysDictDataService;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.service.IQuaQcInspectionRecordService;
import org.springframework.web.multipart.MultipartFile;

/**
 * QC验货问题台账Service业务层处理
 *
 * @author platform
 * @date 2024-03-06
 */
@Service
@SuppressWarnings("all")
public class QuaQcInspectionRecordServiceImpl extends ServiceImpl<QuaQcInspectionRecordMapper, QuaQcInspectionRecord> implements IQuaQcInspectionRecordService {
    @Autowired
    private QuaQcInspectionRecordMapper quaQcInspectionRecordMapper;
    @Autowired
    private QuaQcInspectionRecordAttachMapper attachMapper;
    @Autowired
    private BasProductSeasonMapper basProductSeasonMapper;
    @Autowired
    private BasPlantMapper basPlantMapper;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private ISysDictDataService sysDictDataService;

    private static final String TITLE = "QC验货问题台账";

    /**
     * 查询QC验货问题台账
     *
     * @param qcInspectionRecordSid QC验货问题台账ID
     * @return QC验货问题台账
     */
    @Override
    public QuaQcInspectionRecord selectQuaQcInspectionRecordById(Long qcInspectionRecordSid) {
        QuaQcInspectionRecord quaQcInspectionRecord = quaQcInspectionRecordMapper.selectQuaQcInspectionRecordById(qcInspectionRecordSid);
        // 特殊字段处理
        getData(quaQcInspectionRecord);
        // 附件清单
        quaQcInspectionRecord.setAttachmentList(new ArrayList<>());
        List<QuaQcInspectionRecordAttach> attachList = attachMapper.selectQuaQcInspectionRecordAttachList(new QuaQcInspectionRecordAttach()
                .setQcInspectionRecordSid(qcInspectionRecordSid));
        if (CollectionUtil.isNotEmpty(attachList)) {
            quaQcInspectionRecord.setAttachmentList(attachList);
        }
        // 默认
        MongodbUtil.find(quaQcInspectionRecord);
        return quaQcInspectionRecord;
    }

    /**
     * 查询QC验货问题台账列表
     *
     * @param quaQcInspectionRecord QC验货问题台账
     * @return QC验货问题台账
     */
    @Override
    public List<QuaQcInspectionRecord> selectQuaQcInspectionRecordList(QuaQcInspectionRecord quaQcInspectionRecord) {
        List<QuaQcInspectionRecord> list = quaQcInspectionRecordMapper.selectQuaQcInspectionRecordList(quaQcInspectionRecord);
        // 图片视频字段查询页面要
        if (CollectionUtil.isNotEmpty(list)) {
            for (QuaQcInspectionRecord record : list) {
                getData(record);
            }
        }
        return list;
    }

    /**
     * 写入数据字段处理
     */
    private void setData(QuaQcInspectionRecord record) {
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
                record.setVendorCode(String.valueOf(vendor.getVendorCode()));
            }
        }
        record.setPlantCode(null);
        if (record.getPlantSid() != null) {
            BasPlant plant = basPlantMapper.selectById(record.getPlantSid());
            if (plant != null) {
                record.setPlantCode(String.valueOf(plant.getPlantCode()));
            }
        }
    }

    /**
     * 读取数据字段处理
     */
    private void getData(QuaQcInspectionRecord record) {
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
     * 新增QC验货问题台账
     * 需要注意编码重复校验
     *
     * @param quaQcInspectionRecord QC验货问题台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertQuaQcInspectionRecord(QuaQcInspectionRecord quaQcInspectionRecord) {
        // 写默认值
        setData(quaQcInspectionRecord);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(quaQcInspectionRecord.getHandleStatus())) {
            quaQcInspectionRecord.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = quaQcInspectionRecordMapper.insert(quaQcInspectionRecord);
        if (row > 0) {
            // 附件清单
            if (CollectionUtil.isNotEmpty(quaQcInspectionRecord.getAttachmentList())) {
                quaQcInspectionRecord.getAttachmentList().forEach(item->{
                    item.setQcInspectionRecordSid(quaQcInspectionRecord.getQcInspectionRecordSid());
                });
                attachMapper.inserts(quaQcInspectionRecord.getAttachmentList());
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new QuaQcInspectionRecord(), quaQcInspectionRecord);
            MongodbDeal.insert(quaQcInspectionRecord.getQcInspectionRecordSid(), quaQcInspectionRecord.getHandleStatus(), msgList, TITLE, null, BusinessType.IMPORT.getValue());
        }
        return row;
    }

    /**
     * 批量修改附件信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAttach(QuaQcInspectionRecord record) {
        // 先删后加
        attachMapper.delete(new QueryWrapper<QuaQcInspectionRecordAttach>().lambda()
                .eq(QuaQcInspectionRecordAttach::getQcInspectionRecordSid, record.getQcInspectionRecordSid()));
        if (CollectionUtil.isNotEmpty(record.getAttachmentList())) {
            record.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getQcInspectionRecordAttachSid() == null) {
                    att.setQcInspectionRecordSid(record.getQcInspectionRecordSid());
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
     * 修改QC验货问题台账
     *
     * @param quaQcInspectionRecord QC验货问题台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateQuaQcInspectionRecord(QuaQcInspectionRecord quaQcInspectionRecord) {
        QuaQcInspectionRecord original = quaQcInspectionRecordMapper.selectQuaQcInspectionRecordById(quaQcInspectionRecord.getQcInspectionRecordSid());
        // 写默认值
        setData(quaQcInspectionRecord);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(quaQcInspectionRecord.getHandleStatus())) {
            quaQcInspectionRecord.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, quaQcInspectionRecord);
        if (CollectionUtil.isNotEmpty(msgList)) {
            quaQcInspectionRecord.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = quaQcInspectionRecordMapper.updateAllById(quaQcInspectionRecord);
        if (row > 0) {
            // 附件清单
            updateAttach(quaQcInspectionRecord);
            //插入日志
            MongodbDeal.update(quaQcInspectionRecord.getQcInspectionRecordSid(), original.getHandleStatus(),
                    quaQcInspectionRecord.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更QC验货问题台账
     *
     * @param quaQcInspectionRecord QC验货问题台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeQuaQcInspectionRecord(QuaQcInspectionRecord quaQcInspectionRecord) {
        QuaQcInspectionRecord response = quaQcInspectionRecordMapper.selectQuaQcInspectionRecordById(quaQcInspectionRecord.getQcInspectionRecordSid());
        // 写默认值
        setData(quaQcInspectionRecord);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, quaQcInspectionRecord);
        if (CollectionUtil.isNotEmpty(msgList)) {
            quaQcInspectionRecord.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = quaQcInspectionRecordMapper.updateAllById(quaQcInspectionRecord);
        if (row > 0) {
            // 附件清单
            updateAttach(quaQcInspectionRecord);
            //插入日志
            MongodbUtil.insertUserLog(quaQcInspectionRecord.getQcInspectionRecordSid(), BusinessType.CHANGE.getValue(), response, quaQcInspectionRecord, TITLE);
        }
        return row;
    }

    /**
     * 批量删除QC验货问题台账
     *
     * @param qcInspectionRecordSids 需要删除的QC验货问题台账ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteQuaQcInspectionRecordByIds(List<Long> qcInspectionRecordSids) {
        List<QuaQcInspectionRecord> list = quaQcInspectionRecordMapper.selectList(new QueryWrapper<QuaQcInspectionRecord>()
                .lambda().in(QuaQcInspectionRecord::getQcInspectionRecordSid, qcInspectionRecordSids));
        int row = quaQcInspectionRecordMapper.deleteBatchIds(qcInspectionRecordSids);
        if (row > 0) {
            // 附件清单
            attachMapper.delete(new QueryWrapper<QuaQcInspectionRecordAttach>()
                    .lambda().in(QuaQcInspectionRecordAttach::getQcInspectionRecordSid, qcInspectionRecordSids));
            // 操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new QuaQcInspectionRecord());
                MongodbUtil.insertUserLog(o.getQcInspectionRecordSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param quaQcInspectionRecord
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(QuaQcInspectionRecord quaQcInspectionRecord) {
        Long[] sids = quaQcInspectionRecord.getQcInspectionRecordSidList();
        if (ArrayUtil.isEmpty(sids)) {
            return 0;
        }
        LambdaUpdateWrapper<QuaQcInspectionRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(QuaQcInspectionRecord::getQcInspectionRecordSid, sids);
        updateWrapper.set(QuaQcInspectionRecord::getHandleStatus, quaQcInspectionRecord.getHandleStatus());
        if (ConstantsEms.CHECK_STATUS.equals(quaQcInspectionRecord.getHandleStatus())) {
            updateWrapper.set(QuaQcInspectionRecord::getConfirmDate, new Date());
            updateWrapper.set(QuaQcInspectionRecord::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        int row = quaQcInspectionRecordMapper.update(null, updateWrapper);
        if (row > 0) {
            for (Long id : sids) {
                //插入日志
                MongodbDeal.check(id, quaQcInspectionRecord.getHandleStatus(), null, TITLE, null);
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

            // 数据字典验货阶段
            List<DictData> inspectionStageList = sysDictDataService.selectDictData("s_inspection_stage");
            inspectionStageList = inspectionStageList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> inspectionStageMaps = inspectionStageList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典问题类型
            List<DictData> defectTypeList = sysDictDataService.selectDictData("s_defect_type");
            defectTypeList = defectTypeList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> defectTypeMaps = defectTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典解决状态
            List<DictData> resolveStatusList = sysDictDataService.selectDictData("s_resolve_status");
            resolveStatusList = resolveStatusList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> resolveStatusMaps = resolveStatusList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典验货方式
            List<DictData> inspectionMethodList = sysDictDataService.selectDictData("s_inspection_method");
            inspectionMethodList = inspectionMethodList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> inspectionMethodMaps = inspectionMethodList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典验货结果
            List<DictData> resultList = sysDictDataService.selectDictData("s_inspection_result");
            resultList = resultList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> resultMaps = resultList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典验货结果
            List<DictData> inspectionPartnerTypeList = sysDictDataService.selectDictData("s_inspection_partner_type");
            inspectionPartnerTypeList = inspectionPartnerTypeList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
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
            List<QuaQcInspectionRecord> recordList = new ArrayList<>();

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
                QuaQcInspectionRecord record = new QuaQcInspectionRecord();
                /*
                 * 商品编码 必填
                 */
                String materialCode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString().trim();
                /*
                 * 商品颜色 选填
                 */
                String colorName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString().trim();
                /*
                 * 验货阶段 数据字典 必填
                 */
                String inspectionStageName = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString().trim();
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
                 * 验货批次 选填
                 */
                String inspectionBatchS = objects.get(11) == null || objects.get(11) == "" ? null : objects.get(11).toString().trim();
                /*
                 * 验货数量 选填
                 */
                String quantityS = objects.get(12) == null || objects.get(12) == "" ? null : objects.get(12).toString().trim();
                /*
                 * 不合格数量 选填
                 */
                String bhgQuantityS = objects.get(13) == null || objects.get(13) == "" ? null : objects.get(13).toString().trim();
                /*
                 * 重验次数 选填
                 */
                String repeatInspectionNumS = objects.get(14) == null || objects.get(14) == "" ? null : objects.get(14).toString().trim();
                /*
                 * 验货说明 选填
                 */
                String inspectionRemark = objects.get(15) == null || objects.get(15) == "" ? null : objects.get(15).toString().trim();
                /*
                 * 验货人 选填
                 */
                String inspector = objects.get(16) == null || objects.get(16) == "" ? null : objects.get(16).toString().trim();
                /*
                 * 验货日期 选填
                 */
                String inspectionDateS = objects.get(17) == null || objects.get(17) == "" ? null : objects.get(17).toString().trim();
                /*
                 * 验货方类型 选填
                 */
                String inspectionPartnerType = objects.get(18) == null || objects.get(18) == "" ? null : objects.get(18).toString().trim();
                /*
                 * 排产批次号 选填
                 */
                String paichanBatchS = objects.get(19) == null || objects.get(19) == "" ? null : objects.get(19).toString().trim();
                /*
                 * 备注 选填
                 */
                String remark = objects.get(20) == null || objects.get(20) == "" ? null : objects.get(20).toString().trim();

                // 商品编码
                Long materialSid = null;
                if (StrUtil.isBlank(materialCode)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("商品编码不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        BasMaterial material = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                                .eq(BasMaterial::getMaterialCode, materialCode)
                                .eq(BasMaterial::getMaterialCategory, ConstantsEms.MATERIAL_CATEGORY_SP)
                                .eq(BasMaterial::getStatus, ConstantsEms.ENABLE_STATUS)
                                .eq(BasMaterial::getHandleStatus, ConstantsEms.CHECK_STATUS));
                        if (material == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("商品编码填写错误，导入失败！");
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
                        log.error("QC验货问题台账Service业务层处理->导入功能->商品编码查询报错");
                        e.printStackTrace();
                    }
                }

                if (StrUtil.isNotBlank(colorName)) {
                    if (colorName.length() > 30) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品颜色最大只能输入30位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 验货阶段
                String inspectionStage = null;
                if (StrUtil.isBlank(inspectionStageName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("验货阶段不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    // 通过数据字典标签获取数据字典的值
                    inspectionStage = inspectionStageMaps.get(inspectionStageName);
                    if (StrUtil.isBlank(inspectionStage)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("验货阶段填写错误，导入失败！");
                        errMsgList.add(errMsg);
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

                // 解决状态编码
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
                                .eq(BasProductSeason::getStatus, ConstantsEms.ENABLE_STATUS)
                                .eq(BasProductSeason::getHandleStatus, ConstantsEms.CHECK_STATUS));
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
                        log.error("QC验货问题台账Service业务层处理->导入功能->产品季名称查询报错");
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
                                .eq(BasPlant::getStatus, ConstantsEms.ENABLE_STATUS)
                                .eq(BasPlant::getHandleStatus, ConstantsEms.CHECK_STATUS));
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
                        log.error("QC验货问题台账Service业务层处理->导入功能->工厂简称查询报错");
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
                    if (inspector.length() > 20) {
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
                // 排产批次号
                Long paichanBatch = null;
                if (StrUtil.isNotBlank(paichanBatchS)) {
                    if (!JudgeFormat.isPositiveInteger(paichanBatchS)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("排产批次号格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        paichanBatch = Long.parseLong(paichanBatchS);
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
                if (CollectionUtil.isEmpty(errMsgList)) {
                    record.setMaterialCode(materialCode).setMaterialSid(materialSid).setInspectionMethod(inspectionMethod)
                            .setColorName(colorName).setResult(result).setInspectionStage(inspectionStage)
                            .setQuantity(quantity).setDefectType(defectType).setDefectDescription(defectDescription)
                            .setResolveStatus(resolveStatus).setSolutionRemark(solutionRemark).setInspector(inspector)
                            .setInspectionDate(inspectionDate).setInspectionPartnerType(inspectionPartnerType)
                            .setProductSeasonSid(productSeasonSid).setProductSeasonCode(productSeasonCode)
                            .setPlantSid(plantSid).setPlantCode(plantCode).setInspectionRemark(inspectionRemark)
                            .setInspectionBatch(inspectionBatch).setRepeatInspectionNum(repeatInspectionNum)
                            .setBhgQuantity(bhgQuantity).setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setPaichanBatch(paichanBatch).setRemark(remark);
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
                for (QuaQcInspectionRecord record : recordList) {
                    insertQuaQcInspectionRecord(record);
                }
                String message = null;
                if (CollectionUtil.isNotEmpty(infoMsgList)) {
                    message = "";
                }
                return EmsResultEntity.success(recordList.size(), null, infoMsgList, message);
            }

        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return EmsResultEntity.success();
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
