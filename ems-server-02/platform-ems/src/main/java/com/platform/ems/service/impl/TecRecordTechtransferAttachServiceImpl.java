package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.TecRecordTechtransferAttach;
import com.platform.ems.mapper.TecRecordTechtransferAttachMapper;
import com.platform.ems.service.ITecRecordTechtransferAttachService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 技术转移记录-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-10-11
 */
@Service
@SuppressWarnings("all")
public class TecRecordTechtransferAttachServiceImpl extends ServiceImpl<TecRecordTechtransferAttachMapper, TecRecordTechtransferAttach> implements ITecRecordTechtransferAttachService {
    @Autowired
    private TecRecordTechtransferAttachMapper tecRecordTechtransferAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "技术转移记录-附件";

    /**
     * 查询技术转移记录-附件
     *
     * @param attachmentSid 技术转移记录-附件ID
     * @return 技术转移记录-附件
     */
    @Override
    public TecRecordTechtransferAttach selectTecRecordTechtransferAttachById(Long attachmentSid) {
        TecRecordTechtransferAttach tecRecordTechtransferAttach = tecRecordTechtransferAttachMapper.selectTecRecordTechtransferAttachById(attachmentSid);
        MongodbUtil.find(tecRecordTechtransferAttach);
        return tecRecordTechtransferAttach;
    }

    /**
     * 查询技术转移记录-附件列表
     *
     * @param tecRecordTechtransferAttach 技术转移记录-附件
     * @return 技术转移记录-附件
     */
    @Override
    public List<TecRecordTechtransferAttach> selectTecRecordTechtransferAttachList(TecRecordTechtransferAttach tecRecordTechtransferAttach) {
        return tecRecordTechtransferAttachMapper.selectTecRecordTechtransferAttachList(tecRecordTechtransferAttach);
    }

    /**
     * 新增技术转移记录-附件
     * 需要注意编码重复校验
     *
     * @param tecRecordTechtransferAttach 技术转移记录-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTecRecordTechtransferAttach(TecRecordTechtransferAttach tecRecordTechtransferAttach) {
        int row = tecRecordTechtransferAttachMapper.insert(tecRecordTechtransferAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(tecRecordTechtransferAttach.getAttachmentSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改技术转移记录-附件
     *
     * @param tecRecordTechtransferAttach 技术转移记录-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateTecRecordTechtransferAttach(TecRecordTechtransferAttach tecRecordTechtransferAttach) {
        TecRecordTechtransferAttach response = tecRecordTechtransferAttachMapper.selectTecRecordTechtransferAttachById(tecRecordTechtransferAttach.getAttachmentSid());
        int row = tecRecordTechtransferAttachMapper.updateById(tecRecordTechtransferAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(tecRecordTechtransferAttach.getAttachmentSid(), BusinessType.UPDATE.ordinal(), response, tecRecordTechtransferAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更技术转移记录-附件
     *
     * @param tecRecordTechtransferAttach 技术转移记录-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeTecRecordTechtransferAttach(TecRecordTechtransferAttach tecRecordTechtransferAttach) {
        TecRecordTechtransferAttach response = tecRecordTechtransferAttachMapper.selectTecRecordTechtransferAttachById(tecRecordTechtransferAttach.getAttachmentSid());
        int row = tecRecordTechtransferAttachMapper.updateAllById(tecRecordTechtransferAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(tecRecordTechtransferAttach.getAttachmentSid(), BusinessType.CHANGE.ordinal(), response, tecRecordTechtransferAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除技术转移记录-附件
     *
     * @param attachmentSids 需要删除的技术转移记录-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTecRecordTechtransferAttachByIds(List<Long> attachmentSids) {
        return tecRecordTechtransferAttachMapper.deleteBatchIds(attachmentSids);
    }
}
