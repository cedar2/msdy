package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.TecBomItem;
import com.platform.ems.domain.dto.request.InvFundRequest;
import com.platform.ems.domain.dto.response.InvFundResponse;
import com.platform.ems.mapper.TecBomItemMapper;
import com.platform.ems.service.ITecBomItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 物料清单（BOM）组件清单Service业务层处理
 *
 * @author qhq
 * @date 2021-03-15
 */
@Service
@SuppressWarnings("all")
public class TecBomItemServiceImpl extends ServiceImpl<TecBomItemMapper,TecBomItem>  implements ITecBomItemService {
    @Autowired
    private TecBomItemMapper tecBomItemMapper;

    /**
     * 查询物料清单（BOM）组件清单
     *
     * @param bomItemSid 物料清单（BOM）组件清单ID
     * @return 物料清单（BOM）组件清单
     */
    @Override
    public TecBomItem selectTecBomItemById(Long bomItemSid) {
        return tecBomItemMapper.selectTecBomItemById(bomItemSid);
    }

    /**
     * 查询物料清单（BOM）组件清单列表
     *
     * @param tecBomItem 物料清单（BOM）组件清单
     * @return 物料清单（BOM）组件清单
     */
    @Override
    public List<TecBomItem> selectTecBomItemList(TecBomItem tecBomItem) {
        return tecBomItemMapper.selectTecBomItemList(tecBomItem);
    }

    @Override
    public List<InvFundResponse> getFund(InvFundRequest invFundRequest) {
        return tecBomItemMapper.getFund(invFundRequest);
    }

    @Override
    public List<InvFundResponse> getFundSku2(InvFundRequest invFundRequest) {
        return tecBomItemMapper.getFundSku2(invFundRequest);
    }

    /**
     * 新增物料清单（BOM）组件清单
     * 需要注意编码重复校验
     * @param tecBomItem 物料清单（BOM）组件清单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecBomItem(TecBomItem tecBomItem) {
        return tecBomItemMapper.insert(tecBomItem);
    }

    /**
     * 修改物料清单（BOM）组件清单
     *
     * @param tecBomItem 物料清单（BOM）组件清单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecBomItem(TecBomItem tecBomItem) {
        return tecBomItemMapper.updateById(tecBomItem);
    }

    /**
     * 批量删除物料清单（BOM）组件清单
     *
     * @param clientIds 需要删除的物料清单（BOM）组件清单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecBomItemByIds(List<Long> clientIds) {
        return tecBomItemMapper.deleteBatchIds(clientIds);
    }

    @Override
    public int deleteTecBomItemById(Long clientId) {
        return 0;
    }


}
