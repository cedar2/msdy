package com.platform.ems.service.impl;

import java.util.List;
import java.util.ArrayList;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.mapper.InvReserveInventoryMapper;
import com.platform.ems.domain.InvReserveInventory;
import com.platform.ems.service.IInvReserveInventoryService;

/**
 * 预留库存Service业务层处理
 *
 * @author linhongwei
 * @date 2022-04-01
 */
@Service
@SuppressWarnings("all")
public class InvReserveInventoryServiceImpl extends ServiceImpl<InvReserveInventoryMapper,InvReserveInventory>  implements IInvReserveInventoryService {
    @Autowired
    private InvReserveInventoryMapper invReserveInventoryMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "预留库存";
    /**
     * 查询预留库存
     *
     * @param reserveStockSid 预留库存ID
     * @return 预留库存
     */
    @Override
    public InvReserveInventory selectInvReserveInventoryById(Long reserveStockSid) {
        InvReserveInventory invReserveInventory = invReserveInventoryMapper.selectInvReserveInventoryById(reserveStockSid);
        MongodbUtil.find(invReserveInventory);
        return  invReserveInventory;
    }

    /**
     * 查询预留库存列表
     *
     * @param invReserveInventory 预留库存
     * @return 预留库存
     */
    @Override
    public List<InvReserveInventory> selectInvReserveInventoryList(InvReserveInventory invReserveInventory) {
        return null;
    }

    /**
     * 新增预留库存
     * 需要注意编码重复校验
     * @param invReserveInventory 预留库存
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInvReserveInventory(InvReserveInventory invReserveInventory) {
        int row= invReserveInventoryMapper.insert(invReserveInventory);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(invReserveInventory.getReserveStockSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改预留库存
     *
     * @param invReserveInventory 预留库存
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInvReserveInventory(InvReserveInventory invReserveInventory) {
        InvReserveInventory response = invReserveInventoryMapper.selectInvReserveInventoryById(invReserveInventory.getReserveStockSid());
        int row=invReserveInventoryMapper.updateById(invReserveInventory);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(invReserveInventory.getReserveStockSid(), BusinessType.UPDATE.ordinal(), response,invReserveInventory,TITLE);
        }
        return row;
    }

    /**
     * 变更预留库存
     *
     * @param invReserveInventory 预留库存
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeInvReserveInventory(InvReserveInventory invReserveInventory) {
        InvReserveInventory response = invReserveInventoryMapper.selectInvReserveInventoryById(invReserveInventory.getReserveStockSid());
                                                                                                                                                                                                            int row=invReserveInventoryMapper.updateAllById(invReserveInventory);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(invReserveInventory.getReserveStockSid(), BusinessType.CHANGE.ordinal(), response,invReserveInventory,TITLE);
        }
        return row;
    }

    /**
     * 批量删除预留库存
     *
     * @param reserveStockSids 需要删除的预留库存ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInvReserveInventoryByIds(List<Long> reserveStockSids) {
        return invReserveInventoryMapper.deleteBatchIds(reserveStockSids);
    }

    /**
    * 启用/停用
    * @param invReserveInventory
    * @return
    */
    @Override
    public int changeStatus(InvReserveInventory invReserveInventory){
        int row=0;

        return row;
    }


    /**
     *更改确认状态
     * @param invReserveInventory
     * @return
     */
    @Override
    public int check(InvReserveInventory invReserveInventory){
        int row=0;
        return row;
    }


}
