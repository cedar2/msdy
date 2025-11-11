package com.platform.ems.plug.service.impl;

import java.util.List;
import java.util.ArrayList;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.plug.mapper.ConBuTypeAccountCategoryMapper;
import com.platform.ems.plug.domain.ConBuTypeAccountCategory;
import com.platform.ems.plug.service.IConBuTypeAccountCategoryService;

/**
 * 业务类型对应款项类别Service业务层处理
 *
 * @author chenkw
 * @date 2022-06-22
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeAccountCategoryServiceImpl extends ServiceImpl<ConBuTypeAccountCategoryMapper, ConBuTypeAccountCategory> implements IConBuTypeAccountCategoryService {
    @Autowired
    private ConBuTypeAccountCategoryMapper conBuTypeAccountCategoryMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String TITLE = "业务类型对应款项类别";

    /**
     * 查询业务类型对应款项类别
     *
     * @param sid 业务类型对应款项类别ID
     * @return 业务类型对应款项类别
     */
    @Override
    public ConBuTypeAccountCategory selectConBuTypeAccountCategoryById(Long sid) {
        ConBuTypeAccountCategory conBuTypeAccountCategory = conBuTypeAccountCategoryMapper.selectConBuTypeAccountCategoryById(sid);
        MongodbUtil.find(conBuTypeAccountCategory);
        return conBuTypeAccountCategory;
    }

    /**
     * 查询业务类型对应款项类别列表
     *
     * @param conBuTypeAccountCategory 业务类型对应款项类别
     * @return 业务类型对应款项类别
     */
    @Override
    public List<ConBuTypeAccountCategory> selectConBuTypeAccountCategoryList(ConBuTypeAccountCategory conBuTypeAccountCategory) {
        return conBuTypeAccountCategoryMapper.selectConBuTypeAccountCategoryList(conBuTypeAccountCategory);
    }

    /**
     * 唯一性校验
     */
    private void checkUnique(ConBuTypeAccountCategory conBuTypeAccountCategory){
        QueryWrapper<ConBuTypeAccountCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ConBuTypeAccountCategory::getFinShoufukuanTypeCode, conBuTypeAccountCategory.getFinShoufukuanTypeCode());
        queryWrapper.lambda().eq(ConBuTypeAccountCategory::getBuTypeCode, conBuTypeAccountCategory.getBuTypeCode());
        queryWrapper.lambda().eq(ConBuTypeAccountCategory::getAccountCategoryCode, conBuTypeAccountCategory.getAccountCategoryCode());
        if (StrUtil.isNotBlank(conBuTypeAccountCategory.getBookTypeCode())){
            queryWrapper.lambda().eq(ConBuTypeAccountCategory::getBookTypeCode, conBuTypeAccountCategory.getBookTypeCode());
        }else {
            queryWrapper.lambda().isNull(ConBuTypeAccountCategory::getBookTypeCode);
        }
        if (StrUtil.isNotBlank(conBuTypeAccountCategory.getBookSourceCategory())){
            queryWrapper.lambda().eq(ConBuTypeAccountCategory::getBookSourceCategory, conBuTypeAccountCategory.getBookSourceCategory());
        }else {
            queryWrapper.lambda().isNull(ConBuTypeAccountCategory::getBookSourceCategory);
        }
        if (conBuTypeAccountCategory.getSid() != null){
            queryWrapper.lambda().ne(ConBuTypeAccountCategory::getSid, conBuTypeAccountCategory.getSid());
        }
        List<ConBuTypeAccountCategory> categoryList = conBuTypeAccountCategoryMapper.selectList(queryWrapper);
        if (categoryList != null && categoryList.size() > 0){
            throw new BaseException("该组合已存在，请核实！");
        }
    }

    /**
     * 新增业务类型对应款项类别
     * 需要注意编码重复校验
     *
     * @param conBuTypeAccountCategory 业务类型对应款项类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeAccountCategory(ConBuTypeAccountCategory conBuTypeAccountCategory) {
        checkUnique(conBuTypeAccountCategory);
        int row = conBuTypeAccountCategoryMapper.insert(conBuTypeAccountCategory);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeAccountCategory.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型对应款项类别
     *
     * @param conBuTypeAccountCategory 业务类型对应款项类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeAccountCategory(ConBuTypeAccountCategory conBuTypeAccountCategory) {
        checkUnique(conBuTypeAccountCategory);
        ConBuTypeAccountCategory response = conBuTypeAccountCategoryMapper.selectConBuTypeAccountCategoryById(conBuTypeAccountCategory.getSid());
        int row = conBuTypeAccountCategoryMapper.updateById(conBuTypeAccountCategory);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeAccountCategory.getSid(), BusinessType.UPDATE.getValue(), response, conBuTypeAccountCategory, TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型对应款项类别
     *
     * @param conBuTypeAccountCategory 业务类型对应款项类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeAccountCategory(ConBuTypeAccountCategory conBuTypeAccountCategory) {
        checkUnique(conBuTypeAccountCategory);
        ConBuTypeAccountCategory response = conBuTypeAccountCategoryMapper.selectConBuTypeAccountCategoryById(conBuTypeAccountCategory.getSid());
        int row = conBuTypeAccountCategoryMapper.updateAllById(conBuTypeAccountCategory);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeAccountCategory.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeAccountCategory, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型对应款项类别
     *
     * @param sids 需要删除的业务类型对应款项类别ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeAccountCategoryByIds(List<Long> sids) {
        return conBuTypeAccountCategoryMapper.deleteBatchIds(sids);
    }

    /**
     * 更改确认状态
     *
     * @param conBuTypeAccountCategory
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ConBuTypeAccountCategory conBuTypeAccountCategory) {
        int row = 0;
        Long[] sids = conBuTypeAccountCategory.getSidList();
        if (sids != null && sids.length > 0) {
            row = conBuTypeAccountCategoryMapper.update(null, new UpdateWrapper<ConBuTypeAccountCategory>().lambda().set(ConBuTypeAccountCategory::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConBuTypeAccountCategory::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 获取下拉框接口
     *
     * @param conBuTypeAccountCategory
     * @return
     */
    @Override
    public List<ConBuTypeAccountCategory> getConBuTypeAccountCategoryList(ConBuTypeAccountCategory conBuTypeAccountCategory) {
        return conBuTypeAccountCategoryMapper.getConBuTypeAccountCategoryList(conBuTypeAccountCategory);
    }

    /**
     * 获取款项类别下拉框接口
     *
     * @param conBuTypeAccountCategory 业务类型对应款项类别
     * @return 业务类型对应款项类别集合
     */
    public List<ConBuTypeAccountCategory> getAccountCategoryList(ConBuTypeAccountCategory conBuTypeAccountCategory) {
        return conBuTypeAccountCategoryMapper.getAccountCategoryList(conBuTypeAccountCategory);
    }

    /**
     * 获取流水类型下拉框接口
     *
     * @param conBuTypeAccountCategory 业务类型对应款项类别
     * @return 业务类型对应款项类别集合
     */
    public List<ConBuTypeAccountCategory> getBookTypeList(ConBuTypeAccountCategory conBuTypeAccountCategory) {
        return conBuTypeAccountCategoryMapper.getBookTypeList(conBuTypeAccountCategory);
    }

}
