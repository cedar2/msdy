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
import com.platform.ems.plug.domain.ConOrderBatch;
import com.platform.ems.plug.mapper.ConOrderBatchMapper;
import com.platform.ems.plug.service.IConOrderBatchService;
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
 * 下单批次Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConOrderBatchServiceImpl extends ServiceImpl<ConOrderBatchMapper,ConOrderBatch>  implements IConOrderBatchService {
    @Autowired
    private ConOrderBatchMapper conOrderBatchMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "下单批次";
    /**
     * 查询下单批次
     *
     * @param sid 下单批次ID
     * @return 下单批次
     */
    @Override
    public ConOrderBatch selectConOrderBatchById(Long sid) {
        ConOrderBatch conOrderBatch = conOrderBatchMapper.selectConOrderBatchById(sid);
        MongodbUtil.find(conOrderBatch);
        return  conOrderBatch;
    }

    /**
     * 查询下单批次列表
     *
     * @param conOrderBatch 下单批次
     * @return 下单批次
     */
    @Override
    public List<ConOrderBatch> selectConOrderBatchList(ConOrderBatch conOrderBatch) {
        return conOrderBatchMapper.selectConOrderBatchList(conOrderBatch);
    }

    /**
     * 新增下单批次
     * 需要注意编码重复校验
     * @param conOrderBatch 下单批次
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConOrderBatch(ConOrderBatch conOrderBatch) {
        List<ConOrderBatch> codeList = conOrderBatchMapper.selectList(new QueryWrapper<ConOrderBatch>().lambda()
                .eq(ConOrderBatch::getCode, conOrderBatch.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConOrderBatch> nameList = conOrderBatchMapper.selectList(new QueryWrapper<ConOrderBatch>().lambda()
                .eq(ConOrderBatch::getName, conOrderBatch.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conOrderBatchMapper.insert(conOrderBatch);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conOrderBatch.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改下单批次
     *
     * @param conOrderBatch 下单批次
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConOrderBatch(ConOrderBatch conOrderBatch) {
        ConOrderBatch response = conOrderBatchMapper.selectConOrderBatchById(conOrderBatch.getSid());
        int row=conOrderBatchMapper.updateById(conOrderBatch);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conOrderBatch.getSid(), BusinessType.UPDATE.getValue(), response,conOrderBatch,TITLE);
        }
        return row;
    }

    /**
     * 变更下单批次
     *
     * @param conOrderBatch 下单批次
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConOrderBatch(ConOrderBatch conOrderBatch) {
        List<ConOrderBatch> nameList = conOrderBatchMapper.selectList(new QueryWrapper<ConOrderBatch>().lambda()
                .eq(ConOrderBatch::getName, conOrderBatch.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conOrderBatch.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conOrderBatch.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConOrderBatch response = conOrderBatchMapper.selectConOrderBatchById(conOrderBatch.getSid());
        int row = conOrderBatchMapper.updateAllById(conOrderBatch);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conOrderBatch.getSid(), BusinessType.CHANGE.getValue(), response, conOrderBatch, TITLE);
        }
        return row;
    }

    /**
     * 批量删除下单批次
     *
     * @param sids 需要删除的下单批次ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConOrderBatchByIds(List<Long> sids) {
        return conOrderBatchMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conOrderBatch
    * @return
    */
    @Override
    public int changeStatus(ConOrderBatch conOrderBatch){
        int row=0;
        Long[] sids=conOrderBatch.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conOrderBatch.setSid(id);
                row=conOrderBatchMapper.updateById( conOrderBatch);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conOrderBatch.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conOrderBatch.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conOrderBatch
     * @return
     */
    @Override
    public int check(ConOrderBatch conOrderBatch){
        int row=0;
        Long[] sids=conOrderBatch.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conOrderBatch.setSid(id);
                row=conOrderBatchMapper.updateById( conOrderBatch);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conOrderBatch.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    /**
     * 下单批次下拉框
     */
    @Override
    public List<ConOrderBatch> getList() {
        return conOrderBatchMapper.getList();
    }
}
