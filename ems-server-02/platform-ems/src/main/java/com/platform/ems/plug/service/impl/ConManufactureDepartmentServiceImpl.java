package com.platform.ems.plug.service.impl;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.core.domain.model.LoginUser;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.plug.mapper.ConManufactureDepartmentMapper;
import com.platform.ems.plug.domain.ConManufactureDepartment;
import com.platform.ems.plug.service.IConManufactureDepartmentService;

/**
 * 生产操作部门Service业务层处理
 *
 * @author zhuangyz
 * @date 2022-07-25
 */
@Service
@SuppressWarnings("all")
public class ConManufactureDepartmentServiceImpl extends ServiceImpl<ConManufactureDepartmentMapper, ConManufactureDepartment> implements IConManufactureDepartmentService {
    @Autowired
    private ConManufactureDepartmentMapper conManufactureDepartmentMapper;

    private static final String TITLE = "生产操作部门";

    private final int INSERT_OPERATION = 1;

    private final int UPDATE_OPERATION = 2;

    /**
     * 查询生产操作部门
     *
     * @param sid 生产操作部门ID
     * @return 生产操作部门
     */
    @Override
    public ConManufactureDepartment selectConManufactureDepartmentById(Long sid) {
        ConManufactureDepartment conManufactureDepartment = conManufactureDepartmentMapper.selectConManufactureDepartmentById(sid);
        MongodbUtil.find(conManufactureDepartment);
        return conManufactureDepartment;
    }

    /**
     * 查询生产操作部门列表
     *
     * @param conManufactureDepartment 生产操作部门
     * @return 生产操作部门
     */
    @Override
    public List<ConManufactureDepartment> selectConManufactureDepartmentList(ConManufactureDepartment conManufactureDepartment) {
        return conManufactureDepartmentMapper.selectConManufactureDepartmentList(conManufactureDepartment);
    }

    /**
     * 新增生产操作部门
     * 需要注意编码重复校验
     *
     * @param conManufactureDepartment 生产操作部门
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConManufactureDepartment(ConManufactureDepartment conManufactureDepartment) {
        LoginUser loginUser = ApiThreadLocalUtil.get();
        conManufactureDepartment.setCreateDate(new Date()).setCreatorAccount(loginUser.getUsername());
        uniqueCheck(conManufactureDepartment , INSERT_OPERATION);
        int row = conManufactureDepartmentMapper.insert(conManufactureDepartment);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new ConManufactureDepartment(), conManufactureDepartment);
            MongodbDeal.insert(conManufactureDepartment.getSid(), conManufactureDepartment.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     *
     * @param conManufactureDepartment
     * @param operationType 因为变更操作不能修改编码 1: 新增操作的唯一校验 ， 2：变更操作的唯一校验；
     */
    private void uniqueCheck (ConManufactureDepartment conManufactureDepartment , int operationType) {

        if (operationType == 1) {
            ConManufactureDepartment conManufactureDepartment1 = conManufactureDepartmentMapper
                    .selectOne(Wrappers.lambdaQuery(ConManufactureDepartment.class)
                            .eq(ConManufactureDepartment::getCode, conManufactureDepartment.getCode()));
            if (ObjectUtil.isNotEmpty(conManufactureDepartment1)) {
                throw new CustomException("编码已存在，请核实");
            }
        }

        ConManufactureDepartment conManufactureDepartment2 = conManufactureDepartmentMapper
                                                               .selectOne(Wrappers.lambdaQuery(ConManufactureDepartment.class)
                                                                 .eq(ConManufactureDepartment::getName, conManufactureDepartment.getName()));
        if (ObjectUtil.isNotEmpty(conManufactureDepartment2)) {
            throw new CustomException("名称已存在，请核实");
        }

    }

    /**
     * 修改生产操作部门
     *
     * @param conManufactureDepartment 生产操作部门
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConManufactureDepartment(ConManufactureDepartment conManufactureDepartment) {
        ConManufactureDepartment original = conManufactureDepartmentMapper.selectConManufactureDepartmentById(conManufactureDepartment.getSid());
        int row = conManufactureDepartmentMapper.updateById(conManufactureDepartment);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, conManufactureDepartment);
            MongodbDeal.update(conManufactureDepartment.getSid(), original.getHandleStatus(), conManufactureDepartment.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更生产操作部门
     *
     * @param conManufactureDepartment 生产操作部门
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConManufactureDepartment(ConManufactureDepartment conManufactureDepartment) {
        LoginUser loginUser = ApiThreadLocalUtil.get();
        ConManufactureDepartment response = conManufactureDepartmentMapper.selectConManufactureDepartmentById(conManufactureDepartment.getSid());
        if (!StrUtil.equals(conManufactureDepartment.getName() , response.getName())) {
            uniqueCheck(conManufactureDepartment , UPDATE_OPERATION);
        }
        conManufactureDepartment.setUpdateDate(new Date()).setUpdaterAccount(loginUser.getUsername());
        if (StrUtil.isNotEmpty(conManufactureDepartment.getHandleStatus()) &&
                StrUtil.equals(ConstantsEms.CHECK_STATUS , conManufactureDepartment.getHandleStatus())) {
            conManufactureDepartment.setConfirmDate(new Date()).setConfirmerAccount(loginUser.getUsername());
        }
        int row = conManufactureDepartmentMapper.updateAllById(conManufactureDepartment);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conManufactureDepartment.getSid(), BusinessType.CHANGE.getValue(), response, conManufactureDepartment, TITLE);
        }
        return row;
    }

    /**
     * 批量删除生产操作部门
     *
     * @param sids 需要删除的生产操作部门ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConManufactureDepartmentByIds(List<Long> sids) {
        List<ConManufactureDepartment> list = conManufactureDepartmentMapper.selectList(new QueryWrapper<ConManufactureDepartment>()
                .lambda().in(ConManufactureDepartment::getSid, sids));
        int row = conManufactureDepartmentMapper.deleteBatchIds(sids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new ConManufactureDepartment());
                MongodbUtil.insertUserLog(o.getSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 启用/停用
     *
     * @param conManufactureDepartment
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(ConManufactureDepartment conManufactureDepartment) {
        int row = 0;
        Long[] sids = conManufactureDepartment.getSidList();
        if (sids != null && sids.length > 0) {
            row = conManufactureDepartmentMapper.update(null, new UpdateWrapper<ConManufactureDepartment>().lambda().set(ConManufactureDepartment::getStatus, conManufactureDepartment.getStatus())
                    .in(ConManufactureDepartment::getSid, sids));
            for (Long id : sids) {
                conManufactureDepartment.setSid(id);
                row = conManufactureDepartmentMapper.updateById(conManufactureDepartment);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                MongodbDeal.status(conManufactureDepartment.getSid(), conManufactureDepartment.getStatus(), null, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param conManufactureDepartment
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ConManufactureDepartment conManufactureDepartment) {
        int row = 0;
        Long[] sids = conManufactureDepartment.getSidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<ConManufactureDepartment> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(ConManufactureDepartment::getSid, sids);
            updateWrapper.set(ConManufactureDepartment::getHandleStatus, conManufactureDepartment.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(conManufactureDepartment.getHandleStatus())) {
                updateWrapper.set(ConManufactureDepartment::getConfirmDate, new Date());
                updateWrapper.set(ConManufactureDepartment::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            row = conManufactureDepartmentMapper.update(null, updateWrapper);
            if (row > 0) {
                for (Long id : sids) {
                    //插入日志
                    MongodbDeal.check(id, conManufactureDepartment.getHandleStatus(), null, TITLE, null);
                }
            }
        }
        return row;
    }

}
