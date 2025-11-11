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
import com.platform.ems.plug.domain.ConSaleOrg;
import com.platform.ems.plug.mapper.ConSaleOrgMapper;
import com.platform.ems.plug.service.IConSaleOrgService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 销售组织Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConSaleOrgServiceImpl extends ServiceImpl<ConSaleOrgMapper, ConSaleOrg> implements IConSaleOrgService {
    @Autowired
    private ConSaleOrgMapper conSaleOrgMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "销售组织";

    /**
     * 查询销售组织
     *
     * @param sid 销售组织ID
     * @return 销售组织
     */
    @Override
    public ConSaleOrg selectConSaleOrgById(Long sid) {
        ConSaleOrg conSaleOrg = conSaleOrgMapper.selectConSaleOrgById(sid);
        MongodbUtil.find(conSaleOrg);
        return conSaleOrg;
    }

    /**
     * 查询销售组织列表
     *
     * @param conSaleOrg 销售组织
     * @return 销售组织
     */
    @Override
    public List<ConSaleOrg> selectConSaleOrgList(ConSaleOrg conSaleOrg) {
        return conSaleOrgMapper.selectConSaleOrgList(conSaleOrg);
    }

    /**
     * 新增销售组织
     * 需要注意编码重复校验
     *
     * @param conSaleOrg 销售组织
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConSaleOrg(ConSaleOrg conSaleOrg) {
        List<ConSaleOrg> codeList = conSaleOrgMapper.selectList(new QueryWrapper<ConSaleOrg>().lambda()
                .eq(ConSaleOrg::getCode, conSaleOrg.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConSaleOrg> nameList = conSaleOrgMapper.selectList(new QueryWrapper<ConSaleOrg>().lambda()
                .eq(ConSaleOrg::getName, conSaleOrg.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conSaleOrgMapper.insert(conSaleOrg);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conSaleOrg.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改销售组织
     *
     * @param conSaleOrg 销售组织
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConSaleOrg(ConSaleOrg conSaleOrg) {
        ConSaleOrg response = conSaleOrgMapper.selectConSaleOrgById(conSaleOrg.getSid());
        int row = conSaleOrgMapper.updateById(conSaleOrg);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conSaleOrg.getSid(), BusinessType.UPDATE.getValue(), response, conSaleOrg, TITLE);
        }
        return row;
    }

    /**
     * 变更销售组织
     *
     * @param conSaleOrg 销售组织
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConSaleOrg(ConSaleOrg conSaleOrg) {
        List<ConSaleOrg> nameList = conSaleOrgMapper.selectList(new QueryWrapper<ConSaleOrg>().lambda()
                .eq(ConSaleOrg::getName, conSaleOrg.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conSaleOrg.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conSaleOrg.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConSaleOrg response = conSaleOrgMapper.selectConSaleOrgById(conSaleOrg.getSid());
        int row = conSaleOrgMapper.updateAllById(conSaleOrg);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conSaleOrg.getSid(), BusinessType.CHANGE.getValue(), response, conSaleOrg, TITLE);
        }
        return row;
    }

    /**
     * 批量删除销售组织
     *
     * @param sids 需要删除的销售组织ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConSaleOrgByIds(List<Long> sids) {
        return conSaleOrgMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conSaleOrg
     * @return
     */
    @Override
    public int changeStatus(ConSaleOrg conSaleOrg) {
        int row = 0;
        Long[] sids = conSaleOrg.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conSaleOrg.setSid(id);
                row = conSaleOrgMapper.updateById(conSaleOrg);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conSaleOrg.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conSaleOrg.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conSaleOrg
     * @return
     */
    @Override
    public int check(ConSaleOrg conSaleOrg) {
        int row = 0;
        Long[] sids = conSaleOrg.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conSaleOrg.setSid(id);
                row = conSaleOrgMapper.updateById(conSaleOrg);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conSaleOrg.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConSaleOrg> getConSaleOrgList() {
        return conSaleOrgMapper.getConSaleOrgList();
    }

}
