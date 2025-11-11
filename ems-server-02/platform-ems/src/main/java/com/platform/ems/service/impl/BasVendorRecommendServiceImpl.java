package com.platform.ems.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IBasVendorRecommendAddrService;
import com.platform.ems.service.IBasVendorRecommendAttachService;
import com.platform.ems.util.MongodbDeal;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.service.IBasVendorRecommendService;

/**
 * 供应商推荐-基础Service业务层处理
 *
 * @author chenkw
 * @date 2022-02-21
 */
@Service
@SuppressWarnings("all")
public class BasVendorRecommendServiceImpl extends ServiceImpl<BasVendorRecommendMapper, BasVendorRecommend> implements IBasVendorRecommendService {
    @Autowired
    private BasVendorRecommendMapper basVendorRecommendMapper;
    @Autowired
    private BasVendorRegisterMapper basVendorRegisterMapper;
    @Autowired
    private BasVendorRecommendAddrMapper basVendorRecommendAddrMapper;
    @Autowired
    private BasVendorRecommendAttachMapper basVendorRecommendAttachMapper;
    @Autowired
    private BasVendorMapper basVendorMapper;
    @Autowired
    private IBasVendorRecommendAddrService basVendorRecommendAddrService;
    @Autowired
    private IBasVendorRecommendAttachService basVendorRecommendAttachService;
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String TITLE = "供应商推荐-基础";

    /**
     * 查询供应商推荐-基础
     *
     * @param vendorRecommendSid 供应商推荐-基础ID
     * @return 供应商推荐-基础
     */
    @Override
    public BasVendorRecommend selectBasVendorRecommendById(Long vendorRecommendSid) {
        BasVendorRecommend basVendorRecommend = basVendorRecommendMapper.selectBasVendorRecommendById(vendorRecommendSid);
        //联系方式信息
        List<BasVendorRecommendAddr> addrList = basVendorRecommendAddrService.selectBasVendorRecommendAddrListById(vendorRecommendSid);
        basVendorRecommend.setAddrList(addrList);
        //附件清单信息
        List<BasVendorRecommendAttach> attachmentList = basVendorRecommendAttachService.selectBasVendorRecommendAttachListById(vendorRecommendSid);
        basVendorRecommend.setAttachmentList(attachmentList);
        MongodbUtil.find(basVendorRecommend);
        return basVendorRecommend;
    }

    /**
     * 查询供应商推荐-基础列表
     *
     * @param basVendorRecommend 供应商推荐-基础
     * @return 供应商推荐-基础
     */
    @Override
    public List<BasVendorRecommend> selectBasVendorRecommendList(BasVendorRecommend basVendorRecommend) {
        return basVendorRecommendMapper.selectBasVendorRecommendList(basVendorRecommend);
    }

    /**
     * 新增供应商推荐-基础
     * 需要注意编码重复校验
     *
     * @param basVendorRecommend 供应商推荐-基础
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasVendorRecommend(BasVendorRecommend basVendorRecommend) {
        basVendorRecommend.setClientId(ConstantsEms.CLIENT_ID_10001);
        basVendorRecommend.setCreatorAccount(ConstantsEms.CLIENT_ID_10001);
        checkName(basVendorRecommend);
        int row = basVendorRecommendMapper.insert(basVendorRecommend);
        if (row > 0) {
            int addr = basVendorRecommendAddrService.insertBasVendorRecommendAddr(basVendorRecommend.getAddrList(),basVendorRecommend.getVendorRecommendSid());
            int attach = basVendorRecommendAttachService.insertBasVendorRecommendAttach(basVendorRecommend.getAttachmentList(),basVendorRecommend.getVendorRecommendSid());
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new BasVendorRecommend(), basVendorRecommend);
            MongodbUtil.insertUserLog(basVendorRecommend.getVendorRecommendSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改供应商推荐-基础
     *
     * @param basVendorRecommend 供应商推荐-基础
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasVendorRecommend(BasVendorRecommend basVendorRecommend) {
        checkName(basVendorRecommend);
        BasVendorRecommend response = this.selectBasVendorRecommendById(basVendorRecommend.getVendorRecommendSid());
        int row = basVendorRecommendMapper.updateById(basVendorRecommend);
        if (row > 0) {
            basVendorRecommendAddrService.updateBasVendorRecommendAddr(response.getAddrList(),basVendorRecommend.getAddrList(),basVendorRecommend.getVendorRecommendSid());
            basVendorRecommendAttachService.updateBasVendorRecommendAttach(response.getAttachmentList(),basVendorRecommend.getAttachmentList(),basVendorRecommend.getVendorRecommendSid());
            //插入日志
            MongodbUtil.insertUserLog(basVendorRecommend.getVendorRecommendSid(), BusinessType.UPDATE.getValue(), response, basVendorRecommend, TITLE);
        }
        return row;
    }

    /**
     * 变更供应商推荐-基础
     *
     * @param basVendorRecommend 供应商推荐-基础
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasVendorRecommend(BasVendorRecommend basVendorRecommend) {
        checkName(basVendorRecommend);
        BasVendorRecommend response = this.selectBasVendorRecommendById(basVendorRecommend.getVendorRecommendSid());
        int row = basVendorRecommendMapper.updateAllById(basVendorRecommend);
        if (row > 0) {
            basVendorRecommendAddrService.updateBasVendorRecommendAddr(response.getAddrList(),basVendorRecommend.getAddrList(),basVendorRecommend.getVendorRecommendSid());
            basVendorRecommendAttachService.updateBasVendorRecommendAttach(response.getAttachmentList(),basVendorRecommend.getAttachmentList(),basVendorRecommend.getVendorRecommendSid());
            //插入日志
            MongodbUtil.insertUserLog(basVendorRecommend.getVendorRecommendSid(), BusinessType.CHANGE.getValue(), response, basVendorRecommend, TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商推荐-基础
     *
     * @param vendorRecommendSids 需要删除的供应商推荐-基础ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasVendorRecommendByIds(List<Long> vendorRecommendSids) {
        int row = 0;
        for (Long vendorRecommendSid : vendorRecommendSids) {
            BasVendorRecommend basVendorRecommend = basVendorRecommendMapper.selectById(vendorRecommendSid);
            row += basVendorRecommendMapper.deleteById(vendorRecommendSid);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(basVendorRecommend, new BasVendorRecommendAddr());
            MongodbUtil.insertUserLog(vendorRecommendSid, BusinessType.DELETE.getValue(), msgList, TITLE);
        }
        List<BasVendorRecommendAddr> addrList = basVendorRecommendAddrMapper.selectList(new QueryWrapper<BasVendorRecommendAddr>().lambda()
                .in(BasVendorRecommendAddr::getVendorRecommendSid,vendorRecommendSids));
        List<Long> addrSids = addrList.stream().map(BasVendorRecommendAddr::getVendorRecommendContactSid).collect(Collectors.toList());
        basVendorRecommendAddrService.deleteBasVendorRecommendAddrByIds(addrSids);
        List<BasVendorRecommendAttach> attachList = basVendorRecommendAttachMapper.selectList(new QueryWrapper<BasVendorRecommendAttach>().lambda()
                .in(BasVendorRecommendAttach::getVendorRecommendSid,vendorRecommendSids));
        List<Long> attachSids = attachList.stream().map(BasVendorRecommendAttach::getVendorRecommendAttachSid).collect(Collectors.toList());
        basVendorRecommendAttachService.deleteBasVendorRecommendAttachByIds(attachSids);
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param basVendorRecommend
     * @return
     */
    @Override
    public int check(BasVendorRecommend basVendorRecommend) {
        int row = 0;
        Long[] sids = basVendorRecommend.getVendorRecommendSidList();
        if (sids != null && sids.length > 0) {
            row = basVendorRecommendMapper.update(null, new UpdateWrapper<BasVendorRecommend>().lambda().set(BasVendorRecommend::getHandleStatus, basVendorRecommend.getHandleStatus())
                    .in(BasVendorRecommend::getVendorRecommendSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbDeal.check(id, basVendorRecommend.getHandleStatus(), msgList, TITLE, null);
            }
        }
        return row;
    }

    public void checkName(BasVendorRecommend basVendorRecommend){
        List<BasVendorRecommend> nameList_1 = new ArrayList<>();
        List<BasVendorRecommend> shortNameList_1 = new ArrayList<>();
        if (basVendorRecommend.getVendorRecommendSid() != null){
            nameList_1 = basVendorRecommendMapper.selectList(new QueryWrapper<BasVendorRecommend>().lambda()
                    .eq(BasVendorRecommend::getVendorName,basVendorRecommend.getVendorName()).ne(BasVendorRecommend::getVendorRecommendSid,basVendorRecommend.getVendorRecommendSid()));
            shortNameList_1 = basVendorRecommendMapper.selectList(new QueryWrapper<BasVendorRecommend>().lambda()
                    .eq(BasVendorRecommend::getShortName,basVendorRecommend.getShortName()).ne(BasVendorRecommend::getVendorRecommendSid,basVendorRecommend.getVendorRecommendSid()));
        }else {
            nameList_1 = basVendorRecommendMapper.selectList(new QueryWrapper<BasVendorRecommend>().lambda()
                    .eq(BasVendorRecommend::getVendorName,basVendorRecommend.getVendorName()));
            shortNameList_1 = basVendorRecommendMapper.selectList(new QueryWrapper<BasVendorRecommend>().lambda()
                    .eq(BasVendorRecommend::getShortName,basVendorRecommend.getShortName()));
        }
        if (CollectionUtil.isNotEmpty(nameList_1)){
            throw new BaseException("推荐列表中已存在相同名称的供应商");
        }
        if (CollectionUtil.isNotEmpty(shortNameList_1)){
            throw new BaseException("推荐列表中已存在相同简称的供应商");
        }
        if (ConstantsEms.CHECK_STATUS.equals(basVendorRecommend.getHandleStatus())){
            basVendorRecommend.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
    }


}
