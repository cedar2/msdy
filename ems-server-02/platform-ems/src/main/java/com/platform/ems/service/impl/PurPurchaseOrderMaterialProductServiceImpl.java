package com.platform.ems.service.impl;

import java.util.List;
import java.util.ArrayList;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.dto.request.PurPurchaseOrderMaterialProductAddRequest;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.mapper.PurPurchaseOrderMaterialProductMapper;
import com.platform.ems.domain.PurPurchaseOrderMaterialProduct;
import com.platform.ems.service.IPurPurchaseOrderMaterialProductService;

/**
 * undefinedService业务层处理
 *
 * @author yangqz
 * @date 2022-04-20
 */
@Service
@SuppressWarnings("all")
public class PurPurchaseOrderMaterialProductServiceImpl extends ServiceImpl<PurPurchaseOrderMaterialProductMapper,PurPurchaseOrderMaterialProduct>  implements IPurPurchaseOrderMaterialProductService {
    @Autowired
    private PurPurchaseOrderMaterialProductMapper purPurchaseOrderMaterialProductMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "undefined";
    /**
     * 查询undefined
     *
     * @param itemMaterialProductSid undefinedID
     * @return undefined
     */
    @Override
    public List<PurPurchaseOrderMaterialProduct> selectPurPurchaseOrderMaterialProductById(Long purchaseOrderItemSid) {
        List<PurPurchaseOrderMaterialProduct> list = purPurchaseOrderMaterialProductMapper.selectPurPurchaseOrderMaterialProductById(purchaseOrderItemSid);
        return  list;
    }

    /**
     * 查询undefined列表
     *
     * @param purPurchaseOrderMaterialProduct undefined
     * @return undefined
     */
    @Override
    public List<PurPurchaseOrderMaterialProduct> selectPurPurchaseOrderMaterialProductList(PurPurchaseOrderMaterialProduct purPurchaseOrderMaterialProduct) {
        return purPurchaseOrderMaterialProductMapper.selectPurPurchaseOrderMaterialProductList(purPurchaseOrderMaterialProduct);
    }

    /**
     * 新增所用商品信息
     * 需要注意编码重复校验
     * @param purPurchaseOrderMaterialProduct
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPurPurchaseOrderMaterialProduct(PurPurchaseOrderMaterialProductAddRequest request) {
        Long purchaseOrderItemSid = request.getPurchaseOrderItemSid();
        Long purchaseOrderSid = request.getPurchaseOrderSid();
        List<PurPurchaseOrderMaterialProduct> itemList = request.getItemList();
        purPurchaseOrderMaterialProductMapper.delete(new QueryWrapper<PurPurchaseOrderMaterialProduct>().lambda()
        .eq(PurPurchaseOrderMaterialProduct::getMaterialPurchaseOrderItemSid,purchaseOrderItemSid)
        );
        itemList.forEach(li->{
                li.setMaterialPurchaseOrderSid(purchaseOrderSid)
                        .setMaterialPurchaseOrderItemSid(purchaseOrderItemSid);
                purPurchaseOrderMaterialProductMapper.insert(li);
        });
        return 1;
    }

    /**
     * 修改undefined
     *
     * @param purPurchaseOrderMaterialProduct undefined
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePurPurchaseOrderMaterialProduct(PurPurchaseOrderMaterialProduct purPurchaseOrderMaterialProduct) {
        return 1;
    }

    /**
     * 变更undefined
     *
     * @param purPurchaseOrderMaterialProduct undefined
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePurPurchaseOrderMaterialProduct(PurPurchaseOrderMaterialProduct purPurchaseOrderMaterialProduct) {
        return 1;
    }

    /**
     * 批量删除undefined
     *
     * @param itemMaterialProductSids 需要删除的undefinedID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePurPurchaseOrderMaterialProductByIds(List<Long> itemMaterialProductSids) {
        return purPurchaseOrderMaterialProductMapper.deleteBatchIds(itemMaterialProductSids);
    }

    /**
    * 启用/停用
    * @param purPurchaseOrderMaterialProduct
    * @return
    */
    @Override
    public int changeStatus(PurPurchaseOrderMaterialProduct purPurchaseOrderMaterialProduct){
        int row=0;
        return row;
    }


    /**
     *更改确认状态
     * @param purPurchaseOrderMaterialProduct
     * @return
     */
    @Override
    public int check(PurPurchaseOrderMaterialProduct purPurchaseOrderMaterialProduct){
        int row=0;
        return row;
    }


}
