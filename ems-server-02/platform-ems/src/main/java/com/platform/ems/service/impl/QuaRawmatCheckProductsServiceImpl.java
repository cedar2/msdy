package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.QuaRawmatCheckProducts;
import com.platform.ems.mapper.QuaRawmatCheckProductsMapper;
import com.platform.ems.service.IQuaRawmatCheckProductsService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 面辅料检测单-款明细Service业务层处理
 *
 * @author linhongwei
 * @date 2022-04-11
 */
@Service
@SuppressWarnings("all")
public class QuaRawmatCheckProductsServiceImpl extends ServiceImpl<QuaRawmatCheckProductsMapper, QuaRawmatCheckProducts> implements IQuaRawmatCheckProductsService {
    @Autowired
    private QuaRawmatCheckProductsMapper quaRawmatCheckProductsMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "面辅料检测单-款明细";

    /**
     * 查询面辅料检测单-款明细
     *
     * @param RawmatCheckProductsSid 面辅料检测单-款明细ID
     * @return 面辅料检测单-款明细
     */
    @Override
    public QuaRawmatCheckProducts selectQuaRawmatCheckProductsById(Long RawmatCheckProductsSid) {
        QuaRawmatCheckProducts quaRawmatCheckProducts = quaRawmatCheckProductsMapper.selectQuaRawmatCheckProductsById(RawmatCheckProductsSid);
        MongodbUtil.find(quaRawmatCheckProducts);
        return quaRawmatCheckProducts;
    }

    /**
     * 查询面辅料检测单-款明细列表
     *
     * @param quaRawmatCheckProducts 面辅料检测单-款明细
     * @return 面辅料检测单-款明细
     */
    @Override
    public List<QuaRawmatCheckProducts> selectQuaRawmatCheckProductsList(QuaRawmatCheckProducts quaRawmatCheckProducts) {
        return quaRawmatCheckProductsMapper.selectQuaRawmatCheckProductsList(quaRawmatCheckProducts);
    }

    /**
     * 新增面辅料检测单-款明细
     * 需要注意编码重复校验
     *
     * @param quaRawmatCheckProducts 面辅料检测单-款明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertQuaRawmatCheckProducts(QuaRawmatCheckProducts quaRawmatCheckProducts) {
        int row = quaRawmatCheckProductsMapper.insert(quaRawmatCheckProducts);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(quaRawmatCheckProducts.getRawmatCheckProductsSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改面辅料检测单-款明细
     *
     * @param quaRawmatCheckProducts 面辅料检测单-款明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateQuaRawmatCheckProducts(QuaRawmatCheckProducts quaRawmatCheckProducts) {
        QuaRawmatCheckProducts response = quaRawmatCheckProductsMapper.selectQuaRawmatCheckProductsById(quaRawmatCheckProducts.getRawmatCheckProductsSid());
        int row = quaRawmatCheckProductsMapper.updateById(quaRawmatCheckProducts);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(quaRawmatCheckProducts.getRawmatCheckProductsSid(), BusinessType.UPDATE.ordinal(), response, quaRawmatCheckProducts, TITLE);
        }
        return row;
    }

    /**
     * 变更面辅料检测单-款明细
     *
     * @param quaRawmatCheckProducts 面辅料检测单-款明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeQuaRawmatCheckProducts(QuaRawmatCheckProducts quaRawmatCheckProducts) {
        QuaRawmatCheckProducts response = quaRawmatCheckProductsMapper.selectQuaRawmatCheckProductsById(quaRawmatCheckProducts.getRawmatCheckProductsSid());
        int row = quaRawmatCheckProductsMapper.updateAllById(quaRawmatCheckProducts);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(quaRawmatCheckProducts.getRawmatCheckProductsSid(), BusinessType.CHANGE.ordinal(), response, quaRawmatCheckProducts, TITLE);
        }
        return row;
    }

    /**
     * 批量删除面辅料检测单-款明细
     *
     * @param RawmatCheckProductsSids 需要删除的面辅料检测单-款明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteQuaRawmatCheckProductsByIds(List<Long> RawmatCheckProductsSids) {
        return quaRawmatCheckProductsMapper.deleteBatchIds(RawmatCheckProductsSids);
    }

}
