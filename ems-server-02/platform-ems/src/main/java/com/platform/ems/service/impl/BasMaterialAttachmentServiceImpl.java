package com.platform.ems.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.platform.common.exception.base.BaseException;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.domain.BasMaterial;
import com.platform.ems.domain.BasMaterialAttachment;
import com.platform.ems.domain.FinPurchaseInvoice;
import com.platform.ems.mapper.BasMaterialAttachmentMapper;
import com.platform.ems.mapper.BasMaterialMapper;
import com.platform.ems.plug.domain.ConFileType;
import com.platform.ems.plug.mapper.ConFileTypeMapper;
import com.platform.ems.plug.service.IConFileTypeService;
import com.platform.ems.service.IBasMaterialAttachmentService;
import com.platform.ems.service.IBasMaterialService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 物料&商品-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-03-12
 */
@Service
public class BasMaterialAttachmentServiceImpl implements IBasMaterialAttachmentService {
    @Autowired
    private BasMaterialAttachmentMapper basMaterialAttachmentMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private ConFileTypeMapper conFileTypeMapper;
    @Autowired
    private IBasMaterialService basMaterialService;
    /**
     * 查询物料&商品-附件
     *
     * @param materialAttachmentSid 物料&商品-附件ID
     * @return 物料&商品-附件
     */
    @Override
    public BasMaterialAttachment selectBasMaterialAttachmentById(Long materialAttachmentSid) {
        return basMaterialAttachmentMapper.selectBasMaterialAttachmentById(materialAttachmentSid);
    }

    /**
     * 查询物料&商品-附件列表
     *
     * @param basMaterialAttachment 物料&商品-附件
     * @return 物料&商品-附件
     */
    @Override
    public List<BasMaterialAttachment> selectBasMaterialAttachmentList(BasMaterialAttachment basMaterialAttachment) {
        return basMaterialAttachmentMapper.selectBasMaterialAttachmentList(basMaterialAttachment);
    }

    @Override
    public AjaxResult check(BasMaterialAttachment basMaterialAttachment){
        if (basMaterialAttachment.getMaterialSid() == null){
            throw new BaseException("请先选择商品档案！");
        }
        if (StrUtil.isBlank(basMaterialAttachment.getFileType())){
            return AjaxResult.success(true);
        }else {
            ConFileType conFileType = conFileTypeMapper.selectOne(new QueryWrapper<ConFileType>().lambda().eq(ConFileType::getCode,basMaterialAttachment.getFileType())
                    .eq(ConFileType::getDataobjectCategoryCode, ConstantsEms.DATA_OBJECT_MATERIAL));
/*            if (ConstantsEms.FILE_TYPE_SPEC.equals(basMaterialAttachment.getFileType())){
                BasMaterial basMaterial = basMaterialMapper.selectOne(new QueryWrapper<BasMaterial>().lambda().eq(BasMaterial::getMaterialSid,basMaterialAttachment.getMaterialSid()));
                if (ConstantsEms.NO.equals(basMaterial.getIsUploadZhizaodan())){
                    throw new BaseException("该 " + basMaterial.getMaterialName() + " 档案，不允许上传 " + conFileType.getName());
                }
            }*/
            if (conFileType != null && ConstantsEms.NO.equals(conFileType.getIsUploadMultifile())){
                List<BasMaterialAttachment> list = basMaterialAttachmentMapper.selectList(new QueryWrapper<BasMaterialAttachment>().lambda()
                        .eq(BasMaterialAttachment::getMaterialSid,basMaterialAttachment.getMaterialSid())
                        .eq(BasMaterialAttachment::getFileType,basMaterialAttachment.getFileType()));
                if (CollectionUtils.isNotEmpty(list)){
                    return AjaxResult.success("已存在：" + conFileType.getName() + " 附件，是否进行覆盖?",false);
                }
            }
        }
        return AjaxResult.success(true);
    }

    /**
     * 新增物料&商品-附件
     *
     * @param basMaterialAttachment 物料&商品-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasMaterialAttachment(BasMaterialAttachment basMaterialAttachment) {
        int row = 0;
        if (StrUtil.isNotBlank(basMaterialAttachment.getFileType())){
            ConFileType conFileType = conFileTypeMapper.selectOne(new QueryWrapper<ConFileType>().lambda().eq(ConFileType::getCode,basMaterialAttachment.getFileType())
                    .eq(ConFileType::getDataobjectCategoryCode, ConstantsEms.DATA_OBJECT_MATERIAL));
            if (conFileType != null && ConstantsEms.NO.equals(conFileType.getIsUploadMultifile())){
                basMaterialAttachmentMapper.delete(new QueryWrapper<BasMaterialAttachment>().lambda()
                        .eq(BasMaterialAttachment::getMaterialSid,basMaterialAttachment.getMaterialSid())
                        .eq(BasMaterialAttachment::getFileType,basMaterialAttachment.getFileType()));
            }
            if (ConstantsEms.FILE_TYPE_SPEC.equals(basMaterialAttachment.getFileType())){
                LambdaUpdateWrapper<BasMaterial> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(BasMaterial::getMaterialSid,basMaterialAttachment.getMaterialSid())
                        .set(BasMaterial::getIsHasUploadedZhizaodan, ConstantsEms.YES)
                        .set(BasMaterial::getZhizaodanUploadDate,new Date());
                basMaterialMapper.update(null, updateWrapper);
                BasMaterial basMaterial = basMaterialService.selectBasMaterialById(basMaterialAttachment.getMaterialSid());
                basMaterial.getAttachmentList().add(basMaterialAttachment);
                basMaterialService.sent(basMaterial);
            }
        }
        row = basMaterialAttachmentMapper.insert(basMaterialAttachment);
        return row;
    }

    /**
     * 修改物料&商品-附件
     *
     * @param basMaterialAttachment 物料&商品-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasMaterialAttachment(BasMaterialAttachment basMaterialAttachment) {
        return basMaterialAttachmentMapper.updateById(basMaterialAttachment);
    }

    /**
     * 批量删除物料&商品-附件
     *
     * @param idList 需要删除的物料&商品-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasMaterialAttachmentByIds(List<Long> idList) {
        return basMaterialAttachmentMapper.deleteBatchIds(idList);
    }
}
