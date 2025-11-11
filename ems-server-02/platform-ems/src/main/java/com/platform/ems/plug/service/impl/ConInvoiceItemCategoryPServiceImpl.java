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
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.exception.CustomException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.platform.ems.plug.mapper.ConInvoiceItemCategoryPMapper;
import com.platform.ems.plug.domain.ConInvoiceItemCategoryP;
import com.platform.ems.plug.service.IConInvoiceItemCategoryPService;

/**
 * 类别_采购发票行项目Service业务层处理
 *
 * @author linhongwei
 * @date 2021-05-19
 */
@Service
@SuppressWarnings("all")
public class ConInvoiceItemCategoryPServiceImpl extends ServiceImpl<ConInvoiceItemCategoryPMapper, ConInvoiceItemCategoryP> implements IConInvoiceItemCategoryPService {
    @Autowired
    private ConInvoiceItemCategoryPMapper conInvoiceItemCategoryPMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "类别_采购发票行项目";

    /**
     * 查询类别_采购发票行项目
     *
     * @param sid 类别_采购发票行项目ID
     * @return 类别_采购发票行项目
     */
    @Override
    public ConInvoiceItemCategoryP selectConInvoiceItemCategoryPById(Long sid) {
        ConInvoiceItemCategoryP conInvoiceItemCategoryP = conInvoiceItemCategoryPMapper.selectConInvoiceItemCategoryPById(sid);
        MongodbUtil.find(conInvoiceItemCategoryP);
        return conInvoiceItemCategoryP;
    }

    /**
     * 查询类别_采购发票行项目列表
     *
     * @param conInvoiceItemCategoryP 类别_采购发票行项目
     * @return 类别_采购发票行项目
     */
    @Override
    public List<ConInvoiceItemCategoryP> selectConInvoiceItemCategoryPList(ConInvoiceItemCategoryP conInvoiceItemCategoryP) {
        return conInvoiceItemCategoryPMapper.selectConInvoiceItemCategoryPList(conInvoiceItemCategoryP);
    }

    /**
     * 新增类别_采购发票行项目
     * 需要注意编码重复校验
     *
     * @param conInvoiceItemCategoryP 类别_采购发票行项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConInvoiceItemCategoryP(ConInvoiceItemCategoryP conInvoiceItemCategoryP) {
        List<ConInvoiceItemCategoryP> codeList = conInvoiceItemCategoryPMapper.selectList(new QueryWrapper<ConInvoiceItemCategoryP>().lambda()
                .eq(ConInvoiceItemCategoryP::getCode, conInvoiceItemCategoryP.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConInvoiceItemCategoryP> nameList = conInvoiceItemCategoryPMapper.selectList(new QueryWrapper<ConInvoiceItemCategoryP>().lambda()
                .eq(ConInvoiceItemCategoryP::getName, conInvoiceItemCategoryP.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conInvoiceItemCategoryPMapper.insert(conInvoiceItemCategoryP);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conInvoiceItemCategoryP.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改类别_采购发票行项目
     *
     * @param conInvoiceItemCategoryP 类别_采购发票行项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConInvoiceItemCategoryP(ConInvoiceItemCategoryP conInvoiceItemCategoryP) {
        ConInvoiceItemCategoryP response = conInvoiceItemCategoryPMapper.selectConInvoiceItemCategoryPById(conInvoiceItemCategoryP.getSid());
        ConInvoiceItemCategoryP tempCode = conInvoiceItemCategoryPMapper.selectOne(new QueryWrapper<ConInvoiceItemCategoryP>().lambda().eq(ConInvoiceItemCategoryP::getCode, conInvoiceItemCategoryP.getCode()));
        if (tempCode != null && !conInvoiceItemCategoryP.getSid().equals(tempCode.getSid())) {
            throw new CustomException(conInvoiceItemCategoryP.getCode() + "：编码已存在");
        }
        ConInvoiceItemCategoryP tempName = conInvoiceItemCategoryPMapper.selectOne(new QueryWrapper<ConInvoiceItemCategoryP>().lambda().eq(ConInvoiceItemCategoryP::getName, conInvoiceItemCategoryP.getName()));
        if (tempName != null && !conInvoiceItemCategoryP.getSid().equals(tempName.getSid())) {
            throw new CustomException(conInvoiceItemCategoryP.getName() + "：名称已存在");
        }
        int row = conInvoiceItemCategoryPMapper.updateById(conInvoiceItemCategoryP);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conInvoiceItemCategoryP.getSid(), BusinessType.UPDATE.getValue(), response, conInvoiceItemCategoryP, TITLE);
        }
        return row;
    }

    /**
     * 变更类别_采购发票行项目
     *
     * @param conInvoiceItemCategoryP 类别_采购发票行项目
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConInvoiceItemCategoryP(ConInvoiceItemCategoryP conInvoiceItemCategoryP) {
        List<ConInvoiceItemCategoryP> nameList = conInvoiceItemCategoryPMapper.selectList(new QueryWrapper<ConInvoiceItemCategoryP>().lambda()
                .eq(ConInvoiceItemCategoryP::getName, conInvoiceItemCategoryP.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conInvoiceItemCategoryP.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conInvoiceItemCategoryP.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConInvoiceItemCategoryP response = conInvoiceItemCategoryPMapper.selectConInvoiceItemCategoryPById(conInvoiceItemCategoryP.getSid());
        int row = conInvoiceItemCategoryPMapper.updateAllById(conInvoiceItemCategoryP);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conInvoiceItemCategoryP.getSid(), BusinessType.CHANGE.getValue(), response, conInvoiceItemCategoryP, TITLE);
        }
        return row;
    }

    /**
     * 批量删除类别_采购发票行项目
     *
     * @param sids 需要删除的类别_采购发票行项目ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConInvoiceItemCategoryPByIds(List<Long> sids) {
        return conInvoiceItemCategoryPMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conInvoiceItemCategoryP
     * @return
     */
    @Override
    public int changeStatus(ConInvoiceItemCategoryP conInvoiceItemCategoryP) {
        int row = 0;
        Long[] sids = conInvoiceItemCategoryP.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conInvoiceItemCategoryP.setSid(id);
                row = conInvoiceItemCategoryPMapper.updateById(conInvoiceItemCategoryP);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conInvoiceItemCategoryP.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conInvoiceItemCategoryP.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conInvoiceItemCategoryP
     * @return
     */
    @Override
    public int check(ConInvoiceItemCategoryP conInvoiceItemCategoryP) {
        int row = 0;
        Long[] sids = conInvoiceItemCategoryP.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conInvoiceItemCategoryP.setSid(id);
                row = conInvoiceItemCategoryPMapper.updateById(conInvoiceItemCategoryP);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conInvoiceItemCategoryP.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }


}
