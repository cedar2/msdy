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
import com.platform.ems.plug.domain.ConAccountCategory;
import com.platform.ems.plug.domain.ConBuTypePayBill;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.UserOperLog;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import com.platform.ems.plug.mapper.ConAdjustTypeCustomerMapper;
import com.platform.ems.plug.domain.ConAdjustTypeCustomer;
import com.platform.ems.plug.service.IConAdjustTypeCustomerService;

/**
 * 调账类型_客户Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConAdjustTypeCustomerServiceImpl extends ServiceImpl<ConAdjustTypeCustomerMapper,ConAdjustTypeCustomer>  implements IConAdjustTypeCustomerService {
    @Autowired
    private ConAdjustTypeCustomerMapper conAdjustTypeCustomerMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "调账类型_客户";
    /**
     * 查询调账类型_客户
     *
     * @param sid 调账类型_客户ID
     * @return 调账类型_客户
     */
    @Override
    public ConAdjustTypeCustomer selectConAdjustTypeCustomerById(Long sid) {
        ConAdjustTypeCustomer conAdjustTypeCustomer = conAdjustTypeCustomerMapper.selectConAdjustTypeCustomerById(sid);
        MongodbUtil.find(conAdjustTypeCustomer);
        return  conAdjustTypeCustomer;
    }

    /**
     * 查询调账类型_客户列表
     *
     * @param conAdjustTypeCustomer 调账类型_客户
     * @return 调账类型_客户
     */
    @Override
    public List<ConAdjustTypeCustomer> selectConAdjustTypeCustomerList(ConAdjustTypeCustomer conAdjustTypeCustomer) {
        return conAdjustTypeCustomerMapper.selectConAdjustTypeCustomerList(conAdjustTypeCustomer);
    }

    /**
     * 新增调账类型_客户
     * 需要注意编码重复校验
     * @param conAdjustTypeCustomer 调账类型_客户
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConAdjustTypeCustomer(ConAdjustTypeCustomer conAdjustTypeCustomer) {
        List<ConAdjustTypeCustomer> codeList = conAdjustTypeCustomerMapper.selectList(new QueryWrapper<ConAdjustTypeCustomer>().lambda()
                .eq(ConAdjustTypeCustomer::getCode, conAdjustTypeCustomer.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConAdjustTypeCustomer> nameList = conAdjustTypeCustomerMapper.selectList(new QueryWrapper<ConAdjustTypeCustomer>().lambda()
                .eq(ConAdjustTypeCustomer::getName, conAdjustTypeCustomer.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conAdjustTypeCustomerMapper.insert(conAdjustTypeCustomer);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conAdjustTypeCustomer.getSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改调账类型_客户
     *
     * @param conAdjustTypeCustomer 调账类型_客户
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConAdjustTypeCustomer(ConAdjustTypeCustomer conAdjustTypeCustomer) {
        ConAdjustTypeCustomer response = conAdjustTypeCustomerMapper.selectConAdjustTypeCustomerById(conAdjustTypeCustomer.getSid());
        int row=conAdjustTypeCustomerMapper.updateById(conAdjustTypeCustomer);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conAdjustTypeCustomer.getSid(), BusinessType.UPDATE.ordinal(), response,conAdjustTypeCustomer,TITLE);
        }
        return row;
    }

    /**
     * 变更调账类型_客户
     *
     * @param conAdjustTypeCustomer 调账类型_客户
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConAdjustTypeCustomer(ConAdjustTypeCustomer conAdjustTypeCustomer) {
        List<ConAdjustTypeCustomer> nameList = conAdjustTypeCustomerMapper.selectList(new QueryWrapper<ConAdjustTypeCustomer>().lambda()
                .eq(ConAdjustTypeCustomer::getName, conAdjustTypeCustomer.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conAdjustTypeCustomer.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conAdjustTypeCustomer.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConAdjustTypeCustomer response = conAdjustTypeCustomerMapper.selectConAdjustTypeCustomerById(conAdjustTypeCustomer.getSid());
        int row=conAdjustTypeCustomerMapper.updateAllById(conAdjustTypeCustomer);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conAdjustTypeCustomer.getSid(), BusinessType.CHANGE.ordinal(), response,conAdjustTypeCustomer,TITLE);
        }
        return row;
    }

    /**
     * 批量删除调账类型_客户
     *
     * @param sids 需要删除的调账类型_客户ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConAdjustTypeCustomerByIds(List<Long> sids) {
        return conAdjustTypeCustomerMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conAdjustTypeCustomer
    * @return
    */
    @Override
    public int changeStatus(ConAdjustTypeCustomer conAdjustTypeCustomer){
        int row=0;
        Long[] sids=conAdjustTypeCustomer.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conAdjustTypeCustomer.setSid(id);
                row=conAdjustTypeCustomerMapper.updateById( conAdjustTypeCustomer);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conAdjustTypeCustomer.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conAdjustTypeCustomer.getSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conAdjustTypeCustomer
     * @return
     */
    @Override
    public int check(ConAdjustTypeCustomer conAdjustTypeCustomer){
        int row=0;
        Long[] sids=conAdjustTypeCustomer.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conAdjustTypeCustomer.setSid(id);
                row=conAdjustTypeCustomerMapper.updateById( conAdjustTypeCustomer);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conAdjustTypeCustomer.getSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }

    /**
     * 款项类别下拉框列表
     */
    @Override
    public List<ConAdjustTypeCustomer> getConAdjustTypeCustomerList() {
        return conAdjustTypeCustomerMapper.getConAdjustTypeCustomerList();
    }

}
