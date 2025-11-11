package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.BasMaterialAttachment;
import com.platform.ems.domain.SalSaleContract;
import com.platform.ems.mapper.SalSaleContractMapper;
import com.platform.ems.plug.domain.ConFileType;
import com.platform.ems.plug.mapper.ConFileTypeMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.SalSaleContractAttachmentMapper;
import com.platform.ems.domain.SalSaleContractAttachment;
import com.platform.ems.service.ISalSaleContractAttachmentService;

/**
 * 销售合同信息-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-18
 */
@Service
@SuppressWarnings("all")
public class SalSaleContractAttachmentServiceImpl extends ServiceImpl<SalSaleContractAttachmentMapper,SalSaleContractAttachment>  implements ISalSaleContractAttachmentService {
    @Autowired
    private SalSaleContractAttachmentMapper salSaleContractAttachmentMapper;
    @Autowired
    private SalSaleContractMapper salSaleContractMapper;
    @Autowired
    private ConFileTypeMapper conFileTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "销售合同信息-附件";
    /**
     * 查询销售合同信息-附件
     *
     * @param saleContractAttachmentSid 销售合同信息-附件ID
     * @return 销售合同信息-附件
     */
    @Override
    public SalSaleContractAttachment selectSalSaleContractAttachmentById(Long saleContractAttachmentSid) {
        SalSaleContractAttachment salSaleContractAttachment = salSaleContractAttachmentMapper.selectSalSaleContractAttachmentById(saleContractAttachmentSid);
        MongodbUtil.find(salSaleContractAttachment);
        return  salSaleContractAttachment;
    }

    /**
     * 查询销售合同信息-附件列表
     *
     * @param salSaleContractAttachment 销售合同信息-附件
     * @return 销售合同信息-附件
     */
    @Override
    public List<SalSaleContractAttachment> selectSalSaleContractAttachmentList(SalSaleContractAttachment salSaleContractAttachment) {
        return salSaleContractAttachmentMapper.selectSalSaleContractAttachmentList(salSaleContractAttachment);
    }

    /**
     * 新增销售合同信息-附件
     * 需要注意编码重复校验
     * @param salSaleContractAttachment 销售合同信息-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSalSaleContractAttachment(SalSaleContractAttachment salSaleContractAttachment) {
        if (StrUtil.isNotBlank(salSaleContractAttachment.getFileType())){
            ConFileType conFileType = conFileTypeMapper.selectOne(new QueryWrapper<ConFileType>().lambda().eq(ConFileType::getCode,salSaleContractAttachment.getFileType())
                    .eq(ConFileType::getDataobjectCategoryCode, ConstantsEms.DATA_OBJECT_CONTRACT_S));
            if (conFileType != null && ConstantsEms.NO.equals(conFileType.getIsUploadMultifile())){
                salSaleContractAttachmentMapper.delete(new QueryWrapper<SalSaleContractAttachment>().lambda()
                        .eq(SalSaleContractAttachment::getSaleContractSid,salSaleContractAttachment.getSaleContractSid())
                        .eq(SalSaleContractAttachment::getFileType,salSaleContractAttachment.getFileType()));
            }
            if (ConstantsEms.FILE_TYPE_XSHT.equals(salSaleContractAttachment.getFileType())){
                LambdaUpdateWrapper<SalSaleContract> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(SalSaleContract::getSaleContractSid,salSaleContractAttachment.getSaleContractSid())
                        .set(SalSaleContract::getUploadStatus, ConstantsEms.CONTRACT_UPLOAD_STATUS_Y);
                salSaleContractMapper.update(null, updateWrapper);
            }
        }
        int row= salSaleContractAttachmentMapper.insert(salSaleContractAttachment);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(salSaleContractAttachment.getSaleContractAttachmentSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改销售合同信息-附件
     *
     * @param salSaleContractAttachment 销售合同信息-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSalSaleContractAttachment(SalSaleContractAttachment salSaleContractAttachment) {
        SalSaleContractAttachment response = salSaleContractAttachmentMapper.selectSalSaleContractAttachmentById(salSaleContractAttachment.getSaleContractAttachmentSid());
        int row=salSaleContractAttachmentMapper.updateById(salSaleContractAttachment);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(salSaleContractAttachment.getSaleContractAttachmentSid(), BusinessType.UPDATE.ordinal(), response,salSaleContractAttachment,TITLE);
        }
        return row;
    }

    /**
     * 变更销售合同信息-附件
     *
     * @param salSaleContractAttachment 销售合同信息-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSalSaleContractAttachment(SalSaleContractAttachment salSaleContractAttachment) {
        SalSaleContractAttachment response = salSaleContractAttachmentMapper.selectSalSaleContractAttachmentById(salSaleContractAttachment.getSaleContractAttachmentSid());
                                                        int row=salSaleContractAttachmentMapper.updateAllById(salSaleContractAttachment);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(salSaleContractAttachment.getSaleContractAttachmentSid(), BusinessType.CHANGE.ordinal(), response,salSaleContractAttachment,TITLE);
        }
        return row;
    }

    /**
     * 批量删除销售合同信息-附件
     *
     * @param saleContractAttachmentSids 需要删除的销售合同信息-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalSaleContractAttachmentByIds(List<Long> saleContractAttachmentSids) {
        return salSaleContractAttachmentMapper.deleteBatchIds(saleContractAttachmentSids);
    }

    /**
     * 上传附件前校验
     *
     * @param purPurchaseContractAttachment
     * @return
     */
    @Override
    public AjaxResult check(SalSaleContractAttachment salSaleContractAttachment) {
        if (salSaleContractAttachment.getSaleContractSid() == null){
            throw new BaseException("请先选择销售合同！");
        }
        if (StrUtil.isBlank(salSaleContractAttachment.getFileType())){
            return AjaxResult.success(true);
        }else {
            ConFileType conFileType = conFileTypeMapper.selectOne(new QueryWrapper<ConFileType>().lambda().eq(ConFileType::getCode,salSaleContractAttachment.getFileType())
                    .eq(ConFileType::getDataobjectCategoryCode, ConstantsEms.DATA_OBJECT_CONTRACT_S));
            if (conFileType != null && ConstantsEms.NO.equals(conFileType.getIsUploadMultifile())){
                List<SalSaleContractAttachment> list = salSaleContractAttachmentMapper.selectList(new QueryWrapper<SalSaleContractAttachment>().lambda()
                        .eq(SalSaleContractAttachment::getSaleContractSid,salSaleContractAttachment.getSaleContractSid())
                        .eq(SalSaleContractAttachment::getFileType,salSaleContractAttachment.getFileType()));
                if (CollectionUtils.isNotEmpty(list)){
                    return AjaxResult.success("已存在：" + conFileType.getName() + " 附件，是否进行覆盖?",false);
                }
            }
        }
        return AjaxResult.success(true);
    }


}
