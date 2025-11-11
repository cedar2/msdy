package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.enums.Status;
import com.platform.ems.mapper.*;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.utils.SecurityUtils;
import com.platform.ems.domain.dto.request.MaterialPackageAcitonRequest;
import com.platform.ems.service.IBasMaterialPackageService;


/**
 * 常规辅料包-主Service业务层处理
 *
 * @author linhongwei
 * @date 2021-03-14
 */
@Service
@SuppressWarnings("all")
public class BasMaterialPackageServiceImpl extends ServiceImpl<BasMaterialPackageMapper, BasMaterialPackage> implements IBasMaterialPackageService {
    @Autowired
    private BasMaterialPackageMapper basMaterialPackageMapper;
    @Autowired
    private BasMaterialPackageItemMapper basMaterialPackageItemMapper;
    @Autowired
    private BasProductSeasonMapper basProductSeasonMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private BasCompanyBrandMapper basCompanyBrandMapper;
    @Autowired
    private BasCustomerBrandMapper basCustomerBrandMapper;
    @Autowired
    private BasMaterialMapper basMaterialMapper;
    @Autowired
    private BasSkuMapper basSkuMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;

    private static final String TITLE = "物料包档案";

    /**
     * 查询常规辅料包-主
     *
     * @param clientId 常规辅料包-主ID
     * @return 常规辅料包-主
     */
    @Override
    public BasMaterialPackage selectBasMaterialPackageById(Long sid) {
        //常规辅料包主表
        BasMaterialPackage basMaterialPackage = basMaterialPackageMapper.selectBasMaterialPackageById(sid);
        //常规辅料包明细表
        List<BasMaterialPackageItem> basMaterialPackageItems = basMaterialPackageItemMapper.getMaterialPackageItemList(new BasMaterialPackageItem().setMaterialPackageSid(sid));
        basMaterialPackage.setListBasMaterialPackageItem(basMaterialPackageItems);
        MongodbUtil.find(basMaterialPackage);
        return basMaterialPackage;
    }

    /**
     * 查询常规辅料包-主列表
     *
     * @param basMaterialPackage 常规辅料包-主
     * @return 常规辅料包-主
     */
    @Override
    public List<BasMaterialPackage> selectBasMaterialPackageList(BasMaterialPackage basMaterialPackage) {
        List<BasMaterialPackage> list = basMaterialPackageMapper.selectBasMaterialPackageList(basMaterialPackage);
        return list;
    }

    /**
     * 新增常规辅料包-主
     * 需要注意编码重复校验
     *
     * @param basMaterialPackage 常规辅料包-主
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult insertBasMaterialPackage(BasMaterialPackage request) {
        BasMaterialPackage materialPackage = new BasMaterialPackage();
        BeanUtils.copyProperties(request, materialPackage);
        int row = 0, row2 = 0;

        List<BasMaterialPackage> query1 = basMaterialPackageMapper.selectList(new QueryWrapper<BasMaterialPackage>()
                .eq("package_code", request.getPackageCode()));
        if (query1.size() > 0) {
            throw new CheckedException("物料包编码重复，请查看");
        }
        String packageName = request.getPackageName();
        QueryWrapper<BasMaterialPackage> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("package_name", packageName);
        BasMaterialPackage basMaterialPackage = basMaterialPackageMapper.selectOne(objectQueryWrapper);
        if (basMaterialPackage == null) {
            if (HandleStatus.SAVE.getCode().equals(request.getHandleStatus())) {
                row = basMaterialPackageMapper.insert(materialPackage);
            } else {
                //注入确认人 确认时间
                if (CollectionUtils.isEmpty(request.getListBasMaterialPackageItem())) {
                    throw new CheckedException("确认操作物料包明细至少要有一条");
                }
                materialPackage.setConfirmDate(new Date());
                materialPackage.setConfirmerAccount(SecurityUtils.getLoginUser().getUsername());
                row = basMaterialPackageMapper.insert(materialPackage);
            }
        } else {
            return AjaxResult.error("物料包名称重复，请查看");
        }
        if (!request.getListBasMaterialPackageItem().isEmpty()) {
            //获取自动填充后的id值
            Long id = materialPackage.getMaterialPackageSid();
            List<BasMaterialPackageItem> materialPackageItems = request.getListBasMaterialPackageItem();
            materialPackageItems.forEach(o -> {
                o.setMaterialPackageSid(id)
                        .setUnit(o.getUnitBase());
                basMaterialPackageItemMapper.insert(o);
            });
        }
        //待办通知
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(request.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName("s_bas_material_package")
                    .setDocumentSid(materialPackage.getMaterialPackageSid());
            sysTodoTask.setTitle("物料包档案: " + materialPackage.getPackageCode() + " 当前是保存状态，请及时处理！")
                    .setDocumentCode(String.valueOf(materialPackage.getPackageCode()))
                    .setNoticeDate(new Date())
                    .setUserId(ApiThreadLocalUtil.get().getUserid());
            sysTodoTaskMapper.insert(sysTodoTask);
        }
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        MongodbDeal.insert(Long.valueOf(materialPackage.getMaterialPackageSid()), request.getHandleStatus(), null, TITLE, null);
        return AjaxResult.success(materialPackage);
    }

    /**
     * 修改常规辅料包-主
     *
     * @param materialPackageRequest
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateBasMaterialPackage(BasMaterialPackage basMaterialPackage) {
        Long id = basMaterialPackage.getMaterialPackageSid();
        //当前状态
        BasMaterialPackage materialPackage = basMaterialPackageMapper.selectById(id);
        String nowHandleStatus = materialPackage.getHandleStatus();
        String packageName = basMaterialPackage.getPackageName();
        QueryWrapper<BasMaterialPackage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("package_name", packageName);
        BasMaterialPackage basMaterialPackages = basMaterialPackageMapper.selectOne(queryWrapper);
        if (HandleStatus.SAVE.getCode().equals(nowHandleStatus)) {
            //判断是否存在相同名称
            if (basMaterialPackages != null) {
                //判断是否是它本身
                if (!basMaterialPackages.getMaterialPackageSid().equals(basMaterialPackage.getMaterialPackageSid())) {
                    throw new CustomException("存在相同的物料包名称，修改失败");
                }
            }
            basMaterialPackageMapper.updateAllById(basMaterialPackage);
            QueryWrapper<BasMaterialPackageItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.eq("material_package_sid", id);
            //删除原有的明细表
            basMaterialPackageItemMapper.delete(itemWrapper);
            List<BasMaterialPackageItem> listBasMaterialPackageItem = basMaterialPackage.getListBasMaterialPackageItem();
            if (CollectionUtils.isNotEmpty(listBasMaterialPackageItem)) {
                listBasMaterialPackageItem.forEach(o -> {
                    //插入现有明细表
                    o.setMaterialPackageSid(id)
                            .setUnit(o.getUnitBase());
                    basMaterialPackageItemMapper.insert(o);
                });
            }
            //确认状态后删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(basMaterialPackage.getHandleStatus())) {
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getDocumentSid, basMaterialPackage.getMaterialPackageSid()));
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = com.platform.common.utils.bean.BeanUtils.eq(materialPackage, basMaterialPackage);
            MongodbDeal.update(Long.valueOf(basMaterialPackage.getMaterialPackageSid()), materialPackage.getHandleStatus(), basMaterialPackage.getHandleStatus(), msgList, TITLE, null);
            return AjaxResult.success("修改物料包信息成功");
        }
        return AjaxResult.error("仅保存状态才可编辑!");
    }

    /**
     * 变更常规辅料包-主
     *
     * @param materialPackageRequest
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult changeBasMaterialPackage(BasMaterialPackage basMaterialPackage) {
        Long id = basMaterialPackage.getMaterialPackageSid();
        if (CollectionUtils.isEmpty(basMaterialPackage.getListBasMaterialPackageItem())) {
            throw new CheckedException("确认操作物料包明细至少要有一条");
        }
        if (HandleStatus.CONFIRMED.getCode().equals(basMaterialPackageMapper.selectById(id).getHandleStatus())) {
            String packageName = basMaterialPackage.getPackageName();
            QueryWrapper<BasMaterialPackage> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("package_name", packageName);
            BasMaterialPackage basMaterialPackages = basMaterialPackageMapper.selectOne(queryWrapper);
            if (basMaterialPackages != null) {
                if (!basMaterialPackages.getMaterialPackageSid().equals(basMaterialPackage.getMaterialPackageSid())) {
                    throw new CustomException("存在相同的物料包名称，变更失败");
                }
            }
            BasMaterialPackage materialPackage = basMaterialPackageMapper.selectById(id);
            //修改主表
            basMaterialPackageMapper.updateAllById(basMaterialPackage);
            QueryWrapper<BasMaterialPackageItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.eq("material_package_sid", id);
            //删除原有的明细表
            basMaterialPackageItemMapper.delete(itemWrapper);
            List<BasMaterialPackageItem> listBasMaterialPackageItem = basMaterialPackage.getListBasMaterialPackageItem();
            if (CollectionUtils.isNotEmpty(listBasMaterialPackageItem)) {
                listBasMaterialPackageItem.forEach(o -> {
                    //插入现有明细表
                    o.setMaterialPackageSid(id)
                            .setUnit(o.getUnitBase());
                    basMaterialPackageItemMapper.insert(o);
                });
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = com.platform.common.utils.bean.BeanUtils.eq(materialPackage, basMaterialPackage);
            MongodbUtil.insertUserLog(Long.valueOf(basMaterialPackage.getMaterialPackageSid()), BusinessType.CHANGE.getValue(), msgList, TITLE);
            return AjaxResult.success("变更物料包信息成功");
        }
        return AjaxResult.error("仅确认状态才可修改");
    }

    /**
     * 批量删除常规辅料包-主
     *
     * @param clientIds 需要删除的常规辅料包-主ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult deleteBasMaterialPackageByIds(List<Long> sids) {
        QueryWrapper<BasMaterialPackage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("handle_status", HandleStatus.SAVE.getCode())
                .in("material_package_sid", sids);
        Integer selectCount = basMaterialPackageMapper.selectCount(queryWrapper);
        if (selectCount == sids.size()) {
            int count = basMaterialPackageMapper.deleteBatchIds(sids);
            QueryWrapper<BasMaterialPackageItem> wrapper = new QueryWrapper<>();
            wrapper.in("material_package_sid", sids);
            basMaterialPackageItemMapper.delete(wrapper);
            //删除待办
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, sids));
            sids.forEach(sid->{
                //插入日志
                MongodbUtil.insertUserLog(Long.valueOf(sid), BusinessType.DELETE.getValue(), TITLE);
            });
            return AjaxResult.success("删除成功，删除" + count + "物料包");
        } else {
            return AjaxResult.success("仅保存状态下才可删除");
        }

    }

    /**
     * 确认常规辅料包-主
     *
     * @param materialPackageAcitonRequest
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult confirmBasMaterialPackage(MaterialPackageAcitonRequest materialPackageAcitonRequest) {
        UpdateWrapper<BasMaterialPackage> Wrapper = new UpdateWrapper<>();
        Wrapper.set("handle_status", materialPackageAcitonRequest.getHandleStatus())
                .in("material_package_sid", materialPackageAcitonRequest.getMaterialPackageSid())
                .set("confirm_date", new Date())
                .set("confirmer_account", SecurityUtils.getLoginUser().getUsername());
        BasMaterialPackage basMaterialPackage = new BasMaterialPackage();
        int count = basMaterialPackageMapper.update(basMaterialPackage, Wrapper);
        //确认状态后删除待办
        if (!ConstantsEms.SAVA_STATUS.equals(materialPackageAcitonRequest.getHandleStatus())) {
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, materialPackageAcitonRequest.getMaterialPackageSid()));
        }
        //插入日志
        for (Long sid : materialPackageAcitonRequest.getMaterialPackageSid()) {
            MongodbDeal.check(Long.valueOf(sid), materialPackageAcitonRequest.getHandleStatus(), null, TITLE, null);
        }
        return AjaxResult.success("成功确认，确认" + count + "条物料包数据");
    }

    /**
     * 启用/停用常规辅料包-主
     *
     * @param materialPackageAcitonRequest
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult status(MaterialPackageAcitonRequest materialPackageAcitonRequest) {
        Long[] ids = materialPackageAcitonRequest.getMaterialPackageSid();
        QueryWrapper<BasMaterialPackage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("handle_status", HandleStatus.CONFIRMED.getCode())
                .in("material_package_sid", ids);
        int count = basMaterialPackageMapper.selectCount(queryWrapper);
        //判断是否都符合状态
        if (count != ids.length) {
            return AjaxResult.error("仅确认状态才可启用/停用");
        } else {
            UpdateWrapper<BasMaterialPackage> Wrapper = new UpdateWrapper<>();
            Wrapper.set("status", materialPackageAcitonRequest.getStatus())
                    .in("material_package_sid", ids);
            BasMaterialPackage basMaterialPackage = new BasMaterialPackage();
            basMaterialPackageMapper.update(basMaterialPackage, Wrapper);
            //当前所要改变的状态
            String nowStatus = materialPackageAcitonRequest.getStatus();
            //插入日志
            for (Long sid : materialPackageAcitonRequest.getMaterialPackageSid()) {
                String remark = StrUtil.isEmpty(materialPackageAcitonRequest.getDisableRemark()) ? null : materialPackageAcitonRequest.getDisableRemark();
                MongodbDeal.status(Long.valueOf(sid), materialPackageAcitonRequest.getStatus(), null, TITLE, remark);
            }
            if (Status.ENABLE.getCode().equals(nowStatus)) {
                return AjaxResult.success("启用成功");
            } else {
                return AjaxResult.success("停用成功");
            }
        }
    }

    @Override
    public List<BasMaterialPackage> getMaterialPackageList() {
        return basMaterialPackageMapper.getMaterialPackageList();
    }

    @Override
    public List<BasMaterialPackageItem> getMaterialPackageItemList(List<Long> materialPackageSids) {
        List<BasMaterialPackageItem> list = new ArrayList<BasMaterialPackageItem>();
        if (CollectionUtil.isNotEmpty(materialPackageSids)) {
            Long[] sids = materialPackageSids.toArray(new Long[materialPackageSids.size()]);
            list = basMaterialPackageItemMapper.getMaterialPackageItemList(new BasMaterialPackageItem().setMaterialPackageSidList(sids));
        }
        return list;
    }

    /**
     * 复制常规辅料包-主
     *
     * @param clientId 常规辅料包-主ID
     * @return 常规辅料包-主
     */
    @Override
    public BasMaterialPackage copyBasMaterialPackageById(Long sid) {
        //常规辅料包主表
        BasMaterialPackage basMaterialPackage = basMaterialPackageMapper.selectBasMaterialPackageById(sid);
        if (basMaterialPackage == null) {
            return null;
        }
        basMaterialPackage.setMaterialPackageSid(null).setHandleStatus(null)
                .setCreatorAccount(null).setCreateDate(null).setCreatorAccountName(null)
                .setUpdaterAccount(null).setUpdateDate(null)
                .setConfirmerAccount(null).setConfirmDate(null);
        //常规辅料包明细表
        List<BasMaterialPackageItem> basMaterialPackageItems = basMaterialPackageItemMapper.getMaterialPackageItemList(new BasMaterialPackageItem().setMaterialPackageSid(sid));
        basMaterialPackageItems.forEach(item->{
            item.setMaterialPackageSid(null).setMaterialPackItemSid(null)
                    .setCreatorAccount(null).setCreateDate(null).setCreatorAccountName(null)
                    .setUpdaterAccount(null).setUpdateDate(null);
        });
        basMaterialPackage.setListBasMaterialPackageItem(basMaterialPackageItems);
        return basMaterialPackage;
    }
}
