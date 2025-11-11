package com.platform.ems.service.impl;

import java.math.BigDecimal;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.ManProcessStepCompleteRecordTableRequest;
import com.platform.ems.domain.dto.response.*;
import com.platform.ems.mapper.PayProductProcessStepItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.ManProcessStepCompleteRecordItemMapper;
import com.platform.ems.service.IManProcessStepCompleteRecordItemService;

import static java.util.stream.Collectors.toList;

/**
 * 商品道序完成量台账-明细Service业务层处理
 *
 * @author chenkw
 * @date 2022-10-20
 */
@Service
@SuppressWarnings("all")
public class ManProcessStepCompleteRecordItemServiceImpl extends ServiceImpl<ManProcessStepCompleteRecordItemMapper, ManProcessStepCompleteRecordItem> implements IManProcessStepCompleteRecordItemService {
    @Autowired
    private ManProcessStepCompleteRecordItemMapper manProcessStepCompleteRecordItemMapper;
    @Autowired
    private PayProductProcessStepItemMapper payProductProcessStepItemMapper;
    private static final String TITLE = "商品道序完成量台账-明细";

    /**
     * 查询商品道序完成量台账-明细
     *
     * @param stepCompleteRecordItemSid 商品道序完成量台账-明细ID
     * @return 商品道序完成量台账-明细
     */
    @Override
    public ManProcessStepCompleteRecordItem selectManProcessStepCompleteRecordItemById(Long stepCompleteRecordItemSid) {
        ManProcessStepCompleteRecordItem manProcessStepCompleteRecordItem = manProcessStepCompleteRecordItemMapper.selectManProcessStepCompleteRecordItemById(stepCompleteRecordItemSid);
        MongodbUtil.find(manProcessStepCompleteRecordItem);
        return manProcessStepCompleteRecordItem;
    }

    /**
     * 查询商品道序完成量台账-明细  根据主表sid
     *
     * @param stepCompleteRecordSid 商品道序完成量台账ID
     * @return 商品道序完成量台账-明细
     */
    @Override
    public List<ManProcessStepCompleteRecordItem> selectManProcessStepCompleteRecordItemListById(Long stepCompleteRecordSid) {
        List<ManProcessStepCompleteRecordItem> list = manProcessStepCompleteRecordItemMapper.selectManProcessStepCompleteRecordItemList(
                new ManProcessStepCompleteRecordItem().setStepCompleteRecordSid(stepCompleteRecordSid));
        if (CollectionUtil.isNotEmpty(list)) {
            // 按“款号+操作部门+道序序号+排产批次号+员工”升序
            list = list.stream()
                    .sorted(Comparator.comparing(ManProcessStepCompleteRecordItem::getProductCode, Comparator.nullsLast(String::compareTo)
                                .thenComparing(Collator.getInstance(Locale.CHINA)))
                            .thenComparing(ManProcessStepCompleteRecordItem::getDepartmentName, Comparator.nullsLast(String::compareTo))
                            .thenComparing(ManProcessStepCompleteRecordItem::getSort, Comparator.nullsLast(BigDecimal::compareTo))
                            .thenComparing(ManProcessStepCompleteRecordItem::getPaichanBatch, Comparator.nullsLast(Integer::compareTo))
                            .thenComparing(ManProcessStepCompleteRecordItem::getWorkerName, Comparator.nullsLast(String::compareTo)
                                    .thenComparing(Collator.getInstance(Locale.CHINA)))
                    ).collect(toList());
            list.forEach(item -> {
                MongodbUtil.find(item);
            });
        }
        return list;
    }

    /**
     * 查询商品道序完成量台账-明细列表
     *
     * @param manProcessStepCompleteRecordItem 商品道序完成量台账-明细
     * @return 商品道序完成量台账-明细
     */
    @Override
    public List<ManProcessStepCompleteRecordItem> selectManProcessStepCompleteRecordItemList(ManProcessStepCompleteRecordItem manProcessStepCompleteRecordItem) {
        return manProcessStepCompleteRecordItemMapper.selectManProcessStepCompleteRecordItemList(manProcessStepCompleteRecordItem);
    }

    /**
     * 新增商品道序完成量台账-明细
     * 需要注意编码重复校验
     *
     * @param manProcessStepCompleteRecordItem 商品道序完成量台账-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManProcessStepCompleteRecordItem(ManProcessStepCompleteRecordItem manProcessStepCompleteRecordItem) {
        int row = manProcessStepCompleteRecordItemMapper.insert(manProcessStepCompleteRecordItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new ManProcessStepCompleteRecordItem(), manProcessStepCompleteRecordItem);
            MongodbUtil.insertUserLog(manProcessStepCompleteRecordItem.getStepCompleteRecordItemSid(), BusinessType.INSERT.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量新增商品道序完成量台账-明细
     *
     * @param record 商品道序完成量台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertManProcessStepCompleteRecordItemList(ManProcessStepCompleteRecord record) {
        int row = 0;
        List<ManProcessStepCompleteRecordItem> list = record.getStepCompleteRecordItemList();
        if (CollectionUtil.isNotEmpty(list)) {
            int maxNum = record.getItemNum();
            ManProcessStepCompleteRecordItem item = null;
            for (int i = 0; i < list.size(); i++) {
                item = list.get(i);
                // 写入主表的 sid
                item.setStepCompleteRecordSid(record.getStepCompleteRecordSid());
                item.setItemNum(++maxNum);
                row += insertManProcessStepCompleteRecordItem(item);
            }
        }
        return row;
    }

    /**
     * 获取最大行号
     *
     * @param manProcessStepCompleteRecordItem 商品道序完成量台账-明细
     * @return 结果
     */
    private int getMaxItemNum(List<ManProcessStepCompleteRecordItem> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            Optional<Integer> i = list.stream().map(ManProcessStepCompleteRecordItem::getItemNum)
                    .filter(ObjectUtil::isNotNull).max(Comparator.naturalOrder());
            if (i.isPresent()) {
                return i.get();
            }
        }
        return 0;
    }

    /**
     * 修改商品道序完成量台账-明细
     *
     * @param manProcessStepCompleteRecordItem 商品道序完成量台账-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManProcessStepCompleteRecordItem(ManProcessStepCompleteRecordItem manProcessStepCompleteRecordItem) {
        ManProcessStepCompleteRecordItem original = manProcessStepCompleteRecordItemMapper.selectManProcessStepCompleteRecordItemById(manProcessStepCompleteRecordItem.getStepCompleteRecordItemSid());
        int row = manProcessStepCompleteRecordItemMapper.updateById(manProcessStepCompleteRecordItem);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(original, manProcessStepCompleteRecordItem);
            MongodbUtil.insertUserLog(manProcessStepCompleteRecordItem.getStepCompleteRecordItemSid(), BusinessType.UPDATE.getValue(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量修改商品道序完成量台账-明细
     *
     * @param order 商品道序完成量台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateManProcessStepCompleteRecordItemList(ManProcessStepCompleteRecord record) {
        int row = 0;
        List<ManProcessStepCompleteRecordItem> list = record.getStepCompleteRecordItemList();
        // 原本明细
        List<ManProcessStepCompleteRecordItem> oldList = manProcessStepCompleteRecordItemMapper.selectList(new QueryWrapper<ManProcessStepCompleteRecordItem>()
                .lambda().eq(ManProcessStepCompleteRecordItem::getStepCompleteRecordSid, record.getStepCompleteRecordSid()));
        if (CollectionUtil.isNotEmpty(list)) {
            // 新增行
            List<ManProcessStepCompleteRecordItem> newList = list.stream().filter(o -> o.getStepCompleteRecordItemSid() == null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(newList)) {
                record.setItemNum(getMaxItemNum(oldList)); // 写入最大行号
                record.setStepCompleteRecordItemList(newList);
                insertManProcessStepCompleteRecordItemList(record);
            }
            // 页面中存在sid的行，可能走变更，也可能另一种情况：被删了，不走变更
            List<ManProcessStepCompleteRecordItem> updateList = list.stream().filter(o -> o.getStepCompleteRecordItemSid() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> updateSidList = updateList.stream().map(ManProcessStepCompleteRecordItem::getStepCompleteRecordItemSid).collect(Collectors.toList());
                // 变更行 （为了记录操作日志 旧-新，所以要更新系统中存在的行，若此时系统中不在了，就不更新）
                // 所以上面这种情况 就是 如果查询出来数据库中没有数据了，但是 又走了这边sid存在的变更，则可以推出，数据库的旧数据被另外人删了，所以不用走变更
                if (CollectionUtil.isNotEmpty(oldList)) {
                    // 变更行 过滤出 还在系统中 待变更的行
                    Map<Long, ManProcessStepCompleteRecordItem> map = oldList.stream().collect(Collectors.toMap(
                            ManProcessStepCompleteRecordItem::getStepCompleteRecordItemSid, Function.identity()));
                    updateList.forEach(item->{
                        if (map.containsKey(item.getStepCompleteRecordItemSid())) {
                            manProcessStepCompleteRecordItemMapper.updateAllById(item); // 全量更新
                            //插入日志
                            MongodbUtil.updateItemUserLog(item.getStepCompleteRecordItemSid(), record.getHandleStatus(),
                                    map.get(item.getStepCompleteRecordItemSid()), item, TITLE);
                        }
                    });
                    // 删除行
                    List<ManProcessStepCompleteRecordItem> delList = oldList.stream().filter(o -> !updateSidList.contains(
                            o.getStepCompleteRecordItemSid())).collect(Collectors.toList());
                    deleteManProcessStepCompleteRecordItem(delList);
                }
            }
        }
        else {
            // 如果 请求明细 没有了，但是数据库有明细，则删除数据库的明细
            if (CollectionUtil.isNotEmpty(oldList)) {
                deleteManProcessStepCompleteRecordItem(oldList);
            }
        }
        return row;
    }

    /**
     * 变更商品道序完成量台账-明细
     *
     * @param manProcessStepCompleteRecordItem 商品道序完成量台账-明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeManProcessStepCompleteRecordItem(ManProcessStepCompleteRecordItem manProcessStepCompleteRecordItem) {
        ManProcessStepCompleteRecordItem response = manProcessStepCompleteRecordItemMapper.selectManProcessStepCompleteRecordItemById(manProcessStepCompleteRecordItem.getStepCompleteRecordItemSid());
        int row = manProcessStepCompleteRecordItemMapper.updateAllById(manProcessStepCompleteRecordItem);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(manProcessStepCompleteRecordItem.getStepCompleteRecordItemSid(), BusinessType.CHANGE.getValue(), response, manProcessStepCompleteRecordItem, TITLE);
        }
        return row;
    }

    /**
     * 批量删除商品道序完成量台账-明细
     *
     * @param stepCompleteRecordItemSids 需要删除的商品道序完成量台账-明细ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManProcessStepCompleteRecordItemByIds(List<Long> stepCompleteRecordItemSids) {
        List<ManProcessStepCompleteRecordItem> list = manProcessStepCompleteRecordItemMapper.selectList(new QueryWrapper<ManProcessStepCompleteRecordItem>()
                .lambda().in(ManProcessStepCompleteRecordItem::getStepCompleteRecordItemSid, stepCompleteRecordItemSids));
        int row = manProcessStepCompleteRecordItemMapper.deleteBatchIds(stepCompleteRecordItemSids);
        if (row > 0) {
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new ManProcessStepCompleteRecordItem());
                MongodbUtil.insertUserLog(o.getStepCompleteRecordItemSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }


    /**
     * 批量删除商品道序完成量台账-明细
     *
     * @param itemList 需要删除的商品道序完成量台账-明细列表
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManProcessStepCompleteRecordItem(List<ManProcessStepCompleteRecordItem> itemList) {
        if (CollectionUtil.isEmpty(itemList)) {
            return 0;
        }
        List<Long> stepCompleteRecordItemSidList = itemList.stream().filter(o -> o.getStepCompleteRecordItemSid() != null)
                .map(ManProcessStepCompleteRecordItem::getStepCompleteRecordItemSid).collect(Collectors.toList());
        int row = 0;
        if (CollectionUtil.isNotEmpty(stepCompleteRecordItemSidList)) {
            row = manProcessStepCompleteRecordItemMapper.deleteBatchIds(stepCompleteRecordItemSidList);
            if (row > 0) {
                itemList.forEach(o -> {
                    List<OperMsg> msgList = new ArrayList<>();
                    msgList = BeanUtils.eq(o, new SalSalesIntentOrderItem());
                    MongodbUtil.insertUserLog(o.getStepCompleteRecordItemSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
                });
            }
        }
        return row;
    }

    /**
     * 批量删除商品道序完成量台账-明细 根据主表sids
     *
     * @param orderSids 需要删除的商品道序完成量台账sids
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteManProcessStepCompleteRecordItemByRecordIds(List<Long> recordSids) {
        List<ManProcessStepCompleteRecordItem> itemList = manProcessStepCompleteRecordItemMapper.selectList(new QueryWrapper<ManProcessStepCompleteRecordItem>()
                .lambda().in(ManProcessStepCompleteRecordItem::getStepCompleteRecordSid, recordSids));
        int row = 0;
        if (CollectionUtil.isNotEmpty(itemList)) {
            row = this.deleteManProcessStepCompleteRecordItem(itemList);
        }
        return row;
    }

    /**
     * 明细按款显示
     */
    @Override
    public ManProcessStepCompleteRecordTableResponse itemTable(ManProcessStepCompleteRecordTableRequest request) {
        ManProcessStepCompleteRecordTableResponse response = new ManProcessStepCompleteRecordTableResponse();
        BeanCopyUtils.copyProperties(request, response);
        // 商品道序 明细
        List<PayProductProcessStepItem> stepItemList = payProductProcessStepItemMapper.selectPayProductProcessStepItemList(new PayProductProcessStepItem()
                .setProductSid(request.getProductSid()).setPlantSid(request.getPlantSid()).setDepartment(request.getDepartment())
                .setJixinWangongType(request.getJixinWangongType()).setProductPriceType(response.getProductPriceType())
                .setHandleStatus(ConstantsEms.CHECK_STATUS));
        List<PayProcessStepCompleteTableStepResponse> stepResponseList = new ArrayList<>();
        stepResponseList = BeanCopyUtils.copyListProperties(stepItemList, PayProcessStepCompleteTableStepResponse::new);
        // 排序
        stepResponseList = stepResponseList.stream()
                .sorted(Comparator.comparing(PayProcessStepCompleteTableStepResponse::getSort, Comparator.nullsFirst(BigDecimal::compareTo))).collect(toList());
        response.setStepItemList(stepResponseList);
        // 存放 员工 + 道序序号 作为 键， 完成量 作为 值
        HashMap<String, BigDecimal> staffSortMaps = new HashMap<>();
        // 存放 每个道序序号 对应 完成量的值， 会用来做小计
        HashMap<String, BigDecimal> sortQuantity = new HashMap<>();
        // 当前表单明细中符合所选中商品编码和排产批次号的行数据
        List<ManProcessStepCompleteRecordItem> currentItemList = request.getStepCompleteRecordItemList().stream().filter(
                o -> o.getProductSid().equals(request.getProductSid()) && (
                        (o.getPaichanBatch() != null && o.getPaichanBatch().equals(request.getPaichanBatch())) ||
                                (o.getPaichanBatch() == null && request.getPaichanBatch() == null)
                )
        ).collect(toList());
        // 保持序号小数位一致性
        DecimalFormat df = new DecimalFormat("####.##");
        String sort = null;
        for (PayProcessStepCompleteTableStepResponse step : stepResponseList) {
            sort = df.format(step.getSort());
            // 道序序号  -> 完成量
            if (sortQuantity.get(String.valueOf(sort)) == null) {
                sortQuantity.put(String.valueOf(sort), null);
            }
        }
        if (CollectionUtil.isNotEmpty(currentItemList)) {
            for (ManProcessStepCompleteRecordItem item : currentItemList) {
                if (item.getSort() != null) {
                    sort = df.format(item.getSort());
                }
                // 员工 + 道序序号  ->  完成量
                if (staffSortMaps.get(item.getWorkerSid()+"-"+String.valueOf(sort)) == null) {
                    staffSortMaps.put(item.getWorkerSid()+"-"+String.valueOf(sort), item.getCompleteQuantity());
                }
                // 道序序号  ->  完成量
                if (!sortQuantity.containsKey(String.valueOf(sort))) {
                    sortQuantity.put(String.valueOf(sort), item.getCompleteQuantity());
                }
                else {
                    BigDecimal total = sortQuantity.get(String.valueOf(sort));
                    if (total == null) {
                        total = BigDecimal.ZERO;
                    }
                    total = total.add(item.getCompleteQuantity()==null?BigDecimal.ZERO:item.getCompleteQuantity());
                    sortQuantity.put(String.valueOf(sort), total);
                }
            }
        }
        // 表格要展示的 员工
        List<PayProcessStepCompleteTableStaffResponse> tableStaffList = new ArrayList<>();
        tableStaffList = BeanCopyUtils.copyListProperties(currentItemList, PayProcessStepCompleteTableStaffResponse::new);
        tableStaffList = tableStaffList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                new TreeSet<>(Comparator.comparing(PayProcessStepCompleteTableStaffResponse::getWorkerSid))), ArrayList::new));
        // 写入每行员工 的 每个 道序序号 对应 的完成量
        List<PayProcessStepCompleteTableQuantityResponse> stepQuantityList = new ArrayList<>();
        stepQuantityList = BeanCopyUtils.copyListProperties(stepResponseList, PayProcessStepCompleteTableQuantityResponse::new);
        sort = null;
        for (PayProcessStepCompleteTableStaffResponse staff : tableStaffList) {
            // 声明每个员工自己的道序对应完成量的内存空间，保证每个员工的道序对应完成量都是自己独立内存空间
            List<PayProcessStepCompleteTableQuantityResponse> quantityList = new ArrayList<>();
            quantityList = BeanCopyUtils.copyListProperties(stepQuantityList, PayProcessStepCompleteTableQuantityResponse::new);
            for (PayProcessStepCompleteTableQuantityResponse item : quantityList) {
                if (item.getSort() != null) {
                    sort = df.format(item.getSort());
                }
                item.setCompleteQuantity(staffSortMaps.get(staff.getWorkerSid()+"-"+String.valueOf(sort)));
            }
            staff.setQuantityList(quantityList);
        }
        // 排序
        tableStaffList = tableStaffList.stream()
                .sorted(Comparator.comparing(PayProcessStepCompleteTableStaffResponse::getWorkerName, Collator.getInstance(Locale.CHINA))).collect(toList());
        // 小计
        PayProcessStepCompleteTableStaffResponse xiaoji = new PayProcessStepCompleteTableStaffResponse();
        xiaoji.setWorkerName("小计");
        List<PayProcessStepCompleteTableQuantityResponse> xiaojiQuantity = new ArrayList<>();
        sortQuantity.forEach((key, value) -> {
            PayProcessStepCompleteTableQuantityResponse step = new PayProcessStepCompleteTableQuantityResponse();
            step.setSort(new BigDecimal(key));
            step.setCompleteQuantity(value);
            xiaojiQuantity.add(step);
        });
        xiaoji.setQuantityList(xiaojiQuantity.stream()
                .sorted(Comparator.comparing(PayProcessStepCompleteTableQuantityResponse::getSort, Comparator.nullsFirst(BigDecimal::compareTo))).collect(toList()));
        tableStaffList.add(xiaoji);
        response.setStaffList(tableStaffList);
        return response;
    }

}
