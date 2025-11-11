package com.platform.ems.service.impl;

import java.util.List;
import java.util.Date;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.common.utils.SecurityUtils;
import com.platform.ems.mapper.BasMaterialPackageItemMapper;
import com.platform.ems.domain.BasMaterialPackageItem;
import com.platform.ems.service.IBasMaterialPackageItemService;

/**
 * 常规辅料包-明细Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-03-14
 */
@Service
@SuppressWarnings("all")
public class BasMaterialPackageItemServiceImpl extends ServiceImpl<BasMaterialPackageItemMapper,BasMaterialPackageItem>  implements IBasMaterialPackageItemService {
    @Autowired
    private BasMaterialPackageItemMapper basMaterialPackageItemMapper;

    /**
     * 查询常规辅料包-明细
     * 
     * @param clientId 常规辅料包-明细ID
     * @return 常规辅料包-明细
     */
    @Override
    public BasMaterialPackageItem selectBasMaterialPackageItemById(String materialPackItemSid) {
        return basMaterialPackageItemMapper.selectBasMaterialPackageItemById(materialPackItemSid);
    }

    /**
     * 查询常规辅料包-明细列表
     * 
     * @param basMaterialPackageItem 常规辅料包-明细
     * @return 常规辅料包-明细
     */
    @Override
    public List<BasMaterialPackageItem> selectBasMaterialPackageItemList(BasMaterialPackageItem basMaterialPackageItem) {
        return basMaterialPackageItemMapper.selectBasMaterialPackageItemList(basMaterialPackageItem);
    }

    /**
     * 新增常规辅料包-明细
     * 需要注意编码重复校验
     * @param basMaterialPackageItem 常规辅料包-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasMaterialPackageItem(BasMaterialPackageItem basMaterialPackageItem) {
        return basMaterialPackageItemMapper.insert(basMaterialPackageItem);
    }

    /**
     * 修改常规辅料包-明细
     * 
     * @param basMaterialPackageItem 常规辅料包-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasMaterialPackageItem(BasMaterialPackageItem basMaterialPackageItem) {
        return basMaterialPackageItemMapper.updateById(basMaterialPackageItem);
    }

    /**
     * 批量删除常规辅料包-明细
     * 
     * @param clientIds 需要删除的常规辅料包-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasMaterialPackageItemByIds(List<String> materialPackItemSid) {
        return basMaterialPackageItemMapper.deleteBatchIds(materialPackItemSid);
    }

    @Override
    public List<BasMaterialPackageItem> getReportForm(BasMaterialPackageItem basMaterialPackageItem) {
        List<BasMaterialPackageItem> responseList = basMaterialPackageItemMapper.selectBasMaterialPackageItemList(basMaterialPackageItem);
        return responseList;
    }


}
