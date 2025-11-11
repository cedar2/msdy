package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.SamOsbSampleReimburseAttach;
import com.platform.ems.mapper.SamOsbSampleReimburseAttachMapper;
import com.platform.ems.service.ISamOsbSampleReimburseAttachService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 外采样报销单-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-12-28
 */
@Service
@SuppressWarnings("all")
public class SamOsbSampleReimburseAttachServiceImpl extends ServiceImpl<SamOsbSampleReimburseAttachMapper,SamOsbSampleReimburseAttach>  implements ISamOsbSampleReimburseAttachService {
    @Autowired
    private SamOsbSampleReimburseAttachMapper samOsbSampleReimburseAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "外采样报销单-附件";
    /**
     * 查询外采样报销单-附件
     *
     * @param attachmentSid 外采样报销单-附件ID
     * @return 外采样报销单-附件
     */
    @Override
    public SamOsbSampleReimburseAttach selectSamOsbSampleReimburseAttachById(Long attachmentSid) {
        SamOsbSampleReimburseAttach samOsbSampleReimburseAttach = samOsbSampleReimburseAttachMapper.selectSamOsbSampleReimburseAttachById(attachmentSid);
        MongodbUtil.find(samOsbSampleReimburseAttach);
        return  samOsbSampleReimburseAttach;
    }

    /**
     * 查询外采样报销单-附件列表
     *
     * @param samOsbSampleReimburseAttach 外采样报销单-附件
     * @return 外采样报销单-附件
     */
    @Override
    public List<SamOsbSampleReimburseAttach> selectSamOsbSampleReimburseAttachList(SamOsbSampleReimburseAttach samOsbSampleReimburseAttach) {
        return samOsbSampleReimburseAttachMapper.selectSamOsbSampleReimburseAttachList(samOsbSampleReimburseAttach);
    }

    /**
     * 新增外采样报销单-附件
     * 需要注意编码重复校验
     * @param samOsbSampleReimburseAttach 外采样报销单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSamOsbSampleReimburseAttach(SamOsbSampleReimburseAttach samOsbSampleReimburseAttach) {
        int row= samOsbSampleReimburseAttachMapper.insert(samOsbSampleReimburseAttach);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(samOsbSampleReimburseAttach.getAttachmentSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改外采样报销单-附件
     *
     * @param samOsbSampleReimburseAttach 外采样报销单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSamOsbSampleReimburseAttach(SamOsbSampleReimburseAttach samOsbSampleReimburseAttach) {
        SamOsbSampleReimburseAttach response = samOsbSampleReimburseAttachMapper.selectSamOsbSampleReimburseAttachById(samOsbSampleReimburseAttach.getAttachmentSid());
        int row=samOsbSampleReimburseAttachMapper.updateById(samOsbSampleReimburseAttach);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(samOsbSampleReimburseAttach.getAttachmentSid(), BusinessType.UPDATE.ordinal(), response,samOsbSampleReimburseAttach,TITLE);
        }
        return row;
    }

    /**
     * 变更外采样报销单-附件
     *
     * @param samOsbSampleReimburseAttach 外采样报销单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSamOsbSampleReimburseAttach(SamOsbSampleReimburseAttach samOsbSampleReimburseAttach) {
        SamOsbSampleReimburseAttach response = samOsbSampleReimburseAttachMapper.selectSamOsbSampleReimburseAttachById(samOsbSampleReimburseAttach.getAttachmentSid());
                                                        int row=samOsbSampleReimburseAttachMapper.updateAllById(samOsbSampleReimburseAttach);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(samOsbSampleReimburseAttach.getAttachmentSid(), BusinessType.CHANGE.ordinal(), response,samOsbSampleReimburseAttach,TITLE);
        }
        return row;
    }

    /**
     * 批量删除外采样报销单-附件
     *
     * @param attachmentSids 需要删除的外采样报销单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSamOsbSampleReimburseAttachByIds(List<Long> attachmentSids) {
        return samOsbSampleReimburseAttachMapper.deleteBatchIds(attachmentSids);
    }

}
