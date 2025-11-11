package com.platform.ems.service.impl;

import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.core.domain.document.UserOperLog;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.core.redis.RedisCache;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.AutoIdField;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.*;
import com.platform.ems.enums.Status;
import com.platform.ems.mapper.*;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.CodeRuleUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.service.IBasSkuGroupService;

/**
 * SKU组档案Service业务层处理
 *
 * @author linhongwei
 * @date 2021-03-22
 */
@Service
@SuppressWarnings("all")
public class BasSkuGroupServiceImpl extends ServiceImpl<BasSkuGroupMapper,BasSkuGroup>  implements IBasSkuGroupService {
    @Autowired
    private BasSkuGroupMapper basSkuGroupMapper;
    @Autowired
    private BasSkuMapper basSkuMapper;
    @Autowired
    private BasSkuGroupItemMapper itemMapper;
    @Autowired
    private BasMaterialMapper materialMapper;
    @Autowired
    private BasMaterialSkuMapper materialSkuMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private RedisCache redisService;

    private static final String TITLE = "SKU组档案";


    private static final String DATAOBJECT = "SKUGroup";

    private static String KEY = "";

    /**
     * 查询SKU组档案
     *
     * @param skuGroupSid SKU组档案ID
     * @return SKU组档案
     */
    @Override
    public BasSkuGroup selectBasSkuGroupById(Long skuGroupSid) {
        BasSkuGroup basSkuGroup=basSkuGroupMapper.selectBasSkuGroupById(skuGroupSid);
        if(basSkuGroup!=null){
            BasSkuGroupItem item=new BasSkuGroupItem();
            item.setSkuGroupSid(basSkuGroup.getSkuGroupSid());
            List<BasSkuGroupItem> itemList = itemMapper.selectBasSkuGroupItemListByNameSort(item);
            if (CollectionUtils.isNotEmpty(itemList)){
                if (ConstantsEms.SKUTYP_YS.equals(basSkuGroup.getSkuType())){
                    //对除了尺码外的sku按中文排序
                    Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
                    Collections.sort(itemList, new Comparator<BasSkuGroupItem>() {
                        @Override
                        public int compare(BasSkuGroupItem info1, BasSkuGroupItem info2) {
                            Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
                            return com.compare(info1.getSkuName(), info2.getSkuName());
                        }
                    });
                }else if (ConstantsEms.SKUTYP_CM.equals(basSkuGroup.getSkuType())){
//                itemList.sort(Comparator.comparing(BasSkuGroupItem::getSort,Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(BasSkuGroupItem::getSkuName));
                    //排序
                    itemList.forEach(li->{
                        String skuName = li.getSkuName();
                        String[] nameSplit = skuName.split("/");
                        if(nameSplit.length==1){
                            li.setFirstSort(nameSplit[0]);
                        }else{
                            String[] name2split = nameSplit[1].split("\\(");
                            if(name2split.length==2){
                                li.setSecondSort(name2split[0].replaceAll("[a-zA-Z]","" ));

                                li.setThirdSort(name2split[1]);
                            }else{
                                li.setSecondSort(nameSplit[1].replaceAll("[a-zA-Z]","" ));
                            }
                            li.setFirstSort(nameSplit[0]);
                        }
                    });
                    List<BasSkuGroupItem> allList = new ArrayList<>();
                    List<BasSkuGroupItem> allThirdList = new ArrayList<>();
                    List<BasSkuGroupItem> sortThird = itemList.stream().filter(li -> li.getThirdSort() != null).collect(Collectors.toList());
                    List<BasSkuGroupItem> sortThirdNull = itemList.stream().filter(li -> li.getThirdSort() == null).collect(Collectors.toList());
                    sortThird=sortThird.stream().sorted(Comparator.comparing(li->li.getThirdSort())).collect(Collectors.toList());
                    allThirdList.addAll(sortThird);
                    allThirdList.addAll(sortThirdNull);

                    List<BasSkuGroupItem> sort = allThirdList.stream().filter(li -> li.getSecondSort() != null).collect(Collectors.toList());
                    sort=sort.stream().sorted(Comparator.comparing(li->Integer.valueOf(li.getSecondSort()))).collect(Collectors.toList());
                    List<BasSkuGroupItem> sortNull = allThirdList.stream().filter(li -> li.getSecondSort() == null).collect(Collectors.toList());
                    allList.addAll(sort);
                    allList.addAll(sortNull);
                    itemList= allList.stream().sorted(Comparator.comparing(o -> o.getFirstSort())
                    ).collect(Collectors.toList());
                }
                List<String> skuIds = new ArrayList<>();
                itemList.forEach(i->{
                    skuIds.add(i.getSkuSid());
                });
                QueryWrapper<BasSku> skuListQ = new QueryWrapper<>();
                skuListQ.in("sku_sid",skuIds);
                List<BasSku> skuList = basSkuMapper.selectList(skuListQ);
                for(BasSku sku : skuList){
                    for(BasSkuGroupItem skuitem : itemList){
                        if(skuitem.getSkuSid().equals(String.valueOf(sku.getSkuSid()))){
                            skuitem.setStatus(sku.getStatus());
                            break;
                        }else{
                            continue;
                        }
                    }
                }
                itemList.sort(Comparator.comparing(BasSkuGroupItem::getSort,Comparator.nullsLast(Comparator.naturalOrder())));
                basSkuGroup.setItemList(itemList);
            }
            else {
                basSkuGroup.setItemList(new ArrayList<>());
            }
            //查询日志信息
            Query query = new Query();
            query.addCriteria(Criteria.where("sid").is(skuGroupSid));
            List<UserOperLog> userOperLogList = mongoTemplate.find(query, UserOperLog.class);
            basSkuGroup.setOperLogList(userOperLogList);
        }
        return basSkuGroup;
    }

    /**
     * 查询SKU组档案列表
     *
     * @param basSkuGroup SKU组档案
     * @return SKU组档案
     */
    @Override
    public List<BasSkuGroup> selectBasSkuGroupList(BasSkuGroup basSkuGroup) {
        return basSkuGroupMapper.selectBasSkuGroupList(basSkuGroup);
    }

    /**
     * 获取自动编码的编码
     * @param basSku
     */
    private void getCode(BasSkuGroup basSkuGroup){
        Map<String,String> map = CodeRuleUtil.allocation(DATAOBJECT,basSkuGroup.getSkuType());
        if (map == null || StrUtil.isBlank(map.get(AutoIdField.code))){
            throw new BaseException("编码配置有误，请联系管理员");
        } else {
            basSkuGroup.setSkuGroupCode(map.get(AutoIdField.code));
            KEY = map.get(AutoIdField.key_name);
            Map<String, Object> params = new HashMap<>();
            params.put("sku_group_code", basSkuGroup.getSkuGroupCode());
            List<BasSkuGroup> skuList = basSkuGroupMapper.selectByMap(params);
            if (skuList.size() > 0) {
                //编码已存在就在往下遍历
                getCode(basSkuGroup);
            }
        }
    }

    /**
     * 新增SKU组档案
     * 需要注意编码重复校验
     * @param basSkuGroup SKU组档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasSkuGroup(BasSkuGroup basSkuGroup) {
        int row = 0;
        try {
            if (StrUtil.isNotBlank(basSkuGroup.getSkuGroupCode())){
                List<BasSkuGroup> query1 = basSkuGroupMapper.selectList(new QueryWrapper<BasSkuGroup>()
                        .eq("sku_group_code", basSkuGroup.getSkuGroupCode()));
                if(query1.size()>0){
                    throw new BaseException("sku组编码重复，请查看");
                }
            }else {
                //自动编码
                getCode(basSkuGroup);
            }
            Map<String,Object> params=new HashMap<>();
            params.put("sku_group_name", basSkuGroup.getSkuGroupName());
            List<BasSkuGroup> query=basSkuGroupMapper.selectByMap(params);
            if(query.size()>0){
                throw new CustomException("sku组名重复，请查看");
            }

            if(ConstantsEms.CHECK_STATUS.equals(basSkuGroup.getHandleStatus())){
                List<BasSkuGroupItem> itemList = basSkuGroup.getItemList();
                if(CollectionUtils.isEmpty(itemList)){
                    throw new CustomException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
                }else{
                    List<String> stopList = new ArrayList<>();
                    for(BasSkuGroupItem item:itemList){
                        BasSku sku =  basSkuMapper.selectBasSkuById(Long.valueOf(item.getSkuSid()));
                        if(sku.getStatus().equals(Status.DISABLE.getCode())){
                            stopList.add(sku.getSkuName());
                        }
                    }
                    if(stopList.size()>0){
                        throw new BaseException("SKU档案："+stopList.toString()+"已停用，请核实！");
                    }
                }
                basSkuGroup.setConfirmDate(new Date());
                basSkuGroup.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            }
            row=basSkuGroupMapper.insert(basSkuGroup);
            if(row>0){
                List<BasSkuGroupItem> itemList=basSkuGroup.getItemList();
                if(itemList!=null&&itemList.size()>0){
                    for(BasSkuGroupItem item:itemList){
                        item.setSkuGroupSid(basSkuGroup.getSkuGroupSid());
                        item.setCreateDate(new Date());
                        item.setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                        itemMapper.insert(item);
                    }
                }
                //待办通知
                SysTodoTask sysTodoTask = new SysTodoTask();
                if (ConstantsEms.SAVA_STATUS.equals(basSkuGroup.getHandleStatus())) {
                    List<DictData> skuTypeDict = sysDictDataService.selectDictData("s_sku_type");
                    Map<String, String> skuTypeMaps = skuTypeDict.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                    sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                            .setTableName(ConstantsTable.TABLE_BAS_SKU_GROUP)
                            .setDocumentSid(basSkuGroup.getSkuGroupSid());
                    sysTodoTask.setTitle(skuTypeMaps.get(basSkuGroup.getSkuType()) + "组档案: " + basSkuGroup.getSkuGroupCode() + " 当前是保存状态，请及时处理！")
                            .setDocumentCode(String.valueOf(basSkuGroup.getSkuGroupCode()))
                            .setNoticeDate(new Date())
                            .setUserId(ApiThreadLocalUtil.get().getUserid());
                    sysTodoTaskMapper.insert(sysTodoTask);
                }
                //插入日志
                MongodbDeal.insert(basSkuGroup.getSkuGroupSid(), basSkuGroup.getHandleStatus(), null, TITLE, null);
            }
        }catch (Exception e){
            throw e;
        } finally {
            redisService.deleteObject(KEY);
        }
        return row;
    }

    /**
     * 修改SKU组档案
     *
     * @param basSkuGroup SKU组档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasSkuGroup(BasSkuGroup basSkuGroup) {
        BasSkuGroup old = selectBasSkuGroupById(basSkuGroup.getSkuGroupSid());
        Map<String,Object> queryParams=new HashMap<>();
        queryParams.put("sku_group_name", basSkuGroup.getSkuGroupName());
//        queryParams.put("sku_type", basSkuGroup.getSkuType());
        List<BasSkuGroup> queryResult=basSkuGroupMapper.selectByMap(queryParams);
        if(queryResult.size()>0){
            for(BasSkuGroup group:queryResult){
                if(group.getSkuGroupName().equals(basSkuGroup.getSkuGroupName())&&!group.getSkuGroupSid().equals(basSkuGroup.getSkuGroupSid())){
                    throw new CustomException("名称重复,请查看");
                }
            }
        }
        queryParams.clear();
        queryParams.put("sku_group_code", basSkuGroup.getSkuGroupCode());
//        queryParams.put("sku_type", basSkuGroup.getSkuType());
        List<BasSkuGroup> queryResult2=basSkuGroupMapper.selectByMap(queryParams);
        if(queryResult2.size()>0){
            for(BasSkuGroup group:queryResult2){
                if(group.getSkuGroupCode().equals(basSkuGroup.getSkuGroupCode())&&!group.getSkuGroupSid().equals(basSkuGroup.getSkuGroupSid())){
                    throw new CustomException("编码重复,请查看");
                }
            }
        }
        basSkuGroup.setUpdateDate(new Date());
        basSkuGroup.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        if(ConstantsEms.CHECK_STATUS.equals(basSkuGroup.getHandleStatus())){
            List<BasSkuGroupItem> itemList = basSkuGroup.getItemList();
            if(CollectionUtils.isEmpty(itemList)){
                throw new CustomException(ConstantsEms.CONFIRM_PROMPT_STATEMENT);
            }else{
                List<String> stopList = new ArrayList<>();
                for(BasSkuGroupItem item:itemList){
                    BasSku sku =  basSkuMapper.selectBasSkuById(Long.valueOf(item.getSkuSid()));
                    if(sku.getStatus().equals(Status.DISABLE.getCode())){
                        stopList.add(sku.getSkuName());
                    }
                }
                if(stopList.size()>0){
                    throw new BaseException("SKU档案："+stopList.toString()+"已停用，请核实！");
                }
            }
            basSkuGroup.setConfirmDate(new Date());
            basSkuGroup.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row=basSkuGroupMapper.updateAllById(basSkuGroup);
        List<BasSkuGroupItem> itemList=basSkuGroup.getItemList();
        List<BasSkuGroupItem> oldItemList = old.getItemList();
        List<Long> itemSids = new ArrayList<>();
        List<BasSkuGroupItem> delList = new ArrayList<>();
        for(BasSkuGroupItem item : itemList){
            item.setSkuGroupSid(basSkuGroup.getSkuGroupSid());
            item.setUpdateDate(new Date());
            item.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
            if(item.getSkuGroupItemSid()==null){
                itemMapper.insert(item);
            }else{
                itemSids.add(item.getSkuGroupItemSid());
                itemMapper.updateAllById(item);
            }
        }
        for(BasSkuGroupItem oldItem : oldItemList){
            if(!itemSids.contains(oldItem.getSkuGroupItemSid())){
                Map<String,Object> params=new HashMap<>();
                params.put("sku_group_item_sid", oldItem.getSkuGroupItemSid());
                itemMapper.deleteByMap(params);
            }
        }
        //确认状态后删除待办
        if (!ConstantsEms.SAVA_STATUS.equals(basSkuGroup.getHandleStatus())){
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, basSkuGroup.getSkuGroupSid()));
        }
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        msgList = BeanUtils.eq(old, basSkuGroup);
        MongodbDeal.update(basSkuGroup.getSkuGroupSid(), old.getHandleStatus(), basSkuGroup.getHandleStatus(), msgList,TITLE,null);
        return row;
    }

    /**
     * 批量删除SKU组档案
     *
     * @param skuGroupSids 需要删除的SKU组档案ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasSkuGroupByIds(List<Long> skuGroupSids) {
        int row=basSkuGroupMapper.deleteBatchIds(skuGroupSids);
        for(Long sid:skuGroupSids){
            Map<String,Object> params=new HashMap<>();
            params.put("sku_group_sid", sid);
            itemMapper.deleteByMap(params);
            //插入日志
            MongodbUtil.insertUserLog(Long.valueOf(sid), BusinessType.DELETE.getValue(), null, TITLE);
        }
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid, skuGroupSids));
        return row;
    }

    @Override
    public void deleteBasSkuGroupItemByIdsCheck(List<Long> skuGroupItemSidList){
        //判断该尺码组是否被商品引用 判断被删除的尺码是否被商品引用  已确认状态页面
        String skuNames = "";
        try {
            List<BasSkuGroupItem> itemList = itemMapper.selectBasSkuGroupItemList(new BasSkuGroupItem()
                    .setSkuGroupItemSidList(skuGroupItemSidList.toArray(new Long[skuGroupItemSidList.size()]))
                    .setSkuType(ConstantsEms.SKUTYP_CM).setHandleStatus(ConstantsEms.CHECK_STATUS));
            for (BasSkuGroupItem item : itemList) {
                //判断本尺码组有没有被商品引用
                List<BasMaterial> materialList = materialMapper.selectList(new QueryWrapper<BasMaterial>().lambda()
                        .eq(BasMaterial::getSku2GroupSid,item.getSkuGroupSid())
                        .eq(BasMaterial::getStatus,ConstantsEms.ENABLE_STATUS));
                if (CollectionUtils.isNotEmpty(materialList)){
                    //判断明细被删除的尺码有没有被商品引用
                    List<Long> materialSidList = materialList.stream().map(o->o.getMaterialSid()).collect(Collectors.toList());
                    List<BasMaterialSku> materialSkuList = materialSkuMapper.selectList(new QueryWrapper<BasMaterialSku>()
                            .lambda().in(BasMaterialSku::getMaterialSid,materialSidList).eq(BasMaterialSku::getSkuSid,item.getSkuSid())
                            .eq(BasMaterialSku::getStatus,ConstantsEms.ENABLE_STATUS));
                    if (CollectionUtils.isNotEmpty(materialSkuList)){
                        List<Long> materialSkuSidList = materialSkuList.stream().map(BasMaterialSku::getMaterialSkuSid).collect(Collectors.toList());
                        skuNames = skuNames + item.getSkuName() + ";";
                    }
                }
            }
        }catch (Exception e){
            throw new BaseException(e.getMessage());
        }
        if (StrUtil.isNotBlank(skuNames)){
            if (skuNames.endsWith(";")) {
                skuNames = skuNames.substring(0,skuNames.length() - 1);
            }
            throw new CustomException("尺码"+ skuNames +"已被商品引用，不能删除！");
        }
    }

    @Override
    public List<BasSkuGroup> getList(BasSkuGroup basSkuGroup){
        List<BasSkuGroup> groupList=basSkuGroupMapper.getList(basSkuGroup);
        return groupList;
    }

    @Override
    public List<BasSkuGroupItem> getDetail(Long skuGroupSid){
        List<BasSkuGroupItem> list = itemMapper.getDetail(skuGroupSid);
        //排序
        list.forEach(li->{
            String skuName = li.getSkuName();
            String[] nameSplit = skuName.split("/");
            if(nameSplit.length==1){
                li.setFirstSort(nameSplit[0]);
            }else{
                String[] name2split = nameSplit[1].split("\\(");
                if(name2split.length==2){
                    li.setSecondSort(name2split[0].replaceAll("[a-zA-Z]","" ));

                    li.setThirdSort(name2split[1]);
                }else{
                    li.setSecondSort(nameSplit[1].replaceAll("[a-zA-Z]","" ));
                }
                li.setFirstSort(nameSplit[0]);
            }
        });
        List<BasSkuGroupItem> allList = new ArrayList<>();
        List<BasSkuGroupItem> allThirdList = new ArrayList<>();
        List<BasSkuGroupItem> sortThird = list.stream().filter(li -> li.getThirdSort() != null).collect(Collectors.toList());
        List<BasSkuGroupItem> sortThirdNull = list.stream().filter(li -> li.getThirdSort() == null).collect(Collectors.toList());
        sortThird=sortThird.stream().sorted(Comparator.comparing(li->li.getThirdSort())).collect(Collectors.toList());
        allThirdList.addAll(sortThird);
        allThirdList.addAll(sortThirdNull);

        List<BasSkuGroupItem> sort = allThirdList.stream().filter(li -> li.getSecondSort() != null).collect(Collectors.toList());
        sort=sort.stream().sorted(Comparator.comparing(li->Integer.valueOf(li.getSecondSort()))).collect(Collectors.toList());
        List<BasSkuGroupItem> sortNull = allThirdList.stream().filter(li -> li.getSecondSort() == null).collect(Collectors.toList());
        allList.addAll(sort);
        allList.addAll(sortNull);
        list= allList.stream().sorted(Comparator.comparing(item -> item.getFirstSort())
        ).collect(Collectors.toList());
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(BasSkuGroup basSkuGroup){
        int row=0;
        Long[] sids=basSkuGroup.getSkuGroupSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                basSkuGroup.setSkuGroupSid(id);
                row=basSkuGroupMapper.updateById(basSkuGroup);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                String remark = StrUtil.isEmpty(basSkuGroup.getDisableRemark()) ? null : basSkuGroup.getDisableRemark();
                //插入日志
                MongodbDeal.status(id, basSkuGroup.getStatus(), null,TITLE, remark);
            }
        }
        return row;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(BasSkuGroup basSkuGroup){
        int row=0;
        Long[] sids=basSkuGroup.getSkuGroupSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                BasSkuGroup group = selectBasSkuGroupById(id);
                if(CollectionUtils.isEmpty(group.getItemList())){
                    throw new BaseException(""+ConstantsEms.CONFIRM_PROMPT_STATEMENT);
                }else{
                    List<String> stopList = new ArrayList<>();
                    for(BasSkuGroupItem item:group.getItemList()){
                        BasSku sku =  basSkuMapper.selectBasSkuById(Long.valueOf(item.getSkuSid()));
                        if(sku.getStatus().equals(Status.DISABLE.getCode())){
                            stopList.add(sku.getSkuName());
                        }
                    }
                    if(stopList.size()>0){
                        throw new BaseException("SKU档案："+stopList.toString()+"已停用，请核实！");
                    }
                }
                basSkuGroup.setSkuGroupSid(id).setHandleStatus(ConstantsEms.CHECK_STATUS);
                setConfirmInfo(basSkuGroup);
                row=basSkuGroupMapper.updateById(basSkuGroup);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                MongodbDeal.check(id, basSkuGroup.getHandleStatus(), null, TITLE, null);
            }
            //确认状态后删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(basSkuGroup.getHandleStatus())){
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .in(SysTodoTask::getDocumentSid, sids));
            }
        }
        return row;
    }

    public BasSkuGroup setConfirmInfo(BasSkuGroup entity){
        BasSkuGroup basSkuGroup = this.selectBasSkuGroupById(entity.getSkuGroupSid());
        if (entity.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)){
            if (CollectionUtils.isEmpty(basSkuGroup.getItemList())){
                throw new CustomException("确认操作明细不能为空");
            }
            entity.setConfirmDate(new Date())
                    .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        return entity;
    }


    @Override
    public List<BasSkuGroupItem> getReportForm(BasSkuGroupItem basSkuGroupItem) {
        List<BasSkuGroupItem> itemList = itemMapper.selectBasSkuGroupItemList(basSkuGroupItem);
        return itemList;
    }

}
