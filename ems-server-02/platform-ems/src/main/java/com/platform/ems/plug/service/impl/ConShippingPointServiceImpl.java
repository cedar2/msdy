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
import com.platform.ems.plug.domain.ConShippingPoint;
import com.platform.ems.plug.mapper.ConShippingPointMapper;
import com.platform.ems.plug.service.IConShippingPointService;
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
 * 装运点Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConShippingPointServiceImpl extends ServiceImpl<ConShippingPointMapper,ConShippingPoint>  implements IConShippingPointService {
    @Autowired
    private ConShippingPointMapper conShippingPointMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "装运点";
    /**
     * 查询装运点
     *
     * @param sid 装运点ID
     * @return 装运点
     */
    @Override
    public ConShippingPoint selectConShippingPointById(Long sid) {
        ConShippingPoint conShippingPoint = conShippingPointMapper.selectConShippingPointById(sid);
        MongodbUtil.find(conShippingPoint);
        return  conShippingPoint;
    }

    /**
     * 查询装运点列表
     *
     * @param conShippingPoint 装运点
     * @return 装运点
     */
    @Override
    public List<ConShippingPoint> selectConShippingPointList(ConShippingPoint conShippingPoint) {
        return conShippingPointMapper.selectConShippingPointList(conShippingPoint);
    }

    /**
     * 新增装运点
     * 需要注意编码重复校验
     * @param conShippingPoint 装运点
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConShippingPoint(ConShippingPoint conShippingPoint) {
        List<ConShippingPoint> codeList = conShippingPointMapper.selectList(new QueryWrapper<ConShippingPoint>().lambda()
                .eq(ConShippingPoint::getCode, conShippingPoint.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConShippingPoint> nameList = conShippingPointMapper.selectList(new QueryWrapper<ConShippingPoint>().lambda()
                .eq(ConShippingPoint::getName, conShippingPoint.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conShippingPointMapper.insert(conShippingPoint);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conShippingPoint.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改装运点
     *
     * @param conShippingPoint 装运点
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConShippingPoint(ConShippingPoint conShippingPoint) {
        ConShippingPoint response = conShippingPointMapper.selectConShippingPointById(conShippingPoint.getSid());
        int row=conShippingPointMapper.updateById(conShippingPoint);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conShippingPoint.getSid(), BusinessType.UPDATE.getValue(), response,conShippingPoint,TITLE);
        }
        return row;
    }

    /**
     * 变更装运点
     *
     * @param conShippingPoint 装运点
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConShippingPoint(ConShippingPoint conShippingPoint) {
        List<ConShippingPoint> nameList = conShippingPointMapper.selectList(new QueryWrapper<ConShippingPoint>().lambda()
                .eq(ConShippingPoint::getName, conShippingPoint.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conShippingPoint.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conShippingPoint.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConShippingPoint response = conShippingPointMapper.selectConShippingPointById(conShippingPoint.getSid());
        int row = conShippingPointMapper.updateAllById(conShippingPoint);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conShippingPoint.getSid(), BusinessType.CHANGE.getValue(), response, conShippingPoint, TITLE);
        }
        return row;
    }

    /**
     * 批量删除装运点
     *
     * @param sids 需要删除的装运点ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConShippingPointByIds(List<Long> sids) {
        return conShippingPointMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conShippingPoint
    * @return
    */
    @Override
    public int changeStatus(ConShippingPoint conShippingPoint){
        int row=0;
        Long[] sids=conShippingPoint.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conShippingPoint.setSid(id);
                row=conShippingPointMapper.updateById( conShippingPoint);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conShippingPoint.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conShippingPoint.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conShippingPoint
     * @return
     */
    @Override
    public int check(ConShippingPoint conShippingPoint){
        int row=0;
        Long[] sids=conShippingPoint.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conShippingPoint.setSid(id);
                row=conShippingPointMapper.updateById( conShippingPoint);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conShippingPoint.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
