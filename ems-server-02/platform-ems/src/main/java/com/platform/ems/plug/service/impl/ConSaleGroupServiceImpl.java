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
import com.platform.ems.plug.domain.ConSaleGroup;
import com.platform.ems.plug.mapper.ConSaleGroupMapper;
import com.platform.ems.plug.service.IConSaleGroupService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 销售组Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConSaleGroupServiceImpl extends ServiceImpl<ConSaleGroupMapper, ConSaleGroup> implements IConSaleGroupService {
    @Autowired
    private ConSaleGroupMapper conSaleGroupMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "销售组";

    /**
     * 查询销售组
     *
     * @param sid 销售组ID
     * @return 销售组
     */
    @Override
    public ConSaleGroup selectConSaleGroupById(Long sid) {
        ConSaleGroup conSaleGroup = conSaleGroupMapper.selectConSaleGroupById(sid);
        MongodbUtil.find(conSaleGroup);
        return conSaleGroup;
    }

    /**
     * 查询销售组列表
     *
     * @param conSaleGroup 销售组
     * @return 销售组
     */
    @Override
    public List<ConSaleGroup> selectConSaleGroupList(ConSaleGroup conSaleGroup) {
        return conSaleGroupMapper.selectConSaleGroupList(conSaleGroup);
    }

    /**
     * 新增销售组
     * 需要注意编码重复校验
     *
     * @param conSaleGroup 销售组
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConSaleGroup(ConSaleGroup conSaleGroup) {
        List<ConSaleGroup> codeList = conSaleGroupMapper.selectList(new QueryWrapper<ConSaleGroup>().lambda()
                .eq(ConSaleGroup::getCode, conSaleGroup.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConSaleGroup> nameList = conSaleGroupMapper.selectList(new QueryWrapper<ConSaleGroup>().lambda()
                .eq(ConSaleGroup::getName, conSaleGroup.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conSaleGroupMapper.insert(conSaleGroup);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conSaleGroup.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改销售组
     *
     * @param conSaleGroup 销售组
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConSaleGroup(ConSaleGroup conSaleGroup) {
        ConSaleGroup response = conSaleGroupMapper.selectConSaleGroupById(conSaleGroup.getSid());
        int row = conSaleGroupMapper.updateById(conSaleGroup);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conSaleGroup.getSid(), BusinessType.UPDATE.getValue(), response, conSaleGroup, TITLE);
        }
        return row;
    }

    /**
     * 变更销售组
     *
     * @param conSaleGroup 销售组
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConSaleGroup(ConSaleGroup conSaleGroup) {
        List<ConSaleGroup> nameList = conSaleGroupMapper.selectList(new QueryWrapper<ConSaleGroup>().lambda()
                .eq(ConSaleGroup::getName, conSaleGroup.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conSaleGroup.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conSaleGroup.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConSaleGroup response = conSaleGroupMapper.selectConSaleGroupById(conSaleGroup.getSid());
        int row = conSaleGroupMapper.updateAllById(conSaleGroup);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conSaleGroup.getSid(), BusinessType.CHANGE.getValue(), response, conSaleGroup, TITLE);
        }
        return row;
    }

    /**
     * 批量删除销售组
     *
     * @param sids 需要删除的销售组ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConSaleGroupByIds(List<Long> sids) {
        return conSaleGroupMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conSaleGroup
     * @return
     */
    @Override
    public int changeStatus(ConSaleGroup conSaleGroup) {
        int row = 0;
        Long[] sids = conSaleGroup.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conSaleGroup.setSid(id);
                row = conSaleGroupMapper.updateById(conSaleGroup);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conSaleGroup.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conSaleGroup.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conSaleGroup
     * @return
     */
    @Override
    public int check(ConSaleGroup conSaleGroup) {
        int row = 0;
        Long[] sids = conSaleGroup.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conSaleGroup.setSid(id);
                row = conSaleGroupMapper.updateById(conSaleGroup);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conSaleGroup.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConSaleGroup> getConSaleGroupList() {
        return conSaleGroupMapper.getConSaleGroupList();
    }
}
