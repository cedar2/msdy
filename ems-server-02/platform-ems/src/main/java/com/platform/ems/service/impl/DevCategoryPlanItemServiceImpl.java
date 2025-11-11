package com.platform.ems.service.impl;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.core.domain.entity.ConMaterialClass;
import com.platform.ems.domain.DevCategoryPlan;
import com.platform.ems.domain.dto.request.form.DevCategoryPlanItemFormRequest;
import com.platform.ems.domain.dto.response.form.DevCategoryPlanItemFormResponse;
import com.platform.ems.mapper.ConMaterialClassMapper;
import com.platform.system.mapper.SystemUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.DevCategoryPlanItemMapper;
import com.platform.ems.domain.DevCategoryPlanItem;
import com.platform.ems.service.IDevCategoryPlanItemService;

import static java.util.stream.Collectors.toList;

/**
 * 品类规划-明细Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-09
 */
@Service
@SuppressWarnings("all")
public class DevCategoryPlanItemServiceImpl extends ServiceImpl<DevCategoryPlanItemMapper, DevCategoryPlanItem> implements IDevCategoryPlanItemService {
    @Autowired
    private DevCategoryPlanItemMapper devCategoryPlanItemMapper;
    @Autowired
    private ConMaterialClassMapper materialClassMapper;

    @Autowired
    private SystemUserMapper sysUserMapper;

    private static final String TITLE = "品类规划-明细";

    /**
     * 查询品类规划-明细
     *
     * @param categoryPlanItemSid 品类规划-明细ID
     * @return 品类规划-明细
     */
    @Override
    public DevCategoryPlanItem selectDevCategoryPlanItemById(Long categoryPlanItemSid) {
        DevCategoryPlanItem devCategoryPlanItem = devCategoryPlanItemMapper.selectDevCategoryPlanItemById(categoryPlanItemSid);
        MongodbUtil.find(devCategoryPlanItem);
        return devCategoryPlanItem;
    }

    /**
     * 查询品类规划-明细列表
     *
     * @param devCategoryPlanItem 品类规划-明细
     * @return 品类规划-明细
     */
    @Override
    public List<DevCategoryPlanItem> selectDevCategoryPlanItemList(DevCategoryPlanItem devCategoryPlanItem) {
        return devCategoryPlanItemMapper.selectDevCategoryPlanItemList(devCategoryPlanItem);
    }

    /**
     * 设置部分字段的编码
     *
     * @param old 表中数据，new修改后数据
     * @return 结果
     */
    private void setData(DevCategoryPlanItem oldPlanItem, DevCategoryPlanItem newPlanItem) {
        // 大类
        if (newPlanItem.getBigClassSid() != null && !newPlanItem.getBigClassSid().equals(oldPlanItem.getBigClassSid())) {
            ConMaterialClass materialClass = materialClassMapper.selectById(newPlanItem.getBigClassSid());
            if (materialClass != null) {
                newPlanItem.setBigClassCode(materialClass.getNodeCode());
            } else {
                newPlanItem.setBigClassCode(null);
            }
        } else if (newPlanItem.getBigClassSid() == null) {
            newPlanItem.setBigClassCode(null);
        }
        // 中类
        if (newPlanItem.getMiddleClassSid() != null && !newPlanItem.getMiddleClassSid().equals(oldPlanItem.getMiddleClassSid())) {
            ConMaterialClass materialClass = materialClassMapper.selectById(newPlanItem.getMiddleClassSid());
            if (materialClass != null) {
                newPlanItem.setMiddleClassCode(materialClass.getNodeCode());
            } else {
                newPlanItem.setMiddleClassCode(null);
            }
        } else if (newPlanItem.getMiddleClassSid() == null) {
            newPlanItem.setMiddleClassCode(null);
        }
        // 小类
        if (newPlanItem.getSmallClassSid() != null && !newPlanItem.getSmallClassSid().equals(oldPlanItem.getSmallClassSid())) {
            ConMaterialClass materialClass = materialClassMapper.selectById(newPlanItem.getSmallClassSid());
            if (materialClass != null) {
                newPlanItem.setSmallClassCode(materialClass.getNodeCode());
            } else {
                newPlanItem.setSmallClassCode(null);
            }
        } else if (newPlanItem.getSmallClassSid() == null) {
            newPlanItem.setSmallClassCode(null);
        }
        // 物料分类 = 小类
        newPlanItem.setMaterialClassSid(newPlanItem.getSmallClassSid())
                .setMaterialClassCode(newPlanItem.getSmallClassCode());
        // 写入开发计划负责人的code
        if (newPlanItem.getNextReceiverSid() != null && !newPlanItem.getNextReceiverSid().equals(oldPlanItem.getNextReceiverSid())) {
            SysUser sysUser = sysUserMapper.selectById(newPlanItem.getNextReceiverSid());
            if (sysUser != null) {
                newPlanItem.setNextReceiverCode(sysUser.getUserName());
            } else {
                newPlanItem.setNextReceiverCode(null);
            }
        }
        else if (newPlanItem.getNextReceiverSid() == null) {
            newPlanItem.setNextReceiverCode(null);
        }
    }

    /**
     * 新增品类规划-明细
     * 需要注意编码重复校验
     *
     * @param devCategoryPlanItem 品类规划-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDevCategoryPlanItem(DevCategoryPlanItem devCategoryPlanItem) {
        // 写入物料分类编码
        setData(new DevCategoryPlanItem(), devCategoryPlanItem);
        int row = devCategoryPlanItemMapper.insert(devCategoryPlanItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new DevCategoryPlanItem(), devCategoryPlanItem);
            // 判断是否是导入的数据
            if (devCategoryPlanItem.getImportType() == null) {
                devCategoryPlanItem.setImportType(BusinessType.INSERT.getValue());
            }
            MongodbUtil.insertUserLog(devCategoryPlanItem.getCategoryPlanItemSid(), devCategoryPlanItem.getImportType(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 修改品类规划-明细
     *
     * @param devCategoryPlanItem 品类规划-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDevCategoryPlanItem(DevCategoryPlanItem devCategoryPlanItem) {
        DevCategoryPlanItem original = devCategoryPlanItemMapper.selectDevCategoryPlanItemById(devCategoryPlanItem.getCategoryPlanItemSid());
        int row = devCategoryPlanItemMapper.updateById(devCategoryPlanItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, devCategoryPlanItem);
            MongodbUtil.insertUserLog(devCategoryPlanItem.getCategoryPlanItemSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更品类规划-明细
     *
     * @param devCategoryPlanItem 品类规划-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeDevCategoryPlanItem(DevCategoryPlanItem devCategoryPlanItem) {
        DevCategoryPlanItem response = devCategoryPlanItemMapper.selectDevCategoryPlanItemById(devCategoryPlanItem.getCategoryPlanItemSid());
        int row = devCategoryPlanItemMapper.updateAllById(devCategoryPlanItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(devCategoryPlanItem.getCategoryPlanItemSid(), BusinessType.CHANGE.getValue(), response, devCategoryPlanItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除品类规划-明细
     *
     * @param categoryPlanItemSids 需要删除的品类规划-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDevCategoryPlanItemByIds(List<Long> categoryPlanItemSids) {
        List<DevCategoryPlanItem> list = devCategoryPlanItemMapper.selectList(new QueryWrapper<DevCategoryPlanItem>()
                .lambda().in(DevCategoryPlanItem::getCategoryPlanItemSid, categoryPlanItemSids));
        int row = devCategoryPlanItemMapper.deleteBatchIds(categoryPlanItemSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new DevCategoryPlanItem());
                MongodbUtil.insertUserLog(o.getCategoryPlanItemSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 设置行号
     *
     * @param order 销售意向单
     * @return 结果
     */
    private void setItemNum(List<DevCategoryPlanItem> list) {
        List<DevCategoryPlanItem> nullItemList = list.stream().filter(o->o.getCategoryPlanItemNum()==null).collect(toList());
        if (CollectionUtil.isNotEmpty(nullItemList)) {
            int maxNum = 1;
            if (CollectionUtil.isNotEmpty(list)){
                List<DevCategoryPlanItem> haveItemList = list.stream().filter(o->o.getCategoryPlanItemNum()!=null).collect(toList());
                if (CollectionUtil.isNotEmpty(haveItemList)) {
                    haveItemList = haveItemList.stream().sorted(Comparator.comparing(DevCategoryPlanItem::getCategoryPlanItemNum).reversed()).collect(Collectors.toList());
                    maxNum = haveItemList.get(0).getCategoryPlanItemNum() + 1;
                }
            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getCategoryPlanItemNum() == null) {
                    list.get(i).setCategoryPlanItemNum(maxNum++);
                }
            }
        }
    }

    /**
     * 查询品类规划-明细
     *
     * @param categoryPlanSid 品类规划-主表ID
     * @return 品类规划-明细
     */
    @Override
    public List<DevCategoryPlanItem> selectDevCategoryPlanItemListById(Long categoryPlanSid) {
        List<DevCategoryPlanItem> devCategoryPlanItemList = devCategoryPlanItemMapper
                .selectDevCategoryPlanItemList(new DevCategoryPlanItem()
                        .setCategoryPlanSid(categoryPlanSid));
        // 操作日志
        if (CollectionUtil.isNotEmpty(devCategoryPlanItemList)) {
            devCategoryPlanItemList.forEach(item->{
                MongodbUtil.find(item);
            });
        }
        return devCategoryPlanItemList;
    }

    /**
     * 批量新增品类规划-明细
     *
     * @param plan 品类规划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDevCategoryPlanItemList(DevCategoryPlan plan) {
        int row = 0;
        List<DevCategoryPlanItem> list = plan.getCategoryPlanItemList();
        if (CollectionUtil.isNotEmpty(list)) {
            setItemNum(list);
            DevCategoryPlanItem item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setCategoryPlanSid(plan.getCategoryPlanSid());
                item.setCategoryPlanCode(plan.getCategoryPlanCode());
                row += insertDevCategoryPlanItem(item);
            }
        }
        return row;
    }

    /**
     * 批量修改品类规划-明细
     *
     * @param plan 品类规划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDevCategoryPlanItemList(DevCategoryPlan plan) {
        int row = 0;
        List<DevCategoryPlanItem> list = plan.getCategoryPlanItemList();
        setItemNum(list);
        // 原本明细
        List<DevCategoryPlanItem> oldList = devCategoryPlanItemMapper.selectList(new QueryWrapper<DevCategoryPlanItem>()
                .lambda().eq(DevCategoryPlanItem::getCategoryPlanSid, plan.getCategoryPlanSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<DevCategoryPlanItem> newList = list.stream().filter(o -> o.getCategoryPlanItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                plan.setCategoryPlanItemList(newList);
                insertDevCategoryPlanItemList(plan);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<DevCategoryPlanItem> updateList = list.stream().filter(o -> o.getCategoryPlanItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(DevCategoryPlanItem::getCategoryPlanItemSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, DevCategoryPlanItem> map = oldList.stream().collect(Collectors.toMap(DevCategoryPlanItem::getCategoryPlanItemSid, Function.identity()));
                    updateList.forEach(item->{
                        if (map.containsKey(item.getCategoryPlanItemSid())) {
                            // 更新人更新日期
                            List<OperMsg> msgList;
                            msgList = BeanUtils.eq(map.get(item.getCategoryPlanItemSid()), item);
                            if (CollectionUtil.isNotEmpty(msgList)) {
                                item.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            }
                            // 写入物料分类编码
                            setData(map.get(item.getCategoryPlanItemSid()), item);
                            devCategoryPlanItemMapper.updateAllById(item); // 全量更新
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getCategoryPlanItemSid(), plan.getHandleStatus(), msgList, TITLE);
                        }
                    });
                    // 删除行
                    List<DevCategoryPlanItem> delList = oldList.stream().filter(o -> !updateSidList.contains(o.getCategoryPlanItemSid())).collect(Collectors.toList());
                    deleteDevCategoryPlanItemByList(delList);
                }
            }
        }
        else {
            // 如果 请求明细 没有了，但是数据库有明细，则删除数据库的明细
            if (CollectionUtil.isNotEmpty(oldList)) {
                deleteDevCategoryPlanItemByList(oldList);
            }
        }
        return row;
    }

    /**
     * 批量删除品类规划-明细
     *
     * @param itemList 需要删除的品类规划-明细列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDevCategoryPlanItemByList(List<DevCategoryPlanItem> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> devCategoryPlanItemSidList = itemList.stream().filter(o -> o.getCategoryPlanItemSid() != null)
                .map(DevCategoryPlanItem::getCategoryPlanItemSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(devCategoryPlanItemSidList)) {
            row = devCategoryPlanItemMapper.deleteBatchIds(devCategoryPlanItemSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new DevCategoryPlanItem());
                    MongodbUtil.insertUserLog(o.getCategoryPlanItemSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }

    /**
     * 批量删除品类规划-明细 根据主表sids
     *
     * @param updatedevCategoryPlanSidList 需要删除的品类规划sids
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDevCategoryPlanItemByPlan(List<Long> devCategoryPlanSidList) {
        List<DevCategoryPlanItem> itemList = devCategoryPlanItemMapper.selectList(new QueryWrapper<DevCategoryPlanItem>()
                .lambda().in(DevCategoryPlanItem::getCategoryPlanSid, devCategoryPlanSidList));
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemList)) {
            row = this.deleteDevCategoryPlanItemByList(itemList);
        }
        return row;
    }

    /**
     * 查询品类规划-明细报表
     *
     * @param request 品类规划-明细报表请求体
     * @return 品类规划-明细集合
     */
    @Override
    public List<DevCategoryPlanItemFormResponse> selectDevCategoryPlanItemForm(DevCategoryPlanItemFormRequest request) {
        return devCategoryPlanItemMapper.selectDevCategoryPlanItemForm(request);
    }

}
