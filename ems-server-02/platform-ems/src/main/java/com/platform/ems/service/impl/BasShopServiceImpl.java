package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.BasShop;
import com.platform.ems.domain.BasShopAddr;
import com.platform.ems.domain.BasShopAttach;
import com.platform.ems.mapper.BasShopAddrMapper;
import com.platform.ems.mapper.BasShopAttachMapper;
import com.platform.ems.mapper.BasShopMapper;
import com.platform.ems.service.IBasShopService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 店铺档案Service业务层处理
 *
 * @author c
 * @date 2022-03-31
 */
@Service
@SuppressWarnings("all")
public class BasShopServiceImpl extends ServiceImpl<BasShopMapper, BasShop> implements IBasShopService {
    @Autowired
    private BasShopMapper basShopMapper;
    @Autowired
    private BasShopAddrMapper basShopAddrMapper;
    @Autowired
    private BasShopAttachMapper basShopAttachMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "店铺档案";

    /**
     * 查询店铺档案
     *
     * @param shopSid 店铺档案ID
     * @return 店铺档案
     */
    @Override
    public BasShop selectBasShopById(Long shopSid) {
        BasShop basShop = basShopMapper.selectBasShopById(shopSid);
        if (basShop == null) {
            return null;
        }
        //店铺-联系方式信息
        List<BasShopAddr> addrList = basShopAddrMapper.selectBasShopAddrList(new BasShopAddr().setShopSid(shopSid));
        //店铺-附件
        List<BasShopAttach> attachList = basShopAttachMapper.selectBasShopAttachList(new BasShopAttach().setShopSid(shopSid));
        basShop.setAddrList(addrList);
        basShop.setAttachList(attachList);
        //操作日志
        MongodbUtil.find(basShop);
        return basShop;
    }

    /**
     * 查询店铺档案列表
     *
     * @param basShop 店铺档案
     * @return 店铺档案
     */
    @Override
    public List<BasShop> selectBasShopList(BasShop basShop) {
        return basShopMapper.selectBasShopList(basShop);
    }

    /**
     * 新增店铺档案
     * 需要注意编码重复校验
     *
     * @param basShop 店铺档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasShop(BasShop basShop) {
        //校验店铺名称是否重复
        List<BasShop> list = basShopMapper.selectList(new QueryWrapper<BasShop>().lambda()
                .eq(BasShop::getShopName,basShop.getShopName())
                .ne(BasShop::getShopSid,basShop.getShopSid()));
        if (CollectionUtil.isNotEmpty(list)){
            throw new BaseException("店铺名称已存在");
        }
        //设置确认信息
        setConfirmInfo(basShop);
        int row = basShopMapper.insert(basShop);
        if (row > 0) {
            //店铺-联系方式信息
            List<BasShopAddr> addrList = basShop.getAddrList();
            if (CollUtil.isNotEmpty(addrList)) {
                addBasShopAddr(basShop, addrList);
            }
            //店铺-附件
            List<BasShopAttach> attachList = basShop.getAttachList();
            if (CollUtil.isNotEmpty(attachList)) {
                addBasShopAttach(basShop, attachList);
            }

            //待办通知
            BasShop shop = new BasShop();
            shop = basShopMapper.selectBasShopById(basShop.getShopSid());
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(basShop.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsEms.DP)
                        .setDocumentSid(basShop.getShopSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("店铺" + shop.getShopCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(shop.getShopCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(basShop.getShopSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    private List<BasShop> getBasShops(BasShop basShop) {
        return basShopMapper.selectBasShopList(new BasShop().setShopName(basShop.getShopName()));
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(BasShop o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 店铺-联系方式信息
     */
    private void addBasShopAddr(BasShop basShop, List<BasShopAddr> addrList) {
        deleteAddr(basShop);
        addrList.forEach(o -> {
            o.setShopSid(basShop.getShopSid());
        });
        basShopAddrMapper.inserts(addrList);
    }

    /**
     * 店铺-附件
     */
    private void addBasShopAttach(BasShop basShop, List<BasShopAttach> attachList) {
        deleteAttach(basShop);
        attachList.forEach(o -> {
            o.setShopSid(basShop.getShopSid());
        });
        basShopAttachMapper.inserts(attachList);
    }

    /**
     * 删除联系方式信息
     */
    private void deleteAddr(BasShop basShop) {
        basShopAddrMapper.delete(
                new UpdateWrapper<BasShopAddr>()
                        .lambda()
                        .eq(BasShopAddr::getShopSid, basShop.getShopSid())
        );
    }

    /**
     * 删除附件
     */
    private void deleteAttach(BasShop basShop) {
        basShopAttachMapper.delete(
                new UpdateWrapper<BasShopAttach>()
                        .lambda()
                        .eq(BasShopAttach::getShopSid, basShop.getShopSid())
        );
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(BasShop basShop) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, basShop.getShopSid()));
        if (CollUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, basShop.getShopSid()));
        }
    }

    /**
     * 修改店铺档案
     *
     * @param basShop 店铺档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasShop(BasShop basShop) {
        //校验店铺名称是否重复
        checkNameUnique(basShop);
        //设置确认信息
        setConfirmInfo(basShop);
        basShop.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        BasShop response = basShopMapper.selectBasShopById(basShop.getShopSid());
        int row = basShopMapper.updateAllById(basShop);
        if (row > 0) {
            //店铺-联系方式信息
            List<BasShopAddr> addrList = basShop.getAddrList();
            if (CollUtil.isNotEmpty(addrList)) {
                addBasShopAddr(basShop, addrList);
            } else {
                deleteAddr(basShop);
            }
            //店铺-附件
            List<BasShopAttach> attachList = basShop.getAttachList();
            if (CollUtil.isNotEmpty(attachList)) {
                addBasShopAttach(basShop, attachList);
            } else {
                deleteAttach(basShop);
            }
            if (!ConstantsEms.SAVA_STATUS.equals(basShop.getHandleStatus())) {
                //校验是否存在待办
                checkTodoExist(basShop);
            }
            //插入日志
            MongodbUtil.insertUserLog(basShop.getShopSid(), BusinessType.UPDATE.getValue(), response, basShop, TITLE);
        }
        return row;
    }

    private void checkNameUnique(BasShop basShop) {
        List<BasShop> list = basShopMapper.selectList(new QueryWrapper<BasShop>().lambda()
                .eq(BasShop::getShopName,basShop.getShopName())
                .ne(BasShop::getShopSid,basShop.getShopSid()));
        if (CollectionUtil.isNotEmpty(list)){
            throw new BaseException("店铺名称已存在");
        }
    }

    /**
     * 变更店铺档案
     *
     * @param basShop 店铺档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasShop(BasShop basShop) {
        //校验店铺名称是否重复
        checkNameUnique(basShop);
        //设置确认信息
        setConfirmInfo(basShop);
        basShop.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        BasShop response = basShopMapper.selectBasShopById(basShop.getShopSid());
        int row = basShopMapper.updateAllById(basShop);
        if (row > 0) {
            //店铺-联系方式信息
            List<BasShopAddr> addrList = basShop.getAddrList();
            if (CollUtil.isNotEmpty(addrList)) {
                addBasShopAddr(basShop, addrList);
            } else {
                deleteAddr(basShop);
            }
            //店铺-附件
            List<BasShopAttach> attachList = basShop.getAttachList();
            if (CollUtil.isNotEmpty(attachList)) {
                addBasShopAttach(basShop, attachList);
            } else {
                deleteAttach(basShop);
            }
            //插入日志
            MongodbUtil.insertUserLog(basShop.getShopSid(), BusinessType.CHANGE.getValue(), response, basShop, TITLE);
        }
        return row;
    }

    /**
     * 批量删除店铺档案
     *
     * @param shopSids 需要删除的店铺档案ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasShopByIds(List<Long> shopSids) {
        Integer count = basShopMapper.selectCount(new QueryWrapper<BasShop>().lambda()
                .eq(BasShop::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(BasShop::getShopSid, shopSids));
        if (count != shopSids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        BasShop basShop = new BasShop();
        shopSids.forEach(shopSid -> {
            basShop.setShopSid(shopSid);
            //校验是否存在待办
            checkTodoExist(basShop);
        });
        //删除店铺-联系方式信息
        basShopAddrMapper.delete(new UpdateWrapper<BasShopAddr>().lambda()
                .in(BasShopAddr::getShopSid, shopSids));
        //删除店铺-附件
        basShopAttachMapper.delete(new UpdateWrapper<BasShopAttach>().lambda()
                .in(BasShopAttach::getShopSid, shopSids));
        return basShopMapper.deleteBatchIds(shopSids);
    }

    /**
     * 启用/停用
     *
     * @param basShop
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(BasShop basShop) {
        Long[] sids = basShop.getShopSidList();
        basShopMapper.update(null, new UpdateWrapper<BasShop>().lambda()
                .set(BasShop::getStatus, basShop.getStatus())
                .in(BasShop::getShopSid, sids));
        for (Long id : sids) {
            //插入日志
            String remark = StrUtil.isEmpty(basShop.getDisableRemark()) ? null : basShop.getDisableRemark();
            MongodbDeal.status(basShop.getShopSid(), basShop.getStatus(), null, TITLE, remark);
        }
        return sids.length;
    }


    /**
     * 更改确认状态
     *
     * @param basShop
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(BasShop basShop) {
        Long[] sids = basShop.getShopSidList();
        basShopMapper.update(null, new UpdateWrapper<BasShop>().lambda()
                .set(BasShop::getHandleStatus, ConstantsEms.CHECK_STATUS)
                .set(BasShop::getConfirmDate, new Date())
                .set(BasShop::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername())
                .in(BasShop::getShopSid, sids));
        for (Long id : sids) {
            //校验是否存在待办
            basShop.setShopSid(id);
            checkTodoExist(basShop);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
        }
        return sids.length;
    }

}
