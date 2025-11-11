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
import com.platform.ems.plug.domain.ConDocTypePayBill;
import com.platform.ems.plug.domain.ConDocTypeVendorCashPledge;
import com.platform.ems.plug.mapper.ConDocTypePayBillMapper;
import com.platform.ems.plug.service.IConDocTypePayBillService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 单据类型_付款单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDocTypePayBillServiceImpl extends ServiceImpl<ConDocTypePayBillMapper,ConDocTypePayBill>  implements IConDocTypePayBillService {
    @Autowired
    private ConDocTypePayBillMapper conDocTypePayBillMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String TITLE = "单据类型_付款单";

    /**
     * 查询单据类型_付款单
     *
     * @param sid 单据类型_付款单ID
     * @return 单据类型_付款单
     */
    @Override
    public ConDocTypePayBill selectConDocTypePayBillById(Long sid) {
        ConDocTypePayBill conDocTypePayBill = conDocTypePayBillMapper.selectConDocTypePayBillById(sid);
        MongodbUtil.find(conDocTypePayBill);
        return  conDocTypePayBill;
    }

    /**
     * 查询单据类型_付款单列表
     *
     * @param conDocTypePayBill 单据类型_付款单
     * @return 单据类型_付款单
     */
    @Override
    public List<ConDocTypePayBill> selectConDocTypePayBillList(ConDocTypePayBill conDocTypePayBill) {
        return conDocTypePayBillMapper.selectConDocTypePayBillList(conDocTypePayBill);
    }

    /**
     * 新增单据类型_付款单
     * 需要注意编码重复校验
     * @param conDocTypePayBill 单据类型_付款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypePayBill(ConDocTypePayBill conDocTypePayBill) {
        List<ConDocTypePayBill> codeList = conDocTypePayBillMapper.selectList(new QueryWrapper<ConDocTypePayBill>().lambda()
                .eq(ConDocTypePayBill::getCode, conDocTypePayBill.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocTypePayBill> nameList = conDocTypePayBillMapper.selectList(new QueryWrapper<ConDocTypePayBill>().lambda()
                .eq(ConDocTypePayBill::getName, conDocTypePayBill.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        setConfirmInfo(conDocTypePayBill);
        int row= conDocTypePayBillMapper.insert(conDocTypePayBill);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypePayBill.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ConDocTypePayBill o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 修改单据类型_付款单
     *
     * @param conDocTypePayBill 单据类型_付款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypePayBill(ConDocTypePayBill conDocTypePayBill) {
        ConDocTypePayBill response = conDocTypePayBillMapper.selectConDocTypePayBillById(conDocTypePayBill.getSid());
        int row=conDocTypePayBillMapper.updateById(conDocTypePayBill);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDocTypePayBill.getSid(), BusinessType.UPDATE.getValue(), response,conDocTypePayBill,TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_付款单
     *
     * @param conDocTypePayBill 单据类型_付款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypePayBill(ConDocTypePayBill conDocTypePayBill) {
        List<ConDocTypePayBill> nameList = conDocTypePayBillMapper.selectList(new QueryWrapper<ConDocTypePayBill>().lambda()
                .eq(ConDocTypePayBill::getName, conDocTypePayBill.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDocTypePayBill.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDocTypePayBill.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDocTypePayBill response = conDocTypePayBillMapper.selectConDocTypePayBillById(conDocTypePayBill.getSid());
        int row = conDocTypePayBillMapper.updateAllById(conDocTypePayBill);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypePayBill.getSid(), BusinessType.CHANGE.getValue(), response, conDocTypePayBill, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_付款单
     *
     * @param sids 需要删除的单据类型_付款单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypePayBillByIds(List<Long> sids) {
        return conDocTypePayBillMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDocTypePayBill
    * @return
    */
    @Override
    public int changeStatus(ConDocTypePayBill conDocTypePayBill){
        int row=0;
        Long[] sids=conDocTypePayBill.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypePayBill.setSid(id);
                row=conDocTypePayBillMapper.updateById( conDocTypePayBill);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDocTypePayBill.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDocTypePayBill.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDocTypePayBill
     * @return
     */
    @Override
    public int check(ConDocTypePayBill conDocTypePayBill){
        int row=0;
        Long[] sids=conDocTypePayBill.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypePayBill.setSid(id);
                row=conDocTypePayBillMapper.updateById( conDocTypePayBill);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDocTypePayBill.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConDocTypePayBill> getConDocTypePayBillList(ConDocTypePayBill conDocType) {
        return conDocTypePayBillMapper.getConDocTypePayBillList(conDocType);
    }

}
