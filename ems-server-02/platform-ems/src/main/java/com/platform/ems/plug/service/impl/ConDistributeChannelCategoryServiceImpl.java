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
import com.platform.ems.plug.domain.ConDistributeChannelCategory;
import com.platform.ems.plug.mapper.ConDistributeChannelCategoryMapper;
import com.platform.ems.plug.service.IConDistributeChannelCategoryService;
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
 * 分销渠道类别Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConDistributeChannelCategoryServiceImpl extends ServiceImpl<ConDistributeChannelCategoryMapper,ConDistributeChannelCategory>  implements IConDistributeChannelCategoryService {
    @Autowired
    private ConDistributeChannelCategoryMapper conDistributeChannelCategoryMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "分销渠道类别";
    /**
     * 查询分销渠道类别
     *
     * @param sid 分销渠道类别ID
     * @return 分销渠道类别
     */
    @Override
    public ConDistributeChannelCategory selectConDistributeChannelCategoryById(Long sid) {
        ConDistributeChannelCategory conDistributeChannelCategory = conDistributeChannelCategoryMapper.selectConDistributeChannelCategoryById(sid);
        MongodbUtil.find(conDistributeChannelCategory);
        return  conDistributeChannelCategory;
    }

    /**
     * 查询分销渠道类别列表
     *
     * @param conDistributeChannelCategory 分销渠道类别
     * @return 分销渠道类别
     */
    @Override
    public List<ConDistributeChannelCategory> selectConDistributeChannelCategoryList(ConDistributeChannelCategory conDistributeChannelCategory) {
        return conDistributeChannelCategoryMapper.selectConDistributeChannelCategoryList(conDistributeChannelCategory);
    }

    /**
     * 新增分销渠道类别
     * 需要注意编码重复校验
     * @param conDistributeChannelCategory 分销渠道类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConDistributeChannelCategory(ConDistributeChannelCategory conDistributeChannelCategory) {
        List<ConDistributeChannelCategory> codeList = conDistributeChannelCategoryMapper.selectList(new QueryWrapper<ConDistributeChannelCategory>().lambda()
                .eq(ConDistributeChannelCategory::getCode, conDistributeChannelCategory.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)){
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConDistributeChannelCategory> nameList = conDistributeChannelCategoryMapper.selectList(new QueryWrapper<ConDistributeChannelCategory>().lambda()
                .eq(ConDistributeChannelCategory::getName, conDistributeChannelCategory.getName()));
        if (CollectionUtil.isNotEmpty(nameList)){
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conDistributeChannelCategoryMapper.insert(conDistributeChannelCategory);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conDistributeChannelCategory.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改分销渠道类别
     *
     * @param conDistributeChannelCategory 分销渠道类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConDistributeChannelCategory(ConDistributeChannelCategory conDistributeChannelCategory) {
        ConDistributeChannelCategory response = conDistributeChannelCategoryMapper.selectConDistributeChannelCategoryById(conDistributeChannelCategory.getSid());
        int row=conDistributeChannelCategoryMapper.updateById(conDistributeChannelCategory);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conDistributeChannelCategory.getSid(), BusinessType.UPDATE.getValue(), response,conDistributeChannelCategory,TITLE);
        }
        return row;
    }

    /**
     * 变更分销渠道类别
     *
     * @param conDistributeChannelCategory 分销渠道类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConDistributeChannelCategory(ConDistributeChannelCategory conDistributeChannelCategory) {
        List<ConDistributeChannelCategory> nameList = conDistributeChannelCategoryMapper.selectList(new QueryWrapper<ConDistributeChannelCategory>().lambda()
                .eq(ConDistributeChannelCategory::getName, conDistributeChannelCategory.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conDistributeChannelCategory.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conDistributeChannelCategory.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConDistributeChannelCategory response = conDistributeChannelCategoryMapper.selectConDistributeChannelCategoryById(conDistributeChannelCategory.getSid());
        int row = conDistributeChannelCategoryMapper.updateAllById(conDistributeChannelCategory);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conDistributeChannelCategory.getSid(), BusinessType.CHANGE.getValue(), response, conDistributeChannelCategory, TITLE);
        }
        return row;
    }

    /**
     * 批量删除分销渠道类别
     *
     * @param sids 需要删除的分销渠道类别ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConDistributeChannelCategoryByIds(List<Long> sids) {
        return conDistributeChannelCategoryMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conDistributeChannelCategory
    * @return
    */
    @Override
    public int changeStatus(ConDistributeChannelCategory conDistributeChannelCategory){
        int row=0;
        Long[] sids=conDistributeChannelCategory.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDistributeChannelCategory.setSid(id);
                row=conDistributeChannelCategoryMapper.updateById( conDistributeChannelCategory);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conDistributeChannelCategory.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conDistributeChannelCategory.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conDistributeChannelCategory
     * @return
     */
    @Override
    public int check(ConDistributeChannelCategory conDistributeChannelCategory){
        int row=0;
        Long[] sids=conDistributeChannelCategory.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conDistributeChannelCategory.setSid(id);
                row=conDistributeChannelCategoryMapper.updateById( conDistributeChannelCategory);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conDistributeChannelCategory.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConDistributeChannelCategory> getConDistributeChannelCategoryList() {
        return conDistributeChannelCategoryMapper.getConDistributeChannelCategoryList();
    }
}
