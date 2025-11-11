package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
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
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.domain.dto.response.PurOutsourcePurchasePriceResponse;
import com.platform.ems.domain.dto.response.PurOutsourceReportResponse;
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
import com.platform.ems.workflow.domain.Submit;
import com.platform.ems.workflow.service.IWorkFlowService;
import com.platform.api.service.RemoteFlowableService;
import com.platform.flowable.domain.vo.FormParameter;
import com.platform.system.domain.SysBusinessBcst;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysBusinessBcstMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 加工采购价主Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-12
 */
@Service
@SuppressWarnings("all")
public class PurOutsourcePurchasePriceServiceImpl extends ServiceImpl<PurOutsourcePurchasePriceMapper, PurOutsourcePurchasePrice> implements IPurOutsourcePurchasePriceService {
    @Autowired
    private PurOutsourcePurchasePriceMapper purOutsourcePurchasePriceMapper;
    @Autowired
    private PurOutsourcePurchasePriceAttachmentMapper attachmentMapper;
    @Autowired
    private PurOutsourcePurchasePriceItemMapper itemMapper;
    @Autowired
    private BasPlantMapper basPlantMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private BasMaterialBarcodeMapper basMaterialBarcodeMapper;
    @Autowired
    private ManManufactureOrderProcessMapper manManufactureOrderProcessMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private ConTaxRateMapper conTaxRateMapper;
    @Autowired
    private ConMeasureUnitMapper conMeasureUnitMapper;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private BasSkuMapper basSkuMapper;
    @Autowired
    private ManProcessMapper manProcessMapper;
    @Autowired
    private ISysFormProcessService formProcessService;
    @Autowired
    private ISystemUserService userService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private SysBusinessBcstMapper sysBusinessBcstMapper;
    @Autowired
    private RemoteFlowableService flowableService;
    @Autowired
    private IWorkFlowService workflowService;
    @Autowired
    private BasMaterialSkuMapper basMaterialSkuMapper;
    @Autowired
    IPurOutsourceQuoteBargainService purOutsourceQuoteBargainService;
    @Autowired
    private PurOutsourceQuoteBargainItemMapper itemBargainMapper;

    private static final String TITLE = "加工采购价";

    /**
     * 查询加工采购价主
     *
     * @param outsourcePurchasePriceSid 加工采购价主ID
     * @return 加工采购价主
     */
    @Override
    public PurOutsourcePurchasePrice selectPurOutsourcePurchasePriceById(Long outsourcePurchasePriceSid) {
        PurOutsourcePurchasePrice purOutsourcePurchasePrice = purOutsourcePurchasePriceMapper.selectPurOutsourcePurchasePriceById(outsourcePurchasePriceSid);
        if (purOutsourcePurchasePrice != null) {
            //取详情时将图片路径分割出来存入数组
            if (StrUtil.isNotBlank(purOutsourcePurchasePrice.getPicturePathSecond())) {
                String[] picturePathList = purOutsourcePurchasePrice.getPicturePathSecond().split(";");
                purOutsourcePurchasePrice.setPicturePathList(picturePathList);
            }
            List<PurOutsourcePurchasePriceAttachment> attachmentList = attachmentMapper.selectPurOutsourcePurchasePriceAttachmentList(
                    new PurOutsourcePurchasePriceAttachment().setOutsourcePurchasePriceSid(outsourcePurchasePriceSid));
            purOutsourcePurchasePrice.setAttachmentList(attachmentList);
            List<PurOutsourcePurchasePriceItem> itemList = itemMapper.selectPurOutsourcePurchasePriceItemList(
                    new PurOutsourcePurchasePriceItem().setOutsourcePurchasePriceSid(outsourcePurchasePriceSid));
            if(CollectionUtil.isNotEmpty(itemList)){
                itemList=itemList.stream().sorted(Comparator.comparing(PurOutsourcePurchasePriceItem::getStartDate).reversed()).collect(Collectors.toList());
            }
            purOutsourcePurchasePrice.setItemList(itemList);
            SysFormProcess formProcess = new SysFormProcess();
            formProcess.setFormId(outsourcePurchasePriceSid);
            List<SysFormProcess> list = formProcessService.selectSysFormProcessList(formProcess);
            if (list != null && list.size() > 0) {
                formProcess = new SysFormProcess();
                formProcess = list.get(0);
                purOutsourcePurchasePrice.setApprovalNode(formProcess.getApprovalNode());
                purOutsourcePurchasePrice.setApprovalUserId(formProcess.getApprovalUserId());
                purOutsourcePurchasePrice.setApprovalUserName(formProcess.getApprovalUserName());
                purOutsourcePurchasePrice.setSubmitDate(formProcess.getCreateDate());
                purOutsourcePurchasePrice.setSubmitUserName(userService.selectSysUserById(Long.valueOf(formProcess.getCreateById())).getNickName());
            }
        }
        MongodbUtil.find(purOutsourcePurchasePrice);
        return purOutsourcePurchasePrice;
    }

    /**
     * 不含税值计算
     */
    public void changePrice(List<PurOutsourcePurchasePriceItem> itemList) {
        if (CollectionUtil.isNotEmpty(itemList)) {
            itemList.forEach(li -> {
                if (li.getTaxRate() != null) {
                    if (li.getPurchasePriceTax() != null) {
                        li.setPurchasePrice(li.getPurchasePriceTax().divide(BigDecimal.ONE.add(li.getTaxRate()), 6, BigDecimal.ROUND_HALF_UP));
                    }
                }
                if (li.getInnerCheckPriceTax() != null) {
                    if (li.getTaxRate() != null) {
                        li.setInnerCheckPrice(li.getInnerCheckPriceTax().divide(li.getTaxRate().add(BigDecimal.ONE), 6, BigDecimal.ROUND_HALF_UP));
                    }
                    else {
                        li.setInnerCheckPrice(li.getInnerCheckPriceTax());
                    }
                }
            });
        }
    }
    /**
     * 行号赋值
     */
    public void  setItemNum(List<PurOutsourcePurchasePriceItem> list){
        int size = list.size();
        if(size>0){
            for (int i=1;i<=size;i++){
                list.get(i-1).setItemNum(i);
                if (list.get(i-1).getInnerCheckPriceTax() != null) {
                    if (list.get(i-1).getTaxRate() != null) {
                        list.get(i-1).setInnerCheckPrice(list.get(i-1).getInnerCheckPriceTax()
                                .divide(list.get(i-1).getTaxRate().add(BigDecimal.ONE), 6, BigDecimal.ROUND_HALF_UP));
                    }
                    else {
                        list.get(i-1).setInnerCheckPrice(list.get(i-1).getInnerCheckPriceTax());
                    }
                }
            }
        }
    }
    @Override
    public List<ManManufactureOrderProcess> getPrice(PurOutsourcePurchasePriceResponse response) {
        //生产订单工序list
        List<ManManufactureOrderProcess> processList = response.getProcessList();
        if (CollectionUtil.isNotEmpty(processList)) {
            for (ManManufactureOrderProcess process : processList) {
                //按加工商 + 商品 + 工序维度获取加工采购价
                List<PurOutsourcePurchasePrice> purchasePriceList = purOutsourcePurchasePriceMapper.selectList(new QueryWrapper<PurOutsourcePurchasePrice>().lambda()
                        .eq(PurOutsourcePurchasePrice::getVendorSid, response.getVendorSid())
                        .eq(PurOutsourcePurchasePrice::getMaterialSid, process.getMaterialSid())
                        .eq(PurOutsourcePurchasePrice::getProcessSid, process.getProcessSid()));
                if (CollectionUtil.isNotEmpty(purchasePriceList)) {
                    List<Long> priceSidList = purchasePriceList.stream().map(PurOutsourcePurchasePrice::getOutsourcePurchasePriceSid).collect(Collectors.toList());
                    for (Long sid : priceSidList) {
                        //获取确认、有效期内加工采购价
                        List<PurOutsourcePurchasePriceItem> priceItemList = itemMapper.selectList(new QueryWrapper<PurOutsourcePurchasePriceItem>().lambda()
                                .le(PurOutsourcePurchasePriceItem::getStartDate, new Date())
                                .ge(PurOutsourcePurchasePriceItem::getEndDate, new Date())
                                .ge(PurOutsourcePurchasePriceItem::getHandleStatus, ConstantsEms.CHECK_STATUS)
                                .eq(PurOutsourcePurchasePriceItem::getOutsourcePurchasePriceSid, sid));
                        if (CollectionUtil.isNotEmpty(priceItemList)) {
                            PurOutsourcePurchasePriceItem item = itemMapper.selectPurOutsourcePurchasePriceItemById(priceItemList.get(0).getOutsourcePurchasePriceItemSid());
                            process.setPurOutsourcePurchasePriceItem(item);
                        }
                    }
                }
            }
        }
        return processList;
    }

    /**
     * 查询加工采购价主列表
     *
     * @param purOutsourcePurchasePrice 加工采购价主
     * @return 加工采购价主
     */
    @Override
    public List<PurOutsourcePurchasePrice> selectPurOutsourcePurchasePriceList(PurOutsourcePurchasePrice purOutsourcePurchasePrice) {
        List<PurOutsourcePurchasePrice> purOutsourcePurchasePrices = purOutsourcePurchasePriceMapper.selectPurOutsourcePurchasePriceList(purOutsourcePurchasePrice);
        purOutsourcePurchasePrices.forEach(li -> {
            SysFormProcess formProcess = new SysFormProcess();
            formProcess.setFormId(li.getOutsourcePurchasePriceSid());
            List<SysFormProcess> list = formProcessService.selectSysFormProcessList(formProcess);
            if (list != null && list.size() > 0) {
                formProcess = new SysFormProcess();
                formProcess = list.get(0);
                li.setApprovalNode(formProcess.getApprovalNode());
                li.setApprovalUserId(formProcess.getApprovalUserId());
                li.setApprovalUserName(formProcess.getApprovalUserName());
                li.setSubmitDate(formProcess.getCreateDate());
                li.setSubmitUserName(userService.selectSysUserById(Long.valueOf(formProcess.getCreateById())).getNickName());
            }
        });
        return purOutsourcePurchasePrices;
    }

    /**
     * 查询加工采购价主报表
     *
     * @param purOutsourcePurchasePrice 查询加工采购价主报表
     * @return 加工采购价主
     */
    @Override
    public List<PurOutsourceReportResponse> report(PurOutsourceReportResponse purOutsourcePurchasePrice) {
        List<PurOutsourceReportResponse> purOutsourceReportResponses = itemMapper.reportPurOutsourcePurchasePrice(purOutsourcePurchasePrice);
        purOutsourceReportResponses.forEach(li -> {
            if (li.getPurchasePriceTax() != null) {
                li.setPurchasePriceTaxS(removeZero(li.getPurchasePriceTax().toString()));
            }
            if (li.getPurchasePrice() != null) {
                li.setPurchasePriceS(removeZero(li.getPurchasePrice().toString()));
            }
            if (li.getUnitConversionRate() != null) {
                li.setUnitConversionRateS(removeZero(li.getUnitConversionRate().toString()));
            }
        });
        return purOutsourceReportResponses;
    }

    public String removeZero(String s) {
        if (s.indexOf(".") > 0) {
            //正则表达
            s = s.replaceAll("0+?$", "");//去掉后面无用的零
            s = s.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
        }
        return s;
    }

    public void judgeNull(PurOutsourcePurchasePrice purOutsourcePurchasePrice) {
        String handleStatus = purOutsourcePurchasePrice.getHandleStatus();
            List<PurOutsourcePurchasePriceItem> itemList = purOutsourcePurchasePrice.getItemList();
            if (CollectionUtil.isNotEmpty(itemList)) {
                itemList.forEach(li -> {
                    if (li.getPurchasePriceTax() == null || purOutsourcePurchasePrice.getProcessSid() == null) {
                        throw new CustomException("明细行存在加工采购价/加工项未维护，请填写后再点击确认");
                    }
                });
            }else{
                throw new CustomException("明细行不允许为空");
            }

    }

    /**
     * 新增加工采购价主 校验
     *
     * @param purOutsourcePurchasePrice 加工采购价主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String judgeAdd(PurOutsourcePurchasePrice purOutsourcePurchasePrice) {
        String code = "500";
        BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                .eq(BasMaterial::getMaterialCode, purOutsourcePurchasePrice.getMaterialCode()));
        if (basMaterial != null) {
            String handleStatus = basMaterial.getHandleStatus();
            String status = basMaterial.getStatus();
            if (!ConstantsEms.ENABLE_STATUS.equals(status)) {
                throw new CustomException("输入的商品/物料编码已停用，请检查！");
            }
            if (!ConstantsEms.CHECK_STATUS.equals(handleStatus)) {
                throw new CustomException("输入的商品/物料编码非确认状态，请检查！");
            }
        } else {
            throw new CustomException("输入的商品/物料编码不存在，请检查！");
        }
        //校验是否已配置相同价格
        if (purOutsourcePurchasePrice.getSku1Sid() != null) {
            PurOutsourcePurchasePrice queryResult = purOutsourcePurchasePriceMapper.selectOne(new QueryWrapper<PurOutsourcePurchasePrice>().lambda()
                    .eq(PurOutsourcePurchasePrice::getVendorSid, purOutsourcePurchasePrice.getVendorSid())
                    .eq(PurOutsourcePurchasePrice::getMaterialSid, basMaterial.getMaterialSid())
                    .eq(PurOutsourcePurchasePrice::getProcessSid, purOutsourcePurchasePrice.getProcessSid())
                    .eq(PurOutsourcePurchasePrice::getSku1Sid, purOutsourcePurchasePrice.getSku1Sid()));
            if (queryResult != null) {
                return queryResult.getOutsourcePurchasePriceSid().toString();
            }
        }else {
            List<PurOutsourcePurchasePrice> queryResultList = purOutsourcePurchasePriceMapper.selectList(new QueryWrapper<PurOutsourcePurchasePrice>().lambda()
                    .eq(PurOutsourcePurchasePrice::getVendorSid, purOutsourcePurchasePrice.getVendorSid())
                    .eq(PurOutsourcePurchasePrice::getMaterialSid, basMaterial.getMaterialSid())
                    .eq(PurOutsourcePurchasePrice::getProcessSid, purOutsourcePurchasePrice.getProcessSid()))
                    .stream().filter(o -> o.getSku1Sid() == null).collect(Collectors.toList());
            if (queryResultList.size() == 1) {
                return queryResultList.get(0).getOutsourcePurchasePriceSid().toString();
            }
        }
        return code;
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(PurOutsourcePurchasePrice purchasePrice) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, purchasePrice.getOutsourcePurchasePriceSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, purchasePrice.getOutsourcePurchasePriceSid()));
        }
    }

    /**
     * 新增加工采购价主
     * 需要注意编码重复校验
     *
     * @param purOutsourcePurchasePrice 加工采购价主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurOutsourcePurchasePrice(PurOutsourcePurchasePrice purOutsourcePurchasePrice) {
        //校验明细表时间是否有交集
        List<PurOutsourcePurchasePriceItem> itemList = purOutsourcePurchasePrice.getItemList();
        if(CollectionUtils.isNotEmpty(itemList)){
            setUnit(itemList);
            setItemNum(itemList);
            changePrice(itemList);
            if(!validTime(itemList)){
                throw new BaseException("价格明细配置时间存在交集,请修改后再试");
            }
            itemList.forEach(li -> {
                //第二层校验
                judgeTime(purOutsourcePurchasePrice, li);
            });
        }
        judgeNull(purOutsourcePurchasePrice);
        checkhandleStatus(purOutsourcePurchasePrice);
        int row = purOutsourcePurchasePriceMapper.insert(purOutsourcePurchasePrice);
        itemList.forEach(o->{
            if(purOutsourcePurchasePrice.getImportHandle()==null){
                //插入日志
                MongodbUtil.insertUserLogItem(purOutsourcePurchasePrice.getOutsourcePurchasePriceSid(), BusinessType.INSERT.getValue(),TITLE,o.getItemNum());
            }else{
                //插入日志
                MongodbUtil.insertUserLogItem(purOutsourcePurchasePrice.getOutsourcePurchasePriceSid(), BusinessType.IMPORT.getValue(),TITLE,o.getItemNum());
            }
        });
        if (row > 0) {
            List<PurOutsourcePurchasePriceAttachment> attachmentList = purOutsourcePurchasePrice.getAttachmentList();
            insertAttachment(purOutsourcePurchasePrice.getOutsourcePurchasePriceSid(), attachmentList);
            insertItem(purOutsourcePurchasePrice.getOutsourcePurchasePriceSid(), itemList);
        }
        //待办通知
        PurOutsourcePurchasePrice purchasePrice = purOutsourcePurchasePriceMapper.selectById(purOutsourcePurchasePrice.getOutsourcePurchasePriceSid());
        itemList.forEach(li->{
            SysTodoTask sysTodoTask = new SysTodoTask();
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsEms.TABLE_MANUFACTURE_ORDER)
                    .setDocumentItemSid(li.getOutsourcePurchasePriceItemSid())
                    .setDocumentSid(li.getOutsourcePurchasePriceSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                sysTodoTask.setTitle("加工采购价" + purchasePrice.getOutsourcePurchasePriceCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(purchasePrice.getOutsourcePurchasePriceCode().toString())
                        .setNoticeDate(new Date())
                        .setMenuId(ConstantsWorkbench.pur_outsource_purchase_price)
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        });
        return row;
    }

    /**
     * 校验明细表的时间是否有交集
     *
     * @param itemList
     * @return
     */
    private static boolean validTime(List<PurOutsourcePurchasePriceItem> itemList) {
        for (int i = 0; i < itemList.size(); i++) {
            PurOutsourcePurchasePriceItem item = itemList.get(i);
            for (int j = 0; j < itemList.size(); j++) {
                if (i >= j) {
                    continue;
                }
                PurOutsourcePurchasePriceItem compareItem = itemList.get(j);
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
     * 修改加工采购价主
     *
     * @param purOutsourcePurchasePrice 加工采购价主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurOutsourcePurchasePrice(PurOutsourcePurchasePrice purOutsourcePurchasePrice) {
        judgeNull(purOutsourcePurchasePrice);
        checkhandleStatus(purOutsourcePurchasePrice);
        int row = purOutsourcePurchasePriceMapper.updateAllById(purOutsourcePurchasePrice);
        if (row > 0) {
            List<PurOutsourcePurchasePriceAttachment> attachmentList = purOutsourcePurchasePrice.getAttachmentList();
            if (CollectionUtils.isNotEmpty(attachmentList)) {
                attachmentList.forEach(a -> {
                    if (a.getUpdateDate() == null) {
                        a.setUpdateDate(new Date());
                    }
                    if (StrUtil.isEmpty(a.getCreatorAccount())) {
                        a.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                    }
                });
                insertAttachment(purOutsourcePurchasePrice.getOutsourcePurchasePriceSid(), attachmentList);
            }
            List<PurOutsourcePurchasePriceItem> itemList = purOutsourcePurchasePrice.getItemList();
            changePrice(itemList);
            if (CollectionUtils.isNotEmpty(itemList)) {
                //校验明细表时间是否有交集
                if (!validTime(itemList)) {
                    throw new BaseException("价格明细配置时间存在交集,请修改后再试");
                }
                itemList.forEach(a -> {
                    //第二层校验
                    judgeTime(purOutsourcePurchasePrice, a);
                    if (a.getUpdateDate() == null) {
                        a.setUpdateDate(new Date());
                    }
                    if (StrUtil.isEmpty(a.getCreatorAccount())) {
                        a.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                    }
                });
                insertItem(purOutsourcePurchasePrice.getOutsourcePurchasePriceSid(), itemList);
            }

            //插入日志
            MongodbUtil.insertUserLog(purOutsourcePurchasePrice.getOutsourcePurchasePriceSid(), BusinessType.UPDATE.getValue(), null, TITLE);
        }
        if (!ConstantsEms.SAVA_STATUS.equals(purOutsourcePurchasePrice.getHandleStatus())) {
            //校验是否存在待办
            checkTodoExist(purOutsourcePurchasePrice);
        }
        //更新通知
        if (ConstantsEms.CHECK_STATUS.equals(purOutsourcePurchasePrice.getHandleStatus())) {
            SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
            sysBusinessBcst.setTitle("商品编码" + purOutsourcePurchasePrice.getMaterialCode() + "，加工项" + purOutsourcePurchasePrice.getProcessName() + ",加工采购价编号" + purOutsourcePurchasePrice.getOutsourcePurchasePriceCode() + "的信息发生变更，请知悉！")
                    .setDocumentSid(purOutsourcePurchasePrice.getOutsourcePurchasePriceSid())
                    .setDocumentCode(purOutsourcePurchasePrice.getOutsourcePurchasePriceCode().toString())
                    .setNoticeDate(new Date()).setUserId(ApiThreadLocalUtil.get().getUserid());
            sysBusinessBcstMapper.insert(sysBusinessBcst);
        }
        return row;
    }

    /**
     * 修改加工采购价主-新
     *
     * @param purOutsourcePurchasePrice 加工采购价主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurOutsourcePurchasePriceNew(PurOutsourcePurchasePrice purOutsourcePurchasePrice) {
        judgeNull(purOutsourcePurchasePrice);
        checkhandleStatus(purOutsourcePurchasePrice);
        int row = purOutsourcePurchasePriceMapper.updateAllById(purOutsourcePurchasePrice);
        if (row > 0) {
            List<PurOutsourcePurchasePriceAttachment> attachmentList = purOutsourcePurchasePrice.getAttachmentList();
            if (CollectionUtils.isNotEmpty(attachmentList)) {
                attachmentList.forEach(a -> {
                    if (a.getUpdateDate() == null) {
                        a.setUpdateDate(new Date());
                    }
                    if (StrUtil.isEmpty(a.getCreatorAccount())) {
                        a.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                    }
                });
                insertAttachment(purOutsourcePurchasePrice.getOutsourcePurchasePriceSid(), attachmentList);
            }
            List<PurOutsourcePurchasePriceItem> itemList = purOutsourcePurchasePrice.getItemList();
            if(CollectionUtils.isNotEmpty(itemList)){
                if(itemList.size()==1){
                    itemList.forEach(li->{
                        li.setSubmitHandle(ConstantsEms.YES);
                    });
                }
                setUnit(itemList);
//                setItemNum(itemList);
                changePrice(itemList);
                //校验明细表时间是否有交集
                if (!validTime(itemList)) {
                    throw new BaseException("价格明细配置时间存在交集,请修改后再试");
                }
                itemList.forEach(a -> {
                    //第二层校验
                    judgeTime(purOutsourcePurchasePrice,a);
                    if (StrUtil.isEmpty(a.getCreatorAccount())) {
                        a.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                    }
                });
                if(ConstantsEms.CHECK_STATUS.equals(purOutsourcePurchasePrice.getHandleStatus())){
                    MongodbUtil.insertUserLog(purOutsourcePurchasePrice.getOutsourcePurchasePriceSid(), BusinessType.CHANGE.getValue(),TITLE);
                }
                List<PurOutsourcePurchasePriceItem> purOutsourcePurchasePriceItems = itemMapper.selectList(new QueryWrapper<PurOutsourcePurchasePriceItem>().lambda()
                        .eq(PurOutsourcePurchasePriceItem::getOutsourcePurchasePriceSid, purOutsourcePurchasePrice.getOutsourcePurchasePriceSid())
                );
                List<Long> longs = purOutsourcePurchasePriceItems.stream().map(li -> li.getOutsourcePurchasePriceItemSid()).collect(Collectors.toList());
                List<Long> longsNow = itemList.stream().map(li -> li.getOutsourcePurchasePriceItemSid()).collect(Collectors.toList());
                //两个集合取差集
                List<Long> reduce = longs.stream().filter(item -> !longsNow.contains(item)).collect(Collectors.toList());
                //删除明细
                if(CollectionUtil.isNotEmpty(reduce)){
                    List<PurOutsourcePurchasePriceItem> reduceList = itemMapper.selectList(new QueryWrapper<PurOutsourcePurchasePriceItem>().lambda()
                            .in(PurOutsourcePurchasePriceItem::getOutsourcePurchasePriceItemSid, reduce)
                    );
                    reduceList.forEach(li->{
                        //插入日志
                        MongodbUtil.insertUserLogItem(li.getOutsourcePurchasePriceSid(), BusinessType.DELETE.getValue(),TITLE,li.getItemNum());
                    });
                    itemMapper.deleteBatchIds(reduce);
                    //删除待办
                    sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                            .in(SysTodoTask::getDocumentItemSid, reduce));
                }
                //修改明细
                List<PurOutsourcePurchasePriceItem> exitItem = itemList.stream().filter(li -> li.getOutsourcePurchasePriceItemSid() != null).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(exitItem)) {
                    exitItem.forEach(li -> {
                        itemMapper.updateAllById(li);
                        PurOutsourcePurchasePriceItem oldItem = itemMapper.selectById(li.getOutsourcePurchasePriceItemSid());
                        if(ConstantsEms.YES.equals(li.getSubmitHandle())){
                            if(ConstantsEms.SAVA_STATUS.equals(li.getHandleStatus())){
                                MongodbUtil.insertUserLogItem(li.getOutsourcePurchasePriceSid(), BusinessType.UPDATE.getValue(),TITLE,li.getItemNum());
                            }
                        }
                    });
                }
                //新增明细
                List<PurOutsourcePurchasePriceItem> nullItem = itemList.stream().filter(li -> li.getOutsourcePurchasePriceItemSid() == null).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(nullItem)) {
                    int max = purOutsourcePurchasePriceItems.stream().mapToInt(li -> li.getItemNum()).max().getAsInt();
                    for (int i = 0; i < nullItem.size(); i++) {
                        int maxItem=max+i+1;
                        nullItem.get(i).setItemNum(maxItem);
                        if( nullItem.get(i).getHandleStatus()==null){
                            nullItem.get(i).setHandleStatus(ConstantsEms.SAVA_STATUS);
                        }
                        nullItem.get(i).setOutsourcePurchasePriceSid(purOutsourcePurchasePrice.getOutsourcePurchasePriceSid());
                    }
                    itemMapper.inserts(nullItem);
                    addTodo(nullItem,purOutsourcePurchasePrice);
                    nullItem.forEach(li->{
                        if(ConstantsEms.IMPORT.equals(li.getImportHandle())){
                            //插入日志
                            MongodbUtil.insertUserLogItem(li.getOutsourcePurchasePriceSid(), BusinessType.IMPORT.getValue(),TITLE,li.getItemNum());
                        }else{
                            if(ConstantsEms.YES.equals(li.getSubmitHandle())){
                                //插入日志
                                MongodbUtil.insertUserLogItem(li.getOutsourcePurchasePriceSid(), BusinessType.INSERT.getValue(),TITLE,li.getItemNum());
                            }
                        }
                    });
                }
            }
        }
        List<PurOutsourcePurchasePriceItem> itemList = purOutsourcePurchasePrice.getItemList();
        if(CollectionUtil.isNotEmpty(itemList)){
            itemList.forEach(item->{
                //变更审批
                if (HandleStatus.CHANGEAPPROVAL.getCode().equals(item.getHandleStatus())){
                    Submit submit = new Submit();
                    submit.setStartUserId(ApiThreadLocalUtil.get().getUserid().toString());
                    submit.setFormType(FormType.JGCGJ_BG.getCode());
                    List<FormParameter> list = new ArrayList();
                    FormParameter formParameter = new FormParameter();
                    formParameter.setParentId(item.getOutsourcePurchasePriceSid().toString());
                    formParameter.setFormId(item.getOutsourcePurchasePriceItemSid().toString());
                    formParameter.setFormCode(purOutsourcePurchasePrice.getOutsourcePurchasePriceCode().toString());
                    list.add(formParameter);
                    submit.setFormParameters(list);
                    workflowService.change(submit);
                }
                //新增行 正常审批
                if(HandleStatus.SUBMIT.getCode().equals(item.getHandleStatus())){
                    Submit submit = new Submit();
                    submit.setStartUserId(ApiThreadLocalUtil.get().getUserid().toString());
                    submit.setFormType(FormType.OutsourcePurchasePrice.getCode());
                    List<FormParameter> list = new ArrayList();
                    FormParameter formParameter = new FormParameter();
                    formParameter.setParentId(item.getOutsourcePurchasePriceSid().toString());
                    formParameter.setFormId(item.getOutsourcePurchasePriceItemSid().toString());
                    formParameter.setFormCode(purOutsourcePurchasePrice.getOutsourcePurchasePriceCode().toString());
                    list.add(formParameter);
                    submit.setFormParameters(list);
                    workflowService.submitByItem(submit);
                }
            });
        }
        return row;
    }


    /**
     * 变更加工采购价主
     *
     * @param purOutsourcePurchasePrice 加工采购价主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePurOutsourcePurchasePrice(PurOutsourcePurchasePrice purOutsourcePurchasePrice) {
        judgeNull(purOutsourcePurchasePrice);
        int row = purOutsourcePurchasePriceMapper.updateAllById(purOutsourcePurchasePrice);
        if (row > 0) {
            List<PurOutsourcePurchasePriceAttachment> attachmentList = purOutsourcePurchasePrice.getAttachmentList();
            attachmentList.forEach(a -> {
                if (a.getUpdateDate() == null) {
                    a.setUpdateDate(new Date());
                }
                if (StrUtil.isEmpty(a.getCreatorAccount())) {
                    a.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            insertAttachment(purOutsourcePurchasePrice.getOutsourcePurchasePriceSid(), attachmentList);
            List<PurOutsourcePurchasePriceItem> itemList = purOutsourcePurchasePrice.getItemList();
            changePrice(itemList);
            itemList.forEach(a -> {
                //校验明细表时间是否有交集
                if (!validTime(itemList)) {
                    throw new BaseException("价格明细配置时间存在交集,请修改后再试");
                }
                //第二层校验
                judgeTime(purOutsourcePurchasePrice, a);
                if (a.getUpdateDate() == null) {
                    a.setUpdateDate(new Date());
                }
                if (StrUtil.isEmpty(a.getCreatorAccount())) {
                    a.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            insertItem(purOutsourcePurchasePrice.getOutsourcePurchasePriceSid(), itemList);
            //更新通知
            if (ConstantsEms.CHECK_STATUS.equals(purOutsourcePurchasePrice.getHandleStatus())) {
                SysBusinessBcst sysBusinessBcst = new SysBusinessBcst();
                sysBusinessBcst.setTitle("商品编码" + purOutsourcePurchasePrice.getMaterialCode() + "，加工项" + purOutsourcePurchasePrice.getProcessName() + ",加工采购价编号" + purOutsourcePurchasePrice.getOutsourcePurchasePriceCode() + "的信息发生变更，请知悉！")
                        .setDocumentSid(purOutsourcePurchasePrice.getOutsourcePurchasePriceSid())
                        .setDocumentCode(purOutsourcePurchasePrice.getOutsourcePurchasePriceCode().toString())
                        .setNoticeDate(new Date()).setUserId(ApiThreadLocalUtil.get().getUserid());
                sysBusinessBcstMapper.insert(sysBusinessBcst);
            }
            //插入日志
            MongodbUtil.insertUserLog(purOutsourcePurchasePrice.getOutsourcePurchasePriceSid(), BusinessType.CHANGE.getValue(), null, TITLE);
        }
        return row;
    }

    /**
     * 批量删除加工采购价主
     *
     * @param outsourcePurchasePriceSids 需要删除的加工采购价主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurOutsourcePurchasePriceByIds(List<Long> outsourcePurchasePriceSids) {
        int row = purOutsourcePurchasePriceMapper.deleteBatchIds(outsourcePurchasePriceSids);
        if (row > 0) {
            outsourcePurchasePriceSids.forEach(sid -> {
                deleteAttachment(sid);
                deleteItem(sid);
                PurOutsourcePurchasePrice purOutsourcePurchasePrice = new PurOutsourcePurchasePrice();
                purOutsourcePurchasePrice.setOutsourcePurchasePriceSid(sid);
                //校验是否存在待办
                checkTodoExist(purOutsourcePurchasePrice);
            });
        }
        return row;
    }

    /**
     * 批量删除加工采购价明细
     *
     * @param outsourcePurchasePriceSids 批量删除加工采购价明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteItems(List<Long> outsourcePurchasePriceSids) {
        List<PurOutsourcePurchasePriceItem> items = itemMapper.selectList(new QueryWrapper<PurOutsourcePurchasePriceItem>().lambda()
                .in(PurOutsourcePurchasePriceItem::getOutsourcePurchasePriceItemSid, outsourcePurchasePriceSids)
        );
        items.forEach(item->{
            MongodbUtil.insertUserLogItem(item.getOutsourcePurchasePriceSid(), BusinessType.DELETE.getValue(),TITLE,item.getItemNum());
        });
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentItemSid, outsourcePurchasePriceSids));
        List<Long> longs = items.stream().map(li -> li.getOutsourcePurchasePriceSid()).collect(Collectors.toList());
        int row = itemMapper.deleteBatchIds(outsourcePurchasePriceSids);
        longs.forEach(item -> {
            List<PurOutsourcePurchasePriceItem> itemList = itemMapper.selectList(new QueryWrapper<PurOutsourcePurchasePriceItem>().lambda()
                    .eq(PurOutsourcePurchasePriceItem::getOutsourcePurchasePriceSid, item)
            );
            //明细为空时，删除对应的主表
            if (CollectionUtil.isEmpty(itemList)) {
                purOutsourcePurchasePriceMapper.deleteById(item);
            }
        });
        return row;
    }

    //第二层校验（校验 同一个有效期内只存在 一笔按款或按色）
    public void judgeTime(PurOutsourcePurchasePrice purOutsourcePurchasePrice, PurOutsourcePurchasePriceItem purOutsourcePurchasePriceItem) {
        //获取 按款-按色
        List<PurOutsourcePurchasePriceItem> purchasePriceAll = getPurchasePriceAll(purOutsourcePurchasePrice);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(purchasePriceAll)) {
            List<PurOutsourcePurchasePriceItem> purPurchasePriceItems = new ArrayList<>();
            purPurchasePriceItems.addAll(purchasePriceAll);
            if(CollectionUtil.isNotEmpty(purPurchasePriceItems)&&purOutsourcePurchasePriceItem.getOutsourcePurchasePriceItemSid()!=null){
                purPurchasePriceItems= purPurchasePriceItems.stream().filter(li->!li.getOutsourcePurchasePriceItemSid().toString().equals(purOutsourcePurchasePriceItem.getOutsourcePurchasePriceItemSid().toString())).collect(Collectors.toList());
            }
            //校验有效期是否存在交集
            boolean judge = validTimeOther(purOutsourcePurchasePriceItem, purPurchasePriceItems);
            if (!judge) {
                throw new BaseException("物料编码" + purOutsourcePurchasePrice.getMaterialCode() + "，当前已生效加工采购价的有效期与此单的有效期区间存在交集，请检查！");
            }
        }
    }

    public Boolean validTimeOther(PurOutsourcePurchasePriceItem purPurchasePriceItem, List<PurOutsourcePurchasePriceItem> purPurchasePriceItems) {
        for (int i = 0; i < purPurchasePriceItems.size(); i++) {
            long start = purPurchasePriceItem.getStartDate().getTime();
            long end = purPurchasePriceItem.getEndDate().getTime();
            if (start >= purPurchasePriceItems.get(i).getStartDate().getTime() && start <= purPurchasePriceItems.get(i).getEndDate().getTime()) {
                return false;
            }
            if (end >= purPurchasePriceItems.get(i).getStartDate().getTime() && end <= purPurchasePriceItems.get(i).getEndDate().getTime()) {
                return false;
            }
            if (start < purPurchasePriceItems.get(i).getStartDate().getTime() && end > purPurchasePriceItems.get(i).getEndDate().getTime()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 启用/停用
     *
     * @param purOutsourcePurchasePrice
     * @return
     */
    @Override
    public int changeStatus(PurOutsourcePurchasePrice purOutsourcePurchasePrice) {
        int row = 0;
        Long[] sids = purOutsourcePurchasePrice.getOutsourcePurchasePriceSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                purOutsourcePurchasePrice.setOutsourcePurchasePriceSid(id);
                row = purOutsourcePurchasePriceMapper.updateById(purOutsourcePurchasePrice);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String businessType = purOutsourcePurchasePriceMapper.selectById(id).getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(id, businessType, msgList, TITLE, null);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param purOutsourcePurchasePrice
     * @return
     */
    @Override
    public int check(PurOutsourcePurchasePrice purOutsourcePurchasePrice) {
        int row = 0;
        Long[] sids = purOutsourcePurchasePrice.getOutsourcePurchasePriceSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                purOutsourcePurchasePrice.setOutsourcePurchasePriceSid(id);
                purOutsourcePurchasePrice.setHandleStatus(ConstantsEms.CHECK_STATUS);
                //校验是否存在待办
                checkTodoExist(purOutsourcePurchasePrice);
                PurOutsourcePurchasePrice purchasePrice = selectPurOutsourcePurchasePriceById(id);
                judgeNull(purchasePrice);
                List<PurOutsourcePurchasePriceItem> itemList = purchasePrice.getItemList();
                itemList.forEach(li -> {
                    //第二层校验
                    judgeTime(purchasePrice, li);
                });
                row = purOutsourcePurchasePriceMapper.updateById(purOutsourcePurchasePrice);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                if (purOutsourcePurchasePrice.getHandleStatus().equals(HandleStatus.CONFIRMED)) {
                    MongodbUtil.insertUserLog(purOutsourcePurchasePrice.getOutsourcePurchasePriceSid(), BusinessType.CHECK.getValue(), null, TITLE);
                }
            }
        }
        return row;
    }

    /**
     * 提交时校验
     */
    @Override
    public int processCheck(List<Long> ids) {
        ids.forEach(id->{
            PurOutsourcePurchasePrice purchasePrice = selectPurOutsourcePurchasePriceById(id);
            //校验是否已配置相同价格
            if (purchasePrice.getSku1Sid() != null) {
                List<PurOutsourcePurchasePrice> queryResultList = purOutsourcePurchasePriceMapper.selectList(new QueryWrapper<PurOutsourcePurchasePrice>().lambda()
                        .eq(PurOutsourcePurchasePrice::getVendorSid, purchasePrice.getVendorSid())
                        .eq(PurOutsourcePurchasePrice::getMaterialSid, purchasePrice.getMaterialSid())
                        .eq(PurOutsourcePurchasePrice::getProcessSid, purchasePrice.getProcessSid())
                        .eq(PurOutsourcePurchasePrice::getSku1Sid, purchasePrice.getSku1Sid()));
                queryResultList=queryResultList.stream().filter(li->!li.getOutsourcePurchasePriceSid().toString().equals(purchasePrice.getOutsourcePurchasePriceSid().toString())).collect(Collectors.toList());
                if (queryResultList.size() == 1) {
                    throw  new  CustomException("加工采购价"+purchasePrice.getOutsourcePurchasePriceCode()+"与单号"+queryResultList.get(0).getOutsourcePurchasePriceCode()+"维度相同，不允许提交");
                }
            }else {
                List<PurOutsourcePurchasePrice> queryResultList = purOutsourcePurchasePriceMapper.selectList(new QueryWrapper<PurOutsourcePurchasePrice>().lambda()
                        .eq(PurOutsourcePurchasePrice::getVendorSid, purchasePrice.getVendorSid())
                        .eq(PurOutsourcePurchasePrice::getMaterialSid, purchasePrice.getMaterialSid())
                        .eq(PurOutsourcePurchasePrice::getProcessSid, purchasePrice.getProcessSid()))
                        .stream().filter(o -> o.getSku1Sid() == null).collect(Collectors.toList());
                queryResultList=queryResultList.stream().filter(o->!o.getOutsourcePurchasePriceSid().toString().equals(purchasePrice.getOutsourcePurchasePriceSid().toString())).collect(Collectors.toList());
                if (queryResultList.size() == 1) {
                    throw  new  CustomException("加工采购价"+purchasePrice.getOutsourcePurchasePriceCode()+"与单号"+queryResultList.get(0).getOutsourcePurchasePriceCode()+"维度相同，不允许提交");
                }
            }
            List<PurOutsourcePurchasePriceItem> itemList = purchasePrice.getItemList();
            if (CollectionUtil.isNotEmpty(itemList)) {
                itemList.forEach(li -> {
                    if (li.getPurchasePriceTax() == null || purchasePrice.getProcessSid() == null) {
                        throw new CustomException("明细行存在加工采购价/加工项未维护，请填写后再点击提交");
                    }
                });
            } else {
                throw new CustomException("提交时，明细行不允许为空");
            }
            checkUnique(purchasePrice);
        });
        return 1;
    }

    public void checkUnique(PurOutsourcePurchasePrice purchasePrice){
        PurOutsourceQuoteBargainItem request = new PurOutsourceQuoteBargainItem();
        //供应商，甲供料方式，采购模式，有效期起/至，物料商品sid，价格维度，sku1sid
        request.setVendorSid(purchasePrice.getVendorSid()).setProcessSid(purchasePrice.getProcessSid())
                .setMaterialSid(purchasePrice.getMaterialSid());
        //查询出不是已确认的单据
        request.setHandleStatusList(new String[]{HandleStatus.SUBMIT.getCode(),HandleStatus.CHANGEAPPROVAL.getCode()});
        //报核议价单
        List<PurOutsourceQuoteBargainItem> bargainItemList = new ArrayList<>();
        //采购价单
        List<PurOutsourcePurchasePriceItem> priceItemList = new ArrayList<>();
        if (ConstantsPrice.PRICE_DIMENSION.equals(purchasePrice.getPriceDimension())){
            bargainItemList = itemBargainMapper.selectPurOutsourceRequestQuotationItemList(request);
            priceItemList = itemBargainMapper.selectPriceItemList(request);
        }
        else if (ConstantsPrice.PRICE_DIMENSION_SKU1.equals(purchasePrice.getPriceDimension())){
            //1、查按色 sku1Sid
            request.setSku1Sid(purchasePrice.getSku1Sid()).setPriceDimension(purchasePrice.getPriceDimension());
            bargainItemList = itemBargainMapper.selectPurOutsourceRequestQuotationItemList(request);
            priceItemList = itemBargainMapper.selectPriceItemList(request);
            //如果（1）没查到，则接着查：2、查价格维度：按款
            request.setSku1Sid(null).setPriceDimension(ConstantsPrice.PRICE_DIMENSION);
            if (CollectionUtil.isEmpty(bargainItemList)){
                bargainItemList = itemBargainMapper.selectPurOutsourceRequestQuotationItemList(request);
            }
            if (CollectionUtil.isEmpty(priceItemList)){
                priceItemList = itemBargainMapper.selectPriceItemList(request);
            }
        }else {}
        String materialName = purchasePrice.getMaterialName() == null ? "" : purchasePrice.getMaterialName();
        if (CollectionUtil.isNotEmpty(bargainItemList)){
            //如果是编辑 的，那要去掉跟本身的单据校验冲突
            if (CollectionUtil.isNotEmpty(bargainItemList)){
                String stage = bargainItemList.get(0).getCurrentStage();
                String stageName = setTitle(stage);
                throw new CustomException(purchasePrice.getOutsourcePurchasePriceCode()+materialName + "存在相应的审批中的" + stageName + bargainItemList.get(0).getOutsourceQuoteBargainCode() + "，请先处理此" + stageName);
            }

        }
        if (CollectionUtil.isNotEmpty(priceItemList)){
            if (purchasePrice.getOutsourcePurchasePriceSid() != null){
                priceItemList = priceItemList.stream().filter(o-> !o.getOutsourcePurchasePriceSid().toString()
                        .equals(purchasePrice.getOutsourcePurchasePriceSid().toString())).collect(Collectors.toList());
            }
            if(CollectionUtil.isNotEmpty(priceItemList)){
                throw new CustomException(purchasePrice.getOutsourcePurchasePriceCode()+materialName + "存在相应的审批中的加工采购价信息" + priceItemList.get(0).getOutsourcePurchasePriceCode() + ",请先处理此加工采购价信息");
            }
        }
    }

    private void checkhandleStatus(PurOutsourcePurchasePrice purOutsourcePurchasePrice) {
        if (purOutsourcePurchasePrice.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode())) {
            purOutsourcePurchasePrice.setConfirmDate(new Date());
            purOutsourcePurchasePrice.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
    }

    private void insertAttachment(Long outsourcePurchasePriceSid, List<PurOutsourcePurchasePriceAttachment> attachmentList) {
        deleteAttachment(outsourcePurchasePriceSid);
        if (CollectionUtils.isNotEmpty(attachmentList)) {
            attachmentList.forEach(o -> {
                if (o.getOutsourcePurchasePriceAttachmentSid() == null) {
                    o.setOutsourcePurchasePriceAttachmentSid(IdWorker.getId());
                }
                if (StrUtil.isEmpty(o.getCreatorAccount())) {
                    o.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                }
                if (o.getClientId() == null) {
                    o.setClientId(ApiThreadLocalUtil.get().getClientId());
                }
                if (o.getCreateDate() == null) {
                    o.setCreateDate(new Date());
                }
                o.setOutsourcePurchasePriceSid(outsourcePurchasePriceSid);
            });
            attachmentMapper.inserts(attachmentList);
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
            title = "加工报价单";
        }else if (ConstantsPrice.BAOHEYI_STAGE_HJ.equals(stage)){
            title = "加工核价单";
        } else if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(stage)){
            title = "加工议价单";
        } else {
            title  = "";
        }
        return title;
    }
    private void deleteAttachment(Long outsourcePurchasePriceSid) {
        attachmentMapper.delete(new QueryWrapper<PurOutsourcePurchasePriceAttachment>().lambda().eq(PurOutsourcePurchasePriceAttachment::getOutsourcePurchasePriceSid, outsourcePurchasePriceSid));
    }

    private void insertItem(Long outsourcePurchasePriceSid, List<PurOutsourcePurchasePriceItem> list) {
        deleteItem(outsourcePurchasePriceSid);
        list.forEach(o -> {
            if (o.getOutsourcePurchasePriceItemSid() == null) {
                o.setOutsourcePurchasePriceItemSid(IdWorker.getId());
            }
            if (StrUtil.isEmpty(o.getCreatorAccount())) {
                o.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
            }
            if (o.getClientId() == null) {
                o.setClientId(ApiThreadLocalUtil.get().getClientId());
            }
            if (o.getCreateDate() == null) {
                o.setCreateDate(new Date());
            }
            if(o.getHandleStatus()==null){
                o.setHandleStatus(ConstantsEms.SAVA_STATUS);
            }
            o.setOutsourcePurchasePriceSid(outsourcePurchasePriceSid);
            if (o.getInnerCheckPriceTax() != null) {
                if (o.getTaxRate() != null) {
                    o.setInnerCheckPrice(o.getInnerCheckPriceTax().divide(o.getTaxRate().add(BigDecimal.ONE), 6, BigDecimal.ROUND_HALF_UP));
                }
                else {
                    o.setInnerCheckPrice(o.getInnerCheckPriceTax());
                }
            }
        });
        itemMapper.inserts(list);
    }
    private void deleteItem(Long outsourcePurchasePriceSid) {
        itemMapper.delete(new QueryWrapper<PurOutsourcePurchasePriceItem>().lambda().eq(PurOutsourcePurchasePriceItem::getOutsourcePurchasePriceSid, outsourcePurchasePriceSid));
    }
    //基本计量单位和采购价格单位
    public void setUnit(List<PurOutsourcePurchasePriceItem> listPurPurchasePriceItem){
        listPurPurchasePriceItem.forEach(li->{
            li.setUnitPrice(li.getUnitBase());
            if(li.getUnitBase().equals(li.getUnitPrice())){
                li.setUnitConversionRate(BigDecimal.ONE);
            }else{
                if(li.getUnitConversionRate()==null){
                    throw new  CustomException("采购价单位“与”基本计量单位“不一致，单位换算比例不允许为空");
                }
            }
        });
    }
    /**
     * 获取加工采购价
     */
    @Override
    public PurOutsourcePurchasePriceItem getPurchasePrice(PurOutsourcePurchasePrice purOutsourcePurchasePrice) {
        PurOutsourcePurchasePrice result = null;
        //按：供应商、公司、商品SID、SKU1 SID、工序查
        result = purOutsourcePurchasePriceMapper.selectOne(new QueryWrapper<PurOutsourcePurchasePrice>().lambda()
                .eq(PurOutsourcePurchasePrice::getVendorSid, purOutsourcePurchasePrice.getVendorSid())
                .eq(PurOutsourcePurchasePrice::getMaterialSid, purOutsourcePurchasePrice.getMaterialSid())
                .eq(PurOutsourcePurchasePrice::getSku1Sid, purOutsourcePurchasePrice.getSku1Sid())
                .eq(PurOutsourcePurchasePrice::getProcessSid, purOutsourcePurchasePrice.getPlantSid()));
        if (result == null) {
            //按：供应商、公司、商品SID、工序查
            result = purOutsourcePurchasePriceMapper.selectOne(new QueryWrapper<PurOutsourcePurchasePrice>().lambda()
                    .eq(PurOutsourcePurchasePrice::getVendorSid, purOutsourcePurchasePrice.getVendorSid())
                    .eq(PurOutsourcePurchasePrice::getMaterialSid, purOutsourcePurchasePrice.getMaterialSid())
                    .eq(PurOutsourcePurchasePrice::getProcessSid, purOutsourcePurchasePrice.getPlantSid()));
        }

        if (result != null) {
            PurOutsourcePurchasePriceItem purchasePriceItem = itemMapper.selectOne(new QueryWrapper<PurOutsourcePurchasePriceItem>().lambda()
                    .le(PurOutsourcePurchasePriceItem::getStartDate, new Date())
                    .ge(PurOutsourcePurchasePriceItem::getEndDate, new Date())
                    .eq(PurOutsourcePurchasePriceItem::getOutsourcePurchasePriceSid, result.getOutsourcePurchasePriceSid()));
            return purchasePriceItem;
        }
        return null;
    }

    /**
     * 获取加工采购价明细 （有效期验证）
     */
    public List<PurOutsourcePurchasePriceItem> getPurchasePriceAll(PurOutsourcePurchasePrice purOutsourcePurchasePrice) {
        List<PurOutsourcePurchasePrice> result = null;
        String priceDimension = purOutsourcePurchasePrice.getPriceDimension();
        //按款
        if (ConstantsEms.PRICE_K.equals(priceDimension)) {
            result = purOutsourcePurchasePriceMapper.selectList(new QueryWrapper<PurOutsourcePurchasePrice>()
                    .lambda()
                    .eq(PurOutsourcePurchasePrice::getVendorSid, purOutsourcePurchasePrice.getVendorSid())
                    .eq(PurOutsourcePurchasePrice::getMaterialSid, purOutsourcePurchasePrice.getMaterialSid())
                    .eq(PurOutsourcePurchasePrice::getProcessSid, purOutsourcePurchasePrice.getProcessSid()));
        } else {
            //按色    -只获取价格维度为按款
            result = purOutsourcePurchasePriceMapper.selectList(new QueryWrapper<PurOutsourcePurchasePrice>()
                    .lambda()
                    .eq(PurOutsourcePurchasePrice::getPriceDimension, ConstantsEms.PRICE_K)
                    .eq(PurOutsourcePurchasePrice::getVendorSid, purOutsourcePurchasePrice.getVendorSid())
                    .eq(PurOutsourcePurchasePrice::getMaterialSid, purOutsourcePurchasePrice.getMaterialSid())
                    .eq(PurOutsourcePurchasePrice::getProcessSid, purOutsourcePurchasePrice.getProcessSid()));
        }
        if (CollectionUtil.isNotEmpty(result)) {
            List<Long> sids = result.stream().map(item -> item.getOutsourcePurchasePriceSid()).collect(Collectors.toList());
            QueryWrapper<PurOutsourcePurchasePriceItem> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(PurOutsourcePurchasePriceItem::getOutsourcePurchasePriceSid, sids);
            if (purOutsourcePurchasePrice.getOutsourcePurchasePriceSid() != null) {
                queryWrapper.lambda().ne(PurOutsourcePurchasePriceItem::getOutsourcePurchasePriceSid,
                        purOutsourcePurchasePrice.getOutsourcePurchasePriceSid());
            }
            List<PurOutsourcePurchasePriceItem> priceItem = itemMapper.selectList(queryWrapper);
            return priceItem;
        }
        return null;
    }
    //新增明细时，新增代办
    public void addTodo(List<PurOutsourcePurchasePriceItem> list,PurOutsourcePurchasePrice purOutsourcePurchasePrice){
        list.forEach(li->{
            SysTodoTask sysTodoTask = new SysTodoTask();
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(ConstantsEms.TABLE_MANUFACTURE_ORDER)
                    .setDocumentItemSid(li.getOutsourcePurchasePriceItemSid())
                    .setDocumentSid(li.getOutsourcePurchasePriceSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                sysTodoTask.setTitle("加工采购价" + purOutsourcePurchasePrice.getOutsourcePurchasePriceCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(purOutsourcePurchasePrice.getOutsourcePurchasePriceCode().toString())
                        .setNoticeDate(new Date())
                        .setMenuId(ConstantsWorkbench.pur_outsource_purchase_price)
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        });
    }
    /**
     * 审批流修改状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int flowHandle(PurOutsourcePurchasePriceItem item,String comment){
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentItemSid, item.getOutsourcePurchasePriceItemSid()));
        PurOutsourcePurchasePriceItem purOutsourcePurchasePriceItem = itemMapper.selectById(item.getOutsourcePurchasePriceItemSid());
        if(ConstantsEms.CHECK_STATUS.equals(item.getHandleStatus())){
            //插入日志
//            MongodbUtil.insertUserLogItem(purOutsourcePurchasePriceItem.getOutsourcePurchasePriceSid(), BusinessType.APPROVED.getValue(),TITLE,purOutsourcePurchasePriceItem.getItemNum());
        }else if(HandleStatus.SUBMIT.getCode().equals(item.getHandleStatus())){
            //purOutsourcePurchasePriceItem
            MongodbUtil.insertUserLogItem(purOutsourcePurchasePriceItem.getOutsourcePurchasePriceSid(), BusinessType.SUBMIT.getValue(),TITLE,purOutsourcePurchasePriceItem.getItemNum());
        }else if(HandleStatus.CHANGEAPPROVAL.getCode().equals(item.getHandleStatus())){
            //插入日志
            MongodbUtil.insertUserLogItem(purOutsourcePurchasePriceItem.getOutsourcePurchasePriceSid(), BusinessType.CHANGE.getValue(),TITLE,purOutsourcePurchasePriceItem.getItemNum());
            //插入日志
            MongodbUtil.insertUserLogItem(purOutsourcePurchasePriceItem.getOutsourcePurchasePriceSid(), BusinessType.SUBMIT.getValue(),TITLE,purOutsourcePurchasePriceItem.getItemNum());
        }else{
            //插入日志
            MongodbUtil.insertApprovalLogAddNum(purOutsourcePurchasePriceItem.getOutsourcePurchasePriceSid(), BusinessType.APPROVAL.getValue(),comment,purOutsourcePurchasePriceItem.getItemNum());
        }
        if (HandleStatus.BG_RETURN.getCode().equals(item.getHandleStatus())) {//单笔变更驳回
            item.setHandleStatus(HandleStatus.CONFIRMED.getCode());
        }
        if(HandleStatus.RETURNED.getCode().equals(item.getHandleStatus())){
            if(HandleStatus.CHANGEAPPROVAL.getCode().equals(purOutsourcePurchasePriceItem.getHandleStatus())){//多笔变更驳回
                item.setHandleStatus(HandleStatus.CONFIRMED.getCode());
            }
        }
        int row= itemMapper.update(new PurOutsourcePurchasePriceItem(), new UpdateWrapper<PurOutsourcePurchasePriceItem>().lambda()
                .set(PurOutsourcePurchasePriceItem::getHandleStatus,item.getHandleStatus())
                .set(PurOutsourcePurchasePriceItem::getItemNum,purOutsourcePurchasePriceItem.getItemNum())
                .eq(PurOutsourcePurchasePriceItem::getOutsourcePurchasePriceItemSid,item.getOutsourcePurchasePriceItemSid())
        );
        List<PurOutsourcePurchasePriceItem> purOutsourcePurchaseItem = itemMapper.selectList(new QueryWrapper<PurOutsourcePurchasePriceItem>().lambda()
                .eq(PurOutsourcePurchasePriceItem::getOutsourcePurchasePriceSid, purOutsourcePurchasePriceItem.getOutsourcePurchasePriceSid())
        );
        PurOutsourcePurchasePrice purOutsourcePurchasePrice = purOutsourcePurchasePriceMapper.selectById(purOutsourcePurchasePriceItem.getOutsourcePurchasePriceSid());
        List<PurOutsourcePurchasePriceItem> checkList = purOutsourcePurchaseItem.stream().filter(li -> ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(checkList)){
            if(!ConstantsEms.CHECK_STATUS.equals(purOutsourcePurchasePrice.getHandleStatus())){
                purOutsourcePurchasePriceMapper.update(new PurOutsourcePurchasePrice(),new UpdateWrapper<PurOutsourcePurchasePrice>().lambda()
                        .eq(PurOutsourcePurchasePrice::getOutsourcePurchasePriceSid,purOutsourcePurchasePrice.getOutsourcePurchasePriceSid())
                        .set(PurOutsourcePurchasePrice::getHandleStatus,ConstantsEms.CHECK_STATUS)
                );
            }
        }else{
            if(!ConstantsEms.CHECK_STATUS.equals(purOutsourcePurchasePrice.getHandleStatus())){
                PurOutsourcePurchasePriceItem purOutsourcePurchase = purOutsourcePurchaseItem.get(0);
                purOutsourcePurchasePriceMapper.update(new PurOutsourcePurchasePrice(),new UpdateWrapper<PurOutsourcePurchasePrice>().lambda()
                        .eq(PurOutsourcePurchasePrice::getOutsourcePurchasePriceSid,purOutsourcePurchasePrice.getOutsourcePurchasePriceSid())
                        .set(PurOutsourcePurchasePrice::getHandleStatus,purOutsourcePurchase.getHandleStatus())
                );
            }
        }
        return 1;
    }

    @Override
    public void setApprovalLog(PurOutsourcePurchasePriceItem item, String comment){
        PurOutsourcePurchasePriceItem purOutsourcePurchasePriceItem = itemMapper.selectById(item.getOutsourcePurchasePriceItemSid());
        MongodbUtil.insertApprovalLogAddNum(purOutsourcePurchasePriceItem.getOutsourcePurchasePriceSid(), BusinessType.APPROVAL.getValue(),comment,purOutsourcePurchasePriceItem.getItemNum());
    }

    /**
     * 加工采购价 导入
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult importDataOutPur(MultipartFile file) {
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
            //价格维度
            List<DictData> priceDimension = sysDictDataService.selectDictData("s_price_dimension");
            Map<String, String> priceDimensionMaps = priceDimension.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //递增减sku类型
            List<DictData> skuType = sysDictDataService.selectDictData("s_sku_type_recursion");
            Map<String, String> skuTypeMaps = skuType.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //税率
            Map<String, BigDecimal> taxRateMaps = conTaxRateMapper.getConTaxRateList().stream().collect(Collectors.toMap(ConTaxRate::getTaxRateName, ConTaxRate::getTaxRateValue, (key1, key2) -> key2));
            //基本计量单位
            List<ConMeasureUnit> conMeasureUnitList = conMeasureUnitMapper.getConMeasureUnitList();
            Map<String, String> measureUnitMaps = conMeasureUnitList.stream().collect(Collectors.toMap(ConMeasureUnit::getName, ConMeasureUnit::getCode, (key1, key2) -> key2));
            //取整方式
            List<DictData> roundingType = sysDictDataService.selectDictData("s_rounding_type");
            Map<String, String> roundingTypeMaps = roundingType.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<PurOutsourcePurchasePrice> purOutsourcePurchasePriceList = new ArrayList<>();
            String errMsg="";
            List<CommonErrMsgResponse> msgList=new ArrayList<>();
            for (int i = 0; i < readAll.size(); i++) {
                Long companySid = null;
                String vendorSid = null;
                Long materialSid = null;
                Long sku1Sid = null;
                Long prossSid = null;
                String materialCode = null;
                String priceDimensionValue=null;
                BasMaterial basMaterial=null;
                BigDecimal price=BigDecimal.ZERO;
                BigDecimal priceTax=BigDecimal.ZERO;
                boolean start=true;
                boolean end=true;
                BigDecimal taxRateValue=BigDecimal.ZERO;
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                int num = i + 1;
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                if (objects.get(0) == null || objects.get(0) == "") {
                   // throw new BaseException("第" + num + "行,供应商简称，不能为空，导入失败");
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
                       // throw new BaseException("第" + num + "行,供应商简称为" + bendorCode + "，没有对应的供应商，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("供应商简称为" + bendorCode + "，没有对应的供应商，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        if (!basVendor.getHandleStatus().equals(ConstantsEms.CHECK_STATUS) || !basVendor.getStatus().equals(ConstantsEms.ENABLE_STATUS)) {
                            //throw new BaseException("第" + num + "行,对应的供应商必须是确认且已启用的状态，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("对应的供应商必须是确认且已启用的状态，导入失败");
                            msgList.add(errMsgResponse);
                        }
                        vendorSid = basVendor.getVendorSid().toString();
                    }
                }
                if (objects.get(2) == null || objects.get(2) == "") {
                   // throw new BaseException("第" + num + "行,价格维度，不能为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("价格维度，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }
                if(objects.get(2) != null && objects.get(2) != ""){
                    priceDimensionValue = priceDimensionMaps.get(objects.get(2).toString());
                    if (StrUtil.isEmpty(priceDimensionValue)) {
                       // throw new BaseException("第" + num + "行,价格维度配置错误,请联系管理员，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("价格维度配置错误,请联系管理员，导入失败");
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
                if (objects.get(3) == null || objects.get(3) == "") {
                    //throw new BaseException("第" + num + "行,商品/物料编码，不能为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("商品/物料编码，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }
                if(objects.get(3) != null && objects.get(3)!= ""){
                    basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                            .eq(BasMaterial::getMaterialCode, objects.get(3).toString())
                    );
                    if (basMaterial == null) {
                        //throw new BaseException("第" + num + "行,商品/物料编码为" + objects.get(3).toString() + "，没有对应的商品/物料，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("商品/物料编码为" + objects.get(3).toString() + "，没有对应的商品/物料，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        if (!basMaterial.getHandleStatus().equals(ConstantsEms.CHECK_STATUS) || !basMaterial.getStatus().equals(ConstantsEms.ENABLE_STATUS)) {
                          //  throw new BaseException("第" + num + "行,对应的商品/物料必须是确认且已启用的状态，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("对应的商品/物料必须是确认且已启用的状态，导入失败");
                            msgList.add(errMsgResponse);
                        }
                        materialSid = basMaterial.getMaterialSid();
                        materialCode = basMaterial.getMaterialCode();
                    }
                }
                if (objects.get(4) != null && objects.get(4) != "") {
                    if(ConstantsEms.PRICE_K.equals(priceDimensionValue)){
                       // throw new BaseException("第"+num+"行,价格维度按款时，不允许填写颜色名称，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("价格维度按款时，不允许填写颜色名称，导入失败");
                        msgList.add(errMsgResponse);
                    }
                    BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                            .eq(BasSku::getSkuName, objects.get(4).toString()));
                    if(basSku==null){
                       // throw new BaseException("第"+num+"行,颜色名称为"+objects.get(4).toString()+"，没有对应的颜色，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("颜色名称为"+objects.get(4).toString()+"，没有对应的颜色，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        sku1Sid=basSku.getSkuSid();
                        List<BasMaterialSku> basMaterialSkus = basMaterialSkuMapper.selectList(new QueryWrapper<BasMaterialSku>().lambda()
                                .eq(BasMaterialSku::getMaterialSid, materialSid)
                                .eq(BasMaterialSku::getSkuSid, sku1Sid)
                        );
                        if(org.apache.commons.collections4.CollectionUtils.isEmpty(basMaterialSkus)){
                           // throw new BaseException("第"+num+"行,该物料没有对应的颜色，导入失败");
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
                if(ConstantsEms.PRICE_K1.equals(priceDimensionValue)){
                    if(sku1Sid==null){
                       // throw new BaseException("第"+num+"行,价格维度按色时,颜色为必填，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("价格维度按色时,颜色为必填，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
                if (objects.get(5) == null || objects.get(5) == "") {
                   // throw new BaseException("第" + num + "行,加工项名称，不能为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("加工项名称，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }
                if(objects.get(5) != null && objects.get(5) != ""){
                    ManProcess manProcess = manProcessMapper.selectOne(new QueryWrapper<ManProcess>().lambda()
                            .eq(ManProcess::getProcessName, objects.get(5).toString()));
                    if (manProcess == null) {
                       // throw new BaseException("第" + num + "行,没有对应名称为" + objects.get(5).toString() + "，的加工项，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("没有对应名称为" + objects.get(5).toString() + "，的加工项，导入失败");
                        msgList.add(errMsgResponse);
                    } else {
                        if (!manProcess.getHandleStatus().equals(ConstantsEms.CHECK_STATUS) || !manProcess.getStatus().equals(ConstantsEms.ENABLE_STATUS)) {
                           // throw new BaseException("第" + num + "行,对应的加工项必须是确认且已启用的状态，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("对应的加工项必须是确认且已启用的状态，导入失败");
                            msgList.add(errMsgResponse);
                        }
                        prossSid = manProcess.getProcessSid();
                    }
                }
                if (objects.get(6) == null || objects.get(6) == "") {
                   // throw new BaseException("第" + num + "行,有效期起，不能为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("有效期起，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }
                if (objects.get(7) == null || objects.get(7) == "") {
                   // throw new BaseException("第" + num + "行,有效期至，不能为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("有效期至，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }
                if(objects.get(6) != null && objects.get(6) != ""){
                     start = JudgeFormat.isValidDate(objects.get(6).toString());
                    if (!start) {
                        //throw new BaseException("第" + num + "行,有效期起，日期格式错误");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("有效期起，日期格式错误");
                        msgList.add(errMsgResponse);
                    }
                }
                if(objects.get(7) != null && objects.get(7) != ""){
                     end = JudgeFormat.isValidDate(objects.get(7).toString());
                    if (!end) {
                       // throw new BaseException("第" + num + "行,有效期至，日期格式错误");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("有效期至，日期格式错误");
                        msgList.add(errMsgResponse);
                    }
                }
                if(objects.get(6) != null && objects.get(6) != ""&&objects.get(7) != null && objects.get(7) != ""){
                    if(start&&end){
                        if (DateUtils.parseDate(objects.get(6)).getTime() > DateUtils.parseDate(objects.get(7)).getTime()) {
                            // throw new BaseException("第" + num + "行,有效期起，不能大于有效期至，导入失败");
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("有效期起，不能大于有效期至，导入失败");
                            msgList.add(errMsgResponse);
                        }
                    }
                }
                if (objects.get(8) == null || objects.get(8) == "") {
                    //throw new BaseException("第" + num + "行,采购价（含税），不能为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("采购价（含税），不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }
                if(objects.get(8) != null && objects.get(8) != ""){
                    boolean isVal = isValidDouble((objects.get(8).toString()));
                    if (!isVal) {
                       // throw new BaseException("第" + num + "行,采购价（含税），数据格式错误，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("采购价（含税），数据格式错误，导入失败");
                        msgList.add(errMsgResponse);
                    }
                }
                if (objects.get(9) == null || objects.get(9) == "") {
                   // throw new BaseException("第" + num + "行,税率，不能为空，导入失败");
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(num);
                    errMsgResponse.setMsg("税率，不能为空，导入失败");
                    msgList.add(errMsgResponse);
                }
                if(objects.get(9) != null && objects.get(9) != ""){
                    ConTaxRate conTaxRate = conTaxRateMapper.selectOne(new QueryWrapper<ConTaxRate>().lambda()
                            .like(ConTaxRate::getTaxRateName, objects.get(9).toString())
                    );
                    if (conTaxRate == null) {
                       // throw new BaseException("第" + num + "行,税率配置错误,请联系管理员，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("税率配置错误,请联系管理员，导入失败");
                        msgList.add(errMsgResponse);
                    }else{
                        if(!ConstantsEms.CHECK_STATUS.equals(conTaxRate.getHandleStatus())||!ConstantsEms.ENABLE_STATUS.equals(conTaxRate.getStatus())){
                            errMsg=errMsg+"第"+num+"行,对应的税率必须是确认且已启用的状态"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("对应的税率必须是确认且已启用的状态");
                            msgList.add(errMsgResponse);
                        }
                        taxRateValue=conTaxRate.getTaxRateValue();
                    }
                }

                /*
                 * 内部核算价(含税) 选填
                 */
                String innerCheckPriceTaxS = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString().trim();
                BigDecimal innerCheckPriceTax = null;
                if (StrUtil.isNotBlank(innerCheckPriceTaxS)) {
                    if (!JudgeFormat.isValidDouble(innerCheckPriceTaxS,10,5)){
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(num);
                        errMsgResponse.setMsg("内部核算价(含税)格式错误，导入失败！");
                        msgList.add(errMsgResponse);
                    } else {
                        innerCheckPriceTax = new BigDecimal(innerCheckPriceTaxS);
                        if (innerCheckPriceTax != null && BigDecimal.ZERO.compareTo(innerCheckPriceTax) > 0) {
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(num);
                            errMsgResponse.setMsg("内部核算价(含税)格式错误，导入失败！");
                            msgList.add(errMsgResponse);
                        }
                        innerCheckPriceTax = innerCheckPriceTax.divide(BigDecimal.ONE,5,BigDecimal.ROUND_HALF_UP);
                    }
                }

                /*
                 * 报价(含税) 选填
                 */
                String quotePriceTaxS = objects.get(11) == null || objects.get(11) == "" ? null : objects.get(11).toString().trim();
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
                 * 价格说明 选填
                 */
                String priceRemark = objects.get(12) == null || objects.get(12) == "" ? null : objects.get(12).toString().trim();


                PurOutsourcePurchasePrice purOutsourcePurchasePrice = new PurOutsourcePurchasePrice();
                purOutsourcePurchasePrice.setVendorSid(vendorSid==null?null:Long.valueOf(vendorSid))
                        .setCompanySid(null)
                        .setSku1Sid(sku1Sid)
                        .setProcessSid(prossSid)
                        .setStatus(ConstantsEms.SAVA_STATUS)
                        .setImportHandle(ConstantsEms.SAVA_STATUS)
                        .setMaterialCode(materialCode)
                        .setMaterialSid(materialSid)
                        .setPriceDimension(priceDimensionValue)
                        .setHandleStatus(ConstantsEms.SAVA_STATUS)
                        .setRemark((objects.get(13) == "" || objects.get(13) == null) ? null : objects.get(13).toString());
                List<PurOutsourcePurchasePriceItem> purOutsourcePurchasePriceItems = new ArrayList<>();
                if((objects.get(8)!=""&&objects.get(8)!=null)){
                    boolean validDouble = isValidDouble(objects.get(8).toString());
                    if(validDouble){
                         price = BigDecimal.valueOf(Double.valueOf(objects.get(8).toString()));
                         priceTax = price.divide(BigDecimal.ONE, 3, BigDecimal.ROUND_HALF_UP);
                    }
                }
                PurOutsourcePurchasePriceItem purOutsourcePurchasePriceItem = new PurOutsourcePurchasePriceItem();
                purOutsourcePurchasePriceItem
                        .setUnitBase(basMaterial==null?null:basMaterial.getUnitBase())
                        .setCurrency("CNY")
                        .setHandleStatus(ConstantsEms.SAVA_STATUS)
                        .setCurrencyUnit("YUAN")
                        .setUnitConversionRate(BigDecimal.ONE)
                        .setPriceEnterMode("HS")
                        .setUnitPrice(basMaterial==null?null:basMaterial.getUnitBase())
                        .setStartDate((objects.get(6)==""||objects.get(6)==null)?null:DateUtils.parseDate(objects.get(6).toString()))
                        .setEndDate((objects.get(7)==""||objects.get(7)==null)?null:DateUtils.parseDate(objects.get(7).toString()))
                        .setPurchasePriceTax(priceTax)
                        .setInnerCheckPriceTax(innerCheckPriceTax).setQuotePriceTax(quotePriceTax)
                        .setPriceRemark(priceRemark)
                        .setTaxRate(taxRateValue);
                purOutsourcePurchasePriceItems.add(purOutsourcePurchasePriceItem);
                purOutsourcePurchasePrice.setItemList(purOutsourcePurchasePriceItems);
                purOutsourcePurchasePriceList.add(purOutsourcePurchasePrice);
            }
            if(CollectionUtil.isNotEmpty(msgList)){
                return AjaxResult.error("报错信息",msgList);
            }
            List<String> hashCode=new ArrayList<>();
            purOutsourcePurchasePriceList.forEach(item->{
                Long vendorSid=0L;
                Long skuSid=0L;
                if(item.getVendorSid()!=null){
                    vendorSid=item.getVendorSid();
                }
                if(item.getSku1Sid()!=null){
                    skuSid=item.getSku1Sid();
                }
                String code=skuSid+""+vendorSid+""+item.getMaterialSid()+""+item.getProcessSid();
                hashCode.add(code);
            });
            for (int i=0;i<hashCode.size();i++) {
                int  m=i;
                int sort=i+3;
                List<String> common = hashCode.stream().filter(li -> li.equals(hashCode.get(m))).collect(Collectors.toList());
                if(common.size()>1){
                    //throw new BaseException("第"+sort+"行,当前存在进行中的明细数据，导入失败");
                  // errMsg=errMsg+"第"+sort+"行,表格内存在多笔有效期不同的数据，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                    errMsgResponse.setItemNum(sort);
                    errMsgResponse.setMsg("表格内存在多笔有效期不同的数据，导入失败");
                    msgList.add(errMsgResponse);
                }
            }
            for (int i=0;i<purOutsourcePurchasePriceList.size();i++){
                int sort=i+3;
                List<PurOutsourcePurchasePriceItem> priceList = new ArrayList<>();
                Long vendorSid=0L;
                if(purOutsourcePurchasePriceList.get(i).getVendorSid()!=null){
                    vendorSid=purOutsourcePurchasePriceList.get(i).getVendorSid();
                    String code=vendorSid+""+purOutsourcePurchasePriceList.get(i).getMaterialSid()+""+purOutsourcePurchasePriceList.get(i).getProcessSid();
                    for (int j=0;j<purOutsourcePurchasePriceList.size();j++){
                        if(ConstantsEms.PRICE_K.equals(purOutsourcePurchasePriceList.get(j).getPriceDimension())&&i!=j){
                            String codeMon=purOutsourcePurchasePriceList.get(j).getVendorSid()+""+purOutsourcePurchasePriceList.get(j).getMaterialSid()+""+purOutsourcePurchasePriceList.get(j).getProcessSid();
                            if(code.equals(codeMon)){
                                List<PurOutsourcePurchasePriceItem> itemList = purOutsourcePurchasePriceList.get(j).getItemList();
                                priceList.add(itemList.get(0));
                            }
                        }
                    }

                }
                List<PurOutsourcePurchasePriceItem> itemList = purOutsourcePurchasePriceList.get(i).getItemList();
                if(CollectionUtil.isNotEmpty(priceList)){
                    boolean judge = validTimeOther(itemList.get(0),priceList);
                    if(!judge){
                        // throw new BaseException("第"+sort+"行,有效期存在交集，导入失败");
                       // errMsg=errMsg+"第"+sort+"行,与表格内数据，有效期存在交集，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
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
            for (int i = 0; i < purOutsourcePurchasePriceList.size(); i++) {
                String sid = judgeAdd(purOutsourcePurchasePriceList.get(i));
                int sort = i + 3;
                //存在相同配置则修改
                if (!sid.equals("500")) {
                    PurOutsourcePurchasePrice purOutsourcePurchasePrice = selectPurOutsourcePurchasePriceById(Long.valueOf(sid));
                    List<PurOutsourcePurchasePriceItem> priceItems = purOutsourcePurchasePriceList.get(i).getItemList();
                    List<PurOutsourcePurchasePriceItem> itemList = purOutsourcePurchasePrice.getItemList();
                    itemList.add(priceItems.get(0));
                    purOutsourcePurchasePrice.setItemList(itemList);
                    try {
                        List<PurOutsourcePurchasePriceItem> priceItemList = itemList.stream().filter(li -> ConstantsEms.SAVA_STATUS.equals(li.getHandleStatus())).collect(Collectors.toList());
                        if(priceItemList.size()>1){
                            //throw new BaseException("第"+sort+"行,当前存在进行中的明细数据，导入失败");
                            //errMsg=errMsg+"第"+sort+"行,系统内存在进行中的价格数据，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                            CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                            errMsgResponse.setItemNum(sort);
                            errMsgResponse.setMsg("系统内存在进行中的价格数据，导入失败");
                            msgList.add(errMsgResponse);
                        }
                        if(priceItemList.size()==1){
                            List<PurOutsourcePurchasePriceItem> items = itemList.stream().filter(li -> ConstantsEms.CHECK_STATUS.equals(li.getHandleStatus())).collect(Collectors.toList());
                            if(CollectionUtil.isNotEmpty(items)){
                                if(items.size()!=itemList.size()-1){
                                    //  throw new BaseException("第"+sort+"行,当前存在进行中的明细数，导入失败");
                                    errMsg=errMsg+"第"+sort+"行,系统内存在进行中的价格数据，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(sort);
                                    errMsgResponse.setMsg("系统内存在进行中的价格数据，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                            }else{
                                if(itemList.size()!=1){
                                    // throw new BaseException("第"+sort+"行,当前存在进行中的明细数，导入失败");
                                    errMsg=errMsg+"第"+sort+"行,系统内存在进行中的价格数据，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                                    CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                                    errMsgResponse.setItemNum(sort);
                                    errMsgResponse.setMsg("系统内存在进行中的价格数据，导入失败");
                                    msgList.add(errMsgResponse);
                                }
                            }
                        }
                        judgeImport(purOutsourcePurchasePrice);
                    }catch (CustomException e){
                        errMsg=errMsg+"第"+sort+"行,与系统内数据，有效期存在交集，导入失败"+"<br>&nbsp;&nbsp;&nbsp;&nbsp;";
                        //throw new BaseException("第"+sort+"行,有效期存在交集，导入失败");
                        CommonErrMsgResponse errMsgResponse = new CommonErrMsgResponse();
                        errMsgResponse.setItemNum(sort);
                        errMsgResponse.setMsg("与系统内数据，有效期存在交集，导入失败");
                        msgList.add(errMsgResponse);
                    }
                } else {
                    try {
                        judgeImport(purOutsourcePurchasePriceList.get(i));
                    } catch (BaseException e) {
                        //throw new BaseException("第" + sort + "行,有效期存在交集，导入失败");
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
                return AjaxResult.error("报错信息",msgList);
            }
            for (int i = 0; i < purOutsourcePurchasePriceList.size(); i++) {
                String sid = judgeAdd(purOutsourcePurchasePriceList.get(i));
                int sort = i + 3;
                //存在相同配置则修改
                if (!sid.equals("500")) {
                    PurOutsourcePurchasePrice purOutsourcePurchasePrice = selectPurOutsourcePurchasePriceById(Long.valueOf(sid));
                    List<PurOutsourcePurchasePriceItem> priceItems = purOutsourcePurchasePriceList.get(i).getItemList();
                    List<PurOutsourcePurchasePriceItem> itemList = purOutsourcePurchasePrice.getItemList();
                    itemList.add(priceItems.get(0).setImportHandle(ConstantsEms.IMPORT));
                    purOutsourcePurchasePrice.setImportHandle(ConstantsEms.IMPORT);
                    purOutsourcePurchasePrice.setItemList(itemList);
                    try {
                        updatePurOutsourcePurchasePriceNew(purOutsourcePurchasePrice);
                    }catch (BaseException e){
                        throw new BaseException("第"+sort+"行,有效期存在交集，导入失败");
                    }
                } else {
                    try {
                        insertPurOutsourcePurchasePrice(purOutsourcePurchasePriceList.get(i));
                    } catch (BaseException e) {
                        throw new BaseException("第" + sort + "行,有效期存在交集，导入失败");
                    }
                }
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return AjaxResult.success("导入成功");
    }


    public void judgeImport(PurOutsourcePurchasePrice purOutsourcePurchasePrice){
        List<PurOutsourcePurchasePriceItem> itemList = purOutsourcePurchasePrice.getItemList();
        boolean judege = validTime(itemList);
        if(judege){
            itemList.forEach(item->{
                //二层校验
                judgeTime(purOutsourcePurchasePrice, item);
            });
        }else{
            throw new CustomException("存在交集");
        }
    }
    //填充-主表
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

    //校验输入日期的合法性
    public static boolean isValidDate(String str) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            // e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }

    //校验输入的是否是数字
    public static boolean isValidDouble(String str) {
        boolean convertSuccess = true;
        try {
            Double.valueOf(str);
        } catch (Exception e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }
}
