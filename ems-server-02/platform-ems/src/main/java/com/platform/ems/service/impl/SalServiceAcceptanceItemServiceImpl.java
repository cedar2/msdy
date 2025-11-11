package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.SalServiceAcceptanceItemMapper;
import com.platform.ems.domain.SalServiceAcceptanceItem;
import com.platform.ems.service.ISalServiceAcceptanceItemService;

/**
 * 服务销售验收单-明细Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-06
 */
@Service
@SuppressWarnings("all")
public class SalServiceAcceptanceItemServiceImpl extends ServiceImpl<SalServiceAcceptanceItemMapper,SalServiceAcceptanceItem>  implements ISalServiceAcceptanceItemService {
    @Autowired
    private SalServiceAcceptanceItemMapper salServiceAcceptanceItemMapper;

    /**
     * 查询服务销售验收单-明细
     * 
     * @param serviceAcceptanceItemSid 服务销售验收单-明细ID
     * @return 服务销售验收单-明细
     */
    @Override
    public SalServiceAcceptanceItem selectSalServiceAcceptanceItemById(Long serviceAcceptanceItemSid) {
        return salServiceAcceptanceItemMapper.selectSalServiceAcceptanceItemById(serviceAcceptanceItemSid);
    }

    /**
     * 查询服务销售验收单-明细列表
     * 
     * @param salServiceAcceptanceItem 服务销售验收单-明细
     * @return 服务销售验收单-明细
     */
    @Override
    public List<SalServiceAcceptanceItem> selectSalServiceAcceptanceItemList(SalServiceAcceptanceItem salServiceAcceptanceItem) {
        return salServiceAcceptanceItemMapper.selectSalServiceAcceptanceItemList(salServiceAcceptanceItem);
    }

    /**
     * 新增服务销售验收单-明细
     * 需要注意编码重复校验
     * @param salServiceAcceptanceItem 服务销售验收单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSalServiceAcceptanceItem(SalServiceAcceptanceItem salServiceAcceptanceItem) {
        return salServiceAcceptanceItemMapper.insert(salServiceAcceptanceItem);
    }

    /**
     * 修改服务销售验收单-明细
     * 
     * @param salServiceAcceptanceItem 服务销售验收单-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSalServiceAcceptanceItem(SalServiceAcceptanceItem salServiceAcceptanceItem) {
        return salServiceAcceptanceItemMapper.updateById(salServiceAcceptanceItem);
    }

    /**
     * 批量删除服务销售验收单-明细
     * 
     * @param serviceAcceptanceItemSids 需要删除的服务销售验收单-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalServiceAcceptanceItemByIds(List<Long> serviceAcceptanceItemSids) {
        return salServiceAcceptanceItemMapper.deleteBatchIds(serviceAcceptanceItemSids);
    }


}
