package com.platform.ems.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.alibaba.fastjson.JSONObject;
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
import com.platform.ems.constant.AutoIdField;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsWorkbench;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.SalePriceActionRequest;
import com.platform.ems.domain.dto.response.SaleReportResponse;
import com.platform.ems.domain.dto.response.salSalePriceErrMsgResponse;
import com.platform.ems.enums.FormType;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConMeasureUnit;
import com.platform.ems.plug.domain.ConTaxRate;
import com.platform.ems.plug.mapper.ConMeasureUnitMapper;
import com.platform.ems.plug.mapper.ConTaxRateMapper;
import com.platform.ems.service.*;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.mq.MsgReceiver;
import com.platform.ems.util.mq.MsgSender;
import com.platform.ems.workflow.domain.Submit;
import com.platform.ems.workflow.service.IWorkFlowService;
import com.platform.api.service.RemoteFlowableService;
import com.platform.flowable.domain.vo.FlowTaskVo;
import com.platform.flowable.domain.vo.FormParameter;
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
 * 销售价信息Service业务层处理
 *
 * @author linhongwei
 * @date 2021-03-05
 */
@Service
@SuppressWarnings("all")
public class SalSalePriceServiceImpl extends ServiceImpl<SalSalePriceMapper, SalSalePrice> implements ISalSalePriceService {
    @Autowired
    private SalSalePriceMapper salSalePriceMapper;
    @Autowired
    private SalSalePriceItemMapper salSalePriceItemMapper;
    @Autowired
    private SalSalePriceAttachmentMapper salSalePriceAttachmentMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private BasSkuMapper basSkuMapper;
    @Autowired
    private ITecBomHeadService iTecBomHeadService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private ConTaxRateMapper conTaxRateMapper;
    @Autowired
    private BasMaterialSkuMapper basMaterialSkuMapper;
    @Autowired
    private ConMeasureUnitMapper conMeasureUnitMapper;
    @Autowired
    private ISysFormProcessService formProcessService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private ISystemUserService userService;
    @Autowired
    private SysBusinessBcstMapper sysBusinessBcstMapper;
    @Autowired
    private RemoteFlowableService flowableService;
    @Autowired
    private CosProductCostMapper cosProductCostMapper;
    @Autowired
    private IWorkFlowService workflowService;
    @Autowired
    private RemoteFlowableService  remoteFlowableService;
    @Autowired
    private SalSalesOrderMapper salSalesOrderMapper;
    @Autowired
    private MsgSender msgSender;
    @Autowired
    private  MsgReceiver msgReceiver;
    private static final String TITLE = "销售价信息";


    /**
     * 查询销售价信息
     *
     * @param salePriceSid 销售价信息ID
     * @return 销售价信息
     */
    @Override
    public SalSalePrice selectSalSalePriceById(Long salePriceSid) {
        //主表
        SalSalePrice salSalePrice = salSalePriceMapper.selectSalSalePriceById(salePriceSid);
        //取详情时将图片路径分割出来存入数组
        if (StrUtil.isNotBlank(salSalePrice.getPicturePathSecond())) {
            String[] picturePathList = salSalePrice.getPicturePathSecond().split(";");
            salSalePrice.setPicturePathList(picturePathList);
        }
        //明细表
        List<SalSalePriceItem> salSalePriceItems = salSalePriceItemMapper.selectSalSalePriceItemById(salePriceSid);
        if(CollectionUtil.isNotEmpty(salSalePriceItems)){
            salSalePriceItems=salSalePriceItems.stream().sorted(Comparator.comparing(SalSalePriceItem::getStartDate).reversed()).collect(Collectors.toList());
        }
        QueryWrapper<SalSalePriceAttachment> wrapperAttachment = new QueryWrapper<>();
        wrapperAttachment.eq("sale_price_sid", salePriceSid);
        salSalePrice.setListSalSalePriceItem(salSalePriceItems);
        //附加表
        List<SalSalePriceAttachment> salSalePriceAttachments = salSalePriceAttachmentMapper.selectList(wrapperAttachment);
        salSalePrice.setAttachmentList(salSalePriceAttachments);
        MongodbUtil.find(salSalePrice);
        return salSalePrice;
    }

    /**
     * 不含税值计算
     */
    public void changePrice(List<SalSalePriceItem> salSalePriceItems) {
        if (CollectionUtil.isNotEmpty(salSalePriceItems)) {
            salSalePriceItems.forEach(li -> {
                if (li.getTaxRate() != null) {
                    if (li.getSalePriceTax() != null) {
                        li.setSalePrice(li.getSalePriceTax().divide(BigDecimal.ONE.add(li.getTaxRate()), 6, BigDecimal.ROUND_HALF_UP));
                    }
                    if (li.getDecrePriceTax() != null) {
                        li.setDecrePrice(li.getDecrePriceTax().divide(BigDecimal.ONE.add(li.getTaxRate()), 6, BigDecimal.ROUND_HALF_UP));
                    }
                    if (li.getIncrePriceTax() != null) {
                        li.setIncrePrice(li.getIncrePriceTax().divide(BigDecimal.ONE.add(li.getTaxRate()), 6, BigDecimal.ROUND_HALF_UP));
                    }
                }
            });
        }
    }

    //基本计量单位和采购价格单位
    public void setUnit(List<SalSalePriceItem> list){
        list.forEach(li->{
            if(li.getUnitBase()!=null){
                if(li.getUnitBase().equals(li.getUnitPrice())){
                    li.setUnitConversionRate(BigDecimal.ONE);
                }else{
                    if(li.getUnitConversionRate()==null){
                        throw new  CustomException("销售价单位“与”基本计量单位“不一致，单位换算比例不允许为空");
                    }
                }
            }
        });
    }

    /**
     * 查询销售价信息列表
     *
     * @param salSalePrice 销售价信息
     * @return 销售价信息
     */
    @Override
    public List<SalSalePrice> selectSalSalePriceList(SalSalePrice salSalePrice) {
        List<SalSalePrice> priceList = salSalePriceMapper.getList(salSalePrice);
        priceList.forEach(li -> {
            SysFormProcess formProcess = new SysFormProcess();
            formProcess.setFormId(li.getSalePriceSid());
            List<SysFormProcess> list = formProcessService.selectSysFormProcessList(formProcess);
            if (list != null && list.size() > 0) {
                formProcess = new SysFormProcess();
                formProcess = list.get(0);
                li.setApprovalNode(formProcess.getApprovalNode());
                li.setApprovalUserName(formProcess.getApprovalUserName());
                li.setApprovalUserId(formProcess.getApprovalUserId());
                li.setSubmitDate(formProcess.getCreateDate());
                li.setSubmitUserName(userService.selectSysUserById(Long.valueOf(formProcess.getCreateById())).getNickName());
            }
        });
        return priceList;
    }

    /**
     * 查询销售价报表
     *
     * @param saleReportResponse 销售价信息
     * @return 销售价信息
     */
    @Override
    public List<SaleReportResponse> saleReport(SaleReportResponse saleReportResponse) {
        List<SaleReportResponse> saleReportResponses = salSalePriceItemMapper.saleReport(saleReportResponse);
        String isFinallyNode = saleReportResponse.getIsFinallyNode();
        saleReportResponses.forEach(li -> {
            if (li.getSalePriceTax() != null) {
                li.setSalePriceTaxS(removeZero(li.getSalePriceTax().toString()));
            }
            if (li.getSalePrice() != null) {
                li.setSalePriceS(removeZero(li.getSalePrice().toString()));
            }
            if (li.getDecreQuantity() != null) {
                li.setDecreQuantityS(removeZero(li.getDecreQuantity().toString()));
            }
            if (li.getIncreQuantity() != null) {
                li.setIncreQuantityS(removeZero(li.getIncreQuantity().toString()));
            }
            if (li.getDecPurPriceTax() != null) {
                li.setDecPurPriceTaxS(removeZero(li.getDecPurPriceTax().toString()));
            }
            if (li.getDecPurPrice() != null) {
                li.setDecPurPriceS(removeZero(li.getDecPurPrice().toString()));
            }
            if (li.getDecrePrice() != null) {
                li.setDecrePriceS(removeZero(li.getDecrePrice().toString()));
            }
            if (li.getIncrePurPriceTax() != null) {
                li.setIncrePurPriceTaxS(removeZero(li.getIncrePurPriceTax().toString()));
            }
            if (li.getIncrePurPrice() != null) {
                li.setIncrePurPriceS(removeZero(li.getIncrePurPrice().toString()));
            }
            if (li.getIncrePrice() != null) {
                li.setIncrePriceS(removeZero(li.getIncrePrice().toString()));
            }
            if (li.getPriceMinQuantity() != null) {
                li.setPriceMinQuantityS(removeZero(li.getPriceMinQuantity().toString()));
            }
            if (li.getReferQuantity() != null) {
                li.setReferQuantityS(removeZero(li.getReferQuantity().toString()));
            }
            if (li.getUnitConversionRate() != null) {
                li.setUnitConversionRateS(removeZero(li.getUnitConversionRate().toString()));
            }
        });
        if(ConstantsEms.YES.equals(isFinallyNode)){
            saleReportResponses.stream().forEach(li->{
                FlowTaskVo flowTaskVo = new FlowTaskVo();
                flowTaskVo.setFormId(Long.valueOf(li.getSalePriceItemSid().toString()));
                AjaxResult nextFlowNode = remoteFlowableService.getNextFlowNode(flowTaskVo);
                Object code = nextFlowNode.get("code");
                if(ConstantsEms.CODE_SUCESS.equals(nextFlowNode.get("code").toString())){
                    li.setIsFinallyNode(ConstantsEms.YES);
                }
            });
            List<SaleReportResponse> list = saleReportResponses.stream().filter(li -> isFinallyNode.equals(li.getIsFinallyNode())).collect(Collectors.toList());
            return list;
        }
        return saleReportResponses;
    }

    public String removeZero(String s) {
        if (s.indexOf(".") > 0) {
            //正则表达
            s = s.replaceAll("0+?$", "");//去掉后面无用的零
            s = s.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
        }
        return s;
    }

    public void AddCheck(SalSalePrice salSalePrice) {
        String handleStatus = salSalePrice.getHandleStatus();
        if (ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
            salSalePrice.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            salSalePrice.setConfirmDate(new Date());
        }
    }

    /**
     * @param salePriceListRequest 销售价信息
     * @return 销售价信息
     */
    @Override
    public List<SalSalePrice> getList(SalSalePrice salSalePrice) {
        return salSalePriceMapper.getList(salSalePrice);
    }


    public void judgeImport(SalSalePrice salSalePrice){
        List<SalSalePriceItem> listSalSalePriceItem = salSalePrice.getListSalSalePriceItem();
        boolean judege = validTime(listSalSalePriceItem);
        if(judege){
            listSalSalePriceItem.forEach(item->{
                //二层校验
                judgeTime(salSalePrice, item);
            });
        }else{
            throw new CustomException("存在交集");
        }
    }
    /**
     * 新增销售价信息
     *
     * @param salSalePriceRequest 销售价信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult insertSalSalePrice(SalSalePrice salSalePrice) {
        AddCheck(salSalePrice);
        int row = salSalePriceMapper.insert(salSalePrice);
        //获取自动注入后的id值
        Long salePriceSid = salSalePrice.getSalePriceSid();
        //新增销售价附件表
        List<SalSalePriceAttachment> ListSalSalePriceAttachment = salSalePrice.getAttachmentList();
        if (CollectionUtils.isNotEmpty(ListSalSalePriceAttachment)) {
            ListSalSalePriceAttachment.forEach(m -> {
                m.setSalePriceSid(salePriceSid);
                salSalePriceAttachmentMapper.insert(m);
            });
        }
        //新增销售价明细表
        List<SalSalePriceItem> listSalSalePriceItem = salSalePrice.getListSalSalePriceItem();
        if (CollectionUtils.isNotEmpty(listSalSalePriceItem)) {
            changePrice(listSalSalePriceItem);
            setItemNum(listSalSalePriceItem);
            setUnit(listSalSalePriceItem);
            boolean judege = validTime(listSalSalePriceItem);
            if (judege) {
                listSalSalePriceItem.forEach(m -> {
//                    //二层校验
                    judgeTime(salSalePrice, m);
                    m.setSalePriceSid(salePriceSid);
                    m.setHandleStatus(ConstantsEms.SAVA_STATUS);
                    salSalePriceItemMapper.insert(m);
                    if(!ConstantsEms.YES.equals(salSalePrice.getIsCostSale())){
                        if(salSalePrice.getImportHandle()==null){
                            //插入日志
                            MongodbUtil.insertUserLogItem(m.getSalePriceSid(), BusinessType.INSERT.getValue(),TITLE,m.getItemNum());
                        }else{
                            //插入日志
                            MongodbUtil.insertUserLogItem(m.getSalePriceSid(), BusinessType.IMPORT.getValue(),TITLE,m.getItemNum());
                        }
                    }else{
                        MongodbUtil.insertApprovalLogAddNum(m.getSalePriceSid(), BusinessType.CHECK.getValue(),"来自销售成本核算",m.getItemNum());
                    }
                });
            } else {
                return AjaxResult.error("明细中有效期时间段存在交集，不允许新增");
            }
        }
        //待办通知
        SalSalePrice purchasePrice = salSalePriceMapper.selectById(salSalePrice.getSalePriceSid());
        salSalePrice.setSalePriceCode(purchasePrice.getSalePriceCode());
        listSalSalePriceItem.forEach(m->{
            SysTodoTask sysTodoTask = new SysTodoTask();
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsEms.TABLE_MANUFACTURE_ORDER)
                    .setDocumentItemSid(m.getSalePriceItemSid())
                    .setDocumentSid(m.getSalePriceSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                sysTodoTask.setTitle("销售价" + purchasePrice.getSalePriceCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(purchasePrice.getSalePriceCode())
                        .setNoticeDate(new Date())
                        .setMenuId(ConstantsWorkbench.sal_sale_price)
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        });
        return AjaxResult.success("新增销售价信息成功", new SalSalePrice().setSalePriceSid(salSalePrice.getSalePriceSid()));

    }

    /**
     * 行号赋值
     */
    public void  setItemNum(List<SalSalePriceItem> list){
        int size = list.size();
        if(size>0){
            for (int i=1;i<=size;i++){
                list.get(i-1).setItemNum(i);
            }
        }
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(SalSalePrice purchasePrice) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, purchasePrice.getSalePriceSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, purchasePrice.getSalePriceSid()));
        }
    }
    /**
     * 判断是否可以新增销售价
     *
     * @param salSalePriceRequest 销售价信息
     * @return 结果
     */
    @Override
    public AjaxResult judgeAdd(SalSalePrice salSalePrice){
        BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                .eq(BasMaterial::getMaterialCode,salSalePrice.getMaterialCode()));
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
        List<SalSalePrice>  list=null;
        Boolean exitSku=salSalePrice.getSku1Sid()!=null?true:false;
        Long customerSid = salSalePrice.getCustomerSid();
        if(customerSid==null){
            list= salSalePriceMapper.selectList(new QueryWrapper<SalSalePrice>().lambda()
                    .isNull(SalSalePrice::getCustomerSid)
                    .eq(SalSalePrice::getMaterialSid, salSalePrice.getMaterialSid())
                    .eq(SalSalePrice::getSaleMode,salSalePrice.getSaleMode())
                    .eq(SalSalePrice::getRawMaterialMode, salSalePrice.getRawMaterialMode())
                    .eq(exitSku,SalSalePrice::getSku1Sid,salSalePrice.getSku1Sid()));
        }else{
             list= salSalePriceMapper.selectList(new QueryWrapper<SalSalePrice>().lambda()
                    .eq(SalSalePrice::getCustomerSid, salSalePrice.getCustomerSid())
                    .eq(SalSalePrice::getMaterialSid, salSalePrice.getMaterialSid())
                    .eq(SalSalePrice::getSaleMode,salSalePrice.getSaleMode())
                    .eq(SalSalePrice::getRawMaterialMode, salSalePrice.getRawMaterialMode())
                    .eq(exitSku,SalSalePrice::getSku1Sid,salSalePrice.getSku1Sid()));
        }
        if(exitSku){
            if(CollectionUtils.isNotEmpty(list)){
                Long salePriceSid = list.get(0).getSalePriceSid();
                return AjaxResult.success("1",salePriceSid.toString());
            }else{
                return AjaxResult.success("允许新建销售价","1");
            }
        }else{
            //查找是否存在sku1为null的情况
           SalSalePrice salSalePriceExisted = list.stream().filter(o -> o.getSku1Sid() == null).findFirst().orElse(null);
            if(salSalePriceExisted==null){
                return AjaxResult.success("允许新建销售价","1");
            }else{
                return AjaxResult.success("1",salSalePriceExisted.getSalePriceSid().toString());
            }

        }
    }
        //校验是否存在两个时间段存在交集
    private static boolean validTime(List<SalSalePriceItem> itemList){
        for (int i = 0; i < itemList.size(); i++) {
            SalSalePriceItem item=itemList.get(i);
            for (int j = 0; j < itemList.size(); j++) {
                if(i>=j){
                    continue;
                }
                SalSalePriceItem compareItem=itemList.get(j);
                Date startTime1=item.getStartDate();
                Date endTime1=item.getEndDate();
                Date startTime2=compareItem.getStartDate();
                Date endTime2=compareItem.getEndDate();
                if(startTime2.getTime()>=startTime1.getTime()&&startTime2.getTime()<=endTime1.getTime()){
                    return false;
                }
                if(endTime2.getTime()>=startTime1.getTime()&&endTime2.getTime()<=endTime1.getTime()){
                    return false;
                }
                if(startTime2.getTime()<startTime1.getTime()&&endTime2.getTime()>endTime1.getTime()){
                    return false;
                }
            }
        }
        return true;
    }

    public void JudgeNull(SalSalePrice salSalePrice){
        List<SalSalePriceItem> list = salSalePrice.getListSalSalePriceItem();
        if(CollectionUtils.isNotEmpty(list)){
            list.forEach(li->{
                if(li.getSalePriceTax()==null||li.getTaxRate()==null){
                    throw new CustomException("存在销售价或税率未维护的明细行，请填写后再确认");
                }
            });
        }else{
            throw new CustomException("确认时明细行不允许为空");
        }
    }

    /**
     * 新增/编辑直接提交销售价信息
     *
     * @param purPurchasePrice 销售价信息主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult submit(SalSalePrice salSalePrice) {
        int row = 0;
        AjaxResult result = AjaxResult.success();
        if (salSalePrice.getSalePriceSid() == null) {
            // 新建
            result = this.insertSalSalePrice(salSalePrice);
            if (HttpStatus.ERROR == (int)result.get(AjaxResult.CODE_TAG)) {
                return result;
            }
        }
        else {
            result = this.updateSalSalePrice(salSalePrice);
            if (HttpStatus.ERROR == (int)result.get(AjaxResult.CODE_TAG)) {
                return result;
            }
        }
        row = 1;
        if (row == 1) {
            List<Long> sidList = new ArrayList<Long>(){{add(salSalePrice.getSalePriceSid());}};
            this.processCheck(sidList);
            if (CollectionUtil.isNotEmpty(salSalePrice.getListSalSalePriceItem())) {
                // 提交
                Submit submit = new Submit();
                submit.setFormType(FormType.SalePrice.getCode());
                List<FormParameter> formParameters = new ArrayList<>();
                salSalePrice.getListSalSalePriceItem().forEach(item->{
                    FormParameter formParameter = new FormParameter();
                    formParameter.setParentId(String.valueOf(salSalePrice.getSalePriceSid()));
                    formParameter.setFormId(String.valueOf(item.getSalePriceItemSid()));
                    formParameter.setFormCode(String.valueOf(salSalePrice.getSalePriceCode()));
                    formParameters.add(formParameter);
                });
                submit.setFormParameters(formParameters);
                submit.setStartUserId(String.valueOf(ApiThreadLocalUtil.get().getUserid()));
                workflowService.submitByItem(submit);
            }
        }
        return AjaxResult.success(result.get(AjaxResult.DATA_TAG));
    }

    /**
         * 修改销售价信息
         *
         * @param salSalePrice 销售价信息
         * @return 结果
         */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateSalSalePrice(SalSalePrice salSalePrice) {
        AddCheck(salSalePrice);
        Long id = salSalePrice.getSalePriceSid();
        //修改主表信息
        int row = salSalePriceMapper.updateAllById(salSalePrice);
        SalSalePrice response = salSalePriceMapper.selectSalSalePriceById(salSalePrice.getSalePriceSid());
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(salSalePrice.getSalePriceSid(), BusinessType.UPDATE.getValue(), response, salSalePrice, TITLE);
        }
        List<SalSalePriceItem> listSalSalePriceItem = salSalePrice.getListSalSalePriceItem();
        //不含税值计算
        changePrice(listSalSalePriceItem);
        if (CollectionUtils.isNotEmpty(listSalSalePriceItem)) {
            setPriceSid(id, listSalSalePriceItem);
            boolean judege = validTime(listSalSalePriceItem);
            if (judege) {
                listSalSalePriceItem.forEach(m -> {
                    //二层校验
                    judgeTime(salSalePrice, m);
                });
            } else {
                return AjaxResult.error("明细中有效期时间段存在交集，不允许修改");
            }
        }
        //删除原有明细表信息
        QueryWrapper<SalSalePriceItem> wrapper = new QueryWrapper<>();
        wrapper.eq("sale_price_sid", salSalePrice.getSalePriceSid());
        salSalePriceItemMapper.delete(wrapper);
        if (CollectionUtils.isNotEmpty(listSalSalePriceItem)) {
            listSalSalePriceItem.forEach(li -> {
                li.setSalePriceSid(id);
                salSalePriceItemMapper.insert(li);
            });
        }
        //删除原有附件表信息
        QueryWrapper<SalSalePriceAttachment> wrapperAttachment = new QueryWrapper<>();
        wrapperAttachment.eq("sale_price_sid", salSalePrice.getSalePriceSid());
        salSalePriceAttachmentMapper.delete(wrapperAttachment);
        List<SalSalePriceAttachment> listSalSalePriceAttachment = salSalePrice.getAttachmentList();
        if (CollectionUtils.isNotEmpty(listSalSalePriceAttachment)) {
            listSalSalePriceAttachment.forEach(o -> {
                //插入目前存在的表
                o.setSalePriceSid(id);
                salSalePriceAttachmentMapper.insert(o);
            });
        }
        if (!ConstantsEms.SAVA_STATUS.equals(salSalePrice.getHandleStatus())) {
            //校验是否存在待办
            checkTodoExist(salSalePrice);
        }
        //更新通知
        if (ConstantsEms.CHECK_STATUS.equals(salSalePrice.getHandleStatus())) {
            SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
            sysBusinessBcst.setTitle("商品编码" + salSalePrice.getMaterialCode() + "，销售价编号" + salSalePrice.getSalePriceCode() + "的信息发生变更，请知悉！")
                    .setDocumentSid(salSalePrice.getSalePriceSid())
                    .setDocumentCode(salSalePrice.getSalePriceCode())
                    .setNoticeDate(new Date()).setUserId(ApiThreadLocalUtil.get().getUserid());
            sysBusinessBcstMapper.insert(sysBusinessBcst);
        }
        return AjaxResult.success("销售价信息修改成功");
    }

    /**
     * 修改销售价信息-新
     *
     * @param salSalePrice 销售价信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateSalSalePriceNew(SalSalePrice salSalePrice) {
        // 唯一性校验
        QueryWrapper<SalSalePrice> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .ne(SalSalePrice::getSalePriceSid, salSalePrice.getSalePriceSid())
                .eq(SalSalePrice::getMaterialSid, salSalePrice.getMaterialSid())
                .eq(SalSalePrice::getRawMaterialMode, salSalePrice.getRawMaterialMode())
                .eq(SalSalePrice::getSaleMode, salSalePrice.getSaleMode())
                .eq(SalSalePrice::getPriceDimension, salSalePrice.getPriceDimension());
        if (salSalePrice.getSku1Sid() == null) {
            queryWrapper.lambda().isNull(SalSalePrice::getSku1Sid);
        }
        else {
            queryWrapper.lambda().eq(SalSalePrice::getSku1Sid, salSalePrice.getSku1Sid());
        }
        if (salSalePrice.getCustomerSid() == null) {
            queryWrapper.lambda().isNull(SalSalePrice::getCustomerSid);
        }
        else {
            queryWrapper.lambda().eq(SalSalePrice::getCustomerSid, salSalePrice.getCustomerSid());
        }
        if (CollectionUtil.isNotEmpty(salSalePriceMapper.selectList(queryWrapper))) {
            return AjaxResult.error("物料/商品+客户+客供料方式+销售模式+价格维度+SKU1”维度的销售价已存在！");
        }
        AddCheck(salSalePrice);
        Long id=salSalePrice.getSalePriceSid();
        //修改主表信息
        int row=salSalePriceMapper.updateAllById(salSalePrice);
        List<SalSalePriceItem> listSalSalePriceItem = salSalePrice.getListSalSalePriceItem();
        if(CollectionUtils.isNotEmpty(listSalSalePriceItem)) {
            setUnit(listSalSalePriceItem);
            setPriceSid(id,listSalSalePriceItem);
            boolean judege = validTime(listSalSalePriceItem);
            if(judege==true){
                listSalSalePriceItem.forEach(m -> {
                    //二层校验
                    judgeTime(salSalePrice,m);
                });
            }else{
                return AjaxResult.error("明细中有效期时间段存在交集，不允许修改");
            }
        }
        this.processCheck(new ArrayList<Long>(){{add(salSalePrice.getSalePriceSid());}});
        if(ConstantsEms.CHECK_STATUS.equals(salSalePrice.getHandleStatus())){
            //插入日志
            MongodbUtil.insertUserLog(salSalePrice.getSalePriceSid(), BusinessType.CHANGE.getValue(),TITLE);
        }
        //修改原有明细表信息
        if(CollectionUtils.isNotEmpty(listSalSalePriceItem)){
//            setItemNum(listSalSalePriceItem);
            if(listSalSalePriceItem.size()==1){
                listSalSalePriceItem.forEach(li->{
                    li.setSubmitHandle(ConstantsEms.YES);
                });
            }
            //不含税值计算
            changePrice(listSalSalePriceItem);
            List<SalSalePriceItem> salSalePriceItems = salSalePriceItemMapper.selectList(new QueryWrapper<SalSalePriceItem>().lambda()
                    .eq(SalSalePriceItem::getSalePriceSid, salSalePrice.getSalePriceSid())
            );
            List<Long> longs = salSalePriceItems.stream().map(li -> li.getSalePriceItemSid()).collect(Collectors.toList());
            List<Long> longsNow = listSalSalePriceItem.stream().map(li -> li.getSalePriceItemSid()).collect(Collectors.toList());
            //两个集合取差集
            List<Long> reduce = longs.stream().filter(item -> !longsNow.contains(item)).collect(Collectors.toList());
            //删除明细
            if(CollectionUtil.isNotEmpty(reduce)){
                List<SalSalePriceItem> reduceList = salSalePriceItemMapper.selectList(new QueryWrapper<SalSalePriceItem>().lambda()
                        .in(SalSalePriceItem::getSalePriceItemSid, reduce)
                );
                reduceList.forEach(li->{
                    //插入日志
                    MongodbUtil.insertUserLogItem(li.getSalePriceSid(), BusinessType.DELETE.getValue(),TITLE,li.getItemNum());
                });
                salSalePriceItemMapper.deleteBatchIds(reduce);
                //删除待办
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .in(SysTodoTask::getDocumentItemSid, reduce));
            }
            //修改明细
            List<SalSalePriceItem> exitItem = listSalSalePriceItem.stream().filter(li -> li.getSalePriceItemSid() != null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(exitItem)){
                exitItem.forEach(li->{
                    salSalePriceItemMapper.updateAllById(li);
                    if(ConstantsEms.SAVA_STATUS.equals(salSalePrice.getHandleStatus())){
                        SalSalePriceItem oldItem = salSalePriceItemMapper.selectById(li.getSalePriceItemSid());
                        String bussiness=oldItem.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)?"变更":"编辑";
                        if(!ConstantsEms.IMPORT.equals(salSalePrice.getImportHandle())){
                            if(ConstantsEms.YES.equals(li.getSubmitHandle())){
                                //插入日志
                               MongodbUtil.insertUserLogItem(li.getSalePriceSid(), bussiness,TITLE,li.getItemNum());
                            }
                        }
                    }
                });
            }
            //新增明细
            List<SalSalePriceItem> nullItem = listSalSalePriceItem.stream().filter(li -> li.getSalePriceItemSid() == null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(nullItem)){
                int max = salSalePriceItems.stream().mapToInt(li -> li.getItemNum()).max().getAsInt();
                for (int i = 0; i < nullItem.size(); i++) {
                    int maxItem=max+i+1;
                    if( nullItem.get(i).getHandleStatus()==null){
                        nullItem.get(i).setHandleStatus(ConstantsEms.SAVA_STATUS);
                    }
                    nullItem.get(i).setItemNum(maxItem);
                    nullItem.get(i).setSalePriceSid(salSalePrice.getSalePriceSid());
                    salSalePriceItemMapper.insert(nullItem.get(i));
                }
                addTodo(nullItem, salSalePrice);
                nullItem.forEach(li->{
                    if(ConstantsEms.IMPORT.equals(li.getImportHandle())){
                        //插入日志
                        MongodbUtil.insertUserLogItem(li.getSalePriceSid(), BusinessType.IMPORT.getValue(),TITLE,li.getItemNum());
                    }else{
                        if(ConstantsEms.YES.equals(li.getSubmitHandle())){
                            //插入日志
                            MongodbUtil.insertUserLogItem(li.getSalePriceSid(), BusinessType.INSERT.getValue(),TITLE,li.getItemNum());
                        }
                    }
                });
            }
            List<SalSalePriceItem> checkList = listSalSalePriceItem.stream().filter(li -> ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(checkList)){
                if(!ConstantsEms.CHECK_STATUS.equals(salSalePrice.getHandleStatus())){
                    salSalePriceMapper.update(new SalSalePrice(),new UpdateWrapper<SalSalePrice>().lambda()
                            .eq(SalSalePrice::getSalePriceSid,salSalePrice.getSalePriceSid())
                            .set(SalSalePrice::getHandleStatus,ConstantsEms.CHECK_STATUS)
                    );
                }
            }else{
                if(!ConstantsEms.CHECK_STATUS.equals(salSalePrice.getHandleStatus())){
                    SalSalePriceItem salePriceItem = listSalSalePriceItem.get(0);
                    salSalePriceMapper.update(new SalSalePrice(),new UpdateWrapper<SalSalePrice>().lambda()
                            .eq(SalSalePrice::getSalePriceSid,salSalePrice.getSalePriceSid())
                            .set(SalSalePrice::getHandleStatus,salePriceItem.getHandleStatus())
                    );
                }
            }
        }
        //删除原有附件表信息
        QueryWrapper<SalSalePriceAttachment> wrapperAttachment = new QueryWrapper<>();
        wrapperAttachment.eq("sale_price_sid",salSalePrice.getSalePriceSid());
        salSalePriceAttachmentMapper.delete(wrapperAttachment);
        List<SalSalePriceAttachment> listSalSalePriceAttachment = salSalePrice.getAttachmentList();
        if(CollectionUtils.isNotEmpty(listSalSalePriceAttachment)){
            listSalSalePriceAttachment.forEach(o->{
                //插入目前存在的表
                o.setSalePriceSid(id);
                salSalePriceAttachmentMapper.insert(o);
            });}
        List<SalSalePriceItem> listItem = salSalePrice.getListSalSalePriceItem();
        if(CollectionUtils.isNotEmpty(listItem)){
            listItem.forEach(item->{
                //变更审批
                if (HandleStatus.CHANGEAPPROVAL.getCode().equals(item.getHandleStatus())){
                    Submit submit = new Submit();
                    submit.setStartUserId(ApiThreadLocalUtil.get().getUserid().toString());
                    submit.setFormType(FormType.XSJ_BG.getCode());
                    List<FormParameter> list = new ArrayList();
                    FormParameter formParameter = new FormParameter();
                    formParameter.setParentId(item.getSalePriceSid().toString());
                    formParameter.setFormId(item.getSalePriceItemSid().toString());
                    formParameter.setFormCode(salSalePrice.getSalePriceCode());
                    list.add(formParameter);
                    submit.setFormParameters(list);
                    workflowService.change(submit);
                }
                //新增行 正常审批
                if(HandleStatus.SUBMIT.getCode().equals(item.getHandleStatus())){
                    Submit submit = new Submit();
                    submit.setStartUserId(ApiThreadLocalUtil.get().getUserid().toString());
                    submit.setFormType(FormType.SalePrice.getCode());
                    List<FormParameter> list = new ArrayList();
                    FormParameter formParameter = new FormParameter();
                    formParameter.setParentId(item.getSalePriceSid().toString());
                    formParameter.setFormId(item.getSalePriceItemSid().toString());
                    formParameter.setFormCode(salSalePrice.getSalePriceCode());
                    list.add(formParameter);
                    submit.setFormParameters(list);
                    workflowService.submitByItem(submit);
                }
            });
        }
        return AjaxResult.success("销售价信息修改成功");
    }

    /**
     * 查询页面更新客户与客供料方式
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCustomer(SalSalePrice salSalePrice) {
        int row = 0;
        if (StrUtil.isBlank(salSalePrice.getRawMaterialMode())) {
            throw new BaseException("“客供料方式”不能为空！");
        }
        if (StrUtil.isBlank(salSalePrice.getSaleMode())) {
            throw new BaseException("“销售模式”不能为空！");
        }
        // 按照“物料sid+客户sid+客供料方式+销售模式+价格维度+SKU1”进行销售价唯一性校验，若已存在对应销售价信息，
        // 则报提示信息：“物料/商品+客户+客供料方式+销售模式+价格维度+SKU1”维度的销售价已存在！
        // 若校验通过，则将弹窗填写的相关字段的值，回写至价格主表【s_sal_sale_price】中且需记录操作日志，
        // 操作日志详情中显示：变更客户 | 客供料方式 | 销售模式，原值：XX | YY | ZZ；新值：XX | YY | ZZ（其中XX显示客户简称），示例如下：
        // 变更客户 | 客供料方式 | 销售模式，原值：QPL | 无/供方全包料 | 常规/买断；新值：JMW | 无/供方全包料 | 常规/买断
        QueryWrapper<SalSalePrice> queryWrapper = new QueryWrapper<>();
        if (salSalePrice.getMaterialSid() != null) {
            queryWrapper.lambda().eq(SalSalePrice::getMaterialSid, salSalePrice.getMaterialSid());
        } else {
            queryWrapper.lambda().isNull(SalSalePrice::getMaterialSid);
        }
        if (salSalePrice.getCustomerSid() != null) {
            queryWrapper.lambda().eq(SalSalePrice::getCustomerSid, salSalePrice.getCustomerSid());
        } else {
            queryWrapper.lambda().isNull(SalSalePrice::getCustomerSid);
        }
        if (salSalePrice.getRawMaterialMode() != null) {
            queryWrapper.lambda().eq(SalSalePrice::getRawMaterialMode, salSalePrice.getRawMaterialMode());
        } else {
            queryWrapper.lambda().isNull(SalSalePrice::getRawMaterialMode);
        }
        if (salSalePrice.getSaleMode() != null) {
            queryWrapper.lambda().eq(SalSalePrice::getSaleMode, salSalePrice.getSaleMode());
        } else {
            queryWrapper.lambda().isNull(SalSalePrice::getSaleMode);
        }
        if (salSalePrice.getPriceDimension() != null) {
            queryWrapper.lambda().eq(SalSalePrice::getPriceDimension, salSalePrice.getPriceDimension());
        } else {
            queryWrapper.lambda().isNull(SalSalePrice::getPriceDimension);
        }
        if (salSalePrice.getSku1Sid() != null) {
            queryWrapper.lambda().eq(SalSalePrice::getSku1Sid, salSalePrice.getSku1Sid());
        } else {
            queryWrapper.lambda().isNull(SalSalePrice::getSku1Sid);
        }
        queryWrapper.lambda().ne(SalSalePrice::getSalePriceSid, salSalePrice.getSalePriceSid());
        List<SalSalePrice> existList = salSalePriceMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(existList)) {
            throw new BaseException("“物料/商品+客户+客供料方式+销售模式+价格维度+SKU1”维度的销售价已存在！");
        }
        // 原来
        SalSalePrice price = salSalePriceMapper.selectById(salSalePrice.getSalePriceSid());
        // 修改
        LambdaUpdateWrapper<SalSalePrice> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(SalSalePrice::getSalePriceSid, salSalePrice.getSalePriceSid());
        updateWrapper.set(SalSalePrice::getCustomerSid, salSalePrice.getCustomerSid());
        updateWrapper.set(SalSalePrice::getRawMaterialMode, salSalePrice.getRawMaterialMode());
        updateWrapper.set(SalSalePrice::getSaleMode, salSalePrice.getSaleMode());
        row = salSalePriceMapper.update(null, updateWrapper);
        if ( row > 0) {
            // 客供料方式
            List<DictData> dictDataList = sysDictDataService.selectDictData("s_raw_material_mode");
            Map<String, String> dictDataMaps = dictDataList.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
            String rawMaterialMode1 = (price.getRawMaterialMode() == null || dictDataMaps.get(price.getRawMaterialMode()) == null)
                    ? "" : dictDataMaps.get(price.getRawMaterialMode());
            String rawMaterialMode2 = (salSalePrice.getRawMaterialMode() == null || dictDataMaps.get(salSalePrice.getRawMaterialMode()) == null)
                    ? "" : dictDataMaps.get(salSalePrice.getRawMaterialMode());
            // 销售模式
            List<DictData> dictDataList2 = sysDictDataService.selectDictData("s_price_type");
            Map<String, String> dictDataMaps2 = dictDataList2.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
            String saleMode1 = (price.getSaleMode() == null || dictDataMaps2.get(price.getSaleMode()) == null)
                    ? "" : dictDataMaps2.get(price.getSaleMode());
            String saleMode2 = (salSalePrice.getSaleMode() == null || dictDataMaps2.get(salSalePrice.getSaleMode()) == null)
                    ? "" : dictDataMaps2.get(salSalePrice.getSaleMode());
            // 客户
            String customerShortName1 = "";
            if (price.getCustomerSid() != null) {
                BasCustomer customer = basCustomerMapper.selectById(price.getCustomerSid());
                if (customer != null) {
                    customerShortName1 = customer.getShortName();
                }
            }
            String customerShortName2 = "";
            if (salSalePrice.getCustomerSid() != null) {
                BasCustomer customer = basCustomerMapper.selectById(salSalePrice.getCustomerSid());
                if (customer != null) {
                    customerShortName2 = customer.getShortName();
                }
            }
            String remark = "变更客户 | 客供料方式 | 销售模式，原值：" + customerShortName1 + " | " + rawMaterialMode1 + " | " + saleMode1 +
                    "；新值：" + customerShortName2 + " | " + rawMaterialMode2 + " | " + saleMode2;
            //插入日志
            MongodbUtil.insertUserLog(salSalePrice.getSalePriceSid(), BusinessType.CHANGE.getValue(), null,TITLE, remark);
        }
        return row;
    }

    /**
     * 查询页面变更有效期
     *
     * @param salSalePriceItem
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult changeItemTime(SaleReportResponse request) {
        SalSalePriceItem salSalePriceItem = new SalSalePriceItem();
        BeanUtil.copyProperties(request, salSalePriceItem);
        if (salSalePriceItem.getSalePriceSid() == null) {
            return AjaxResult.error();
        }
        SalSalePrice price = salSalePriceMapper.selectSalSalePriceById(salSalePriceItem.getSalePriceSid());
        if (price == null) {
            return AjaxResult.error();
        }
        List<SalSalePriceItem> priceItemList = salSalePriceItemMapper.selectList(new QueryWrapper<SalSalePriceItem>()
                .lambda().eq(SalSalePriceItem::getSalePriceSid, salSalePriceItem.getSalePriceSid()));
        if (CollectionUtil.isEmpty(priceItemList)) {
            return AjaxResult.error();
        }
        // 移除原有单，写入更新有效期后的新单
        String oldStart = ""; String oldEnd = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Iterator it = priceItemList.iterator();
        while(it.hasNext()){
            SalSalePriceItem item = (SalSalePriceItem)it.next();
            if (item.getSalePriceItemSid().equals(salSalePriceItem.getSalePriceItemSid())) {
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
        priceItemList.add(salSalePriceItem);
        // 校验本单
        boolean judege = validTime(priceItemList);
        if (judege) {
            //二层校验 校验其它单
            judgeTime(price, salSalePriceItem);
        } else {
            return AjaxResult.error("明细中有效期时间段存在交集，不允许修改");
        }
        // 变更有效期
        LambdaUpdateWrapper<SalSalePriceItem> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SalSalePriceItem::getSalePriceItemSid, salSalePriceItem.getSalePriceItemSid())
                .set(SalSalePriceItem::getStartDate, salSalePriceItem.getStartDate())
                .set(SalSalePriceItem::getEndDate, salSalePriceItem.getEndDate())
                .set(SalSalePriceItem::getRemark, salSalePriceItem.getRemark());
        int row = salSalePriceItemMapper.update(null, updateWrapper);
        // 记录操作日志
        if (row > 0) {
            String newStart = ""; String newEnd = "";
            // 得到新的有效期
            if (salSalePriceItem.getStartDate() != null) {
                newStart = sdf.format(salSalePriceItem.getStartDate());
            }
            if (salSalePriceItem.getEndDate() != null) {
                newEnd = sdf.format(salSalePriceItem.getEndDate());
            }
            String remark = "变更前：有效期" + oldStart + "至" + oldEnd + "；变更后：有效期" + newStart + "至" + newEnd;
            MongodbUtil.insertUserLogItem(salSalePriceItem.getSalePriceSid(),BusinessType.CHANGE.getValue(), TITLE, request.getItemNum(), remark);
            MongodbUtil.insertUserLogItem(salSalePriceItem.getSalePriceItemSid(),BusinessType.CHANGE.getValue(), TITLE, request.getItemNum(), remark);
        }
        return AjaxResult.success(row);
    }

    //新增明细时，新增代办
    public void addTodo(List<SalSalePriceItem> list,SalSalePrice salSalePrice){
        list.forEach(li->{
            if(!HandleStatus.SUBMIT.getCode().equals(li.getHandleStatus())){
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.TABLE_MANUFACTURE_ORDER)
                        .setDocumentItemSid(li.getSalePriceItemSid())
                        .setDocumentSid(li.getSalePriceSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("销售价" + salSalePrice.getSalePriceCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(salSalePrice.getSalePriceCode())
                            .setNoticeDate(new Date())
                            .setMenuId(ConstantsWorkbench.sal_sale_price)
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            }
        });
    }
    /**
     * 明细赋值主表sid
     */
    public void setPriceSid(Long id,List<SalSalePriceItem> list ){
            list.forEach(li->{
                li.setSalePriceSid(id);
            });
    }
    /**
     * 删除销售价信息信息
     * @param salSalePrice 销售价信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteSalSalePriceById(List<Long> ids) {
        //保存 1
        String[] handeleStatus = {HandleStatus.SAVE.getCode(),HandleStatus.RETURNED.getCode()};
        QueryWrapper<SalSalePrice> wrapper = new QueryWrapper<>();
        wrapper.in("handle_status",handeleStatus)
                .in("sale_price_sid",ids);
        Integer size=salSalePriceMapper.selectCount(wrapper);
        if (size == ids.size()) {
            //删除主表
            int count=salSalePriceMapper.deleteBatchIds(ids);
            //删除明细表
            QueryWrapper<SalSalePriceItem> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("sale_price_sid",ids);
            salSalePriceItemMapper.delete(queryWrapper);
            //删除附件表
            QueryWrapper<SalSalePriceAttachment> queryWrapperAttachment = new QueryWrapper<>();
            queryWrapperAttachment.in("sale_price_sid",ids);
            salSalePriceAttachmentMapper.delete(queryWrapperAttachment);
            ids.forEach(li->{
                SalSalePrice price = new SalSalePrice();
                price.setSalePriceSid(li);
                //校验是否存在待办
                checkTodoExist(price);
                MongodbUtil.insertUserLog(li,BusinessType.DELETE.getValue(), TITLE);
            });
            return AjaxResult.success("删除成功，共删除"+count+"条数据");
        } else {
            return AjaxResult.error("仅保存状态下才可删除");
        }
    }

    /**
     * 删除销售价信息信息
     * @param salSalePrice 销售价信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteItem(List<Long> ids) {
        List<SalSalePriceItem> salSalePriceItems = salSalePriceItemMapper.selectList(new QueryWrapper<SalSalePriceItem>().lambda()
                .in(SalSalePriceItem::getSalePriceItemSid, ids)
        );
        salSalePriceItems.forEach(item->{
            MongodbUtil.insertUserLogItem(item.getSalePriceSid(), BusinessType.DELETE.getValue(),TITLE,item.getItemNum());
        });
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentItemSid, ids));

        List<Long> longs = salSalePriceItems.stream().map(li -> li.getSalePriceSid()).collect(Collectors.toList());
        salSalePriceItemMapper.deleteBatchIds(ids);
        longs.forEach(item->{
            List<SalSalePriceItem> items = salSalePriceItemMapper.selectList(new QueryWrapper<SalSalePriceItem>().lambda()
                    .eq(SalSalePriceItem::getSalePriceSid, item)
            );
            if(CollectionUtil.isEmpty(items)){
                //明细为空时，删除对应的主表
                salSalePriceMapper.deleteById(item);
                SalSalePrice salSalePrice = new SalSalePrice();
                salSalePrice.setSalePriceSid(item);
                //校验是否存在待办
                checkTodoExist(salSalePrice);
            }
        });
        return 1;
    }
    /**
     * 批量 修改处理状态（确认）
     *
     * @param salePriceActionRequest
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult handleStatusConfirm(SalePriceActionRequest salePriceActionRequest){
        Long[] ids = salePriceActionRequest.getSalePriceSid();
        for (Long id : ids) {
            SalSalePrice salSalePrice = selectSalSalePriceById(id);
            //插入日志
            MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), TITLE);
            //校验是否存在待办
            checkTodoExist(salSalePrice);
            JudgeNull(salSalePrice);
            salSalePrice.setHandleStatus(ConstantsEms.CHECK_STATUS);
            List<SalSalePriceItem> list = salSalePrice.getListSalSalePriceItem();
            if(CollectionUtils.isNotEmpty(list)){
                list.forEach(li->{
                    //二层校验
                    judgeTime(salSalePrice,li);
                });
            }
        }
        UpdateWrapper<SalSalePrice> UpdateWrapper = new UpdateWrapper<>();
        UpdateWrapper.in("sale_price_sid",ids)
                .set("handle_status",salePriceActionRequest.getHandleStatus())
                .set("confirm_date",new Date())
                .set("confirmer_account", ApiThreadLocalUtil.get().getUsername());
        SalSalePrice salSalePrice = new SalSalePrice();
        int count=salSalePriceMapper.update(salSalePrice,UpdateWrapper);
            return AjaxResult.success("确认成功，确认"+count+"条销售价信息");
    }
    /**
     * 提交时校验
     */
    @Override
    public int processCheck(List<Long> salSalePriceSids){
        salSalePriceSids.forEach(salesOrderSid->{
            SalSalePrice salSalePrice = selectSalSalePriceById(salesOrderSid);
            List<SalSalePrice>  listSale=null;
            Long sku1Sid = salSalePrice.getSku1Sid();
            Boolean exitSku=salSalePrice.getSku1Sid()!=null?true:false;
            Long customerSid = salSalePrice.getCustomerSid();
            if(customerSid==null){
                listSale= salSalePriceMapper.selectList(new QueryWrapper<SalSalePrice>().lambda()
                        .isNull(SalSalePrice::getCustomerSid)
                        .eq(SalSalePrice::getMaterialSid, salSalePrice.getMaterialSid())
                        .eq(SalSalePrice::getSaleMode,salSalePrice.getSaleMode())
                        .eq(SalSalePrice::getRawMaterialMode, salSalePrice.getRawMaterialMode())
                        .eq(exitSku,SalSalePrice::getSku1Sid,salSalePrice.getSku1Sid()));
            }else{
                listSale= salSalePriceMapper.selectList(new QueryWrapper<SalSalePrice>().lambda()
                        .eq(SalSalePrice::getCustomerSid, salSalePrice.getCustomerSid())
                        .eq(SalSalePrice::getMaterialSid, salSalePrice.getMaterialSid())
                        .eq(SalSalePrice::getSaleMode,salSalePrice.getSaleMode())
                        .eq(SalSalePrice::getRawMaterialMode, salSalePrice.getRawMaterialMode())
                        .eq(exitSku,SalSalePrice::getSku1Sid,salSalePrice.getSku1Sid()));
            }
            listSale=listSale.stream().filter(li->!li.getSalePriceSid().toString().equals(salSalePrice.getSalePriceSid().toString())).collect(Collectors.toList());
            if(exitSku){
                if(CollectionUtils.isNotEmpty(listSale)){
                    Long salePriceSid = listSale.get(0).getSalePriceSid();
                    throw  new  CustomException("销售价"+salSalePrice.getSalePriceCode()+"与单号"+listSale.get(0).getSalePriceCode()+"维度相同，不允许提交");
                }
            }else{
                if(CollectionUtils.isNotEmpty(listSale)){
                    //查找是否存在sku1为null的情况
                    SalSalePrice salSalePriceExisted = listSale.stream().filter(o -> o.getSku1Sid() == null).findFirst().orElse(null);
                    if(salSalePriceExisted!=null){
                        throw  new  CustomException("销售价"+salSalePrice.getSalePriceCode()+"与单号"+salSalePriceExisted.getSalePriceCode()+"维度相同，不允许提交");
                    }
                }
            }
            salSalePrice.setHandleStatus(ConstantsEms.CHECK_STATUS);
            List<SalSalePriceItem> list = salSalePrice.getListSalSalePriceItem();
            if(CollectionUtils.isNotEmpty(list)){
                list.forEach(li->{
                    if(li.getSalePriceTax()==null||li.getTaxRate()==null){
                        throw new CustomException("存在销售价或税率未维护的明细行，请填写后再确认");
                    }
                });
            }else{
                throw new CustomException("提交时明细行不允许为空");
            }
            //按款
            if(ConstantsEms.PRICE_K.equals(salSalePrice.getPriceDimension())){
                List<CosProductCost> cosProductCosts = cosProductCostMapper.selectList(new QueryWrapper<CosProductCost>().lambda()
                        .eq(CosProductCost::getRawMaterialMode, salSalePrice.getRawMaterialMode())
                        .eq(CosProductCost::getMaterialSid, salSalePrice.getMaterialSid())
                        .eq(CosProductCost::getCustomerSid, salSalePrice.getCustomerSid())
                        .eq(CosProductCost::getBusinessMode, salSalePrice.getSaleMode())
                        .in(CosProductCost::getHandleStatus, new String[]{HandleStatus.SUBMIT.getCode(), HandleStatus.CHANGEAPPROVAL.getCode()})
                );
                if(CollectionUtil.isNotEmpty(cosProductCosts)){
                    throw new CustomException(salSalePrice.getSalePriceCode()+salSalePrice.getMaterialName()+"存在相应的审批中的销售成本核算信息，请先处理此销售成本核算信息");
                }
                salSalePrice.setHandleStatuses(new String[]{HandleStatus.SUBMIT.getCode(), HandleStatus.CHANGEAPPROVAL.getCode()})
                        .setPriceDimension(null);
                List<SalSalePrice> costList = salSalePriceMapper.getCostList(salSalePrice);
                costList=costList.stream().filter(li->!li.getSalePriceSid().toString().equals(salSalePrice.getSalePriceSid().toString())).collect(Collectors.toList());
                if(CollectionUtil.isNotEmpty(costList)){
                    String salePriceCode = costList.get(0).getSalePriceCode();
                    throw new CustomException(salSalePrice.getSalePriceCode()+salSalePrice.getMaterialName()+"存在相应的审批中的销售价"+salePriceCode+"，请先处理此销售价信息");
                }
            }else{
                List<CosProductCost> cosProductCosts = cosProductCostMapper.selectList(new QueryWrapper<CosProductCost>().lambda()
                        .eq(CosProductCost::getRawMaterialMode, salSalePrice.getRawMaterialMode())
                        .eq(CosProductCost::getMaterialSid, salSalePrice.getMaterialSid())
                        .eq(CosProductCost::getCustomerSid, salSalePrice.getCustomerSid())
                        .eq(CosProductCost::getPriceDimension,ConstantsEms.PRICE_K)
                        .eq(CosProductCost::getBusinessMode, salSalePrice.getSaleMode())
                        .in(CosProductCost::getHandleStatus, new String[]{HandleStatus.SUBMIT.getCode(), HandleStatus.CHANGEAPPROVAL.getCode()})
                );
                if(CollectionUtil.isNotEmpty(cosProductCosts)){
                    throw new CustomException(salSalePrice.getSalePriceCode()+salSalePrice.getMaterialName()+"存在相应的审批中的销售成本核算信息，请先处理此销售成本核算信息");
                }
                salSalePrice.setHandleStatuses(new String[]{HandleStatus.SUBMIT.getCode(), HandleStatus.CHANGEAPPROVAL.getCode()})
                        .setPriceDimension(ConstantsEms.PRICE_K)
                        .setSku1Sid(null);
                List<SalSalePrice> costList = salSalePriceMapper.getCostList(salSalePrice);
                costList=costList.stream().filter(li->!li.getSalePriceSid().toString().equals(salSalePrice.getSalePriceSid().toString())).collect(Collectors.toList());
                if(CollectionUtil.isNotEmpty(costList)){
                    String salePriceCode = costList.get(0).getSalePriceCode();
                    throw new CustomException(salSalePrice.getSalePriceCode()+salSalePrice.getMaterialName()+"存在相应的审批中的销售价"+salePriceCode+"，请先处理此销售价信息");
                }
                List<CosProductCost> cosProductCostsK1 = cosProductCostMapper.selectList(new QueryWrapper<CosProductCost>().lambda()
                        .eq(CosProductCost::getRawMaterialMode, salSalePrice.getRawMaterialMode())
                        .eq(CosProductCost::getMaterialSid, salSalePrice.getMaterialSid())
                        .eq(CosProductCost::getCustomerSid, salSalePrice.getCustomerSid())
                        .eq(CosProductCost::getSku1Sid,sku1Sid)
                        .eq(CosProductCost::getBusinessMode, salSalePrice.getSaleMode())
                        .in(CosProductCost::getHandleStatus, new String[]{HandleStatus.SUBMIT.getCode(), HandleStatus.CHANGEAPPROVAL.getCode()})
                );
                if(CollectionUtil.isNotEmpty(cosProductCostsK1)){
                    throw new CustomException(salSalePrice.getSalePriceCode()+salSalePrice.getMaterialName()+"存在相应的审批中的销售成本核算信息，请先处理此销售成本核算信息");
                }
                salSalePrice.setHandleStatuses(new String[]{HandleStatus.SUBMIT.getCode(), HandleStatus.CHANGEAPPROVAL.getCode()})
                        .setSku1Sid(sku1Sid)
                        .setPriceDimension(ConstantsEms.PRICE_K1);
                List<SalSalePrice> costListK1 = salSalePriceMapper.getCostList(salSalePrice);
                costListK1=costListK1.stream().filter(li->!li.getSalePriceSid().toString().equals(salSalePrice.getSalePriceSid().toString())).collect(Collectors.toList());
                if(CollectionUtil.isNotEmpty(costListK1)){
                    String salePriceCode = costListK1.get(0).getSalePriceCode();
                    throw new CustomException(salSalePrice.getSalePriceCode()+salSalePrice.getMaterialName()+"存在相应的审批中的销售价"+salePriceCode+"，请先处理此销售价信息");
                }
            }
            if(CollectionUtils.isNotEmpty(list)){
                list.forEach(li->{
                    //二层校验
                    judgeTime(salSalePrice,li);
                });
            }
        });
        return 1;
    }
    /**
     * 批量 启用 /停用
     *
     * @param salePriceActionRequest
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult status(SalePriceActionRequest salePriceActionRequest) {
        //确认 5
        String confirmHandleStatus=ConstantsEms.CHECK_STATUS;
        Long[] ids = salePriceActionRequest.getSalePriceSid();
        QueryWrapper<SalSalePrice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("handle_status",confirmHandleStatus)
                .in("sale_price_sid",ids);
        Integer selectCount = salSalePriceMapper.selectCount(queryWrapper);
        if(selectCount==ids.length){
            UpdateWrapper<SalSalePrice> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("status",salePriceActionRequest.getStatus())
                    .in("sale_price_sid",ids);
            SalSalePrice salSalePrice = new SalSalePrice();
            int count=salSalePriceMapper.update(salSalePrice,updateWrapper);
            for (Long id : ids) {
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=salSalePriceMapper.selectById(id).getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
            return AjaxResult.success(count);
        }else{
            return AjaxResult.error("仅确认状态下，才可启用/停用");
        }

    }


    //第二层校验
    @Override
    public void judgeTime(SalSalePrice salSalePrice,SalSalePriceItem salSalePriceItem){
        String handleStatus = salSalePrice.getHandleStatus();
            //获取 按款-按色
            List<SalSalePriceItem> salSalePriceAll = getSalePriceAll(salSalePrice);
            if(CollectionUtils.isNotEmpty(salSalePriceAll)&&salSalePriceItem.getSalePriceSid()!=null){
                    salSalePriceAll=salSalePriceAll.stream().filter(li->!li.getSalePriceSid().toString().equals(salSalePriceItem.getSalePriceSid().toString())).collect(Collectors.toList());
            }
            if(CollectionUtils.isNotEmpty(salSalePriceAll)){
                List<SalSalePriceItem> salSalePriceAllItems = new ArrayList<>();
                salSalePriceAllItems.addAll(salSalePriceAll);
                //校验有效期是否存在交集
                boolean judge = validTimeOther(salSalePriceItem,salSalePriceAllItems);
                if(!judge){
                    throw new CustomException("物料编码"+salSalePrice.getMaterialCode()+"，当前已生效销售价的有效期与此销售价的有效期区间存在交集，请检查！");
                }
            }
    }

    public Boolean validTimeOther(SalSalePriceItem salSalePriceItem,List<SalSalePriceItem> salSalePriceItems){
        for (int i=0;i<salSalePriceItems.size();i++){
            long start = salSalePriceItem.getStartDate().getTime();
            long end = salSalePriceItem.getEndDate().getTime();
            if(start>=salSalePriceItems.get(i).getStartDate().getTime()&&start<=salSalePriceItems.get(i).getEndDate().getTime()){
                return false;
            }
            if(end>=salSalePriceItems.get(i).getStartDate().getTime()&&end<=salSalePriceItems.get(i).getEndDate().getTime()){
                return false;
            }
            if(start<salSalePriceItems.get(i).getStartDate().getTime()&&end>salSalePriceItems.get(i).getEndDate().getTime()){
                return false;
            }
        }
        return true;
    }

    /**
     * 销售价信息变更
     *
     * @param salSalePriceRequest
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult change(SalSalePrice salSalePrice) {
        AddCheck(salSalePrice);
        Long id=salSalePrice.getSalePriceSid();
        //确认 5
        String confirmHandleStatus=ConstantsEms.CHECK_STATUS;
        String nowHandleStatus=salSalePriceMapper.selectById(id).getHandleStatus();
        if(confirmHandleStatus.equals(nowHandleStatus)) {
            //修改主表信息
            int row=salSalePriceMapper.updateAllById(salSalePrice);
            SalSalePrice response = salSalePriceMapper.selectSalSalePriceById(salSalePrice.getSalePriceSid());
            if(row>0){
                //插入日志
                MongodbUtil.insertUserLog(salSalePrice.getSalePriceSid(), BusinessType.CHANGE.getValue(), response,salSalePrice,TITLE);
            }
            List<SalSalePriceItem> listSalSalePriceItem = salSalePrice.getListSalSalePriceItem();
            //不含税值计算
            changePrice(listSalSalePriceItem);
            if(CollectionUtils.isNotEmpty(listSalSalePriceItem)) {
                setPriceSid(id,listSalSalePriceItem);
                boolean judege = validTime(listSalSalePriceItem);
                if(judege==true){
                    listSalSalePriceItem.forEach(m -> {
                        //二层校验
                        judgeTime(salSalePrice,m);
                    });
                }else{
                    return AjaxResult.error("明细中有效期时间段存在交集，不允许变更");
                }
            }
            //删除原有明细表信息
            QueryWrapper<SalSalePriceItem> wrapper = new QueryWrapper<>();
            wrapper.eq("sale_price_sid",salSalePrice.getSalePriceSid());
            salSalePriceItemMapper.delete(wrapper);
            if(CollectionUtils.isNotEmpty(listSalSalePriceItem)){
                listSalSalePriceItem.forEach(li->{
                    li.setSalePriceSid(id);
                    salSalePriceItemMapper.insert(li);
                });
            }
            //删除原有附件表信息
            QueryWrapper<SalSalePriceAttachment> wrapperAttachment = new QueryWrapper<>();
            wrapperAttachment.eq("sale_price_sid",salSalePrice.getSalePriceSid());
            salSalePriceAttachmentMapper.delete(wrapperAttachment);
            List<SalSalePriceAttachment> listSalSalePriceAttachment = salSalePrice.getAttachmentList();
            if(CollectionUtils.isNotEmpty(listSalSalePriceAttachment)){
                listSalSalePriceAttachment.forEach(o->{
                    //插入目前存在的表
                    o.setSalePriceSid(id);
                    salSalePriceAttachmentMapper.insert(o);
                });}
            //更新通知
            if (ConstantsEms.CHECK_STATUS.equals(salSalePrice.getHandleStatus())) {
                SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
                sysBusinessBcst.setTitle("商品编码"+salSalePrice.getMaterialCode()+"，销售价编号"+salSalePrice.getSalePriceCode()+"的信息发生变更，请知悉！")
                        .setDocumentSid(salSalePrice.getSalePriceSid())
                        .setDocumentCode(salSalePrice.getSalePriceCode())
                        .setNoticeDate(new Date()).setUserId(ApiThreadLocalUtil.get().getUserid());
                sysBusinessBcstMapper.insert(sysBusinessBcst);
            }
            return AjaxResult.success("销售价信息变更成功");
        }else{
            return AjaxResult.error("仅确认状态下才可变更");
        }
    }

    @Override
    public BasMaterial getMaterialSkus(BasMaterial material){
        //根据code获取商品
        BasMaterial newMaterial= basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                .eq(BasMaterial::getMaterialCode, material.getMaterialCode()));
        if(newMaterial==null){
            throw  new CustomException("输入的商品编码不存在");
        }
        List<BasMaterialSku> basMaterialSkus = basMaterialSkuMapper.getskuList(Long.valueOf(newMaterial.getMaterialSid())).stream()
                .filter(o->o.getSkuType().equals(ConstantsEms.SKUTYP_YS)&&o.getStatus().equals(ConstantsEms.SAVA_STATUS)).collect(Collectors.toList());
        newMaterial.setBasMaterialSkuList(basMaterialSkus);
        return newMaterial;
    }

    /**
     * 获取销售价
     */
    @Override
    public SalSalePriceItem getSalePrice(SalSalePrice salSalePrice) {
        String[] handleStatus=salSalePrice.getNotApprovalStatus()!=null?salSalePrice.getNotApprovalStatus():new String[]{ConstantsEms.CHECK_STATUS};
        SalSalePrice result = null;
        //按：客户、销售模式、客供料方式、商品/物料sid、SKU1sid查
        result = salSalePriceMapper.selectOne(new QueryWrapper<SalSalePrice>().lambda()
                                   .eq(SalSalePrice::getCustomerSid, salSalePrice.getCustomerSid())
                                   .eq(SalSalePrice::getSaleMode, salSalePrice.getSaleMode())
                                   .eq(SalSalePrice::getRawMaterialMode, salSalePrice.getRawMaterialMode())
                                   .eq(SalSalePrice::getMaterialSid, salSalePrice.getMaterialSid())
                                   .eq(SalSalePrice::getSku1Sid, salSalePrice.getSku1Sid()));

        if (result != null){
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String now = sdf.format(date);
            //直接截取到日
            Date nowDate=DateUtils.parseDate(now);
            SalSalePriceItem salSalePriceItem =salSalePriceItemMapper.selectOne(new QueryWrapper<SalSalePriceItem>().lambda()
                    .le(SalSalePriceItem::getStartDate, nowDate)
                    .ge(SalSalePriceItem::getEndDate, nowDate)
                    .in(SalSalePriceItem::getHandleStatus,handleStatus)
                    .eq(SalSalePriceItem::getSalePriceSid, result.getSalePriceSid()));
            if(salSalePriceItem!=null){
                salSalePriceItemMapper.updateRe(salSalePriceItem);
                return salSalePriceItem;
            }else{
                result=null;
            }
        }
        if (result == null){
            //按：客户、公司、销售模式、客供料方式、商品/物料sid查
            result = salSalePriceMapper.selectOne(new QueryWrapper<SalSalePrice>().lambda()
                                       .eq(SalSalePrice::getCustomerSid, salSalePrice.getCustomerSid())
                                       .eq(SalSalePrice::getSaleMode, salSalePrice.getSaleMode())
                                       .eq(SalSalePrice::getRawMaterialMode, salSalePrice.getRawMaterialMode())
                                       .eq(SalSalePrice::getMaterialSid, salSalePrice.getMaterialSid())
                                        .isNull(SalSalePrice::getSku1Sid));
            if (result != null){
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String now = sdf.format(date);
                //直接截取到日
                Date nowDate=DateUtils.parseDate(now);
                SalSalePriceItem salSalePriceItem =salSalePriceItemMapper.selectOne(new QueryWrapper<SalSalePriceItem>().lambda()
                        .le(SalSalePriceItem::getStartDate,nowDate)
                        .in(SalSalePriceItem::getHandleStatus,handleStatus)
                        .ge(SalSalePriceItem::getEndDate, nowDate)
                        .eq(SalSalePriceItem::getSalePriceSid, result.getSalePriceSid()));
                if(salSalePriceItem!=null){
                    salSalePriceItemMapper.updateRe(salSalePriceItem);
                    return salSalePriceItem;
                }else{
                    result=null;
                }
            }
        }
        if (result == null){
            //按：销售模式、客供料方式、商品/物料sid、SKU1sid查
            result = salSalePriceMapper.selectOne(new QueryWrapper<SalSalePrice>().lambda()
                                       .eq(SalSalePrice::getSaleMode, salSalePrice.getSaleMode())
                                       .eq(SalSalePrice::getRawMaterialMode, salSalePrice.getRawMaterialMode())
                                       .eq(SalSalePrice::getMaterialSid, salSalePrice.getMaterialSid())
                                       .eq(SalSalePrice::getSku1Sid, salSalePrice.getSku1Sid())
                                        .isNull(SalSalePrice::getCustomerSid));
            if (result != null){
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String now = sdf.format(date);
                //直接截取到日
                Date nowDate=DateUtils.parseDate(now);
                SalSalePriceItem salSalePriceItem =salSalePriceItemMapper.selectOne(new QueryWrapper<SalSalePriceItem>().lambda()
                        .le(SalSalePriceItem::getStartDate, nowDate)
                        .in(SalSalePriceItem::getHandleStatus,handleStatus)
                        .ge(SalSalePriceItem::getEndDate, nowDate)
                        .eq(SalSalePriceItem::getSalePriceSid, result.getSalePriceSid()));
                if(salSalePriceItem!=null){
                    salSalePriceItemMapper.updateRe(salSalePriceItem);
                    return salSalePriceItem;
                }else{
                    result=null;
                }
            }
        }
        if (result == null){
            //按：销售模式、客供料方式、商品/物料sid查
            result = salSalePriceMapper.selectOne(new QueryWrapper<SalSalePrice>().lambda()
                                       .eq(SalSalePrice::getSaleMode, salSalePrice.getSaleMode())
                                       .eq(SalSalePrice::getRawMaterialMode, salSalePrice.getRawMaterialMode())
                                       .eq(SalSalePrice::getMaterialSid, salSalePrice.getMaterialSid())
                                        .isNull(SalSalePrice::getSku1Sid)
                                        .isNull(SalSalePrice::getCustomerSid));
            if (result != null){
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String now = sdf.format(date);
                //直接截取到日
                Date nowDate=DateUtils.parseDate(now);
                SalSalePriceItem salSalePriceItem =salSalePriceItemMapper.selectOne(new QueryWrapper<SalSalePriceItem>().lambda()
                        .le(SalSalePriceItem::getStartDate, nowDate)
                        .ge(SalSalePriceItem::getEndDate, nowDate)
                        .in(SalSalePriceItem::getHandleStatus,handleStatus)
                        .eq(SalSalePriceItem::getSalePriceSid, result.getSalePriceSid()));
                if(salSalePriceItem!=null){
                    salSalePriceItemMapper.updateRe(salSalePriceItem);
                    return salSalePriceItem;
                }
            }
        }
        return null;
    }

    /**
     * 获取销售价(有效期验证)
     */
    public  List<SalSalePriceItem>  getSalePriceAll(SalSalePrice salSalePrice) {
        List<SalSalePrice> result = null;
        String priceDimension = salSalePrice.getPriceDimension();
        if(ConstantsEms.PRICE_K.equals(priceDimension)){
            result = salSalePriceMapper.selectList(new QueryWrapper<SalSalePrice>().lambda()
                    .eq(SalSalePrice::getCustomerSid, salSalePrice.getCustomerSid())
                    .eq(SalSalePrice::getSaleMode, salSalePrice.getSaleMode())
                    .eq(SalSalePrice::getRawMaterialMode, salSalePrice.getRawMaterialMode())
                    .eq(SalSalePrice::getMaterialSid, salSalePrice.getMaterialSid()));
            if(CollectionUtil.isEmpty(result)&&salSalePrice.getCustomerSid()==null){
                result = salSalePriceMapper.selectList(new QueryWrapper<SalSalePrice>().lambda()
                        .isNull(SalSalePrice::getCustomerSid)
                        .eq(SalSalePrice::getSaleMode, salSalePrice.getSaleMode())
                        .eq(SalSalePrice::getRawMaterialMode, salSalePrice.getRawMaterialMode())
                        .eq(SalSalePrice::getMaterialSid, salSalePrice.getMaterialSid()));
            }
        }else{
            result = salSalePriceMapper.selectList(new QueryWrapper<SalSalePrice>().lambda()
                    .eq(SalSalePrice::getCustomerSid, salSalePrice.getCustomerSid())
                    .eq(SalSalePrice::getSaleMode, salSalePrice.getSaleMode())
                    .eq(SalSalePrice::getRawMaterialMode, salSalePrice.getRawMaterialMode())
                    .eq(SalSalePrice::getPriceDimension,ConstantsEms.PRICE_K)
                    .eq(SalSalePrice::getMaterialSid, salSalePrice.getMaterialSid()));
            if(CollectionUtil.isEmpty(result)&&salSalePrice.getCustomerSid()==null){
                result = salSalePriceMapper.selectList(new QueryWrapper<SalSalePrice>().lambda()
                        .isNull(SalSalePrice::getCustomerSid)
                        .eq(SalSalePrice::getSaleMode, salSalePrice.getSaleMode())
                        .eq(SalSalePrice::getRawMaterialMode, salSalePrice.getRawMaterialMode())
                        .eq(SalSalePrice::getPriceDimension,ConstantsEms.PRICE_K)
                        .eq(SalSalePrice::getMaterialSid, salSalePrice.getMaterialSid()));
            }
        }

        if (CollectionUtil.isNotEmpty(result)){
            List<Long> sids = result.stream().map(item -> item.getSalePriceSid()).collect(Collectors.toList());
            List<SalSalePriceItem> salSalePriceItem =salSalePriceItemMapper.selectList(new QueryWrapper<SalSalePriceItem>().lambda()
                    .in(SalSalePriceItem::getSalePriceSid, sids)
                    .notIn(SalSalePriceItem::getHandleStatus, new String[]{HandleStatus.SAVE.getCode(), HandleStatus.RETURNED.getCode()}));
            return salSalePriceItem;
        }
        return null;
    }
    //获取组合拉链销售价
    @Override
    public SalSalePriceItem zipperPriceZH(SalSalePrice salSalePrice){
        Long materialSid = salSalePrice.getMaterialSid();
        TecBomHead zipper = iTecBomHeadService.getZipper(materialSid);
        BigDecimal purchaseZippe=null;
        SalSalePriceItem salItem=new SalSalePriceItem();
        List<TecBomItem> itemList = zipper.getItemList();
        List<BigDecimal> prices = new ArrayList<>();
        List<BigDecimal> taxs = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(itemList)){
            itemList.forEach(li->{
                String zipperFlag = li.getZipperFlag();
                salSalePrice.setMaterialSid(li.getBomMaterialSid());
                //获取组件清单中的销售价
                SalSalePriceItem item = getSalePrice(salSalePrice);
                BigDecimal price=null;
                if(item!=null) {
                    taxs.add(item.getTaxRate());
                    price=item.getSalePriceTax();
                }
                if(price!=null){
                    prices.add(price);
                }
                if(ConstantsEms.ZIPPER_LP.equals(zipperFlag)){
                    BeanCopyUtils.copyProperties(item,salItem);
                }
            });
            if(prices.size()>0){
                if(prices.size()==itemList.size()){
                    //求和
                    BigDecimal totalPrice = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                    purchaseZippe=totalPrice.divide(BigDecimal.ONE,5,BigDecimal.ROUND_UP);
                }else{
                    return null;
                }
            }
            if(CollectionUtil.isNotEmpty(taxs)){
                salItem.setTaxRate(taxs.get(0));
            }
            salItem.setSalePriceTax(purchaseZippe);
            return salItem;
        }
        return salItem;
    }

    //获取销售价 最新
    @Override
    public  SalSalePriceItem getNewSalePrice(SalSalePrice salSalePrice){
        SalSalePriceItem item=null;
        Long materialSid = salSalePrice.getMaterialSid();
        String zipperFlag = basMaterialMapper.selectById(materialSid).getZipperFlag();
        if(ConstantsEms.ZIPPER_ZH.equals(zipperFlag)){
            item = zipperPriceZH(salSalePrice);
        }else{
            item = zipperPriceZT(salSalePrice,null);
        }
        //默认获取通用税率
        ConTaxRate taxRate = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>().lambda()
                .eq(ConTaxRate::getIsDefault, "Y")
        );
        if(item==null){
            item=new SalSalePriceItem();
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

    //获取整条拉链销售价
    public SalSalePriceItem zipperPriceZT(SalSalePrice salSalePrice,SalSalePriceItem item){
        //获取整条拉链销售价
        SalSalePriceItem salSalePriceItem = item!=null?item:getSalePrice(salSalePrice);
        BigDecimal price=null;
        if(salSalePriceItem!=null){
            String isRecursionPrice = salSalePriceItem.getIsRecursionPrice();
            if(ConstantsEms.YES.equals(isRecursionPrice)){
                //最小起算量
                BigDecimal priceMinQuantity = salSalePriceItem.getPriceMinQuantity();
                //取整方式
                String roundingType = salSalePriceItem.getRoundingType();
                //获取链胚长度->等于整合拉链sku2的长度
                Long sku2Sid = salSalePrice.getSku2Sid();
                BasSku basSku = basSkuMapper.selectById(sku2Sid);
                BigDecimal lenth =(basSku.getSkuNumeralValue());
                if(priceMinQuantity!=null){
                    if(lenth.subtract(priceMinQuantity).compareTo(BigDecimal.ZERO)==-1){
                        lenth=priceMinQuantity;
                    }
                }
                //差异量
                BigDecimal diver = lenth.subtract(salSalePriceItem.getReferQuantity());
                if(diver.compareTo(new BigDecimal(0))==1){
                    //递增
                    BigDecimal value = getVale(diver.abs(), salSalePriceItem.getIncreQuantity(), roundingType);
                    price=salSalePriceItem.getSalePriceTax().add(value.multiply(salSalePriceItem.getIncrePriceTax()));
                }else if(diver.compareTo(new BigDecimal(0))==-1){
                    //递减少
                    BigDecimal value = getVale(diver.abs(),salSalePriceItem.getDecreQuantity(), roundingType);
                    price=salSalePriceItem.getSalePriceTax().subtract(value.multiply(salSalePriceItem.getDecrePriceTax()));
                }else{
                    //相等
                    price=salSalePriceItem.getSalePriceTax();
                }
            }else{
                price=salSalePriceItem.getSalePriceTax();
            }
            salSalePriceItem.setSalePriceTax(price)
                    .setSalePrice(price.divide(BigDecimal.ONE.add(salSalePriceItem.getTaxRate()),6,BigDecimal.ROUND_HALF_DOWN));
        }
        return salSalePriceItem;
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
     * 根据订单中的“编码+客供料方式+销售模式”在销售价档案中获取销售价的处理状态为“审批中/已确认/变更审批中”且有效期（至）>=当前日期的销售价数据
     * 若查找到多笔数据，则选择有效期（至）距离当前日期最近的价格
     */
    @Override
    public SalSalePriceItem getNearSalePrice(SalSalePrice salSalePrice) {
        SalSalePriceItem item = null;
        Long materialSid = salSalePrice.getMaterialSid();
        String zipperFlag = basMaterialMapper.selectById(materialSid).getZipperFlag();
        // 组合拉链
        if(ConstantsEms.ZIPPER_ZH.equals(zipperFlag)){
            TecBomHead bom = iTecBomHeadService.getZipper(materialSid);
            BigDecimal purchaseZippe=null;
            SalSalePriceItem salItem = new SalSalePriceItem();
            List<TecBomItem> itemList = bom.getItemList();
            List<BigDecimal> prices = new ArrayList<>();
            List<BigDecimal> taxs = new ArrayList<>();
            // 这里加个标识判断有没有提前返回
            boolean flag = true;
            if(CollectionUtils.isNotEmpty(itemList)){
                for (TecBomItem li : itemList) {
                    String zipper = li.getZipperFlag();
                    salSalePrice.setMaterialSid(li.getBomMaterialSid());
                    //获取组件清单中的销售价
                    item = getPrice(salSalePrice);
                    BigDecimal price=null;
                    if(item!=null) {
                        taxs.add(item.getTaxRate());
                        price=item.getSalePriceTax();
                    }
                    if(price!=null){
                        prices.add(price);
                    }
                    if(ConstantsEms.ZIPPER_LP.equals(zipper)){
                        BeanCopyUtils.copyProperties(item,salItem);
                    }
                }
                if(prices.size()>0){
                    if(prices.size()==itemList.size()){
                        //求和
                        BigDecimal totalPrice = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                        purchaseZippe=totalPrice.divide(BigDecimal.ONE,5,BigDecimal.ROUND_UP);
                    }else{
                        flag = false;
                    }
                }
                if (flag) {
                    if(CollectionUtil.isNotEmpty(taxs)){
                        salItem.setTaxRate(taxs.get(0));
                    }
                    salItem.setSalePriceTax(purchaseZippe);
                    item = salItem;
                }
            }
            else {
                item = salItem;
            }
        }
        else{
            //获取整条拉链销售价
            SalSalePriceItem salSalePriceItem = item != null ? item : getPrice(salSalePrice);
            BigDecimal price = null;
            if(salSalePriceItem != null){
                String isRecursionPrice = salSalePriceItem.getIsRecursionPrice();
                if(ConstantsEms.YES.equals(isRecursionPrice)){
                    //最小起算量
                    BigDecimal priceMinQuantity = salSalePriceItem.getPriceMinQuantity();
                    //取整方式
                    String roundingType = salSalePriceItem.getRoundingType();
                    //获取链胚长度->等于整合拉链sku2的长度
                    Long sku2Sid = salSalePrice.getSku2Sid();
                    BasSku basSku = basSkuMapper.selectById(sku2Sid);
                    BigDecimal lenth = basSku.getSkuNumeralValue();
                    if(priceMinQuantity != null){
                        if(lenth.subtract(priceMinQuantity).compareTo(BigDecimal.ZERO) == -1){
                            lenth = priceMinQuantity;
                        }
                    }
                    //差异量
                    BigDecimal diver = lenth.subtract(salSalePriceItem.getReferQuantity());
                    if(diver.compareTo(BigDecimal.ZERO) == 1){
                        //递增
                        BigDecimal value = getVale(diver.abs(), salSalePriceItem.getIncreQuantity(), roundingType);
                        price = salSalePriceItem.getSalePriceTax().add(value.multiply(salSalePriceItem.getIncrePriceTax()));
                    }else if(diver.compareTo(BigDecimal.ZERO) == -1){
                        //递减少
                        BigDecimal value = getVale(diver.abs(), salSalePriceItem.getDecreQuantity(), roundingType);
                        price = salSalePriceItem.getSalePriceTax().subtract(value.multiply(salSalePriceItem.getDecrePriceTax()));
                    }else{
                        //相等
                        price = salSalePriceItem.getSalePriceTax();
                    }
                }else{
                    price = salSalePriceItem.getSalePriceTax();
                }
                salSalePriceItem.setSalePriceTax(price)
                        .setSalePrice(price.divide(BigDecimal.ONE.add(salSalePriceItem.getTaxRate()),6,BigDecimal.ROUND_HALF_DOWN));
            }
            item = salSalePriceItem;
        }
        //默认获取通用税率
        ConTaxRate taxRate = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>().lambda()
                .eq(ConTaxRate::getIsDefault, "Y")
        );
        if(item == null){
            item = new SalSalePriceItem();
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
     * 根据订单中的“编码+客供料方式+销售模式”在销售价档案中获取销售价的处理状态为“审批中/已确认/变更审批中”且有效期（至）>=当前日期的销售价数据
     * 若查找到多笔数据，则选择有效期（至）距离当前日期最近的价格
     */
    private SalSalePriceItem getPrice(SalSalePrice salSalePrice) {
        SalSalePriceItem response = null;
        String[] handleStatus = new String[]{HandleStatus.SUBMIT.getCode(),HandleStatus.CONFIRMED.getCode(),HandleStatus.CHANGEAPPROVAL.getCode()};
        List<SalSalePrice> result = null;
        //按：客供料方式、商品/物料sid、销售模式
        result = salSalePriceMapper.selectList(new QueryWrapper<SalSalePrice>().lambda()
                .eq(SalSalePrice::getCustomerSid, salSalePrice.getCustomerSid())
                .eq(SalSalePrice::getSaleMode, salSalePrice.getSaleMode())
                .eq(SalSalePrice::getRawMaterialMode, salSalePrice.getRawMaterialMode())
                .eq(SalSalePrice::getMaterialSid, salSalePrice.getMaterialSid())
                .eq(SalSalePrice::getSku1Sid, salSalePrice.getSku1Sid()));
        if (CollectionUtil.isNotEmpty(result)){
            List<Long> sidList = result.stream().map(SalSalePrice::getSalePriceSid).collect(Collectors.toList());
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String now = sdf.format(date);
            //直接截取到日
            Date nowDate = DateUtils.parseDate(now);
            List<SalSalePriceItem> salSalePriceItem =salSalePriceItemMapper.selectList(new QueryWrapper<SalSalePriceItem>().lambda()
                    .in(SalSalePriceItem::getSalePriceSid, sidList)
                    .in(SalSalePriceItem::getHandleStatus, handleStatus)
                    .ge(SalSalePriceItem::getEndDate, nowDate)
                    .orderByAsc(SalSalePriceItem::getEndDate));
            if(CollectionUtil.isNotEmpty(salSalePriceItem)){
                response = salSalePriceItem.get(0);
            }
        }
        if (response == null){
            //按：销售模式、客供料方式、商品/物料sid、
            result = salSalePriceMapper.selectList(new QueryWrapper<SalSalePrice>().lambda()
                    .eq(SalSalePrice::getSaleMode, salSalePrice.getSaleMode())
                    .eq(SalSalePrice::getRawMaterialMode, salSalePrice.getRawMaterialMode())
                    .eq(SalSalePrice::getMaterialSid, salSalePrice.getMaterialSid())
                    .isNull(SalSalePrice::getSku1Sid)
                    .eq(SalSalePrice::getCustomerSid, salSalePrice.getCustomerSid()));
            if (CollectionUtil.isNotEmpty(result)){
                List<Long> sidList = result.stream().map(SalSalePrice::getSalePriceSid).collect(Collectors.toList());
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String now = sdf.format(date);
                //直接截取到日
                Date nowDate = DateUtils.parseDate(now);
                List<SalSalePriceItem> salSalePriceItem =salSalePriceItemMapper.selectList(new QueryWrapper<SalSalePriceItem>().lambda()
                        .in(SalSalePriceItem::getSalePriceSid, sidList)
                        .in(SalSalePriceItem::getHandleStatus, handleStatus)
                        .ge(SalSalePriceItem::getEndDate, nowDate)
                        .orderByAsc(SalSalePriceItem::getEndDate));
                if(CollectionUtil.isNotEmpty(salSalePriceItem)){
                    response = salSalePriceItem.get(0);
                }
            }
        }
        if (response == null){
            //按：销售模式、客供料方式、商品/物料sid、
            result = salSalePriceMapper.selectList(new QueryWrapper<SalSalePrice>().lambda()
                    .eq(SalSalePrice::getSaleMode, salSalePrice.getSaleMode())
                    .eq(SalSalePrice::getRawMaterialMode, salSalePrice.getRawMaterialMode())
                    .eq(SalSalePrice::getMaterialSid, salSalePrice.getMaterialSid())
                    .eq(SalSalePrice::getSku1Sid, salSalePrice.getSku1Sid())
                    .isNull(SalSalePrice::getCustomerSid));
            if (CollectionUtil.isNotEmpty(result)){
                List<Long> sidList = result.stream().map(SalSalePrice::getSalePriceSid).collect(Collectors.toList());
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String now = sdf.format(date);
                //直接截取到日
                Date nowDate = DateUtils.parseDate(now);
                List<SalSalePriceItem> salSalePriceItem =salSalePriceItemMapper.selectList(new QueryWrapper<SalSalePriceItem>().lambda()
                        .in(SalSalePriceItem::getSalePriceSid, sidList)
                        .in(SalSalePriceItem::getHandleStatus, handleStatus)
                        .ge(SalSalePriceItem::getEndDate, nowDate)
                        .orderByAsc(SalSalePriceItem::getEndDate));
                if(CollectionUtil.isNotEmpty(salSalePriceItem)){
                    response = salSalePriceItem.get(0);
                }
            }
        }
        if (response == null){
            //按：销售模式、客供料方式、商品/物料sid、
            result = salSalePriceMapper.selectList(new QueryWrapper<SalSalePrice>().lambda()
                    .eq(SalSalePrice::getSaleMode, salSalePrice.getSaleMode())
                    .eq(SalSalePrice::getRawMaterialMode, salSalePrice.getRawMaterialMode())
                    .eq(SalSalePrice::getMaterialSid, salSalePrice.getMaterialSid())
                    .isNull(SalSalePrice::getSku1Sid)
                    .isNull(SalSalePrice::getCustomerSid));
            if (CollectionUtil.isNotEmpty(result)){
                List<Long> sidList = result.stream().map(SalSalePrice::getSalePriceSid).collect(Collectors.toList());
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String now = sdf.format(date);
                //直接截取到日
                Date nowDate = DateUtils.parseDate(now);
                List<SalSalePriceItem> salSalePriceItem =salSalePriceItemMapper.selectList(new QueryWrapper<SalSalePriceItem>().lambda()
                        .in(SalSalePriceItem::getSalePriceSid, sidList)
                        .in(SalSalePriceItem::getHandleStatus, handleStatus)
                        .ge(SalSalePriceItem::getEndDate, nowDate)
                        .orderByAsc(SalSalePriceItem::getEndDate));
                if(CollectionUtil.isNotEmpty(salSalePriceItem)){
                    response = salSalePriceItem.get(0);
                }
            }
        }
        return response;
    }

    /**
     * 审批流修改状态
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int flowHandle(SalSalePriceItem item,String comment){
        String status=null;
        SalSalePriceItem salSalePriceItem = salSalePriceItemMapper.selectById(item.getSalePriceItemSid());
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentItemSid, item.getSalePriceItemSid()));
        if(ConstantsEms.CHECK_STATUS.equals(item.getHandleStatus())){
            //插入日志
//            MongodbUtil.insertUserLogItem(salSalePriceItem.getSalePriceSid(), BusinessType.APPROVED.getValue(),TITLE,salSalePriceItem.getItemNum());
        }else if(HandleStatus.SUBMIT.getCode().equals(item.getHandleStatus())||HandleStatus.CHANGEAPPROVAL.getCode().equals(item.getHandleStatus())){
            if(HandleStatus.CHANGEAPPROVAL.getCode().equals(item.getHandleStatus())){
                MongodbUtil.insertUserLogItem(salSalePriceItem.getSalePriceSid(), BusinessType.CHANGE.getValue(),TITLE,salSalePriceItem.getItemNum());
            }
            //插入日志
            MongodbUtil.insertUserLogItem(salSalePriceItem.getSalePriceSid(), BusinessType.SUBMIT.getValue(),TITLE,salSalePriceItem.getItemNum());
        }else{
            //插入日志
            MongodbUtil.insertApprovalLogAddNum(salSalePriceItem.getSalePriceSid(), BusinessType.APPROVAL.getValue(),comment,salSalePriceItem.getItemNum());
        }
        if (HandleStatus.BG_RETURN.getCode().equals(item.getHandleStatus())) {//单笔变更驳回
            item.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        }
        if(HandleStatus.RETURNED.getCode().equals(item.getHandleStatus())){
            if(HandleStatus.CHANGEAPPROVAL.getCode().equals(salSalePriceItem.getHandleStatus())){//多笔变更驳回
                item.setHandleStatus(HandleStatus.CONFIRMED.getCode());
            }
        }
        int row = salSalePriceItemMapper.update(new SalSalePriceItem(), new UpdateWrapper<SalSalePriceItem>().lambda()
                .set(SalSalePriceItem::getHandleStatus,item.getHandleStatus())
                .set(SalSalePriceItem::getItemNum,salSalePriceItem.getItemNum())
                .eq(SalSalePriceItem::getSalePriceItemSid,item.getSalePriceItemSid())
        );
        List<SalSalePriceItem> listSalSalePriceItem = salSalePriceItemMapper.selectList(new QueryWrapper<SalSalePriceItem>().lambda()
                .eq(SalSalePriceItem::getSalePriceSid, salSalePriceItem.getSalePriceSid())
        );
        SalSalePrice salSalePrice = salSalePriceMapper.selectById(salSalePriceItem.getSalePriceSid());
        if(ConstantsEms.CHECK_STATUS.equals(item.getHandleStatus())){
            new Thread(()->{
                //价格回写
                orderUpdate(salSalePrice, salSalePriceItem);
            }).start();
        }
        List<SalSalePriceItem> checkList = listSalSalePriceItem.stream().filter(li -> ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(checkList)){
            if(!ConstantsEms.CHECK_STATUS.equals(salSalePrice.getHandleStatus())){
                salSalePriceMapper.update(new SalSalePrice(),new UpdateWrapper<SalSalePrice>().lambda()
                        .eq(SalSalePrice::getSalePriceSid,salSalePrice.getSalePriceSid())
                        .set(SalSalePrice::getHandleStatus,ConstantsEms.CHECK_STATUS)
                );
            }
        }else{
            if(!ConstantsEms.CHECK_STATUS.equals(salSalePrice.getHandleStatus())){
                SalSalePriceItem salePriceItem = listSalSalePriceItem.get(0);
                salSalePriceMapper.update(new SalSalePrice(),new UpdateWrapper<SalSalePrice>().lambda()
                        .eq(SalSalePrice::getSalePriceSid,salSalePrice.getSalePriceSid())
                        .set(SalSalePrice::getHandleStatus,salePriceItem.getHandleStatus())
                );
            }
        }
        return row;
    }

    @Override
    public void setApprovalLog(SalSalePriceItem item,String comment){
        SalSalePriceItem salSalePriceItem = salSalePriceItemMapper.selectById(item.getSalePriceItemSid());
        MongodbUtil.insertApprovalLogAddNum(salSalePriceItem.getSalePriceSid(), BusinessType.APPROVAL.getValue(),comment,salSalePriceItem.getItemNum());
    }

    private synchronized void pushPrice(SalSalePrice salSalePrice,SalSalePriceItem salSalePriceItem) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("salePriceTax",salSalePriceItem.getSalePriceTax());
        jsonObject.put("salePrice",salSalePriceItem.getSalePrice());
        jsonObject.put("unitBase",salSalePriceItem.getUnitBase());
        jsonObject.put("unitPrice",salSalePriceItem.getUnitPrice());
        jsonObject.put("unitConversionRate",salSalePriceItem.getUnitConversionRate());
        jsonObject.put("type", AutoIdField.change_sale_price_key);
        jsonObject.put("taxRate",salSalePriceItem.getTaxRate());
        jsonObject.put("sku1Sid",salSalePrice.getSku1Sid());
        jsonObject.put("materialSid",salSalePrice.getMaterialSid());
        jsonObject.put("rawMaterialMode",salSalePrice.getRawMaterialMode());
        jsonObject.put("saleMode",salSalePrice.getSaleMode());
        jsonObject.put("customerSid",salSalePrice.getCustomerSid());
        jsonObject.put("endDate",salSalePriceItem.getEndDate());
        jsonObject.put("startDate",salSalePriceItem.getStartDate());
        msgSender.sendMessage(jsonObject);
    }
    //审批后价格回写
    private  void orderUpdate(SalSalePrice salSalePrice,SalSalePriceItem salSalePriceItem) {
        Date startDate = salSalePriceItem.getStartDate();
        Date endDate = salSalePriceItem.getEndDate();
        if(startDate.getTime()<=new Date().getTime()
                &&new Date().getTime()<=endDate.getTime()
        ){
            SalSalesOrder salSalesOrder = new SalSalesOrder();
            BeanCopyUtils.copyProperties(salSalePrice,salSalesOrder);
            BeanCopyUtils.copyProperties(salSalePriceItem,salSalesOrder);
            Long materialSid = salSalePrice.getMaterialSid();
            String zipperFlag = basMaterialMapper.selectById(materialSid).getZipperFlag();
            //整条拉链地增减价计算
            if(ConstantsEms.YES.equals(salSalePriceItem.getIsRecursionPrice())&&!ConstantsEms.ZIPPER_ZH.equals(zipperFlag)){
                List<SalSalesOrderItem> items = salSalesOrderMapper.getUpdatePrice(salSalesOrder);
                if(CollectionUtil.isNotEmpty(items)){
                    items.stream().forEach(li->{
                        salSalePrice.setSku2Sid(li.getSku2Sid());
                        SalSalePriceItem newPrice = new SalSalePriceItem();
                        BeanCopyUtils.copyProperties(salSalePriceItem,newPrice);
                        SalSalePriceItem priceItem = zipperPriceZT(salSalePrice,newPrice);
                        SalSalesOrder order = new SalSalesOrder();
                        BeanCopyUtils.copyProperties(salSalesOrder,order);
                        order.setSalesOrderItemSid(li.getSalesOrderItemSid())
                                .setSalePriceTax(priceItem.getSalePriceTax())
                                .setSalePrice(priceItem.getSalePrice());
                        salSalesOrderMapper.updatePrice(order);
                    });
                }
            }else if(!ConstantsEms.ZIPPER_ZH.equals(zipperFlag)){
                salSalesOrderMapper.updatePrice(salSalesOrder);
            }
        }
    }
    /**
     * 销售价 导入
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
            List<SalSalePrice> salSalePriceList = new ArrayList<>();
            String errMsg="";
            List<salSalePriceErrMsgResponse> msgList=new ArrayList<>();
            for (int i = 0; i < readAll.size(); i++) {
                Long companySid=null;
                Long customerSid=null;
                Long materialSid=null;
                Long sku1Sid=null;
                String unitPrice=null;
                String materialCode=null;
                String materialCategory = null;
                String priceDimensionValue=null;
                String is=null;
                String priceTypeValue=null;
                String rawMaterialModeValue=null;
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
                if (objects.get(0) != null && objects.get(0) != "") {
                    String customer = objects.get(0).toString();
                    BasCustomer basCustomer = basCustomerMapper.selectOne(new QueryWrapper<BasCustomer>()
                            .lambda().eq(BasCustomer::getShortName, customer));
                    if (basCustomer == null) {
                     // throw new BaseException("第"+num+"行,客户简称为" + customer + "，没有对应的客户，导入失败");
                        errMsg=errMsg+"第"+num+"行,客户简称为" + customer + "，没有对应的客户，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("客户简称为" + customer + "，没有对应的客户，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    } else {
                        if(!basCustomer.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)||!basCustomer.getStatus().equals(ConstantsEms.ENABLE_STATUS)){
                           // throw new BaseException("第"+num+"行,对应的客户必须是确认且已启用的状态，导入失败");
                            errMsg=errMsg+"第"+num+"行,对应的客户必须是确认且已启用的状态，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                            salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                            salSalePriceErrMsgResponse.setItemNum(num);
                            salSalePriceErrMsgResponse.setMsg("对应的客户必须是确认且已启用的状态，导入失败");
                            msgList.add(salSalePriceErrMsgResponse);
                        }
                        customerSid = basCustomer.getCustomerSid();
                    }
                }
                if (objects.get(2) == null || objects.get(2) == "") {
                   // throw new BaseException("第"+num+"行,客供料方式，不能为空，导入失败");
                    errMsg=errMsg+"第"+num+"行,客供料方式，不能为空，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                    salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                    salSalePriceErrMsgResponse.setItemNum(num);
                    salSalePriceErrMsgResponse.setMsg("客供料方式，不能为空，导入失败");
                    msgList.add(salSalePriceErrMsgResponse);
                }
                if(objects.get(2) != null && objects.get(2) != ""){
                     rawMaterialModeValue = rawMaterialModeMaps.get(objects.get(2).toString());
                    if(StrUtil.isEmpty(rawMaterialModeValue)){
                        // throw new BaseException("第"+num+"行,客供料方式配置错误，导入失败,请联系管理员");
                        errMsg=errMsg+"第"+num+"行,客供料方式配置错误，导入失败,请联系管理员"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("客供料方式配置错误，导入失败,请联系管理员");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                    String vale=rawMaterialModeValue;
                    if(rawMaterialModeValue!=null){
                        if(CollectionUtil.isNotEmpty(rawMaterialMode)){
                            List<DictData> list = rawMaterialMode.stream()
                                    .filter(m -> ConstantsEms.CHECK_STATUS.equals(m.getHandleStatus()) && "0".equals(m.getStatus()) && vale.equals(m.getDictValue()))
                                    .collect(Collectors.toList());
                            if(CollectionUtil.isEmpty(list)){
                                salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                                salSalePriceErrMsgResponse.setItemNum(num);
                                salSalePriceErrMsgResponse.setMsg("客供料方式必须是确认且已启用状态，导入失败");
                                msgList.add(salSalePriceErrMsgResponse);
                            }
                        }
                    }
                }
                if (objects.get(3) == null || objects.get(3) == "") {
                   // throw new BaseException("第"+num+"行,采购模式，不能为空，导入失败");
                    errMsg=errMsg+"第"+num+"行,销售模式，不能为空，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                    salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                    salSalePriceErrMsgResponse.setItemNum(num);
                    salSalePriceErrMsgResponse.setMsg("销售模式，不能为空，导入失败");
                    msgList.add(salSalePriceErrMsgResponse);
                }
                if(objects.get(3) != null && objects.get(3) != ""){
                     priceTypeValue = priceTypeMaps.get(objects.get(3).toString());
                    if(StrUtil.isEmpty(priceTypeValue)){
                        // throw new BaseException("第"+num+"行,销售模式配置错误，导入失败,请联系管理员");
                        errMsg=errMsg+"第"+num+"行,销售模式配置错误，导入失败,请联系管理员"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("销售模式配置错误，导入失败,请联系管理员");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                    if(priceTypeValue!=null){
                        String value=priceTypeValue;
                        if(CollectionUtil.isNotEmpty(priceType)){
                            List<DictData> list = priceType.stream()
                                    .filter(m -> ConstantsEms.CHECK_STATUS.equals(m.getHandleStatus()) && "0".equals(m.getStatus()) && value.equals(m.getDictValue()))
                                    .collect(Collectors.toList());
                            if(CollectionUtil.isEmpty(list)){
                                salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                                salSalePriceErrMsgResponse.setItemNum(num);
                                salSalePriceErrMsgResponse.setMsg("销售模式必须是确认且已启用状态，导入失败");
                                msgList.add(salSalePriceErrMsgResponse);
                            }
                        }
                    }
                }
                if (objects.get(4) == null || objects.get(4) == "") {
                   // throw new BaseException("第"+num+"行,价格维度，不能为空，导入失败");
                    errMsg=errMsg+"第"+num+"行,价格维度，不能为空，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                    salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                    salSalePriceErrMsgResponse.setItemNum(num);
                    salSalePriceErrMsgResponse.setMsg("价格维度，不能为空，导入失败");
                    msgList.add(salSalePriceErrMsgResponse);
                }
                if(objects.get(4) != null && objects.get(4) != ""){
                     priceDimensionValue = priceDimensionMaps.get(objects.get(4).toString());
                    if(StrUtil.isEmpty(priceDimensionValue)){

                        // throw new BaseException("第"+num+"行,价格维度配置错误，导入失败,请联系管理员");
                        errMsg=errMsg+"第"+num+"行,价格维度配置错误，导入失败,请联系管理员"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("价格维度配置错误，导入失败,请联系管理员");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                    if(priceDimensionValue!=null){
                        String value=priceDimensionValue;
                        if(CollectionUtil.isNotEmpty(priceDimension)){
                            List<DictData> list = priceDimension.stream()
                                    .filter(m -> ConstantsEms.CHECK_STATUS.equals(m.getHandleStatus()) && "0".equals(m.getStatus()) && value.equals(m.getDictValue()))
                                    .collect(Collectors.toList());
                            if(CollectionUtil.isEmpty(list)){
                                salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                                salSalePriceErrMsgResponse.setItemNum(num);
                                salSalePriceErrMsgResponse.setMsg("价格维度必须是确认且已启用状态，导入失败");
                                msgList.add(salSalePriceErrMsgResponse);
                            }
                        }
                    }
                }
                if (objects.get(5) == null || objects.get(5) == "") {
                    //throw new BaseException("第"+num+"行,商品/物料编码，不能为空，导入失败");
                    errMsg=errMsg+"第"+num+"行,商品/物料编码，不能为空，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                    salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                    salSalePriceErrMsgResponse.setItemNum(num);
                    salSalePriceErrMsgResponse.setMsg("商品/物料编码，不能为空，导入失败");
                    msgList.add(salSalePriceErrMsgResponse);
                }
                if(objects.get(5) != null && objects.get(5) != ""){
                     basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                            .eq(BasMaterial::getMaterialCode, objects.get(5).toString())
                    );
                    if(basMaterial==null){
                        // throw new BaseException("第"+num+"行,商品/物料编码为"+objects.get(5).toString()+"，没有对应的商品/物料，导入失败");
                        errMsg=errMsg+"第"+num+"行,商品/物料编码为"+objects.get(5).toString()+"，没有对应的商品/物料，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("商品/物料编码为"+objects.get(5).toString()+"，没有对应的商品/物料，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }else{
                        if(!basMaterial.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)||!basMaterial.getStatus().equals(ConstantsEms.ENABLE_STATUS)){
                            //throw new BaseException("第"+num+"行,对应的商品/物料必须是确认且已启用的状态，导入失败");
                            errMsg=errMsg+"第"+num+"行,对应的商品/物料必须是确认且已启用的状态，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                            salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                            salSalePriceErrMsgResponse.setItemNum(num);
                            salSalePriceErrMsgResponse.setMsg("对应的商品/物料必须是确认且已启用的状态，导入失败");
                            msgList.add(salSalePriceErrMsgResponse);
                        }
                        materialSid=basMaterial.getMaterialSid();
                        materialCode=basMaterial.getMaterialCode();
                        materialCategory = basMaterial.getMaterialCategory();
                        if (ConstantsEms.MATERIAL_CATEGORY_WL.equals(materialCategory) && !ConstantsEms.RAW_MATERIAL_MODE_WU.equals(rawMaterialModeValue)) {
                            salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                            salSalePriceErrMsgResponse.setItemNum(num);
                            salSalePriceErrMsgResponse.setMsg(objects.get(5).toString() + "为物料，客供料方式需选择“无/供方全包料”，导入失败");
                            msgList.add(salSalePriceErrMsgResponse);
                        }
                    }
                }
                if (objects.get(6) != null && objects.get(6) != "") {
                    if(ConstantsEms.PRICE_K.equals(priceDimensionValue)){
                       // throw new BaseException("第"+num+"行,价格维度按款时，不允许填写颜色名称，导入失败");
                        errMsg=errMsg+"第"+num+"行,价格维度按款时，不允许填写颜色名称，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("价格维度按款时，不允许填写颜色名称，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                    BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                            .eq(BasSku::getSkuName, objects.get(6).toString()));
                    if(basSku==null){
                      //  throw new BaseException("第"+num+"行,颜色名称为"+objects.get(6).toString()+"，没有对应的颜色，导入失败");
                        errMsg=errMsg+"第"+num+"行,颜色名称为"+objects.get(6).toString()+"，没有对应的颜色，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("颜色名称为"+objects.get(6).toString()+"，没有对应的颜色，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }else{
                        sku1Sid=basSku.getSkuSid();
                        List<BasMaterialSku> basMaterialSkus = basMaterialSkuMapper.selectList(new QueryWrapper<BasMaterialSku>().lambda()
                                .eq(BasMaterialSku::getMaterialSid, materialSid)
                                .eq(BasMaterialSku::getSkuSid, sku1Sid)
                        );
                        if(CollectionUtils.isEmpty(basMaterialSkus)){
                           // throw new BaseException("第"+num+"行,该物料没有对应的颜色，导入失败");
                            errMsg=errMsg+"第"+num+"行,该物料没有对应的颜色，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                            salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                            salSalePriceErrMsgResponse.setItemNum(num);
                            salSalePriceErrMsgResponse.setMsg("该物料没有对应的颜色，导入失败");
                            msgList.add(salSalePriceErrMsgResponse);
                        }else {
                            if(!ConstantsEms.SAVA_STATUS.equals(basMaterialSkus.get(0).getStatus())){
                                // throw new BaseException("第"+num+"行,该颜色名称必须启用状态，导入失败");
                                errMsg=errMsg+"第"+num+"行,该颜色名称必须启用状态，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                                salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                                salSalePriceErrMsgResponse.setItemNum(num);
                                salSalePriceErrMsgResponse.setMsg("该颜色名称必须启用状态，导入失败");
                                msgList.add(salSalePriceErrMsgResponse);
                            }
                        }
                    }
                }
                if(objects.get(12) != null && objects.get(12) != ""){
                    String skuTyp = skuTypeMaps.get(objects.get(12).toString());
                    if(skuTyp==null){
                       // throw new BaseException("第"+num+"行,递增减SKU类型配置错误，导入失败");
                        errMsg=errMsg+"第"+num+"行,递增减SKU类型配置错误，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("递增减SKU类型配置错误，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                    if(skuTyp!=null){
                        if(CollectionUtil.isNotEmpty(skuType)){
                            List<DictData> list = skuType.stream()
                                    .filter(m -> ConstantsEms.CHECK_STATUS.equals(m.getHandleStatus()) && "0".equals(m.getStatus()) && skuTyp.equals(m.getDictValue()))
                                    .collect(Collectors.toList());
                            if(CollectionUtil.isEmpty(list)){
                                salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                                salSalePriceErrMsgResponse.setItemNum(num);
                                salSalePriceErrMsgResponse.setMsg("递增减SKU类型必须是确认且已启用状态，导入失败");
                                msgList.add(salSalePriceErrMsgResponse);
                            }
                        }
                    }
                }
                if (objects.get(9)!=  null && objects.get(9) != "") {
                    boolean validDouble = isValidDouble(objects.get(9).toString());
                    if(!validDouble){
                       // throw new BaseException("第"+num+"行,销售价（含税）,数据格式错误，导入失败");
                        errMsg=errMsg+"第"+num+"行,销售价（含税）,数据格式错误，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("销售价（含税）,数据格式错误，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                }
                if (objects.get(13)!=  null && objects.get(13) != "") {
                    boolean validDouble = JudgeFormat.isValidDoubleLgZero(objects.get(13).toString(), 7, 3);
                    if(!validDouble){
                       // throw new BaseException("第"+num+"行,递增量,数据格式错误，导入失败");
                        errMsg=errMsg+"第"+num+"行,递增量,数据格式错误，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("递增量,数据格式错误，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                }
                if (objects.get(14) !=  null && objects.get(14) != "") {
                    boolean validDouble = isValidDouble(objects.get(14).toString());
                    if(!validDouble){
                        //throw new BaseException("第"+num+"行,递增价(含税),数据格式错误，导入失败");
                        errMsg=errMsg+"第"+num+"行,递增价(含税),数据格式错误，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("递增价(含税),数据格式错误，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                }
                if (objects.get(15) !=  null && objects.get(15) != "") {
                    boolean validDouble = JudgeFormat.isValidDoubleLgZero(objects.get(15).toString(),7,3);
                    if(!validDouble){
                       // throw new BaseException("第"+num+"行,递减量,数据格式错误，导入失败");
                        errMsg=errMsg+"第"+num+"行,递减量,数据格式错误，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("递减量,数据格式错误，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                }
                if (objects.get(16) != null && objects.get(16) != "") {
                    boolean validDouble = isValidDouble(objects.get(16).toString());
                    if(!validDouble){
                       // throw new BaseException("第"+num+"行,递减价(含税),数据格式错误，导入失败");
                        errMsg=errMsg+"第"+num+"行,递减价(含税),数据格式错误，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("递减价(含税),数据格式错误，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                }
                if (objects.get(17) != null && objects.get(17) != "") {
                    boolean validDouble = JudgeFormat.isValidDoubleLgZero(objects.get(17).toString(),7,3);
                    if(!validDouble){
                        //throw new BaseException("第"+num+"行,基准量,数据格式错误，导入失败");
                        errMsg=errMsg+"第"+num+"行,基准量,数据格式错误，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("基准量,数据格式错误，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                }
                if (objects.get(18) !=  null && objects.get(18) != "") {
                    boolean validDouble = JudgeFormat.isValidDoubleLgZero(objects.get(18).toString(),7,3);
                    if(!validDouble){
                       // throw new BaseException("第"+num+"行,价格最小起算量,数据格式错误，导入失败");
                        errMsg=errMsg+"第"+num+"行,价格最小起算量,数据格式错误，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("价格最小起算量,数据格式错误，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                }
                if(ConstantsEms.PRICE_K1.equals(priceDimensionValue)){
                    if(sku1Sid==null){
                       // throw new BaseException("第"+num+"行,价格维度按色时,颜色为必填，导入失败");
                        errMsg=errMsg+"第"+num+"行,价格维度按色时,颜色为必填，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("价格维度按色时,颜色为必填，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                }
                if (objects.get(7) == null || objects.get(7) == "") {
                    //throw new BaseException("第"+num+"行,有效期起，不能为空，导入失败");
                    errMsg=errMsg+"第"+num+"行,有效期起，不能为空，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                    salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                    salSalePriceErrMsgResponse.setItemNum(num);
                    salSalePriceErrMsgResponse.setMsg("有效期起，不能为空，导入失败");
                    msgList.add(salSalePriceErrMsgResponse);
                }
                if (objects.get(8) == null || objects.get(8) == "") {
                        //throw new BaseException("第"+num+"行,有效期至，不能为空，导入失败");
                        errMsg=errMsg+"第"+num+"行,有效期至，不能为空，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                    salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                    salSalePriceErrMsgResponse.setItemNum(num);
                    salSalePriceErrMsgResponse.setMsg("有效期至，不能为空，导入失败");
                    msgList.add(salSalePriceErrMsgResponse);
                }
                if(objects.get(7) !=  null && objects.get(7) != ""){
                     start = JudgeFormat.isValidDate(objects.get(7).toString());
                    if(!start){
                        // throw new BaseException("第"+num+"行,有效期起，日期格式错误");
                        errMsg=errMsg+"第"+num+"行,有效期起，日期格式错误"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("有效期起，日期格式错误");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                }
                if(objects.get(8) !=  null && objects.get(8) != ""){
                     end = JudgeFormat.isValidDate(objects.get(8).toString());
                    if(!end){
                        //  throw new BaseException("第"+num+"行,有效期至，日期格式错误");
                        errMsg=errMsg+"第"+num+"行,有效期至，日期格式错误"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("有效期至，日期格式错误");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                }
                if(objects.get(7) !=  null && objects.get(7) != ""&&objects.get(8) !=  null && objects.get(8) != ""){
                    if(start&&end){
                        if(DateUtils.parseDate(objects.get(7)).getTime()>DateUtils.parseDate(objects.get(8)).getTime()){
                            // throw new BaseException("第"+num+"行,有效期起，不能大于有效期至，导入失败");
                            errMsg=errMsg+"第"+num+"行,有效期起，不能大于有效期至，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                            salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                            salSalePriceErrMsgResponse.setItemNum(num);
                            salSalePriceErrMsgResponse.setMsg("有效期起，不能大于有效期至，导入失败");
                            msgList.add(salSalePriceErrMsgResponse);
                        }
                    }
                }
                if (objects.get(9) == null || objects.get(9) == "") {
                   // throw new BaseException("第"+num+"行,销售价（含税），不能为空，导入失败");
                    errMsg=errMsg+"第"+num+"行,销售价（含税），不能为空，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                    salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                    salSalePriceErrMsgResponse.setItemNum(num);
                    salSalePriceErrMsgResponse.setMsg("销售价（含税），不能为空，导入失败");
                    msgList.add(salSalePriceErrMsgResponse);
                }
                if (objects.get(10) == null || objects.get(10) == "") {
                   // throw new BaseException("第"+num+"行,税率，不能为空，导入失败");
                    errMsg=errMsg+"第"+num+"行,税率，不能为空，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                    salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                    salSalePriceErrMsgResponse.setItemNum(num);
                    salSalePriceErrMsgResponse.setMsg("税率，不能为空，导入失败");
                    msgList.add(salSalePriceErrMsgResponse);
                }
                if(objects.get(17) != null && objects.get(17) != ""){
                    if(objects.get(18) != null && objects.get(18) != ""){
                        if(isValidDouble(objects.get(17).toString())&&isValidDouble(objects.get(18).toString())){
                            if(Double.valueOf(objects.get(17).toString())-Double.valueOf(objects.get(18).toString())<0){
                                //throw new BaseException("第"+num+"行,价格最小起算量大于基准量，导入失败！");
                                errMsg=errMsg+"第"+num+"行,价格最小起算量大于基准量，导入失败！"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                                salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                                salSalePriceErrMsgResponse.setItemNum(num);
                                salSalePriceErrMsgResponse.setMsg("价格最小起算量大于基准量，导入失败！");
                                msgList.add(salSalePriceErrMsgResponse);
                            }
                        }
                    }
                }
                if(objects.get(10) !=  null && objects.get(10) != ""){
                    ConTaxRate conTaxRate = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>().lambda()
                            .eq(ConTaxRate::getTaxRateValue, objects.get(10).toString())
                    );
                    if(conTaxRate==null){
                        // throw new BaseException("第"+num+"行,税率配置错误，导入失败,请联系管理员");
                        errMsg=errMsg+"第"+num+"行,税率配置错误，导入失败,请联系管理员"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("税率配置错误，导入失败,请联系管理员");
                        msgList.add(salSalePriceErrMsgResponse);
                    }else{
                        if(!ConstantsEms.CHECK_STATUS.equals(conTaxRate.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(conTaxRate.getStatus())){
                            errMsg=errMsg+"第"+num+"行,对应的税率必须是确认且已启用的状态"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                            salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                            salSalePriceErrMsgResponse.setItemNum(num);
                            salSalePriceErrMsgResponse.setMsg("对应的税率必须是确认且已启用的状态");
                            msgList.add(salSalePriceErrMsgResponse);
                        }
                    }
                }
                if (objects.get(11) == null || objects.get(11) == "") {
                   // throw new BaseException("第"+num+"行,是否递增减价，不允许为空，导入失败");
                    errMsg=errMsg+"第"+num+"行,是否递增减价，不允许为空，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                    salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                    salSalePriceErrMsgResponse.setItemNum(num);
                    salSalePriceErrMsgResponse.setMsg("是否递增减价，不允许为空，导入失败");
                    msgList.add(salSalePriceErrMsgResponse);
                }
                if(objects.get(11) !=  null && objects.get(11) != ""){
                     is = yesMaps.get(objects.get(11).toString());
                    if(is==null){
                        // throw new BaseException("第"+num+"行,是否递增减价配置错误，导入失败");
                        errMsg=errMsg+"第"+num+"行,是否递增减价配置错误，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("是否递增减价配置错误，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                }
                if(ConstantsEms.YES.equals(is)){
                    if (objects.get(12) == null || objects.get(12) == "") {
                       // throw new BaseException("第"+num+"行,递增减价，递增减SKU类型不能为空，导入失败");
                        errMsg=errMsg+"第"+num+"行,递增减价，递增减SKU类型不能为空，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("递增减价，递增减SKU类型不能为空，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                    if (objects.get(13) == null || objects.get(13) == "") {
                      //  throw new BaseException("第"+num+"行,递增减价，递增量不能为空，导入失败");
                        errMsg=errMsg+"第"+num+"行,递增减价，递增量不能为空，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("递增减价，递增量不能为空，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                    if (objects.get(14) == null || objects.get(14) == "") {
                       // throw new BaseException("第"+num+"行,递增减价，递增价(含税)不能为空，导入失败");
                        errMsg=errMsg+"第"+num+"行,递增减价，递增价(含税)不能为空，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("递增减价，递增价(含税)不能为空，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                    if (objects.get(15) == null || objects.get(15) == "") {
                       // throw new BaseException("第"+num+"行,递增减价，递减量不能为空，导入失败");
                        errMsg=errMsg+"第"+num+"行,递增减价，递减量不能为空，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("递增减价，递减量不能为空，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                    if (objects.get(16) == null || objects.get(16) == "") {
                        //throw new BaseException("第"+num+"行,递增减价，递减价(含税)不能为空，导入失败");
                        errMsg=errMsg+"第"+num+"行,递增减价，递减价(含税)不能为空，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("递增减价，递减价(含税)不能为空，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                    if (objects.get(17) == null || objects.get(17) == "") {
                      //  throw new BaseException("第"+num+"行,递增减价，基准量不能为空，导入失败");
                        errMsg=errMsg+"第"+num+"行,递增减价，基准量不能为空，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("递增减价，基准量不能为空，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                    if (objects.get(19) == null || objects.get(19) == "") {
                      //  throw new BaseException("第"+num+"行,递增减价，取整方式(递增减)，不能为空，导入失败");
                        errMsg=errMsg+"第"+num+"行,递增减价，取整方式(递增减)，不能为空，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("递增减价，取整方式(递增减)，不能为空，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                    if (objects.get(20) == null || objects.get(20) == "") {
                       // throw new BaseException("第"+num+"行,递增减价，递增减计量单位名称，不能为空，导入失败");
                        errMsg=errMsg+"第"+num+"行,递增减价，递增减计量单位名称，不能为空，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("递增减价，递增减计量单位名称，不能为空，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                }else if(ConstantsEms.NO.equals(is)){
                    if (objects.get(12) != null && objects.get(12) != "") {
                        //throw new BaseException("第"+num+"行,非递增减价，递增减SKU类型，不需要填写，导入失败");
                        errMsg=errMsg+"第"+num+"行,非递增减价，递增减SKU类型，不需要填写，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("非递增减价，递增减SKU类型，不需要填写，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                    if (objects.get(13)!=  null && objects.get(13) != "") {
                        //throw new BaseException("第"+num+"行,非递增减价，递增量不需要填写，导入失败");
                        errMsg=errMsg+"第"+num+"行,非递增减价，递增量不需要填写，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("非递增减价，递增量不需要填写，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                    if (objects.get(14) !=  null && objects.get(14) != "") {
                       // throw new BaseException("第"+num+"行,非递增减价，递增价(含税)，不需要填写，导入失败");
                        errMsg=errMsg+"第"+num+"行,非递增减价，递增价(含税)，不需要填写，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("非递增减价，递增价(含税)，不需要填写，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                    if (objects.get(15) !=  null && objects.get(15) != "") {
                        //throw new BaseException("第"+num+"行,非递增减价，递减量不需要填写，导入失败");
                        errMsg=errMsg+"第"+num+"行,非递增减价，递减量不需要填写，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("非递增减价，递减量不需要填写，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                    if (objects.get(16) != null && objects.get(16) != "") {
                        //throw new BaseException("第"+num+"行,非递增减价，递减价(含税)不需要填写，导入失败");
                        errMsg=errMsg+"第"+num+"行,非递增减价，递减价(含税)不需要填写，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("非递增减价，递减价(含税)不需要填写，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                    if (objects.get(17) !=  null && objects.get(17) != "") {
                        //throw new BaseException("第"+num+"行,非递增减价，基准量不需要填写，导入失败");
                        errMsg=errMsg+"第"+num+"行,非递增减价，基准量不需要填写，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("非递增减价，基准量不需要填写，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                    if (objects.get(19) !=  null && objects.get(19) != "") {
                       // throw new BaseException("第"+num+"行,非递增减价，取整方式(递增减)，不需要填写，导入失败");
                        errMsg=errMsg+"第"+num+"行,非递增减价，取整方式(递增减)，不需要填写，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("非递增减价，取整方式(递增减)，不需要填写，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                    if (objects.get(20) !=  null && objects.get(20) != "") {
                        //throw new BaseException("第"+num+"行,非递增减价，递增减计量单位名称不需要填写，导入失败");
                        errMsg=errMsg+"第"+num+"行,非递增减价，递增减计量单位名称不需要填写，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("非递增减价，递增减计量单位名称不需要填写，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                }
                if (objects.get(19) !=  null && objects.get(19) != "") {
                    String round = roundingTypeMaps.get(objects.get(19).toString());
                    if(round==null){
                       // throw new BaseException("第"+num+"行,取整方式(递增减)配置错误，导入失败");
                        errMsg=errMsg+"第"+num+"行,取整方式(递增减)配置错误，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("取整方式(递增减)配置错误，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }else{
                        if(CollectionUtil.isNotEmpty(roundingType)){
                            List<DictData> list = roundingType.stream()
                                    .filter(m -> ConstantsEms.CHECK_STATUS.equals(m.getHandleStatus()) && "0".equals(m.getStatus()) && round.equals(m.getDictValue()))
                                    .collect(Collectors.toList());
                            if(CollectionUtil.isEmpty(list)){
                                salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                                salSalePriceErrMsgResponse.setItemNum(num);
                                salSalePriceErrMsgResponse.setMsg("取整方式(递增减)必须是确认且已启用状态，导入失败");
                                msgList.add(salSalePriceErrMsgResponse);
                            }
                        }
                    }
                }
                if (objects.get(20) !=  null && objects.get(20) != "") {
                    ConMeasureUnit unit = conMeasureUnitMapper.selectOne(new QueryWrapper<ConMeasureUnit>().lambda()
                            .eq(ConMeasureUnit::getName, objects.get(20).toString())
                    );
                    if(unit==null){
                        //throw new BaseException("第"+num+"行,递增减计量单位配置错误，导入失败");
                        errMsg=errMsg+"第"+num+"行,递增减计量单位配置错误，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("递增减计量单位配置错误，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }else{
                        if(!ConstantsEms.CHECK_STATUS.equals(unit.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(unit.getStatus())){
                            errMsg=errMsg+"第"+num+"行,对应的递增减计量单位必须是确认且已启用的状态"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                            salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                            salSalePriceErrMsgResponse.setItemNum(num);
                            salSalePriceErrMsgResponse.setMsg("对应的递增减计量单位必须是确认且已启用的状态");
                            msgList.add(salSalePriceErrMsgResponse);
                        }
                    }
                }
                if(objects.get(21) !=  null && objects.get(21) != ""){
                    ConMeasureUnit conMeasureUnit = conMeasureUnitMapper.selectOne(new QueryWrapper<ConMeasureUnit>().lambda()
                            .eq(ConMeasureUnit::getName, objects.get(21).toString())
                    );
                    if(conMeasureUnit==null){
                        //throw new BaseException("第"+num+"行,销售价单位配置错误，导入失败");
                        errMsg=errMsg+"第"+num+"行,销售价单位配置错误，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("销售价单位配置错误，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }else{
                        if(!HandleStatus.CONFIRMED.getCode().equals(conMeasureUnit.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(conMeasureUnit.getStatus())){
                           // throw new BaseException("第"+num+"行,销售价单位必须是启用且已确认状态，导入失败");
                            errMsg=errMsg+"第"+num+"行,销售价单位必须是启用且已确认状态，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                            salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                            salSalePriceErrMsgResponse.setItemNum(num);
                            salSalePriceErrMsgResponse.setMsg("销售价单位必须是启用且已确认状态，导入失败");
                            msgList.add(salSalePriceErrMsgResponse);
                        }else{
                            unitPrice=conMeasureUnit.getCode();
                        }
                    }
                    if(objects.get(22) ==  null || objects.get(22) == ""){
                     //   throw new BaseException("第"+num+"行,单位换算比例(销售价单位/基本计量单位)不允许为空，导入失败");
                        errMsg=errMsg+"第"+num+"行,单位换算比例(销售价单位/基本计量单位)不允许为空，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("单位换算比例(销售价单位/基本计量单位)不允许为空，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                }
                if(objects.get(22) !=  null && objects.get(22) != ""){
                    if(objects.get(21) ==  null || objects.get(21) == ""){
                       // throw new BaseException("第"+num+"行,销售价单位不允许为空，导入失败");
                        errMsg=errMsg+"第"+num+"行,销售价单位不允许为空，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("销售价单位不允许为空，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                    boolean validDouble = JudgeFormat.isValidDoubleLgZero(objects.get(22).toString(),4,4);
                    if(!validDouble){
                       // throw new BaseException("第"+num+"行,单位换算比例,数据格式错误，导入失败");
                        errMsg=errMsg+"第"+num+"行,单位换算比例,数据格式错误，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(num);
                        salSalePriceErrMsgResponse.setMsg("单位换算比例,数据格式错误，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                }
                /*
                 * 报价(含税) 选填
                 */
                String quotePriceTaxS = objects.get(23) == null || objects.get(23) == "" ? null : objects.get(23).toString().trim();
                BigDecimal quotePriceTax = null;
                if (StrUtil.isNotBlank(quotePriceTaxS)) {
                    if (!JudgeFormat.isValidDouble(quotePriceTaxS,10,5)){
                        salSalePriceErrMsgResponse errMsgResponse = new salSalePriceErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("报价(含税)格式错误，导入失败！");
                        msgList.add(errMsgResponse);
                    } else {
                        quotePriceTax = new BigDecimal(quotePriceTaxS);
                        if (quotePriceTax != null && BigDecimal.ZERO.compareTo(quotePriceTax) > 0) {
                            salSalePriceErrMsgResponse errMsgResponse = new salSalePriceErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("报价(含税)格式错误，导入失败！");
                            msgList.add(errMsgResponse);
                        }
                        quotePriceTax = quotePriceTax.divide(BigDecimal.ONE,5,BigDecimal.ROUND_HALF_UP);
                    }
                }
                /*
                 * 价格说明 选填
                 */
                String priceRemark = objects.get(24) == null || objects.get(24) == "" ? null : objects.get(24).toString().trim();

                SalSalePrice sale = new SalSalePrice();
                sale.setCustomerSid(customerSid)
                        .setMaterialCode(materialCode)
                        .setMaterialCategory(materialCategory)
                        .setImportHandle(ConstantsEms.IMPORT)
                        .setCompanySid(null)
                        .setSaleMode(priceTypeValue)
                        .setSku1Sid(sku1Sid)
                        .setStatus(ConstantsEms.SAVA_STATUS)
                        .setMaterialSid(materialSid)
                        .setSkuTypeRecursion((objects.get(12)==""||objects.get(12)==null)?null:skuTypeMaps.get(objects.get(12).toString()))
                        .setRawMaterialMode(rawMaterialModeValue)
                        .setPriceDimension(priceDimensionValue)
                        .setRemark((objects.get(25)==""||objects.get(25)==null)?null:objects.get(25).toString())
                        .setHandleStatus(ConstantsEms.SAVA_STATUS);
                List<SalSalePriceItem> salSalePriceItems = new ArrayList<>();
                SalSalePriceItem salSalePriceItem = new SalSalePriceItem();
                if((objects.get(9)!=""&&objects.get(9)!=null)){
                    boolean validDouble = isValidDouble(objects.get(9).toString());
                    if(validDouble){
                        price = BigDecimal.valueOf(Double.valueOf(objects.get(9).toString()));
                        priceTax = price.divide(BigDecimal.ONE, 5, BigDecimal.ROUND_HALF_UP);
                    }
                }
                salSalePriceItem.setStartDate((objects.get(7)==""||objects.get(7)==null)?null:DateUtils.parseDate(objects.get(7).toString()))
                        .setEndDate((objects.get(8)==""||objects.get(8)==null)?null:DateUtils.parseDate(objects.get(8).toString()))
                        .setSalePriceTax((objects.get(9)==""||objects.get(9)==null)?null:priceTax)
                        .setQuotePriceTax(quotePriceTax).setPriceRemark(priceRemark)
                        .setSkuTypeRecursion((objects.get(12)==""||objects.get(12)==null)?null:skuTypeMaps.get(objects.get(12).toString()))
                        .setUnitBase(basMaterial==null?null:basMaterial.getUnitBase())
                        .setCurrency("CNY")
                        .setHandleStatus(ConstantsEms.SAVA_STATUS)
                        .setCurrencyUnit("YUAN")
                        .setPriceEnterMode("HS")
                        .setUnitPrice((objects.get(21)==""||objects.get(21)==null)?null:unitPrice)
                        .setUnitConversionRate((objects.get(22)==""||objects.get(22)==null)?null:isValidDouble(objects.get(22).toString())?BigDecimal.valueOf(Double.valueOf(objects.get(22).toString())):null)
                        .setTaxRate((objects.get(10)==""||objects.get(10)==null)?null:taxRateMaps.get(objects.get(10).toString()))
                        .setIsRecursionPrice((objects.get(11)==""||objects.get(11)==null)?null:yesMaps.get(objects.get(11).toString()))
                        .setIncreQuantity((objects.get(13)==""||objects.get(13)==null)?null:isValidDouble(objects.get(13).toString())?BigDecimal.valueOf(Double.valueOf(objects.get(13).toString())):null)
                        .setIncrePriceTax((objects.get(14)==""||objects.get(14)==null)?null:isValidDouble(objects.get(14).toString())?BigDecimal.valueOf(Double.valueOf(objects.get(14).toString())):null)
                        .setDecreQuantity((objects.get(15)==""||objects.get(15)==null)?null:isValidDouble(objects.get(15).toString())?BigDecimal.valueOf(Double.valueOf(objects.get(15).toString())):null)
                        .setDecrePriceTax((objects.get(16)==""||objects.get(16)==null)?null:isValidDouble(objects.get(16).toString())?BigDecimal.valueOf(Double.valueOf(objects.get(16).toString())):null)
                        .setReferQuantity((objects.get(17)==""||objects.get(17)==null)?null:isValidDouble(objects.get(17).toString())?BigDecimal.valueOf(Double.valueOf(objects.get(17).toString())):null)
                        .setPriceMinQuantity((objects.get(18)==""||objects.get(18)==null)?null:isValidDouble(objects.get(18).toString())?BigDecimal.valueOf(Double.valueOf(objects.get(18).toString())):null)
                        .setRoundingType((objects.get(19)==""||objects.get(19)==null)?null:roundingTypeMaps.get(objects.get(19).toString()))
                        .setUnitRecursion((objects.get(20)==""||objects.get(20)==null)?null:measureUnitMaps.get(objects.get(20).toString()));
                if(salSalePriceItem.getUnitPrice()==null&&salSalePriceItem.getUnitConversionRate()==null){
                    salSalePriceItem.setUnitPrice(salSalePriceItem.getUnitBase());
                    salSalePriceItem.setUnitConversionRate(BigDecimal.ONE);
                }
                salSalePriceItems.add(salSalePriceItem);
                try {
                    setUnitImport(salSalePriceItems,num);
                }catch (CustomException e){
                    errMsg=errMsg+"第"+num+"行,销售价单位“与”基本计量单位“不一致，单位换算比例不允许为空,不允许导入"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                    salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                    salSalePriceErrMsgResponse.setItemNum(num);
                    salSalePriceErrMsgResponse.setMsg("销售价单位“与”基本计量单位“不一致，单位换算比例不允许为空,不允许导入");
                    msgList.add(salSalePriceErrMsgResponse);
                }
                sale.setListSalSalePriceItem(salSalePriceItems);
                salSalePriceList.add(sale);
            }
            if(CollectionUtil.isNotEmpty(msgList)){
                return  AjaxResult.error("报错信息",msgList);
            }
            List<String> hashCode=new ArrayList<>();
            salSalePriceList.forEach(item->{
                Long customerSid=0L;
                Long skuSid=0L;
                if(item.getCustomerSid()!=null){
                    customerSid=item.getCustomerSid();
                }
                if(item.getSku1Sid()!=null){
                    skuSid=item.getSku1Sid();
                }
                String code=skuSid+""+customerSid+""+item.getMaterialSid()+""+item.getRawMaterialMode()+""+item.getSaleMode();
                hashCode.add(code);
            });
            for (int i=0;i<hashCode.size();i++) {
                int  m=i;
                int sort=i+3;
                List<String> common = hashCode.stream().filter(li -> li.equals(hashCode.get(m))).collect(Collectors.toList());
                if(common.size()>1){
                    //throw new BaseException("第"+sort+"行,当前存在进行中的明细数据，导入失败");
                    errMsg=errMsg+"第"+sort+"行,表格内存在多笔有效期不同的数据，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                    salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                    salSalePriceErrMsgResponse.setItemNum(sort);
                    salSalePriceErrMsgResponse.setMsg("表格内存在多笔有效期不同的数据，导入失败");
                    msgList.add(salSalePriceErrMsgResponse);
                }
            }
            for (int i=0;i<salSalePriceList.size();i++){
                int sort=i+3;
                List<SalSalePriceItem> priceList = new ArrayList<>();
                Long customerSid=0L;
                if(salSalePriceList.get(i).getCustomerSid()!=null){
                    customerSid=salSalePriceList.get(i).getCustomerSid();
                    String code=customerSid+salSalePriceList.get(i).getMaterialSid()+salSalePriceList.get(i).getRawMaterialMode()+salSalePriceList.get(i).getSaleMode();
                    for (int j=0;j<salSalePriceList.size();j++){
                        if(ConstantsEms.PRICE_K.equals(salSalePriceList.get(j).getPriceDimension())&&i!=j){
                            Long customerSidCom=0L;
                            if(salSalePriceList.get(j).getCustomerSid()!=null){
                                customerSidCom=salSalePriceList.get(j).getCustomerSid();
                            }
                            String codeMon=customerSidCom+salSalePriceList.get(j).getMaterialSid()+salSalePriceList.get(j).getRawMaterialMode()+salSalePriceList.get(j).getSaleMode();
                            if(code.equals(codeMon)){
                                List<SalSalePriceItem> listSalSalePriceItem = salSalePriceList.get(j).getListSalSalePriceItem();
                                priceList.add(listSalSalePriceItem.get(0));
                            }
                        }
                    }

                }else{
                    String code=customerSid+salSalePriceList.get(i).getMaterialSid()+salSalePriceList.get(i).getRawMaterialMode()+salSalePriceList.get(i).getSaleMode();
                    for (int j=0;j<salSalePriceList.size();j++){
                        if(ConstantsEms.PRICE_K.equals(salSalePriceList.get(j).getPriceDimension())&&i!=j){
                            Long customerSidCom=0L;
                            if(salSalePriceList.get(j).getCustomerSid()!=null){
                                customerSidCom=salSalePriceList.get(j).getCustomerSid();
                            }
                            String codeMon=customerSidCom+salSalePriceList.get(j).getMaterialSid()+salSalePriceList.get(j).getRawMaterialMode()+salSalePriceList.get(j).getSaleMode();
                            if(code.equals(codeMon)){
                                List<SalSalePriceItem> listSalSalePriceItem = salSalePriceList.get(j).getListSalSalePriceItem();
                                priceList.add(listSalSalePriceItem.get(0));
                            }
                        }
                    }
                }
                List<SalSalePriceItem> listSalSalePriceItem = salSalePriceList.get(i).getListSalSalePriceItem();
                if(CollectionUtil.isNotEmpty(priceList)){
                    boolean judge = validTimeOther(listSalSalePriceItem.get(0),priceList);
                    if(!judge){
                        // throw new BaseException("第"+sort+"行,有效期存在交集，导入失败");
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(sort);
                        salSalePriceErrMsgResponse.setMsg("与表格内数据，有效期存在交集，导入失败");
                        errMsg=errMsg+"第"+sort+"行,与表格内数据，有效期存在交集，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        msgList.add(salSalePriceErrMsgResponse);
                    }
                }
            }
//            if(errMsg!=""){
//                String addmsg="<br>&nbsp;&nbsp;&nbsp;&nbsp;";
//                throw new BaseException(addmsg+errMsg);
//            }
            if(CollectionUtil.isNotEmpty(msgList)){
                return  AjaxResult.error("报错信息",msgList);
            }
            for (int i=0;i<salSalePriceList.size();i++){
                AjaxResult result = judgeAdd(salSalePriceList.get(i));
                Object msg = result.get("msg");
                int sort=i+3;
                if(ConstantsEms.SAVA_STATUS.equals(msg.toString())){
                    Long sid = Long.valueOf(result.get("data").toString());
                    SalSalePrice salSalePrice = selectSalSalePriceById(sid);
                    List<SalSalePriceItem> list = salSalePrice.getListSalSalePriceItem();
                    List<SalSalePriceItem> listSaleItem = salSalePriceList.get(i).getListSalSalePriceItem();
                    list.add(listSaleItem.get(0));
                    salSalePrice.setListSalSalePriceItem(list);
                    try {
                        List<SalSalePriceItem> priceItems = list.stream().filter(li -> ConstantsEms.SAVA_STATUS.equals(li.getHandleStatus())).collect(Collectors.toList());
                        if(priceItems.size()>1){
                            //throw new BaseException("第"+sort+"行,当前存在进行中的明细数据，导入失败");
                            salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                            salSalePriceErrMsgResponse.setItemNum(sort);
                            salSalePriceErrMsgResponse.setMsg("系统内存在进行中的价格数据，导入失败");
                            msgList.add(salSalePriceErrMsgResponse);
                            errMsg=errMsg+"第"+sort+"行,系统内存在进行中的价格数据，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        }
                        if(priceItems.size()==1){
                            List<SalSalePriceItem> items = list.stream().filter(li -> ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())).collect(Collectors.toList());
                            if(CollectionUtil.isNotEmpty(items)){
                                if(items.size()!=list.size()-1){
                                  //  throw new BaseException("第"+sort+"行,当前存在进行中的明细数，导入失败");
                                    salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                                    salSalePriceErrMsgResponse.setItemNum(sort);
                                    salSalePriceErrMsgResponse.setMsg("系统内存在进行中的价格数据，导入失败");
                                    msgList.add(salSalePriceErrMsgResponse);
                                    errMsg=errMsg+"第"+sort+"行,系统内存在进行中的价格数据，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                                }
                            }else{
                                if(list.size()!=1){
                                    salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                                    salSalePriceErrMsgResponse.setItemNum(sort);
                                    salSalePriceErrMsgResponse.setMsg("系统内存在进行中的价格数据，导入失败");
                                    msgList.add(salSalePriceErrMsgResponse);
                                   // throw new BaseException("第"+sort+"行,当前存在进行中的明细数，导入失败");
                                    errMsg=errMsg+"第"+sort+"行,系统内存在进行中的价格数据，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                                }
                            }
                        }
                        judgeImport(salSalePrice);
                    }catch (CustomException e){
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(sort);
                        salSalePriceErrMsgResponse.setMsg("与系统内数据，有效期存在交集，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                        errMsg=errMsg+"第"+sort+"行,与系统内数据，有效期存在交集，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        //throw new BaseException("第"+sort+"行,有效期存在交集，导入失败");
                    }
                }else{
                    try {
                        judgeImport(salSalePriceList.get(i));
                    }catch (CustomException e){
                        salSalePriceErrMsgResponse salSalePriceErrMsgResponse = new salSalePriceErrMsgResponse();
                        salSalePriceErrMsgResponse.setItemNum(sort);
                        salSalePriceErrMsgResponse.setMsg("与系统内数据，有效期存在交集，导入失败");
                        msgList.add(salSalePriceErrMsgResponse);
                       errMsg=errMsg+"第"+sort+"行,与系统内数据，有效期存在交集，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        //throw new BaseException("第"+sort+"行,有效期存在交集，导入失败");
                    }
                }
            }
//            if(errMsg!=""){
//                String addmsg="<br>&nbsp;&nbsp;&nbsp;&nbsp;";
//                throw new BaseException(addmsg+errMsg);
//            }
            if(CollectionUtil.isNotEmpty(msgList)){
              return  AjaxResult.error("报错信息",msgList);
            }
            for (int i=0;i<salSalePriceList.size();i++){
                AjaxResult result = judgeAdd(salSalePriceList.get(i));
                Object msg = result.get("msg");
                int sort=i+3;
                if(ConstantsEms.SAVA_STATUS.equals(msg.toString())){
                    Long sid = Long.valueOf(result.get("data").toString());
                    SalSalePrice salSalePrice = selectSalSalePriceById(sid);
                    List<SalSalePriceItem> list = salSalePrice.getListSalSalePriceItem();
                    List<SalSalePriceItem> listSaleItem = salSalePriceList.get(i).getListSalSalePriceItem();
                    list.add(listSaleItem.get(0).setImportHandle(ConstantsEms.IMPORT));
                    salSalePrice.setImportHandle(ConstantsEms.IMPORT);
                    salSalePrice.setListSalSalePriceItem(list);
                    AjaxResult res=null;
                    try {
                        List<SalSalePriceItem> priceItems = list.stream().filter(li -> ConstantsEms.SAVA_STATUS.equals(li.getHandleStatus())).collect(Collectors.toList());
                        if(priceItems.size()>1){
                            throw new BaseException("第"+sort+"行,当前存在进行中的明细数，导入失败");
                        }
                        if(priceItems.size()==1){
                            List<SalSalePriceItem> items = list.stream().filter(li -> ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())).collect(Collectors.toList());
                            if(CollectionUtil.isNotEmpty(items)){
                                if(items.size()!=list.size()-1){
                                    throw new BaseException("第"+sort+"行,当前存在进行中的明细数，导入失败");
                                }
                            }else{
                                if(list.size()!=1){
                                    throw new BaseException("第"+sort+"行,当前存在进行中的明细数，导入失败");
                                }
                            }
                        }
                        res = updateSalSalePriceNew(salSalePrice);
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
                        ajaxResult = insertSalSalePrice(salSalePriceList.get(i));
                    }catch (CustomException e){
                        throw new BaseException("第"+sort+"行,有效期存在交集，导入失败");
                    }
                }
            }
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }

        return AjaxResult.success(1);
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

    //导入校验
    public void setUnitImport(List<SalSalePriceItem> listPurPurchasePriceItem,int i){
        listPurPurchasePriceItem.forEach(li->{
            if(li.getUnitBase()!=null){
                if(li.getUnitBase().equals(li.getUnitPrice())){
                    li.setUnitConversionRate(BigDecimal.ONE);
                }else{
                    if(li.getUnitConversionRate()==null){
                        throw new  CustomException("第"+i+"行销售价单位“与”基本计量单位“不一致，单位换算比例不允许为空,不允许导入");
                    }
                }
            }
        });
    }
    //校验输入日期的合法性
    public static boolean isValidDate(String str) {
        boolean convertSuccess = true;
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
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

}

