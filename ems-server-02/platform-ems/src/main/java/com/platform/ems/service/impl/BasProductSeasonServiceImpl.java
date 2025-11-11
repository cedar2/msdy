package com.platform.ems.service.impl;

import java.util.*;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.core.domain.document.UserOperLog;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.SecurityUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.mapper.BasProductSeasonMapper;
import com.platform.ems.domain.BasProductSeason;
import com.platform.ems.service.IBasProductSeasonService;

/**
 * 产品季档案Service业务层处理
 *
 * @author linhongwei
 * @date 2021-03-22
 */
@Service
@SuppressWarnings("all")
public class BasProductSeasonServiceImpl extends ServiceImpl<BasProductSeasonMapper, BasProductSeason> implements IBasProductSeasonService {
    @Autowired
    private BasProductSeasonMapper basProductSeasonMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    private static final String TITLE = "产品季档案";

    /**
     * 查询产品季档案
     *
     * @param productSeasonSid 产品季档案ID
     * @return 产品季档案
     */
    @Override
    public BasProductSeason selectBasProductSeasonById(Long productSeasonSid) {
        BasProductSeason basProductSeason = basProductSeasonMapper.selectBasProductSeasonById(productSeasonSid);
        //查询日志信息
        Query query = new Query();
        query.addCriteria(Criteria.where("sid").is(productSeasonSid));
        List<UserOperLog> userOperLogList = mongoTemplate.find(query, UserOperLog.class);
        basProductSeason.setOperLogList(userOperLogList);
        return basProductSeason;
    }

    /**
     * 查询产品季档案列表
     *
     * @param basProductSeason 产品季档案
     * @return 产品季档案
     */
    @Override
    public List<BasProductSeason> selectBasProductSeasonList(BasProductSeason basProductSeason) {
        return basProductSeasonMapper.selectBasProductSeasonList(basProductSeason);
    }

    @Override
    public List<BasProductSeason> getList(BasProductSeason basProductSeason) {
        return basProductSeasonMapper.getList(basProductSeason);
    }

    /**
     * 新增产品季档案
     * 需要注意编码重复校验
     *
     * @param basProductSeason 产品季档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasProductSeason(BasProductSeason basProductSeason) {
        Map<String, Object> params = new HashMap<>();
        params.put("product_season_name", basProductSeason.getProductSeasonName());
        List<BasProductSeason> queryList = basProductSeasonMapper.selectByMap(params);
        if (queryList.size() > 0) {
            throw new CustomException("名称已存在");
        }
        params = new HashMap<>();
        params.put("product_season_code", basProductSeason.getProductSeasonCode());
        queryList = basProductSeasonMapper.selectByMap(params);
        if (queryList.size() > 0) {
            throw new CustomException("编码已存在");
        }
        if (ConstantsEms.CHECK_STATUS.equals(basProductSeason.getHandleStatus())) {
            basProductSeason.setConfirmDate(new Date());
            basProductSeason.setConfirmerAccount(SecurityUtils.getUsername());
        }
        int row = basProductSeasonMapper.insert(basProductSeason);
        //待办通知
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(basProductSeason.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName("s_bas_product_season")
                    .setDocumentSid(basProductSeason.getProductSeasonSid());
            sysTodoTask.setTitle("产品季档案: " + basProductSeason.getProductSeasonCode() + " 当前是保存状态，请及时处理！")
                    .setDocumentCode(String.valueOf(basProductSeason.getProductSeasonCode()))
                    .setNoticeDate(new Date())
                    .setUserId(ApiThreadLocalUtil.get().getUserid());
            sysTodoTaskMapper.insert(sysTodoTask);
        }
        if (row > 0) {
            //插入日志
            String remark = null;
            MongodbDeal.insert(basProductSeason.getProductSeasonSid(), basProductSeason.getHandleStatus(), null, TITLE,remark);
        }
        return row;
    }

    /**
     * 修改产品季档案
     *
     * @param basProductSeason 产品季档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasProductSeason(BasProductSeason basProductSeason) {
        BasProductSeason old = basProductSeasonMapper.selectById(basProductSeason.getProductSeasonSid());
        Map<String, Object> params = new HashMap<>();
        params.put("product_season_name", basProductSeason.getProductSeasonName());
        List<BasProductSeason> queryResult = basProductSeasonMapper.selectByMap(params);
        if (queryResult.size() > 0) {
            for (BasProductSeason season : queryResult) {
                if (season.getProductSeasonName().equals(basProductSeason.getProductSeasonName()) && !season.getProductSeasonSid().equals(basProductSeason.getProductSeasonSid())) {
                    throw new CustomException("名称重复,请查看");
                }
            }
        }
        basProductSeason.setUpdateDate(new Date());
        basProductSeason.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        if (ConstantsEms.CHECK_STATUS.equals(basProductSeason.getHandleStatus())) {
            basProductSeason.setConfirmDate(new Date());
            basProductSeason.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = basProductSeasonMapper.updateAllById(basProductSeason);
        //确认状态后删除待办
        if (!ConstantsEms.SAVA_STATUS.equals(basProductSeason.getHandleStatus())){
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, basProductSeason.getProductSeasonSid()));
        }
        if (row > 0) {
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(old, basProductSeason);
            String remark = null;
            MongodbDeal.update(basProductSeason.getProductSeasonSid(), old.getHandleStatus(), basProductSeason.getHandleStatus(), msgList, TITLE, remark);
        }
        return row;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasProductSeason(BasProductSeason basProductSeason) {
        BasProductSeason old = basProductSeasonMapper.selectById(basProductSeason.getProductSeasonSid());
        Map<String, Object> params = new HashMap<>();
        params.put("product_season_name", basProductSeason.getProductSeasonName());
        List<BasProductSeason> queryResult = basProductSeasonMapper.selectByMap(params);
        if (queryResult.size() > 0) {
            for (BasProductSeason season : queryResult) {
                if (season.getProductSeasonName().equals(basProductSeason.getProductSeasonName()) && !season.getProductSeasonSid().equals(basProductSeason.getProductSeasonSid())) {
                    throw new CustomException("名称重复,请查看");
                }
            }
        }
        basProductSeason.setUpdateDate(new Date());
        basProductSeason.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        if (ConstantsEms.CHECK_STATUS.equals(basProductSeason.getHandleStatus())) {
            basProductSeason.setConfirmDate(new Date());
            basProductSeason.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = basProductSeasonMapper.updateAllById(basProductSeason);
        if (row > 0) {
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(old, basProductSeason);
            String remark = null;
            MongodbDeal.update(basProductSeason.getProductSeasonSid(), old.getHandleStatus(), basProductSeason.getHandleStatus(), msgList, TITLE, remark);
        }
        return row;
    }

    /**
     * 批量删除产品季档案
     *
     * @param clientIds 需要删除的产品季档案ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasProductSeasonByIds(List<String> productSeasonSid) {
        List<BasProductSeason> list = basProductSeasonMapper.selectList(new QueryWrapper<BasProductSeason>()
                .lambda().in(BasProductSeason::getProductSeasonSid,productSeasonSid));
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid, productSeasonSid));
        list.forEach(item->{
            //插入日志
            MongodbUtil.insertUserLog(Long.valueOf(item.getProductSeasonSid()), BusinessType.DELETE.getValue(), null, TITLE);
        });
        return basProductSeasonMapper.deleteBatchIds(productSeasonSid);
    }

    @Override
    public int changeStatus(BasProductSeason basProductSeason) {
        int row = 0;
        Long[] sids = basProductSeason.getProductSeasonSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                basProductSeason.setProductSeasonSid(id);
                row = basProductSeasonMapper.updateById(basProductSeason);
                if (row == 0) {
                    throw new CustomException(id + "更改状态失败,请联系管理员");
                }
                String remark = StrUtil.isEmpty(basProductSeason.getDisableRemark()) ? null : basProductSeason.getDisableRemark();
                MongodbDeal.status(Long.valueOf(id), basProductSeason.getStatus(), null, TITLE, remark);
            }
        }
        return row;
    }

    @Override
    public int check(BasProductSeason basProductSeason) {
        int row = 0;
        Long[] sids = basProductSeason.getProductSeasonSidList();
        if (sids != null && sids.length > 0) {
            for (Long id : sids) {
                basProductSeason.setProductSeasonSid(id);
                row = basProductSeasonMapper.updateById(basProductSeason);
                if (row == 0) {
                    throw new CustomException(id + "确认失败,请联系管理员");
                }
                //插入日志
                MongodbDeal.check(id, basProductSeason.getHandleStatus(), null,TITLE, null);
            }
        }
        //确认状态后删除待办
        if (!ConstantsEms.SAVA_STATUS.equals(basProductSeason.getHandleStatus())){
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, sids));
        }
        return row;
    }


}
