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
import com.platform.ems.plug.domain.ConShelfStorageType;
import com.platform.ems.plug.mapper.ConShelfStorageTypeMapper;
import com.platform.ems.plug.service.IConShelfStorageTypeService;
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
 * 货架存储类型Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConShelfStorageTypeServiceImpl extends ServiceImpl<ConShelfStorageTypeMapper,ConShelfStorageType>  implements IConShelfStorageTypeService {
    @Autowired
    private ConShelfStorageTypeMapper conShelfStorageTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "货架存储类型";
    /**
     * 查询货架存储类型
     *
     * @param sid 货架存储类型ID
     * @return 货架存储类型
     */
    @Override
    public ConShelfStorageType selectConShelfStorageTypeById(Long sid) {
        ConShelfStorageType conShelfStorageType = conShelfStorageTypeMapper.selectConShelfStorageTypeById(sid);
        MongodbUtil.find(conShelfStorageType);
        return  conShelfStorageType;
    }

    /**
     * 查询货架存储类型列表
     *
     * @param conShelfStorageType 货架存储类型
     * @return 货架存储类型
     */
    @Override
    public List<ConShelfStorageType> selectConShelfStorageTypeList(ConShelfStorageType conShelfStorageType) {
        return conShelfStorageTypeMapper.selectConShelfStorageTypeList(conShelfStorageType);
    }

    /**
     * 新增货架存储类型
     * 需要注意编码重复校验
     * @param conShelfStorageType 货架存储类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConShelfStorageType(ConShelfStorageType conShelfStorageType) {
        List<ConShelfStorageType> codeList = conShelfStorageTypeMapper.selectList(new QueryWrapper<ConShelfStorageType>().lambda()
                .eq(ConShelfStorageType::getCode, conShelfStorageType.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConShelfStorageType> nameList = conShelfStorageTypeMapper.selectList(new QueryWrapper<ConShelfStorageType>().lambda()
                .eq(ConShelfStorageType::getName, conShelfStorageType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conShelfStorageTypeMapper.insert(conShelfStorageType);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conShelfStorageType.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改货架存储类型
     *
     * @param conShelfStorageType 货架存储类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConShelfStorageType(ConShelfStorageType conShelfStorageType) {
        ConShelfStorageType response = conShelfStorageTypeMapper.selectConShelfStorageTypeById(conShelfStorageType.getSid());
        int row=conShelfStorageTypeMapper.updateById(conShelfStorageType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conShelfStorageType.getSid(), BusinessType.UPDATE.getValue(), response,conShelfStorageType,TITLE);
        }
        return row;
    }

    /**
     * 变更货架存储类型
     *
     * @param conShelfStorageType 货架存储类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConShelfStorageType(ConShelfStorageType conShelfStorageType) {
        List<ConShelfStorageType> nameList = conShelfStorageTypeMapper.selectList(new QueryWrapper<ConShelfStorageType>().lambda()
                .eq(ConShelfStorageType::getName, conShelfStorageType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conShelfStorageType.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conShelfStorageType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConShelfStorageType response = conShelfStorageTypeMapper.selectConShelfStorageTypeById(conShelfStorageType.getSid());
        int row = conShelfStorageTypeMapper.updateAllById(conShelfStorageType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conShelfStorageType.getSid(), BusinessType.CHANGE.getValue(), response, conShelfStorageType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除货架存储类型
     *
     * @param sids 需要删除的货架存储类型ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConShelfStorageTypeByIds(List<Long> sids) {
        return conShelfStorageTypeMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conShelfStorageType
    * @return
    */
    @Override
    public int changeStatus(ConShelfStorageType conShelfStorageType){
        int row=0;
        Long[] sids=conShelfStorageType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conShelfStorageType.setSid(id);
                row=conShelfStorageTypeMapper.updateById( conShelfStorageType);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conShelfStorageType.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conShelfStorageType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conShelfStorageType
     * @return
     */
    @Override
    public int check(ConShelfStorageType conShelfStorageType){
        int row=0;
        Long[] sids=conShelfStorageType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conShelfStorageType.setSid(id);
                row=conShelfStorageTypeMapper.updateById( conShelfStorageType);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conShelfStorageType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
