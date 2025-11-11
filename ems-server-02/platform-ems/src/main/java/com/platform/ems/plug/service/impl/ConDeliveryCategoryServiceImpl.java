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
import com.platform.ems.plug.domain.ConDeliveryCategory;
import com.platform.ems.plug.mapper.ConDeliveryCategoryMapper;
import com.platform.ems.plug.service.IConDeliveryCategoryService;
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
 * 交货类别Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDeliveryCategoryServiceImpl extends ServiceImpl<ConDeliveryCategoryMapper,ConDeliveryCategory>  implements IConDeliveryCategoryService {
    @Autowired
    private ConDeliveryCategoryMapper conDeliveryCategoryMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "交货类别";
    /**
     * 查询交货类别
     *
     * @param sid 交货类别ID
     * @return 交货类别
     */
    @Override
    public ConDeliveryCategory selectConDeliveryCategoryById(Long sid) {
        ConDeliveryCategory conDeliveryCategory = conDeliveryCategoryMapper.selectConDeliveryCategoryById(sid);
        MongodbUtil.find(conDeliveryCategory);
        return  conDeliveryCategory;
    }

    /**
     * 查询交货类别列表
     *
     * @param conDeliveryCategory 交货类别
     * @return 交货类别
     */
    @Override
    public List<ConDeliveryCategory> selectConDeliveryCategoryList(ConDeliveryCategory conDeliveryCategory) {
        return conDeliveryCategoryMapper.selectConDeliveryCategoryList(conDeliveryCategory);
    }

    /**
     * 新增交货类别
     * 需要注意编码重复校验
     * @param conDeliveryCategory 交货类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDeliveryCategory(ConDeliveryCategory conDeliveryCategory) {
        List<ConDeliveryCategory> codeList = conDeliveryCategoryMapper.selectList(new QueryWrapper<ConDeliveryCategory>().lambda()
                .eq(ConDeliveryCategory::getCode, conDeliveryCategory.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)){
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDeliveryCategory> nameList = conDeliveryCategoryMapper.selectList(new QueryWrapper<ConDeliveryCategory>().lambda()
                .eq(ConDeliveryCategory::getName, conDeliveryCategory.getName()));
        if (CollectionUtil.isNotEmpty(nameList)){
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conDeliveryCategoryMapper.insert(conDeliveryCategory);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDeliveryCategory.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改交货类别
     *
     * @param conDeliveryCategory 交货类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDeliveryCategory(ConDeliveryCategory conDeliveryCategory) {
        ConDeliveryCategory response = conDeliveryCategoryMapper.selectConDeliveryCategoryById(conDeliveryCategory.getSid());
        int row=conDeliveryCategoryMapper.updateById(conDeliveryCategory);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDeliveryCategory.getSid(), BusinessType.UPDATE.getValue(), response,conDeliveryCategory,TITLE);
        }
        return row;
    }

    /**
     * 变更交货类别
     *
     * @param conDeliveryCategory 交货类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDeliveryCategory(ConDeliveryCategory conDeliveryCategory) {
        List<ConDeliveryCategory> nameList = conDeliveryCategoryMapper.selectList(new QueryWrapper<ConDeliveryCategory>().lambda()
                .eq(ConDeliveryCategory::getName, conDeliveryCategory.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDeliveryCategory.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDeliveryCategory.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDeliveryCategory response = conDeliveryCategoryMapper.selectConDeliveryCategoryById(conDeliveryCategory.getSid());
        int row = conDeliveryCategoryMapper.updateAllById(conDeliveryCategory);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDeliveryCategory.getSid(), BusinessType.CHANGE.getValue(), response, conDeliveryCategory, TITLE);
        }
        return row;
    }

    /**
     * 批量删除交货类别
     *
     * @param sids 需要删除的交货类别ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDeliveryCategoryByIds(List<Long> sids) {
        return conDeliveryCategoryMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDeliveryCategory
    * @return
    */
    @Override
    public int changeStatus(ConDeliveryCategory conDeliveryCategory){
        int row=0;
        Long[] sids=conDeliveryCategory.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDeliveryCategory.setSid(id);
                row=conDeliveryCategoryMapper.updateById( conDeliveryCategory);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDeliveryCategory.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDeliveryCategory.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDeliveryCategory
     * @return
     */
    @Override
    public int check(ConDeliveryCategory conDeliveryCategory){
        int row=0;
        Long[] sids=conDeliveryCategory.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDeliveryCategory.setSid(id);
                row=conDeliveryCategoryMapper.updateById( conDeliveryCategory);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDeliveryCategory.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
