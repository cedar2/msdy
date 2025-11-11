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
import com.platform.ems.plug.domain.ConBuTypePurchaseContract;
import com.platform.ems.plug.domain.ConBuTypePurchaseOrder;
import com.platform.ems.plug.mapper.ConBuTypePurchaseContractMapper;
import com.platform.ems.plug.service.IConBuTypePurchaseContractService;
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
 * 业务类型_采购合同信息Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBuTypePurchaseContractServiceImpl extends ServiceImpl<ConBuTypePurchaseContractMapper,ConBuTypePurchaseContract>  implements IConBuTypePurchaseContractService {
    @Autowired
    private ConBuTypePurchaseContractMapper conBuTypePurchaseContractMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_采购合同信息";
    /**
     * 查询业务类型_采购合同信息
     *
     * @param sid 业务类型_采购合同信息ID
     * @return 业务类型_采购合同信息
     */
    @Override
    public ConBuTypePurchaseContract selectConBuTypePurchaseContractById(Long sid) {
        ConBuTypePurchaseContract conBuTypePurchaseContract = conBuTypePurchaseContractMapper.selectConBuTypePurchaseContractById(sid);
        MongodbUtil.find(conBuTypePurchaseContract);
        return  conBuTypePurchaseContract;
    }

    /**
     * 查询业务类型_采购合同信息列表
     *
     * @param conBuTypePurchaseContract 业务类型_采购合同信息
     * @return 业务类型_采购合同信息
     */
    @Override
    public List<ConBuTypePurchaseContract> selectConBuTypePurchaseContractList(ConBuTypePurchaseContract conBuTypePurchaseContract) {
        return conBuTypePurchaseContractMapper.selectConBuTypePurchaseContractList(conBuTypePurchaseContract);
    }

    /**
     * 新增业务类型_采购合同信息
     * 需要注意编码重复校验
     * @param conBuTypePurchaseContract 业务类型_采购合同信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypePurchaseContract(ConBuTypePurchaseContract conBuTypePurchaseContract) {
        List<ConBuTypePurchaseContract> codeList = conBuTypePurchaseContractMapper.selectList(new QueryWrapper<ConBuTypePurchaseContract>().lambda()
                .eq(ConBuTypePurchaseContract::getCode, conBuTypePurchaseContract.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypePurchaseContract> nameList = conBuTypePurchaseContractMapper.selectList(new QueryWrapper<ConBuTypePurchaseContract>().lambda()
                .eq(ConBuTypePurchaseContract::getName, conBuTypePurchaseContract.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conBuTypePurchaseContractMapper.insert(conBuTypePurchaseContract);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypePurchaseContract.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_采购合同信息
     *
     * @param conBuTypePurchaseContract 业务类型_采购合同信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypePurchaseContract(ConBuTypePurchaseContract conBuTypePurchaseContract) {
        ConBuTypePurchaseContract response = conBuTypePurchaseContractMapper.selectConBuTypePurchaseContractById(conBuTypePurchaseContract.getSid());
        int row=conBuTypePurchaseContractMapper.updateById(conBuTypePurchaseContract);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBuTypePurchaseContract.getSid(), BusinessType.UPDATE.getValue(), response,conBuTypePurchaseContract,TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_采购合同信息
     *
     * @param conBuTypePurchaseContract 业务类型_采购合同信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypePurchaseContract(ConBuTypePurchaseContract conBuTypePurchaseContract) {
        List<ConBuTypePurchaseContract> nameList = conBuTypePurchaseContractMapper.selectList(new QueryWrapper<ConBuTypePurchaseContract>().lambda()
                .eq(ConBuTypePurchaseContract::getName, conBuTypePurchaseContract.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBuTypePurchaseContract.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypePurchaseContract.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypePurchaseContract response = conBuTypePurchaseContractMapper.selectConBuTypePurchaseContractById(conBuTypePurchaseContract.getSid());
        int row = conBuTypePurchaseContractMapper.updateAllById(conBuTypePurchaseContract);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypePurchaseContract.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypePurchaseContract, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_采购合同信息
     *
     * @param sids 需要删除的业务类型_采购合同信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypePurchaseContractByIds(List<Long> sids) {
        return conBuTypePurchaseContractMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conBuTypePurchaseContract
    * @return
    */
    @Override
    public int changeStatus(ConBuTypePurchaseContract conBuTypePurchaseContract){
        int row=0;
        Long[] sids=conBuTypePurchaseContract.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypePurchaseContract.setSid(id);
                row=conBuTypePurchaseContractMapper.updateById( conBuTypePurchaseContract);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conBuTypePurchaseContract.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conBuTypePurchaseContract.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conBuTypePurchaseContract
     * @return
     */
    @Override
    public int check(ConBuTypePurchaseContract conBuTypePurchaseContract){
        int row=0;
        Long[] sids=conBuTypePurchaseContract.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypePurchaseContract.setSid(id);
                row=conBuTypePurchaseContractMapper.updateById( conBuTypePurchaseContract);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conBuTypePurchaseContract.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
