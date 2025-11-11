package com.platform.ems.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.platform.common.utils.StringUtils;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.utils.SecurityUtils;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.*;
import com.platform.ems.util.MongodbDeal;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.platform.common.exception.base.BaseException;
import com.platform.common.exception.CheckedException;
import com.platform.common.exception.CustomException;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.core.redis.RedisCache;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.dto.request.BasButtonRequest;
import com.platform.ems.service.IBasCompanyService;
import com.platform.ems.util.MongodbUtil;
import com.platform.api.service.RemoteFlowableService;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 公司档案Service业务层处理
 *
 * @author hjj
 * @date 2021-01-22
 */
@Slf4j
@Service
@SuppressWarnings("all")
public class BasCompanyServiceImpl implements IBasCompanyService {
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private BasCompanyAttachMapper basCompanyAttachMapper;
    @Autowired
    private BasCompanyBrandMapper brandMapper;
    @Autowired
    private BasCompanyBrandMarkMapper markMapper;
    @Autowired
    private RemoteFlowableService remoteFlowableService;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisCache redisService;

    @Autowired
    private BasCompanyBankAccountMapper basCompanyBankAccountMapper;
    private static final String TITLE = "公司档案";

    /**
     * 查询公司档案
     *
     * @param companySid 公司档案ID
     * @return 公司档案
     */
    @Override
    public BasCompany selectBasCompanyById(Long companySid) {
        BasCompany basCompany = basCompanyMapper.selectBasCompanyById(companySid);
        BasCompanyBankAccount bank = new BasCompanyBankAccount();
        bank.setCompanySid(companySid);
        List<BasCompanyBankAccount> list = basCompanyBankAccountMapper.selectBasCompanyBankAccountList(bank);
        List<BasCompanyBankAccount> base = list.stream().filter(o->ConstantsEms.BANK_ACCOUNT_TYPE_JBH.equals(o.getAccountType())).collect(Collectors.toList());
        List<BasCompanyBankAccount> common = list.stream().filter(o->ConstantsEms.BANK_ACCOUNT_TYPE_YBH.equals(o.getAccountType())).collect(Collectors.toList());
        basCompany.setBasCompanyBankAccountList(common);
        basCompany.setBaseBankAccountList(base);
        if (basCompany != null) {
            //品牌
            List<BasCompanyBrand> brandList = brandMapper.selectBasCompanyBrandList(new BasCompanyBrand().setCompanySid(companySid));
            basCompany.setCompanyBrandList(brandList);
            //品标
            List<BasCompanyBrandMark> markList = markMapper.selectBasCompanyBrandMarkList(new BasCompanyBrandMark().setCompanySid(companySid));
            basCompany.setCompanyBrandMarkList(markList);
            //公司-附件对象
            BasCompanyAttach basCompanyAttach = new BasCompanyAttach();
            List<BasCompanyAttach> basCompanyAttachList = basCompanyAttachMapper.selectBasCompanyAttachList(new BasCompanyAttach().setCompanySid(companySid));
            basCompany.setAttachmentList(basCompanyAttachList);
            //查询日志信息
            MongodbUtil.find(basCompany);
        }
        return basCompany;
    }

    /**
     * 查询公司档案列表
     *
     * @param request 公司档案sid
     * @return 公司档案
     */
    @Override
    public List<BasCompany> selectBasCompanyList(BasCompany request) {
        return basCompanyMapper.selectBasCompanyList(request);
    }

    /**
     * 新增公司档案
     *
     * @param request 公司档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasCompany(BasCompany request) {
        if (basCompanyMapper.checkNameUnique(request.getCompanyName()) > 0) {
            throw new CheckedException("公司名称已存在，请核实！");
        }
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("company_code", request.getCompanyCode());
        List<BasCompany> queryResult = basCompanyMapper.selectByMap(queryParams);
        if (queryResult.size() > 0) {
            for (BasCompany item : queryResult) {
                if (item.getCompanyCode().equals(request.getCompanyCode()) && !item.getCompanySid().equals(request.getCompanySid())) {
                    throw new CustomException("公司代码已存在，请核实！");
                }
            }
        }
        queryParams.clear();
        queryResult.clear();
        queryParams.put("short_name", request.getShortName());
        queryResult = basCompanyMapper.selectByMap(queryParams);
        if (queryResult.size() > 0) {
            for (BasCompany item : queryResult) {
                if (item.getShortName().equals(request.getShortName()) && !item.getCompanySid().equals(request.getCompanySid())) {
                    throw new CustomException("公司简称已存在，请核实！");
                }
            }
        }
        if (ConstantsEms.CHECK_STATUS.equals(request.getHandleStatus())) {
            request.setConfirmDate(new Date());
            request.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        List<BasCompanyBankAccount> list = request.getBasCompanyBankAccountList();
        list.addAll(request.getBaseBankAccountList());
        if (CollectionUtil.isNotEmpty(list)) {
            for (BasCompanyBankAccount bankAccount : list) {
                if (StringUtils.isEmpty(bankAccount.getBankAccount())) {
                    continue;
                }
                bankAccount.setCompanySid(request.getCompanySid());
                bankAccount.setClientId(SecurityUtils.getClientId());
                basCompanyBankAccountMapper.insert(bankAccount);
            }
        }
        int row = basCompanyMapper.insert(request);
        if (row > 0) {
            //品标
            insertBrands(request.getCompanySid(), request.getCompanyBrandList());
            //品标
            insertMarks(request.getCompanySid(), request.getCompanyBrandMarkList());
            //公司-附件对象
            addBasCompanyAttachment(request);
            //待办通知
            if (ConstantsEms.SAVA_STATUS.equals(request.getHandleStatus())) {
                SysTodoTask sysTodoTask = new SysTodoTask();
                sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                        .setTableName("s_bas_company")
                        .setDocumentSid(request.getCompanySid());
                sysTodoTask.setTitle("公司档案: " + request.getCompanyCode() + " 当前是保存状态，请及时处理！")
                        .setDocumentCode(String.valueOf(request.getCompanyCode()))
                        .setNoticeDate(new Date())
                        .setUserId(ApiThreadLocalUtil.get().getUserid());
                sysTodoTaskMapper.insert(sysTodoTask);
            }
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbDeal.insert(Long.valueOf(request.getCompanySid()), request.getHandleStatus(), null, TITLE,null);
        }
        return row;
    }

    /**
     * 品牌表
     */
    private void insertBrands(Long companySid, List<BasCompanyBrand> brandList) {
        brandMapper.delete(
                new UpdateWrapper<BasCompanyBrand>()
                        .lambda()
                        .eq(BasCompanyBrand::getCompanySid, companySid)
        );
        if (CollectionUtils.isNotEmpty(brandList)) {
            brandList.forEach(m -> {
                m.setCompanySid(companySid);
            });
            brandMapper.inserts(brandList);
        }
    }

    /**
     * 品标表
     */
    private void insertMarks(Long companySid, List<BasCompanyBrandMark> markList) {
        markMapper.delete(
                new UpdateWrapper<BasCompanyBrandMark>()
                        .lambda()
                        .eq(BasCompanyBrandMark::getCompanySid, companySid)
        );
        if (CollectionUtils.isNotEmpty(markList)) {
            markList.forEach(m -> {
                m.setCompanySid(companySid);
            });
            markMapper.inserts(markList);
        }
    }


    private void validMark(BasCompanyBrandMark mark) {
        if (mark.getBrandMarkCode() == null) {
            throw new BaseException("品标编码不能为空");
        }
        if (StrUtil.isEmpty(mark.getBrandMarkName())) {
            throw new BaseException("品标名称不能为空");
        }
        if (mark.getStatus() == null) {
            throw new BaseException("品标状态不能为空");
        }
        BasCompanyBrandMark queryResult = markMapper.selectOne(new QueryWrapper<BasCompanyBrandMark>().lambda().eq(BasCompanyBrandMark::getBrandMarkCode, mark.getBrandMarkCode()));
        if (queryResult != null) {
            throw new BaseException("品标编码已存在,请修改后再试");
        }
        queryResult = markMapper.selectOne(new QueryWrapper<BasCompanyBrandMark>().lambda().eq(BasCompanyBrandMark::getBrandMarkName, mark.getBrandMarkName()));
        if (queryResult != null) {
            throw new BaseException("品标名称已存在,请修改后再试");
        }
    }

    /**
     * 修改公司档案
     *
     * @param request 公司档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int editBasCompany(BasCompany basCompany) {
        Long companySid = basCompany.getCompanySid();
        BasCompany response = basCompanyMapper.selectBasCompanyById(companySid);
        String companyName = response.getCompanyName();
        String status = response.getHandleStatus();
        //判断公司名称是否修改过
        if (!basCompany.getCompanyName().equals(companyName)) {
            checkNameUnique(basCompany);
        }
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("company_code", basCompany.getCompanyCode());
        List<BasCompany> queryResult = basCompanyMapper.selectByMap(queryParams);
        if (queryResult.size() > 0) {
            for (BasCompany item : queryResult) {
                if (item.getCompanyCode().equals(basCompany.getCompanyCode()) && !item.getCompanySid().equals(basCompany.getCompanySid())) {
                    throw new CustomException("公司代码已存在，请核实！");
                }
            }
        }
        queryParams.clear();
        queryResult.clear();
        queryParams.put("short_name", basCompany.getShortName());
        queryResult = basCompanyMapper.selectByMap(queryParams);
        if (queryResult.size() > 0) {
            for (BasCompany item : queryResult) {
                if (item.getShortName().equals(basCompany.getShortName()) && !item.getCompanySid().equals(basCompany.getCompanySid())) {
                    throw new CustomException("公司简称已存在，请核实！");
                }
            }
        }
        basCompanyBankAccountMapper.deleteBankAccountByCompanySid(basCompany.getCompanySid());
        List<BasCompanyBankAccount> bankList = basCompany.getBasCompanyBankAccountList();
        bankList.addAll(basCompany.getBaseBankAccountList());
        if (CollectionUtil.isNotEmpty(bankList)) {
            for (BasCompanyBankAccount bank : bankList) {
                if (StringUtils.isEmpty(bank.getBankAccount())) {
                    continue;
                }
                bank.setCompanySid(basCompany.getCompanySid());
                bank.setClientId(SecurityUtils.getClientId());
                basCompanyBankAccountMapper.insert(bank);
            }
        }
        basCompany.setUpdateDate(new Date());
        basCompany.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        int row = basCompanyMapper.updateBasCompany(basCompany);
        //品牌
        insertBrands(basCompany.getCompanySid(),basCompany.getCompanyBrandList());
        //品标
        insertMarks(basCompany.getCompanySid(),basCompany.getCompanyBrandMarkList());
        //公司-附件对象
        addBasCompanyAttachment(basCompany);
        //确认状态后删除待办
        if (!ConstantsEms.SAVA_STATUS.equals(basCompany.getHandleStatus())){
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, basCompany.getCompanySid()));
        }
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        msgList = BeanUtils.eq(response, basCompany);
        MongodbDeal.update(basCompany.getCompanySid(), response.getHandleStatus(), basCompany.getHandleStatus(), msgList, TITLE, null);
        return row;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(BasCompany basCompany) {
        basCompany.setConfirmDate(new Date());
        basCompany.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        basCompany.setHandleStatus(ConstantsEms.CHECK_STATUS);
        String[] sids = basCompany.getCompanySidList();
        if (ArrayUtil.isNotEmpty(sids)) {
            basCompanyMapper.update(null, new UpdateWrapper<BasCompany>()
                            .lambda()
                            .set(BasCompany::getHandleStatus, basCompany.getHandleStatus())
                            .set(BasCompany::getConfirmerAccount, basCompany.getConfirmerAccount())
                            .set(BasCompany::getConfirmDate, basCompany.getConfirmDate())
                            .in(BasCompany::getCompanySid, sids));
        }
        //确认状态后删除待办
        if (!ConstantsEms.SAVA_STATUS.equals(basCompany.getHandleStatus())){
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, sids));
        }
        for (String sid : sids) {
            //插入日志
            MongodbDeal.check(Long.parseLong(sid), basCompany.getHandleStatus(), null, TITLE, null);
            deleteCache(Long.valueOf(sid));
        }
        return sids.length;
    }

    @CacheEvict(value = "basCompany", key = "#sid")
    private void deleteCache(Long sid) {
        log.info("删除缓存:" + sid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeBasCompany(BasCompany request) {
        BasCompany response = basCompanyMapper.selectBasCompanyById(request.getCompanySid());
        String companyName = response.getCompanyName();
        String status = response.getHandleStatus();
        if (status == "3") {
            //判断公司名称是否修改过
            if (!request.getCompanyName().equals(companyName)) {
                checkNameUnique(request);
            }
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("company_code", request.getCompanyCode());
            List<BasCompany> queryResult = basCompanyMapper.selectByMap(queryParams);
            if (queryResult.size() > 0) {
                for (BasCompany item : queryResult) {
                    if (item.getCompanyCode().equals(request.getCompanyCode()) && !request.getCompanySid().equals(item.getCompanySid())) {
                        throw new CustomException("公司代码已存在，请核实！");
                    }
                }
            }
            queryParams.clear();
            queryResult.clear();
            queryParams.put("short_name", request.getShortName());
            queryResult = basCompanyMapper.selectByMap(queryParams);
            if (queryResult.size() > 0) {
                for (BasCompany item : queryResult) {
                    if (item.getShortName().equals(request.getShortName()) && !item.getCompanySid().equals(request.getCompanySid())) {
                        throw new CustomException("公司简称已存在，请核实！");
                    }
                }
            }
            int row = basCompanyMapper.updateBasCompany(request);
            //品牌
            insertBrands(request.getCompanySid(),request.getCompanyBrandList());
            //品标
            insertMarks(request.getCompanySid(),request.getCompanyBrandMarkList());
            //公司-附件对象
            addBasCompanyAttachment(request);
            //插入日志
            List<OperMsg> msgList = new ArrayList<>();
            msgList = BeanUtils.eq(response, request);
            MongodbUtil.insertUserLog(request.getCompanySid(), BusinessType.CHANGE.getValue(), msgList, TITLE);
//            redisService.deleteCacheDict(BasCompany.class, ApiThreadLocalUtil.get().getClientId());
            return row;
        }
        throw new CheckedException("该数据不可变更");
    }

    /**
     * 判断公司名称是否已存在
     */
    private void checkNameUnique(BasCompany request) {
        if (basCompanyMapper.checkNameUnique(request.getCompanyName()) > 0) {
            throw new CheckedException("公司名称已存在，请核实！");
        }
    }


    /**
     * 存为草稿修改处理状态为：保存  提交修改状态为：已提交
     */
    private void saveDraftOrSubmit(BasCompany request) {
        //提交
        if (ConstantsEms.CHECK_STATUS.equals(request.getHandleStatus())) {
            request.setConfirmDate(new Date());
            request.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
    }

    /**
     * 批量删除公司档案
     *
     * @param companySids 需要删除的公司档案ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasCompanyByIds(List<Long> companySids) {
        int row = 0;
        Map<String, Object> deleteMap = new HashMap<>();
        for (Long companySid : companySids) {
            BasCompany companyResponse = basCompanyMapper.selectBasCompanyById(companySid);
            if (!ConstantsEms.SAVA_STATUS.equals(companyResponse.getHandleStatus())) {
                throw new CheckedException("仅保存状态才允许删除");
            }
            MongodbUtil.insertUserLog(Long.valueOf(companySid), BusinessType.DELETE.getValue(), null, TITLE);
        }
        row = basCompanyMapper.delete(new QueryWrapper<BasCompany>().lambda().in(BasCompany::getCompanySid, companySids));
        if (row > 0) {
            //删除公司-品牌
            brandMapper.delete(
                    new UpdateWrapper<BasCompanyBrand>()
                            .lambda()
                            .in(BasCompanyBrand::getCompanySid, companySids)
            );
            //删除公司-品标
            markMapper.delete(
                    new UpdateWrapper<BasCompanyBrandMark>()
                            .lambda()
                            .in(BasCompanyBrandMark::getCompanySid, companySids)
            );
            //删除公司-附件清单
            basCompanyAttachMapper.delete(
                    new QueryWrapper<BasCompanyAttach>()
                            .lambda()
                            .in(BasCompanyAttach::getCompanySid, companySids)
            );
            companySids.forEach(id -> {
                deleteCache(id);
            });
            basCompanyBankAccountMapper.delete(new UpdateWrapper<BasCompanyBankAccount>().lambda().in(BasCompanyBankAccount::getCompanySid,companySids));

            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid, companySids));
        }
        return row;
    }

    /**
     * 批量启用/停用
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int editStatus(BasButtonRequest request) {
        int row = basCompanyMapper.editStatus(request);
        //更新品牌
        LambdaUpdateWrapper<BasCompanyBrand> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(BasCompanyBrand::getCompanySid,request.getIds()).set(BasCompanyBrand::getStatus, ConstantsEms.ENABLE_STATUS);
        brandMapper.update(null, updateWrapper);
        //更新品标
        LambdaUpdateWrapper<BasCompanyBrandMark> updateWrapper2 = new LambdaUpdateWrapper<>();
        updateWrapper2.in(BasCompanyBrandMark::getCompanySid,request.getIds()).set(BasCompanyBrandMark::getStatus, ConstantsEms.ENABLE_STATUS);
        markMapper.update(null, updateWrapper2);
        if (row == request.getIds().length) {
            for (int i = 0; i < request.getIds().length; i++) {
                String id = request.getIds()[i];
                //插入日志
                String remark = StrUtil.isEmpty(request.getDisableRemark()) ? null : request.getDisableRemark();
                MongodbDeal.status(Long.valueOf(id), request.getStatus(), null, TITLE, remark);
            }
//            redisService.deleteCacheDict(BasCompany.class, ApiThreadLocalUtil.get().getClientId());
        }
        return row;
    }


    /**
     * 批量导出
     */
    @Override
    public List<BasCompany> export(BasCompany request) {
        String[] companySids = request.getCompanySidList();
        if (companySids != null && companySids.length > 0) {
            return basCompanyMapper.selectBasCompanyListByIds(companySids);
        } else {
            return basCompanyMapper.selectBasCompanyList(request);
        }
    }

    /**
     * 公司档案下拉框列表
     */
    @Override
    public List<BasCompany> getCompanyList(BasCompany company) {
        return basCompanyMapper.getCompanyList(company);
    }

    @Override
    public List<BasCompanyBrand> getCompanyBrandList(String companySid) {
        List<BasCompanyBrand> basCompanyBrandList = brandMapper.selectList(new QueryWrapper<BasCompanyBrand>()
                .lambda()
                .eq(BasCompanyBrand::getCompanySid, companySid)
                .eq(BasCompanyBrand::getStatus, ConstantsEms.ENABLE_STATUS)
                .orderByAsc(BasCompanyBrand::getBrandName));
        return basCompanyBrandList;
    }

    @Override
    public List<BasCompanyBrand> getBrandList(BasCompanyBrand companyBrand) {
        return brandMapper.selectBasCompanyBrandList(companyBrand);
    }

    @Override
    public List<BasCompanyBrandMark> getCompanyBrandMarkList(Long brandSid) {
        List<BasCompanyBrandMark> markList = markMapper.selectList(new QueryWrapper<BasCompanyBrandMark>().lambda().eq(BasCompanyBrandMark::getCompanySid, brandSid).orderByAsc(BasCompanyBrandMark::getBrandMarkCode));
        return markList;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(BasCompany basCompany) {
        int row = 0;
        String[] sids = basCompany.getCompanySidList();
        if (sids != null && sids.length > 0) {
            row = basCompanyMapper.update(basCompany, new UpdateWrapper<BasCompany>().lambda().set(BasCompany::getStatus, basCompany.getStatus())
                    .in(BasCompany::getCompanySid, basCompany.getCompanySidList()));
            if (row != basCompany.getCompanySidList().length) {
                throw new MybatisPlusException("更改状态异常");
            }
            for (String id : sids) {
                basCompany.setCompanySid(Long.valueOf(id));
                //操作日志
                MongodbDeal.status(Long.valueOf(id), basCompany.getStatus(), null, TITLE, null);
                deleteCache(Long.valueOf(id));
            }
        }
        return row;
    }

    /**
     * 公司-附件对象
     */
    private void addBasCompanyAttachment(BasCompany basCompany) {
        basCompanyAttachMapper.delete(
                new UpdateWrapper<BasCompanyAttach>()
                        .lambda()
                        .eq(BasCompanyAttach::getCompanySid, basCompany.getCompanySid())
        );
        if (CollectionUtils.isNotEmpty(basCompany.getAttachmentList())) {
            basCompany.getAttachmentList().forEach(item -> {
                item.setCompanySid(basCompany.getCompanySid());
                basCompanyAttachMapper.insert(item);
            });
        }
    }
}
