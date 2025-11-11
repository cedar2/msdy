package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.constant.HttpStatus;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.DateUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsPrice;
import com.platform.ems.constant.ConstantsWorkbench;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.request.CheckUniqueCommonRequest;
import com.platform.ems.domain.dto.request.PurPurchasePriceActionRequest;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.domain.dto.response.PurPurchasePriceReportResponse;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConMeasureUnit;
import com.platform.ems.plug.domain.ConPurchaseType;
import com.platform.ems.plug.domain.ConTaxRate;
import com.platform.ems.plug.mapper.ConMeasureUnitMapper;
import com.platform.ems.plug.mapper.ConPurchaseGroupMapper;
import com.platform.ems.plug.mapper.ConPurchaseTypeMapper;
import com.platform.ems.plug.mapper.ConTaxRateMapper;
import com.platform.ems.service.*;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.workflow.domain.Submit;
import com.platform.ems.workflow.service.IWorkFlowService;
import com.platform.api.service.RemoteFlowableService;
import com.platform.flowable.domain.vo.FlowTaskVo;
import com.platform.flowable.domain.vo.FormParameter;
import com.platform.flowable.service.ISysDeployFormService;
import com.platform.system.domain.SysBusinessBcst;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysBusinessBcstMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 采购价信息主Service业务层处理
 *
 * @author ChenPinzhen
 * @date 2021-02-04
 */
@Service
@SuppressWarnings("all")
public  class PurPurchasePriceServiceImpl extends ServiceImpl<PurPurchasePriceMapper,PurPurchasePrice> implements IPurPurchasePriceService {
    @Autowired
    private PurPurchasePriceMapper purPurchasePriceMapper;
    @Autowired
    private PurPurchasePriceItemMapper purPurchasePriceItemMapper;
    @Autowired
    private PurPurchasePriceAttachmentMapper purPurchasePriceAttachmentMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private BasMaterialSkuMapper basMaterialSkuMapper;
    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private BasSkuMapper basSkuMapper;
    @Autowired
    private BasMaterialBarcodeMapper basMaterialBarcodeMapper;
    @Autowired
    private ITecBomHeadService iTecBomHeadService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private ConTaxRateMapper conTaxRateMapper;
    @Autowired
    private ConMeasureUnitMapper conMeasureUnitMapper;
    @Autowired
    private ISysFormProcessService formProcessService;
    @Autowired
	private ISystemUserService userService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private SysBusinessBcstMapper sysBusinessBcstMapper;
    @Autowired
    private ConPurchaseGroupMapper conPurchaseGroupMapper;
    @Autowired
    private ConPurchaseTypeMapper conPurchaseTypeMapper;
    @Autowired
    private RemoteFlowableService flowableService;
    @Autowired
    private IWorkFlowService workflowService;
    @Autowired
    private ISysDeployFormService sysDeployService;
    @Autowired
    private PurQuoteBargainItemMapper purQuoteBargainItemMapper;
    @Autowired
    private PurPurchaseOrderMapper purPurchaseOrderMapper;

    private static final String TITLE = "采购价";

    private static final String TTABLE = "s_pur_purchase_price";
    @Autowired
    private RemoteFlowableService  remoteFlowableService;

    /**
     * 查询采购价信息主
     *
     * @param clientId 采购价信息主ID
     * @return 采购价信息主
     */
    @Override
    public PurPurchasePrice selectPurPurchasePriceById(Long id) {
        //采购价主表
        PurPurchasePrice purPurchasePrice = purPurchasePriceMapper.selectPurPurchasePriceById(id);
        //取详情时将图片路径分割出来存入数组
        if (StrUtil.isNotBlank(purPurchasePrice.getPicturePathSecond())) {
            String[] picturePathList = purPurchasePrice.getPicturePathSecond().split(";");
            purPurchasePrice.setPicturePathList(picturePathList);
        }
        //采购价明细表
        List<PurPurchasePriceItem> purPurchasePriceItems = purPurchasePriceItemMapper.selectPurPurchasePriceItemById(id);
        if(CollectionUtil.isNotEmpty(purPurchasePriceItems)){
            purPurchasePriceItems=purPurchasePriceItems.stream().sorted(Comparator.comparing(PurPurchasePriceItem::getStartDate).reversed()).collect(Collectors.toList());
        }
        //采购价明附件表
        QueryWrapper<PurPurchasePriceAttachment> queryWrapperAttachment = new QueryWrapper<>();
        queryWrapperAttachment.eq("purchase_price_sid", id);
        List<PurPurchasePriceAttachment> purPurchasePriceAttachments = purPurchasePriceAttachmentMapper.selectList(queryWrapperAttachment);
//        setItemNum(purPurchasePriceItems);
        purPurchasePrice.setListPurPurchasePriceItem(purPurchasePriceItems);
        purPurchasePrice.setAttachmentList(purPurchasePriceAttachments);
        MongodbUtil.find(purPurchasePrice);
        return purPurchasePrice;
    }

    /**
     * 不含税值计算
     */
    public void changePrice( List<PurPurchasePriceItem> purPurchasePriceItems){
        if(CollectionUtil.isNotEmpty(purPurchasePriceItems)){
            purPurchasePriceItems.forEach(li->{
                if(li.getTaxRate()!=null){
                    if(li.getPurchasePriceTax()!=null){
                        li.setPurchasePrice(li.getPurchasePriceTax().divide(BigDecimal.ONE.add(li.getTaxRate()), 6, BigDecimal.ROUND_HALF_UP));
                    }
                    if(li.getDecPurPriceTax()!=null){
                        li.setDecPurPrice(li.getDecPurPriceTax().divide(BigDecimal.ONE.add(li.getTaxRate()), 6, BigDecimal.ROUND_HALF_UP));
                    }
                    if(li.getIncrePurPriceTax()!=null){
                        li.setIncrePurPrice(li.getIncrePurPriceTax().divide(BigDecimal.ONE.add(li.getTaxRate()), 6, BigDecimal.ROUND_HALF_UP));
                    }
                }
            });
        }
    }

    /**
     * 查询采购价信息主列表
     *
     * @param purPurchasePrice 采购价信息主
     * @return 采购价信息主
     */
    @Override
    public List<PurPurchasePrice> selectPurPurchasePriceList(PurPurchasePrice purPurchasePrice) {
    	List<PurPurchasePrice> purchasePriceList =  purPurchasePriceMapper.selectPurPurchasePriceList(purPurchasePrice);
        return purchasePriceList;
    }

    //基本计量单位和采购价格单位
    public void setUnit(List<PurPurchasePriceItem> listPurPurchasePriceItem){
        listPurPurchasePriceItem.forEach(li->{
            if(li.getUnitBase().equals(li.getUnitPrice())){
                li.setUnitConversionRate(BigDecimal.ONE);
            }else{
                if(li.getUnitConversionRate()==null){
                throw new  CustomException("采购价单位“与”基本计量单位“不一致，单位换算比例不允许为空");
                }
            }
        });
    }

    //导入校验
    public void setUnitImport(List<PurPurchasePriceItem> listPurPurchasePriceItem,int i){
        listPurPurchasePriceItem.forEach(li->{
            if(li.getUnitBase()!=null){
                if(li.getUnitBase().equals(li.getUnitPrice())){
                    li.setUnitConversionRate(BigDecimal.ONE);
                }else{
                    if(li.getUnitConversionRate()==null){
                        throw new  CustomException("第"+i+"行采购价单位“与”基本计量单位“不一致，单位换算比例不允许为空,不允许导入");
                    }
                }
            }
        });
    }
    /**
     * 新增采购价信息主
     *
     * @param purPurchasePrice 采购价信息主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult insertPurPurchasePrice(PurPurchasePrice purPurchasePrice) {
        AddCheck(purPurchasePrice);
        int row=purPurchasePriceMapper.insert(purPurchasePrice);
        //获取插入时自动填充的id 值
        Long purchasePriceSid = purPurchasePrice.getPurchasePriceSid();
        List<PurPurchasePriceAttachment> listPurPurchasePriceAttachment = purPurchasePrice.getAttachmentList();
        List<PurPurchasePriceItem> listPurPurchasePriceItem = purPurchasePrice.getListPurPurchasePriceItem();
        // 插入采购价明细表
        if (CollectionUtils.isNotEmpty(listPurPurchasePriceItem)) {
            setItemNum(listPurPurchasePriceItem);
            setUnit(listPurPurchasePriceItem);
            changePrice(listPurPurchasePriceItem);
            boolean judege = validTime(listPurPurchasePriceItem);
            if (judege) {
                listPurPurchasePriceItem.forEach(o -> {
                    //二层校验
                    judgeTime(purPurchasePrice,o);
                    o.setPurchasePriceSid(purchasePriceSid);
                    o.setHandleStatus(ConstantsEms.SAVA_STATUS);
                    purPurchasePriceItemMapper.insert(o);
                    if(purPurchasePrice.getImportHandle()==null){
                        //插入日志
                        MongodbUtil.insertUserLogItem(o.getPurchasePriceSid(), BusinessType.INSERT.getValue(),TITLE,o.getItemNum());
                    }else{
                        //插入日志
                        MongodbUtil.insertUserLogItem(o.getPurchasePriceSid(), BusinessType.IMPORT.getValue(),TITLE,o.getItemNum());
                    }
                });
            } else {
                return AjaxResult.error("明细中有效期时间段存在交集，不允许新增");
            }
        }
        // 插入采购价附加表
        if (CollectionUtils.isNotEmpty(listPurPurchasePriceAttachment)) {
            listPurPurchasePriceAttachment.forEach(o -> {
                o.setPurchasePriceSid(purchasePriceSid);
                purPurchasePriceAttachmentMapper.insert(o);
            });
        }
        //待办通知
        PurPurchasePrice purchasePrice = purPurchasePriceMapper.selectById(purPurchasePrice.getPurchasePriceSid());
        purPurchasePrice.setPurchasePriceCode(purchasePrice.getPurchasePriceCode());
        listPurPurchasePriceItem.forEach(li->{
            SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(TTABLE)
                        .setDocumentSid(purPurchasePrice.getPurchasePriceSid())
                        .setDocumentItemSid(li.getPurchasePriceItemSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("采购价" + purchasePrice.getPurchasePriceCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(purchasePrice.getPurchasePriceCode())
                            .setNoticeDate(new Date())
                            .setMenuId(ConstantsWorkbench.purchase_price)
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
        });
        return AjaxResult.success("采购价新增成功", new PurPurchasePrice().setPurchasePriceSid(purPurchasePrice.getPurchasePriceSid()));
    }

    /**
     * 新增/编辑直接提交采购价信息
     *
     * @param purPurchasePrice 采购价信息主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult submit(PurPurchasePrice purPurchasePrice) {
        int row = 0;
        AjaxResult result = AjaxResult.success();
        if (purPurchasePrice.getPurchasePriceSid() == null) {
            // 新建
            result = this.insertPurPurchasePrice(purPurchasePrice);
            if (HttpStatus.ERROR == (int)result.get(AjaxResult.CODE_TAG)) {
                return result;
            }
        }
        else {
            result = this.updatePurPurchasePrice(purPurchasePrice);
            if (HttpStatus.ERROR == (int)result.get(AjaxResult.CODE_TAG)) {
                return result;
            }
        }
        row = 1;
        if (row == 1) {
            List<Long> sidList = new ArrayList<Long>(){{add(purPurchasePrice.getPurchasePriceSid());}};
            this.processCheck(sidList);
            if (CollectionUtil.isNotEmpty(purPurchasePrice.getListPurPurchasePriceItem())) {
                // 查询是否审批
                String isApproval = ConstantsEms.NO;
                BasMaterial material = basMaterialMapper.selectById(purPurchasePrice.getMaterialSid());
                if (material != null && material.getPurchaseType() != null) {
                    ConPurchaseType purchaseType = conPurchaseTypeMapper.selectOne(new QueryWrapper<ConPurchaseType>()
                            .lambda().eq(ConPurchaseType::getCode, material.getPurchaseType()));
                    if(!(purchaseType!=null&&isApproval(purchaseType.getCode()))){
                        isApproval = ConstantsEms.YES;
                    }
                }
                // 提交
                Submit submit = new Submit();
                submit.setFormType(FormType.PurchasePrice.getCode());
                List<FormParameter> formParameters = new ArrayList<>();
                for (PurPurchasePriceItem item : purPurchasePrice.getListPurPurchasePriceItem()) {
                    FormParameter formParameter = new FormParameter();
                    formParameter.setParentId(String.valueOf(purPurchasePrice.getPurchasePriceSid()));
                    formParameter.setFormId(String.valueOf(item.getPurchasePriceItemSid()));
                    formParameter.setFormCode(String.valueOf(purPurchasePrice.getPurchasePriceCode()));
                    formParameter.setIsApproval(isApproval);
                    formParameters.add(formParameter);
                }
                submit.setFormParameters(formParameters);
                submit.setStartUserId(String.valueOf(ApiThreadLocalUtil.get().getUserid()));
                workflowService.submitByItem(submit);
            }
        }
        return AjaxResult.success(result.get(AjaxResult.DATA_TAG));
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(PurPurchasePrice purchasePrice) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, purchasePrice.getPurchasePriceSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, purchasePrice.getPurchasePriceSid()));
        }
    }

    //校验是否存在两个时间段存在交集
    private static boolean validTime(List<PurPurchasePriceItem> itemList) {
        for (int i = 0; i < itemList.size(); i++) {
            PurPurchasePriceItem item = itemList.get(i);
            for (int j = 0; j < itemList.size(); j++) {
                if (i >= j) {
                    continue;
                }
                PurPurchasePriceItem compareItem = itemList.get(j);
                Date startTime1 = item.getStartDate();
                Date endTime1 = item.getEndDate();
                Date startTime2 = compareItem.getStartDate();
                Date endTime2 = compareItem.getEndDate();
                if (startTime2.getTime() >= startTime1.getTime() && startTime2.getTime() <= endTime1.getTime()) {
                    return false;
                }
                if (endTime2.getTime() >= startTime1.getTime() && endTime2.getTime() <= endTime1.getTime()) {
                    return false;
                }
                if (startTime2.getTime() < startTime1.getTime() && endTime2.getTime() > endTime1.getTime()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断是否能新增采购价信息
     *
     * @param purPurchasePrice 采购价信息主
     * @return 结果
     */
    @Override
    public AjaxResult judgeAdd(PurPurchasePrice purPurchasePrice) {
        BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                .eq(BasMaterial::getMaterialCode,purPurchasePrice.getMaterialCode()));
        if(basMaterial!=null){
            String handleStatus = basMaterial.getHandleStatus();
            String status = basMaterial.getStatus();
            if(!ConstantsEms.ENABLE_STATUS.equals(status)){
                throw new CustomException("输入的商品/物料编码已停用，请检查！");
            }
            if(!ConstantsEms.CHECK_STATUS.equals(handleStatus)){
                throw new CustomException("输入的商品/物料编码非确认状态，请检查！");
            }
        }else{
            throw new CustomException("输入的商品/物料编码不存在，请检查！");
        }
        Boolean exitSku = purPurchasePrice.getSku1Sid() != null ? true : false;
        List<PurPurchasePrice> list = purPurchasePriceMapper.selectList(new QueryWrapper<PurPurchasePrice>().lambda()
                .eq(PurPurchasePrice::getVendorSid, purPurchasePrice.getVendorSid())
                .eq(PurPurchasePrice::getMaterialSid, purPurchasePrice.getMaterialSid())
                .eq(PurPurchasePrice::getPurchaseMode, purPurchasePrice.getPurchaseMode())
                .eq(PurPurchasePrice::getRawMaterialMode, purPurchasePrice.getRawMaterialMode())
                .eq(exitSku, PurPurchasePrice::getSku1Sid, purPurchasePrice.getSku1Sid()));

        if (exitSku) {
            if (CollectionUtils.isNotEmpty(list)) {
                PurPurchasePrice price = list.get(0);
                Long purchasePriceSid = price.getPurchasePriceSid();
                return AjaxResult.success("1",purchasePriceSid.toString());
            } else {
                return AjaxResult.success("允许新建采购价", "1");
            }
        } else {
            //查找是否存在sku1为null的情况
            PurPurchasePrice PurPurchasePriceExisted = list.stream().filter(o -> o.getSku1Sid() == null).findFirst().orElse(null);
            if (PurPurchasePriceExisted == null) {
                return AjaxResult.success("允许新建采购价", "1");
            } else {
                return AjaxResult.success("1",PurPurchasePriceExisted.getPurchasePriceSid().toString());
            }

        }
    }

    /**
     * 修改采购价信息主
     *
     * @param purPurchasePrice 采购价信息主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updatePurPurchasePrice(PurPurchasePrice purPurchasePrice) {
        AddCheck(purPurchasePrice);
        Long purchasePriceSid = purPurchasePrice.getPurchasePriceSid();
        List<PurPurchasePriceItem> listPurPurchasePriceItem = purPurchasePrice.getListPurPurchasePriceItem();
        List<PurPurchasePriceAttachment> listPurPurchasePriceAttachment = purPurchasePrice.getAttachmentList();
        PurPurchasePrice old = purPurchasePriceMapper.selectById(purPurchasePrice.getPurchasePriceSid());
        //修改采购价主表
        int row = purPurchasePriceMapper.updateAllById(purPurchasePrice);
        Long sid = purPurchasePrice.getPurchasePriceSid();
        boolean judege = validTime(listPurPurchasePriceItem);
        changePrice(listPurPurchasePriceItem);
        if (judege) {
            //新增现有采购价明细表
            if (CollectionUtils.isNotEmpty(listPurPurchasePriceItem)) {
                setPriceSid(purchasePriceSid,listPurPurchasePriceItem);
                listPurPurchasePriceItem.forEach(o -> {
                    //二层校验
                    judgeTime(purPurchasePrice, o);
                });
            }
        } else {
            return AjaxResult.error("明细中有效期时间段存在交集，不允许修改");
        }
        QueryWrapper<PurPurchasePriceItem> wrapper = new QueryWrapper<>();
        wrapper.eq("purchase_price_sid", sid);
        //删除原有的明细表
        purPurchasePriceItemMapper.delete(wrapper);
        if (CollectionUtils.isNotEmpty(listPurPurchasePriceItem)) {
            listPurPurchasePriceItem.forEach(li -> {
                li.setPurchasePriceSid(sid);
                purPurchasePriceItemMapper.insert(li);
            });
        }
        QueryWrapper<PurPurchasePriceAttachment> wrapperAttachment = new QueryWrapper<>();
        wrapperAttachment.eq("purchase_price_sid", sid);
        //删除原有的采购价附件表
        purPurchasePriceAttachmentMapper.delete(wrapperAttachment);
        //修改采购价附件表
        if (CollectionUtils.isNotEmpty(listPurPurchasePriceAttachment)) {
            listPurPurchasePriceAttachment.forEach(o -> {
                o.setPurchasePriceSid(sid);
                purPurchasePriceAttachmentMapper.insert(o);
            });
        }
        if (!ConstantsEms.SAVA_STATUS.equals(purPurchasePrice.getHandleStatus())) {
            //校验是否存在待办
            checkTodoExist(purPurchasePrice);
        }
        //更新通知
        if (ConstantsEms.CHECK_STATUS.equals(purPurchasePrice.getHandleStatus())) {
            SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
            sysBusinessBcst.setTitle("物料编码"+purPurchasePrice.getMaterialCode()+"，采购价编号"+purPurchasePrice.getPurchasePriceCode()+"的信息发生变更，请知悉！")
                    .setDocumentSid(purPurchasePrice.getPurchasePriceSid())
                    .setDocumentCode(purPurchasePrice.getPurchasePriceCode())
                    .setMenuId(ConstantsWorkbench.purchase_price)
                    .setNoticeDate(new Date()).setUserId(ApiThreadLocalUtil.get().getUserid());
            sysBusinessBcstMapper.insert(sysBusinessBcst);
        }
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(purPurchasePrice.getPurchasePriceSid(), BusinessType.UPDATE.getValue(), TITLE);
        }
        return AjaxResult.success("修改采购价信息成功");
        }

    /**
     * 校验同一时刻只允许存在一笔采购价进行中
     * @author
     * @return
     */
    @Override
    public void checkUnique(CheckUniqueCommonRequest request) {
        PurQuoteBargainItem purQuoteBargainItem = new PurQuoteBargainItem();
        BeanCopyUtils.copyProperties(request, purQuoteBargainItem);
        //查询出不是已确认的单据
        purQuoteBargainItem.setHandleStatusList(new String[]{HandleStatus.SUBMIT.getCode(), HandleStatus.CHANGEAPPROVAL.getCode()});
        //报核议价单
        List<PurQuoteBargainItem> bargainItemList = new ArrayList<>();
        //采购价单
        List<PurPurchasePriceItem> priceItemList = new ArrayList<>();
        //采购成本核算
        List<CosProductCost> productCostList = new ArrayList<>();
        if (ConstantsPrice.PRICE_DIMENSION.equals(request.getPriceDimension())) {
            purQuoteBargainItem.setPriceDimension(null);
            bargainItemList = purQuoteBargainItemMapper.selectPurRequestQuotationItemList(purQuoteBargainItem);
            priceItemList = purQuoteBargainItemMapper.selectPriceItemList(purQuoteBargainItem);
            productCostList = purQuoteBargainItemMapper.selectProductCostList(purQuoteBargainItem);
        } else if (ConstantsPrice.PRICE_DIMENSION_SKU1.equals(request.getPriceDimension())) {
            //1、查按色 sku1Sid
            purQuoteBargainItem.setSku1Sid(request.getSku1Sid());
            bargainItemList = purQuoteBargainItemMapper.selectPurRequestQuotationItemList(purQuoteBargainItem);
            priceItemList = purQuoteBargainItemMapper.selectPriceItemList(purQuoteBargainItem);
            productCostList = purQuoteBargainItemMapper.selectProductCostList(purQuoteBargainItem);
            //如果（1）没查到，则接着查：2、查价格维度：按款
            purQuoteBargainItem.setSku1Sid(null).setPriceDimension(ConstantsPrice.PRICE_DIMENSION);
            if (CollectionUtil.isEmpty(bargainItemList)) {
                bargainItemList = purQuoteBargainItemMapper.selectPurRequestQuotationItemList(purQuoteBargainItem);
            }
            if (CollectionUtil.isEmpty(priceItemList)) {
                priceItemList = purQuoteBargainItemMapper.selectPriceItemList(purQuoteBargainItem);
            }
            if (CollectionUtil.isEmpty(productCostList)) {
                productCostList = purQuoteBargainItemMapper.selectProductCostList(purQuoteBargainItem);
            }
        }
        String materialName = request.getMaterialName() == null ? "" : request.getMaterialName();
        if (CollectionUtil.isNotEmpty(bargainItemList)) {
            //如果是编辑 的，那要去掉跟本身的单据校验冲突
            if (request.getId() != null) {
                bargainItemList = bargainItemList.stream().filter(o -> !o.getQuoteBargainSid().toString().equals(request.getId().toString())).collect(Collectors.toList());
            }
            if (CollectionUtil.isNotEmpty(bargainItemList)) {
                String stage = bargainItemList.get(0).getCurrentStage();
                String stageName = setTitle(stage);
                throw new CustomException(request.getCode()+materialName + "存在相应的审批中的" + stageName + bargainItemList.get(0).getQuoteBargainCode() + "，请先处理此" + stageName);
            }
        }
        if (CollectionUtil.isNotEmpty(priceItemList)) {
            if (request.getId() != null) {
                priceItemList = priceItemList.stream().filter(o -> !o.getPurchasePriceSid().toString().equals(request.getId().toString())).collect(Collectors.toList());
            }
            if (CollectionUtil.isNotEmpty(priceItemList)) {
                throw new CustomException(request.getCode()+materialName + "存在相应的审批中的采购价信息" + priceItemList.get(0).getPurchasePriceCode() + "，请先处理此采购价信息");
            }
        }
        if (CollectionUtil.isNotEmpty(productCostList)) {
            if (request.getId() != null) {
                productCostList = productCostList.stream().filter(o -> !o.getProductCostSid().toString().equals(request.getId().toString())).collect(Collectors.toList());
            }
            if (CollectionUtil.isNotEmpty(productCostList)) {
                throw new CustomException(request.getCode()+materialName + "存在相应的审批中的采购成本核算信息，请先处理此采购成本核算信息");
            }
        }

    }

    /**
     * 采购价价格更新
     */
    public void insertPrice(PurPurchasePrice common,PurPurchasePriceItem commonItem){
        Long purchasePriceItemSid = commonItem.getPurchasePriceItemSid();
        String skipInsert = common.getSkipInsert();
        String handleStatus = common.getHandleStatus();
        Boolean exit=true;
        SalSalePrice salSalePrice = null;
        List<PurPurchasePrice> purPurchasePrices=null;
        List<Long>   purPurchasePricesids=null;
        String priceDimension = common.getPriceDimension();
        //按款
        if (ConstantsEms.PRICE_K.equals(priceDimension)) {
            purPurchasePrices = purPurchasePriceMapper.selectList(new QueryWrapper<PurPurchasePrice>().lambda()
                    .eq(PurPurchasePrice::getMaterialSid, common.getMaterialSid())
                    .eq(PurPurchasePrice::getRawMaterialMode, common.getRawMaterialMode())
                    .eq(PurPurchasePrice::getVendorSid, common.getVendorSid())
                    .eq(PurPurchasePrice::getHandleStatus,ConstantsEms.CHECK_STATUS)
                    .eq(PurPurchasePrice::getPurchaseMode, common.getPurchaseMode())
            );
        }else{
            //按色
            purPurchasePrices = purPurchasePriceMapper.selectList(new QueryWrapper<PurPurchasePrice>().lambda()
                    .eq(PurPurchasePrice::getMaterialSid, common.getMaterialSid())
                    .eq(PurPurchasePrice::getRawMaterialMode, common.getRawMaterialMode())
                    .eq(PurPurchasePrice::getPurchaseMode, common.getPurchaseMode())
                    .eq(PurPurchasePrice::getHandleStatus,ConstantsEms.CHECK_STATUS)
                    .eq(PurPurchasePrice::getPriceDimension, common.getPriceDimension())
                    .eq(PurPurchasePrice::getVendorSid, common.getVendorSid())
                    .eq(PurPurchasePrice::getSku1Sid,common.getSku1Sid())
            );
            if(CollectionUtil.isEmpty(purPurchasePrices)){
                purPurchasePrices = purPurchasePriceMapper.selectList(new QueryWrapper<PurPurchasePrice>().lambda()
                        .eq(PurPurchasePrice::getMaterialSid, common.getMaterialSid())
                        .eq(PurPurchasePrice::getHandleStatus,ConstantsEms.CHECK_STATUS)
                        .eq(PurPurchasePrice::getRawMaterialMode, common.getRawMaterialMode())
                        .eq(PurPurchasePrice::getVendorSid, common.getVendorSid())
                        .eq(PurPurchasePrice::getPurchaseMode, common.getPurchaseMode())
                        .eq(PurPurchasePrice::getPriceDimension, ConstantsEms.PRICE_K)
                );
            }
        }
        if (CollectionUtils.isNotEmpty(purPurchasePrices)) {
            purPurchasePricesids= purPurchasePrices.stream().map(o -> o.getPurchasePriceSid()).collect(Collectors.toList());
        }
        if (CollectionUtil.isNotEmpty(purPurchasePricesids)) {
            List<PurPurchasePriceItem> purPurchasePriceItems = purPurchasePriceItemMapper.selectList(new QueryWrapper<PurPurchasePriceItem>()
                    .lambda().in(PurPurchasePriceItem::getPurchasePriceSid, purPurchasePricesids)
            );
            purPurchasePriceItems= purPurchasePriceItems.stream().filter(li->!li.getPurchasePriceItemSid().toString().equals(commonItem.getPurchasePriceItemSid().toString())).collect(Collectors.toList());
            commonItem.setPurchasePriceItemSid(null);
            if(CollectionUtil.isNotEmpty(purPurchasePriceItems)){
                int max = -1;
                Date endTime = commonItem.getEndDate();
                //设置有效期 起
                Date startTime = commonItem.getStartDate();
                Optional<PurPurchasePriceItem> optiona = purPurchasePriceItems.stream().max(Comparator.comparingLong(li -> li.getEndDate().getTime()));
                //最大的有效期明细
                PurPurchasePriceItem item = optiona.get();
                PurPurchasePrice purPurchasePriceMax= purPurchasePriceMapper.selectById(item.getPurchasePriceSid());
                //判断新价格信息的“有效期（起）”是否大于旧的价格信息的最大的“有效期（至）”，如是，则直接写入新的价格
                if (item.getEndDate().getTime() < startTime.getTime()) {
                    if(!ConstantsEms.YES.equals(skipInsert)){
                        if(purPurchasePriceMax.getPriceDimension().equals(priceDimension)){
                            List<PurPurchasePriceItem> purPurchasePriceItemMaxs = purPurchasePriceItemMapper.selectList(new QueryWrapper<PurPurchasePriceItem>()
                                    .lambda().eq(PurPurchasePriceItem::getPurchasePriceSid, item.getPurchasePriceSid())
                            );
                            int maxItem = purPurchasePriceItemMaxs.stream().mapToInt(li -> li.getItemNum()).max().getAsInt();
                            max=maxItem+1;
                            commonItem.setPurchasePriceSid(item.getPurchasePriceSid())
                                    .setItemNum(max)
                                    .setPurchasePrice(commonItem.getPurchasePriceTax().divide(BigDecimal.ONE.add(commonItem.getTaxRate()),6,BigDecimal.ROUND_HALF_UP))
                                    .setHandleStatus(ConstantsEms.CHECK_STATUS);
                            MongodbUtil.insertApprovalLogAddNum(item.getPurchasePriceSid(), BusinessType.CHECK.getValue(), "来自采购价",maxItem+1);
                            purPurchasePriceItemMapper.insert(commonItem);
                            PurPurchasePrice price = new PurPurchasePrice();
                            price.setPurchasePriceSid(item.getPurchasePriceSid())
                                    .setHandleStatus(ConstantsEms.CHECK_STATUS);
                            purPurchasePriceMapper.updateById(price);
                            return;
                        }else{
                            common.setStatus(ConstantsEms.SAVA_STATUS)
                                    .setHandleStatus(ConstantsEms.CHECK_STATUS);
                            commonItem
                                    .setPurchasePrice(commonItem.getPurchasePriceTax().divide(BigDecimal.ONE.add(commonItem.getTaxRate()),6,BigDecimal.ROUND_HALF_UP))
                                    .setItemNum(1);
                            //插入销售价信息
                            purPurchasePriceMapper.insert(common);
                            commonItem.setPurchasePriceSid(common.getPurchasePriceSid());
                            purPurchasePriceItemMapper.insert(commonItem);
                            MongodbUtil.insertApprovalLogAddNum(item.getPurchasePriceSid(), BusinessType.CHECK.getValue(), "来自采购价",1);
                            return;
                        }
                    }
                }
                //判断系统中是否存在跟新价格信息的“有效期（起）”和“有效期（至）”一样的有效期
                for (int i = 0; i < purPurchasePriceItems.size(); i++) {
                    if (purPurchasePriceItems.get(i).getStartDate().getTime() == startTime.getTime() && purPurchasePriceItems.get(i).getEndDate().getTime() == endTime.getTime()) {
                        purPurchasePriceItems.get(i).setPurchasePriceTax(commonItem.getPurchasePriceTax());
                        purPurchasePriceItems.get(i).setPurchasePrice(commonItem.getPurchasePriceTax().divide(BigDecimal.ONE.add(commonItem.getTaxRate()),6,BigDecimal.ROUND_HALF_UP));
                        PurPurchasePrice purchasePrice = purPurchasePriceMapper.selectById(purPurchasePriceItems.get(i).getPurchasePriceSid());
                        String dimension =purchasePrice.getPriceDimension();
                        if(dimension.equals(priceDimension)){
                            if(!ConstantsEms.YES.equals(skipInsert)){
                                purPurchasePriceItemMapper.updateAllById(purPurchasePriceItems.get(i));
                                MongodbUtil.insertApprovalLogAddNum(purPurchasePriceItems.get(i).getPurchasePriceSid(), BusinessType.PRICE.getValue(), "来自采购价",purPurchasePriceItems.get(i).getItemNum());
                            }
                            return;
                        }else{
                            String dimensionMsg=dimension.equals(ConstantsEms.PRICE_K1)?"按色的":"按款的";
                            throw new CustomException(common.getPurchasePriceCode()+common.getMaterialName() + "当前存在" +  dimensionMsg + "采购价"+ purchasePrice.getPurchasePriceCode() + "的有效期与此成本核算单的有效期区间\n\n存在交集，请先手工更新旧的有效期后，再进行此操作。");
                        }
                    }
                }
                String dimensionMsg=purPurchasePriceMax.getPriceDimension().equals(ConstantsEms.PRICE_K1)?"按色的":"按款的";
                //判断新价格信息的“有效期（起）”是否比旧的价格的最大的“有效期（起）”大且新的价格信息的“有效期（至）”是否比旧的价格的最大的“有效期（至）”大或是相等，如是，将最大的旧的价格的“有效期（至）”改成“新的有效期（起）-1”，
                if (item.getStartDate().getTime() < startTime.getTime() && item.getEndDate().getTime() <= endTime.getTime()) {
                    if(purPurchasePriceMax.getPriceDimension().equals(priceDimension)){
                        item.setEndDate(DateUtil.offsetDay(startTime, -1));
                        if(!ConstantsEms.YES.equals(skipInsert)){
                            PurPurchasePrice price = new PurPurchasePrice();
                            price.setPurchasePriceSid(item.getPurchasePriceSid())
                                    .setHandleStatus(ConstantsEms.CHECK_STATUS);
                            purPurchasePriceMapper.updateById(price);
                            purPurchasePriceItemMapper.updateAllById(item);
                            MongodbUtil.insertApprovalLogAddNum(item.getPurchasePriceSid(), BusinessType.CHANGE.getValue(), "更新有效期至",item.getItemNum());
                            //插入一笔新的明细
                            List<PurPurchasePriceItem> purPurchasePriceItemMaxs = purPurchasePriceItemMapper.selectList(new QueryWrapper<PurPurchasePriceItem>()
                                    .lambda().eq(PurPurchasePriceItem::getPurchasePriceSid, item.getPurchasePriceSid())
                            );
                            int maxItem = purPurchasePriceItemMaxs.stream().mapToInt(li -> li.getItemNum()).max().getAsInt();
                            max=maxItem+1;
                            commonItem.setPurchasePriceSid(item.getPurchasePriceSid())
                                    .setItemNum(max)
                                    .setPurchasePrice(commonItem.getPurchasePriceTax().divide(BigDecimal.ONE.add(commonItem.getTaxRate()),6,BigDecimal.ROUND_HALF_UP));
                            MongodbUtil.insertApprovalLogAddNum(item.getPurchasePriceSid(), BusinessType.CHECK.getValue(), "来自采购价",maxItem+1);
                            purPurchasePriceItemMapper.insert(commonItem);
                        }
                        return;
                    }else{
                        throw new CustomException(common.getPurchasePriceCode()+common.getMaterialName() + "当前存在" +  dimensionMsg + "采购价"+ purPurchasePriceMax.getPurchasePriceCode() + "的有效期与此成本核算单的有效期区间\n\n存在交集，请先手工更新旧的有效期后，再进行此操作。");
                    }
                }else{
                    throw new CustomException(common.getPurchasePriceCode()+common.getMaterialName() + "当前存在" +  dimensionMsg + "采购价"+ purPurchasePriceMax.getPurchasePriceCode() + "的有效期与此成本核算单的有效期区间\n\n存在交集，请先手工更新旧的有效期后，再进行此操作。");
                }
            }else{
                if(!ConstantsEms.YES.equals(skipInsert)){
                    common.setStatus(ConstantsEms.SAVA_STATUS)
                            .setHandleStatus(ConstantsEms.CHECK_STATUS);
                    commonItem
                            .setPurchasePrice(commonItem.getPurchasePriceTax().divide(BigDecimal.ONE.add(commonItem.getTaxRate()),6,BigDecimal.ROUND_HALF_UP))
                            .setItemNum(1);
                    //插入采购价信息
                    purPurchasePriceMapper.insert(common);
                    commonItem.setPurchasePriceSid(common.getPurchasePriceSid());
                    commonItem.setPurchasePriceItemSid(null);
                    purPurchasePriceItemMapper.insert(commonItem);
                    MongodbUtil.insertApprovalLogAddNum(common.getPurchasePriceSid(), BusinessType.CHECK.getValue(), "来自采购价",1);
                }
            }
        } else {
            if(!ConstantsEms.YES.equals(skipInsert)){
                common.setStatus(ConstantsEms.SAVA_STATUS)
                        .setHandleStatus(ConstantsEms.CHECK_STATUS);
                commonItem
                        .setPurchasePrice(commonItem.getPurchasePriceTax().divide(BigDecimal.ONE.add(commonItem.getTaxRate()),6,BigDecimal.ROUND_HALF_UP))
                        .setItemNum(1);
                //插入采购价信息
                purPurchasePriceMapper.insert(common);
                commonItem.setPurchasePriceSid(common.getPurchasePriceSid());
                commonItem.setPurchasePriceItemSid(null);
                purPurchasePriceItemMapper.insert(commonItem);
                MongodbUtil.insertApprovalLogAddNum(common.getPurchasePriceSid(), BusinessType.CHECK.getValue(), "来自采购价",1);
            }
        }
        if(!ConstantsEms.YES.equals(skipInsert)){
            purPurchasePriceItemMapper.deleteById(purchasePriceItemSid);
        }
    }

    /**
     * 设置对应单据状态的标题
     * @author chenkw
     * @return title
     */
    public String setTitle(String stage){
        String title = "";
        if (ConstantsPrice.BAOHEYI_STAGE_BJ.equals(stage)){
            title = "采购报价单";
        }else if (ConstantsPrice.BAOHEYI_STAGE_HJ.equals(stage)){
            title = "采购核价单";
        } else if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(stage)){
            title = "采购议价单";
        } else {
            title  = "";
        }
        return title;
    }

    /**
     * 修改采购价信息主-新
     *
     * @param purPurchasePrice 采购价信息主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updatePurPurchasePriceNew(PurPurchasePrice purPurchasePrice) {
        // 唯一性校验
        QueryWrapper<PurPurchasePrice> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .ne(PurPurchasePrice::getPurchasePriceSid, purPurchasePrice.getPurchasePriceSid())
                .eq(PurPurchasePrice::getMaterialSid, purPurchasePrice.getMaterialSid())
                .eq(PurPurchasePrice::getRawMaterialMode, purPurchasePrice.getRawMaterialMode())
                .eq(PurPurchasePrice::getPurchaseMode, purPurchasePrice.getPurchaseMode())
                .eq(PurPurchasePrice::getPriceDimension, purPurchasePrice.getPriceDimension());
        if (purPurchasePrice.getSku1Sid() == null) {
            queryWrapper.lambda().isNull(PurPurchasePrice::getSku1Sid);
        }
        else {
            queryWrapper.lambda().eq(PurPurchasePrice::getSku1Sid, purPurchasePrice.getSku1Sid());
        }
        if (purPurchasePrice.getVendorSid() == null) {
            queryWrapper.lambda().isNull(PurPurchasePrice::getVendorSid);
        }
        else {
            queryWrapper.lambda().eq(PurPurchasePrice::getVendorSid, purPurchasePrice.getVendorSid());
        }
        if (CollectionUtil.isNotEmpty(purPurchasePriceMapper.selectList(queryWrapper))) {
            return AjaxResult.error("物料/商品+供应商+甲供料方式+采购模式+价格维度+SKU1”维度的采购价已存在！");
        }

        AddCheck(purPurchasePrice);
        Long purchasePriceSid = purPurchasePrice.getPurchasePriceSid();
        List<PurPurchasePriceItem> listPurPurchasePriceItem = purPurchasePrice.getListPurPurchasePriceItem();
        List<PurPurchasePriceAttachment> listPurPurchasePriceAttachment = purPurchasePrice.getAttachmentList();
        PurPurchasePrice old = purPurchasePriceMapper.selectById(purPurchasePrice.getPurchasePriceSid());
        //修改采购价主表
        int row = purPurchasePriceMapper.updateAllById(purPurchasePrice);
        Long sid = purPurchasePrice.getPurchasePriceSid();
        boolean judege = validTime(listPurPurchasePriceItem);
        if (judege) {
            //新增现有采购价明细表
            if (CollectionUtils.isNotEmpty(listPurPurchasePriceItem)) {
                setUnit(listPurPurchasePriceItem);
                setPriceSid(purchasePriceSid,listPurPurchasePriceItem);
                listPurPurchasePriceItem.forEach(o -> {
                    //二层校验
                    judgeTime(purPurchasePrice, o);
                });
            }
        } else {
            return AjaxResult.error("明细中有效期时间段存在交集，不允许修改");
        }
        this.processCheck(new ArrayList<Long>(){{add(purPurchasePrice.getPurchasePriceSid());}});
        if(ConstantsEms.CHECK_STATUS.equals(purPurchasePrice.getHandleStatus())){
            //插入日志
            MongodbUtil.insertUserLog(purPurchasePrice.getPurchasePriceSid(), BusinessType.CHANGE.getValue(),TITLE);
        }
        if (CollectionUtils.isNotEmpty(listPurPurchasePriceItem)) {
            if(listPurPurchasePriceItem.size()==1){
                listPurPurchasePriceItem.forEach(item->{
                    item.setSubmitHandle(ConstantsEms.YES);
                });
            }
//            setItemNum(listPurPurchasePriceItem);
            changePrice(listPurPurchasePriceItem);
            List<PurPurchasePriceItem> purPurchasePriceItems = purPurchasePriceItemMapper.selectList(new QueryWrapper<PurPurchasePriceItem>().lambda()
                    .eq(PurPurchasePriceItem::getPurchasePriceSid, purPurchasePrice.getPurchasePriceSid())
            );
            List<Long> longs = purPurchasePriceItems.stream().map(li -> li.getPurchasePriceItemSid()).collect(Collectors.toList());
            List<Long> longsNow = listPurPurchasePriceItem.stream().map(li -> li.getPurchasePriceItemSid()).collect(Collectors.toList());
            //两个集合取差集
            List<Long> reduce = longs.stream().filter(item -> !longsNow.contains(item)).collect(Collectors.toList());
            //删除明细
            if(CollectionUtil.isNotEmpty(reduce)){
                List<PurPurchasePriceItem> reduceList = purPurchasePriceItemMapper.selectList(new QueryWrapper<PurPurchasePriceItem>().lambda()
                        .in(PurPurchasePriceItem::getPurchasePriceItemSid, reduce)
                );
                reduceList.forEach(li->{
                    //插入日志
                    MongodbUtil.insertUserLogItem(li.getPurchasePriceSid(), BusinessType.DELETE.getValue(),TITLE,li.getItemNum());
                });
                purPurchasePriceItemMapper.deleteBatchIds(reduce);
                //删除待办
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .in(SysTodoTask::getDocumentItemSid, reduce));
            }
            //修改明细
            List<PurPurchasePriceItem> exitItem = listPurPurchasePriceItem.stream().filter(li -> li.getPurchasePriceItemSid() != null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(exitItem)){
                exitItem.forEach(li->{
                    if(HandleStatus.CHANGEAPPROVAL.getCode().equals(li.getHandleStatus())&&purPurchasePrice.getPurchaseType()!=null&&isApproval(purPurchasePrice.getPurchaseType())){
                        li.setHandleStatus(ConstantsEms.CHECK_STATUS);
                        MongodbUtil.insertUserLogItem(li.getPurchasePriceSid(), BusinessType.CHECK.getValue(),TITLE,li.getItemNum());
                    }
                    purPurchasePriceItemMapper.updateAllById(li);
                    if(purPurchasePrice.getImportHandle()==null){
                        PurPurchasePriceItem oldItem = purPurchasePriceItemMapper.selectById(li.getPurchasePriceItemSid());
                        String bussiness=oldItem.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)?"变更":"编辑";
                        if(!ConstantsEms.IMPORT.equals(purPurchasePrice.getImportHandle())){
                            if(ConstantsEms.YES.equals(li.getSubmitHandle())){
                                if(ConstantsEms.SAVA_STATUS.equals(oldItem.getHandleStatus())){
                                    //插入日志
                                    MongodbUtil.insertUserLogItem(li.getPurchasePriceSid(), bussiness,TITLE,li.getItemNum());
                                }
                            }
                        }
                    }
                });
            }
            //新增明细
            List<PurPurchasePriceItem> nullItem = listPurPurchasePriceItem.stream().filter(li -> li.getPurchasePriceItemSid() == null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(nullItem)){
                int max = purPurchasePriceItems.stream().mapToInt(li -> li.getItemNum()).max().getAsInt();
                for (int i = 0; i < nullItem.size(); i++) {
                    int maxItem=max+i+1;
                    nullItem.get(i).setItemNum(maxItem);
                    nullItem.get(i).setPurchasePriceSid(purPurchasePrice.getPurchasePriceSid());
                    if(nullItem.get(i).getHandleStatus()==null){
                        nullItem.get(i).setHandleStatus(ConstantsEms.SAVA_STATUS);
                    }
                    if(ConstantsEms.IMPORT.equals(nullItem.get(i).getImportHandle())){
                        //插入日志
                        MongodbUtil.insertUserLogItem(nullItem.get(i).getPurchasePriceSid(), BusinessType.IMPORT.getValue(),TITLE,nullItem.get(i).getItemNum());
                    }else{
                        if(ConstantsEms.YES.equals(nullItem.get(i).getSubmitHandle())){
                            //插入日志
                            MongodbUtil.insertUserLogItem(nullItem.get(i).getPurchasePriceSid(), BusinessType.INSERT.getValue(),TITLE,nullItem.get(i).getItemNum());
                        }
                    }
                    if(HandleStatus.SUBMIT.getCode().equals(nullItem.get(i).getHandleStatus())&&purPurchasePrice.getPurchaseType()!=null&&isApproval(purPurchasePrice.getPurchaseType())){
                        nullItem.get(i).setHandleStatus(ConstantsEms.CHECK_STATUS);
                        MongodbUtil.insertUserLogItem(nullItem.get(i).getPurchasePriceSid(), BusinessType.CHECK.getValue(),TITLE,nullItem.get(i).getItemNum());
                    }
                    purPurchasePriceItemMapper.insert(nullItem.get(i));
                }
                addTodo(nullItem,purPurchasePrice);
            }
        }
        QueryWrapper<PurPurchasePriceAttachment> wrapperAttachment = new QueryWrapper<>();
        wrapperAttachment.eq("purchase_price_sid", sid);
        //删除原有的采购价附件表
        purPurchasePriceAttachmentMapper.delete(wrapperAttachment);
        //修改采购价附件表
        if (CollectionUtils.isNotEmpty(listPurPurchasePriceAttachment)) {
            listPurPurchasePriceAttachment.forEach(o -> {
                o.setPurchasePriceSid(sid);
                purPurchasePriceAttachmentMapper.insert(o);
            });
        }
        List<PurPurchasePriceItem> listItem = purPurchasePrice.getListPurPurchasePriceItem();
        if(!(purPurchasePrice.getPurchaseType()!=null&&isApproval(purPurchasePrice.getPurchaseType()))){
            if(CollectionUtils.isNotEmpty(listItem)){
                listItem.forEach(item->{
                    //变更审批
                    if (HandleStatus.CHANGEAPPROVAL.getCode().equals(item.getHandleStatus())){
                        Submit submit = new Submit();
                        submit.setStartUserId(ApiThreadLocalUtil.get().getUserid().toString());
                        submit.setFormType(FormType.CGJ_BG.getCode());
                        List<FormParameter> list = new ArrayList();
                        FormParameter formParameter = new FormParameter();
                        formParameter.setParentId(item.getPurchasePriceSid().toString());
                        formParameter.setFormId(item.getPurchasePriceItemSid().toString());
                        formParameter.setFormCode(purPurchasePrice.getPurchasePriceCode());
                        list.add(formParameter);
                        submit.setFormParameters(list);
                        workflowService.change(submit);
                    }
                    //新增行 正常审批
                    if(HandleStatus.SUBMIT.getCode().equals(item.getHandleStatus())){
                        Submit submit = new Submit();
                        submit.setStartUserId(ApiThreadLocalUtil.get().getUserid().toString());
                        submit.setFormType(FormType.PurchasePrice.getCode());
                        List<FormParameter> list = new ArrayList();
                        FormParameter formParameter = new FormParameter();
                        formParameter.setParentId(item.getPurchasePriceSid().toString());
                        formParameter.setFormId(item.getPurchasePriceItemSid().toString());
                        formParameter.setFormCode(purPurchasePrice.getPurchasePriceCode());
                        formParameter.setIsApproval(ConstantsEms.YES);
                        list.add(formParameter);
                        submit.setFormParameters(list);
                        workflowService.submitByItem(submit);
                    }
                });
            }
        }
        return AjaxResult.success("修改采购价信息成功");
    }

    /**
     * 查询页面变更有效期
     *
     * @param purPurchasePrice
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult changeItemTime(PurPurchasePriceReportResponse request) {
        PurPurchasePriceItem purPurchasePriceItem = new PurPurchasePriceItem();
        BeanCopyUtils.copyProperties(request, purPurchasePriceItem);
        if (purPurchasePriceItem.getPurchasePriceSid() == null) {
            return AjaxResult.error();
        }
        PurPurchasePrice price = purPurchasePriceMapper.selectPurPurchasePriceById(purPurchasePriceItem.getPurchasePriceSid());
        if (price == null) {
            return AjaxResult.error();
        }
        List<PurPurchasePriceItem> priceItemList = purPurchasePriceItemMapper.selectList(new QueryWrapper<PurPurchasePriceItem>()
                .lambda().eq(PurPurchasePriceItem::getPurchasePriceSid, purPurchasePriceItem.getPurchasePriceSid()));
        if (CollectionUtil.isEmpty(priceItemList)) {
            return AjaxResult.error();
        }
        // 移除原有单，写入更新有效期后的新单
        String oldStart = ""; String oldEnd = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Iterator it = priceItemList.iterator();
        while(it.hasNext()){
            PurPurchasePriceItem item = (PurPurchasePriceItem)it.next();
            if (item.getPurchasePriceItemSid().equals(purPurchasePriceItem.getPurchasePriceItemSid())) {
                // 得到旧的有效期和备注
                if (item.getStartDate() != null) {
                    oldStart = sdf.format(item.getStartDate());
                }
                if (item.getEndDate() != null) {
                    oldEnd = sdf.format(item.getEndDate());
                }
                it.remove(); //移除该对象
                break;
            }
        }
        priceItemList.add(purPurchasePriceItem);
        // 校验本单
        boolean judege = validTime(priceItemList);
        if (judege) {
            //二层校验 校验其它单
            judgeTime(price, purPurchasePriceItem);
        } else {
            return AjaxResult.error("明细中有效期时间段存在交集，不允许修改");
        }
        // 变更有效期
        LambdaUpdateWrapper<PurPurchasePriceItem> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PurPurchasePriceItem::getPurchasePriceItemSid, purPurchasePriceItem.getPurchasePriceItemSid())
                .set(PurPurchasePriceItem::getStartDate, purPurchasePriceItem.getStartDate())
                .set(PurPurchasePriceItem::getEndDate, purPurchasePriceItem.getEndDate())
                .set(PurPurchasePriceItem::getRemark, purPurchasePriceItem.getRemark());
        int row = purPurchasePriceItemMapper.update(null, updateWrapper);
        // 记录操作日志
        if (row > 0) {
            String newStart = ""; String newEnd = "";
            // 得到新的有效期
            if (purPurchasePriceItem.getStartDate() != null) {
                newStart = sdf.format(purPurchasePriceItem.getStartDate());
            }
            if (purPurchasePriceItem.getEndDate() != null) {
                newEnd = sdf.format(purPurchasePriceItem.getEndDate());
            }
            String remark = "变更前：有效期" + oldStart + "至" + oldEnd + "；变更后：有效期" + newStart + "至" + newEnd;
            MongodbUtil.insertUserLogItem(purPurchasePriceItem.getPurchasePriceSid(),BusinessType.CHANGE.getValue(), TITLE, request.getItemNum(), remark);
            MongodbUtil.insertUserLogItem(purPurchasePriceItem.getPurchasePriceItemSid(),BusinessType.CHANGE.getValue(), TITLE, request.getItemNum(), remark);
        }
        return AjaxResult.success(row);
    }

    //新增明细时，新增代办
    public void addTodo(List<PurPurchasePriceItem> list,PurPurchasePrice purPurchasePrice){
        list.forEach(li->{
            if(!HandleStatus.SUBMIT.getCode().equals(li.getHandleStatus())){
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(TTABLE)
                        .setDocumentSid(purPurchasePrice.getPurchasePriceSid())
                        .setDocumentItemSid(li.getPurchasePriceItemSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("采购价" + purPurchasePrice.getPurchasePriceCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(purPurchasePrice.getPurchasePriceCode())
                            .setNoticeDate(new Date())
                            .setMenuId(ConstantsWorkbench.purchase_price)
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            }
        });
    }


    /**
     * 明细赋值主表sid
     */
    public void setPriceSid(Long id,List<PurPurchasePriceItem>list ){
        list.forEach(li->{
            li.setPurchasePriceSid(id);
        });
    }

    public void AddCheck(PurPurchasePrice purPurchasePrice){
        String handleStatus = purPurchasePrice.getHandleStatus();
        if(ConstantsEms.CHECK_STATUS.equals(handleStatus)){
            purPurchasePrice.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            purPurchasePrice.setConfirmDate(new Date());
        }
    }

    /**
     * 变更采购价信息主
     *
     * @param purPurchasePrice 采购价信息主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult changePurPurchasePrice(PurPurchasePrice purPurchasePrice) {
        PurPurchasePrice old=purPurchasePriceMapper.selectById(purPurchasePrice.getPurchasePriceSid());
        AddCheck(purPurchasePrice);
        Long purchasePriceSid = purPurchasePrice.getPurchasePriceSid();
        //确认 5
        String confirmHandeleStatus = ConstantsEms.CHECK_STATUS;
        //当前状态
        String nowHandleStatus = purPurchasePriceMapper.selectById(purchasePriceSid).getHandleStatus();
        List<PurPurchasePriceItem> listPurPurchasePriceItem = purPurchasePrice.getListPurPurchasePriceItem();
        changePrice(listPurPurchasePriceItem);
        List<PurPurchasePriceAttachment> listPurPurchasePriceAttachment = purPurchasePrice.getAttachmentList();
        if (confirmHandeleStatus.equals(nowHandleStatus)) {
            //修改采购价主表
            int row=purPurchasePriceMapper.updateAllById(purPurchasePrice);
            Long sid = purPurchasePrice.getPurchasePriceSid();
            boolean judege = validTime(listPurPurchasePriceItem);
            if (judege) {
                //新增现有采购价明细表
                if (CollectionUtils.isNotEmpty(listPurPurchasePriceItem)) {
                    setPriceSid(purchasePriceSid,listPurPurchasePriceItem);
                    listPurPurchasePriceItem.forEach(o -> {
                        //二层校验
                        judgeTime(purPurchasePrice,o);
                    });
                }
            } else {
                return AjaxResult.error("明细中有效期时间段存在交集，不允许变更");
            }
            QueryWrapper<PurPurchasePriceItem> wrapper = new QueryWrapper<>();
            wrapper.eq("purchase_price_sid", sid);
            //删除原有的明细表
            purPurchasePriceItemMapper.delete(wrapper);
            if(CollectionUtils.isNotEmpty(listPurPurchasePriceItem)){
                listPurPurchasePriceItem.forEach(li->{
                    li.setPurchasePriceSid(sid);
                    purPurchasePriceItemMapper.insert(li);
                });
            }
            QueryWrapper<PurPurchasePriceAttachment> wrapperAttachment = new QueryWrapper<>();
            wrapperAttachment.eq("purchase_price_sid", sid);
            //删除原有的采购价附件表
            purPurchasePriceAttachmentMapper.delete(wrapperAttachment);
            //修改采购价附件表
            if (CollectionUtils.isNotEmpty(listPurPurchasePriceAttachment)) {
                listPurPurchasePriceAttachment.forEach(o -> {
                    o.setPurchasePriceSid(sid);
                    purPurchasePriceAttachmentMapper.insert(o);
                });
            }
            //更新通知
            if (ConstantsEms.CHECK_STATUS.equals(purPurchasePrice.getHandleStatus())) {
                SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
                sysBusinessBcst.setTitle("物料编码"+purPurchasePrice.getMaterialCode()+"，采购价编号"+purPurchasePrice.getPurchasePriceCode()+"的信息发生变更，请知悉！")
                        .setDocumentSid(purPurchasePrice.getPurchasePriceSid())
                        .setDocumentCode(purPurchasePrice.getPurchasePriceCode())
                        .setMenuId(ConstantsWorkbench.purchase_price)
                        .setNoticeDate(new Date()).setUserId(ApiThreadLocalUtil.get().getUserid());
                sysBusinessBcstMapper.insert(sysBusinessBcst);
            }
            if(row>0){
                //插入日志
                MongodbUtil.insertUserLog(purPurchasePrice.getPurchasePriceSid(), BusinessType.CHANGE.getValue(), old,purPurchasePrice,TITLE);
            }
            return AjaxResult.success("变更采购价信息成功");
        } else {
            return AjaxResult.error("仅确认状态下才可修改");
        }

    }

    /**
     * 批量删除采购价信息主
     *
     * @param ids 需要删除的采购价信息主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deletePurPurchasePriceByIds(List<Long> ids) {
        //保存 1
        String[] handeleStatus = {HandleStatus.SAVE.getCode(),HandleStatus.RETURNED.getCode()};
        QueryWrapper<PurPurchasePrice> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("handle_status", handeleStatus)
                .in("purchase_price_sid", ids);
        Integer selectCount = purPurchasePriceMapper.selectCount(queryWrapper);
        //判断是否符合条件
        if (selectCount == ids.size()) {
            //删除采购价主表
            int count = purPurchasePriceMapper.deleteBatchIds(ids);
            //删除采购价明细表
            QueryWrapper<PurPurchasePriceItem> wrapperItem = new QueryWrapper<>();
            wrapperItem.in("purchase_price_sid", ids);
            purPurchasePriceItemMapper.delete(wrapperItem);
            //删除采购价附件表
            QueryWrapper<PurPurchasePriceAttachment> wrapperAttachment = new QueryWrapper<>();
            wrapperAttachment.in("purchase_price_sid", ids);
            purPurchasePriceAttachmentMapper.delete(wrapperAttachment);
            ids.forEach(li->{
                PurPurchasePrice purchasePrice = new PurPurchasePrice();
                purchasePrice.setPurchasePriceSid(li);
                //校验是否存在待办
                checkTodoExist(purchasePrice);
                //插入日志
                MongodbUtil.insertUserLog(li,BusinessType.DELETE.getValue(),  TITLE);
            });
            return AjaxResult.success("删除成功，删除" + count + "条采购价信息");
        } else {
            return AjaxResult.error("仅保存状态下才可删除");
        }

    }

    /**
     * 批量删除采购价信息主
     *
     * @param ids 需要删除的采购价信息主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteItem(List<Long> ids) {
        List<PurPurchasePriceItem> purPurchasePriceItems = purPurchasePriceItemMapper.selectList(new QueryWrapper<PurPurchasePriceItem>().lambda()
                .in(PurPurchasePriceItem::getPurchasePriceItemSid, ids)
        );
        purPurchasePriceItems.forEach(item->{
            MongodbUtil.insertUserLogItem(item.getPurchasePriceSid(), BusinessType.DELETE.getValue(),TITLE,item.getItemNum());
        });
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentItemSid, ids));
        List<Long> longs = purPurchasePriceItems.stream().map(li -> li.getPurchasePriceSid()).collect(Collectors.toList());
        purPurchasePriceItemMapper.deleteBatchIds(ids);
        longs.forEach(item->{
            List<PurPurchasePriceItem> items = purPurchasePriceItemMapper.selectList(new QueryWrapper<PurPurchasePriceItem>().lambda()
                    .eq(PurPurchasePriceItem::getPurchasePriceSid, item)
            );
            if(CollectionUtil.isEmpty(items)){
                //明细为空时，删除对应的主表
                purPurchasePriceMapper.deleteById(item);
            }
        });
        return 1;
    }
    public void JudgeNull(PurPurchasePrice purPurchasePrice){
        List<PurPurchasePriceItem> list = purPurchasePrice.getListPurPurchasePriceItem();
        if(CollectionUtils.isNotEmpty(list)){
            list.forEach(li->{
                if(li.getPurchasePriceTax()==null||li.getTaxRate()==null){
                    throw new CustomException("存在采购价或税率未维护的明细行，请填写后再确认");
                }
            });
        }else{
            throw new CustomException("确认时明细行不允许为空");
        }
    }

    /**
     * 确认采购价信息主信息
     *
     * @param purPurchasePriceActionRequset 采购价信息主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult confirm(PurPurchasePriceActionRequest purPurchasePriceActionRequset) {
        //所要改变的状态
        String ChangeHandleStatus = purPurchasePriceActionRequset.getHandleStatus();
        Long[] purchasePriceSid = purPurchasePriceActionRequset.getPurchasePriceSids();
        for (Long li : purchasePriceSid) {
            PurPurchasePrice purPurchasePrice = selectPurPurchasePriceById(li);
            //校验是否存在待办
            checkTodoExist(purPurchasePrice);
            purPurchasePrice.setHandleStatus(ConstantsEms.CHECK_STATUS);
            JudgeNull(purPurchasePrice);
            List<PurPurchasePriceItem> list = purPurchasePrice.getListPurPurchasePriceItem();
            if(CollectionUtils.isNotEmpty(list)){
                list.forEach(item->{
                    //二层校验
                    judgeTime(purPurchasePrice,item);
                });
            }
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(li, BusinessType.CHECK.getValue(), msgList,TITLE);
        }
        PurPurchasePrice purPurchasePrice = new PurPurchasePrice();
        int count = purPurchasePriceMapper.update(purPurchasePrice, new UpdateWrapper<PurPurchasePrice>().lambda()
                .in(PurPurchasePrice::getPurchasePriceSid, purchasePriceSid)
                .set(PurPurchasePrice::getHandleStatus, ConstantsEms.CHECK_STATUS)
                .set(PurPurchasePrice::getConfirmDate, new Date())
                .set(PurPurchasePrice::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
        );
        return AjaxResult.success("确认成功，确认" + count + "条采购价信息");
    }
    /**
     * 行号赋值
     */
    public void  setItemNum(List<PurPurchasePriceItem> list){
        int size = list.size();
        if(size>0){
            for (int i=1;i<=size;i++){
                list.get(i-1).setItemNum(i);
            }
        }
    }
    /**
     * 提交时校验
     */
    public int processCheck(Long purchasePriceSid){
        PurPurchasePrice purPurchasePrice = selectPurPurchasePriceById(purchasePriceSid);
        purPurchasePrice.setHandleStatus(ConstantsEms.CHECK_STATUS);
        List<PurPurchasePriceItem> list = purPurchasePrice.getListPurPurchasePriceItem();
        if(CollectionUtils.isNotEmpty(list)){
            list.forEach(li->{
                if(li.getPurchasePriceTax()==null||li.getTaxRate()==null){
                    throw new CustomException("存在采购价或税率未维护的明细行，请填写后再确认");
                }
            });
        }else{
            throw new CustomException("提交时明细行不允许为空");
        }
        if(CollectionUtils.isNotEmpty(list)){
            list.forEach(item->{
                //二层校验
                judgeTime(purPurchasePrice,item);
            });
        }
        return 1;
    }
    /**
     * 启用/停用 采购价信息主信息
     *
     * @param purPurchasePriceActionRequset 采购价信息主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult status(PurPurchasePriceActionRequest purPurchasePriceActionRequset) {
        //确认状态 5
        String confirmHandleStatus = ConstantsEms.SAVA_STATUS;
        Long[] ids = purPurchasePriceActionRequset.getPurchasePriceSids();
        UpdateWrapper<PurPurchasePrice> queryWrapper = new UpdateWrapper<>();
        queryWrapper.eq("handle_status", confirmHandleStatus)
                .in("purchase_price_sid", ids);
        Integer selectCount = purPurchasePriceMapper.selectCount(queryWrapper);
        //当前要改变的状态
        String changStatus = purPurchasePriceActionRequset.getStatus();
        //判断是否符合条件
        if (selectCount == ids.length) {
            UpdateWrapper<PurPurchasePrice> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("status", changStatus)
                    .in("purchase_price_sid", ids);
            PurPurchasePrice purPurchasePrice = new PurPurchasePrice();
            purPurchasePriceMapper.update(purPurchasePrice, updateWrapper);
            for (Long id : ids) {
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String businessType=purPurchasePriceMapper.selectById(id).getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(id,businessType, msgList,TITLE,null);
            }
            return AjaxResult.success(purPurchasePriceMapper.update(purPurchasePrice, updateWrapper));
        } else {
            return AjaxResult.error("仅确认状态下，才可启用/停用");
        }

    }

    /**
     * 采购价报表
     *
     * @param purPurchasePriceActionRequset 采购价报表
     * @return 结果
     */
    @Override
    public List<PurPurchasePriceReportResponse> report(PurPurchasePriceReportResponse response) {
        List<PurPurchasePriceReportResponse> purPurchasePriceReportResponses = purPurchasePriceItemMapper.purPurchasePriceReport(response);
        purPurchasePriceReportResponses.forEach(li->{
            if(li.getPurchasePriceTax()!=null){
                li.setPurchasePriceTaxS(removeZero(li.getPurchasePriceTax().toString()));
            }
            if(li.getPurchasePrice()!=null){
                li.setPurchasePriceS(removeZero(li.getPurchasePrice().toString()));
            }
            if(li.getDecreQuantity()!=null){
                li.setDecreQuantityS(removeZero(li.getDecreQuantity().toString()));
            }
            if(li.getIncreQuantity()!=null){
                li.setIncreQuantityS(removeZero(li.getIncreQuantity().toString()));
            }
            if(li.getDecPurPriceTax()!=null){
                li.setDecPurPriceTaxS(removeZero(li.getDecPurPriceTax().toString()));
            }
            if(li.getDecPurPrice()!=null){
                li.setDecPurPriceS(removeZero(li.getDecPurPrice().toString()));
            }
            if(li.getIncrePurPriceTax()!=null){
                li.setIncrePurPriceTaxS(removeZero(li.getIncrePurPriceTax().toString()));
            }
            if(li.getIncrePurPrice()!=null){
                li.setIncrePurPriceS(removeZero(li.getIncrePurPrice().toString()));
            }
            if(li.getPriceMinQuantity()!=null){
                li.setPriceMinQuantityS(removeZero(li.getPriceMinQuantity().toString()));
            }
            if(li.getReferQuantity()!=null){
                li.setReferQuantityS(removeZero(li.getReferQuantity().toString()));
            }
            if(li.getUnitConversionRate()!=null){
                li.setUnitConversionRateS(removeZero(li.getUnitConversionRate().toString()));
            }
            if(li.getPurchaseType()!=null&&isApproval(li.getPurchaseType())){
                li.setIsApproval(ConstantsEms.NO);
            }else{
                li.setIsApproval(ConstantsEms.YES);
            }
        });
        String isFinallyNode = response.getIsFinallyNode();
        if(ConstantsEms.YES.equals(isFinallyNode)){
            purPurchasePriceReportResponses.stream().forEach(li->{
                FlowTaskVo flowTaskVo = new FlowTaskVo();
                flowTaskVo.setFormId(li.getPurchasePriceItemSid());
                AjaxResult nextFlowNode = remoteFlowableService.getNextFlowNode(flowTaskVo);
                Object code = nextFlowNode.get("code");
                if(ConstantsEms.CODE_SUCESS.equals(nextFlowNode.get("code").toString())){
                    li.setIsFinallyNode(ConstantsEms.YES);
                }
            });
            List<PurPurchasePriceReportResponse> list = purPurchasePriceReportResponses.stream().filter(li -> isFinallyNode.equals(li.getIsFinallyNode())).collect(Collectors.toList());
            return list;
        }
        return purPurchasePriceReportResponses;
    }
    //审批后价格回写
    @Override
    public   void orderUpdate(PurPurchasePrice purPurchasePrice,PurPurchasePriceItem purchasePriceItem) {
        Date startDate = purchasePriceItem.getStartDate();
        Date endDate = purchasePriceItem.getEndDate();
        if(startDate.getTime()<=new Date().getTime()
                &&new Date().getTime()<=endDate.getTime()
        ){
            PurPurchaseOrder purPurchaseOrder = new PurPurchaseOrder();
            BeanCopyUtils.copyProperties(purPurchasePrice,purPurchaseOrder);
            BeanCopyUtils.copyProperties(purchasePriceItem,purPurchaseOrder);
            Long materialSid = purPurchasePrice.getMaterialSid();
            String zipperFlag = basMaterialMapper.selectById(materialSid).getZipperFlag();
            //整条拉链地增减价计算
            if(ConstantsEms.YES.equals(purchasePriceItem.getIsRecursionPrice())&&!ConstantsEms.ZIPPER_ZH.equals(zipperFlag)){
                List<PurPurchaseOrderItem> items = purPurchaseOrderMapper.getUpdatePrice(purPurchaseOrder);
                if(CollectionUtil.isNotEmpty(items)){
                    items.stream().forEach(li->{
                        purPurchasePrice.setSku2Sid(li.getSku2Sid());
                        PurPurchasePriceItem newPrice = new PurPurchasePriceItem();
                        BeanCopyUtils.copyProperties(purchasePriceItem,newPrice);
                        PurPurchasePriceItem priceItem = zipperPriceZT(purPurchasePrice,newPrice);
                        PurPurchaseOrder order = new PurPurchaseOrder();
                        BeanCopyUtils.copyProperties(purPurchaseOrder,order);
                        order.setPurchaseOrderItemSid(li.getPurchaseOrderItemSid())
                                .setPurchasePriceTax(priceItem.getPurchasePriceTax())
                                .setPurchasePrice(priceItem.getPurchasePrice());
                        purPurchaseOrderMapper.updatePrice(order);
                    });
                }
            }else if(!ConstantsEms.ZIPPER_ZH.equals(zipperFlag)){
                purPurchaseOrderMapper.updatePrice(purPurchaseOrder);
            }
        }
    }

    /**
     * 获取采购价
     */
    @Override
    public PurPurchasePriceItem getPurchasePrice(PurPurchasePrice purPurchasePrice) {
        String[] handleStatus=purPurchasePrice.getNotApprovalStatus()!=null?purPurchasePrice.getNotApprovalStatus():new String[]{ConstantsEms.CHECK_STATUS};
        PurPurchasePrice result = null;
        //按：供应商、公司、采购模式、甲供料方式、商品/物料sid、SKU1sid查
        result = purPurchasePriceMapper.selectOne(new QueryWrapper<PurPurchasePrice>()
                .lambda()
                .eq(PurPurchasePrice::getVendorSid, purPurchasePrice.getVendorSid())
                .eq(PurPurchasePrice::getPurchaseMode, purPurchasePrice.getPurchaseMode())
                .eq(PurPurchasePrice::getRawMaterialMode, purPurchasePrice.getRawMaterialMode())
                .eq(PurPurchasePrice::getMaterialSid, purPurchasePrice.getMaterialSid())
                .eq(PurPurchasePrice::getSku1Sid, purPurchasePrice.getSku1Sid()));
        if (result != null) {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String now = sdf.format(date);
            //直接截取到日
            Date nowDate=DateUtils.parseDate(now);
            PurPurchasePriceItem priceItem = purPurchasePriceItemMapper.selectOne(new QueryWrapper<PurPurchasePriceItem>()
                    .lambda()
                    .le(PurPurchasePriceItem::getStartDate, nowDate)
                    .ge(PurPurchasePriceItem::getEndDate, nowDate)
                    .in(PurPurchasePriceItem::getHandleStatus,handleStatus)
                    .eq(PurPurchasePriceItem::getPurchasePriceSid, result.getPurchasePriceSid()));
            if(priceItem==null){
                result = null;
            }else{
                //foreach时   重新写入时mybatis缓存失效
                purPurchasePriceItemMapper.updateRe(priceItem);
                return priceItem;
            }
        }
        if (result == null) {
            //按：供应商、公司、采购模式、甲供料方式、商品/物料sid查
            result = purPurchasePriceMapper.selectOne(new QueryWrapper<PurPurchasePrice>()
                    .lambda()
                    .eq(PurPurchasePrice::getVendorSid, purPurchasePrice.getVendorSid())
                    .eq(PurPurchasePrice::getPurchaseMode, purPurchasePrice.getPurchaseMode())
                    .eq(PurPurchasePrice::getRawMaterialMode, purPurchasePrice.getRawMaterialMode())
                    .eq(PurPurchasePrice::getMaterialSid, purPurchasePrice.getMaterialSid())
                     .isNull(PurPurchasePrice::getSku1Sid));
            if (result != null) {
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String now = sdf.format(date);
                //直接截取到日
                Date nowDate=DateUtils.parseDate(now);
                PurPurchasePriceItem priceItem = purPurchasePriceItemMapper.selectOne(new QueryWrapper<PurPurchasePriceItem>()
                        .lambda()
                        .le(PurPurchasePriceItem::getStartDate, nowDate)
                        .in(PurPurchasePriceItem::getHandleStatus,handleStatus)
                        .ge(PurPurchasePriceItem::getEndDate, nowDate)
                        .eq(PurPurchasePriceItem::getPurchasePriceSid, result.getPurchasePriceSid()));
                if(priceItem!=null){
                    purPurchasePriceItemMapper.updateRe(priceItem);
                }
                return priceItem;
            }
        }
        return null;
    }

    //第二层校验
    @Override
    public void judgeTime(PurPurchasePrice purPurchasePrice,PurPurchasePriceItem purPurchasePriceItem){
        String handleStatus = purPurchasePrice.getHandleStatus();
            //获取 按款-按色
            List<PurPurchasePriceItem> purchasePriceAll = getPurchasePriceAll(purPurchasePrice);
            if(CollectionUtils.isNotEmpty(purchasePriceAll)&&purPurchasePriceItem.getPurchasePriceSid()!=null){
                    purchasePriceAll=purchasePriceAll.stream().filter(li->!li.getPurchasePriceSid().toString().equals(purPurchasePriceItem.getPurchasePriceSid().toString())).collect(Collectors.toList());
            }
            if(CollectionUtils.isNotEmpty(purchasePriceAll)){
                List<PurPurchasePriceItem> purPurchasePriceItems = new ArrayList<>();
                purPurchasePriceItems.addAll(purchasePriceAll);
                //校验有效期是否存在交集
                boolean judge = validTimeOther(purPurchasePriceItem,purPurchasePriceItems);
                if(!judge){
                    throw new CustomException("物料编码"+purPurchasePrice.getMaterialCode()+"，当前已生效采购价的有效期与此采购价的有效期区间存在交集，请检查！");
                }
            }
    }

    public Boolean validTimeOther(PurPurchasePriceItem purPurchasePriceItem,List<PurPurchasePriceItem> purPurchasePriceItems){
        for (int i=0;i<purPurchasePriceItems.size();i++){
            long start = purPurchasePriceItem.getStartDate().getTime();
            long end = purPurchasePriceItem.getEndDate().getTime();
            if(start>=purPurchasePriceItems.get(i).getStartDate().getTime()&&start<=purPurchasePriceItems.get(i).getEndDate().getTime()){
                return false;
            }
            if(end>=purPurchasePriceItems.get(i).getStartDate().getTime()&&end<=purPurchasePriceItems.get(i).getEndDate().getTime()){
                return false;
            }
            if(start<purPurchasePriceItems.get(i).getStartDate().getTime()&&end>purPurchasePriceItems.get(i).getEndDate().getTime()){
                return false;
            }
        }
        return true;
    }

    /**
     * 获取采购价明细 （有效期验证）
     */
    public List<PurPurchasePriceItem> getPurchasePriceAll(PurPurchasePrice purPurchasePrice) {
        List<PurPurchasePrice>  result = null;
        String priceDimension = purPurchasePrice.getPriceDimension();
        //按款
        if(ConstantsEms.PRICE_K.equals(priceDimension)){
            result = purPurchasePriceMapper.selectList(new QueryWrapper<PurPurchasePrice>()
                    .lambda()
                    .eq(PurPurchasePrice::getVendorSid, purPurchasePrice.getVendorSid())
                    .eq(PurPurchasePrice::getPurchaseMode, purPurchasePrice.getPurchaseMode())
                    .eq(PurPurchasePrice::getRawMaterialMode, purPurchasePrice.getRawMaterialMode())
                    .eq(PurPurchasePrice::getMaterialSid, purPurchasePrice.getMaterialSid()));
        }else{
            //按色    -只获取价格维度为按款
            result = purPurchasePriceMapper.selectList(new QueryWrapper<PurPurchasePrice>()
                    .lambda()
                    .eq(PurPurchasePrice::getVendorSid, purPurchasePrice.getVendorSid())
                    .eq(PurPurchasePrice::getPurchaseMode, purPurchasePrice.getPurchaseMode())
                    .eq(PurPurchasePrice::getPriceDimension,ConstantsEms.PRICE_K)
                    .eq(PurPurchasePrice::getRawMaterialMode, purPurchasePrice.getRawMaterialMode())
                    .eq(PurPurchasePrice::getMaterialSid, purPurchasePrice.getMaterialSid()));
        }
        if (CollectionUtils.isNotEmpty(result)) {
            List<Long> sids = result.stream().map(item -> item.getPurchasePriceSid()).collect(Collectors.toList());
            List<PurPurchasePriceItem> priceItem = purPurchasePriceItemMapper.selectList(new QueryWrapper<PurPurchasePriceItem>()
                    .lambda()
                    .in(PurPurchasePriceItem::getPurchasePriceSid, sids)
                    .notIn(PurPurchasePriceItem::getHandleStatus, new String[]{HandleStatus.SAVE.getCode(), HandleStatus.RETURNED.getCode()}));
           return priceItem;
        }
        return null;
    }
    //获取组合拉链的采购价
    @Override
    public PurPurchasePriceItem getZipperPurchase(PurPurchasePrice purPurchasePrice){
        Long materialSid = purPurchasePrice.getMaterialSid();
        TecBomHead zipper = iTecBomHeadService.getZipper(materialSid);
        BigDecimal purchaseZippe=null;
        List<TecBomItem> itemList = zipper.getItemList();
        List<BigDecimal> prices = new ArrayList<>();
        PurPurchasePriceItem item=new PurPurchasePriceItem();
        List<BigDecimal> taxs = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(itemList)){
            itemList.forEach(li->{
                String zipperFlag = li.getZipperFlag();
                purPurchasePrice.setMaterialSid(li.getBomMaterialSid());
                //获取组件清单中的采购价
                PurPurchasePriceItem purchasePrice = getPurchasePrice(purPurchasePrice);
                BigDecimal price;
                if(purchasePrice!=null){
                    taxs.add(purchasePrice.getTaxRate());
                    String isRecursionPrice = purchasePrice.getIsRecursionPrice();
                    //链胚
                    if(ConstantsEms.ZIPPER_LP.equals(zipperFlag)&&ConstantsEms.YES.equals(isRecursionPrice)){
                        BeanCopyUtils.copyProperties(purchasePrice,item);
                        //最小起算量
                        BigDecimal priceMinQuantity = purchasePrice.getPriceMinQuantity();
                        //取整方式
                        String roundingType = purchasePrice.getRoundingType();
                        //获取链胚长度->等于整合拉链sku2的长度
                        Long sku2Sid = purPurchasePrice.getSku2Sid();
                        BasSku basSku = basSkuMapper.selectById(sku2Sid);
                        BigDecimal lenth =basSku.getSkuNumeralValue();
                        if(priceMinQuantity!=null){
                            if(lenth.subtract(priceMinQuantity).compareTo(BigDecimal.ZERO)==-1){
                                lenth=priceMinQuantity;
                            }
                        }
                        //差异量
                        BigDecimal diver = lenth.subtract(purchasePrice.getReferQuantity());
                        if(diver.compareTo(new BigDecimal(0))==1){
                            //递增
                            BigDecimal value = getVale(diver.abs(), purchasePrice.getIncreQuantity(), roundingType);
                            price=purchasePrice.getPurchasePriceTax().add(value.multiply(purchasePrice.getIncrePurPriceTax()));
                            price=price.multiply(li.getInnerQuantity());
                        }else if(diver.compareTo(new BigDecimal(0))==-1){
                            //递减少
                            BigDecimal value = getVale(diver.abs(),purchasePrice.getDecreQuantity(), roundingType);
                            price=purchasePrice.getPurchasePriceTax().subtract(value.multiply(purchasePrice.getDecPurPriceTax()));
                            price=price.multiply(li.getInnerQuantity());
                        }else{
                            //相等
                            price=purchasePrice.getPurchasePriceTax();
                            price=price.multiply(li.getInnerQuantity());
                        }
                    }else{
                        price=purchasePrice.getPurchasePriceTax().multiply(li.getInnerQuantity());
                    }
                    prices.add(price);
                }

            });
            if(prices.size()>0){
                if(prices.size()==itemList.size()){
                    //求和
                    BigDecimal totalPrice = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                    purchaseZippe=totalPrice.divide(BigDecimal.ONE,4,BigDecimal.ROUND_UP);
                }else{
                    return null;
                }
            }
            if(CollectionUtil.isNotEmpty(taxs)){
                item.setTaxRate(taxs.get(0));
            }
            item.setPurchasePriceTax(purchaseZippe);
            return item;
        }
        return item;
    }

    //获取整条拉链采购价
    @Override
    public PurPurchasePriceItem zipperPriceZT(PurPurchasePrice purPurchasePrice,PurPurchasePriceItem item){
        //获取整条拉链采购价
        PurPurchasePriceItem purchasePrice = item!=null?item:getPurchasePrice(purPurchasePrice);
        BigDecimal price=null;
        if(purchasePrice!=null){
            String isRecursionPrice = purchasePrice.getIsRecursionPrice();
            if(ConstantsEms.YES.equals(isRecursionPrice)){
                //最小起算量
                BigDecimal priceMinQuantity = purchasePrice.getPriceMinQuantity();
                //取整方式
                String roundingType = purchasePrice.getRoundingType();
                //获取链胚长度->等于整合拉链sku2的长度
                Long sku2Sid = purPurchasePrice.getSku2Sid();
                BasSku basSku = basSkuMapper.selectById(sku2Sid);
                BigDecimal lenth =(basSku.getSkuNumeralValue());
                if(priceMinQuantity!=null){
                    if(lenth.subtract(priceMinQuantity).compareTo(BigDecimal.ZERO)==-1){
                        lenth=priceMinQuantity;
                    }
                }
                //差异量
                BigDecimal diver = lenth.subtract(purchasePrice.getReferQuantity());
                if(diver.compareTo(new BigDecimal(0))==1){
                    //递增
                    BigDecimal value = getVale(diver.abs(), purchasePrice.getIncreQuantity(), roundingType);
                    price=purchasePrice.getPurchasePriceTax().add(value.multiply(purchasePrice.getIncrePurPriceTax()));
                }else if(diver.compareTo(new BigDecimal(0))==-1){
                    //递减少
                    BigDecimal value = getVale(diver.abs(),purchasePrice.getDecreQuantity(), roundingType);
                    price=purchasePrice.getPurchasePriceTax().subtract(value.multiply(purchasePrice.getDecPurPriceTax()));
                }else{
                    //相等
                    price=purchasePrice.getPurchasePriceTax();
                }
            }else{
                price=purchasePrice.getPurchasePriceTax();
            }
            purchasePrice.setPurchasePriceTax(price)
                    .setPurchasePrice(price.divide(BigDecimal.ONE.add(purchasePrice.getTaxRate()),6,BigDecimal.ROUND_HALF_DOWN));
        }
        return purchasePrice;
    }

    public BigDecimal getVale(BigDecimal diver,BigDecimal quaily,String roundingType){
        BigDecimal treal=null;
        if(ConstantsEms.QZFS_UP.equals(roundingType)){
            treal=diver.divide(quaily,0,BigDecimal.ROUND_UP);
        }else if(ConstantsEms.QZFS_DOWN.equals(roundingType)){
            treal=diver.divide(quaily,0,BigDecimal.ROUND_DOWN);
        }else{
            treal=diver.divide(quaily,0,BigDecimal.ROUND_HALF_UP);
        }
        return treal;
    }
    /**
     * 审批流修改状态
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int flowHandle(PurPurchasePriceItem item,String comment){
        PurPurchasePriceItem purPurchasePriceItem = purPurchasePriceItemMapper.selectById(item.getPurchasePriceItemSid());
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentItemSid, item.getPurchasePriceItemSid()));
        if(ConstantsEms.CHECK_STATUS.equals(item.getHandleStatus())){
            if(ConstantsEms.NO.equals(item.getIsApproval())){
                //插入日志
                MongodbUtil.insertUserLogItem(purPurchasePriceItem.getPurchasePriceSid(), BusinessType.CHECK.getValue(),TITLE,purPurchasePriceItem.getItemNum());
            }
        }else if(HandleStatus.SUBMIT.getCode().equals(item.getHandleStatus())){
            //插入日志
            MongodbUtil.insertUserLogItem(purPurchasePriceItem.getPurchasePriceSid(), BusinessType.SUBMIT.getValue(),TITLE,purPurchasePriceItem.getItemNum());
        }else if(HandleStatus.CHANGEAPPROVAL.getCode().equals(item.getHandleStatus())){
            //插入日志
            MongodbUtil.insertUserLogItem(purPurchasePriceItem.getPurchasePriceSid(), BusinessType.CHANGE.getValue(),TITLE,purPurchasePriceItem.getItemNum());
            //插入日志
            MongodbUtil.insertUserLogItem(purPurchasePriceItem.getPurchasePriceSid(), BusinessType.SUBMIT.getValue(),TITLE,purPurchasePriceItem.getItemNum());
        }else{
            //插入日志
            MongodbUtil.insertApprovalLogAddNum(purPurchasePriceItem.getPurchasePriceSid(), BusinessType.APPROVAL.getValue(),comment,purPurchasePriceItem.getItemNum());
        }
        if (HandleStatus.BG_RETURN.getCode().equals(item.getHandleStatus())) {//单笔变更驳回
            item.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        }
        if(HandleStatus.RETURNED.getCode().equals(item.getHandleStatus())){
            if(HandleStatus.CHANGEAPPROVAL.getCode().equals(purPurchasePriceItem.getHandleStatus())){//多笔变更驳回
                item.setHandleStatus(HandleStatus.CONFIRMED.getCode());
            }
        }
        int row = purPurchasePriceItemMapper.update(new PurPurchasePriceItem(), new UpdateWrapper<PurPurchasePriceItem>().lambda()
                .set(PurPurchasePriceItem::getHandleStatus,item.getHandleStatus())
                .set(PurPurchasePriceItem::getItemNum,purPurchasePriceItem.getItemNum())
                .eq(PurPurchasePriceItem::getPurchasePriceItemSid,item.getPurchasePriceItemSid())
        );
        List<PurPurchasePriceItem> listPurPurchasePriceItem = purPurchasePriceItemMapper.selectList(new QueryWrapper<PurPurchasePriceItem>().lambda()
                .eq(PurPurchasePriceItem::getPurchasePriceSid, purPurchasePriceItem.getPurchasePriceSid())
        );
        PurPurchasePrice purPurchasePrice = purPurchasePriceMapper.selectById(purPurchasePriceItem.getPurchasePriceSid());
        if(ConstantsEms.CHECK_STATUS.equals(item.getHandleStatus())||ConstantsEms.NO.equals(item.getIsApproval())){
            new Thread(()->{
                //价格回写
                orderUpdate(purPurchasePrice, purPurchasePriceItem);
            }).start();
        }
        List<PurPurchasePriceItem> checkList = listPurPurchasePriceItem.stream().filter(li -> ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(checkList)){
            if(!ConstantsEms.CHECK_STATUS.equals(purPurchasePrice.getHandleStatus())){
                purPurchasePriceMapper.update(new PurPurchasePrice(),new UpdateWrapper<PurPurchasePrice>().lambda()
                        .eq(PurPurchasePrice::getPurchasePriceSid,purPurchasePrice.getPurchasePriceSid())
                        .set(PurPurchasePrice::getHandleStatus,ConstantsEms.CHECK_STATUS)
                );
            }
        }else{
            if(!ConstantsEms.CHECK_STATUS.equals(purPurchasePrice.getHandleStatus())){
                PurPurchasePriceItem purPurchaseItem = listPurPurchasePriceItem.get(0);
                purPurchasePriceMapper.update(new PurPurchasePrice(),new UpdateWrapper<PurPurchasePrice>().lambda()
                        .eq(PurPurchasePrice::getPurchasePriceSid,purPurchasePrice.getPurchasePriceSid())
                        .set(PurPurchasePrice::getHandleStatus,purPurchaseItem.getHandleStatus())
                );
            }
        }
        return row;
    }

    @Override
    public void setApprovalLog(PurPurchasePriceItem item, String comment){
        PurPurchasePriceItem purPurchasePriceItem = purPurchasePriceItemMapper.selectById(item.getPurchasePriceItemSid());
        MongodbUtil.insertApprovalLogAddNum(purPurchasePriceItem.getPurchasePriceSid(), BusinessType.APPROVAL.getValue(),comment,purPurchasePriceItem.getItemNum());
    }

    /**
     * 采购价 导入
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult importDataPur(MultipartFile file){
        try{
            File toFile=null;
            try {
                toFile   = FileUtils.multipartFileToFile(file);
            }catch (Exception e){
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            //甲供料 方式
            List<DictData> rawMaterialMode=sysDictDataService.selectDictData("s_raw_material_mode");
            Map<String,String> rawMaterialModeMaps=rawMaterialMode.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            //采购模式
            List<DictData> priceType=sysDictDataService.selectDictData("s_price_type");
            Map<String,String> priceTypeMaps=priceType.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            //价格维度
            List<DictData> priceDimension=sysDictDataService.selectDictData("s_price_dimension");
            Map<String,String> priceDimensionMaps=priceDimension.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            //递增减sku类型
            List<DictData> skuType=sysDictDataService.selectDictData("s_sku_type");
            Map<String,String> skuTypeMaps=skuType.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            //税率
            Map<String,BigDecimal> taxRateMaps = conTaxRateMapper.getConTaxRateList().stream().collect(Collectors.toMap(ConTaxRate::getTaxRateName, ConTaxRate::getTaxRateValue, (key1, key2) -> key2));
            //基本计量单位
            List<ConMeasureUnit> conMeasureUnitList = conMeasureUnitMapper.getConMeasureUnitList();
            Map<String,String> measureUnitMaps=conMeasureUnitList.stream().collect(Collectors.toMap(ConMeasureUnit::getName, ConMeasureUnit::getCode,(key1, key2)->key2));
            //取整方式
            List<DictData> roundingType=sysDictDataService.selectDictData("s_rounding_type");
            Map<String,String> roundingTypeMaps=roundingType.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            //是否
            List<DictData> yes=sysDictDataService.selectDictData("sys_yes_no");
            Map<String,String> yesMaps=yes.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            List<PurPurchasePrice> PurPurchasePriceList = new ArrayList<>();
            String errMsg="";
            List<CommonErrMsgResponse> msgList=new ArrayList<>();
            List<CommonErrMsgResponse> warnList = new ArrayList<>();
            for (int i = 0; i < readAll.size(); i++) {
                Long companySid=null;
                String vendorSid=null;
                Long materialSid=null;
                String unitPrice=null;
                Long sku1Sid=null;
                String materialCode=null;
                String materialCategory = null;
                String rawMaterialModeValue=null;
                String priceTypeValue=null;
                String priceDimensionValue=null;
                String is=null;
                BasMaterial basMaterial=null;
                BigDecimal price=BigDecimal.ZERO;
                BigDecimal priceTax=BigDecimal.ZERO;
                boolean start=true;
                boolean end=true;
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                int num=i+1;
                if (objects.get(0) == null || objects.get(0) == "") {
                    //throw new BaseException("第"+num+"行,供应商简称，不能为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("供应商简称，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }
                if(objects.get(0) != null && objects.get(0) != ""){
                    String bendorCode = objects.get(0).toString();
                    BasVendor basVendor = basVendorMapper.selectOne(new QueryWrapper<BasVendor>()
                            .lambda().eq(BasVendor::getShortName, bendorCode));
                    if (basVendor == null) {
                        //throw new BaseException("第"+num+"行,供应商简称为" + bendorCode + "，没有对应的供应商，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("供应商简称为" + bendorCode + "，没有对应的供应商，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        if(!basVendor.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)||!basVendor.getStatus().equals(ConstantsEms.ENABLE_STATUS)){
                            //throw new BaseException("第"+num+"行,对应的供应商必须是确认且已启用的状态，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("对应的供应商必须是确认且已启用的状态，导入失败");
                            msgList.add(errMsgResponse);
                        }
                        vendorSid = basVendor.getVendorSid().toString();
                    }
                }
                if (objects.get(2) == null || objects.get(2) == "") {
                    //throw new BaseException("第"+num+"行,甲供料方式，不能为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("甲供料方式，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }
                if(objects.get(2) != null && objects.get(2) != ""){
                     rawMaterialModeValue = rawMaterialModeMaps.get(objects.get(2).toString());
                    if(StrUtil.isEmpty(rawMaterialModeValue)){
                        //throw new BaseException("第"+num+"行,甲供料方式配置错误，导入失败,请联系管理员");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("甲供料方式配置错误，导入失败,请联系管理员");
                        msgList.add(errMsgResponse);
                    }
                    String vale=rawMaterialModeValue;
                    if(rawMaterialModeValue!=null){
                        if(CollectionUtil.isNotEmpty(rawMaterialMode)){
                            List<DictData> list = rawMaterialMode.stream()
                                    .filter(m -> ConstantsEms.CHECK_STATUS.equals(m.getHandleStatus()) && "0".equals(m.getStatus()) && vale.equals(m.getDictValue()))
                                    .collect(Collectors.toList());
                            if(CollectionUtil.isEmpty(list)){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("甲供料方式必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                }
                if (objects.get(3) == null || objects.get(3) == "") {
                   // throw new BaseException("第"+num+"行,采购模式，不能为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("采购模式，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }
                if(objects.get(3) != null && objects.get(3) != ""){
                     priceTypeValue = priceTypeMaps.get(objects.get(3).toString());
                    if(StrUtil.isEmpty(priceTypeValue)){
                        //throw new BaseException("第"+num+"行,采购模式配置错误，导入失败,请联系管理员");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("采购模式配置错误，导入失败,请联系管理员");
                        msgList.add(errMsgResponse);
                    }
                    if(priceTypeValue!=null){
                        String value=priceTypeValue;
                        if(CollectionUtil.isNotEmpty(priceType)){
                            List<DictData> list = priceType.stream()
                                    .filter(m -> ConstantsEms.CHECK_STATUS.equals(m.getHandleStatus()) && "0".equals(m.getStatus()) && value.equals(m.getDictValue()))
                                    .collect(Collectors.toList());
                            if(CollectionUtil.isEmpty(list)){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("采购模式必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                }
                if (objects.get(4) == null || objects.get(4) == "") {
                    //throw new BaseException("第"+num+"行,价格维度，不能为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("价格维度，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }
                if(objects.get(4) != null && objects.get(4) != ""){
                     priceDimensionValue = priceDimensionMaps.get(objects.get(4).toString());
                    if(StrUtil.isEmpty(priceDimensionValue)){
                        //throw new BaseException("第"+num+"行,价格维度配置错误，导入失败,请联系管理员");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("价格维度配置错误，导入失败,请联系管理员");
                        msgList.add(errMsgResponse);
                    }
                    if(priceDimensionValue!=null){
                        String value=priceDimensionValue;
                        if(CollectionUtil.isNotEmpty(priceDimension)){
                            List<DictData> list = priceDimension.stream()
                                    .filter(m -> ConstantsEms.CHECK_STATUS.equals(m.getHandleStatus()) && "0".equals(m.getStatus()) && value.equals(m.getDictValue()))
                                    .collect(Collectors.toList());
                            if(CollectionUtil.isEmpty(list)){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("价格维度必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                }
                if (objects.get(5) == null || objects.get(5) == "") {
                    //throw new BaseException("第"+num+"行,商品/物料编码，不能为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("商品/物料编码，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }
                if(objects.get(5) != null && objects.get(5) != ""){
                    basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                            .eq(BasMaterial::getMaterialCode, objects.get(5).toString())
                    );
                    if(basMaterial==null){
                       // throw new BaseException("第"+num+"行,商品/物料编码为"+objects.get(5).toString()+"，没有对应的商品/物料，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("商品/物料编码为"+objects.get(5).toString()+"，没有对应的商品/物料，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(!basMaterial.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)||!basMaterial.getStatus().equals(ConstantsEms.ENABLE_STATUS)){
                            //throw new BaseException("第"+num+"行,对应的商品/物料必须是确认且已启用的状态，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("对应的商品/物料必须是确认且已启用的状态，导入失败");
                            msgList.add(errMsgResponse);
                        }
                        materialSid=basMaterial.getMaterialSid();
                        materialCode=basMaterial.getMaterialCode();
                        materialCategory = basMaterial.getMaterialCategory();
                        if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(materialCategory) && !ConstantsEms.RAW_MATERIAL_MODE_WU.equals(rawMaterialModeValue)) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg(objects.get(5).toString() + "为物料，甲供料方式需选择“无/供方全包料”，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }
                if (objects.get(6) != null && objects.get(6) != "") {
                    if(ConstantsEms.PRICE_K.equals(priceDimensionValue)){
                       // throw new BaseException("第"+num+"行,价格维度按款时，不允许填写颜色名称，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("价格维度按款时，不允许填写颜色名称，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                            .eq(BasSku::getSkuName, objects.get(6).toString()));
                    if(basSku==null){
                        //throw new BaseException("第"+num+"行,颜色名称为"+objects.get(6).toString()+"，没有对应的颜色，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("颜色名称为"+objects.get(6).toString()+"，没有对应的颜色，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        sku1Sid=basSku.getSkuSid();
                        List<BasMaterialSku> basMaterialSkus = basMaterialSkuMapper.selectList(new QueryWrapper<BasMaterialSku>().lambda()
                                .eq(BasMaterialSku::getMaterialSid, materialSid)
                                .eq(BasMaterialSku::getSkuSid, sku1Sid)
                        );
                        if(CollectionUtils.isEmpty(basMaterialSkus)){
                            //throw new BaseException("第"+num+"行,该物料没有对应的颜色，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("该物料没有对应的颜色，导入失败");
                            msgList.add(errMsgResponse);
                        }else{
                            if(!ConstantsEms.SAVA_STATUS.equals(basMaterialSkus.get(0).getStatus())){
                               // throw new BaseException("第"+num+"行,该颜色名称必须启用状态，导入失败");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("该颜色名称必须启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                }
                if(objects.get(12) != null && objects.get(12) != ""){
                    String skuTyp = skuTypeMaps.get(objects.get(12).toString());
                    if(skuTyp==null){
                       // throw new BaseException("第"+num+"行,递增减SKU类型配置错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("递增减SKU类型配置错误，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    if(skuTyp!=null){
                        if(CollectionUtil.isNotEmpty(skuType)){
                            List<DictData> list = skuType.stream()
                                    .filter(m -> ConstantsEms.CHECK_STATUS.equals(m.getHandleStatus()) && "0".equals(m.getStatus()) && skuTyp.equals(m.getDictValue()))
                                    .collect(Collectors.toList());
                            if(CollectionUtil.isEmpty(list)){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("递增减SKU类型必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                }
                if (objects.get(9)!=  null && objects.get(9) != "") {
                    boolean validDouble = isValidDouble(objects.get(9).toString());
                    if(!validDouble){
                        //throw new BaseException("第"+num+"行,采购价（含税）,数据格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("采购价（含税）,数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
                if (objects.get(13)!=  null && objects.get(13) != "") {
                    boolean validDouble = JudgeFormat.isValidDoubleLgZero(objects.get(13).toString(),7,3);
                    if(!validDouble){
                       // throw new BaseException("第"+num+"行,递增量,数据格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("递增量,数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
                if (objects.get(14) !=  null && objects.get(14) != "") {
                    boolean validDouble = isValidDouble(objects.get(14).toString());
                    if(!validDouble){
                       // throw new BaseException("第"+num+"行,递增价(含税),数据格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("递增价(含税),数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
                if (objects.get(15) !=  null && objects.get(15) != "") {
                    boolean validDouble = JudgeFormat.isValidDoubleLgZero(objects.get(15).toString(),7,3);
                    if(!validDouble){
                        //throw new BaseException("第"+num+"行,递减量,数据格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("递减量,数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
                if (objects.get(16) != null && objects.get(16) != "") {
                    boolean validDouble = isValidDouble(objects.get(16).toString());
                    if(!validDouble){
                       // throw new BaseException("第"+num+"行,递减价(含税),数据格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("递减价(含税),数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
                if (objects.get(17) != null && objects.get(17) != "") {
                    boolean validDouble = JudgeFormat.isValidDoubleLgZero(objects.get(17).toString(),7,3);
                    if(!validDouble){
                       // throw new BaseException("第"+num+"行,基准量,数据格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("基准量,数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
                if (objects.get(18) !=  null && objects.get(18) != "") {
                    boolean validDouble = JudgeFormat.isValidDoubleLgZero(objects.get(18).toString(),7,3);
                    if(!validDouble){
                       // throw new BaseException("第"+num+"行,价格最小起算量,数据格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("价格最小起算量,数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
                if(ConstantsEms.PRICE_K1.equals(priceDimensionValue)){
                    if(sku1Sid==null){
                       // throw new BaseException("第"+num+"行,价格维度按色时,颜色为必填，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("价格维度按色时,颜色为必填，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
                if (objects.get(7) == null || objects.get(7) == "") {
                    //throw new BaseException("第"+num+"行,有效期起，不能为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("有效期起，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }
                if (objects.get(8) == null || objects.get(8) == "") {
                   // throw new BaseException("第"+num+"行,有效期至，不能为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("有效期至，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }
                if(objects.get(7) !=  null && objects.get(7) != ""){
                    start = JudgeFormat.isValidDate(objects.get(7).toString());
                    if(!start){
                        // throw new BaseException("第"+num+"行,有效期起，日期格式错误");
                        errMsg=errMsg+"第"+num+"行,有效期起，日期格式错误"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("有效期起，日期格式错误,导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
                if(objects.get(8) !=  null && objects.get(8) != ""){
                    end = JudgeFormat.isValidDate(objects.get(8).toString());
                    if(!end){
                        //  throw new BaseException("第"+num+"行,有效期至，日期格式错误");
                        errMsg=errMsg+"第"+num+"行,有效期至，日期格式错误"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("有效期至，日期格式错误,导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
                if(objects.get(7) !=  null && objects.get(7) != ""&&objects.get(8) !=  null && objects.get(8) != ""){
                    if(start&&end){
                        if(DateUtils.parseDate(objects.get(7)).getTime()>DateUtils.parseDate(objects.get(8)).getTime()){
                            // throw new BaseException("第"+num+"行,有效期起，不能大于有效期至，导入失败");
                            errMsg=errMsg+"第"+num+"行,有效期起，不能大于有效期至，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("有效期起，不能大于有效期至，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }
                if (objects.get(9) == null || objects.get(9) == "") {
                   // throw new BaseException("第"+num+"行,采购价（含税），不能为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("采购价（含税），不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }
                if (objects.get(10) == null || objects.get(10) == "") {
                    //throw new BaseException("第"+num+"行,税率，不能为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("税率，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }
                if(objects.get(17) != null && objects.get(17) != ""&&objects.get(18) != null && objects.get(18) != ""){
                    if(isValidDouble(objects.get(17).toString())&&isValidDouble(objects.get(18).toString())){
                        if(objects.get(18) != null && objects.get(18) != ""){
                            if(Double.valueOf(objects.get(17).toString())-Double.valueOf(objects.get(18).toString())<0){
                                //throw new BaseException("第"+num+"行,价格最小起算量大于基准量，导入失败！");
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("价格最小起算量大于基准量，导入失败！");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                }
                if(objects.get(10) !=  null && objects.get(10) != ""){
                    ConTaxRate conTaxRate = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>().lambda()
                            .eq(ConTaxRate::getTaxRateValue, objects.get(10).toString())
                    );
                    if(conTaxRate==null){
                        //throw new BaseException("第"+num+"行,税率配置错误，导入失败,请联系管理员");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("税率配置错误，导入失败,请联系管理员");
                        msgList.add(errMsgResponse);
                    }else{
                        if(!ConstantsEms.CHECK_STATUS.equals(conTaxRate.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(conTaxRate.getStatus())){
                            errMsg=errMsg+"第"+num+"行,对应的税率必须是确认且已启用的状态"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("对应的税率必须是确认且已启用的状态");
                            msgList.add(errMsgResponse);
                        }
                    }
                }
                if (objects.get(11) == null || objects.get(11) == "") {
                    //throw new BaseException("第"+num+"行,是否递增减价，不允许为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("是否递增减价，不允许为空，导入失败");
                    msgList.add(errMsgResponse);
                }
                if(objects.get(11) != null && objects.get(11) != ""){
                    is = yesMaps.get(objects.get(11).toString());
                    if(is==null){
                        //  throw new BaseException("第"+num+"行,是否递增减价配置错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("是否递增减价配置错误，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
                if(ConstantsEms.YES.equals(is)){
                    if (objects.get(12) == null || objects.get(12) == "") {
                       // throw new BaseException("第"+num+"行,递增减价，递增减SKU类型不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("递增减价，递增减SKU类型不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    if (objects.get(13) == null || objects.get(13) == "") {
                       // throw new BaseException("第"+num+"行,递增减价，递增量不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("递增减价，递增量不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    if (objects.get(14) == null || objects.get(14) == "") {
                        //throw new BaseException("第"+num+"行,递增减价，递增价(含税)不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("递增减价，递增价(含税)不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    if (objects.get(15) == null || objects.get(15) == "") {
                       // throw new BaseException("第"+num+"行,递增减价，递减量不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("递增减价，递减量不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    if (objects.get(16) == null || objects.get(16) == "") {
                       // throw new BaseException("第"+num+"行,递增减价，递减价(含税)不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("递增减价，递减价(含税)不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    if (objects.get(17) == null || objects.get(17) == "") {
                       // throw new BaseException("第"+num+"行,递增减价，基准量不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("递增减价，基准量不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    if (objects.get(19) == null || objects.get(19) == "") {
                        //throw new BaseException("第"+num+"行,递增减价，取整方式(递增减)，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("递增减价，取整方式(递增减)，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    if (objects.get(20) == null || objects.get(20) == "") {
                       // throw new BaseException("第"+num+"行,递增减价，递增减计量单位名称，不能为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("递增减价，递增减计量单位名称，不能为空，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
                else if(ConstantsEms.NO.equals(is)){
                    if (objects.get(12) != null && objects.get(12) != "") {
                        //throw new BaseException("第"+num+"行,非递增减价，递增减SKU类型，不需要填写，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("非递增减价，递增减SKU类型，不需要填写，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    if (objects.get(13)!=  null && objects.get(13) != "") {
                        //throw new BaseException("第"+num+"行,非递增减价，递增量不需要填写，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("非递增减价，递增量不需要填写，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    if (objects.get(14) !=  null && objects.get(14) != "") {
                        //throw new BaseException("第"+num+"行,非递增减价，递增价(含税)，不需要填写，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("非递增减价，递增价(含税)，不需要填写，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    if (objects.get(15) !=  null && objects.get(15) != "") {
                        //throw new BaseException("第"+num+"行,非递增减价，递减量不需要填写，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("非递增减价，递减量不需要填写，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    if (objects.get(16) != null && objects.get(16) != "") {
                       // throw new BaseException("第"+num+"行,非递增减价，递减价(含税)不需要填写，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("非递增减价，递减价(含税)不需要填写，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    if (objects.get(17) !=  null && objects.get(17) != "") {
                        //throw new BaseException("第"+num+"行,非递增减价，基准量不需要填写，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("非递增减价，基准量不需要填写，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    if (objects.get(19) !=  null && objects.get(19) != "") {
                       // throw new BaseException("第"+num+"行,非递增减价，取整方式(递增减)，不需要填写，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("非递增减价，取整方式(递增减)，不需要填写，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    if (objects.get(20) !=  null && objects.get(20) != "") {
                        //throw new BaseException("第"+num+"行,非递增减价，递增减计量单位名称不需要填写，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("非递增减价，递增减计量单位名称不需要填写，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
                if (objects.get(19) !=  null && objects.get(19) != "") {
                    String round = roundingTypeMaps.get(objects.get(19).toString());
                    if(round==null){
                       // throw new BaseException("第"+num+"行,取整方式(递增减)配置错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("取整方式(递增减)配置错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(CollectionUtil.isNotEmpty(roundingType)){
                            List<DictData> list = roundingType.stream()
                                    .filter(m -> ConstantsEms.CHECK_STATUS.equals(m.getHandleStatus()) && "0".equals(m.getStatus()) && round.equals(m.getDictValue()))
                                    .collect(Collectors.toList());
                            if(CollectionUtil.isEmpty(list)){
                                CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                errMsgResponse.setItemNum(num);
                                errMsgResponse.setMsg("取整方式(递增减)必须是确认且已启用状态，导入失败");
                                msgList.add(errMsgResponse);
                            }
                        }
                    }
                }
                if (objects.get(20) !=  null && objects.get(20) != "") {
                    ConMeasureUnit unit = conMeasureUnitMapper.selectOne(new QueryWrapper<ConMeasureUnit>().lambda()
                            .eq(ConMeasureUnit::getName, objects.get(20).toString())
                    );
                    if(unit==null){
                       // throw new BaseException("第"+num+"行,递增减计量单位配置错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("递增减计量单位配置错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(!ConstantsEms.CHECK_STATUS.equals(unit.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(unit.getStatus())){
                            errMsg=errMsg+"第"+num+"行,对应的递增减计量单位必须是确认且已启用的状态"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("对应的递增减计量单位必须是确认且已启用的状态");
                            msgList.add(errMsgResponse);
                        }
                    }
                }
                if(objects.get(21) !=  null && objects.get(21) != ""){
                    ConMeasureUnit conMeasureUnit = conMeasureUnitMapper.selectOne(new QueryWrapper<ConMeasureUnit>().lambda()
                            .eq(ConMeasureUnit::getName, objects.get(21).toString())
                    );
                    if(conMeasureUnit==null){
                        //throw new BaseException("第"+num+"行,采购价单位配置错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("采购价单位配置错误，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(!HandleStatus.CONFIRMED.getCode().equals(conMeasureUnit.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(conMeasureUnit.getStatus())){
                           // throw new BaseException("第"+num+"行,采购价单位必须是启用且已确认状态，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("采购价单位必须是启用且已确认状态");
                            msgList.add(errMsgResponse);
                        }
                        unitPrice=conMeasureUnit.getCode();
                        // 判断 基本单位与采购价单位不一致，是否继续操作？
                        if (!unitPrice.equals(basMaterial.getUnitBase())) {
                            CommonErrMsgResponse warnMsgResponse = new CommonErrMsgResponse();
                            warnMsgResponse.setItemNum(num);
                            warnMsgResponse.setMsg("基本单位与采购价单位不一致，是否继续操作？");
                            warnList.add(warnMsgResponse);
                        }
                    }
                    if(objects.get(22) ==  null || objects.get(22) == ""){
                       // throw new BaseException("第"+num+"行,单位换算比例(采购价单位/基本计量单位)不允许为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("单位换算比例(采购价单位/基本计量单位)不允许为空，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
                if(objects.get(22) !=  null && objects.get(22) != ""){
                    if(objects.get(21) ==  null || objects.get(21) == ""){
                        //throw new BaseException("第"+num+"行,采购价单位不允许为空，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("采购价单位不允许为空，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    boolean validDouble = JudgeFormat.isValidDoubleLgZero(objects.get(22).toString(),4,4);
                    if(!validDouble){
                       // throw new BaseException("第"+num+"行,单位换算比例,数据格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("单位换算比例,数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }

                /*
                 * 报价(含税) 选填
                 */
                String quotePriceTaxS = objects.get(23) == null || objects.get(23) == "" ? null : objects.get(23).toString().trim();
                BigDecimal quotePriceTax = null;
                if (StrUtil.isNotBlank(quotePriceTaxS)) {
                    if (!JudgeFormat.isValidDouble(quotePriceTaxS,10,5)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("报价(含税)格式错误，导入失败！");
                        msgList.add(errMsgResponse);
                    } else {
                        quotePriceTax = new BigDecimal(quotePriceTaxS);
                        if (quotePriceTax != null && BigDecimal.ZERO.compareTo(quotePriceTax) > 0) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("报价(含税)格式错误，导入失败！");
                            msgList.add(errMsgResponse);
                        }
                        quotePriceTax = quotePriceTax.divide(BigDecimal.ONE,5,BigDecimal.ROUND_HALF_UP);
                    }
                }
                /*
                 * 核价(含税) 选填
                 */
                String checkPriceTaxS = objects.get(24) == null || objects.get(24) == "" ? null : objects.get(24).toString().trim();
                BigDecimal checkPriceTax = null;
                if (StrUtil.isNotBlank(checkPriceTaxS)) {
                    if (!JudgeFormat.isValidDouble(checkPriceTaxS,10,5)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("核价(含税)格式错误，导入失败！");
                        msgList.add(errMsgResponse);
                    } else {
                        checkPriceTax = new BigDecimal(checkPriceTaxS);
                        if (checkPriceTax != null && BigDecimal.ZERO.compareTo(checkPriceTax) > 0) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("核价(含税)格式错误，导入失败！");
                            msgList.add(errMsgResponse);
                        }
                        checkPriceTax = checkPriceTax.divide(BigDecimal.ONE,5,BigDecimal.ROUND_HALF_UP);
                    }
                }
                /*
                 * 价格说明 选填
                 */
                String priceRemark = objects.get(25) == null || objects.get(25) == "" ? null : objects.get(25).toString().trim();

                PurPurchasePrice purPurchasePrice = new PurPurchasePrice();
                purPurchasePrice.setVendorSid(vendorSid==null?null:Long.valueOf(vendorSid))
                        .setMaterialCode(materialCode)
                        .setMaterialCategory(materialCategory)
                        .setImportHandle(ConstantsEms.IMPORT)
                        .setCompanySid(null)
                        .setPurchaseMode(priceTypeValue)
                        .setSku1Sid(sku1Sid)
                        .setStatus(ConstantsEms.SAVA_STATUS)
                        .setMaterialSid(materialSid)
                        .setSkuTypeRecursion((objects.get(12)==""||objects.get(12)==null)?null:skuTypeMaps.get(objects.get(12).toString()))
                        .setRawMaterialMode(rawMaterialModeValue)
                        .setPriceDimension(priceDimensionValue)
                        .setRemark((objects.get(26)==""||objects.get(26)==null)?null:objects.get(26).toString())
                        .setHandleStatus(ConstantsEms.SAVA_STATUS);
                List<PurPurchasePriceItem> purPurchasePriceItems = new ArrayList<>();
                PurPurchasePriceItem purPurchasePriceItem = new PurPurchasePriceItem();
                if((objects.get(9)!=""&&objects.get(9)!=null)){
                    boolean validDouble = isValidDouble(objects.get(9).toString());
                    if(validDouble){
                        price = BigDecimal.valueOf(Double.valueOf(objects.get(9).toString()));
                        priceTax = price.divide(BigDecimal.ONE, 5, BigDecimal.ROUND_HALF_UP);
                    }
                }
                purPurchasePriceItem.setStartDate((objects.get(7)==""||objects.get(7)==null)?null:DateUtils.parseDate(objects.get(7).toString()))
                        .setEndDate((objects.get(8)==""||objects.get(8)==null)?null:DateUtils.parseDate(objects.get(8).toString()))
                        .setPurchasePriceTax(priceTax)
                        .setUnitBase(basMaterial==null?null:basMaterial.getUnitBase())
                        .setHandleStatus(ConstantsEms.SAVA_STATUS)
                        .setCurrency("CNY")
                        .setCurrencyUnit("YUAN")
                        .setUnitPrice(unitPrice)
                        .setUnitConversionRate((objects.get(22)==""||objects.get(22)==null)?null:isValidDouble(objects.get(22).toString())?BigDecimal.valueOf(Double.valueOf(objects.get(22).toString())):null)
                        .setPriceEnterMode("HS")
                        .setQuotePriceTax(quotePriceTax)
                        .setCheckPriceTax(checkPriceTax)
                        .setPriceRemark(priceRemark)
                        .setSkuTypeRecursion((objects.get(12)==""||objects.get(12)==null)?null:skuTypeMaps.get(objects.get(12).toString()))
                        .setTaxRate((objects.get(10)==""||objects.get(10)==null)?null:taxRateMaps.get(objects.get(10).toString()))
                        .setIsRecursionPrice((objects.get(11)==""||objects.get(11)==null)?null:yesMaps.get(objects.get(11).toString()))
                        .setIncreQuantity((objects.get(13)==""||objects.get(13)==null)?null:isValidDouble(objects.get(13).toString())?BigDecimal.valueOf(Double.valueOf(objects.get(13).toString())):null)
                        .setIncrePurPriceTax((objects.get(14)==""||objects.get(14)==null)?null:isValidDouble(objects.get(14).toString())?BigDecimal.valueOf(Double.valueOf(objects.get(14).toString())):null)
                        .setDecreQuantity((objects.get(15)==""||objects.get(15)==null)?null:isValidDouble(objects.get(15).toString())?BigDecimal.valueOf(Double.valueOf(objects.get(15).toString())):null)
                        .setDecPurPriceTax((objects.get(16)==""||objects.get(16)==null)?null:isValidDouble(objects.get(16).toString())?BigDecimal.valueOf(Double.valueOf(objects.get(16).toString())):null)
                        .setReferQuantity((objects.get(17)==""||objects.get(17)==null)?null:isValidDouble(objects.get(17).toString())?BigDecimal.valueOf(Double.valueOf(objects.get(17).toString())):null)
                        .setPriceMinQuantity((objects.get(18)==""||objects.get(18)==null)?null:isValidDouble(objects.get(18).toString())?BigDecimal.valueOf(Double.valueOf(objects.get(18).toString())):null)
                        .setRoundingType((objects.get(19)==""||objects.get(19)==null)?null:roundingTypeMaps.get(objects.get(19).toString()))
                        .setUnitRecursion((objects.get(20)==""||objects.get(20)==null)?null:measureUnitMaps.get(objects.get(20).toString()));
                if(purPurchasePriceItem.getUnitPrice()==null&&purPurchasePriceItem.getUnitConversionRate()==null){
                    purPurchasePriceItem.setUnitPrice(purPurchasePriceItem.getUnitBase());
                    purPurchasePriceItem.setUnitConversionRate(BigDecimal.ONE);
                }
                purPurchasePriceItems.add(purPurchasePriceItem);
                purPurchasePrice.setListPurPurchasePriceItem(purPurchasePriceItems);
                PurPurchasePriceList.add(purPurchasePrice);
            }
            if(CollectionUtil.isNotEmpty(msgList)){
                return  AjaxResult.error("报错信息",msgList);
            }
            List<String> hashCode=new ArrayList<>();
            PurPurchasePriceList.forEach(item->{
                Long vendorSid=0L;
                Long skuSid=0L;
                if(item.getVendorSid()!=null){
                    vendorSid=item.getVendorSid();
                }
                if(item.getSku1Sid()!=null){
                    skuSid=item.getSku1Sid();
                }
                String code=skuSid+""+vendorSid+""+item.getMaterialSid()+""+item.getRawMaterialMode()+""+item.getPurchaseMode();
                hashCode.add(code);
            });
            for (int i=0;i<hashCode.size();i++) {
                int  m=i;
                int sort=i+3;
                List<String> common = hashCode.stream().filter(li -> li.equals(hashCode.get(m))).collect(Collectors.toList());
                if(common.size()>1){
                    //throw new BaseException("第"+sort+"行,当前存在进行中的明细数据，导入失败");
                    errMsg=errMsg+"第"+sort+"行,表格内存在多笔有效期不同的数据，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(sort);
                    errMsgResponse.setMsg("表格内存在多笔有效期不同的数据，导入失败");
                    msgList.add(errMsgResponse);
                }
            }
            for (int i=0;i<PurPurchasePriceList.size();i++){
                int sort=i+3;
                List<PurPurchasePriceItem> priceList = new ArrayList<>();
                Long vendorSid=0L;
                if(PurPurchasePriceList.get(i).getVendorSid()!=null){
                    vendorSid=PurPurchasePriceList.get(i).getVendorSid();
                    String code=vendorSid+PurPurchasePriceList.get(i).getMaterialSid()+PurPurchasePriceList.get(i).getRawMaterialMode()+PurPurchasePriceList.get(i).getPurchaseMode();
                    for (int j=0;j<PurPurchasePriceList.size();j++){
                        if(ConstantsEms.PRICE_K.equals(PurPurchasePriceList.get(j).getPriceDimension())&&i!=j){
                            String codeMon=PurPurchasePriceList.get(j).getVendorSid()+PurPurchasePriceList.get(j).getMaterialSid()+PurPurchasePriceList.get(j).getRawMaterialMode()+PurPurchasePriceList.get(j).getPurchaseMode();
                            if(code.equals(codeMon)){
                                List<PurPurchasePriceItem> listPurPurchasePriceItem = PurPurchasePriceList.get(j).getListPurPurchasePriceItem();
                                priceList.add(listPurPurchasePriceItem.get(0));
                            }
                        }
                    }

                }
                List<PurPurchasePriceItem> listPurPurchasePriceItem = PurPurchasePriceList.get(i).getListPurPurchasePriceItem();
                if(CollectionUtil.isNotEmpty(priceList)){
                    boolean judge = validTimeOther(listPurPurchasePriceItem.get(0),priceList);
                    if(!judge){
                        // throw new BaseException("第"+sort+"行,有效期存在交集，导入失败");
                        errMsg=errMsg+"第"+sort+"行,与表格内数据，有效期存在交集，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(sort);
                        errMsgResponse.setMsg("与表格内数据，有效期存在交集，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
            }
//            if(errMsg!=""){
//                String addmsg="<br>&nbsp;&nbsp;&nbsp;&nbsp;";
//                throw new BaseException(addmsg+errMsg);
//            }
            if(CollectionUtil.isNotEmpty(msgList)){
                return AjaxResult.error("报错信息",msgList);
            }
            for (int i=0;i<PurPurchasePriceList.size();i++){
                AjaxResult result = judgeAdd(PurPurchasePriceList.get(i));
                Object msg = result.get("msg");
                int sort=i+3;
                if(ConstantsEms.SAVA_STATUS.equals(msg.toString())){
                    Long sid = Long.valueOf(result.get("data").toString());
                    PurPurchasePrice purPurchasePrice = selectPurPurchasePriceById(sid);
                    List<PurPurchasePriceItem> list = purPurchasePrice.getListPurPurchasePriceItem();
                    List<PurPurchasePriceItem> listPurPurchasePriceItem = PurPurchasePriceList.get(i).getListPurPurchasePriceItem();
                    list.add(listPurPurchasePriceItem.get(0));
                    purPurchasePrice.setImportHandle(ConstantsEms.IMPORT);
                    purPurchasePrice.setListPurPurchasePriceItem(list);
                    try {
                        List<PurPurchasePriceItem> priceItems = list.stream().filter(li -> ConstantsEms.SAVA_STATUS.equals(li.getHandleStatus())).collect(Collectors.toList());
                        if(priceItems.size()>1){
                            //throw new BaseException("第"+sort+"行,当前存在进行中的明细数据，导入失败");
                            errMsg=errMsg+"第"+sort+"行,系统内存在进行中的价格数据，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(sort);
                            errMsgResponse.setMsg("系统内存在进行中的价格数据，导入失败");
                            msgList.add(errMsgResponse);
                        }
                        if(priceItems.size()==1){
                            List<PurPurchasePriceItem> items = list.stream().filter(li -> ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())).collect(Collectors.toList());
                            if(CollectionUtil.isNotEmpty(items)){
                                if(items.size()!=list.size()-1){
                                    //  throw new BaseException("第"+sort+"行,当前存在进行中的明细数，导入失败");
                                    errMsg=errMsg+"第"+sort+"行,系统内存在进行中的价格数据，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(sort);
                                    errMsgResponse.setMsg("系统内存在进行中的价格数据，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                            }else{
                                if(list.size()!=1){
                                    // throw new BaseException("第"+sort+"行,当前存在进行中的明细数，导入失败");
                                    errMsg=errMsg+"第"+sort+"行,系统内存在进行中的价格数据，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(sort);
                                    errMsgResponse.setMsg("系统内存在进行中的价格数据，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                            }
                        }
                        judgeImport(purPurchasePrice);
                    }catch (CustomException e){
                        errMsg=errMsg+"第"+sort+"行,与系统内数据，有效期存在交集，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        //throw new BaseException("第"+sort+"行,有效期存在交集，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(sort);
                        errMsgResponse.setMsg("与系统内数据，有效期存在交集，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }else{
                    try {
                        judgeImport(PurPurchasePriceList.get(i));
                    }catch (CustomException e){
                        errMsg=errMsg+"第"+sort+"行,与系统内数据，有效期存在交集，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        //throw new BaseException("第"+sort+"行,有效期存在交集，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(sort);
                        errMsgResponse.setMsg("与系统内数据，有效期存在交集，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
            }
//            if(errMsg!=""){
//                String addmsg="<br>&nbsp;&nbsp;&nbsp;&nbsp;";
//                throw new BaseException(addmsg+errMsg);
//            }
            if(CollectionUtil.isNotEmpty(msgList)){
               return AjaxResult.success(EmsResultEntity.error(msgList, "报错信息"));
            }
            if (CollectionUtil.isNotEmpty(warnList)) {
                return AjaxResult.success(EmsResultEntity.warning(PurPurchasePriceList, warnList, null));
            }
            for (int i=0;i<PurPurchasePriceList.size();i++){
                AjaxResult result = judgeAdd(PurPurchasePriceList.get(i));
                Object msg = result.get("msg");
                int sort=i+3;
                if(ConstantsEms.SAVA_STATUS.equals(msg.toString())){
                    Long sid = Long.valueOf(result.get("data").toString());
                    PurPurchasePrice purPurchasePrice = selectPurPurchasePriceById(sid);
                    List<PurPurchasePriceItem> list = purPurchasePrice.getListPurPurchasePriceItem();
                    List<PurPurchasePriceItem> listPurPurchasePriceItem = PurPurchasePriceList.get(i).getListPurPurchasePriceItem();
                    list.add(listPurPurchasePriceItem.get(0).setImportHandle(ConstantsEms.IMPORT));
                    purPurchasePrice.setImportHandle(ConstantsEms.IMPORT);
                    purPurchasePrice.setListPurPurchasePriceItem(list);
                    AjaxResult res=null;
                    try {
                         res = updatePurPurchasePriceNew(purPurchasePrice);
                    }catch (CustomException e){
                        throw new BaseException("第"+sort+"行,有效期存在交集，导入失败");
                    }
                    String code = res.get("code").toString();
                    if(!code.equals("200")){
                        throw new BaseException("第"+sort+"行,有效期存在交集，导入失败");
                    }
                }else{
                    AjaxResult ajaxResult=null;
                    try {
                         ajaxResult = insertPurPurchasePrice(PurPurchasePriceList.get(i));

                    }catch (CustomException e){
                        throw new BaseException("第"+sort+"行,有效期存在交集，导入失败");
                    }
                    String code = ajaxResult.get("code").toString();
                    if(!code.equals("200")){
                        throw new BaseException("第"+sort+"行,有效期存在交集，导入失败");
                    }
                }
            }
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }

        return AjaxResult.success("导入成功");
    }

    /**
     * 导入提示信息后继续导入， 复制来源于导入功能结尾的执行代码
     * @param PurPurchasePriceList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult insertImport(List<PurPurchasePrice> PurPurchasePriceList) {
        for (int i=0;i<PurPurchasePriceList.size();i++){
            AjaxResult result = judgeAdd(PurPurchasePriceList.get(i));
            Object msg = result.get("msg");
            int sort=i+3;
            if(ConstantsEms.SAVA_STATUS.equals(msg.toString())){
                Long sid = Long.valueOf(result.get("data").toString());
                PurPurchasePrice purPurchasePrice = selectPurPurchasePriceById(sid);
                List<PurPurchasePriceItem> list = purPurchasePrice.getListPurPurchasePriceItem();
                List<PurPurchasePriceItem> listPurPurchasePriceItem = PurPurchasePriceList.get(i).getListPurPurchasePriceItem();
                list.add(listPurPurchasePriceItem.get(0).setImportHandle(ConstantsEms.IMPORT));
                purPurchasePrice.setImportHandle(ConstantsEms.IMPORT);
                purPurchasePrice.setListPurPurchasePriceItem(list);
                AjaxResult res=null;
                try {
                    res = updatePurPurchasePriceNew(purPurchasePrice);
                }catch (CustomException e){
                    throw new BaseException("第"+sort+"行,有效期存在交集，导入失败");
                }
                String code = res.get("code").toString();
                if(!code.equals("200")){
                    throw new BaseException("第"+sort+"行,有效期存在交集，导入失败");
                }
            }else{
                AjaxResult ajaxResult=null;
                try {
                    ajaxResult = insertPurPurchasePrice(PurPurchasePriceList.get(i));

                }catch (CustomException e){
                    throw new BaseException("第"+sort+"行,有效期存在交集，导入失败");
                }
                String code = ajaxResult.get("code").toString();
                if(!code.equals("200")){
                    throw new BaseException("第"+sort+"行,有效期存在交集，导入失败");
                }
            }
        }
        return AjaxResult.success();
    }

    //填充-主表
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
    /**
     * 获取采购价最新
     */
    @Override
    public PurPurchasePriceItem getNewPurchase(PurPurchasePrice purPurchasePrice){
        PurPurchasePriceItem item=null;
        Long materialSid = purPurchasePrice.getMaterialSid();
        String zipperFlag = basMaterialMapper.selectById(materialSid).getZipperFlag();
         if(ConstantsEms.ZIPPER_ZH.equals(zipperFlag)){
            item = getZipperPurchase(purPurchasePrice);
        } else{
            item = zipperPriceZT(purPurchasePrice,null);
        }
        //默认获取通用税率
        ConTaxRate taxRate = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>().lambda()
                .eq(ConTaxRate::getIsDefault, "Y")
        );
        if(item==null){
            item=new PurPurchasePriceItem();
        }
        item.setSystemTaxRate(taxRate.getTaxRateValue());
        if(item.getUnitPrice()!=null){
            ConMeasureUnit conMeasureUnit = conMeasureUnitMapper.selectOne(new QueryWrapper<ConMeasureUnit>().lambda()
                    .eq(ConMeasureUnit::getCode, item.getUnitPrice()));
            item.setUnitPriceName(conMeasureUnit.getName());
        }
        if(item.getUnitBase()!=null){
            ConMeasureUnit conMeasureUnit = conMeasureUnitMapper.selectOne(new QueryWrapper<ConMeasureUnit>().lambda()
                    .eq(ConMeasureUnit::getCode, item.getUnitBase()));
            item.setUnitBaseName(conMeasureUnit.getName());
        }
        return item;
    }

    /**
     *3、新增列：价格(元)
     *若订单中有价格，就显示订单价格；
     *若没有，进行如下操作：
     *1）根据订单中的“编码+供应商+甲供料方式+采购模式”在采购价档案中获取采购价的处理状态为“审批中/已确认/变更审批中”且有效期（至）>=当前日期的采购价数据
     *若1）查找到多笔数据，则选择有效期（至）距离当前日期最近的价格
     */
    @Override
    public PurPurchasePriceItem getNearPurchase(PurPurchasePrice purPurchasePrice) {
        PurPurchasePriceItem item=null;
        Long materialSid = purPurchasePrice.getMaterialSid();
        String zipperFlag = basMaterialMapper.selectById(materialSid).getZipperFlag();
        if(ConstantsEms.ZIPPER_ZH.equals(zipperFlag)){
            TecBomHead bom = iTecBomHeadService.getZipper(materialSid);
            BigDecimal purchaseZippe = null;
            List<TecBomItem> itemList = bom.getItemList();
            List<BigDecimal> prices = new ArrayList<>();
            PurPurchasePriceItem priceItem = new PurPurchasePriceItem();
            List<BigDecimal> taxs = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(itemList)){
                for (TecBomItem li : itemList) {
                    String zipper = li.getZipperFlag();
                    purPurchasePrice.setMaterialSid(li.getBomMaterialSid());
                    //获取组件清单中的采购价
                    PurPurchasePriceItem purchasePrice = getPrice(purPurchasePrice);
                    BigDecimal price;
                    if(purchasePrice!=null){
                        taxs.add(purchasePrice.getTaxRate());
                        String isRecursionPrice = purchasePrice.getIsRecursionPrice();
                        //链胚
                        if(ConstantsEms.ZIPPER_LP.equals(zipper) && ConstantsEms.YES.equals(isRecursionPrice)){
                            BeanCopyUtils.copyProperties(purchasePrice,priceItem);
                            //最小起算量
                            BigDecimal priceMinQuantity = purchasePrice.getPriceMinQuantity();
                            //取整方式
                            String roundingType = purchasePrice.getRoundingType();
                            //获取链胚长度->等于整合拉链sku2的长度
                            Long sku2Sid = purPurchasePrice.getSku2Sid();
                            BasSku basSku = basSkuMapper.selectById(sku2Sid);
                            BigDecimal lenth = basSku.getSkuNumeralValue();
                            if(priceMinQuantity != null){
                                if(lenth.subtract(priceMinQuantity).compareTo(BigDecimal.ZERO)==-1){
                                    lenth = priceMinQuantity;
                                }
                            }
                            //差异量
                            BigDecimal diver = lenth.subtract(purchasePrice.getReferQuantity());
                            if(diver.compareTo(new BigDecimal(0))==1){
                                //递增
                                BigDecimal value = getVale(diver.abs(), purchasePrice.getIncreQuantity(), roundingType);
                                price = purchasePrice.getPurchasePriceTax().add(value.multiply(purchasePrice.getIncrePurPriceTax()));
                                price = price.multiply(li.getInnerQuantity());
                            }else if(diver.compareTo(new BigDecimal(0))==-1){
                                //递减少
                                BigDecimal value = getVale(diver.abs(),purchasePrice.getDecreQuantity(), roundingType);
                                price = purchasePrice.getPurchasePriceTax().subtract(value.multiply(purchasePrice.getDecPurPriceTax()));
                                price = price.multiply(li.getInnerQuantity());
                            }else{
                                //相等
                                price = purchasePrice.getPurchasePriceTax();
                                price = price.multiply(li.getInnerQuantity());
                            }
                        }else{
                            price = purchasePrice.getPurchasePriceTax().multiply(li.getInnerQuantity());
                        }
                        prices.add(price);
                    }
                }
                // 这里加个标识判断有没有提前返回
                boolean flag = true;
                if(prices.size() > 0){
                    if(prices.size() == itemList.size()){
                        //求和
                        BigDecimal totalPrice = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                        purchaseZippe = totalPrice.divide(BigDecimal.ONE,4,BigDecimal.ROUND_UP);
                    }else{
                        flag = false;
                    }
                }
                if (flag) {
                    if(CollectionUtil.isNotEmpty(taxs)){
                        priceItem.setTaxRate(taxs.get(0));
                    }
                    priceItem.setPurchasePriceTax(purchaseZippe);
                    item = priceItem;
                }
            }
            else {
                item = priceItem;
            }
        } else{
            //获取整条拉链采购价
            PurPurchasePriceItem purchasePrice = item != null ? item : getPrice(purPurchasePrice);
            BigDecimal price = null;
            if(purchasePrice != null){
                String isRecursionPrice = purchasePrice.getIsRecursionPrice();
                if(ConstantsEms.YES.equals(isRecursionPrice)){
                    //最小起算量
                    BigDecimal priceMinQuantity = purchasePrice.getPriceMinQuantity();
                    //取整方式
                    String roundingType = purchasePrice.getRoundingType();
                    //获取链胚长度->等于整合拉链sku2的长度
                    Long sku2Sid = purPurchasePrice.getSku2Sid();
                    BasSku basSku = basSkuMapper.selectById(sku2Sid);
                    BigDecimal lenth = basSku.getSkuNumeralValue();
                    if(priceMinQuantity != null){
                        if(lenth.subtract(priceMinQuantity).compareTo(BigDecimal.ZERO) == -1){
                            lenth = priceMinQuantity;
                        }
                    }
                    //差异量
                    BigDecimal diver = lenth.subtract(purchasePrice.getReferQuantity());
                    if(diver.compareTo(BigDecimal.ZERO) == 1){
                        //递增
                        BigDecimal value = getVale(diver.abs(), purchasePrice.getIncreQuantity(), roundingType);
                        price = purchasePrice.getPurchasePriceTax().add(value.multiply(purchasePrice.getIncrePurPriceTax()));
                    }else if(diver.compareTo(BigDecimal.ZERO) == -1){
                        //递减少
                        BigDecimal value = getVale(diver.abs(),purchasePrice.getDecreQuantity(), roundingType);
                        price = purchasePrice.getPurchasePriceTax().subtract(value.multiply(purchasePrice.getDecPurPriceTax()));
                    }else{
                        //相等
                        price = purchasePrice.getPurchasePriceTax();
                    }
                }else{
                    price = purchasePrice.getPurchasePriceTax();
                }
                purchasePrice.setPurchasePriceTax(price)
                        .setPurchasePrice(price.divide(BigDecimal.ONE.add(purchasePrice.getTaxRate()),6,BigDecimal.ROUND_HALF_DOWN));
            }
            item = purchasePrice;
        }
        //默认获取通用税率
        ConTaxRate taxRate = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>().lambda()
                .eq(ConTaxRate::getIsDefault, "Y")
        );
        if(item == null){
            item = new PurPurchasePriceItem();
        }
        item.setSystemTaxRate(taxRate.getTaxRateValue());
        if(item.getUnitPrice() != null){
            ConMeasureUnit conMeasureUnit = conMeasureUnitMapper.selectOne(new QueryWrapper<ConMeasureUnit>().lambda()
                    .eq(ConMeasureUnit::getCode, item.getUnitPrice()));
            item.setUnitPriceName(conMeasureUnit.getName());
        }
        if(item.getUnitBase() != null){
            ConMeasureUnit conMeasureUnit = conMeasureUnitMapper.selectOne(new QueryWrapper<ConMeasureUnit>().lambda()
                    .eq(ConMeasureUnit::getCode, item.getUnitBase()));
            item.setUnitBaseName(conMeasureUnit.getName());
        }
        return item;
    }

    /**
     * 获取采购价
     */
    private PurPurchasePriceItem getPrice(PurPurchasePrice purPurchasePrice) {
        PurPurchasePriceItem response = null;
        String[] handleStatus = new String[]{HandleStatus.SUBMIT.getCode(), HandleStatus.CONFIRMED.getCode(), HandleStatus.CHANGEAPPROVAL.getCode()};
        List<PurPurchasePrice> result = null;
        //按：供应商、公司、采购模式、甲供料方式、商品/物料sid、SKU1sid查
        result = purPurchasePriceMapper.selectList(new QueryWrapper<PurPurchasePrice>().lambda()
                .eq(PurPurchasePrice::getVendorSid, purPurchasePrice.getVendorSid())
                .eq(PurPurchasePrice::getPurchaseMode, purPurchasePrice.getPurchaseMode())
                .eq(PurPurchasePrice::getRawMaterialMode, purPurchasePrice.getRawMaterialMode())
                .eq(PurPurchasePrice::getMaterialSid, purPurchasePrice.getMaterialSid())
                .eq(PurPurchasePrice::getSku1Sid, purPurchasePrice.getSku1Sid()));
        if (CollectionUtil.isNotEmpty(result)) {
            List<Long> sidList = result.stream().map(PurPurchasePrice::getPurchasePriceSid).collect(Collectors.toList());
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String now = sdf.format(date);
            //直接截取到日
            Date nowDate=DateUtils.parseDate(now);
            List<PurPurchasePriceItem> priceItem = purPurchasePriceItemMapper.selectList(new QueryWrapper<PurPurchasePriceItem>()
                    .lambda().in(PurPurchasePriceItem::getPurchasePriceSid, sidList)
                    .in(PurPurchasePriceItem::getHandleStatus, handleStatus)
                    .ge(PurPurchasePriceItem::getEndDate, nowDate)
                    .orderByAsc(PurPurchasePriceItem::getEndDate));
            if(CollectionUtil.isNotEmpty(priceItem)){
                response = priceItem.get(0);
            }
        }
        if (response == null) {
            //按：供应商、公司、采购模式、甲供料方式、商品/物料sid查
            result = purPurchasePriceMapper.selectList(new QueryWrapper<PurPurchasePrice>()
                    .lambda().eq(PurPurchasePrice::getVendorSid, purPurchasePrice.getVendorSid())
                    .eq(PurPurchasePrice::getPurchaseMode, purPurchasePrice.getPurchaseMode())
                    .eq(PurPurchasePrice::getRawMaterialMode, purPurchasePrice.getRawMaterialMode())
                    .eq(PurPurchasePrice::getMaterialSid, purPurchasePrice.getMaterialSid())
                    .isNull(PurPurchasePrice::getSku1Sid));
            if (CollectionUtil.isNotEmpty(result)) {
                List<Long> sidList = result.stream().map(PurPurchasePrice::getPurchasePriceSid).collect(Collectors.toList());
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String now = sdf.format(date);
                //直接截取到日
                Date nowDate=DateUtils.parseDate(now);
                List<PurPurchasePriceItem> priceItem = purPurchasePriceItemMapper.selectList(new QueryWrapper<PurPurchasePriceItem>()
                        .lambda().in(PurPurchasePriceItem::getPurchasePriceSid, sidList)
                        .in(PurPurchasePriceItem::getHandleStatus, handleStatus)
                        .ge(PurPurchasePriceItem::getEndDate, nowDate)
                        .orderByAsc(PurPurchasePriceItem::getEndDate));
                if(CollectionUtil.isNotEmpty(priceItem)){
                    response = priceItem.get(0);
                }
            }
        }
        if (response == null) {
            //按：供应商、公司、采购模式、甲供料方式、商品/物料sid查
            result = purPurchasePriceMapper.selectList(new QueryWrapper<PurPurchasePrice>()
                    .lambda().isNull(PurPurchasePrice::getVendorSid)
                    .eq(PurPurchasePrice::getPurchaseMode, purPurchasePrice.getPurchaseMode())
                    .eq(PurPurchasePrice::getRawMaterialMode, purPurchasePrice.getRawMaterialMode())
                    .eq(PurPurchasePrice::getMaterialSid, purPurchasePrice.getMaterialSid())
                    .eq(PurPurchasePrice::getSku1Sid, purPurchasePrice.getSku1Sid()));
            if (CollectionUtil.isNotEmpty(result)) {
                List<Long> sidList = result.stream().map(PurPurchasePrice::getPurchasePriceSid).collect(Collectors.toList());
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String now = sdf.format(date);
                //直接截取到日
                Date nowDate=DateUtils.parseDate(now);
                List<PurPurchasePriceItem> priceItem = purPurchasePriceItemMapper.selectList(new QueryWrapper<PurPurchasePriceItem>()
                        .lambda().in(PurPurchasePriceItem::getPurchasePriceSid, sidList)
                        .in(PurPurchasePriceItem::getHandleStatus, handleStatus)
                        .ge(PurPurchasePriceItem::getEndDate, nowDate)
                        .orderByAsc(PurPurchasePriceItem::getEndDate));
                if(CollectionUtil.isNotEmpty(priceItem)){
                    response = priceItem.get(0);
                }
            }
        }
        if (response == null) {
            //按：供应商、公司、采购模式、甲供料方式、商品/物料sid查
            result = purPurchasePriceMapper.selectList(new QueryWrapper<PurPurchasePrice>()
                    .lambda().isNull(PurPurchasePrice::getVendorSid)
                    .eq(PurPurchasePrice::getPurchaseMode, purPurchasePrice.getPurchaseMode())
                    .eq(PurPurchasePrice::getRawMaterialMode, purPurchasePrice.getRawMaterialMode())
                    .eq(PurPurchasePrice::getMaterialSid, purPurchasePrice.getMaterialSid())
                    .isNull(PurPurchasePrice::getSku1Sid));
            if (CollectionUtil.isNotEmpty(result)) {
                List<Long> sidList = result.stream().map(PurPurchasePrice::getPurchasePriceSid).collect(Collectors.toList());
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String now = sdf.format(date);
                //直接截取到日
                Date nowDate=DateUtils.parseDate(now);
                List<PurPurchasePriceItem> priceItem = purPurchasePriceItemMapper.selectList(new QueryWrapper<PurPurchasePriceItem>()
                        .lambda().in(PurPurchasePriceItem::getPurchasePriceSid, sidList)
                        .in(PurPurchasePriceItem::getHandleStatus, handleStatus)
                        .ge(PurPurchasePriceItem::getEndDate, nowDate)
                        .orderByAsc(PurPurchasePriceItem::getEndDate));
                if(CollectionUtil.isNotEmpty(priceItem)){
                    response = priceItem.get(0);
                }
            }
        }
        return response;
    }

    @Override
    public int processCheck(List<Long> purchasePriceSid) {
        purchasePriceSid.forEach(li->{
            PurPurchasePrice purchasePrice = purPurchasePriceMapper.selectPurPurchasePriceById(li);
            Boolean exitSku = purchasePrice.getSku1Sid() != null ? true : false;
            List<PurPurchasePrice> list = purPurchasePriceMapper.selectList(new QueryWrapper<PurPurchasePrice>().lambda()
                    .eq(PurPurchasePrice::getVendorSid, purchasePrice.getVendorSid())
                    .eq(PurPurchasePrice::getMaterialSid, purchasePrice.getMaterialSid())
                    .eq(PurPurchasePrice::getPurchaseMode, purchasePrice.getPurchaseMode())
                    .eq(PurPurchasePrice::getRawMaterialMode, purchasePrice.getRawMaterialMode())
                    .eq(exitSku, PurPurchasePrice::getSku1Sid, purchasePrice.getSku1Sid()));
            list=list.stream().filter(m->!m.getPurchasePriceSid().toString().equals(purchasePrice.getPurchasePriceSid().toString())).collect(Collectors.toList());
            if (exitSku) {
                if (CollectionUtils.isNotEmpty(list)) {
                    PurPurchasePrice price = list.get(0);
                    throw  new  CustomException("采购价"+purchasePrice.getPurchasePriceCode()+"与单号"+price.getPurchasePriceCode()+"维度相同，不允许提交");
                }
            } else {
                if (CollectionUtils.isNotEmpty(list)) {
                    //查找是否存在sku1为null的情况
                    PurPurchasePrice PurPurchasePriceExisted = list.stream().filter(o -> o.getSku1Sid() == null).findFirst().orElse(null);
                    if (PurPurchasePriceExisted != null) {
                        throw  new  CustomException("采购价"+purchasePrice.getPurchasePriceCode()+"与单号"+PurPurchasePriceExisted.getPurchasePriceCode()+"维度相同，不允许提交");
                    }
                }
            }
            CheckUniqueCommonRequest request = new CheckUniqueCommonRequest();
            BeanCopyUtils.copyProperties(purchasePrice,request);
            request.setId(purchasePrice.getPurchasePriceSid())
                    .setCode(purchasePrice.getPurchasePriceCode());
            //校验
            checkUnique(request);

        });
        return 0;
    }


    public void judgeImport(PurPurchasePrice purPurchasePrice){
        List<PurPurchasePriceItem> listPurPurchasePriceItem = purPurchasePrice.getListPurPurchasePriceItem();
        boolean judege = validTime(listPurPurchasePriceItem);
        if(judege){
            listPurPurchasePriceItem.forEach(item->{
                //二层校验
                judgeTime(purPurchasePrice, item);
            });
        }else{
            throw new CustomException("存在交集");
        }
    }

    //校验输入日期的合法性
    public static boolean isValidDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        boolean convertSuccess = true;
        try {
            format.setLenient(false);
            format.parse(str);
            DateUtil.parse(str);
        } catch (Exception e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }
    //校验输入的是否是数字
    public static boolean isValidDouble(String str){
        boolean convertSuccess = true;
        try {
            Double.valueOf(str);
        } catch (Exception e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }

    public String removeZero(String s){
        if(s.indexOf(".") > 0){
            //正则表达
            s = s.replaceAll("0+?$", "");//去掉后面无用的零
            s = s.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
        }
        return s;
    }

    //是否走审批
    public Boolean isApproval(String code){
        List<String> transferList = Arrays.asList("ZCKHZD","ZCKHXS");
        boolean exit = transferList.stream().anyMatch(item -> item.equals(code));
        return exit;
    }
}
