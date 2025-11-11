package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.UserOperLog;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import com.platform.ems.mapper.ManManufactureCompleteNoteAttachMapper;
import com.platform.ems.domain.ManManufactureCompleteNoteAttach;
import com.platform.ems.service.IManManufactureCompleteNoteAttachService;

/**
 * 生产完工确认单-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-09
 */
@Service
@SuppressWarnings("all")
public class ManManufactureCompleteNoteAttachServiceImpl extends ServiceImpl<ManManufactureCompleteNoteAttachMapper,ManManufactureCompleteNoteAttach>  implements IManManufactureCompleteNoteAttachService {
    @Autowired
    private ManManufactureCompleteNoteAttachMapper manManufactureCompleteNoteAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "生产完工确认单-附件";
    /**
     * 查询生产完工确认单-附件
     *
     * @param manufactureCompleteNoteAttachSid 生产完工确认单-附件ID
     * @return 生产完工确认单-附件
     */
    @Override
    public ManManufactureCompleteNoteAttach selectManManufactureCompleteNoteAttachById(Long manufactureCompleteNoteAttachSid) {
        ManManufactureCompleteNoteAttach manManufactureCompleteNoteAttach = manManufactureCompleteNoteAttachMapper.selectManManufactureCompleteNoteAttachById(manufactureCompleteNoteAttachSid);
        MongodbUtil.find(manManufactureCompleteNoteAttach);
        return  manManufactureCompleteNoteAttach;
    }

    /**
     * 查询生产完工确认单-附件列表
     *
     * @param manManufactureCompleteNoteAttach 生产完工确认单-附件
     * @return 生产完工确认单-附件
     */
    @Override
    public List<ManManufactureCompleteNoteAttach> selectManManufactureCompleteNoteAttachList(ManManufactureCompleteNoteAttach manManufactureCompleteNoteAttach) {
        return manManufactureCompleteNoteAttachMapper.selectManManufactureCompleteNoteAttachList(manManufactureCompleteNoteAttach);
    }

    /**
     * 新增生产完工确认单-附件
     * 需要注意编码重复校验
     * @param manManufactureCompleteNoteAttach 生产完工确认单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManManufactureCompleteNoteAttach(ManManufactureCompleteNoteAttach manManufactureCompleteNoteAttach) {
        int row= manManufactureCompleteNoteAttachMapper.insert(manManufactureCompleteNoteAttach);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(manManufactureCompleteNoteAttach.getManufactureCompleteNoteAttachSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改生产完工确认单-附件
     *
     * @param manManufactureCompleteNoteAttach 生产完工确认单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManManufactureCompleteNoteAttach(ManManufactureCompleteNoteAttach manManufactureCompleteNoteAttach) {
        ManManufactureCompleteNoteAttach response = manManufactureCompleteNoteAttachMapper.selectManManufactureCompleteNoteAttachById(manManufactureCompleteNoteAttach.getManufactureCompleteNoteAttachSid());
        int row=manManufactureCompleteNoteAttachMapper.updateById(manManufactureCompleteNoteAttach);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(manManufactureCompleteNoteAttach.getManufactureCompleteNoteAttachSid(), BusinessType.UPDATE.getValue(), response,manManufactureCompleteNoteAttach,TITLE);
        }
        return row;
    }

    /**
     * 变更生产完工确认单-附件
     *
     * @param manManufactureCompleteNoteAttach 生产完工确认单-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManManufactureCompleteNoteAttach(ManManufactureCompleteNoteAttach manManufactureCompleteNoteAttach) {
        ManManufactureCompleteNoteAttach response = manManufactureCompleteNoteAttachMapper.selectManManufactureCompleteNoteAttachById(manManufactureCompleteNoteAttach.getManufactureCompleteNoteAttachSid());
                                                        int row=manManufactureCompleteNoteAttachMapper.updateAllById(manManufactureCompleteNoteAttach);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(manManufactureCompleteNoteAttach.getManufactureCompleteNoteAttachSid(), BusinessType.CHANGE.getValue(), response,manManufactureCompleteNoteAttach,TITLE);
        }
        return row;
    }

    /**
     * 批量删除生产完工确认单-附件
     *
     * @param manufactureCompleteNoteAttachSids 需要删除的生产完工确认单-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManManufactureCompleteNoteAttachByIds(List<Long> manufactureCompleteNoteAttachSids) {
        return manManufactureCompleteNoteAttachMapper.deleteBatchIds(manufactureCompleteNoteAttachSids);
    }

    /**
     *更改确认状态
     * @param manManufactureCompleteNoteAttach
     * @return
     */
    @Override
    public int check(ManManufactureCompleteNoteAttach manManufactureCompleteNoteAttach){
        int row=0;
        Long[] sids=manManufactureCompleteNoteAttach.getManufactureCompleteNoteAttachSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                manManufactureCompleteNoteAttach.setManufactureCompleteNoteAttachSid(id);
                row=manManufactureCompleteNoteAttachMapper.updateById( manManufactureCompleteNoteAttach);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(manManufactureCompleteNoteAttach.getManufactureCompleteNoteAttachSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }


}
