package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConDataobjectCategory;
import com.platform.ems.plug.domain.ConDataobjectCodeRule;
import com.platform.ems.plug.mapper.ConDataobjectCategoryMapper;
import com.platform.ems.plug.mapper.ConDataobjectCodeRuleMapper;
import com.platform.ems.plug.service.IConDataobjectCategoryService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数据对象类别Service业务层处理
 *
 * @author c
 * @date 2021-09-06
 */
@Service
@SuppressWarnings("all")
public class ConDataobjectCategoryServiceImpl extends ServiceImpl<ConDataobjectCategoryMapper, ConDataobjectCategory> implements IConDataobjectCategoryService {
    @Autowired
    private ConDataobjectCategoryMapper conDataobjectCategoryMapper;
    @Autowired
    private ConDataobjectCodeRuleMapper conDataobjectCodeRuleMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "数据对象类别";

    /**
     * 查询数据对象类别
     *
     * @param sid 数据对象类别ID
     * @return 数据对象类别
     */
    @Override
    public ConDataobjectCategory selectConDataobjectCategoryById(Long sid) {
        ConDataobjectCategory conDataobjectCategory = conDataobjectCategoryMapper.selectConDataobjectCategoryById(sid);
        //编码规则
        List<ConDataobjectCodeRule> ruleList = conDataobjectCodeRuleMapper.selectConDataobjectCodeRuleList(new ConDataobjectCodeRule()
            .setDataobjectCategorySid(sid));
        conDataobjectCategory.setRuleItemList(ruleList);
        MongodbUtil.find(conDataobjectCategory);
        return conDataobjectCategory;
    }

    /**
     * 查询数据对象类别列表
     *
     * @param conDataobjectCategory 数据对象类别
     * @return 数据对象类别
     */
    @Override
    public List<ConDataobjectCategory> selectConDataobjectCategoryList(ConDataobjectCategory conDataobjectCategory) {
        return conDataobjectCategoryMapper.selectConDataobjectCategoryList(conDataobjectCategory);
    }

    /**
     * 新增数据对象类别
     * 需要注意编码重复校验
     *
     * @param conDataobjectCategory 数据对象类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDataobjectCategory(ConDataobjectCategory conDataobjectCategory) {
        List<ConDataobjectCategory> codeList = conDataobjectCategoryMapper.selectList(new QueryWrapper<ConDataobjectCategory>().lambda()
                .eq(ConDataobjectCategory::getCode, conDataobjectCategory.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)){
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDataobjectCategory> nameList = conDataobjectCategoryMapper.selectList(new QueryWrapper<ConDataobjectCategory>().lambda()
                .eq(ConDataobjectCategory::getName, conDataobjectCategory.getName()));
        if (CollectionUtil.isNotEmpty(nameList)){
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        setConfirmInfo(conDataobjectCategory);
        int row = conDataobjectCategoryMapper.insert(conDataobjectCategory);
        if (row > 0) {
            if (CollectionUtils.isNotEmpty(conDataobjectCategory.getRuleItemList())){
                conDataobjectCategory.getRuleItemList().forEach(item->{
                    conDataobjectCodeRuleMapper.insert(item);
                });
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conDataobjectCategory.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ConDataobjectCategory o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 修改数据对象类别
     *
     * @param conDataobjectCategory 数据对象类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDataobjectCategory(ConDataobjectCategory conDataobjectCategory) {
        ConDataobjectCategory response = conDataobjectCategoryMapper.selectConDataobjectCategoryById(conDataobjectCategory.getSid());
        int row = conDataobjectCategoryMapper.updateById(conDataobjectCategory);
        if (row > 0) {
            //编码规则
            setCodeRule(conDataobjectCategory);
            //插入日志
            MongodbUtil.insertUserLog(conDataobjectCategory.getSid(), BusinessType.UPDATE.getValue(), response, conDataobjectCategory, TITLE);
        }
        return row;
    }

    /**
     * 变更数据对象类别
     *
     * @param conDataobjectCategory 数据对象类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDataobjectCategory(ConDataobjectCategory conDataobjectCategory) {
        ConDataobjectCategory response = conDataobjectCategoryMapper.selectConDataobjectCategoryById(conDataobjectCategory.getSid());
        List<ConDataobjectCategory> nameList = conDataobjectCategoryMapper.selectList(new QueryWrapper<ConDataobjectCategory>().lambda()
                .eq(ConDataobjectCategory::getName, conDataobjectCategory.getName()));
        if (CollectionUtil.isNotEmpty(nameList)){
            nameList.forEach(o ->{
                if (!conDataobjectCategory.getSid().equals(o.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        setConfirmInfo(conDataobjectCategory);
        conDataobjectCategory.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = conDataobjectCategoryMapper.updateAllById(conDataobjectCategory);
        if (row > 0) {
            //编码规则
            setCodeRule(conDataobjectCategory);
            //插入日志
            MongodbUtil.insertUserLog(conDataobjectCategory.getSid(), BusinessType.CHANGE.getValue(), response, conDataobjectCategory, TITLE);
        }
        return row;
    }

    /**
     * 编码规则
     * @param conDataobjectCategory
     */
    private void setCodeRule(ConDataobjectCategory conDataobjectCategory){
        conDataobjectCodeRuleMapper.delete(new QueryWrapper<ConDataobjectCodeRule>()
                .lambda().eq(ConDataobjectCodeRule::getDataobjectCategorySid,conDataobjectCategory.getSid()));
        if (CollectionUtils.isNotEmpty(conDataobjectCategory.getRuleItemList())){
            conDataobjectCategory.getRuleItemList().forEach(item->{
                conDataobjectCodeRuleMapper.insert(item);
            });
        }
    }

    /**
     * 批量删除数据对象类别
     *
     * @param sids 需要删除的数据对象类别ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDataobjectCategoryByIds(List<Long> sids) {
        List<ConDataobjectCategory> list = conDataobjectCategoryMapper.selectList(new QueryWrapper<ConDataobjectCategory>()
                .lambda().in(ConDataobjectCategory::getSid,sids));
        int row = conDataobjectCategoryMapper.deleteBatchIds(sids);
        if (row > 0){
            int i = conDataobjectCodeRuleMapper.delete(new QueryWrapper<ConDataobjectCodeRule>().lambda().in(ConDataobjectCodeRule::getDataobjectCategorySid,sids));
            list.forEach(item->{
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(item, new ConDataobjectCategory());
                MongodbUtil.insertUserLog(item.getSid(),BusinessType.DELETE.getValue(), msgList,TITLE);
            });
        }
        return row;
    }

    /**
     * 启用/停用
     *
     * @param conDataobjectCategory
     * @return
     */
    @Override
    public int changeStatus(ConDataobjectCategory conDataobjectCategory) {
        int row = 0;
        Long[] sids = conDataobjectCategory.getSidList();
        if (sids != null && sids.length > 0) {
            row = conDataobjectCategoryMapper.update(null, new UpdateWrapper<ConDataobjectCategory>().lambda().set(ConDataobjectCategory::getStatus, conDataobjectCategory.getStatus())
                    .in(ConDataobjectCategory::getSid, sids));
            for (Long id : sids) {
                conDataobjectCategory.setSid(id);
                row = conDataobjectCategoryMapper.updateById(conDataobjectCategory);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbDeal.status(conDataobjectCategory.getSid(), conDataobjectCategory.getStatus(), msgList, TITLE, null);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conDataobjectCategory
     * @return
     */
    @Override
    public int check(ConDataobjectCategory conDataobjectCategory) {
        int row = 0;
        Long[] sids = conDataobjectCategory.getSidList();
        if (sids != null && sids.length > 0) {
            row = conDataobjectCategoryMapper.update(null, new UpdateWrapper<ConDataobjectCategory>().lambda().set(ConDataobjectCategory::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConDataobjectCategory::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbDeal.check(id, conDataobjectCategory.getHandleStatus(), msgList, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 数据对象类别下拉接口
     */
    @Override
    public List<ConDataobjectCategory> getDataobjectCategoryList(ConDataobjectCategory conDataobjectCategory) {
        return conDataobjectCategoryMapper.getDataobjectCategoryList(conDataobjectCategory);
    }
}
