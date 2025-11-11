package com.platform.ems.service.impl;

import java.util.List;
import java.util.ArrayList;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.BasCompanyAttachMapper;
import com.platform.ems.domain.BasCompanyAttach;
import com.platform.ems.service.IBasCompanyAttachService;

/**
 * 公司档案-附件Service业务层处理
 *
 * @author chenkw
 * @date 2021-09-15
 */
@Service
@SuppressWarnings("all")
public class BasCompanyAttachServiceImpl extends ServiceImpl<BasCompanyAttachMapper,BasCompanyAttach>  implements IBasCompanyAttachService {
    @Autowired
    private BasCompanyAttachMapper basCompanyAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "公司档案-附件";
    /**
     * 查询公司档案-附件
     *
     * @param attachmentSid 公司档案-附件ID
     * @return 公司档案-附件
     */
    @Override
    public BasCompanyAttach selectBasCompanyAttachById(Long attachmentSid) {
        BasCompanyAttach basCompanyAttach = basCompanyAttachMapper.selectBasCompanyAttachById(attachmentSid);
        MongodbUtil.find(basCompanyAttach);
        return  basCompanyAttach;
    }

    /**
     * 查询公司档案-附件列表
     *
     * @param basCompanyAttach 公司档案-附件
     * @return 公司档案-附件
     */
    @Override
    public List<BasCompanyAttach> selectBasCompanyAttachList(BasCompanyAttach basCompanyAttach) {
        return basCompanyAttachMapper.selectBasCompanyAttachList(basCompanyAttach);
    }

    /**
     * 新增公司档案-附件
     * 需要注意编码重复校验
     * @param basCompanyAttach 公司档案-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasCompanyAttach(BasCompanyAttach basCompanyAttach) {
        int row= basCompanyAttachMapper.insert(basCompanyAttach);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(basCompanyAttach.getAttachmentSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改公司档案-附件
     *
     * @param basCompanyAttach 公司档案-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasCompanyAttach(BasCompanyAttach basCompanyAttach) {
        BasCompanyAttach response = basCompanyAttachMapper.selectBasCompanyAttachById(basCompanyAttach.getAttachmentSid());
        int row=basCompanyAttachMapper.updateById(basCompanyAttach);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(basCompanyAttach.getAttachmentSid(), BusinessType.UPDATE.ordinal(), response,basCompanyAttach,TITLE);
        }
        return row;
    }

    /**
     * 变更公司档案-附件
     *
     * @param basCompanyAttach 公司档案-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasCompanyAttach(BasCompanyAttach basCompanyAttach) {
        BasCompanyAttach response = basCompanyAttachMapper.selectBasCompanyAttachById(basCompanyAttach.getAttachmentSid());
        int row=basCompanyAttachMapper.updateAllById(basCompanyAttach);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(basCompanyAttach.getAttachmentSid(), BusinessType.CHANGE.ordinal(), response,basCompanyAttach,TITLE);
        }
        return row;
    }

    /**
     * 批量删除公司档案-附件
     *
     * @param attachmentSids 需要删除的公司档案-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasCompanyAttachByIds(List<Long> attachmentSids) {
        return basCompanyAttachMapper.deleteBatchIds(attachmentSids);
    }

}
