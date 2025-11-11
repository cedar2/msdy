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
import com.platform.ems.plug.domain.ConBuTypePayBill;
import com.platform.ems.plug.mapper.ConBuTypePayBillMapper;
import com.platform.ems.plug.service.IConBuTypePayBillService;
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
 * 业务类型_付款单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBuTypePayBillServiceImpl extends ServiceImpl<ConBuTypePayBillMapper,ConBuTypePayBill>  implements IConBuTypePayBillService {
    @Autowired
    private ConBuTypePayBillMapper conBuTypePayBillMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_付款单";
    /**
     * 查询业务类型_付款单
     *
     * @param sid 业务类型_付款单ID
     * @return 业务类型_付款单
     */
    @Override
    public ConBuTypePayBill selectConBuTypePayBillById(Long sid) {
        ConBuTypePayBill conBuTypePayBill = conBuTypePayBillMapper.selectConBuTypePayBillById(sid);
        MongodbUtil.find(conBuTypePayBill);
        return  conBuTypePayBill;
    }

    /**
     * 查询业务类型_付款单列表
     *
     * @param conBuTypePayBill 业务类型_付款单
     * @return 业务类型_付款单
     */
    @Override
    public List<ConBuTypePayBill> selectConBuTypePayBillList(ConBuTypePayBill conBuTypePayBill) {
        return conBuTypePayBillMapper.selectConBuTypePayBillList(conBuTypePayBill);
    }

    /**
     * 新增业务类型_付款单
     * 需要注意编码重复校验
     * @param conBuTypePayBill 业务类型_付款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypePayBill(ConBuTypePayBill conBuTypePayBill) {
        List<ConBuTypePayBill> codeList = conBuTypePayBillMapper.selectList(new QueryWrapper<ConBuTypePayBill>().lambda()
                .eq(ConBuTypePayBill::getCode, conBuTypePayBill.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypePayBill> nameList = conBuTypePayBillMapper.selectList(new QueryWrapper<ConBuTypePayBill>().lambda()
                .eq(ConBuTypePayBill::getName, conBuTypePayBill.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conBuTypePayBillMapper.insert(conBuTypePayBill);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypePayBill.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_付款单
     *
     * @param conBuTypePayBill 业务类型_付款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypePayBill(ConBuTypePayBill conBuTypePayBill) {
        ConBuTypePayBill response = conBuTypePayBillMapper.selectConBuTypePayBillById(conBuTypePayBill.getSid());
        int row=conBuTypePayBillMapper.updateById(conBuTypePayBill);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBuTypePayBill.getSid(), BusinessType.UPDATE.getValue(), response,conBuTypePayBill,TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_付款单
     *
     * @param conBuTypePayBill 业务类型_付款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypePayBill(ConBuTypePayBill conBuTypePayBill) {
        List<ConBuTypePayBill> nameList = conBuTypePayBillMapper.selectList(new QueryWrapper<ConBuTypePayBill>().lambda()
                .eq(ConBuTypePayBill::getName, conBuTypePayBill.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBuTypePayBill.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypePayBill.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypePayBill response = conBuTypePayBillMapper.selectConBuTypePayBillById(conBuTypePayBill.getSid());
        int row = conBuTypePayBillMapper.updateAllById(conBuTypePayBill);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypePayBill.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypePayBill, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_付款单
     *
     * @param sids 需要删除的业务类型_付款单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypePayBillByIds(List<Long> sids) {
        return conBuTypePayBillMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conBuTypePayBill
    * @return
    */
    @Override
    public int changeStatus(ConBuTypePayBill conBuTypePayBill){
        int row=0;
        Long[] sids=conBuTypePayBill.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypePayBill.setSid(id);
                row=conBuTypePayBillMapper.updateById( conBuTypePayBill);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conBuTypePayBill.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conBuTypePayBill.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conBuTypePayBill
     * @return
     */
    @Override
    public int check(ConBuTypePayBill conBuTypePayBill){
        int row=0;
        Long[] sids=conBuTypePayBill.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypePayBill.setSid(id);
                row=conBuTypePayBillMapper.updateById( conBuTypePayBill);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conBuTypePayBill.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConBuTypePayBill> getConBuTypePayBillList() {
        return conBuTypePayBillMapper.getConBuTypePayBillList();
    }
}
