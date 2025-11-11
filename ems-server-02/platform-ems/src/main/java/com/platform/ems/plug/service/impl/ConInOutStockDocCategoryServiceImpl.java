package com.platform.ems.plug.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConInOutStockDocCategory;
import com.platform.ems.plug.mapper.ConInOutStockDocCategoryMapper;
import com.platform.ems.plug.service.IConInOutStockDocCategoryService;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 出入库对应的单据类别Service业务层处理
 *
 * @author linhongwei
 * @date 2021-06-15
 */
@Service
@SuppressWarnings("all")
public class ConInOutStockDocCategoryServiceImpl extends ServiceImpl<ConInOutStockDocCategoryMapper, ConInOutStockDocCategory> implements IConInOutStockDocCategoryService {
    @Autowired
    private ConInOutStockDocCategoryMapper conInOutStockDocCategoryMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "出入库对应的单据类别";

    @Override
    public List<ConInOutStockDocCategory> getList() {
        return conInOutStockDocCategoryMapper.getList();
    }

    @Override
    public List<ConInOutStockDocCategory> getListCategory(String movementTypeCode) {
        List<ConInOutStockDocCategory> list = conInOutStockDocCategoryMapper.getListCategory(movementTypeCode);
        return list;
    }

    /**
     * 查询出入库对应的单据类别
     *
     * @param sid 出入库对应的单据类别ID
     * @return 出入库对应的单据类别
     */
    @Override
    public ConInOutStockDocCategory selectConInOutStockDocCategoryById(Long sid) {
        ConInOutStockDocCategory conInOutStockDocCategory = conInOutStockDocCategoryMapper.selectConInOutStockDocCategoryById(sid);
        MongodbUtil.find(conInOutStockDocCategory);
        return conInOutStockDocCategory;
    }

    /**
     * 查询出入库对应的单据类别列表
     *
     * @param conInOutStockDocCategory 出入库对应的单据类别
     * @return 出入库对应的单据类别
     */
    @Override
    public List<ConInOutStockDocCategory> selectConInOutStockDocCategoryList(ConInOutStockDocCategory conInOutStockDocCategory) {
        return conInOutStockDocCategoryMapper.selectConInOutStockDocCategoryList(conInOutStockDocCategory);
    }

    /**
     * 新增出入库对应的单据类别
     * 需要注意编码重复校验
     *
     * @param conInOutStockDocCategory 出入库对应的单据类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConInOutStockDocCategory(ConInOutStockDocCategory conInOutStockDocCategory) {
        List<ConInOutStockDocCategory> list =
                conInOutStockDocCategoryMapper.selectList(new QueryWrapper<ConInOutStockDocCategory>().lambda()
                        .eq(ConInOutStockDocCategory::getInvDocCategoryCode, conInOutStockDocCategory.getInvDocCategoryCode())
                        .eq(ConInOutStockDocCategory::getMovementTypeCode, conInOutStockDocCategory.getMovementTypeCode())
                        .eq(ConInOutStockDocCategory::getDocCategoryCode, conInOutStockDocCategory.getDocCategoryCode()));
        if (CollectionUtils.isNotEmpty(list)) {
            throw new BaseException("已存在相同配置档案，请核实");
        }
        int row = conInOutStockDocCategoryMapper.insert(conInOutStockDocCategory);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conInOutStockDocCategory.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改出入库对应的单据类别
     *
     * @param conInOutStockDocCategory 出入库对应的单据类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConInOutStockDocCategory(ConInOutStockDocCategory conInOutStockDocCategory) {
        ConInOutStockDocCategory response = conInOutStockDocCategoryMapper.selectConInOutStockDocCategoryById(conInOutStockDocCategory.getSid());
        int row = conInOutStockDocCategoryMapper.updateById(conInOutStockDocCategory);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conInOutStockDocCategory.getSid(), BusinessType.UPDATE.getValue(), response, conInOutStockDocCategory, TITLE);
        }
        return row;
    }

    /**
     * 变更出入库对应的单据类别
     *
     * @param conInOutStockDocCategory 出入库对应的单据类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConInOutStockDocCategory(ConInOutStockDocCategory conInOutStockDocCategory) {
        List<ConInOutStockDocCategory> list =
                conInOutStockDocCategoryMapper.selectList(new QueryWrapper<ConInOutStockDocCategory>().lambda()
                        .eq(ConInOutStockDocCategory::getInvDocCategoryCode, conInOutStockDocCategory.getInvDocCategoryCode())
                        .eq(ConInOutStockDocCategory::getMovementTypeCode, conInOutStockDocCategory.getMovementTypeCode())
                        .eq(ConInOutStockDocCategory::getDocCategoryCode, conInOutStockDocCategory.getDocCategoryCode()));
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(item ->{
                if (!conInOutStockDocCategory.getSid().equals(item.getSid())){
                    throw new BaseException("已存在相同配置档案，请核实");
                }
            });
        }
        conInOutStockDocCategory.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConInOutStockDocCategory response = conInOutStockDocCategoryMapper.selectConInOutStockDocCategoryById(conInOutStockDocCategory.getSid());
        int row = conInOutStockDocCategoryMapper.updateAllById(conInOutStockDocCategory);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conInOutStockDocCategory.getSid(), BusinessType.CHANGE.getValue(), response, conInOutStockDocCategory, TITLE);
        }
        return row;
    }

    /**
     * 批量删除出入库对应的单据类别
     *
     * @param sids 需要删除的出入库对应的单据类别ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConInOutStockDocCategoryByIds(List<Long> sids) {
        return conInOutStockDocCategoryMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conInOutStockDocCategory
     * @return
     */
    @Override
    public int changeStatus(ConInOutStockDocCategory conInOutStockDocCategory) {
        int row = 0;
        Long[] sids = conInOutStockDocCategory.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conInOutStockDocCategory.setSid(id);
                row = conInOutStockDocCategoryMapper.updateById(conInOutStockDocCategory);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conInOutStockDocCategory.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conInOutStockDocCategory.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conInOutStockDocCategory
     * @return
     */
    @Override
    public int check(ConInOutStockDocCategory conInOutStockDocCategory) {
        int row = 0;
        Long[] sids = conInOutStockDocCategory.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conInOutStockDocCategory.setSid(id);
                row = conInOutStockDocCategoryMapper.updateById(conInOutStockDocCategory);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conInOutStockDocCategory.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }


}
