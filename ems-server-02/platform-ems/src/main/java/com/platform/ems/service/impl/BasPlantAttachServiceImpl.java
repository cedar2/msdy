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
import com.platform.ems.mapper.BasPlantAttachMapper;
import com.platform.ems.domain.BasPlantAttach;
import com.platform.ems.service.IBasPlantAttachService;

/**
 * 工厂档案-附件Service业务层处理
 *
 * @author chenkw
 * @date 2021-09-15
 */
@Service
@SuppressWarnings("all")
public class BasPlantAttachServiceImpl extends ServiceImpl<BasPlantAttachMapper,BasPlantAttach>  implements IBasPlantAttachService {
    @Autowired
    private BasPlantAttachMapper basPlantAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "工厂档案-附件";
    /**
     * 查询工厂档案-附件
     *
     * @param attachmentSid 工厂档案-附件ID
     * @return 工厂档案-附件
     */
    @Override
    public BasPlantAttach selectBasPlantAttachById(Long attachmentSid) {
        BasPlantAttach basPlantAttach = basPlantAttachMapper.selectBasPlantAttachById(attachmentSid);
        MongodbUtil.find(basPlantAttach);
        return  basPlantAttach;
    }

    /**
     * 查询工厂档案-附件列表
     *
     * @param basPlantAttach 工厂档案-附件
     * @return 工厂档案-附件
     */
    @Override
    public List<BasPlantAttach> selectBasPlantAttachList(BasPlantAttach basPlantAttach) {
        return basPlantAttachMapper.selectBasPlantAttachList(basPlantAttach);
    }

    /**
     * 新增工厂档案-附件
     * 需要注意编码重复校验
     * @param basPlantAttach 工厂档案-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasPlantAttach(BasPlantAttach basPlantAttach) {
        int row= basPlantAttachMapper.insert(basPlantAttach);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(basPlantAttach.getAttachmentSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改工厂档案-附件
     *
     * @param basPlantAttach 工厂档案-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasPlantAttach(BasPlantAttach basPlantAttach) {
        BasPlantAttach response = basPlantAttachMapper.selectBasPlantAttachById(basPlantAttach.getAttachmentSid());
        int row=basPlantAttachMapper.updateById(basPlantAttach);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(basPlantAttach.getAttachmentSid(), BusinessType.UPDATE.ordinal(), response,basPlantAttach,TITLE);
        }
        return row;
    }

    /**
     * 变更工厂档案-附件
     *
     * @param basPlantAttach 工厂档案-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasPlantAttach(BasPlantAttach basPlantAttach) {
        BasPlantAttach response = basPlantAttachMapper.selectBasPlantAttachById(basPlantAttach.getAttachmentSid());
        int row=basPlantAttachMapper.updateAllById(basPlantAttach);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(basPlantAttach.getAttachmentSid(), BusinessType.CHANGE.ordinal(), response,basPlantAttach,TITLE);
        }
        return row;
    }

    /**
     * 批量删除工厂档案-附件
     *
     * @param attachmentSids 需要删除的工厂档案-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasPlantAttachByIds(List<Long> attachmentSids) {
        return basPlantAttachMapper.deleteBatchIds(attachmentSids);
    }

}
