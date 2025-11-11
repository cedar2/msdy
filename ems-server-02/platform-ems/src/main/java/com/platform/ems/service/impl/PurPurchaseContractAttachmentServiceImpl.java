package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.PurPurchaseContract;
import com.platform.ems.domain.SalSaleContract;
import com.platform.ems.domain.SalSaleContractAttachment;
import com.platform.ems.mapper.PurPurchaseContractMapper;
import com.platform.ems.plug.domain.ConFileType;
import com.platform.ems.plug.mapper.ConFileTypeMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.PurPurchaseContractAttachmentMapper;
import com.platform.ems.domain.PurPurchaseContractAttachment;
import com.platform.ems.service.IPurPurchaseContractAttachmentService;

/**
 * 采购合同信息-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@Service
@SuppressWarnings("all")
public class PurPurchaseContractAttachmentServiceImpl extends ServiceImpl<PurPurchaseContractAttachmentMapper, PurPurchaseContractAttachment> implements IPurPurchaseContractAttachmentService {
    @Autowired
    private PurPurchaseContractAttachmentMapper purPurchaseContractAttachmentMapper;
    @Autowired
    private PurPurchaseContractMapper purPurchaseContractMapper;
    @Autowired
    private ConFileTypeMapper conFileTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "采购合同信息-附件";

    /**
     * 查询采购合同信息-附件
     *
     * @param purchaseContractAttachmentSid 采购合同信息-附件ID
     * @return 采购合同信息-附件
     */
     @Override
     public PurPurchaseContractAttachment selectPurPurchaseContractAttachmentById(Long purchaseContractAttachmentSid) {
     PurPurchaseContractAttachment purPurchaseContractAttachment = purPurchaseContractAttachmentMapper.selectPurPurchaseContractAttachmentById(purchaseContractAttachmentSid);
     MongodbUtil.find(purPurchaseContractAttachment);
     return purPurchaseContractAttachment;
     }

    /**
     * 查询采购合同信息-附件列表
     *
     * @param purPurchaseContractAttachment 采购合同信息-附件
     * @return 采购合同信息-附件
     */
    @Override
    public List<PurPurchaseContractAttachment> selectPurPurchaseContractAttachmentList(PurPurchaseContractAttachment purPurchaseContractAttachment) {
        return purPurchaseContractAttachmentMapper.selectPurPurchaseContractAttachmentList(purPurchaseContractAttachment);
    }

    /**
     * 新增采购合同信息-附件
     * 需要注意编码重复校验
     *
     * @param purPurchaseContractAttachment 采购合同信息-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurPurchaseContractAttachment(PurPurchaseContractAttachment purPurchaseContractAttachment) {
        if (StrUtil.isNotBlank(purPurchaseContractAttachment.getFileType())){
            ConFileType conFileType = conFileTypeMapper.selectOne(new QueryWrapper<ConFileType>().lambda().eq(ConFileType::getCode,purPurchaseContractAttachment.getFileType())
                    .eq(ConFileType::getDataobjectCategoryCode, ConstantsEms.DATA_OBJECT_CONTRACT_P));
            if (conFileType != null && ConstantsEms.NO.equals(conFileType.getIsUploadMultifile())){
                purPurchaseContractAttachmentMapper.delete(new QueryWrapper<PurPurchaseContractAttachment>().lambda()
                        .eq(PurPurchaseContractAttachment::getPurchaseContractSid,purPurchaseContractAttachment.getPurchaseContractSid())
                        .eq(PurPurchaseContractAttachment::getFileType,purPurchaseContractAttachment.getFileType()));
            }
            if (ConstantsEms.FILE_TYPE_CGHT.equals(purPurchaseContractAttachment.getFileType())){
                LambdaUpdateWrapper<PurPurchaseContract> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(PurPurchaseContract::getPurchaseContractSid,purPurchaseContractAttachment.getPurchaseContractSid())
                        .set(PurPurchaseContract::getUploadStatus, ConstantsEms.CONTRACT_UPLOAD_STATUS_Y);
                purPurchaseContractMapper.update(null, updateWrapper);
            }
        }
        int row = purPurchaseContractAttachmentMapper.insert(purPurchaseContractAttachment);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(purPurchaseContractAttachment.getPurchaseContractAttachmentSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改采购合同信息-附件
     *
     * @param purPurchaseContractAttachment 采购合同信息-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurPurchaseContractAttachment(PurPurchaseContractAttachment purPurchaseContractAttachment) {
        PurPurchaseContractAttachment response = purPurchaseContractAttachmentMapper.selectPurPurchaseContractAttachmentById(purPurchaseContractAttachment.getPurchaseContractAttachmentSid());
        int row = purPurchaseContractAttachmentMapper.updateById(purPurchaseContractAttachment);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(purPurchaseContractAttachment.getPurchaseContractAttachmentSid(), BusinessType.UPDATE.ordinal(), response, purPurchaseContractAttachment, TITLE);
        }
        return row;
    }

    /**
     * 变更采购合同信息-附件
     *
     * @param purPurchaseContractAttachment 采购合同信息-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePurPurchaseContractAttachment(PurPurchaseContractAttachment purPurchaseContractAttachment) {
        PurPurchaseContractAttachment response = purPurchaseContractAttachmentMapper.selectPurPurchaseContractAttachmentById(purPurchaseContractAttachment.getPurchaseContractAttachmentSid());
        int row = purPurchaseContractAttachmentMapper.updateAllById(purPurchaseContractAttachment);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(purPurchaseContractAttachment.getPurchaseContractAttachmentSid(), BusinessType.CHANGE.ordinal(), response, purPurchaseContractAttachment, TITLE);
        }
        return row;
    }

    /**
     * 批量删除采购合同信息-附件
     *
     * @param purchaseContractAttachmentSids 需要删除的采购合同信息-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurPurchaseContractAttachmentByIds(List<Long> purchaseContractAttachmentSids) {
        return purPurchaseContractAttachmentMapper.deleteBatchIds(purchaseContractAttachmentSids);
    }

    /**
     * 上传附件前校验
     *
     * @param purPurchaseContractAttachment
     * @return
     */
    @Override
    public AjaxResult check(PurPurchaseContractAttachment purPurchaseContractAttachment) {
        if (purPurchaseContractAttachment.getPurchaseContractSid() == null){
            throw new BaseException("请先选择采购合同！");
        }
        if (StrUtil.isBlank(purPurchaseContractAttachment.getFileType())){
            return AjaxResult.success(true);
        }else {
            ConFileType conFileType = conFileTypeMapper.selectOne(new QueryWrapper<ConFileType>().lambda().eq(ConFileType::getCode,purPurchaseContractAttachment.getFileType())
                    .eq(ConFileType::getDataobjectCategoryCode, ConstantsEms.DATA_OBJECT_CONTRACT_P));
            if (conFileType != null && ConstantsEms.NO.equals(conFileType.getIsUploadMultifile())){
                List<PurPurchaseContractAttachment> list = purPurchaseContractAttachmentMapper.selectList(new QueryWrapper<PurPurchaseContractAttachment>().lambda()
                        .eq(PurPurchaseContractAttachment::getPurchaseContractSid,purPurchaseContractAttachment.getPurchaseContractSid())
                        .eq(PurPurchaseContractAttachment::getFileType,purPurchaseContractAttachment.getFileType()));
                if (CollectionUtils.isNotEmpty(list)){
                    return AjaxResult.success("已存在：" + conFileType.getName() + " 附件，是否进行覆盖?",false);
                }
            }
        }
        return AjaxResult.success(true);
    }


}
