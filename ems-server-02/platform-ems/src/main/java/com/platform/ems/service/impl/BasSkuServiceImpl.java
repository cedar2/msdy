package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
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
import com.platform.common.core.redis.RedisCache;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.SecurityUtils;
import com.platform.ems.constant.AutoIdField;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.BasCustomer;
import com.platform.ems.domain.BasMaterialSku;
import com.platform.ems.domain.BasSku;
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.BasCustomerMapper;
import com.platform.ems.mapper.BasMaterialSkuMapper;
import com.platform.ems.mapper.BasSkuMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.service.IBasSkuService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.CodeRuleUtil;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SKU档案Service业务层处理
 *
 * @author linhongwei
 * @date 2021-03-22
 */
@Service
@SuppressWarnings("all")
public class BasSkuServiceImpl extends ServiceImpl<BasSkuMapper, BasSku> implements IBasSkuService {
    @Autowired
    private BasSkuMapper basSkuMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private BasMaterialSkuMapper basMaterialSkuMapper;

    @Autowired
    private RedisCache redisService;

    private static final String TITLE = "SKU档案";

    private static final String DATAOBJECT = "SKU";

    private static String KEY = "";


    /**
     * 查询SKU档案
     *
     * @param clientId SKU档案ID
     * @return SKU档案
     */
    @Override
    public BasSku selectBasSkuById(Long skuSid) {
        BasSku basSku = basSkuMapper.selectBasSkuById(skuSid);
        //查询日志信息
        MongodbUtil.find(basSku);
        return basSku;
    }

    /**
     * 查询SKU档案列表
     *
     * @param basSku SKU档案
     * @return SKU档案
     */
    @Override
    public List<BasSku> selectBasSkuList(BasSku basSku) {
        List<BasSku> basSkus = basSkuMapper.selectBasSkuList(basSku);
        return basSkus;
    }

    /**
     * 获取自动编码的编码
     *
     * @param basSku
     */
    private void getCode(BasSku basSku) {
        Map<String, String> map = CodeRuleUtil.allocation(DATAOBJECT, basSku.getSkuType());
        if (map == null || StrUtil.isBlank(map.get(AutoIdField.code))) {
            throw new BaseException("编码不能为空");
        } else {
            basSku.setSkuCode(map.get("code"));
            KEY = map.get(AutoIdField.key_name);
            Map<String, Object> params = new HashMap<>();
            params.put("sku_code", basSku.getSkuCode());
            List<BasSku> skuList2 = basSkuMapper.selectByMap(params);
            if (skuList2.size() > 0) {
                //编码已存在就在往下遍历
                getCode(basSku);
            }
        }
    }

    /**
     * 新增SKU档案
     * 需要注意编码重复校验
     *
     * @param basSku SKU档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasSku(BasSku basSku) {
        int row = 0;
        try {
            Map<String, Object> params = new HashMap<>();
            if (StrUtil.isNotBlank(basSku.getSkuCode())) {
                params.put("sku_code", basSku.getSkuCode());
                List<BasSku> skuList2 = basSkuMapper.selectByMap(params);
                if (skuList2.size() > 0) {
                    throw new CustomException("sku编码已存在,请查看");
                }
            } else {
                //自动编码
                getCode(basSku);
            }
            params.clear();
            params.put("sku_name", basSku.getSkuName());
            if (basSku.getSkuType() != null){
                params.put("sku_type", basSku.getSkuType());
            }
            List<BasSku> skuList = basSkuMapper.selectByMap(params);
            if (CollectionUtils.isNotEmpty(skuList)) {
                throw new CustomException("已存在同名的SKU,不允许此操作！");
            }
            if (ConstantsEms.CHECK_STATUS.equals(basSku.getHandleStatus())) {
                basSku.setConfirmDate(new Date());
                basSku.setConfirmerAccount(SecurityUtils.getUsername());
            }
            row = basSkuMapper.insert(basSku);
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(basSku.getHandleStatus())) {
                List<DictData> skuTypeDict = sysDictDataService.selectDictData("s_sku_type");
                Map<String, String> skuTypeMaps = skuTypeDict.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_BAS_SKU)
                        .setDocumentSid(basSku.getSkuSid());
                sysTodoTask.setTitle(skuTypeMaps.get(basSku.getSkuType()) + "档案: " + basSku.getSkuCode() + " 当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(basSku.getSkuCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            if (row > 0) {
                String remark = null;
                //插入日志
                MongodbDeal.insert(basSku.getSkuSid(), basSku.getHandleStatus(), null, TITLE, remark);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            redisService.deleteObject(KEY);
        }
        return row;
    }

    /**
     * 修改SKU档案
     *
     * @param basSku SKU档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasSku(BasSku basSku) {
        if (StrUtil.isBlank(basSku.getSkuCode())) {
            throw new BaseException("编码不能为空");
        }
        BasSku old = basSkuMapper.selectBasSkuById(basSku.getSkuSid());
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("sku_name", basSku.getSkuName());
        if (basSku.getSkuType() != null){
            queryParams.put("sku_type", basSku.getSkuType());
        }
        List<BasSku> queryResult = basSkuMapper.selectByMap(queryParams);
        if (queryResult.size() > 0) {
            for (BasSku sku : queryResult) {
                if (!sku.getSkuSid().equals(basSku.getSkuSid())) {
                    throw new CustomException("已存在同名的SKU,不允许此操作！");
                }
            }
        }
        queryParams.clear();
        queryParams.put("sku_code", basSku.getSkuCode());
        List<BasSku> queryResult2 = basSkuMapper.selectByMap(queryParams);
        if (queryResult2.size() > 0) {
            for (BasSku sku : queryResult2) {
                if (sku.getSkuCode().equals(basSku.getSkuCode()) && !sku.getSkuSid().equals(basSku.getSkuSid())) {
                    throw new CustomException("编码重复,请查看");
                }
            }
        }
        basSku.setUpdateDate(new Date());
        basSku.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        if (ConstantsEms.CHECK_STATUS.equals(basSku.getHandleStatus())) {
            basSku.setConfirmDate(new Date());
            basSku.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = basSkuMapper.updateAllById(basSku);
        //确认状态后删除待办
        if (!ConstantsEms.SAVA_STATUS.equals(basSku.getHandleStatus())) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, basSku.getSkuSid()));
        }
        if (row > 0) {
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(old, basSku);
            String remark = null;
            MongodbDeal.update(basSku.getSkuSid(), old.getHandleStatus(), basSku.getHandleStatus(), msgList, TITLE, remark);
        }
        return row;
    }

    /**
     * 批量删除SKU档案
     *
     * @param skuSids 需要删除的SKU档案ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasSkuByIds(List<String> skuSids) {
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid, skuSids));
        skuSids.forEach(id -> {
            //插入日志
            MongodbUtil.insertUserLog(Long.valueOf(id), BusinessType.DELETE.getValue(), null, TITLE);
        });
        return basSkuMapper.deleteBatchIds(skuSids);
    }

    @Override
    public List<BasSku> getList(String skuType) {
        return basSkuMapper.getList(skuType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(BasSku basSku) {
        int row = 0;
        Long[] sids = basSku.getSkuSidList();
        if (sids != null && sids.length > 0) {
            if (ConstantsEms.DISENABLE_STATUS.equals(basSku.getStatus())
                    && (basSku.getIsContinue() == null || basSku.getIsContinue() != true)) {
                List<BasMaterialSku> basMaterialSkuList =
                        basMaterialSkuMapper.selectBasMaterialSkuList(new BasMaterialSku().setSkuSidList(sids).setStatus(ConstantsEms.ENABLE_STATUS));
                if (CollUtil.isNotEmpty(basMaterialSkuList)) {
                    List<String> skuNameList = basMaterialSkuList.stream().map(BasMaterialSku::getSkuName).distinct().collect(Collectors.toList());
                    throw new BaseException(EmsResultEntity.WARN_TAG, "SKU" + skuNameList.toString() + "已被物料/商品引用，是否确认停用！");
                }
            }
            for (Long id : sids) {
                basSku.setSkuSid(id);
                BasSku entity = basSkuMapper.selectById(id);
                List<BasSku> entityList = basSkuMapper.selectList(new QueryWrapper<BasSku>().lambda()
                        .eq(BasSku::getSkuName, entity.getSkuName()));
                if (CollectionUtils.isNotEmpty(entityList)) {
                    entityList.forEach(item -> {
                        if (!item.getSkuSid().equals(id)) {
                            throw new CustomException("已存在同名的SKU,不允许此操作！");
                        }
                    });
                }
                row = basSkuMapper.updateById(basSku);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                String remark = StrUtil.isEmpty(basSku.getDisableRemark()) ? null : basSku.getDisableRemark();
                //插入日志
                MongodbDeal.status(id, basSku.getStatus(), null, TITLE, remark);
            }
        }
        return row;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(BasSku basSku) {
        int row = 0;
        Long[] sids = basSku.getSkuSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                basSku.setSkuSid(id);
                row = basSkuMapper.updateById(basSku);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                MongodbDeal.check(id, basSku.getHandleStatus(), null, TITLE, null);
            }
            //确认状态后删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(basSku.getHandleStatus())) {
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .in(SysTodoTask::getDocumentSid, sids));
            }
        }
        return row;
    }

    /**
     * sku 导入
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object importData(MultipartFile file) {
        List<BasSku> basSkuList = new ArrayList<>();
        //错误信息
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        CommonErrMsgResponse errMsg = null;
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
            //sku类型
            List<DictData> skuTypeDict = sysDictDataService.selectDictData("s_sku_type");
            skuTypeDict = skuTypeDict.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> skuTypeMaps = skuTypeDict.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //上下装
            List<DictData> upDownList = sysDictDataService.selectDictData("s_up_down_suit");
            upDownList = upDownList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> upDownListMaps = upDownList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            int num = 0;
            Map<String, String> codeMap = new HashMap<>();
            Map<String, String> nameMap = new HashMap<>();
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                num = i + 1;
                /*
                 * sku编码
                 */
                String code = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                if (code == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("sku编码不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (code.getBytes().length != code.length()) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("sku编码数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        if (code.length() > 8) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("sku编码长度不能超过8个字符，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (codeMap.get(code) == null) {
                                codeMap.put(code, "1");
                                List<BasSku> basSkuCodeList = basSkuMapper.selectList(new QueryWrapper<BasSku>().lambda()
                                        .eq(BasSku::getSkuCode, code));
                                if (CollectionUtil.isNotEmpty(basSkuCodeList)) {
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg(code + " 编码已存在，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                            } else {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(code + " 编码重复，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }
                }
                /*
                 * sku名称
                 */
                String name = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                if (name == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("sku名称不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if (code.length() > 180) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("sku名称长度不能超过180个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * sku类型
                 */
                String type_s = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                String type = null;
                if (type_s == null) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("sku类型不可为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    if(StrUtil.isBlank(skuTypeMaps.get(type_s))){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("sku类型配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        type=skuTypeMaps.get(type_s);
                    }
                }
                // 名称同类型不能重复
                if (name != null && type != null){
                    if (nameMap.get(name+type) == null) {
                        nameMap.put(name+type, "1");
                        List<BasSku> basSkuNameList = basSkuMapper.selectList(new QueryWrapper<BasSku>().lambda()
                                .eq(BasSku::getSkuName, name).eq(BasSku::getSkuType, type));
                        if (CollectionUtil.isNotEmpty(basSkuNameList)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(name + " 名称已存在，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    } else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(name + " 名称重复，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 上下装
                 */
                String updownSuit = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                if (updownSuit != null) {
                    if(StrUtil.isBlank(upDownListMaps.get(updownSuit))){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("上下装配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        updownSuit=upDownListMaps.get(updownSuit);
                    }
                }
                /*
                 * 客户简称
                 */
                String customerShortName = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                Long customerSid = null;
                if (customerShortName != null) {
                    try {
                        BasCustomer basCustomer = basCustomerMapper.selectOne(new QueryWrapper<BasCustomer>()
                                .lambda().eq(BasCustomer::getShortName, customerShortName));
                        if (basCustomer == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("客户简称为 "+customerShortName+" 没有对应的客户，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            if (!ConstantsEms.CHECK_STATUS.equals(basCustomer.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(basCustomer.getStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("客户简称为 "+customerShortName+" 对应的客户必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }else {
                                customerSid = basCustomer.getCustomerSid();
                            }
                        }
                    }catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("系统中 " + customerShortName + " 客户档案存在重复数据，请先检查该客户，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * sku数值
                 */
                String value_s = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                BigDecimal value = null;
                if (value_s != null){
                    if (ConstantsEms.SKUTYP_YS.equals(type) || ConstantsEms.SKUTYP_CM.equals(type)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("sku类型为颜色或者尺码时，sku数值必须为空，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        if (!JudgeFormat.isValidDouble(value_s, 6, 2)) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("sku数值数据格式错误，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            value = new BigDecimal(value_s);
                            if (value.compareTo(BigDecimal.ZERO) < 0) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("sku数值不能小于等于0，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }
                }else {
                    if (ConstantsEms.SKUTYPE_LE.equals(type)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("sku类型为长度时，sku数值不能为空，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 备注
                 */
                String remark = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                if (remark != null && remark.length() > 600){
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("备注长度不能大于600位，导入失败！");
                    errMsgList.add(errMsg);
                }
                if (CollectionUtil.isEmpty(errMsgList)){
                    BasSku basSku = new BasSku();
                    basSku.setSkuType(type)
                            .setSkuTypeName(type_s)
                            .setStatus(ConstantsEms.ENABLE_STATUS)
                            .setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setUpDownSuit(updownSuit);
                    basSku.setCustomerSid(customerSid);
                    basSku.setSkuNumeralValue(value)
                            .setRemark(remark);
                    basSku.setSkuCode(code);
                    basSku.setSkuName(name);
                    basSkuList.add(basSku);
                }
            }
            if (CollectionUtil.isNotEmpty(errMsgList)){
                return errMsgList;
            }else {
                int row = 0;
                row = basSkuMapper.inserts(basSkuList);
                //待办通知
                List<SysTodoTask> sysTodoTaskList = new ArrayList<>();
                basSkuList.forEach(basSku -> {
                    SysTodoTask sysTodoTask = new SysTodoTask();
                    sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                            .setTableName(ConstantsTable.TABLE_BAS_SKU)
                            .setDocumentSid(basSku.getSkuSid());
                    sysTodoTask.setTitle(basSku.getSkuTypeName() + "档案: " + basSku.getSkuCode() + " 当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(basSku.getSkuCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskList.add(sysTodoTask);
                    //插入日志
                    MongodbUtil.insertUserLog(basSku.getSkuSid(), BusinessType.IMPORT.getValue(), null, TITLE);
                });
                sysTodoTaskMapper.inserts(sysTodoTaskList);
                return row;
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
    }

    //填充
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
