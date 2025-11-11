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
import com.platform.ems.mapper.DevDesignDrawFormAttachMapper;
import com.platform.ems.domain.DevDesignDrawFormAttach;
import com.platform.ems.service.IDevDesignDrawFormAttachService;

/**
 * 图稿批复单-附件Service业务层处理
 *
 * @author qhq
 * @date 2021-11-05
 */
@Service
@SuppressWarnings("all")
public class DevDesignDrawFormAttachServiceImpl extends ServiceImpl<DevDesignDrawFormAttachMapper,DevDesignDrawFormAttach>  implements IDevDesignDrawFormAttachService {
    @Autowired
    private DevDesignDrawFormAttachMapper devDesignDrawFormAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "图稿批复单-附件";
    /**
     * 查询图稿批复单-附件
     *
     * @param attachmentSid 图稿批复单-附件ID
     * @return 图稿批复单-附件
     */
    @Override
    public DevDesignDrawFormAttach selectDevDesignDrawFormAttachById(Long attachmentSid) {
        DevDesignDrawFormAttach devDesignDrawFormAttach = devDesignDrawFormAttachMapper.selectDevDesignDrawFormAttachById(attachmentSid);
        MongodbUtil.find(devDesignDrawFormAttach);
        return  devDesignDrawFormAttach;
    }

    /**
     * 查询图稿批复单-附件列表
     *
     * @param devDesignDrawFormAttach 图稿批复单-附件
     * @return 图稿批复单-附件
     */
    @Override
    public List<DevDesignDrawFormAttach> selectDevDesignDrawFormAttachList(DevDesignDrawFormAttach devDesignDrawFormAttach) {
        return devDesignDrawFormAttachMapper.selectDevDesignDrawFormAttachList(devDesignDrawFormAttach);
    }

    /**
     * 新增图稿批复单-附件
     * 需要注意编码重复校验
     * @param devDesignDrawFormAttach 图稿批复单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDevDesignDrawFormAttach(DevDesignDrawFormAttach devDesignDrawFormAttach) {
        int row= devDesignDrawFormAttachMapper.insert(devDesignDrawFormAttach);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(devDesignDrawFormAttach.getAttachmentSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改图稿批复单-附件
     *
     * @param devDesignDrawFormAttach 图稿批复单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDevDesignDrawFormAttach(DevDesignDrawFormAttach devDesignDrawFormAttach) {
        DevDesignDrawFormAttach response = devDesignDrawFormAttachMapper.selectDevDesignDrawFormAttachById(devDesignDrawFormAttach.getAttachmentSid());
        int row=devDesignDrawFormAttachMapper.updateById(devDesignDrawFormAttach);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(devDesignDrawFormAttach.getAttachmentSid(), BusinessType.UPDATE.ordinal(), response,devDesignDrawFormAttach,TITLE);
        }
        return row;
    }

    /**
     * 变更图稿批复单-附件
     *
     * @param devDesignDrawFormAttach 图稿批复单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeDevDesignDrawFormAttach(DevDesignDrawFormAttach devDesignDrawFormAttach) {
        DevDesignDrawFormAttach response = devDesignDrawFormAttachMapper.selectDevDesignDrawFormAttachById(devDesignDrawFormAttach.getAttachmentSid());
                                                        int row=devDesignDrawFormAttachMapper.updateAllById(devDesignDrawFormAttach);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(devDesignDrawFormAttach.getAttachmentSid(), BusinessType.CHANGE.ordinal(), response,devDesignDrawFormAttach,TITLE);
        }
        return row;
    }

    /**
     * 批量删除图稿批复单-附件
     *
     * @param attachmentSids 需要删除的图稿批复单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDevDesignDrawFormAttachByIds(List<Long> attachmentSids) {
        return devDesignDrawFormAttachMapper.deleteBatchIds(attachmentSids);
    }

}
