package com.platform.ems.service.impl;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.InvRecordCustomerRepairRequest;
import com.platform.ems.domain.dto.response.InvRecordCustomerRepairResponse;
import com.platform.ems.mapper.InvRecordCustomerRepairAttachMapper;
import com.platform.ems.mapper.InvRecordCustomerRepairItemMapper;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.ems.mapper.InvRecordCustomerRepairMapper;
import com.platform.ems.service.IInvRecordCustomerRepairService;

import javax.validation.constraints.NotEmpty;

/**
 * 客户返修台账Service业务层处理
 *
 * @author linhongwei
 * @date 2021-10-27
 */
@Service
@SuppressWarnings("all")
public class InvRecordCustomerRepairServiceImpl extends ServiceImpl<InvRecordCustomerRepairMapper,InvRecordCustomerRepair>  implements IInvRecordCustomerRepairService {
    @Autowired
    private InvRecordCustomerRepairMapper invRecordCustomerRepairMapper;
    @Autowired
    private InvRecordCustomerRepairItemMapper itemMapper;
    @Autowired
    private InvRecordCustomerRepairAttachMapper attachMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;


    private static final String TITLE = "客户返修台账";
    private static final String TITLE_NAME = "s_inv_record_customer_repair";
    /**
     * 查询客户返修台账
     *
     * @param customerRepairSid 客户返修台账ID
     * @return 客户返修台账
     */
    @Override
    public InvRecordCustomerRepair selectInvRecordCustomerRepairById(Long customerRepairSid) {
        InvRecordCustomerRepair invRecordCustomerRepair = invRecordCustomerRepairMapper.selectInvRecordCustomerRepairById(customerRepairSid);
        List<InvRecordCustomerRepairItem> invRecordCustomerRepairItems = itemMapper.selectInvRecordCustomerRepairItemList(new InvRecordCustomerRepairItem().setCustomerRepairSid(customerRepairSid));
        List<InvRecordCustomerRepairItem> items = sort(invRecordCustomerRepairItems,null);
        invRecordCustomerRepair.setListItem(items);
        List<InvRecordCustomerRepairAttach> invRecordCustomerRepairAttaches = attachMapper.selectInvRecordCustomerRepairAttachList(new InvRecordCustomerRepairAttach().setCustomerRepairSid(customerRepairSid));
        invRecordCustomerRepair.setListAttach(invRecordCustomerRepairAttaches);
        MongodbUtil.find(invRecordCustomerRepair);
        return  invRecordCustomerRepair;
    }
    @Override
    public List<InvRecordCustomerRepairItem> sort(List<InvRecordCustomerRepairItem> items, String type){
        if(CollectionUtil.isNotEmpty(items)){
            List<InvRecordCustomerRepairItem> skuExit = items.stream().filter(li -> li.getSku2Name() != null).collect(Collectors.toList());
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
                    List<InvRecordCustomerRepairItem> allList = new ArrayList<>();
                    List<InvRecordCustomerRepairItem> allThirdList = new ArrayList<>();
                    List<InvRecordCustomerRepairItem> sortThird = skuExit.stream().filter(li -> li.getThirdSort() != null).collect(Collectors.toList());
                    List<InvRecordCustomerRepairItem> sortThirdNull = skuExit.stream().filter(li -> li.getThirdSort() == null).collect(Collectors.toList());
                    sortThird = sortThird.stream().sorted(Comparator.comparing(li -> li.getThirdSort())).collect(Collectors.toList());
                    allThirdList.addAll(sortThird);
                    allThirdList.addAll(sortThirdNull);
                    List<InvRecordCustomerRepairItem> sort = allThirdList.stream().filter(li -> li.getSecondSort() != null).collect(Collectors.toList());
                    sort = sort.stream().sorted(Comparator.comparing(li -> Integer.valueOf(li.getSecondSort()))).collect(Collectors.toList());
                    List<InvRecordCustomerRepairItem> sortNull = allThirdList.stream().filter(li -> li.getSecondSort() == null).collect(Collectors.toList());
                    allList.addAll(sort);
                    allList.addAll(sortNull);
                    skuExit = allList.stream().sorted(Comparator.comparing(item -> Double.valueOf(item.getFirstSort()))
                    ).collect(Collectors.toList());
                }
            }
            List<InvRecordCustomerRepairItem> skuExitNo = items.stream().filter(li -> li.getSku2Name() == null).collect(Collectors.toList());
            ArrayList<InvRecordCustomerRepairItem> itemArrayListAll = new ArrayList<>();
            itemArrayListAll.addAll(skuExit);
            itemArrayListAll.addAll(skuExitNo);
            if(ConstantsEms.YES.equals(type)){
                items=itemArrayListAll.stream().sorted(Comparator.comparing(InvRecordCustomerRepairItem::getMaterialCode)
                        .thenComparing(InvRecordCustomerRepairItem::getSku1Name)
                ).collect(Collectors.toList());
            }else{
                items=itemArrayListAll.stream().sorted(Comparator.comparing(InvRecordCustomerRepairItem::getMaterialCode)
                        .thenComparing(InvRecordCustomerRepairItem::getSku1Name)
                ).collect(Collectors.toList());
            }
            return items;
        }
        return new ArrayList<>();
    }

    /**
     * 查询客户返修台账列表
     *
     * @param invRecordCustomerRepair 客户返修台账
     * @return 客户返修台账
     */
    @Override
    public List<InvRecordCustomerRepair> selectInvRecordCustomerRepairList(InvRecordCustomerRepair invRecordCustomerRepair) {
        return invRecordCustomerRepairMapper.selectInvRecordCustomerRepairList(invRecordCustomerRepair);
    }

    /**
     * 查询客户返修台账明细报表
     *
     * @param invRecordCustomerRepair 客户返修台账
     * @return 客户返修台账
     */
    @Override
    public List<InvRecordCustomerRepairResponse> report(InvRecordCustomerRepairRequest invRecordCustomerRepair) {
        return itemMapper.report(invRecordCustomerRepair);
    }

    /**
     * 新增客户返修台账
     * 需要注意编码重复校验
     * @param invRecordCustomerRepair 客户返修台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertInvRecordCustomerRepair(InvRecordCustomerRepair invRecordCustomerRepair) {
        setConfim(invRecordCustomerRepair);
        int row= invRecordCustomerRepairMapper.insert(invRecordCustomerRepair);
        if(row>0){
            //明细
            List<InvRecordCustomerRepairItem> listItem = invRecordCustomerRepair.getListItem();
            setItemNum(listItem);
            insertItem(listItem, invRecordCustomerRepair);
            //附件
            List<InvRecordCustomerRepairAttach> listAttach = invRecordCustomerRepair.getListAttach();
            insertAttah(listAttach, invRecordCustomerRepair);
        }
        //待办通知
        InvRecordCustomerRepair note = invRecordCustomerRepairMapper.selectById(invRecordCustomerRepair.getCustomerRepairSid());
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(invRecordCustomerRepair.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName(TITLE_NAME)
                    .setDocumentSid(invRecordCustomerRepair.getCustomerRepairSid());
            List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
            if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                sysTodoTask.setTitle("客户返修台账:" + note.getCustomerRepairCode() + ",当前是保存状态，请及时处理！")
                        .setDocumentCode(note.getCustomerRepairCode().toString())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
        } else {
            //校验是否存在待办
            checkTodoExist(note);
        }
        //插入日志
        MongodbUtil.insertUserLog(invRecordCustomerRepair.getCustomerRepairSid(), BusinessType.INSERT.getValue(),TITLE);
        if(ConstantsEms.CHECK_STATUS.equals(invRecordCustomerRepair.getHandleStatus())){
            MongodbUtil.insertUserLog(invRecordCustomerRepair.getCustomerRepairSid(), BusinessType.CHECK.getValue(),TITLE);
        }
        return row;
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(InvRecordCustomerRepair note) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, note.getCustomerRepairSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, note.getCustomerRepairSid()));
        }
    }
    public void insertItem(List<InvRecordCustomerRepairItem> listItem,InvRecordCustomerRepair invRecordCustomerRepair){
        itemMapper.delete(new QueryWrapper<InvRecordCustomerRepairItem>()
                .lambda()
                .eq(InvRecordCustomerRepairItem::getCustomerRepairSid, invRecordCustomerRepair.getCustomerRepairSid())
        );
        if (CollectionUtil.isNotEmpty(listItem)) {
            listItem.forEach(item->{
                if(ConstantsEms.CHECK_STATUS.equals(invRecordCustomerRepair.getHandleStatus())){
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
                item.setCustomerRepairSid(invRecordCustomerRepair.getCustomerRepairSid());
            });
            itemMapper.inserts(listItem);
        }
    }

    public void insertAttah(List<InvRecordCustomerRepairAttach> listAttach,InvRecordCustomerRepair invRecordCustomerRepair){
        attachMapper.delete(new QueryWrapper<InvRecordCustomerRepairAttach>()
                .lambda()
                .eq(InvRecordCustomerRepairAttach::getCustomerRepairSid,invRecordCustomerRepair.getCustomerRepairSid())
        );
        if(CollectionUtil.isNotEmpty(listAttach)){
            listAttach.forEach(item->{
                item.setCustomerRepairSid(invRecordCustomerRepair.getCustomerRepairSid());
            });
        }
    }

    /**
     * 行号赋值
     */
    public void  setItemNum(List<InvRecordCustomerRepairItem> list){
        int size = list.size();
        if(size>0){
            for (int i=1;i<=size;i++){
                list.get(i-1).setItemNum(Long.valueOf(i));
            }
        }
    }
    /**
     * 修改客户返修台账
     *
     * @param invRecordCustomerRepair 客户返修台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateInvRecordCustomerRepair(InvRecordCustomerRepair invRecordCustomerRepair) {
        InvRecordCustomerRepair response = invRecordCustomerRepairMapper.selectInvRecordCustomerRepairById(invRecordCustomerRepair.getCustomerRepairSid());
        setConfim(invRecordCustomerRepair);
        int row=invRecordCustomerRepairMapper.updateById(invRecordCustomerRepair);
        if(row>0){
            //明细
            List<InvRecordCustomerRepairItem> listItem = invRecordCustomerRepair.getListItem();
            if (CollectionUtils.isNotEmpty(listItem)) {
                if(ConstantsEms.CHECK_STATUS.equals(invRecordCustomerRepair.getHandleStatus())){
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
                        item.setCustomerRepairSid(invRecordCustomerRepair.getCustomerRepairSid());
                    });
                }
                setItemNum(listItem);
                List<InvRecordCustomerRepairItem> invRecordCustomerRepairItems = itemMapper.selectList(new QueryWrapper<InvRecordCustomerRepairItem>().lambda()
                        .eq(InvRecordCustomerRepairItem::getCustomerRepairSid, invRecordCustomerRepair.getCustomerRepairSid())
                );
                List<Long> longs = invRecordCustomerRepairItems.stream().map(li -> li.getCustomerRepairItemSid()).collect(Collectors.toList());
                List<Long> longsNow = listItem.stream().map(li -> li.getCustomerRepairItemSid()).collect(Collectors.toList());
                //两个集合取差集
                List<Long> reduce = longs.stream().filter(item -> !longsNow.contains(item)).collect(Collectors.toList());
                //删除明细
                if(CollectionUtil.isNotEmpty(reduce)){
                    List<InvRecordCustomerRepairItem> reduceList = itemMapper.selectList(new QueryWrapper<InvRecordCustomerRepairItem>().lambda()
                            .in(InvRecordCustomerRepairItem::getCustomerRepairItemSid, reduce)
                    );
                    itemMapper.deleteBatchIds(reduce);
                }
                //修改明细
                List<InvRecordCustomerRepairItem> exitItem = listItem.stream().filter(li -> li.getCustomerRepairItemSid() != null).collect(Collectors.toList());
                if(CollectionUtil.isNotEmpty(exitItem)){
                    exitItem.forEach(li->{
                        itemMapper.updateById(li);
                    });
                }
                //新增明细
                List<InvRecordCustomerRepairItem> nullItem = listItem.stream().filter(li -> li.getCustomerRepairItemSid() == null).collect(Collectors.toList());
                if(CollectionUtil.isNotEmpty(nullItem)){
                    nullItem.forEach(li->{
                        li.setCustomerRepairSid(invRecordCustomerRepair.getCustomerRepairSid());
                        itemMapper.insert(li);
                    });
                }
            }else{
                itemMapper.delete(new QueryWrapper<InvRecordCustomerRepairItem>().lambda()
                .eq(InvRecordCustomerRepairItem::getCustomerRepairSid,invRecordCustomerRepair.getCustomerRepairSid())
                );
            }
            //附件
            List<InvRecordCustomerRepairAttach> listAttach = invRecordCustomerRepair.getListAttach();
            insertAttah(listAttach, invRecordCustomerRepair);
            if (!ConstantsEms.SAVA_STATUS.equals(invRecordCustomerRepair.getHandleStatus())) {
                //校验是否存在待办
                checkTodoExist(invRecordCustomerRepair);
            }
            String bussinessTyes=response.getHandleStatus().equals(ConstantsEms.SAVA_STATUS)?"编辑":"变更";
            //插入日志
            MongodbUtil.insertUserLog(invRecordCustomerRepair.getCustomerRepairSid(), bussinessTyes, response,invRecordCustomerRepair,TITLE);
            if(ConstantsEms.CHECK_STATUS.equals(invRecordCustomerRepair.getHandleStatus())){
                MongodbUtil.insertUserLog(invRecordCustomerRepair.getCustomerRepairSid(), BusinessType.CHECK.getValue(), TITLE);
            }
        }
        return row;
    }
     public void setConfim(InvRecordCustomerRepair invRecordCustomerRepair){
         String handleStatus = invRecordCustomerRepair.getHandleStatus();
         if(ConstantsEms.CHECK_STATUS.equals(handleStatus)){
             invRecordCustomerRepair.setConfirmDate(new Date())
                     .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
         }
     }

    /**
     * 变更客户返修台账
     *
     * @param invRecordCustomerRepair 客户返修台账
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeInvRecordCustomerRepair(InvRecordCustomerRepair invRecordCustomerRepair) {
        InvRecordCustomerRepair response = invRecordCustomerRepairMapper.selectInvRecordCustomerRepairById(invRecordCustomerRepair.getCustomerRepairSid());
                                                                                    int row=invRecordCustomerRepairMapper.updateAllById(invRecordCustomerRepair);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(invRecordCustomerRepair.getCustomerRepairSid(), BusinessType.CHANGE.ordinal(), response,invRecordCustomerRepair,TITLE);
        }
        return row;
    }

    /**
     * 批量删除客户返修台账
     *
     * @param customerRepairSids 需要删除的客户返修台账ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteInvRecordCustomerRepairByIds(List<Long> customerRepairSids) {
        int row = invRecordCustomerRepairMapper.deleteBatchIds(customerRepairSids);
        if(row>0){
            itemMapper.delete(new QueryWrapper<InvRecordCustomerRepairItem>()
                    .lambda()
                    .in(InvRecordCustomerRepairItem::getCustomerRepairSid, customerRepairSids)
            );
            attachMapper.delete(new QueryWrapper<InvRecordCustomerRepairAttach>()
                    .lambda()
                    .in(InvRecordCustomerRepairAttach::getCustomerRepairSid,customerRepairSids)
            );
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, customerRepairSids));
        }
        return row ;
    }

    /**
    * 启用/停用
    * @param invRecordCustomerRepair
    * @return
    */
    @Override
    public int changeStatus(InvRecordCustomerRepair invRecordCustomerRepair){
        int row=0;
        return row;
    }

    @Override
    public int judgeRepeat(List<InvRecordCustomerRepair> list){
        list.forEach(item->{
            List<InvRecordCustomerRepair> itemList = invRecordCustomerRepairMapper.getItemList(item);
            if(CollectionUtils.isNotEmpty(itemList)){
                Long customerRepairCode = itemList.get(0).getCustomerRepairCode();
                throw  new CustomException("客户返修台账"+customerRepairCode+",已存在物料/商品"+item.getMaterialName()+"，不能添加！");
            }
        });
        return 1;
    }

    /**
     *更改确认状态
     * @param invRecordCustomerRepair
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(InvRecordCustomerRepair invRecordCustomerRepair){
        int row=0;
        Long[] sids=invRecordCustomerRepair.getCustomerRepairSidList();
        if(sids!=null&&sids.length>0){
            row=invRecordCustomerRepairMapper.update(null,new UpdateWrapper<InvRecordCustomerRepair>().lambda()
                    .set(InvRecordCustomerRepair::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .set(InvRecordCustomerRepair::getConfirmDate,new Date())
                    .set(InvRecordCustomerRepair::getConfirmerAccount,ApiThreadLocalUtil.get().getUsername())
                    .in(InvRecordCustomerRepair::getCustomerRepairSid,sids));
            for(Long id:sids){
                InvRecordCustomerRepair repair = new InvRecordCustomerRepair();
                repair.setCustomerRepairSid(id);
                //校验是否存在待办
                checkTodoExist(repair);
                List<InvRecordCustomerRepairItem> invRecordCustomerRepairItems = itemMapper.selectList(new QueryWrapper<InvRecordCustomerRepairItem>().lambda()
                        .eq(InvRecordCustomerRepairItem::getCustomerRepairSid, id)
                );
                if(CollectionUtil.isNotEmpty(invRecordCustomerRepairItems)){
                    invRecordCustomerRepairItems.forEach(item->{
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
                //插入日志
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), TITLE);
            }
        }
        return row;
    }


}
