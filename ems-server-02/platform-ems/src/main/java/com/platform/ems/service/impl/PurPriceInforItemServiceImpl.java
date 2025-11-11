package com.platform.ems.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.PurPriceInforItemMapper;
import com.platform.ems.domain.PurPriceInforItem;
import com.platform.ems.service.IPurPriceInforItemService;

/**
 * 采购价格记录明细(报价/核价/议价)Service业务层处理
 * 
 * @author linhongwei
 * @date 2021-04-26
 */
@Service
@SuppressWarnings("all")
public class PurPriceInforItemServiceImpl extends ServiceImpl<PurPriceInforItemMapper,PurPriceInforItem>  implements IPurPriceInforItemService {
    @Autowired
    private PurPriceInforItemMapper purPriceInforItemMapper;

    /**
     * 查询采购价格记录明细(报价/核价/议价)
     * 
     * @param priceInforItemSid 采购价格记录明细(报价/核价/议价)ID
     * @return 采购价格记录明细(报价/核价/议价)
     */
    @Override
    public PurPriceInforItem selectPurPriceInforItemById(Long priceInforItemSid) {
        return purPriceInforItemMapper.selectPurPriceInforItemById(priceInforItemSid);
    }

    /**
     * 查询采购价格记录明细(报价/核价/议价)列表
     * 
     * @param purPriceInforItem 采购价格记录明细(报价/核价/议价)
     * @return 采购价格记录明细(报价/核价/议价)
     */
    @Override
    public List<PurPriceInforItem> selectPurPriceInforItemList(PurPriceInforItem purPriceInforItem) {
        return purPriceInforItemMapper.selectPurPriceInforItemList(purPriceInforItem);
    }

    /**
     * 新增采购价格记录明细(报价/核价/议价)
     * 需要注意编码重复校验
     * @param purPriceInforItem 采购价格记录明细(报价/核价/议价)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurPriceInforItem(PurPriceInforItem purPriceInforItem) {
        return purPriceInforItemMapper.insert(purPriceInforItem);
    }

    /**
     * 修改采购价格记录明细(报价/核价/议价)
     * 
     * @param purPriceInforItem 采购价格记录明细(报价/核价/议价)
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurPriceInforItem(PurPriceInforItem purPriceInforItem) {
        return purPriceInforItemMapper.updateById(purPriceInforItem);
    }

    /**
     * 批量删除采购价格记录明细(报价/核价/议价)
     * 
     * @param priceInforItemSids 需要删除的采购价格记录明细(报价/核价/议价)ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurPriceInforItemByIds(List<Long> priceInforItemSids) {
        return purPriceInforItemMapper.deleteBatchIds(priceInforItemSids);
    }


}
