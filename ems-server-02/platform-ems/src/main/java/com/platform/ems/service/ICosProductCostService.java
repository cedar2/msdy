package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.CosProductCostLaborRequest;
import com.platform.ems.domain.dto.request.CosProductCostMaterialRequest;
import com.platform.ems.domain.dto.request.OrderErrRequest;
import com.platform.ems.domain.dto.response.CosProductCostLaborResponse;
import com.platform.ems.domain.dto.response.CosProductCostMaterialResponse;

/**
 * 商品成本核算主Service接口
 * 
 * @author qhq
 * @date 2021-04-02
 */
public interface ICosProductCostService extends IService<CosProductCost>{
    /**
     * 查询商品成本核算主
     * 
     * @param
     * @return 商品成本核算主
     */
    public  CosProductCost selectCosProductCostById(Long productCostSid);
	/**
	 * 通过序列号查询出成本核算其他项
	 *
	 */
	public  List<CosProductCostLaborOther> getByNum(int num);

    /**
     * 查询商品成本核算主列表
     * 
     * @param cosProductCost 商品成本核算主
     * @return 商品成本核算主集合
     */
    public List<CosProductCost> selectCosProductCostList(CosProductCost cosProductCost);

	public List<CosProductCostMaterialResponse> reportMaterialList(CosProductCostMaterialRequest cosProductCostMaterialRequest);

	public List<CosProductCostLaborResponse> reportProductCostLabor(CosProductCostLaborRequest cosProductCostLaborRequest);
    /**
     * 新增商品成本核算主
     * 
     * @param
     * @return 结果
     */
    public int insertCosProductCost(CosProductCost cost);

    /**
     * 修改商品成本核算主
     * 
     * @param cost 商品成本核算主
     * @return 结果
     */
    public int updateCosProductCost(CosProductCost cost);

    /**
     * 批量删除商品成本核算主
     * 
     * @param materialSids 需要删除的商品成本核算主ID
     * @return 结果
     */
    public int deleteCosProductCostByIds(List<Long>  materialSids);

    //提交时校验
	public OrderErrRequest processCheck(OrderErrRequest request);
	/**
	 * 确认商品成本核算
	 *
	 * @param materialSids 商品成本核算主ID
	 * @return 结果
	 */
	int handleStatus(List<Long> materialSids);
	
	/**
	 * 启停用商品成本核算
	 *
	 * @return 结果
	 */
	int status(List<Long> materialSids , String status);
	/**
	 * 更新清单列
	 *
	 */
	public List<CosProductCostMaterial> updateBom(Long bomSid);
	/**
	 * 更新成本价格
	 *
	 */
	public List<CosProductCostLabor> updateCostPrice(CosProductCost cost);
	/**
	 * 价格
	 *
	 */
	public List<CosProductCostMaterial> updatePrice(Long productCostSid);

	CosProductCost getInsertInfo(BasMaterial basMaterial);

}
