package com.platform.ems.plug.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CustomException;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.plug.domain.ConInvoiceItemCategoryS;
import com.platform.ems.plug.mapper.ConInvoiceItemCategorySMapper;
import com.platform.ems.plug.service.IConInvoiceItemCategorySService;
import com.platform.ems.util.MongodbUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 类别_销售发票行项目Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@Service
@SuppressWarnings("all")
public class ConInvoiceItemCategorySServiceImpl extends ServiceImpl<ConInvoiceItemCategorySMapper, ConInvoiceItemCategoryS> implements IConInvoiceItemCategorySService {
    @Autowired
    private ConInvoiceItemCategorySMapper conInvoiceItemCategorySMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "类别_销售发票行项目";

    /**
     * 查询类别_销售发票行项目
     *
     * @param sid 类别_销售发票行项目ID
     * @return 类别_销售发票行项目
     */
    @Override
    public ConInvoiceItemCategoryS selectConInvoiceItemCategorySById(Long sid) {
        ConInvoiceItemCategoryS conInvoiceItemCategoryS = conInvoiceItemCategorySMapper.selectConInvoiceItemCategorySById(sid);
        MongodbUtil.find(conInvoiceItemCategoryS);
        return conInvoiceItemCategoryS;
    }

    /**
     * 查询类别_销售发票行项目列表
     *
     * @param conInvoiceItemCategoryS 类别_销售发票行项目
     * @return 类别_销售发票行项目
     */
    @Override
    public List<ConInvoiceItemCategoryS> selectConInvoiceItemCategorySList(ConInvoiceItemCategoryS conInvoiceItemCategoryS) {
        return conInvoiceItemCategorySMapper.selectConInvoiceItemCategorySList(conInvoiceItemCategoryS);
    }

    /**
     * 新增类别_销售发票行项目
     * 需要注意编码重复校验
     *
     * @param conInvoiceItemCategoryS 类别_销售发票行项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConInvoiceItemCategoryS(ConInvoiceItemCategoryS conInvoiceItemCategoryS) {
        List<ConInvoiceItemCategoryS> codeList = conInvoiceItemCategorySMapper.selectList(new QueryWrapper<ConInvoiceItemCategoryS>().lambda()
                .eq(ConInvoiceItemCategoryS::getCode, conInvoiceItemCategoryS.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConInvoiceItemCategoryS> nameList = conInvoiceItemCategorySMapper.selectList(new QueryWrapper<ConInvoiceItemCategoryS>().lambda()
                .eq(ConInvoiceItemCategoryS::getName, conInvoiceItemCategoryS.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conInvoiceItemCategorySMapper.insert(conInvoiceItemCategoryS);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conInvoiceItemCategoryS.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改类别_销售发票行项目
     *
     * @param conInvoiceItemCategoryS 类别_销售发票行项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConInvoiceItemCategoryS(ConInvoiceItemCategoryS conInvoiceItemCategoryS) {
        ConInvoiceItemCategoryS response = conInvoiceItemCategorySMapper.selectConInvoiceItemCategorySById(conInvoiceItemCategoryS.getSid());
        int row = conInvoiceItemCategorySMapper.updateById(conInvoiceItemCategoryS);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conInvoiceItemCategoryS.getSid(), BusinessType.UPDATE.getValue(), response, conInvoiceItemCategoryS, TITLE);
        }
        return row;
    }

    /**
     * 变更类别_销售发票行项目
     *
     * @param conInvoiceItemCategoryS 类别_销售发票行项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConInvoiceItemCategoryS(ConInvoiceItemCategoryS conInvoiceItemCategoryS) {
        List<ConInvoiceItemCategoryS> nameList = conInvoiceItemCategorySMapper.selectList(new QueryWrapper<ConInvoiceItemCategoryS>().lambda()
                .eq(ConInvoiceItemCategoryS::getName, conInvoiceItemCategoryS.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conInvoiceItemCategoryS.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conInvoiceItemCategoryS.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConInvoiceItemCategoryS response = conInvoiceItemCategorySMapper.selectConInvoiceItemCategorySById(conInvoiceItemCategoryS.getSid());
        int row = conInvoiceItemCategorySMapper.updateAllById(conInvoiceItemCategoryS);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conInvoiceItemCategoryS.getSid(), BusinessType.CHANGE.getValue(), response, conInvoiceItemCategoryS, TITLE);
        }
        return row;
    }

    /**
     * 批量删除类别_销售发票行项目
     *
     * @param sids 需要删除的类别_销售发票行项目ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConInvoiceItemCategorySByIds(List<Long> sids) {
        return conInvoiceItemCategorySMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conInvoiceItemCategoryS
     * @return
     */
    @Override
    public int changeStatus(ConInvoiceItemCategoryS conInvoiceItemCategoryS) {
        int row = 0;
        Long[] sids = conInvoiceItemCategoryS.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conInvoiceItemCategoryS.setSid(id);
                row = conInvoiceItemCategorySMapper.updateById(conInvoiceItemCategoryS);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conInvoiceItemCategoryS.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conInvoiceItemCategoryS.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conInvoiceItemCategoryS
     * @return
     */
    @Override
    public int check(ConInvoiceItemCategoryS conInvoiceItemCategoryS) {
        int row = 0;
        Long[] sids = conInvoiceItemCategoryS.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conInvoiceItemCategoryS.setSid(id);
                row = conInvoiceItemCategorySMapper.updateById(conInvoiceItemCategoryS);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conInvoiceItemCategoryS.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }


}
