package com.platform.ems.plug.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.plug.mapper.ConDeductionTypeCustomerMapper;
import com.platform.ems.plug.domain.ConDeductionTypeCustomer;
import com.platform.ems.plug.service.IConDeductionTypeCustomerService;

/**
 * 扣款类型_销售Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDeductionTypeCustomerServiceImpl extends ServiceImpl<ConDeductionTypeCustomerMapper, ConDeductionTypeCustomer> implements IConDeductionTypeCustomerService {
    @Autowired
    private ConDeductionTypeCustomerMapper conDeductionTypeCustomerMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "扣款类型_销售";

    /**
     * 查询扣款类型_销售
     *
     * @param sid 扣款类型_销售ID
     * @return 扣款类型_销售
     */
    @Override
    public ConDeductionTypeCustomer selectConDeductionTypeCustomerById(Long sid) {
        ConDeductionTypeCustomer conDeductionTypeCustomer = conDeductionTypeCustomerMapper.selectConDeductionTypeCustomerById(sid);
        MongodbUtil.find(conDeductionTypeCustomer);
        return conDeductionTypeCustomer;
    }

    /**
     * 查询扣款类型_销售列表
     *
     * @param conDeductionTypeCustomer 扣款类型_销售
     * @return 扣款类型_销售
     */
    @Override
    public List<ConDeductionTypeCustomer> selectConDeductionTypeCustomerList(ConDeductionTypeCustomer conDeductionTypeCustomer) {
        return conDeductionTypeCustomerMapper.selectConDeductionTypeCustomerList(conDeductionTypeCustomer);
    }

    /**
     * 新增扣款类型_销售
     * 需要注意编码重复校验
     *
     * @param conDeductionTypeCustomer 扣款类型_销售
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDeductionTypeCustomer(ConDeductionTypeCustomer conDeductionTypeCustomer) {
        List<ConDeductionTypeCustomer> codeList = conDeductionTypeCustomerMapper.selectList(new QueryWrapper<ConDeductionTypeCustomer>().lambda()
                .eq(ConDeductionTypeCustomer::getCode, conDeductionTypeCustomer.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)){
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDeductionTypeCustomer> nameList = conDeductionTypeCustomerMapper.selectList(new QueryWrapper<ConDeductionTypeCustomer>().lambda()
                .eq(ConDeductionTypeCustomer::getName, conDeductionTypeCustomer.getName()));
        if (CollectionUtil.isNotEmpty(nameList)){
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conDeductionTypeCustomerMapper.insert(conDeductionTypeCustomer);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conDeductionTypeCustomer.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改扣款类型_销售
     *
     * @param conDeductionTypeCustomer 扣款类型_销售
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDeductionTypeCustomer(ConDeductionTypeCustomer conDeductionTypeCustomer) {
        List<ConDeductionTypeCustomer> nameList = conDeductionTypeCustomerMapper.selectList(new QueryWrapper<ConDeductionTypeCustomer>().lambda()
                .eq(ConDeductionTypeCustomer::getName, conDeductionTypeCustomer.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDeductionTypeCustomer.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        ConDeductionTypeCustomer response = conDeductionTypeCustomerMapper.selectConDeductionTypeCustomerById(conDeductionTypeCustomer.getSid());
        int row = conDeductionTypeCustomerMapper.updateById(conDeductionTypeCustomer);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDeductionTypeCustomer.getSid(), BusinessType.UPDATE.getValue(), response, conDeductionTypeCustomer, TITLE);
        }
        return row;
    }

    /**
     * 变更扣款类型_销售
     *
     * @param conDeductionTypeCustomer 扣款类型_销售
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDeductionTypeCustomer(ConDeductionTypeCustomer conDeductionTypeCustomer) {
        ConDeductionTypeCustomer response = conDeductionTypeCustomerMapper.selectConDeductionTypeCustomerById(conDeductionTypeCustomer.getSid());
        List<ConDeductionTypeCustomer> nameList = conDeductionTypeCustomerMapper.selectList(new QueryWrapper<ConDeductionTypeCustomer>().lambda()
                .eq(ConDeductionTypeCustomer::getName, conDeductionTypeCustomer.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDeductionTypeCustomer.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDeductionTypeCustomer.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        int row = conDeductionTypeCustomerMapper.updateAllById(conDeductionTypeCustomer);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDeductionTypeCustomer.getSid(), BusinessType.CHANGE.getValue(), response, conDeductionTypeCustomer, TITLE);
        }
        return row;
    }

    /**
     * 批量删除扣款类型_销售
     *
     * @param sids 需要删除的扣款类型_销售ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDeductionTypeCustomerByIds(List<Long> sids) {
        return conDeductionTypeCustomerMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conDeductionTypeCustomer
     * @return
     */
    @Override
    public int changeStatus(ConDeductionTypeCustomer conDeductionTypeCustomer) {
        int row = 0;
        Long[] sids = conDeductionTypeCustomer.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conDeductionTypeCustomer.setSid(id);
                row = conDeductionTypeCustomerMapper.updateById(conDeductionTypeCustomer);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conDeductionTypeCustomer.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conDeductionTypeCustomer.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conDeductionTypeCustomer
     * @return
     */
    @Override
    public int check(ConDeductionTypeCustomer conDeductionTypeCustomer) {
        int row = 0;
        Long[] sids = conDeductionTypeCustomer.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conDeductionTypeCustomer.setSid(id);
                row = conDeductionTypeCustomerMapper.updateById(conDeductionTypeCustomer);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conDeductionTypeCustomer.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConDeductionTypeCustomer> getConDeductionTypeCustomerList() {
        return conDeductionTypeCustomerMapper.getConDeductionTypeCustomerList();
    }
}
