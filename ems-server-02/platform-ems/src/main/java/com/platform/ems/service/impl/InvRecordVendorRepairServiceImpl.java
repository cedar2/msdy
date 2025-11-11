package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.exception.CustomException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.InvRecordVendorRepair;
import com.platform.ems.domain.InvRecordVendorRepairAttach;
import com.platform.ems.domain.InvRecordVendorRepairItem;
import com.platform.ems.domain.dto.request.InvRecordVendorRepairRequest;
import com.platform.ems.domain.dto.response.InvRecordVendorRepairResponse;
import com.platform.ems.mapper.InvRecordVendorRepairAttachMapper;
import com.platform.ems.mapper.InvRecordVendorRepairItemMapper;
import com.platform.ems.mapper.InvRecordVendorRepairMapper;
import com.platform.ems.service.IInvRecordVendorRepairService;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 供应商返修台账Service业务层处理
 *
 * @author linhongwei
 * @date 2021-10-27
 */
@Service
@SuppressWarnings("all")
public class InvRecordVendorRepairServiceImpl extends ServiceImpl<InvRecordVendorRepairMapper,InvRecordVendorRepair>  implements IInvRecordVendorRepairService {
    @Autowired
    private InvRecordVendorRepairMapper invRecordVendorRepairMapper;
    @Autowired
    private InvRecordVendorRepairItemMapper itemMapper;
    @Autowired
    private InvRecordVendorRepairAttachMapper attachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;

    private static final String TITLE_NAME = "s_inv_record_vendor_repair";
    private static final String TITLE = "供应商返修台账";
    /**
     * 查询供应商返修台账
     *
     * @param vendorRepairSid 供应商返修台账ID
     * @return 供应商返修台账
     */
    @Override
    public InvRecordVendorRepair selectInvRecordVendorRepairById(Long vendorRepairSid) {
        InvRecordVendorRepair invRecordVendorRepair = invRecordVendorRepairMapper.selectInvRecordVendorRepairById(vendorRepairSid);
        List<InvRecordVendorRepairItem> invRecordVendorRepairItems = itemMapper.selectInvRecordVendorRepairItemList(new InvRecordVendorRepairItem().setVendorRepairSid(vendorRepairSid));
        List<InvRecordVendorRepairItem> items = sort(invRecordVendorRepairItems, null);
        invRecordVendorRepair.setListItem(items);
        List<InvRecordVendorRepairAttach> invRecordVendorRepairAttaches = attachMapper.selectInvRecordVendorRepairAttachList(new InvRecordVendorRepairAttach().setVendorRepairSid(vendorRepairSid));
        invRecordVendorRepair.setListAttach(invRecordVendorRepairAttaches);
        MongodbUtil.find(invRecordVendorRepair);
        return  invRecordVendorRepair;
    }
    @Override
    public List<InvRecordVendorRepairItem> sort(List<InvRecordVendorRepairItem> items, String type){
        if(CollectionUtil.isNotEmpty(items)){
            List<InvRecordVendorRepairItem> skuExit = items.stream().filter(li -> li.getSku2Name() != null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(skuExit)){
                //对尺码排序
                if (CollectionUtil.isNotEmpty(skuExit)) {
                    skuExit.forEach(li -> {
                        String skuName = li.getSku2Name();
                        String[] nameSplit = skuName.split("/");
                        if (nameSplit.length == 1) {
                            li.setFirstSort(nameSplit[0].replaceAll("[a-zA-Z]", ""));
                        } else {
                            String[] name2split = nameSplit[1].split("\\(");
                            if (name2split.length == 2) {
                                li.setSecondSort(name2split[0].replaceAll("[a-zA-Z]", ""));

                                li.setThirdSort(name2split[1].replaceAll("[a-zA-Z]", ""));
                            } else {
                                li.setSecondSort(nameSplit[1].replaceAll("[a-zA-Z]", ""));
                            }
                            li.setFirstSort(nameSplit[0].replaceAll("[a-zA-Z]", ""));
                        }
                    });
                    List<InvRecordVendorRepairItem> allList = new ArrayList<>();
                    List<InvRecordVendorRepairItem> allThirdList = new ArrayList<>();
                    List<InvRecordVendorRepairItem> sortThird = skuExit.stream().filter(li -> li.getThirdSort() != null).collect(Collectors.toList());
                    List<InvRecordVendorRepairItem> sortThirdNull = skuExit.stream().filter(li -> li.getThirdSort() == null).collect(Collectors.toList());
                    sortThird = sortThird.stream().sorted(Comparator.comparing(li -> li.getThirdSort())).collect(Collectors.toList());
                    allThirdList.addAll(sortThird);
                    allThirdList.addAll(sortThirdNull);
                    List<InvRecordVendorRepairItem> sort = allThirdList.stream().filter(li -> li.getSecondSort() != null).collect(Collectors.toList());
                    sort = sort.stream().sorted(Comparator.comparing(li -> Integer.valueOf(li.getSecondSort()))).collect(Collectors.toList());
                    List<InvRecordVendorRepairItem> sortNull = allThirdList.stream().filter(li -> li.getSecondSort() == null).collect(Collectors.toList());
                    allList.addAll(sort);
                    allList.addAll(sortNull);
                    skuExit = allList.stream().sorted(Comparator.comparing(item -> Double.valueOf(item.getFirstSort()))
                    ).collect(Collectors.toList());
                }
            }
            List<InvRecordVendorRepairItem> skuExitNo = items.stream().filter(li -> li.getSku2Name() == null).collect(Collectors.toList());
            ArrayList<InvRecordVendorRepairItem> itemArrayListAll = new ArrayList<>();
            itemArrayListAll.addAll(skuExit);
            itemArrayListAll.addAll(skuExitNo);
            if(ConstantsEms.YES.equals(type)){
                items=itemArrayListAll.stream().sorted(Comparator.comparing(InvRecordVendorRepairItem::getMaterialCode)
                        .thenComparing(InvRecordVendorRepairItem::getSku1Name)
                ).collect(Collectors.toList());
            }else{
                items=itemArrayListAll.stream().sorted(Comparator.comparing(InvRecordVendorRepairItem::getMaterialCode)
                        .thenComparing(InvRecordVendorRepairItem::getSku1Name)
                ).collect(Collectors.toList());
            }
            return items;
        }
        return new ArrayList<>();
    }
    @Override
    public int judgeRepeat(List<InvRecordVendorRepair> list){
        list.forEach(item->{
            List<InvRecordVendorRepair> itemList = invRecordVendorRepairMapper.getItemList(item);
            if(CollectionUtils.isNotEmpty(itemList)){
                Long vendorRepairCode = itemList.get(0).getVendorRepairCode();
                throw  new CustomException("供应商返修台账"+vendorRepairCode+"已存在物料/商品,"+item.getMaterialName()+"，不能添加！");
            }
        });
        return 1;
    }
    /**
     * 查询供应商返修台账列表
     *
     * @param invRecordVendorRepair 供应商返修台账
     * @return 供应商返修台账
     */
    @Override
    public List<InvRecordVendorRepair> selectInvRecordVendorRepairList(InvRecordVendorRepair invRecordVendorRepair) {
        return invRecordVendorRepairMapper.selectInvRecordVendorRepairList(invRecordVendorRepair);
    }

    /**
     * 查询供应商返修台账明细报表
     *
     * @param invRecordVendorRepair 供应商返修台账
     * @return 供应商返修台账
     */
    @Override
    public List<InvRecordVendorRepairResponse> report(InvRecordVendorRepairRequest invRecordVendorRepair) {
        return itemMapper.report(invRecordVendorRepair);
    }
    /**
     * 新增供应商返修台账
     * 需要注意编码重复校验
     * @param invRecordVendorRepair 供应商返修台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInvRecordVendorRepair(InvRecordVendorRepair invRecordVendorRepair) {
        setConfim(invRecordVendorRepair);
        int row= invRecordVendorRepairMapper.insert(invRecordVendorRepair);
        if(row>0){
            List<InvRecordVendorRepairItem> listItem = invRecordVendorRepair.getListItem();
            setItemNum(listItem);
            //明细
            insertItem(listItem, invRecordVendorRepair);
            List<InvRecordVendorRepairAttach> listAttach = invRecordVendorRepair.getListAttach();
            //附件
            insertAttah( listAttach, invRecordVendorRepair);
            //待办通知
            InvRecordVendorRepair note = invRecordVendorRepairMapper.selectById(invRecordVendorRepair.getVendorRepairSid());
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(invRecordVendorRepair.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(TITLE_NAME)
                        .setDocumentSid(invRecordVendorRepair.getVendorRepairSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("供应商返修台账:" + note.getVendorRepairCode() + ",当前是保存状态，请及时处理！")
                            .setDocumentCode(note.getVendorRepairCode().toString())
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            } else {
                //校验是否存在待办
                checkTodoExist(note);
            }
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbUtil.insertUserLog(invRecordVendorRepair.getVendorRepairSid(), BusinessType.INSERT.getValue(), msgList,TITLE);
            if(ConstantsEms.CHECK_STATUS.equals(invRecordVendorRepair.getHandleStatus())){
                MongodbUtil.insertUserLog(invRecordVendorRepair.getVendorRepairSid(), BusinessType.CHECK.getValue(), msgList,TITLE);
            }
        }
        return row;
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(InvRecordVendorRepair note) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, note.getVendorRepairSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, note.getVendorRepairSid()));
        }
    }

    public void insertItem(List<InvRecordVendorRepairItem> listItem,InvRecordVendorRepair invRecordVendorRepair){
        itemMapper.delete(new QueryWrapper<InvRecordVendorRepairItem>()
                .lambda()
                .eq(InvRecordVendorRepairItem::getVendorRepairSid, invRecordVendorRepair.getVendorRepairSid())
        );
        if (CollectionUtil.isNotEmpty(listItem)) {
            listItem.forEach(item->{
                if(ConstantsEms.CHECK_STATUS.equals(invRecordVendorRepair.getHandleStatus())){
                    if(item.getPlanReturnDate()==null||item.getRepairQuantity()==null){
                        throw  new CustomException("确认时，返修量和计划退还日期不允许为空");
                    }
                    if(item.getReturnQuantity()==null||item.getReturnQuantity().compareTo(BigDecimal.ZERO)==0){
                        item.setReturnStatus(ConstantsEms.RETURN_W);
                    }else if(item.getReturnQuantity().compareTo(item.getRepairQuantity())==0){
                        item.setReturnStatus(ConstantsEms.RETURN_Q);
                    }else{
                        item.setReturnStatus(ConstantsEms.RETURN_B);
                    }
                    if(item.getReturnQuantity()!=null&&item.getRepairQuantity()!=null){
                        if(item.getReturnQuantity().compareTo(item.getRepairQuantity())==1){
                            throw  new CustomException("确认时，退还量不能大于返修量");
                        }
                    }
                }
                item.setVendorRepairSid(invRecordVendorRepair.getVendorRepairSid());
            });
            itemMapper.inserts(listItem);
        }
    }

    public void insertAttah(List<InvRecordVendorRepairAttach> listAttach,InvRecordVendorRepair invRecordVendorRepair){
        attachMapper.delete(new QueryWrapper<InvRecordVendorRepairAttach>()
        .lambda()
                .eq(InvRecordVendorRepairAttach::getVendorRepairSid,invRecordVendorRepair.getVendorRepairSid())
        );
        if(CollectionUtil.isNotEmpty(listAttach)){
            listAttach.forEach(item->{
                item.setVendorRepairSid(invRecordVendorRepair.getVendorRepairSid());
            });
        }
    }
    /**
     * 行号赋值
     */
    public void  setItemNum(List<InvRecordVendorRepairItem> list){
        int size = list.size();
        if(size>0){
            for (int i=1;i<=size;i++){
                list.get(i-1).setItemNum(Long.valueOf(i));
            }
        }
    }
    /**
     * 修改供应商返修台账
     *
     * @param invRecordVendorRepair 供应商返修台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInvRecordVendorRepair(InvRecordVendorRepair invRecordVendorRepair) {
        InvRecordVendorRepair repair = invRecordVendorRepairMapper.selectById(invRecordVendorRepair.getVendorRepairSid());
        setConfim(invRecordVendorRepair);
        int row=invRecordVendorRepairMapper.updateById(invRecordVendorRepair);
        if(row>0){
            List<InvRecordVendorRepairItem> listItem = invRecordVendorRepair.getListItem();
            if (CollectionUtils.isNotEmpty(listItem)) {
                if(ConstantsEms.CHECK_STATUS.equals(invRecordVendorRepair.getHandleStatus())){
                    listItem.forEach(item->{
                        if(item.getPlanReturnDate()==null||item.getRepairQuantity()==null){
                            throw  new CustomException("确认时，返修量和计划退还日期不允许为空");
                        }
                        if(item.getReturnQuantity()==null||item.getReturnQuantity().compareTo(BigDecimal.ZERO)==0){
                            item.setReturnStatus(ConstantsEms.RETURN_W);
                        }else if(item.getReturnQuantity().compareTo(item.getRepairQuantity())==0){
                            item.setReturnStatus(ConstantsEms.RETURN_Q);
                        }else{
                            item.setReturnStatus(ConstantsEms.RETURN_B);
                        }
                        if(item.getReturnQuantity()!=null&&item.getRepairQuantity()!=null){
                            if(item.getReturnQuantity().compareTo(item.getRepairQuantity())==1){
                                throw  new CustomException("确认时，退还量不能大于返修量");
                            }
                        }
                        item.setVendorRepairSid(invRecordVendorRepair.getVendorRepairSid());
                    });
                }
                setItemNum(listItem);
                List<InvRecordVendorRepairItem> invRecordVendorRepairItems = itemMapper.selectList(new QueryWrapper<InvRecordVendorRepairItem>().lambda()
                        .eq(InvRecordVendorRepairItem::getVendorRepairSid, invRecordVendorRepair.getVendorRepairSid())
                );
                List<Long> longs = invRecordVendorRepairItems.stream().map(li -> li.getVendorRepairItemSid()).collect(Collectors.toList());
                List<Long> longsNow = listItem.stream().map(li -> li.getVendorRepairItemSid()).collect(Collectors.toList());
                //两个集合取差集
                List<Long> reduce = longs.stream().filter(item -> !longsNow.contains(item)).collect(Collectors.toList());
                //删除明细
                if(CollectionUtil.isNotEmpty(reduce)){
                    List<InvRecordVendorRepairItem> reduceList = itemMapper.selectList(new QueryWrapper<InvRecordVendorRepairItem>().lambda()
                            .in(InvRecordVendorRepairItem::getVendorRepairItemSid, reduce)
                    );
                    itemMapper.deleteBatchIds(reduce);
                }
                //修改明细
                List<InvRecordVendorRepairItem> exitItem = listItem.stream().filter(li -> li.getVendorRepairItemSid() != null).collect(Collectors.toList());
                if(CollectionUtil.isNotEmpty(exitItem)){
                    exitItem.forEach(li->{
                        itemMapper.updateById(li);
                    });
                }
                //新增明细
                List<InvRecordVendorRepairItem> nullItem = listItem.stream().filter(li -> li.getVendorRepairItemSid() == null).collect(Collectors.toList());
                if(CollectionUtil.isNotEmpty(nullItem)){
                    nullItem.forEach(li->{
                        li.setVendorRepairSid(invRecordVendorRepair.getVendorRepairSid());
                        itemMapper.insert(li);
                    });
                }

            }else{
                itemMapper.delete(new QueryWrapper<InvRecordVendorRepairItem>().lambda()
                .eq(InvRecordVendorRepairItem::getVendorRepairSid,invRecordVendorRepair.getVendorRepairSid())
                );
            }
            List<InvRecordVendorRepairAttach> listAttach = invRecordVendorRepair.getListAttach();
            //附件
            insertAttah( listAttach, invRecordVendorRepair);
            if (!ConstantsEms.SAVA_STATUS.equals(invRecordVendorRepair.getHandleStatus())) {
                //校验是否存在待办
                checkTodoExist(invRecordVendorRepair);
            }
            String bussinessTyes=repair.getHandleStatus().equals(ConstantsEms.SAVA_STATUS)?"编辑":"变更";
            //插入日志
            MongodbUtil.insertUserLog(invRecordVendorRepair.getVendorRepairSid(), bussinessTyes,TITLE);
            if(ConstantsEms.CHECK_STATUS.equals(invRecordVendorRepair.getHandleStatus())){
                MongodbUtil.insertUserLog(invRecordVendorRepair.getVendorRepairSid(), BusinessType.CHECK.getValue(), TITLE);
            }
        }
        return row;
    }

    public void setConfim(InvRecordVendorRepair invRecordVendorRepair){
        String handleStatus = invRecordVendorRepair.getHandleStatus();
        if(ConstantsEms.CHECK_STATUS.equals(handleStatus)){
            invRecordVendorRepair.setConfirmDate(new Date())
                    .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
    }
    /**
     * 变更供应商返修台账
     *
     * @param invRecordVendorRepair 供应商返修台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeInvRecordVendorRepair(InvRecordVendorRepair invRecordVendorRepair) {
        InvRecordVendorRepair response = invRecordVendorRepairMapper.selectInvRecordVendorRepairById(invRecordVendorRepair.getVendorRepairSid());
                                                                                    int row=invRecordVendorRepairMapper.updateAllById(invRecordVendorRepair);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(invRecordVendorRepair.getVendorRepairSid(), BusinessType.CHANGE.ordinal(), response,invRecordVendorRepair,TITLE);
        }
        return row;
    }

    /**
     * 批量删除供应商返修台账
     *
     * @param vendorRepairSids 需要删除的供应商返修台账ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInvRecordVendorRepairByIds(List<Long> vendorRepairSids) {
        int row = invRecordVendorRepairMapper.deleteBatchIds(vendorRepairSids);
        if(row>0){
            itemMapper.delete(new QueryWrapper<InvRecordVendorRepairItem>()
                    .lambda()
                    .in(InvRecordVendorRepairItem::getVendorRepairSid, vendorRepairSids)
            );
            attachMapper.delete(new QueryWrapper<InvRecordVendorRepairAttach>()
                    .lambda()
                    .in(InvRecordVendorRepairAttach::getVendorRepairSid,vendorRepairSids)
            );
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, vendorRepairSids));
        }
        return row;
    }

    /**
    * 启用/停用
    * @param invRecordVendorRepair
    * @return
    */
    @Override
    public int changeStatus(InvRecordVendorRepair invRecordVendorRepair){
        int row=0;
        return row;
    }


    /**
     *更改确认状态
     * @param invRecordVendorRepair
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(InvRecordVendorRepair invRecordVendorRepair){
        int row=0;
        Long[] sids=invRecordVendorRepair.getVendorRepairSidList();
        if(sids!=null&&sids.length>0){
            row=invRecordVendorRepairMapper.update(null,new UpdateWrapper<InvRecordVendorRepair>().lambda()
                    .set(InvRecordVendorRepair::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .set(InvRecordVendorRepair::getConfirmerAccount,ApiThreadLocalUtil.get().getUsername())
                    .set(InvRecordVendorRepair::getConfirmDate,new Date())
                    .in(InvRecordVendorRepair::getVendorRepairSid,sids));
            for(Long id:sids){
                InvRecordVendorRepair repair = new InvRecordVendorRepair();
                repair.setVendorRepairSid(id);
                //校验是否存在待办
                checkTodoExist(repair);
                //插入日志
                List<InvRecordVendorRepairItem> invRecordVendorRepairItems = itemMapper.selectList(new QueryWrapper<InvRecordVendorRepairItem>().lambda()
                        .eq(InvRecordVendorRepairItem::getVendorRepairSid, id)
                );
                if(CollectionUtil.isNotEmpty(invRecordVendorRepairItems)){
                    invRecordVendorRepairItems.forEach(item->{
                        if(item.getPlanReturnDate()==null||item.getRepairQuantity()==null){
                            throw  new CustomException("确认时，返修量和计划退还日期不允许为空");
                        }
                        if(item.getReturnQuantity()==null||item.getReturnQuantity().compareTo(BigDecimal.ZERO)==0){
                            item.setReturnStatus(ConstantsEms.RETURN_W);
                        }else if(item.getReturnQuantity().compareTo(item.getRepairQuantity())==0){
                            item.setReturnStatus(ConstantsEms.RETURN_Q);
                        }else{
                            item.setReturnStatus(ConstantsEms.RETURN_B);
                        }
                        if(item.getReturnQuantity()!=null&&item.getRepairQuantity()!=null){
                            if(item.getReturnQuantity().compareTo(item.getRepairQuantity())==1){
                                throw  new CustomException("确认时，退还量不能大于返修量");
                            }
                        }
                        itemMapper.updateById(item);
                    });
                }else{
                    throw  new CustomException("确认时，明细行不允许为空");
                }
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(),TITLE);
            }
        }
        return row;
    }


}
