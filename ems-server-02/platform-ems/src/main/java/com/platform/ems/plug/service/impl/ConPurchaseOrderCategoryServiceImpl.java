package com.platform.ems.plug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConPurchaseOrderCategory;
import com.platform.ems.plug.mapper.ConPurchaseOrderCategoryMapper;
import com.platform.ems.plug.service.IConPurchaseOrderCategoryService;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 采购订单类别Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConPurchaseOrderCategoryServiceImpl extends ServiceImpl<ConPurchaseOrderCategoryMapper,ConPurchaseOrderCategory>  implements IConPurchaseOrderCategoryService {
    @Autowired
    private ConPurchaseOrderCategoryMapper conPurchaseOrderCategoryMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "采购订单类别";
    /**
     * 查询采购订单类别
     *
     * @param sid 采购订单类别ID
     * @return 采购订单类别
     */
    @Override
    public ConPurchaseOrderCategory selectConPurchaseOrderCategoryById(Long sid) {
        ConPurchaseOrderCategory conPurchaseOrderCategory = conPurchaseOrderCategoryMapper.selectConPurchaseOrderCategoryById(sid);
        MongodbUtil.find(conPurchaseOrderCategory);
        return  conPurchaseOrderCategory;
    }

    /**
     * 查询采购订单类别列表
     *
     * @param conPurchaseOrderCategory 采购订单类别
     * @return 采购订单类别
     */
    @Override
    public List<ConPurchaseOrderCategory> selectConPurchaseOrderCategoryList(ConPurchaseOrderCategory conPurchaseOrderCategory) {
        return conPurchaseOrderCategoryMapper.selectConPurchaseOrderCategoryList(conPurchaseOrderCategory);
    }

    /**
     * 新增采购订单类别
     * 需要注意编码重复校验
     * @param conPurchaseOrderCategory 采购订单类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConPurchaseOrderCategory(ConPurchaseOrderCategory conPurchaseOrderCategory) {
        String name = conPurchaseOrderCategory.getName();
        String code = conPurchaseOrderCategory.getCode();
        List<ConPurchaseOrderCategory> list = conPurchaseOrderCategoryMapper.selectList(new QueryWrapper<ConPurchaseOrderCategory>().lambda()
                .or().eq(ConPurchaseOrderCategory::getName, name)
                .or().eq(ConPurchaseOrderCategory::getCode, code)
        );
        if(CollectionUtils.isNotEmpty(list)){
            throw new CustomException("配置档案已存在相同的名称或编码，不允许重复");
        }
        int row= conPurchaseOrderCategoryMapper.insert(conPurchaseOrderCategory);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(conPurchaseOrderCategory.getSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改采购订单类别
     *
     * @param conPurchaseOrderCategory 采购订单类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConPurchaseOrderCategory(ConPurchaseOrderCategory conPurchaseOrderCategory) {
        ConPurchaseOrderCategory response = conPurchaseOrderCategoryMapper.selectConPurchaseOrderCategoryById(conPurchaseOrderCategory.getSid());
        int row=conPurchaseOrderCategoryMapper.updateById(conPurchaseOrderCategory);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(conPurchaseOrderCategory.getSid(), BusinessType.UPDATE.ordinal(), response,conPurchaseOrderCategory,TITLE);
        }
        return row;
    }

    /**
     * 变更采购订单类别
     *
     * @param conPurchaseOrderCategory 采购订单类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConPurchaseOrderCategory(ConPurchaseOrderCategory conPurchaseOrderCategory) {
        String name = conPurchaseOrderCategory.getName();
        ConPurchaseOrderCategory item = conPurchaseOrderCategoryMapper.selectOne(new QueryWrapper<ConPurchaseOrderCategory>().lambda()
                .eq(ConPurchaseOrderCategory::getName, name)
        );
        if (item != null && !item.getSid().equals(conPurchaseOrderCategory.getSid())) {
            throw new CustomException("配置档案已存在相同的名称，不允许重复");
        }
        ConPurchaseOrderCategory response = conPurchaseOrderCategoryMapper.selectConPurchaseOrderCategoryById(conPurchaseOrderCategory.getSid());
        int row = conPurchaseOrderCategoryMapper.updateAllById(conPurchaseOrderCategory);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conPurchaseOrderCategory.getSid(), BusinessType.CHANGE.ordinal(), response, conPurchaseOrderCategory, TITLE);
        }
        return row;
    }

    /**
     * 批量删除采购订单类别
     *
     * @param sids 需要删除的采购订单类别ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConPurchaseOrderCategoryByIds(List<Long> sids) {
        return conPurchaseOrderCategoryMapper.deleteBatchIds(sids);
    }

    /**
    * 启用/停用
    * @param conPurchaseOrderCategory
    * @return
    */
    @Override
    public int changeStatus(ConPurchaseOrderCategory conPurchaseOrderCategory){
        int row=0;
        Long[] sids=conPurchaseOrderCategory.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conPurchaseOrderCategory.setSid(id);
                row=conPurchaseOrderCategoryMapper.updateById( conPurchaseOrderCategory);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                String remark=conPurchaseOrderCategory.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(conPurchaseOrderCategory.getSid(), BusinessType.CHECK.ordinal(), msgList,TITLE,remark);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param conPurchaseOrderCategory
     * @return
     */
    @Override
    public int check(ConPurchaseOrderCategory conPurchaseOrderCategory){
        int row=0;
        Long[] sids=conPurchaseOrderCategory.getSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                conPurchaseOrderCategory.setSid(id);
                row=conPurchaseOrderCategoryMapper.updateById( conPurchaseOrderCategory);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(conPurchaseOrderCategory.getSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
