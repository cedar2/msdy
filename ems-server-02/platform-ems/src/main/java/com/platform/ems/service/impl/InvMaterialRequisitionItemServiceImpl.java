package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.InvMaterialRequisitionItemMapper;
import com.platform.ems.domain.InvMaterialRequisitionItem;
import com.platform.ems.service.IInvMaterialRequisitionItemService;

/**
 * 领退料单-明细Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-08
 */
@Service
@SuppressWarnings("all")
public class InvMaterialRequisitionItemServiceImpl extends ServiceImpl<InvMaterialRequisitionItemMapper,InvMaterialRequisitionItem>  implements IInvMaterialRequisitionItemService {
    @Autowired
    private InvMaterialRequisitionItemMapper invMaterialRequisitionItemMapper;

    /**
     * 查询领退料单-明细
     * 
     * @param materialRequisitionItemSid 领退料单-明细ID
     * @return 领退料单-明细
     */
    @Override
    public InvMaterialRequisitionItem selectInvMaterialRequisitionItemById(Long materialRequisitionItemSid) {
        return invMaterialRequisitionItemMapper.selectById(materialRequisitionItemSid);
    }

    /**
     * 查询领退料单-明细列表
     * 
     * @param invMaterialRequisitionItem 领退料单-明细
     * @return 领退料单-明细
     */
    @Override
    public List<InvMaterialRequisitionItem> selectInvMaterialRequisitionItemList(InvMaterialRequisitionItem invMaterialRequisitionItem) {
        return invMaterialRequisitionItemMapper.selectInvMaterialRequisitionItemList(invMaterialRequisitionItem);
    }

    /**
     * 新增领退料单-明细
     * 需要注意编码重复校验
     * @param invMaterialRequisitionItem 领退料单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInvMaterialRequisitionItem(InvMaterialRequisitionItem invMaterialRequisitionItem) {
        return invMaterialRequisitionItemMapper.insert(invMaterialRequisitionItem);
    }

    /**
     * 修改领退料单-明细
     * 
     * @param invMaterialRequisitionItem 领退料单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInvMaterialRequisitionItem(InvMaterialRequisitionItem invMaterialRequisitionItem) {
        return invMaterialRequisitionItemMapper.updateById(invMaterialRequisitionItem);
    }

    /**
     * 批量删除领退料单-明细
     * 
     * @param materialRequisitionItemSids 需要删除的领退料单-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInvMaterialRequisitionItemByIds(List<Long> materialRequisitionItemSids) {
        return invMaterialRequisitionItemMapper.deleteBatchIds(materialRequisitionItemSids);
    }


}
