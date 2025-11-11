package com.platform.ems.service.impl;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.CosCostLaborTemplateItem;
import com.platform.ems.domain.PayWorkattendRecordItem;
import com.platform.ems.mapper.CosCostLaborTemplateItemMapper;
import com.platform.ems.plug.service.IConLaborTypeService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.ems.domain.CosCostLaborTemplate;
import com.platform.ems.mapper.CosCostLaborTemplateMapper;
import com.platform.ems.service.ICosCostLaborTemplateService;

/**
 * 成本核算工价模板Service业务层处理
 *
 * @author qhq
 * @date 2021-04-02
 */
@Service
@SuppressWarnings("all")
public class CosCostLaborTemplateServiceImpl extends ServiceImpl<CosCostLaborTemplateMapper, CosCostLaborTemplate> implements ICosCostLaborTemplateService {
    @Autowired
    private CosCostLaborTemplateMapper cosCostLaborTemplateMapper;
    @Autowired
    private CosCostLaborTemplateItemMapper cosCostLaborTemplateItemMapper;
    @Autowired
    private IConLaborTypeService conLaborTypeService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static final String TITLE = "成本核算工价模板";

    /**
     * 查询成本核算工价模板
     *
     * @param costLaborTemplateSid 成本核算工价模板ID
     * @return 成本核算工价模板
     */
    @Override
    public CosCostLaborTemplate selectCosCostLaborTemplateById(Long costLaborTemplateSid) {
        CosCostLaborTemplate cosCostLaborTemplate = cosCostLaborTemplateMapper.selectCosCostLaborTemplateById(costLaborTemplateSid);
        if(cosCostLaborTemplate!=null){
            List<CosCostLaborTemplateItem> itemList=cosCostLaborTemplateItemMapper.selectCosCostLaborTemplateItemList(new CosCostLaborTemplateItem().setCostLaborTemplateSid(costLaborTemplateSid));
            itemList.forEach(item->{
                MongodbUtil.find(item);
            });
            cosCostLaborTemplate.setItemList(itemList);
            MongodbUtil.find(cosCostLaborTemplate);
        }
        return cosCostLaborTemplate;
    }

    /**
     * 查询成本核算工价模板列表
     *
     * @param cosCostLaborTemplate 成本核算工价模板
     * @return 成本核算工价模板
     */
    @Override
    public List<CosCostLaborTemplate> selectCosCostLaborTemplateList(CosCostLaborTemplate cosCostLaborTemplate) {
        return cosCostLaborTemplateMapper.selectCosCostLaborTemplateList(cosCostLaborTemplate);
    }

    @Override
    public AjaxResult checkUnique(CosCostLaborTemplate cosCostLaborTemplate){
        if (cosCostLaborTemplate.getMaterialType()!=null && cosCostLaborTemplate.getProductTechniqueType()!=null &&cosCostLaborTemplate.getBusinessType()!=null){
            CosCostLaborTemplate request = new CosCostLaborTemplate()
                    .setMaterialType(cosCostLaborTemplate.getMaterialType()).setProductTechniqueType(cosCostLaborTemplate.getProductTechniqueType())
                    .setBusinessType(cosCostLaborTemplate.getBusinessType());
            List<CosCostLaborTemplate> costLaborTemplateList = cosCostLaborTemplateMapper.selectCosCostLaborTemplateList(request);
            if (CollectionUtils.isNotEmpty(costLaborTemplateList)){
                if (costLaborTemplateList.size() == 1){
                    List<DictData> businessTypeDict = sysDictDataService.selectDictData("s_cost_business_type"); //业务类型
                    Map<String, String> businessTypeMaps = businessTypeDict.stream().collect(Collectors.toMap(DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                    if (cosCostLaborTemplate.getCostLaborTemplateSid() != null){
                        if (!cosCostLaborTemplate.getCostLaborTemplateSid().equals(costLaborTemplateList.get(0).getCostLaborTemplateSid())){
                            String msg = "物料类型："+costLaborTemplateList.get(0).getMaterialTypeName()+
                                    "，生产工艺类型："+ costLaborTemplateList.get(0).getProductTechniqueTypeName() +
                                    "，已创建"+ businessTypeMaps.get(costLaborTemplateList.get(0).getBusinessType()) + "的工价模板，是否跳转至该模板详情页";
                            return AjaxResult.success(msg,costLaborTemplateList.get(0).getCostLaborTemplateSid().toString());
                        }
                    }else {
                        String msg = "物料类型："+costLaborTemplateList.get(0).getMaterialTypeName()+
                                "，生产工艺类型："+ costLaborTemplateList.get(0).getProductTechniqueTypeName() +
                                "，已创建"+ businessTypeMaps.get(costLaborTemplateList.get(0).getBusinessType()) + "的工价模板，是否跳转至该模板详情页";
                        return AjaxResult.success(msg,costLaborTemplateList.get(0).getCostLaborTemplateSid().toString());
                    }

                } else{
                    throw new BaseException("该物料类型，生产工艺类型，业务类型已存在多笔工价模板，请先核实！");
                }
            }
        }
        return null;
    }

    /**
     * 新增成本核算工价模板
     * 需要注意编码重复校验
     *
     * @param cosCostLaborTemplate 成本核算工价模板
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String insertCosCostLaborTemplate(CosCostLaborTemplate cosCostLaborTemplate) {
        List<CosCostLaborTemplate> query = cosCostLaborTemplateMapper.selectList(new QueryWrapper<CosCostLaborTemplate>().lambda().eq(CosCostLaborTemplate::getCostLaborTemplateCode, cosCostLaborTemplate.getCostLaborTemplateCode()));
        if (query.size() > 0) {
            throw new CheckedException("工价模板编码已存在！");
        }
        List<CosCostLaborTemplate> query2 = cosCostLaborTemplateMapper.selectList(new QueryWrapper<CosCostLaborTemplate>().lambda().eq(CosCostLaborTemplate::getCostLaborTemplateName, cosCostLaborTemplate.getCostLaborTemplateName()));
        if (query2.size() > 0) {
            throw new CheckedException("工价模板名称已存在！");
        }
        if(ConstantsEms.CHECK_STATUS.equals(cosCostLaborTemplate.getHandleStatus())){
            if (CollectionUtils.isEmpty(cosCostLaborTemplate.getItemList())){
                throw new BaseException("工价项明细不能为空！");
            }
            cosCostLaborTemplate.setConfirmerDate(new Date());
            cosCostLaborTemplate.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = cosCostLaborTemplateMapper.insert(cosCostLaborTemplate);
        if (row > 0) {
            addItem(cosCostLaborTemplate.getCostLaborTemplateSid(),cosCostLaborTemplate.getItemList());
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new CosCostLaborTemplate(), cosCostLaborTemplate);
            MongodbUtil.insertUserLog(cosCostLaborTemplate.getCostLaborTemplateSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
            if (ConstantsEms.CHECK_STATUS.equals(cosCostLaborTemplate.getHandleStatus())){
                MongodbUtil.insertUserLog(cosCostLaborTemplate.getCostLaborTemplateSid(), BusinessType.CONFIRM.getValue(), null, TITLE);
            }
        }
        return String.valueOf(row);
    }

    private void addItem(Long costLaborTemplateSid,List<CosCostLaborTemplateItem> itemList){
        if(itemList.size() <=0){
            return;
        }
        itemList.forEach(item->{
            item.setCostLaborTemplateSid(costLaborTemplateSid);
            item.setLaborTypeCode(conLaborTypeService.selectConLaborTypeCodeBySid(item.getLaborTypeSid()));
        });
        cosCostLaborTemplateItemMapper.inserts(itemList);
        //插入日志
        itemList.forEach(item->{
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new CosCostLaborTemplateItem(), item);
            MongodbUtil.insertUserLog(item.getCostLaborTemplateItemSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        });
    }

    private void deleteItem(Long costLaborTemplateSid){
        List<CosCostLaborTemplateItem> oldList = cosCostLaborTemplateItemMapper.selectList(new QueryWrapper<CosCostLaborTemplateItem>()
                .lambda().eq(CosCostLaborTemplateItem::getCostLaborTemplateSid,costLaborTemplateSid));
        cosCostLaborTemplateItemMapper.delete(new QueryWrapper<CosCostLaborTemplateItem>().lambda().eq(CosCostLaborTemplateItem::getCostLaborTemplateSid, costLaborTemplateSid));
        //插入日志
        oldList.forEach(item->{
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(item, new CosCostLaborTemplateItem());
            MongodbUtil.insertUserLog(item.getCostLaborTemplateItemSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
        });
    }

    private void updateItem(Long costLaborTemplateSid,List<CosCostLaborTemplateItem> itemList){
        if (CollectionUtil.isEmpty(itemList)) {
            deleteItem(costLaborTemplateSid);
            return;
        }
        List<CosCostLaborTemplateItem> oldList = cosCostLaborTemplateItemMapper.selectList(new QueryWrapper<CosCostLaborTemplateItem>()
                .lambda().eq(CosCostLaborTemplateItem::getCostLaborTemplateSid,costLaborTemplateSid));
        Map<Long,CosCostLaborTemplateItem> oldMaps = oldList.stream().collect(Collectors.toMap(CosCostLaborTemplateItem::getCostLaborTemplateItemSid, Function.identity()));
        if (CollectionUtils.isNotEmpty(oldList)){
            //原有数据ids
            List<Long> originalIds = oldList.stream().map(CosCostLaborTemplateItem::getCostLaborTemplateItemSid).collect(Collectors.toList());
            //还存在的数据
            List<CosCostLaborTemplateItem> updateList = itemList.stream().filter(o->o.getCostLaborTemplateItemSid() != null).collect(Collectors.toList());
            //更改还存在的数据
            if (CollectionUtil.isNotEmpty(updateList)) {
                List<Long> currentIds = updateList.stream().map(CosCostLaborTemplateItem::getCostLaborTemplateItemSid).collect(Collectors.toList());
                //清空删除的数据
                List<Long> result = originalIds.stream().filter(id -> !currentIds.contains(id)).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(result)) {
                    cosCostLaborTemplateItemMapper.deleteBatchIds(result);
                    //插入日志
                    result.forEach(sid->{
                        CosCostLaborTemplateItem temp = oldMaps.get(sid);
                        List<OperMsg> msgList = new ArrayList<>();
                        msgList = BeanUtils.eq(temp, new CosCostLaborTemplateItem());
                        MongodbUtil.insertUserLog(sid, BusinessType.DELETE.getValue(), msgList, TITLE);
                    });
                }
                //找出与旧的不同
                updateList.forEach(item->{
                    CosCostLaborTemplateItem temp = oldMaps.get(item.getCostLaborTemplateItemSid());
                    if (temp != null){
                        List<OperMsg> msgList = new ArrayList<>();
                        msgList = BeanUtils.eq(temp, item);
                        if (msgList.size() > 0){
                            item.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            item.setUpdateDate(new Date());
                            if (temp.getLaborTypeSid() == null || !temp.getLaborTypeSid().equals(item.getLaborTypeSid())){
                                item.setLaborTypeCode(conLaborTypeService.selectConLaborTypeCodeBySid(item.getLaborTypeSid()));
                            }
                            cosCostLaborTemplateItemMapper.updateAllById(item);
                            MongodbUtil.insertUserLog(item.getCostLaborTemplateItemSid(),BusinessType.CHANGE.getValue(), msgList, "工价模板-工价项明细", null);
                        }
                    }
                });
            }
            else {
                deleteItem(costLaborTemplateSid);
            }
            //新增数据
            List<CosCostLaborTemplateItem> newList = itemList.stream().filter(o->o.getCostLaborTemplateSid() == null).collect(Collectors.toList());
            addItem(costLaborTemplateSid,newList);
        }else {
            addItem(costLaborTemplateSid,itemList);
        }
    }

    /**
     * 修改成本核算工价模板
     *
     * @param cosCostLaborTemplate 成本核算工价模板
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateCosCostLaborTemplate(CosCostLaborTemplate cosCostLaborTemplate) {
        CosCostLaborTemplate query = cosCostLaborTemplateMapper.selectOne(new QueryWrapper<CosCostLaborTemplate>().lambda().eq(CosCostLaborTemplate::getCostLaborTemplateCode, cosCostLaborTemplate.getCostLaborTemplateCode()));
        if (query!=null&&!query.getCostLaborTemplateSid().equals(cosCostLaborTemplate.getCostLaborTemplateSid())) {
            throw new CheckedException("工价模板编码已存在！");
        }
        CosCostLaborTemplate query2 = cosCostLaborTemplateMapper.selectOne(new QueryWrapper<CosCostLaborTemplate>().lambda().eq(CosCostLaborTemplate::getCostLaborTemplateName, cosCostLaborTemplate.getCostLaborTemplateName()));
        if (query2!=null&&!query2.getCostLaborTemplateSid().equals(cosCostLaborTemplate.getCostLaborTemplateSid())) {
            throw new CheckedException("工价模板名称已存在！");
        }
        if (ConstantsEms.CHECK_STATUS.equals(cosCostLaborTemplate.getHandleStatus()) && CollectionUtils.isEmpty(cosCostLaborTemplate.getItemList())){
            throw new BaseException("工价项明细不能为空！");
        }
        CosCostLaborTemplate response = cosCostLaborTemplateMapper.selectCosCostLaborTemplateById(cosCostLaborTemplate.getCostLaborTemplateSid());
        if (ConstantsEms.CHECK_STATUS.equals(cosCostLaborTemplate.getHandleStatus())){
            cosCostLaborTemplate.setConfirmerDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        cosCostLaborTemplate.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        int row = cosCostLaborTemplateMapper.updateAllById(cosCostLaborTemplate);
        if (row > 0) {
            updateItem(cosCostLaborTemplate.getCostLaborTemplateSid(),cosCostLaborTemplate.getItemList());
            //插入日志
            MongodbUtil.insertUserLog(cosCostLaborTemplate.getCostLaborTemplateSid(), BusinessType.UPDATE.getValue(), response, cosCostLaborTemplate, TITLE);
            if (ConstantsEms.CHECK_STATUS.equals(cosCostLaborTemplate.getHandleStatus())){
                MongodbUtil.insertUserLog(cosCostLaborTemplate.getCostLaborTemplateSid(), BusinessType.CONFIRM.getValue(), null, TITLE);
            }
        }
        return String.valueOf(row);
    }

    /**
     * 变更成本核算工价模板
     *
     * @param cosCostLaborTemplate 成本核算工价模板
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeCosCostLaborTemplate(CosCostLaborTemplate cosCostLaborTemplate) {
        if (CollectionUtils.isEmpty(cosCostLaborTemplate.getItemList())){
            throw new BaseException("工价项明细不能为空！");
        }
        CosCostLaborTemplate query = cosCostLaborTemplateMapper.selectOne(new QueryWrapper<CosCostLaborTemplate>().lambda().eq(CosCostLaborTemplate::getCostLaborTemplateName, cosCostLaborTemplate.getCostLaborTemplateName()));
        if (query!=null&&!query.getCostLaborTemplateSid().equals(cosCostLaborTemplate.getCostLaborTemplateSid())) {
            throw new CheckedException("工价模板名称已存在！");
        }
        CosCostLaborTemplate response = cosCostLaborTemplateMapper.selectCosCostLaborTemplateById(cosCostLaborTemplate.getCostLaborTemplateSid());
        if (ConstantsEms.CHECK_STATUS.equals(cosCostLaborTemplate.getHandleStatus())){
            cosCostLaborTemplate.setConfirmerDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        cosCostLaborTemplate.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        int row = cosCostLaborTemplateMapper.updateAllById(cosCostLaborTemplate);
        if (row > 0) {
            updateItem(cosCostLaborTemplate.getCostLaborTemplateSid(),cosCostLaborTemplate.getItemList());
            //插入日志
            if (!ConstantsEms.CHECK_STATUS.equals(response.getHandleStatus()) && ConstantsEms.CHECK_STATUS.equals(cosCostLaborTemplate.getHandleStatus())){
                MongodbUtil.insertUserLog(cosCostLaborTemplate.getCostLaborTemplateSid(), BusinessType.UPDATE.getValue(), response, cosCostLaborTemplate, TITLE);
                MongodbUtil.insertUserLog(cosCostLaborTemplate.getCostLaborTemplateSid(), BusinessType.CONFIRM.getValue(), null, TITLE);
            }else {
                MongodbUtil.insertUserLog(cosCostLaborTemplate.getCostLaborTemplateSid(), BusinessType.CHANGE.getValue(), response, cosCostLaborTemplate, TITLE);
            }
        }
        return row;
    }

    /**
     * 批量删除成本核算工价模板
     *
     * @param costLaborTemplateSids 需要删除的成本核算工价模板ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCosCostLaborTemplateByIds(List<Long> costLaborTemplateSids) {
        List<CosCostLaborTemplate> cosCostLaborTemplateList = cosCostLaborTemplateMapper.selectList(new QueryWrapper<CosCostLaborTemplate>()
                .lambda().in(CosCostLaborTemplate::getCostLaborTemplateSid,costLaborTemplateSids));
        int row=cosCostLaborTemplateMapper.deleteBatchIds(costLaborTemplateSids);
        if(row!=costLaborTemplateSids.size()){
            throw new BaseException("批量删除异常,请联系管理员");
        }
        //插入日志
        if (CollectionUtil.isNotEmpty(cosCostLaborTemplateList)){
            cosCostLaborTemplateList.forEach(template->{
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(template, new CosCostLaborTemplateItem());
                MongodbUtil.insertUserLog(template.getCostLaborTemplateSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        List<CosCostLaborTemplateItem> itemList = cosCostLaborTemplateItemMapper.selectList(new QueryWrapper<CosCostLaborTemplateItem>()
                .lambda().in(CosCostLaborTemplateItem::getCostLaborTemplateSid,costLaborTemplateSids));
        cosCostLaborTemplateItemMapper.delete(new QueryWrapper<CosCostLaborTemplateItem>().lambda().in(CosCostLaborTemplateItem::getCostLaborTemplateSid, costLaborTemplateSids));
        //插入日志
        itemList.forEach(item->{
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(item, new CosCostLaborTemplateItem());
            MongodbUtil.insertUserLog(item.getCostLaborTemplateItemSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
        });
        return row;
    }

    /**
     * 启用/停用
     *
     * @param cosCostLaborTemplate
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(CosCostLaborTemplate cosCostLaborTemplate) {
        int row = 0;
        Long[] sids = cosCostLaborTemplate.getCostLaborTemplateSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                cosCostLaborTemplate.setCostLaborTemplateSid(id);
                row = cosCostLaborTemplateMapper.updateById(cosCostLaborTemplate);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                if (ConstantsEms.ENABLE_STATUS.equals(cosCostLaborTemplate.getStatus())){
                    MongodbUtil.insertUserLog(cosCostLaborTemplate.getCostLaborTemplateSid(), BusinessType.ENABLE.getValue(), null, TITLE, null);
                }else {
                    MongodbUtil.insertUserLog(cosCostLaborTemplate.getCostLaborTemplateSid(), BusinessType.DISENABLE.getValue(), null, TITLE, null);
                }

            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param cosCostLaborTemplate
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(CosCostLaborTemplate cosCostLaborTemplate) {
        int row = 0;
        Long[] sids = cosCostLaborTemplate.getCostLaborTemplateSidList();
        if (sids != null && sids.length > 0) {
            String code = "";
            for (Long id : sids) {
                List<CosCostLaborTemplateItem> itemList = cosCostLaborTemplateItemMapper.selectCosCostLaborTemplateItemList(new CosCostLaborTemplateItem()
                        .setCostLaborTemplateSid(id));
                if (CollectionUtils.isEmpty(itemList)) {
                    CosCostLaborTemplate template = cosCostLaborTemplateMapper.selectById(id);
                    code = code + template.getCostLaborTemplateCode() + ";";
                }
            }
            if (StrUtil.isNotBlank(code)){
                if (code.endsWith(";")) {
                    code = code.substring(0,code.length() - 1);
                }
                throw new CustomException("确认失败，" + code + " 工价项明细不能为空！");
            }
            else {
                if (ConstantsEms.CHECK_STATUS.equals(cosCostLaborTemplate.getHandleStatus())){
                    cosCostLaborTemplate.setConfirmerDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
                }
                for (Long id : sids) {
                    cosCostLaborTemplate.setCostLaborTemplateSid(id);
                    row = cosCostLaborTemplateMapper.updateById(cosCostLaborTemplate);
                    if (row == 0) {
                        throw new CustomException(id + "确认失败,请联系管理员");
                    }
                    //插入日志
                    MongodbUtil.insertUserLog(cosCostLaborTemplate.getCostLaborTemplateSid(), BusinessType.CONFIRM.getValue(), null, TITLE);
                }
            }
        }
        return row;
    }

    /**
     * 复制
     *
     * @param costLaborTemplateSid
     * @return
     */
    @Override
    public CosCostLaborTemplate copy(Long costLaborTemplateSid, boolean type){
        CosCostLaborTemplate cosCostLaborTemplate = cosCostLaborTemplateMapper.selectCosCostLaborTemplateById(costLaborTemplateSid);
        if (cosCostLaborTemplate == null){
            throw new BaseException("找不到该工价模板");
        }
        cosCostLaborTemplate.setCostLaborTemplateSid(null).setConfirmerAccountName(null).setUpdaterAccountName(null).setCreatorAccountName(null).setClientId(null)
                .setCreatorAccount(null).setCreateDate(null).setUpdateDate(null).setUpdaterAccount(null).setConfirmerAccount(null).setConfirmerDate(null).setRemark(null);
        cosCostLaborTemplate.setCostLaborTemplateCode(null).setCostLaborTemplateName(null).setMaterialType(null).setMaterialTypeName(null).setHandleStatus(ConstantsEms.SAVA_STATUS)
                .setProductTechniqueType(null).setProductTechniqueTypeName(null).setBusinessType(null).setPriceEnterMode(null).setStatus(ConstantsEms.ENABLE_STATUS);
        List<CosCostLaborTemplateItem> itemList=cosCostLaborTemplateItemMapper.selectCosCostLaborTemplateItemList(new CosCostLaborTemplateItem()
                .setCostLaborTemplateSid(costLaborTemplateSid));
        if (CollectionUtils.isNotEmpty(itemList)){
            itemList.forEach(item->{
                item.setCostLaborTemplateSid(null).setCostLaborTemplateItemSid(null).setClientId(null)
                        .setCreatorAccount(null).setCreateDate(null).setUpdateDate(null).setUpdaterAccount(null);
                if (false==type){
                    item.setSubtotalFormulaCheck(null).setSubtotalFormulaConfirm(null).setSubtotalFormulaInner(null).setSubtotalFormulaQuote(null);
                }
            });
            cosCostLaborTemplate.setItemList(itemList);
        }
        return cosCostLaborTemplate;
    }

}
