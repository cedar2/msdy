package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.QuaSpecraftCheckProducts;
import com.platform.ems.mapper.QuaSpecraftCheckProductsMapper;
import com.platform.ems.service.IQuaSpecraftCheckProductsService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 特殊工艺检测单-款明细Service业务层处理
 *
 * @author linhongwei
 * @date 2022-04-12
 */
@Service
@SuppressWarnings("all")
public class QuaSpecraftCheckProductsServiceImpl extends ServiceImpl<QuaSpecraftCheckProductsMapper, QuaSpecraftCheckProducts> implements IQuaSpecraftCheckProductsService {
    @Autowired
    private QuaSpecraftCheckProductsMapper quaSpecraftCheckProductsMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "特殊工艺检测单-款明细";

    /**
     * 查询特殊工艺检测单-款明细
     *
     * @param specraftCheckProductsSid 特殊工艺检测单-款明细ID
     * @return 特殊工艺检测单-款明细
     */
    @Override
    public QuaSpecraftCheckProducts selectQuaSpecraftCheckProductsById(Long specraftCheckProductsSid) {
        QuaSpecraftCheckProducts quaSpecraftCheckProducts = quaSpecraftCheckProductsMapper.selectQuaSpecraftCheckProductsById(specraftCheckProductsSid);
        MongodbUtil.find(quaSpecraftCheckProducts);
        return quaSpecraftCheckProducts;
    }

    /**
     * 查询特殊工艺检测单-款明细列表
     *
     * @param quaSpecraftCheckProducts 特殊工艺检测单-款明细
     * @return 特殊工艺检测单-款明细
     */
    @Override
    public List<QuaSpecraftCheckProducts> selectQuaSpecraftCheckProductsList(QuaSpecraftCheckProducts quaSpecraftCheckProducts) {
        return quaSpecraftCheckProductsMapper.selectQuaSpecraftCheckProductsList(quaSpecraftCheckProducts);
    }

    /**
     * 新增特殊工艺检测单-款明细
     * 需要注意编码重复校验
     *
     * @param quaSpecraftCheckProducts 特殊工艺检测单-款明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertQuaSpecraftCheckProducts(QuaSpecraftCheckProducts quaSpecraftCheckProducts) {
        int row = quaSpecraftCheckProductsMapper.insert(quaSpecraftCheckProducts);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(quaSpecraftCheckProducts.getSpecraftCheckProductsSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改特殊工艺检测单-款明细
     *
     * @param quaSpecraftCheckProducts 特殊工艺检测单-款明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateQuaSpecraftCheckProducts(QuaSpecraftCheckProducts quaSpecraftCheckProducts) {
        QuaSpecraftCheckProducts response = quaSpecraftCheckProductsMapper.selectQuaSpecraftCheckProductsById(quaSpecraftCheckProducts.getSpecraftCheckProductsSid());
        int row = quaSpecraftCheckProductsMapper.updateById(quaSpecraftCheckProducts);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(quaSpecraftCheckProducts.getSpecraftCheckProductsSid(), BusinessType.UPDATE.ordinal(), response, quaSpecraftCheckProducts, TITLE);
        }
        return row;
    }

    /**
     * 变更特殊工艺检测单-款明细
     *
     * @param quaSpecraftCheckProducts 特殊工艺检测单-款明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeQuaSpecraftCheckProducts(QuaSpecraftCheckProducts quaSpecraftCheckProducts) {
        QuaSpecraftCheckProducts response = quaSpecraftCheckProductsMapper.selectQuaSpecraftCheckProductsById(quaSpecraftCheckProducts.getSpecraftCheckProductsSid());
        int row = quaSpecraftCheckProductsMapper.updateAllById(quaSpecraftCheckProducts);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(quaSpecraftCheckProducts.getSpecraftCheckProductsSid(), BusinessType.CHANGE.ordinal(), response, quaSpecraftCheckProducts, TITLE);
        }
        return row;
    }

    /**
     * 批量删除特殊工艺检测单-款明细
     *
     * @param specraftCheckProductsSids 需要删除的特殊工艺检测单-款明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteQuaSpecraftCheckProductsByIds(List<Long> specraftCheckProductsSids) {
        return quaSpecraftCheckProductsMapper.deleteBatchIds(specraftCheckProductsSids);
    }

}
