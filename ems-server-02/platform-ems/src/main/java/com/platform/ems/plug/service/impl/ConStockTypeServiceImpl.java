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
import com.platform.ems.plug.domain.ConStockType;
import com.platform.ems.plug.mapper.ConStockTypeMapper;
import com.platform.ems.plug.service.IConStockTypeService;
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
 * 库存类型Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@Service
@SuppressWarnings("all")
public class ConStockTypeServiceImpl extends ServiceImpl<ConStockTypeMapper,ConStockType>  implements IConStockTypeService {
    @Autowired
    private ConStockTypeMapper conStockTypeMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "库存类型";
    /**
     * 查询库存类型
     *
     * @param sid 库存类型ID
     * @return 库存类型
     */
    @Override
    public ConStockType selectConStockTypeById(Long sid) {
        ConStockType conStockType = conStockTypeMapper.selectConStockTypeById(sid);
        MongodbUtil.find(conStockType);
        return  conStockType;
    }

    /**
     * 查询库存类型列表
     *
     * @param conStockType 库存类型
     * @return 库存类型
     */
    @Override
    public List<ConStockType> selectConStockTypeList(ConStockType conStockType) {
        return conStockTypeMapper.selectConStockTypeList(conStockType);
    }

    /**
     * 新增库存类型
     * 需要注意编码重复校验
     * @param conStockType 库存类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConStockType(ConStockType conStockType) {
        List<ConStockType> codeList = conStockTypeMapper.selectList(new QueryWrapper<ConStockType>().lambda()
                .eq(ConStockType::getCode, conStockType.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConStockType> nameList = conStockTypeMapper.selectList(new QueryWrapper<ConStockType>().lambda()
                .eq(ConStockType::getName, conStockType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conStockTypeMapper.insert(conStockType);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conStockType.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改库存类型
     *
     * @param conStockType 库存类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConStockType(ConStockType conStockType) {
        ConStockType response = conStockTypeMapper.selectConStockTypeById(conStockType.getSid());
        int row=conStockTypeMapper.updateById(conStockType);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conStockType.getSid(), BusinessType.UPDATE.getValue(), response,conStockType,TITLE);
        }
        return row;
    }

    /**
     * 变更库存类型
     *
     * @param conStockType 库存类型
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConStockType(ConStockType conStockType) {
        List<ConStockType> nameList = conStockTypeMapper.selectList(new QueryWrapper<ConStockType>().lambda()
                .eq(ConStockType::getName, conStockType.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conStockType.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conStockType.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConStockType response = conStockTypeMapper.selectConStockTypeById(conStockType.getSid());
        int row = conStockTypeMapper.updateAllById(conStockType);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conStockType.getSid(), BusinessType.CHANGE.getValue(), response, conStockType, TITLE);
        }
        return row;
    }

    /**
     * 批量删除库存类型
     *
     * @param sids 需要删除的库存类型ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConStockTypeByIds(List<Long> sids) {
        return conStockTypeMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conStockType
    * @return
    */
    @Override
    public int changeStatus(ConStockType conStockType){
        int row=0;
        Long[] sids=conStockType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conStockType.setSid(id);
                row=conStockTypeMapper.updateById( conStockType);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conStockType.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conStockType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conStockType
     * @return
     */
    @Override
    public int check(ConStockType conStockType){
        int row=0;
        Long[] sids=conStockType.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conStockType.setSid(id);
                row=conStockTypeMapper.updateById( conStockType);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conStockType.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
