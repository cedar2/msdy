package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.TecRecordFengyangAttach;
import com.platform.ems.mapper.TecRecordFengyangAttachMapper;
import com.platform.ems.service.ITecRecordFengyangAttachService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 封样记录-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-10-11
 */
@Service
@SuppressWarnings("all")
public class TecRecordFengyangAttachServiceImpl extends ServiceImpl<TecRecordFengyangAttachMapper, TecRecordFengyangAttach> implements ITecRecordFengyangAttachService {
    @Autowired
    private TecRecordFengyangAttachMapper tecRecordFengyangAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "封样记录-附件";

    /**
     * 查询封样记录-附件
     *
     * @param attachmentSid 封样记录-附件ID
     * @return 封样记录-附件
     */
    @Override
    public TecRecordFengyangAttach selectTecRecordFengyangAttachById(Long attachmentSid) {
        TecRecordFengyangAttach tecRecordFengyangAttach = tecRecordFengyangAttachMapper.selectTecRecordFengyangAttachById(attachmentSid);
        MongodbUtil.find(tecRecordFengyangAttach);
        return tecRecordFengyangAttach;
    }

    /**
     * 查询封样记录-附件列表
     *
     * @param tecRecordFengyangAttach 封样记录-附件
     * @return 封样记录-附件
     */
    @Override
    public List<TecRecordFengyangAttach> selectTecRecordFengyangAttachList(TecRecordFengyangAttach tecRecordFengyangAttach) {
        return tecRecordFengyangAttachMapper.selectTecRecordFengyangAttachList(tecRecordFengyangAttach);
    }

    /**
     * 新增封样记录-附件
     * 需要注意编码重复校验
     *
     * @param tecRecordFengyangAttach 封样记录-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecRecordFengyangAttach(TecRecordFengyangAttach tecRecordFengyangAttach) {
        int row = tecRecordFengyangAttachMapper.insert(tecRecordFengyangAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(tecRecordFengyangAttach.getAttachmentSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改封样记录-附件
     *
     * @param tecRecordFengyangAttach 封样记录-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecRecordFengyangAttach(TecRecordFengyangAttach tecRecordFengyangAttach) {
        TecRecordFengyangAttach response = tecRecordFengyangAttachMapper.selectTecRecordFengyangAttachById(tecRecordFengyangAttach.getAttachmentSid());
        int row = tecRecordFengyangAttachMapper.updateById(tecRecordFengyangAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(tecRecordFengyangAttach.getAttachmentSid(), BusinessType.UPDATE.ordinal(), response, tecRecordFengyangAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更封样记录-附件
     *
     * @param tecRecordFengyangAttach 封样记录-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeTecRecordFengyangAttach(TecRecordFengyangAttach tecRecordFengyangAttach) {
        TecRecordFengyangAttach response = tecRecordFengyangAttachMapper.selectTecRecordFengyangAttachById(tecRecordFengyangAttach.getAttachmentSid());
        int row = tecRecordFengyangAttachMapper.updateAllById(tecRecordFengyangAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(tecRecordFengyangAttach.getAttachmentSid(), BusinessType.CHANGE.ordinal(), response, tecRecordFengyangAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除封样记录-附件
     *
     * @param attachmentSids 需要删除的封样记录-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecRecordFengyangAttachByIds(List<Long> attachmentSids) {
        return tecRecordFengyangAttachMapper.deleteBatchIds(attachmentSids);
    }
}
