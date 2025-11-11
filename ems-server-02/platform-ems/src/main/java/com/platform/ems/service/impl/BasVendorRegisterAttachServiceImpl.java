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
import com.platform.ems.domain.BasVendorRegisterAttach;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.BasVendorRegisterAttachMapper;
import com.platform.ems.service.IBasVendorRegisterAttachService;

/**
 * 供应商注册-附件Service业务层处理
 *
 * @author chenkw
 * @date 2022-02-21
 */
@Service
@SuppressWarnings("all")
public class BasVendorRegisterAttachServiceImpl extends ServiceImpl<BasVendorRegisterAttachMapper, BasVendorRegisterAttach> implements IBasVendorRegisterAttachService {
    @Autowired
    private BasVendorRegisterAttachMapper basVendorRegisterAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商注册-附件";

    /**
     * 查询供应商注册-附件
     *
     * @param vendorRegisterAttachSid 供应商注册-附件ID
     * @return 供应商注册-附件
     */
    @Override
    public BasVendorRegisterAttach selectBasVendorRegisterAttachById(Long vendorRegisterAttachSid) {
        BasVendorRegisterAttach basVendorRegisterAttach = basVendorRegisterAttachMapper.selectBasVendorRegisterAttachById(vendorRegisterAttachSid);
        MongodbUtil.find(basVendorRegisterAttach);
        return basVendorRegisterAttach;
    }

    /**
     * 查询供应商注册-附件列表
     *
     * @param basVendorRegisterAttach 供应商注册-附件
     * @return 供应商注册-附件
     */
    @Override
    public List<BasVendorRegisterAttach> selectBasVendorRegisterAttachList(BasVendorRegisterAttach basVendorRegisterAttach) {
        return basVendorRegisterAttachMapper.selectBasVendorRegisterAttachList(basVendorRegisterAttach);
    }

    /**
     * 新增供应商注册-附件
     * 需要注意编码重复校验
     *
     * @param basVendorRegisterAttach 供应商注册-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorRegisterAttach(BasVendorRegisterAttach basVendorRegisterAttach) {
        int row = basVendorRegisterAttachMapper.insert(basVendorRegisterAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new BasVendorRegisterAttach(), basVendorRegisterAttach);
            MongodbUtil.insertUserLog(basVendorRegisterAttach.getVendorRegisterAttachSid(), BusinessType.INSERT.ordinal(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改供应商注册-附件
     *
     * @param basVendorRegisterAttach 供应商注册-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterAttach(BasVendorRegisterAttach basVendorRegisterAttach) {
        BasVendorRegisterAttach response = basVendorRegisterAttachMapper.selectBasVendorRegisterAttachById(basVendorRegisterAttach.getVendorRegisterAttachSid());
        int row = basVendorRegisterAttachMapper.updateById(basVendorRegisterAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basVendorRegisterAttach.getVendorRegisterAttachSid(), BusinessType.UPDATE.ordinal(), response, basVendorRegisterAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更供应商注册-附件
     *
     * @param basVendorRegisterAttach 供应商注册-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasVendorRegisterAttach(BasVendorRegisterAttach basVendorRegisterAttach) {
        BasVendorRegisterAttach response = basVendorRegisterAttachMapper.selectBasVendorRegisterAttachById(basVendorRegisterAttach.getVendorRegisterAttachSid());
        int row = basVendorRegisterAttachMapper.updateAllById(basVendorRegisterAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basVendorRegisterAttach.getVendorRegisterAttachSid(), BusinessType.CHANGE.ordinal(), response, basVendorRegisterAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商注册-附件
     *
     * @param vendorRegisterAttachSids 需要删除的供应商注册-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorRegisterAttachByIds(List<Long> vendorRegisterAttachSids) {
        int row = 0;
        for (Long sid : vendorRegisterAttachSids) {
            BasVendorRegisterAttach attach = basVendorRegisterAttachMapper.selectById(sid);
            row += basVendorRegisterAttachMapper.deleteById(sid);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(attach, new BasVendorRegisterAttach());
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 查询主表下的附件信息
     *
     * @param vendorRegisterSid 供应商注册ID
     * @return 供应商注册-附件
     */
    @Override
    public List<BasVendorRegisterAttach> selectBasVendorRegisterAttachListById(Long vendorRegisterSid) {
        List<BasVendorRegisterAttach> basVendorRegisterAttachList = basVendorRegisterAttachMapper.selectBasVendorRegisterAttachList
                (new BasVendorRegisterAttach().setVendorRegisterSid(vendorRegisterSid));
        basVendorRegisterAttachList.forEach(basVendorRegisterAttach -> {
            MongodbUtil.find(basVendorRegisterAttach);
        });
        return basVendorRegisterAttachList;
    }

    /**
     * 由主表批量新增供应商注册-附件
     *
     * @param basVendorRegisterAttachList 供应商注册-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorRegisterAttach(List<BasVendorRegisterAttach> basVendorRegisterAttachList, Long vendorRegisterSid) {
        if (CollectionUtil.isEmpty(basVendorRegisterAttachList)) {
            return 0;
        }
        basVendorRegisterAttachList.forEach(basVendorRegisterAttach -> {
            basVendorRegisterAttach.setClientId(ConstantsEms.CLIENT_ID_10001);
            basVendorRegisterAttach.setCreatorAccount(ConstantsEms.CLIENT_ID_10001);
            basVendorRegisterAttach.setVendorRegisterSid(vendorRegisterSid);
        });
        int row = basVendorRegisterAttachMapper.inserts(basVendorRegisterAttachList);
        if (row > 0) {
            //插入日志
            basVendorRegisterAttachList.forEach(basVendorRegisterAttach -> {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(new BasVendorRegisterAttach(), basVendorRegisterAttach);
                MongodbUtil.insertUserLog(basVendorRegisterAttach.getVendorRegisterAttachSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 修改供应商注册-附件
     *
     * @param basVendorRegisterAttach 供应商注册-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterAttach(List<BasVendorRegisterAttach> basVendorRegisterAttachList) {
        int row = 0;
        for (BasVendorRegisterAttach vendorRegisterAttach : basVendorRegisterAttachList) {
            BasVendorRegisterAttach response = basVendorRegisterAttachMapper.selectBasVendorRegisterAttachById(vendorRegisterAttach.getVendorRegisterAttachSid());
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, vendorRegisterAttach);
            if (msgList.size() > 0) {
                row += basVendorRegisterAttachMapper.updateById(vendorRegisterAttach);
                //插入日志
                MongodbUtil.insertUserLog(vendorRegisterAttach.getVendorRegisterAttachSid(), BusinessType.UPDATE.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 更改主表的附件明细信息
     *
     * @param list    旧的信息
     * @param request 新的请求信息
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRegisterAttach(List<BasVendorRegisterAttach> list, List<BasVendorRegisterAttach> request, Long vendorRegisterSid) {
        int row = 0;
        //旧的附件信息
        List<Long> oldIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)){
            oldIds = list.stream().map(BasVendorRegisterAttach::getVendorRegisterAttachSid).collect(Collectors.toList());
        }
        if (CollectionUtil.isNotEmpty(oldIds)) {
            //保留的附件
            List<BasVendorRegisterAttach> updateAttachList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(request)){
                updateAttachList = request.stream().filter(item -> item.getVendorRegisterAttachSid() != null).collect(Collectors.toList());
            }
            if (CollectionUtil.isNotEmpty(updateAttachList)) {
                List<Long> updateIds = updateAttachList.stream().map(BasVendorRegisterAttach::getVendorRegisterAttachSid).collect(Collectors.toList());
                //旧的附件减保留的附件等于被删除的附件
                List<Long> delIds = oldIds.stream().filter(o -> !updateIds.contains(o)).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(delIds)) {
                    row += this.deleteBasVendorRegisterAttachByIds(delIds);
                }
                row += this.updateBasVendorRegisterAttach(updateAttachList);
            } else {
                row += this.deleteBasVendorRegisterAttachByIds(oldIds);
            }
        }
        //新增加的附件
        List<BasVendorRegisterAttach> newAttachList = request.stream().filter(item -> item.getVendorRegisterAttachSid() == null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(newAttachList)) {
            row += this.insertBasVendorRegisterAttach(newAttachList, vendorRegisterSid);
        }
        return row;
    }

    /**
     * 由主表批量删除供应商注册-附件
     *
     * @param vendorRegisterSids 供应商注册-IDs
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorRegisterAttachListByIds(List<Long> vendorRegisterSids){
        List<BasVendorRegisterAttach> AttachList = basVendorRegisterAttachMapper.selectList(new QueryWrapper<BasVendorRegisterAttach>().lambda()
                .in(BasVendorRegisterAttach::getVendorRegisterSid,vendorRegisterSids));
        List<Long> AttachSids = AttachList.stream().map(BasVendorRegisterAttach::getVendorRegisterAttachSid).collect(Collectors.toList());
        return this.deleteBasVendorRegisterAttachByIds(AttachSids);
    }
}
