package com.platform.ems.service.impl;

import java.util.List;
import java.util.ArrayList;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.mapper.DevProductPlanAttachMapper;
import com.platform.ems.domain.DevProductPlanAttach;
import com.platform.ems.service.IDevProductPlanAttachService;

/**
 * 品类规划信息-附件Service业务层处理
 *
 * @author qhq
 * @date 2021-11-08
 */
@Service
@SuppressWarnings("all")
public class DevProductPlanAttachServiceImpl extends ServiceImpl<DevProductPlanAttachMapper,DevProductPlanAttach>  implements IDevProductPlanAttachService {
    @Autowired
    private DevProductPlanAttachMapper devProductPlanAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "品类规划信息-附件";
    /**
     * 查询品类规划信息-附件
     *
     * @param attachmentSid 品类规划信息-附件ID
     * @return 品类规划信息-附件
     */
    @Override
    public DevProductPlanAttach selectDevProductPlanAttachById(Long attachmentSid) {
        DevProductPlanAttach devProductPlanAttach = devProductPlanAttachMapper.selectDevProductPlanAttachById(attachmentSid);
        MongodbUtil.find(devProductPlanAttach);
        return  devProductPlanAttach;
    }

    /**
     * 查询品类规划信息-附件列表
     *
     * @param devProductPlanAttach 品类规划信息-附件
     * @return 品类规划信息-附件
     */
    @Override
    public List<DevProductPlanAttach> selectDevProductPlanAttachList(DevProductPlanAttach devProductPlanAttach) {
        return devProductPlanAttachMapper.selectDevProductPlanAttachList(devProductPlanAttach);
    }

    /**
     * 新增品类规划信息-附件
     * 需要注意编码重复校验
     * @param devProductPlanAttach 品类规划信息-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDevProductPlanAttach(DevProductPlanAttach devProductPlanAttach) {
        int row= devProductPlanAttachMapper.insert(devProductPlanAttach);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(devProductPlanAttach.getAttachmentSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改品类规划信息-附件
     *
     * @param devProductPlanAttach 品类规划信息-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDevProductPlanAttach(DevProductPlanAttach devProductPlanAttach) {
        DevProductPlanAttach response = devProductPlanAttachMapper.selectDevProductPlanAttachById(devProductPlanAttach.getAttachmentSid());
        int row=devProductPlanAttachMapper.updateById(devProductPlanAttach);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(devProductPlanAttach.getAttachmentSid(), BusinessType.UPDATE.ordinal(), response,devProductPlanAttach,TITLE);
        }
        return row;
    }

    /**
     * 变更品类规划信息-附件
     *
     * @param devProductPlanAttach 品类规划信息-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeDevProductPlanAttach(DevProductPlanAttach devProductPlanAttach) {
        DevProductPlanAttach response = devProductPlanAttachMapper.selectDevProductPlanAttachById(devProductPlanAttach.getAttachmentSid());
                                                        int row=devProductPlanAttachMapper.updateAllById(devProductPlanAttach);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(devProductPlanAttach.getAttachmentSid(), BusinessType.CHANGE.ordinal(), response,devProductPlanAttach,TITLE);
        }
        return row;
    }

    /**
     * 批量删除品类规划信息-附件
     *
     * @param attachmentSids 需要删除的品类规划信息-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDevProductPlanAttachByIds(List<Long> attachmentSids) {
        return devProductPlanAttachMapper.deleteBatchIds(attachmentSids);
    }

}
