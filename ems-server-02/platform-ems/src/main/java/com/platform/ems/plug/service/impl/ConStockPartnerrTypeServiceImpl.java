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
import com.platform.ems.plug.domain.ConStockPartnerrType;
import com.platform.ems.plug.mapper.ConStockPartnerrTypeMapper;
import com.platform.ems.plug.service.IConStockPartnerrTypeService;
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
 * 类型_库存合作伙伴Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@Service
@SuppressWarnings("all")
public class ConStockPartnerrTypeServiceImpl extends ServiceImpl<ConStockPartnerrTypeMapper,ConStockPartnerrType>  implements IConStockPartnerrTypeService {
    @Autowired
    private ConStockPartnerrTypeMapper conStockPartnerrTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "类型_库存合作伙伴";
    /**
     * 查询类型_库存合作伙伴
     *
     * @param sid 类型_库存合作伙伴ID
     * @return 类型_库存合作伙伴
     */
    @Override
    public ConStockPartnerrType selectConStockPartnerrTypeById(Long sid) {
        ConStockPartnerrType conStockPartnerrType = conStockPartnerrTypeMapper.selectConStockPartnerrTypeById(sid);
        MongodbUtil.find(conStockPartnerrType);
        return  conStockPartnerrType;
    }

    /**
     * 查询类型_库存合作伙伴列表
     *
     * @param conStockPartnerrType 类型_库存合作伙伴
     * @return 类型_库存合作伙伴
     */
    @Override
    public List<ConStockPartnerrType> selectConStockPartnerrTypeList(ConStockPartnerrType conStockPartnerrType) {
        return conStockPartnerrTypeMapper.selectConStockPartnerrTypeList(conStockPartnerrType);
    }

    /**
     * 新增类型_库存合作伙伴
     * 需要注意编码重复校验
     * @param conStockPartnerrType 类型_库存合作伙伴
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConStockPartnerrType(ConStockPartnerrType conStockPartnerrType) {
        List<ConStockPartnerrType> codeList = conStockPartnerrTypeMapper.selectList(new QueryWrapper<ConStockPartnerrType>().lambda()
                .eq(ConStockPartnerrType::getCode, conStockPartnerrType.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConStockPartnerrType> nameList = conStockPartnerrTypeMapper.selectList(new QueryWrapper<ConStockPartnerrType>().lambda()
                .eq(ConStockPartnerrType::getName, conStockPartnerrType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conStockPartnerrTypeMapper.insert(conStockPartnerrType);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conStockPartnerrType.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改类型_库存合作伙伴
     *
     * @param conStockPartnerrType 类型_库存合作伙伴
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConStockPartnerrType(ConStockPartnerrType conStockPartnerrType) {
        ConStockPartnerrType response = conStockPartnerrTypeMapper.selectConStockPartnerrTypeById(conStockPartnerrType.getSid());
        int row=conStockPartnerrTypeMapper.updateById(conStockPartnerrType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conStockPartnerrType.getSid(), BusinessType.UPDATE.getValue(), response,conStockPartnerrType,TITLE);
        }
        return row;
    }

    /**
     * 变更类型_库存合作伙伴
     *
     * @param conStockPartnerrType 类型_库存合作伙伴
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConStockPartnerrType(ConStockPartnerrType conStockPartnerrType) {
        List<ConStockPartnerrType> nameList = conStockPartnerrTypeMapper.selectList(new QueryWrapper<ConStockPartnerrType>().lambda()
                .eq(ConStockPartnerrType::getName, conStockPartnerrType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conStockPartnerrType.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conStockPartnerrType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConStockPartnerrType response = conStockPartnerrTypeMapper.selectConStockPartnerrTypeById(conStockPartnerrType.getSid());
        int row = conStockPartnerrTypeMapper.updateAllById(conStockPartnerrType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conStockPartnerrType.getSid(), BusinessType.CHANGE.getValue(), response, conStockPartnerrType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除类型_库存合作伙伴
     *
     * @param sids 需要删除的类型_库存合作伙伴ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConStockPartnerrTypeByIds(List<Long> sids) {
        return conStockPartnerrTypeMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conStockPartnerrType
    * @return
    */
    @Override
    public int changeStatus(ConStockPartnerrType conStockPartnerrType){
        int row=0;
        Long[] sids=conStockPartnerrType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conStockPartnerrType.setSid(id);
                row=conStockPartnerrTypeMapper.updateById( conStockPartnerrType);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conStockPartnerrType.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conStockPartnerrType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conStockPartnerrType
     * @return
     */
    @Override
    public int check(ConStockPartnerrType conStockPartnerrType){
        int row=0;
        Long[] sids=conStockPartnerrType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conStockPartnerrType.setSid(id);
                row=conStockPartnerrTypeMapper.updateById( conStockPartnerrType);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conStockPartnerrType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
