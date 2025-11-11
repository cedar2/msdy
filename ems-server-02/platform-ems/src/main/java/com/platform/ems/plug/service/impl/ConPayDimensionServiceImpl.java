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
import com.platform.ems.plug.domain.ConPayDimension;
import com.platform.ems.plug.mapper.ConPayDimensionMapper;
import com.platform.ems.plug.service.IConPayDimensionService;
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
 * 付款维度Service业务层处理
 *
 * @author chenkw
 * @date 2021-05-20
 */
@Service
@SuppressWarnings("all")
public class ConPayDimensionServiceImpl extends ServiceImpl<ConPayDimensionMapper, ConPayDimension> implements IConPayDimensionService {
    @Autowired
    private ConPayDimensionMapper conPayDimensionMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    private static final String TITLE = "付款维度";

    /**
     * 查询付款维度
     *
     * @param sid 付款维度ID
     * @return 付款维度
     */
    @Override
    public ConPayDimension selectConPayDimensionById(Long sid) {
        ConPayDimension conPayDimension = conPayDimensionMapper.selectConPayDimensionById(sid);
        MongodbUtil.find(conPayDimension);
        return conPayDimension;
    }

    /**
     * 查询付款维度列表
     *
     * @param conPayDimension 付款维度
     * @return 付款维度
     */
    @Override
    public List<ConPayDimension> selectConPayDimensionList(ConPayDimension conPayDimension) {
        return conPayDimensionMapper.selectConPayDimensionList(conPayDimension);
    }

    /**
     * 新增付款维度
     * 需要注意编码重复校验
     *
     * @param conPayDimension 付款维度
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertConPayDimension(ConPayDimension conPayDimension) {
        List<ConPayDimension> codeList = conPayDimensionMapper.selectList(new QueryWrapper<ConPayDimension>().lambda()
                .eq(ConPayDimension::getCode, conPayDimension.getCode()));
        if (CollectionUtil.isNotEmpty(codeList)) {
            throw new BaseException(ConstantsEms.CODE_REPETITION);
        }
        List<ConPayDimension> nameList = conPayDimensionMapper.selectList(new QueryWrapper<ConPayDimension>().lambda()
                .eq(ConPayDimension::getName, conPayDimension.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            throw new BaseException(ConstantsEms.NAME_REPETITION);
        }
        int row = conPayDimensionMapper.insert(conPayDimension);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            MongodbUtil.insertUserLog(conPayDimension.getSid(), BusinessType.INSERT.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 修改付款维度
     *
     * @param conPayDimension 付款维度
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateConPayDimension(ConPayDimension conPayDimension) {
        ConPayDimension response = conPayDimensionMapper.selectConPayDimensionById(conPayDimension.getSid());
        int row = conPayDimensionMapper.updateById(conPayDimension);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conPayDimension.getSid(), BusinessType.UPDATE.getValue(), response, conPayDimension, TITLE);
        }
        return row;
    }

    /**
     * 变更付款维度
     *
     * @param conPayDimension 付款维度
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeConPayDimension(ConPayDimension conPayDimension) {
        List<ConPayDimension> nameList = conPayDimensionMapper.selectList(new QueryWrapper<ConPayDimension>().lambda()
                .eq(ConPayDimension::getName, conPayDimension.getName()));
        if (CollectionUtil.isNotEmpty(nameList)) {
            nameList.forEach(o ->{
                if (!o.getSid().equals(conPayDimension.getSid())){
                    throw new BaseException(ConstantsEms.NAME_REPETITION);
                }
            });
        }
        conPayDimension.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername()).setUpdateDate(new Date())
                .setConfirmerAccount(ApiThreadLocalUtil.get().getUsername()).setConfirmDate(new Date());
        ConPayDimension response = conPayDimensionMapper.selectConPayDimensionById(conPayDimension.getSid());
        int row = conPayDimensionMapper.updateAllById(conPayDimension);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(conPayDimension.getSid(), BusinessType.CHANGE.getValue(), response, conPayDimension, TITLE);
        }
        return row;
    }

    /**
     * 批量删除付款维度
     *
     * @param sids 需要删除的付款维度ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConPayDimensionByIds(List<Long> sids) {
        return conPayDimensionMapper.deleteBatchIds(sids);
    }

    /**
     * 启用/停用
     *
     * @param conPayDimension
     * @return
     */
    @Override
    public int changeStatus(ConPayDimension conPayDimension) {
        int row = 0;
        Long[] sids = conPayDimension.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conPayDimension.setSid(id);
                row = conPayDimensionMapper.updateById(conPayDimension);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                String remark = conPayDimension.getStatus().equals(ConstantsEms.ENABLE_STATUS) ? "启用" : "停用";
                MongodbUtil.insertUserLog(conPayDimension.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE, remark);
            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param conPayDimension
     * @return
     */
    @Override
    public int check(ConPayDimension conPayDimension) {
        int row = 0;
        Long[] sids = conPayDimension.getSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                conPayDimension.setSid(id);
                row = conPayDimensionMapper.updateById(conPayDimension);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                List<OperMsg> msgList = new ArrayList<>();
                MongodbUtil.insertUserLog(conPayDimension.getSid(), BusinessType.CHECK.getValue(), msgList, TITLE);
            }
        }
        return row;
    }


}
