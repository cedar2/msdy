package com.platform.ems.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.BasPlant;
import com.platform.ems.domain.CosRecordProductActualCost;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.mapper.BasMaterialMapper;
import com.platform.ems.mapper.BasPlantMapper;
import com.platform.ems.util.JudgeFormat;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.CosRecordProductActualCostMapper;
import com.platform.ems.service.ICosRecordProductActualCostService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商品实际成本台账表Service业务层处理
 *
 * @author chenkw
 * @date 2023-04-27
 */
@Service
@SuppressWarnings("all")
public class CosRecordProductActualCostServiceImpl extends ServiceImpl<CosRecordProductActualCostMapper, CosRecordProductActualCost> implements ICosRecordProductActualCostService {
    @Autowired
    private CosRecordProductActualCostMapper productActualCostMapper;
    @Autowired
    private BasMaterialMapper materialMapper;
    @Autowired
    private BasPlantMapper plantMapper;

    private static final String TITLE = "商品实际成本台账表";

    /**
     * 查询商品实际成本台账表
     *
     * @param recordCostSid 商品实际成本台账表ID
     * @return 商品实际成本台账表
     */
    @Override
    public CosRecordProductActualCost selectCosRecordProductActualCostById(Long recordCostSid) {
        CosRecordProductActualCost actualCost = productActualCostMapper.selectCosRecordProductActualCostById(recordCostSid);
        MongodbUtil.find(actualCost);
        return actualCost;
    }

    /**
     * 查询商品实际成本台账表列表
     *
     * @param actualCost 商品实际成本台账表
     * @return 商品实际成本台账表
     */
    @Override
    public List<CosRecordProductActualCost> selectCosRecordProductActualCostList(CosRecordProductActualCost actualCost) {
        return productActualCostMapper.selectCosRecordProductActualCostList(actualCost);
    }

    /**
     * 新增商品实际成本台账表
     * 需要注意编码重复校验
     *
     * @param actualCost 商品实际成本台账表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertCosRecordProductActualCost(CosRecordProductActualCost actualCost) {
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(actualCost.getHandleStatus())) {
            actualCost.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        actualCost.setCurrency(ConstantsFinance.CURRENCY_CNY).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN);
        setData(new CosRecordProductActualCost(), actualCost);
        int row = productActualCostMapper.insert(actualCost);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new CosRecordProductActualCost(), actualCost);
            MongodbDeal.insert(actualCost.getRecordCostSid(), actualCost.getHandleStatus(), msgList, TITLE, null, actualCost.getImportType());
        }
        return row;
    }


    /**
     * 设置部分字段的编码
     *
     * @param old 表中数据，new修改后数据
     * @return 结果
     */
    private void setData(CosRecordProductActualCost oldActualCost, CosRecordProductActualCost newActualCost) {
        // 项目负责人
        if (newActualCost.getPlantSid() != null && !newActualCost.getPlantSid().equals(oldActualCost.getPlantSid())) {
            BasPlant plant = plantMapper.selectById(newActualCost.getPlantSid());
            if (plant != null) {
                newActualCost.setPlantCode(plant.getPlantCode());
            } else {
                newActualCost.setPlantCode(null);
            }
        } else if (newActualCost.getPlantSid() == null) {
            newActualCost.setPlantCode(null);
        }
    }

    /**
     * 变更商品实际成本台账表
     *
     * @param actualCost 商品实际成本台账表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeCosRecordProductActualCost(CosRecordProductActualCost actualCost) {
        CosRecordProductActualCost response = productActualCostMapper.selectCosRecordProductActualCostById(actualCost.getRecordCostSid());
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(actualCost.getHandleStatus())) {
            actualCost.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        setData(response, actualCost);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, actualCost);
        if (CollectionUtil.isNotEmpty(msgList)) {
            actualCost.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = productActualCostMapper.updateAllById(actualCost);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(actualCost.getRecordCostSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 批量删除商品实际成本台账表
     *
     * @param recordCostSids 需要删除的商品实际成本台账表ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCosRecordProductActualCostByIds(List<Long> recordCostSids) {
        List<CosRecordProductActualCost> list = productActualCostMapper.selectList(new QueryWrapper<CosRecordProductActualCost>()
                .lambda().in(CosRecordProductActualCost::getRecordCostSid, recordCostSids));
        int row = productActualCostMapper.deleteBatchIds(recordCostSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new CosRecordProductActualCost());
                MongodbUtil.insertUserLog(o.getRecordCostSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 导入
     * @param file 文件
     * @return 返回
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity importActualCost(MultipartFile file) {
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

            // 错误信息
            CommonErrMsgResponse errMsg = null;
            List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
            // 警告信息
            CommonErrMsgResponse warnMsg = null;
            List<CommonErrMsgResponse> warnMsgList = new ArrayList<>();
            // 提示信息
            CommonErrMsgResponse infoMsg = null;
            List<CommonErrMsgResponse> infoMsgList = new ArrayList<>();

            // 重复判断
            Map<String, Integer> map = new HashMap<>();

            List<CosRecordProductActualCost> list = new ArrayList<>();

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

                CosRecordProductActualCost actualCost = new CosRecordProductActualCost();

                /*
                 * 成本核算日期 必填
                 */
                String productCostDate_s = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                Date productCostDate = null;
                if (productCostDate_s == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("成本核算日期不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    if (!JudgeFormat.isValidDateFormat(productCostDate_s)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("成本核算日期格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        productCostDate = DateUtil.parse(productCostDate_s);
                    }
                }

                /*
                 * 商品编码(款号)
                 */
                String materialCode = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                Long materialSid = null;
                if (materialCode == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("商品编码(款号)不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    try {
                        BasMaterial material = materialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                                .eq(BasMaterial::getMaterialCode, materialCode));
                        if (material != null) {
                            materialSid = material.getMaterialSid();
                        }
                        else {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("商品编码(款号)" + materialCode + "不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品编码(款号)" + materialCode + "系统中存在重复数据，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * SKU1属性名称
                 */
                String sku1Name = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();

                /*
                 * SKU2属性名称
                 */
                String sku2Name = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();

                /*
                 * 工厂简称
                 */
                String plantShortName = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                Long plantSid = null;
                String plantCode = null;
                if (plantShortName == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("工厂简称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    try {
                        BasPlant plant = plantMapper.selectOne(new QueryWrapper<BasPlant>().lambda()
                                .eq(BasPlant::getShortName, plantShortName));
                        if (plant != null) {
                            plantSid = Long.valueOf(plant.getPlantSid());
                            plantCode = plant.getPlantCode();
                        }
                        else {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("工厂简称" + plantShortName + "不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("工厂简称" + plantShortName + "系统中存在重复数据，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 材料费(元)
                 */
                String materialPrice_s = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                BigDecimal materialPrice = null;
                if (materialPrice_s == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("材料费(元)不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    try {
                        materialPrice = new BigDecimal(materialPrice_s);
                        if (materialPrice.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("材料费(元)不能小于0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            materialPrice = materialPrice.setScale(2, RoundingMode.HALF_UP);
                        }
                    } catch (NumberFormatException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("材料费(元)数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 工价(元)
                 */
                String price_s = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                BigDecimal price = null;
                if (price_s == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("工价(元)不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    try {
                        price = new BigDecimal(price_s);
                        if (price.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("工价(元)不能小于0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            price = price.setScale(2, RoundingMode.HALF_UP);
                        }
                    } catch (NumberFormatException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("工价(元)数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 特殊工艺外发加工费(元)
                 */
                String specialCraftPrice_s = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();
                BigDecimal specialCraftPrice = null;
                if (specialCraftPrice_s != null) {
                    try {
                        specialCraftPrice = new BigDecimal(specialCraftPrice_s);
                        if (price.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("特殊工艺外发加工费(元)不能小于0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            specialCraftPrice = specialCraftPrice.setScale(2, RoundingMode.HALF_UP);
                        }
                    } catch (NumberFormatException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("特殊工艺外发加工费(元)数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 其它费(元)
                 */
                String otherPrice_s = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();
                BigDecimal otherPrice = null;
                if (specialCraftPrice_s != null) {
                    try {
                        otherPrice = new BigDecimal(otherPrice_s);
                        if (price.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("其它费(元)不能小于0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            otherPrice = otherPrice.setScale(2, RoundingMode.HALF_UP);
                        }
                    } catch (NumberFormatException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("其它费(元)数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 销售价(元)
                 */
                String salePrice_s = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString();
                BigDecimal salePrice = null;
                if (specialCraftPrice_s != null) {
                    try {
                        salePrice = new BigDecimal(salePrice_s);
                        if (price.compareTo(BigDecimal.ZERO) < 0) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("销售价(元)不能小于0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            salePrice = salePrice.setScale(2, RoundingMode.HALF_UP);
                        }
                    } catch (NumberFormatException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("销售价(元)数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 说明
                 */
                String remark = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString();
                if (remark != null && remark.length() > 600) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("说明最多只能输入600位，导入失败！");
                    errMsgList.add(errMsg);
                }

                if (CollectionUtil.isNotEmpty(errMsgList)) {
                    continue;
                }
                // 写入数据
                actualCost.setMaterialSid(materialSid).setMaterialCode(materialCode).setProductCostDate(productCostDate)
                        .setSku1Name(sku1Name).setSku2Name(sku2Name).setPlantSid(plantSid).setPlantCode(plantCode)
                        .setMaterialPrice(materialPrice).setSpecialCraftPrice(specialCraftPrice).setImportType(BusinessType.IMPORT.getValue())
                        .setPrice(price).setOtherPrice(otherPrice).setSalePrice(salePrice).setRemark(remark)
                        .setCurrency(ConstantsEms.RMB).setCurrencyUnit(ConstantsEms.YUAN).setHandleStatus(ConstantsEms.CHECK_STATUS);
                list.add(actualCost);
            }
            // 判断
            if (CollectionUtil.isNotEmpty(errMsgList)) {
                return EmsResultEntity.error(errMsgList);
            }
            else if (CollectionUtil.isNotEmpty(warnMsgList)) {
                return EmsResultEntity.warning(list, warnMsgList, infoMsgList, null);
            }
            else if (CollectionUtil.isNotEmpty(list)) {
                for (CosRecordProductActualCost item : list) {
                    insertCosRecordProductActualCost(item);
                }
                return EmsResultEntity.success(num-2, null, infoMsgList, null);
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
