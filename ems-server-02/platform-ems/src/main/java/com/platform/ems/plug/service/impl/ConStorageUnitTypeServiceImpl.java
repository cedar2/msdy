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
import com.platform.ems.plug.domain.ConStorageUnitType;
import com.platform.ems.plug.mapper.ConStorageUnitTypeMapper;
import com.platform.ems.plug.service.IConStorageUnitTypeService;
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
 * 托盘类型Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConStorageUnitTypeServiceImpl extends ServiceImpl<ConStorageUnitTypeMapper,ConStorageUnitType>  implements IConStorageUnitTypeService {
    @Autowired
    private ConStorageUnitTypeMapper conStorageUnitTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "托盘类型";
    /**
     * 查询托盘类型
     *
     * @param sid 托盘类型ID
     * @return 托盘类型
     */
    @Override
    public ConStorageUnitType selectConStorageUnitTypeById(Long sid) {
        ConStorageUnitType conStorageUnitType = conStorageUnitTypeMapper.selectConStorageUnitTypeById(sid);
        MongodbUtil.find(conStorageUnitType);
        return  conStorageUnitType;
    }

    /**
     * 查询托盘类型列表
     *
     * @param conStorageUnitType 托盘类型
     * @return 托盘类型
     */
    @Override
    public List<ConStorageUnitType> selectConStorageUnitTypeList(ConStorageUnitType conStorageUnitType) {
        return conStorageUnitTypeMapper.selectConStorageUnitTypeList(conStorageUnitType);
    }

    /**
     * 新增托盘类型
     * 需要注意编码重复校验
     * @param conStorageUnitType 托盘类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConStorageUnitType(ConStorageUnitType conStorageUnitType) {
        List<ConStorageUnitType> codeList = conStorageUnitTypeMapper.selectList(new QueryWrapper<ConStorageUnitType>().lambda()
                .eq(ConStorageUnitType::getCode, conStorageUnitType.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConStorageUnitType> nameList = conStorageUnitTypeMapper.selectList(new QueryWrapper<ConStorageUnitType>().lambda()
                .eq(ConStorageUnitType::getName, conStorageUnitType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conStorageUnitTypeMapper.insert(conStorageUnitType);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conStorageUnitType.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改托盘类型
     *
     * @param conStorageUnitType 托盘类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConStorageUnitType(ConStorageUnitType conStorageUnitType) {
        ConStorageUnitType response = conStorageUnitTypeMapper.selectConStorageUnitTypeById(conStorageUnitType.getSid());
        int row=conStorageUnitTypeMapper.updateById(conStorageUnitType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conStorageUnitType.getSid(), BusinessType.UPDATE.getValue(), response,conStorageUnitType,TITLE);
        }
        return row;
    }

    /**
     * 变更托盘类型
     *
     * @param conStorageUnitType 托盘类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConStorageUnitType(ConStorageUnitType conStorageUnitType) {
        List<ConStorageUnitType> nameList = conStorageUnitTypeMapper.selectList(new QueryWrapper<ConStorageUnitType>().lambda()
                .eq(ConStorageUnitType::getName, conStorageUnitType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conStorageUnitType.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conStorageUnitType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConStorageUnitType response = conStorageUnitTypeMapper.selectConStorageUnitTypeById(conStorageUnitType.getSid());
        int row = conStorageUnitTypeMapper.updateAllById(conStorageUnitType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conStorageUnitType.getSid(), BusinessType.CHANGE.getValue(), response, conStorageUnitType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除托盘类型
     *
     * @param sids 需要删除的托盘类型ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConStorageUnitTypeByIds(List<Long> sids) {
        return conStorageUnitTypeMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conStorageUnitType
    * @return
    */
    @Override
    public int changeStatus(ConStorageUnitType conStorageUnitType){
        int row=0;
        Long[] sids=conStorageUnitType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conStorageUnitType.setSid(id);
                row=conStorageUnitTypeMapper.updateById( conStorageUnitType);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conStorageUnitType.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conStorageUnitType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conStorageUnitType
     * @return
     */
    @Override
    public int check(ConStorageUnitType conStorageUnitType){
        int row=0;
        Long[] sids=conStorageUnitType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conStorageUnitType.setSid(id);
                row=conStorageUnitTypeMapper.updateById( conStorageUnitType);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conStorageUnitType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
