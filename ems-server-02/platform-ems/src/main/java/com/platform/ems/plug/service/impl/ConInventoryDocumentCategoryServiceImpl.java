package com.platform.ems.plug.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.plug.domain.ConDocTypeMaterialRequisition;
import com.platform.ems.plug.domain.ConInventoryDocumentCategory;
import com.platform.ems.plug.mapper.ConInventoryDocumentCategoryMapper;
import com.platform.ems.plug.service.IConInventoryDocumentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;


/**
 * 库存凭证类别Service业务层处理
 *
 * @author c
 * @date 2021-07-29
 */
@Service
@SuppressWarnings("all")
public class ConInventoryDocumentCategoryServiceImpl extends ServiceImpl<ConInventoryDocumentCategoryMapper,ConInventoryDocumentCategory>  implements IConInventoryDocumentCategoryService {
    @Autowired
    private ConInventoryDocumentCategoryMapper conInventoryDocumentCategoryMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "库存凭证类别";
    /**
     * 查询库存凭证类别
     *
     * @param sid 库存凭证类别ID
     * @return 库存凭证类别
     */
    @Override
    public ConInventoryDocumentCategory selectConInventoryDocumentCategoryById(Long sid) {
        ConInventoryDocumentCategory conInventoryDocumentCategory = conInventoryDocumentCategoryMapper.selectConInventoryDocumentCategoryById(sid);
        MongodbUtil.find(conInventoryDocumentCategory);
        return  conInventoryDocumentCategory;
    }

    /**
     * 库存凭证类别 下拉列表
     *
     */
    @Override
    public List<ConInventoryDocumentCategory> getList() {
        return conInventoryDocumentCategoryMapper.getList();
    }

    /**
     * 查询库存凭证类别列表
     *
     * @param conInventoryDocumentCategory 库存凭证类别
     * @return 库存凭证类别
     */
    @Override
    public List<ConInventoryDocumentCategory> selectConInventoryDocumentCategoryList(ConInventoryDocumentCategory conInventoryDocumentCategory) {
        return conInventoryDocumentCategoryMapper.selectConInventoryDocumentCategoryList(conInventoryDocumentCategory);
    }

    /**
     * 新增库存凭证类别
     * 需要注意编码重复校验
     * @param conInventoryDocumentCategory 库存凭证类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConInventoryDocumentCategory(ConInventoryDocumentCategory conInventoryDocumentCategory) {
        List<ConInventoryDocumentCategory> codeList = conInventoryDocumentCategoryMapper.selectList(new QueryWrapper<ConInventoryDocumentCategory>().lambda()
                .eq(ConInventoryDocumentCategory::getCode, conInventoryDocumentCategory.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConInventoryDocumentCategory> nameList = conInventoryDocumentCategoryMapper.selectList(new QueryWrapper<ConInventoryDocumentCategory>().lambda()
                .eq(ConInventoryDocumentCategory::getName, conInventoryDocumentCategory.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row= conInventoryDocumentCategoryMapper.insert(conInventoryDocumentCategory);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conInventoryDocumentCategory.getSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改库存凭证类别
     *
     * @param conInventoryDocumentCategory 库存凭证类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConInventoryDocumentCategory(ConInventoryDocumentCategory conInventoryDocumentCategory) {
        checkNameUnique(conInventoryDocumentCategory);
        ConInventoryDocumentCategory response = conInventoryDocumentCategoryMapper.selectConInventoryDocumentCategoryById(conInventoryDocumentCategory.getSid());
        int row=conInventoryDocumentCategoryMapper.updateById(conInventoryDocumentCategory);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conInventoryDocumentCategory.getSid(), BusinessType.UPDATE.getValue(), response,conInventoryDocumentCategory,TITLE);
        }
        return row;
    }

    /**
     * 变更库存凭证类别
     *
     * @param conInventoryDocumentCategory 库存凭证类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConInventoryDocumentCategory(ConInventoryDocumentCategory conInventoryDocumentCategory) {
        ConInventoryDocumentCategory response = conInventoryDocumentCategoryMapper.selectConInventoryDocumentCategoryById(conInventoryDocumentCategory.getSid());
        checkNameUnique(conInventoryDocumentCategory);
        int row=conInventoryDocumentCategoryMapper.updateAllById(conInventoryDocumentCategory);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conInventoryDocumentCategory.getSid(), BusinessType.CHANGE.getValue(), response,conInventoryDocumentCategory,TITLE);
        }
        return row;
    }

    private void checkNameUnique(ConInventoryDocumentCategory conInventoryDocumentCategory) {
        List<ConInventoryDocumentCategory> nameList = conInventoryDocumentCategoryMapper.selectList(new QueryWrapper<ConInventoryDocumentCategory>().lambda()
                .eq(ConInventoryDocumentCategory::getName, conInventoryDocumentCategory.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conInventoryDocumentCategory.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conInventoryDocumentCategory.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
    }

    /**
     * 批量删除库存凭证类别
     *
     * @param sids 需要删除的库存凭证类别ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConInventoryDocumentCategoryByIds(List<Long> sids) {
        return conInventoryDocumentCategoryMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conInventoryDocumentCategory
    * @return
    */
    @Override
    public int changeStatus(ConInventoryDocumentCategory conInventoryDocumentCategory){
        int row=0;
        Long[] sids=conInventoryDocumentCategory.getSidList();
        if(sids!=null&&sids.length>0){
            row=conInventoryDocumentCategoryMapper.update(null, new UpdateWrapper<ConInventoryDocumentCategory>().lambda().set(ConInventoryDocumentCategory::getStatus ,conInventoryDocumentCategory.getStatus() )
                    .in(ConInventoryDocumentCategory::getSid,sids));
            for(Long id:sids){
                conInventoryDocumentCategory.setSid(id);
                row=conInventoryDocumentCategoryMapper.updateById( conInventoryDocumentCategory);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conInventoryDocumentCategory.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conInventoryDocumentCategory.getSid(), BusinessType.CHECK.getValue(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conInventoryDocumentCategory
     * @return
     */
    @Override
    public int check(ConInventoryDocumentCategory conInventoryDocumentCategory){
        int row=0;
        Long[] sids=conInventoryDocumentCategory.getSidList();
        if(sids!=null&&sids.length>0){
            row=conInventoryDocumentCategoryMapper.update(null,new UpdateWrapper<ConInventoryDocumentCategory>().lambda().set(ConInventoryDocumentCategory::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .in(ConInventoryDocumentCategory::getSid,sids));
            for(Long id:sids){
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
