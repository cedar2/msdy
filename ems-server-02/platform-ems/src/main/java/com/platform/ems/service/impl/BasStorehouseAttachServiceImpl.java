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
import com.platform.ems.mapper.BasStorehouseAttachMapper;
import com.platform.ems.domain.BasStorehouseAttach;
import com.platform.ems.service.IBasStorehouseAttachService;

/**
 * 仓库档案-附件Service业务层处理
 *
 * @author chenwk
 * @date 2021-09-15
 */
@Service
@SuppressWarnings("all")
public class BasStorehouseAttachServiceImpl extends ServiceImpl<BasStorehouseAttachMapper,BasStorehouseAttach>  implements IBasStorehouseAttachService {
    @Autowired
    private BasStorehouseAttachMapper basStorehouseAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "仓库档案-附件";
    /**
     * 查询仓库档案-附件
     *
     * @param attachmentSid 仓库档案-附件ID
     * @return 仓库档案-附件
     */
    @Override
    public BasStorehouseAttach selectBasStorehouseAttachById(Long attachmentSid) {
        BasStorehouseAttach basStorehouseAttach = basStorehouseAttachMapper.selectBasStorehouseAttachById(attachmentSid);
        MongodbUtil.find(basStorehouseAttach);
        return  basStorehouseAttach;
    }

    /**
     * 查询仓库档案-附件列表
     *
     * @param basStorehouseAttach 仓库档案-附件
     * @return 仓库档案-附件
     */
    @Override
    public List<BasStorehouseAttach> selectBasStorehouseAttachList(BasStorehouseAttach basStorehouseAttach) {
        return basStorehouseAttachMapper.selectBasStorehouseAttachList(basStorehouseAttach);
    }

    /**
     * 新增仓库档案-附件
     * 需要注意编码重复校验
     * @param basStorehouseAttach 仓库档案-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasStorehouseAttach(BasStorehouseAttach basStorehouseAttach) {
        int row= basStorehouseAttachMapper.insert(basStorehouseAttach);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(basStorehouseAttach.getAttachmentSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改仓库档案-附件
     *
     * @param basStorehouseAttach 仓库档案-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasStorehouseAttach(BasStorehouseAttach basStorehouseAttach) {
        BasStorehouseAttach response = basStorehouseAttachMapper.selectBasStorehouseAttachById(basStorehouseAttach.getAttachmentSid());
        int row=basStorehouseAttachMapper.updateById(basStorehouseAttach);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(basStorehouseAttach.getAttachmentSid(), BusinessType.UPDATE.ordinal(), response,basStorehouseAttach,TITLE);
        }
        return row;
    }

    /**
     * 变更仓库档案-附件
     *
     * @param basStorehouseAttach 仓库档案-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasStorehouseAttach(BasStorehouseAttach basStorehouseAttach) {
        BasStorehouseAttach response = basStorehouseAttachMapper.selectBasStorehouseAttachById(basStorehouseAttach.getAttachmentSid());
        int row=basStorehouseAttachMapper.updateAllById(basStorehouseAttach);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(basStorehouseAttach.getAttachmentSid(), BusinessType.CHANGE.ordinal(), response,basStorehouseAttach,TITLE);
        }
        return row;
    }

    /**
     * 批量删除仓库档案-附件
     *
     * @param attachmentSids 需要删除的仓库档案-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasStorehouseAttachByIds(List<Long> attachmentSids) {
        return basStorehouseAttachMapper.deleteBatchIds(attachmentSids);
    }

}
