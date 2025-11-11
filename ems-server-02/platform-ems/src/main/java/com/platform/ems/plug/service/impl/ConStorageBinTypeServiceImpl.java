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
import com.platform.ems.plug.domain.ConStorageBinType;
import com.platform.ems.plug.mapper.ConStorageBinTypeMapper;
import com.platform.ems.plug.service.IConStorageBinTypeService;
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
 * 仓位存储类型Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConStorageBinTypeServiceImpl extends ServiceImpl<ConStorageBinTypeMapper,ConStorageBinType>  implements IConStorageBinTypeService {
    @Autowired
    private ConStorageBinTypeMapper conStorageBinTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "仓位存储类型";
    /**
     * 查询仓位存储类型
     *
     * @param sid 仓位存储类型ID
     * @return 仓位存储类型
     */
    @Override
    public ConStorageBinType selectConStorageBinTypeById(Long sid) {
        ConStorageBinType conStorageBinType = conStorageBinTypeMapper.selectConStorageBinTypeById(sid);
        MongodbUtil.find(conStorageBinType);
        return  conStorageBinType;
    }

    /**
     * 查询仓位存储类型列表
     *
     * @param conStorageBinType 仓位存储类型
     * @return 仓位存储类型
     */
    @Override
    public List<ConStorageBinType> selectConStorageBinTypeList(ConStorageBinType conStorageBinType) {
        return conStorageBinTypeMapper.selectConStorageBinTypeList(conStorageBinType);
    }

    /**
     * 新增仓位存储类型
     * 需要注意编码重复校验
     * @param conStorageBinType 仓位存储类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConStorageBinType(ConStorageBinType conStorageBinType) {
        List<ConStorageBinType> codeList = conStorageBinTypeMapper.selectList(new QueryWrapper<ConStorageBinType>().lambda()
                .eq(ConStorageBinType::getCode, conStorageBinType.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConStorageBinType> nameList = conStorageBinTypeMapper.selectList(new QueryWrapper<ConStorageBinType>().lambda()
                .eq(ConStorageBinType::getName, conStorageBinType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conStorageBinTypeMapper.insert(conStorageBinType);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conStorageBinType.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改仓位存储类型
     *
     * @param conStorageBinType 仓位存储类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConStorageBinType(ConStorageBinType conStorageBinType) {
        ConStorageBinType response = conStorageBinTypeMapper.selectConStorageBinTypeById(conStorageBinType.getSid());
        int row=conStorageBinTypeMapper.updateById(conStorageBinType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conStorageBinType.getSid(), BusinessType.UPDATE.getValue(), response,conStorageBinType,TITLE);
        }
        return row;
    }

    /**
     * 变更仓位存储类型
     *
     * @param conStorageBinType 仓位存储类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConStorageBinType(ConStorageBinType conStorageBinType) {
        List<ConStorageBinType> nameList = conStorageBinTypeMapper.selectList(new QueryWrapper<ConStorageBinType>().lambda()
                .eq(ConStorageBinType::getName, conStorageBinType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conStorageBinType.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conStorageBinType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConStorageBinType response = conStorageBinTypeMapper.selectConStorageBinTypeById(conStorageBinType.getSid());
        int row = conStorageBinTypeMapper.updateAllById(conStorageBinType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conStorageBinType.getSid(), BusinessType.CHANGE.getValue(), response, conStorageBinType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除仓位存储类型
     *
     * @param sids 需要删除的仓位存储类型ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConStorageBinTypeByIds(List<Long> sids) {
        return conStorageBinTypeMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conStorageBinType
    * @return
    */
    @Override
    public int changeStatus(ConStorageBinType conStorageBinType){
        int row=0;
        Long[] sids=conStorageBinType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conStorageBinType.setSid(id);
                row=conStorageBinTypeMapper.updateById( conStorageBinType);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conStorageBinType.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conStorageBinType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conStorageBinType
     * @return
     */
    @Override
    public int check(ConStorageBinType conStorageBinType){
        int row=0;
        Long[] sids=conStorageBinType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conStorageBinType.setSid(id);
                row=conStorageBinTypeMapper.updateById( conStorageBinType);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conStorageBinType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
