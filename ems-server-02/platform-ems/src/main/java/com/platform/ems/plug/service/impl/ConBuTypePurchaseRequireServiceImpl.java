package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.SysFormProcess;
import com.platform.ems.plug.domain.ConBuTypePurchaseRequire;
import com.platform.ems.plug.mapper.ConBuTypePurchaseRequireMapper;
import com.platform.ems.plug.service.IConBuTypePurchaseRequireService;
import com.platform.ems.service.ISysFormProcessService;
import com.platform.ems.service.ISystemUserService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 业务类型_申购单Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConBuTypePurchaseRequireServiceImpl extends ServiceImpl<ConBuTypePurchaseRequireMapper, ConBuTypePurchaseRequire> implements IConBuTypePurchaseRequireService {
    @Autowired
    private ConBuTypePurchaseRequireMapper conBuTypePurchaseRequireMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ISysFormProcessService formProcessService;
    @Autowired
    private ISystemUserService userService;

    private static final String TITLE = "业务类型_申购单";

    /**
     * 查询业务类型_申购单
     *
     * @param sid 业务类型_申购单ID
     * @return 业务类型_申购单
     */
    @Override
    public ConBuTypePurchaseRequire selectConBuTypePurchaseRequireById(Long sid) {
        ConBuTypePurchaseRequire conBuTypePurchaseRequire = conBuTypePurchaseRequireMapper.selectConBuTypePurchaseRequireById(
                sid);
        MongodbUtil.find(conBuTypePurchaseRequire);
        SysFormProcess formProcess = new SysFormProcess();
        formProcess.setFormId(sid);
        List<SysFormProcess> list = formProcessService.selectSysFormProcessList(formProcess);
        if (list != null && list.size() > 0) {
            formProcess = new SysFormProcess();
            formProcess = list.get(0);
            conBuTypePurchaseRequire.setApprovalNode(formProcess.getApprovalNode());
            conBuTypePurchaseRequire.setApprovalUserName(formProcess.getApprovalUserName());
            conBuTypePurchaseRequire.setSubmitDate(formProcess.getCreateDate());
            conBuTypePurchaseRequire.setSubmitUserName(userService.selectSysUserById(Long.valueOf(formProcess.getCreateById())).getNickName());
        }
        return conBuTypePurchaseRequire;
    }

    /**
     * 查询业务类型_申购单列表
     *
     * @param conBuTypePurchaseRequire 业务类型_申购单
     * @return 业务类型_申购单
     */
    @Override
    public List<ConBuTypePurchaseRequire> selectConBuTypePurchaseRequireList(ConBuTypePurchaseRequire conBuTypePurchaseRequire) {
        List<ConBuTypePurchaseRequire> purchaseRequireList = conBuTypePurchaseRequireMapper.selectConBuTypePurchaseRequireList(
                conBuTypePurchaseRequire);
        SysFormProcess formProcess = new SysFormProcess();
        for (ConBuTypePurchaseRequire purchaseRequire : purchaseRequireList) {
            formProcess.setFormId(purchaseRequire.getSid());
            List<SysFormProcess> list = formProcessService.selectSysFormProcessList(formProcess);
            if (list != null && list.size() > 0) {
                formProcess = new SysFormProcess();
                formProcess = list.get(0);
                purchaseRequire.setApprovalNode(formProcess.getApprovalNode());
                purchaseRequire.setApprovalUserName(formProcess.getApprovalUserName());
                purchaseRequire.setSubmitDate(formProcess.getCreateDate());
                purchaseRequire.setSubmitUserName(userService.selectSysUserById(Long.valueOf(formProcess.getCreateById())).getNickName());
            }
        }
        return purchaseRequireList;
    }

    /**
     * 新增业务类型_申购单
     * 需要注意编码重复校验
     *
     * @param conBuTypePurchaseRequire 业务类型_申购单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConBuTypePurchaseRequire(ConBuTypePurchaseRequire conBuTypePurchaseRequire) {
        List<ConBuTypePurchaseRequire> codeList = conBuTypePurchaseRequireMapper
                .selectList(new QueryWrapper<ConBuTypePurchaseRequire>()
                                    .lambda()
                                    .eq(ConBuTypePurchaseRequire::getCode,
                                        conBuTypePurchaseRequire.getCode()));

        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }

        List<ConBuTypePurchaseRequire> nameList = conBuTypePurchaseRequireMapper.selectList(
                new QueryWrapper<ConBuTypePurchaseRequire>()
                        .lambda()
                        .eq(ConBuTypePurchaseRequire::getName,
                            conBuTypePurchaseRequire.getName()));

        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }

        int row = conBuTypePurchaseRequireMapper.insert(conBuTypePurchaseRequire);
        if (row > 0) {
            // 插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conBuTypePurchaseRequire.getSid(),
                                      BusinessType.INSERT.getValue(),
                                      msgList,
                                      TITLE);
        }
        return row;
    }

    /**
     * 修改业务类型_申购单
     *
     * @param conBuTypePurchaseRequire 业务类型_申购单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConBuTypePurchaseRequire(ConBuTypePurchaseRequire conBuTypePurchaseRequire) {
        ConBuTypePurchaseRequire response = conBuTypePurchaseRequireMapper.selectConBuTypePurchaseRequireById(
                conBuTypePurchaseRequire.getSid());
        int row = conBuTypePurchaseRequireMapper.updateById(conBuTypePurchaseRequire);
        if (row > 0) {
            // 插入日志
            MongodbUtil.insertUserLog(conBuTypePurchaseRequire.getSid(),
                                      BusinessType.UPDATE.getValue(),
                                      response,
                                      conBuTypePurchaseRequire,
                                      TITLE);
        }
        return row;
    }

    /**
     * 变更业务类型_申购单
     *
     * @param conBuTypePurchaseRequire 业务类型_申购单
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConBuTypePurchaseRequire(ConBuTypePurchaseRequire conBuTypePurchaseRequire) {
        List<ConBuTypePurchaseRequire> nameList = conBuTypePurchaseRequireMapper.selectList(new QueryWrapper<ConBuTypePurchaseRequire>().lambda()
                                                                                                                                        .eq(ConBuTypePurchaseRequire::getName,
                                                                                                                                            conBuTypePurchaseRequire.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conBuTypePurchaseRequire.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conBuTypePurchaseRequire.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConBuTypePurchaseRequire response = conBuTypePurchaseRequireMapper.selectConBuTypePurchaseRequireById(
                conBuTypePurchaseRequire.getSid());
        int row = conBuTypePurchaseRequireMapper.updateAllById(conBuTypePurchaseRequire);
        if (row > 0) {
            // 插入日志
            MongodbUtil.insertUserLog(conBuTypePurchaseRequire.getSid(),
                                      BusinessType.CHANGE.getValue(),
                                      response,
                                      conBuTypePurchaseRequire,
                                      TITLE);
        }
        return row;
    }

    /**
     * 批量删除业务类型_申购单
     *
     * @param sids 需要删除的业务类型_申购单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConBuTypePurchaseRequireByIds(List<Long> sids) {
        return conBuTypePurchaseRequireMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conBuTypePurchaseRequire
     * @return
     */
    @Override
    public int changeStatus(ConBuTypePurchaseRequire conBuTypePurchaseRequire) {
        int row = 0;
        Long[] sids = conBuTypePurchaseRequire.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conBuTypePurchaseRequire.setSid(id);
                row = conBuTypePurchaseRequireMapper.updateById(conBuTypePurchaseRequire);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                // 插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conBuTypePurchaseRequire.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conBuTypePurchaseRequire.getSid(),
                                          BusinessType.CHECK.getValue(),
                                          msgList,
                                          TITLE,
                                          remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conBuTypePurchaseRequire
     * @return
     */
    @Override
    public int check(ConBuTypePurchaseRequire conBuTypePurchaseRequire) {
        int row = 0;
        Long[] sids = conBuTypePurchaseRequire.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conBuTypePurchaseRequire.setSid(id);
                row = conBuTypePurchaseRequireMapper.updateById(conBuTypePurchaseRequire);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                // 插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conBuTypePurchaseRequire.getSid(),
                                          BusinessType.CHECK.getValue(),
                                          msgList,
                                          TITLE);
            }
        }
        return row;
    }


}
