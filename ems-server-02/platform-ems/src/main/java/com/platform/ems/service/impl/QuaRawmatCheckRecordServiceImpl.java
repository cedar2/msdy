package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;

import java.io.File;
import java.util.*;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.service.IQuaRawmatCheckRecordService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 物料检测问题台账Service业务层处理
 *
 * @author platform
 * @date 2024-03-06
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class QuaRawmatCheckRecordServiceImpl extends ServiceImpl<QuaRawmatCheckRecordMapper, QuaRawmatCheckRecord> implements IQuaRawmatCheckRecordService {
    @Autowired
    private QuaRawmatCheckRecordMapper quaRawmatCheckRecordMapper;
    @Autowired
    private QuaRawmatCheckRecordAttachMapper attachMapper;
    @Autowired
    private BasProductSeasonMapper basProductSeasonMapper;
    @Autowired
    private BasPlantMapper basPlantMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private ConCheckItemMapper conCheckItemMapper;
    @Autowired
    private ConCheckStandardMapper conCheckStandardMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static final String TITLE = "物料检测问题台账";

    /**
     * 查询物料检测问题台账
     *
     * @param rawmatCheckRecordSid 物料检测问题台账ID
     * @return 物料检测问题台账
     */
    @Override
    public QuaRawmatCheckRecord selectQuaRawmatCheckRecordById(Long rawmatCheckRecordSid) {
        QuaRawmatCheckRecord quaRawmatCheckRecord = quaRawmatCheckRecordMapper.selectQuaRawmatCheckRecordById(rawmatCheckRecordSid);
        // 特殊字段处理
        getData(quaRawmatCheckRecord);
        // 附件
        List<QuaRawmatCheckRecordAttach> attachList = attachMapper.selectQuaRawmatCheckRecordAttachList(new QuaRawmatCheckRecordAttach()
                .setRawmatCheckRecordSid(rawmatCheckRecordSid));
        quaRawmatCheckRecord.setAttachmentList(new ArrayList<>());
        if (CollectionUtil.isNotEmpty(attachList)) {
            quaRawmatCheckRecord.setAttachmentList(attachList);
        }
        // 操作日志
        MongodbUtil.find(quaRawmatCheckRecord);
        return quaRawmatCheckRecord;
    }

    /**
     * 写入数据字段处理
     */
    private void setData(QuaRawmatCheckRecord record) {
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
    }

    /**
     * 读取数据字段处理
     */
    private void getData(QuaRawmatCheckRecord record) {
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
     * 查询物料检测问题台账列表
     *
     * @param quaRawmatCheckRecord 物料检测问题台账
     * @return 物料检测问题台账
     */
    @Override
    public List<QuaRawmatCheckRecord> selectQuaRawmatCheckRecordList(QuaRawmatCheckRecord quaRawmatCheckRecord) {
        List<QuaRawmatCheckRecord> recordList = quaRawmatCheckRecordMapper.selectQuaRawmatCheckRecordList(quaRawmatCheckRecord);
        // 图片视频字段查询页面要
        if (CollectionUtil.isNotEmpty(recordList)) {
            for (QuaRawmatCheckRecord record : recordList) {
                getData(record);
            }
        }
        return recordList;
    }

    /**
     * 新增物料检测问题台账
     * 需要注意编码重复校验
     *
     * @param quaRawmatCheckRecord 物料检测问题台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertQuaRawmatCheckRecord(QuaRawmatCheckRecord quaRawmatCheckRecord) {
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(quaRawmatCheckRecord.getHandleStatus())) {
            quaRawmatCheckRecord.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 特殊字段处理
        setData(quaRawmatCheckRecord);
        int row = quaRawmatCheckRecordMapper.insert(quaRawmatCheckRecord);
        if (row > 0) {
            // 附件清单
            if (CollectionUtil.isNotEmpty(quaRawmatCheckRecord.getAttachmentList())) {
                quaRawmatCheckRecord.getAttachmentList().forEach(item->{
                    item.setRawmatCheckRecordSid(quaRawmatCheckRecord.getRawmatCheckRecordSid());
                });
                attachMapper.inserts(quaRawmatCheckRecord.getAttachmentList());
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new QuaRawmatCheckRecord(), quaRawmatCheckRecord);
            MongodbDeal.insert(quaRawmatCheckRecord.getRawmatCheckRecordSid(), quaRawmatCheckRecord.getHandleStatus(), msgList, TITLE, null, BusinessType.IMPORT.getValue());
        }
        return row;
    }

    /**
     * 批量修改附件信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAttach(QuaRawmatCheckRecord record) {
        // 先删后加
        attachMapper.delete(new QueryWrapper<QuaRawmatCheckRecordAttach>().lambda()
                .eq(QuaRawmatCheckRecordAttach::getRawmatCheckRecordSid, record.getRawmatCheckRecordSid()));
        if (CollectionUtil.isNotEmpty(record.getAttachmentList())) {
            record.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getRawmatCheckRecordAttachSid() == null) {
                    att.setRawmatCheckRecordSid(record.getRawmatCheckRecordSid());
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
     * 修改物料检测问题台账
     *
     * @param quaRawmatCheckRecord 物料检测问题台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateQuaRawmatCheckRecord(QuaRawmatCheckRecord quaRawmatCheckRecord) {
        QuaRawmatCheckRecord original = quaRawmatCheckRecordMapper.selectQuaRawmatCheckRecordById(quaRawmatCheckRecord.getRawmatCheckRecordSid());
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(quaRawmatCheckRecord.getHandleStatus())) {
            quaRawmatCheckRecord.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 特殊字段处理
        setData(quaRawmatCheckRecord);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, quaRawmatCheckRecord);
        if (CollectionUtil.isNotEmpty(msgList)) {
            quaRawmatCheckRecord.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = quaRawmatCheckRecordMapper.updateAllById(quaRawmatCheckRecord);
        if (row > 0) {
            // 附件清单
            updateAttach(quaRawmatCheckRecord);
            //插入日志
            MongodbDeal.update(quaRawmatCheckRecord.getRawmatCheckRecordSid(), original.getHandleStatus(),
                    quaRawmatCheckRecord.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更物料检测问题台账
     *
     * @param quaRawmatCheckRecord 物料检测问题台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeQuaRawmatCheckRecord(QuaRawmatCheckRecord quaRawmatCheckRecord) {
        QuaRawmatCheckRecord response = quaRawmatCheckRecordMapper.selectQuaRawmatCheckRecordById(quaRawmatCheckRecord.getRawmatCheckRecordSid());
        // 特殊字段处理
        setData(quaRawmatCheckRecord);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, quaRawmatCheckRecord);
        if (CollectionUtil.isNotEmpty(msgList)) {
            quaRawmatCheckRecord.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = quaRawmatCheckRecordMapper.updateAllById(quaRawmatCheckRecord);
        if (row > 0) {
            // 附件清单
            updateAttach(quaRawmatCheckRecord);
            //插入日志
            MongodbUtil.insertUserLog(quaRawmatCheckRecord.getRawmatCheckRecordSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 批量删除物料检测问题台账
     *
     * @param rawmatCheckRecordSids 需要删除的物料检测问题台账ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteQuaRawmatCheckRecordByIds(List<Long> rawmatCheckRecordSids) {
        List<QuaRawmatCheckRecord> list = quaRawmatCheckRecordMapper.selectList(new QueryWrapper<QuaRawmatCheckRecord>()
                .lambda().in(QuaRawmatCheckRecord::getRawmatCheckRecordSid, rawmatCheckRecordSids));
        int row = quaRawmatCheckRecordMapper.deleteBatchIds(rawmatCheckRecordSids);
        if (row > 0) {
            // 附件
            attachMapper.delete(new QueryWrapper<QuaRawmatCheckRecordAttach>()
                    .lambda().in(QuaRawmatCheckRecordAttach::getRawmatCheckRecordSid, rawmatCheckRecordSids));
            // 操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new QuaRawmatCheckRecord());
                MongodbUtil.insertUserLog(o.getRawmatCheckRecordSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param quaRawmatCheckRecord
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(QuaRawmatCheckRecord quaRawmatCheckRecord) {
        Long[] sids = quaRawmatCheckRecord.getRawmatCheckRecordSidList();
        if (ArrayUtil.isEmpty(sids)) {
            return 0;
        }
        LambdaUpdateWrapper<QuaRawmatCheckRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(QuaRawmatCheckRecord::getRawmatCheckRecordSid, sids);
        updateWrapper.set(QuaRawmatCheckRecord::getHandleStatus, quaRawmatCheckRecord.getHandleStatus());
        if (ConstantsEms.CHECK_STATUS.equals(quaRawmatCheckRecord.getHandleStatus())) {
            updateWrapper.set(QuaRawmatCheckRecord::getConfirmDate, new Date());
            updateWrapper.set(QuaRawmatCheckRecord::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        int row = quaRawmatCheckRecordMapper.update(null, updateWrapper);
        if (row > 0) {
            for (Long id : sids) {
                //插入日志
                MongodbDeal.check(id, quaRawmatCheckRecord.getHandleStatus(), null, TITLE, null);
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

            // 数据字典判定结果
            List<DictData> resultList = sysDictDataService.selectDictData("s_check_result");
            resultList = resultList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> resultMaps = resultList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典样品类别
            List<DictData> sampleCategoryList = sysDictDataService.selectDictData("s_sample_category");
            sampleCategoryList = sampleCategoryList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> sampleCategoryMaps = sampleCategoryList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典问题类型
            List<DictData> defectTypeList = sysDictDataService.selectDictData("s_defect_type");
            defectTypeList = defectTypeList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> defectTypeMaps = defectTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典解决状态
            List<DictData> resolveStatusList = sysDictDataService.selectDictData("s_resolve_status");
            resolveStatusList = resolveStatusList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> resolveStatusMaps = resolveStatusList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典检测类型
            List<DictData> checkTypeList = sysDictDataService.selectDictData("s_check_type");
            checkTypeList = checkTypeList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> checkTypeMaps = checkTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典检测方类型
            List<DictData> checkPartnerList = sysDictDataService.selectDictData("s_check_partner");
            checkPartnerList = checkPartnerList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> checkPartnerMaps = checkPartnerList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典等级
            List<DictData> gradeList = sysDictDataService.selectDictData("s_grade_product");
            gradeList = gradeList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> gradeMaps = gradeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典安全类别
            List<DictData> safeCategoryList = sysDictDataService.selectDictData("s_safe_category");
            safeCategoryList = safeCategoryList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> safeCategoryMaps = safeCategoryList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

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
            List<QuaRawmatCheckRecord> recordList = new ArrayList<>();

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
                QuaRawmatCheckRecord record = new QuaRawmatCheckRecord();
                /*
                 * 物料编码 必填
                 */
                String materialCode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString().trim();
                /*
                 * 物料颜色 选填
                 */
                String colorName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString().trim();
                /*
                 * 检测项名称 配置档案 必填
                 */
                String checkItemName = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString().trim();
                /*
                 * 判定结果 数据字典 必填
                 */
                String resultName = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString().trim();
                /*
                 * 检测结果值 必填
                 */
                String checkResultValue = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString().trim();
                /*
                 * 样品类别 字典 必填
                 */
                String sampleCategoryName = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString().trim();
                /*
                 * 样品数量 必填
                 */
                String quantityS = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString().trim();
                /*
                 * 问题类型 字典 必填
                 */
                String defectTypeName = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString().trim();
                /*
                 * 问题描述 必填
                 */
                String defectDescription = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString().trim();
                /*
                 * 解决状态 字典 必填
                 */
                String resolveStatusName = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString().trim();
                /*
                 * 解决说明 选填
                 */
                String solutionRemark = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString().trim();
                /*
                 * 检测参考值 选填
                 */
                String checkReferenceValue = objects.get(11) == null || objects.get(11) == "" ? null : objects.get(11).toString().trim();
                /*
                 * 产品季名称 选填
                 */
                String productSeasonName = objects.get(12) == null || objects.get(12) == "" ? null : objects.get(12).toString().trim();
                /*
                 * 工厂简称 选填
                 */
                String plantShortName = objects.get(13) == null || objects.get(13) == "" ? null : objects.get(13).toString().trim();
                /*
                 * 检测类型 字典 选填
                 */
                String checkTypeName = objects.get(14) == null || objects.get(14) == "" ? null : objects.get(14).toString().trim();
                /*
                 * 检测标准名称 字典 选填
                 */
                String checkStandardName = objects.get(15) == null || objects.get(15) == "" ? null : objects.get(15).toString().trim();
                /*
                 * 检测批次 选填
                 */
                String checkBatchS = objects.get(16) == null || objects.get(16) == "" ? null : objects.get(16).toString().trim();
                /*
                 * 供应商名称 选填
                 */
                String vendorName = objects.get(17) == null || objects.get(17) == "" ? null : objects.get(17).toString().trim();
                /*
                 * 供方编码 选填
                 */
                String supplierProductCode = objects.get(18) == null || objects.get(18) == "" ? null : objects.get(18).toString().trim();
                /*
                 * 供方色号 选填
                 */
                String supplierProductColor = objects.get(19) == null || objects.get(19) == "" ? null : objects.get(19).toString().trim();
                /*
                 * 报告号 选填
                 */
                String reportCode = objects.get(20) == null || objects.get(20) == "" ? null : objects.get(20).toString().trim();
                /*
                 * 检测方类型 选填
                 */
                String checkPartner = objects.get(21) == null || objects.get(21) == "" ? null : objects.get(21).toString().trim();
                /*
                 * 检测日期 选填
                 */
                String checkDateS = objects.get(22) == null || objects.get(22) == "" ? null : objects.get(22).toString().trim();
                /*
                 * 等级 字典 选填
                 */
                String gradeName = objects.get(23) == null || objects.get(23) == "" ? null : objects.get(23).toString().trim();
                /*
                 * 安全类别 字典 选填
                 */
                String safeCategoryName = objects.get(24) == null || objects.get(24) == "" ? null : objects.get(24).toString().trim();
                /*
                 * 备注 选填
                 */
                String remark = objects.get(25) == null || objects.get(25) == "" ? null : objects.get(25).toString().trim();

                // 物料编码
                Long materialSid = null;
                if (StrUtil.isBlank(materialCode)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("物料编码不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        BasMaterial material = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                                .eq(BasMaterial::getMaterialCode, materialCode)
                                .eq(BasMaterial::getMaterialCategory, ConstantsEms.MATERIAL_CATEGORY_WL)
                                .eq(BasMaterial::getStatus, ConstantsEms.ENABLE_STATUS)
                                .eq(BasMaterial::getHandleStatus, ConstantsEms.CHECK_STATUS));
                        if (material == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("物料编码填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            materialSid = material.getMaterialSid();
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("物料编码“" + materialCode + "”系统存在多值，导入失败！");
                        errMsgList.add(errMsg);
                        log.error("物料检测问题台账Service业务层处理->导入功能->物料编码查询报错");
                        e.printStackTrace();
                    }
                }

                // 检测项编码
                String checkItem = null;
                if (StrUtil.isBlank(checkItemName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("检测项名称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        ConCheckItem conCheckItem = conCheckItemMapper.selectOne(new QueryWrapper<ConCheckItem>().lambda()
                                .eq(ConCheckItem::getName, checkItemName)
                                .eq(ConCheckItem::getHandleStatus, ConstantsEms.CHECK_STATUS)
                                .eq(ConCheckItem::getStatus, ConstantsEms.ENABLE_STATUS));
                        if (conCheckItem == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("检测项名称填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            checkItem = conCheckItem.getCode();
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("检测项名称“" + checkItemName + "”系统存在多值，导入失败！");
                        errMsgList.add(errMsg);
                        log.error("物料检测问题台账Service业务层处理->导入功能->检测项名称查询报错");
                        e.printStackTrace();
                    }
                }

                // 判定结果编码
                String result = null;
                if (StrUtil.isBlank(resultName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("判定结果不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    // 通过数据字典标签获取数据字典的值
                    result = resultMaps.get(resultName);
                    if (StrUtil.isBlank(result)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("判定结果填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 检测结果值
                if (StrUtil.isBlank(checkResultValue)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("检测结果值不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }

                // 样品类别编码
                String sampleCategory = null;
                if (StrUtil.isBlank(sampleCategoryName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("样品类别不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    // 通过数据字典标签获取数据字典的值
                    sampleCategory = sampleCategoryMaps.get(sampleCategoryName);
                    if (StrUtil.isBlank(sampleCategory)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("样品类别填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 样品数量
                Long quantity = null;
                if (StrUtil.isBlank(quantityS)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("样品数量不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isPositiveInteger(quantityS)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("样品数量格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        quantity = Long.parseLong(quantityS);
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
                        log.error("物料检测问题台账Service业务层处理->导入功能->产品季名称查询报错");
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
                        log.error("物料检测问题台账Service业务层处理->导入功能->工厂简称查询报错");
                        e.printStackTrace();
                    }
                }

                // 检测类型编码
                String checkType = null;
                if (StrUtil.isNotBlank(checkTypeName)) {
                    // 通过数据字典标签获取数据字典的值
                    checkType = checkTypeMaps.get(checkTypeName);
                    if (StrUtil.isBlank(checkType)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("检测类型填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 检测标准编码
                String checkStandard = null;
                if (StrUtil.isNotBlank(checkStandardName)) {
                    try {
                        ConCheckStandard conCheckStandard = conCheckStandardMapper.selectOne(new QueryWrapper<ConCheckStandard>().lambda()
                                .eq(ConCheckStandard::getName, checkStandardName)
                                .eq(ConCheckStandard::getHandleStatus, ConstantsEms.CHECK_STATUS)
                                .eq(ConCheckStandard::getStatus, ConstantsEms.ENABLE_STATUS));
                        if (conCheckStandard == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("检测标准名称填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            checkStandard = conCheckStandard.getCode();
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("检测标准名称“" + checkItemName + "”系统存在多值，导入失败！");
                        errMsgList.add(errMsg);
                        log.error("物料检测问题台账Service业务层处理->导入功能->检测标准名称查询报错");
                        e.printStackTrace();
                    }
                }

                // 检测批次
                Long checkBatch = null;
                if (StrUtil.isNotBlank(checkBatchS)) {
                    if (!JudgeFormat.isPositiveInteger(checkBatchS)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("检测批次格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        checkBatch = Long.parseLong(checkBatchS);
                    }
                }

                if (StrUtil.isNotBlank(vendorName)) {
                    if (vendorName.length() > 30) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("供应商名称最大只能输入30位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                if (StrUtil.isNotBlank(supplierProductCode)) {
                    if (supplierProductCode.length() > 30) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("供方编码最大只能输入30位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                if (StrUtil.isNotBlank(supplierProductColor)) {
                    if (supplierProductColor.length() > 30) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("供方色号最大只能输入30位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                if (StrUtil.isNotBlank(reportCode)) {
                    if (reportCode.length() > 30) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("报告号最大只能输入30位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                if (StrUtil.isNotBlank(checkPartner)) {
                    checkPartner = checkPartnerMaps.get(checkPartner);
                    if (StrUtil.isBlank(checkPartner)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("检测方类型填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else if (checkPartner.length() > 20) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("检测方类型最大只能输入20位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 检测日期
                Date checkDate = null;
                if (StrUtil.isNotBlank(checkDateS)) {
                    if (!JudgeFormat.isValidDate(checkDateS)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("检测日期格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        checkDate = new Date();
                        try {
                            checkDate = DateUtil.parse(checkDateS);
                        } catch (Exception e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("检测日期格式错误，导入失败！");
                            errMsgList.add(errMsg);
                            checkDate = null;
                        }
                    }
                }

                // 等级编码
                String grade = null;
                if (StrUtil.isNotBlank(gradeName)) {
                    // 通过数据字典标签获取数据字典的值
                    grade = gradeMaps.get(gradeName);
                    if (StrUtil.isBlank(grade)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("等级填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 安全类别编码
                String safeCategory = null;
                if (StrUtil.isNotBlank(safeCategoryName)) {
                    // 通过数据字典标签获取数据字典的值
                    safeCategory = safeCategoryMaps.get(safeCategoryName);
                    if (StrUtil.isBlank(safeCategory)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("安全类别填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 写入数据
                if (CollectionUtil.isEmpty(errMsgList)) {
                    record.setMaterialCode(materialCode).setMaterialSid(materialSid)
                            .setColorName(colorName).setCheckItem(checkItem).setResult(result)
                            .setCheckResultValue(checkResultValue).setSampleCategory(sampleCategory)
                            .setQuantity(quantity).setDefectType(defectType).setDefectDescription(defectDescription)
                            .setResolveStatus(resolveStatus).setSolutionRemark(solutionRemark)
                            .setCheckReferenceValue(checkReferenceValue).setProductSeasonSid(productSeasonSid)
                            .setProductSeasonCode(productSeasonCode).setPlantSid(plantSid).setPlantCode(plantCode)
                            .setCheckType(checkType).setCheckStandard(checkStandard).setCheckBatch(checkBatch)
                            .setVendorName(vendorName).setSupplierProductCode(supplierProductCode).setRemark(remark)
                            .setSupplierProductColor(supplierProductColor).setReportCode(reportCode)
                            .setCheckPartner(checkPartner).setCheckDate(checkDate).setGrade(grade).setSafeCategory(safeCategory)
                            .setHandleStatus(ConstantsEms.SAVA_STATUS).setCheckCategory("WL");
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
                for (QuaRawmatCheckRecord record : recordList) {
                    insertQuaRawmatCheckRecord(record);
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
