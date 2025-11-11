package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.UserOperLog;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import com.platform.ems.mapper.InvCusSpecialInventoryMapper;
import com.platform.ems.domain.InvCusSpecialInventory;
import com.platform.ems.service.IInvCusSpecialInventoryService;

/**
 * 客户特殊库存（寄售/客供料）Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-01
 */
@Service
@SuppressWarnings("all")
public class InvCusSpecialInventoryServiceImpl extends ServiceImpl<InvCusSpecialInventoryMapper,InvCusSpecialInventory>  implements IInvCusSpecialInventoryService {
    @Autowired
    private InvCusSpecialInventoryMapper invCusSpecialInventoryMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "客户特殊库存（寄售/客供料）";
    /**
     * 查询客户特殊库存（寄售/客供料）
     *
     * @param customerSpecialStockSid 客户特殊库存（寄售/客供料）ID
     * @return 客户特殊库存（寄售/客供料）
     */
    @Override
    public InvCusSpecialInventory selectInvCusSpecialInventoryById(Long customerSpecialStockSid) {
        InvCusSpecialInventory invCusSpecialInventory = invCusSpecialInventoryMapper.selectInvCusSpecialInventoryById(customerSpecialStockSid);
        MongodbUtil.find(invCusSpecialInventory);
        return  invCusSpecialInventory;
    }

    /**
     * 查询客户特殊库存（寄售/客供料）列表
     *
     * @param invCusSpecialInventory 客户特殊库存（寄售/客供料）
     * @return 客户特殊库存（寄售/客供料）
     */
    @Override
    public List<InvCusSpecialInventory> selectInvCusSpecialInventoryList(InvCusSpecialInventory invCusSpecialInventory) {
        return invCusSpecialInventoryMapper.selectInvCusSpecialInventoryList(invCusSpecialInventory);
    }

    /**
     * 新增客户特殊库存（寄售/客供料）
     * 需要注意编码重复校验
     * @param invCusSpecialInventory 客户特殊库存（寄售/客供料）
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInvCusSpecialInventory(InvCusSpecialInventory invCusSpecialInventory) {
        int row= invCusSpecialInventoryMapper.insert(invCusSpecialInventory);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(invCusSpecialInventory.getCustomerSpecialStockSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改客户特殊库存（寄售/客供料）
     *
     * @param invCusSpecialInventory 客户特殊库存（寄售/客供料）
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInvCusSpecialInventory(InvCusSpecialInventory invCusSpecialInventory) {
        InvCusSpecialInventory response = invCusSpecialInventoryMapper.selectInvCusSpecialInventoryById(invCusSpecialInventory.getCustomerSpecialStockSid());
        int row=invCusSpecialInventoryMapper.updateById(invCusSpecialInventory);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(invCusSpecialInventory.getCustomerSpecialStockSid(), BusinessType.UPDATE.ordinal(), response,invCusSpecialInventory,TITLE);
        }
        return row;
    }

    /**
     * 变更客户特殊库存（寄售/客供料）
     *
     * @param invCusSpecialInventory 客户特殊库存（寄售/客供料）
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeInvCusSpecialInventory(InvCusSpecialInventory invCusSpecialInventory) {
        InvCusSpecialInventory response = invCusSpecialInventoryMapper.selectInvCusSpecialInventoryById(invCusSpecialInventory.getCustomerSpecialStockSid());
                                                                                                                int row=invCusSpecialInventoryMapper.updateAllById(invCusSpecialInventory);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(invCusSpecialInventory.getCustomerSpecialStockSid(), BusinessType.CHANGE.ordinal(), response,invCusSpecialInventory,TITLE);
        }
        return row;
    }

    /**
     * 批量删除客户特殊库存（寄售/客供料）
     *
     * @param customerSpecialStockSids 需要删除的客户特殊库存（寄售/客供料）ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInvCusSpecialInventoryByIds(List<Long> customerSpecialStockSids) {
        return invCusSpecialInventoryMapper.deleteBatchIds(customerSpecialStockSids);
    }

    /**
    * 启用/停用
    * @param invCusSpecialInventory
    * @return
    */
    @Override
    public int changeStatus(InvCusSpecialInventory invCusSpecialInventory){
        int row=0;
        Long[] sids=invCusSpecialInventory.getCustomerSpecialStockSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                invCusSpecialInventory.setCustomerSpecialStockSid(id);
                row=invCusSpecialInventoryMapper.updateById( invCusSpecialInventory);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
//                String remark=invCusSpecialInventory.getStatus().equals(ConstantsEms.ENABLE_STATUS)?"启用":"停用";
                MongodbUtil.insertUserLog(invCusSpecialInventory.getCustomerSpecialStockSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


    /**
     *更改确认状态
     * @param invCusSpecialInventory
     * @return
     */
    @Override
    public int check(InvCusSpecialInventory invCusSpecialInventory){
        int row=0;
        Long[] sids=invCusSpecialInventory.getCustomerSpecialStockSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                invCusSpecialInventory.setCustomerSpecialStockSid(id);
                row=invCusSpecialInventoryMapper.updateById( invCusSpecialInventory);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(invCusSpecialInventory.getCustomerSpecialStockSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
