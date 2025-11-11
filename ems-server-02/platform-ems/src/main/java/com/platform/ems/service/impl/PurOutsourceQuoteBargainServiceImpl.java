package com.platform.ems.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.core.domain.R;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.DateUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.*;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.PurOutsourceQuotationRequest;
import com.platform.ems.domain.dto.request.PurOutsourceQuoteBargainRequest;
import com.platform.ems.domain.dto.response.PurOutsourceQuoteBargainReportResponse;
import com.platform.ems.domain.dto.response.PurOutsourceQuoteBargainResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.ems.plug.domain.ConMeasureUnit;
import com.platform.ems.plug.domain.ConTaxRate;
import com.platform.ems.plug.mapper.ConMeasureUnitMapper;
import com.platform.ems.plug.mapper.ConTaxRateMapper;
import com.platform.ems.service.IPurOutsourcePriceInforService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbUtil;
import com.platform.api.service.RemoteUserService;
import com.platform.common.core.domain.model.LoginUser;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.system.mapper.SystemUserMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.service.IPurOutsourceQuoteBargainService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 加工询报议价单主(询价/报价/核价/议价)Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-10
 */
@Service
@SuppressWarnings("all")
public class PurOutsourceQuoteBargainServiceImpl extends ServiceImpl<PurOutsourceQuoteBargainMapper, PurOutsourceQuoteBargain>  implements IPurOutsourceQuoteBargainService {
    @Autowired
    private PurOutsourceQuoteBargainMapper purOutsourceQuoteBargainMapper;
    @Autowired
    private PurOutsourceQuoteBargainAttachMapper attachmentMapper;
    @Autowired
    private PurOutsourceQuoteBargainItemMapper itemMapper;
    @Autowired
    private IPurOutsourcePriceInforService purOutsourcePriceInforService;
    @Autowired
    private PurOutsourcePurchasePriceMapper purOutsourcePurchasePriceMapper;
    @Autowired
    private PurOutsourcePurchasePriceItemMapper purchaseitemMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private ManProcessMapper  manProcessMapper;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private BasSkuMapper basSkuMapper;
    @Autowired
    private BasMaterialSkuMapper basMaterialSkuMapper;
    @Autowired
    private ISystemUserService userService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private SystemUserMapper sysUserMapper;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private ConTaxRateMapper conTaxRateMapper;
    @Autowired
    private ConMeasureUnitMapper conMeasureUnitMapper;
    @Autowired
    private BasProductSeasonMapper basProductSeasonMapper;

    private static final String TITLE = "加工报价/核价/议价单";

    private static final String PUR_PRICE_TITLE = "加工采购价";

    private static final String PUR_PRICE_ITEM_TITLE = "加工采购价明细";

    /**
     * 查询报议价单明细(报价/核价/议价)
     *
     * @param outsourceQuoteBargainItemSid 报议价单明细(报价/核价/议价)ID
     * @return 报议价单明细( 报价 / 核价 / 议价)
     */
    @Override
    public PurOutsourceQuoteBargainItem selectPurOutsourceRequestQuotationByItemId(Long outsourceQuoteBargainItemSid) {
        return itemMapper.selectPurOutsourceRequestQuotationItemByItemId(outsourceQuoteBargainItemSid);
    }

    /**
     * 设置明细表中的价格更新时间
     *
     * @param purOutsourceQuoteBargainItem 报议价单明细(报价/核价/议价)
     * @return 结果
     */
    private void setUpdateDate(PurOutsourceQuoteBargainItem purOutsourceQuoteBargainItem) {
        if (ConstantsPrice.BAOHEYI_STAGE_BJ.equals(purOutsourceQuoteBargainItem.getCurrentStage())) {
            purOutsourceQuoteBargainItem.setQuoteUpdateDate(new Date());
            purOutsourceQuoteBargainItem.setQuoteUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        if (ConstantsPrice.BAOHEYI_STAGE_HJ.equals(purOutsourceQuoteBargainItem.getCurrentStage())) {
            purOutsourceQuoteBargainItem.setCheckUpdateDate(new Date());
            purOutsourceQuoteBargainItem.setCheckUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(purOutsourceQuoteBargainItem.getCurrentStage())) {
            purOutsourceQuoteBargainItem.setPurchaseUpdateDate(new Date());
            purOutsourceQuoteBargainItem.setPurchaseUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
    }

    /**
     * 不含税价计算公式
     * @author chenkw
     * @param
     * @return 结果 不含税价
     */
    public BigDecimal formula(BigDecimal priceTax, BigDecimal taxRate){
        if (priceTax == null){
            return null;
        }
        BigDecimal notTax = priceTax.divide(BigDecimal.ONE.add(taxRate==null?BigDecimal.ONE:taxRate),6,BigDecimal.ROUND_HALF_UP);
        return notTax;

    }

    /**
     * 计算不含税价
     * @author chenkw
     * @param purOutsourceQuoteBargainItem
     * @return 结果
     */
    public void calculatePriceTax(PurOutsourceQuoteBargainItem purOutsourceQuoteBargainItem){
        if (ConstantsPrice.BAOHEYI_STAGE_BJ.equals(purOutsourceQuoteBargainItem.getCurrentStage())){
            //得到报价不含税金额
            purOutsourceQuoteBargainItem.setQuotePrice(formula(purOutsourceQuoteBargainItem.getQuotePriceTax(),purOutsourceQuoteBargainItem.getTaxRate()));
        }
        if (ConstantsPrice.BAOHEYI_STAGE_HJ.equals(purOutsourceQuoteBargainItem.getCurrentStage())){
            //得到核价不含税金额
            purOutsourceQuoteBargainItem.setCheckPrice(formula(purOutsourceQuoteBargainItem.getCheckPriceTax(),purOutsourceQuoteBargainItem.getTaxRate()));
        }
        if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(purOutsourceQuoteBargainItem.getCurrentStage())){
            //得到报价不含税金额
            purOutsourceQuoteBargainItem.setQuotePrice(formula(purOutsourceQuoteBargainItem.getQuotePriceTax(),purOutsourceQuoteBargainItem.getTaxRate()));
            //得到核价不含税金额
            purOutsourceQuoteBargainItem.setCheckPrice(formula(purOutsourceQuoteBargainItem.getCheckPriceTax(),purOutsourceQuoteBargainItem.getTaxRate()));
            //得到采购价不含税金额
            purOutsourceQuoteBargainItem.setPurchasePrice(formula(purOutsourceQuoteBargainItem.getPurchasePriceTax(),purOutsourceQuoteBargainItem.getTaxRate()));
            //得到确认价不含税金额
            purOutsourceQuoteBargainItem.setConfirmPrice(formula(purOutsourceQuoteBargainItem.getConfirmPriceTax(),purOutsourceQuoteBargainItem.getTaxRate()));
        }
    }

    /**
     * 修改加工报议价单明细(报价/核价/议价)
     *
     * @param purOutsourceQuoteBargainItem 加工报议价单明细(报价/核价/议价)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurOutsourceRequestQuotationItem(PurOutsourceQuoteBargainItem purOutsourceQuoteBargainItem) {
        purOutsourceQuoteBargainItem.setConfirmPriceTax(purOutsourceQuoteBargainItem.getPurchasePriceTax());
        checkPrice(purOutsourceQuoteBargainItem);
        setUpdateDate(purOutsourceQuoteBargainItem);
        calculatePriceTax(purOutsourceQuoteBargainItem);
        //因为核价员字段是存储在主表中的
        LambdaUpdateWrapper<PurOutsourceQuoteBargain> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PurOutsourceQuoteBargain::getOutsourceQuoteBargainSid,purOutsourceQuoteBargainItem.getOutsourceQuoteBargainSid());
        if (ConstantsPrice.BAOHEYI_STAGE_HJ.equals(purOutsourceQuoteBargainItem.getCurrentStage())){
            updateWrapper.set(PurOutsourceQuoteBargain::getDateCheck,purOutsourceQuoteBargainItem.getDateCheck());
            updateWrapper.set(PurOutsourceQuoteBargain::getChecker,purOutsourceQuoteBargainItem.getChecker());
            purOutsourceQuoteBargainMapper.update(null, updateWrapper);
            MongodbUtil.insertUserLogItem(purOutsourceQuoteBargainItem.getOutsourceQuoteBargainSid(), BusinessType.PRICE.getValue(),
                    "加工核价单", purOutsourceQuoteBargainItem.getItemNum(),"核价更新");
        }
        if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(purOutsourceQuoteBargainItem.getCurrentStage())){
            //涉及到主表需要修改的字段
            updateWrapper.set(PurOutsourceQuoteBargain::getDateConfirm,purOutsourceQuoteBargainItem.getDateConfirm());
            updateWrapper.set(PurOutsourceQuoteBargain::getStartDate,purOutsourceQuoteBargainItem.getStartDate());
            updateWrapper.set(PurOutsourceQuoteBargain::getEndDate,purOutsourceQuoteBargainItem.getEndDate());
            purOutsourceQuoteBargainMapper.update(null, updateWrapper);
            MongodbUtil.insertUserLogItem(purOutsourceQuoteBargainItem.getOutsourceQuoteBargainSid(), BusinessType.PRICE.getValue(),
                    "加工议价单", purOutsourceQuoteBargainItem.getItemNum(),"议价更新");
        }
        purOutsourceQuoteBargainItem.setUpdateDate(null).setUpdaterAccount(null);
        return itemMapper.updateAllById(purOutsourceQuoteBargainItem);
    }

    /**
     * 查询加工询报议价单主(询价/报价/核价/议价)
     *
     * @param outsourceRequestQuotationSid 加工询报议价单主(询价/报价/核价/议价)ID
     * @return 加工询报议价单主(询价/报价/核价/议价)
     */
    @Override
    public PurOutsourceQuoteBargain selectPurOutsourceRequestQuotationById(Long outsourceRequestQuotationSid) {
        PurOutsourceQuoteBargain purOutsourceQuoteBargain = purOutsourceQuoteBargainMapper.selectPurOutsourceRequestQuotationById(outsourceRequestQuotationSid);
        if(purOutsourceQuoteBargain !=null){
            List<PurOutsourceQuoteBargainAttach> attachmentList=attachmentMapper.selectPurOutsourceRequestQuotationAttachmentList(
                    new PurOutsourceQuoteBargainAttach().setOutsourceQuoteBargainSid(outsourceRequestQuotationSid));
            purOutsourceQuoteBargain.setAttachmentList(attachmentList);
            List<PurOutsourceQuoteBargainItem> itemList=itemMapper.selectPurOutsourceRequestQuotationItemById(outsourceRequestQuotationSid);
            purOutsourceQuoteBargain.setItemList(itemList);
        }
        MongodbUtil.find(purOutsourceQuoteBargain);
        return purOutsourceQuoteBargain;
    }

    /**
     * 查询加工询报议价单主(询价/报价/核价/议价)列表
     *
     * @param purOutsourceQuoteBargain 加工询报议价单主(询价/报价/核价/议价)
     * @return 加工询报议价单主(询价/报价/核价/议价)
     */
    @Override
    public List<PurOutsourceQuoteBargain> selectPurOutsourceRequestQuotationList(PurOutsourceQuoteBargain purOutsourceQuoteBargain) {
        List<PurOutsourceQuoteBargain>  outsourceRequestQuotationList =  purOutsourceQuoteBargainMapper.selectPurOutsourceRequestQuotationList(purOutsourceQuoteBargain);
        return outsourceRequestQuotationList;
    }

    //基本计量单位和采购价格单位（新建，编辑）
    public void setUnit(PurOutsourceQuoteBargainItem purPurchasePriceItem){
        purPurchasePriceItem.setConfirmPriceTax(purPurchasePriceItem.getPurchasePriceTax());
        calculatePriceTax(purPurchasePriceItem);
        purPurchasePriceItem.setUnitPrice(purPurchasePriceItem.getUnitBase());
        if(purPurchasePriceItem.getUnitBase().equals(purPurchasePriceItem.getUnitPrice())){
            purPurchasePriceItem.setUnitConversionRate(BigDecimal.ONE);
        }else{
            if(purPurchasePriceItem.getUnitConversionRate()==null){
                throw new  CustomException("采购价单位“与”基本计量单位“不一致，单位换算比例不允许为空");
            }
        }
    }

    /**
     * 查询加工询报议价单主 --- 查询页面
     *
     * @param request 加工询报议价单主请求实体
     * @return 加工询报议价单主(询价/报价/核价/议价)
     */
    @Override
    public List<PurOutsourceQuoteBargainReportResponse> report(PurOutsourceQuotationRequest request) {
        //报价查询页面，查询到建单来源是”询价“”报价“且当前所属阶段是”报价“。或者建单来源是“报价”且当前所属阶段是”报价“”核价“”议价“
        if (ConstantsPrice.BAOHEYI_STAGE_BJ.equals(request.getStage())){
            request.setStageList(new String[]{request.getStage()});
            request.setCreatedStageList(new String[]{ConstantsPrice.BAOHEYI_STAGE_XJ,ConstantsPrice.BAOHEYI_STAGE_BJ});
            request.setCurrentStageList(new String[]{ConstantsPrice.BAOHEYI_STAGE_BJ,ConstantsPrice.BAOHEYI_STAGE_HJ,ConstantsPrice.BAOHEYI_STAGE_YJ});
        }
        //核价查询页面，查询到建单来源是”询价“”报价“”核价“且当前所属阶段是”核价“。或者建单来源是”核价“且当前所属阶段是”核价“”议价“
        if (ConstantsPrice.BAOHEYI_STAGE_HJ.equals(request.getStage())){
            request.setStageList(new String[]{request.getStage(),ConstantsPrice.BAOHEYI_STAGE_YJ});
            request.setCreatedStageList(new String[]{ConstantsPrice.BAOHEYI_STAGE_XJ,ConstantsPrice.BAOHEYI_STAGE_BJ,ConstantsPrice.BAOHEYI_STAGE_HJ});
            request.setCurrentStageList(new String[]{ConstantsPrice.BAOHEYI_STAGE_HJ,ConstantsPrice.BAOHEYI_STAGE_YJ});
        }
        //核价查询页面，查询到建单来源是”询价“”报价“”核价“”议价“且当前所属阶段是”议价“。
        if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(request.getStage())){
            request.setStageList(new String[]{request.getStage()});
            request.setCreatedStageList(new String[]{ConstantsPrice.BAOHEYI_STAGE_XJ,ConstantsPrice.BAOHEYI_STAGE_BJ,ConstantsPrice.BAOHEYI_STAGE_HJ,ConstantsPrice.BAOHEYI_STAGE_YJ});
            request.setCurrentStageList(new String[]{ConstantsPrice.BAOHEYI_STAGE_YJ});
        }
        List<PurOutsourceQuoteBargainReportResponse> items = itemMapper.reportOutPurRequest(request);
        return items;
    }

    /**
     * 查询加工询报议价单主 --- 明细报表页面
     *
     * @param request 加工询报议价单主请求实体
     * @return 加工询报议价单主(询价/报价/核价/议价)
     */
    @Override
    public List<PurOutsourceQuoteBargainResponse> getReport(PurOutsourceQuoteBargainRequest request){
        //报价查询页面，查询到建单来源是”询价“”报价“且当前所属阶段是”报价“。或者建单来源是“报价”且当前所属阶段是”报价“”核价“”议价“
        if (ConstantsPrice.BAOHEYI_STAGE_BJ.equals(request.getStage())){
            request.setCreatedStageList(new String[]{ConstantsPrice.BAOHEYI_STAGE_XJ,ConstantsPrice.BAOHEYI_STAGE_BJ});
            request.setCurrentStageList(new String[]{ConstantsPrice.BAOHEYI_STAGE_BJ,ConstantsPrice.BAOHEYI_STAGE_HJ,ConstantsPrice.BAOHEYI_STAGE_YJ});
        }
        //核价查询页面，查询到建单来源是”询价“”报价“”核价“且当前所属阶段是”核价“。或者建单来源是”核价“且当前所属阶段是”核价“”议价“
        if (ConstantsPrice.BAOHEYI_STAGE_HJ.equals(request.getStage())){
            request.setCreatedStageList(new String[]{ConstantsPrice.BAOHEYI_STAGE_XJ,ConstantsPrice.BAOHEYI_STAGE_BJ,ConstantsPrice.BAOHEYI_STAGE_HJ});
            request.setCurrentStageList(new String[]{ConstantsPrice.BAOHEYI_STAGE_HJ,ConstantsPrice.BAOHEYI_STAGE_YJ});
        }
        //核价查询页面，查询到建单来源是”询价“”报价“”核价“”议价“且当前所属阶段是”议价“。
        if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(request.getStage())){
            request.setCreatedStageList(new String[]{ConstantsPrice.BAOHEYI_STAGE_XJ,ConstantsPrice.BAOHEYI_STAGE_BJ,ConstantsPrice.BAOHEYI_STAGE_HJ,ConstantsPrice.BAOHEYI_STAGE_YJ});
            request.setCurrentStageList(new String[]{ConstantsPrice.BAOHEYI_STAGE_YJ});
        }
        List<PurOutsourceQuoteBargainResponse> list = itemMapper.reportNew(request);
        return list;
    }

    /**
     * 新增加工询报议价单主(询价/报价/核价/议价)
     * 需要注意编码重复校验
     * @param purOutsourceQuoteBargain 加工询报议价单主(询价/报价/核价/议价)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurOutsourceRequestQuotation(PurOutsourceQuoteBargain request) {
        if (request.getOutsourceInquirySid() != null){
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().eq(SysTodoTask::getDocumentSid,request.getOutsourceInquirySid()));
        }
        int row = 0;
        judgeNull(request);
        List<PurOutsourceQuoteBargainItem> list = request.getItemList();
        String handleStatus = request.getHandleStatus();
        if(handleStatus.equals(ConstantsEms.CHECK_STATUS)){
            if(CollectionUtils.isEmpty(list)){
                throw  new CustomException("明细行不允许为空");
            }
            request.setConfirmDate(new Date())
                    .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        List<PurOutsourceQuoteBargainItem> itemList = request.getItemList();
        if (CollectionUtil.isNotEmpty(itemList)){
            itemList.forEach(li->{
                PurOutsourceQuoteBargain purOutsourceQuoteBargain = new PurOutsourceQuoteBargain();
                BeanCopyUtils.copyProperties(request,purOutsourceQuoteBargain);
                li.setCurrentStage(purOutsourceQuoteBargain.getCreatedStage());
                li.setHandleStatus(ConstantsEms.SAVA_STATUS);
                li.setCreatorAccount(null).setCreateDate(null);
                li.setUpdaterAccount(null).setUpdateDate(null);
                li.setConfirmerAccount(null).setConfirmDate(null);
                setUnit(li);
                purOutsourceQuoteBargain.setItemList(new ArrayList<PurOutsourceQuoteBargainItem>(){
                    {
                        add(li);
                    }
                });
                purOutsourceQuoteBargainMapper.insert(purOutsourceQuoteBargain);
                insertAttachment(purOutsourceQuoteBargain.getOutsourceQuoteBargainSid(), request.getAttachmentList());
                insertItem(purOutsourceQuoteBargain);
                //待办通知
                PurOutsourceQuoteBargain purchasePrice = purOutsourceQuoteBargainMapper.selectById(purOutsourceQuoteBargain.getOutsourceQuoteBargainSid());
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_PUR_OUT_QUOTE_BARGAIN)
                        .setDocumentItemSid(li.getOutsourceQuoteBargainItemSid())
                        .setDocumentSid(li.getOutsourceQuoteBargainSid());
                String title = "";
                String comment = "";
                if (ConstantsPrice.BAOHEYI_STAGE_BJ.equals(request.getCreatedStage())){
                    title = "加工报价单";
                    comment = "报价新建";
                }
                if (ConstantsPrice.BAOHEYI_STAGE_HJ.equals(request.getCreatedStage())){
                    title = "加工核价单";
                    comment = "核价新建";
                }
                if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(request.getCreatedStage())){
                    title = "加工议价单";
                    comment = "议价新建";
                }
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle(title + purchasePrice.getOutsourceQuoteBargainCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(purchasePrice.getOutsourceQuoteBargainCode().toString())
                            .setNoticeDate(new Date())
                            .setMenuId(ConstantsWorkbench.pur_outsource_quote_bargain)
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
                if (request.getImportHandle() == null) {
                    //插入日志
                    MongodbUtil.insertUserLogItem(li.getOutsourceQuoteBargainSid(), BusinessType.INSERT.getValue(), TITLE, li.getItemNum(),comment);
                } else {
                    //插入日志
                    MongodbUtil.insertUserLogItem(li.getOutsourceQuoteBargainSid(), BusinessType.IMPORT.getValue(), TITLE, li.getItemNum(),comment);
                }
            });
            row = itemList.size();
        }
        return row;
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
        List<PurOutsourceQuoteBargainItem> purOutsourceQuoteBargainItems = itemMapper.selectList(new QueryWrapper<PurOutsourceQuoteBargainItem>().lambda()
                .in(PurOutsourceQuoteBargainItem::getOutsourceQuoteBargainItemSid, ids)
        );
        List<Long> longs = purOutsourceQuoteBargainItems.stream().distinct().map(li -> li.getOutsourceQuoteBargainSid()).collect(Collectors.toList());
        itemMapper.deleteBatchIds(ids);
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentItemSid, ids));
        //插入日志
        ids.forEach(sid->{
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), null, TITLE);
        });
        if (CollectionUtil.isNotEmpty(longs)){
            longs.forEach(sid->{
                List<PurOutsourceQuoteBargainItem> itemList = itemMapper.selectList(new QueryWrapper<PurOutsourceQuoteBargainItem>().lambda()
                        .eq(PurOutsourceQuoteBargainItem::getOutsourceQuoteBargainSid, sid)
                );
                if(CollectionUtil.isEmpty(itemList)){
                    //明细为空时，删除对应的主表
                    purOutsourceQuoteBargainMapper.deleteById(sid);
                    sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                            .in(SysTodoTask::getDocumentSid, sid));
                    //删除附件
                    attachmentMapper.delete(new QueryWrapper<PurOutsourceQuoteBargainAttach>().lambda().in(PurOutsourceQuoteBargainAttach::getOutsourceQuoteBargainSid, sid));
                }
            });
        }
        return 1;
    }

    /**
     * 修改加工询报议价单主(询价/报价/核价/议价)
     *
     * @param purOutsourceQuoteBargain 加工询报议价单主(询价/报价/核价/议价)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurOutsourceRequestQuotation(PurOutsourceQuoteBargain purOutsourceQuoteBargain) {
        List<PurOutsourceQuoteBargainItem> list = purOutsourceQuoteBargain.getItemList();
        String handleStatus = purOutsourceQuoteBargain.getHandleStatus();
        if(handleStatus.equals(ConstantsEms.CHECK_STATUS)){
            purOutsourceQuoteBargain.setConfirmDate(new Date())
                    .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            if(CollectionUtils.isEmpty(list)){
                throw  new CustomException("确认时，明细行不为空");
            }
        }
        judgeNull(purOutsourceQuoteBargain);
        purOutsourceQuoteBargain.setUpdateDate(null).setUpdaterAccount(null);
        int row= purOutsourceQuoteBargainMapper.updateById(purOutsourceQuoteBargain);
        if(row>0){
            insertAttachment(purOutsourceQuoteBargain.getOutsourceQuoteBargainSid(), purOutsourceQuoteBargain.getAttachmentList());
            List<PurOutsourceQuoteBargainItem> itemList = purOutsourceQuoteBargain.getItemList();
            itemList.forEach(item->{
                setUnit(item);
                setUpdateDate(item);
            });
            List<PurOutsourceQuoteBargainItem> purOutsourceQuoteBargainItems = itemMapper.selectList(new QueryWrapper<PurOutsourceQuoteBargainItem>().lambda()
                    .eq(PurOutsourceQuoteBargainItem::getOutsourceQuoteBargainSid, purOutsourceQuoteBargain.getOutsourceQuoteBargainSid())
            );
            List<Long> longs = purOutsourceQuoteBargainItems.stream().map(li -> li.getOutsourceQuoteBargainItemSid()).collect(Collectors.toList());
            List<Long> longsNow = itemList.stream().map(li -> li.getOutsourceQuoteBargainItemSid()).collect(Collectors.toList());
            //两个集合取差集
            List<Long> reduce = longs.stream().filter(item -> !longsNow.contains(item)).collect(Collectors.toList());
            //删除明细
            if(CollectionUtil.isNotEmpty(reduce)){
                List<PurOutsourceQuoteBargainItem> reduceList = itemMapper.selectList(new QueryWrapper<PurOutsourceQuoteBargainItem>().lambda()
                        .in(PurOutsourceQuoteBargainItem::getOutsourceQuoteBargainItemSid, reduce)
                );
                if (CollectionUtil.isNotEmpty(reduceList)){
                    reduceList.forEach(li->{
                        //插入日志
                        MongodbUtil.insertUserLogItem(li.getOutsourceQuoteBargainSid(), BusinessType.DELETE.getValue(),TITLE,li.getItemNum());
                    });
                }
                itemMapper.deleteBatchIds(reduce);
                //删除待办
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .in(SysTodoTask::getDocumentSid, reduce));
            }
            //修改明细
            List<PurOutsourceQuoteBargainItem> exitItem = itemList.stream().filter(li -> li.getOutsourceQuoteBargainItemSid() != null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(exitItem)){
                exitItem.forEach(li->{
                    itemMapper.updateById(li);
                    PurOutsourceQuoteBargainItem oldItem = itemMapper.selectById(li.getOutsourceQuoteBargainItemSid());
                    String bussiness=ConstantsEms.CHECK_STATUS.equals(oldItem.getHandleStatus())?"变更":"编辑";
                    //插入日志
                    MongodbUtil.insertUserLogItem(li.getOutsourceQuoteBargainSid(), bussiness,TITLE,li.getItemNum());
                });
            }
            //新增明细
            List<PurOutsourceQuoteBargainItem> nullItem = itemList.stream().filter(li -> li.getOutsourceQuoteBargainItemSid() == null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(nullItem)){
                int max = purOutsourceQuoteBargainItems.stream().mapToInt(li -> li.getItemNum()).max().getAsInt();
                for (int i = 0; i < nullItem.size(); i++) {
                    int maxItem=max+i+1;
                    nullItem.get(i).setItemNum(maxItem);
                    nullItem.get(i).setOutsourceQuoteBargainSid(purOutsourceQuoteBargain.getOutsourceQuoteBargainSid());
                    nullItem.get(i).setHandleStatus(ConstantsEms.SAVA_STATUS);
                    itemMapper.insert(nullItem.get(i));
                }
                if (CollectionUtil.isNotEmpty(nullItem)){
                    nullItem.forEach(li->{
                        //插入日志
                        MongodbUtil.insertUserLogItem(li.getOutsourceQuoteBargainSid(), BusinessType.INSERT.getValue(),TITLE,li.getItemNum());
                    });
                }
            }
        }
        //加工议价回写
        insertOutsourcePurchasePrice(purOutsourceQuoteBargain);
        return row;
    }

    /**
     * 批量删除加工报议价单主(报价/核价/议价)
     * @author chenkw
     * @param quoteBargainSids 需要删除的加工报议价单主(报价/核价/议价)ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurOutsourceRequestQuotationByIds(List<Long> outsourceRequestQuotationSids) {
        if (CollectionUtil.isEmpty(outsourceRequestQuotationSids)){
            return 0;
        }
        int row = purOutsourceQuoteBargainMapper.deleteBatchIds(outsourceRequestQuotationSids);
        //删除待办
        sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().in(SysTodoTask::getDocumentSid, outsourceRequestQuotationSids));
        //删除明细
        itemMapper.delete(new QueryWrapper<PurOutsourceQuoteBargainItem>().lambda().in(PurOutsourceQuoteBargainItem::getOutsourceQuoteBargainSid, outsourceRequestQuotationSids));
        //删除附件
        attachmentMapper.delete(new QueryWrapper<PurOutsourceQuoteBargainAttach>().lambda()
                .in(PurOutsourceQuoteBargainAttach::getOutsourceQuoteBargainSid, outsourceRequestQuotationSids));
        //插入日志
        outsourceRequestQuotationSids.forEach(sid -> {
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), null, TITLE);
        });
        return row;
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

    /**
     * 检查明细的相关价格金额有没有填写完整
     * @chenkw
     * @param purOutsourceQuoteBargainItem 加工报议价单明细(报价/核价/议价)
     * @return 加工报议价单明细(报价/核价/议价)集合
     */
    @Override
    public void checkPrice(PurOutsourceQuoteBargainItem purOutsourceQuoteBargainItem){
        if (purOutsourceQuoteBargainItem.getHandleStatus() == null && purOutsourceQuoteBargainItem.getOutsourceQuoteBargainItemSid() != null){
            purOutsourceQuoteBargainItem = itemMapper.selectById(purOutsourceQuoteBargainItem.getOutsourceQuoteBargainItemSid());
        }
        if (ConstantsPrice.BAOHEYI_STAGE_BJ.equals(purOutsourceQuoteBargainItem.getCurrentStage())) {
            if (purOutsourceQuoteBargainItem.getQuotePriceTax() == null) {
                throw new CustomException("报价未填写");
            }
        }
        if (ConstantsPrice.BAOHEYI_STAGE_HJ.equals(purOutsourceQuoteBargainItem.getCurrentStage())) {
            if (purOutsourceQuoteBargainItem.getCheckPriceTax() == null) {
                throw new CustomException("核定价未填写");
            }
        }
        if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(purOutsourceQuoteBargainItem.getCurrentStage())) {
            if (purOutsourceQuoteBargainItem.getPurchasePriceTax() == null) {
                throw new CustomException("采购价未填写");
            }
        }
    }

    /**
     * 检查是否已存在于采购价和采购成本核算流程中
     * @author chenkw
     * @return
     */
    @Override
    public void checkUnique(PurOutsourceQuoteBargain purOutsourceQuoteBargain){
        purOutsourceQuoteBargain.getItemList().forEach(item->{
            PurOutsourceQuoteBargainItem request = new PurOutsourceQuoteBargainItem();
            //供应商，甲供料方式，采购模式，有效期起/至，物料商品sid，价格维度，sku1sid
            request.setVendorSid(purOutsourceQuoteBargain.getVendorSid()).setProcessSid(item.getProcessSid())
                    .setMaterialSid(item.getMaterialSid());
            //查询出不是已确认的单据
            request.setHandleStatusList(new String[]{HandleStatus.SUBMIT.getCode(),HandleStatus.CHANGEAPPROVAL.getCode()});
            //报核议价单
            List<PurOutsourceQuoteBargainItem> bargainItemList = new ArrayList<>();
            //采购价单
            List<PurOutsourcePurchasePriceItem> priceItemList = new ArrayList<>();
            if (ConstantsPrice.PRICE_DIMENSION.equals(item.getPriceDimension())){
                bargainItemList = itemMapper.selectPurOutsourceRequestQuotationItemList(request);
                priceItemList = itemMapper.selectPriceItemList(request);
            }
            else if (ConstantsPrice.PRICE_DIMENSION_SKU1.equals(item.getPriceDimension())){
                //1、查按色 sku1Sid
                request.setSku1Sid(item.getSku1Sid()).setPriceDimension(item.getPriceDimension());
                bargainItemList = itemMapper.selectPurOutsourceRequestQuotationItemList(request);
                priceItemList = itemMapper.selectPriceItemList(request);
                //如果（1）没查到，则接着查：2、查价格维度：按款
                request.setSku1Sid(null).setPriceDimension(ConstantsPrice.PRICE_DIMENSION);
                if (CollectionUtil.isEmpty(bargainItemList)){
                    bargainItemList = itemMapper.selectPurOutsourceRequestQuotationItemList(request);
                }
                if (CollectionUtil.isEmpty(priceItemList)){
                    priceItemList = itemMapper.selectPriceItemList(request);
                }
            }else {}
            String materialName = item.getMaterialName() == null ? "" : item.getMaterialName();
            if (CollectionUtil.isNotEmpty(bargainItemList)){
                //如果是编辑 的，那要去掉跟本身的单据校验冲突
                if (purOutsourceQuoteBargain.getOutsourceQuoteBargainSid() != null){
                    bargainItemList = bargainItemList.stream().filter(o-> !o.getOutsourceQuoteBargainSid()
                            .equals(purOutsourceQuoteBargain.getOutsourceQuoteBargainSid())).collect(Collectors.toList());
                }
                if (CollectionUtil.isNotEmpty(bargainItemList)){
                    String stage = bargainItemList.get(0).getCurrentStage();
                    String stageName = setTitle(stage);
                    throw new CustomException(materialName + "存在相应的审批中的" + stageName + bargainItemList.get(0).getOutsourceQuoteBargainCode() + "，请先处理此" + stageName);
                }
            }
            if (CollectionUtil.isNotEmpty(priceItemList)){
                throw new CustomException(materialName + "存在相应的审批中的加工采购价信息" + priceItemList.get(0).getOutsourcePurchasePriceCode() + ",请先处理此加工采购价信息");
            }
        });
    }

    /**
     * 检查有效期范围
     * @chenkw
     * @return 结果
     */
    @Override
    public void checkDateRange(PurOutsourceQuoteBargain purOutsourceQuoteBargain){
        purOutsourceQuoteBargain.getItemList().forEach(item->{
            //议价再去判断
            if (!ConstantsPrice.BAOHEYI_STAGE_YJ.equals(item.getCurrentStage())){
                return;
            }
            String materialName = item.getMaterialName() == null ? "" : item.getMaterialName();
            //供应商，甲供料方式，采购模式，有效期起/至，物料商品sid，价格维度，sku1sid
            PurOutsourceQuoteBargainItem request = new PurOutsourceQuoteBargainItem();
            request.setVendorSid(purOutsourceQuoteBargain.getVendorSid()).setProcessSid(item.getProcessSid())
                    .setStartDate(purOutsourceQuoteBargain.getStartDate()).setEndDate(purOutsourceQuoteBargain.getEndDate())
                    .setMaterialSid(item.getMaterialSid());
            //加工采购价单
            List<PurOutsourcePurchasePriceItem> priceItemList = new ArrayList<>();
            //按款
            if (ConstantsPrice.PRICE_DIMENSION.equals(item.getPriceDimension())){
                priceItemList = itemMapper.selectPriceItemList(request);
            }
            //按色
            if (ConstantsPrice.PRICE_DIMENSION_SKU1.equals(item.getPriceDimension())){
                //1、查sku1Sid
                request.setSku1Sid(item.getSku1Sid());
                request.setPriceDimension(item.getPriceDimension());
                priceItemList = itemMapper.selectPriceItemList(request);
                //如果（1）没查到，则接着查：2、查价格维度：按款
                request.setSku1Sid(null).setPriceDimension(ConstantsPrice.PRICE_DIMENSION);
                if (CollectionUtil.isEmpty(priceItemList)){
                    priceItemList = itemMapper.selectPriceItemList(request);
                }
            }
            //采购价单
            String priceDimensionName = "";
            if (CollectionUtil.isNotEmpty(priceItemList) && ConstantsPrice.BAOHEYI_STAGE_YJ.equals(item.getCurrentStage())){
                //对有效期止倒叙排序，方便获得最大的有效期止
                priceItemList = priceItemList.stream().sorted(Comparator.comparing(PurOutsourcePurchasePriceItem::getEndDate,Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                //标记是否符合条件，1：符合可以更新价格的条件，0：不符合，报错
                int flag = 0;
                //查询是否存在有效期起和有效期止刚好相同的单据
                for (PurOutsourcePurchasePriceItem priceItem : priceItemList) {
                    if (purOutsourceQuoteBargain.getStartDate().compareTo(priceItem.getStartDate()) == 0 &&
                            purOutsourceQuoteBargain.getEndDate().compareTo(priceItem.getEndDate()) == 0 &&
                            item.getPriceDimension().equals(priceItem.getPriceDimension())){
                        flag = 1;
                    }else {
                        flag = 0;
                        break;
                    }
                }
                //对比同 维度的 按款或者按色
                List<PurOutsourcePurchasePriceItem> priceItemList1 = priceItemList.stream().filter(o->item.getPriceDimension().equals(o.getPriceDimension())).collect(Collectors.toList());
                //另一个维度的用来获取提示编号
                List<PurOutsourcePurchasePriceItem> priceItemList2 = priceItemList.stream().filter(o->!item.getPriceDimension().equals(o.getPriceDimension())).collect(Collectors.toList());
                //采购价编号
                Long code = null;
                if (CollectionUtil.isNotEmpty(priceItemList1)){
                    //当前的有效期止大于存在交集中有效期止最大的有效期止，且，（当前的有效期起大于存在交集中有效期止最大的有效期起，且，当前有效期起小于存在交集中有效期止最大的有效期止）
                    if (purOutsourceQuoteBargain.getEndDate().compareTo(priceItemList1.get(0).getEndDate()) >= 0 &&
                            purOutsourceQuoteBargain.getStartDate().compareTo(priceItemList1.get(0).getEndDate()) <= 0 &&
                            purOutsourceQuoteBargain.getStartDate().compareTo(priceItemList1.get(0).getStartDate()) > 0 ){
                        flag = 1;
                    }
                    else {
                        code = priceItemList1.get(0).getOutsourcePurchasePriceCode();
                        if (item.getPriceDimension().equals("K")){
                            priceDimensionName = "按款的";
                        }else if (item.getPriceDimension().equals("K1")){
                            priceDimensionName = "按色(SKU1)的";
                        }
                    }
                }else {
                    code = priceItemList2.get(0).getOutsourcePurchasePriceCode();
                    if (item.getPriceDimension().equals("K")){
                        priceDimensionName = "按色(SKU1)的";
                    }else if (item.getPriceDimension().equals("K1")){
                        priceDimensionName = "按款的";
                    }
                }
                String priceCode = "";
                if (code != null){
                    priceCode = code.toString();
                }
                if (flag != 1){
                    throw new CustomException(materialName + "当前存在" + priceDimensionName + "加工采购价"+ priceCode + "的有效期与此议价单的有效期区间\n\n存在交集，请先手工更新旧的有效期后，再进行此操作。");
                }
            }
        });
    }

    /**
     * 价格回写加工采购价
     * @author chenkw
     * @return title
     */
    public void insertOutsourcePurchasePrice(PurOutsourceQuoteBargain purOutsourceQuoteBargain){
        if (!ConstantsEms.CHECK_STATUS.equals(purOutsourceQuoteBargain.getHandleStatus())){
            return;
        }
        purOutsourceQuoteBargain.getItemList().forEach(item->{
            if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(item.getCurrentStage())){
                //先判断是更新价格还是插入价格，就是要先处理是否存在交集，然后对交集的情况进行分类处理
                PurOutsourceQuoteBargainItem request = new PurOutsourceQuoteBargainItem();
                //供应商，甲供料方式，采购模式，有效期起/至，物料商品sid，价格维度，sku1sid
                request.setVendorSid(purOutsourceQuoteBargain.getVendorSid()).setProcessSid(item.getProcessSid())
                        .setStartDate(purOutsourceQuoteBargain.getStartDate()).setEndDate(purOutsourceQuoteBargain.getEndDate())
                        .setMaterialSid(item.getMaterialSid());
                //采购价单
                List<PurOutsourcePurchasePriceItem> priceItemList = new ArrayList<>();
                //按款
                if (ConstantsPrice.PRICE_DIMENSION.equals(item.getPriceDimension())){
                    priceItemList = itemMapper.selectPriceItemList(request);
                }
                //按色
                if (ConstantsPrice.PRICE_DIMENSION_SKU1.equals(item.getPriceDimension())){
                    //1、查sku1Sid
                    request.setSku1Sid(item.getSku1Sid());
                    request.setPriceDimension(item.getPriceDimension());
                    priceItemList = itemMapper.selectPriceItemList(request);
                    //如果（1）没查到，则接着查：2、查价格维度：按款
                    request.setSku1Sid(null).setPriceDimension(ConstantsPrice.PRICE_DIMENSION);
                    if (CollectionUtil.isEmpty(priceItemList)){
                        priceItemList = itemMapper.selectPriceItemList(request);
                    }
                }
                //用来标记是否回写采购价，1：回写，0：不回写
                int flag = 1;
                //采购价单
                if (CollectionUtil.isNotEmpty(priceItemList)){
                    flag = 0;
                    boolean check = true; //判读走哪一步，1、走了更新价格就不要再走更新日期的；2、没有走更新价格就看看是不是可以走更新日期
                    //对有效期止倒叙排序，方便获得最大的有效期止
                    priceItemList = priceItemList.stream().sorted(Comparator.comparing(PurOutsourcePurchasePriceItem::getEndDate,Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                    //查询是否存在有效期起和有效期止刚好相同的单据
                    for (PurOutsourcePurchasePriceItem priceItem : priceItemList) {
                        if (purOutsourceQuoteBargain.getStartDate().compareTo(priceItem.getStartDate()) == 0 &&
                                purOutsourceQuoteBargain.getEndDate().compareTo(priceItem.getEndDate()) == 0){
                            //则 更新采购价
                            priceItem.setTaxRate(item.getTaxRate())
                                    .setPurchasePriceTax(item.getPurchasePriceTax()).setPurchasePrice(item.getPurchasePrice())
                                    .setUnitBase(item.getUnitBase()).setUnitPrice(item.getUnitPrice()).setUnitConversionRate(item.getUnitConversionRate());
                            purchaseitemMapper.updateById(priceItem);
                            MongodbUtil.insertUserLogItem(priceItem.getOutsourcePurchasePriceSid(), BusinessType.PRICE.getValue(), PUR_PRICE_ITEM_TITLE, priceItem.getItemNum() , "来自议价单");
                            check = false;  //拒绝走更新日期了
                        }
                    }
                    if (check){
                        priceItemList = priceItemList.stream().filter(o->item.getPriceDimension().equals(o.getPriceDimension())).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(priceItemList)){
                            //当前的有效期止大于存在交集中有效期止最大的有效期止，且，（当前的有效期起大于存在交集中有效期止最大的有效期起，且，当前有效期起小于存在交集中有效期止最大的有效期止）
                            if (purOutsourceQuoteBargain.getEndDate().compareTo(priceItemList.get(0).getEndDate()) >= 0 &&
                                    purOutsourceQuoteBargain.getStartDate().compareTo(priceItemList.get(0).getEndDate()) <= 0 &&
                                    purOutsourceQuoteBargain.getStartDate().compareTo(priceItemList.get(0).getStartDate()) > 0 ){
                                //则 将旧采购价的有效期止变更成当前有效期起的前一天，然后写入新的采购价
                                PurOutsourcePurchasePriceItem priceItem = priceItemList.get(0);
                                SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(item.getStartDate());
                                calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
                                try {
                                    Date endDate = dft.parse(dft.format(calendar.getTime()));
                                    priceItem.setEndDate(endDate);
                                    purchaseitemMapper.updateById(priceItem);
                                    MongodbUtil.insertUserLogItem(priceItem.getOutsourcePurchasePriceSid(), BusinessType.CHANGE.getValue(), PUR_PRICE_ITEM_TITLE, priceItem.getItemNum(), "更新有效期(至)");
                                    flag = 1;
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                if (flag == 1){
                    //主表
                    //按款：(因为如果sku1Sid为空时，sql查询会是： sku1_sid = null，就查询不了数据)
                    QueryWrapper<PurOutsourcePurchasePrice> queryWrapper = new QueryWrapper<>();
                    queryWrapper.lambda().eq(PurOutsourcePurchasePrice::getVendorSid,purOutsourceQuoteBargain.getVendorSid())
                            .eq(PurOutsourcePurchasePrice::getMaterialSid,item.getMaterialSid())
                            .eq(PurOutsourcePurchasePrice::getProcessSid,item.getProcessSid())
                            .eq(PurOutsourcePurchasePrice::getPriceDimension,item.getPriceDimension());
                    if (item.getSku1Sid() != null){
                        queryWrapper.lambda().eq(PurOutsourcePurchasePrice::getSku1Sid,item.getSku1Sid());
                    }
                    List<PurOutsourcePurchasePrice> priceList = purOutsourcePurchasePriceMapper.selectList(queryWrapper);
                    if (CollectionUtil.isNotEmpty(priceList)) {
                        priceList.forEach(o -> {
                            //明细
                            //获取主表当前明细最大行号
                            List<PurOutsourcePurchasePriceItem> purchasePriceItemList = purchaseitemMapper.selectList(new QueryWrapper<PurOutsourcePurchasePriceItem>()
                                    .lambda().eq(PurOutsourcePurchasePriceItem::getOutsourcePurchasePriceSid,o.getOutsourcePurchasePriceSid()));
                            purchasePriceItemList = purchasePriceItemList.stream().sorted(Comparator.comparing(PurOutsourcePurchasePriceItem::getItemNum).reversed()).collect(Collectors.toList());
                            PurOutsourcePurchasePriceItem priceItem = new PurOutsourcePurchasePriceItem();
                            BeanCopyUtils.copyProperties(item, priceItem);
                            priceItem.setOutsourcePurchasePriceSid(o.getOutsourcePurchasePriceSid());
                            priceItem.setStartDate(purOutsourceQuoteBargain.getStartDate()).setEndDate(purOutsourceQuoteBargain.getEndDate());
                            priceItem.setRemark(item.getRemarkConfirm());
                            priceItem.setCreateDate(new Date()).setCreatorAccount(null).setItemNum(purchasePriceItemList.get(0).getItemNum()+1)
                                    .setCurrency(ConstantsFinance.CURRENCY_CNY).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN);
                            //插入
                            purchaseitemMapper.insert(priceItem);
                            MongodbUtil.insertUserLogItem(priceItem.getOutsourcePurchasePriceSid(), BusinessType.CHECK.getValue(), PUR_PRICE_ITEM_TITLE, purchasePriceItemList.get(0).getItemNum()+1 , "来自议价单");
                        });
                    }
                    else {
                        //主表
                        PurOutsourcePurchasePrice price = new PurOutsourcePurchasePrice();
                        price.setVendorSid(purOutsourceQuoteBargain.getVendorSid())
                                .setMaterialCategory(purOutsourceQuoteBargain.getMaterialCategory())
                                .setPurchaseOrg(purOutsourceQuoteBargain.getPurchaseOrg());
                        price.setMaterialSid(item.getMaterialSid())
                                .setBarcodeSid(item.getBarcodeSid())
                                .setProcessSid(item.getProcessSid())
                                .setPriceDimension(item.getPriceDimension())
                                .setSku1Sid(item.getSku1Sid())
                                .setSku2Sid(item.getSku2Sid());
                        price.setStatus(ConstantsEms.ENABLE_STATUS).setHandleStatus(ConstantsEms.CHECK_STATUS).setRemark(purOutsourceQuoteBargain.getRemarkConfirm());
                        price.setConfirmerAccount(purOutsourceQuoteBargain.getConfirmerAccount()).setConfirmDate(purOutsourceQuoteBargain.getConfirmDate());
                        //插入
                        purOutsourcePurchasePriceMapper.insert(price);
                        //明细
                        PurOutsourcePurchasePriceItem priceItem = new PurOutsourcePurchasePriceItem();
                        BeanCopyUtils.copyProperties(item, priceItem);
                        priceItem.setOutsourcePurchasePriceSid(price.getOutsourcePurchasePriceSid());
                        priceItem.setStartDate(purOutsourceQuoteBargain.getStartDate()).setEndDate(purOutsourceQuoteBargain.getEndDate());
                        priceItem.setRemark(item.getRemarkConfirm());
                        priceItem.setCreateDate(new Date()).setCreatorAccount(null)
                                .setCurrency(ConstantsFinance.CURRENCY_CNY).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN);
                        //插入
                        purchaseitemMapper.insert(priceItem);
                        MongodbUtil.insertUserLogItem(priceItem.getOutsourcePurchasePriceSid(), BusinessType.CHECK.getValue(), PUR_PRICE_ITEM_TITLE, priceItem.getItemNum(), "来自议价单");
                    }
                }
            }
        });
    }

    /**
     * 驳回
     * @author chenkw
     * @param purOutsourceQuoteBargainItem 需要明细sid和操作后的状态
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int rejected(PurOutsourceQuoteBargainItem purOutsourceQuoteBargainItem) {
        int row = 0;
        if (purOutsourceQuoteBargainItem.getOutsourceQuoteBargainItemSid() != null){
            PurOutsourceQuoteBargainItem one = itemMapper.selectPurOutsourceRequestQuotationItemByItemId(purOutsourceQuoteBargainItem.getOutsourceQuoteBargainItemSid());
            //明细驳回到建单时的状态（驳回到提交人）
            LambdaUpdateWrapper<PurOutsourceQuoteBargainItem> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(PurOutsourceQuoteBargainItem::getOutsourceQuoteBargainItemSid,one.getOutsourceQuoteBargainItemSid());
            //主表处理状态
            LambdaUpdateWrapper<PurOutsourceQuoteBargain> updateWrapper2 = new LambdaUpdateWrapper<>();
            updateWrapper2.eq(PurOutsourceQuoteBargain::getOutsourceQuoteBargainSid,one.getOutsourceQuoteBargainSid());
            //驳回到提交人
            if (ConstantsTask.TASK_NAME_SUBMIT.equals(purOutsourceQuoteBargainItem.getApprovalNode())){
                //明细表
                updateWrapper.set(PurOutsourceQuoteBargainItem::getCurrentStage,one.getCreatedStage())
                        .set(PurOutsourceQuoteBargainItem::getHandleStatus,HandleStatus.RETURNED.getCode());
                row = itemMapper.update(null, updateWrapper);
                //主表
                updateWrapper2.set(PurOutsourceQuoteBargain::getHandleStatus,HandleStatus.RETURNED.getCode());
                purOutsourceQuoteBargainMapper.update(null, updateWrapper2);
            }
            //明细驳回到核价录入的状态
            else if (ConstantsTask.TASK_NAME_HJLR.equals(purOutsourceQuoteBargainItem.getApprovalNode())){
                //明细表
                updateWrapper.set(PurOutsourceQuoteBargainItem::getCurrentStage,ConstantsPrice.BAOHEYI_STAGE_HJ);
                row = itemMapper.update(null, updateWrapper);
            }
            MongodbUtil.insertUserLogItem(one.getOutsourceQuoteBargainSid(), BusinessType.APPROVAL.getValue(),TITLE, one.getItemNum(),purOutsourceQuoteBargainItem.getComment());
        }
        return row;
    }

    /**
     * 提交报价单到核价
     * @chenkw
     * @param outsourceQuoteBargainSids 需要的报议价单主(报价/核价/议价)ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int submit(PurOutsourceQuoteBargainItem purOutsourceQuoteBargainItem) {
        int row = 0;
        //单笔
        if (purOutsourceQuoteBargainItem.getOutsourceQuoteBargainItemSid() != null){
            PurOutsourceQuoteBargainItem one = itemMapper.selectPurOutsourceRequestQuotationItemByItemId(purOutsourceQuoteBargainItem.getOutsourceQuoteBargainItemSid());
            //校验价格是否正确填写
            this.checkPrice(one);
            //处理明细
            LambdaUpdateWrapper<PurOutsourceQuoteBargainItem> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(PurOutsourceQuoteBargainItem::getOutsourceQuoteBargainItemSid,purOutsourceQuoteBargainItem.getOutsourceQuoteBargainItemSid());
            //处理主表
            LambdaUpdateWrapper<PurOutsourceQuoteBargain> updateWrapper2 = new LambdaUpdateWrapper<>();
            updateWrapper2.eq(PurOutsourceQuoteBargain::getOutsourceQuoteBargainSid,one.getOutsourceQuoteBargainSid());
            //如果是审批中进来的就不会传处理状态就不用改变处理状态
            if (StrUtil.isNotBlank(purOutsourceQuoteBargainItem.getHandleStatus())){
                updateWrapper.set(PurOutsourceQuoteBargainItem::getHandleStatus,purOutsourceQuoteBargainItem.getHandleStatus());
                updateWrapper2.set(PurOutsourceQuoteBargain::getHandleStatus,purOutsourceQuoteBargainItem.getHandleStatus());
                if (ConstantsEms.CHECK_STATUS.equals(purOutsourceQuoteBargainItem.getHandleStatus())){
                    //主表的确认日期
                    updateWrapper2.set(PurOutsourceQuoteBargain::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
                    updateWrapper2.set(PurOutsourceQuoteBargain::getConfirmDate, new Date());
                }
                purOutsourceQuoteBargainMapper.update(null, updateWrapper2);
            }
            //如果是报价
            if (ConstantsPrice.BAOHEYI_STAGE_BJ.equals(one.getCurrentStage())){
                //改为核价状态
                updateWrapper.set(PurOutsourceQuoteBargainItem::getCurrentStage, ConstantsPrice.BAOHEYI_STAGE_HJ);
                //得到报价不含税金额
                BigDecimal tax = one.getQuotePriceTax().divide(BigDecimal.ONE.add(one.getTaxRate()==null?BigDecimal.ONE:one.getTaxRate()),6,BigDecimal.ROUND_HALF_UP);
                updateWrapper.set(PurOutsourceQuoteBargainItem::getQuotePrice, tax);
                row = itemMapper.update(null, updateWrapper);
                //删除询价待报价的待办消息(查询对应询价单下的所有报价明细是否还有待报价)
                if (one.getOutsourceInquirySid() != null){
                    List<PurOutsourceQuoteBargainItem> inquiryQuote = itemMapper.selectPurOutsourceRequestQuotationItemList(new PurOutsourceQuoteBargainItem()
                                    .setOutsourceInquirySid(one.getOutsourceInquirySid())
                                    .setCurrentStage(ConstantsPrice.BAOHEYI_STAGE_BJ)
                                    .setVendorSid(one.getVendorSid()));
                    //如果已经没有待报价的询价单就删除待报价的通知
                    if (CollectionUtil.isEmpty(inquiryQuote)){
                        //只删除对应供应商下的所有用户关于这条询价单的待办
                        List<SysUser> userList = sysUserMapper.selectList(new QueryWrapper<SysUser>().lambda().eq(SysUser::getVendorSid,one.getVendorSid()));
                        if (CollectionUtil.isNotEmpty(userList)){
                            List<Long> userIdList = userList.stream().map(SysUser::getUserId).collect(Collectors.toList());
                            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                                    .eq(SysTodoTask::getDocumentSid,one.getOutsourceInquirySid())
                                    .eq(SysTodoTask::getTaskCategory,ConstantsEms.TODO_TASK_DB)
                                    .in(SysTodoTask::getUserId,userIdList));
                        }
                    }
                }
                //删除报价的待办消息
                sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().eq(SysTodoTask::getDocumentItemSid,one.getOutsourceQuoteBargainItemSid())
                        .eq(SysTodoTask::getTaskCategory,ConstantsEms.TODO_TASK_DB));
                MongodbUtil.insertUserLogItem(one.getOutsourceQuoteBargainSid(), BusinessType.SUBMIT.getValue(),TITLE, one.getItemNum(),"报价提交");
                //价格记录表
                insertPriceInfo(one);
            }
            //如果是核价
            else if (ConstantsPrice.BAOHEYI_STAGE_HJ.equals(one.getCurrentStage())){
                //改为议价状态
                updateWrapper.set(PurOutsourceQuoteBargainItem::getCurrentStage, ConstantsPrice.BAOHEYI_STAGE_YJ);
                //得到核价不含税金额
                BigDecimal tax = one.getCheckPriceTax().divide(BigDecimal.ONE.add(one.getTaxRate()==null?BigDecimal.ONE:one.getTaxRate()),6,BigDecimal.ROUND_HALF_UP);
                updateWrapper.set(PurOutsourceQuoteBargainItem::getCheckPrice, tax);
                row = itemMapper.update(null, updateWrapper);
                MongodbUtil.insertUserLogItem(one.getOutsourceQuoteBargainSid(), BusinessType.NEXT.getValue(),TITLE, one.getItemNum(),purOutsourceQuoteBargainItem.getComment());
                //价格记录表
                insertPriceInfo(one);
            }
            //如果是议价
            else if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(one.getCurrentStage())){
                //如果是最后一级审批则更改处理状态为审批通过（审批流程传过来的handleStatus）
                if (ConstantsEms.CHECK_STATUS.equals(purOutsourceQuoteBargainItem.getHandleStatus())){
                    //得到议价不含税金额
                    BigDecimal tax = one.getPurchasePriceTax().divide(BigDecimal.ONE.add(one.getTaxRate()==null?BigDecimal.ONE:one.getTaxRate()),6,BigDecimal.ROUND_HALF_UP);
                    updateWrapper.set(PurOutsourceQuoteBargainItem::getPurchasePrice, tax);
                    updateWrapper.set(PurOutsourceQuoteBargainItem::getConfirmPrice, tax);
                    //写入审批通过确认人和确认日期
                    updateWrapper.set(PurOutsourceQuoteBargainItem::getHandleStatus, ConstantsEms.CHECK_STATUS);
                    updateWrapper.set(PurOutsourceQuoteBargainItem::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
                    updateWrapper.set(PurOutsourceQuoteBargainItem::getConfirmDate, new Date());
                    row = itemMapper.update(null, updateWrapper);
                    //回写采购价记录表
                    PurOutsourceQuoteBargain bargain = this.selectPurOutsourceRequestQuotationById(one.getOutsourceQuoteBargainSid());
                    insertOutsourcePurchasePrice(bargain);
                    MongodbUtil.insertUserLogItem(one.getOutsourceQuoteBargainSid(), BusinessType.APPROVAL.getValue(),TITLE, one.getItemNum(),purOutsourceQuoteBargainItem.getComment());
                    //价格记录表
                    insertPriceInfo(one);
                }else if (ConstantsEms.SUBMIT_STATUS.equals(purOutsourceQuoteBargainItem.getHandleStatus())){
                    //删除报价的待办消息
                    sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().eq(SysTodoTask::getDocumentItemSid,one.getOutsourceQuoteBargainItemSid())
                            .eq(SysTodoTask::getTaskCategory,ConstantsEms.TODO_TASK_DB));
                    updateWrapper.set(PurOutsourceQuoteBargainItem::getHandleStatus, ConstantsEms.SUBMIT_STATUS);
                    row = itemMapper.update(null, updateWrapper);
                    MongodbUtil.insertUserLogItem(one.getOutsourceQuoteBargainSid(), BusinessType.SUBMIT.getValue(),TITLE, one.getItemNum(),"议价提交");
                    //价格记录表
                    insertPriceInfo(one);
                }else {
                    MongodbUtil.insertUserLogItem(one.getOutsourceQuoteBargainSid(), BusinessType.APPROVAL.getValue(),TITLE, one.getItemNum(),purOutsourceQuoteBargainItem.getComment());
                }
            }
            else {
                return 0;
            }
        }
        return row;
    }

    /**
     * 复制加工报价/议价
     * @author chenkw
     * @param purOutsourceQuoteBargainSid 需要主表sid
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurOutsourceQuoteBargain copy(PurOutsourceQuoteBargainItem request) {
        if (request.getOutsourceQuoteBargainItemSid() == null){
            return null;
        }
        PurOutsourceQuoteBargainItem purOutsourceQuoteBargainItem = itemMapper.selectPurOutsourceRequestQuotationItemByItemId(request.getOutsourceQuoteBargainItemSid());
        if (purOutsourceQuoteBargainItem == null || purOutsourceQuoteBargainItem.getOutsourceQuoteBargainSid() == null){
            return null;
        }
        PurOutsourceQuoteBargain purOutsourceQuoteBargain = purOutsourceQuoteBargainMapper.selectPurOutsourceRequestQuotationById(purOutsourceQuoteBargainItem.getOutsourceQuoteBargainSid());
        if (purOutsourceQuoteBargain == null){
            return null;
        }
        //复制后返回的
        PurOutsourceQuoteBargainItem responseItem = new PurOutsourceQuoteBargainItem();
        PurOutsourceQuoteBargain response = new PurOutsourceQuoteBargain();
        //报价单复制  stage由前端传回区分
        if (ConstantsPrice.BAOHEYI_STAGE_BJ.equals(request.getStage())){
            BeanCopyUtils.copyProperties(purOutsourceQuoteBargainItem, responseItem);
            responseItem.setOutsourceQuoteBargainSid(null).setOutsourceQuoteBargainItemSid(null).setOutsourceInquirySid(null)
                    .setOutsourceInquiryCode(null).setOutsourceInquiryItemSid(null).setCheckPriceTax(null).setCheckPrice(null)
                    .setConfirmPriceTax(null).setConfirmPrice(null).setPurchasePriceTax(null).setPurchasePrice(null)
                    .setQuoteUpdaterAccount(null).setQuoteUpdateDate(null)
                    .setCheckUpdaterAccount(null).setCheckUpdateDate(null).setConfirmUpdateDate(null).setConfirmUpdaterAccount(null)
                    .setPurchaseUpdaterAccount(null).setPurchaseUpdateDate(null).setRemarkRequest(null).setRemarkCheck(null)
                    .setRemarkConfirm(null).setRemarkPurchase(null).setHandleStatus(ConstantsEms.SAVA_STATUS)
                    .setCreatorAccountName(ApiThreadLocalUtil.get().getSysUser().getNickName()).setCurrentStage(ConstantsPrice.BAOHEYI_STAGE_BJ)
                    .setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(new Date())
                    .setUpdaterAccount(null).setUpdateDate(null).setConfirmerAccount(null).setConfirmDate(null);
            //主表
            BeanCopyUtils.copyProperties(purOutsourceQuoteBargain, response);
            response.setOutsourceQuoteBargainSid(null).setOutsourceQuoteBargainCode(null).setDateRequest(null).setDateCheck(null).setDateConfirm(null)
                    .setChecker(null).setPurchaseOrg(null).setRemarkRequest(null).setRemarkCheck(null).setRemarkConfirm(null)
                    .setRemarkPurchase(null).setHandleStatus(ConstantsEms.SAVA_STATUS).setCreatedStage(ConstantsPrice.BAOHEYI_STAGE_BJ)
                    .setCreatorAccountName(ApiThreadLocalUtil.get().getSysUser().getNickName())
                    .setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(new Date())
                    .setUpdaterAccount(null).setUpdateDate(null).setConfirmerAccount(null).setConfirmDate(null);
            response.setItemList(new ArrayList<PurOutsourceQuoteBargainItem>(){
                {
                    add(responseItem);
                }
            });
            return response;
        }
        //议价单复制
        if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(request.getStage())){
            BeanCopyUtils.copyProperties(purOutsourceQuoteBargainItem, responseItem);
            responseItem.setOutsourceQuoteBargainSid(null).setOutsourceQuoteBargainItemSid(null).setOutsourceInquirySid(null)
                    .setOutsourceInquiryCode(null).setOutsourceInquiryItemSid(null)
                    .setQuotePrice(null).setQuotePriceTax(null).setCheckPriceTax(null).setCheckPrice(null)
                    .setQuoteUpdaterAccount(null).setQuoteUpdateDate(null).setCurrentStage(ConstantsPrice.BAOHEYI_STAGE_YJ)
                    .setCheckUpdaterAccount(null).setCheckUpdateDate(null).setConfirmUpdateDate(null).setConfirmUpdaterAccount(null)
                    .setPurchaseUpdaterAccount(null).setPurchaseUpdateDate(null).setRemarkRequest(null).setRemarkCheck(null)
                    .setRemarkQuote(null).setHandleStatus(ConstantsEms.SAVA_STATUS)
                    .setCreatorAccountName(ApiThreadLocalUtil.get().getSysUser().getNickName())
                    .setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(new Date())
                    .setUpdaterAccount(null).setUpdateDate(null).setConfirmerAccount(null).setConfirmDate(null);
            //主表
            BeanCopyUtils.copyProperties(purOutsourceQuoteBargain, response);
            response.setOutsourceQuoteBargainSid(null).setOutsourceQuoteBargainCode(null).setDateRequest(null).setDateCheck(null).setDateQuote(null)
                    .setChecker(null).setRemarkRequest(null).setRemarkCheck(null).setRemarkQuote(null)
                    .setHandleStatus(ConstantsEms.SAVA_STATUS).setCreatedStage(ConstantsPrice.BAOHEYI_STAGE_YJ)
                    .setCreatorAccountName(ApiThreadLocalUtil.get().getSysUser().getNickName())
                    .setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(new Date())
                    .setUpdaterAccount(null).setUpdateDate(null).setConfirmerAccount(null).setConfirmDate(null);
            response.setItemList(new ArrayList<PurOutsourceQuoteBargainItem>(){
                {
                    add(responseItem);
                }
            });
            return response;
        }
        return response;
    }

    /**
     * 写入价格记录表
     * 提交流转审批通过
     * @param purOutsourceQuoteBargainItem 需要的报议价单明细(报价/核价/议价)
     * @return 结果
     */
    public void insertPriceInfo(PurOutsourceQuoteBargainItem purOutsourceQuoteBargainItem){
        PurOutsourceQuoteBargain quoteBargain = purOutsourceQuoteBargainMapper.selectById(purOutsourceQuoteBargainItem.getOutsourceQuoteBargainSid());
        //价格记录信息主表
        PurOutsourcePriceInfor priceInfor = new PurOutsourcePriceInfor();
        BeanCopyUtils.copyProperties(purOutsourceQuoteBargainItem,priceInfor);
        priceInfor.setVendorSid(quoteBargain.getVendorSid()).setMaterialCategory(quoteBargain.getMaterialCategory())
                .setCompanySid(quoteBargain.getCompanySid()).setPurchaseOrg(quoteBargain.getPurchaseOrg());
        //价格记录信息明细表
        PurOutsourcePriceInforItem priceInforItem = new PurOutsourcePriceInforItem();
        BeanCopyUtils.copyProperties(purOutsourceQuoteBargainItem,priceInforItem);
        purOutsourcePriceInforService.updateAllPriceInfor(priceInfor,priceInforItem);
    }

    /**
     * 校验是否存在相同的加工项
     * @param purOutsourceQuoteBargain 需要的报议价单主(报价/核价/议价)
     * @return 结果
     */
    public void judgeNull(PurOutsourceQuoteBargain purOutsourceQuoteBargain){
        List<PurOutsourceQuoteBargainItem> itemList = purOutsourceQuoteBargain.getItemList();
        if(CollectionUtil.isNotEmpty(itemList)){
            HashSet<String> comList = new HashSet<>();
            itemList.forEach(li->{
                if (ConstantsPrice.BAOHEYI_STAGE_BJ.equals(li.getCurrentStage())){
                    if(li.getQuotePriceTax()==null||li.getProcessSid()==null){
                        throw new CustomException("明细行存在报价/加工项未填写");
                    }
                }else if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(li.getCurrentStage())){
                    if(li.getPurchasePriceTax()==null||li.getProcessSid()==null){
                        throw new CustomException("明细行存在采购价/加工项未填写");
                    }
                }
                int old = comList.size();
                String codeSample = "";
                if (li.getMaterialCode() == null){
                    codeSample = li.getSampleCodeSelf();
                }else {
                    codeSample = li.getMaterialCode();
                }
                if(ConstantsEms.PRICE_K.equals(li.getPriceDimension())){
                    comList.add(codeSample+""+li.getProcessSid());
                }else{
                    comList.add(codeSample+""+li.getProcessSid()+""+li.getSku1Sid());
                }
                int now = comList.size();
                if(old==now){
                    if(ConstantsEms.PRICE_K.equals(li.getPriceDimension())){
                        ManProcess manProcess = manProcessMapper.selectById(li.getProcessSid());
                        throw new CustomException(codeSample+li.getMaterialName()+",存在相同的加工项"+manProcess.getProcessName()+"，请检查");
                    }else{
                        ManProcess manProcess = manProcessMapper.selectById(li.getProcessSid());
                        throw new CustomException(codeSample+li.getMaterialName()+","+li.getSku1Name()+",存在相同的加工项"+manProcess.getProcessName()+"，请检查");
                    }
                }
            });
        }else{
            throw new CustomException("明细行不允许为空");
        }
    }

    /**
     * 更新附件
     */
    private void insertAttachment(Long outsourceRequestQuotationSid,List<PurOutsourceQuoteBargainAttach> attachmentList){
        //删除旧数据
        attachmentMapper.delete(new QueryWrapper<PurOutsourceQuoteBargainAttach>().lambda()
                .eq(PurOutsourceQuoteBargainAttach::getOutsourceQuoteBargainSid, outsourceRequestQuotationSid));
        if(CollectionUtils.isNotEmpty(attachmentList)){
            //插入新数据
            if (CollectionUtil.isNotEmpty(attachmentList)){
                attachmentList.forEach(attachment -> {
                    attachment.setOutsourceQuoteBargainSid(outsourceRequestQuotationSid);
                    attachmentMapper.insert(attachment);
                });
            }
        }
    }

    /**
     * 更新明细
     */
    private void insertItem(PurOutsourceQuoteBargain purOutsourceQuoteBargain){
        itemMapper.delete(new QueryWrapper<PurOutsourceQuoteBargainItem>().lambda()
                .eq(PurOutsourceQuoteBargainItem::getOutsourceQuoteBargainSid, purOutsourceQuoteBargain.getOutsourceQuoteBargainSid()));
        if(CollectionUtils.isNotEmpty(purOutsourceQuoteBargain.getItemList())){
            //插入新数据
            if (CollectionUtil.isNotEmpty(purOutsourceQuoteBargain.getItemList())){
                purOutsourceQuoteBargain.getItemList().forEach(item -> {
                    item.setItemNum(1);
                    setUpdateDate(item);
                    item.setOutsourceQuoteBargainSid(purOutsourceQuoteBargain.getOutsourceQuoteBargainSid());
                    itemMapper.insert(item);
                });
            }
        }
    }

    /**
     * 加工采购议价 导入
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importDataPur(MultipartFile file){
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
            Map<String, BigDecimal> taxRateMaps = conTaxRateMapper.getConTaxRateList().stream().collect(Collectors.toMap(ConTaxRate::getTaxRateName, ConTaxRate::getTaxRateValue, (key1, key2) -> key2));
            //基本计量单位
            List<ConMeasureUnit> conMeasureUnitList = conMeasureUnitMapper.getConMeasureUnitList();
            Map<String,String> measureUnitMaps=conMeasureUnitList.stream().collect(Collectors.toMap(ConMeasureUnit::getName, ConMeasureUnit::getCode,(key1, key2)->key2));
            //取整方式
            List<DictData> roundingType=sysDictDataService.selectDictData("s_rounding_type");
            Map<String,String> roundingTypeMaps=roundingType.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            //是否
            List<DictData> yes=sysDictDataService.selectDictData("sys_yes_no");
            Map<String,String> yesMaps=yes.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue,(key1, key2)->key2));
            PurOutsourceQuoteBargain purOutsourceQuoteBargain = new PurOutsourceQuoteBargain();
            List<PurOutsourceQuoteBargainItem> purOutsourceQuoteBargainItemList = new ArrayList<>();
            for (int i = 0; i < readAll.size(); i++) {
                Long companySid=null;
                String vendorSid=null;
                Long materialSid=null;
                Long manProcessSid=null;
                Long sku1Sid=null;
                Long productSeasonSid=null;
                String materialCode=null;
                if (i < 2 || i==3||i==4) {
                    //前两行跳过
                    continue;
                }
                if(i==2){
                    List<Object> objects = readAll.get(i);
                    copy(objects, readAll);
                    int num=i+1;
                    if (objects.get(0) == null || objects.get(0) == "") {
                        throw new BaseException("第"+num+"行,供应商简称，不能为空，导入失败");
                    }
                    String bendorCode = objects.get(0).toString();
                    BasVendor basVendor = basVendorMapper.selectOne(new QueryWrapper<BasVendor>()
                            .lambda().eq(BasVendor::getShortName, bendorCode));
                    if (basVendor == null) {
                        throw new BaseException("第"+num+"行,供应商简称为" + bendorCode + "，没有对应的供应商，导入失败");
                    } else {
                        if(!basVendor.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)||!basVendor.getStatus().equals(ConstantsEms.ENABLE_STATUS)){
                            throw new BaseException("第"+num+"行,对应的供应商必须是确认且已启用的状态，导入失败");
                        }
                        vendorSid = basVendor.getVendorSid().toString();
                    }
                    if (objects.get(1) == null || objects.get(1) == "") {
                        throw new BaseException("第"+num+"行,价格维度，不能为空，导入失败");
                    }
                    String priceDimensionValue = priceDimensionMaps.get(objects.get(1).toString());
                    if(StrUtil.isEmpty(priceDimensionValue)){
                        throw new BaseException("第"+num+"行,价格维度配置错误，导入失败,请联系管理员");
                    }
                    if(objects.get(2) == null || objects.get(2) == ""){
                        throw new BaseException("第"+num+"行,议价日期不允许为空，导入失败");
                    }
                    boolean validDate = JudgeFormat.isValidDate((objects.get(4).toString()));
                    if(!validDate){
                        throw new BaseException("第"+num+"行,议价日期,数据格式错误，导入失败");
                    }
                    if(objects.get(3) == null || objects.get(3) == ""){
                        throw new BaseException("第"+num+"行,采购员不允许为空，导入失败");
                    }
                    R<LoginUser> userInfo = remoteUserService.getUserInfo(objects.get(3).toString());
                    if(userInfo.getData()==null){
                        throw new BaseException("第"+num+"行,没有账号为"+objects.get(3).toString()+"的采购员,导入失败");
                    }
                    String status = userInfo.getData().getSysUser().getStatus();
                    if(!"0".equals(status)){
                        throw new BaseException("第"+num+"行,采购员账号必须是启用状态，导入失败");
                    }
                    if (objects.get(4) == null || objects.get(4) == "") {
                        throw new BaseException("第"+num+"行,有效期起，不能为空，导入失败");
                    }
                    if (objects.get(5) == null || objects.get(5) == "") {
                        throw new BaseException("第"+num+"行,有效期至，不能为空，导入失败");
                    }
                    boolean start = JudgeFormat.isValidDate(objects.get(4).toString());
                    boolean end = JudgeFormat.isValidDate(objects.get(5).toString());
                    if(!start){
                        throw new BaseException("第"+num+"行,有效期起，日期格式错误");
                    }
                    if(!end){
                        throw new BaseException("第"+num+"行,有效期至，日期格式错误");
                    }
                    if(DateUtils.parseDate(objects.get(4)).getTime()>DateUtils.parseDate(objects.get(5)).getTime()){
                        throw new BaseException("第"+num+"行,有效期起，不能大于有效期至，导入失败");
                    }
                    if(objects.get(6) != null && objects.get(6) != ""){
                        BasProductSeason basProductSeason = basProductSeasonMapper.selectOne(new QueryWrapper<BasProductSeason>()
                                .lambda().eq(BasProductSeason::getProductSeasonName, objects.get(6).toString()));
                        if (basProductSeason == null) {
                            throw new BaseException("第"+num+"行,产品季名称为" + objects.get(6).toString() + "，没有对应的产品季，导入失败");
                        } else {
                            if(!basProductSeason.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)||!basProductSeason.getStatus().equals(ConstantsEms.ENABLE_STATUS)){
                                throw new BaseException("第"+num+"行,对应的产品季必须是确认且已启用的状态，导入失败");
                            }
                            productSeasonSid = basProductSeason.getProductSeasonSid();
                        }

                    }
                    if (objects.get(8) !=  null && objects.get(8) != "") {
                        boolean phone = JudgeFormat.isPhone(objects.get(8).toString());
                        if(!phone){
                            throw new BaseException("第"+num+"行,采购员电话格式错误，导入失败");
                        }
                    }
                    if (objects.get(9) !=  null && objects.get(9) != "") {
                        boolean email = JudgeFormat.checkEmail(objects.get(9).toString());
                        if(!email){
                            throw new BaseException("第"+num+"行,采购员邮箱格式错误，导入失败");
                        }
                    }
                    if (objects.get(10) !=  null && objects.get(10) != "") {
                        boolean phone = JudgeFormat.isPhone(objects.get(10).toString());
                        if(!phone){
                            throw new BaseException("第"+num+"行,报价员电话格式错误，导入失败");
                        }
                    }
                    if (objects.get(11) !=  null && objects.get(11) != "") {
                        boolean email = JudgeFormat.checkEmail(objects.get(11).toString());
                        if(!email){
                            throw new BaseException("第"+num+"行,报价员邮箱格式错误，导入失败");
                        }
                    }
                    purOutsourceQuoteBargain.setVendorSid(Long.valueOf(vendorSid))
                            .setProductSeasonSid(productSeasonSid)
                            .setCreateDate(new Date())
                            .setPriceDimension(priceDimensionValue)
                            .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                            .setStartDate(DateUtils.parseDate(objects.get(4)))
                            .setEndDate(DateUtils.parseDate(objects.get(5)))
                            .setBuyerTelephone((objects.get(8)==""||objects.get(8)==null)?null:objects.get(8).toString())
                            .setBuyerEmail((objects.get(9)==""||objects.get(9)==null)?null:objects.get(9).toString())
                            .setBuyer(objects.get(3).toString())
                            .setCurrency("CNY")
                            .setCreatedStage(ConstantsPrice.BAOHEYI_STAGE_YJ)
                            .setCurrencyUnit("YUAN")
                            .setQuoterTelephone((objects.get(10)==""||objects.get(10)==null)?null:objects.get(10).toString())
                            .setQuoterEmail((objects.get(11)==""||objects.get(11)==null)?null:objects.get(11).toString())
                            .setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setDateConfirm(DateUtils.parseDate(objects.get(2)))
                            .setPurchaseGroup((objects.get(7)==""||objects.get(7)==null)?null:objects.get(7).toString())
                            .setRemarkPurchase((objects.get(12)==""||objects.get(12)==null)?null:objects.get(12).toString());
                }
                if(i!=2){
                    int num=i+1;
                    List<Object> objects = readAll.get(i);
                    copyItem(objects, readAll);
                    if (objects.get(0) == null || objects.get(0) == "") {
                        throw new BaseException("第"+num+"行,商品/物料编码，不能为空，导入失败");
                    }
                    BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                            .eq(BasMaterial::getMaterialCode, objects.get(0).toString())
                    );
                    if(basMaterial==null){
                        throw new BaseException("第"+num+"行,商品/物料编码为"+objects.get(0).toString()+"，没有对应的商品/物料，导入失败");
                    }else{
                        if(!basMaterial.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)||!basMaterial.getStatus().equals(ConstantsEms.ENABLE_STATUS)){
                            throw new BaseException("第"+num+"行,对应的商品/物料必须是确认且已启用的状态，导入失败");
                        }
                        materialSid=basMaterial.getMaterialSid();
                        materialCode=basMaterial.getMaterialCode();
                    }
                    if (objects.get(1) != null && objects.get(1) != "") {
                        if (ConstantsEms.PRICE_K.equals(purOutsourceQuoteBargain.getPriceDimension())) {
                            throw new BaseException("第" + num + "行,价格维度按款时，不允许填写颜色名称，导入失败");
                        }
                        BasSku basSku = basSkuMapper.selectOne(new QueryWrapper<BasSku>().lambda()
                                .eq(BasSku::getSkuName, objects.get(1).toString()));
                        if (basSku == null) {
                            throw new BaseException("第" + num + "行,颜色名称为" + objects.get(1).toString() + "，没有对应的颜色，导入失败");
                        } else {
                            sku1Sid = basSku.getSkuSid();
                            List<BasMaterialSku> basMaterialSkus = basMaterialSkuMapper.selectList(new QueryWrapper<BasMaterialSku>().lambda()
                                    .eq(BasMaterialSku::getMaterialSid, materialSid)
                                    .eq(BasMaterialSku::getSkuSid, sku1Sid)
                            );
                            if (CollectionUtils.isEmpty(basMaterialSkus)) {
                                throw new BaseException("第" + num + "行,该商品/物料没有对应的颜色，导入失败");
                            }
                            if (!ConstantsEms.SAVA_STATUS.equals(basMaterialSkus.get(0).getSkuStatus())) {
                                throw new BaseException("第" + num + "行,该颜色名称必须启用状态，导入失败");
                            }
                        }
                    }
                    if (ConstantsEms.PRICE_K1.equals(purOutsourceQuoteBargain.getPriceDimension())) {
                        if (sku1Sid == null) {
                            throw new BaseException("第" + num + "行,价格维度按色时,颜色为必填，导入失败");
                        }
                    }
                    if (objects.get(2) == null || objects.get(2) == "") {
                        throw new BaseException("第"+num+"行,加工项名称，不能为空，导入失败");
                    }
                    ManProcess manProcess = manProcessMapper.selectOne(new QueryWrapper<ManProcess>().lambda()
                            .eq(ManProcess::getProcessName, objects.get(2).toString()));
                    if(manProcess==null){
                        throw new BaseException("第"+num+"行,加工项名称为"+objects.get(2).toString()+"，没有对应的加工项，导入失败");
                    }else{
                        if(!manProcess.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)||!manProcess.getStatus().equals(ConstantsEms.ENABLE_STATUS)){
                            throw new BaseException("第"+num+"行,对应的加工项必须是确认且已启用的状态，导入失败");
                        }
                        manProcessSid=manProcess.getProcessSid();
                    }
                    if (objects.get(3) == null || objects.get(3) == "") {
                        throw new BaseException("第"+num+"行,采购价（含税），不能为空，导入失败");
                    }
                    boolean validDouble = JudgeFormat.isValidDouble(objects.get(3).toString());
                    if(!validDouble){
                        throw new BaseException("第"+num+"行,采购价（含税）,数据格式错误，导入失败");
                    }
                    if (objects.get(4) == null || objects.get(4) == "") {
                        throw new BaseException("第"+num+"行,税率，不能为空，导入失败");
                    }
                    BigDecimal taxRateValue = taxRateMaps.get(objects.get(4).toString());
                    if(taxRateValue==null){
                        throw new BaseException("第"+num+"行,税率配置错误，导入失败,请联系管理员");
                    }
                    if (objects.get(5) != null && objects.get(5) != "") {
                        boolean validQutor= JudgeFormat.isValidDouble(objects.get(5).toString());
                        if(!validQutor){
                            throw new BaseException("第"+num+"行,报价,数据格式错误，导入失败");
                        }
                    }
                    if (objects.get(6) != null && objects.get(6) != "") {
                        boolean validQutor= JudgeFormat.isValidDouble(objects.get(6).toString());
                        if(!validQutor){
                            throw new BaseException("第"+num+"行,核定价(含税),数据格式错误，导入失败");
                        }
                    }
                    if (objects.get(7) != null && objects.get(7) != "") {
                        boolean validQutor= JudgeFormat.isValidDouble(objects.get(7).toString());
                        if(!validQutor){
                            throw new BaseException("第"+num+"行,客方确认价(含税),数据格式错误，导入失败");
                        }
                    }
                    PurOutsourceQuoteBargainItem purOutsourceQuoteBargainItem = new PurOutsourceQuoteBargainItem();
                    BigDecimal price = BigDecimal.valueOf(Double.valueOf(objects.get(3).toString()));
                    BigDecimal priceTax = price.divide(BigDecimal.ONE, 3, BigDecimal.ROUND_HALF_UP);
                    purOutsourceQuoteBargainItem
                            .setPurchasePriceTax(priceTax)
                            .setMaterialSid(materialSid)
                            .setUnitBase(basMaterial.getUnitBase())
                            .setPriceEnterMode("HS")
                            .setProcessSid(manProcessSid)
                            .setSku1Sid(sku1Sid)
                            .setPriceDimension(purOutsourceQuoteBargain.getPriceDimension())
                            .setUnitPrice(basMaterial.getUnitPrice())
                            .setTaxRate(taxRateMaps.get((objects.get(4)==""||objects.get(4)==null)?null:objects.get(4).toString()))
                            .setQuotePriceTax((objects.get(5)==""||objects.get(5)==null)?null:BigDecimal.valueOf(Double.valueOf(objects.get(5).toString())))
                            .setCheckPriceTax((objects.get(6)==""||objects.get(6)==null)?null:BigDecimal.valueOf(Double.valueOf(objects.get(6).toString())))
                            .setCustomerPriceTax((objects.get(7)==""||objects.get(7)==null)?null:BigDecimal.valueOf(Double.valueOf(objects.get(7).toString())));
                    purOutsourceQuoteBargainItemList.add(purOutsourceQuoteBargainItem);
                }
            }
            purOutsourceQuoteBargain.setItemList(purOutsourceQuoteBargainItemList);
            try{
                insertPurOutsourceRequestQuotation(purOutsourceQuoteBargain);
                return 1;
            }catch (Exception e){
                throw new CustomException("系统未知错误，请联系管理员");
            }
        }catch (BaseException e){
            throw new BaseException(e.getDefaultMessage());
        }
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

    //填充-明细表
    public void copyItem(List<Object> objects,List<List<Object>> readAll){
        //获取第三行的列数
        int size = readAll.get(3).size();
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
