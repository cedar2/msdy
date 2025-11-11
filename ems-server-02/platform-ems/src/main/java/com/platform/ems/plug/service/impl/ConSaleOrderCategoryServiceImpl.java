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
import com.platform.ems.plug.domain.ConSaleOrderCategory;
import com.platform.ems.plug.mapper.ConSaleOrderCategoryMapper;
import com.platform.ems.plug.service.IConSaleOrderCategoryService;
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
 * 销售订单类别Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-21
 */
@Service
@SuppressWarnings("all")
public class ConSaleOrderCategoryServiceImpl extends ServiceImpl<ConSaleOrderCategoryMapper,ConSaleOrderCategory>  implements IConSaleOrderCategoryService {
    @Autowired
    private ConSaleOrderCategoryMapper conSaleOrderCategoryMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "销售订单类别";
    /**
     * 查询销售订单类别
     *
     * @param sid 销售订单类别ID
     * @return 销售订单类别
     */
    @Override
    public ConSaleOrderCategory selectConSaleOrderCategoryById(Long sid) {
        ConSaleOrderCategory conSaleOrderCategory = conSaleOrderCategoryMapper.selectConSaleOrderCategoryById(sid);
        MongodbUtil.find(conSaleOrderCategory);
        return  conSaleOrderCategory;
    }

    /**
     * 查询销售订单类别列表
     *
     * @param conSaleOrderCategory 销售订单类别
     * @return 销售订单类别
     */
    @Override
    public List<ConSaleOrderCategory> selectConSaleOrderCategoryList(ConSaleOrderCategory conSaleOrderCategory) {
        return conSaleOrderCategoryMapper.selectConSaleOrderCategoryList(conSaleOrderCategory);
    }

    /**
     * 新增销售订单类别
     * 需要注意编码重复校验
     * @param conSaleOrderCategory 销售订单类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConSaleOrderCategory(ConSaleOrderCategory conSaleOrderCategory) {
        List<ConSaleOrderCategory> codeList = conSaleOrderCategoryMapper.selectList(new QueryWrapper<ConSaleOrderCategory>().lambda()
                .eq(ConSaleOrderCategory::getCode, conSaleOrderCategory.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConSaleOrderCategory> nameList = conSaleOrderCategoryMapper.selectList(new QueryWrapper<ConSaleOrderCategory>().lambda()
                .eq(ConSaleOrderCategory::getName, conSaleOrderCategory.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conSaleOrderCategoryMapper.insert(conSaleOrderCategory);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conSaleOrderCategory.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改销售订单类别
     *
     * @param conSaleOrderCategory 销售订单类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConSaleOrderCategory(ConSaleOrderCategory conSaleOrderCategory) {
        ConSaleOrderCategory response = conSaleOrderCategoryMapper.selectConSaleOrderCategoryById(conSaleOrderCategory.getSid());
        int row=conSaleOrderCategoryMapper.updateById(conSaleOrderCategory);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conSaleOrderCategory.getSid(), BusinessType.UPDATE.getValue(), response,conSaleOrderCategory,TITLE);
        }
        return row;
    }

    /**
     * 变更销售订单类别
     *
     * @param conSaleOrderCategory 销售订单类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConSaleOrderCategory(ConSaleOrderCategory conSaleOrderCategory) {
        List<ConSaleOrderCategory> nameList = conSaleOrderCategoryMapper.selectList(new QueryWrapper<ConSaleOrderCategory>().lambda()
                .eq(ConSaleOrderCategory::getName, conSaleOrderCategory.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conSaleOrderCategory.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conSaleOrderCategory.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConSaleOrderCategory response = conSaleOrderCategoryMapper.selectConSaleOrderCategoryById(conSaleOrderCategory.getSid());
        int row = conSaleOrderCategoryMapper.updateAllById(conSaleOrderCategory);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conSaleOrderCategory.getSid(), BusinessType.CHANGE.getValue(), response, conSaleOrderCategory, TITLE);
        }
        return row;
    }

    /**
     * 批量删除销售订单类别
     *
     * @param sids 需要删除的销售订单类别ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConSaleOrderCategoryByIds(List<Long> sids) {
        return conSaleOrderCategoryMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conSaleOrderCategory
    * @return
    */
    @Override
    public int changeStatus(ConSaleOrderCategory conSaleOrderCategory){
        int row=0;
        Long[] sids=conSaleOrderCategory.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conSaleOrderCategory.setSid(id);
                row=conSaleOrderCategoryMapper.updateById( conSaleOrderCategory);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conSaleOrderCategory.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conSaleOrderCategory.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conSaleOrderCategory
     * @return
     */
    @Override
    public int check(ConSaleOrderCategory conSaleOrderCategory){
        int row=0;
        Long[] sids=conSaleOrderCategory.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conSaleOrderCategory.setSid(id);
                row=conSaleOrderCategoryMapper.updateById( conSaleOrderCategory);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conSaleOrderCategory.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
