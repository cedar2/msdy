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
import com.platform.ems.mapper.SalSalesOrderAttachmentMapper;
import com.platform.ems.domain.SalSalesOrderAttachment;
import com.platform.ems.service.ISalSalesOrderAttachmentService;

/**
 * 销售订单-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-04-08
 */
@Service
@SuppressWarnings("all")
public class SalSalesOrderAttachmentServiceImpl extends ServiceImpl<SalSalesOrderAttachmentMapper, SalSalesOrderAttachment> implements ISalSalesOrderAttachmentService {
    @Autowired
    private SalSalesOrderAttachmentMapper salSalesOrderAttachmentMapper;
    @Autowired
    private ConFileTypeMapper conFileTypeMapper;

    /**
     * 查询销售订单-附件
     *
     * @param salesOrderAttachmentSid 销售订单-附件ID
     * @return 销售订单-附件
     */
    @Override
    public SalSalesOrderAttachment selectSalSalesOrderAttachmentById(Long salesOrderAttachmentSid) {
        return salSalesOrderAttachmentMapper.selectSalSalesOrderAttachmentById(salesOrderAttachmentSid);
    }

    /**
     * 查询销售订单-附件列表
     *
     * @param salSalesOrderAttachment 销售订单-附件
     * @return 销售订单-附件
     */
    @Override
    public List<SalSalesOrderAttachment> selectSalSalesOrderAttachmentList(SalSalesOrderAttachment salSalesOrderAttachment) {
        return salSalesOrderAttachmentMapper.selectSalSalesOrderAttachmentList(salSalesOrderAttachment);
    }

    /**
     * 新增销售订单-附件
     * 需要注意编码重复校验
     *
     * @param salSalesOrderAttachment 销售订单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSalSalesOrderAttachment(SalSalesOrderAttachment salSalesOrderAttachment) {
        return salSalesOrderAttachmentMapper.insert(salSalesOrderAttachment);
    }

    /**
     * 修改销售订单-附件
     *
     * @param salSalesOrderAttachment 销售订单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSalSalesOrderAttachment(SalSalesOrderAttachment salSalesOrderAttachment) {
        return salSalesOrderAttachmentMapper.updateById(salSalesOrderAttachment);
    }

    /**
     * 批量删除销售订单-附件
     *
     * @param salesOrderAttachmentSids 需要删除的销售订单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalSalesOrderAttachmentByIds(List<Long> salesOrderAttachmentSids) {
        return salSalesOrderAttachmentMapper.deleteBatchIds(salesOrderAttachmentSids);
    }

    /**
     * 上传附件前校验
     *
     * @param salSalesOrderAttachment
     * @return
     */
    @Override
    public AjaxResult check(SalSalesOrderAttachment salSalesOrderAttachment) {
        if (salSalesOrderAttachment.getSalesOrderSid() == null) {
            throw new BaseException("请先选择销售订单！");
        }
        if (StrUtil.isBlank(salSalesOrderAttachment.getFileType())) {
            return AjaxResult.success(true);
        } else {
            ConFileType conFileType = conFileTypeMapper.selectOne(new QueryWrapper<ConFileType>().lambda().eq(ConFileType::getCode, salSalesOrderAttachment.getFileType())
                    .eq(ConFileType::getDataobjectCategoryCode, ConstantsEms.DATA_OBJECT_CONTRACT_S));
            if (conFileType != null && ConstantsEms.NO.equals(conFileType.getIsUploadMultifile())) {
                List<SalSalesOrderAttachment> list = salSalesOrderAttachmentMapper.selectList(new QueryWrapper<SalSalesOrderAttachment>().lambda()
                        .eq(SalSalesOrderAttachment::getSalesOrderSid, salSalesOrderAttachment.getSalesOrderSid())
                        .eq(SalSalesOrderAttachment::getFileType, salSalesOrderAttachment.getFileType()));
                if (CollectionUtils.isNotEmpty(list)) {
                    return AjaxResult.success("已存在：" + conFileType.getName() + " 附件，是否进行覆盖?", false);
                }
            }
        }
        return AjaxResult.success(true);
    }
}
