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
import com.platform.ems.plug.domain.ConBuTypeReceivableBill;
import com.platform.ems.plug.mapper.ConBuTypeReceivableBillMapper;
import com.platform.ems.plug.service.IConBuTypeReceivableBillService;
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
 * 业务类型_收款单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeReceivableBillServiceImpl extends ServiceImpl<ConBuTypeReceivableBillMapper,ConBuTypeReceivableBill>  implements IConBuTypeReceivableBillService {
    @Autowired
    private ConBuTypeReceivableBillMapper conBuTypeReceivableBillMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_收款单";
    /**
     * 查询业务类型_收款单
     *
     * @param sid 业务类型_收款单ID
     * @return 业务类型_收款单
     */
    @Override
    public ConBuTypeReceivableBill selectConBuTypeReceivableBillById(Long sid) {
        ConBuTypeReceivableBill conBuTypeReceivableBill = conBuTypeReceivableBillMapper.selectConBuTypeReceivableBillById(sid);
        MongodbUtil.find(conBuTypeReceivableBill);
        return  conBuTypeReceivableBill;
    }

    /**
     * 查询业务类型_收款单列表
     *
     * @param conBuTypeReceivableBill 业务类型_收款单
     * @return 业务类型_收款单
     */
    @Override
    public List<ConBuTypeReceivableBill> selectConBuTypeReceivableBillList(ConBuTypeReceivableBill conBuTypeReceivableBill) {
        return conBuTypeReceivableBillMapper.selectConBuTypeReceivableBillList(conBuTypeReceivableBill);
    }

    /**
     * 新增业务类型_收款单
     * 需要注意编码重复校验
     * @param conBuTypeReceivableBill 业务类型_收款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeReceivableBill(ConBuTypeReceivableBill conBuTypeReceivableBill) {
        List<ConBuTypeReceivableBill> codeList = conBuTypeReceivableBillMapper.selectList(new QueryWrapper<ConBuTypeReceivableBill>().lambda()
                .eq(ConBuTypeReceivableBill::getCode, conBuTypeReceivableBill.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeReceivableBill> nameList = conBuTypeReceivableBillMapper.selectList(new QueryWrapper<ConBuTypeReceivableBill>().lambda()
                .eq(ConBuTypeReceivableBill::getName, conBuTypeReceivableBill.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conBuTypeReceivableBillMapper.insert(conBuTypeReceivableBill);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeReceivableBill.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_收款单
     *
     * @param conBuTypeReceivableBill 业务类型_收款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeReceivableBill(ConBuTypeReceivableBill conBuTypeReceivableBill) {
        ConBuTypeReceivableBill response = conBuTypeReceivableBillMapper.selectConBuTypeReceivableBillById(conBuTypeReceivableBill.getSid());
        int row=conBuTypeReceivableBillMapper.updateById(conBuTypeReceivableBill);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeReceivableBill.getSid(), BusinessType.UPDATE.getValue(), response,conBuTypeReceivableBill,TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_收款单
     *
     * @param conBuTypeReceivableBill 业务类型_收款单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeReceivableBill(ConBuTypeReceivableBill conBuTypeReceivableBill) {
        List<ConBuTypeReceivableBill> nameList = conBuTypeReceivableBillMapper.selectList(new QueryWrapper<ConBuTypeReceivableBill>().lambda()
                .eq(ConBuTypeReceivableBill::getName, conBuTypeReceivableBill.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBuTypeReceivableBill.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeReceivableBill.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeReceivableBill response = conBuTypeReceivableBillMapper.selectConBuTypeReceivableBillById(conBuTypeReceivableBill.getSid());
        int row = conBuTypeReceivableBillMapper.updateAllById(conBuTypeReceivableBill);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeReceivableBill.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeReceivableBill, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_收款单
     *
     * @param sids 需要删除的业务类型_收款单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeReceivableBillByIds(List<Long> sids) {
        return conBuTypeReceivableBillMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conBuTypeReceivableBill
    * @return
    */
    @Override
    public int changeStatus(ConBuTypeReceivableBill conBuTypeReceivableBill){
        int row=0;
        Long[] sids=conBuTypeReceivableBill.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeReceivableBill.setSid(id);
                row=conBuTypeReceivableBillMapper.updateById( conBuTypeReceivableBill);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conBuTypeReceivableBill.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conBuTypeReceivableBill.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conBuTypeReceivableBill
     * @return
     */
    @Override
    public int check(ConBuTypeReceivableBill conBuTypeReceivableBill){
        int row=0;
        Long[] sids=conBuTypeReceivableBill.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeReceivableBill.setSid(id);
                row=conBuTypeReceivableBillMapper.updateById( conBuTypeReceivableBill);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conBuTypeReceivableBill.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConBuTypeReceivableBill> getConBuTypeReceivableBillList() {
        return conBuTypeReceivableBillMapper.getConBuTypeReceivableBillList();
    }


}
