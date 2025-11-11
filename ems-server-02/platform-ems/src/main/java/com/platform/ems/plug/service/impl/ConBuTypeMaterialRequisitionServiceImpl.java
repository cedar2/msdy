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
import com.platform.ems.plug.domain.ConBuTypeMaterialRequisition;
import com.platform.ems.plug.mapper.ConBuTypeMaterialRequisitionMapper;
import com.platform.ems.plug.service.IConBuTypeMaterialRequisitionService;
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
 * 业务类型_领退料单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBuTypeMaterialRequisitionServiceImpl extends ServiceImpl<ConBuTypeMaterialRequisitionMapper,ConBuTypeMaterialRequisition>  implements IConBuTypeMaterialRequisitionService {
    @Autowired
    private ConBuTypeMaterialRequisitionMapper conBuTypeMaterialRequisitionMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "业务类型_领退料单";
    /**
     * 查询业务类型_领退料单
     *
     * @param sid 业务类型_领退料单ID
     * @return 业务类型_领退料单
     */
    @Override
    public ConBuTypeMaterialRequisition selectConBuTypeMaterialRequisitionById(Long sid) {
        ConBuTypeMaterialRequisition conBuTypeMaterialRequisition = conBuTypeMaterialRequisitionMapper.selectConBuTypeMaterialRequisitionById(sid);
        MongodbUtil.find(conBuTypeMaterialRequisition);
        return  conBuTypeMaterialRequisition;
    }

    @Override
    public List<ConBuTypeMaterialRequisition> getList() {
        return conBuTypeMaterialRequisitionMapper.getList();
    }

    /**
     * 查询业务类型_领退料单列表
     *
     * @param conBuTypeMaterialRequisition 业务类型_领退料单
     * @return 业务类型_领退料单
     */
    @Override
    public List<ConBuTypeMaterialRequisition> selectConBuTypeMaterialRequisitionList(ConBuTypeMaterialRequisition conBuTypeMaterialRequisition) {
        return conBuTypeMaterialRequisitionMapper.selectConBuTypeMaterialRequisitionList(conBuTypeMaterialRequisition);
    }

    /**
     * 新增业务类型_领退料单
     * 需要注意编码重复校验
     * @param conBuTypeMaterialRequisition 业务类型_领退料单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypeMaterialRequisition(ConBuTypeMaterialRequisition conBuTypeMaterialRequisition) {
        List<ConBuTypeMaterialRequisition> codeList = conBuTypeMaterialRequisitionMapper.selectList(new QueryWrapper<ConBuTypeMaterialRequisition>().lambda()
                .eq(ConBuTypeMaterialRequisition::getCode, conBuTypeMaterialRequisition.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConBuTypeMaterialRequisition> nameList = conBuTypeMaterialRequisitionMapper.selectList(new QueryWrapper<ConBuTypeMaterialRequisition>().lambda()
                .eq(ConBuTypeMaterialRequisition::getName, conBuTypeMaterialRequisition.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conBuTypeMaterialRequisitionMapper.insert(conBuTypeMaterialRequisition);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypeMaterialRequisition.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_领退料单
     *
     * @param conBuTypeMaterialRequisition 业务类型_领退料单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypeMaterialRequisition(ConBuTypeMaterialRequisition conBuTypeMaterialRequisition) {
        ConBuTypeMaterialRequisition response = conBuTypeMaterialRequisitionMapper.selectConBuTypeMaterialRequisitionById(conBuTypeMaterialRequisition.getSid());
        int row=conBuTypeMaterialRequisitionMapper.updateById(conBuTypeMaterialRequisition);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeMaterialRequisition.getSid(), BusinessType.UPDATE.getValue(), response,conBuTypeMaterialRequisition,TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_领退料单
     *
     * @param conBuTypeMaterialRequisition 业务类型_领退料单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypeMaterialRequisition(ConBuTypeMaterialRequisition conBuTypeMaterialRequisition) {
        List<ConBuTypeMaterialRequisition> nameList = conBuTypeMaterialRequisitionMapper.selectList(new QueryWrapper<ConBuTypeMaterialRequisition>().lambda()
                .eq(ConBuTypeMaterialRequisition::getName, conBuTypeMaterialRequisition.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conBuTypeMaterialRequisition.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypeMaterialRequisition.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypeMaterialRequisition response = conBuTypeMaterialRequisitionMapper.selectConBuTypeMaterialRequisitionById(conBuTypeMaterialRequisition.getSid());
        int row = conBuTypeMaterialRequisitionMapper.updateAllById(conBuTypeMaterialRequisition);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conBuTypeMaterialRequisition.getSid(), BusinessType.CHANGE.getValue(), response, conBuTypeMaterialRequisition, TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_领退料单
     *
     * @param sids 需要删除的业务类型_领退料单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypeMaterialRequisitionByIds(List<Long> sids) {
        return conBuTypeMaterialRequisitionMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conBuTypeMaterialRequisition
    * @return
    */
    @Override
    public int changeStatus(ConBuTypeMaterialRequisition conBuTypeMaterialRequisition){
        int row=0;
        Long[] sids=conBuTypeMaterialRequisition.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeMaterialRequisition.setSid(id);
                row=conBuTypeMaterialRequisitionMapper.updateById( conBuTypeMaterialRequisition);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conBuTypeMaterialRequisition.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conBuTypeMaterialRequisition.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conBuTypeMaterialRequisition
     * @return
     */
    @Override
    public int check(ConBuTypeMaterialRequisition conBuTypeMaterialRequisition){
        int row=0;
        Long[] sids=conBuTypeMaterialRequisition.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conBuTypeMaterialRequisition.setSid(id);
                row=conBuTypeMaterialRequisitionMapper.updateById( conBuTypeMaterialRequisition);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conBuTypeMaterialRequisition.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
