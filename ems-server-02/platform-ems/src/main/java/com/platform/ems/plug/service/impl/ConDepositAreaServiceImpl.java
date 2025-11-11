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
import com.platform.ems.plug.domain.ConDepositArea;
import com.platform.ems.plug.mapper.ConDepositAreaMapper;
import com.platform.ems.plug.service.IConDepositAreaService;
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
 * 投料区域Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConDepositAreaServiceImpl extends ServiceImpl<ConDepositAreaMapper,ConDepositArea>  implements IConDepositAreaService {
    @Autowired
    private ConDepositAreaMapper conDepositAreaMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "投料区域";
    /**
     * 查询投料区域
     *
     * @param sid 投料区域ID
     * @return 投料区域
     */
    @Override
    public ConDepositArea selectConDepositAreaById(Long sid) {
        ConDepositArea conDepositArea = conDepositAreaMapper.selectConDepositAreaById(sid);
        MongodbUtil.find(conDepositArea);
        return  conDepositArea;
    }

    /**
     * 查询投料区域列表
     *
     * @param conDepositArea 投料区域
     * @return 投料区域
     */
    @Override
    public List<ConDepositArea> selectConDepositAreaList(ConDepositArea conDepositArea) {
        return conDepositAreaMapper.selectConDepositAreaList(conDepositArea);
    }

    /**
     * 新增投料区域
     * 需要注意编码重复校验
     * @param conDepositArea 投料区域
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDepositArea(ConDepositArea conDepositArea) {
        List<ConDepositArea> codeList = conDepositAreaMapper.selectList(new QueryWrapper<ConDepositArea>().lambda()
                .eq(ConDepositArea::getCode, conDepositArea.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)){
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDepositArea> nameList = conDepositAreaMapper.selectList(new QueryWrapper<ConDepositArea>().lambda()
                .eq(ConDepositArea::getName, conDepositArea.getName()));
        if (CollectionUtil.isNotEmpty(nameList)){
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conDepositAreaMapper.insert(conDepositArea);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDepositArea.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改投料区域
     *
     * @param conDepositArea 投料区域
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDepositArea(ConDepositArea conDepositArea) {
        ConDepositArea response = conDepositAreaMapper.selectConDepositAreaById(conDepositArea.getSid());
        int row=conDepositAreaMapper.updateById(conDepositArea);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDepositArea.getSid(), BusinessType.UPDATE.getValue(), response,conDepositArea,TITLE);
        }
        return row;
    }

    /**
     * 变更投料区域
     *
     * @param conDepositArea 投料区域
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDepositArea(ConDepositArea conDepositArea) {
        List<ConDepositArea> nameList = conDepositAreaMapper.selectList(new QueryWrapper<ConDepositArea>().lambda()
                .eq(ConDepositArea::getName, conDepositArea.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDepositArea.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDepositArea.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDepositArea response = conDepositAreaMapper.selectConDepositAreaById(conDepositArea.getSid());
        int row = conDepositAreaMapper.updateAllById(conDepositArea);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDepositArea.getSid(), BusinessType.CHANGE.getValue(), response, conDepositArea, TITLE);
        }
        return row;
    }

    /**
     * 批量删除投料区域
     *
     * @param sids 需要删除的投料区域ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDepositAreaByIds(List<Long> sids) {
        return conDepositAreaMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDepositArea
    * @return
    */
    @Override
    public int changeStatus(ConDepositArea conDepositArea){
        int row=0;
        Long[] sids=conDepositArea.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDepositArea.setSid(id);
                row=conDepositAreaMapper.updateById( conDepositArea);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDepositArea.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDepositArea.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDepositArea
     * @return
     */
    @Override
    public int check(ConDepositArea conDepositArea){
        int row=0;
        Long[] sids=conDepositArea.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDepositArea.setSid(id);
                row=conDepositAreaMapper.updateById( conDepositArea);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDepositArea.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
