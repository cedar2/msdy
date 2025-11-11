package com.platform.ems.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.BasVendorRegisterTeam;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.BasVendorRegisterTeamMapper;
import com.platform.ems.service.IBasVendorRegisterTeamService;

/**
 * 供应商注册-人员信息Service业务层处理
 *
 * @author chenkw
 * @date 2022-02-21
 */
@Service
@SuppressWarnings("all")
public class BasVendorRegisterTeamServiceImpl extends ServiceImpl<BasVendorRegisterTeamMapper, BasVendorRegisterTeam> implements IBasVendorRegisterTeamService {
    @Autowired
    private BasVendorRegisterTeamMapper basVendorRegisterTeamMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商注册-人员信息";

    /**
     * 查询供应商注册-人员信息
     *
     * @param vendorRegisterTeamSid 供应商注册-人员信息ID
     * @return 供应商注册-人员信息
     */
    @Override
    public BasVendorRegisterTeam selectBasVendorRegisterTeamById(Long vendorRegisterTeamSid) {
        BasVendorRegisterTeam basVendorRegisterTeam = basVendorRegisterTeamMapper.selectBasVendorRegisterTeamById(vendorRegisterTeamSid);
        MongodbUtil.find(basVendorRegisterTeam);
        return basVendorRegisterTeam;
    }

    /**
     * 查询供应商注册-人员信息列表
     *
     * @param basVendorRegisterTeam 供应商注册-人员信息
     * @return 供应商注册-人员信息
     */
    @Override
    public List<BasVendorRegisterTeam> selectBasVendorRegisterTeamList(BasVendorRegisterTeam basVendorRegisterTeam) {
        return basVendorRegisterTeamMapper.selectBasVendorRegisterTeamList(basVendorRegisterTeam);
    }

    /**
     * 新增供应商注册-人员信息
     * 需要注意编码重复校验
     *
     * @param basVendorRegisterTeam 供应商注册-人员信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorRegisterTeam(BasVendorRegisterTeam basVendorRegisterTeam) {
        int row = basVendorRegisterTeamMapper.insert(basVendorRegisterTeam);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new BasVendorRegisterTeam(), basVendorRegisterTeam);
            MongodbUtil.insertUserLog(basVendorRegisterTeam.getVendorRegisterTeamSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改供应商注册-人员信息
     *
     * @param basVendorRegisterTeam 供应商注册-人员信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterTeam(BasVendorRegisterTeam basVendorRegisterTeam) {
        BasVendorRegisterTeam response = basVendorRegisterTeamMapper.selectBasVendorRegisterTeamById(basVendorRegisterTeam.getVendorRegisterTeamSid());
        int row = basVendorRegisterTeamMapper.updateById(basVendorRegisterTeam);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basVendorRegisterTeam.getVendorRegisterTeamSid(), BusinessType.UPDATE.ordinal(), response, basVendorRegisterTeam, TITLE);
        }
        return row;
    }

    /**
     * 变更供应商注册-人员信息
     *
     * @param basVendorRegisterTeam 供应商注册-人员信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasVendorRegisterTeam(BasVendorRegisterTeam basVendorRegisterTeam) {
        BasVendorRegisterTeam response = basVendorRegisterTeamMapper.selectBasVendorRegisterTeamById(basVendorRegisterTeam.getVendorRegisterTeamSid());
        int row = basVendorRegisterTeamMapper.updateAllById(basVendorRegisterTeam);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basVendorRegisterTeam.getVendorRegisterTeamSid(), BusinessType.CHANGE.ordinal(), response, basVendorRegisterTeam, TITLE);
        }
        return row;
    }


    /**
     * 批量删除供应商注册-人员信息
     *
     * @param vendorRegisterTeamSids 需要删除的供应商注册-人员信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorRegisterTeamByIds(List<Long> vendorRegisterTeamSids) {
        int row = 0;
        for (Long sid : vendorRegisterTeamSids) {
            BasVendorRegisterTeam response = basVendorRegisterTeamMapper.selectById(sid);
            row += basVendorRegisterTeamMapper.deleteById(sid);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, new BasVendorRegisterTeam());
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 由主表查询供应商注册-人员信息列表
     *
     * @param vendorRegisterSid 供应商注册-SID
     * @return 供应商注册-人员信息集合
     */
    @Override
    public List<BasVendorRegisterTeam> selectBasVendorRegisterTeamListById(Long vendorRegisterSid) {
        List<BasVendorRegisterTeam> response = basVendorRegisterTeamMapper.selectBasVendorRegisterTeamList
                (new BasVendorRegisterTeam().setVendorRegisterSid(vendorRegisterSid));
        response.forEach(basVendorRegisterTeam -> {
            MongodbUtil.find(basVendorRegisterTeam);
        });
        return response;
    }


    /**
     * 新增供应商注册-人员信息
     * 需要注意编码重复校验
     *
     * @param basVendorRegisterTeam 供应商注册-人员信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorRegisterTeam(List<BasVendorRegisterTeam> basVendorRegisterTeamList, Long vendorRegisterSid) {
        if (CollectionUtil.isEmpty(basVendorRegisterTeamList)) {
            return 0;
        }
        basVendorRegisterTeamList.forEach(item -> {
            item.setClientId(ConstantsEms.CLIENT_ID_10001);
            item.setCreatorAccount(ConstantsEms.CLIENT_ID_10001);
            item.setVendorRegisterSid(vendorRegisterSid);
        });
        int row = basVendorRegisterTeamMapper.inserts(basVendorRegisterTeamList);
        if (row > 0) {
            //插入日志
            basVendorRegisterTeamList.forEach(basVendorRegisterTeam -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(new BasVendorRegisterTeam(), basVendorRegisterTeam);
                MongodbUtil.insertUserLog(basVendorRegisterTeam.getVendorRegisterTeamSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 批量修改供应商注册-人员信息
     *
     * @param basVendorRegisterTeamList 供应商注册-人员信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterTeam(List<BasVendorRegisterTeam> basVendorRegisterTeamList) {
        int row = 0;
        for (BasVendorRegisterTeam basVendorRegisterTeam : basVendorRegisterTeamList) {
            BasVendorRegisterTeam response = basVendorRegisterTeamMapper.selectBasVendorRegisterTeamById(basVendorRegisterTeam.getVendorRegisterTeamSid());
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, basVendorRegisterTeam);
            if (msgList.size() > 0) {
                row += basVendorRegisterTeamMapper.updateById(basVendorRegisterTeam);
                MongodbUtil.insertUserLog(basVendorRegisterTeam.getVendorRegisterTeamSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 由主表批量修改供应商注册-人员信息
     *
     * @param basVendorRegisterTeamList 供应商注册-人员信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterTeam(List<BasVendorRegisterTeam> response, List<BasVendorRegisterTeam> request, Long vendorRegisterSid) {
        int row = 0;
        //旧的明细信息
        List<Long> oldIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(response)){
            oldIds = response.stream().map(BasVendorRegisterTeam::getVendorRegisterTeamSid).collect(Collectors.toList());
        }
        if (CollectionUtil.isNotEmpty(oldIds)) {
            //保留的明细 如果没有保留的明细就删除全部旧的
            List<BasVendorRegisterTeam> updateTeamList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(request)){
                updateTeamList = request.stream().filter(item -> item.getVendorRegisterTeamSid() != null).collect(Collectors.toList());
            }
            if (CollectionUtil.isEmpty(updateTeamList)) {
                this.deleteBasVendorRegisterTeamByIds(oldIds);
            } else {
                List<Long> updateIds = updateTeamList.stream().map(BasVendorRegisterTeam::getVendorRegisterTeamSid).collect(Collectors.toList());
                //旧的明细减保留的明细等于被删除的明细
                List<Long> delIds = oldIds.stream().filter(o -> !updateIds.contains(o)).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(delIds)) {
                    row += this.deleteBasVendorRegisterTeamByIds(delIds);
                }
                //修改保留的
                row += this.updateBasVendorRegisterTeam(updateTeamList);
            }
        }
        //新增加的明细
        List<BasVendorRegisterTeam> newTeamList = request.stream().filter(item -> item.getVendorRegisterTeamSid() == null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(newTeamList)) {
            row += this.insertBasVendorRegisterTeam(newTeamList, vendorRegisterSid);
        }
        return row;
    }

    /**
     * 由主表批量删除供应商注册-人员信息
     *
     * @param vendorRegisterSids 供应商注册-IDs
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorRegisterTeamListByIds(List<Long> vendorRegisterSids){
        List<BasVendorRegisterTeam> TeamList = basVendorRegisterTeamMapper.selectList(new QueryWrapper<BasVendorRegisterTeam>().lambda()
                .in(BasVendorRegisterTeam::getVendorRegisterSid,vendorRegisterSids));
        List<Long> TeamSids = TeamList.stream().map(BasVendorRegisterTeam::getVendorRegisterTeamSid).collect(Collectors.toList());
        return this.deleteBasVendorRegisterTeamByIds(TeamSids);
    }
}
