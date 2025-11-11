package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.TecProductLineposMat;
import com.platform.ems.mapper.TecProductLineposMatMapper;
import com.platform.ems.service.ITecProductLineposMatService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品线部位-线料Service业务层处理
 *
 * @author linhongwei
 * @date 2021-10-21
 */
@Service
@SuppressWarnings("all")
public class TecProductLineposMatServiceImpl extends ServiceImpl<TecProductLineposMatMapper, TecProductLineposMat> implements ITecProductLineposMatService {
    @Autowired
    private TecProductLineposMatMapper tecProductLineposMatMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "商品线部位-线料";

    /**
     * 查询商品线部位-线料
     *
     * @param lineposMatSid 商品线部位-线料ID
     * @return 商品线部位-线料
     */
    @Override
    public TecProductLineposMat selectTecProductLineposMatById(Long lineposMatSid) {
        TecProductLineposMat tecProductLineposMat = tecProductLineposMatMapper.selectTecProductLineposMatById(lineposMatSid);
        MongodbUtil.find(tecProductLineposMat);
        return tecProductLineposMat;
    }

    /**
     * 查询商品线部位-线料列表
     *
     * @param tecProductLineposMat 商品线部位-线料
     * @return 商品线部位-线料
     */
    @Override
    public List<TecProductLineposMat> selectTecProductLineposMatList(TecProductLineposMat tecProductLineposMat) {
        return tecProductLineposMatMapper.selectTecProductLineposMatList(tecProductLineposMat);
    }

    /**
     * 新增商品线部位-线料
     * 需要注意编码重复校验
     *
     * @param tecProductLineposMat 商品线部位-线料
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecProductLineposMat(TecProductLineposMat tecProductLineposMat) {
        int row = tecProductLineposMatMapper.insert(tecProductLineposMat);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(tecProductLineposMat.getLineposMatSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改商品线部位-线料
     *
     * @param tecProductLineposMat 商品线部位-线料
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecProductLineposMat(TecProductLineposMat tecProductLineposMat) {
        TecProductLineposMat response = tecProductLineposMatMapper.selectTecProductLineposMatById(tecProductLineposMat.getLineposMatSid());
        int row = tecProductLineposMatMapper.updateById(tecProductLineposMat);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(tecProductLineposMat.getLineposMatSid(), BusinessType.UPDATE.ordinal(), response, tecProductLineposMat, TITLE);
        }
        return row;
    }

    /**
     * 变更商品线部位-线料
     *
     * @param tecProductLineposMat 商品线部位-线料
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeTecProductLineposMat(TecProductLineposMat tecProductLineposMat) {
        TecProductLineposMat response = tecProductLineposMatMapper.selectTecProductLineposMatById(tecProductLineposMat.getLineposMatSid());
        int row = tecProductLineposMatMapper.updateAllById(tecProductLineposMat);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(tecProductLineposMat.getLineposMatSid(), BusinessType.CHANGE.ordinal(), response, tecProductLineposMat, TITLE);
        }
        return row;
    }

    /**
     * 批量删除商品线部位-线料
     *
     * @param lineposMatSids 需要删除的商品线部位-线料ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecProductLineposMatByIds(List<Long> lineposMatSids) {
        return tecProductLineposMatMapper.deleteBatchIds(lineposMatSids);
    }
}
