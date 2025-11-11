package com.platform.ems.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.domain.*;
import com.platform.ems.mapper.*;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CheckedException;
import com.platform.common.utils.SecurityUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.service.IBasCustomerService;

/**
 * 客户档案Service业务层处理
 *
 * @author qhq
 * @date 2021-03-22
 */
@Service
@SuppressWarnings("all")
public class BasCustomerServiceImpl extends ServiceImpl<BasCustomerMapper, BasCustomer> implements IBasCustomerService {

    @Autowired
    private BasCustomerMapper basCustomerMapper;
    @Autowired
    private BasCustomerBrandMapper brandMapper;
    @Autowired
    private BasCustomerAddrMapper basCustomerAddrMapper;
    @Autowired
    private BasCustomerBrandMarkMapper markMapper;
    @Autowired
    private BasCustomerAttachMapper basCustomerAttachMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;

    private static final String TITLE = "客户档案";

    private static final String TITLE_ADDR = "客户档案-联系方式";

    /**
     * 查询客户档案
     *
     * @param clientId 客户档案ID
     * @return 客户档案
     */
    @Override
    public BasCustomer selectBasCustomerById(Long customerSid) {
        BasCustomer basCustomer = basCustomerMapper.selectBasCustomerById(customerSid);
        List<BasCustomerBrand> brandList = brandMapper.selectBasCustomerBrandByCustomerSid(customerSid);
        List<BasCustomerBrandMark> markList = markMapper.selectBasCustomerBrandMarkList(new BasCustomerBrandMark().setCustomerSid(customerSid));
        List<BasCustomerAddr> addrList = basCustomerAddrMapper.selectBasCustomerAddrList(new BasCustomerAddr().setCustomerSid(customerSid));
        //客户-附件对象
        BasCustomerAttach basCustomerAttach = new BasCustomerAttach();
        List<BasCustomerAttach> basCustomerAttachList = basCustomerAttachMapper.selectBasCustomerAttachList(
                new BasCustomerAttach().setCustomerSid(customerSid)
        );
        basCustomer.setAttachmentList(basCustomerAttachList);
        basCustomer.setBrandList(brandList);
        basCustomer.setMarkList(markList);
        basCustomer.setAddrList(addrList);
        //查询日志信息
        MongodbUtil.find(basCustomer);
        return basCustomer;
    }

    /**
     * 查询客户档案列表
     *
     * @param basCustomer 客户档案
     * @return 客户档案
     */
    @Override
    public List<BasCustomer> selectBasCustomerList(BasCustomer basCustomer) {
        List<BasCustomer> basCustomerList = basCustomerMapper.selectBasCustomerList(basCustomer);
        return basCustomerList;
    }

    /**
     * 新增客户档案
     * 需要注意编码重复校验
     *
     * @param basCustomer 客户档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasCustomer(BasCustomer basCustomer) {
        //判断“客户编码“在“客户档案”数据库表中是否存在
        List<BasCustomer> query = basCustomerMapper.selectList(new QueryWrapper<BasCustomer>().lambda().eq(BasCustomer::getCustomerCode, basCustomer.getCustomerCode()));
        if (query.size() > 0) {
            throw new CheckedException("客户编码已存在！");
        }
        //判断“客户名称“在“客户档案”数据库表中是否存在
        List<BasCustomer> query2 = basCustomerMapper.selectList(new QueryWrapper<BasCustomer>().lambda().eq(BasCustomer::getCustomerName, basCustomer.getCustomerName()));
        if (query2.size() > 0) {
            throw new CheckedException("客户名称已存在！");
        }
        QueryWrapper<BasCustomer> wrapper = new QueryWrapper<BasCustomer>();
        wrapper = new QueryWrapper<BasCustomer>();
        wrapper.eq("short_name", basCustomer.getShortName());
        List<BasCustomer> query3 = basCustomerMapper.selectList(wrapper);
        if (query3.size() > 0) {
            throw new CheckedException("客户简称重复，请查看");
        }
//        if (CollectionUtils.isNotEmpty(basCustomer.getBrandList())) {
//            for (BasCustomerBrand brand : basCustomer.getBrandList()) {
//                //验证客方品牌编码是否存在
//                List<BasCustomerBrand> brandQuery = brandMapper.selectList(new QueryWrapper<BasCustomerBrand>().lambda().eq(BasCustomerBrand::getBrandCode, brand.getBrandCode()));
//                if (brandQuery.size() > 0) {
//                    throw new CheckedException("客方品牌编码已存在！");
//                }
//                List<BasCustomerBrand> brandQuery2 = brandMapper.selectList(new QueryWrapper<BasCustomerBrand>().lambda().eq(BasCustomerBrand::getBrandName, brand.getBrandName()));
//                if (brandQuery2.size() > 0) {
//                    throw new CheckedException("品牌名称已存在！");
//                }
//            }
//        }
        if (ConstantsEms.CHECK_STATUS.equals(basCustomer.getHandleStatus())) {
            basCustomer.setConfirmDate(new Date());
            basCustomer.setConfirmerAccount(SecurityUtils.getUsername());
        }
        int row = basCustomerMapper.insert(basCustomer);
        if (0 == row) {
            throw new CheckedException("新增客户档案失败！");
        }
        //品标
        insertMarks(basCustomer.getCustomerSid(),basCustomer.getMarkList());
        //品牌
        insertBrands(basCustomer.getCustomerSid(),basCustomer.getBrandList());
        //联系方式
        if (CollectionUtils.isNotEmpty(basCustomer.getAddrList())){
            basCustomer.getAddrList().forEach(item->{
                item.setCustomerSid(basCustomer.getCustomerSid());
            });
            basCustomerAddrMapper.inserts(basCustomer.getAddrList());
        }
        //客户-附件对象
        addBasCustomerAttachment(basCustomer);
        //待办通知
        SysTodoTask sysTodoTask = new SysTodoTask();
        if (ConstantsEms.SAVA_STATUS.equals(basCustomer.getHandleStatus())) {
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName("s_bas_customer")
                    .setDocumentSid(basCustomer.getCustomerSid());
            sysTodoTask.setTitle("客户档案: " + basCustomer.getCustomerCode() + " 当前是保存状态，请及时处理！")
                    .setDocumentCode(String.valueOf(basCustomer.getCustomerCode()))
                    .setNoticeDate(new Date())
                    .setUserId(ApiThreadLocalUtil.get().getUserid());
            sysTodoTaskMapper.insert(sysTodoTask);
        }
        //插入日志
        List<OperMsg> msgList=new ArrayList<>();
        MongodbDeal.insert(basCustomer.getCustomerSid(), basCustomer.getHandleStatus(), null, TITLE,null);
        return row;
    }

    //品牌
    private void insertBrands(Long customerSid,List<BasCustomerBrand> markList){
        brandMapper.delete(
                new UpdateWrapper<BasCustomerBrand>()
                        .lambda()
                        .eq(BasCustomerBrand::getCustomerSid, customerSid)
        );
        if(CollectionUtils.isNotEmpty(markList)){
            markList.forEach(m->{
                m.setCustomerSid(customerSid);
                if (CollectionUtils.isNotEmpty(m.getGroupList())){
                    m.getGroupList().forEach(item->{
                        item.setCustomerSid(customerSid)
                                .setCustomerBrandSid(m.getCustomerBrandSid());
                    });
                }
            });
            brandMapper.inserts(markList);
        }
    }

    //品标
    private Map insertMarks(Long customerSid,List<BasCustomerBrandMark> markList){
        markMapper.delete(
                new UpdateWrapper<BasCustomerBrandMark>()
                        .lambda()
                        .eq(BasCustomerBrandMark::getCustomerSid, customerSid)
        );
        Map marksMap = null;
        if(CollectionUtils.isNotEmpty(markList)){
            markList.forEach(m->{
                m.setCustomerSid(customerSid);
            });
            markMapper.inserts(markList);
            marksMap = markList.stream().collect(
                    Collectors.toMap(e -> e.getBrandMarkName(), e -> e));
        }
        return marksMap;
    }

    private void validMark(BasCustomerBrandMark mark){
        if(mark.getBrandMarkCode()==null){
            throw new BaseException("品标编码不能为空");
        }
        if(StrUtil.isEmpty(mark.getBrandMarkName())){
            throw new BaseException("品标名称不能为空");
        }
        if(mark.getStatus()==null){
            throw new BaseException("品标状态不能为空");
        }
        BasCustomerBrandMark queryResult=markMapper.selectOne(new QueryWrapper<BasCustomerBrandMark>().lambda().eq(BasCustomerBrandMark::getBrandMarkCode, mark.getBrandMarkCode()));
        if(queryResult!=null){
            throw new BaseException("品标编码已存在,请修改后再试");
        }
        queryResult=markMapper.selectOne(new QueryWrapper<BasCustomerBrandMark>().lambda().eq(BasCustomerBrandMark::getBrandMarkName, mark.getBrandMarkName()));
        if(queryResult!=null){
            throw new BaseException("品标名称已存在,请修改后再试");
        }
    }


    /**
     * 修改客户档案
     *
     * @param basCustomer 客户档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasCustomer(BasCustomer basCustomer) {
        BasCustomer old = basCustomerMapper.selectById(basCustomer.getCustomerSid());
        QueryWrapper<BasCustomer> wrapper = new QueryWrapper<BasCustomer>();
        if (!old.getCustomerName().equals(basCustomer.getCustomerName())) {
            wrapper = new QueryWrapper<BasCustomer>();
            wrapper.eq("customer_name", basCustomer.getCustomerName());
            List<BasCustomer> query2 = basCustomerMapper.selectList(wrapper);
            if (query2.size() > 0) {
                throw new CheckedException("客户名称已存在！");
            }
        }
        if (!old.getShortName().equals(basCustomer.getShortName())) {
            wrapper = new QueryWrapper<BasCustomer>();
            wrapper.eq("short_name", basCustomer.getShortName());
            List<BasCustomer> query3 = basCustomerMapper.selectList(wrapper);
            if (query3.size() > 0) {
                throw new CheckedException("客户简称已存在！");
            }
        }
        //品标
        insertMarks(basCustomer.getCustomerSid(),basCustomer.getMarkList());
        //品牌
        insertBrands(basCustomer.getCustomerSid(),basCustomer.getBrandList());
        //联系方式
        if (CollectionUtils.isNotEmpty(basCustomer.getAddrList())){
            basCustomer.getAddrList().forEach(item->{
                item.setCustomerSid(basCustomer.getCustomerSid());
                if (item.getCustomerContactSid() != null){
                    BasCustomerAddr addr = basCustomerAddrMapper.selectById(item.getCustomerContactSid());
                    if (addr != null){
                        List<OperMsg> msgList = new ArrayList<>();
                        msgList = BeanUtils.eq(addr, item);
                        if (msgList.size() > 0){
                            item.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                            item.setUpdateDate(new Date());
                            MongodbDeal.update(item.getCustomerContactSid(), old.getHandleStatus(), basCustomer.getHandleStatus(), msgList, TITLE_ADDR, null);
                        }
                    }
                }
            });
            basCustomerAddrMapper.delete(
                    new UpdateWrapper<BasCustomerAddr>()
                            .lambda()
                            .eq(BasCustomerAddr::getCustomerSid, basCustomer.getCustomerSid())
            );
            basCustomerAddrMapper.inserts(basCustomer.getAddrList());
        }
        //客户-附件对象
        addBasCustomerAttachment(basCustomer);
        if (basCustomer.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)) {
            basCustomer.setConfirmDate(new Date());
            basCustomer.setConfirmerAccount(SecurityUtils.getUsername());
        }
        //确认状态后删除待办
        if (!ConstantsEms.SAVA_STATUS.equals(basCustomer.getHandleStatus())){
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, basCustomer.getCustomerSid()));
        }
        List<OperMsg> msgList = new ArrayList<>();
        msgList = BeanUtils.eq(old, basCustomer);
        MongodbDeal.update(basCustomer.getCustomerSid(), old.getHandleStatus(), basCustomer.getHandleStatus(), msgList, TITLE, null);
        return basCustomerMapper.updateAllById(basCustomer);
    }

    /**
     * 批量删除客户档案
     *
     * @param clientIds 需要删除的客户档案ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasCustomerByIds(List<Long> customerSids) {
        for (Long sid : customerSids) {
            BasCustomer basCustomer = basCustomerMapper.selectById(sid);
            if (!basCustomer.getHandleStatus().equals("1")) {
                throw new CheckedException("选择数据存在不可删除状态，请查看！");
            }
            MongodbUtil.insertUserLog(Long.valueOf(sid), BusinessType.DELETE.getValue(), null, TITLE);
        }
        brandMapper.delete(new QueryWrapper<BasCustomerBrand>().lambda().in(BasCustomerBrand::getCustomerSid, customerSids));
        markMapper.delete(new QueryWrapper<BasCustomerBrandMark>().lambda().in(BasCustomerBrandMark::getCustomerSid,customerSids));
        basCustomerAddrMapper.delete(new QueryWrapper<BasCustomerAddr>().lambda().in(BasCustomerAddr::getCustomerSid,customerSids));
        //删除客户-附件清单
        basCustomerAttachMapper.delete(new QueryWrapper<BasCustomerAttach>().lambda().in(BasCustomerAttach::getCustomerSid, customerSids));
        //删除待办
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid, customerSids));
        int row=basCustomerMapper.delete(new QueryWrapper<BasCustomer>().lambda().in(BasCustomer::getCustomerSid,customerSids));
        return row;
    }

    @Override
    public List<BasCustomer> getCustomerList(BasCustomer basCustomer) {
        return basCustomerMapper.getCustomerList(basCustomer);
    }

    @Override
    public List<BasCustomerBrandMark> getCustomerBrandMarkList(Long brandSid) {
        List<BasCustomerBrandMark> markList = markMapper.selectList(new QueryWrapper<BasCustomerBrandMark>().lambda().eq(BasCustomerBrandMark::getCustomerSid, brandSid).orderByAsc(BasCustomerBrandMark::getBrandMarkCode));
        return markList;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int editStatus(BasCustomer basCustomer) {
        BasCustomer bas = new BasCustomer();
        for (Long id : basCustomer.getCustomerSids()) {
            bas = new BasCustomer();
            bas.setCustomerSid(id);
            bas.setStatus(basCustomer.getStatus());
            bas.setUpdateDate(new Date());
            bas.setUpdaterAccount(SecurityUtils.getUsername());
            basCustomerMapper.editStatus(bas);
            //插入日志
            String remark = StrUtil.isEmpty(basCustomer.getDisableRemark()) ? null : basCustomer.getDisableRemark();
            MongodbDeal.status(Long.valueOf(id), basCustomer.getStatus(), null, TITLE, remark);
        }
        //更新品牌
        LambdaUpdateWrapper<BasCustomerBrand> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(BasCustomerBrand::getCustomerSid,basCustomer.getCustomerSids()).set(BasCustomerBrand::getStatus, ConstantsEms.ENABLE_STATUS);
        brandMapper.update(null, updateWrapper);
        //更新品标
        LambdaUpdateWrapper<BasCustomerBrandMark> updateWrapper2 = new LambdaUpdateWrapper<>();
        updateWrapper2.in(BasCustomerBrandMark::getCustomerSid,basCustomer.getCustomerSids()).set(BasCustomerBrandMark::getStatus, ConstantsEms.ENABLE_STATUS);
        markMapper.update(null, updateWrapper2);
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int editHandleStatus(BasCustomer basCustomer) {
        BasCustomer bas = new BasCustomer();
        for (Long id : basCustomer.getCustomerSids()) {
            bas = new BasCustomer();
            bas.setCustomerSid(id);
            bas.setHandleStatus(basCustomer.getHandleStatus());
            bas.setConfirmDate(new Date());
            bas.setConfirmerAccount(SecurityUtils.getUsername());
            basCustomerMapper.editHandleStatus(bas);
            //确认状态后删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(basCustomer.getHandleStatus())){
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .in(SysTodoTask::getDocumentSid, basCustomer.getCustomerSids()));
            }
            //插入日志
            MongodbDeal.check(id, basCustomer.getHandleStatus(), null,TITLE, null);
        }
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setOperator(BasCustomer basCustomer) {
        if (CollectionUtils.isEmpty(basCustomer.getCustomerSids())){
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<BasCustomer> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        //我方跟单员
        if (StrUtil.isBlank(basCustomer.getBuOperator())){
            basCustomer.setBuOperator(null);
        }
        updateWrapper.in(BasCustomer::getCustomerSid,basCustomer.getCustomerSids()).set(BasCustomer::getBuOperator, basCustomer.getBuOperator());
        row = basCustomerMapper.update(null, updateWrapper);
        return row;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setOperatorCustomer(BasCustomer basCustomer) {
        if (CollectionUtils.isEmpty(basCustomer.getCustomerSids())){
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<BasCustomer> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        //供方业务员
        if (StrUtil.isBlank(basCustomer.getBuOperatorCustomer())){
            basCustomer.setBusinessScope(null);
        }
        updateWrapper.in(BasCustomer::getCustomerSid,basCustomer.getCustomerSids()).set(BasCustomer::getBuOperatorCustomer, basCustomer.getBuOperatorCustomer());
        row = basCustomerMapper.update(null, updateWrapper);
        return row;
    }

    /**
     * 设置合作状态
     * @param basCustomer
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setCooperate(BasCustomer basCustomer) {
        if (CollectionUtils.isEmpty(basCustomer.getCustomerSids())){
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<BasCustomer> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(BasCustomer::getCustomerSid,basCustomer.getCustomerSids())
                .set(BasCustomer::getCooperateStatus, basCustomer.getCooperateStatus());
        return basCustomerMapper.update(null, updateWrapper);
    }

    /**
     * 客户-附件对象
     */
    private void addBasCustomerAttachment(BasCustomer basCustomer) {
        basCustomerAttachMapper.delete(
                new UpdateWrapper<BasCustomerAttach>()
                        .lambda()
                        .eq(BasCustomerAttach::getCustomerSid, basCustomer.getCustomerSid())
        );
        if (CollectionUtils.isNotEmpty(basCustomer.getAttachmentList())) {
            basCustomer.getAttachmentList().forEach(item -> {
                item.setCustomerSid(basCustomer.getCustomerSid());
                basCustomerAttachMapper.insert(item);
            });
        }
    }

    /**
     * 查询客户档案列表
     *
     * @param basCustomer 客户档案
     * @return 客户档案
     */
    @Override
    public List<BasCustomerAddr> selectBasCustomerAddrList(BasCustomerAddr addr) {
        List<BasCustomerAddr> list = basCustomerAddrMapper.selectBasCustomerAddrList(addr);
        return list;
    }
}
