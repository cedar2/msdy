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
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.service.IQuaShouhouRecordService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 售后质量问题台账Service业务层处理
 *
 * @author platform
 * @date 2024-03-06
 */
@Service
@SuppressWarnings("all")
public class QuaShouhouRecordServiceImpl extends ServiceImpl<QuaShouhouRecordMapper, QuaShouhouRecord> implements IQuaShouhouRecordService {
    @Autowired
    private QuaShouhouRecordMapper quaShouhouRecordMapper;
    @Autowired
    private QuaShouhouRecordAttachMapper attachMapper;
    @Autowired
    private BasProductSeasonMapper basProductSeasonMapper;
    @Autowired
    private BasPlantMapper basPlantMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static final String TITLE = "售后质量问题台账";

    /**
     * 查询售后质量问题台账
     *
     * @param shouhouRecordSid 售后质量问题台账ID
     * @return 售后质量问题台账
     */
    @Override
    public QuaShouhouRecord selectQuaShouhouRecordById(Long shouhouRecordSid) {
        QuaShouhouRecord quaShouhouRecord = quaShouhouRecordMapper.selectQuaShouhouRecordById(shouhouRecordSid);
        // 特殊字段处理
        getData(quaShouhouRecord);
        // 附件清单
        quaShouhouRecord.setAttachmentList(new ArrayList<>());
        List<QuaShouhouRecordAttach> attacheList = attachMapper.selectQuaShouhouRecordAttachList(new QuaShouhouRecordAttach()
                .setShouhouRecordSid(shouhouRecordSid));
        if (CollectionUtil.isNotEmpty(attacheList)) {
            quaShouhouRecord.setAttachmentList(attacheList);
        }
        // 操作日志
        MongodbUtil.find(quaShouhouRecord);
        return quaShouhouRecord;
    }

    /**
     * 查询售后质量问题台账列表
     *
     * @param quaShouhouRecord 售后质量问题台账
     * @return 售后质量问题台账
     */
    @Override
    public List<QuaShouhouRecord> selectQuaShouhouRecordList(QuaShouhouRecord quaShouhouRecord) {
        List<QuaShouhouRecord> list = quaShouhouRecordMapper.selectQuaShouhouRecordList(quaShouhouRecord);
        // 图片视频字段查询页面要
        if (CollectionUtil.isNotEmpty(list)) {
            for (QuaShouhouRecord record : list) {
                getData(record);
            }
        }
        return list;
    }

    /**
     * 写入数据字段处理
     */
    private void setData(QuaShouhouRecord record) {
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
        record.setCustomerCode(null);
        if (record.getCustomerSid() != null) {
            BasCustomer customer = basCustomerMapper.selectById(record.getVendorSid());
            if (customer != null) {
                record.setCustomerCode(String.valueOf(customer.getCustomerCode()));
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
    private void getData(QuaShouhouRecord record) {
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
     * 新增售后质量问题台账
     * 需要注意编码重复校验
     *
     * @param quaShouhouRecord 售后质量问题台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertQuaShouhouRecord(QuaShouhouRecord quaShouhouRecord) {
        // 写默认值
        setData(quaShouhouRecord);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(quaShouhouRecord.getHandleStatus())) {
            quaShouhouRecord.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = quaShouhouRecordMapper.insert(quaShouhouRecord);
        if (row > 0) {
            // 附件清单
            if (CollectionUtil.isNotEmpty(quaShouhouRecord.getAttachmentList())) {
                quaShouhouRecord.getAttachmentList().forEach(item->{
                    item.setShouhouRecordSid(quaShouhouRecord.getShouhouRecordSid());
                });
                attachMapper.inserts(quaShouhouRecord.getAttachmentList());
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new QuaShouhouRecord(), quaShouhouRecord);
            MongodbDeal.insert(quaShouhouRecord.getShouhouRecordSid(), quaShouhouRecord.getHandleStatus(), msgList, TITLE, null, BusinessType.IMPORT.getValue());
        }
        return row;
    }


    /**
     * 批量修改附件信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAttach(QuaShouhouRecord record) {
        // 先删后加
        attachMapper.delete(new QueryWrapper<QuaShouhouRecordAttach>().lambda()
                .eq(QuaShouhouRecordAttach::getShouhouRecordSid, record.getShouhouRecordSid()));
        if (CollectionUtil.isNotEmpty(record.getAttachmentList())) {
            record.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getShouhouRecordAttachSid() == null) {
                    att.setShouhouRecordSid(record.getShouhouRecordSid());
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
     * 修改售后质量问题台账
     *
     * @param quaShouhouRecord 售后质量问题台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateQuaShouhouRecord(QuaShouhouRecord quaShouhouRecord) {
        QuaShouhouRecord original = quaShouhouRecordMapper.selectQuaShouhouRecordById(quaShouhouRecord.getShouhouRecordSid());
        // 写默认值
        setData(quaShouhouRecord);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(quaShouhouRecord.getHandleStatus())) {
            quaShouhouRecord.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, quaShouhouRecord);
        if (CollectionUtil.isNotEmpty(msgList)) {
            quaShouhouRecord.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = quaShouhouRecordMapper.updateAllById(quaShouhouRecord);
        if (row > 0) {
            // 附件清单
            updateAttach(quaShouhouRecord);
            //插入日志
            MongodbDeal.update(quaShouhouRecord.getShouhouRecordSid(), original.getHandleStatus(),
                    quaShouhouRecord.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更售后质量问题台账
     *
     * @param quaShouhouRecord 售后质量问题台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeQuaShouhouRecord(QuaShouhouRecord quaShouhouRecord) {
        QuaShouhouRecord response = quaShouhouRecordMapper.selectQuaShouhouRecordById(quaShouhouRecord.getShouhouRecordSid());
        // 写默认值
        setData(quaShouhouRecord);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, quaShouhouRecord);
        if (CollectionUtil.isNotEmpty(msgList)) {
            quaShouhouRecord.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = quaShouhouRecordMapper.updateAllById(quaShouhouRecord);
        if (row > 0) {
            // 附件清单
            updateAttach(quaShouhouRecord);
            //插入日志
            MongodbUtil.insertUserLog(quaShouhouRecord.getShouhouRecordSid(), BusinessType.CHANGE.getValue(), response, quaShouhouRecord, TITLE);
        }
        return row;
    }

    /**
     * 批量删除售后质量问题台账
     *
     * @param shouhouRecordSids 需要删除的售后质量问题台账ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteQuaShouhouRecordByIds(List<Long> shouhouRecordSids) {
        List<QuaShouhouRecord> list = quaShouhouRecordMapper.selectList(new QueryWrapper<QuaShouhouRecord>()
                .lambda().in(QuaShouhouRecord::getShouhouRecordSid, shouhouRecordSids));
        int row = quaShouhouRecordMapper.deleteBatchIds(shouhouRecordSids);
        if (row > 0) {
            // 附件清单
            attachMapper.delete(new QueryWrapper<QuaShouhouRecordAttach>()
                    .lambda().in(QuaShouhouRecordAttach::getShouhouRecordSid, shouhouRecordSids));
            // 操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new QuaShouhouRecord());
                MongodbUtil.insertUserLog(o.getShouhouRecordSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param quaShouhouRecord
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(QuaShouhouRecord quaShouhouRecord) {
        Long[] sids = quaShouhouRecord.getShouhouRecordSidList();
        if (ArrayUtil.isEmpty(sids)) {
            return 0;
        }
        LambdaUpdateWrapper<QuaShouhouRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(QuaShouhouRecord::getShouhouRecordSid, sids);
        updateWrapper.set(QuaShouhouRecord::getHandleStatus, quaShouhouRecord.getHandleStatus());
        if (ConstantsEms.CHECK_STATUS.equals(quaShouhouRecord.getHandleStatus())) {
            updateWrapper.set(QuaShouhouRecord::getConfirmDate, new Date());
            updateWrapper.set(QuaShouhouRecord::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        int row = quaShouhouRecordMapper.update(null, updateWrapper);
        if (row > 0) {
            for (Long id : sids) {
                //插入日志
                MongodbDeal.check(id, quaShouhouRecord.getHandleStatus(), null, TITLE, null);
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
            defectTypeList = defectTypeList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> defectTypeMaps = defectTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典解决状态
            List<DictData> resolveStatusList = sysDictDataService.selectDictData("s_resolve_status");
            resolveStatusList = resolveStatusList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> resolveStatusMaps = resolveStatusList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典责任归属方
            List<DictData> chargerList = sysDictDataService.selectDictData("s_charger");
            chargerList = chargerList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> chargerMaps = chargerList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典售后处理方式
            List<DictData> shouhouTypeList = sysDictDataService.selectDictData("s_shouhou_type");
            shouhouTypeList = shouhouTypeList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> shouhouTypeMaps = shouhouTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典售后处理方式
            List<DictData> returnsTypeList = sysDictDataService.selectDictData("s_returns_type");
            returnsTypeList = returnsTypeList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> returnsTypeMaps = returnsTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

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
            List<QuaShouhouRecord> recordList = new ArrayList<>();

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
                QuaShouhouRecord record = new QuaShouhouRecord();
                /*
                 * 商品编码 必填
                 */
                String materialCode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString().trim();
                /*
                 * 商品颜色 选填
                 */
                String colorName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString().trim();
                /*
                 * 客户简称 必填
                 */
                String customerShortName = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString().trim();
                /*
                 * 售后处理方式 数据字典 必填
                 */
                String shouhouTypeName = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString().trim();
                /*
                 * 问题类型 数据字典 必填
                 */
                String defectTypeName = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString().trim();
                /*
                 * 问题描述 必填
                 */
                String defectDescription = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString().trim();
                /*
                 * 解决状态 字典 必填
                 */
                String resolveStatusName = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString().trim();
                /*
                 * 处理结果 选填
                 */
                String solutionRemark = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString().trim();
                /*
                 * 产品季名称 选填
                 */
                String productSeasonName = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString().trim();
                /*
                 * 工厂简称 选填
                 */
                String plantShortName = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString().trim();
                /*
                 * 数量 选填
                 */
                String quantityS = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString().trim();
                /*
                 * 责任归属方 数据字典 选填
                 */
                String chargerName = objects.get(11) == null || objects.get(11) == "" ? null : objects.get(11).toString().trim();
                /*
                 * 验货人 选填
                 */
                String inspector = objects.get(12) == null || objects.get(12) == "" ? null : objects.get(12).toString().trim();
                /*
                 * 验货日期 选填
                 */
                String inspectionDateS = objects.get(13) == null || objects.get(13) == "" ? null : objects.get(13).toString().trim();
                /*
                 * 退货类型 选填
                 */
                String returnsType = objects.get(14) == null || objects.get(14) == "" ? null : objects.get(14).toString().trim();
                /*
                 * 备注 选填
                 */
                String remark = objects.get(15) == null || objects.get(15) == "" ? null : objects.get(15).toString().trim();

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
                        log.error("售后质量问题台账Service业务层处理->导入功能->商品编码查询报错");
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

                // 客户简称
                Long customerSid = null;
                if (StrUtil.isBlank(customerShortName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("客户简称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        BasCustomer customer = basCustomerMapper.selectOne(new QueryWrapper<BasCustomer>().lambda()
                                .eq(BasCustomer::getShortName, customerShortName)
                                .eq(BasCustomer::getStatus, ConstantsEms.ENABLE_STATUS)
                                .eq(BasCustomer::getHandleStatus, ConstantsEms.CHECK_STATUS));
                        if (customer == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("客户简称填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            customerSid = customer.getCustomerSid();
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("客户简称“" + customerShortName + "”系统存在多值，导入失败！");
                        errMsgList.add(errMsg);
                        log.error("售后质量问题台账Service业务层处理->导入功能->商品编码查询报错");
                        e.printStackTrace();
                    }
                }

                // 售后处理方式
                String shouhouType = null;
                if (StrUtil.isBlank(shouhouTypeName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("售后处理方式不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    // 通过数据字典标签获取数据字典的值
                    shouhouType = shouhouTypeMaps.get(shouhouTypeName);
                    if (StrUtil.isBlank(shouhouType)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("售后处理方式填写错误，导入失败！");
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
                } else {
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
                        errMsg.setMsg("处理结果最大只能输入600位，导入失败！");
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
                        log.error("售后质量问题台账Service业务层处理->导入功能->产品季名称查询报错");
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
                        log.error("售后质量问题台账Service业务层处理->导入功能->工厂简称查询报错");
                        e.printStackTrace();
                    }
                }

                // 数量
                Long quantity = null;
                if (StrUtil.isNotBlank(quantityS)) {
                    if (!JudgeFormat.isPositiveInteger(quantityS)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("数量格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        quantity = Long.parseLong(quantityS);
                    }
                }

                // 责任归属方
                String charger = null;
                if (StrUtil.isNotBlank(chargerName)) {
                    // 通过数据字典标签获取数据字典的值
                    charger = chargerMaps.get(chargerName);
                    if (StrUtil.isBlank(charger)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("责任归属方填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 验货人
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
                if (StrUtil.isNotBlank(returnsType)) {
                    returnsType = returnsTypeMaps.get(returnsType);
                    if (StrUtil.isBlank(returnsType)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("退货类型填写错误，导入失败！");
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
                if (CollectionUtil.isEmpty(errMsgList)) {
                    record.setMaterialCode(materialCode).setMaterialSid(materialSid)
                            .setColorName(colorName).setCustomerSid(customerSid).setShouhouType(shouhouType)
                            .setQuantity(quantity).setDefectType(defectType).setDefectDescription(defectDescription)
                            .setResolveStatus(resolveStatus).setSolutionRemark(solutionRemark).setInspector(inspector)
                            .setInspectionDate(inspectionDate).setCharger(charger)
                            .setProductSeasonSid(productSeasonSid).setProductSeasonCode(productSeasonCode)
                            .setPlantSid(plantSid).setPlantCode(plantCode).setReturnsType(returnsType)
                            .setHandleStatus(ConstantsEms.SAVA_STATUS).setRemark(remark);
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
                for (QuaShouhouRecord record : recordList) {
                    insertQuaShouhouRecord(record);
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
