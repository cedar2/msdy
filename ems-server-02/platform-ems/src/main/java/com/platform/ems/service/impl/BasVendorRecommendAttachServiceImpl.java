package com.platform.ems.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.mapper.BasVendorRecommendAttachMapper;
import com.platform.ems.domain.BasVendorRecommendAttach;
import com.platform.ems.service.IBasVendorRecommendAttachService;

/**
 * 供应商推荐-附件Service业务层处理
 *
 * @author chenkw
 * @date 2022-02-21
 */
@Service
@SuppressWarnings("all")
public class BasVendorRecommendAttachServiceImpl extends ServiceImpl<BasVendorRecommendAttachMapper, BasVendorRecommendAttach> implements IBasVendorRecommendAttachService {
    @Autowired
    private BasVendorRecommendAttachMapper basVendorRecommendAttachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "供应商推荐-附件";

    /**
     * 查询供应商推荐-附件
     *
     * @param vendorRecommendAttachSid 供应商推荐-附件ID
     * @return 供应商推荐-附件
     */
    @Override
    public BasVendorRecommendAttach selectBasVendorRecommendAttachById(Long vendorRecommendAttachSid) {
        BasVendorRecommendAttach basVendorRecommendAttach = basVendorRecommendAttachMapper.selectBasVendorRecommendAttachById(vendorRecommendAttachSid);
        MongodbUtil.find(basVendorRecommendAttach);
        return basVendorRecommendAttach;
    }

    /**
     * 查询主表下的联系方式信息
     *
     * @param vendorRecommendSid 供应商推荐ID
     * @return 供应商推荐-联系方式信息
     */
    @Override
    public List<BasVendorRecommendAttach> selectBasVendorRecommendAttachListById(Long vendorRecommendSid) {
        List<BasVendorRecommendAttach> basVendorRecommendAttachList = basVendorRecommendAttachMapper.selectBasVendorRecommendAttachList
                (new BasVendorRecommendAttach().setVendorRecommendSid(vendorRecommendSid));
        basVendorRecommendAttachList.forEach(basVendorRecommendAttach -> {
            MongodbUtil.find(basVendorRecommendAttach);
        });
        return basVendorRecommendAttachList;
    }

    /**
     * 查询供应商推荐-附件列表
     *
     * @param basVendorRecommendAttach 供应商推荐-附件
     * @return 供应商推荐-附件
     */
    @Override
    public List<BasVendorRecommendAttach> selectBasVendorRecommendAttachList(BasVendorRecommendAttach basVendorRecommendAttach) {
        return basVendorRecommendAttachMapper.selectBasVendorRecommendAttachList(basVendorRecommendAttach);
    }

    /**
     * 新增供应商推荐-附件
     * 需要注意编码重复校验
     *
     * @param basVendorRecommendAttach 供应商推荐-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorRecommendAttach(BasVendorRecommendAttach basVendorRecommendAttach) {
        int row = basVendorRecommendAttachMapper.insert(basVendorRecommendAttach);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new BasVendorRecommendAttach(), basVendorRecommendAttach);
            MongodbUtil.insertUserLog(basVendorRecommendAttach.getVendorRecommendAttachSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改供应商推荐-附件
     *
     * @param basVendorRecommendAttach 供应商推荐-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRecommendAttach(BasVendorRecommendAttach basVendorRecommendAttach) {
        BasVendorRecommendAttach response = basVendorRecommendAttachMapper.selectBasVendorRecommendAttachById(basVendorRecommendAttach.getVendorRecommendAttachSid());
        int row = basVendorRecommendAttachMapper.updateById(basVendorRecommendAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basVendorRecommendAttach.getVendorRecommendAttachSid(), BusinessType.UPDATE.getValue(), response, basVendorRecommendAttach, TITLE);
        }
        return row;
    }

    /**
     * 变更供应商推荐-附件
     *
     * @param basVendorRecommendAttach 供应商推荐-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasVendorRecommendAttach(BasVendorRecommendAttach basVendorRecommendAttach) {
        BasVendorRecommendAttach response = basVendorRecommendAttachMapper.selectBasVendorRecommendAttachById(basVendorRecommendAttach.getVendorRecommendAttachSid());
        int row = basVendorRecommendAttachMapper.updateAllById(basVendorRecommendAttach);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basVendorRecommendAttach.getVendorRecommendAttachSid(), BusinessType.CHANGE.getValue(), response, basVendorRecommendAttach, TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商推荐-附件
     *
     * @param vendorRecommendAttachSids 需要删除的供应商推荐-附件ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorRecommendAttachByIds(List<Long> vendorRecommendAttachSids) {
        int row = 0;
        for (Long sid : vendorRecommendAttachSids) {
            BasVendorRecommendAttach attach = basVendorRecommendAttachMapper.selectById(sid);
            row += basVendorRecommendAttachMapper.deleteById(sid);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(attach,new BasVendorRecommendAttach());
            MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 由主表批量新增供应商推荐-附件
     *
     * @param basVendorRecommendAttachList 供应商推荐-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorRecommendAttach(List<BasVendorRecommendAttach> basVendorRecommendAttachList, Long vendorRecommendSid) {
        if (CollectionUtil.isEmpty(basVendorRecommendAttachList)){
            return 0;
        }
        basVendorRecommendAttachList.forEach(basVendorRecommendAttach -> {
            basVendorRecommendAttach.setClientId(ConstantsEms.CLIENT_ID_10001);
            basVendorRecommendAttach.setCreatorAccount(ConstantsEms.CLIENT_ID_10001);
            basVendorRecommendAttach.setVendorRecommendSid(vendorRecommendSid);
        });
        int row = basVendorRecommendAttachMapper.inserts(basVendorRecommendAttachList);
        if (row > 0) {
            //插入日志
            basVendorRecommendAttachList.forEach(basVendorRecommendAttach -> {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(new BasVendorRecommendAttach(), basVendorRecommendAttach);
                MongodbUtil.insertUserLog(basVendorRecommendAttach.getVendorRecommendAttachSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 修改供应商推荐-附件
     *
     * @param basVendorRecommendAttach 供应商推荐-附件
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRecommendAttach(List<BasVendorRecommendAttach> basVendorRecommendAttachList) {
        int row = 0;
        for (BasVendorRecommendAttach vendorRecommendAttach : basVendorRecommendAttachList) {
            BasVendorRecommendAttach response = basVendorRecommendAttachMapper.selectBasVendorRecommendAttachById(vendorRecommendAttach.getVendorRecommendAttachSid());
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, vendorRecommendAttach);
            if (msgList.size() > 0) {
                row += basVendorRecommendAttachMapper.updateById(vendorRecommendAttach);
                //插入日志
                MongodbUtil.insertUserLog(vendorRecommendAttach.getVendorRecommendAttachSid(), BusinessType.UPDATE.getValue(), msgList, TITLE);
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
    public int updateBasVendorRecommendAttach(List<BasVendorRecommendAttach> list, List<BasVendorRecommendAttach> request, Long vendorRecommendSid) {
        int row = 0;
        //旧的附件信息
        List<Long> oldIds = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)){
            oldIds = list.stream().map(BasVendorRecommendAttach::getVendorRecommendAttachSid).collect(Collectors.toList());
        }
        if (CollectionUtil.isNotEmpty(oldIds)){
            //保留的附件
            List<BasVendorRecommendAttach> updateAttachList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(request)){
                updateAttachList = request.stream().filter(item -> item.getVendorRecommendAttachSid() != null).collect(Collectors.toList());
            }
            if (CollectionUtil.isNotEmpty(updateAttachList)){
                List<Long> updateIds = updateAttachList.stream().map(BasVendorRecommendAttach::getVendorRecommendAttachSid).collect(Collectors.toList());
                //旧的附件减保留的附件等于被删除的附件
                List<Long> delIds = oldIds.stream().filter(o -> !updateIds.contains(o)).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(delIds)){
                    row += this.deleteBasVendorRecommendAttachByIds(delIds);
                }
                row += this.updateBasVendorRecommendAttach(updateAttachList);
            }else {
                row += this.deleteBasVendorRecommendAttachByIds(oldIds);
            }
        }
        //新增加的附件
        List<BasVendorRecommendAttach> newAttachList = request.stream().filter(item -> item.getVendorRecommendAttachSid() == null).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(newAttachList)){
            row += this.insertBasVendorRecommendAttach(newAttachList, vendorRecommendSid);
        }
        return row;
    }
}
