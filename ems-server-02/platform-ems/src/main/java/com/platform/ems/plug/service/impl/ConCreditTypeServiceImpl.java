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
import com.platform.ems.plug.domain.ConCreditType;
import com.platform.ems.plug.mapper.ConCreditTypeMapper;
import com.platform.ems.plug.service.IConCreditTypeService;
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
 * 信用类型Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConCreditTypeServiceImpl extends ServiceImpl<ConCreditTypeMapper,ConCreditType>  implements IConCreditTypeService {
    @Autowired
    private ConCreditTypeMapper conCreditTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "信用类型";
    /**
     * 查询信用类型
     *
     * @param sid 信用类型ID
     * @return 信用类型
     */
    @Override
    public ConCreditType selectConCreditTypeById(Long sid) {
        ConCreditType conCreditType = conCreditTypeMapper.selectConCreditTypeById(sid);
        MongodbUtil.find(conCreditType);
        return  conCreditType;
    }

    /**
     * 查询信用类型列表
     *
     * @param conCreditType 信用类型
     * @return 信用类型
     */
    @Override
    public List<ConCreditType> selectConCreditTypeList(ConCreditType conCreditType) {
        return conCreditTypeMapper.selectConCreditTypeList(conCreditType);
    }

    /**
     * 新增信用类型
     * 需要注意编码重复校验
     * @param conCreditType 信用类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConCreditType(ConCreditType conCreditType) {
        List<ConCreditType> codeList = conCreditTypeMapper.selectList(new QueryWrapper<ConCreditType>().lambda()
                .eq(ConCreditType::getCode, conCreditType.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConCreditType> nameList = conCreditTypeMapper.selectList(new QueryWrapper<ConCreditType>().lambda()
                .eq(ConCreditType::getName, conCreditType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conCreditTypeMapper.insert(conCreditType);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conCreditType.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改信用类型
     *
     * @param conCreditType 信用类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConCreditType(ConCreditType conCreditType) {
        ConCreditType response = conCreditTypeMapper.selectConCreditTypeById(conCreditType.getSid());
        int row=conCreditTypeMapper.updateById(conCreditType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conCreditType.getSid(), BusinessType.UPDATE.getValue(), response,conCreditType,TITLE);
        }
        return row;
    }

    /**
     * 变更信用类型
     *
     * @param conCreditType 信用类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConCreditType(ConCreditType conCreditType) {
        List<ConCreditType> nameList = conCreditTypeMapper.selectList(new QueryWrapper<ConCreditType>().lambda()
                .eq(ConCreditType::getName, conCreditType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conCreditType.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conCreditType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConCreditType response = conCreditTypeMapper.selectConCreditTypeById(conCreditType.getSid());
        int row = conCreditTypeMapper.updateAllById(conCreditType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conCreditType.getSid(), BusinessType.CHANGE.getValue(), response, conCreditType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除信用类型
     *
     * @param sids 需要删除的信用类型ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConCreditTypeByIds(List<Long> sids) {
        return conCreditTypeMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conCreditType
    * @return
    */
    @Override
    public int changeStatus(ConCreditType conCreditType){
        int row=0;
        Long[] sids=conCreditType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conCreditType.setSid(id);
                row=conCreditTypeMapper.updateById( conCreditType);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conCreditType.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conCreditType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conCreditType
     * @return
     */
    @Override
    public int check(ConCreditType conCreditType){
        int row=0;
        Long[] sids=conCreditType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conCreditType.setSid(id);
                row=conCreditTypeMapper.updateById( conCreditType);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conCreditType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
