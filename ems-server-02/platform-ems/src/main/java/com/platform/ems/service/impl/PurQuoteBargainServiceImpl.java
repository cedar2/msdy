package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.api.service.RemoteUserService;
import com.platform.common.core.domain.R;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.core.domain.model.LoginUser;
import com.platform.common.exception.CustomException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.DateUtils;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.ems.constant.*;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.PurQuoteBargainRequest;
import com.platform.ems.domain.dto.response.PurQuoteBargainReportResponse;
import com.platform.ems.domain.dto.response.PurQuoteBargainResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConMeasureUnit;
import com.platform.ems.plug.domain.ConPurchaseGroup;
import com.platform.ems.plug.domain.ConTaxRate;
import com.platform.ems.plug.mapper.ConMeasureUnitMapper;
import com.platform.ems.plug.mapper.ConPurchaseGroupMapper;
import com.platform.ems.plug.mapper.ConTaxRateMapper;
import com.platform.ems.service.IPurPriceInforService;
import com.platform.ems.service.IPurQuoteBargainItemService;
import com.platform.ems.service.IPurQuoteBargainService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.system.mapper.SystemUserMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 报议价单主(报价/核价/议价)Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-26
 */
@Service
@SuppressWarnings("all")
public class PurQuoteBargainServiceImpl extends ServiceImpl<PurQuoteBargainMapper, PurQuoteBargain> implements IPurQuoteBargainService {
    @Autowired
    private PurQuoteBargainMapper purQuoteBargainMapper;
    @Autowired
    private PurQuoteBargainItemMapper purQuoteBargainItemMapper;
    @Autowired
    private PurQuoteBargainAttachMapper purQuoteBargainAttachMapper;
    @Autowired
    private IPurQuoteBargainItemService purQuoteBargainItemService;
    @Autowired
    private IPurPriceInforService purPriceInforService;
    @Autowired
    private PurPriceInforMapper purPriceInforMapper;
    @Autowired
    private PurPriceInforItemMapper purPriceInforItemMapper;
    @Autowired
    private PurPurchasePriceMapper purPurchasePriceMapper;
    @Autowired
    private PurPurchasePriceItemMapper purPurchasePriceItemMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private BasMaterialSkuMapper basMaterialSkuMapper;
    @Autowired
    private PurPurchasePriceServiceImpl purPurchasePriceImpl;
    @Autowired
    private RemoteUserService remoteUserService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private SystemUserMapper sysUserMapper;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private BasSkuMapper basSkuMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private ConTaxRateMapper conTaxRateMapper;
    @Autowired
    private ConMeasureUnitMapper conMeasureUnitMapper;
    @Autowired
    private BasProductSeasonMapper basProductSeasonMapper;
    @Autowired
    private ConPurchaseGroupMapper conPurchaseGroupMapper;

    private static final String TITLE = "采购报价/核价/议价";

    private static final String PUR_PRICE_TITLE = "采购价";

    private static final String PUR_PRICE_ITEM_TITLE = "采购价";

    /**
     * 查询报议价单主(报价/核价/议价)
     *
     * @param quoteBargainSid 报议价单主(报价/核价/议价)ID
     * @return 报议价单主(询价 / 报价 / 核价 / 议价)
     */
    @Override
    public PurQuoteBargain selectPurRequestQuotationById(Long quoteBargainSid) {
        //获取主表
        PurQuoteBargain purQuoteBargain = purQuoteBargainMapper.selectPurRequestQuotationById(quoteBargainSid);
        //获取明细表
        List<PurQuoteBargainItem> purQuoteBargainItems = purQuoteBargainItemMapper.selectPurRequestQuotationItemById(quoteBargainSid);
        purQuoteBargain.setPurRequestQuotationItemList(purQuoteBargainItems);
        //获取附件表
        List<PurQuoteBargainAttach> purQuoteBargainAttaches = purQuoteBargainAttachMapper.selectList(new QueryWrapper<PurQuoteBargainAttach>()
                .lambda().eq(PurQuoteBargainAttach::getQuoteBargainSid,quoteBargainSid));
        purQuoteBargain.setPurRequestQuotationAttachmentList(purQuoteBargainAttaches);
        MongodbUtil.find(purQuoteBargain);
        return purQuoteBargain;
    }

    /**
     * 查询报议价单主(报价/核价/议价)列表
     *
     * @param purQuoteBargain 报议价单主(报价/核价/议价)
     * @return 报议价单主(询价 / 报价 / 核价 / 议价)
     */
    @Override
    public List<PurQuoteBargain> selectPurRequestQuotationList(PurQuoteBargain purQuoteBargain) {
        List<PurQuoteBargain> quotations = purQuoteBargainMapper.selectPurRequestQuotationList(purQuoteBargain);
        return quotations;
    }

    /**
     * 采购报核议价报表  -----查询页面
     *
     * @param purQuoteBargainReportResponse 报议价单主(报价/核价/议价)
     * @return 结果
     */
    @Override
    public List<PurQuoteBargainReportResponse> report(PurQuoteBargainReportResponse purQuoteBargainReportResponse) {
        //报价查询页面，查询到建单来源是”询价“”报价“且当前所属阶段是”报价“。或者建单来源是“报价”且当前所属阶段是”报价“”核价“”议价“
        if (ConstantsPrice.BAOHEYI_STAGE_BJ.equals(purQuoteBargainReportResponse.getStage())){
            purQuoteBargainReportResponse.setStageList(new String[]{purQuoteBargainReportResponse.getStage()});
            purQuoteBargainReportResponse.setCreatedStageList(new String[]{ConstantsPrice.BAOHEYI_STAGE_XJ,ConstantsPrice.BAOHEYI_STAGE_BJ});
            purQuoteBargainReportResponse.setCurrentStageList(new String[]{ConstantsPrice.BAOHEYI_STAGE_BJ,ConstantsPrice.BAOHEYI_STAGE_HJ,ConstantsPrice.BAOHEYI_STAGE_YJ});
        }
        //核价查询页面，查询到建单来源是”询价“”报价“”核价“且当前所属阶段是”核价“。或者建单来源是”核价“且当前所属阶段是”核价“”议价“
        if (ConstantsPrice.BAOHEYI_STAGE_HJ.equals(purQuoteBargainReportResponse.getStage())){
            purQuoteBargainReportResponse.setStageList(new String[]{purQuoteBargainReportResponse.getStage(),ConstantsPrice.BAOHEYI_STAGE_YJ});
            purQuoteBargainReportResponse.setCreatedStageList(new String[]{ConstantsPrice.BAOHEYI_STAGE_XJ,ConstantsPrice.BAOHEYI_STAGE_BJ,ConstantsPrice.BAOHEYI_STAGE_HJ});
            purQuoteBargainReportResponse.setCurrentStageList(new String[]{ConstantsPrice.BAOHEYI_STAGE_HJ,ConstantsPrice.BAOHEYI_STAGE_YJ});
        }
        //议价查询页面，查询到建单来源是”询价“”报价“”核价“”议价“且当前所属阶段是”议价“。
        if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(purQuoteBargainReportResponse.getStage())){
            purQuoteBargainReportResponse.setStageList(new String[]{purQuoteBargainReportResponse.getStage()});
            purQuoteBargainReportResponse.setCreatedStageList(new String[]{ConstantsPrice.BAOHEYI_STAGE_XJ,ConstantsPrice.BAOHEYI_STAGE_BJ,ConstantsPrice.BAOHEYI_STAGE_HJ,ConstantsPrice.BAOHEYI_STAGE_YJ});
            purQuoteBargainReportResponse.setCurrentStageList(new String[]{ConstantsPrice.BAOHEYI_STAGE_YJ});
        }
        List<PurQuoteBargainReportResponse> items = purQuoteBargainItemMapper.report(purQuoteBargainReportResponse);
        return items;
    }

    /**
     * 采购报核议价报表  -----明细报表页面
     *
     * @param purQuoteBargainReportResponse 报议价单主(报价/核价/议价)
     * @return 结果
     */
    @Override
    public List<PurQuoteBargainResponse> getReport(PurQuoteBargainRequest request) {
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
        List<PurQuoteBargainResponse> list = purQuoteBargainItemMapper.reportNew(request);
        return list;
    }

    /**
     * 新增报议价单主(报价/核价/议价)
     * 需要注意编码重复校验
     *
     * @param purQuoteBargain 报议价单主(报价/核价/议价)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurRequestQuotation(PurQuoteBargain request) {
        int row = 0;
        List<PurQuoteBargainAttach> purQuoteBargainAttachList = request.getPurRequestQuotationAttachmentList();
        //插入明细表
        List<PurQuoteBargainItem> purQuoteBargainItemList = request.getPurRequestQuotationItemList();
        if (CollectionUtils.isNotEmpty(purQuoteBargainItemList)) {
            purQuoteBargainItemList.forEach(o -> {
                PurQuoteBargain purQuoteBargain = new PurQuoteBargain();
                BeanCopyUtils.copyProperties(request,purQuoteBargain);
                o.setItemNum(1);
                o.setCurrentStage(purQuoteBargain.getCreatedStage());
                o.setHandleStatus(ConstantsEms.SAVA_STATUS);
                o.setCreatorAccount(null).setCreateDate(null);
                o.setUpdaterAccount(null).setUpdateDate(null);
                o.setConfirmerAccount(null).setConfirmDate(null);
                setUnit(o);
                purQuoteBargainItemService.calculatePriceTax(o);
                purQuoteBargain.setPurRequestQuotationItemList(new ArrayList<PurQuoteBargainItem>() {
                    {
                        add(o);
                    }
                });
                //插入主表
                purQuoteBargainMapper.insert(purQuoteBargain);
                Long quoteBargainSid = purQuoteBargain.getQuoteBargainSid();
                //插入附件表
                if (CollectionUtils.isNotEmpty(purQuoteBargainAttachList)) {
                    purQuoteBargainAttachList.forEach(attach -> {
                        attach.setQuoteBargainAttachSid(null);
                        attach.setQuoteBargainSid(quoteBargainSid);
                        purQuoteBargainAttachMapper.insert(attach);
                    });
                }
                //插入明细表
                o.setQuoteBargainSid(quoteBargainSid);
                if (ConstantsPrice.BAOHEYI_STAGE_BJ.equals(o.getCurrentStage())){
                    o.setQuoteUpdateDate(new Date()).setQuoteUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
                if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(o.getCurrentStage())){
                    o.setPurchaseUpdateDate(new Date()).setPurchaseUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
                purQuoteBargainItemMapper.insert(o);
                String title = "";
                String comment = "";
                if (ConstantsPrice.BAOHEYI_STAGE_BJ.equals(request.getCreatedStage())){
                    title = "采购报价单";
                    comment = "报价新建";
                }
                if (ConstantsPrice.BAOHEYI_STAGE_HJ.equals(request.getCreatedStage())){
                    title = "采购核价单";
                    comment = "核价新建";
                }
                if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(request.getCreatedStage())){
                    title = "采购议价单";
                    comment = "议价新建";
                }
                if (purQuoteBargain.getImportHandle() == null) {
                    //插入日志
                    MongodbUtil.insertUserLogItem(o.getQuoteBargainSid(),BusinessType.INSERT.getValue(),title,o.getItemNum(),comment);
                }
                else {
                    //插入日志
                    MongodbUtil.insertUserLogItem(o.getQuoteBargainSid(), BusinessType.IMPORT.getValue(), title, o.getItemNum(),comment);
                }
                //待办通知
                PurQuoteBargain purchasePrice = purQuoteBargainMapper.selectById(purQuoteBargain.getQuoteBargainSid());
                if (CollectionUtils.isNotEmpty(purQuoteBargainItemList)){
                    for (PurQuoteBargainItem item : purQuoteBargainItemList) {
                        SysTodoTask sysTodoTask = new SysTodoTask();
                        sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                                .setTableName(ConstantsTable.TABLE_PUR_QUOTE_BARGAIN)
                                .setDocumentItemSid(item.getQuoteBargainItemSid())
                                .setDocumentSid(item.getQuoteBargainSid());
                        List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                        if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                            sysTodoTask.setTitle(title + purchasePrice.getQuoteBargainCode() + "当前是保存状态，请及时处理！")
                                    .setDocumentCode(purchasePrice.getQuoteBargainCode().toString())
                                    .setNoticeDate(new Date())
                                    .setMenuId(ConstantsWorkbench.pur_quote_bargain)
                                    .setUserId(ApiThreadLocalUtil.get().getUserid());
                            sysTodoTaskMapper.insert(sysTodoTask);
                        }
                    }
                }
            });
            row = purQuoteBargainItemList.size();
        }else {
            throw new CustomException("新建时明细行不允许为空");
        }
        return row;
    }


    //基本计量单位和采购价格单位（新建，编辑）
    public void setUnit(PurQuoteBargainItem purPurchasePriceItem) {
        purPurchasePriceItem.setConfirmPriceTax(purPurchasePriceItem.getPurchasePriceTax())
                .setIncreConfPriceTax(purPurchasePriceItem.getIncrePurPriceTax())
                .setDecreConfPriceTax(purPurchasePriceItem.getDecrePurPriceTax());
        if (purPurchasePriceItem.getUnitBase().equals(purPurchasePriceItem.getUnitPrice())) {
            purPurchasePriceItem.setUnitConversionRate(BigDecimal.ONE);
        } else {
            if (purPurchasePriceItem.getUnitConversionRate() == null) {
                throw new CustomException("采购价单位“与”基本计量单位“不一致，单位换算比例不允许为空");
            }
        }
    }

    /**
     * 行号赋值
     */
    public void setItemNum(List<PurQuoteBargainItem> list) {
        int size = list.size();
        if (size > 0) {
            for (int i = 1; i <= size; i++) {
                list.get(i - 1).setItemNum(i);
            }
        }
    }

    /**
     * 修改报议价单主(报价/核价/议价)
     *
     * @param purQuoteBargain 报议价单主(报价/核价/议价)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurRequestQuotation(PurQuoteBargain purQuoteBargain) {
        PurQuoteBargain old = purQuoteBargainMapper.selectById(purQuoteBargain.getQuoteBargainSid());
        purQuoteBargain.setUpdateDate(null).setUpdaterAccount(null);
        int row = purQuoteBargainMapper.updateById(purQuoteBargain);
        //附件
        addAttachmentList(purQuoteBargain);
        List<PurQuoteBargainItem> purQuoteBargainItemList = purQuoteBargain.getPurRequestQuotationItemList();
        if (CollectionUtils.isNotEmpty(purQuoteBargainItemList)) {
            purQuoteBargainItemList.forEach(li->{
                setUnit(li);
                purQuoteBargainItemService.calculatePriceTax(li);
            });
            List<PurQuoteBargainItem> purQuoteBargainItems = purQuoteBargainItemMapper.selectList(new QueryWrapper<PurQuoteBargainItem>().lambda()
                    .eq(PurQuoteBargainItem::getQuoteBargainSid, purQuoteBargain.getQuoteBargainSid())
            );
            List<Long> longs = purQuoteBargainItems.stream().map(li -> li.getQuoteBargainItemSid()).collect(Collectors.toList());
            List<Long> longsNow = purQuoteBargainItemList.stream().map(li -> li.getQuoteBargainItemSid()).collect(Collectors.toList());
            //两个集合取差集
            List<Long> reduce = longs.stream().filter(item -> !longsNow.contains(item)).collect(Collectors.toList());
            //删除明细
            if (CollectionUtil.isNotEmpty(reduce)) {
                List<PurQuoteBargainItem> reduceList = purQuoteBargainItemMapper.selectList(new QueryWrapper<PurQuoteBargainItem>().lambda()
                        .in(PurQuoteBargainItem::getQuoteBargainItemSid, reduce)
                );
                reduceList.forEach(li -> {
                    String title = setTitle(li.getCurrentStage());
                    //插入日志
                    MongodbUtil.insertUserLogItem(li.getQuoteBargainSid(), BusinessType.DELETE.getValue(), title, li.getItemNum());
                });
                purQuoteBargainItemMapper.deleteBatchIds(reduce);
                //删除待办
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .in(SysTodoTask::getDocumentItemSid, reduce));
            }
            //修改明细
            List<PurQuoteBargainItem> exitItem = purQuoteBargainItemList.stream().filter(li -> li.getQuoteBargainItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(exitItem)) {
                exitItem.forEach(li -> {
                    String title = setTitle(li.getCurrentStage());
                    PurQuoteBargainItem oldItem = purQuoteBargainItemMapper.selectById(li.getQuoteBargainItemSid());
                    if (ConstantsPrice.BAOHEYI_STAGE_BJ.equals(li.getCurrentStage())){
                        li.setQuoteUpdateDate(new Date()).setQuoteUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                    }
                    if (ConstantsPrice.BAOHEYI_STAGE_HJ.equals(li.getCurrentStage())){
                        li.setCheckUpdateDate(new Date()).setCheckUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                    }
                    if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(li.getCurrentStage())){
                        li.setPurchaseUpdateDate(new Date()).setPurchaseUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                    }
                    purQuoteBargainItemMapper.updateAllById(li);
                    String bussiness = ConstantsEms.CHECK_STATUS.equals(oldItem.getHandleStatus()) ? "变更" : "编辑";
                    //插入日志
                    MongodbUtil.insertUserLogItem(li.getQuoteBargainSid(), bussiness, title, li.getItemNum());
                });
            }
            //新增明细
            List<PurQuoteBargainItem> nullItem = purQuoteBargainItemList.stream().filter(li -> li.getQuoteBargainItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(nullItem)) {
                int max = purQuoteBargainItems.stream().mapToInt(li -> li.getItemNum()).max().getAsInt();
                for (int i = 0; i < nullItem.size(); i++) {
                    int maxItem = max + i + 1;
                    nullItem.get(i).setItemNum(maxItem);
                    nullItem.get(i).setQuoteBargainSid(purQuoteBargain.getQuoteBargainSid());
                    nullItem.get(i).setHandleStatus(ConstantsEms.SAVA_STATUS);
                    if (ConstantsPrice.BAOHEYI_STAGE_BJ.equals(nullItem.get(i).getCurrentStage())){
                        nullItem.get(i).setQuoteUpdateDate(new Date()).setQuoteUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                    }
                    if (ConstantsPrice.BAOHEYI_STAGE_HJ.equals(nullItem.get(i).getCurrentStage())){
                        nullItem.get(i).setCheckUpdateDate(new Date()).setCheckUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                    }
                    if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(nullItem.get(i).getCurrentStage())){
                        nullItem.get(i).setPurchaseUpdateDate(new Date()).setPurchaseUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                    }
                    purQuoteBargainItemMapper.insert(nullItem.get(i));
                }
                if (CollectionUtils.isNotEmpty(nullItem)){
                    nullItem.forEach(li -> {
                        String title = setTitle(li.getCurrentStage());
                        //插入日志
                        MongodbUtil.insertUserLogItem(li.getQuoteBargainSid(), BusinessType.INSERT.getValue(), title, li.getItemNum());
                    });
                }
            }
        }
        return row;
    }

    /**
     * 批量删除采购议价明细行
     * @author chenkw
     * @param ids 需要删除的采购价信息明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteItem(List<Long> ids) {
        List<PurQuoteBargainItem> purQuoteBargainItems = purQuoteBargainItemMapper.selectList(new QueryWrapper<PurQuoteBargainItem>().lambda()
                .in(PurQuoteBargainItem::getQuoteBargainItemSid, ids)
        );
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda().in(SysTodoTask::getDocumentItemSid, ids));
        //寻找这些明细行都挂在哪些主表下
        List<Long> quoteBargainSidList = purQuoteBargainItems.stream().distinct().map(li -> li.getQuoteBargainSid()).collect(Collectors.toList());
        //删除明细
        purQuoteBargainItemMapper.deleteBatchIds(ids);
        //插入日志
        ids.forEach(sid->{
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), null, TITLE);
        });
        if (CollectionUtil.isNotEmpty(quoteBargainSidList)){
            List<Long> sidList = new ArrayList<>();
            quoteBargainSidList.forEach(sid -> {
                List<PurQuoteBargainItem> items = purQuoteBargainItemMapper.selectList(new QueryWrapper<PurQuoteBargainItem>().lambda()
                        .eq(PurQuoteBargainItem::getQuoteBargainSid, sid)
                );
                //如果对应主表下没有明细了就一起删除主表
                if (CollectionUtil.isEmpty(items)) {
                    sidList.add(sid);
                }
            });
            //删除主表
            deletePurRequestQuotationByIds(sidList);
        }
        return 1;
    }

    /**
     * 批量删除报议价单主(报价/核价/议价)
     * @author chenkw
     * @param quoteBargainSids 需要删除的报议价单主(报价/核价/议价)ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurRequestQuotationByIds(List<Long> quoteBargainSidList) {
        if (CollectionUtil.isEmpty(quoteBargainSidList)){
            return 0;
        }
        int row = purQuoteBargainMapper.deleteBatchIds(quoteBargainSidList);
        //删除待办
        sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().in(SysTodoTask::getDocumentSid, quoteBargainSidList));
        //删除明细
        purQuoteBargainItemMapper.delete(new QueryWrapper<PurQuoteBargainItem>().lambda().in(PurQuoteBargainItem::getQuoteBargainSid, quoteBargainSidList));
        //删除附件
        purQuoteBargainAttachMapper.delete(new QueryWrapper<PurQuoteBargainAttach>().lambda().in(PurQuoteBargainAttach::getQuoteBargainSid, quoteBargainSidList));
        //插入日志
        quoteBargainSidList.forEach(sid -> {
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), null, TITLE);
        });
        return row;
    }

    /**
     * 更新附件清单
     *
     * @param purInquiry
     * @return
     */
    private int addAttachmentList(PurQuoteBargain purQuoteBargain) {
        int row = 0;
        purQuoteBargainAttachMapper.delete(new QueryWrapper<PurQuoteBargainAttach>().lambda().eq(PurQuoteBargainAttach::getQuoteBargainSid,purQuoteBargain.getQuoteBargainSid()));
        if (CollectionUtil.isNotEmpty(purQuoteBargain.getPurRequestQuotationAttachmentList())){
            purQuoteBargain.getPurRequestQuotationAttachmentList().forEach(attach -> {
                attach.setQuoteBargainSid(purQuoteBargain.getQuoteBargainSid());
                purQuoteBargainAttachMapper.insert(attach);
            });
        }
        return row;
    }

    /**
     * 检查是否已存在于采购价和采购成本核算流程中
     * @author chenkw
     * @return
     */
    @Override
    public void checkUnique(PurQuoteBargain purQuoteBargain){
        purQuoteBargain.getPurRequestQuotationItemList().forEach(item->{
            PurQuoteBargainItem request = new PurQuoteBargainItem();
            //供应商，甲供料方式，采购模式，有效期起/至，物料商品sid，价格维度，sku1sid
            request.setVendorSid(purQuoteBargain.getVendorSid()).setRawMaterialMode(purQuoteBargain.getRawMaterialMode())
                    .setPurchaseMode(purQuoteBargain.getPurchaseMode())
                    .setMaterialSid(item.getMaterialSid());
            //查询出不是已确认的单据
            request.setHandleStatusList(new String[]{HandleStatus.SUBMIT.getCode(),HandleStatus.CHANGEAPPROVAL.getCode()});
            //报核议价单
            List<PurQuoteBargainItem> bargainItemList = new ArrayList<>();
            //采购价单
            List<PurPurchasePriceItem> priceItemList = new ArrayList<>();
            //采购成本核算
            List<CosProductCost> productCostList = new ArrayList<>();
            if (ConstantsPrice.PRICE_DIMENSION.equals(item.getPriceDimension())){
                bargainItemList = purQuoteBargainItemMapper.selectPurRequestQuotationItemList(request);
                priceItemList = purQuoteBargainItemMapper.selectPriceItemList(request);
                productCostList = purQuoteBargainItemMapper.selectProductCostList(request);
            }
            else if (ConstantsPrice.PRICE_DIMENSION_SKU1.equals(item.getPriceDimension())){
                //1、查按色 sku1Sid
                request.setSku1Sid(item.getSku1Sid()).setPriceDimension(item.getPriceDimension());
                bargainItemList = purQuoteBargainItemMapper.selectPurRequestQuotationItemList(request);
                priceItemList = purQuoteBargainItemMapper.selectPriceItemList(request);
                productCostList = purQuoteBargainItemMapper.selectProductCostList(request);
                //如果（1）没查到，则接着查：2、查价格维度：按款
                request.setSku1Sid(null).setPriceDimension(ConstantsPrice.PRICE_DIMENSION);
                if (CollectionUtil.isEmpty(bargainItemList)){
                    bargainItemList = purQuoteBargainItemMapper.selectPurRequestQuotationItemList(request);
                }
                if (CollectionUtil.isEmpty(priceItemList)){
                    priceItemList = purQuoteBargainItemMapper.selectPriceItemList(request);
                }
                if (CollectionUtil.isEmpty(productCostList)){
                    productCostList = purQuoteBargainItemMapper.selectProductCostList(request);
                }
            }else {}
            String materialName = item.getMaterialName() == null ? "" : item.getMaterialName();
            if (CollectionUtil.isNotEmpty(bargainItemList)){
                //如果是编辑 的，那要去掉跟本身的单据校验冲突
                if (purQuoteBargain.getQuoteBargainSid() != null){
                    bargainItemList = bargainItemList.stream().filter(o-> !o.getQuoteBargainSid().equals(purQuoteBargain.getQuoteBargainSid())).collect(Collectors.toList());
                }
                if (CollectionUtil.isNotEmpty(bargainItemList)){
                    String stage = bargainItemList.get(0).getCurrentStage();
                    String stageName = setTitle(stage);
                    throw new CustomException(materialName + "存在相应的审批中的" + stageName + bargainItemList.get(0).getQuoteBargainCode() + "，请先处理此" + stageName);
                }

            }
            if (CollectionUtil.isNotEmpty(priceItemList)){
                throw new CustomException(materialName + "存在相应的审批中的采购价信息" + priceItemList.get(0).getPurchasePriceCode() + "，请先处理此采购价信息");
            }
            if (CollectionUtil.isNotEmpty(productCostList)){
                throw new CustomException(materialName + "存在相应的审批中的采购成本核算信息，请先处理此采购成本核算信息");
            }
        });
    }

    /**
     * 检查有效期范围
     * @author chenkw
     * @return 是否存在交集
     */
    @Override
    public void checkDateRange(PurQuoteBargain purQuoteBargain){
        purQuoteBargain.getPurRequestQuotationItemList().forEach(item->{
            //议价再去判断
            if (!ConstantsPrice.BAOHEYI_STAGE_YJ.equals(item.getCurrentStage())){
                return;
            }
            String materialName = item.getMaterialName() == null ? "" : item.getMaterialName();
            PurQuoteBargainItem request = new PurQuoteBargainItem();
            //供应商，甲供料方式，采购模式，有效期起/至，物料商品sid，价格维度，sku1sid
            request.setVendorSid(purQuoteBargain.getVendorSid()).setRawMaterialMode(purQuoteBargain.getRawMaterialMode())
                    .setPurchaseMode(purQuoteBargain.getPurchaseMode())
                    .setStartDate(purQuoteBargain.getStartDate()).setEndDate(purQuoteBargain.getEndDate())
                    .setMaterialSid(item.getMaterialSid());
            //自身报核议价单
            List<PurQuoteBargainItem> itemList = new ArrayList<>();
            //采购价单
            List<PurPurchasePriceItem> priceItemList = new ArrayList<>();
            //按款
            if (ConstantsPrice.PRICE_DIMENSION.equals(item.getPriceDimension())){
                priceItemList = purQuoteBargainItemMapper.selectPriceItemList(request);
            }
            //按色
            if (ConstantsPrice.PRICE_DIMENSION_SKU1.equals(item.getPriceDimension())){
                //1、查sku1Sid
                request.setSku1Sid(item.getSku1Sid());
                request.setPriceDimension(item.getPriceDimension());
                priceItemList = purQuoteBargainItemMapper.selectPriceItemList(request);
                //则接着查：2、查价格维度：按款
                request.setSku1Sid(null).setPriceDimension(ConstantsPrice.PRICE_DIMENSION);
                priceItemList.addAll(purQuoteBargainItemMapper.selectPriceItemList(request));

            }
            //采购价单
            String priceDimensionName = "";
            if (CollectionUtil.isNotEmpty(priceItemList) && ConstantsPrice.BAOHEYI_STAGE_YJ.equals(item.getCurrentStage())){
                //对有效期止倒叙排序，方便获得最大的有效期止
                priceItemList = priceItemList.stream().sorted(Comparator.comparing(PurPurchasePriceItem::getEndDate,Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                //标记是否符合条件，1：符合可以更新价格的条件，0：不符合，报错
                int flag = 0;
                //查询是否存在有效期起和有效期止刚好相同的单据
                for (PurPurchasePriceItem priceItem : priceItemList) {
                    if (purQuoteBargain.getStartDate().compareTo(priceItem.getStartDate()) == 0 &&
                        purQuoteBargain.getEndDate().compareTo(priceItem.getEndDate()) == 0 &&
                        item.getPriceDimension().equals(priceItem.getPriceDimension())){
                        flag = 1;
                    }else {
                        flag = 0;
                        break;
                    }
                }
                //对比同 维度的 按款或者按色
                List<PurPurchasePriceItem> priceItemList1 = priceItemList.stream().filter(o->item.getPriceDimension().equals(o.getPriceDimension())).collect(Collectors.toList());
                //另一个维度的用来获取提示编号
                List<PurPurchasePriceItem> priceItemList2 = priceItemList.stream().filter(o->!item.getPriceDimension().equals(o.getPriceDimension())).collect(Collectors.toList());
                //采购价编号
                Long code = null;
                if (CollectionUtil.isNotEmpty(priceItemList1)){
                    //当前的有效期止大于存在交集中有效期止最大的有效期止，且，（当前的有效期起大于存在交集中有效期止最大的有效期起，且，当前有效期起小于存在交集中有效期止最大的有效期止）
                    if (purQuoteBargain.getEndDate().compareTo(priceItemList1.get(0).getEndDate()) >= 0 &&
                            purQuoteBargain.getStartDate().compareTo(priceItemList1.get(0).getEndDate()) <= 0 &&
                            purQuoteBargain.getStartDate().compareTo(priceItemList1.get(0).getStartDate()) > 0 ){
                        flag = 1;
                    }else {
                        code = priceItemList1.get(0).getPurchasePriceCode();
                        if (item.getPriceDimension().equals("K")){
                            priceDimensionName = "按款的";
                        }else if (item.getPriceDimension().equals("K1")){
                            priceDimensionName = "按色(SKU1)的";
                        }
                    }
                }else {
                    code = priceItemList2.get(0).getPurchasePriceCode();
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
                    throw new CustomException(materialName + "当前存在" + priceDimensionName + "采购价"+ priceCode + "的有效期与此议价单的有效期区间\n\n存在交集，请先手工更新旧的有效期后，再进行此操作。");
                }
            }
        });
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
     * 写入价格记录表
     * 提交流转审批通过
     * @param purQuoteBargainItem 需要的报议价单明细(报价/核价/议价)
     * @return 结果
     */
    public void insertPriceInfo(PurQuoteBargainItem purQuoteBargainItem){
        PurQuoteBargain quoteBargain = purQuoteBargainMapper.selectById(purQuoteBargainItem.getQuoteBargainSid());
        //价格记录信息主表
        PurPriceInfor priceInfor = new PurPriceInfor();
        BeanCopyUtils.copyProperties(purQuoteBargainItem,priceInfor);
        priceInfor.setVendorSid(quoteBargain.getVendorSid()).setMaterialCategory(quoteBargain.getMaterialCategory())
                .setRawMaterialMode(quoteBargain.getRawMaterialMode()).setPurchaseMode(quoteBargain.getPurchaseMode())
                .setCompanySid(quoteBargain.getCompanySid()).setPurchaseOrg(quoteBargain.getPurchaseOrg()).setCostOrg(quoteBargain.getCostOrg());
        //价格记录信息明细表
        PurPriceInforItem priceInforItem = new PurPriceInforItem();
        BeanCopyUtils.copyProperties(purQuoteBargainItem,priceInforItem);
        priceInforItem.setTaxRate(purQuoteBargainItem.getTaxRate().toString());
        purPriceInforService.updateAllPriceInfor(priceInfor,priceInforItem);
    }

    /**
     * 价格回写采购价
     * @author chenkw
     * @return title
     */
    public void insertPurchasePrice(PurQuoteBargain purQuoteBargain){
        if (!ConstantsEms.CHECK_STATUS.equals(purQuoteBargain.getHandleStatus())){
            return;
        }
        purQuoteBargain.getPurRequestQuotationItemList().forEach(item->{
            if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(item.getCurrentStage())){
                //先判断是更新价格还是插入价格，就是要先处理是否存在交集，然后对交集的情况进行分类处理
                PurQuoteBargainItem request = new PurQuoteBargainItem();
                //供应商，甲供料方式，采购模式，有效期起/至，物料商品sid，价格维度，sku1sid
                request.setVendorSid(purQuoteBargain.getVendorSid()).setRawMaterialMode(purQuoteBargain.getRawMaterialMode())
                        .setPurchaseMode(purQuoteBargain.getPurchaseMode())
                        .setStartDate(purQuoteBargain.getStartDate()).setEndDate(purQuoteBargain.getEndDate())
                        .setMaterialSid(item.getMaterialSid());
                //采购价单
                List<PurPurchasePriceItem> priceItemList = new ArrayList<>();
                //按款
                if (ConstantsPrice.PRICE_DIMENSION.equals(item.getPriceDimension())){
                    priceItemList = purQuoteBargainItemMapper.selectPriceItemList(request);
                }
                //按色
                if (ConstantsPrice.PRICE_DIMENSION_SKU1.equals(item.getPriceDimension())){
                    //1、查sku1Sid
                    request.setSku1Sid(item.getSku1Sid());
                    request.setPriceDimension(item.getPriceDimension());
                    priceItemList = purQuoteBargainItemMapper.selectPriceItemList(request);
                    //如果（1）没查到，则接着查：2、查价格维度：按款
                    request.setSku1Sid(null).setPriceDimension(ConstantsPrice.PRICE_DIMENSION);
                    priceItemList.addAll(purQuoteBargainItemMapper.selectPriceItemList(request));
                }
                //用来标记是否回写采购价，1：回写，0：不回写
                int flag = 1;
                //采购价单
                if (CollectionUtil.isNotEmpty(priceItemList)){
                    flag = 0;
                    boolean check = true; //判读走哪一步，1、走了更新价格就不要再走更新日期的；2、没有走更新价格就看看是不是可以走更新日期
                    //对有效期止倒叙排序，方便获得最大的有效期止
                    priceItemList = priceItemList.stream().sorted(Comparator.comparing(PurPurchasePriceItem::getEndDate,Comparator.nullsLast(Comparator.reverseOrder()))).collect(Collectors.toList());
                    //查询是否存在有效期起和有效期止刚好相同的单据
                    for (PurPurchasePriceItem priceItem : priceItemList) {
                        if (purQuoteBargain.getStartDate().compareTo(priceItem.getStartDate()) == 0 &&
                                purQuoteBargain.getEndDate().compareTo(priceItem.getEndDate()) == 0 &&
                                item.getPriceDimension().equals(priceItem.getPriceDimension())){
                            //则 更新采购价
                            priceItem.setIsRecursionPrice(item.getIsRecursionPrice())
                                    .setUnitRecursion(item.getUnitRecursion()).setSkuTypeRecursion(item.getSkuTypeRecursion())
                                    .setReferQuantity(item.getReferQuantity()).setIncreQuantity(item.getIncreQuantity())
                                    .setDecreQuantity(item.getDecreQuantity()).setPriceMinQuantity(item.getPriceMinQuantity())
                                    .setRoundingType(item.getRoundingType()).setTaxRate(item.getTaxRate())
                                    .setIncrePurPriceTax(item.getIncrePurPriceTax()).setIncrePurPrice(item.getIncrePurPrice())
                                    .setDecPurPriceTax(item.getDecrePurPriceTax()).setDecPurPrice(item.getDecrePurPrice())
                                    .setPurchasePriceTax(item.getPurchasePriceTax()).setPurchasePrice(item.getPurchasePrice())
                                    .setUnitBase(item.getUnitBase()).setUnitPrice(item.getUnitPrice()).setUnitConversionRate(item.getUnitConversionRate());
                            purPurchasePriceItemMapper.updateById(priceItem);
                            MongodbUtil.insertUserLogItem(priceItem.getPurchasePriceSid(), BusinessType.PRICE.getValue(), PUR_PRICE_ITEM_TITLE, priceItem.getItemNum(),"来自议价单");
                            check = false;  //拒绝走更新日期了
                        }
                    }
                    if (check){
                        priceItemList = priceItemList.stream().filter(o->item.getPriceDimension().equals(o.getPriceDimension())).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(priceItemList)){
                            //当前的有效期止大于存在交集中有效期止最大的有效期止，且，（当前的有效期起大于存在交集中有效期止最大的有效期起，且，当前有效期起小于存在交集中有效期止最大的有效期止）
                            if (purQuoteBargain.getEndDate().compareTo(priceItemList.get(0).getEndDate()) >= 0 &&
                                    purQuoteBargain.getStartDate().compareTo(priceItemList.get(0).getEndDate()) <= 0 &&
                                    purQuoteBargain.getStartDate().compareTo(priceItemList.get(0).getStartDate()) > 0 ){
                                //则 将旧采购价的有效期止变更成当前有效期起的前一天，然后写入新的采购价
                                PurPurchasePriceItem priceItem = priceItemList.get(0);
                                SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(item.getStartDate());
                                calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
                                try {
                                    Date endDate = dft.parse(dft.format(calendar.getTime()));
                                    priceItem.setEndDate(endDate);
                                    purPurchasePriceItemMapper.updateById(priceItem);
                                    MongodbUtil.insertUserLogItem(priceItem.getPurchasePriceSid(), BusinessType.CHANGE.getValue(), PUR_PRICE_ITEM_TITLE, priceItemList.get(0).getItemNum() ,"更新有效期(至)");
                                    flag = 1;
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                if (flag == 1){
                    //主表,查询是否要挂进去明细 --- 挂载主表
                    QueryWrapper<PurPurchasePrice> queryWrapper = new QueryWrapper<>();
                    queryWrapper.lambda().eq(PurPurchasePrice::getVendorSid,purQuoteBargain.getVendorSid())
                            .eq(PurPurchasePrice::getMaterialSid,item.getMaterialSid())
                            .eq(PurPurchasePrice::getRawMaterialMode,purQuoteBargain.getRawMaterialMode())
                            .eq(PurPurchasePrice::getPurchaseMode,purQuoteBargain.getPurchaseMode())
                            .eq(PurPurchasePrice::getPriceDimension,item.getPriceDimension());
                    if (item.getSku1Sid() != null){
                        queryWrapper.lambda().eq(PurPurchasePrice::getSku1Sid,item.getSku1Sid());
                    }
                    List<PurPurchasePrice> priceList = purPurchasePriceMapper.selectList(queryWrapper);
                    if (CollectionUtil.isNotEmpty(priceList)){
                        priceList.forEach(o->{
                            //明细 --- 挂载主表
                            //获取主表当前明细最大行号
                            List<PurPurchasePriceItem> purchasePriceItemList = purPurchasePriceItemMapper.selectList(new QueryWrapper<PurPurchasePriceItem>()
                                    .lambda().eq(PurPurchasePriceItem::getPurchasePriceSid,o.getPurchasePriceSid()));
                            purchasePriceItemList = purchasePriceItemList.stream().sorted(Comparator.comparing(PurPurchasePriceItem::getItemNum).reversed()).collect(Collectors.toList());
                            PurPurchasePriceItem priceItem = new PurPurchasePriceItem();
                            BeanCopyUtils.copyProperties(item, priceItem);
                            priceItem.setPurchasePriceSid(o.getPurchasePriceSid());
                            priceItem.setStartDate(purQuoteBargain.getStartDate()).setEndDate(purQuoteBargain.getEndDate());
                            priceItem.setDecPurPrice(item.getDecrePurPrice()).setDecPurPriceTax(item.getDecrePurPriceTax());
                            priceItem.setRemark(item.getRemarkConfirm());
                            priceItem.setCreateDate(new Date()).setCreatorAccount(null).setItemNum(purchasePriceItemList.get(0).getItemNum()+1)
                                    .setCurrency(ConstantsFinance.CURRENCY_CNY).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN);
                            //插入
                            purPurchasePriceItemMapper.insert(priceItem);
                            MongodbUtil.insertUserLogItem(priceItem.getPurchasePriceSid(), BusinessType.CHECK.getValue(), PUR_PRICE_ITEM_TITLE, purchasePriceItemList.get(0).getItemNum()+1, "来自议价单");
                        });
                    }
                    else {
                        PurPurchasePrice price = new PurPurchasePrice();
                        price.setVendorSid(purQuoteBargain.getVendorSid())
                                .setMaterialCategory(purQuoteBargain.getMaterialCategory())
                                .setPurchaseOrg(purQuoteBargain.getPurchaseOrg())
                                .setRawMaterialMode(purQuoteBargain.getRawMaterialMode())
                                .setPurchaseMode(purQuoteBargain.getPurchaseMode());
                        price.setMaterialSid(item.getMaterialSid())
                                .setBarcodeSid(item.getBarcodeSid())
                                .setPriceDimension(item.getPriceDimension())
                                .setSku1Sid(item.getSku1Sid())
                                .setSku2Sid(item.getSku2Sid())
                                .setSkuTypeRecursion(item.getSkuTypeRecursion());
                        price.setStatus(ConstantsEms.ENABLE_STATUS).setHandleStatus(ConstantsEms.CHECK_STATUS).setRemark(purQuoteBargain.getRemarkConfirm())
                                .setConfirmerAccount(purQuoteBargain.getConfirmerAccount()).setConfirmDate(purQuoteBargain.getConfirmDate());
                        //插入
                        purPurchasePriceMapper.insert(price);
                        //明细
                        PurPurchasePriceItem priceItem = new PurPurchasePriceItem();
                        BeanCopyUtils.copyProperties(item, priceItem);
                        priceItem.setPurchasePriceSid(price.getPurchasePriceSid());
                        priceItem.setStartDate(purQuoteBargain.getStartDate()).setEndDate(purQuoteBargain.getEndDate());
                        priceItem.setDecPurPrice(item.getDecrePurPrice()).setDecPurPriceTax(item.getDecrePurPriceTax());
                        priceItem.setRemark(item.getRemarkConfirm());
                        priceItem.setCreateDate(new Date()).setCreatorAccount(null)
                                .setCurrency(ConstantsFinance.CURRENCY_CNY).setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN);
                        //插入
                        purPurchasePriceItemMapper.insert(priceItem);
                        MongodbUtil.insertUserLogItem(priceItem.getPurchasePriceSid(), BusinessType.CHECK.getValue(), PUR_PRICE_ITEM_TITLE, priceItem.getItemNum(), "来自议价单");
                    }
                }
            }
        });
    }

    /**
     * 驳回
     * @author chenkw
     * @param purQuoteBargainItem 需要明细sid和操作后的状态
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int rejected(PurQuoteBargainItem purQuoteBargainItem) {
        int row = 0;
        if (purQuoteBargainItem.getQuoteBargainItemSid() != null){
            PurQuoteBargainItem one = purQuoteBargainItemMapper.selectPurRequestQuotationItemByItemId(purQuoteBargainItem.getQuoteBargainItemSid());
            //明细驳回到建单时的状态（驳回到提交人）
            LambdaUpdateWrapper<PurQuoteBargainItem> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(PurQuoteBargainItem::getQuoteBargainItemSid,one.getQuoteBargainItemSid());
            //主表处理状态
            LambdaUpdateWrapper<PurQuoteBargain> updateWrapper2 = new LambdaUpdateWrapper<>();
            updateWrapper2.eq(PurQuoteBargain::getQuoteBargainSid,one.getQuoteBargainSid());
            //驳回到提交人
            if (ConstantsTask.TASK_NAME_SUBMIT.equals(purQuoteBargainItem.getApprovalNode())){
                //明细表
                updateWrapper.set(PurQuoteBargainItem::getCurrentStage,one.getCreatedStage())
                        .set(PurQuoteBargainItem::getHandleStatus,HandleStatus.RETURNED.getCode());
                row = purQuoteBargainItemMapper.update(null, updateWrapper);
                //主表
                updateWrapper2.set(PurQuoteBargain::getHandleStatus,HandleStatus.RETURNED.getCode());
                purQuoteBargainMapper.update(null, updateWrapper2);
            }
            //明细驳回到核价录入的状态
            else if (ConstantsTask.TASK_NAME_HJLR.equals(purQuoteBargainItem.getApprovalNode())){
                //明细表
                updateWrapper.set(PurQuoteBargainItem::getCurrentStage,ConstantsPrice.BAOHEYI_STAGE_HJ);
                row = purQuoteBargainItemMapper.update(null, updateWrapper);
            }
            MongodbUtil.insertUserLogItem(one.getQuoteBargainSid(), BusinessType.APPROVAL.getValue(),TITLE, one.getItemNum(),purQuoteBargainItem.getComment());
        }
        return row;
    }

    /**
     * 报价提交，核价流转，议价审批
     * @author chenkw
     * @param purQuoteBargainItem 需要明细sid和操作后的状态
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int submit(PurQuoteBargainItem purQuoteBargainItem) {
        int row = 0;
        //单笔
        if (purQuoteBargainItem.getQuoteBargainItemSid() != null){
            PurQuoteBargainItem one = purQuoteBargainItemMapper.selectPurRequestQuotationItemByItemId(purQuoteBargainItem.getQuoteBargainItemSid());
            //校验价格是否正确填写
            purQuoteBargainItemService.checkPrice(one);
            //处理明细
            LambdaUpdateWrapper<PurQuoteBargainItem> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(PurQuoteBargainItem::getQuoteBargainItemSid,purQuoteBargainItem.getQuoteBargainItemSid());
            //处理主表
            LambdaUpdateWrapper<PurQuoteBargain> updateWrapper2 = new LambdaUpdateWrapper<>();
            updateWrapper2.eq(PurQuoteBargain::getQuoteBargainSid,one.getQuoteBargainSid());
            //如果是审批中进来的就不会传处理状态就不用改变处理状态
            if (StrUtil.isNotBlank(purQuoteBargainItem.getHandleStatus())){
                updateWrapper.set(PurQuoteBargainItem::getHandleStatus,purQuoteBargainItem.getHandleStatus());
                updateWrapper2.set(PurQuoteBargain::getHandleStatus,purQuoteBargainItem.getHandleStatus());
                //主表的确认日期
                if (ConstantsEms.CHECK_STATUS.equals(purQuoteBargainItem.getHandleStatus())){
                    updateWrapper2.set(PurQuoteBargain::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
                    updateWrapper2.set(PurQuoteBargain::getConfirmDate, new Date());
                }
                purQuoteBargainMapper.update(null, updateWrapper2);
            }
            //如果是报价
            if (ConstantsPrice.BAOHEYI_STAGE_BJ.equals(one.getCurrentStage())){
                //改为核价状态
                updateWrapper.set(PurQuoteBargainItem::getCurrentStage, ConstantsPrice.BAOHEYI_STAGE_HJ);
                //得到报价不含税金额
                BigDecimal tax = one.getQuotePriceTax().divide(BigDecimal.ONE.add(one.getTaxRate()==null?BigDecimal.ONE:one.getTaxRate()),6,BigDecimal.ROUND_HALF_UP);
                updateWrapper.set(PurQuoteBargainItem::getQuotePrice, tax);
                row = purQuoteBargainItemMapper.update(null, updateWrapper);
                if (one.getInquirySid() != null){
                    //删除询价待报价的待办消息(查询对应供应商的询价单下的所有报价明细是否还有待报价)
                    List<PurQuoteBargainItem> inquiryQuote = purQuoteBargainItemMapper
                            .selectPurRequestQuotationItemList(new PurQuoteBargainItem()
                                    .setInquirySid(one.getInquirySid())
                                    .setCurrentStage(ConstantsPrice.BAOHEYI_STAGE_BJ)
                                    .setVendorSid(one.getVendorSid()));
                    //如果已经没有待报价的询价单就删除待报价的通知
                    if (CollectionUtil.isEmpty(inquiryQuote)){
                        //只删除对应供应商下的所有用户关于这条询价单的待办
                        List<SysUser> userList = sysUserMapper.selectList(new QueryWrapper<SysUser>().lambda().eq(SysUser::getVendorSid,one.getVendorSid()));
                        if (CollectionUtil.isNotEmpty(userList)){
                            List<Long> userIdList = userList.stream().map(SysUser::getUserId).collect(Collectors.toList());
                            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                                    .eq(SysTodoTask::getDocumentSid,one.getInquirySid())
                                    .eq(SysTodoTask::getTaskCategory,ConstantsEms.TODO_TASK_DB)
                                    .in(SysTodoTask::getUserId,userIdList));
                        }
                    }
                }
                //删除报价的待办消息
                sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().eq(SysTodoTask::getDocumentItemSid,one.getQuoteBargainItemSid())
                        .eq(SysTodoTask::getTaskCategory,ConstantsEms.TODO_TASK_DB));
                String title = setTitle(ConstantsPrice.BAOHEYI_STAGE_BJ);
                MongodbUtil.insertUserLogItem(one.getQuoteBargainSid(), BusinessType.SUBMIT.getValue(),title, one.getItemNum(),"报价提交");
                //价格记录表
                insertPriceInfo(one);
            }
            //如果是核价
            else if (ConstantsPrice.BAOHEYI_STAGE_HJ.equals(one.getCurrentStage())){
                //改为议价状态
                updateWrapper.set(PurQuoteBargainItem::getCurrentStage, ConstantsPrice.BAOHEYI_STAGE_YJ);
                //得到核价不含税金额
                BigDecimal tax = one.getCheckPriceTax().divide(BigDecimal.ONE.add(one.getTaxRate()==null?BigDecimal.ONE:one.getTaxRate()),6,BigDecimal.ROUND_HALF_UP);
                updateWrapper.set(PurQuoteBargainItem::getCheckPrice, tax);
                row = purQuoteBargainItemMapper.update(null, updateWrapper);
                String title = setTitle(ConstantsPrice.BAOHEYI_STAGE_HJ);
                MongodbUtil.insertUserLogItem(one.getQuoteBargainSid(), BusinessType.NEXT.getValue(),title, one.getItemNum(),purQuoteBargainItem.getComment());
                //价格记录表
                insertPriceInfo(one);
            }
            //如果是议价
            else if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(one.getCurrentStage())){
                String title = setTitle(ConstantsPrice.BAOHEYI_STAGE_YJ);
                //如果是最后一级审批则更改处理状态为审批通过（审批流程传过来的handleStatus）
                if (ConstantsEms.CHECK_STATUS.equals(purQuoteBargainItem.getHandleStatus())){
                    //得到议价不含税金额
                    BigDecimal tax = one.getPurchasePriceTax().divide(BigDecimal.ONE.add(one.getTaxRate()==null?BigDecimal.ONE:one.getTaxRate()),6,BigDecimal.ROUND_HALF_UP);
                    updateWrapper.set(PurQuoteBargainItem::getPurchasePrice, tax);
                    updateWrapper.set(PurQuoteBargainItem::getConfirmPrice, tax);
                    //写入审批通过确认人和确认日期
                    updateWrapper.set(PurQuoteBargainItem::getHandleStatus, ConstantsEms.CHECK_STATUS);
                    updateWrapper.set(PurQuoteBargainItem::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
                    updateWrapper.set(PurQuoteBargainItem::getConfirmDate, new Date());
                    row = purQuoteBargainItemMapper.update(null, updateWrapper);
                    //回写采购价记录表
                    PurQuoteBargain bargain = this.selectPurRequestQuotationById(one.getQuoteBargainSid());
                    //订单价格回写
                    updateOrder(one,bargain);
                    insertPurchasePrice(bargain);
                    MongodbUtil.insertUserLogItem(one.getQuoteBargainSid(), BusinessType.APPROVAL.getValue(),title, one.getItemNum(),purQuoteBargainItem.getComment());
                    //价格记录表
                    insertPriceInfo(one);
                }else if (ConstantsEms.SUBMIT_STATUS.equals(purQuoteBargainItem.getHandleStatus())){
                    //删除议价的待办消息
                    sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda().eq(SysTodoTask::getDocumentItemSid,one.getQuoteBargainItemSid())
                            .eq(SysTodoTask::getTaskCategory,ConstantsEms.TODO_TASK_DB));
                    updateWrapper.set(PurQuoteBargainItem::getHandleStatus, ConstantsEms.SUBMIT_STATUS);
                    row = purQuoteBargainItemMapper.update(null, updateWrapper);
                    MongodbUtil.insertUserLogItem(one.getQuoteBargainSid(), BusinessType.SUBMIT.getValue(),title, one.getItemNum(),"议价提交");
                    //价格记录表
                    insertPriceInfo(one);
                }
                else {
                    MongodbUtil.insertUserLogItem(one.getQuoteBargainSid(), BusinessType.APPROVAL.getValue(),title, one.getItemNum(),purQuoteBargainItem.getComment());
                }
            }
            else {
                return 0;
            }
        }
        return row;
    }
   //审批完成后回写价格
    public void updateOrder(PurQuoteBargainItem item,PurQuoteBargain bargain){
        new Thread(()->{
            PurPurchasePrice purchasePrice = new PurPurchasePrice();
            PurPurchasePriceItem purPurchasePriceItem = new PurPurchasePriceItem();
            BeanCopyUtils.copyProperties(item,purchasePrice);
            BeanCopyUtils.copyProperties(item,purPurchasePriceItem);
            BeanCopyUtils.copyProperties(bargain,purchasePrice);
            BeanCopyUtils.copyProperties(bargain,purPurchasePriceItem);
            purPurchasePriceImpl.orderUpdate(purchasePrice,purPurchasePriceItem);
        }).start();
    }
    /**
     * 复制报价/议价
     * @author chenkw
     * @param purQuoteBargainSid 需要主表sid
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PurQuoteBargain copy(PurQuoteBargainItem request) {
        if (request.getQuoteBargainItemSid() == null){
            return null;
        }
        PurQuoteBargainItem purQuoteBargainItem = purQuoteBargainItemMapper.selectPurRequestQuotationItemByItemId(request.getQuoteBargainItemSid());
        if (purQuoteBargainItem == null || purQuoteBargainItem.getQuoteBargainSid() == null){
            return null;
        }
        PurQuoteBargain purQuoteBargain = purQuoteBargainMapper.selectPurRequestQuotationById(purQuoteBargainItem.getQuoteBargainSid());
        if (purQuoteBargain == null){
            return null;
        }
        //复制后返回的
        PurQuoteBargainItem responseItem = new PurQuoteBargainItem();
        PurQuoteBargain response = new PurQuoteBargain();
        //报价单复制
        if (ConstantsPrice.BAOHEYI_STAGE_BJ.equals(request.getStage())){
            BeanCopyUtils.copyProperties(purQuoteBargainItem, responseItem);
            responseItem.setQuoteBargainSid(null).setQuoteBargainItemSid(null).setInquirySid(null)
                    .setInquiryCode(null).setInquiryItemSid(null).setCheckPriceTax(null).setCheckPrice(null)
                    .setConfirmPriceTax(null).setConfirmPrice(null).setPurchasePriceTax(null).setPurchasePrice(null)
                    .setIncreChePrice(null).setIncreConfPrice(null).setIncrePurPrice(null).setIncreChePriceTax(null)
                    .setIncreConfPriceTax(null).setIncrePurPriceTax(null).setDecreChePrice(null).setDecreConfPrice(null)
                    .setDecrePurPrice(null).setDecreChePriceTax(null).setDecreConfPriceTax(null).setDecrePurPriceTax(null)
                    .setQuoteUpdaterAccount(null).setQuoteUpdateDate(null).setCurrentStage(ConstantsPrice.BAOHEYI_STAGE_BJ)
                    .setCheckUpdaterAccount(null).setCheckUpdateDate(null).setConfirmUpdateDate(null).setConfirmUpdaterAccount(null)
                    .setPurchaseUpdaterAccount(null).setPurchaseUpdateDate(null).setRemarkRequest(null).setRemarkCheck(null)
                    .setRemarkConfirm(null).setRemarkPurchase(null).setHandleStatus(ConstantsEms.SAVA_STATUS)
                    .setCreatorAccountName(ApiThreadLocalUtil.get().getSysUser().getNickName())
                    .setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(new Date())
                    .setUpdaterAccount(null).setUpdateDate(null).setConfirmerAccount(null).setConfirmDate(null);
            //主表
            BeanCopyUtils.copyProperties(purQuoteBargain, response);
            response.setQuoteBargainSid(null).setQuoteBargainCode(null).setDateRequest(null).setDateCheck(null).setDateConfirm(null)
                    .setChecker(null).setPurchaseOrg(null).setCostOrg(null).setRemarkRequest(null).setRemarkCheck(null).setRemarkConfirm(null)
                    .setRemarkPurchase(null).setHandleStatus(ConstantsEms.SAVA_STATUS).setCreatedStage(ConstantsPrice.BAOHEYI_STAGE_BJ)
                    .setCreatorAccountName(ApiThreadLocalUtil.get().getSysUser().getNickName())
                    .setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(new Date())
                    .setUpdaterAccount(null).setUpdateDate(null).setConfirmerAccount(null).setConfirmDate(null);
            response.setPurRequestQuotationItemList(new ArrayList<PurQuoteBargainItem>(){
                {
                    add(responseItem);
                }
            });
            return response;
        }
        //议价单复制
        if (ConstantsPrice.BAOHEYI_STAGE_YJ.equals(request.getStage())){
            BeanCopyUtils.copyProperties(purQuoteBargainItem, responseItem);
            responseItem.setQuoteBargainSid(null).setQuoteBargainItemSid(null).setInquirySid(null)
                    .setInquiryCode(null).setInquiryItemSid(null)
                    .setQuotePrice(null).setQuotePriceTax(null).setCheckPriceTax(null).setCheckPrice(null)
                    .setIncreQuoPriceTax(null).setDecreQuoPriceTax(null).setIncreChePriceTax(null).setDecreChePriceTax(null)
                    .setIncreQuoPrice(null).setDecreQuoPrice(null).setIncreChePrice(null).setDecreChePrice(null)
                    .setQuoteUpdaterAccount(null).setQuoteUpdateDate(null).setCurrentStage(ConstantsPrice.BAOHEYI_STAGE_YJ)
                    .setCheckUpdaterAccount(null).setCheckUpdateDate(null).setConfirmUpdateDate(null).setConfirmUpdaterAccount(null)
                    .setPurchaseUpdaterAccount(null).setPurchaseUpdateDate(null).setRemarkRequest(null).setRemarkCheck(null)
                    .setRemarkQuote(null).setHandleStatus(ConstantsEms.SAVA_STATUS)
                    .setCreatorAccountName(ApiThreadLocalUtil.get().getSysUser().getNickName())
                    .setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(new Date())
                    .setUpdaterAccount(null).setUpdateDate(null).setConfirmerAccount(null).setConfirmDate(null);
            //主表
            BeanCopyUtils.copyProperties(purQuoteBargain, response);
            response.setQuoteBargainSid(null).setQuoteBargainCode(null).setDateRequest(null).setDateCheck(null).setDateQuote(null)
                    .setChecker(null).setRemarkRequest(null).setRemarkCheck(null).setRemarkQuote(null)
                    .setHandleStatus(ConstantsEms.SAVA_STATUS).setCreatedStage(ConstantsPrice.BAOHEYI_STAGE_YJ)
                    .setCreatorAccountName(ApiThreadLocalUtil.get().getSysUser().getNickName())
                    .setCreatorAccount(ApiThreadLocalUtil.get().getUsername()).setCreateDate(new Date())
                    .setUpdaterAccount(null).setUpdateDate(null).setConfirmerAccount(null).setConfirmDate(null);
            response.setPurRequestQuotationItemList(new ArrayList<PurQuoteBargainItem>(){
                {
                    add(responseItem);
                }
            });
            return response;
        }
        return response;
    }

    /**
     * 采购议价 导入
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importDataPur(MultipartFile file) {
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
            //甲供料 方式
            List<DictData> rawMaterialMode = sysDictDataService.selectDictData("s_raw_material_mode");
            Map<String, String> rawMaterialModeMaps = rawMaterialMode.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //采购模式
            List<DictData> priceType = sysDictDataService.selectDictData("s_price_type");
            Map<String, String> priceTypeMaps = priceType.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //价格维度
            List<DictData> priceDimension = sysDictDataService.selectDictData("s_price_dimension");
            Map<String, String> priceDimensionMaps = priceDimension.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //递增减sku类型
            List<DictData> skuType = sysDictDataService.selectDictData("s_sku_type");
            Map<String, String> skuTypeMaps = skuType.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //税率
            Map<String, BigDecimal> taxRateMaps = conTaxRateMapper.getConTaxRateList().stream().collect(Collectors.toMap(ConTaxRate::getTaxRateName, ConTaxRate::getTaxRateValue, (key1, key2) -> key2));
            //基本计量单位
            List<ConMeasureUnit> conMeasureUnitList = conMeasureUnitMapper.getConMeasureUnitList();
            Map<String, String> measureUnitMaps = conMeasureUnitList.stream().collect(Collectors.toMap(ConMeasureUnit::getName, ConMeasureUnit::getCode, (key1, key2) -> key2));
            //取整方式
            List<DictData> roundingType = sysDictDataService.selectDictData("s_rounding_type");
            Map<String, String> roundingTypeMaps = roundingType.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //是否
            List<DictData> yes = sysDictDataService.selectDictData("sys_yes_no");
            Map<String, String> yesMaps = yes.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            //采购组
            Map<String, String> purchaseGroupMaps = conPurchaseGroupMapper.getList().stream().collect(Collectors.toMap(ConPurchaseGroup::getCode, ConPurchaseGroup::getName, (key1, key2) -> key2));
            PurQuoteBargain purQuoteBargain = new PurQuoteBargain();
            List<PurQuoteBargainItem> purQuoteBargainItemList = new ArrayList<>();
            for (int i = 0; i < readAll.size(); i++) {
                Long companySid = null;
                String vendorSid = null;
                Long materialSid = null;
                Long sku1Sid = null;
                Long productSeasonSid = null;
                String unitPrice = null;
                String materialCode = null;
                if (i < 2 || i == 3 || i == 4) {
                    //前两行跳过
                    continue;
                }
                if (i == 2) {
                    List<Object> objects = readAll.get(i);
                    copy(objects, readAll);
                    int num = i + 1;
                    if (objects.get(0) == null || objects.get(0) == "") {
                        throw new BaseException("第" + num + "行,供应商简称，不能为空，导入失败");
                    }
                    String bendorCode = objects.get(0).toString();
                    BasVendor basVendor = basVendorMapper.selectOne(new QueryWrapper<BasVendor>()
                            .lambda().eq(BasVendor::getShortName, bendorCode));
                    if (basVendor == null) {
                        throw new BaseException("第" + num + "行,供应商简称为" + bendorCode + "，没有对应的供应商，导入失败");
                    } else {
                        if (!basVendor.getHandleStatus().equals(ConstantsEms.CHECK_STATUS) || !basVendor.getStatus().equals(ConstantsEms.ENABLE_STATUS)) {
                            throw new BaseException("第" + num + "行,对应的供应商必须是确认且已启用的状态，导入失败");
                        }
                        vendorSid = basVendor.getVendorSid().toString();
                    }
                    if (objects.get(1) == null || objects.get(1) == "") {
                        throw new BaseException("第" + num + "行,甲供料方式，不能为空，导入失败");
                    }
                    String rawMaterialModeValue = rawMaterialModeMaps.get(objects.get(1).toString());
                    if (StrUtil.isEmpty(rawMaterialModeValue)) {
                        throw new BaseException("第" + num + "行,甲供料方式配置错误，导入失败,请联系管理员");
                    }
                    if (objects.get(2) == null || objects.get(2) == "") {
                        throw new BaseException("第" + num + "行,采购模式，不能为空，导入失败");
                    }
                    String priceTypeValue = priceTypeMaps.get(objects.get(2).toString());
                    if (StrUtil.isEmpty(priceTypeValue)) {
                        throw new BaseException("第" + num + "行,采购模式配置错误，导入失败,请联系管理员");
                    }
                    if (objects.get(3) == null || objects.get(3) == "") {
                        throw new BaseException("第" + num + "行,价格维度，不能为空，导入失败");
                    }
                    String priceDimensionValue = priceDimensionMaps.get(objects.get(3).toString());
                    if (StrUtil.isEmpty(priceDimensionValue)) {
                        throw new BaseException("第" + num + "行,价格维度配置错误，导入失败,请联系管理员");
                    }
                    if (objects.get(4) == null || objects.get(4) == "") {
                        throw new BaseException("第" + num + "行,议价日期不允许为空，导入失败");
                    }
                    boolean validDate = JudgeFormat.isValidDate((objects.get(4).toString()));
                    if (!validDate) {
                        throw new BaseException("第" + num + "行,议价日期,数据格式错误，导入失败");
                    }
                    if (objects.get(5) == null || objects.get(5) == "") {
                        throw new BaseException("第" + num + "行,采购员不允许为空，导入失败");
                    }
                    R<LoginUser> userInfo = remoteUserService.getUserInfo(objects.get(5).toString());
                    if (userInfo.getData() == null) {
                        throw new BaseException("第" + num + "行,没有账号为" + objects.get(5).toString() + "的采购员,导入失败");
                    }
                    String status = userInfo.getData().getSysUser().getStatus();
                    if (!"0".equals(status)) {
                        throw new BaseException("第" + num + "行,采购员账号必须是启用状态，导入失败");
                    }
                    if (objects.get(6) == null || objects.get(6) == "") {
                        throw new BaseException("第" + num + "行,有效期起，不能为空，导入失败");
                    }
                    if (objects.get(7) == null || objects.get(7) == "") {
                        throw new BaseException("第" + num + "行,有效期至，不能为空，导入失败");
                    }
                    boolean start = JudgeFormat.isValidDate(objects.get(6).toString());
                    boolean end = JudgeFormat.isValidDate(objects.get(7).toString());
                    if (!start) {
                        throw new BaseException("第" + num + "行,有效期起，日期格式错误");
                    }
                    if (!end) {
                        throw new BaseException("第" + num + "行,有效期至，日期格式错误");
                    }
                    if (DateUtils.parseDate(objects.get(6)).getTime() > DateUtils.parseDate(objects.get(7)).getTime()) {
                        throw new BaseException("第" + num + "行,有效期起，不能大于有效期至，导入失败");
                    }
                    if (objects.get(8) != null && objects.get(8) != "") {
                        BasProductSeason basProductSeason = basProductSeasonMapper.selectOne(new QueryWrapper<BasProductSeason>()
                                .lambda().eq(BasProductSeason::getProductSeasonName, objects.get(8).toString()));
                        if (basProductSeason == null) {
                            throw new BaseException("第" + num + "行,产品季名称为" + objects.get(8).toString() + "，没有对应的产品季，导入失败");
                        } else {
                            if (!basProductSeason.getHandleStatus().equals(ConstantsEms.CHECK_STATUS) || !basProductSeason.getStatus().equals(ConstantsEms.ENABLE_STATUS)) {
                                throw new BaseException("第" + num + "行,对应的产品季必须是确认且已启用的状态，导入失败");
                            }
                            productSeasonSid = basProductSeason.getProductSeasonSid();
                        }

                    }
                    if (objects.get(9) != null && objects.get(9) != "") {
                        ConPurchaseGroup conPurchaseGroup = conPurchaseGroupMapper.selectOne(new QueryWrapper<ConPurchaseGroup>().lambda()
                                .eq(ConPurchaseGroup::getCode, objects.get(9).toString())
                        );
                        if (conPurchaseGroup == null) {
                            throw new BaseException("第" + num + "行,采购组编码配置错误，导入失败");
                        }
                        if (!ConstantsEms.CHECK_STATUS.equals(conPurchaseGroup.getHandleStatus()) || !ConstantsEms.SAVA_STATUS.equals(conPurchaseGroup.getStatus())) {
                            throw new BaseException("第" + num + "行,采购组编码必须是确认且已启用状态，导入失败");
                        }
                    }
                    if (objects.get(10) != null && objects.get(10) != "") {
                        boolean phone = JudgeFormat.isPhone(objects.get(10).toString());
                        if (!phone) {
                            throw new BaseException("第" + num + "行,采购员电话格式错误，导入失败");
                        }
                    }
                    if (objects.get(11) != null && objects.get(11) != "") {
                        boolean email = JudgeFormat.checkEmail(objects.get(11).toString());
                        if (!email) {
                            throw new BaseException("第" + num + "行,采购员邮箱格式错误，导入失败");
                        }
                    }
                    if (objects.get(12) != null && objects.get(12) != "") {
                        boolean phone = JudgeFormat.isPhone(objects.get(12).toString());
                        if (!phone) {
                            throw new BaseException("第" + num + "行,报价员电话格式错误，导入失败");
                        }
                    }
                    if (objects.get(13) != null && objects.get(13) != "") {
                        boolean email = JudgeFormat.checkEmail(objects.get(13).toString());
                        if (!email) {
                            throw new BaseException("第" + num + "行,报价员邮箱格式错误，导入失败");
                        }
                    }
                    purQuoteBargain.setVendorSid(Long.valueOf(vendorSid))
                            .setProductSeasonSid(productSeasonSid)
                            .setCreateDate(new Date())
                            .setPriceDimension(priceDimensionValue)
                            .setImportHandle("1")
                            .setCreatorAccount(ApiThreadLocalUtil.get().getUsername())
                            .setRawMaterialMode(rawMaterialModeValue)
                            .setPurchaseMode(priceTypeValue)
                            .setStartDate(DateUtils.parseDate(objects.get(6)))
                            .setEndDate(DateUtils.parseDate(objects.get(7)))
                            .setBuyerTelephone((objects.get(10) == "" || objects.get(10) == null) ? null : objects.get(10).toString())
                            .setBuyerEmail((objects.get(11) == "" || objects.get(11) == null) ? null : objects.get(11).toString())
                            .setBuyer(objects.get(5).toString())
                            .setCurrency(ConstantsFinance.CURRENCY_CNY)
                            .setCreatedStage(ConstantsPrice.BAOHEYI_STAGE_YJ)
                            .setCurrencyUnit(ConstantsFinance.CURRENCY_UNIT_YUAN)
                            .setQuoterTelephone((objects.get(12) == "" || objects.get(12) == null) ? null : objects.get(12).toString())
                            .setQuoterEmail((objects.get(13) == "" || objects.get(13) == null) ? null : objects.get(13).toString())
                            .setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setDateConfirm(DateUtils.parseDate(objects.get(4)))
                            .setPurchaseGroup((objects.get(9) == "" || objects.get(9) == null) ? null : objects.get(9).toString())
                            .setRemarkPurchase((objects.get(14) == "" || objects.get(14) == null) ? null : objects.get(14).toString());
                }
                if (i != 2) {
                    int num = i + 1;
                    List<Object> objects = readAll.get(i);
                    copyItem(objects, readAll);
                    if (objects.get(0) == null || objects.get(0) == "") {
                        throw new BaseException("第" + num + "行,商品/物料编码，不能为空，导入失败");
                    }
                    BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda()
                            .eq(BasMaterial::getMaterialCode, objects.get(0).toString())
                    );
                    if (basMaterial == null) {
                        throw new BaseException("第" + num + "行,商品/物料编码为" + objects.get(0).toString() + "，没有对应的商品/物料，导入失败");
                    } else {
                        if (!basMaterial.getHandleStatus().equals(ConstantsEms.CHECK_STATUS) || !basMaterial.getStatus().equals(ConstantsEms.ENABLE_STATUS)) {
                            throw new BaseException("第" + num + "行,对应的商品/物料必须是确认且已启用的状态，导入失败");
                        }
                        materialSid = basMaterial.getMaterialSid();
                        materialCode = basMaterial.getMaterialCode();
                    }
                    if (objects.get(1) != null && objects.get(1) != "") {
                        if (ConstantsEms.PRICE_K.equals(purQuoteBargain.getPriceDimension())) {
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
                                throw new BaseException("第" + num + "行,该物料没有对应的颜色，导入失败");
                            }
                            if (!ConstantsEms.SAVA_STATUS.equals(basMaterialSkus.get(0).getSkuStatus())) {
                                throw new BaseException("第" + num + "行,该颜色名称必须启用状态，导入失败");
                            }
                        }
                    }
                    if (ConstantsEms.PRICE_K1.equals(purQuoteBargain.getPriceDimension())) {
                        if (sku1Sid == null) {
                            throw new BaseException("第" + num + "行,价格维度按色时,颜色为必填，导入失败");
                        }
                    }
                    if (objects.get(2) == null || objects.get(2) == "") {
                        throw new BaseException("第" + num + "行,采购价（含税），不能为空，导入失败");
                    }
                    boolean validDouble = JudgeFormat.isValidDouble(objects.get(2).toString());
                    if (!validDouble) {
                        throw new BaseException("第" + num + "行,采购价（含税）,数据格式错误，导入失败");
                    }
                    if (objects.get(3) == null || objects.get(3) == "") {
                        throw new BaseException("第" + num + "行,税率，不能为空，导入失败");
                    }
                    BigDecimal taxRateValue = taxRateMaps.get(objects.get(3).toString());
                    if (taxRateValue == null) {
                        throw new BaseException("第" + num + "行,税率配置错误，导入失败,请联系管理员");
                    }
                    if (objects.get(4) != null && objects.get(4) != "") {
                        boolean validQutor = JudgeFormat.isValidDouble(objects.get(4).toString());
                        if (!validQutor) {
                            throw new BaseException("第" + num + "行,报价,数据格式错误，导入失败");
                        }
                    }
                    if (objects.get(6) != null && objects.get(6) != "") {
                        String skuTyp = skuTypeMaps.get(objects.get(6).toString());
                        if (skuTyp == null) {
                            throw new BaseException("第" + num + "行,递增减SKU类型配置错误，导入失败");
                        }
                    }

                    if (objects.get(7) != null && objects.get(7) != "") {
                        boolean valid = JudgeFormat.isValidDoubleLgZero(objects.get(7).toString(),7,3);
                        if (!valid) {
                            throw new BaseException("第" + num + "行,递增量,数据格式错误，导入失败");
                        }
                    }
                    if (objects.get(8) != null && objects.get(8) != "") {
                        boolean valid = JudgeFormat.isValidDouble(objects.get(8).toString());
                        if (!valid) {
                            throw new BaseException("第" + num + "行,递增价(含税),数据格式错误，导入失败");
                        }
                    }
                    if (objects.get(9) != null && objects.get(9) != "") {
                        boolean valid = JudgeFormat.isValidDoubleLgZero(objects.get(9).toString(),7,3);
                        if (!valid) {
                            throw new BaseException("第" + num + "行,递减量,数据格式错误，导入失败");
                        }
                    }
                    if (objects.get(10) != null && objects.get(10) != "") {
                        boolean valid = JudgeFormat.isValidDouble(objects.get(10).toString());
                        if (!valid) {
                            throw new BaseException("第" + num + "行,递减价(含税),数据格式错误，导入失败");
                        }
                    }
                    if (objects.get(11) != null && objects.get(11) != "") {
                        boolean valid = JudgeFormat.isValidDoubleLgZero(objects.get(11).toString(),7,3);
                        if (!valid) {
                            throw new BaseException("第" + num + "行,基准量,数据格式错误，导入失败");
                        }
                    }
                    if (objects.get(12) != null && objects.get(12) != "") {
                        boolean valid = JudgeFormat.isValidDoubleLgZero(objects.get(12).toString(),7,3);
                        if (!valid) {
                            throw new BaseException("第" + num + "行,价格最小起算量,数据格式错误，导入失败");
                        }
                    }
                    if (objects.get(11) != null && objects.get(11) != "") {
                        if (objects.get(12) != null && objects.get(12) != "") {
                            if (Double.valueOf(objects.get(11).toString()) - Double.valueOf(objects.get(12).toString()) < 0) {
                                throw new BaseException("第" + num + "行,价格最小起算量大于基准量，导入失败！");
                            }
                        }
                    }
                    if (objects.get(5) == null || objects.get(5) == "") {
                        throw new BaseException("第" + num + "行,是否递增减价，不允许为空，导入失败");
                    }
                    String is = yesMaps.get(objects.get(5).toString());
                    if (is == null) {
                        throw new BaseException("第" + num + "行,是否递增减价配置错误，导入失败");
                    }
                    if (ConstantsEms.YES.equals(is)) {
                        if (objects.get(6) == null || objects.get(6) == "") {
                            throw new BaseException("第" + num + "行,递增减价，递增减SKU类型不能为空，导入失败");
                        }
                        if (objects.get(7) == null || objects.get(7) == "") {
                            throw new BaseException("第" + num + "行,递增减价，递增量不能为空，导入失败");
                        }
                        if (objects.get(8) == null || objects.get(8) == "") {
                            throw new BaseException("第" + num + "行,递增减价，递增价(含税)不能为空，导入失败");
                        }
                        if (objects.get(9) == null || objects.get(9) == "") {
                            throw new BaseException("第" + num + "行,递增减价，递减量不能为空，导入失败");
                        }
                        if (objects.get(10) == null || objects.get(10) == "") {
                            throw new BaseException("第" + num + "行,递增减价，递减价(含税)不能为空，导入失败");
                        }
                        if (objects.get(11) == null || objects.get(11) == "") {
                            throw new BaseException("第" + num + "行,递增减价，基准量不能为空，导入失败");
                        }
                        if (objects.get(13) == null || objects.get(13) == "") {
                            throw new BaseException("第" + num + "行,递增减价，取整方式(递增减)，不能为空，导入失败");
                        }
                        if (objects.get(14) == null || objects.get(14) == "") {
                            throw new BaseException("第" + num + "行,递增减价，递增减计量单位名称，不能为空，导入失败");
                        }
                    } else {
                        if (objects.get(6) != null && objects.get(6) != "") {
                            throw new BaseException("第" + num + "行,非递增减价，递增减SKU类型，不需要填写，导入失败");
                        }
                        if (objects.get(7) != null && objects.get(7) != "") {
                            throw new BaseException("第" + num + "行,非递增减价，递增量不需要填写，导入失败");
                        }
                        if (objects.get(8) != null && objects.get(8) != "") {
                            throw new BaseException("第" + num + "行,非递增减价，递增价(含税)，不需要填写，导入失败");
                        }
                        if (objects.get(9) != null && objects.get(9) != "") {
                            throw new BaseException("第" + num + "行,非递增减价，递减量不需要填写，导入失败");
                        }
                        if (objects.get(10) != null && objects.get(10) != "") {
                            throw new BaseException("第" + num + "行,非递增减价，递减价(含税)不需要填写，导入失败");
                        }
                        if (objects.get(11) != null && objects.get(11) != "") {
                            throw new BaseException("第" + num + "行,非递增减价，基准量不需要填写，导入失败");
                        }
                        if (objects.get(13) != null && objects.get(13) != "") {
                            throw new BaseException("第" + num + "行,非递增减价，取整方式(递增减)，不需要填写，导入失败");
                        }
                        if (objects.get(14) != null && objects.get(14) != "") {
                            throw new BaseException("第" + num + "行,非递增减价，递增减计量单位名称不需要填写，导入失败");
                        }
                    }
                    if (objects.get(13) != null && objects.get(13) != "") {
                        String round = roundingTypeMaps.get(objects.get(13).toString());
                        if (round == null) {
                            throw new BaseException("第" + num + "行,取整方式(递增减)配置错误，导入失败");
                        }
                    }
                    if (objects.get(14) != null && objects.get(14) != "") {
                        String unit = measureUnitMaps.get(objects.get(14).toString());
                        if (unit == null) {
                            throw new BaseException("第" + num + "行,递增减计量单位配置错误，导入失败");
                        }
                    }
                    if (objects.get(15) != null && objects.get(15) != "") {
                        ConMeasureUnit conMeasureUnit = conMeasureUnitMapper.selectOne(new QueryWrapper<ConMeasureUnit>().lambda()
                                .eq(ConMeasureUnit::getName, objects.get(15).toString())
                        );
                        if (conMeasureUnit == null) {
                            throw new BaseException("第" + num + "行,采购价单位配置错误，导入失败");
                        } else {
                            if (!HandleStatus.CONFIRMED.getCode().equals(conMeasureUnit.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(conMeasureUnit.getStatus())) {
                                throw new BaseException("第" + num + "行,采购价单位必须是启用且已确认状态，导入失败");
                            }
                        }
                        unitPrice = conMeasureUnit.getCode();
                        if (objects.get(16) == null || objects.get(16) == "") {
                            throw new BaseException("第" + num + "行,单位换算比例(采购价单位/基本计量单位)不允许为空，导入失败");
                        }
                    }
                    if (objects.get(16) != null && objects.get(16) != "") {
                        if (objects.get(15) == null || objects.get(15) == "") {
                            throw new BaseException("第" + num + "行,采购价单位不允许为空，导入失败");
                        }
                        boolean valid = JudgeFormat.isValidDoubleLgZero(objects.get(16).toString(),4,4);
                        if (!valid) {
                            throw new BaseException("第" + num + "行,单位换算比例,数据格式错误，导入失败");
                        }
                    }
                    PurQuoteBargainItem purQuoteBargainItem = new PurQuoteBargainItem();
                    BigDecimal price = BigDecimal.valueOf(Double.valueOf(objects.get(2).toString()));
                    BigDecimal priceTax = price.divide(BigDecimal.ONE, 3, BigDecimal.ROUND_HALF_UP);
                    purQuoteBargainItem
                            .setPurchasePriceTax(priceTax)
                            .setMaterialSid(materialSid)
                            .setUnitBase(basMaterial.getUnitBase())
                            .setPriceEnterMode("HS")
                            .setHandleStatus(ConstantsEms.SAVA_STATUS)
                            .setSku1Sid(sku1Sid)
                            .setPriceDimension(purQuoteBargain.getPriceDimension())
                            .setUnitPrice(unitPrice)
                            .setTaxRate(taxRateMaps.get((objects.get(3) == "" || objects.get(3) == null) ? null : objects.get(3).toString()))
                            .setQuotePriceTax((objects.get(4) == "" || objects.get(4) == null) ? null : BigDecimal.valueOf(Double.valueOf(objects.get(4).toString())))
                            .setIsRecursionPrice(yesMaps.get(objects.get(5).toString()))
                            .setSkuTypeRecursion(objects.get(6) == null ? null : objects.get(6).toString())
                            .setUnitConversionRate((objects.get(16) == "" || objects.get(16) == null) ? null : BigDecimal.valueOf(Double.valueOf(objects.get(16).toString())))
                            .setIncreQuantity((objects.get(7) == "" || objects.get(7) == null) ? null : BigDecimal.valueOf(Double.valueOf(objects.get(7).toString())))
                            .setIncrePurPriceTax((objects.get(8) == "" || objects.get(8) == null) ? null : BigDecimal.valueOf(Double.valueOf(objects.get(8).toString())))
                            .setDecreQuantity((objects.get(9) == "" || objects.get(9) == null) ? null : BigDecimal.valueOf(Double.valueOf(objects.get(9).toString())))
                            .setDecrePurPriceTax((objects.get(10) == "" || objects.get(10) == null) ? null : BigDecimal.valueOf(Double.valueOf(objects.get(10).toString())))
                            .setReferQuantity((objects.get(11) == "" || objects.get(11) == null) ? null : BigDecimal.valueOf(Double.valueOf(objects.get(11).toString())))
                            .setPriceMinQuantity((objects.get(12) == "" || objects.get(12) == null) ? null : BigDecimal.valueOf(Double.valueOf(objects.get(12).toString())))
                            .setRoundingType((objects.get(13) == "" || objects.get(13) == null) ? null : roundingTypeMaps.get(objects.get(13).toString()))
                            .setUnitRecursion((objects.get(14) == "" || objects.get(14) == null) ? null : measureUnitMaps.get(objects.get(14).toString()));
                    if (purQuoteBargainItem.getUnitPrice() == null && purQuoteBargainItem.getUnitConversionRate() == null) {
                        purQuoteBargainItem.setUnitPrice(purQuoteBargainItem.getUnitBase());
                        purQuoteBargainItem.setUnitConversionRate(BigDecimal.ONE);
                    }
                    purQuoteBargainItemList.add(purQuoteBargainItem);
                }
            }
            purQuoteBargain.setPurRequestQuotationItemList(purQuoteBargainItemList);
            for (int i = 6; i < purQuoteBargainItemList.size() + 6; i++) {
                try {
                    PurQuoteBargain quotation = new PurQuoteBargain();
                    BeanCopyUtils.copyProperties(purQuoteBargain, quotation);
                    ArrayList<PurQuoteBargainItem> purQuoteBargainItems = new ArrayList<>();
                    purQuoteBargainItems.add(purQuoteBargainItemList.get(i - 6));
                    quotation.setPurRequestQuotationItemList(purQuoteBargainItems);
                } catch (Exception e) {
                    throw new CustomException("第" + i + "行有效期存在交集，导入失败");
                }
            }
            try {
                insertPurRequestQuotation(purQuoteBargain);
            } catch (Exception e) {
                throw new CustomException("存在有效期");
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return 1;
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

    //填充-明细表
    public void copyItem(List<Object> objects, List<List<Object>> readAll) {
        //获取第三行的列数
        int size = readAll.get(3).size();
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
