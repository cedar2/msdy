package com.platform.ems.plug.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.plug.mapper.ConInvoiceCategoryMapper;
import com.platform.ems.plug.domain.ConInvoiceCategory;
import com.platform.ems.plug.service.IConInvoiceCategoryService;

/**
 * 发票类别Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConInvoiceCategoryServiceImpl extends ServiceImpl<ConInvoiceCategoryMapper, ConInvoiceCategory> implements IConInvoiceCategoryService {
    @Autowired
    private ConInvoiceCategoryMapper conInvoiceCategoryMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "发票类别";

    /**
     * 查询发票类别
     *
     * @param sid 发票类别ID
     * @return 发票类别
     */
    @Override
    public ConInvoiceCategory selectConInvoiceCategoryById(Long sid) {
        ConInvoiceCategory conInvoiceCategory = conInvoiceCategoryMapper.selectConInvoiceCategoryById(sid);
        MongodbUtil.find(conInvoiceCategory);
        return conInvoiceCategory;
    }

    /**
     * 查询发票类别列表
     *
     * @param conInvoiceCategory 发票类别
     * @return 发票类别
     */
    @Override
    public List<ConInvoiceCategory> selectConInvoiceCategoryList(ConInvoiceCategory conInvoiceCategory) {
        return conInvoiceCategoryMapper.selectConInvoiceCategoryList(conInvoiceCategory);
    }

    /**
     * 新增发票类别
     * 需要注意编码重复校验
     *
     * @param conInvoiceCategory 发票类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConInvoiceCategory(ConInvoiceCategory conInvoiceCategory) {
        List<ConInvoiceCategory> codeList = conInvoiceCategoryMapper.selectList(new QueryWrapper<ConInvoiceCategory>().lambda()
                .eq(ConInvoiceCategory::getCode, conInvoiceCategory.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConInvoiceCategory> nameList = conInvoiceCategoryMapper.selectList(new QueryWrapper<ConInvoiceCategory>().lambda()
                .eq(ConInvoiceCategory::getName, conInvoiceCategory.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conInvoiceCategoryMapper.insert(conInvoiceCategory);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conInvoiceCategory.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改发票类别
     *
     * @param conInvoiceCategory 发票类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConInvoiceCategory(ConInvoiceCategory conInvoiceCategory) {
        ConInvoiceCategory response = conInvoiceCategoryMapper.selectConInvoiceCategoryById(conInvoiceCategory.getSid());
        List<ConInvoiceCategory> nameList = conInvoiceCategoryMapper.selectList(new QueryWrapper<ConInvoiceCategory>().lambda()
                .eq(ConInvoiceCategory::getName, conInvoiceCategory.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conInvoiceCategory.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        int row = conInvoiceCategoryMapper.updateById(conInvoiceCategory);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conInvoiceCategory.getSid(), BusinessType.UPDATE.getValue(), response, conInvoiceCategory, TITLE);
        }
        return row;
    }

    /**
     * 变更发票类别
     *
     * @param conInvoiceCategory 发票类别
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConInvoiceCategory(ConInvoiceCategory conInvoiceCategory) {
        List<ConInvoiceCategory> nameList = conInvoiceCategoryMapper.selectList(new QueryWrapper<ConInvoiceCategory>().lambda()
                .eq(ConInvoiceCategory::getName, conInvoiceCategory.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conInvoiceCategory.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conInvoiceCategory.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConInvoiceCategory response = conInvoiceCategoryMapper.selectConInvoiceCategoryById(conInvoiceCategory.getSid());
        int row = conInvoiceCategoryMapper.updateAllById(conInvoiceCategory);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conInvoiceCategory.getSid(), BusinessType.CHANGE.getValue(), response, conInvoiceCategory, TITLE);
        }
        return row;
    }

    /**
     * 批量删除发票类别
     *
     * @param sids 需要删除的发票类别ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConInvoiceCategoryByIds(List<Long> sids) {
        return conInvoiceCategoryMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conInvoiceCategory
     * @return
     */
    @Override
    public int changeStatus(ConInvoiceCategory conInvoiceCategory) {
        int row = 0;
        Long[] sids = conInvoiceCategory.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conInvoiceCategory.setSid(id);
                row = conInvoiceCategoryMapper.updateById(conInvoiceCategory);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conInvoiceCategory.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conInvoiceCategory.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conInvoiceCategory
     * @return
     */
    @Override
    public int check(ConInvoiceCategory conInvoiceCategory) {
        int row = 0;
        Long[] sids = conInvoiceCategory.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conInvoiceCategory.setSid(id);
                row = conInvoiceCategoryMapper.updateById(conInvoiceCategory);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conInvoiceCategory.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }

    //获取下拉框
    @Override
    public List<ConInvoiceCategory> getConInvoiceCategoryList() {
        return conInvoiceCategoryMapper.getConInvoiceCategoryList();
    }

}
