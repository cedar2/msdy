package com.platform.ems.service.impl;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.domain.dto.response.form.BasMatBarcodeOperLvlCategorySkuForm;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.BasMaterialBarcodeMapper;
import com.platform.ems.plug.domain.ConSaleStation;
import com.platform.ems.plug.mapper.ConSaleStationMapper;
import com.platform.ems.service.ISystemDictDataService;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.BasMaterialBarcodeOperateLevelMapper;
import com.platform.ems.service.IBasMaterialBarcodeOperateLevelService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商品SKU条码-网店运营信息Service业务层处理
 *
 * @author chenkw
 * @date 2023-01-18
 */
@Service
@SuppressWarnings("all")
public class BasMaterialBarcodeOperateLevelServiceImpl extends ServiceImpl<BasMaterialBarcodeOperateLevelMapper, BasMaterialBarcodeOperateLevel> implements IBasMaterialBarcodeOperateLevelService {
    @Autowired
    private BasMaterialBarcodeOperateLevelMapper basMaterialBarcodeOperateLevelMapper;
    @Autowired
    private BasMaterialBarcodeMapper basMaterialBarcodeMapper;
    @Autowired
    private ConSaleStationMapper conSaleStationMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static final String TITLE = "商品SKU条码-网店运营信息";

    /**
     * 查询商品SKU条码-网店运营信息
     *
     * @param materialBarcodeOperateLevelSid 商品SKU条码-网店运营信息ID
     * @return 商品SKU条码-网店运营信息
     */
    @Override
    public BasMaterialBarcodeOperateLevel selectBasMaterialBarcodeOperateLevelById(Long materialBarcodeOperateLevelSid) {
        BasMaterialBarcodeOperateLevel basMaterialBarcodeOperateLevel = basMaterialBarcodeOperateLevelMapper
                .selectBasMaterialBarcodeOperateLevelById(materialBarcodeOperateLevelSid);
        MongodbUtil.find(basMaterialBarcodeOperateLevel);
        return basMaterialBarcodeOperateLevel;
    }

    /**
     * 查询商品SKU条码-网店运营信息列表
     *
     * @param basMaterialBarcodeOperateLevel 商品SKU条码-网店运营信息
     * @return 商品SKU条码-网店运营信息
     */
    @Override
    public List<BasMaterialBarcodeOperateLevel> selectBasMaterialBarcodeOperateLevelList(
            BasMaterialBarcodeOperateLevel basMaterialBarcodeOperateLevel) {
        return basMaterialBarcodeOperateLevelMapper.selectBasMaterialBarcodeOperateLevelList(basMaterialBarcodeOperateLevel);
    }

    /**
     * 设置销售站点编码
     *
     * @param basMaterialBarcodeOperateLevel 商品SKU条码-网店运营信息
     * @return 结果
     */
    private void setData(BasMaterialBarcodeOperateLevel operateLevel) {
        operateLevel.setSaleStationCode(null);
        if (operateLevel.getSaleStationSid() != null) {
            ConSaleStation saleStation = conSaleStationMapper.selectById(operateLevel.getSaleStationSid());
            if (saleStation != null) {
                operateLevel.setSaleStationCode(saleStation.getCode());
            }
        }
    }

    /**
     * 新增商品SKU条码-网店运营信息
     * 需要注意编码重复校验
     *
     * @param basMaterialBarcodeOperateLevel 商品SKU条码-网店运营信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasMaterialBarcodeOperateLevel(BasMaterialBarcodeOperateLevel operateLevel) {
        // 校验是否重复添加 , 有则跳过
        List<BasMaterialBarcodeOperateLevel> levels = basMaterialBarcodeOperateLevelMapper
                .selectList(new QueryWrapper<BasMaterialBarcodeOperateLevel>().lambda()
                        .eq(BasMaterialBarcodeOperateLevel::getMaterialBarcodeSid, operateLevel.getMaterialBarcodeSid())
                        .eq(BasMaterialBarcodeOperateLevel::getSaleStationSid, operateLevel.getSaleStationSid()));
        if (CollectionUtil.isNotEmpty(levels)) {
            return 1;
        }
        // 销售站点
        this.setData(operateLevel);
        int row = basMaterialBarcodeOperateLevelMapper.insert(operateLevel);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new BasMaterialBarcodeOperateLevel(), operateLevel);
            MongodbDeal.insert(operateLevel.getMaterialBarcodeOperateLevelSid(),
                    BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改商品SKU条码-网店运营信息
     *
     * @param basMaterialBarcodeOperateLevel 商品SKU条码-网店运营信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasMaterialBarcodeOperateLevel(BasMaterialBarcodeOperateLevel basMaterialBarcodeOperateLevel) {
        BasMaterialBarcodeOperateLevel original = basMaterialBarcodeOperateLevelMapper
                .selectBasMaterialBarcodeOperateLevelById(basMaterialBarcodeOperateLevel.getMaterialBarcodeOperateLevelSid());
        // 销售站点
        if (original.getSaleStationSid() == null || !original.getSaleStationSid().equals(
                basMaterialBarcodeOperateLevel.getSaleStationSid())) {
            this.setData(basMaterialBarcodeOperateLevel);
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, basMaterialBarcodeOperateLevel);
        if (CollectionUtil.isNotEmpty(msgList)) {
            basMaterialBarcodeOperateLevel.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新主表
        int row = basMaterialBarcodeOperateLevelMapper.updateAllById(basMaterialBarcodeOperateLevel);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basMaterialBarcodeOperateLevel.getMaterialBarcodeOperateLevelSid(),
                    BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更商品SKU条码-网店运营信息
     *
     * @param basMaterialBarcodeOperateLevel 商品SKU条码-网店运营信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasMaterialBarcodeOperateLevel(BasMaterialBarcodeOperateLevel basMaterialBarcodeOperateLevel) {
        BasMaterialBarcodeOperateLevel response = basMaterialBarcodeOperateLevelMapper
                .selectBasMaterialBarcodeOperateLevelById(basMaterialBarcodeOperateLevel.getMaterialBarcodeOperateLevelSid());
        // 销售站点
        if (response.getSaleStationSid() == null || !response.getSaleStationSid().equals(
                basMaterialBarcodeOperateLevel.getSaleStationSid())) {
            this.setData(basMaterialBarcodeOperateLevel);
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, basMaterialBarcodeOperateLevel);
        if (CollectionUtil.isNotEmpty(msgList)) {
            basMaterialBarcodeOperateLevel.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = basMaterialBarcodeOperateLevelMapper.updateAllById(basMaterialBarcodeOperateLevel);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basMaterialBarcodeOperateLevel.getMaterialBarcodeOperateLevelSid(),
                    BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 批量删除商品SKU条码-网店运营信息
     *
     * @param materialBarcodeOperateLevelSids 需要删除的商品SKU条码-网店运营信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasMaterialBarcodeOperateLevelByIds(List<Long> materialBarcodeOperateLevelSids) {
        List<BasMaterialBarcodeOperateLevel> list = basMaterialBarcodeOperateLevelMapper.selectList
                (new QueryWrapper<BasMaterialBarcodeOperateLevel>()
                .lambda().in(BasMaterialBarcodeOperateLevel::getMaterialBarcodeOperateLevelSid, materialBarcodeOperateLevelSids));
        int row = basMaterialBarcodeOperateLevelMapper.deleteBatchIds(materialBarcodeOperateLevelSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new BasMaterialBarcodeOperateLevel());
                MongodbUtil.insertUserLog(o.getMaterialBarcodeOperateLevelSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 设置产品级别
     *
     * @param basMaterialBarcode 设置产品级别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setProductLevel(BasMaterialBarcodeOperateLevel basMaterialBarcodeOperateLevel) {
        int row = 0;
        if (StrUtil.isBlank(basMaterialBarcodeOperateLevel.getProductLevel())) {
            throw new BaseException("请选择产品级别！");
        }
        LambdaUpdateWrapper<BasMaterialBarcodeOperateLevel> updateWrapper = new LambdaUpdateWrapper<>();
        //产品级别
        updateWrapper.in(BasMaterialBarcodeOperateLevel::getMaterialBarcodeOperateLevelSid, basMaterialBarcodeOperateLevel.getMaterialBarcodeOperateLevelSidList());
        updateWrapper.set(BasMaterialBarcodeOperateLevel::getProductLevel, basMaterialBarcodeOperateLevel.getProductLevel());
        row = basMaterialBarcodeOperateLevelMapper.update(new BasMaterialBarcodeOperateLevel(), updateWrapper);
        return basMaterialBarcodeOperateLevel.getMaterialBarcodeOperateLevelSidList().length;
    }

    /**
     * 设置商品MSKU编码(ERP)
     *
     * @param mskuCode 商品MSKU编码(ERP)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setMskuCode(BasMaterialBarcodeOperateLevel basMaterialBarcodeOperateLevel) {
        int row = 0;
        if (StrUtil.isBlank(basMaterialBarcodeOperateLevel.getErpMaterialMskuCode())) {
            throw new BaseException("请输入商品MSKU编码(ERP)！");
        }
        LambdaUpdateWrapper<BasMaterialBarcodeOperateLevel> updateWrapper = new LambdaUpdateWrapper<>();
        //商品MSKU编码(ERP)
        updateWrapper.in(BasMaterialBarcodeOperateLevel::getMaterialBarcodeOperateLevelSid, basMaterialBarcodeOperateLevel.getMaterialBarcodeOperateLevelSidList());
        updateWrapper.set(BasMaterialBarcodeOperateLevel::getErpMaterialMskuCode, basMaterialBarcodeOperateLevel.getErpMaterialMskuCode());
        row = basMaterialBarcodeOperateLevelMapper.update(new BasMaterialBarcodeOperateLevel(), updateWrapper);
        return basMaterialBarcodeOperateLevel.getMaterialBarcodeOperateLevelSidList().length;
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
            List<DictData> operateLevelDict = sysDictDataService.selectDictData("s_sale_station_operate_level"); // 运营级别
            operateLevelDict = operateLevelDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> operateLevelMaps = operateLevelDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> yesnoDict = sysDictDataService.selectDictData("sys_yes_no"); // 是否
            yesnoDict = yesnoDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> yesnoMaps = yesnoDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> productLevelDict = sysDictDataService.selectDictData("s_product_level"); // 产品级别
            productLevelDict = productLevelDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> productLevelMaps = productLevelDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            // 错误信息
            CommonErrMsgResponse errMsg = null;
            List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
            HashMap<String, String> codeMap = new HashMap<>();
            HashMap<String, String> nameMap = new HashMap<>();
            // 基本
            BasMaterialBarcodeOperateLevel operateLevel = null;
            List<BasMaterialBarcodeOperateLevel> operateLevelUpList = new ArrayList<>();
            Map<String, BasMaterialBarcodeOperateLevel> addMap = new HashMap<>();
            Map<String, BasMaterialBarcodeOperateLevel> updMap = new HashMap<>();
            // key ， 所在的行
            Map<String, String> operateLevelHasMap = new HashMap<>();
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
                 * 商品SKU条码 必填
                 */
                String materialBarcode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                Long materialBarcodeSid = null;
                if (StrUtil.isBlank(materialBarcode)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("商品SKU条码不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        BasMaterialBarcode barcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>()
                                .lambda().eq(BasMaterialBarcode::getBarcode, materialBarcode));
                        if (barcode == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("找不到" + materialBarcode + "对应的商品条码，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            materialBarcodeSid = barcode.getBarcodeSid();
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(materialBarcode + "商品SKU条码存在重复，请先检查该商品SKU条码，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 销售站点/网店 必填
                 */
                String saleStationName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                Long saleStationSid = null;
                Long saleStationCode = null;
                if (StrUtil.isBlank(saleStationName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("销售站点/网店不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        ConSaleStation saleStation = conSaleStationMapper.selectOne(new QueryWrapper<ConSaleStation>().lambda()
                                .eq(ConSaleStation::getName, saleStationName));
                        if (saleStation == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("找不到" + saleStationName + "对应的销售站点/网店，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            if (!ConstantsEms.CHECK_STATUS.equals(saleStation.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(saleStation.getStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的销售站点/网店必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            saleStationSid = saleStation.getSid();
                            saleStationCode = saleStation.getCode();
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(saleStationName + "销售站点/网店存在重复，请先检查该销售站点/网店，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 商品MSKU编码(ERP)是否更改 选填
                 */
                String erpMaterialMskuCodeIsUpdName = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                String erpMaterialMskuCode = null;
                if (StrUtil.isNotBlank(erpMaterialMskuCodeIsUpdName)) {
                    String erpMaterialMskuCodeIsUpd = yesnoMaps.get(erpMaterialMskuCodeIsUpdName); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(erpMaterialMskuCodeIsUpd)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品MSKU编码(ERP)是否更改填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else if (ConstantsEms.YES.equals(erpMaterialMskuCodeIsUpd)) {
                        /**
                         * 商品MSKU编码(ERP) 选填
                         */
                        erpMaterialMskuCode = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                        if (StrUtil.isBlank(erpMaterialMskuCode)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("商品MSKU编码(ERP)不可为空，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            erpMaterialMskuCode = erpMaterialMskuCode.trim();
                        }
                    }
                }

                /**
                 * 产品级别是否更改 选填
                 */
                String productLevelIsUpdName = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                String productLevel = null;
                if (StrUtil.isNotBlank(productLevelIsUpdName)) {
                    String productLevelIsUpd = yesnoMaps.get(productLevelIsUpdName); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(productLevelIsUpd)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("产品级别是否更改填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else if (ConstantsEms.YES.equals(productLevelIsUpd)) {
                        /**
                         * 产品级别 选填
                         */
                        String productLevelName = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                        if (StrUtil.isBlank(productLevelName)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("产品级别不可为空，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            productLevel = productLevelMaps.get(productLevelName); //通过数据字典标签获取数据字典的值
                            if (StrUtil.isBlank(productLevel)) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("产品级别填写错误，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }
                }

                /**
                 * 运营级别是否更改 选填
                 */
                String operateLevelIsUpdName = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                String operateLevelCode = null;
                if (StrUtil.isNotBlank(operateLevelIsUpdName)) {
                    String operateLevelIsUpd = yesnoMaps.get(operateLevelIsUpdName); //通过数据字典标签获取数据字典的值
                    if (StrUtil.isBlank(operateLevelIsUpd)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("运营级别是否更改填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else if (ConstantsEms.YES.equals(operateLevelIsUpd)) {
                        /**
                         * 运营级别名称 选填
                         */
                        String operateLevelName = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();
                        if (StrUtil.isBlank(operateLevelName)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("运营级别不可为空，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            operateLevelCode = operateLevelMaps.get(operateLevelName); //通过数据字典标签获取数据字典的值
                            if (StrUtil.isBlank(operateLevelCode)) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("运营级别填写错误，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }
                }

                /**
                 * 备注 选填
                 */
                String remark = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();
                if (StrUtil.isNotBlank(remark) && remark.length() > 600){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("备注长度不能超过600个字符，导入失败！");
                    errMsgList.add(errMsg);
                }

                // 写入数据
                if (CollectionUtil.isEmpty(errMsgList)){
                    operateLevel = new BasMaterialBarcodeOperateLevel();
                    operateLevel.setMaterialBarcode(materialBarcode)
                            .setMaterialBarcodeSid(materialBarcodeSid).setOperateLevel(operateLevelCode)
                            .setSaleStationSid(saleStationSid).setSaleStationCode(saleStationCode)
                            .setErpMaterialMskuCode(erpMaterialMskuCode).setProductLevel(productLevel).setRemark(remark);
                    // 判断是否重复
                    if (materialBarcodeSid != null && saleStationSid != null) {
                        String key = String.valueOf(materialBarcodeSid) + String.valueOf(saleStationSid);
                        // 表格内重复 覆盖
                        if (operateLevelHasMap.containsKey(key)) {
                            if (addMap.containsKey(key)) {
                                addMap.put(key, operateLevel);
                            }
                            else if (updMap.containsKey(key)) {
                                BasMaterialBarcodeOperateLevel origin = updMap.get(key);
                                origin.setOperateLevel(operateLevelCode).setProductLevel(productLevel)
                                        .setErpMaterialMskuCode(erpMaterialMskuCode).setRemark(remark);
                                updMap.put(key, origin);
                            }
                        } else {
                            // 存入map
                            operateLevelHasMap.put(key, String.valueOf(num));
                            // 如果数据库存在 则更新，不存在则新增
                            try {
                                BasMaterialBarcodeOperateLevel origin = basMaterialBarcodeOperateLevelMapper
                                        .selectOne(new QueryWrapper<BasMaterialBarcodeOperateLevel>().lambda()
                                                .eq(BasMaterialBarcodeOperateLevel::getMaterialBarcodeSid, materialBarcodeSid)
                                                .eq(BasMaterialBarcodeOperateLevel::getSaleStationSid, saleStationSid));
                                if (origin != null) {
                                    origin.setOperateLevel(operateLevelCode).setProductLevel(productLevel)
                                            .setErpMaterialMskuCode(erpMaterialMskuCode)
                                            .setRemark(remark);
                                    updMap.put(key, origin);
                                } else {
                                    addMap.put(key, operateLevel);
                                }
                            } catch (TooManyResultsException e) {
                                e.printStackTrace();
                                log.error(num + "行的系统数据存在重复！");
                            }
                        }
                    }
                }
            }

            if (CollectionUtil.isNotEmpty(errMsgList)){
                return EmsResultEntity.error(errMsgList);
            }
            if (addMap != null && addMap.size() != 0) {
                for (String key : addMap.keySet()) {
                    this.insertBasMaterialBarcodeOperateLevel(addMap.get(key));
                }
            }
            if (updMap != null && updMap.size() != 0) {
                for (String key : updMap.keySet()) {
                    operateLevelUpList.add(updMap.get(key));
                }
                basMaterialBarcodeOperateLevelMapper.updatesAllById(operateLevelUpList);
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

    /**
     * 报表中心类目明细报表
     *
     * @param request BasMatBarcodeOperLvlCategorySkuForm
     * @return 报表中心类目明细报表
     */
    @Override
    public List<BasMatBarcodeOperLvlCategorySkuForm> selectBasMaterialBarcodeOperateLevelCategorySkuForm(BasMatBarcodeOperLvlCategorySkuForm request) {
        request.setClientId(ApiThreadLocalUtil.get().getClientId());
        List<BasMatBarcodeOperLvlCategorySkuForm> reponse =  basMaterialBarcodeOperateLevelMapper.selectMaterialBarcodeOperateLevelCategorySkuForm(request);
        return reponse;
    }

    /**
     * 按钮设置采购状态
     *
     * @param basMaterialBarcodeOperateLevel 商品SKU条码-网店运营信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurchaseFlag(BasMaterialBarcodeOperateLevel request) {
        int row = 0;
        if (request.getMaterialBarcodeOperateLevelSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<BasMaterialBarcodeOperateLevel> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(BasMaterialBarcodeOperateLevel::getMaterialBarcodeOperateLevelSid, request.getMaterialBarcodeOperateLevelSidList());
        // 判读是否修改
        boolean flag = false;
        // 一次采购
        if (ConstantsEms.YES.equals(request.getFirstPuchaseFlagIsUpd())) {
            updateWrapper.set(BasMaterialBarcodeOperateLevel::getFirstPuchaseFlag, request.getFirstPuchaseFlag());
            flag = true;
        }
        // 二次采购
        if (ConstantsEms.YES.equals(request.getSecondPuchaseFlagIsUpd())) {
            updateWrapper.set(BasMaterialBarcodeOperateLevel::getSecondPuchaseFlag, request.getSecondPuchaseFlag());
            flag = true;
        }
        // 一次到货通知
        if (ConstantsEms.YES.equals(request.getArrivalNoticeFlagFirstPurchaseIsUpd())) {
            updateWrapper.set(BasMaterialBarcodeOperateLevel::getArrivalNoticeFlagFirstPurchase, request.getArrivalNoticeFlagFirstPurchase());
            flag = true;
        }
        // 修改
        if (flag) {
            row = basMaterialBarcodeOperateLevelMapper.update(null, updateWrapper);
        }
        return row;
    }

    /**
     * 更新数据导入 MSKU + 产品级别 + 运营级别
     * @param file
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity importUpdateData(MultipartFile file) {
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
            String YES = "是", NO = "否";
            // 运营级别
            List<DictData> operateLevelDict = sysDictDataService.selectDictData("s_sale_station_operate_level");
            operateLevelDict = operateLevelDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> operateLevelMaps = operateLevelDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            // 产品级别
            List<DictData> productLevelDict = sysDictDataService.selectDictData("s_product_level");
            productLevelDict = productLevelDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> productLevelMaps = productLevelDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            // 错误信息
            CommonErrMsgResponse errMsg = null;
            List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
            // 更新数据
            List<BasMaterialBarcodeOperateLevel> updateLevelList = new ArrayList<>();
            // 新增数据
            List<BasMaterialBarcodeOperateLevel> addLevelList = new ArrayList<>();
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

                // 行对应数据
                BasMaterialBarcodeOperateLevel item = new BasMaterialBarcodeOperateLevel();

                /**
                 * 商品SKU条码 必填
                 */
                String materialBarcode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                Long materialBarcodeSid = null;
                if (StrUtil.isBlank(materialBarcode)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("商品SKU条码不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        BasMaterialBarcode barcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>()
                                .lambda().eq(BasMaterialBarcode::getBarcode, materialBarcode));
                        if (barcode == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("找不到" + materialBarcode + "对应的商品条码，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            materialBarcodeSid = barcode.getBarcodeSid();
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(materialBarcode + "商品SKU条码存在重复，请先检查该商品SKU条码，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 销售站点/网店 必填
                 */
                String saleStationName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                Long saleStationSid = null;
                Long saleStationCode = null;
                if (StrUtil.isBlank(saleStationName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("销售站点/网店不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        ConSaleStation saleStation = conSaleStationMapper.selectOne(new QueryWrapper<ConSaleStation>().lambda()
                                .eq(ConSaleStation::getName, saleStationName));
                        if (saleStation == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("找不到" + saleStationName + "对应的销售站点/网店，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            if (!ConstantsEms.CHECK_STATUS.equals(saleStation.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(saleStation.getStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的销售站点/网店必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            saleStationSid = saleStation.getSid();
                            saleStationCode = saleStation.getCode();
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(saleStationName + "销售站点/网店存在重复，请先检查该销售站点，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 判断是否存在，不存在则考虑新增, flag = false 不存在， flag = true 存在
                boolean flag = false;
                if (materialBarcodeSid != null && saleStationSid != null) {
                    item.setMaterialBarcodeSid(materialBarcodeSid).setMaterialBarcode(materialBarcode)
                            .setSaleStationSid(saleStationSid).setSaleStationCode(saleStationCode);
                    try {
                        BasMaterialBarcodeOperateLevel exist = basMaterialBarcodeOperateLevelMapper.selectOne(
                                new QueryWrapper<BasMaterialBarcodeOperateLevel>().lambda()
                                        .eq(BasMaterialBarcodeOperateLevel::getMaterialBarcodeSid, materialBarcodeSid)
                                        .eq(BasMaterialBarcodeOperateLevel::getSaleStationSid, saleStationSid));
                        // 不存在，要新建
                        if (exist == null) {
                            addLevelList.add(item);
                        }
                        else {
                            // 已存在，要更新
                            BeanUtil.copyProperties(exist, item);
                            updateLevelList.add(item);
                            flag = true;
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品SKU条码(" + materialBarcode + ")和销售站点/网店("+ saleStationName +")组合系统中存在重复，请先检查该数据，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 商品MSKU编码(ERP)是否更改 选填
                 */
                String erpMaterialMskuCodeIsUpd = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                if (StrUtil.isNotBlank(erpMaterialMskuCodeIsUpd)) {
                    if (!YES.equals(erpMaterialMskuCodeIsUpd) && !NO.equals(erpMaterialMskuCodeIsUpd)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品MSKU编码(ERP)是否更改填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 商品MSKU编码(ERP) 选填
                 */
                String erpMaterialMskuCode = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                if (YES.equals(erpMaterialMskuCodeIsUpd)) {
                    if (StrUtil.isNotBlank(erpMaterialMskuCode)) {
                        erpMaterialMskuCode = erpMaterialMskuCode.trim();
                        item.setErpMaterialMskuCode(erpMaterialMskuCode);
                    }
                    else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品MSKU编码(ERP)不能为空，导入失败！");
                        errMsgList.add(errMsg);
                        item.setErpMaterialMskuCode(null);
                    }
                }

                /**
                 * 产品级别是否更改 选填
                 */
                String productLevelIsUpd = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                if (StrUtil.isNotBlank(productLevelIsUpd)) {
                    if (!YES.equals(productLevelIsUpd) && !NO.equals(productLevelIsUpd)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("产品级别是否更改填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 产品级别 选填
                 */
                String productLevel_s = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                String productLevel = null;
                if (YES.equals(productLevelIsUpd)) {
                    if (StrUtil.isNotBlank(productLevel_s)) {
                        // 通过数据字典标签获取数据字典的值
                        productLevel = productLevelMaps.get(productLevel_s);
                        if (StrUtil.isBlank(productLevel)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("产品级别填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        item.setProductLevel(productLevel);
                    }
                    else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("产品级别不能为空，导入失败！");
                        errMsgList.add(errMsg);
                        item.setProductLevel(null);
                    }
                }

                /**
                 * 运营级别是否更改 选填
                 */
                String operateLevelIsUpd = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                if (StrUtil.isNotBlank(operateLevelIsUpd)) {
                    if (!YES.equals(operateLevelIsUpd) && !NO.equals(operateLevelIsUpd)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("运营级别是否更改填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /**
                 * 运营级别 选填
                 */
                String operateLevel_s = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();
                String operateLevel = null;
                if (YES.equals(operateLevelIsUpd)) {
                    if (StrUtil.isNotBlank(operateLevel_s)) {
                        // 通过数据字典标签获取数据字典的值
                        operateLevel = operateLevelMaps.get(operateLevel_s);
                        if (StrUtil.isBlank(operateLevel)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("运营级别填写错误，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        item.setOperateLevel(operateLevel);
                    }
                    else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("运营级别不能为空，导入失败！");
                        errMsgList.add(errMsg);
                        item.setOperateLevel(null);
                    }
                }

                /**
                 * 备注 选填
                 */
                item.setRemark(objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString());
            }
            if (CollectionUtil.isNotEmpty(errMsgList)) {
                return EmsResultEntity.error(errMsgList);
            }
            else {
                if (CollectionUtil.isNotEmpty(addLevelList)) {
                    basMaterialBarcodeOperateLevelMapper.inserts(addLevelList);
                }
                if (CollectionUtil.isNotEmpty(updateLevelList)) {
                    basMaterialBarcodeOperateLevelMapper.updatesAllById(updateLevelList);
                }
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return EmsResultEntity.success(num-2);
    }

}
