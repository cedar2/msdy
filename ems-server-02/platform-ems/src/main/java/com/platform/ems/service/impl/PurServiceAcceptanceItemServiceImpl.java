package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.PurServiceAcceptanceItemMapper;
import com.platform.ems.domain.PurServiceAcceptanceItem;
import com.platform.ems.service.IPurServiceAcceptanceItemService;

/**
 * 服务采购验收单-明细Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-07
 */
@Service
@SuppressWarnings("all")
public class PurServiceAcceptanceItemServiceImpl extends ServiceImpl<PurServiceAcceptanceItemMapper,PurServiceAcceptanceItem>  implements IPurServiceAcceptanceItemService {
    @Autowired
    private PurServiceAcceptanceItemMapper purServiceAcceptanceItemMapper;

    /**
     * 查询服务采购验收单-明细
     * 
     * @param clientId 服务采购验收单-明细ID
     * @return 服务采购验收单-明细
     */
    @Override
    public PurServiceAcceptanceItem selectPurServiceAcceptanceItemById(String clientId) {
        return purServiceAcceptanceItemMapper.selectPurServiceAcceptanceItemById(clientId);
    }

    /**
     * 查询服务采购验收单-明细列表
     * 
     * @param purServiceAcceptanceItem 服务采购验收单-明细
     * @return 服务采购验收单-明细
     */
    @Override
    public List<PurServiceAcceptanceItem> selectPurServiceAcceptanceItemList(PurServiceAcceptanceItem purServiceAcceptanceItem) {
        return purServiceAcceptanceItemMapper.selectPurServiceAcceptanceItemList(purServiceAcceptanceItem);
    }

    /**
     * 新增服务采购验收单-明细
     * 需要注意编码重复校验
     * @param purServiceAcceptanceItem 服务采购验收单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurServiceAcceptanceItem(PurServiceAcceptanceItem purServiceAcceptanceItem) {
        return purServiceAcceptanceItemMapper.insert(purServiceAcceptanceItem);
    }

    /**
     * 修改服务采购验收单-明细
     * 
     * @param purServiceAcceptanceItem 服务采购验收单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurServiceAcceptanceItem(PurServiceAcceptanceItem purServiceAcceptanceItem) {
        return purServiceAcceptanceItemMapper.updateById(purServiceAcceptanceItem);
    }

    /**
     * 批量删除服务采购验收单-明细
     * 
     * @param clientIds 需要删除的服务采购验收单-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurServiceAcceptanceItemByIds(List<String> clientIds) {
        return purServiceAcceptanceItemMapper.deleteBatchIds(clientIds);
    }


}
