package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.request.SamSampleLendreturnItemRequest;
import com.platform.ems.domain.dto.response.SamSampleLendreturnReportResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IInvInventoryDocumentService;
import com.platform.ems.service.ISamSampleLendreturnService;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
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
 * 样品借还单-主Service业务层处理
 *
 * @author linhongwei
 * @date 2021-12-20
 */
@Service
@SuppressWarnings("all")
public class SamSampleLendreturnServiceImpl extends ServiceImpl<SamSampleLendreturnMapper,SamSampleLendreturn>  implements ISamSampleLendreturnService {
    @Autowired
    private SamSampleLendreturnMapper samSampleLendreturnMapper;
    @Autowired
    private SamSampleLendreturnItemMapper itemMapper;
    @Autowired
    private SamSampleLendreturnAttachMapper attachMapper;
    @Autowired
    private IInvInventoryDocumentService invInventoryDocumentService;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private InvStorehouseMaterialMapper invStorehouseMaterialMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private InvInventoryLocationMapper invInventoryLocationMapper;
    private static final String table = "s_sam_sample_lendreturn";


    private static final String TITLE = "样品借还单-主";
    /**
     * 查询样品借还单-主
     *
     * @param lendreturnSid 样品借还单-主ID
     * @return 样品借还单-主
     */
    @Override
    public SamSampleLendreturn selectSamSampleLendreturnById(Long lendreturnSid) {
        SamSampleLendreturn samSampleLendreturn = samSampleLendreturnMapper.selectSamSampleLendreturnById(lendreturnSid);
        List<SamSampleLendreturnItem> samSampleLendreturnItems = itemMapper.selectSamSampleLendreturnItemById(lendreturnSid);
        List<SamSampleLendreturnAttach> samSampleLendreturnAttaches = attachMapper.selectSamSampleLendreturnAttachById(lendreturnSid);
        samSampleLendreturn.setListSamSampleLendreturnItem(samSampleLendreturnItems);
        samSampleLendreturn.setListSamSampleLendreturnAttach(samSampleLendreturnAttaches);
        String documentType = samSampleLendreturn.getDocumentType();
        if(CollectionUtil.isNotEmpty(samSampleLendreturnItems)){
            if(ConstantsEms.SAMPLE_G.equals(documentType)||ConstantsEms.SAMPLE_Y.equals(documentType)){
                //归还\遗失
                samSampleLendreturnItems.forEach(item->{
                    //借出量
                    SamSampleLendreturnItem samSampleLendreturnItem = itemMapper.selectOne(new QueryWrapper<SamSampleLendreturnItem>().lambda()
                            .eq(SamSampleLendreturnItem::getLendreturnItemSid, item.getPreLendreturnSid())
                    );
                    int sumJ = samSampleLendreturnItem.getQuantity();
                    item.setQuantityJ(sumJ);
                    //归还计算
                    SamSampleLendreturnItem sampleLendreturnItem = new SamSampleLendreturnItem();
                    //已归还量
                    sampleLendreturnItem.setPreLendreturnSid(item.getPreLendreturnSid())
                            .setHandleStatusList(new String[]{HandleStatus.CONFIRMED.getCode(),HandleStatus.SUBMIT.getCode()});
                    List<SamSampleLendreturnItem> itemsAll = itemMapper.getItemHandle(sampleLendreturnItem);
                    int itemsYGCheckCount=0;
                    if(CollectionUtil.isNotEmpty(itemsAll)){
                        List<SamSampleLendreturnItem> itemsYG = itemsAll.stream().filter(li -> li.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode())).collect(Collectors.toList());
                        if(CollectionUtil.isNotEmpty(itemsYG)){
                            int sumY = itemsYG.stream().mapToInt(li -> li.getQuantity().intValue()).sum();
                            item.setQuantityYG(sumY);
                        }
                        //待归还量
                        itemsYGCheckCount = itemsAll.stream().mapToInt(li -> li.getQuantity().intValue()).sum();
                        //归还中量
                        List<SamSampleLendreturnItem> itemsGHZ = itemsAll.stream().filter(li -> li.getHandleStatus().equals(HandleStatus.SUBMIT.getCode())).collect(Collectors.toList());
                        if(CollectionUtil.isNotEmpty(itemsGHZ)){
                            int sumGHZ= itemsGHZ.stream().mapToInt(li -> li.getQuantity().intValue()).sum();
                            item.setQuantityGHZ(sumGHZ);
                        }

                    }
                    item.setQuantityDG(sumJ-itemsYGCheckCount);
                });
            }else{
                samSampleLendreturnItems.forEach(item->{
                    SamSampleLendreturnItem sampleLendreturnItem = new SamSampleLendreturnItem();
                    sampleLendreturnItem.setPreLendreturnSid(item.getLendreturnItemSid())
                            .setHandleStatusList(new String[]{HandleStatus.CONFIRMED.getCode(),HandleStatus.SUBMIT.getCode()});
                    List<SamSampleLendreturnItem> list = itemMapper.getItemHandle(sampleLendreturnItem);
                    if(CollectionUtil.isEmpty(list)){
                        //未借还
                        item.setReturnStatus("WGH");
                    }else{
                        List<SamSampleLendreturnItem> itemsCheck = list.stream().filter(li -> li.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode())).collect(Collectors.toList());
                        if(CollectionUtil.isEmpty(itemsCheck)){
                            //借还中
                            item.setReturnStatus("BFGH");
                        }else{
                            //借还中
                            int sum = itemsCheck.stream().mapToInt(li -> li.getQuantity().intValue()).sum();
                            if(item.getQuantity()==sum){
                                //已借还
                                item.setReturnStatus("QBGH");
                            }else{
                                //借还中
                                item.setReturnStatus("BFGH");
                            }
                        }
                    }
                });
            }
        }
        MongodbUtil.find(samSampleLendreturn);
        return  samSampleLendreturn;
    }

    /**
     * 行号赋值
     */
    public void  setItemNum(List<SamSampleLendreturnItem> list){
        int size = list.size();
        if(size>0){
            for (int i=1;i<=size;i++){
                list.get(i-1).setItemNum(i);
            }
        }
    }
    @Override
    public int processCheck(List<Long> sidList){
        sidList.forEach(item->{
            SamSampleLendreturn samSampleLendreturn = selectSamSampleLendreturnById(item);
            String documentType = samSampleLendreturn.getDocumentType();
            List<SamSampleLendreturnItem> listSamSampleLendreturnItem = samSampleLendreturn.getListSamSampleLendreturnItem();
            if(CollectionUtil.isEmpty(listSamSampleLendreturnItem)){
                throw  new CustomException("明细不能为空");
            }
            if(ConstantsEms.SAMPLE_J.equals(documentType)){
                listSamSampleLendreturnItem.forEach(li->{
                    if(li.getQuantity()==null||li.getPlanReturnDate()==null){
                        throw  new CustomException("行号为"+li.getItemNum()+"的明细借出量/计划归还日期不能为空！");
                    }
                    InvInventoryLocation invInventoryLocation = invInventoryLocationMapper.selectOne(new QueryWrapper<InvInventoryLocation>().lambda()
                            .eq(InvInventoryLocation::getBarcodeSid, li.getBarcodeSid())
                            .eq(InvInventoryLocation::getStorehouseLocationSid, samSampleLendreturn.getStorehouseLocationSid())
                            .eq(InvInventoryLocation::getStorehouseSid, samSampleLendreturn.getStorehouseSid())
                    );
                    if(invInventoryLocation==null){
                        throw  new CustomException("行号为"+li.getItemNum()+"的明细没有对应的库存，请核实！");
                    }else{
                        BigDecimal unlimitedQuantity = invInventoryLocation.getUnlimitedQuantity();
                        Integer quantity = li.getQuantity();
                        BigDecimal value = BigDecimal.valueOf(quantity);
                        if(value.compareTo(unlimitedQuantity)==1){
                            throw  new CustomException("行号为"+li.getItemNum()+"的明细库存量不足，请核实！");
                        }
                    }
                });
            }else{
                listSamSampleLendreturnItem.forEach(li->{
                    if(li.getQuantity()==null){
                        if(ConstantsEms.SAMPLE_G.equals(documentType)){
                            throw  new CustomException("行号为"+li.getItemNum()+"的明细本次归还量不能为空！");
                        }
                        if(ConstantsEms.SAMPLE_Y.equals(documentType)){
                            throw  new CustomException("行号为"+li.getItemNum()+"的明细本次异常量不能为空！");
                        }
                    }
                });
            }
        });
        return  1;
    }
    //获取添加明细信息
    @Override
    public List<SamSampleLendreturnItem> getSamSampleLendreturnItem(SamSampleLendreturnItem samSampleLendreturn){
        List<SamSampleLendreturnItem> samSampleLendreturnItems = itemMapper.selectSamSampleLendreturnItemList(samSampleLendreturn);
        samSampleLendreturnItems=samSampleLendreturnItems.stream()
                .sorted(Comparator.comparing(SamSampleLendreturnItem::getLendreturnCode).reversed())
                .collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(samSampleLendreturnItems)){
            //归还\遗失
            samSampleLendreturnItems.forEach(item->{
                //借出量
                item.setQuantityJ(item.getQuantity());
                //归还计算
                SamSampleLendreturnItem sampleLendreturnItem = new SamSampleLendreturnItem();
                //已归还量
                sampleLendreturnItem.setPreLendreturnSid(item.getLendreturnItemSid())
                        .setHandleStatusList(new String[]{HandleStatus.CONFIRMED.getCode(),HandleStatus.SUBMIT.getCode()});
                List<SamSampleLendreturnItem> itemsAll = itemMapper.getItemHandle(sampleLendreturnItem);
                int itemsYGCheckCount=0;
                if(CollectionUtil.isNotEmpty(itemsAll)){
                    List<SamSampleLendreturnItem> itemsYG = itemsAll.stream().filter(li -> li.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)).collect(Collectors.toList());
                    if(CollectionUtil.isNotEmpty(itemsYG)){
                        int sumY = itemsYG.stream().mapToInt(li -> li.getQuantity().intValue()).sum();
                        item.setQuantityYG(sumY);
                    }
                    //待归还量
                        itemsYGCheckCount = itemsAll.stream().mapToInt(li -> li.getQuantity().intValue()).sum();

                }else{
                    item.setQuantityYG(0);
                }
                item.setQuantityDG(item.getQuantity()-itemsYGCheckCount);
                if(item.getQuantityYG()==item.getQuantity()){
                    item.setReturnStatus("QBGH");
                }else{
                    if(item.getQuantityDG()==item.getQuantity()){
                        item.setReturnStatus("WGH");
                    }else{
                        item.setReturnStatus("BFGH");
                    }
                }
                item.setQuantity(null);
            });
        }
        return samSampleLendreturnItems;
    }
    //借还单明细报表
    @Override
    public List<SamSampleLendreturnReportResponse> getReport(SamSampleLendreturn samSampleLendreturn){
        List<SamSampleLendreturnReportResponse> list = itemMapper.reportList(samSampleLendreturn);
        //计算数量
        if(CollectionUtil.isNotEmpty(list)){
                //归还\遗失
            list.forEach(item->{
                String documentType = item.getDocumentType();
                if(ConstantsEms.SAMPLE_J.equals(documentType)){
                    //借出量
                    item.setQuantityJ(item.getQuantity());
                    //归还计算
                    SamSampleLendreturnItem sampleLendreturnItem = new SamSampleLendreturnItem();
                    //已归还量
                    sampleLendreturnItem.setPreLendreturnSid(item.getLendreturnItemSid())
                            .setHandleStatusList(new String[]{HandleStatus.CONFIRMED.getCode(),HandleStatus.SUBMIT.getCode()});
                    List<SamSampleLendreturnItem> itemsAll = itemMapper.getItemHandle(sampleLendreturnItem);
                    int itemsYGCheckCount=0;
                    if(CollectionUtil.isNotEmpty(itemsAll)){
                        List<SamSampleLendreturnItem> itemsYG = itemsAll.stream().filter(li -> li.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)).collect(Collectors.toList());
                        if(CollectionUtil.isNotEmpty(itemsYG)){
                            int sumY = itemsYG.stream().mapToInt(li -> li.getQuantity().intValue()).sum();
                            item.setQuantityYG(sumY);
                        }
                        //待归还量
                        itemsYGCheckCount = itemsAll.stream().mapToInt(li -> li.getQuantity().intValue()).sum();

                    }else{
                        item.setQuantityYG(null);
                    }
                    if(item.getQuantity()!=null){
                        item.setQuantityDG(item.getQuantity()-itemsYGCheckCount);
                    }
                }
                });
        }
        return list;
    }
@Override
    public List<SamSampleLendreturnItem> getPrice(SamSampleLendreturnItemRequest data){
        List<SamSampleLendreturnItem> listSamSampleLendreturnItem = data.getListSamSampleLendreturnItem();
        Long storehouseSid = data.getStorehouseSid();
        listSamSampleLendreturnItem.forEach(item->{
            InvStorehouseMaterial material = invStorehouseMaterialMapper.selectOne(new QueryWrapper<InvStorehouseMaterial>().lambda()
                    .eq(InvStorehouseMaterial::getBarcodeSid, item.getBarcodeSid())
                    .eq(InvStorehouseMaterial::getStorehouseSid, storehouseSid)
            );
            if(material!=null){
                item.setPrice(material.getPrice());
            }
        });
        return listSamSampleLendreturnItem;
    }

    /**
     * 查询样品借还单-主列表
     *
     * @param samSampleLendreturn 样品借还单-主
     * @return 样品借还单-主
     */
    @Override
    public List<SamSampleLendreturn> selectSamSampleLendreturnList(SamSampleLendreturn samSampleLendreturn) {
        return samSampleLendreturnMapper.selectSamSampleLendreturnList(samSampleLendreturn);
    }

    /**
     * 新增样品借还单-主
     * 需要注意编码重复校验
     * @param samSampleLendreturn 样品借还单-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSamSampleLendreturn(SamSampleLendreturn samSampleLendreturn) {
        String documentType = samSampleLendreturn.getDocumentType();
        String handleStatus = samSampleLendreturn.getHandleStatus();
        List<SamSampleLendreturnItem> listSamSampleLendreturnItem = samSampleLendreturn.getListSamSampleLendreturnItem();
        if(ConstantsEms.SAMPLE_J.equals(documentType)){
            if(samSampleLendreturn.getLender()==null){
                samSampleLendreturn.setLender(ApiThreadLocalUtil.get().getUsername());
            }
            if(CollectionUtil.isNotEmpty(listSamSampleLendreturnItem)) {
                listSamSampleLendreturnItem.forEach(li -> {
                    if (li.getVendorSid() != null && li.getCustomerSid() != null) {
                        throw new CustomException("供应商和客户，不允许同时填值");
                    }
                });
            }
        }else{
            if(CollectionUtil.isNotEmpty(listSamSampleLendreturnItem)){
                listSamSampleLendreturnItem.forEach(li->{
                    if(li.getQuantity()!=null){
                        if(li.getQuantity()>li.getQuantityDG()){
                            if(ConstantsEms.SAMPLE_G.equals(documentType)){
                                throw  new  CustomException("本次归还量不能大于待归还量");
                            }else{
                                throw  new  CustomException("不允许存在本次异常量大于待归还量");
                            }
                        }
                    }
                });
            }
            if(samSampleLendreturn.getReturner()==null){
                samSampleLendreturn.setReturner(ApiThreadLocalUtil.get().getUsername());
            }
        }
        int row= samSampleLendreturnMapper.insert(samSampleLendreturn);
        if(row>0){
            if(CollectionUtil.isNotEmpty(listSamSampleLendreturnItem)){
                listSamSampleLendreturnItem.forEach(li->{
                    li.setLendreturnSid(samSampleLendreturn.getLendreturnSid());
                    if(li.getLendreturnItemSid()!=null){
                        li.setPreLendreturnSid(li.getLendreturnItemSid());
                        li.setLendreturnItemSid(null);
                    }
                });
                setItemNum(listSamSampleLendreturnItem);
                itemMapper.inserts(listSamSampleLendreturnItem);
            }
            List<SamSampleLendreturnAttach> listSamSampleLendreturnAttach = samSampleLendreturn.getListSamSampleLendreturnAttach();
            if(CollectionUtil.isNotEmpty(listSamSampleLendreturnAttach)){
                listSamSampleLendreturnAttach.forEach(li->{
                    li.setLendreturnSid(samSampleLendreturn.getLendreturnSid());
                });
                attachMapper.inserts(listSamSampleLendreturnAttach);
            }
            //待办通知
            SamSampleLendreturn sampleLendreturn = samSampleLendreturnMapper.selectById(samSampleLendreturn.getLendreturnSid());
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(sampleLendreturn.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(table)
                        .setDocumentSid(sampleLendreturn.getLendreturnSid());
                List<SysTodoTask> sysTodoTaskList = sysTodoTaskMapper.selectSysTodoTaskList(sysTodoTask);
                if (CollectionUtil.isEmpty(sysTodoTaskList)) {
                    sysTodoTask.setTitle("样品借还单" + sampleLendreturn.getLendreturnCode() + "当前是保存状态，请及时处理！")
                            .setDocumentCode(sampleLendreturn.getLendreturnCode().toString())
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
            } else {
                //校验是否存在待办
                checkTodoExist(sampleLendreturn);
            }
            //插入日志
            MongodbUtil.insertUserLog(samSampleLendreturn.getLendreturnSid(),  BusinessType.INSERT.getValue(),TITLE);
        }
        return row;
    }

    /**
     * 修改样品借还单-主
     *
     * @param samSampleLendreturn 样品借还单-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateSamSampleLendreturn(SamSampleLendreturn samSampleLendreturn) {
        SamSampleLendreturn response = samSampleLendreturnMapper.selectSamSampleLendreturnById(samSampleLendreturn.getLendreturnSid());
        int row=samSampleLendreturnMapper.updateById(samSampleLendreturn);
        String documentType = samSampleLendreturn.getDocumentType();
        List<SamSampleLendreturnItem> itemList= samSampleLendreturn.getListSamSampleLendreturnItem();
        if(!ConstantsEms.SAMPLE_J.equals(documentType)){
            if(CollectionUtil.isNotEmpty(itemList)){
                itemList.forEach(li->{
                    if(li.getQuantity()!=null){
                        if(li.getQuantity()>li.getQuantityDG()){
                            if(ConstantsEms.SAMPLE_G.equals(documentType)){
                                throw  new  CustomException("本次归还量不能大于待归还量");
                            }else{
                                throw  new  CustomException("不允许存在本次异常量大于待归还量");
                            }
                        }
                    }
                });
            }
        }
        if(CollectionUtil.isNotEmpty(itemList)){
            itemList.forEach(li->{
                if(li.getVendorSid()!=null&&li.getCustomerSid()!=null){
                    throw  new  CustomException("供应商和客户，不允许同时填值");
                }
            });
            setItemNum(itemList);
            List<SamSampleLendreturnItem> samSampleLendreturnItem = itemMapper.selectList(new QueryWrapper<SamSampleLendreturnItem>().lambda()
                    .eq(SamSampleLendreturnItem::getLendreturnSid, samSampleLendreturn.getLendreturnSid())
            );
            List<Long> longs = samSampleLendreturnItem.stream().map(li -> li.getLendreturnItemSid()).collect(Collectors.toList());
            List<Long> longsNow = itemList.stream().map(li -> li.getLendreturnItemSid()).collect(Collectors.toList());
            //两个集合取差集
            List<Long> reduce = longs.stream().filter(item -> !longsNow.contains(item)).collect(Collectors.toList());
            //删除明细
            if(CollectionUtil.isNotEmpty(reduce)){
                List<SamSampleLendreturnItem> reduceList = itemMapper.selectList(new QueryWrapper<SamSampleLendreturnItem>().lambda()
                        .in(SamSampleLendreturnItem::getLendreturnItemSid, reduce)
                );
                itemMapper.deleteBatchIds(reduce);
            }
            //修改明细
            List<SamSampleLendreturnItem> exitItem = itemList.stream().filter(li -> li.getLendreturnItemSid() != null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(exitItem)){
                exitItem.forEach(li->{
                    //借还
                    if(!li.getLendreturnSid().toString().equals(samSampleLendreturn.getLendreturnSid().toString())){
                        li.setLendreturnSid(samSampleLendreturn.getLendreturnSid())
                                .setPreLendreturnSid(li.getLendreturnItemSid())
                                .setLendreturnItemSid(null);
                    }else{
                        itemMapper.updateAllById(li);
                    }
                });
            }
            //新增明细
            List<SamSampleLendreturnItem> nullItem = itemList.stream().filter(li -> li.getLendreturnItemSid() == null).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(nullItem)){
                for (int i = 0; i < nullItem.size(); i++) {
                    nullItem.get(i).setLendreturnSid(samSampleLendreturn.getLendreturnSid());
                    itemMapper.insert(nullItem.get(i));
                }
            }
        }else{
            itemMapper.delete(new QueryWrapper<SamSampleLendreturnItem>().lambda()
            .eq(SamSampleLendreturnItem::getLendreturnSid,samSampleLendreturn.getLendreturnSid())
            );
        }
        List<SamSampleLendreturnAttach> listSamSampleLendreturnAttach = samSampleLendreturn.getListSamSampleLendreturnAttach();
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(samSampleLendreturn.getLendreturnSid(), BusinessType.UPDATE.getValue(), response,samSampleLendreturn,TITLE);
        }
        return row;
    }

    /**
     * 校验是否存在待办
     */
    private void checkTodoExist(SamSampleLendreturn samSampleLendreturn) {
        List<SysTodoTask> todoTaskList = sysTodoTaskMapper.selectList(new QueryWrapper<SysTodoTask>().lambda()
                .eq(SysTodoTask::getDocumentSid, samSampleLendreturn.getLendreturnSid()));
        if (CollectionUtil.isNotEmpty(todoTaskList)) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, samSampleLendreturn.getLendreturnSid()));
        }
    }
    /**
     * 变更样品借还单-主
     *
     * @param samSampleLendreturn 样品借还单-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeSamSampleLendreturn(SamSampleLendreturn samSampleLendreturn) {
        SamSampleLendreturn response = samSampleLendreturnMapper.selectSamSampleLendreturnById(samSampleLendreturn.getLendreturnSid());
                                                                                int row=samSampleLendreturnMapper.updateAllById(samSampleLendreturn);
        if(row>0){
            //插入日志
            MongodbUtil.insertUserLog(samSampleLendreturn.getLendreturnSid(), BusinessType.CHANGE.ordinal(), response,samSampleLendreturn,TITLE);
        }
        return row;
    }

    /**
     * 批量删除样品借还单-主
     *
     * @param lendreturnSids 需要删除的样品借还单-主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteSamSampleLendreturnByIds(List<Long> lendreturnSids) {
        int  row=  samSampleLendreturnMapper.deleteBatchIds(lendreturnSids);
        if(row>0){
            itemMapper.delete(new QueryWrapper<SamSampleLendreturnItem>()
            .lambda()
                    .in(SamSampleLendreturnItem::getLendreturnSid,lendreturnSids)
            );
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, lendreturnSids));
        }
        return row;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDOTo(List<Long> lendreturnSids) {
        int row = sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid, lendreturnSids));
        return row;
    }

    /**
    * 启用/停用
    * @param samSampleLendreturn
    * @return
    */
    @Override
    public int changeStatus(SamSampleLendreturn samSampleLendreturn){
        int row=0;
        return row;
    }


    /**
     *更改确认状态
     * @param samSampleLendreturn
     * @return
     */
    @Override
    public int check(SamSampleLendreturn samSampleLendreturn){
        int row=0;
        Long[] sids=samSampleLendreturn.getLendreturnSidList();
        if(sids!=null&&sids.length>0){
            row=samSampleLendreturnMapper.update(null,new UpdateWrapper<SamSampleLendreturn>().lambda().set(SamSampleLendreturn::getHandleStatus ,ConstantsEms.CHECK_STATUS)
                    .in(SamSampleLendreturn::getLendreturnSid,sids));
            for(Long id:sids){
                SamSampleLendreturn lendreturn = selectSamSampleLendreturnById(id);
                String documentType = lendreturn.getDocumentType();
                InvInventoryDocument invInventoryDocument = new InvInventoryDocument();
                BeanCopyUtils.copyProperties(lendreturn, invInventoryDocument);
                invInventoryDocument.setCreatorAccount(null)
                        .setCreateDate(null)
                        .setUpdateDate(null)
                        .setReferDocCategory("SLRN")
                        .setUpdaterAccount(null)
                        .setRemark(null);
                List<SamSampleLendreturnItem> listSamSampleLendreturnItem = lendreturn.getListSamSampleLendreturnItem();
               List<InvInventoryDocumentItem> invInventoryDocumentItems = new ArrayList<>();
                listSamSampleLendreturnItem.forEach(li->{
                    InvInventoryDocumentItem invInventoryDocumentItem = new InvInventoryDocumentItem();
                    BeanCopyUtils.copyProperties(li, invInventoryDocumentItem);
                    invInventoryDocumentItem.setMaterialSid(li.getSampleSid());
                    invInventoryDocumentItem.setQuantity(BigDecimal.valueOf(li.getQuantity()));
                    invInventoryDocumentItem.setRemark(null);
                    invInventoryDocumentItem.setCreatorAccount(null)
                            .setCreateDate(null)
                            .setUpdateDate(null)
                            .setUpdaterAccount(null);
                    invInventoryDocumentItems.add(invInventoryDocumentItem);
                });
                invInventoryDocument.setInvInventoryDocumentItemList(invInventoryDocumentItems);
                //借出
                if (ConstantsEms.SAMPLE_J.equals(documentType)) {
                    invInventoryDocument.setType(ConstantsEms.CHU_KU);
                    invInventoryDocument.setDocumentCategory("CK");
                    invInventoryDocument.setMovementType("SC63");
                    invInventoryDocument.setReferDocumentCode(lendreturn.getLendreturnCode().toString());
                    invInventoryDocumentService.insertInvInventoryDocument(invInventoryDocument);
                } else if (ConstantsEms.SAMPLE_G.equals(documentType)) {
                    //归还
                    invInventoryDocument.setMovementType("SR63");
                    invInventoryDocument.setDocumentCategory("RK");
                    invInventoryDocument.setType(ConstantsEms.RU_KU);
                    invInventoryDocument.setReferDocumentCode(lendreturn.getLendreturnCode().toString());
                    invInventoryDocumentService.insertInvInventoryDocument(invInventoryDocument);
                } else {
                    //遗失

                }
            }
        }
        return row;
    }


}
