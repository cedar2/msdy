package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.TecProductLineposMatColor;
import com.platform.ems.mapper.TecProductLineposMatColorMapper;
import com.platform.ems.service.ITecProductLineposMatColorService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品线部位-款色线色Service业务层处理
 *
 * @author linhongwei
 * @date 2021-08-23
 */
@Service
@SuppressWarnings("all")
public class TecProductLineposMatColorServiceImpl extends ServiceImpl<TecProductLineposMatColorMapper, TecProductLineposMatColor> implements ITecProductLineposMatColorService {
    @Autowired
    private TecProductLineposMatColorMapper tecProductLineposMatColorMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "商品线部位-款色线色";

    /**
     * 查询商品线部位-款色线色
     *
     * @param lineposMatColor 商品线部位-款色线色ID
     * @return 商品线部位-款色线色
     */
    @Override
    public TecProductLineposMatColor selectTecProductLineposMatColorById(Long lineposMatColor) {
        TecProductLineposMatColor tecProductLineposMatColor = tecProductLineposMatColorMapper.selectTecProductLineposMatColorById(lineposMatColor);
        MongodbUtil.find(tecProductLineposMatColor);
        return tecProductLineposMatColor;
    }

    /**
     * 查询商品线部位-款色线色列表
     *
     * @param tecProductLineposMatColor 商品线部位-款色线色
     * @return 商品线部位-款色线色
     */
    @Override
    public List<TecProductLineposMatColor> selectTecProductLineposMatColorList(TecProductLineposMatColor tecProductLineposMatColor) {
        return tecProductLineposMatColorMapper.selectTecProductLineposMatColorList(tecProductLineposMatColor);
    }

    /**
     * 新增商品线部位-款色线色
     * 需要注意编码重复校验
     *
     * @param tecProductLineposMatColor 商品线部位-款色线色
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecProductLineposMatColor(TecProductLineposMatColor tecProductLineposMatColor) {
        int row = tecProductLineposMatColorMapper.insert(tecProductLineposMatColor);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(tecProductLineposMatColor.getLineposMatColor(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改商品线部位-款色线色
     *
     * @param tecProductLineposMatColor 商品线部位-款色线色
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecProductLineposMatColor(TecProductLineposMatColor tecProductLineposMatColor) {
        TecProductLineposMatColor response = tecProductLineposMatColorMapper.selectTecProductLineposMatColorById(tecProductLineposMatColor.getLineposMatColor());
        int row = tecProductLineposMatColorMapper.updateById(tecProductLineposMatColor);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(tecProductLineposMatColor.getLineposMatColor(), BusinessType.UPDATE.getValue(), response, tecProductLineposMatColor, TITLE);
        }
        return row;
    }

    /**
     * 变更商品线部位-款色线色
     *
     * @param tecProductLineposMatColor 商品线部位-款色线色
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeTecProductLineposMatColor(TecProductLineposMatColor tecProductLineposMatColor) {
        TecProductLineposMatColor response = tecProductLineposMatColorMapper.selectTecProductLineposMatColorById(tecProductLineposMatColor.getLineposMatColor());
        int row = tecProductLineposMatColorMapper.updateAllById(tecProductLineposMatColor);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(tecProductLineposMatColor.getLineposMatColor(), BusinessType.CHANGE.getValue(), response, tecProductLineposMatColor, TITLE);
        }
        return row;
    }

    /**
     * 批量删除商品线部位-款色线色
     *
     * @param lineposMatColors 需要删除的商品线部位-款色线色ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecProductLineposMatColorByIds(List<Long> lineposMatColors) {
        return tecProductLineposMatColorMapper.deleteBatchIds(lineposMatColors);
    }

}
