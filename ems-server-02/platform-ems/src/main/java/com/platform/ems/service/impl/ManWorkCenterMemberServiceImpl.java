package com.platform.ems.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.ManWorkCenterMember;
import com.platform.ems.mapper.ManWorkCenterMemberMapper;
import com.platform.ems.service.IManWorkCenterMemberService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 工作中心-成员Service业务层处理
 *
 * @author c
 * @date 2022-03-21
 */
@Service
@SuppressWarnings("all")
public class ManWorkCenterMemberServiceImpl extends ServiceImpl<ManWorkCenterMemberMapper, ManWorkCenterMember> implements IManWorkCenterMemberService {
    @Autowired
    private ManWorkCenterMemberMapper manWorkCenterMemberMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "工作中心-成员";

    /**
     * 查询工作中心-成员
     *
     * @param workCenterMemberSid 工作中心-成员ID
     * @return 工作中心-成员
     */
    @Override
    public ManWorkCenterMember selectManWorkCenterMemberById(Long workCenterMemberSid) {
        ManWorkCenterMember manWorkCenterMember = manWorkCenterMemberMapper.selectManWorkCenterMemberById(workCenterMemberSid);
        MongodbUtil.find(manWorkCenterMember);
        return manWorkCenterMember;
    }

    /**
     * 查询工作中心-成员列表
     *
     * @param manWorkCenterMember 工作中心-成员
     * @return 工作中心-成员
     */
    @Override
    public List<ManWorkCenterMember> selectManWorkCenterMemberList(ManWorkCenterMember manWorkCenterMember) {
        return manWorkCenterMemberMapper.selectManWorkCenterMemberList(manWorkCenterMember);
    }

    /**
     * 新增工作中心-成员
     * 需要注意编码重复校验
     *
     * @param manWorkCenterMember 工作中心-成员
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManWorkCenterMember(ManWorkCenterMember manWorkCenterMember) {
        int row = manWorkCenterMemberMapper.insert(manWorkCenterMember);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(manWorkCenterMember.getWorkCenterMemberSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改工作中心-成员
     *
     * @param manWorkCenterMember 工作中心-成员
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManWorkCenterMember(ManWorkCenterMember manWorkCenterMember) {
        ManWorkCenterMember response = manWorkCenterMemberMapper.selectManWorkCenterMemberById(manWorkCenterMember.getWorkCenterMemberSid());
        int row = manWorkCenterMemberMapper.updateById(manWorkCenterMember);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(manWorkCenterMember.getWorkCenterMemberSid(), BusinessType.UPDATE.ordinal(), response, manWorkCenterMember, TITLE);
        }
        return row;
    }

    /**
     * 变更工作中心-成员
     *
     * @param manWorkCenterMember 工作中心-成员
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManWorkCenterMember(ManWorkCenterMember manWorkCenterMember) {
        ManWorkCenterMember response = manWorkCenterMemberMapper.selectManWorkCenterMemberById(manWorkCenterMember.getWorkCenterMemberSid());
        int row = manWorkCenterMemberMapper.updateAllById(manWorkCenterMember);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(manWorkCenterMember.getWorkCenterMemberSid(), BusinessType.CHANGE.ordinal(), response, manWorkCenterMember, TITLE);
        }
        return row;
    }

    /**
     * 批量删除工作中心-成员
     *
     * @param workCenterMemberSids 需要删除的工作中心-成员ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManWorkCenterMemberByIds(List<Long> workCenterMemberSids) {
        return manWorkCenterMemberMapper.deleteBatchIds(workCenterMemberSids);
    }

}
