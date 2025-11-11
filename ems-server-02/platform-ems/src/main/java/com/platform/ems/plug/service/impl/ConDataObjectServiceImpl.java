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
import com.platform.ems.plug.domain.ConDataObject;
import com.platform.ems.plug.mapper.ConDataObjectMapper;
import com.platform.ems.plug.service.IConDataObjectService;
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
 * 数据对象Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@Service
@SuppressWarnings("all")
public class ConDataObjectServiceImpl extends ServiceImpl<ConDataObjectMapper,ConDataObject>  implements IConDataObjectService {
    @Autowired
    private ConDataObjectMapper conDataObjectMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "数据对象";
    /**
     * 查询数据对象
     *
     * @param sid 数据对象ID
     * @return 数据对象
     */
    @Override
    public ConDataObject selectConDataObjectById(Long sid) {
        ConDataObject conDataObject = conDataObjectMapper.selectConDataObjectById(sid);
        MongodbUtil.find(conDataObject);
        return  conDataObject;
    }

    /**
     * 查询数据对象列表
     *
     * @param conDataObject 数据对象
     * @return 数据对象
     */
    @Override
    public List<ConDataObject> selectConDataObjectList(ConDataObject conDataObject) {
        return conDataObjectMapper.selectConDataObjectList(conDataObject);
    }

    /**
     * 新增数据对象
     * 需要注意编码重复校验
     * @param conDataObject 数据对象
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDataObject(ConDataObject conDataObject) {
        List<ConDataObject> codeList = conDataObjectMapper.selectList(new QueryWrapper<ConDataObject>().lambda()
                .eq(ConDataObject::getCode, conDataObject.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)){
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDataObject> nameList = conDataObjectMapper.selectList(new QueryWrapper<ConDataObject>().lambda()
                .eq(ConDataObject::getName, conDataObject.getName()));
        if (CollectionUtil.isNotEmpty(nameList)){
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conDataObjectMapper.insert(conDataObject);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDataObject.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改数据对象
     *
     * @param conDataObject 数据对象
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDataObject(ConDataObject conDataObject) {
        ConDataObject response = conDataObjectMapper.selectConDataObjectById(conDataObject.getSid());
        int row=conDataObjectMapper.updateById(conDataObject);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDataObject.getSid(), BusinessType.UPDATE.getValue(), response,conDataObject,TITLE);
        }
        return row;
    }

    /**
     * 变更数据对象
     *
     * @param conDataObject 数据对象
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDataObject(ConDataObject conDataObject) {
        List<ConDataObject> nameList = conDataObjectMapper.selectList(new QueryWrapper<ConDataObject>().lambda()
                .eq(ConDataObject::getName, conDataObject.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDataObject.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDataObject.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDataObject response = conDataObjectMapper.selectConDataObjectById(conDataObject.getSid());
        int row = conDataObjectMapper.updateAllById(conDataObject);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDataObject.getSid(), BusinessType.CHANGE.getValue(), response, conDataObject, TITLE);
        }
        return row;
    }

    /**
     * 批量删除数据对象
     *
     * @param sids 需要删除的数据对象ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDataObjectByIds(List<Long> sids) {
        return conDataObjectMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDataObject
    * @return
    */
    @Override
    public int changeStatus(ConDataObject conDataObject){
        int row=0;
        Long[] sids=conDataObject.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDataObject.setSid(id);
                row=conDataObjectMapper.updateById( conDataObject);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDataObject.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDataObject.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDataObject
     * @return
     */
    @Override
    public int check(ConDataObject conDataObject){
        int row=0;
        Long[] sids=conDataObject.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDataObject.setSid(id);
                row=conDataObjectMapper.updateById( conDataObject);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDataObject.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
