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
import com.platform.ems.constant.ConstantsFinance;
import com.platform.ems.plug.domain.ConAccountMethodGroup;
import com.platform.ems.plug.domain.ConAccountMethodGroupMethod;
import com.platform.ems.plug.mapper.ConAccountMethodGroupMapper;
import com.platform.ems.plug.mapper.ConAccountMethodGroupMethodMapper;
import com.platform.ems.plug.service.IConAccountMethodGroupService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 收付款方式组合Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@Service
@SuppressWarnings("all")
public class ConAccountMethodGroupServiceImpl extends ServiceImpl<ConAccountMethodGroupMapper, ConAccountMethodGroup> implements IConAccountMethodGroupService {
    @Autowired
    private ConAccountMethodGroupMapper conAccountMethodGroupMapper;
    @Autowired
    private ConAccountMethodGroupMethodMapper conAccountMethodGroupMethodMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "收付款方式组合";

    /**
     * 查询收付款方式组合
     *
     * @param sid 收付款方式组合ID
     * @return 收付款方式组合
     */
    @Override
    public ConAccountMethodGroup selectConAccountMethodGroupById(Long sid) {
        ConAccountMethodGroup conAccountMethodGroup = conAccountMethodGroupMapper.selectConAccountMethodGroupById(sid);
        if (conAccountMethodGroup != null) {
            List<ConAccountMethodGroupMethod> methods = conAccountMethodGroupMethodMapper.selectConAccountMethodGroupMethodList(
                    new ConAccountMethodGroupMethod().setAccountMethodGroupSid(sid));
            conAccountMethodGroup.setMethodList(methods);
        }
        MongodbUtil.find(conAccountMethodGroup);
        return conAccountMethodGroup;
    }

    /**
     * 查询收付款方式组合列表
     *
     * @param conAccountMethodGroup 收付款方式组合
     * @return 收付款方式组合
     */
    @Override
    public List<ConAccountMethodGroup> selectConAccountMethodGroupList(ConAccountMethodGroup conAccountMethodGroup) {
        return conAccountMethodGroupMapper.selectConAccountMethodGroupList(conAccountMethodGroup);
    }

    /**
     * 新增收付款方式组合
     * 需要注意编码重复校验
     *
     * @param conAccountMethodGroup 收付款方式组合
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConAccountMethodGroup(ConAccountMethodGroup conAccountMethodGroup) {
        List<ConAccountMethodGroup> codeList = conAccountMethodGroupMapper.selectList(new QueryWrapper<ConAccountMethodGroup>().lambda()
                .eq(ConAccountMethodGroup::getCode, conAccountMethodGroup.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConAccountMethodGroup> nameList = conAccountMethodGroupMapper.selectList(new QueryWrapper<ConAccountMethodGroup>().lambda()
                .eq(ConAccountMethodGroup::getName, conAccountMethodGroup.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        checkUnique(conAccountMethodGroup);
        setConfirmInfo(conAccountMethodGroup);
        valPass(conAccountMethodGroup);
        int row = conAccountMethodGroupMapper.insert(conAccountMethodGroup);
        if (row > 0) {
            //插入日志
            addMethod(conAccountMethodGroup.getSid(), conAccountMethodGroup.getMethodList());
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conAccountMethodGroup.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(ConAccountMethodGroup o) {
        if (o == null) {
            return;
        }
        if (ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    private void checkUnique(ConAccountMethodGroup conAccountMethodGroup){
        if (conAccountMethodGroup.getAdvanceRate() != null && conAccountMethodGroup.getMiddleRate() != null
                && conAccountMethodGroup.getRemainRate() != null){
            QueryWrapper<ConAccountMethodGroup> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(ConAccountMethodGroup::getAdvanceRate,conAccountMethodGroup.getAdvanceRate())
                    .eq(ConAccountMethodGroup::getMiddleRate,conAccountMethodGroup.getMiddleRate())
                    .eq(ConAccountMethodGroup::getRemainRate,conAccountMethodGroup.getRemainRate());
            if (conAccountMethodGroup.getShoufukuanType() != null){
                queryWrapper.lambda().eq(ConAccountMethodGroup::getShoufukuanType,conAccountMethodGroup.getShoufukuanType());
            }
            if (conAccountMethodGroup.getSid() != null){
                queryWrapper.lambda().ne(ConAccountMethodGroup::getSid,conAccountMethodGroup.getSid());
            }
            List<ConAccountMethodGroup> list = conAccountMethodGroupMapper.selectList(queryWrapper);
            if (CollectionUtil.isNotEmpty(list)){
                if (ConstantsEms.SHOUFUKUAN_TYPE_SK.equals(conAccountMethodGroup.getShoufukuanType())){
                    throw new BaseException("此比例组合的收款方式组合已存在，不允许重复创建，请核实！");
                }else if (ConstantsEms.SHOUFUKUAN_TYPE_FK.equals(conAccountMethodGroup.getShoufukuanType())){
                    throw new BaseException("此比例组合的付款方式组合已存在，不允许重复创建，请核实！");
                }else {
                    throw new BaseException("此比例组合的收付款方式组合已存在，不允许重复创建，请核实！");
                }
            }
        }
    }

    private static void valPass(ConAccountMethodGroup conAccountMethodGroup) {
        if (!validSum(conAccountMethodGroup.getAdvanceRate(), conAccountMethodGroup.getMiddleRate(), conAccountMethodGroup.getRemainRate())) {
            throw new BaseException("付款比例总和不等于100%,请修改后再试");
        }
        //校验支付方式比例
        List<ConAccountMethodGroupMethod> methodList = conAccountMethodGroup.getMethodList();
        Map<String, Double> collets = methodList.stream().collect(Collectors.groupingBy(ConAccountMethodGroupMethod::getAccountCategory, Collectors.summingDouble(ConAccountMethodGroupMethod::getRate)));
        collets.forEach((k, v) -> {
            BigDecimal sum = new BigDecimal(v);
            if (sum.compareTo(new BigDecimal(1)) != 0) {
                throw new BaseException("支付方式总和不等于100%,请修改后再试");
            }
        });
    }


    private static boolean validSum(String... valuesBigDecimals) {
        BigDecimal sum = new BigDecimal(0);
        for (String value : valuesBigDecimals) {
            sum = sum.add(new BigDecimal(value));
        }
        if (sum.compareTo(new BigDecimal(1)) == 0) {
            return true;
        }
        return false;
    }

    private void addMethod(Long sid, List<ConAccountMethodGroupMethod> methodList) {
        if (methodList == null || methodList.size() <= 0) {
            return;
        }
        methodList.forEach(m -> {
            m.setAccountMethodGroupSid(sid);
        });
        conAccountMethodGroupMethodMapper.inserts(methodList);
    }

    private void deleteMethod(Long sid) {
        conAccountMethodGroupMethodMapper.delete(new QueryWrapper<ConAccountMethodGroupMethod>().lambda()
                .eq(ConAccountMethodGroupMethod::getAccountMethodGroupSid, sid));
    }

    private void updateMethod(Long sid, List<ConAccountMethodGroupMethod> methodList) {
        deleteMethod(sid);
        addMethod(sid, methodList);
    }

    /**
     * 修改收付款方式组合
     *
     * @param conAccountMethodGroup 收付款方式组合
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConAccountMethodGroup(ConAccountMethodGroup conAccountMethodGroup) {
        checkUnique(conAccountMethodGroup);
        setConfirmInfo(conAccountMethodGroup);
        valPass(conAccountMethodGroup);
        checkNameUnique(conAccountMethodGroup);
        ConAccountMethodGroup response = conAccountMethodGroupMapper.selectConAccountMethodGroupById(conAccountMethodGroup.getSid());
        conAccountMethodGroup.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = conAccountMethodGroupMapper.updateAllById(conAccountMethodGroup);
        if (row > 0) {
            updateMethod(conAccountMethodGroup.getSid(), conAccountMethodGroup.getMethodList());
            //插入日志
            MongodbUtil.insertUserLog(conAccountMethodGroup.getSid(), BusinessType.UPDATE.getValue(), response, conAccountMethodGroup, TITLE);
        }
        return row;
    }

    /**
     * 校验名称是否重复
     */
    private void checkNameUnique(ConAccountMethodGroup conAccountMethodGroup) {
        List<ConAccountMethodGroup> nameList = conAccountMethodGroupMapper.selectList(new QueryWrapper<ConAccountMethodGroup>().lambda()
                .eq(ConAccountMethodGroup::getName, conAccountMethodGroup.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o -> {
                if (!o.getSid().equals(conAccountMethodGroup.getSid())) {
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
    }

    /**
     * 变更收付款方式组合
     *
     * @param conAccountMethodGroup 收付款方式组合
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConAccountMethodGroup(ConAccountMethodGroup conAccountMethodGroup) {
        checkUnique(conAccountMethodGroup);
        setConfirmInfo(conAccountMethodGroup);
        valPass(conAccountMethodGroup);
        checkNameUnique(conAccountMethodGroup);
        conAccountMethodGroup.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConAccountMethodGroup response = conAccountMethodGroupMapper.selectConAccountMethodGroupById(conAccountMethodGroup.getSid());
        conAccountMethodGroup.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date());
        int row = conAccountMethodGroupMapper.updateAllById(conAccountMethodGroup);
        if (row > 0) {
            updateMethod(conAccountMethodGroup.getSid(), conAccountMethodGroup.getMethodList());
            //插入日志
            MongodbUtil.insertUserLog(conAccountMethodGroup.getSid(), BusinessType.CHANGE.getValue(), response, conAccountMethodGroup, TITLE);
        }
        return row;
    }

    /**
     * 批量删除收付款方式组合
     *
     * @param sids 需要删除的收付款方式组合ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConAccountMethodGroupByIds(List<Long> sids) {
        Integer count = conAccountMethodGroupMapper.selectCount(new QueryWrapper<ConAccountMethodGroup>().lambda()
                .eq(ConAccountMethodGroup::getHandleStatus, ConstantsEms.SAVA_STATUS)
                .in(ConAccountMethodGroup::getSid, sids));
        if (count != sids.size()) {
            throw new BaseException(ConstantsEms.DELETE_PROMPT_STATEMENT);
        }
        int row = conAccountMethodGroupMapper.deleteBatchIds(sids);
        if (row > 0) {
            conAccountMethodGroupMethodMapper.delete(new QueryWrapper<ConAccountMethodGroupMethod>().lambda()
                    .in(ConAccountMethodGroupMethod::getAccountMethodGroupSid, sids));
        }
        return row;
    }

    /**
     * 启用/停用
     *
     * @param conAccountMethodGroup
     * @return
     */
    @Override
    public int changeStatus(ConAccountMethodGroup conAccountMethodGroup) {
        int row = 0;
        Long[] sids = conAccountMethodGroup.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conAccountMethodGroup.setSid(id);
                row = conAccountMethodGroupMapper.updateById(conAccountMethodGroup);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conAccountMethodGroup.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conAccountMethodGroup.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conAccountMethodGroup
     * @return
     */
    @Override
    public int check(ConAccountMethodGroup conAccountMethodGroup) {
        int row = 0;
        Long[] sids = conAccountMethodGroup.getSidList();
        if (sids != null && sids.length > 0) {
            Integer count = conAccountMethodGroupMapper.selectCount(new QueryWrapper<ConAccountMethodGroup>().lambda()
                    .eq(ConAccountMethodGroup::getHandleStatus, ConstantsEms.SAVA_STATUS)
                    .in(ConAccountMethodGroup::getSid, sids));
            if (count != sids.length) {
                throw new BaseException(ConstantsEms.CHECK_PROMPT_STATEMENT);
            }
            for (Long id : sids) {
                conAccountMethodGroup.setSid(id);
                row = conAccountMethodGroupMapper.updateById(conAccountMethodGroup);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conAccountMethodGroup.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }


}
