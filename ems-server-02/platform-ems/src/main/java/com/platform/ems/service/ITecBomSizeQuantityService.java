package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.TecBomSizeQuantity;
import com.platform.ems.domain.dto.request.TecBomSizeSkuInsertRequest;

import java.util.List;

/**
 * 物料清单（BOM）组件具体尺码用量Service接口
 * 
 * @author qhq
 * @date 2021-03-15
 */
public interface ITecBomSizeQuantityService extends IService<TecBomSizeQuantity>{
    /**
     * 查询物料清单（BOM）组件具体尺码用量
     * 
     * @param request 物料清单（BOM）组件具体尺码用量ID
     * @return 物料清单（BOM）组件具体尺码用量
     */

    public List<TecBomSizeQuantity> selectTecBomSizeQuantityById(List<TecBomSizeQuantity> request);

    /**
     * 查询物料清单（BOM）组件具体尺码用量列表
     * 
     * @param tecBomSizeQuantity 物料清单（BOM）组件具体尺码用量
     * @return 物料清单（BOM）组件具体尺码用量集合
     */
    public List<TecBomSizeQuantity> selectTecBomSizeQuantityList(TecBomSizeQuantity tecBomSizeQuantity);

    /**
     * 新增物料清单（BOM）组件具体尺码用量
     * 
     * @param request 物料清单（BOM）组件具体尺码用量
     * @return 结果
     */
    public int insertTecBomSizeQuantity(List<TecBomSizeQuantity> request);

    /**
     * 修改物料清单（BOM）组件具体尺码用量
     * 
     * @param request 物料清单（BOM）组件具体尺码用量
     * @return 结果
     */
    public int updateTecBomSizeQuantity(List<TecBomSizeQuantity> request);

    /**
     * 批量删除物料清单（BOM）组件具体尺码用量
     * 
     * @param clientIds 需要删除的物料清单（BOM）组件具体尺码用量ID
     * @return 结果
     */
    public int deleteTecBomSizeQuantityByIds(List<String>  clientIds);

    /**
     * 删除物料清单（BOM）组件具体尺码用量信息
     * 
     * @param clientId 物料清单（BOM）组件具体尺码用量ID
     * @return 结果
     */
    public int deleteTecBomSizeQuantityById(String clientId);
}
