package com.platform.ems.service.impl;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.lock.UniqueLockUtil;
import com.platform.common.redis.lock.UniqueLocker;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.ManWorkstation;
import com.platform.ems.mapper.ManWorkstationMapper;
import com.platform.ems.service.HandleStatusInfoService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 工位档案Service业务层处理
 *
 * @author Straw
 * @date 2023-03-31
 */
@Service
public class ManWorkstationServiceImpl extends ServiceImpl<ManWorkstationMapper, ManWorkstation> implements HandleStatusInfoService {

    private static final String TITLE = "工位档案";
    private static final String REDIS_UNIQUE_LOCK_HASH_KEY = "s_man_workstation:unique";
    UniqueLocker<ManWorkstation> locker = UniqueLockUtil.getLocker(REDIS_UNIQUE_LOCK_HASH_KEY,
                                                                   ManWorkstation::getWorkstationName,
                                                                   "工位名称已存在！");
    @Autowired
    private ManWorkstationMapper manWorkstationMapper;

    /**
     * 查询工位档案
     *
     * @param workstationSid 工位档案ID
     * @return 工位档案
     */

    public ManWorkstation selectManWorkstationById(Long workstationSid) {
        ManWorkstation manWorkstation = manWorkstationMapper.selectManWorkstationById(workstationSid);
        MongodbUtil.find(manWorkstation);
        return manWorkstation;
    }

    /**
     * 查询工位档案列表
     *
     * @param manWorkstation 工位档案
     * @return 工位档案
     */

    public List<ManWorkstation> selectManWorkstationList(ManWorkstation manWorkstation) {
        return manWorkstationMapper.selectManWorkstationList(manWorkstation);
    }

    /**
     * 新增工位档案
     * 需要注意编码重复校验
     *
     * @param manWorkstation 工位档案
     * @return 结果
     */

    @Transactional(rollbackFor = Exception.class)
    public int insertManWorkstation(ManWorkstation manWorkstation) {
        // 1. 重复校验
        locker.lockUnique(manWorkstation);

        try {
            // 2. 设置创建人
            this.setHandleStatusInfoWhenNew(manWorkstation);

            int row = manWorkstationMapper.insert(manWorkstation);
            if (row <= 0) {
                return row;
            }

            // 插入日志
            List<OperMsg> msgList = BeanUtils.eq(new ManWorkstation(), manWorkstation);
            MongodbDeal.insert(manWorkstation.getWorkstationSid(),
                               manWorkstation.getHandleStatus(),
                               msgList,
                               TITLE,
                               null);
            return row;
        } catch (Exception e) {
            locker.unlockUnique(manWorkstation);
            throw e;
        }
    }


    /**
     * 修改工位档案
     *
     * @param theNew 工位档案
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int updateManWorkstation(ManWorkstation theNew) {
        this.setHandleStatusInfoWhenUpdate(theNew);
        ManWorkstation theOld = manWorkstationMapper.selectManWorkstationById(theNew.getWorkstationSid());
        return locker.updateUnique(
                theNew,
                theOld,
                () -> {
                    int row = manWorkstationMapper.updateById(theNew);
                    if (row <= 0) {
                        return row;
                    }

                    // 插入日志
                    List<OperMsg> msgList = BeanUtils.eq(theOld, theNew);
                    MongodbDeal.update(theNew.getWorkstationSid(),
                                       theOld.getHandleStatus(),
                                       theNew.getHandleStatus(),
                                       msgList,
                                       TITLE,
                                       null);
                    return row;
                }
        );
    }

    /**
     * 变更工位档案
     *
     * @param theNew 工位档案
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int changeManWorkstation(ManWorkstation theNew) {
        this.setHandleStatusInfoWhenUpdate(theNew);
        ManWorkstation theOld = manWorkstationMapper.selectManWorkstationById(theNew.getWorkstationSid());
        return locker.updateUnique(
                theNew,
                theOld,
                () -> {
                    int row = manWorkstationMapper.updateAllById(theNew);
                    if (row <= 0) {
                        return row;
                    }

                    // 插入日志
                    MongodbUtil.insertUserLog(theNew.getWorkstationSid(),
                                              BusinessType.CHANGE.getValue(),
                                              theOld,
                                              theNew,
                                              TITLE);
                    return row;
                }
        );
    }

    /**
     * 批量删除工位档案
     *
     * @param workstationSids 需要删除的工位档案ID
     * @return 结果
     */

    @Transactional(rollbackFor = Exception.class)
    public int deleteManWorkstationByIds(List<Long> workstationSids) {
        List<ManWorkstation> list = manWorkstationMapper.selectList(new QueryWrapper<ManWorkstation>()
                                                                            .lambda().in(ManWorkstation::getWorkstationSid,
                                                                                         workstationSids));
        int row = manWorkstationMapper.deleteBatchIds(workstationSids);
        if (row <= 0) {
            return row;
        }

        list.forEach(o -> {
            List<OperMsg> msgList = BeanUtils.eq(o, new ManWorkstation());
            MongodbUtil.insertUserLog(o.getWorkstationSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
        });

        // 删除旧记录的锁
        locker.unlockUniqueBatch(list);

        return row;
    }

    /**
     * 启用/停用
     *
     * @param manWorkstation
     * @return
     */

    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(ManWorkstation manWorkstation) {
        int row = 0;
        Long[] sids = manWorkstation.getWorkstationSidList();
        if (sids != null && sids.length > 0) {
            row = manWorkstationMapper.update(null,
                                              new UpdateWrapper<ManWorkstation>().lambda().set(ManWorkstation::getStatus,
                                                                                               manWorkstation.getStatus())
                                                                                 .in(ManWorkstation::getWorkstationSid,
                                                                                     sids));
            if (row == 0) {
                throw new CustomException("更改状态失败,请联系管理员");
            }
            for (Long id : sids) {
                // 插入日志
                String remark = manWorkstation.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbDeal.status(id, manWorkstation.getStatus(), null, TITLE, remark);
            }
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param manWorkstation
     * @return
     */

    @Transactional(rollbackFor = Exception.class)
    public int check(ManWorkstation manWorkstation) {
        Long[] sids = manWorkstation.getWorkstationSidList();
        if (ArrayUtil.isEmpty(sids)) {
            return 0;
        }

        int row;
        LambdaUpdateWrapper<ManWorkstation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ManWorkstation::getWorkstationSid, sids);
        updateWrapper.set(ManWorkstation::getHandleStatus, manWorkstation.getHandleStatus());
        if (ConstantsEms.CHECK_STATUS.equals(manWorkstation.getHandleStatus())) {
            updateWrapper.set(ManWorkstation::getConfirmDate, new Date());
            updateWrapper.set(ManWorkstation::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }

        row = manWorkstationMapper.update(null, updateWrapper);
        if (row <= 0) {
            return row;
        }

        for (Long id : sids) {
            // 插入日志
            MongodbDeal.check(id, manWorkstation.getHandleStatus(), null, TITLE, null);
        }

        return row;
    }

}
