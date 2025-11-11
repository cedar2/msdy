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

import com.platform.ems.plug.mapper.ConPaymentMethodMapper;
import com.platform.ems.plug.domain.ConPaymentMethod;
import com.platform.ems.plug.service.IConPaymentMethodService;

/**
 * 支付方式Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConPaymentMethodServiceImpl extends ServiceImpl<ConPaymentMethodMapper, ConPaymentMethod> implements IConPaymentMethodService {
    @Autowired
    private ConPaymentMethodMapper conPaymentMethodMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "支付方式";

    /**
     * 查询支付方式
     *
     * @param sid 支付方式ID
     * @return 支付方式
     */
    @Override
    public ConPaymentMethod selectConPaymentMethodById(Long sid) {
        ConPaymentMethod conPaymentMethod = conPaymentMethodMapper.selectConPaymentMethodById(sid);
        MongodbUtil.find(conPaymentMethod);
        return conPaymentMethod;
    }

    /**
     * 查询支付方式列表
     *
     * @param conPaymentMethod 支付方式
     * @return 支付方式
     */
    @Override
    public List<ConPaymentMethod> selectConPaymentMethodList(ConPaymentMethod conPaymentMethod) {
        return conPaymentMethodMapper.selectConPaymentMethodList(conPaymentMethod);
    }

    /**
     * 新增支付方式
     * 需要注意编码重复校验
     *
     * @param conPaymentMethod 支付方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConPaymentMethod(ConPaymentMethod conPaymentMethod) {
        List<ConPaymentMethod> codeList = conPaymentMethodMapper.selectList(new QueryWrapper<ConPaymentMethod>().lambda()
                .eq(ConPaymentMethod::getCode, conPaymentMethod.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConPaymentMethod> nameList = conPaymentMethodMapper.selectList(new QueryWrapper<ConPaymentMethod>().lambda()
                .eq(ConPaymentMethod::getName, conPaymentMethod.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conPaymentMethodMapper.insert(conPaymentMethod);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conPaymentMethod.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改支付方式
     *
     * @param conPaymentMethod 支付方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConPaymentMethod(ConPaymentMethod conPaymentMethod) {
        ConPaymentMethod response = conPaymentMethodMapper.selectConPaymentMethodById(conPaymentMethod.getSid());
        int row = conPaymentMethodMapper.updateById(conPaymentMethod);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conPaymentMethod.getSid(), BusinessType.UPDATE.getValue(), response, conPaymentMethod, TITLE);
        }
        return row;
    }

    /**
     * 变更支付方式
     *
     * @param conPaymentMethod 支付方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConPaymentMethod(ConPaymentMethod conPaymentMethod) {
        List<ConPaymentMethod> nameList = conPaymentMethodMapper.selectList(new QueryWrapper<ConPaymentMethod>().lambda()
                .eq(ConPaymentMethod::getName, conPaymentMethod.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conPaymentMethod.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conPaymentMethod.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConPaymentMethod response = conPaymentMethodMapper.selectConPaymentMethodById(conPaymentMethod.getSid());
        int row = conPaymentMethodMapper.updateAllById(conPaymentMethod);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conPaymentMethod.getSid(), BusinessType.CHANGE.getValue(), response, conPaymentMethod, TITLE);
        }
        return row;
    }

    /**
     * 批量删除支付方式
     *
     * @param sids 需要删除的支付方式ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConPaymentMethodByIds(List<Long> sids) {
        return conPaymentMethodMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conPaymentMethod
     * @return
     */
    @Override
    public int changeStatus(ConPaymentMethod conPaymentMethod) {
        int row = 0;
        Long[] sids = conPaymentMethod.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conPaymentMethod.setSid(id);
                row = conPaymentMethodMapper.updateById(conPaymentMethod);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conPaymentMethod.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conPaymentMethod.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conPaymentMethod
     * @return
     */
    @Override
    public int check(ConPaymentMethod conPaymentMethod) {
        int row = 0;
        Long[] sids = conPaymentMethod.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conPaymentMethod.setSid(id);
                row = conPaymentMethodMapper.updateById(conPaymentMethod);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conPaymentMethod.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConPaymentMethod> getConPaymentMethodList(ConPaymentMethod conPaymentMethod) {
        return conPaymentMethodMapper.getConPaymentMethodList(conPaymentMethod);
    }

}
