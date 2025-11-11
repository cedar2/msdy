package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.core.domain.entity.ConMaterialClass;
import com.platform.ems.mapper.ConMaterialClassMapper;
import com.platform.ems.service.IConMaterialClassService;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 物料分类Service业务层处理
 *
 * @author linhongwei
 * @date 2021-09-29
 */
@Service
@SuppressWarnings("all")
public class ConMaterialClassServiceImpl extends ServiceImpl<ConMaterialClassMapper, ConMaterialClass> implements IConMaterialClassService {
    @Autowired
    private ConMaterialClassMapper conMaterialClassMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "物料分类";

    /**
     * 查询物料分类
     *
     * @param materialClassSid 物料分类ID
     * @return 物料分类
     */
    @Override
    public ConMaterialClass selectConMaterialClassById(Long materialClassSid) {
        ConMaterialClass conMaterialClass = conMaterialClassMapper.selectConMaterialClassById(materialClassSid);
        MongodbUtil.find(conMaterialClass);
        return conMaterialClass;
    }

    /**
     * 查询物料分类列表
     *
     * @param conMaterialClass 物料分类
     * @return 物料分类
     */
    @Override
    public List<ConMaterialClass> selectConMaterialClassList(ConMaterialClass conMaterialClass) {
        if (StrUtil.isNotBlank(conMaterialClass.getMaterialType())) {
            return this.selectConMaterialClassListByMaterialType(conMaterialClass);
        }
        return conMaterialClassMapper.selectConMaterialClassList(conMaterialClass);
    }

    /**
     * 查询物料分类列表
     *
     * @param conMaterialClass 物料分类
     * @return 物料分类
     */
    @Override
    public List<ConMaterialClass> selectConMaterialClassListByMaterialType(ConMaterialClass conMaterialClass) {
        String clientId = ApiThreadLocalUtil.get().getClientId();
        List<ConMaterialClass> list = new ArrayList<>();
        List<ConMaterialClass> allList = new ArrayList<>();
        list = conMaterialClassMapper.selectList(new QueryWrapper<ConMaterialClass>().lambda()
                .eq(ConMaterialClass::getNodeType,conMaterialClass.getMaterialType())
                        .isNull(ConMaterialClass::getParentCodeSid)
                .eq(ConMaterialClass::getHandleStatus,ConstantsEms.CHECK_STATUS)
                .eq(ConMaterialClass::getStatus,ConstantsEms.ENABLE_STATUS));
        if (CollectionUtil.isNotEmpty(list)) {
            allList.addAll(list);
            for (ConMaterialClass materialClass : list) {
                List<ConMaterialClass> itemList = conMaterialClassMapper.selectConMaterialClassListByParentId(materialClass.getMaterialClassSid());
                if (CollectionUtil.isNotEmpty(itemList)) {
                    itemList = itemList.stream().filter(o-> clientId.equals(o.getClientId())
                            && ConstantsEms.CHECK_STATUS.equals(o.getHandleStatus())
                            && ConstantsEms.ENABLE_STATUS.equals(o.getStatus())).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(itemList)) {
                        allList.addAll(itemList);
                    }
                }
            }
        }
        return allList;
    }

    static <T> Predicate<T> distinctByField(Function<? super T,?> fieldExtractor){
        Map<Object,Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(fieldExtractor.apply(t), Boolean.TRUE) == null;
    }

    private List<ConMaterialClass> getList(List<ConMaterialClass> list){
        if (CollectionUtil.isNotEmpty(list)){
            List<Long> listSid = list.stream().map(ConMaterialClass::getMaterialClassSid).collect(Collectors.toList());
            List<ConMaterialClass> list2 = conMaterialClassMapper.selectList(new QueryWrapper<ConMaterialClass>().lambda()
                    .in(ConMaterialClass::getParentCodeSid,listSid));
            return list2;
        }
        return null;
    }

    /**
     * 新增物料分类
     * 需要注意编码重复校验
     *
     * @param conMaterialClass 物料分类
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConMaterialClass(ConMaterialClass conMaterialClass) {
        if (conMaterialClass.getLevel() > 4) {
            throw new BaseException("物料分类最多只允许维护4级");
        }
        Long parentCodeSid = conMaterialClass.getParentCodeSid();
        List<ConMaterialClass> nodeCodeList = conMaterialClassMapper.selectList(new QueryWrapper<ConMaterialClass>().lambda()
                .eq(ConMaterialClass::getNodeCode, conMaterialClass.getNodeCode()).eq(ConMaterialClass::getClientId, ApiThreadLocalUtil.get().getClientId()));
        if (CollectionUtil.isNotEmpty(nodeCodeList)) {
            throw new BaseException("同一个租户ID，物料分类编码不能重复");
        }
        if (parentCodeSid != null) {
            List<ConMaterialClass> nodeNameList = conMaterialClassMapper.selectList(new QueryWrapper<ConMaterialClass>().lambda()
                    .eq(ConMaterialClass::getNodeName, conMaterialClass.getNodeName())
                    .eq(ConMaterialClass::getParentCodeSid, parentCodeSid)
                    .eq(ConMaterialClass::getStatus, ConstantsEms.ENABLE_STATUS));
            if (CollectionUtil.isNotEmpty(nodeNameList)) {
                throw new BaseException("隶属同一个上级的同一级的名称不能重复");
            }
        } else {
            List<ConMaterialClass> nodeNameList = conMaterialClassMapper.selectList(new QueryWrapper<ConMaterialClass>().lambda()
                    .eq(ConMaterialClass::getNodeName, conMaterialClass.getNodeName())
                    .eq(ConMaterialClass::getStatus, ConstantsEms.ENABLE_STATUS));
            if (CollectionUtil.isNotEmpty(nodeNameList)) {
                nodeNameList.forEach(o -> {
                    if (o.getParentCodeSid() == null) {
                        throw new BaseException("已存在相同物料分类名称，请核实");
                    }
                });
            }
        }
        conMaterialClass.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        int row = conMaterialClassMapper.insert(conMaterialClass);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conMaterialClass.getMaterialClassSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改物料分类
     *
     * @param conMaterialClass 物料分类
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConMaterialClass(ConMaterialClass conMaterialClass) {
        ConMaterialClass response = conMaterialClassMapper.selectConMaterialClassById(conMaterialClass.getMaterialClassSid());
        int row = conMaterialClassMapper.updateById(conMaterialClass);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conMaterialClass.getMaterialClassSid(), BusinessType.UPDATE.getValue(), response, conMaterialClass, TITLE);
        }
        return row;
    }

    /**
     * 变更物料分类
     *
     * @param conMaterialClass 物料分类
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConMaterialClass(ConMaterialClass conMaterialClass) {
        int row = 0;
        Long parentCodeSid = conMaterialClass.getParentCodeSid();
        checkNameUnique(conMaterialClass, parentCodeSid);
        if (ConstantsEms.DISENABLE_STATUS.equals(conMaterialClass.getStatus())) {
            List<ConMaterialClass> childClassList = new ArrayList<>();
            List<Long> sidList = new ArrayList<>();
            sidList.add(conMaterialClass.getMaterialClassSid());
            row = batchDisable(conMaterialClass, sidList, childClassList);
        }
        List<ConMaterialClass> childClassList = new ArrayList<>();
        conMaterialClass.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConMaterialClass response = conMaterialClassMapper.selectConMaterialClassById(conMaterialClass.getMaterialClassSid());
        row = conMaterialClassMapper.updateAllById(conMaterialClass);
        if (row > 0) {
            // 变更物料类型时同步修改子类的物料类型
            if ((conMaterialClass.getMaterialType() != null && !conMaterialClass.getMaterialType().equals(response.getMaterialType()))
                    || (conMaterialClass.getMaterialType() == null && response.getMaterialType() != null)) {
                conMaterialClassMapper.update(null, new UpdateWrapper<ConMaterialClass>().lambda()
                        .set(ConMaterialClass::getMaterialType, conMaterialClass.getMaterialType())
                        .eq(ConMaterialClass::getParentCodeSid, conMaterialClass.getMaterialClassSid()));
            }
            //插入日志
            MongodbUtil.insertUserLog(conMaterialClass.getMaterialClassSid(), BusinessType.CHANGE.getValue(), response, conMaterialClass, TITLE);
        }
        return row;
    }

    private void checkNameUnique(ConMaterialClass conMaterialClass, Long parentCodeSid) {
        if (parentCodeSid != null) {
            List<ConMaterialClass> nodeNameList = conMaterialClassMapper.selectList(new QueryWrapper<ConMaterialClass>().lambda()
                    .eq(ConMaterialClass::getNodeName, conMaterialClass.getNodeName())
                    .eq(ConMaterialClass::getParentCodeSid, parentCodeSid)
                    .eq(ConMaterialClass::getStatus, ConstantsEms.ENABLE_STATUS));
            if (CollectionUtil.isNotEmpty(nodeNameList)) {
                nodeNameList.forEach(o -> {
                    if (!o.getMaterialClassSid().equals(conMaterialClass.getMaterialClassSid())) {
                        throw new BaseException("编码：" + o.getNodeCode() + "隶属同一个上级的同一级的名称不能重复");
                    }
                });
            }
        } else {
            List<ConMaterialClass> nodeNameList = conMaterialClassMapper.selectList(new QueryWrapper<ConMaterialClass>().lambda()
                    .eq(ConMaterialClass::getNodeName, conMaterialClass.getNodeName())
                    .eq(ConMaterialClass::getStatus, ConstantsEms.ENABLE_STATUS));
            if (CollectionUtil.isNotEmpty(nodeNameList)) {
                nodeNameList.forEach(o -> {
                    if (!o.getMaterialClassSid().equals(conMaterialClass.getMaterialClassSid()) && o.getParentCodeSid() == null) {
                        throw new BaseException("已存在相同物料分类名称，请核实");
                    }
                });
            }
        }
    }

    private void disable(ConMaterialClass conMaterialClass, Long id) {
        if (ConstantsEms.DISENABLE_STATUS.equals(conMaterialClass.getStatus())) {
            conMaterialClassMapper.update(null, new UpdateWrapper<ConMaterialClass>().lambda()
                    .set(ConMaterialClass::getStatus, conMaterialClass.getStatus())
                    .in(ConMaterialClass::getParentCodeSid, id));
            List<ConMaterialClass> materialClassList = conMaterialClassMapper.selectList(new QueryWrapper<ConMaterialClass>().lambda()
                    .eq(ConMaterialClass::getParentCodeSid, id));
            if (CollectionUtil.isNotEmpty(materialClassList)) {
                materialClassList.forEach(o -> {
                    conMaterialClassMapper.update(null, new UpdateWrapper<ConMaterialClass>().lambda()
                            .set(ConMaterialClass::getStatus, conMaterialClass.getStatus())
                            .in(ConMaterialClass::getParentCodeSid, o.getMaterialClassSid()));
                });
            }
        }
    }

    /**
     * 批量删除物料分类
     *
     * @param materialClassSids 需要删除的物料分类ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConMaterialClassByIds(List<Long> materialClassSids) {
        return conMaterialClassMapper.deleteBatchIds(materialClassSids);
    }

    /**
     * 启用/停用
     *
     * @param conMaterialClass
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(ConMaterialClass conMaterialClass) {
        int row = 0;
        Long[] sids = conMaterialClass.getMaterialClassSidList();
        List<Long> sidList = Arrays.asList(sids);
        List<ConMaterialClass> childClassList = new ArrayList<>();
        if (sids != null && sids.length > 0) {
            if (ConstantsEms.ENABLE_STATUS.equals(conMaterialClass.getStatus())) {
                for (Long sid : sids) {
                    ConMaterialClass materialClass = conMaterialClassMapper.selectConMaterialClassById(sid);
                    Long parentCodeSid = materialClass.getParentCodeSid();
                    checkNameUnique(materialClass, parentCodeSid);
                }
                row = conMaterialClassMapper.update(null, new UpdateWrapper<ConMaterialClass>().lambda().set(ConMaterialClass::getStatus, conMaterialClass.getStatus())
                        .in(ConMaterialClass::getMaterialClassSid, sids));
            } else {
                row = batchDisable(conMaterialClass, sidList, childClassList);
            }

            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conMaterialClass.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conMaterialClass.getMaterialClassSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }

        }
        return row;
    }

    /**
     * 批量停用
     */
    private int batchDisable(ConMaterialClass conMaterialClass, List<Long> sidList, List<ConMaterialClass> childClassList) {
        int row;
        List<ConMaterialClass> list = conMaterialClassMapper.selectConMaterialClassList(new ConMaterialClass());
        for (Long id : sidList) {
            List<ConMaterialClass> materialClassList = getChildren(id, list);
            childClassList.addAll(materialClassList);
        }
        List<Long> materialClassSids = childClassList.stream().map(ConMaterialClass::getMaterialClassSid).collect(Collectors.toList());
        materialClassSids.addAll(sidList);
        row = conMaterialClassMapper.update(null, new UpdateWrapper<ConMaterialClass>().lambda()
                .set(ConMaterialClass::getStatus, conMaterialClass.getStatus())
                .in(ConMaterialClass::getMaterialClassSid, materialClassSids));
        return row;
    }

    /**
     * 递归子节点
     */
    private List<ConMaterialClass> getChildren(Long sid, List<ConMaterialClass> list) {
        List<ConMaterialClass> materialClassList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)) {
            for (ConMaterialClass materialClass : list) {
                if (sid.equals(materialClass.getParentCodeSid())) {
                    //添加子级节点
                    materialClassList.add(materialClass);
                    //递归获取深层节点
                    materialClassList.addAll(getChildren(materialClass.getMaterialClassSid(), list));
                }
            }
        }
        return materialClassList;
    }

    /**
     * 更改确认状态
     *
     * @param conMaterialClass
     * @return
     */
    @Override
    public int check(ConMaterialClass conMaterialClass) {
        int row = 0;
        Long[] sids = conMaterialClass.getMaterialClassSidList();
        if (sids != null && sids.length > 0) {
            row = conMaterialClassMapper.update(null, new UpdateWrapper<ConMaterialClass>().lambda().set(ConMaterialClass::getHandleStatus, ConstantsEms.CHECK_STATUS)
                    .in(ConMaterialClass::getMaterialClassSid, sids));
            for (Long id : sids) {
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(id, BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    /**
     * 校验非同级是否存在同名
     */
    @Override
    public ConMaterialClass selectConMaterialClassByName(ConMaterialClass conMaterialClass) {
        List<ConMaterialClass> materialClassList = conMaterialClassMapper.selectList(new QueryWrapper<ConMaterialClass>().lambda()
                .eq(ConMaterialClass::getNodeName, conMaterialClass.getNodeName()).eq(ConMaterialClass::getStatus, ConstantsEms.ENABLE_STATUS));
        ConMaterialClass materialClass = new ConMaterialClass();
        if (CollectionUtil.isEmpty(materialClassList)) {
            materialClass.setCheckUnique("2");//不存在
        } else {
            materialClassList.forEach(o -> {
                if (!o.getLevel().equals(conMaterialClass.getLevel())) {
                    materialClass.setCheckUnique("1");//非同级名称相同
                }
                if (o.getParentCodeSid() != null && conMaterialClass.getParentCodeSid() != null) {
                    if (!o.getParentCodeSid().equals(conMaterialClass.getParentCodeSid())) {
                        materialClass.setCheckUnique("1");//同级但不同上级
                    }
                }
                if (conMaterialClass.getParentCodeSid() == null && o.getParentCodeSid() != null) {
                    materialClass.setCheckUnique("1");//存在
                }
            });
        }
        return materialClass;
    }

    /**
     * 获取物料分类下拉列表
     */
    @Override
    public List<ConMaterialClass> getConMaterialClassList(ConMaterialClass conMaterialClass) {
        return conMaterialClassMapper.getConMaterialClassList(conMaterialClass);
    }
}
