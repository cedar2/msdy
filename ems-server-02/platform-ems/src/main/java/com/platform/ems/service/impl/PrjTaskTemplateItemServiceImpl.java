package com.platform.ems.service.impl;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.BasPositionMapper;
import com.platform.system.mapper.SystemUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.PrjTaskTemplateItemMapper;
import com.platform.ems.service.IPrjTaskTemplateItemService;

/**
 * 项目任务模板-明细Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-07
 */
@Service
@SuppressWarnings("all")
public class PrjTaskTemplateItemServiceImpl extends ServiceImpl<PrjTaskTemplateItemMapper, PrjTaskTemplateItem> implements IPrjTaskTemplateItemService {
    @Autowired
    private PrjTaskTemplateItemMapper prjTaskTemplateItemMapper;
    @Autowired
    private BasPositionMapper basPositionMapper;
    @Autowired
    private SystemUserMapper sysUserMapper;

    private static final String TITLE = "项目任务模板-明细";

    /**
     * 查询项目任务模板-明细
     *
     * @param taskTemplateItemSid 项目任务模板-明细ID
     * @return 项目任务模板-明细
     */
    @Override
    public PrjTaskTemplateItem selectPrjTaskTemplateItemById(Long taskTemplateItemSid) {
        PrjTaskTemplateItem prjTaskTemplateItem = prjTaskTemplateItemMapper.selectPrjTaskTemplateItemById(taskTemplateItemSid);
        MongodbUtil.find(prjTaskTemplateItem);
        return prjTaskTemplateItem;
    }

    /**
     * 查询项目任务模板-明细列表
     *
     * @param prjTaskTemplateItem 项目任务模板-明细
     * @return 项目任务模板-明细
     */
    @Override
    public List<PrjTaskTemplateItem> selectPrjTaskTemplateItemList(PrjTaskTemplateItem prjTaskTemplateItem) {
        return prjTaskTemplateItemMapper.selectPrjTaskTemplateItemList(prjTaskTemplateItem);
    }

    /**
     * 新增项目任务模板-明细
     * 需要注意编码重复校验
     *
     * @param prjTaskTemplateItem 项目任务模板-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPrjTaskTemplateItem(PrjTaskTemplateItem prjTaskTemplateItem) {
        // 设置数据
        int row = prjTaskTemplateItemMapper.insert(prjTaskTemplateItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new PrjTaskTemplateItem(), prjTaskTemplateItem);
            MongodbUtil.insertUserLog(prjTaskTemplateItem.getTaskTemplateItemSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改项目任务模板-明细
     *
     * @param prjTaskTemplateItem 项目任务模板-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePrjTaskTemplateItem(PrjTaskTemplateItem prjTaskTemplateItem) {
        PrjTaskTemplateItem original = prjTaskTemplateItemMapper.selectPrjTaskTemplateItemById(prjTaskTemplateItem.getTaskTemplateItemSid());
        int row = prjTaskTemplateItemMapper.updateAllById(prjTaskTemplateItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, prjTaskTemplateItem);
            MongodbUtil.insertUserLog(prjTaskTemplateItem.getTaskTemplateItemSid(),BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更项目任务模板-明细
     *
     * @param prjTaskTemplateItem 项目任务模板-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changePrjTaskTemplateItem(PrjTaskTemplateItem prjTaskTemplateItem) {
        PrjTaskTemplateItem response = prjTaskTemplateItemMapper.selectPrjTaskTemplateItemById(prjTaskTemplateItem.getTaskTemplateItemSid());
        int row = prjTaskTemplateItemMapper.updateAllById(prjTaskTemplateItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(prjTaskTemplateItem.getTaskTemplateItemSid(), BusinessType.CHANGE.getValue(), response, prjTaskTemplateItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除项目任务模板-明细
     *
     * @param taskTemplateItemSids 需要删除的项目任务模板-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePrjTaskTemplateItemByIds(List<Long> taskTemplateItemSids) {
        List<PrjTaskTemplateItem> list = prjTaskTemplateItemMapper.selectList(new QueryWrapper<PrjTaskTemplateItem>()
                .lambda().in(PrjTaskTemplateItem::getTaskTemplateItemSid, taskTemplateItemSids));
        int row = prjTaskTemplateItemMapper.deleteBatchIds(taskTemplateItemSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new PrjTaskTemplateItem());
                MongodbUtil.insertUserLog(o.getTaskTemplateItemSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 数据字段处理
     *
     * @param prjTask 任务节点
     * @return 结果
     */
    private void setData(PrjTaskTemplateItem templateItem) {
        // 发起岗位处理
        String startCode = null;
        if (ArrayUtil.isNotEmpty(templateItem.getStartPositionCodeList())) {
            startCode = "";
            for (int i = 0; i < templateItem.getStartPositionCodeList().length; i++) {
                startCode = startCode + templateItem.getStartPositionCodeList()[i] + ";";
            }
            if (templateItem.getStartPositionCodeList().length > 1) {
                templateItem.setStartPositionSid(null);
            }
        }
        templateItem.setStartPositionCode(startCode);
        if (startCode == null) {
            templateItem.setStartPositionSid(null);
        }
        // 负责岗位处理
        String chargeCode = null;
        if (ArrayUtil.isNotEmpty(templateItem.getChargePositionCodeList())) {
            chargeCode = "";
            for (int i = 0; i < templateItem.getChargePositionCodeList().length; i++) {
                chargeCode = chargeCode + templateItem.getChargePositionCodeList()[i] + ";";
            }
            if (templateItem.getChargePositionCodeList().length > 1) {
                templateItem.setChargePositionSid(null);
            }
        }
        templateItem.setChargePositionCode(chargeCode);
        if (chargeCode == null) {
            templateItem.setChargePositionSid(null);
        }
        // 告知岗位处理
        String noticeCode = null;
        if (ArrayUtil.isNotEmpty(templateItem.getNoticePositionCodeList())) {
            noticeCode = "";
            for (int i = 0; i < templateItem.getNoticePositionCodeList().length; i++) {
                noticeCode = noticeCode + templateItem.getNoticePositionCodeList()[i] + ";";
            }
            if (templateItem.getNoticePositionCodeList().length > 1) {
                templateItem.setNoticePositionSid(null);
            }
        }
        templateItem.setNoticePositionCode(noticeCode);
        if (noticeCode == null) {
            templateItem.setNoticePositionSid(null);
        }
        // 处理人
        String handlerTask = null;
        if (ArrayUtil.isNotEmpty(templateItem.getHandlerTaskList())) {
            handlerTask = "";
            for (int i = 0; i < templateItem.getHandlerTaskList().length; i++) {
                handlerTask = handlerTask + templateItem.getHandlerTaskList()[i] + ";";
            }
        }
        templateItem.setHandlerTask(handlerTask);
    }

    /**
     * 获取岗位名称
     *
     * @param prjTask 任务节点
     * @return 结果
     */
    private void getPosition(PrjTaskTemplateItem templateItem) {
        // 发起岗位
        if (StrUtil.isNotBlank(templateItem.getStartPositionCode())) {
            String[] starts = templateItem.getStartPositionCode().split(";");
            templateItem.setStartPositionCodeList(starts);
            List<BasPosition> startList = basPositionMapper.selectList(new QueryWrapper<BasPosition>()
                    .lambda().in(BasPosition::getPositionCode, starts));
            if (ArrayUtil.isNotEmpty(startList)) {
                String startName = "";
                for (int i = 0; i < startList.size(); i++) {
                    startName = startName + startList.get(i).getPositionName() + ";";
                }
                templateItem.setStartPositionName(startName);
            }
        }
        // 负责岗位
        if (StrUtil.isNotBlank(templateItem.getChargePositionCode())) {
            String[] charges = templateItem.getChargePositionCode().split(";");
            templateItem.setChargePositionCodeList(charges);
            List<BasPosition> chargeList = basPositionMapper.selectList(new QueryWrapper<BasPosition>()
                    .lambda().in(BasPosition::getPositionCode, charges));
            if (ArrayUtil.isNotEmpty(chargeList)) {
                String chargeName = "";
                for (int i = 0; i < chargeList.size(); i++) {
                    chargeName = chargeName + chargeList.get(i).getPositionName() + ";";
                }
                templateItem.setChargePositionName(chargeName);
            }
        }
        // 告知岗位
        if (StrUtil.isNotBlank(templateItem.getNoticePositionCode())) {
            String[] notices = templateItem.getNoticePositionCode().split(";");
            templateItem.setNoticePositionCodeList(notices);
            List<BasPosition> noticeList = basPositionMapper.selectList(new QueryWrapper<BasPosition>()
                    .lambda().in(BasPosition::getPositionCode, notices));
            if (ArrayUtil.isNotEmpty(noticeList)) {
                String noticeName = "";
                for (int i = 0; i < noticeList.size(); i++) {
                    noticeName = noticeName + noticeList.get(i).getPositionName() + ";";
                }
                templateItem.setNoticePositionName(noticeName);
            }
        }
        // 处理人
        if (StrUtil.isNotBlank(templateItem.getHandlerTask())) {
            String[] handlerTask = templateItem.getHandlerTask().split(";");
            templateItem.setHandlerTaskList(handlerTask);
        }
    }

    /**
     * 查询项目任务模板-明细
     *
     * @param taskTemplateSid 项目任务模板-主表ID
     * @return 项目任务模板-明细
     */
    @Override
    public List<PrjTaskTemplateItem> selectPrjTaskTemplateItemListById(Long taskTemplateSid) {
        List<PrjTaskTemplateItem> prjTaskTemplateItemList = prjTaskTemplateItemMapper
                .selectPrjTaskTemplateItemList(new PrjTaskTemplateItem()
                        .setTaskTemplateSid(taskTemplateSid));
        // 操作日志
        if (CollectionUtil.isNotEmpty(prjTaskTemplateItemList)) {
            prjTaskTemplateItemList.forEach(item->{
                this.getPosition(item);
                MongodbUtil.find(item);
            });
        }
        return prjTaskTemplateItemList;
    }

    /**
     * 批量新增项目任务模板-明细
     *
     * @param template 项目任务模板
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPrjTaskTemplateItemList(PrjTaskTemplate template) {
        int row = 0;
        List<PrjTaskTemplateItem> list = template.getTaskTemplateItemList();
        if (CollectionUtil.isNotEmpty(list)) {
            PrjTaskTemplateItem item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setTaskTemplateSid(template.getTaskTemplateSid());
                item.setTaskTemplateCode(template.getTaskTemplateCode());
                // 处理岗位
                this.setData(item);
                row += insertPrjTaskTemplateItem(item);
            }
        }
        return row;
    }

    /**
     * 批量修改项目任务模板-明细
     *
     * @param template 项目任务模板
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePrjTaskTemplateItemList(PrjTaskTemplate template) {
        int row = 0;
        List<PrjTaskTemplateItem> list = template.getTaskTemplateItemList();
        // 原本明细
        List<PrjTaskTemplateItem> oldList = prjTaskTemplateItemMapper.selectList(new QueryWrapper<PrjTaskTemplateItem>()
                .lambda().eq(PrjTaskTemplateItem::getTaskTemplateSid, template.getTaskTemplateSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<PrjTaskTemplateItem> newList = list.stream().filter(o -> o.getTaskTemplateItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                template.setTaskTemplateItemList(newList);
                insertPrjTaskTemplateItemList(template);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<PrjTaskTemplateItem> updateList = list.stream().filter(o -> o.getTaskTemplateItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(PrjTaskTemplateItem::getTaskTemplateItemSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, PrjTaskTemplateItem> map = oldList.stream().collect(Collectors.toMap(PrjTaskTemplateItem::getTaskTemplateItemSid, Function.identity()));
                    updateList.forEach(item->{
                        if (map.containsKey(item.getTaskTemplateItemSid())) {
                            // 处理岗位
                            this.setData(item);
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getTaskTemplateItemSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            prjTaskTemplateItemMapper.updateAllById(item);
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getTaskTemplateItemSid(), template.getHandleStatus(), msgList, TITLE);
                        }
                    });
                    // 删除行
                    List<PrjTaskTemplateItem> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getTaskTemplateItemSid())).collect(Collectors.toList());
                    deletePrjTaskTemplateItemByList(delList);
                }
            }
        }
        else {
            // 如果 请求明细 没有了，但是数据库有明细，则删除数据库的明细
            if (CollectionUtil.isNotEmpty(oldList)) {
                deletePrjTaskTemplateItemByList(oldList);
            }
        }
        return row;
    }

    /**
     * 批量删除项目任务模板-明细
     *
     * @param itemList 需要删除的项目任务模板-明细列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePrjTaskTemplateItemByList(List<PrjTaskTemplateItem> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> prjTaskTemplateItemSidList = itemList.stream().filter(o -> o.getTaskTemplateItemSid() != null)
                .map(PrjTaskTemplateItem::getTaskTemplateItemSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(prjTaskTemplateItemSidList)) {
            row = prjTaskTemplateItemMapper.deleteBatchIds(prjTaskTemplateItemSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new PrjTaskTemplateItem());
                    MongodbUtil.insertUserLog(o.getTaskTemplateItemSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }

    /**
     * 批量删除项目任务模板-明细 根据主表sids
     *
     * @param updatePrjTaskTemplateSidList 需要删除的项目任务模板sids
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePrjTaskTemplateItemByTemplete(List<Long> prjTaskTemplateSidList) {
        List<PrjTaskTemplateItem> itemList = prjTaskTemplateItemMapper.selectList(new QueryWrapper<PrjTaskTemplateItem>()
                .lambda().in(PrjTaskTemplateItem::getTaskTemplateSid, prjTaskTemplateSidList));
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemList)) {
            row = this.deletePrjTaskTemplateItemByList(itemList);
        }
        return row;
    }

    /**
     * 任务模板明细报表分配任务处理人
     * @param taskTemplateItem 入参
     * @return 出参
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setTaskHandler(PrjTaskTemplateItem taskTemplateItem) {
        if (taskTemplateItem.getTaskTemplateItemSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        // 原数据
        List<PrjTaskTemplateItem> taskList = prjTaskTemplateItemMapper.selectPrjTaskTemplateItemList(new PrjTaskTemplateItem()
                .setTaskTemplateItemSidList(taskTemplateItem.getTaskTemplateItemSidList()));
        // 修改
        LambdaUpdateWrapper<PrjTaskTemplateItem> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        // 处理人
        updateWrapper.in(PrjTaskTemplateItem::getTaskTemplateItemSid, taskTemplateItem.getTaskTemplateItemSidList());
        // 新的处理人
        List<SysUser> userList = new ArrayList<>();
        // 带分号的用户名
        String handlerTask = null;
        // 带分号的用户昵称
        String handlerTaskName = "";
        if (ArrayUtil.isNotEmpty(taskTemplateItem.getHandlerTaskList())) {
            handlerTask = "";
            List<String> handlerTaskArrayList = Arrays.asList(taskTemplateItem.getHandlerTaskList());
            // 按照用户名排序
            handlerTaskArrayList = handlerTaskArrayList.stream().sorted().collect(Collectors.toList());
            for (int i = 0; i < handlerTaskArrayList.size(); i++) {
                if (i > 0) {
                    handlerTask = handlerTask + ";" + handlerTaskArrayList.get(i);
                } else {
                    handlerTask = handlerTaskArrayList.get(i);
                }
            }
            // 保存数据库的字段
            taskTemplateItem.setHandlerTask(handlerTask);
            // 获取对应的昵称
            userList = sysUserMapper.selectList(new QueryWrapper<SysUser>().lambda().in(SysUser::getUserName, taskTemplateItem.getHandlerTaskList()));
            if (CollectionUtil.isNotEmpty(userList)) {
                userList = userList.stream().sorted(Comparator.comparing(SysUser::getUserName)).collect(Collectors.toList());
                for (int i = 0; i < userList.size(); i++) {
                    if (i > 0) {
                        handlerTaskName = handlerTaskName + ";" + userList.get(i).getNickName();
                    } else {
                        handlerTaskName = userList.get(i).getNickName();
                    }

                }
            }
        }
        // 修改数据库中的 处理人字段
        updateWrapper.set(PrjTaskTemplateItem::getHandlerTask, taskTemplateItem.getHandlerTask());
        row = prjTaskTemplateItemMapper.update(new PrjTaskTemplateItem(), updateWrapper);
        // 操作日志记录
        for (int i = 0; i < taskList.size(); i++) {
            PrjTaskTemplateItem nowData = new PrjTaskTemplateItem();
            BeanUtil.copyProperties(taskList.get(i), nowData);
            // 处理人
            if ((taskList.get(i).getHandlerTask() != null && !taskList.get(i).getHandlerTask().equals(taskTemplateItem.getHandlerTask()))
                    || (taskList.get(i).getHandlerTask() == null && taskTemplateItem.getHandlerTask() != null)) {
                nowData.setHandlerTask(taskTemplateItem.getHandlerTask());
                List<OperMsg> msgList;
                msgList = BeanUtils.eq(taskList.get(i), nowData);
                if (CollectionUtil.isNotEmpty(msgList)) {
                    String oldCode = StrUtil.isBlank(taskList.get(i).getHandlerTaskName()) ? "" : taskList.get(i).getHandlerTaskName();
                    String remark = "明细”" + taskList.get(i).getTaskName() + "“处理人变更，变更前：" + oldCode + "；变更后：" + handlerTaskName;
                    MongodbUtil.insertUserLog(taskList.get(i).getTaskTemplateSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
                    MongodbUtil.insertUserLog(taskList.get(i).getTaskTemplateItemSid(), BusinessType.QITA.getValue(), msgList, TITLE, remark);
                }
            }
        }
        return taskTemplateItem.getTaskTemplateItemSidList().length;
    }
}
