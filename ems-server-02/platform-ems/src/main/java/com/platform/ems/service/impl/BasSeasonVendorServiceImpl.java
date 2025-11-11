package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.base.BaseException;
import com.platform.common.log.enums.BusinessType;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import org.springframework.stereotype.Service;
import com.platform.ems.util.MongodbUtil;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.constant.ConstantsEms;
import com.platform.common.utils.bean.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import com.platform.ems.service.IBasSeasonVendorService;

/**
 * 季度供应商Service业务层处理
 *
 * @author chenkw
 * @date 2023-04-13
 */
@Service
@SuppressWarnings("all")
public class BasSeasonVendorServiceImpl extends ServiceImpl<BasSeasonVendorMapper, BasSeasonVendor> implements IBasSeasonVendorService {
    @Autowired
    private BasSeasonVendorMapper basSeasonVendorMapper;
    @Autowired
    private BasCompanyMapper companyMapper;
    @Autowired
    private BasCompanyBrandMapper companyBrandMapper;
    @Autowired
    private BasVendorMapper vendorMapper;
    @Autowired
    private BasProductSeasonMapper productSeasonMapper;

    private static final String TITLE = "季度供应商";

    /**
     * 查询季度供应商
     *
     * @param seasonVendorSid 季度供应商ID
     * @return 季度供应商
     */
    @Override
    public BasSeasonVendor selectBasSeasonVendorById(Long seasonVendorSid) {
        BasSeasonVendor basSeasonVendor = basSeasonVendorMapper.selectBasSeasonVendorById(seasonVendorSid);
        MongodbUtil.find(basSeasonVendor);
        return basSeasonVendor;
    }

    /**
     * 查询季度供应商列表
     *
     * @param basSeasonVendor 季度供应商
     * @return 季度供应商
     */
    @Override
    public List<BasSeasonVendor> selectBasSeasonVendorList(BasSeasonVendor basSeasonVendor) {
        return basSeasonVendorMapper.selectBasSeasonVendorList(basSeasonVendor);
    }

    /**
     * 新增季度供应商
     * 需要注意编码重复校验
     *
     * @param basSeasonVendor 季度供应商
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasSeasonVendor(BasSeasonVendor basSeasonVendor) {
        // 校验是否已存在
        judgeRepeat(basSeasonVendor);
        // 写入字段编码
        setData(new BasSeasonVendor(), basSeasonVendor);
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(basSeasonVendor.getHandleStatus())) {
            basSeasonVendor.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = basSeasonVendorMapper.insert(basSeasonVendor);
        if (row > 0) {
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(new BasSeasonVendor(), basSeasonVendor);
            MongodbDeal.insert(basSeasonVendor.getSeasonVendorSid(), basSeasonVendor.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 写入字段编码
     * @param old
     * @param newOne
     */
    private void setData(BasSeasonVendor old, BasSeasonVendor newOne) {
        // 公司
        if (newOne.getCompanySid() != null && !newOne.getCompanySid().equals(old.getCompanySid())) {
            BasCompany company = companyMapper.selectById(newOne.getCompanySid());
            if (company != null) {
                newOne.setCompanyCode(company.getCompanyCode());
            } else {
                newOne.setCompanyCode(null);
            }
        } else if (newOne.getCompanySid() == null) {
            newOne.setCompanyCode(null);
        }
        // 公司品牌
        if (newOne.getCompanyBrandSid() != null && !newOne.getCompanyBrandSid().equals(old.getCompanyBrandSid())) {
            BasCompanyBrand companyBrand = companyBrandMapper.selectById(newOne.getCompanyBrandSid());
            if (companyBrand != null) {
                newOne.setCompanyBrandCode(companyBrand.getBrandCode());
            } else {
                newOne.setCompanyBrandCode(null);
            }
        } else if (newOne.getCompanyBrandSid() == null) {
            newOne.setCompanyBrandCode(null);
        }
        // 供应商
        if (newOne.getVendorSid() != null && !newOne.getVendorSid().equals(old.getVendorSid())) {
            BasVendor vendor = vendorMapper.selectById(newOne.getVendorSid());
            if (vendor != null && vendor.getVendorCode() != null) {
                newOne.setVendorCode(vendor.getVendorCode().toString());
            } else {
                newOne.setVendorCode(null);
            }
        } else if (newOne.getVendorSid() == null) {
            newOne.setVendorCode(null);
        }
        // 产品季
        if (newOne.getProductSeasonSid() != null && !newOne.getProductSeasonSid().equals(old.getProductSeasonSid())) {
            BasProductSeason season = productSeasonMapper.selectById(newOne.getProductSeasonSid());
            if (season != null) {
                newOne.setProductSeasonCode(season.getProductSeasonCode());
            } else {
                newOne.setProductSeasonCode(null);
            }
        } else if (newOne.getProductSeasonSid() == null) {
            newOne.setProductSeasonCode(null);
        }
    }

    /*
     * 校验是否已存在
     */
    private void judgeRepeat(BasSeasonVendor seasonVendor) {
        QueryWrapper<BasSeasonVendor> queryWrapper = new QueryWrapper<>();
        if (seasonVendor.getSeasonVendorSid() != null) {
            // 去掉与本身校验
            queryWrapper.lambda().ne(BasSeasonVendor::getSeasonVendorSid, seasonVendor.getSeasonVendorSid());
        }
        if (seasonVendor.getProductSeasonSid() != null) {
            queryWrapper.lambda().eq(BasSeasonVendor::getProductSeasonSid, seasonVendor.getProductSeasonSid());
        } else {
            queryWrapper.lambda().isNull(BasSeasonVendor::getProductSeasonSid);
        }
        if (seasonVendor.getVendorSid() != null) {
            queryWrapper.lambda().eq(BasSeasonVendor::getVendorSid, seasonVendor.getVendorSid());
        } else {
            queryWrapper.lambda().isNull(BasSeasonVendor::getVendorSid);
        }
        if (seasonVendor.getCompanySid() != null) {
            queryWrapper.lambda().eq(BasSeasonVendor::getCompanySid, seasonVendor.getCompanySid());
        } else {
            queryWrapper.lambda().isNull(BasSeasonVendor::getCompanySid);
        }
        if (seasonVendor.getCompanyBrandSid() != null) {
            queryWrapper.lambda().eq(BasSeasonVendor::getCompanyBrandSid, seasonVendor.getCompanyBrandSid());
        } else {
            queryWrapper.lambda().isNull(BasSeasonVendor::getCompanyBrandSid);
        }
        List<BasSeasonVendor> existList = basSeasonVendorMapper.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(existList)) {
            throw new BaseException("该季度供应商已存在，请核实！");
        }
    }

    /**
     * 修改季度供应商
     *
     * @param basSeasonVendor 季度供应商
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasSeasonVendor(BasSeasonVendor basSeasonVendor) {
        // 校验是否已存在
        judgeRepeat(basSeasonVendor);
        // 获取原数据
        BasSeasonVendor original = basSeasonVendorMapper.selectBasSeasonVendorById(basSeasonVendor.getSeasonVendorSid());
        // 写入字段编码
        setData(original, basSeasonVendor);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(original, basSeasonVendor);
        if (CollectionUtil.isNotEmpty(msgList)) {
            basSeasonVendor.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(basSeasonVendor.getHandleStatus())) {
            basSeasonVendor.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = basSeasonVendorMapper.updateById(basSeasonVendor);
        if (row > 0) {
            //插入日志
            MongodbDeal.update(basSeasonVendor.getSeasonVendorSid(), original.getHandleStatus(), basSeasonVendor.getHandleStatus(), msgList, TITLE, null);
        }
        return row;
    }

    /**
     * 变更季度供应商
     *
     * @param basSeasonVendor 季度供应商
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasSeasonVendor(BasSeasonVendor basSeasonVendor) {
        // 校验是否已存在
        judgeRepeat(basSeasonVendor);
        // 获取原数据
        BasSeasonVendor response = basSeasonVendorMapper.selectBasSeasonVendorById(basSeasonVendor.getSeasonVendorSid());
        // 写入字段编码
        setData(response, basSeasonVendor);
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, basSeasonVendor);
        if (CollectionUtil.isNotEmpty(msgList)) {
            basSeasonVendor.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = basSeasonVendorMapper.updateById(basSeasonVendor);
        if (row > 0) {
            //插入日志
            MongodbUtil.insertUserLog(basSeasonVendor.getSeasonVendorSid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
        }
        return row;
    }

    /**
     * 批量删除季度供应商
     *
     * @param seasonVendorSids 需要删除的季度供应商ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasSeasonVendorByIds(List<Long> seasonVendorSids) {
        List<BasSeasonVendor> list = basSeasonVendorMapper.selectList(new QueryWrapper<BasSeasonVendor>()
                .lambda().in(BasSeasonVendor::getSeasonVendorSid, seasonVendorSids));
        int row = basSeasonVendorMapper.deleteBatchIds(seasonVendorSids);
        if (row > 0) {
            // 操作日志
            list.forEach(o -> {
                List<OperMsg> msgList = new ArrayList<>();
                msgList = BeanUtils.eq(o, new BasSeasonVendor());
                MongodbUtil.insertUserLog(o.getSeasonVendorSid(), BusinessType.DELETE.getValue(), msgList, TITLE);
            });
        }
        return row;
    }

    /**
     * 启用/停用
     *
     * @param basSeasonVendor
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(BasSeasonVendor basSeasonVendor) {
        int row = 0;
        Long[] sids = basSeasonVendor.getSeasonVendorSidList();
        if (sids != null && sids.length > 0) {
            row = basSeasonVendorMapper.update(null, new UpdateWrapper<BasSeasonVendor>().lambda()
                    .set(BasSeasonVendor::getStatus, basSeasonVendor.getStatus())
                    .in(BasSeasonVendor::getSeasonVendorSid, sids));
            for (Long id : sids) {
                //插入日志
                MongodbDeal.status(id, basSeasonVendor.getStatus(), null, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 更改确认状态
     *
     * @param basSeasonVendor
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(BasSeasonVendor basSeasonVendor) {
        int row = 0;
        Long[] sids = basSeasonVendor.getSeasonVendorSidList();
        if (sids != null && sids.length > 0) {
            LambdaUpdateWrapper<BasSeasonVendor> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.in(BasSeasonVendor::getSeasonVendorSid, sids);
            updateWrapper.set(BasSeasonVendor::getHandleStatus, basSeasonVendor.getHandleStatus());
            if (ConstantsEms.CHECK_STATUS.equals(basSeasonVendor.getHandleStatus())) {
                updateWrapper.set(BasSeasonVendor::getConfirmDate, new Date());
                updateWrapper.set(BasSeasonVendor::getConfirmerAccount, ApiThreadLocalUtil.get().getUsername());
            }
            row = basSeasonVendorMapper.update(null, updateWrapper);
            for (Long id : sids) {
                //插入日志
                MongodbDeal.check(id, basSeasonVendor.getHandleStatus(), null, TITLE, null);
            }
        }
        return row;
    }

    /**
     * 设置是否快反供应商
     *
     * @param basSeasonVendor
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setKuaiFan(BasSeasonVendor basSeasonVendor) {
        if (basSeasonVendor.getSeasonVendorSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<BasSeasonVendor> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        if (StrUtil.isBlank(basSeasonVendor.getIsKuaifanVendor())) {
            throw new BaseException("是否快反供应商不能为空！");
        }
        //是否快反供应商
        updateWrapper.in(BasSeasonVendor::getSeasonVendorSid, basSeasonVendor.getSeasonVendorSidList())
                .set(BasSeasonVendor::getIsKuaifanVendor, basSeasonVendor.getIsKuaifanVendor());
        row = basSeasonVendorMapper.update(null, updateWrapper);
        return row;
    }
}
