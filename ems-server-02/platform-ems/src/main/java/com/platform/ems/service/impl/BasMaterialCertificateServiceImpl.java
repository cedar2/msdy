package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanCopyUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.dto.response.external.*;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IBasMaterialCertificateService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 商品合格证洗唛信息Service业务层处理
 *
 * @author linhongwei
 * @date 2021-03-19
 */
@Service
@SuppressWarnings("all")
public class BasMaterialCertificateServiceImpl extends ServiceImpl<BasMaterialCertificateMapper,BasMaterialCertificate>  implements IBasMaterialCertificateService {
    @Autowired
    private BasMaterialCertificateMapper basMaterialCertificateMapper;

    @Autowired
    private BasMaterialMapper basMaterialMapper;

    @Autowired
    private BasMaterialSkuComponentMapper basMaterialSkuComponentMapper;

    @Autowired
    private BasMaterialSkuDownMapper basMaterialSkuDownMapper;

    @Autowired
    private BasMaterialCertificateFieldValueMapper basMaterialCertificateFieldValueMapper;

    @Autowired
    private BasMaterialCertificateAttachmentMapper basMaterialCertificateAttachmentMapper;

    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;

    @Autowired
    RedissonClient redissonClient;

    private static final String LOCK_KEY = "CERTIFICATE_STOCK";

    private static final String TITLE = "合格证洗唛档案";

    /**
     * 查询商品合格证洗唛信息
     *
     * @param materialCertificateSid 商品合格证洗唛信息ID
     * @return 商品合格证洗唛信息
     */
    @Override
    public BasMaterialCertificateExternal selectForExternalById(Long materialCertificateSid) {
        BasMaterialCertificateExternal basMaterialCertificate = basMaterialCertificateMapper.selectForExternalById(materialCertificateSid);
        //实测成分
        List<BasMaterialSkuComponentExternal> skuComponentExternalList = basMaterialSkuComponentMapper.selectForExternalList(basMaterialCertificate.getProductSid());
        basMaterialCertificate.setSkuComponentList(skuComponentExternalList);
        //羽绒充绒量
        List<BasMaterialSkuDownExternal> skuDownExternalList = basMaterialSkuDownMapper.selectForExternalList(basMaterialCertificate.getProductSid());
        basMaterialCertificate.setSkuDownList(skuDownExternalList);
        //自定义字段
        basMaterialCertificate.setFieldValueList(null);
        //附件
        List<BasMaterialCertificateAttachment> basMaterialCertificateAttachmentList = basMaterialCertificateAttachmentMapper.selectList
                (new QueryWrapper<BasMaterialCertificateAttachment>().lambda().eq(BasMaterialCertificateAttachment::getMaterialCertificateSid,materialCertificateSid));
        List<BasMaterialCertificateAttachExternal> attachExternalList = BeanCopyUtils.copyListProperties(
                basMaterialCertificateAttachmentList, BasMaterialCertificateAttachExternal::new);
        basMaterialCertificate.setAttachmentList(attachExternalList);
        return basMaterialCertificate;
    }

    /**
     * 查询商品合格证洗唛信息
     *
     * @param materialCertificateSid 商品合格证洗唛信息ID
     * @return 商品合格证洗唛信息
     */
    @Override
    public BasMaterialCertificate selectBasMaterialCertificateById(Long materialCertificateSid) {
        //合格证洗唛信息详情
        BasMaterialCertificate basMaterialCertificate = basMaterialCertificateMapper.selectBasMaterialCertificateById(materialCertificateSid);
        if (basMaterialCertificate == null){
            return null;
        }
        Long materialSid = basMaterialCertificate.getMaterialSid();
        //实测成分list
        BasMaterialSkuComponent basMaterialSkuComponent = new BasMaterialSkuComponent();
        basMaterialSkuComponent.setMaterialSid(materialSid);
        List<BasMaterialSkuComponent> basMaterialSkuComponentList = basMaterialSkuComponentMapper.selectBasMaterialSkuComponentList(basMaterialSkuComponent);
        //羽绒充绒量list
        BasMaterialSkuDown basMaterialSkuDown = new BasMaterialSkuDown();
        basMaterialSkuDown.setMaterialSid(materialSid);
        List<BasMaterialSkuDown> basMaterialSkuDownList = basMaterialSkuDownMapper.selectBasMaterialSkuDownList(basMaterialSkuDown);
        //自定义字段-值list
        BasMaterialCertificateFieldValue basMaterialCertificateFieldValue = new BasMaterialCertificateFieldValue();
        basMaterialCertificateFieldValue.setMaterialSid(materialSid);
        List<BasMaterialCertificateFieldValue> basMaterialCertificateFieldValueList = basMaterialCertificateFieldValueMapper.selectBasMaterialCertificateFieldValueList(basMaterialCertificateFieldValue);
        //合格证洗唛-附件list
        BasMaterialCertificateAttachment basMaterialCertificateAttachment = new BasMaterialCertificateAttachment();
        basMaterialCertificateAttachment.setMaterialCertificateSid(basMaterialCertificate.getMaterialCertificateSid());
        List<BasMaterialCertificateAttachment> basMaterialCertificateAttachmentList = basMaterialCertificateAttachmentMapper.selectBasMaterialCertificateAttachmentList(basMaterialCertificateAttachment);

        basMaterialCertificate.setBasMaterialSkuComponentList(basMaterialSkuComponentList);
        basMaterialCertificate.setBasMaterialSkuDownList(basMaterialSkuDownList);
        basMaterialCertificate.setBasMaterialCertificateFieldValueList(basMaterialCertificateFieldValueList);
        basMaterialCertificate.setAttachmentList(basMaterialCertificateAttachmentList);
        //查询日志信息
        MongodbUtil.find(basMaterialCertificate);
        return basMaterialCertificate;
    }

    /**
     * 查询商品合格证洗唛信息列表
     *
     * @param basMaterial 物料档案对象
     * @return 商品合格证洗唛信息
     */
    @Override
    public List<BasMaterialCertificate> selectBasMaterialCertificateList(BasMaterialCertificate materialCertificate) {
        return basMaterialCertificateMapper.selectBasMaterialCertificateList(materialCertificate);
    }

    /**
     * 新增商品合格证洗唛信息
     * 需要注意编码重复校验
     * @param basMaterialCertificate 商品合格证洗唛信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasMaterialCertificate(BasMaterialCertificate request) {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        lock.lock(10L, TimeUnit.SECONDS);
        try {
            BasMaterial basMaterial = basMaterialMapper.selectById(request.getMaterialSid());
            if (!basMaterial.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)) {
                throw new BaseException("输入的商品/物料/服务编码不存在，请检查！");
            }
            if (!basMaterial.getStatus().equals(ConstantsEms.ENABLE_STATUS)) {
                throw new BaseException("输入的商品/物料/服务编码已停用，请检查！");
            }
            BasMaterialCertificate basMaterialCertificate = basMaterialCertificateMapper.selectOne(new QueryWrapper<BasMaterialCertificate>().lambda()
                    .eq(BasMaterialCertificate::getMaterialSid, request.getMaterialSid()));
            if (basMaterialCertificate != null) {
                throw new BaseException("该商品编码已经创建过合格证洗唛档案了！");
            }
            //设置确认信息
            setConfirmInfo(request);
            //新增商品合格证洗唛信息
            basMaterialCertificateMapper.insert(request);
            //实测成分list
            List<BasMaterialSkuComponent> basMaterialSkuComponents = request.getBasMaterialSkuComponentList();
            if (CollectionUtils.isNotEmpty(basMaterialSkuComponents)) {
                addBasMaterialSkuComponent(request, basMaterialSkuComponents);
            }
            //羽绒充绒量list
            List<BasMaterialSkuDown> basMaterialSkuDowns = request.getBasMaterialSkuDownList();
            if (CollectionUtils.isNotEmpty(basMaterialSkuDowns)) {
                addBasMaterialSkuDown(request, basMaterialSkuDowns);
            }
            //自定义字段值list
            List<BasMaterialCertificateFieldValue> basMaterialCertificateFieldValues = request.getBasMaterialCertificateFieldValueList();
            if (CollectionUtils.isNotEmpty(basMaterialCertificateFieldValues)) {
                addBasMaterialCertificateFieldValue(request, basMaterialCertificateFieldValues);
            }
            //合格证洗唛附件list
            addBasMaterialCertificateAttachment(request);
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            if (ConstantsEms.SAVA_STATUS.equals(request.getHandleStatus())) {
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName("s_bas_material_certificate")
                        .setDocumentSid(request.getMaterialCertificateSid());
                sysTodoTask.setTitle("合格证洗唛档案: " + basMaterial.getMaterialCode() + " 当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(request.getMaterialCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbDeal.insert(Long.valueOf(request.getMaterialCertificateSid()), request.getHandleStatus(), null, TITLE, null);
        }catch (BaseException e){
            throw new BaseException(e.getMessage());
        }finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return 1;
    }

    /**
     * 商品SKU实测成分对象
     */
    private void addBasMaterialSkuComponent(BasMaterialCertificate request, List<BasMaterialSkuComponent> basMaterialSkuComponents) {
        basMaterialSkuComponentMapper.deleteMaterialSkuComponentById(request.getMaterialSid());
        basMaterialSkuComponents.forEach(o -> {
            o.setMaterialSid(request.getMaterialSid());
            basMaterialSkuComponentMapper.insert(o);
        });
    }

    /**
     * 商品SKU羽绒充绒量对象
     */
    private void addBasMaterialSkuDown(BasMaterialCertificate request, List<BasMaterialSkuDown> basMaterialSkuDowns) {
        basMaterialSkuDownMapper.deleteMaterialSkuDownById(request.getMaterialSid());
        basMaterialSkuDowns.forEach(o -> {
            o.setMaterialSid(request.getMaterialSid());
            basMaterialSkuDownMapper.insert(o);
        });
    }

    /**
     * 商品合格证洗唛自定义字段-值对象
     */
    private void addBasMaterialCertificateFieldValue(BasMaterialCertificate request, List<BasMaterialCertificateFieldValue> basMaterialCertificateFieldValues) {
        basMaterialCertificateFieldValueMapper.deleteMaterialCertificateFieldValueById(request.getMaterialSid());
        basMaterialCertificateFieldValues.forEach(o -> {
            o.setMaterialSid(request.getMaterialSid());
            basMaterialCertificateFieldValueMapper.insert(o);
        });
    }

    /**
     * 商品合格证洗唛-附件对象
     */
    private void addBasMaterialCertificateAttachment(BasMaterialCertificate request) {
        basMaterialCertificateAttachmentMapper.delete(
                new UpdateWrapper<BasMaterialCertificateAttachment>()
                        .lambda()
                        .eq(BasMaterialCertificateAttachment::getMaterialCertificateSid, request.getMaterialCertificateSid())
        );
        if (CollectionUtils.isNotEmpty(request.getAttachmentList())) {
            request.getAttachmentList().forEach(o -> {
                o.setMaterialCertificateSid(request.getMaterialCertificateSid());
            });
            basMaterialCertificateAttachmentMapper.inserts(request.getAttachmentList());
        }
    }

    /**
     * 设置确认信息
     */
    private void setConfirmInfo(BasMaterialCertificate o) {
        if (o == null) {
            return;
        }
        if (HandleStatus.CONFIRMED.getCode().equals(o.getHandleStatus())) {
            o.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            o.setConfirmDate(new Date());
        }
    }

    /**
     * 修改商品合格证洗唛信息
     *
     * @param basMaterialCertificate 商品合格证洗唛信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasMaterialCertificate(BasMaterialCertificate request) {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        lock.lock(10L, TimeUnit.SECONDS);
        try {
            BasMaterialCertificate old = this.selectBasMaterialCertificateById(request.getMaterialCertificateSid());
            //设置确认信息
            setConfirmInfo(request);
            basMaterialCertificateMapper.updateAllById(request);
            //实测成分list
            List<BasMaterialSkuComponent> basMaterialSkuComponents = request.getBasMaterialSkuComponentList();
            if (CollectionUtils.isNotEmpty(basMaterialSkuComponents)) {
                basMaterialSkuComponents.stream().forEach(o -> {
                    o.setUpdateDate(new Date());
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
                addBasMaterialSkuComponent(request, basMaterialSkuComponents);
            }
            else {
                basMaterialSkuComponentMapper.deleteMaterialSkuComponentById(request.getMaterialSid());
            }
            //羽绒充绒量list
            List<BasMaterialSkuDown> basMaterialSkuDowns = request.getBasMaterialSkuDownList();
            if (CollectionUtils.isNotEmpty(basMaterialSkuDowns)) {
                basMaterialSkuDowns.stream().forEach(o -> {
                    o.setUpdateDate(new Date());
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
                addBasMaterialSkuDown(request, basMaterialSkuDowns);
            }
            else {
                basMaterialSkuDownMapper.deleteMaterialSkuDownById(request.getMaterialSid());
            }
            //自定义字段值list
            List<BasMaterialCertificateFieldValue> basMaterialCertificateFieldValues = request.getBasMaterialCertificateFieldValueList();
            if (CollectionUtils.isNotEmpty(basMaterialCertificateFieldValues)) {
                basMaterialCertificateFieldValues.stream().forEach(o -> {
                    o.setUpdateDate(new Date());
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
                addBasMaterialCertificateFieldValue(request, basMaterialCertificateFieldValues);
            }
            else {
                basMaterialCertificateFieldValueMapper.deleteMaterialCertificateFieldValueById(request.getMaterialSid());
            }
            //合格证洗唛附件list
            addBasMaterialCertificateAttachment(request);
            //确认状态后删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(request.getHandleStatus())){
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getDocumentSid, request.getMaterialCertificateSid()));
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = com.platform.common.utils.bean.BeanUtils.eq(old, request);
            MongodbDeal.update(Long.valueOf(request.getMaterialCertificateSid()), old.getHandleStatus(), request.getHandleStatus(), msgList, TITLE, null);
        }catch (BaseException e){
            throw new BaseException(e.getMessage());
        }finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return 1;
    }

    /**
     * 批量删除商品合格证洗唛信息
     *
     * @param materialCertificateSidList 需要删除的商品合格证洗唛信息ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasMaterialCertificateByIds(Long[] materialCertificateSidList) {
        BasMaterialCertificate params = new BasMaterialCertificate();
        params.setMaterialCertificateSidList(materialCertificateSidList);
        params.setHandleStatus(HandleStatus.SAVE.getCode());
        int count = basMaterialCertificateMapper.countByDomain(params);
        if (count != materialCertificateSidList.length) {
            throw new BaseException("仅保存状态才允许删除");
        }
        List<BasMaterialCertificate> basMaterialCertificateList =
                basMaterialCertificateMapper.selectMaterialCertificateListByParams(materialCertificateSidList);
        //商品sids
        List<Long> materialSids = basMaterialCertificateList.stream()
                                                              .filter(o -> Objects.nonNull(o))
                                                              .map(o -> o.getMaterialSid())
                                                              .filter(materialSid -> Objects.nonNull(materialSid))
                                                              .collect(Collectors.toList());
        //删除商品合格证洗唛信息
        basMaterialCertificateMapper.deleteBasMaterialCertificateByIds(materialCertificateSidList);
        //删除实测成分
        basMaterialSkuComponentMapper.deleteBasMaterialSkuComponentByIds(materialSids);
        //删除羽绒充绒量
        basMaterialSkuDownMapper.deleteBasMaterialSkuDownByIds(materialSids);
        //删除自定义字段值
        basMaterialCertificateFieldValueMapper.deleteBasMaterialCertificateFieldValueByIds(materialSids);
        //删除合格证洗唛附件
        basMaterialCertificateAttachmentMapper.deleteBasMaterialCertificateAttachmentByIds(materialCertificateSidList);
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid, materialCertificateSidList));
        //插入日志
        basMaterialCertificateList.forEach(item->{
            MongodbUtil.insertUserLog(Long.valueOf(item.getMaterialCertificateSid()), BusinessType.DELETE.getValue(), TITLE);
        });
        return materialCertificateSidList.length;
    }

    /**
     * 商品合格证洗唛信息确认
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirm(BasMaterialCertificate basMaterialCertificate) {
        //商品合格证洗唛信息sids
        Long[] materialCertificateSidList = basMaterialCertificate.getMaterialCertificateSidList();

        BasMaterialCertificate params = new BasMaterialCertificate();
        params.setMaterialCertificateSidList(materialCertificateSidList);
        params.setHandleStatus(HandleStatus.SAVE.getCode());
        int count = basMaterialCertificateMapper.countByDomain(params);
        if (count != materialCertificateSidList.length){
            throw new BaseException("仅保存状态才允许确认");
        }
        //确认状态后删除待办
        if (ConstantsEms.CHECK_STATUS.equals(basMaterialCertificate.getHandleStatus())){
            basMaterialCertificate.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            basMaterialCertificate.setConfirmDate(new Date());
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, materialCertificateSidList));
        }
        //插入日志
        for (Long sid : materialCertificateSidList){
            MongodbDeal.check(Long.valueOf(sid), basMaterialCertificate.getHandleStatus(), null, TITLE, null);
        }
        return basMaterialCertificateMapper.confirm(basMaterialCertificate);
    }

    /**
     * 商品合格证洗唛信息变更
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int change(BasMaterialCertificate basMaterialCertificate) {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        lock.lock(10L, TimeUnit.SECONDS);
        try {
            Long materialCertificateSid = basMaterialCertificate.getMaterialCertificateSid();
            BasMaterialCertificate materialCertificate = basMaterialCertificateMapper.selectBasMaterialCertificateById(materialCertificateSid);
            //验证是否确认状态
            if (!HandleStatus.CONFIRMED.getCode().equals(materialCertificate.getHandleStatus())) {
                throw new BaseException("仅确认状态才允许变更");
            }

            basMaterialCertificateMapper.updateAllById(basMaterialCertificate);
            //实测成分list
            List<BasMaterialSkuComponent> basMaterialSkuComponents = basMaterialCertificate.getBasMaterialSkuComponentList();
            if (CollectionUtils.isNotEmpty(basMaterialSkuComponents)) {
                basMaterialSkuComponents.stream().forEach(o -> {
                    o.setUpdateDate(new Date());
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
                addBasMaterialSkuComponent(basMaterialCertificate, basMaterialSkuComponents);
            }
            else {
                basMaterialSkuComponentMapper.deleteMaterialSkuComponentById(basMaterialCertificate.getMaterialSid());
            }
            //羽绒充绒量list
            List<BasMaterialSkuDown> basMaterialSkuDowns = basMaterialCertificate.getBasMaterialSkuDownList();
            if (CollectionUtils.isNotEmpty(basMaterialSkuDowns)) {
                basMaterialSkuDowns.stream().forEach(o -> {
                    o.setUpdateDate(new Date());
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
                addBasMaterialSkuDown(basMaterialCertificate, basMaterialSkuDowns);
            }
            else {
                basMaterialSkuDownMapper.deleteMaterialSkuDownById(basMaterialCertificate.getMaterialSid());
            }
            //自定义字段值list
            List<BasMaterialCertificateFieldValue> basMaterialCertificateFieldValues = basMaterialCertificate.getBasMaterialCertificateFieldValueList();
            if (CollectionUtils.isNotEmpty(basMaterialCertificateFieldValues)) {
                basMaterialCertificateFieldValues.stream().forEach(o -> {
                    o.setUpdateDate(new Date());
                    o.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                });
                addBasMaterialCertificateFieldValue(basMaterialCertificate, basMaterialCertificateFieldValues);
            }
            else {
                basMaterialCertificateFieldValueMapper.deleteMaterialCertificateFieldValueById(basMaterialCertificate.getMaterialSid());
            }
            //合格证洗唛附件list
            addBasMaterialCertificateAttachment(basMaterialCertificate);
            //插入日志
            MongodbUtil.insertUserLog(Long.valueOf(basMaterialCertificate.getMaterialCertificateSid()), BusinessType.CHANGE.getValue(), materialCertificate, basMaterialCertificate, TITLE);
        }catch (BaseException e){
            throw new BaseException(e.getMessage());
        }finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return 1;
    }

//    /**
//     * 新建商品合格证洗唛初始时验证商品状态，和是否存在，已存在就返详情页信息
//     */
//    @Override
//    public BasMaterialCertificate checkPoint(Long basMaterialSid){
//        BasMaterial basMaterial = basMaterialMapper.selectBasMaterialById(basMaterialSid);
//        if (!basMaterial.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)){
//            throw new BaseException("该商品/物料/服务编码不存在，请检查！");
//        }
//        BasMaterialCertificate basMaterialCertificate = this.selectBasMaterialCertificateById(basMaterialSid);
//        return basMaterialCertificate;
//    }
}
