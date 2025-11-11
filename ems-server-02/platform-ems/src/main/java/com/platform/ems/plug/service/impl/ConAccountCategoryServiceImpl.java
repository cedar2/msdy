package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConAccountCategory;
import com.platform.ems.plug.mapper.ConAccountCategoryMapper;
import com.platform.ems.plug.service.IConAccountCategoryService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 款项类别Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-22
 */
@Service
@SuppressWarnings("all")
public class ConAccountCategoryServiceImpl extends ServiceImpl<ConAccountCategoryMapper, ConAccountCategory> implements IConAccountCategoryService {
    @Autowired
    private ConAccountCategoryMapper conAccountCategoryMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "款项类别";

    /**
     * 查询款项类别
     *
     * @param sid 款项类别ID
     * @return 款项类别
     */
    @Override
    public ConAccountCategory selectConAccountCategoryById(Long sid) {
        ConAccountCategory conAccountCategory = conAccountCategoryMapper.selectConAccountCategoryById(sid);
        MongodbUtil.find(conAccountCategory);
        return conAccountCategory;
    }

    /**
     * 查询款项类别列表
     *
     * @param conAccountCategory 款项类别
     * @return 款项类别
     */
    @Override
    public List<ConAccountCategory> selectConAccountCategoryList(ConAccountCategory conAccountCategory) {
        return conAccountCategoryMapper.selectConAccountCategoryList(conAccountCategory);
    }

    /**
     * 新增款项类别
     * 需要注意编码重复校验
     *
     * @param conAccountCategory 款项类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConAccountCategory(ConAccountCategory conAccountCategory) {
        List<ConAccountCategory> codeList = conAccountCategoryMapper.selectList(new QueryWrapper<ConAccountCategory>().lambda()
                .eq(ConAccountCategory::getCode, conAccountCategory.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConAccountCategory> nameList = conAccountCategoryMapper.selectList(new QueryWrapper<ConAccountCategory>().lambda()
                .eq(ConAccountCategory::getName, conAccountCategory.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conAccountCategoryMapper.insert(conAccountCategory);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conAccountCategory.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改款项类别
     *
     * @param conAccountCategory 款项类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConAccountCategory(ConAccountCategory conAccountCategory) {
        ConAccountCategory response = conAccountCategoryMapper.selectConAccountCategoryById(conAccountCategory.getSid());
        int row = conAccountCategoryMapper.updateById(conAccountCategory);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conAccountCategory.getSid(), BusinessType.UPDATE.getValue(), response, conAccountCategory, TITLE);
        }
        return row;
    }

    /**
     * 变更款项类别
     *
     * @param conAccountCategory 款项类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConAccountCategory(ConAccountCategory conAccountCategory) {
        List<ConAccountCategory> nameList = conAccountCategoryMapper.selectList(new QueryWrapper<ConAccountCategory>().lambda()
                .eq(ConAccountCategory::getName, conAccountCategory.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conAccountCategory.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conAccountCategory.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConAccountCategory response = conAccountCategoryMapper.selectConAccountCategoryById(conAccountCategory.getSid());
        int row = conAccountCategoryMapper.updateAllById(conAccountCategory);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conAccountCategory.getSid(), BusinessType.CHANGE.getValue(), response, conAccountCategory, TITLE);
        }
        return row;
    }

    /**
     * 批量删除款项类别
     *
     * @param sids 需要删除的款项类别ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConAccountCategoryByIds(List<Long> sids) {
        return conAccountCategoryMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conAccountCategory
     * @return
     */
    @Override
    public int changeStatus(ConAccountCategory conAccountCategory) {
        int row = 0;
        Long[] sids = conAccountCategory.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conAccountCategory.setSid(id);
                row = conAccountCategoryMapper.updateById(conAccountCategory);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conAccountCategory.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conAccountCategory.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conAccountCategory
     * @return
     */
    @Override
    public int check(ConAccountCategory conAccountCategory) {
        int row = 0;
        Long[] sids = conAccountCategory.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conAccountCategory.setSid(id);
                row = conAccountCategoryMapper.updateById(conAccountCategory);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conAccountCategory.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 款项类别下拉框列表
     */
    @Override
    public List<ConAccountCategory> getConAccountCategoryList() {
        return conAccountCategoryMapper.getConAccountCategoryList();
    }
}
