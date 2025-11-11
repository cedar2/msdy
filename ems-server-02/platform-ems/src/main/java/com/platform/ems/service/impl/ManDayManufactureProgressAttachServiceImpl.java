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
import com.platform.ems.mapper.ManDayManufactureProgressAttachMapper;
import com.platform.ems.domain.ManDayManufactureProgressAttach;
import com.platform.ems.service.IManDayManufactureProgressAttachService;

/**
 * 生产进度日报-附件Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-09
 */
@Service
@SuppressWarnings("all")
public class ManDayManufactureProgressAttachServiceImpl extends ServiceImpl<ManDayManufactureProgressAttachMapper,ManDayManufactureProgressAttach>  implements IManDayManufactureProgressAttachService {
    @Autowired
    private ManDayManufactureProgressAttachMapper manDayManufactureProgressAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "生产进度日报-附件";
    /**
     * 查询生产进度日报-附件
     *
     * @param dayManufactureProgressAttachSid 生产进度日报-附件ID
     * @return 生产进度日报-附件
     */
    @Override
    public ManDayManufactureProgressAttach selectManDayManufactureProgressAttachById(Long dayManufactureProgressAttachSid) {
        ManDayManufactureProgressAttach manDayManufactureProgressAttach = manDayManufactureProgressAttachMapper.selectManDayManufactureProgressAttachById(dayManufactureProgressAttachSid);
        MongodbUtil.find(manDayManufactureProgressAttach);
        return  manDayManufactureProgressAttach;
    }

    /**
     * 查询生产进度日报-附件列表
     *
     * @param manDayManufactureProgressAttach 生产进度日报-附件
     * @return 生产进度日报-附件
     */
    @Override
    public List<ManDayManufactureProgressAttach> selectManDayManufactureProgressAttachList(ManDayManufactureProgressAttach manDayManufactureProgressAttach) {
        return manDayManufactureProgressAttachMapper.selectManDayManufactureProgressAttachList(manDayManufactureProgressAttach);
    }

    /**
     * 新增生产进度日报-附件
     * 需要注意编码重复校验
     * @param manDayManufactureProgressAttach 生产进度日报-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManDayManufactureProgressAttach(ManDayManufactureProgressAttach manDayManufactureProgressAttach) {
        int row= manDayManufactureProgressAttachMapper.insert(manDayManufactureProgressAttach);
        if(row>0){
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(manDayManufactureProgressAttach.getDayManufactureProgressAttachSid(), BusinessType.INSERT.ordinal(), msgList,TITLE);
        }
        return row;
    }

    /**
     * 修改生产进度日报-附件
     *
     * @param manDayManufactureProgressAttach 生产进度日报-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManDayManufactureProgressAttach(ManDayManufactureProgressAttach manDayManufactureProgressAttach) {
        ManDayManufactureProgressAttach response = manDayManufactureProgressAttachMapper.selectManDayManufactureProgressAttachById(manDayManufactureProgressAttach.getDayManufactureProgressAttachSid());
        int row=manDayManufactureProgressAttachMapper.updateById(manDayManufactureProgressAttach);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(manDayManufactureProgressAttach.getDayManufactureProgressAttachSid(), BusinessType.UPDATE.ordinal(), response,manDayManufactureProgressAttach,TITLE);
        }
        return row;
    }

    /**
     * 变更生产进度日报-附件
     *
     * @param manDayManufactureProgressAttach 生产进度日报-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManDayManufactureProgressAttach(ManDayManufactureProgressAttach manDayManufactureProgressAttach) {
        ManDayManufactureProgressAttach response = manDayManufactureProgressAttachMapper.selectManDayManufactureProgressAttachById(manDayManufactureProgressAttach.getDayManufactureProgressAttachSid());
                                                        int row=manDayManufactureProgressAttachMapper.updateAllById(manDayManufactureProgressAttach);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(manDayManufactureProgressAttach.getDayManufactureProgressAttachSid(), BusinessType.CHANGE.ordinal(), response,manDayManufactureProgressAttach,TITLE);
        }
        return row;
    }

    /**
     * 批量删除生产进度日报-附件
     *
     * @param dayManufactureProgressAttachSids 需要删除的生产进度日报-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManDayManufactureProgressAttachByIds(List<Long> dayManufactureProgressAttachSids) {
        return manDayManufactureProgressAttachMapper.deleteBatchIds(dayManufactureProgressAttachSids);
    }

    /**
     *更改确认状态
     * @param manDayManufactureProgressAttach
     * @return
     */
    @Override
    public int check(ManDayManufactureProgressAttach manDayManufactureProgressAttach){
        int row=0;
        Long[] sids=manDayManufactureProgressAttach.getDayManufactureProgressAttachSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                manDayManufactureProgressAttach.setDayManufactureProgressAttachSid(id);
                row=manDayManufactureProgressAttachMapper.updateById( manDayManufactureProgressAttach);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList=new ArrayList<>();
                MongodbUtil.insertUserLog(manDayManufactureProgressAttach.getDayManufactureProgressAttachSid(), BusinessType.CHECK.ordinal(), msgList,TITLE);
            }
        }
        return row;
    }


}
