package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.TecModelLinePos;
import com.platform.ems.mapper.TecModelLinePosMapper;
import com.platform.ems.service.ITecModelLinePosService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 版型-线部位Service业务层处理
 *
 * @author linhongwei
 * @date 2021-10-19
 */
@Service
@SuppressWarnings("all")
public class TecModelLinePosServiceImpl extends ServiceImpl<TecModelLinePosMapper, TecModelLinePos> implements ITecModelLinePosService {
    @Autowired
    private TecModelLinePosMapper tecModelLinePosMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "版型-线部位";

    /**
     * 查询版型-线部位
     *
     * @param modelLinePosSid 版型-线部位ID
     * @return 版型-线部位
     */
    @Override
    public TecModelLinePos selectTecModelLinePosById(Long modelLinePosSid) {
        TecModelLinePos tecModelLinePos = tecModelLinePosMapper.selectTecModelLinePosById(modelLinePosSid);
        MongodbUtil.find(tecModelLinePos);
        return tecModelLinePos;
    }

    /**
     * 查询版型-线部位列表
     *
     * @param tecModelLinePos 版型-线部位
     * @return 版型-线部位
     */
    @Override
    public List<TecModelLinePos> selectTecModelLinePosList(TecModelLinePos tecModelLinePos) {
        return tecModelLinePosMapper.selectTecModelLinePosList(tecModelLinePos);
    }

    /**
     * 新增版型-线部位
     * 需要注意编码重复校验
     *
     * @param tecModelLinePos 版型-线部位
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecModelLinePos(TecModelLinePos tecModelLinePos) {
        int row = tecModelLinePosMapper.insert(tecModelLinePos);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(tecModelLinePos.getModelLinePosSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改版型-线部位
     *
     * @param tecModelLinePos 版型-线部位
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecModelLinePos(TecModelLinePos tecModelLinePos) {
        TecModelLinePos response = tecModelLinePosMapper.selectTecModelLinePosById(tecModelLinePos.getModelLinePosSid());
        int row = tecModelLinePosMapper.updateById(tecModelLinePos);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(tecModelLinePos.getModelLinePosSid(), BusinessType.UPDATE.ordinal(), response, tecModelLinePos, TITLE);
        }
        return row;
    }

    /**
     * 变更版型-线部位
     *
     * @param tecModelLinePos 版型-线部位
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeTecModelLinePos(TecModelLinePos tecModelLinePos) {
        TecModelLinePos response = tecModelLinePosMapper.selectTecModelLinePosById(tecModelLinePos.getModelLinePosSid());
        int row = tecModelLinePosMapper.updateAllById(tecModelLinePos);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(tecModelLinePos.getModelLinePosSid(), BusinessType.CHANGE.ordinal(), response, tecModelLinePos, TITLE);
        }
        return row;
    }

    /**
     * 批量删除版型-线部位
     *
     * @param modelLinePosSids 需要删除的版型-线部位ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecModelLinePosByIds(List<Long> modelLinePosSids) {
        return tecModelLinePosMapper.deleteBatchIds(modelLinePosSids);
    }

}
