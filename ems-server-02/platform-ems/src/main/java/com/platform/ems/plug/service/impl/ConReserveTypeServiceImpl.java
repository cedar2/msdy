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
import com.platform.ems.plug.domain.ConReserveType;
import com.platform.ems.plug.mapper.ConReserveTypeMapper;
import com.platform.ems.plug.service.IConReserveTypeService;
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
 * 预留类型Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConReserveTypeServiceImpl extends ServiceImpl<ConReserveTypeMapper,ConReserveType>  implements IConReserveTypeService {
    @Autowired
    private ConReserveTypeMapper conReserveTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "预留类型";
    /**
     * 查询预留类型
     *
     * @param sid 预留类型ID
     * @return 预留类型
     */
    @Override
    public ConReserveType selectConReserveTypeById(Long sid) {
        ConReserveType conReserveType = conReserveTypeMapper.selectConReserveTypeById(sid);
        MongodbUtil.find(conReserveType);
        return  conReserveType;
    }

    /**
     * 查询预留类型列表
     *
     * @param conReserveType 预留类型
     * @return 预留类型
     */
    @Override
    public List<ConReserveType> selectConReserveTypeList(ConReserveType conReserveType) {
        return conReserveTypeMapper.selectConReserveTypeList(conReserveType);
    }

    /**
     * 新增预留类型
     * 需要注意编码重复校验
     * @param conReserveType 预留类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConReserveType(ConReserveType conReserveType) {
        List<ConReserveType> codeList = conReserveTypeMapper.selectList(new QueryWrapper<ConReserveType>().lambda()
                .eq(ConReserveType::getCode, conReserveType.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConReserveType> nameList = conReserveTypeMapper.selectList(new QueryWrapper<ConReserveType>().lambda()
                .eq(ConReserveType::getName, conReserveType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conReserveTypeMapper.insert(conReserveType);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conReserveType.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改预留类型
     *
     * @param conReserveType 预留类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConReserveType(ConReserveType conReserveType) {
        ConReserveType response = conReserveTypeMapper.selectConReserveTypeById(conReserveType.getSid());
        int row=conReserveTypeMapper.updateById(conReserveType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conReserveType.getSid(), BusinessType.UPDATE.getValue(), response,conReserveType,TITLE);
        }
        return row;
    }

    /**
     * 变更预留类型
     *
     * @param conReserveType 预留类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConReserveType(ConReserveType conReserveType) {
        List<ConReserveType> nameList = conReserveTypeMapper.selectList(new QueryWrapper<ConReserveType>().lambda()
                .eq(ConReserveType::getName, conReserveType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conReserveType.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conReserveType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConReserveType response = conReserveTypeMapper.selectConReserveTypeById(conReserveType.getSid());
        int row = conReserveTypeMapper.updateAllById(conReserveType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conReserveType.getSid(), BusinessType.CHANGE.getValue(), response, conReserveType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除预留类型
     *
     * @param sids 需要删除的预留类型ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConReserveTypeByIds(List<Long> sids) {
        return conReserveTypeMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conReserveType
    * @return
    */
    @Override
    public int changeStatus(ConReserveType conReserveType){
        int row=0;
        Long[] sids=conReserveType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conReserveType.setSid(id);
                row=conReserveTypeMapper.updateById( conReserveType);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conReserveType.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conReserveType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conReserveType
     * @return
     */
    @Override
    public int check(ConReserveType conReserveType){
        int row=0;
        Long[] sids=conReserveType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conReserveType.setSid(id);
                row=conReserveTypeMapper.updateById( conReserveType);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conReserveType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
