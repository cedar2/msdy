package com.platform.ems.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.file.FileUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.BasMaterialBarcode;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.domain.dto.response.external.BasMaterialBarcodeExternal;
import com.platform.ems.mapper.BasMaterialMapper;
import com.platform.ems.service.IBasMaterialService;
import com.platform.ems.util.JudgeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.BasMaterialBarcodeMapper;
import com.platform.ems.service.IBasMaterialBarcodeService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商品条码Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-23
 */
@Service
@SuppressWarnings("all")
public class BasMaterialBarcodeServiceImpl extends ServiceImpl<BasMaterialBarcodeMapper,BasMaterialBarcode>  implements IBasMaterialBarcodeService {

    @Autowired
    private BasMaterialBarcodeMapper basMaterialBarcodeMapper;

    @Autowired
    private BasMaterialMapper basMaterialMapper;

    @Autowired
    private IBasMaterialService basMaterialService;

    /**
     * 查询物料&商品&服务档案--外部打印产用
     *
     * @param materialSid 物料&商品&服务档案ID
     * @return 物料&商品&服务档案
     */
    @Override
    public List<BasMaterialBarcodeExternal> selectForExternalById(Long materialSid) {
        List<BasMaterialBarcodeExternal> list = basMaterialBarcodeMapper.selectForExternalById(materialSid);
        return list;
    }

    /**
     * 查询商品条码列表
     *
     * @param basMaterialBarcode 商品条码
     * @return 商品条码
     */
    @Override
    public List<BasMaterialBarcode> selectBasMaterialBarcodeList(BasMaterialBarcode basMaterialBarcode) {
        return basMaterialBarcodeMapper.selectBasMaterialBarcodeList(basMaterialBarcode);
    }

    /**
     * 新增商品条码
     * 需要注意编码重复校验
     * @param basMaterialBarcode 商品条码
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasMaterialBarcode(BasMaterialBarcode basMaterialBarcode) {
        return basMaterialBarcodeMapper.insert(basMaterialBarcode);
    }

    /**
     * 修改商品条码
     *
     * @param basMaterialBarcode 商品条码
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasMaterialBarcode(BasMaterialBarcode basMaterialBarcode) {
        return basMaterialBarcodeMapper.updateById(basMaterialBarcode);
    }

    /**
     * 批量删除商品条码
     *
     * @param materialBarcodeSids 需要删除的商品条码ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasMaterialBarcodeByIds(List<Long> materialBarcodeSids) {
        return basMaterialBarcodeMapper.deleteBatchIds(materialBarcodeSids);
    }

    /**
     * 设置产品级别
     *
     * @param basMaterialBarcode 设置产品级别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setProductLevel(BasMaterialBarcode basMaterialBarcode) {
        int row = 0;
        if (StrUtil.isBlank(basMaterialBarcode.getProductLevel())) {
            throw new BaseException("请选择产品级别！");
        }
        LambdaUpdateWrapper<BasMaterialBarcode> updateWrapper = new LambdaUpdateWrapper<>();
        //产品级别
        updateWrapper.in(BasMaterialBarcode::getBarcodeSid, basMaterialBarcode.getBarcodeSidList());
        updateWrapper.set(BasMaterialBarcode::getProductLevel, basMaterialBarcode.getProductLevel());
        row = basMaterialBarcodeMapper.update(new BasMaterialBarcode(), updateWrapper);
        return basMaterialBarcode.getBarcodeSidList().length;
    }

    /**
     * 设置商品SKU编码(ERP)
     *
     * @param basMaterialBarcode 设置产品级别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setErpCode(BasMaterialBarcode basMaterialBarcode) {
        int row = 0;
        if (StrUtil.isBlank(basMaterialBarcode.getErpMaterialSkuBarcode())) {
            throw new BaseException("请输入商品SKU编码(ERP) ！");
        }
        LambdaUpdateWrapper<BasMaterialBarcode> updateWrapper = new LambdaUpdateWrapper<>();
        //商品SKU编码(ERP)
        updateWrapper.in(BasMaterialBarcode::getBarcodeSid, basMaterialBarcode.getBarcodeSidList());
        updateWrapper.set(BasMaterialBarcode::getErpMaterialSkuBarcode, basMaterialBarcode.getErpMaterialSkuBarcode());
        row = basMaterialBarcodeMapper.update(new BasMaterialBarcode(), updateWrapper);
        return basMaterialBarcode.getBarcodeSidList().length;
    }

    /**
     * 文件导入更新商品SKU编码(ERP)
     * @param file
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity importErpCode(MultipartFile file) {
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
            HashMap<String, String> codeMap = new HashMap<>();
            HashMap<String, String> nameMap = new HashMap<>();
            // 基本
            BasMaterialBarcode materialBarcode = null;
            List<BasMaterialBarcode> materialBarcodeList = new ArrayList<>();
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
                String barcode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                if (StrUtil.isBlank(barcode)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("商品SKU条码不可为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    BasMaterialBarcode basMaterialBarcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>()
                            .lambda().eq(BasMaterialBarcode::getBarcode, barcode));
                    if (basMaterialBarcode == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品SKU条码"+ barcode +"不存在，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /**
                 * 商品SKU编码(ERP) 必填
                 */
                String erpCode = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                if (StrUtil.isBlank(erpCode)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("商品SKU编码(ERP)不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                if (CollectionUtil.isEmpty(errMsgList)){
                    materialBarcode = new BasMaterialBarcode();
                    materialBarcode.setBarcode(barcode).setErpMaterialSkuBarcode(erpCode);
                    materialBarcodeList.add(materialBarcode);
                }
            }
            if (CollectionUtil.isNotEmpty(errMsgList)){
                return EmsResultEntity.error(errMsgList);
            }
            // 写入数据
            else {
                if (CollectionUtil.isNotEmpty(materialBarcodeList)) {
                    for (int i = 0; i < materialBarcodeList.size(); i++) {
                        LambdaUpdateWrapper<BasMaterialBarcode> updateWrapper = new LambdaUpdateWrapper<>();
                        updateWrapper.eq(BasMaterialBarcode::getBarcode, materialBarcodeList.get(i).getBarcode());
                        updateWrapper.set(BasMaterialBarcode::getErpMaterialSkuBarcode, materialBarcodeList.get(i).getErpMaterialSkuBarcode());
                        basMaterialBarcodeMapper.update(new BasMaterialBarcode(), updateWrapper);
                    }
                }

            }
        }catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return EmsResultEntity.success(num-2);
    }

    /**
     * 根据materialCode生成商品条码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public HashMap<String, Object> importData(MultipartFile file, String materialCategory) {
        // 返回体
        HashMap<String, Object> response = new HashMap<>();
        //错误信息
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        CommonErrMsgResponse errMsg = null;
        //
        String categoryName = "";
        if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(materialCategory)){
            categoryName = "物料";
        }
        if (ConstantsEms.MATERIAL_CATEGORY_SP.equals(materialCategory)){
            categoryName = "商品";
        }
        try {
            File toFile = null;
            try {
                toFile = FileUtils.multipartFileToFile(file);
            } catch (Exception e) {
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            //
            BasMaterial material = null;
            HashMap<String, Integer> codeMap = new HashMap<>();
            List<String> materialCodeList = new ArrayList<>();
            List<Long> materialSidList = new ArrayList<>();
            for (int i = 0; i < readAll.size(); i++) {
                int num = i + 1;
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);

                String materialCode = objects.get(0)==null||objects.get(0)==""?null:objects.get(0).toString();
                // 为空就跳过
                if (materialCode == null){
                    continue;
                }
                /*
                 * 物料/商品编码
                 */
                Long materialSid = null;
                if (materialCode != null){
                    // 重复就跳过
                    if (codeMap.get(materialCode) == null){
                        materialCodeList.add(materialCode);
                        codeMap.put(materialCode, num);
                    }else {
                        continue;
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(materialCodeList)){
                List<BasMaterial> materialList = basMaterialMapper.selectList(new QueryWrapper<BasMaterial>().lambda()
                        .in(BasMaterial::getMaterialCode, materialCodeList).eq(BasMaterial::getHandleStatus, ConstantsEms.CHECK_STATUS)
                        .eq(BasMaterial::getStatus, ConstantsEms.ENABLE_STATUS).eq(BasMaterial::getMaterialCategory, materialCategory));
                // 如果数据都正确
                if (materialList.size() == materialCodeList.size()){
                    materialSidList = materialList.stream().map(BasMaterial::getMaterialSid).collect(Collectors.toList());
                    int row = basMaterialService.insertBarcode(materialSidList);
                    response.put("result",row);
                }else {
                    List<String> errCodeList = new ArrayList<>();
                    // 可支持导入的
                    if (CollectionUtil.isNotEmpty(materialList)){
                        List<String> correctCodeList = materialList.stream().map(BasMaterial::getMaterialCode).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(correctCodeList)){
                            //匹配出不支持导入的编码用来提示
                            errCodeList = materialCodeList.stream().filter(code->!correctCodeList.contains(code)).collect(Collectors.toList());
                        }
                    }else {
                        errCodeList = materialCodeList;
                    }
                    if (CollectionUtil.isNotEmpty(errCodeList)){
                        for (String code : errCodeList) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(codeMap.get(code));
                            errMsg.setMsg(categoryName + "编码" + code + "不存在或不是确认且已启用状态，是否忽略该" + categoryName + "？");
                            errMsgList.add(errMsg);
                        }
                        materialSidList = materialList.stream().map(BasMaterial::getMaterialSid).collect(Collectors.toList());
                        response.put("errList",errMsgList);
                        response.put("warn",true);
                        List<String> sids = materialSidList.stream().map(e-> String.valueOf(e)).collect(Collectors.toList());
                        response.put("tableData",sids);
                    }
                }
            }
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }
        return response;
    }

    /**
     * 商品条码导入商品条形码
     * @param file
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity importBarcodeShapeCode(MultipartFile file) {
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
            // 列表
            List<BasMaterialBarcode> barcodeList = new ArrayList<>();
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
                // 单行
                BasMaterialBarcode barcode = new BasMaterialBarcode();
                /*
                 * 商品SKU编码(系统) 必填
                 */
                String barcodeCode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                if (StrUtil.isBlank(barcodeCode)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("商品SKU编码(系统)不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    barcodeCode = barcodeCode.replace(" ","");
                    barcode = basMaterialBarcodeMapper.selectOne(new QueryWrapper<BasMaterialBarcode>().lambda()
                            .eq(BasMaterialBarcode::getBarcode, barcodeCode));
                    if (barcode == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品SKU编码(系统)不存在，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 商品条形码 必填
                 */
                String shaprCode = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                if (StrUtil.isBlank(shaprCode)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("商品条形码不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    shaprCode = shaprCode.replace(" ","");
                    if (!JudgeFormat.isNumeric(shaprCode)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品条形码格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    if (shaprCode.length() > 30) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("商品条形码最大只支持输入30位数字，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    if (barcode != null) {
                        barcode.setShangpinTiaoxingma(shaprCode);
                    }
                }
                if (CollectionUtil.isEmpty(errMsgList)) {
                    barcodeList.add(barcode);
                }
            }
            // 报错返回
            if (CollectionUtil.isNotEmpty(errMsgList)) {
                return EmsResultEntity.error(errMsgList);
            }
            // 批量更新商品条形码
            num = basMaterialBarcodeMapper.updatesShapeCodeById(barcodeList);
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return EmsResultEntity.success(num);
    }

    //填充
    public void copy(List<Object> objects,List<List<Object>> readAll){
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
