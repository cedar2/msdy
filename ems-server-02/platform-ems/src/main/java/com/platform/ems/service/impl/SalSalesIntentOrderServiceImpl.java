package com.platform.ems.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsOrder;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.mapper.*;
import com.platform.ems.plug.domain.ConFileType;
import com.platform.ems.plug.mapper.ConFileTypeMapper;
import com.platform.ems.service.ISalSalesIntentOrderItemService;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.service.ISalSalesIntentOrderService;

/**
 * 销售意向单Service业务层处理
 *
 * @author chenkw
 * @date 2022-10-17
 */
@Service
@SuppressWarnings("all" )
public class SalSalesIntentOrderServiceImpl extends ServiceImpl<SalSalesIntentOrderMapper,SalSalesIntentOrder> implements ISalSalesIntentOrderService {
    @Autowired
    private SalSalesIntentOrderMapper salSalesIntentOrderMapper;
    @Autowired
    private SalSalesIntentOrderItemMapper salSalesIntentOrderItemMapper;
    @Autowired
    private SalSalesIntentOrderAttachMapper salSalesIntentOrderAttachMapper;
    @Autowired
    private ConFileTypeMapper conFileTypeMapper;
    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private BasProductSeasonMapper basProductSeasonMapper;
    @Autowired
    private SalSaleContractMapper salSaleContractMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;

    @Autowired
    private ISalSalesIntentOrderItemService salSalesIntentOrderItemService;

    private static final String TITLE = "销售意向单" ;

    /**
     * 查询销售意向单
     *
     * @param salesIntentOrderSid 销售意向单ID
     * @return 销售意向单
     */
    @Override
    public SalSalesIntentOrder selectSalSalesIntentOrderById(Long salesIntentOrderSid) {
        SalSalesIntentOrder salSalesIntentOrder =salSalesIntentOrderMapper.selectSalSalesIntentOrderById(salesIntentOrderSid);
        if (salSalesIntentOrder == null) {
            throw new BaseException("所选单号不存在");
        }
        salSalesIntentOrder.setAttachmentList(new ArrayList<>());
        salSalesIntentOrder.setIntentOrderItemList(new ArrayList<>());
        // 明细表
        List<SalSalesIntentOrderItem> intentOrderItemList  = salSalesIntentOrderItemService.selectSalSalesIntentOrderItemByOrderId(salesIntentOrderSid);
        if (CollectionUtil.isNotEmpty(intentOrderItemList)) {
            // 排序
            try {
                intentOrderItemList = salSalesIntentOrderItemService.newSort(intentOrderItemList);
            } catch (Exception e) {
                log.warn("明细表排序错误");
            }
            salSalesIntentOrder.setIntentOrderItemList(intentOrderItemList);
        }
        // 附件
        List<SalSalesIntentOrderAttach> attachmentList = salSalesIntentOrderAttachMapper.selectSalSalesIntentOrderAttachList(
                new SalSalesIntentOrderAttach().setSalesIntentOrderSid(salesIntentOrderSid));
        if (CollectionUtil.isNotEmpty(attachmentList)) {
            salSalesIntentOrder.setAttachmentList(attachmentList);
        }
        // 计算明细汇总金额 （参考销售订单）
        SalSalesIntentOrder count = getCount(intentOrderItemList);
        salSalesIntentOrder.setSumMoneyAmount(count.getSumMoneyAmount())
                .setSumQuantity(count.getSumQuantity()).setSumQuantityCode(count.getSumQuantityCode());
        // 操作日志
        MongodbUtil.find(salSalesIntentOrder);
        return salSalesIntentOrder;
    }

    /**
     * 计算金额
     *
     */
    @Override
    public SalSalesIntentOrder getCount(List<SalSalesIntentOrderItem> intentOrderItemList) {
        SalSalesIntentOrder intentOrder = new SalSalesIntentOrder();
        if(CollectionUtil.isNotEmpty(intentOrderItemList)){
            BigDecimal sumQu = intentOrderItemList.stream().map(li->{if( li.getQuantity()==null){
                return BigDecimal.ZERO;
            }else{
                return li.getQuantity();
            }}).reduce(BigDecimal.ZERO, BigDecimal::add);
            intentOrder.setSumQuantity(sumQu);
            BigDecimal sumCu = intentOrderItemList.stream().map(li ->{
                BigDecimal price=li.getSalePriceTax()!=null?li.getSalePriceTax():BigDecimal.ZERO;
                BigDecimal qutatil=li.getQuantity()!=null?li.getQuantity():BigDecimal.ZERO;
                return price.multiply(qutatil);
            }).reduce(BigDecimal.ZERO, BigDecimal::add);
            intentOrder.setSumMoneyAmount(sumCu.divide(BigDecimal.ONE,2,BigDecimal.ROUND_HALF_UP));
            HashSet<Long> longs = new HashSet<>();
            intentOrderItemList.forEach(li->{
                longs.add(li.getMaterialSid());
            });
            intentOrder.setSumQuantityCode(longs.size());
        }
        return intentOrder;
    }

    /**
     * 复制销售意向单
     *
     * @param salesIntentOrderSid 销售意向单ID
     * @return 销售意向单
     */
    @Override
    public SalSalesIntentOrder copySalSalesIntentOrderById(Long salesIntentOrderSid) {
        SalSalesIntentOrder salSalesIntentOrder =salSalesIntentOrderMapper.selectSalSalesIntentOrderById(salesIntentOrderSid);
        if (salSalesIntentOrder == null) {
            throw new BaseException("所选单号不存在");
        }
        salSalesIntentOrder.setAttachmentList(new ArrayList<>());
        salSalesIntentOrder.setIntentOrderItemList(new ArrayList<>());
        salSalesIntentOrder.setSalesIntentOrderSid(null).setSalesIntentOrderCode(null)
                .setHandleStatus(ConstantsEms.SAVA_STATUS).setCreateDate(null).setCreatorAccount(null)
                .setUpdateDate(null).setUpdaterAccount(null).setConfirmDate(null).setConfirmerAccount(null);
        // 明细表
        List<SalSalesIntentOrderItem> intentOrderItemList = salSalesIntentOrderItemMapper.selectSalSalesIntentOrderItemList(new SalSalesIntentOrderItem()
                .setSalesIntentOrderSid(salesIntentOrderSid));
        if (CollectionUtil.isNotEmpty(intentOrderItemList)) {
            intentOrderItemList.forEach(item -> {
                item.setSalesIntentOrderItemSid(null).setSalesIntentOrderSid(null)
                        .setCreateDate(null).setCreatorAccount(null).setUpdateDate(null).setUpdaterAccount(null);
            });
            salSalesIntentOrder.setIntentOrderItemList(intentOrderItemList);
        }
        return salSalesIntentOrder;
    }

    /**
     * 查询销售意向单列表
     *
     * @param salSalesIntentOrder 销售意向单
     * @return 销售意向单
     */
    @Override
    public List<SalSalesIntentOrder> selectSalSalesIntentOrderList(SalSalesIntentOrder salSalesIntentOrder) {
        return salSalesIntentOrderMapper.selectSalSalesIntentOrderList(salSalesIntentOrder);
    }

    /**
     * 新增销售意向单
     * 需要注意编码重复校验
     * @param salSalesIntentOrder 销售意向单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSalSalesIntentOrder(SalSalesIntentOrder salSalesIntentOrder) {
        setData(null, salSalesIntentOrder);
        int row = salSalesIntentOrderMapper.insert(salSalesIntentOrder);
        if (row > 0) {
            // 写入明细
            if (CollectionUtil.isNotEmpty(salSalesIntentOrder.getIntentOrderItemList())) {
                salSalesIntentOrderItemService.insertSalSalesIntentOrderItemList(salSalesIntentOrder);
            }
            // 写入附件
            if (CollectionUtil.isNotEmpty(salSalesIntentOrder.getAttachmentList())) {
                salSalesIntentOrder.getAttachmentList().forEach(item->{
                    item.setSalesIntentOrderSid(salSalesIntentOrder.getSalesIntentOrderSid());
                });
                salSalesIntentOrderAttachMapper.inserts(salSalesIntentOrder.getAttachmentList());
            }
            // 待办通知
            if (ConstantsEms.SAVA_STATUS.equals(salSalesIntentOrder.getHandleStatus())) {
                SalSalesIntentOrder order = salSalesIntentOrderMapper.selectById(salSalesIntentOrder.getSalesIntentOrderSid());
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_SAL_SALES_INTENT_ORDER)
                        .setDocumentSid(order.getSalesIntentOrderSid())
                        .setTitle("销售意向单: " + order.getSalesIntentOrderCode() + " 当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(order.getSalesIntentOrderCode()))
                        .setNoticeDate(new Date()).setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new SalSalesIntentOrder(), salSalesIntentOrder);
            MongodbDeal.insert(salSalesIntentOrder.getSalesIntentOrderSid(), salSalesIntentOrder.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 写入一些需要后端自己获取的数据
     * @param salSalesIntentOrder 销售意向单 新的
     * @return 结果
     */
    private void setData(SalSalesIntentOrder old, SalSalesIntentOrder salSalesIntentOrder){
        if (salSalesIntentOrder == null) {
            return;
        }
        if (old == null) {
            old = new SalSalesIntentOrder();
        }
        // 客户
        if (salSalesIntentOrder.getCustomerSid() == null) {
            salSalesIntentOrder.setCustomerCode(null);
        } else if (!salSalesIntentOrder.getCustomerSid().equals(old.getCustomerSid())) {
            setCustomer((salSalesIntentOrder));
        }
        // 公司
        if (salSalesIntentOrder.getCompanySid() == null) {
            salSalesIntentOrder.setCompanyCode(null);
        } else if (!salSalesIntentOrder.getCompanySid().equals(old.getCompanySid())) {
            setCompany((salSalesIntentOrder));
        }
        // 产品季
        if (salSalesIntentOrder.getProductSeasonSid() == null) {
            salSalesIntentOrder.setProductSeasonCode(null);
        } else if (!salSalesIntentOrder.getProductSeasonSid().equals(old.getProductSeasonSid())) {
            setProductSeason((salSalesIntentOrder));
        }

        // 合同
        if (salSalesIntentOrder.getSaleIntentContractSid() == null) {
            salSalesIntentOrder.setSaleIntentContractCode(null);
        } else if (!salSalesIntentOrder.getSaleIntentContractSid().equals(old.getSaleIntentContractSid())) {
            setContract((salSalesIntentOrder));
        }

        // 初始化合同上传状态
        if (CollectionUtil.isNotEmpty(salSalesIntentOrder.getAttachmentList())) {
            boolean containsMaleUser = salSalesIntentOrder.getAttachmentList().stream()
                    .anyMatch(e -> ConstantsOrder.PAPER_CONTRACT_XSYXDDHT.equals(e.getFileType()));
            if (containsMaleUser) {
                salSalesIntentOrder.setUploadStatus(ConstantsEms.CONTRACT_UPLOAD_STATUS_Y);
            }
        }

    }

    /**
     * 客户
     */
    private void setCustomer(SalSalesIntentOrder salSalesIntentOrder) {
        BasCustomer customer = basCustomerMapper.selectById(salSalesIntentOrder.getCustomerSid());
        if (customer != null) {
            salSalesIntentOrder.setCustomerCode(customer.getCustomerCode());
        }
    }

    /**
     * 合同
     */
    private void setContract(SalSalesIntentOrder salSalesIntentOrder) {
        SalSaleContract contract = salSaleContractMapper.selectById(salSalesIntentOrder.getSaleIntentContractSid());
        if (contract != null) {
            salSalesIntentOrder.setSaleIntentContractCode(contract.getSaleContractCode());
        }
    }

    /**
     * 公司
     */
    private void setCompany(SalSalesIntentOrder salSalesIntentOrder) {
        BasCompany company = basCompanyMapper.selectById(salSalesIntentOrder.getCompanySid());
        if (company != null) {
            salSalesIntentOrder.setCompanyCode(company.getCompanyCode());
        }
    }

    /**
     * 产品季
     */
    private void setProductSeason(SalSalesIntentOrder salSalesIntentOrder) {
        BasProductSeason productSeason = basProductSeasonMapper.selectById(salSalesIntentOrder.getProductSeasonSid());
        if (productSeason != null) {
            salSalesIntentOrder.setProductSeasonCode(productSeason.getProductSeasonCode());
        }
    }

    /**
     * 修改销售意向单
     *
     * @param salSalesIntentOrder 销售意向单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSalSalesIntentOrder(SalSalesIntentOrder salSalesIntentOrder) {
        SalSalesIntentOrder original = salSalesIntentOrderMapper.selectSalSalesIntentOrderById(salSalesIntentOrder.getSalesIntentOrderSid());
        setData(original, salSalesIntentOrder);
        int row = salSalesIntentOrderMapper.updateAllById(salSalesIntentOrder);
        if (row > 0) {
            // 确认操作删除待办
            if (ConstantsEms.CHECK_STATUS.equals(salSalesIntentOrder.getHandleStatus())) {
                sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                        .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_SAL_SALES_INTENT_ORDER)
                        .eq(SysTodoTask::getDocumentSid, salSalesIntentOrder.getSalesIntentOrderSid()));
            }
            // 修改明细
            salSalesIntentOrderItemService.updateSalSalesIntentOrderItemList(salSalesIntentOrder);
            // 修改附件
            this.updateSalSalesIntentOrderAttach(salSalesIntentOrder);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, salSalesIntentOrder);
            MongodbDeal.update(salSalesIntentOrder.getSalesIntentOrderSid(), original.getHandleStatus(), salSalesIntentOrder.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量修改附件信息
     *
     * @param salSalesIntentOrder 销售意向单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSalSalesIntentOrderAttach(SalSalesIntentOrder salSalesIntentOrder) {
        // 先删后加
        salSalesIntentOrderAttachMapper.delete(new QueryWrapper<SalSalesIntentOrderAttach>().lambda()
                .eq(SalSalesIntentOrderAttach::getSalesIntentOrderSid, salSalesIntentOrder.getSalesIntentOrderSid()));
        if (CollectionUtil.isNotEmpty(salSalesIntentOrder.getAttachmentList())) {
            salSalesIntentOrder.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getSalesIntentOrderAttachSid() == null) {
                    att.setSalesIntentOrderSid(salSalesIntentOrder.getSalesIntentOrderSid());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            salSalesIntentOrderAttachMapper.inserts(salSalesIntentOrder.getAttachmentList());
        }
    }

    /**
     * 变更销售意向单
     *
     * @param salSalesIntentOrder 销售意向单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSalSalesIntentOrder(SalSalesIntentOrder salSalesIntentOrder) {
        SalSalesIntentOrder response = salSalesIntentOrderMapper.selectSalSalesIntentOrderById(salSalesIntentOrder.getSalesIntentOrderSid());
        setData(response, salSalesIntentOrder);
        int row = salSalesIntentOrderMapper.updateAllById(salSalesIntentOrder);
        if (row > 0) {
            // 修改明细
            salSalesIntentOrderItemService.updateSalSalesIntentOrderItemList(salSalesIntentOrder);
            // 修改附件
            this.updateSalSalesIntentOrderAttach(salSalesIntentOrder);
            //插入日志
            MongodbUtil.insertUserLog(salSalesIntentOrder.getSalesIntentOrderSid(), BusinessType.CHANGE.getValue(), response, salSalesIntentOrder, TITLE);
        }
        return row;
    }

    /**
     * 批量删除销售意向单
     *
     * @param salesIntentOrderSids 需要删除的销售意向单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalSalesIntentOrderByIds(List<Long> salesIntentOrderSids) {
        List<SalSalesIntentOrder> list = salSalesIntentOrderMapper.selectList(new QueryWrapper<SalSalesIntentOrder>()
                .lambda().in(SalSalesIntentOrder::getSalesIntentOrderSid, salesIntentOrderSids));
        int row = salSalesIntentOrderMapper.deleteBatchIds(salesIntentOrderSids);
        if (row > 0) {
            // 删除待办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_SAL_SALES_INTENT_ORDER)
                    .in(SysTodoTask::getDocumentSid, salesIntentOrderSids));
            // 删除明细
            salSalesIntentOrderItemService.deleteSalSalesIntentOrderItemListByOrder(salesIntentOrderSids);
            // 删除附件
            salSalesIntentOrderAttachMapper.delete(new QueryWrapper<SalSalesIntentOrderAttach>().lambda()
                    .in(SalSalesIntentOrderAttach::getSalesIntentOrderSid, salesIntentOrderSids));
            // 操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new SalSalesIntentOrder());
                MongodbUtil.insertUserLog(o.getSalesIntentOrderSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     *更改确认状态
     * @param salSalesIntentOrder
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(SalSalesIntentOrder salSalesIntentOrder) {
        int row = 0;
        Long[] sids =salSalesIntentOrder.getSalesIntentOrderSidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<SalSalesIntentOrder> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(SalSalesIntentOrder::getSalesIntentOrderSid, sids);
            updateWrapper.set(SalSalesIntentOrder::getHandleStatus, salSalesIntentOrder.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(salSalesIntentOrder.getHandleStatus())) {
                updateWrapper.set(SalSalesIntentOrder::getConfirmDate, new Date());
                updateWrapper.set(SalSalesIntentOrder::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            row = salSalesIntentOrderMapper.update(null, updateWrapper);
            if (row > 0){
                // 确认操作删除待办
                if (ConstantsEms.CHECK_STATUS.equals(salSalesIntentOrder.getHandleStatus())) {
                    sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                            .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                            .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_SAL_SALES_INTENT_ORDER)
                            .in(SysTodoTask::getDocumentSid, sids));
                }
                for (Long id : sids) {
                    //插入日志
                    MongodbDeal.check(id, salSalesIntentOrder.getHandleStatus(), null, TITLE, null);
                }
            }
        }
        return row;
    }

    /**
     * 作废销售意向单 多选  salesIntentOrderSidList
     *
     * @param salSalesIntentOrder
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancel(SalSalesIntentOrder salSalesIntentOrder) {
        int row = 0;
        Long[] sids =salSalesIntentOrder.getSalesIntentOrderSidList();
        if (ArrayUtil.isEmpty(sids)) {
            return row;
        }
        // 只有已确认状态才允许作废
        List<SalSalesIntentOrder> list = salSalesIntentOrderMapper.selectList(new QueryWrapper<SalSalesIntentOrder>()
                .lambda().in(SalSalesIntentOrder::getSalesIntentOrderSid, sids)
                .eq(SalSalesIntentOrder::getHandleStatus, ConstantsEms.CHECK_STATUS));
        if (list.size() != sids.length) {
            throw new BaseException("所选数据存在非'已确认'状态，无法作废！");
        }
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setCancelType(salSalesIntentOrder.getCancelType())
                    .setCancelRemark(salSalesIntentOrder.getCancelRemark())
                    .setHandleStatus(ConstantsEms.INVALID_STATUS);
        }
        row = salSalesIntentOrderMapper.updatesAllById(list);
        if (row > 0) {
            for (Long id : sids) {
                //插入日志
                MongodbUtil.insertUserLog(id, BusinessType.CANCEL.getValue(), null, TITLE, salSalesIntentOrder.getCancelRemark());
            }
        }
        return row;
    }

    /**
     * 新增按钮“纸质合同签收”（需做按钮权限）【可参照“商品销售订单查询”页面的”纸质合同签收“按钮的逻辑实现】
     *     1》若未勾选任何数据，此按钮置灰
     *     2》仅勾选处理状态为“已确认”且签收状态为“未签收”的销售意向单时，该按钮可点击；支持勾选多笔数据，批量签收
     *     3》点击此按钮，弹出提示信息弹窗：确定执行纸质合同签收操作？点击“取消”则关闭弹窗，点击“确定”按钮，操作成功后，
     *     则将所勾选的销售意向单的“签收状态”更新为：已签收，提示：操作成功，并刷新查询页面的数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setSignStatus(SalSalesIntentOrder salSalesIntentOrder) {
        int row = 0;
        Long[] sids =salSalesIntentOrder.getSalesIntentOrderSidList();
        if (ArrayUtil.isEmpty(sids)) {
            return row;
        }
        // 校验
        List<SalSalesIntentOrder> list = salSalesIntentOrderMapper.selectList(new QueryWrapper<SalSalesIntentOrder>()
                .lambda().in(SalSalesIntentOrder::getSalesIntentOrderSid, sids)
                .eq(SalSalesIntentOrder::getHandleStatus, ConstantsEms.CHECK_STATUS)
                .eq(SalSalesIntentOrder::getSignInStatus, ConstantsEms.SIGN_IN_STATUS_WQS));
        if (list.size() != sids.length) {
            throw new BaseException("仅勾选处理状态为“已确认”且签收状态为“未签收”的销售意向单时，才可进行此操作！");
        }
        for (Long id : sids) {
            row = row + salSalesIntentOrderMapper.update(null, new UpdateWrapper<SalSalesIntentOrder>()
                    .lambda().eq(SalSalesIntentOrder::getSalesIntentOrderSid, id)
                    .set(SalSalesIntentOrder::getSignInStatus, ConstantsEms.SIGN_IN_STATUS_YQS));
        }
        return row;
    }

    /**
     * 维护纸质合同号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity setPaperContract(SalSalesIntentOrder salesOrder) {
        int row = 0;
        // 修改
        LambdaUpdateWrapper<SalSalesIntentOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SalSalesIntentOrder::getSalesIntentOrderSid, salesOrder.getSalesIntentOrderSid())
                .set(SalSalesIntentOrder::getPaperSaleIntentContractCode, salesOrder.getPaperSaleIntentContractCode());
        row = salSalesIntentOrderMapper.update(new SalSalesIntentOrder(), updateWrapper);
        MongodbUtil.insertUserLog(salesOrder.getSalesIntentOrderSid(), BusinessType.QITA.getValue(), null, TITLE, "维护纸质合同号");
        if (StrUtil.isNotBlank(salesOrder.getPaperSaleIntentContractCode())) {
            List<SalSalesIntentOrderAttach> attachments = salSalesIntentOrderAttachMapper.selectList(new QueryWrapper<SalSalesIntentOrderAttach>()
                    .lambda().eq(SalSalesIntentOrderAttach::getFileType, ConstantsOrder.PAPER_CONTRACT_XSYXDDHT)
                    .eq(SalSalesIntentOrderAttach::getSalesIntentOrderSid, salesOrder.getSalesIntentOrderSid()));
            if (CollectionUtil.isEmpty(attachments)) {
                // 弹出提示框 是否上传“销售意向订单合同(盖章版)”附件
                return EmsResultEntity.warning(row, null, "是否上传“销售意向订单合同(盖章版)”附件");
            }
        }
        return EmsResultEntity.success(row, "操作成功");
    }

    /**
     * 查询页面上传附件前的校验
     */
    @Override
    public AjaxResult checkAttach(SalSalesIntentOrderAttach salesIntentOrderAttach) {
        if (salesIntentOrderAttach.getSalesIntentOrderSid() == null) {
            throw new BaseException("请先选择销售意向订单！");
        }
        if (StrUtil.isBlank(salesIntentOrderAttach.getFileType())) {
            return AjaxResult.success(true);
        } else {
            ConFileType conFileType = conFileTypeMapper.selectOne(new QueryWrapper<ConFileType>().lambda().eq(ConFileType::getCode, salesIntentOrderAttach.getFileType())
                    .eq(ConFileType::getDataobjectCategoryCode, ConstantsEms.DATA_OBJECT_CONTRACT_P));
            if (conFileType != null && ConstantsEms.NO.equals(conFileType.getIsUploadMultifile())) {
                List<SalSalesIntentOrderAttach> list = salSalesIntentOrderAttachMapper.selectList(new QueryWrapper<SalSalesIntentOrderAttach>().lambda()
                        .eq(SalSalesIntentOrderAttach::getSalesIntentOrderSid, salesIntentOrderAttach.getSalesIntentOrderSid())
                        .eq(SalSalesIntentOrderAttach::getFileType, salesIntentOrderAttach.getFileType()));
                if (CollectionUtils.isNotEmpty(list)) {
                    return AjaxResult.success("已存在：" + conFileType.getName() + " 附件，是否进行覆盖?", false);
                }
            }
        }
        return AjaxResult.success(true);
    }

    /**
     * 新增附件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSalSalesIntentOrderAttach(SalSalesIntentOrderAttach salesIntentOrderAttach) {
        return salSalesIntentOrderAttachMapper.insert(salesIntentOrderAttach);
    }
}
