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
import com.platform.ems.plug.domain.ConBuTypeSaleContract;
import com.platform.ems.plug.mapper.ConBuTypeSaleContractMapper;
import com.platform.ems.plug.service.IConBuTypeSaleContractService;
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
 * 业务类型_销售合同信息Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeSaleContractServiceImpl extends ServiceImpl<ConBuTypeSaleContractMapper,ConBuTypeSaleContract>  implements IConBuTypeSaleContractService {
    @Autowired
    private ConBuTypeSaleContractMapper conBuTypeSaleContractMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_销售合同信息";
    /**
     * 查询业务类型_销售合同信息
     *
     * @param sid 业务类型_销售合同信息ID
     * @return 业务类型_销售合同信息
     */
    @Override
    public ConBuTypeSaleContract selectConBuTypeSaleContractById(Long sid) {
        ConBuTypeSaleContract conBuTypeSaleContract = conBuTypeSaleContractMapper.selectConBuTypeSaleContractById(sid);
        MongodbUtil.find(conBuTypeSaleContract);
        return  conBuTypeSaleContract;
    }

    /**
     * 查询业务类型_销售合同信息列表
     *
     * @param conBuTypeSaleContract 业务类型_销售合同信息
     * @return 业务类型_销售合同信息
     */
    @Override
    public List<ConBuTypeSaleContract> selectConBuTypeSaleContractList(ConBuTypeSaleContract conBuTypeSaleContract) {
        return conBuTypeSaleContractMapper.selectConBuTypeSaleContractList(conBuTypeSaleContract);
    }

    /**
     * 新增业务类型_销售合同信息
     * 需要注意编码重复校验
     * @param conBuTypeSaleContract 业务类型_销售合同信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeSaleContract(ConBuTypeSaleContract conBuTypeSaleContract) {
        List<ConBuTypeSaleContract> codeList = conBuTypeSaleContractMapper.selectList(new QueryWrapper<ConBuTypeSaleContract>().lambda()
                .eq(ConBuTypeSaleContract::getCode, conBuTypeSaleContract.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeSaleContract> nameList = conBuTypeSaleContractMapper.selectList(new QueryWrapper<ConBuTypeSaleContract>().lambda()
                .eq(ConBuTypeSaleContract::getName, conBuTypeSaleContract.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conBuTypeSaleContractMapper.insert(conBuTypeSaleContract);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeSaleContract.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_销售合同信息
     *
     * @param conBuTypeSaleContract 业务类型_销售合同信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeSaleContract(ConBuTypeSaleContract conBuTypeSaleContract) {
        ConBuTypeSaleContract response = conBuTypeSaleContractMapper.selectConBuTypeSaleContractById(conBuTypeSaleContract.getSid());
        int row=conBuTypeSaleContractMapper.updateById(conBuTypeSaleContract);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeSaleContract.getSid(), BusinessType.UPDATE.getValue(), response,conBuTypeSaleContract,TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_销售合同信息
     *
     * @param conBuTypeSaleContract 业务类型_销售合同信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeSaleContract(ConBuTypeSaleContract conBuTypeSaleContract) {
        List<ConBuTypeSaleContract> nameList = conBuTypeSaleContractMapper.selectList(new QueryWrapper<ConBuTypeSaleContract>().lambda()
                .eq(ConBuTypeSaleContract::getName, conBuTypeSaleContract.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBuTypeSaleContract.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeSaleContract.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeSaleContract response = conBuTypeSaleContractMapper.selectConBuTypeSaleContractById(conBuTypeSaleContract.getSid());
        int row = conBuTypeSaleContractMapper.updateAllById(conBuTypeSaleContract);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeSaleContract.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeSaleContract, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_销售合同信息
     *
     * @param sids 需要删除的业务类型_销售合同信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeSaleContractByIds(List<Long> sids) {
        return conBuTypeSaleContractMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conBuTypeSaleContract
    * @return
    */
    @Override
    public int changeStatus(ConBuTypeSaleContract conBuTypeSaleContract){
        int row=0;
        Long[] sids=conBuTypeSaleContract.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeSaleContract.setSid(id);
                row=conBuTypeSaleContractMapper.updateById( conBuTypeSaleContract);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conBuTypeSaleContract.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conBuTypeSaleContract.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conBuTypeSaleContract
     * @return
     */
    @Override
    public int check(ConBuTypeSaleContract conBuTypeSaleContract){
        int row=0;
        Long[] sids=conBuTypeSaleContract.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeSaleContract.setSid(id);
                row=conBuTypeSaleContractMapper.updateById( conBuTypeSaleContract);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conBuTypeSaleContract.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
