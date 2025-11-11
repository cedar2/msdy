package com.platform.ems.service.impl;

import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConFileType;
import com.platform.ems.plug.mapper.ConFileTypeMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.PurPurchaseOrderAttachmentMapper;
import com.platform.ems.domain.PurPurchaseOrderAttachment;
import com.platform.ems.service.IPurPurchaseOrderAttachmentService;

/**
 * 采购订单-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-08
 */
@Service
@SuppressWarnings("all")
public class PurPurchaseOrderAttachmentServiceImpl extends ServiceImpl<PurPurchaseOrderAttachmentMapper,PurPurchaseOrderAttachment>  implements IPurPurchaseOrderAttachmentService {
    @Autowired
    private PurPurchaseOrderAttachmentMapper purPurchaseOrderAttachmentMapper;
    @Autowired
    private ConFileTypeMapper conFileTypeMapper;

    /**
     * 查询采购订单-附件
     *
     * @param purchaseOrderAttachmentSid 采购订单-附件ID
     * @return 采购订单-附件
     */
    @Override
    public PurPurchaseOrderAttachment selectPurPurchaseOrderAttachmentById(Long purchaseOrderAttachmentSid) {
        return purPurchaseOrderAttachmentMapper.selectPurPurchaseOrderAttachmentById(purchaseOrderAttachmentSid);
    }

    /**
     * 查询采购订单-附件列表
     *
     * @param purPurchaseOrderAttachment 采购订单-附件
     * @return 采购订单-附件
     */
    @Override
    public List<PurPurchaseOrderAttachment> selectPurPurchaseOrderAttachmentList(PurPurchaseOrderAttachment purPurchaseOrderAttachment) {
        return purPurchaseOrderAttachmentMapper.selectPurPurchaseOrderAttachmentList(purPurchaseOrderAttachment);
    }

    /**
     * 新增采购订单-附件
     * 需要注意编码重复校验
     * @param purPurchaseOrderAttachment 采购订单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurPurchaseOrderAttachment(PurPurchaseOrderAttachment purPurchaseOrderAttachment) {
        return purPurchaseOrderAttachmentMapper.insert(purPurchaseOrderAttachment);
    }

    /**
     * 修改采购订单-附件
     *
     * @param purPurchaseOrderAttachment 采购订单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurPurchaseOrderAttachment(PurPurchaseOrderAttachment purPurchaseOrderAttachment) {
        return purPurchaseOrderAttachmentMapper.updateById(purPurchaseOrderAttachment);
    }

    /**
     * 批量删除采购订单-附件
     *
     * @param purchaseOrderAttachmentSids 需要删除的采购订单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurPurchaseOrderAttachmentByIds(List<Long> purchaseOrderAttachmentSids) {
        return purPurchaseOrderAttachmentMapper.deleteBatchIds(purchaseOrderAttachmentSids);
    }

    /**
     * 上传附件前校验
     *
     * @param salSalesOrderAttachment
     * @return
     */
    @Override
    public AjaxResult check(PurPurchaseOrderAttachment purPurchaseOrderAttachment) {
        if (purPurchaseOrderAttachment.getPurchaseOrderSid() == null) {
            throw new BaseException("请先选择采购订单！");
        }
        if (StrUtil.isBlank(purPurchaseOrderAttachment.getFileType())) {
            return AjaxResult.success(true);
        } else {
            ConFileType conFileType = conFileTypeMapper.selectOne(new QueryWrapper<ConFileType>().lambda().eq(ConFileType::getCode, purPurchaseOrderAttachment.getFileType())
                    .eq(ConFileType::getDataobjectCategoryCode, ConstantsEms.DATA_OBJECT_CONTRACT_P));
            if (conFileType != null && ConstantsEms.NO.equals(conFileType.getIsUploadMultifile())) {
                List<PurPurchaseOrderAttachment> list = purPurchaseOrderAttachmentMapper.selectList(new QueryWrapper<PurPurchaseOrderAttachment>().lambda()
                        .eq(PurPurchaseOrderAttachment::getPurchaseOrderSid, purPurchaseOrderAttachment.getPurchaseOrderSid())
                        .eq(PurPurchaseOrderAttachment::getFileType, purPurchaseOrderAttachment.getFileType()));
                if (CollectionUtils.isNotEmpty(list)) {
                    return AjaxResult.success("已存在：" + conFileType.getName() + " 附件，是否进行覆盖?", false);
                }
            }
        }
        return AjaxResult.success(true);
    }
}
