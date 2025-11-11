package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.api.service.RemoteMenuService;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.core.domain.entity.ConMaterialClass;
import com.platform.common.core.domain.entity.SysMenu;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.constant.ConstantsWorkbench;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IDevCategoryPlanItemService;
import com.platform.ems.service.IDevCategoryPlanService;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.system.mapper.SystemUserMapper;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 品类规划Service业务层处理
 *
 * @author chenkw
 * @date 2022-12-09
 */
@Service
@SuppressWarnings("all")
public class DevCategoryPlanServiceImpl extends ServiceImpl<DevCategoryPlanMapper, DevCategoryPlan> implements IDevCategoryPlanService {
    @Autowired
    private DevCategoryPlanMapper devCategoryPlanMapper;
    @Autowired
    private DevCategoryPlanItemMapper devCategoryPlanItemMapper;
    @Autowired
    private IDevCategoryPlanItemService devCategoryPlanItemService;
    @Autowired
    private DevCategoryPlanAttachMapper devCategoryPlanAttachMapper;
    @Autowired
    private DevDevelopPlanMapper devDevelopPlanMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private BasCompanyBrandMapper basCompanyBrandMapper;
    @Autowired
    private BasProductSeasonMapper basProductSeasonMapper;
    @Autowired
    private SystemUserMapper sysUserMapper;
    @Autowired
    private ConMaterialClassMapper materialClassMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private RemoteMenuService remoteMenuService;
    @Autowired
    private ISystemDictDataService sysDictDataService;

    private static final String TITLE = "品类规划";

    /**
     * 查询品类规划
     *
     * @param categoryPlanSid 品类规划ID
     * @return 品类规划
     */
    @Override
    public DevCategoryPlan selectDevCategoryPlanById(Long categoryPlanSid) {
        DevCategoryPlan devCategoryPlan = devCategoryPlanMapper.selectDevCategoryPlanById(categoryPlanSid);
        devCategoryPlan.setCategoryPlanItemList(new ArrayList<>());
        devCategoryPlan.setAttachmentList(new ArrayList<>());
        // 明细列表
        List<DevCategoryPlanItem> itemList = devCategoryPlanItemService.selectDevCategoryPlanItemList(
                new DevCategoryPlanItem().setCategoryPlanSid(categoryPlanSid));
        if (CollectionUtil.isNotEmpty(itemList)) {
            itemList.forEach(item->{
                List<DevDevelopPlan> planList = devDevelopPlanMapper.selectDevDevelopPlanList(new DevDevelopPlan()
                        .setCategoryPlanItemSid(item.getCategoryPlanItemSid()).setSortRule("category"));
                if (CollectionUtil.isNotEmpty(planList)) {
                    item.setDevelopPlanList(planList);
                }
            });
            devCategoryPlan.setCategoryPlanItemList(itemList);
        }
        // 附件
        List<DevCategoryPlanAttach> attachmentList = devCategoryPlanAttachMapper.selectDevCategoryPlanAttachList(
                new DevCategoryPlanAttach().setCategoryPlanSid(categoryPlanSid));
        if (CollectionUtil.isNotEmpty(attachmentList)) {
            devCategoryPlan.setAttachmentList(attachmentList);
        }
        MongodbUtil.find(devCategoryPlan);
        return devCategoryPlan;
    }

    /**
     * 复制品类规划
     *
     * @param categoryPlanSid 品类规划ID
     * @return 品类规划
     */
    @Override
    public DevCategoryPlan copyDevCategoryPlanById(Long categoryPlanSid) {
        DevCategoryPlan devCategoryPlan = devCategoryPlanMapper.selectDevCategoryPlanById(categoryPlanSid);
        devCategoryPlan.setCategoryPlanSid(null).setCreateDate(null)
                .setCreatorAccount(null).setCreatorAccountName(null).setHandleStatus(ConstantsEms.SAVA_STATUS)
                .setUpdateDate(null).setUpdaterAccount(null).setConfirmerAccount(null)
                .setUpdaterAccountName(null).setConfirmDate(null).setConfirmerAccountName(null);
        devCategoryPlan.setCategoryPlanItemList(new ArrayList<>());
        devCategoryPlan.setAttachmentList(new ArrayList<>());
        // 明细列表
        List<DevCategoryPlanItem> itemList = devCategoryPlanItemService.selectDevCategoryPlanItemList(
                new DevCategoryPlanItem().setCategoryPlanSid(categoryPlanSid));
        if (CollectionUtil.isNotEmpty(itemList)) {
            itemList.forEach(item->{
                item.setCategoryPlanSid(null).setCategoryPlanItemSid(null).setCreateDate(null)
                        .setCreatorAccount(null).setCreatorAccountName(null)
                        .setUpdateDate(null).setUpdaterAccount(null)
                        .setUpdaterAccountName(null);
            });
            devCategoryPlan.setCategoryPlanItemList(itemList);
        }
        return devCategoryPlan;
    }

    /**
     * 查询品类规划列表
     *
     * @param devCategoryPlan 品类规划
     * @return 品类规划
     */
    @Override
    public List<DevCategoryPlan> selectDevCategoryPlanList(DevCategoryPlan devCategoryPlan) {
        return devCategoryPlanMapper.selectDevCategoryPlanList(devCategoryPlan);
    }

    /**
     * 校验
     *
     * @param devCategoryPlan 品类规划
     * @return 结果
     */
    private void judge(DevCategoryPlan devCategoryPlan) {
        // 确认状态明细不能为空
        if (ConstantsEms.CHECK_STATUS.equals(devCategoryPlan.getHandleStatus())
                && CollectionUtil.isEmpty(devCategoryPlan.getCategoryPlanItemList())) {
            throw new BaseException("规划明细不能为空！");
        }
        // 明细的组别要和基本信息的主表保持一致
        if (StrUtil.isNotBlank(devCategoryPlan.getGroupType())) {
            devCategoryPlan.getCategoryPlanItemList().forEach(item -> {
                if (StrUtil.isNotBlank(item.getGroupType())
                        && !item.getGroupType().equals(devCategoryPlan.getGroupType())) {
                    throw new BaseException("基本信息的组别跟明细的组别不一致！");
                }
            });
        }
    }

    /**
     * 校验明细唯一性
     *
     * @param devCategoryPlan 品类规划
     * @return EmsResultEntity
     */
    @Override
    public EmsResultEntity judgeItemUnique(DevCategoryPlan devCategoryPlan) {
        // 对明细，通过“年度（主表）X公司（主表）X品牌（主表）X产品季（主表）X组别（明细）”进行校重校验，若已创建的品类规划明细（不含本单）存在此组合，
        // 提示：已存在此“年度X公司X品牌X产品季X组别”组合的品类规划明细，是否继续创建？（若明细的组别重复，仅显示一次提示）
        List<DevCategoryPlanItem> itemList = devCategoryPlan.getCategoryPlanItemList();
        if (CollectionUtil.isNotEmpty(itemList) && devCategoryPlan.getCompanySid() != null && StrUtil.isNotBlank(devCategoryPlan.getYear())) {
            itemList = itemList.stream().filter(o->StrUtil.isNotBlank(o.getGroupType())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(itemList)) {
                String[] groupType = itemList.stream().map(DevCategoryPlanItem::getGroupType).toArray(String[]::new);
                // 得到符合条件的数据库中的明细
                List<DevCategoryPlanItem> existItem = devCategoryPlanItemMapper.selectDevCategoryPlanItemList(new DevCategoryPlanItem()
                        .setYear(devCategoryPlan.getYear()).setCompanySid(devCategoryPlan.getCompanySid()).setBrandCode(devCategoryPlan.getBrandCode())
                        .setProductSeasonSid(devCategoryPlan.getProductSeasonSid()).setGroupTypeList(groupType));
                // 如果本单的品牌是空的，则对应已存在的品牌也应该是空的
                if (CollectionUtil.isNotEmpty(existItem) && StrUtil.isBlank(devCategoryPlan.getBrandCode())) {
                    existItem  = existItem.stream().filter(o->StrUtil.isBlank(o.getBrandCode())).collect(Collectors.toList());
                }
                // 如果本单的产品季是空的，则对应已存在的产品季也应该是空的
                if (CollectionUtil.isNotEmpty(existItem) && devCategoryPlan.getProductSeasonSid() == null) {
                    existItem  = existItem.stream().filter(o->o.getProductSeasonSid() == null).collect(Collectors.toList());
                }
                // 去掉自身
                if (CollectionUtil.isNotEmpty(existItem) && devCategoryPlan.getCategoryPlanSid() != null) {
                    existItem  = existItem.stream().filter(o->!o.getCategoryPlanSid().equals(devCategoryPlan.getCategoryPlanSid())).collect(Collectors.toList());
                }
                if (CollectionUtil.isNotEmpty(existItem)) {
                    // 报错
                    if (CollectionUtil.isNotEmpty(existItem)) {
                        Map<String, List<DevCategoryPlanItem>> existMap = existItem.stream()
                                .collect(Collectors.groupingBy(e -> e.getGroupType()));
                        // 年度
                        String year = devCategoryPlan.getYear();
                        // 公司简称
                        String companyName = "";
                        BasCompany company = basCompanyMapper.selectById(devCategoryPlan.getCompanySid());
                        companyName = StrUtil.isBlank(company.getShortName()) ? company.getCompanyName() : company.getShortName();
                        // 品牌名称
                        String brandName = "";
                        if (StrUtil.isNotBlank(devCategoryPlan.getBrandCode())) {
                            BasCompanyBrand brand = basCompanyBrandMapper.selectOne(new QueryWrapper<BasCompanyBrand>().lambda()
                                    .eq(BasCompanyBrand::getCompanySid, devCategoryPlan.getCompanySid())
                                    .eq(BasCompanyBrand::getBrandCode, devCategoryPlan.getBrandCode()));
                            brandName = "+" + brand.getBrandName();
                        }
                        // 产品季
                        String productSeasonName = "";
                        if (devCategoryPlan.getProductSeasonSid() != null) {
                            BasProductSeason season = basProductSeasonMapper.selectOne(new QueryWrapper<BasProductSeason>().lambda()
                                    .eq(BasProductSeason::getProductSeasonSid, devCategoryPlan.getProductSeasonSid()));
                            productSeasonName = "+" + season.getProductSeasonName();
                        }
                        // 组别
                        List<DictData> groupGroupDictList = sysDictDataService.selectDictData("s_product_group");
                        Map<String, String> groupGroupDictMaps = groupGroupDictList.stream().collect(Collectors.toMap
                                (DictData::getDictValue, DictData::getDictLabel, (key1, key2) -> key2));
                        // 提示
                        List<CommonErrMsgResponse> msgList = new ArrayList<>();
                        Map<String,Integer> map = new HashMap<>();
                        int i = 0;
                        for (DevCategoryPlanItem item : itemList) {
                            if (existMap.containsKey(item.getGroupType()) && !map.containsKey(item.getGroupType())) {
                                map.put(item.getGroupType(), 1);
                                CommonErrMsgResponse res = new CommonErrMsgResponse();
                                res.setItemNum(i);
                                res.setMsg("已存在此“" + year + "+" + companyName + brandName + productSeasonName + "+" +
                                        groupGroupDictMaps.get(item.getGroupType()) + "”组合的品类规划明细，是否继续创建？");
                                msgList.add(res);
                            }
                            i++;
                        }
                        if (CollectionUtil.isNotEmpty(msgList)) {
                            return EmsResultEntity.warning(msgList);
                        }
                    }
                }
            }
        }
        return EmsResultEntity.success();
    }

    /**
     * 校验编号不能重复
     *
     * @param devCategoryPlan 品类规划
     * @return 结果
     */
    private void judgeCode(DevCategoryPlan devCategoryPlan) {
        QueryWrapper<DevCategoryPlan> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(DevCategoryPlan::getCategoryPlanCode, devCategoryPlan.getCategoryPlanCode());
        if (devCategoryPlan.getCategoryPlanSid() != null) {
            queryWrapper.lambda().ne(DevCategoryPlan::getCategoryPlanSid, devCategoryPlan.getCategoryPlanSid());
        }
        List<DevCategoryPlan> templateNameList = devCategoryPlanMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(templateNameList)) {
            throw new BaseException("品类规划编号已存在！");
        }
    }

    /**
     * 设置部分字段的编码
     *
     * @param old 表中数据，new修改后数据
     * @return 结果
     */
    private void setData(DevCategoryPlan oldPlan, DevCategoryPlan newPlan) {
        // 公司
        if (newPlan.getCompanySid() != null && !newPlan.getCompanySid().equals(oldPlan.getCompanySid())) {
            BasCompany company = basCompanyMapper.selectById(newPlan.getCompanySid());
            if (company != null) {
                newPlan.setCompanyCode(company.getCompanyCode());
            } else {
                newPlan.setCompanyCode(null);
            }
        } else if (newPlan.getCompanySid() == null) {
            newPlan.setCompanyCode(null);
        }
    }

    /**
     * 新增品类规划
     * 需要注意编码重复校验
     *
     * @param devCategoryPlan 品类规划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDevCategoryPlan(DevCategoryPlan devCategoryPlan) {
        //处理编码
        if (StrUtil.isNotBlank(devCategoryPlan.getCategoryPlanCode())){
            String code = devCategoryPlan.getCategoryPlanCode().replaceAll("\\s*", "");
            devCategoryPlan.setCategoryPlanCode(code);
        }
        // 校验编号不能重复
        judgeCode(devCategoryPlan);
        // 其它校验
        judge(devCategoryPlan);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(devCategoryPlan.getHandleStatus())) {
            devCategoryPlan.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 写入部分字段的code
        this.setData(new DevCategoryPlan(), devCategoryPlan);
        int row = devCategoryPlanMapper.insert(devCategoryPlan);
        if (row > 0) {
            // 获取品类规划编号
            DevCategoryPlan plan = devCategoryPlanMapper.selectById(devCategoryPlan.getCategoryPlanSid());
            devCategoryPlan.setCategoryPlanCode(plan.getCategoryPlanCode());
            // 写入明细
            if (CollectionUtil.isNotEmpty(devCategoryPlan.getCategoryPlanItemList())) {
                devCategoryPlanItemService.insertDevCategoryPlanItemList(devCategoryPlan);
            }
            // 写入附件
            if (CollectionUtil.isNotEmpty(devCategoryPlan.getAttachmentList())) {
                devCategoryPlan.getAttachmentList().forEach(item->{
                    item.setCategoryPlanSid(plan.getCategoryPlanSid());
                    item.setCategoryPlanCode(plan.getCategoryPlanCode());
                });
                devCategoryPlanAttachMapper.inserts(devCategoryPlan.getAttachmentList());
            }
            // 待办
            if (ConstantsEms.SAVA_STATUS.equals(devCategoryPlan.getHandleStatus())) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName(ConstantsTable.TABLE_DEV_CATEGORY_PLAN)
                        .setDocumentSid(devCategoryPlan.getCategoryPlanSid());
                sysTodoTask.setTitle("品类规划" + plan.getCategoryPlanCode() + "当前是保存状态，请及时处理！")
                        .setDocumentCode(plan.getCategoryPlanCode().toString())
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                // 获取菜单id
                SysMenu menu = new SysMenu();
                menu.setMenuName(ConstantsWorkbench.TODO_DEV_CATEGORY_PLAN);
                try {
                    menu = remoteMenuService.getInfoByName(menu).getData();
                } catch (Exception e){
                    log.warn(ConstantsWorkbench.TODO_DEV_CATEGORY_PLAN + "菜单获取失败！");
                }
                if (menu != null && menu.getMenuId() != null) {
                    sysTodoTask.setMenuId(menu.getMenuId());
                }
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new DevCategoryPlan(), devCategoryPlan);
            MongodbDeal.insert(devCategoryPlan.getCategoryPlanSid(), devCategoryPlan.getHandleStatus(), msgList, TITLE, null, devCategoryPlan.getImportType());
        }
        return row;
    }

    /**
     * 修改品类规划
     *
     * @param devCategoryPlan 品类规划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDevCategoryPlan(DevCategoryPlan devCategoryPlan) {
        DevCategoryPlan original = devCategoryPlanMapper.selectDevCategoryPlanById(devCategoryPlan.getCategoryPlanSid());
        // 校验编号不能重复
        if (!devCategoryPlan.getCategoryPlanCode().equals(original.getCategoryPlanCode())) {
            judgeCode(devCategoryPlan);
        }
        // 其它校验
        judge(devCategoryPlan);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(devCategoryPlan.getHandleStatus())) {
            devCategoryPlan.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, devCategoryPlan);
        if (CollectionUtil.isNotEmpty(msgList)) {
            devCategoryPlan.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 写入部分字段的code
        this.setData(original, devCategoryPlan);
        // 更新主表
        int row = devCategoryPlanMapper.updateAllById(devCategoryPlan);
        if (row > 0) {
            // 修改明细
            devCategoryPlanItemService.updateDevCategoryPlanItemList(devCategoryPlan);
            // 修改附件
            this.updateDevCategoryPlanAttach(devCategoryPlan);
            // 不是保存状态删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(devCategoryPlan.getHandleStatus())) {
                sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                        .eq(SysTodoTask::getDocumentSid, devCategoryPlan.getCategoryPlanSid())
                        .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                        .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_DEV_CATEGORY_PLAN));
            }
            //插入日志
            MongodbDeal.update(devCategoryPlan.getCategoryPlanSid(), original.getHandleStatus(), devCategoryPlan.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 批量修改附件信息
     *
     * @param devCategoryPlan 品类档案
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDevCategoryPlanAttach(DevCategoryPlan devCategoryPlan) {
        // 先删后加
        devCategoryPlanAttachMapper.delete(new QueryWrapper<DevCategoryPlanAttach>().lambda()
                .eq(DevCategoryPlanAttach::getCategoryPlanSid, devCategoryPlan.getCategoryPlanSid()));
        if (CollectionUtil.isNotEmpty(devCategoryPlan.getAttachmentList())) {
            devCategoryPlan.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getCategoryPlanAttachSid() == null) {
                    att.setCategoryPlanSid(devCategoryPlan.getCategoryPlanSid());
                    att.setCategoryPlanCode(devCategoryPlan.getCategoryPlanCode());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            devCategoryPlanAttachMapper.inserts(devCategoryPlan.getAttachmentList());
        }
    }

    /**
     * 变更品类规划
     *
     * @param devCategoryPlan 品类规划
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeDevCategoryPlan(DevCategoryPlan devCategoryPlan) {
        DevCategoryPlan response = devCategoryPlanMapper.selectDevCategoryPlanById(devCategoryPlan.getCategoryPlanSid());
        // 校验编号不能重复
        if (!devCategoryPlan.getCategoryPlanCode().equals(response.getCategoryPlanCode())) {
            judgeCode(devCategoryPlan);
        }
        // 其它校验
        judge(devCategoryPlan);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, devCategoryPlan);
        if (CollectionUtil.isNotEmpty(msgList)) {
            devCategoryPlan.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 写入部分字段的code
        this.setData(response, devCategoryPlan);
        // 更新主表
        int row = devCategoryPlanMapper.updateAllById(devCategoryPlan);
        if (row > 0) {
            // 修改明细
            devCategoryPlanItemService.updateDevCategoryPlanItemList(devCategoryPlan);
            // 修改附件
            this.updateDevCategoryPlanAttach(devCategoryPlan);
            //插入日志
            MongodbUtil.insertUserLog(devCategoryPlan.getCategoryPlanSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 批量删除品类规划
     *
     * @param categoryPlanSids 需要删除的品类规划ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDevCategoryPlanByIds(List<Long> categoryPlanSids) {
        List<DevCategoryPlan> list = devCategoryPlanMapper.selectList(new QueryWrapper<DevCategoryPlan>()
                .lambda().in(DevCategoryPlan::getCategoryPlanSid, categoryPlanSids));
        // 删除校验
        list = list.stream().filter(o-> !HandleStatus.SAVE.getCode().equals(o.getHandleStatus()) && !HandleStatus.RETURNED.getCode().equals(o.getHandleStatus()))
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(list)) {
            throw new BaseException("只有保存或者已退回状态才允许删除！");
        }
        int row = devCategoryPlanMapper.deleteBatchIds(categoryPlanSids);
        if (row > 0) {
            // 删除明细
            devCategoryPlanItemService.deleteDevCategoryPlanItemByPlan(categoryPlanSids);
            // 删除附件
            devCategoryPlanAttachMapper.delete(new QueryWrapper<DevCategoryPlanAttach>().lambda()
                    .in(DevCategoryPlanAttach::getCategoryPlanSid, categoryPlanSids));
            // 删除待办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, categoryPlanSids)
                    .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                    .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_DEV_CATEGORY_PLAN));
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new DevCategoryPlan());
                MongodbUtil.insertUserLog(o.getCategoryPlanSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param devCategoryPlan
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(DevCategoryPlan devCategoryPlan) {
        int row = 0;
        Long[] sids = devCategoryPlan.getCategoryPlanSidList();
        if (sids != null && sids.length > 0) {
            // 校验
            // 是否没有明细
            boolean noItem = true;
            List<DevCategoryPlanItem> itemList = devCategoryPlanItemMapper.selectList(new QueryWrapper<DevCategoryPlanItem>()
                    .lambda().in(DevCategoryPlanItem::getCategoryPlanSid, sids));
            if (CollectionUtil.isNotEmpty(itemList)) {
                Long[] itemSids = itemList.stream().map(DevCategoryPlanItem::getCategoryPlanSid).distinct().toArray(Long[]::new);
                Set<Long> set = Arrays.stream(itemSids).collect(Collectors.toSet());
                int length = 0; length = set.size(); CollectionUtil.addAll(set, sids);
                if (length == set.size()) {
                    noItem = false;
                }
            }
            if (noItem) {
                throw new BaseException("存在规划明细为空的品类规划，确认失败！");
            }
            // 更新
            LambdaUpdateWrapper<DevCategoryPlan> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(DevCategoryPlan::getCategoryPlanSid, sids);
            updateWrapper.set(DevCategoryPlan::getHandleStatus, devCategoryPlan.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(devCategoryPlan.getHandleStatus())) {
                updateWrapper.set(DevCategoryPlan::getConfirmDate, new Date());
                updateWrapper.set(DevCategoryPlan::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            row = devCategoryPlanMapper.update(null, updateWrapper);
            if (row > 0) {
                // 删除待办
                if (ConstantsEms.CHECK_STATUS.equals(devCategoryPlan.getHandleStatus())) {
                    sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                            .in(SysTodoTask::getDocumentSid, sids)
                            .eq(SysTodoTask::getTaskCategory, ConstantsEms.TODO_TASK_DB)
                            .eq(SysTodoTask::getTableName, ConstantsTable.TABLE_DEV_CATEGORY_PLAN));
                }
                for (Long id : sids) {
                    //插入日志
                    MongodbDeal.check(id, devCategoryPlan.getHandleStatus(), null, TITLE, null);
                }
            }
        }
        return row;
    }

    /**
     * 作废品类规划
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancellationDevCategoryPlanById(DevCategoryPlan devCategoryPlan) {
        Long categoryPlanSid = devCategoryPlan.getCategoryPlanSid();
        DevCategoryPlan response = devCategoryPlanMapper.selectById(categoryPlanSid);
        if (!ConstantsEms.CHECK_STATUS.equals(response.getHandleStatus())) {
            throw new BaseException("所选数据非'已确认'状态，无法作废！");
        }
        response.setHandleStatus(HandleStatus.INVALID.getCode());
        // 作废说明不允许为空
        response.setCancelRemark(devCategoryPlan.getCancelRemark());
        int row = devCategoryPlanMapper.updateById(response);
        MongodbUtil.insertUserLog(categoryPlanSid, BusinessType.CANCEL.getValue(), null, TITLE, devCategoryPlan.getCancelRemark());
        return row;
    }

    /**
     * 删除项目档案明细前的校验
     *
     * @param projectSids 需要删除的项目档案ID
     * @return 结果
     */
    @Override
    public void deleteDevCategoryPlanItemByIdsJudge(List<Long> categoryPlanItemSids) {
        categoryPlanItemSids = categoryPlanItemSids.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (categoryPlanItemSids.size() != 0) {
            List<DevDevelopPlan> planList = devDevelopPlanMapper.selectList(new QueryWrapper<DevDevelopPlan>().lambda()
                    .in(DevDevelopPlan::getCategoryPlanItemSid, categoryPlanItemSids));
            if (CollectionUtil.isNotEmpty(planList)) {
                throw new BaseException("存在明细已创建开发计划，不允许删除！");
            }
        }
    }

    /**
     * 导入品类规划
     * @param file 文件
     * @return 返回
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity importCategory(MultipartFile file) {
        int num = 0;
        try {
            File toFile = null;
            try {
                toFile = FileUtils.multipartFileToFile(file);
            } catch (Exception e) {
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();

            // 数据字典年份
            List<DictData> yearList = sysDictDataService.selectDictData("s_year");
            yearList = yearList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> yearMaps = yearList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            // 数据字典开发类型
            List<DictData> planTypeList = sysDictDataService.selectDictData("s_plan_type");
            planTypeList = planTypeList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> planTypeMaps = planTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            // 数据字典组别
            List<DictData> groupTypeList = sysDictDataService.selectDictData("s_product_group");
            groupTypeList = groupTypeList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> groupTypeMaps = groupTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 错误信息
            CommonErrMsgResponse errMsg = null;
            List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
            // 警告信息
            CommonErrMsgResponse warnMsg = null;
            List<CommonErrMsgResponse> warnMsgList = new ArrayList<>();
            // 提示信息
            CommonErrMsgResponse infoMsg = null;
            List<CommonErrMsgResponse> infoMsgList = new ArrayList<>();

            // 列表
            List<DevCategoryPlan> categoryPlanList = new ArrayList<>();

            Map<String, DevCategoryPlan> map = new HashMap<>();

            Map<String, Integer> groupMap = new HashMap<>();

            // 编码年度+公司+品牌作为值 ,是否已存在作为值
            Map<String, Boolean> yearCompanyBrandMap = new HashMap<>();

            // 编码作为key ，年度+公司+品牌作为值
            Map<String, String> codeYearCompanyBrandMap = new HashMap<>();

            // 编码作为值 ，年度+公司+品牌+组别作为key
            Map<String, String> yearCompanyBrandGroupMap = new HashMap<>();

            if (readAll.size() > 100) {
                throw new BaseException("导入表格中数据请不要超过100行！");
            }

            // 存在重复的负责人行数
            int staff = 0;

            // 循环文件
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
                //填充总列数
                copy(objects, readAll);
                num = i + 1;

                // 单行明细
                DevCategoryPlanItem planItem = new DevCategoryPlanItem();

                // 主表
                DevCategoryPlan plan = null;
                /*
                 * 品类规划编号 必填
                 */
                String categoryPlanCode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                /*
                 * 年度 必填
                 */
                String yearName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                /*
                 * 公司简称 必填
                 */
                String companyShortName = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                /*
                 * 品牌名称 选填
                 */
                String companyBrandName = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                /*
                 * 产品季 选填
                 */
                String productSeasonName = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                /*
                 * 大类名称 必填
                 */
                String bigClassName = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                /*
                 * 中类名称 必填
                 */
                String middleClassName = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                /*
                 * 小类名称 必填
                 */
                String smallClassName = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();
                /*
                 * 计划类型 必填
                 */
                String planTypeName = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString();
                /*
                 * 组别 选填
                 */
                String groupTypeName = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString();
                // 校验是否重复
                if (groupMap.containsKey(String.valueOf(categoryPlanCode) + "-" + String.valueOf(yearName)
                        + "-" + String.valueOf(companyShortName) + "-" + String.valueOf(companyBrandName)
                        + "-" + String.valueOf(bigClassName)+"-"+ String.valueOf(middleClassName)
                        +"-"+String.valueOf(smallClassName)+"-"+String.valueOf(planTypeName)
                        +"-"+String.valueOf(groupTypeName))) {
                    continue;
                }
                else {
                    groupMap.put(String.valueOf(categoryPlanCode) + "-" + String.valueOf(yearName)
                            + "-" + String.valueOf(companyShortName) + "-" + String.valueOf(companyBrandName)
                            + "-" + String.valueOf(bigClassName) +"-"+String.valueOf(middleClassName)
                            +"-"+String.valueOf(smallClassName)+"-"+ String.valueOf(planTypeName)
                            +"-"+String.valueOf(groupTypeName), num);
                }

                /*
                 * 品类规划编号 必填
                 */
                if (StrUtil.isBlank(categoryPlanCode)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("品类规划编号不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    DevCategoryPlan one = devCategoryPlanMapper.selectOne(new QueryWrapper<DevCategoryPlan>().lambda()
                            .eq(DevCategoryPlan::getCategoryPlanCode, categoryPlanCode));
                    if (one != null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("品类规划编号“" + categoryPlanCode + "”已存在，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        categoryPlanCode = categoryPlanCode.trim();

                        if (categoryPlanCode.length() > 30) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("品类规划编号长度不能超过30位，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }

                /*
                 * 年度 必填
                 */
                String year = null;
                if (StrUtil.isBlank(yearName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("年度不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    // 通过数据字典标签获取数据字典的值
                    year = yearMaps.get(yearName);
                    if (StrUtil.isBlank(year)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("年度填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 公司简称 必填
                 */
                String companyCode = null;
                String companyName = null;
                Long companySid = null;
                if (StrUtil.isBlank(companyShortName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("公司简称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        BasCompany basCompany = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>().lambda().eq(BasCompany::getShortName, companyShortName));
                        if (basCompany == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("公司简称“"+ companyShortName +"”不存在，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(basCompany.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(basCompany.getHandleStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("公司简称“" + companyShortName + "”必须是确认且启用的数据，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            companySid = basCompany.getCompanySid();
                            companyCode = basCompany.getCompanyCode();
                            companyName = basCompany.getCompanyName();
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("公司简称“" + companyShortName + "“系统中存在重复，请先检查该公司，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 品牌名称 选填
                 */
                Long companyBrandSid = null;
                String brandCode = null;
                if (StrUtil.isNotBlank(companyBrandName)) {
                    try {
                        BasCompanyBrand basCompanyBrand = basCompanyBrandMapper.selectOne(new QueryWrapper<BasCompanyBrand>().lambda()
                                .eq(BasCompanyBrand::getCompanySid, companySid).eq(BasCompanyBrand::getStatus, ConstantsEms.ENABLE_STATUS)
                                .eq(BasCompanyBrand::getBrandName, companyBrandName));
                        if (basCompanyBrand == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("公司“"+ companyShortName +"”下不存在启用的品牌”" + companyBrandName + "“，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            companyBrandSid = basCompanyBrand.getCompanyBrandSid();
                            brandCode = basCompanyBrand.getBrandCode();
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("公司“" + companyShortName + "“下存在重复的”" + companyBrandName + "“品牌，请先检查该公司，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 产品季 选填
                 */
                Long productSeasonSid = null;
                String productSeasonCode = null;
                if (StrUtil.isNotBlank(productSeasonName)) {
                    try {
                        BasProductSeason productSeason = basProductSeasonMapper.selectOne(new QueryWrapper<BasProductSeason>().lambda()
                                .eq(BasProductSeason::getProductSeasonName, productSeasonName));
                        if (productSeason == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("产品季“"+ productSeasonName +"”不存在，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(productSeason.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(productSeason.getHandleStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("产品季“" + productSeasonName + "”必须是确认且启用的数据，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            else {
                                productSeasonSid = productSeason.getProductSeasonSid();
                                productSeasonCode = productSeason.getProductSeasonCode();
                            }
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("产品季“"+ productSeasonName +"”系统中存在重复，请先检查该产品季，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 大类名称 必填
                 */
                Long bigClassSid = null;
                String bigClassCode = null;
                if (StrUtil.isBlank(bigClassName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("大类名称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    try {
                        ConMaterialClass materialClass = materialClassMapper.selectOne(new QueryWrapper<ConMaterialClass>().lambda()
                                .eq(ConMaterialClass::getNodeName, bigClassName).eq(ConMaterialClass::getLevel, "1"));
                        if (materialClass == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("大类名称”" + bigClassName + "“不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(materialClass.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(materialClass.getHandleStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("大类名称“" + bigClassName + "”填写错误，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            bigClassSid = materialClass.getMaterialClassSid();
                            bigClassCode = materialClass.getNodeCode();
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("大类名称”" + bigClassName + "“系统中存在重复，请先检查此大类，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 中类名称 必填
                 */
                Long middleClassSid = null;
                String middleClassCode = null;
                if (StrUtil.isBlank(middleClassName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("中类名称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else if (bigClassSid != null) {
                    try {
                        ConMaterialClass materialClass = materialClassMapper.selectOne(new QueryWrapper<ConMaterialClass>().lambda()
                                .eq(ConMaterialClass::getNodeName, middleClassName).eq(ConMaterialClass::getLevel, "2")
                                .eq(ConMaterialClass::getParentCodeSid, bigClassSid));
                        if (materialClass == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("中类名称”" + middleClassName + "“不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(materialClass.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(materialClass.getHandleStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("中类名称“" + middleClassName + "”填写错误，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            middleClassSid = materialClass.getMaterialClassSid();
                            middleClassCode = materialClass.getNodeCode();
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("中类名称”" + middleClassName + "“系统中存在重复，请先检查此中类，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 小类名称 必填
                 */
                Long smallClassSid = null;
                String smallClassCode = null;
                if (StrUtil.isBlank(smallClassName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("小类名称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else if (middleClassSid != null) {
                    try {
                        ConMaterialClass materialClass = materialClassMapper.selectOne(new QueryWrapper<ConMaterialClass>().lambda()
                                .eq(ConMaterialClass::getNodeName, smallClassName).eq(ConMaterialClass::getLevel, "3")
                                .eq(ConMaterialClass::getParentCodeSid, middleClassSid));
                        if (materialClass == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("小类名称”" + smallClassName + "“不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }
                        else {
                            if (ConstantsEms.DISENABLE_STATUS.equals(materialClass.getStatus()) || !ConstantsEms.CHECK_STATUS.equals(materialClass.getHandleStatus())) {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("小类名称“" + smallClassName + "”填写错误，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            smallClassSid = materialClass.getMaterialClassSid();
                            smallClassCode = materialClass.getNodeCode();
                        }
                    } catch (TooManyResultsException e) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("小类名称”" + smallClassName + "“系统中存在重复，请先检查此小类，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 规划款数量 必填
                 */
                String planQuantityS = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();
                Long planQuantity = null;
                if (StrUtil.isBlank(planQuantityS)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("规划款数量不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isPositiveInteger(planQuantityS)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("规划款数量数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    else {
                        planQuantity = Long.parseLong(planQuantityS);
                    }
                }

                /*
                 * 计划类型 必填
                 */
                String planType = null;
                if (StrUtil.isBlank(planTypeName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("计划类型不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    // 通过数据字典标签获取数据字典的值
                    planType = planTypeMaps.get(planTypeName);
                    if (StrUtil.isBlank(year)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("计划类型填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 组别 选填
                 */
                String groupType = null;
                if (StrUtil.isNotBlank(groupTypeName)) {
                    // 通过数据字典标签获取数据字典的值
                    groupType = groupTypeMaps.get(groupTypeName);
                    if (StrUtil.isBlank(groupType)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("组别填写错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                /*
                 * 若表格中的同一品类规划编码的“公司、品牌、年度”数据不同，批量报错：品类规划编号XXX填写的“公司、品牌、年度”数据不一致，请核实！
                 */
                String value = String.valueOf(yearName) + "-" + String.valueOf(companyShortName) + "-" + String.valueOf(companyBrandName);
                if (categoryPlanCode != null) {
                    if (codeYearCompanyBrandMap.containsKey(categoryPlanCode)) {
                        if (!value.equals(codeYearCompanyBrandMap.get(categoryPlanCode))) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            String code = categoryPlanCode == null ? "" : "“" + categoryPlanCode + "“";
                            errMsg.setMsg("品类规划编号“" + categoryPlanCode + "”填写的“公司、品牌、年度”数据不一致，请核实！");
                            errMsgList.add(errMsg);
                        }
                    }
                    else {
                        codeYearCompanyBrandMap.put(categoryPlanCode, value);
                    }
                }

                // 年度+公司+品牌+组别 判断唯一
                String seasonName = null;
                if (productSeasonSid != null) {
                    seasonName = productSeasonName;
                }
                if (StrUtil.isNotBlank(year) && companySid != null && StrUtil.isNotBlank(groupType)) {
                    String groupKey = String.valueOf(year) + "-" + String.valueOf(companySid) + "-" + String.valueOf(brandCode)
                        + "-" + String.valueOf(seasonName) + "-" + String.valueOf(groupType);
                    // 表格内
                    if (yearCompanyBrandGroupMap.containsKey(groupKey)) {
                        if (!yearCompanyBrandGroupMap.get(groupKey).equals(String.valueOf(categoryPlanCode))) {
                            warnMsg = new CommonErrMsgResponse();
                            warnMsg.setItemNum(num);
                            String brandName = companyBrandName == null ? "" : "+" + companyBrandName;
                            String season = seasonName == null ? "" : "+" + seasonName;
                            warnMsg.setMsg("表格中已存在此“" + year + "+" + companyShortName + brandName + seasonName +
                                    "+" + groupTypeName + "”组合的品类规划明细，是否继续创建？");
                            warnMsgList.add(warnMsg);
                        }
                    }
                    else {
                        yearCompanyBrandGroupMap.put(groupKey, String.valueOf(categoryPlanCode));
                        // 数据库
                        List<DevCategoryPlanItem> itemList = devCategoryPlanItemMapper.selectDevCategoryPlanItemList(new DevCategoryPlanItem()
                                .setYear(year).setCompanySid(companySid).setBrandCode(brandCode).setProductSeasonSid(productSeasonSid).setGroupType(groupType));
                        if (CollectionUtil.isNotEmpty(itemList)) {
                            if (StrUtil.isBlank(brandCode)) {
                                // 如果品牌是空的则要找品牌是空的才算存在
                                itemList = itemList.stream().filter(o->StrUtil.isBlank(o.getBrandCode())).collect(Collectors.toList());
                            }
                            if (CollectionUtil.isNotEmpty(itemList) && productSeasonSid == null) {
                                // 如果产品季是空的则要找产品季是空的才算存在
                                itemList = itemList.stream().filter(o->o.getProductSeasonSid() == null).collect(Collectors.toList());
                            }
                            if (CollectionUtil.isNotEmpty(itemList)) {
                                warnMsg = new CommonErrMsgResponse();
                                warnMsg.setItemNum(num);
                                String brandName = companyBrandName == null ? "" : "+" + companyBrandName;
                                String season = seasonName == null ? "" : "+" + seasonName;
                                warnMsg.setMsg("系统中已存在此“" + year + "+" + companyShortName + brandName + season +
                                        "+" + groupTypeName + "”组合的品类规划明细，是否继续创建？");
                                warnMsgList.add(warnMsg);
                            }
                        }
                    }
                }

                /*
                 * 开发计划负责人姓名 选填
                 */
                String nextReceiverName = objects.get(11) == null || objects.get(11) == "" ? null : objects.get(11).toString();
                Long nextReceiverSid = null;
                String nextReceiverCode = null;
                if (StrUtil.isNotBlank(nextReceiverName)) {
                    List<SysUser> userList = sysUserMapper.selectList(new QueryWrapper<SysUser>().lambda().eq(SysUser::getNickName, nextReceiverName)
                            .eq(SysUser::getClientId, ApiThreadLocalUtil.get().getClientId())
                            .eq(SysUser::getStatus, ConstantsEms.SYS_COMMON_STATUS_Y));
                    if (CollectionUtil.isEmpty(userList)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("开发计划负责人“"+ nextReceiverName +"”不存在或已停用，导入失败！");
                        errMsgList.add(errMsg);
                    } else {
                        SysUser user = userList.get(0);
                        if (userList.size() > 1) {
                            infoMsg = new CommonErrMsgResponse();
                            infoMsg.setItemNum(num);
                            infoMsg.setMsg("系统存在多个姓名“" + nextReceiverName + "”的负责人档案，本次导入的是账号 " + user.getUserName() + " 的负责人");
                            infoMsgList.add(infoMsg);
                            staff += 1;
                        }
                        nextReceiverSid = user.getUserId();
                        nextReceiverCode = user.getUserName();
                    }
                }

                /*
                 * 款式 选填
                 */
                String kuanTypeName = objects.get(12) == null || objects.get(12) == "" ? null : objects.get(12).toString();

                /*
                 * 系列 选填
                 */
                String seriesName = objects.get(13) == null || objects.get(13) == "" ? null : objects.get(13).toString();

                /*
                 * 规划说明 选填
                 */
                String planRemark = objects.get(14) == null || objects.get(14) == "" ? null : objects.get(14).toString();
                if (StrUtil.isNotBlank(planRemark)) {
                    if (planRemark.length() > 600) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("规划说明最大长度只能到600位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 写入数据
                if (CollectionUtil.isEmpty(errMsgList)) {
                    // 明细表
                    planItem.setCategoryPlanCode(categoryPlanCode).setPlanRemark(planRemark)
                            .setBigClassSid(bigClassSid).setBigClassCode(bigClassCode)
                            .setMiddleClassSid(middleClassSid).setMiddleClassCode(middleClassCode)
                            .setSmallClassSid(smallClassSid).setSmallClassCode(smallClassCode)
                            .setPlanQuantity(planQuantity).setPlanType(planType).setGroupType(groupType)
                            .setNextReceiverSid(nextReceiverSid).setNextReceiverCode(nextReceiverCode);
                    planItem.setMaterialClassSid(smallClassSid).setMaterialClassCode(smallClassCode);
                    // 主表
                    if (map.containsKey(categoryPlanCode)) {
                        plan = map.get(categoryPlanCode);
                        List<DevCategoryPlanItem> itemList = plan.getCategoryPlanItemList();
                        if (CollectionUtil.isNotEmpty(itemList)) {
                            planItem.setCategoryPlanItemNum(itemList.get(itemList.size()-1).getCategoryPlanItemNum()+1);
                        }
                        else {
                            itemList = new ArrayList<>();
                            planItem.setCategoryPlanItemNum(1);
                        }
                        itemList.add(planItem);
                        plan.setCategoryPlanItemList(itemList);
                        map.put(categoryPlanCode, plan);
                    }
                    else {
                        plan = new DevCategoryPlan();
                        plan.setCategoryPlanCode(categoryPlanCode).setYear(year).setImportType(BusinessType.IMPORT.getValue())
                                .setCompanySid(companySid).setCompanyCode(companyCode)
                                .setBrandCode(brandCode).setProductSeasonSid(productSeasonSid).setHandleStatus(ConstantsEms.SAVA_STATUS);
                        List<DevCategoryPlanItem> itemList = new ArrayList<>();
                        planItem.setCategoryPlanItemNum(1);
                        itemList.add(planItem);
                        plan.setCategoryPlanItemList(itemList);
                        map.put(categoryPlanCode, plan);
                    }
                }
            }

            for (String key : map.keySet()) {
                categoryPlanList.add(map.get(key));
            }

            // 报错
            if (CollectionUtil.isNotEmpty(errMsgList)) {
                return EmsResultEntity.error(errMsgList);
            }
            else if (CollectionUtil.isNotEmpty(warnMsgList)) {
                String message = null;
                if (CollectionUtil.isNotEmpty(infoMsgList)) {
                    message = "导入成功" + categoryPlanList.size() + "数据，系统中负责人姓名存在重复" + staff + "条";
                }
                return EmsResultEntity.warning(categoryPlanList, warnMsgList, infoMsgList, message);
            }
            else if (CollectionUtil.isNotEmpty(categoryPlanList)) {
                for (DevCategoryPlan categoryPlan : categoryPlanList) {
                    categoryPlan.setCategoryPlanSid(IdWorker.getId());
                    insertDevCategoryPlan(categoryPlan);
                }
                String message = null;
                if (CollectionUtil.isNotEmpty(infoMsgList)) {
                    message = "导入成功" + categoryPlanList.size() + "数据，系统中负责人姓名存在重复" + staff + "条";
                }
                return EmsResultEntity.success(categoryPlanList.size(), null, infoMsgList, message);
            }

        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return EmsResultEntity.success();
    }

    public void copy(List<Object> objects, List<List<Object>> readAll){
        //获取第一行的列数
        int size = readAll.get(0).size();
        //当前行的列数
        int lineSize = objects.size();
        ArrayList<Object> all = new ArrayList<>();
        for (int i=lineSize;i<size;i++){
            Object o = new Object();
            o=null;
            objects.add(o);
        }
    }
}
