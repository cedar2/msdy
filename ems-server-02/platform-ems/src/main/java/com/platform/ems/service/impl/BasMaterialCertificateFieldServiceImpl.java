package com.platform.ems.service.impl;

import java.util.Date;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.BasMaterialCertificateFieldMapper;
import com.platform.ems.domain.BasMaterialCertificateField;
import com.platform.ems.service.IBasMaterialCertificateFieldService;

/**
 * 商品合格证洗唛自定义字段Service业务层处理
 *
 * @author linhongwei
 * @date 2021-03-31
 */
@Service
@SuppressWarnings("all")
public class BasMaterialCertificateFieldServiceImpl extends ServiceImpl<BasMaterialCertificateFieldMapper,BasMaterialCertificateField>  implements IBasMaterialCertificateFieldService {
    @Autowired
    private BasMaterialCertificateFieldMapper basMaterialCertificateFieldMapper;

    private static final String TITLE = "商品合格证洗唛自定义字段";

    /**
     * 查询商品合格证洗唛自定义字段
     *
     * @param materialCertificateFieldSid 商品合格证洗唛自定义字段ID
     * @return 商品合格证洗唛自定义字段
     */
    @Override
    public BasMaterialCertificateField selectBasMaterialCertificateFieldById(Long materialCertificateFieldSid) {
        return basMaterialCertificateFieldMapper.selectBasMaterialCertificateFieldById(materialCertificateFieldSid);
    }

    /**
     * 查询商品合格证洗唛自定义字段列表
     *
     * @param basMaterialCertificateField 商品合格证洗唛自定义字段
     * @return 商品合格证洗唛自定义字段
     */
    @Override
    public List<BasMaterialCertificateField> selectBasMaterialCertificateFieldList(BasMaterialCertificateField field) {
        return basMaterialCertificateFieldMapper.selectBasMaterialCertificateFieldList(field);
    }

    /**
     * 校验重复
     * @param field
     */
    private void judgeRepeat(BasMaterialCertificateField field) {
        QueryWrapper<BasMaterialCertificateField> queryWrapper = new QueryWrapper<>();
        if (field.getMaterialCertificateFieldSid() != null) {
            queryWrapper.lambda().ne(BasMaterialCertificateField::getMaterialCertificateFieldSid,
                    field.getMaterialCertificateFieldSid());
        }
        queryWrapper.lambda().eq(BasMaterialCertificateField::getFieldName,
                field.getFieldName());
        List<BasMaterialCertificateField> list = basMaterialCertificateFieldMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            throw new BaseException("字段名已存在！");
        }
    }

    /**
     * 新增商品合格证洗唛自定义字段
     * 需要注意编码重复校验
     * @param field 商品合格证洗唛自定义字段
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasMaterialCertificateField(BasMaterialCertificateField field) {
        judgeRepeat(field);
        // 确认人
        if (ConstantsEms.CHECK_STATUS.equals(field.getHandleStatus())) {
            field.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        return basMaterialCertificateFieldMapper.insert(field);
    }

    /**
     * 修改商品合格证洗唛自定义字段
     *
     * @param field 商品合格证洗唛自定义字段
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasMaterialCertificateField(BasMaterialCertificateField field) {
        judgeRepeat(field);
        BasMaterialCertificateField old = basMaterialCertificateFieldMapper.selectById(field.getMaterialCertificateFieldSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(old, field);
        if (CollectionUtil.isNotEmpty(msgList)) {
            field.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 确认人
        if (!ConstantsEms.CHECK_STATUS.equals(old.getHandleStatus()) && ConstantsEms.CHECK_STATUS.equals(field.getHandleStatus())) {
            field.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        return basMaterialCertificateFieldMapper.updateAllById(field);
    }

    /**
     * 批量删除商品合格证洗唛自定义字段
     *
     * @param materialCertificateFieldSids 需要删除的商品合格证洗唛自定义字段ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasMaterialCertificateFieldByIds(List<Long> materialCertificateFieldSids) {
        return basMaterialCertificateFieldMapper.deleteBatchIds(materialCertificateFieldSids);
    }

    /**
     * 更改确认状态
     * @param field
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int status(BasMaterialCertificateField field) {
        int row = 0;
        Long[] sids = field.getMaterialCertificateFieldSidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<BasMaterialCertificateField> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(BasMaterialCertificateField::getMaterialCertificateFieldSid, sids);
            updateWrapper.set(BasMaterialCertificateField::getStatus, field.getStatus());
            row = basMaterialCertificateFieldMapper.update(null, updateWrapper);
        }
        return row;
    }

    /**
     * 更改确认状态
     * @param basMaterialCertificateField
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(BasMaterialCertificateField basMaterialCertificateField) {
        int row = 0;
        Long[] sids = basMaterialCertificateField.getMaterialCertificateFieldSidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<BasMaterialCertificateField> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(BasMaterialCertificateField::getMaterialCertificateFieldSid, sids);
            updateWrapper.set(BasMaterialCertificateField::getHandleStatus, basMaterialCertificateField.getHandleStatus());
            // 确认人
            if (ConstantsEms.CHECK_STATUS.equals(basMaterialCertificateField.getHandleStatus())) {
                updateWrapper.set(BasMaterialCertificateField::getConfirmDate, new Date());
                updateWrapper.set(BasMaterialCertificateField::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            row = basMaterialCertificateFieldMapper.update(null, updateWrapper);
        }
        return row;
    }

}
