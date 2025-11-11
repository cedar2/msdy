package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.TecBomItem;
import com.platform.ems.domain.dto.request.InvFundRequest;
import com.platform.ems.domain.dto.response.InvFundResponse;

import java.util.List;

/**
 * 物料清单（BOM）组件清单Service接口
 *
 * @author qhq
 * @date 2021-03-15
 */
public interface ITecBomItemService extends IService<TecBomItem>{
    /**
     * 查询物料清单（BOM）组件清单
     *
     * @param clientId 物料清单（BOM）组件清单ID
     * @return 物料清单（BOM）组件清单
     */
    public TecBomItem selectTecBomItemById(Long clientId);

    /**
     * 查询物料清单（BOM）组件清单列表
     *
     * @param tecBomItem 物料清单（BOM）组件清单
     * @return 物料清单（BOM）组件清单集合
     */
    public List<TecBomItem> selectTecBomItemList(TecBomItem tecBomItem);


    public List<InvFundResponse> getFund(InvFundRequest invFundRequest);

    public List<InvFundResponse> getFundSku2(InvFundRequest invFundRequest);
    /**
     * 新增物料清单（BOM）组件清单
     *
     * @param tecBomItem 物料清单（BOM）组件清单
     * @return 结果
     */
    public int insertTecBomItem(TecBomItem tecBomItem);

    /**
     * 修改物料清单（BOM）组件清单
     *
     * @param tecBomItem 物料清单（BOM）组件清单
     * @return 结果
     */
    public int updateTecBomItem(TecBomItem tecBomItem);

    /**
     * 批量删除物料清单（BOM）组件清单
     *
     * @param clientIds 需要删除的物料清单（BOM）组件清单ID
     * @return 结果
     */
    public int deleteTecBomItemByIds(List<Long>  clientIds);

    /**
     * 删除物料清单（BOM）组件清单信息
     *
     * @param clientId 物料清单（BOM）组件清单ID
     * @return 结果
     */
    public int deleteTecBomItemById(Long clientId);
}
