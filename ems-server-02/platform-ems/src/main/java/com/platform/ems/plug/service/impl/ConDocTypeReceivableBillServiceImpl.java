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
import com.platform.ems.plug.domain.ConDocTypeReceivableBill;
import com.platform.ems.plug.mapper.ConDocTypeReceivableBillMapper;
import com.platform.ems.plug.service.IConDocTypeReceivableBillService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 单据类型_收款单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDocTypeReceivableBillServiceImpl extends ServiceImpl<ConDocTypeReceivableBillMapper,ConDocTypeReceivableBill>  implements IConDocTypeReceivableBillService {
    @Autowired
    private ConDocTypeReceivableBillMapper conDocTypeReceivableBillMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String TITLE = "单据类型_收款单";

    /**
     * 查询单据类型_收款单
     *
     * @param sid 单据类型_收款单ID
     * @return 单据类型_收款单
     */
    @Override
    public ConDocTypeReceivableBill selectConDocTypeReceivableBillById(Long sid) {
        ConDocTypeReceivableBill conDocTypeReceivableBill = conDocTypeReceivableBillMapper.selectConDocTypeReceivableBillById(sid);
        MongodbUtil.find(conDocTypeReceivableBill);
        return  conDocTypeReceivableBill;
    }

    /**
     * 查询单据类型_收款单列表
     *
     * @param conDocTypeReceivableBill 单据类型_收款单
     * @return 单据类型_收款单
     */
    @Override
    public List<ConDocTypeReceivableBill> selectConDocTypeReceivableBillList(ConDocTypeReceivableBill conDocTypeReceivableBill) {
        return conDocTypeReceivableBillMapper.selectConDocTypeReceivableBillList(conDocTypeReceivableBill);
    }

    /**
     * 新增单据类型_收款单
     * 需要注意编码重复校验
     * @param conDocTypeReceivableBill 单据类型_收款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDocTypeReceivableBill(ConDocTypeReceivableBill conDocTypeReceivableBill) {
        List<ConDocTypeReceivableBill> codeList = conDocTypeReceivableBillMapper.selectList(new QueryWrapper<ConDocTypeReceivableBill>().lambda()
                .eq(ConDocTypeReceivableBill::getCode, conDocTypeReceivableBill.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDocTypeReceivableBill> nameList = conDocTypeReceivableBillMapper.selectList(new QueryWrapper<ConDocTypeReceivableBill>().lambda()
                .eq(ConDocTypeReceivableBill::getName, conDocTypeReceivableBill.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        setConfirmInfo(conDocTypeReceivableBill);
        int row= conDocTypeReceivableBillMapper.insert(conDocTypeReceivableBill);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDocTypeReceivableBill.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ConDocTypeReceivableBill o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 修改单据类型_收款单
     *
     * @param conDocTypeReceivableBill 单据类型_收款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDocTypeReceivableBill(ConDocTypeReceivableBill conDocTypeReceivableBill) {
        ConDocTypeReceivableBill response = conDocTypeReceivableBillMapper.selectConDocTypeReceivableBillById(conDocTypeReceivableBill.getSid());
        int row=conDocTypeReceivableBillMapper.updateById(conDocTypeReceivableBill);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeReceivableBill.getSid(), BusinessType.UPDATE.getValue(), response,conDocTypeReceivableBill,TITLE);
        }
        return row;
    }

    /**
     * 变更单据类型_收款单
     *
     * @param conDocTypeReceivableBill 单据类型_收款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDocTypeReceivableBill(ConDocTypeReceivableBill conDocTypeReceivableBill) {
        List<ConDocTypeReceivableBill> nameList = conDocTypeReceivableBillMapper.selectList(new QueryWrapper<ConDocTypeReceivableBill>().lambda()
                .eq(ConDocTypeReceivableBill::getName, conDocTypeReceivableBill.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDocTypeReceivableBill.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDocTypeReceivableBill.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDocTypeReceivableBill response = conDocTypeReceivableBillMapper.selectConDocTypeReceivableBillById(conDocTypeReceivableBill.getSid());
        int row = conDocTypeReceivableBillMapper.updateAllById(conDocTypeReceivableBill);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDocTypeReceivableBill.getSid(), BusinessType.CHANGE.getValue(), response, conDocTypeReceivableBill, TITLE);
        }
        return row;
    }

    /**
     * 批量删除单据类型_收款单
     *
     * @param sids 需要删除的单据类型_收款单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDocTypeReceivableBillByIds(List<Long> sids) {
        return conDocTypeReceivableBillMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDocTypeReceivableBill
    * @return
    */
    @Override
    public int changeStatus(ConDocTypeReceivableBill conDocTypeReceivableBill){
        int row=0;
        Long[] sids=conDocTypeReceivableBill.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeReceivableBill.setSid(id);
                row=conDocTypeReceivableBillMapper.updateById( conDocTypeReceivableBill);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDocTypeReceivableBill.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDocTypeReceivableBill.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDocTypeReceivableBill
     * @return
     */
    @Override
    public int check(ConDocTypeReceivableBill conDocTypeReceivableBill){
        int row=0;
        Long[] sids=conDocTypeReceivableBill.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDocTypeReceivableBill.setSid(id);
                row=conDocTypeReceivableBillMapper.updateById( conDocTypeReceivableBill);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDocTypeReceivableBill.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConDocTypeReceivableBill> getConDocTypeReceivableBillList(ConDocTypeReceivableBill conDocType) {
        return conDocTypeReceivableBillMapper.getConDocTypeReceivableBillList(conDocType);
    }
}
