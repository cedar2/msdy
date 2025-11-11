package com.platform.ems.plug.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.plug.domain.ConBuTypeSalesOrder;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.plug.mapper.ConDocCategoryMapper;
import com.platform.ems.plug.domain.ConDocCategory;
import com.platform.ems.plug.service.IConDocCategoryService;

/**
 * 单据类别Service业务层处理
 *
 * @author chenkw
 * @date 2021-08-02
 */
@Service
@SuppressWarnings("all")
public class ConDocCategoryServiceImpl extends ServiceImpl<ConDocCategoryMapper, ConDocCategory> implements IConDocCategoryService {
    @Autowired
    private ConDocCategoryMapper conDocCategoryMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "单据类别";

    /**
     * 查询单据类别
     *
     * @param sid 单据类别ID
     * @return 单据类别
     */
    @Override
    public ConDocCategory selectConDocCategoryById(Long sid) {
        ConDocCategory conDocCategory = conDocCategoryMapper.selectConDocCategoryById(sid);
        MongodbUtil.find(conDocCategory);
        return conDocCategory;
    }

    /**
     * 查询单据类别列表
     *
     * @param conDocCategory 单据类别
     * @return 单据类别
     */
    @Override
    public List<ConDocCategory> selectConDocCategoryList(ConDocCategory conDocCategory) {
        return conDocCategoryMapper.selectConDocCategoryList(conDocCategory);
    }

    /**
     * 新增单据类别
     * 需要注意编码重复校验
     *
     * @param conDocCategory 单据类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocCategory(ConDocCategory conDocCategory) {
        List<ConDocCategory> codeList = conDocCategoryMapper.selectList(new QueryWrapper<ConDocCategory>().lambda()
                .eq(ConDocCategory::getCode, conDocCategory.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)){
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocCategory> nameList = conDocCategoryMapper.selectList(new QueryWrapper<ConDocCategory>().lambda()
                .eq(ConDocCategory::getName, conDocCategory.getName()));
        if (CollectionUtil.isNotEmpty(nameList)){
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conDocCategoryMapper.insert(conDocCategory);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conDocCategory.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改单据类别
     *
     * @param conDocCategory 单据类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocCategory(ConDocCategory conDocCategory) {
        ConDocCategory response = conDocCategoryMapper.selectConDocCategoryById(conDocCategory.getSid());
        int row = conDocCategoryMapper.updateById(conDocCategory);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocCategory.getSid(), BusinessType.UPDATE.getValue(), response, conDocCategory, TITLE);
        }
        return row;
    }

    /**
     * 变更单据类别
     *
     * @param conDocCategory 单据类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocCategory(ConDocCategory conDocCategory) {
        List<ConDocCategory> nameList = conDocCategoryMapper.selectList(new QueryWrapper<ConDocCategory>().lambda()
                .eq(ConDocCategory::getName, conDocCategory.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDocCategory.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDocCategory.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDocCategory response = conDocCategoryMapper.selectConDocCategoryById(conDocCategory.getSid());
        int row = conDocCategoryMapper.updateAllById(conDocCategory);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocCategory.getSid(), BusinessType.CHANGE.getValue(), response, conDocCategory, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类别
     *
     * @param sids 需要删除的单据类别ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocCategoryByIds(List<Long> sids) {
        return conDocCategoryMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conDocCategory
     * @return
     */
    @Override
    public int changeStatus(ConDocCategory conDocCategory) {
        int row = 0;
        Long[] sids = conDocCategory.getSidList();
        if (sids != null && sids.length > 0) {
            row = conDocCategoryMapper.update(null, new UpdateWrapper<ConDocCategory>().lambda().set(ConDocCategory::getStatus, conDocCategory.getStatus())
                    .in(ConDocCategory::getSid, sids));
            for (Long id : sids) {
                conDocCategory.setSid(id);
                row = conDocCategoryMapper.updateById(conDocCategory);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conDocCategory.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conDocCategory.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conDocCategory
     * @return
     */
    @Override
    public int check(ConDocCategory conDocCategory) {
        int row = 0;
        Long[] sids = conDocCategory.getSidList();
        if (sids != null && sids.length > 0) {
            row = conDocCategoryMapper.update(null, new UpdateWrapper<ConDocCategory>().lambda().set(ConDocCategory::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConDocCategory::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConDocCategory> getConDocCategoryList() {
        return conDocCategoryMapper.getConDocCategoryList();
    }
}
