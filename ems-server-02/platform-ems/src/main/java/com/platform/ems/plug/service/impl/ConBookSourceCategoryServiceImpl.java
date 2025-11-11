package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConBookSourceCategory;
import com.platform.ems.plug.mapper.ConBookSourceCategoryMapper;
import com.platform.ems.plug.service.IConBookSourceCategoryService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 流水来源类别_财务Service业务层处理
 *
 * @author chenkw
 * @date 2021-08-03
 */
@Service
@SuppressWarnings("all")
public class ConBookSourceCategoryServiceImpl extends ServiceImpl<ConBookSourceCategoryMapper, ConBookSourceCategory> implements IConBookSourceCategoryService {
    @Autowired
    private ConBookSourceCategoryMapper conBookSourceCategoryMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "流水来源类别_财务";

    /**
     * 查询流水来源类别_财务
     *
     * @param sid 流水来源类别_财务ID
     * @return 流水来源类别_财务
     */
    @Override
    public ConBookSourceCategory selectConBookSourceCategoryById(Long sid) {
        ConBookSourceCategory conBookSourceCategory = conBookSourceCategoryMapper.selectConBookSourceCategoryById(sid);
        MongodbUtil.find(conBookSourceCategory);
        return conBookSourceCategory;
    }

    /**
     * 查询流水来源类别_财务列表
     *
     * @param conBookSourceCategory 流水来源类别_财务
     * @return 流水来源类别_财务
     */
    @Override
    public List<ConBookSourceCategory> selectConBookSourceCategoryList(ConBookSourceCategory conBookSourceCategory) {
        return conBookSourceCategoryMapper.selectConBookSourceCategoryList(conBookSourceCategory);
    }

    /**
     * 新增流水来源类别_财务
     * 需要注意编码重复校验
     *
     * @param conBookSourceCategory 流水来源类别_财务
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBookSourceCategory(ConBookSourceCategory conBookSourceCategory) {
        List<ConBookSourceCategory> codeList = conBookSourceCategoryMapper.selectList(new QueryWrapper<ConBookSourceCategory>().lambda()
                .eq(ConBookSourceCategory::getCode, conBookSourceCategory.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBookSourceCategory> nameList = conBookSourceCategoryMapper.selectList(new QueryWrapper<ConBookSourceCategory>().lambda()
                .eq(ConBookSourceCategory::getName, conBookSourceCategory.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conBookSourceCategoryMapper.insert(conBookSourceCategory);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conBookSourceCategory.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改流水来源类别_财务
     *
     * @param conBookSourceCategory 流水来源类别_财务
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBookSourceCategory(ConBookSourceCategory conBookSourceCategory) {
        ConBookSourceCategory response = conBookSourceCategoryMapper.selectConBookSourceCategoryById(conBookSourceCategory.getSid());
        int row = conBookSourceCategoryMapper.updateById(conBookSourceCategory);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBookSourceCategory.getSid(), BusinessType.UPDATE.getValue(), response, conBookSourceCategory, TITLE);
        }
        return row;
    }

    /**
     * 变更流水来源类别_财务
     *
     * @param conBookSourceCategory 流水来源类别_财务
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBookSourceCategory(ConBookSourceCategory conBookSourceCategory) {
        ConBookSourceCategory response = conBookSourceCategoryMapper.selectConBookSourceCategoryById(conBookSourceCategory.getSid());
        List<ConBookSourceCategory> nameList = conBookSourceCategoryMapper.selectList(new QueryWrapper<ConBookSourceCategory>().lambda()
                .eq(ConBookSourceCategory::getName, conBookSourceCategory.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conBookSourceCategory.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBookSourceCategory.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        int row = conBookSourceCategoryMapper.updateAllById(conBookSourceCategory);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBookSourceCategory.getSid(), BusinessType.CHANGE.getValue(), response, conBookSourceCategory, TITLE);
        }
        return row;
    }

    /**
     * 批量删除流水来源类别_财务
     *
     * @param sids 需要删除的流水来源类别_财务ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBookSourceCategoryByIds(List<Long> sids) {
        return conBookSourceCategoryMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conBookSourceCategory
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(ConBookSourceCategory conBookSourceCategory) {
        int row = 0;
        Long[] sids = conBookSourceCategory.getSidList();
        if (sids != null && sids.length > 0) {
            row = conBookSourceCategoryMapper.update(null, new UpdateWrapper<ConBookSourceCategory>().lambda().set(ConBookSourceCategory::getStatus, conBookSourceCategory.getStatus())
                    .in(ConBookSourceCategory::getSid, sids));
            for (Long id : sids) {
                conBookSourceCategory.setSid(id);
                row = conBookSourceCategoryMapper.updateById(conBookSourceCategory);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conBookSourceCategory.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conBookSourceCategory.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conBookSourceCategory
     * @return
     */
    @Override
    public int check(ConBookSourceCategory conBookSourceCategory) {
        int row = 0;
        Long[] sids = conBookSourceCategory.getSidList();
        if (sids != null && sids.length > 0) {
            row = conBookSourceCategoryMapper.update(null, new UpdateWrapper<ConBookSourceCategory>().lambda().set(ConBookSourceCategory::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConBookSourceCategory::getSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 下拉框列表
     */
    @Override
    public List<ConBookSourceCategory> getConBookSourceCategoryList() {
        return conBookSourceCategoryMapper.getConBookSourceCategoryList();
    }
}
