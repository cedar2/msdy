package com.platform.ems.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.ManProduceConcernTaskGroupRequest;
import com.platform.ems.domain.dto.response.ManProduceConcernTaskGroupResponse;
import com.platform.ems.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.service.IManProduceConcernTaskGroupService;

/**
 * 生产关注事项组Service业务层处理
 *
 * @author zhuangyz
 * @date 2022-08-02
 */
@Service
@SuppressWarnings("all")
public class ManProduceConcernTaskGroupServiceImpl extends ServiceImpl<ManProduceConcernTaskGroupMapper, ManProduceConcernTaskGroup> implements IManProduceConcernTaskGroupService {

    @Autowired
    private ManProduceConcernTaskGroupMapper manProduceConcernTaskGroupMapper;

    @Autowired
    private ManProduceConcernTaskMapper manProduceConcernTaskMapper;

    @Autowired
    private ManProduceConcernTaskGroupItemMapper manProduceConcernTaskGroupItemMapper;

    @Autowired
    private BasPlantMapper basPlantMapper;

    @Autowired
    private BasStaffMapper basStaffMapper;

    private static final String TITLE = "生产关注事项组";

    /**
     * 查询生产关注事项组
     *
     * @param concernTaskGroupSid 生产关注事项组ID
     * @return 生产关注事项组
     */
    @Override
    public ManProduceConcernTaskGroup selectManProduceConcernTaskGroupById(Long concernTaskGroupSid) {
        ManProduceConcernTaskGroup manProduceConcernTaskGroup = manProduceConcernTaskGroupMapper.selectManProduceConcernTaskGroupById(concernTaskGroupSid);
        ManProduceConcernTaskGroupItem item = new ManProduceConcernTaskGroupItem();
        item.setConcernTaskGroupSid(concernTaskGroupSid);
        List<ManProduceConcernTaskGroupItem> itemList = manProduceConcernTaskGroupItemMapper.selectManProduceConcernTaskGroupItemList(item);
        itemList.forEach((val) -> {
            Long concernTaskSid = val.getConcernTaskSid();
            ManProduceConcernTask manProduceConcernTask = manProduceConcernTaskMapper.selectManProduceConcernTaskById(concernTaskSid);
            val.setConcernTaskCode(manProduceConcernTask.getConcernTaskCode())
               .setConcernTaskName(manProduceConcernTask.getConcernTaskName())
               .setStatus(manProduceConcernTask.getStatus())
               .setProduceStageName(manProduceConcernTask.getProduceStageName());
        });

        manProduceConcernTaskGroup.setItemList(itemList);
        MongodbUtil.find(manProduceConcernTaskGroup);
        return manProduceConcernTaskGroup;
    }


    @Override
    public List<ManProduceConcernTaskGroupItem>  monthConcernTaskGroupById(Long concernTaskGroupSid) {
        ManProduceConcernTaskGroupItem item = new ManProduceConcernTaskGroupItem();
        item.setConcernTaskGroupSid(concernTaskGroupSid);
        List<ManProduceConcernTaskGroupItem> itemList = manProduceConcernTaskGroupItemMapper.selectManProduceConcernTaskGroupItemList(item);
        itemList = itemList.stream().sorted(Comparator.comparing(ManProduceConcernTaskGroupItem::getSort, Comparator.nullsLast(Long::compareTo)))
                .collect(Collectors.toList());
        return itemList;
    }
    /**
     * 查询生产关注事项组列表
     *
     * @param manProduceConcernTaskGroup 生产关注事项组
     * @return 生产关注事项组
     */
    @Override
    public List<ManProduceConcernTaskGroup> selectManProduceConcernTaskGroupList(ManProduceConcernTaskGroup manProduceConcernTaskGroup) {
        List<ManProduceConcernTaskGroup> list = manProduceConcernTaskGroupMapper.selectManProduceConcernTaskGroupList(manProduceConcernTaskGroup);
        return list;
    }

    @Override
    public List<ManProduceConcernTaskGroupResponse> getReport(ManProduceConcernTaskGroupRequest manProduceConcernTaskGroupRequest){
        return  manProduceConcernTaskGroupItemMapper.getReport(manProduceConcernTaskGroupRequest);
    }

    /**
     * 新增生产关注事项组
     * 需要注意编码重复校验
     *
     * @param manProduceConcernTaskGroup 生产关注事项组
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManProduceConcernTaskGroup(ManProduceConcernTaskGroup manProduceConcernTaskGroup) {

        validHandlerSidIsExistAtPlant(manProduceConcernTaskGroup);

        List<ManProduceConcernTaskGroupItem> itemList = manProduceConcernTaskGroup.getItemList();
        if (CollectionUtil.isEmpty(itemList)) {
            throw new CustomException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
        }

        if (validManProduceConcernTaskGroupName(manProduceConcernTaskGroup) > 0) {
            throw new CustomException("生产关注事项组名称已存在，请核实!");
        }
        if (StrUtil.equals(manProduceConcernTaskGroup.getHandleStatus() , ConstantsEms.CHECK_STATUS)) {
            manProduceConcernTaskGroup.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        BasPlant basPlant = basPlantMapper.selectBasPlantById(manProduceConcernTaskGroup.getPlantSid());
        manProduceConcernTaskGroup.setPlantCode(basPlant.getPlantCode()).setPlantSid(Long.valueOf(basPlant.getPlantSid()));

        manProduceConcernTaskGroup.setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());

        int row = manProduceConcernTaskGroupMapper.insert(manProduceConcernTaskGroup);
        if (row > 0) {

            itemList.forEach((item) -> {
                item.setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                item.setConcernTaskGroupSid(manProduceConcernTaskGroup.getConcernTaskGroupSid());
                if (ObjectUtil.isNotEmpty(item.getHandlerSid())) {
                    BasStaff basStaff = basStaffMapper.selectBasStaffById(item.getHandlerSid());
                    item.setHandlerCode(basStaff.getStaffCode());
                }

            });
            int itemInsertCount = manProduceConcernTaskGroupItemMapper.inserts(itemList);
            if (itemInsertCount != itemList.size()) {
                throw new CustomException("系统异常,请联系联系管理员(明细新增失败)!");
            }


            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new ManProduceConcernTaskGroup(), manProduceConcernTaskGroup);
            MongodbDeal.insert(manProduceConcernTaskGroup.getConcernTaskGroupSid(), manProduceConcernTaskGroup.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    public int validManProduceConcernTaskGroupName (ManProduceConcernTaskGroup manProduceConcernTaskGroup) {

        List<ManProduceConcernTaskGroup> list = manProduceConcernTaskGroupMapper.selectList(Wrappers.lambdaQuery(ManProduceConcernTaskGroup.class)
                .eq(ManProduceConcernTaskGroup::getConcernTaskGroupName, manProduceConcernTaskGroup.getConcernTaskGroupName())
                .eq(ManProduceConcernTaskGroup::getPlantSid, manProduceConcernTaskGroup.getPlantSid()));

        return ObjectUtil.isEmpty(list) ? 0 : list.size();
    }

    public void validHandlerSidIsExistAtPlant (ManProduceConcernTaskGroup manProduceConcernTaskGroup) {

        HashSet<String> noExistNameList = new HashSet<>();
        List<ManProduceConcernTaskGroupItem> itemList = manProduceConcernTaskGroup.getItemList();
        if (CollectionUtil.isEmpty(itemList)) {
            throw new CustomException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
        }

        Long[] handlerSids = itemList.stream().map(ManProduceConcernTaskGroupItem::getHandlerSid).toArray(Long[]::new);

        BasStaff basStaff = new BasStaff();
        basStaff.setPlantSid(manProduceConcernTaskGroup.getPlantSid());
        //工厂下的员工
        List<BasStaff> basStaffs = basStaffMapper.selectBasStaffList(basStaff);

        //如果工厂没有员工
        if (CollectionUtil.isEmpty(basStaffs)) {
            for (Long handlerSid : handlerSids) {
                if (handlerSid != null) {
                    BasStaff noExitStaff = basStaffMapper.selectBasStaffById(handlerSid);
                    noExistNameList.add(noExitStaff.getStaffName());
                }
            }
        }else {
            boolean isExit;
            for (Long handlerSid : handlerSids) {
                isExit = false;
                for (BasStaff staff : basStaffs) {
                    if (ObjectUtil.equal(handlerSid , staff.getStaffSid())) {
                        isExit = true;
                        break;
                    }
                }
                if (!isExit && handlerSid != null) {
                    BasStaff noExitStaff = basStaffMapper.selectBasStaffById(handlerSid);
                    noExistNameList.add(noExitStaff.getStaffName());
                }
            }

        }
        if (noExistNameList != null && noExistNameList.size() > 0) {
            String message = "";
            for (String name : noExistNameList) {
                message += name + "、";
            }

            BasPlant basPlant = basPlantMapper.selectBasPlantById(manProduceConcernTaskGroup.getPlantSid());

            throw new CustomException("事项负责人" + message.substring(0,message.lastIndexOf("、")) + "不属于" + basPlant.getShortName() + " , 请核实！");
        }


    }

    /**
     * 修改生产关注事项组
     *
     * @param manProduceConcernTaskGroup 生产关注事项组
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManProduceConcernTaskGroup(ManProduceConcernTaskGroup manProduceConcernTaskGroup) {
        ManProduceConcernTaskGroup original = manProduceConcernTaskGroupMapper.selectManProduceConcernTaskGroupById(manProduceConcernTaskGroup.getConcernTaskGroupSid());
        int row = manProduceConcernTaskGroupMapper.updateById(manProduceConcernTaskGroup);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, manProduceConcernTaskGroup);
            MongodbDeal.update(manProduceConcernTaskGroup.getConcernTaskGroupSid(), original.getHandleStatus(), manProduceConcernTaskGroup.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更生产关注事项组
     *
     * @param manProduceConcernTaskGroup 生产关注事项组
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManProduceConcernTaskGroup(ManProduceConcernTaskGroup manProduceConcernTaskGroup) {
        ManProduceConcernTaskGroup response = manProduceConcernTaskGroupMapper.selectManProduceConcernTaskGroupById(manProduceConcernTaskGroup.getConcernTaskGroupSid());
        if (StrUtil.equals(manProduceConcernTaskGroup.getHandleStatus() , ConstantsEms.CHECK_STATUS)) {
            manProduceConcernTaskGroup.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        }
        manProduceConcernTaskGroup.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        List<ManProduceConcernTaskGroupItem> itemList = manProduceConcernTaskGroup.getItemList();

        if (!StrUtil.equals(response.getConcernTaskGroupName() , manProduceConcernTaskGroup.getConcernTaskGroupName())) {
            int exitCounts = validManProduceConcernTaskGroupName(manProduceConcernTaskGroup);
            if (exitCounts > 0) {
                throw new CustomException("生产关注事项组名称已存在，请核实!");
            }
        }

        int row = manProduceConcernTaskGroupMapper.updateAllById(manProduceConcernTaskGroup);
        if (row > 0) {

            if (CollectionUtil.isNotEmpty(manProduceConcernTaskGroup.getItemList())) {

                int deleteCounts = manProduceConcernTaskGroupItemMapper.delete(Wrappers.lambdaQuery(ManProduceConcernTaskGroupItem.class)
                        .eq(ManProduceConcernTaskGroupItem::getConcernTaskGroupSid, manProduceConcernTaskGroup.getConcernTaskGroupSid()));
                if (deleteCounts > 0) {
                    itemList.forEach((item) -> {
                        item.setConcernTaskGroupSid(manProduceConcernTaskGroup.getConcernTaskGroupSid());
                        if (ObjectUtil.isNotEmpty(item.getHandlerSid())) {
                            BasStaff basStaff = basStaffMapper.selectBasStaffById(item.getHandlerSid());
                            item.setHandlerCode(basStaff.getStaffCode());
                        }

                    });
                    int itemInsertCount = manProduceConcernTaskGroupItemMapper.inserts(itemList);
                    if (itemInsertCount != itemList.size()) {
                        throw new CustomException("系统异常,请联系联系管理员(新增明细失败)!");
                    }
                }else {
                    throw new CustomException("系统异常,请联系联系管理员(删除明细失败)!");
                }

            }else {
                throw new CustomException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
            }

            //插入日志
            MongodbUtil.insertUserLog(manProduceConcernTaskGroup.getConcernTaskGroupSid(), BusinessType.CHANGE.getValue(), response, manProduceConcernTaskGroup, TITLE);
        }
        return row;
    }

    /**
     * 批量删除生产关注事项组
     *
     * @param concernTaskGroupSids 需要删除的生产关注事项组ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManProduceConcernTaskGroupByIds(List<Long> concernTaskGroupSids) {
        List<ManProduceConcernTaskGroup> list = manProduceConcernTaskGroupMapper.selectList(new QueryWrapper<ManProduceConcernTaskGroup>()
                .lambda().in(ManProduceConcernTaskGroup::getConcernTaskGroupSid, concernTaskGroupSids));
        int row = manProduceConcernTaskGroupMapper.deleteBatchIds(concernTaskGroupSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new ManProduceConcernTaskGroup());
                MongodbUtil.insertUserLog(o.getConcernTaskGroupSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 启用/停用
     *
     * @param manProduceConcernTaskGroup
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(ManProduceConcernTaskGroup manProduceConcernTaskGroup) {
        int row = 0;
        Long[] sids = manProduceConcernTaskGroup.getConcernTaskGroupSidList();
        if (sids != null && sids.length > 0) {
            row = manProduceConcernTaskGroupMapper.update(null, new UpdateWrapper<ManProduceConcernTaskGroup>().lambda().set(ManProduceConcernTaskGroup::getStatus, manProduceConcernTaskGroup.getStatus())
                    .in(ManProduceConcernTaskGroup::getConcernTaskGroupSid, sids));
            for (Long id : sids) {
                manProduceConcernTaskGroup.setConcernTaskGroupSid(id);
                row = manProduceConcernTaskGroupMapper.updateById(manProduceConcernTaskGroup);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                MongodbDeal.status(manProduceConcernTaskGroup.getConcernTaskGroupSid(), manProduceConcernTaskGroup.getStatus(), null, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param manProduceConcernTaskGroup
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(ManProduceConcernTaskGroup manProduceConcernTaskGroup) {
        int row = 0;
        Long[] sids = manProduceConcernTaskGroup.getConcernTaskGroupSidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<ManProduceConcernTaskGroup> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(ManProduceConcernTaskGroup::getConcernTaskGroupSid, sids);
            updateWrapper.set(ManProduceConcernTaskGroup::getHandleStatus, manProduceConcernTaskGroup.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(manProduceConcernTaskGroup.getHandleStatus())) {
                updateWrapper.set(ManProduceConcernTaskGroup::getConfirmDate, new Date());
                updateWrapper.set(ManProduceConcernTaskGroup::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            row = manProduceConcernTaskGroupMapper.update(null, updateWrapper);
            if (row > 0) {
                for (Long id : sids) {
                    //插入日志
                    MongodbDeal.check(id, manProduceConcernTaskGroup.getHandleStatus(), null, TITLE, null);
                }
            }
        }
        return row;
    }

}
