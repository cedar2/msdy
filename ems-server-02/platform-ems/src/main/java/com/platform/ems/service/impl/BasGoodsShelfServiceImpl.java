package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.TreeSelect;
import com.platform.common.core.domain.entity.ConMaterialClass;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.experimental.util.UniqueCheckUtil;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.constant.ConstantsTable;
import com.platform.ems.domain.*;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.HandleStatusInfoService;
import com.platform.ems.service.IBasGoodsShelfService;
import com.platform.ems.service.IConMaterialClassService;
import com.platform.ems.util.BuildTreeService;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

import static com.platform.common.redis.thread.ApiThreadLocalUtil.getLoginUserClientId;
import static java.util.stream.Collectors.toList;

/**
 * 货架档案Service业务层处理
 *
 * @author straw
 * @date 2023-02-02
 */
@SuppressWarnings({"DuplicatedCode", "SpringJavaAutowiredFieldsWarningInspection"})
@Service
public class BasGoodsShelfServiceImpl extends ServiceImpl<BasGoodsShelfMapper, BasGoodsShelf> implements IBasGoodsShelfService, HandleStatusInfoService {
    private static final String TITLE = "货架档案";

    @Resource
    BasGoodsShelfMapper shelfMapper;

    @Resource
    BasGoodsShelfMaterialClassMapper selfClassMapper;

    @Resource
    BasMaterialMapper materialMapper;

    @Resource
    SysTodoTaskMapper sysTodoTaskMapper;

    @Autowired
    IConMaterialClassService conMaterialClassService;

    @Resource
    BasStorehouseMapper storehouseMapper;
    @Resource
    BasStorehouseLocationMapper storehouseLocationMapper;

    @Resource
    ConMaterialClassMapper classMapper;

    /**
     * 修改货架档案
     *
     * @param basGoodsShelf 货架档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasGoodsShelf(BasGoodsShelf basGoodsShelf) {
        // 更新人信息
        this.setHandleStatusInfoWhenUpdate(basGoodsShelf);
        // 要保存仓库code、库位code、明细的物料分类code
        this.appendCodes(basGoodsShelf);
        // 更新明细
        this.updateAssociationTable(basGoodsShelf);

        BasGoodsShelf original = shelfMapper.selectBasGoodsShelfById(basGoodsShelf.getGoodsShelfSid());
        int row = shelfMapper.updateAllById(basGoodsShelf);
        if (row <= 0) {
            return row;
        }

        if (HandleStatus.isConfirmed(basGoodsShelf.getHandleStatus())) {
            // 删除待办
            deleteSysTodoTask(basGoodsShelf.getGoodsShelfSid());
        }

        // 插入日志
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, basGoodsShelf);
        MongodbDeal.update(basGoodsShelf.getGoodsShelfSid(),
                           original.getHandleStatus(),
                           basGoodsShelf.getHandleStatus(),
                           msgList,
                           TITLE,
                           null);
        return row;
    }

    private void updateAssociationTable(BasGoodsShelf shelf) {
        // 已经有的
        Set<Long> current = new HashSet<>(this.selfClassMapper.selectClassSidList(shelf.getGoodsShelfSid()));

        // 预期的
        List<ConMaterialClass> expectedClassList = shelf.getMaterialClassList();
        Set<Long> expected = expectedClassList
                .stream()
                .map(ConMaterialClass::getMaterialClassSid)
                .collect(Collectors.toSet());

        // 1. 找出需要增加的
        Set<Long> needToAdd = new HashSet<>(expected);
        needToAdd.removeAll(current);

        // 2. 找出需要删除的
        Collection<Long> needToDelete = new HashSet<>(current);
        needToDelete.removeAll(expected);

        // 3. 删除关联表的
        if (CollectionUtil.isNotEmpty(needToDelete)) {
            selfClassMapper.delete(new LambdaQueryWrapper<BasGoodsShelfMaterialClass>()
                                           .eq(BasGoodsShelfMaterialClass::getGoodsShelfSid,
                                               shelf.getGoodsShelfSid())
                                           .in(BasGoodsShelfMaterialClass::getMaterialClassSid,
                                               needToDelete));
        }

        // 4. 新增关联表的
        if (CollectionUtil.isNotEmpty(needToAdd)) {
            HashSet<ConMaterialClass> needToAddClassSet = new HashSet<>(expectedClassList);
            needToAddClassSet.removeIf(aClass -> !needToAdd.contains(aClass.getMaterialClassSid()));
            shelf.setMaterialClassList(new ArrayList<>(needToAddClassSet));
            this.createInsertBasGoodsShelfMaterialClass(shelf);
        }
    }

    /**
     * 变更货架档案
     *
     * @param basGoodsShelf 货架档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasGoodsShelf(BasGoodsShelf basGoodsShelf) {
        this.setHandleStatusInfoWhenUpdate(basGoodsShelf);
        this.appendCodes(basGoodsShelf);
        // 更新明细
        this.updateAssociationTable(basGoodsShelf);

        BasGoodsShelf response = shelfMapper.selectBasGoodsShelfById(basGoodsShelf.getGoodsShelfSid());
        int row = shelfMapper.updateAllById(basGoodsShelf);
        if (row > 0) {
            // 插入日志
            MongodbUtil.insertUserLog(basGoodsShelf.getGoodsShelfSid(),
                                      BusinessType.CHANGE.getValue(),
                                      response,
                                      basGoodsShelf,
                                      TITLE);
        }
        return row;
    }

    /**
     * 批量删除货架档案
     *
     * @param goodsShelfSids 需要删除的货架档案ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasGoodsShelfByIds(List<Long> goodsShelfSids) {
        List<BasGoodsShelf> list = shelfMapper.selectList(new QueryWrapper<BasGoodsShelf>()
                                                                  .lambda().in(BasGoodsShelf::getGoodsShelfSid,
                                                                               goodsShelfSids));
        int row = shelfMapper.deleteBatchIds(goodsShelfSids);

        if (row <= 0) {
            return row;
        }

        list.forEach(o -> {
            // 删除待办
            deleteSysTodoTask(o.getGoodsShelfSid());
            List<OperMsg> msgList = BeanUtils.eq(o, new BasGoodsShelf());
            MongodbUtil.insertUserLog(o.getGoodsShelfSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
        });
        return row;
    }

    /**
     * 启用/停用
     *
     * @param basGoodsShelf
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(BasGoodsShelf basGoodsShelf) {
        int row = 0;
        Long[] sids = basGoodsShelf.getGoodsShelfSidList();
        if (sids != null && sids.length > 0) {
            row = shelfMapper.update(null,
                                     new UpdateWrapper<BasGoodsShelf>().lambda().set(BasGoodsShelf::getStatus,
                                                                                     basGoodsShelf.getStatus())
                                                                       .in(BasGoodsShelf::getGoodsShelfSid,
                                                                           (Object[]) sids));
            if (row == 0) {
                throw new CustomException("更改状态失败,请联系管理员");
            }
            for (Long id : sids) {
                // 插入日志
                String remark = basGoodsShelf.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbDeal.status(id, basGoodsShelf.getStatus(), null, TITLE, remark);
            }
        }
        return row;
    }

    private void createInsertBasGoodsShelfMaterialClass(BasGoodsShelf shelf) {
        if (shelf.getGoodsShelfSid() == null) {
            throw new CheckedException("关联表插入时，缺失货架档案sid:" + shelf);
        }

        for (ConMaterialClass cmc : shelf.getMaterialClassList()) {
            if (cmc.getNodeCode() == null || cmc.getMaterialClassSid() == null) {
                throw new CheckedException("关联表插入时，缺失NodeCode或MaterialClassSid:" + cmc);
            }

            BasGoodsShelfMaterialClass detail =
                    new BasGoodsShelfMaterialClass()
                            .setGoodsShelfSid(shelf.getGoodsShelfSid())
                            .setGoodsShelfCode(shelf.getGoodsShelfCode())
                            .setMaterialClassSid(cmc.getMaterialClassSid())
                            .setMaterialClassCode(cmc.getNodeCode());
            this.setCreatorInfo(detail);
            selfClassMapper.insert(detail);
        }

    }

    /**
     * 查询货架档案
     *
     * @param goodsShelfSid 货架档案ID
     * @return 货架档案
     */
    @Override
    public BasGoodsShelf selectBasGoodsShelfById(Long goodsShelfSid) {
        BasGoodsShelf shelf = shelfMapper.selectBasGoodsShelfById(goodsShelfSid);
        appendMaterialInfo(shelf);
        return shelf;
    }

    private void createSysTodoTask(BasGoodsShelf basGoodsShelf) {
        SysTodoTask sysTodoTask = new SysTodoTask();
        sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                   .setTableName(ConstantsTable.TABLE_S_BAS_GOODS_SHELF)
                   .setDocumentSid(basGoodsShelf.getGoodsShelfSid())
                   .setTitle("货架档案" + basGoodsShelf.getGoodsShelfCode() + "当前是保存状态，请及时处理！")
                   .setDocumentCode(basGoodsShelf.getGoodsShelfCode())
                   .setNoticeDate(new Date())
                   .setUserId(ApiThreadLocalUtil.get().getUserid());
        sysTodoTaskMapper.insert(sysTodoTask);
    }

    private void deleteSysTodoTask(Long sid) {
        sysTodoTaskMapper.delete(Wrappers.lambdaQuery(SysTodoTask.class)
                                         .in(SysTodoTask::getDocumentSid,
                                             sid)
                                         .eq(SysTodoTask::getTaskCategory,
                                             ConstantsEms.TODO_TASK_DB)
                                         .eq(SysTodoTask::getTableName,
                                             ConstantsTable.TABLE_S_BAS_GOODS_SHELF));
    }

    /**
     * 新增货架档案
     * 需要注意编码重复校验
     *
     * @param basGoodsShelf 货架档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasGoodsShelf(BasGoodsShelf basGoodsShelf) {
        // 新增校验
        checkEntity(basGoodsShelf);
        // 创建人信息
        this.setHandleStatusInfoWhenNew(basGoodsShelf);
        // 要保存仓库code、库位code、明细的物料分类code
        this.appendCodes(basGoodsShelf);

        int row = shelfMapper.insert(basGoodsShelf);

        if (row <= 0) {
            return row;
        }

        // 插入关联表
        createInsertBasGoodsShelfMaterialClass(basGoodsShelf);

        // 待办
        if (ConstantsEms.SAVA_STATUS.equals(basGoodsShelf.getHandleStatus())) {
            createSysTodoTask(basGoodsShelf);
        }

        // 插入日志
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(new BasGoodsShelf(), basGoodsShelf);
        MongodbDeal.insert(basGoodsShelf.getGoodsShelfSid(), basGoodsShelf.getHandleStatus(), msgList, TITLE, null);

        return row;
    }

    /**
     * 更改确认状态
     *
     * @param basGoodsShelf
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(BasGoodsShelf basGoodsShelf) {
        Long[] sids = basGoodsShelf.getGoodsShelfSidList();
        if (ArrayUtil.isEmpty(sids)) {
            return 0;
        }

        int row;
        LambdaUpdateWrapper<BasGoodsShelf> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(BasGoodsShelf::getGoodsShelfSid, (Object[]) sids);
        updateWrapper.set(BasGoodsShelf::getHandleStatus, basGoodsShelf.getHandleStatus());
        if (ConstantsEms.CHECK_STATUS.equals(basGoodsShelf.getHandleStatus())) {
            updateWrapper.set(BasGoodsShelf::getConfirmDate, new Date());
            updateWrapper.set(BasGoodsShelf::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
        }
        row = shelfMapper.update(null, updateWrapper);
        if (row <= 0) {
            return row;
        }

        if (HandleStatus.isConfirmed(basGoodsShelf.getHandleStatus())) {
            // 删除待办
            for (Long sid : sids) {
                deleteSysTodoTask(sid);
            }
        }


        for (Long id : sids) {
            // 插入日志
            MongodbDeal.check(id, basGoodsShelf.getHandleStatus(), null, TITLE, null);
        }
        return row;
    }

    private void checkEntity(BasGoodsShelf obj) {
        // 货架编号
        // 文本字段，必填，可编辑
        // 默认显示提示文本：请输入
        // 不允许输入中文、空格
        String code = obj.getGoodsShelfCode();
        if (StrUtil.isBlank(code)) {
            throw new CheckedException("不允许输入中文、空格");
        }

        if (StrUtil.containsBlank(code)) {
            code = code.replace(" ", "")
                       .replace("　", "")
                       .replace("\t", "")
                       .replace("\n", "");
            obj.setGoodsShelfCode(code);
        }

        // 唯一性校验：校验该租户下货架编号是否已存在，提示：
        UniqueCheckUtil.checkUnique(
                obj,
                () -> this.shelfMapper.selectList(
                        new LambdaQueryWrapper<BasGoodsShelf>()
                                .eq(BasGoodsShelf::getGoodsShelfCode,
                                    obj.getGoodsShelfCode())
                                .eq(BasGoodsShelf::getClientId, getLoginUserClientId())
                ),
                BasGoodsShelf::getGoodsShelfSid,
                "货架编号已存在！"

        );

    }

    /**
     * 查询货架档案列表
     *
     * @param shelf 货架档案
     * @return 货架档案
     */
    @Override
    public List<BasGoodsShelf> selectBasGoodsShelfList(BasGoodsShelf shelf) {
        List<BasGoodsShelf> shelfList = shelfMapper.selectBasGoodsShelfList(shelf);
        shelfList = this.conditionFilter(shelf, shelfList);
        shelfList.forEach(this::appendMaterialInfo);
        return shelfList;
    }

    private void appendMaterialInfo(BasGoodsShelf shelf) {
        List<ConMaterialClass> list = shelf.getMaterialClassList();

        // 1. 设置物料名称
        if (CollectionUtil.isEmpty(list)) {
            shelf.setMaterialClassName("");
            return;
        }
        String joinResult = list.stream()
                                .map(ConMaterialClass::getNodeName)
                                .collect(Collectors.joining(";"));
        shelf.setMaterialClassName(joinResult);

        List<TreeSelect> collect = getTreeSelectList(new ConMaterialClass());
        // 2. 查询大类、中类、小类
        for (ConMaterialClass clazz : list) {
            setBigMiddleSmallClassId(clazz, collect);
        }

    }

    private void setBigMiddleSmallClassId(ConMaterialClass clazz, List<TreeSelect> collect) {
        Long clazzId = clazz.getMaterialClassSid();

        // clazz 对象，可能是大类/中类/小类
        // 层层遍历判断，通过 clazzId 判断是 大类/中类/小类
        for (TreeSelect bigClass : collect) {
            Long bigClassId = bigClass.getId();

            // 判断是不是大类
            if (bigClassId.equals(clazzId)) {
                // 是大类
                clazz.setBigClassSid(bigClassId);
                clazz.setMiddleClassSid(null);
                clazz.setSmallClassSid(null);
                // 方法结束，不会有中类和小类
                return;
            }

            // 遍历中类
            List<TreeSelect> middleClassList = bigClass.getChildren();
            if (CollectionUtil.isEmpty(middleClassList)) {
                continue;
            }

            for (TreeSelect middleClass : middleClassList) {
                if (clazzId.equals(middleClass.getId())) {
                    // 是中类
                    clazz.setBigClassSid(bigClassId);
                    clazz.setMiddleClassSid(middleClass.getId());
                    clazz.setSmallClassSid(null);
                    return;
                }
            }

            // 是小类
            for (TreeSelect middleClass : middleClassList) {
                List<TreeSelect> smallClassList = middleClass.getChildren();
                if (CollectionUtil.isEmpty(smallClassList)) {
                    continue;
                }
                for (TreeSelect smallClass : smallClassList) {
                    if (clazzId.equals(smallClass.getId())) {
                        // 是这个大类下的、这个中类的、小类
                        clazz.setBigClassSid(bigClassId);
                        clazz.setMiddleClassSid(middleClass.getId());
                        clazz.setSmallClassSid(smallClass.getId());
                        return;
                    }
                }
            }
        }
    }

    private List<TreeSelect> getTreeSelectList(ConMaterialClass query) {
        List<ConMaterialClass> clazzList = conMaterialClassService.selectConMaterialClassList(query);
        BuildTreeService<ConMaterialClass> buildTreeService = new BuildTreeService<>("materialClassSid",
                                                                                     "parentCodeSid",
                                                                                     "children");
        List<ConMaterialClass> trees = buildTreeService.buildTree(clazzList);
        return trees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    private List<BasGoodsShelf> conditionFilter(BasGoodsShelf shelf, List<BasGoodsShelf> shelfList) {
        Long sid = shelf.getMaterialClassSid();
        if (sid == null) {
            return shelfList;
        }

        List<BasGoodsShelf> ret = new ArrayList<>();
        for (BasGoodsShelf goodsShelf : shelfList) {

            if (CollectionUtil.isEmpty(goodsShelf.getMaterialClassList())) {
                continue;
            }
            boolean match = goodsShelf.getMaterialClassList()
                                      .stream()
                                      .anyMatch(clazz -> sid.equals(clazz.getMaterialClassSid()));
            if (match) {
                ret.add(goodsShelf);
            }
        }

        return ret;
    }

    private void appendCodes(BasGoodsShelf shelf) {
        if (shelf.getStorehouseSid() != null) {
            BasStorehouse storehouse = storehouseMapper.selectById(shelf.getStorehouseSid());
            shelf.setStorehouseCode(storehouse.getStorehouseCode());
        } else {
            shelf.setStorehouseCode(null);
        }

        if (shelf.getStorehouseLocationSid() != null) {
            BasStorehouseLocation storehouseLocation = storehouseLocationMapper.selectById(shelf.getStorehouseLocationSid());
            shelf.setLocationCode(storehouseLocation.getLocationCode());
        } else {
            shelf.setLocationCode(null);
        }

        List<ConMaterialClass> classList = shelf.getMaterialClassList();
        if (CollectionUtil.isEmpty(classList)) {
            return;
        }


        for (int i = 0; i < classList.size(); i++) {
            ConMaterialClass materialClass = classList.get(i);
            if (materialClass.getNodeCode() != null) {
                continue;
            }
            ConMaterialClass conMaterialClass = classMapper.selectConMaterialClassById(
                    materialClass.getMaterialClassSid()
            );
            classList.set(i, conMaterialClass);
        }
    }

    /**
     * 根据物料分类和仓库和库位获取货架编号多值用分号隔开
     * @param request
     * @return
     */
    @Override
    public List<InvInventoryDocumentItem> getCodes(List<InvInventoryDocumentItem> request) {
        List<Long> materialClassSidList = new ArrayList<>();
        List<BasMaterial> materialList = new ArrayList<>();
        List<Long> materialSidList = request.stream().map(InvInventoryDocumentItem::getMaterialSid).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(materialSidList)) {
            materialList = materialMapper.selectBatchIds(materialSidList);
            if (CollectionUtil.isNotEmpty(materialList)) {
                materialList = materialList.stream().filter(o->o.getMaterialClassSid() != null).collect(toList());
                if (CollectionUtil.isNotEmpty(materialList)) {
                    materialClassSidList = materialList.stream().map(BasMaterial::getMaterialClassSid).collect(Collectors.toList());
                }
            }
        }
        List<Long> storehouseLocationSidList = request.stream().map(InvInventoryDocumentItem::getStorehouseLocationSid).collect(Collectors.toList());
        // 如果入参找不到 物料分类 和 库位的信息 则不用找货架编码了
        if (CollectionUtil.isEmpty(materialClassSidList) || CollectionUtil.isEmpty(storehouseLocationSidList)) {
            return request;
        }
        // 将物料分类写进入参列表
        Map<Long, Long> materialAndClassMap = materialList.stream().collect(Collectors.toMap(BasMaterial::getMaterialSid, BasMaterial::getMaterialClassSid, (key1, key2) -> key2));
        for (int i = 0; i < request.size(); i++) {
            request.get(i).setMaterialClassSid(materialAndClassMap.get(request.get(i).getMaterialSid()));
        }
        // 先从货架档案和物料分类关联中找到 入参的物料分类所属的  货架档案sid
        List<BasGoodsShelfMaterialClass> materialClassList = selfClassMapper.selectBasGoodsShelfMaterialClassList(new BasGoodsShelfMaterialClass()
                .setMaterialClassSidList(materialClassSidList.toArray(new Long[materialClassSidList.size()]))
                .setStorehouseLocationSidList(storehouseLocationSidList.toArray(new Long[storehouseLocationSidList.size()])));
        if (CollectionUtil.isNotEmpty(materialClassList)) {
                    // 找到的 货架档案 结果  用 库位 分开，对比入参库位 找到对应的 货架编码 可能存在多个货架
            Map<String, List<BasGoodsShelfMaterialClass>> map = materialClassList.stream().collect(Collectors.groupingBy(o ->
                    String.valueOf(o.getStorehouseLocationSid()) + "-" + String.valueOf(o.getMaterialClassSid())));
            for (InvInventoryDocumentItem rsp : request) {
                String key = String.valueOf(rsp.getStorehouseLocationSid()) + "-" + String.valueOf(rsp.getMaterialClassSid());
                if (map.containsKey(key)) {
                    List<BasGoodsShelfMaterialClass> item = map.get(key);
                    item = item.stream().sorted(Comparator.comparing(BasGoodsShelfMaterialClass::getGoodsShelfCode)).collect(toList());
                    String code = "";
                    for (BasGoodsShelfMaterialClass result : item) {
                        code = code + result.getGoodsShelfCode() + ";";
                    }
                    if (StrUtil.isNotBlank(code)) {
                        code = code.substring(0, code.length()-1);
                    }
                    rsp.setGoodsShelfCodes(code);
                }
                else {
                    rsp.setGoodsShelfCodes("");
                }
            }
        }
        request = request.stream().sorted(
                Comparator.comparing(InvInventoryDocumentItem::getLocationName, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                        .thenComparing(InvInventoryDocumentItem::getGoodsShelfCodes, Comparator.nullsLast(String::compareTo))
                        .thenComparing(InvInventoryDocumentItem::getMaterialCode, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                        .thenComparing(InvInventoryDocumentItem::getMaterialName, Comparator.nullsLast(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                        .thenComparing(InvInventoryDocumentItem::getSku1Name, Comparator.nullsFirst(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
                        .thenComparing(InvInventoryDocumentItem::getSku2Name, Comparator.nullsFirst(String::compareTo).thenComparing(Collator.getInstance(Locale.CHINA)))
        ).collect(toList());
        return request;
    }
}
