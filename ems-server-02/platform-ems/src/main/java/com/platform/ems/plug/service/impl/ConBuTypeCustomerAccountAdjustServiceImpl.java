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
import com.platform.ems.plug.domain.ConBuTypeCustomerAccountAdjust;
import com.platform.ems.plug.mapper.ConBuTypeCustomerAccountAdjustMapper;
import com.platform.ems.plug.service.IConBuTypeCustomerAccountAdjustService;
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
 * 业务类型_客户调账单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeCustomerAccountAdjustServiceImpl extends ServiceImpl<ConBuTypeCustomerAccountAdjustMapper,ConBuTypeCustomerAccountAdjust>  implements IConBuTypeCustomerAccountAdjustService {
    @Autowired
    private ConBuTypeCustomerAccountAdjustMapper conBuTypeCustomerAccountAdjustMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_客户调账单";
    /**
     * 查询业务类型_客户调账单
     *
     * @param sid 业务类型_客户调账单ID
     * @return 业务类型_客户调账单
     */
    @Override
    public ConBuTypeCustomerAccountAdjust selectConBuTypeCustomerAccountAdjustById(Long sid) {
        ConBuTypeCustomerAccountAdjust conBuTypeCustomerAccountAdjust = conBuTypeCustomerAccountAdjustMapper.selectConBuTypeCustomerAccountAdjustById(sid);
        MongodbUtil.find(conBuTypeCustomerAccountAdjust);
        return  conBuTypeCustomerAccountAdjust;
    }

    /**
     * 查询业务类型_客户调账单列表
     *
     * @param conBuTypeCustomerAccountAdjust 业务类型_客户调账单
     * @return 业务类型_客户调账单
     */
    @Override
    public List<ConBuTypeCustomerAccountAdjust> selectConBuTypeCustomerAccountAdjustList(ConBuTypeCustomerAccountAdjust conBuTypeCustomerAccountAdjust) {
        return conBuTypeCustomerAccountAdjustMapper.selectConBuTypeCustomerAccountAdjustList(conBuTypeCustomerAccountAdjust);
    }

    /**
     * 新增业务类型_客户调账单
     * 需要注意编码重复校验
     * @param conBuTypeCustomerAccountAdjust 业务类型_客户调账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeCustomerAccountAdjust(ConBuTypeCustomerAccountAdjust conBuTypeCustomerAccountAdjust) {
        List<ConBuTypeCustomerAccountAdjust> codeList = conBuTypeCustomerAccountAdjustMapper.selectList(new QueryWrapper<ConBuTypeCustomerAccountAdjust>().lambda()
                .eq(ConBuTypeCustomerAccountAdjust::getCode, conBuTypeCustomerAccountAdjust.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeCustomerAccountAdjust> nameList = conBuTypeCustomerAccountAdjustMapper.selectList(new QueryWrapper<ConBuTypeCustomerAccountAdjust>().lambda()
                .eq(ConBuTypeCustomerAccountAdjust::getName, conBuTypeCustomerAccountAdjust.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conBuTypeCustomerAccountAdjustMapper.insert(conBuTypeCustomerAccountAdjust);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeCustomerAccountAdjust.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_客户调账单
     *
     * @param conBuTypeCustomerAccountAdjust 业务类型_客户调账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeCustomerAccountAdjust(ConBuTypeCustomerAccountAdjust conBuTypeCustomerAccountAdjust) {
        ConBuTypeCustomerAccountAdjust response = conBuTypeCustomerAccountAdjustMapper.selectConBuTypeCustomerAccountAdjustById(conBuTypeCustomerAccountAdjust.getSid());
        int row=conBuTypeCustomerAccountAdjustMapper.updateById(conBuTypeCustomerAccountAdjust);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeCustomerAccountAdjust.getSid(), BusinessType.UPDATE.getValue(), response,conBuTypeCustomerAccountAdjust,TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_客户调账单
     *
     * @param conBuTypeCustomerAccountAdjust 业务类型_客户调账单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeCustomerAccountAdjust(ConBuTypeCustomerAccountAdjust conBuTypeCustomerAccountAdjust) {
        List<ConBuTypeCustomerAccountAdjust> nameList = conBuTypeCustomerAccountAdjustMapper.selectList(new QueryWrapper<ConBuTypeCustomerAccountAdjust>().lambda()
                .eq(ConBuTypeCustomerAccountAdjust::getName, conBuTypeCustomerAccountAdjust.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBuTypeCustomerAccountAdjust.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeCustomerAccountAdjust.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeCustomerAccountAdjust response = conBuTypeCustomerAccountAdjustMapper.selectConBuTypeCustomerAccountAdjustById(conBuTypeCustomerAccountAdjust.getSid());
        int row = conBuTypeCustomerAccountAdjustMapper.updateAllById(conBuTypeCustomerAccountAdjust);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeCustomerAccountAdjust.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeCustomerAccountAdjust, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_客户调账单
     *
     * @param sids 需要删除的业务类型_客户调账单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeCustomerAccountAdjustByIds(List<Long> sids) {
        return conBuTypeCustomerAccountAdjustMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conBuTypeCustomerAccountAdjust
    * @return
    */
    @Override
    public int changeStatus(ConBuTypeCustomerAccountAdjust conBuTypeCustomerAccountAdjust){
        int row=0;
        Long[] sids=conBuTypeCustomerAccountAdjust.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeCustomerAccountAdjust.setSid(id);
                row=conBuTypeCustomerAccountAdjustMapper.updateById( conBuTypeCustomerAccountAdjust);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conBuTypeCustomerAccountAdjust.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conBuTypeCustomerAccountAdjust.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conBuTypeCustomerAccountAdjust
     * @return
     */
    @Override
    public int check(ConBuTypeCustomerAccountAdjust conBuTypeCustomerAccountAdjust){
        int row=0;
        Long[] sids=conBuTypeCustomerAccountAdjust.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeCustomerAccountAdjust.setSid(id);
                row=conBuTypeCustomerAccountAdjustMapper.updateById( conBuTypeCustomerAccountAdjust);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conBuTypeCustomerAccountAdjust.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
