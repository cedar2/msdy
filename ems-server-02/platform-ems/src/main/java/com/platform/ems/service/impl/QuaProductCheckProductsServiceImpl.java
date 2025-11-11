package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.QuaProductCheckProducts;
import com.platform.ems.mapper.QuaProductCheckProductsMapper;
import com.platform.ems.service.IQuaProductCheckProductsService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 成衣检测单-款明细Service业务层处理
 *
 * @author linhongwei
 * @date 2022-04-13
 */
@Service
@SuppressWarnings("all")
public class QuaProductCheckProductsServiceImpl extends ServiceImpl<QuaProductCheckProductsMapper, QuaProductCheckProducts> implements IQuaProductCheckProductsService {
    @Autowired
    private QuaProductCheckProductsMapper quaProductCheckProductsMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "成衣检测单-款明细";

    /**
     * 查询成衣检测单-款明细
     *
     * @param productCheckProductsSid 成衣检测单-款明细ID
     * @return 成衣检测单-款明细
     */
    @Override
    public QuaProductCheckProducts selectQuaProductCheckProductsById(Long productCheckProductsSid) {
        QuaProductCheckProducts quaProductCheckProducts = quaProductCheckProductsMapper.selectQuaProductCheckProductsById(productCheckProductsSid);
        MongodbUtil.find(quaProductCheckProducts);
        return quaProductCheckProducts;
    }

    /**
     * 查询成衣检测单-款明细列表
     *
     * @param quaProductCheckProducts 成衣检测单-款明细
     * @return 成衣检测单-款明细
     */
    @Override
    public List<QuaProductCheckProducts> selectQuaProductCheckProductsList(QuaProductCheckProducts quaProductCheckProducts) {
        return quaProductCheckProductsMapper.selectQuaProductCheckProductsList(quaProductCheckProducts);
    }

    /**
     * 新增成衣检测单-款明细
     * 需要注意编码重复校验
     *
     * @param quaProductCheckProducts 成衣检测单-款明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertQuaProductCheckProducts(QuaProductCheckProducts quaProductCheckProducts) {
        int row = quaProductCheckProductsMapper.insert(quaProductCheckProducts);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(quaProductCheckProducts.getProductCheckProductsSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改成衣检测单-款明细
     *
     * @param quaProductCheckProducts 成衣检测单-款明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateQuaProductCheckProducts(QuaProductCheckProducts quaProductCheckProducts) {
        QuaProductCheckProducts response = quaProductCheckProductsMapper.selectQuaProductCheckProductsById(quaProductCheckProducts.getProductCheckProductsSid());
        int row = quaProductCheckProductsMapper.updateById(quaProductCheckProducts);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(quaProductCheckProducts.getProductCheckProductsSid(), BusinessType.UPDATE.ordinal(), response, quaProductCheckProducts, TITLE);
        }
        return row;
    }

    /**
     * 变更成衣检测单-款明细
     *
     * @param quaProductCheckProducts 成衣检测单-款明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeQuaProductCheckProducts(QuaProductCheckProducts quaProductCheckProducts) {
        QuaProductCheckProducts response = quaProductCheckProductsMapper.selectQuaProductCheckProductsById(quaProductCheckProducts.getProductCheckProductsSid());
        int row = quaProductCheckProductsMapper.updateAllById(quaProductCheckProducts);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(quaProductCheckProducts.getProductCheckProductsSid(), BusinessType.CHANGE.ordinal(), response, quaProductCheckProducts, TITLE);
        }
        return row;
    }

    /**
     * 批量删除成衣检测单-款明细
     *
     * @param productCheckProductsSids 需要删除的成衣检测单-款明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteQuaProductCheckProductsByIds(List<Long> productCheckProductsSids) {
        return quaProductCheckProductsMapper.deleteBatchIds(productCheckProductsSids);
    }

}
