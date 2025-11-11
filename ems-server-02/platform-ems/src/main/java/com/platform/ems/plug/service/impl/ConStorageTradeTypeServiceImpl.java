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
import com.platform.ems.plug.domain.ConStorageTradeType;
import com.platform.ems.plug.mapper.ConStorageTradeTypeMapper;
import com.platform.ems.plug.service.IConStorageTradeTypeService;
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
 * 交易类型Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConStorageTradeTypeServiceImpl extends ServiceImpl<ConStorageTradeTypeMapper,ConStorageTradeType>  implements IConStorageTradeTypeService {
    @Autowired
    private ConStorageTradeTypeMapper conStorageTradeTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "交易类型";
    /**
     * 查询交易类型
     *
     * @param sid 交易类型ID
     * @return 交易类型
     */
    @Override
    public ConStorageTradeType selectConStorageTradeTypeById(Long sid) {
        ConStorageTradeType conStorageTradeType = conStorageTradeTypeMapper.selectConStorageTradeTypeById(sid);
        MongodbUtil.find(conStorageTradeType);
        return  conStorageTradeType;
    }

    /**
     * 查询交易类型列表
     *
     * @param conStorageTradeType 交易类型
     * @return 交易类型
     */
    @Override
    public List<ConStorageTradeType> selectConStorageTradeTypeList(ConStorageTradeType conStorageTradeType) {
        return conStorageTradeTypeMapper.selectConStorageTradeTypeList(conStorageTradeType);
    }

    /**
     * 新增交易类型
     * 需要注意编码重复校验
     * @param conStorageTradeType 交易类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConStorageTradeType(ConStorageTradeType conStorageTradeType) {
        List<ConStorageTradeType> codeList = conStorageTradeTypeMapper.selectList(new QueryWrapper<ConStorageTradeType>().lambda()
                .eq(ConStorageTradeType::getCode, conStorageTradeType.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConStorageTradeType> nameList = conStorageTradeTypeMapper.selectList(new QueryWrapper<ConStorageTradeType>().lambda()
                .eq(ConStorageTradeType::getName, conStorageTradeType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conStorageTradeTypeMapper.insert(conStorageTradeType);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conStorageTradeType.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改交易类型
     *
     * @param conStorageTradeType 交易类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConStorageTradeType(ConStorageTradeType conStorageTradeType) {
        ConStorageTradeType response = conStorageTradeTypeMapper.selectConStorageTradeTypeById(conStorageTradeType.getSid());
        int row=conStorageTradeTypeMapper.updateById(conStorageTradeType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conStorageTradeType.getSid(), BusinessType.UPDATE.getValue(), response,conStorageTradeType,TITLE);
        }
        return row;
    }

    /**
     * 变更交易类型
     *
     * @param conStorageTradeType 交易类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConStorageTradeType(ConStorageTradeType conStorageTradeType) {
        List<ConStorageTradeType> nameList = conStorageTradeTypeMapper.selectList(new QueryWrapper<ConStorageTradeType>().lambda()
                .eq(ConStorageTradeType::getName, conStorageTradeType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conStorageTradeType.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conStorageTradeType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConStorageTradeType response = conStorageTradeTypeMapper.selectConStorageTradeTypeById(conStorageTradeType.getSid());
        int row = conStorageTradeTypeMapper.updateAllById(conStorageTradeType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conStorageTradeType.getSid(), BusinessType.CHANGE.getValue(), response, conStorageTradeType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除交易类型
     *
     * @param sids 需要删除的交易类型ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConStorageTradeTypeByIds(List<Long> sids) {
        return conStorageTradeTypeMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conStorageTradeType
    * @return
    */
    @Override
    public int changeStatus(ConStorageTradeType conStorageTradeType){
        int row=0;
        Long[] sids=conStorageTradeType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conStorageTradeType.setSid(id);
                row=conStorageTradeTypeMapper.updateById( conStorageTradeType);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conStorageTradeType.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conStorageTradeType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conStorageTradeType
     * @return
     */
    @Override
    public int check(ConStorageTradeType conStorageTradeType){
        int row=0;
        Long[] sids=conStorageTradeType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conStorageTradeType.setSid(id);
                row=conStorageTradeTypeMapper.updateById( conStorageTradeType);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conStorageTradeType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
