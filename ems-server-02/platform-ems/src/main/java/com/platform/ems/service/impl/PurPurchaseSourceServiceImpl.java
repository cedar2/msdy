package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.SecurityUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.EmsDbTable;
import com.platform.ems.domain.PurMaterialSkuVencode;
import com.platform.ems.domain.PurPurchaseSource;
import com.platform.system.domain.SysTodoTask;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.enums.Status;
import com.platform.ems.mapper.PurMaterialSkuVencodeMapper;
import com.platform.ems.mapper.PurPurchaseSourceMapper;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.service.HandleStatusInfoService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 采购货源清单Service业务层处理
 */
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
public class PurPurchaseSourceServiceImpl extends ServiceImpl<PurPurchaseSourceMapper, PurPurchaseSource> implements HandleStatusInfoService {
    @Autowired
    private PurPurchaseSourceMapper purPurchaseSourceMapper;

    @Autowired
    private PurMaterialSkuVencodeMapper purMaterialSkuVencodeMapper;

    @Autowired
    SysTodoTaskMapper mapper;

    /**
     * 查询采购货源清单列表
     *
     * @param purPurchaseSource 采购货源清单
     * @return 采购货源清单
     */

    public List<PurPurchaseSource> selectPurPurchaseSourceList(PurPurchaseSource purPurchaseSource) {
        return purPurchaseSourceMapper.selectPurPurchaseSourceList(purPurchaseSource);
    }

    private static final String TITLE = "货源信息";

    /**
     * 新增采购货源清单
     * 需要注意编码重复校验
     *
     * @param pps 采购货源清单
     * @return 结果
     */

    @Transactional(rollbackFor = Exception.class)
    public int insertPurPurchaseSource(PurPurchaseSource pps) {
        // 验证供应商编码和商品/物料/服务编码组合是否重复
        checkVendorAndMaterialUnique(pps);

        // 设置创建人信息
        this.setHandleStatusInfoWhenNew(pps);

        // 插入数据
        int row = purPurchaseSourceMapper.insert(pps);
        if (row <= 0) {
            return row;
        }

        // 插入日志
        PurPurchaseSource response = this.purPurchaseSourceMapper.selectPurPurchaseSourceById(pps.getPurchaseSourceSid());
        List<OperMsg> msgList = BeanUtils.eq(response, pps);
        MongodbDeal.insert(pps.getPurchaseSourceSid(),
                           pps.getHandleStatus(),
                           msgList,
                           TITLE,
                           null);

        // 采购货源供方SKU编码list
        List<PurMaterialSkuVencode> purMaterialSkuVencodeList = pps.getPurMaterialSkuVencodeList();
        if (CollectionUtil.isNotEmpty(purMaterialSkuVencodeList)) {
            addPurMaterialSkuVencode(pps, purMaterialSkuVencodeList);
        }

        // 暂存 - 发待办
        if (HandleStatus.isTemporarySave(pps.getHandleStatus())) {
            // 重新查一下，因为有一些字段是数据库自动生成
            PurPurchaseSource dbEntity = this.purPurchaseSourceMapper.selectById(pps.getPurchaseSourceSid());
            addSysTodoTask(dbEntity);
            return row;
        }

        if (!HandleStatus.isConfirmed(pps.getHandleStatus())) {
            return row;
        }

        // 下面处理确认状态的业务逻辑
        if (!pps.isTheDefault()) {
            // 不是 “是”，return
            return row;
        }

        /*
        确认成功后，若该笔货源信息记录的“是否默认供应商”的值为“是”，
        则需将其它同样“商品/物料编码”的货源信息记录单的“是否默认供应商”的值，更新为“否”
         */

        // update xx set xx = N if sid != #{pps.sid} and code = ${pps.code}
        updateIsDefaultForAll(pps);

        return row;
    }


    /**
     * 采购货源供方SKU编码对象
     */
    private void addPurMaterialSkuVencode(PurPurchaseSource purPurchaseSource,
                                          List<PurMaterialSkuVencode> purMaterialSkuVencodeList) {
        purMaterialSkuVencodeMapper.delete(
                new UpdateWrapper<PurMaterialSkuVencode>()
                        .lambda()
                        .eq(PurMaterialSkuVencode::getPurchaseSourceSid, purPurchaseSource.getPurchaseSourceSid())
        );
        purMaterialSkuVencodeList.forEach(o -> {
            o.setClientId(SecurityUtils.getClientId());
            o.setMaterialVendorSkuSid(IdWorker.getId());
            o.setPurchaseSourceSid(purPurchaseSource.getPurchaseSourceSid());
            o.setCreatorAccount(SecurityUtils.getUsername());
            o.setCreateDate(new Date());
            purMaterialSkuVencodeMapper.insert(o);
        });
    }

    /**
     * 验证供应商编码和商品/物料/服务编码组合是否重复
     */
    private void checkVendorAndMaterialUnique(PurPurchaseSource purPurchaseSource) {
        if (purPurchaseSourceMapper.checkVendorAndMaterial(purPurchaseSource) > 0) {
            throw new BaseException("“商品/物料编码+供应商”的值的组合已存在！");
        }
    }

    /**
     * 修改采购货源清单
     *
     * @param purPurchaseSource 采购货源清单
     * @return 结果
     */

    @Transactional(rollbackFor = Exception.class)
    public int updatePurPurchaseSource(PurPurchaseSource purPurchaseSource) {
        PurPurchaseSource result = purPurchaseSourceMapper.selectPurPurchaseSourceById(purPurchaseSource.getPurchaseSourceSid());
        // 验证供应商编码和商品编码组合是否修改
        if (!purPurchaseSource.getVendorSid().equals(result.getVendorSid()) || !purPurchaseSource.getMaterialSid().equals(
                result.getMaterialSid())) {
            // 验证供应商编码和商品编码组合是否重复
            checkVendorAndMaterialUnique(purPurchaseSource);
        }
        // 设置确认信息
        this.setHandleStatusInfoWhenUpdate(purPurchaseSource);

        // 删除公司时，删除code
        if (purPurchaseSource.getCompanySid() == null) {
            purPurchaseSource.setCompanyCode(null);
        }

        // noinspection DuplicatedCode
        purPurchaseSourceMapper.updateAllById(purPurchaseSource);
        // 采购货源供方SKU编码list
        List<PurMaterialSkuVencode> purMaterialSkuVencodeList = purPurchaseSource.getPurMaterialSkuVencodeList();
        if (CollectionUtil.isNotEmpty(purMaterialSkuVencodeList)) {
            addPurMaterialSkuVencode(purPurchaseSource, purMaterialSkuVencodeList);
        }

        // 默认供应商
        doAfterUpdateEntity(purPurchaseSource);

        // 插入日志
        // 更新人更新日期
        List<OperMsg> msgList = BeanUtils.eq(result, purPurchaseSource);
        MongodbDeal.update(purPurchaseSource.getPurchaseSourceSid(),
                           result.getHandleStatus(),
                           purPurchaseSource.getHandleStatus(),
                           msgList,
                           TITLE,
                           null);
        return 1;
    }

    /**
     * 查询采购货源清单
     *
     * @param purchaseSourceSid 采购货源清单ID
     * @return 采购货源清单
     */

    public PurPurchaseSource selectPurPurchaseSourceById(Long purchaseSourceSid) {
        // 货源清单详情
        PurPurchaseSource purPurchaseSource = purPurchaseSourceMapper.selectPurPurchaseSourceById(purchaseSourceSid);
        if (purPurchaseSource == null) {
            return null;
        }
        // 采购货源供方SKU编码list
        PurMaterialSkuVencode purMaterialSkuVencode = new PurMaterialSkuVencode();
        purMaterialSkuVencode.setPurchaseSourceSid(purchaseSourceSid);
        List<PurMaterialSkuVencode> purMaterialSkuVencodeList = purMaterialSkuVencodeMapper.selectPurMaterialSkuVencodeList(
                purMaterialSkuVencode);
        purPurchaseSource.setPurMaterialSkuVencodeList(purMaterialSkuVencodeList);
        // 操作日志
        MongodbUtil.find(purPurchaseSource);
        return purPurchaseSource;
    }

    /**
     * 采购货源清单确认
     */

    public int confirm(PurPurchaseSource purPurchaseSource) {
        // 采购货源清单sids
        Long[] purchaseSourceSids = purPurchaseSource.getPurchaseSourceSids();
        PurPurchaseSource params = new PurPurchaseSource();
        params.setPurchaseSourceSids(purchaseSourceSids);
        params.setHandleStatus(HandleStatus.SAVE.getCode());
        int count = purPurchaseSourceMapper.countByDomain(params);
        if (count != purchaseSourceSids.length) {
            throw new BaseException("仅保存状态才允许确认");
        }
        purPurchaseSource.setConfirmerAccount(SecurityUtils.getUsername());
        purPurchaseSource.setConfirmDate(new Date());
        int confirm = purPurchaseSourceMapper.confirm(purPurchaseSource);

        if (confirm <= 0) {
            return confirm;
        }

        if (HandleStatus.isConfirmed(purPurchaseSource.getHandleStatus())) {
            // 删除待办
            for (Long sid : purchaseSourceSids) {
                deleteSysTodoTask(sid);
            }
        }
        for (Long id : purchaseSourceSids) {
            // 插入日志
            MongodbDeal.check(id, purPurchaseSource.getHandleStatus(), null, TITLE, null);
        }

        return confirm;
    }

    /**
     * 批量删除采购货源清单
     *
     * @param purchaseSourceSids 需要删除的采购货源清单ID
     * @return 结果
     */

    @Transactional(rollbackFor = Exception.class)
    public int deletePurPurchaseSourceByIds(Long[] purchaseSourceSids) {
        PurPurchaseSource params = new PurPurchaseSource();
        params.setPurchaseSourceSids(purchaseSourceSids);
        params.setHandleStatus(HandleStatus.SAVE.getCode());
        int count = purPurchaseSourceMapper.countByDomain(params);
        if (count != purchaseSourceSids.length) {
            throw new BaseException("仅保存状态才允许删除");
        }
        // 删除采购货源清单
        purPurchaseSourceMapper.deletePurPurchaseSourceByIds(purchaseSourceSids);
        // 删除采购货源供方SKU编码
        purMaterialSkuVencodeMapper.deletePurMaterialSkuVencodeByIds(purchaseSourceSids);

        for (Long sid : purchaseSourceSids) {
            deleteSysTodoTask(sid);
        }

        List<PurPurchaseSource> list = this.purPurchaseSourceMapper.selectList(
                new QueryWrapper<PurPurchaseSource>()
                        .lambda().in(PurPurchaseSource::getPurchaseSourceSid,
                                     (Object[]) purchaseSourceSids)
        );
        // 操作日志
        list.forEach(o -> {
            List<OperMsg> msgList = BeanUtils.eq(o, new PurPurchaseSource());
            MongodbUtil.insertUserLog(o.getPurchaseSourceSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
        });


        return purchaseSourceSids.length;
    }

    /**
     * 服务销售验收单变更
     */

    public int change(PurPurchaseSource purPurchaseSource) {
        Long purchaseSourceSid = purPurchaseSource.getPurchaseSourceSid();
        PurPurchaseSource purchaseSource = purPurchaseSourceMapper.selectPurPurchaseSourceById(purchaseSourceSid);
        // 验证是否确认状态
        if (!HandleStatus.CONFIRMED.getCode().equals(purchaseSource.getHandleStatus())) {
            throw new BaseException("仅确认状态才允许变更");
        }

        // 验证供应商编码和商品编码组合是否修改
        // noinspection DuplicatedCode
        if (!purPurchaseSource.getVendorSid().equals(purchaseSource.getVendorSid()) || !purPurchaseSource.getMaterialSid().equals(
                purchaseSource.getMaterialSid())) {
            // 验证供应商编码和商品编码组合是否重复
            checkVendorAndMaterialUnique(purPurchaseSource);
        }

        this.setHandleStatusInfoWhenUpdate(purPurchaseSource);

        // 删除公司时，删除code
        if (purPurchaseSource.getCompanySid() == null) {
            purPurchaseSource.setCompanyCode(null);
        }
        purPurchaseSourceMapper.updateAllById(purPurchaseSource);
        // 采购货源供方SKU编码list
        List<PurMaterialSkuVencode> purMaterialSkuVencodeList = purPurchaseSource.getPurMaterialSkuVencodeList();
        if (CollectionUtil.isNotEmpty(purMaterialSkuVencodeList)) {
            addPurMaterialSkuVencode(purPurchaseSource, purMaterialSkuVencodeList);
        }

        doAfterUpdateEntity(purPurchaseSource);
        // 插入日志
        List<OperMsg> msgList;
        PurPurchaseSource response = this.purPurchaseSourceMapper.selectPurPurchaseSourceById(purPurchaseSource.getPurchaseSourceSid());
        msgList = BeanUtils.eq(response, purPurchaseSource);
        MongodbUtil.insertUserLog(purPurchaseSource.getPurchaseSourceSid(),
                                  BusinessType.CHANGE.getValue(),
                                  msgList,
                                  TITLE);

        return 1;
    }

    /**
     * 批量启用/停用物料&商品&服务档案
     */

    public int status(PurPurchaseSource purPurchaseSource) {
        // 物料&商品&服务档案sids
        Long[] purchaseSourceSids = purPurchaseSource.getPurchaseSourceSids();
        // 启用
        if (Status.ENABLE.getCode().equals(purPurchaseSource.getStatus())) {
            PurPurchaseSource params = new PurPurchaseSource();
            params.setPurchaseSourceSids(purchaseSourceSids);
            params.setHandleStatus(HandleStatus.CONFIRMED.getCode());
            int count = purPurchaseSourceMapper.countByDomain(params);
            if (count != purchaseSourceSids.length) {
                throw new BaseException("仅确认状态才允许启用");
            }
        }
        int confirm = purPurchaseSourceMapper.confirm(purPurchaseSource);
        if (confirm <= 0) {
            return confirm;
        }

        for (Long id : purchaseSourceSids) {
            // 插入日志
            MongodbDeal.status(id, purPurchaseSource.getStatus(), null, TITLE, null);
        }
        return confirm;
    }

    private void deleteSysTodoTask(Long sid) {
        mapper.delete(
                Wrappers.lambdaQuery(SysTodoTask.class)
                        .in(SysTodoTask::getDocumentSid,
                            sid)
                        .eq(SysTodoTask::getTaskCategory,
                            ConstantsEms.TODO_TASK_DB)
                        .eq(SysTodoTask::getTableName,
                            EmsDbTable.s_pur_purchase_source)
        );

    }

    private void addSysTodoTask(PurPurchaseSource pps) {
        if (ObjectUtil.hasEmpty(pps.getPurchaseSourceSid(), pps.getSourceInfoId())) {
            throw new CheckedException("货源信息发送待办失败，存在空字段: " +
                                               pps.getPurchaseSourceSid() +
                                               ":" +
                                               pps.getSourceInfoId()
            );
        }

        mapper.insert(
                new SysTodoTask()
                        .setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(EmsDbTable.s_pur_purchase_source)
                        .setDocumentSid(pps.getPurchaseSourceSid())
                        .setTitle("货源信息记录号" + pps.getSourceInfoId() + "当前是保存状态，请及时处理！")
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid())
        );
    }

    /**
     * 处理默认
     *
     * @param purPurchaseSource
     */
    private void doAfterUpdateEntity(PurPurchaseSource purPurchaseSource) {
        this.updateIsDefaultForAll(purPurchaseSource);
        if (HandleStatus.isConfirmed(purPurchaseSource.getHandleStatus())) {
            // 删除待办
            deleteSysTodoTask(purPurchaseSource.getPurchaseSourceSid());
        }
    }


    private void updateIsDefaultForAll(PurPurchaseSource pps) {
        LambdaQueryWrapper<PurPurchaseSource> wrapper = Wrappers.lambdaQuery();
        wrapper.ne(PurPurchaseSource::getPurchaseSourceSid,
                   pps.getPurchaseSourceSid())
               .eq(PurPurchaseSource::getMaterialSid,
                   pps.getMaterialSid());
        this.purPurchaseSourceMapper.update(new PurPurchaseSource().setIsDefault("N"), wrapper);
    }


    public void setDefaultBatch(PurPurchaseSource purPurchaseSource) {
        // 1. 拿到实体类数组
        List<PurPurchaseSource> list = purPurchaseSource.getPurPurchaseSourceList();
        // 2. 根据id，把这些状态设置为Y

        Long[] sidArray = list.stream().map(PurPurchaseSource::getPurchaseSourceSid).toArray(Long[]::new);
        this.purPurchaseSourceMapper.update(new PurPurchaseSource().setIsDefault("Y"),
                                            new LambdaQueryWrapper<PurPurchaseSource>().in(
                                                    PurPurchaseSource::getPurchaseSourceSid,
                                                    (Object[]) sidArray
                                            ));
        // 3. 遍历，进行 update
        list.forEach(this::updateIsDefaultForAll);
    }


}
