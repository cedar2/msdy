package com.platform.ems.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.platform.common.core.domain.AjaxResult;
import com.platform.common.core.domain.entity.SysUser;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.utils.file.FileUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.utils.SecurityUtils;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.domain.dto.response.form.BasStaffConditionForm;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.enums.Status;
import com.platform.ems.mapper.*;
import com.platform.ems.service.ISystemDictDataService;
import com.platform.ems.task.DianqianTask;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.system.mapper.SystemUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.exception.CustomException;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.service.IBasStaffService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 员工档案Service业务层处理
 *
 * @author linhongwei
 * @date 2021-03-17
 */
@Service
@SuppressWarnings("all")
public class BasStaffServiceImpl extends ServiceImpl<BasStaffMapper,BasStaff>  implements IBasStaffService {
    @Autowired
    private BasStaffMapper basStaffMapper;
    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private BasDepartmentMapper basDepartmentMapper;
    @Autowired
    private BasPositionMapper basPositionMapper;
    @Autowired
    private BasPlantMapper basPlantMapper;
    @Autowired
    private ISystemDictDataService sysDictDataService;
    @Autowired
    private BasStaffAttachmentMapper basStaffAttachmentMapper;
    @Autowired
    private ManWorkCenterMapper manWorkCenterMapper;
    @Autowired
    private ManWorkCenterMemberMapper manWorkCenterMemberMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;
    @Autowired
    private HrDimissionCertificateMapper hrDimissionCertificateMapper;

    @Autowired
    private HrIncomeCertificateMapper hrIncomeCertificateMapper;

    @Autowired
    private HrOtherPersonnelCertificateMapper hrOtherPersonnelCertificateMapper;

    @Autowired
    private SystemUserMapper userMapper;


    private static final String TITLE = "员工档案";

    /**
     * 查询员工档案
     *
     * @param staffSid 员工档案ID
     * @return 员工档案
     */
    @Override
    public BasStaff selectBasStaffById(Long staffSid) {
    	BasStaff staff = basStaffMapper.selectBasStaffById(staffSid);
        //供应商-附件对象
        List<BasStaffAttachment> basStaffAttachList = basStaffAttachmentMapper.selectBasStaffAttachmentList(new BasStaffAttachment().setStaffSid(staffSid));
        staff.setAttachmentList(basStaffAttachList);        //查询日志信息
        MongodbUtil.find(staff);
    	return staff;
    }

    /**
     * 查询员工档案的编码和名称
     *
     * @param staffSid 员工档案ID
     * @return 员工档案
     */
    @Override
    public BasStaff selectCodeNameById(Long staffSid) {
        BasStaff staff = basStaffMapper.selectById(staffSid);
        return staff;
    }

    /**
     * 查询员工档案列表
     *
     * @param basStaff 员工档案
     * @return 员工档案
     */
    @Override
    public List<BasStaff> selectBasStaffList(BasStaff basStaff) {
        return basStaffMapper.selectBasStaffList(basStaff);
    }

    /**
     * 新增员工档案
     * 需要注意编码重复校验
     * @param basStaff 员工档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasStaff(BasStaff basStaff) {
        //校验员工编码是否重复
        checkCodeUnique(basStaff);
        if(basStaff.getIdentityCard()!=null){
            //校验身份证号是否重复
            checkIdentityCardUnique(basStaff);
        }
        String checkStatus= ConstantsEms.CHECK_STATUS;
        if(checkStatus.equals(basStaff.getHandleStatus())){
            basStaff.setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
            basStaff.setConfirmDate(new Date());
        }
        int i = basStaffMapper.insert(basStaff);
        //供应商-附件对象
        List<BasStaffAttachment> basStaffAttachmentList = basStaff.getAttachmentList();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(basStaffAttachmentList)) {
            addBasStaffAttachment(basStaff);
        }
        //待办通知
        if (!StrUtil.equals(basStaff.getHandleStatus() , ConstantsEms.CHECK_STATUS)) {
            SysTodoTask sysTodoTask = new SysTodoTask();
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName("s_bas_staff")
                    .setDocumentSid(basStaff.getStaffSid());
            sysTodoTask.setTitle("员工档案: " + basStaff.getStaffCode() + " 当前是保存状态，请及时处理！")
                    .setDocumentCode(String.valueOf(basStaff.getStaffCode()))
                    .setNoticeDate(new Date())
                    .setUserId(ApiThreadLocalUtil.get().getUserid());
            sysTodoTaskMapper.insert(sysTodoTask);
        }
        //插入日志
        List<OperMsg> msgList=new ArrayList<>();
        MongodbDeal.insert(Long.valueOf(basStaff.getStaffSid()), basStaff.getHandleStatus(), null, TITLE,null);
        return i;
    }

    /**
     * 批量直接新增员工档案
     * @param basStaff 员工档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBasStaff(List<BasStaff> basStaffList) {
        int row = basStaffMapper.inserts(basStaffList);
        basStaffList.forEach(staff->{
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName("s_bas_staff")
                    .setDocumentSid(staff.getStaffSid());
            sysTodoTask.setTitle("员工档案: " + staff.getStaffCode() + " 当前是保存状态，请及时处理！")
                    .setDocumentCode(String.valueOf(staff.getStaffCode()))
                    .setNoticeDate(new Date())
                    .setUserId(ApiThreadLocalUtil.get().getUserid());
            sysTodoTaskMapper.insert(sysTodoTask);
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbDeal.insert(Long.valueOf(staff.getStaffSid()), staff.getHandleStatus(), null, TITLE,null);
        });
        return row;
    }

    /**
     * 修改员工档案
     *
     * @param basStaff 员工档案
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBasStaff(BasStaff basStaff) {
        BasStaff staff = basStaffMapper.selectBasStaffById(basStaff.getStaffSid());
        if (!basStaff.getStaffCode().equals(staff.getStaffCode())){
            //校验员工编码是否重复
            checkCodeUnique(basStaff);
        }
        if(basStaff.getIdentityCard()!=null){
            if(!basStaff.getIdentityCard().equals(staff.getIdentityCard())){
                //校验身份证号是否重复
                checkIdentityCardUnique(basStaff);
            }
        }
        basStaff.setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        basStaff.setUpdateDate(new Date());
        //供应商-附件对象
        addBasStaffAttachment(basStaff);
        if (basStaff.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)) {
            basStaff.setConfirmDate(new Date());
            basStaff.setConfirmerAccount(SecurityUtils.getUsername());
        }
        //确认状态后删除待办
        if (!ConstantsEms.SAVA_STATUS.equals(basStaff.getHandleStatus())){
            sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                    .eq(SysTodoTask::getDocumentSid, basStaff.getStaffSid()));
        }
        //插入日志
        List<OperMsg> msgList = new ArrayList<>();
        msgList = BeanUtils.eq(staff, basStaff);
        MongodbDeal.update(Long.valueOf(basStaff.getStaffSid()), staff.getHandleStatus(), basStaff.getHandleStatus(), msgList, TITLE, null);
        if(basStaff.getHandleStatus().equals("5")){
            //判断是否是变更->获取租户id下是否存在 （员工sid、类型：员工
            List<SysUser> userList  = userMapper.selectList(new QueryWrapper<SysUser>().lambda()
                    .eq(SysUser::getClientId, ApiThreadLocalUtil.get().getSysUser().getClientId())
                    .eq(SysUser::getStaffSid, basStaff.getStaffSid())
                    .eq(SysUser::getAccountType, "YG"));
            if(userList != null && !userList.isEmpty()){
                //获取用户档案->修改员工表后将用户表的数据同步更新员工的（电子邮箱、移动电话）->用户的（邮箱、手机号码）
                Long[] userids = userList.stream().map(SysUser::getUserId).toArray(Long[]::new);
                userMapper.update(null, new UpdateWrapper<SysUser>().lambda().in(SysUser::getUserId, userids)
                        .set(SysUser::getEmail,basStaff.getEmailPersonal())
                        .set(SysUser::getPhonenumber,basStaff.getMobphone()));
            }

        }
        return basStaffMapper.updateAllById(basStaff);
    }

    @Override
    public AjaxResult cheackHrDimissionCertificateById(Long staffSid) {
        List<HrDimissionCertificate> hrDimissionCertificateList = hrDimissionCertificateMapper.selectList(new QueryWrapper<HrDimissionCertificate>().lambda()
                .eq(HrDimissionCertificate::getStaffSid, staffSid)
                .eq(HrDimissionCertificate::getEsignStatus, "QSZ"));
        if(hrDimissionCertificateList.size()>0){
            return AjaxResult.success("该员工已存在签署中的离职证明记录，正在跳转页面......",
                    new HrDimissionCertificate().setDimissionCertificateSid(hrDimissionCertificateList.get(0).getDimissionCertificateSid()));
        }else{
            return AjaxResult.success(false);
        }
    }

    @Override
    public AjaxResult cheackHrIncomeCertificateById(Long staffSid) {
        List<HrIncomeCertificate> hrIncomeCertificateList = hrIncomeCertificateMapper.selectList(new QueryWrapper<HrIncomeCertificate>().lambda()
                .eq(HrIncomeCertificate::getStaffSid, staffSid)
                .eq(HrIncomeCertificate::getEsignStatus, "QSZ"));
        if(hrIncomeCertificateList.size()>0){
            return AjaxResult.success("该员工已存在签署中的收入证明记录，正在跳转页面......",
                    new HrIncomeCertificate().setIncomeCertificateSid(hrIncomeCertificateList.get(0).getIncomeCertificateSid()));
        }else{
            return AjaxResult.success(false);
        }
    }

    @Override
    public AjaxResult cheackHrOtherPersonnelCertificateById(Long staffSid) {
        List<HrOtherPersonnelCertificate> hrOtherPersonnelCertificateList = hrOtherPersonnelCertificateMapper.selectList(new QueryWrapper<HrOtherPersonnelCertificate>().lambda()
                .eq(HrOtherPersonnelCertificate::getStaffSid, staffSid)
                .eq(HrOtherPersonnelCertificate::getEsignStatus, "QSZ"));
        if(hrOtherPersonnelCertificateList.size()>0){
            return AjaxResult.success("该员工已存在签署中的其它人事证明记录，正在跳转页面......",
                    new HrOtherPersonnelCertificate().setOtherPersonnelCertificateSid(hrOtherPersonnelCertificateList.get(0).getOtherPersonnelCertificateSid()));
        }else{
            return AjaxResult.success(false);
        }
    }

    //校验员工编码是否重复
    private void checkCodeUnique(BasStaff basStaff) {
        if (StrUtil.isNotBlank(basStaff.getStaffCode())){
            QueryWrapper<BasStaff> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(BasStaff::getStaffCode, basStaff.getStaffCode());
            if (basStaff.getStaffSid() != null) {
                queryWrapper.lambda()
                        .ne(BasStaff::getStaffSid, basStaff.getStaffSid());
            }
            List<BasStaff> result = basStaffMapper.selectList(queryWrapper);
            if (CollectionUtil.isNotEmpty(result)){
                throw new BaseException("员工编码已存在，请核实！");
            }
        }
    }
    //校验员工编码是否重复
    private void checkIdentityCardUnique(BasStaff basStaff) {
        basStaff.setIdentityCard(basStaff.getIdentityCard().replaceAll(" +",""));
        BasStaff result = basStaffMapper.selectOne(new QueryWrapper<BasStaff>().lambda()
                .eq(BasStaff::getIdentityCard, basStaff.getIdentityCard())
        );
        if (result != null){
            throw new BaseException("身份证号已存在，请核实！");
        }
    }

    /**
     * 批量删除员工档案
     *
     * @param staffSids 需要删除的员工档案ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteBasStaffByIds(List<Long> staffSids) {

        staffSids.forEach(sid->{
            //插入日志
            MongodbUtil.insertUserLog(Long.valueOf(sid), BusinessType.DELETE.getValue(), TITLE);
        });
        sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                .in(SysTodoTask::getDocumentSid, staffSids));
        return basStaffMapper.deleteBatchIds(staffSids);
    }

    /**
     * 启用停用
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeStatus(BasStaff basStaff){
        int row=0;
        Long[] sids=basStaff.getStaffSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                basStaff.setStaffSid(id);
                row=basStaffMapper.updateById(basStaff);
                if(row==0){
                    throw new CustomException(id+"更改状态失败,请联系管理员");
                }
                //插入日志
                String remark = StrUtil.isEmpty(basStaff.getDisableRemark()) ? null : basStaff.getDisableRemark();
                MongodbDeal.status(Long.valueOf(id), basStaff.getStatus(), null, TITLE, remark);
            }
        }
        return row;
    }

    /**
     * 批量确认
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int check(BasStaff basStaff){
        int row=0;
        Long[] sids=basStaff.getStaffSidList();
        if(sids!=null&&sids.length>0){
            for(Long id:sids){
                basStaff.setStaffSid(id);
                row=basStaffMapper.updateById(basStaff);
                if(row==0){
                    throw new CustomException(id+"确认失败,请联系管理员");
                }
                //插入日志
                MongodbDeal.check(id, basStaff.getHandleStatus(), null, TITLE, null);
            }
            //确认状态后删除待办
            if (!ConstantsEms.SAVA_STATUS.equals(basStaff.getHandleStatus())){
                sysTodoTaskMapper.delete(new UpdateWrapper<SysTodoTask>().lambda()
                        .in(SysTodoTask::getDocumentSid, basStaff.getStaffSidList()));
            }
        }
        return row;
    }

    /**
     * 根据公司查询公司下面的员工
     */
    @Override
    public List<BasStaff> getCompanyStaff(Long companySid) {
        QueryWrapper<BasStaff> staffWrapper = new QueryWrapper<BasStaff>();
        staffWrapper.eq("company_sid", companySid);
        List<BasStaff> deptList = basStaffMapper.selectList(staffWrapper);
        return deptList;
    }

    /**
     * 员工下拉框
     */
    @Override
    public List<BasStaff> getStaffList(BasStaff basStaff){
        List<BasStaff> staffList = basStaffMapper.getStaffList(basStaff);
        if (CollectionUtil.isNotEmpty(staffList)){
            staffList.forEach(o ->{
                o.setNamePlusCode(o.getStaffName() + "-" + o.getStaffCode());
            });
        }
        return staffList;
    }

    /**
     * 员工下拉框  适用于件薪那边 取并集
     */
    @Override
    public List<BasStaff> getStaffAndWorkList(BasStaff basStaff){
        basStaff.setIsOnJob(ConstantsEms.IS_ON_JOB_ZZ);
        List<BasStaff> staffList = basStaffMapper.getStaffList(basStaff);
        if (basStaff.getWorkCenterSid() != null){
            List<ManWorkCenterMember> menberList = manWorkCenterMemberMapper.selectManWorkCenterMemberList(new ManWorkCenterMember()
                    .setWorkCenterSid(basStaff.getWorkCenterSid()).setIsOnJob(ConstantsEms.IS_ON_JOB_ZZ).setHandleStatus(ConstantsEms.CHECK_STATUS));
            Long[] workerSidList = menberList.stream().map(ManWorkCenterMember::getMemberSid).toArray(Long[]::new);
            if (workerSidList != null && workerSidList.length > 0){
                List<BasStaff> workerList = basStaffMapper.getStaffList(new BasStaff().setStaffSidList(workerSidList));
                staffList.addAll(workerList);
                Set set = new HashSet(); //新建一个HashSet
                set.addAll(staffList); //list里的所有东西放入set中 进行去重
                staffList = new ArrayList<>();
                staffList.addAll(set); //把去重完的set重新放回list里
            }
        }
        if (CollectionUtil.isNotEmpty(staffList)){
            staffList.forEach(o ->{
                o.setNamePlusCode(o.getStaffName() + "-" + o.getStaffCode());
            });
        }
        Collections.sort(staffList, new Comparator<BasStaff>() {
            @Override
            public int compare(BasStaff info1, BasStaff info2) {
                Comparator<Object> com = Collator.getInstance(java.util.Locale.CHINA);
                return com.compare(info1.getNamePlusCode(), info2.getNamePlusCode());
            }
        });
        return staffList;
    }

    /**
     * 导入员工档案
     */
    @Override
    public EmsResultEntity importData(MultipartFile file) {
        List<BasStaff> staffList = new ArrayList<>();
        //错误信息
        CommonErrMsgResponse errMsg = null;
        CommonErrMsgResponse warnMsg = null;
        List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
        List<CommonErrMsgResponse> warnMsgList = new ArrayList<>();
        EmsResultEntity resultEntity = null;
        try {
            File toFile = null;
            try {
                toFile = FileUtils.multipartFileToFile(file);
            } catch (Exception e) {
                e.getMessage();
                throw new BaseException("文件转换失败");
            }
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(toFile);
            FileUtils.delteTempFile(toFile);
            List<List<Object>> readAll = reader.read();
            List<DictData> genderList = sysDictDataService.selectDictData("s_gender"); //性别
            genderList = genderList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> genderMaps = genderList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            List<DictData> staffTypeList = sysDictDataService.selectDictData("s_staff_type"); //员工类型
            staffTypeList = staffTypeList.stream().filter(o -> o.getHandleStatus().equals(HandleStatus.CONFIRMED.getCode()) && o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> staffTypeMaps = staffTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));
            int num=0;
            HashMap<String, String> codeMap = new HashMap<>(); // 编码重复校验
            HashMap<String, String> nameMap = new HashMap<>(); // 编码重复校验
            HashMap<String, String> cardMap = new HashMap<>(); // 身份证号重复校验
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                num=i+1;
                List<Object> objects = readAll.get(i);
                copy(objects, readAll);
                String staffCode = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString();
                if (StrUtil.isBlank(staffCode)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("员工编号不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    boolean isCodeType = JudgeFormat.isCodeType(staffCode);
                    if(!isCodeType){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("员工编号数据格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    // 判断是否与表格内的编码重复
                    if (codeMap.get(staffCode) == null){
                        codeMap.put(staffCode,String.valueOf(num));
                        // 如果表格内没重复则判断与数据库之间是否存在重复
                        List<BasStaff> basStaffList = basStaffMapper.selectList(new QueryWrapper<BasStaff>().lambda()
                                .eq(BasStaff::getStaffCode,staffCode));
                        if (CollectionUtil.isNotEmpty(basStaffList)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("员工编码已存在，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }else {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("表格内员工编码重复，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                String staffName = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString();
                if (StrUtil.isBlank(staffName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("员工姓名不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }
                else {
                    // 判断是否与表格内的编码重复
                    if (nameMap.get(staffName) == null) {
                        nameMap.put(staffName, String.valueOf(num));
                        // 校验名称重复
                        BasStaff sta = new BasStaff();
                        sta.setStaffName(staffName);
                        List<CommonErrMsgResponse> warn = checkName(sta).getMsgList();
                        if (CollectionUtil.isNotEmpty(warn)) {
                            warnMsg = new CommonErrMsgResponse();
                            warnMsg.setItemNum(num);
                            warnMsg.setMsg("系统中已存在姓名为" + staffName + "的员工，是否继续？");
                            warnMsgList.add(warnMsg);
                        }
                    }
                    else {
                        warnMsg = new CommonErrMsgResponse();
                        warnMsg.setItemNum(num);
                        warnMsg.setMsg("表格中已存在姓名为" + staffName + "的员工，是否继续？");
                        warnMsgList.add(warnMsg);
                    }
                }
                String gender = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString();
                if (StrUtil.isBlank(gender)) {
//                    errMsg = new CommonErrMsgResponse();
//                    errMsg.setItemNum(num);
//                    errMsg.setMsg("性别不能为空，导入失败！");
//                    errMsgList.add(errMsg);
                }else {
                    gender = genderMaps.get(gender);
                    if (StrUtil.isBlank(gender)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("性别配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                String checkInDate_s = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString();
                Date checkInDate = null;
                if (StrUtil.isBlank(checkInDate_s)) {
//                    errMsg = new CommonErrMsgResponse();
//                    errMsg.setItemNum(num);
//                    errMsg.setMsg("入职日期不能为空，导入失败！");
//                    errMsgList.add(errMsg);
                }else {
                    if (!JudgeFormat.isValidDate(checkInDate_s)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("入职日期格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        checkInDate = DateUtil.parse(checkInDate_s);
                    }
                }
                String identityCard = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString();
                if (StrUtil.isBlank(identityCard)) {
//                    errMsg = new CommonErrMsgResponse();
//                    errMsg.setItemNum(num);
//                    errMsg.setMsg("身份证号不能为空，导入失败！");
//                    errMsgList.add(errMsg);
                }
                else {
                    if (identityCard.length() > 60){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("身份证号最大只能输入18位，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        // 判断是否与表格内的编码重复
                        if (codeMap.get(identityCard) == null){
                            codeMap.put(identityCard,String.valueOf(num));
                            // 如果表格内没重复则判断与数据库之间是否存在重复
                            List<BasStaff> basStaffList = basStaffMapper.selectList(new QueryWrapper<BasStaff>().lambda()
                                    .eq(BasStaff::getIdentityCard,identityCard));
                            if (CollectionUtil.isNotEmpty(basStaffList)){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("身份证号已存在，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }else {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("表格内身份证号重复，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 公司简称
                 */
                String companyShortName = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString();
                Long companySid = null;
                String companyName = null;
                if (StrUtil.isBlank(companyShortName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("公司简称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }else {
                    try {
                        BasCompany basCompany = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>().lambda()
                                .eq(BasCompany::getShortName,companyShortName));
                        if (basCompany == null){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(companyShortName + "公司简称不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }else {
                            if (!ConstantsEms.CHECK_STATUS.equals(basCompany.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(basCompany.getStatus())){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("对应的公司必须是确认且已启用的状态，导入失败！");
                                errMsgList.add(errMsg);
                            }
                            else {
                                companySid = basCompany.getCompanySid();
                                companyName = basCompany.getCompanyName();
                            }
                        }
                    } catch (Exception e){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg(companyShortName + "公司简称存在重复，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 部门名称
                 */
                String departmentName = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString();
                Long departmentSid = null;
                if (StrUtil.isNotBlank(departmentName)) {
                    if (companySid != null){
                        try {
                            BasDepartment basDepartment = basDepartmentMapper.selectOne(new QueryWrapper<BasDepartment>().lambda()
                                    .eq(BasDepartment::getDepartmentName, departmentName)
                                    .eq(BasDepartment::getCompanySid, companySid));
                            if (basDepartment == null){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(companyShortName + "公司下的" + departmentName + "部门名称不存在，导入失败！");
                                errMsgList.add(errMsg);
                            }else {
                                if (!ConstantsEms.CHECK_STATUS.equals(basDepartment.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(basDepartment.getStatus())){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("对应的部门必须是确认且已启用的状态，导入失败！");
                                    errMsgList.add(errMsg);
                                }else {
                                    departmentSid = basDepartment.getDepartmentSid();
                                }
                            }
                        }catch (Exception e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(companyShortName + "公司下的" + departmentName + "部门名称存在重复，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                    else {
                        List<BasDepartment> basDepartmentList = basDepartmentMapper.selectList(new QueryWrapper<BasDepartment>().lambda()
                                .eq(BasDepartment::getDepartmentName, departmentName));
                        if (CollectionUtil.isEmpty(basDepartmentList)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(departmentName + "部门名称不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 岗位名称
                 */
                String positionName = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString();
                Long positionSid = null;
                if (StrUtil.isNotBlank(positionName)) {
                    if (companySid != null){
                        try {
                            BasPosition basPosition = basPositionMapper.selectOne(new QueryWrapper<BasPosition>().lambda()
                                    .eq(BasPosition::getPositionName, positionName)
                                    .eq(BasPosition::getCompanySid, companySid));
                            if (basPosition == null){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(companyShortName + "公司下的" + positionName + "岗位名称不存在，导入失败！");
                                errMsgList.add(errMsg);
                            }else {
                                if (!ConstantsEms.CHECK_STATUS.equals(basPosition.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(basPosition.getStatus())){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("对应的岗位必须是确认且已启用的状态，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                                else {
                                    positionSid = basPosition.getPositionSid();
                                }
                            }
                        } catch (Exception e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(companyShortName + "公司下的" + positionName + "岗位名称存在重复，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                    else {
                        List<BasPosition> basPositionList = basPositionMapper.selectList(new QueryWrapper<BasPosition>().lambda()
                                .eq(BasPosition::getPositionName, positionName));
                        if (CollectionUtil.isEmpty(basPositionList)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(positionName + "岗位名称不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                String staffType = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString();
                if (StrUtil.isNotBlank(staffType)) {
                    staffType = staffTypeMaps.get(staffType);
                    if (StrUtil.isBlank(staffType)) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("员工类型配置错误，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                /*
                 * 工厂名称
                 */
                String plantShortName = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString();
                Long plantSid = null;
                String plantName = null;
                if (StrUtil.isNotBlank(plantShortName)){
                    if (companySid != null){
                        try {
                            BasPlant basPlant = basPlantMapper.selectOne(new QueryWrapper<BasPlant>().lambda()
                                    .eq(BasPlant::getShortName, plantShortName)
                                    .eq(BasPlant::getCompanySid, companySid));
                            if (basPlant == null){
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(companyShortName + "公司下的" + plantShortName + "工厂简称不存在，导入失败！");
                                errMsgList.add(errMsg);
                            }else {
                                if (!ConstantsEms.CHECK_STATUS.equals(basPlant.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(basPlant.getStatus())){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("对应的工厂必须是确认且已启用的状态，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                                else {
                                    plantSid = new Long(basPlant.getPlantSid());
                                    plantName = basPlant.getPlantName();
                                }
                            }
                        } catch (Exception e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(companyShortName + "公司下的" + plantShortName + "工厂简称存在重复，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                    else {
                        List<BasPlant> basPlantList = basPlantMapper.selectList(new QueryWrapper<BasPlant>().lambda()
                                .eq(BasPlant::getShortName, plantShortName));
                        if (CollectionUtil.isEmpty(basPlantList)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(plantShortName + "工厂简称不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /*
                 * 班组名称
                 */
                String workCenterName = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString();
                Long workCenterSid = null;
                String workCenterCode = null;
                if (StrUtil.isNotBlank(workCenterName)){
                    if (StrUtil.isBlank(plantShortName)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("填写班组名称前请填写工厂简称，导入失败！");
                        errMsgList.add(errMsg);
                    }
                    if (plantSid != null){
                        try {
                            ManWorkCenter center = manWorkCenterMapper.selectOne(new QueryWrapper<ManWorkCenter>().lambda()
                                    .eq(ManWorkCenter::getWorkCenterName,workCenterName)
                                    .eq(ManWorkCenter::getPlantSid,plantSid));
                            if (center != null){
                                if (!ConstantsEms.CHECK_STATUS.equals(center.getHandleStatus()) || !ConstantsEms.ENABLE_STATUS.equals(center.getStatus())){
                                    errMsg = new CommonErrMsgResponse();
                                    errMsg.setItemNum(num);
                                    errMsg.setMsg("对应的班组必须是确认且已启用的状态，导入失败！");
                                    errMsgList.add(errMsg);
                                }
                                else {
                                    workCenterSid = center.getWorkCenterSid();
                                    workCenterCode = center.getWorkCenterCode();
                                }
                            }else {
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg(plantShortName + "工厂下的" + workCenterName + "班组名称不存在，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }catch (Exception e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(plantShortName + "工厂下的" + workCenterName + "班组名称出现重复，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                    else {
                        List<ManWorkCenter> centerList = manWorkCenterMapper.selectList(new QueryWrapper<ManWorkCenter>().lambda()
                                .eq(ManWorkCenter::getWorkCenterName,workCenterName));
                        if (CollectionUtil.isEmpty(centerList)){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg(workCenterName + "班组名称不存在，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /**
                 * 月薪（试用）（元） 选填
                 */
                String permonthWageProbation_s = objects.get(22) == null || objects.get(22) == "" ? null : objects.get(22).toString();
                BigDecimal permonthWageProbation = null;
                if (StrUtil.isNotBlank(permonthWageProbation_s)){
                    if (!JudgeFormat.isValidDouble(permonthWageProbation_s,8,2)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("月薪(试用)格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        permonthWageProbation = new BigDecimal(permonthWageProbation_s);
                        if (BigDecimal.ZERO.compareTo(permonthWageProbation) >= 0){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("月薪(试用)不能输入负数或0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /**
                 * 月薪（转正）（元） 选填
                 */
                String permonthWageFormal_s = objects.get(23) == null || objects.get(23) == "" ? null : objects.get(23).toString();
                BigDecimal permonthWageFormal = null;
                if (StrUtil.isNotBlank(permonthWageFormal_s)){
                    if (!JudgeFormat.isValidDouble(permonthWageFormal_s,8,2)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("月薪(转正)格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        permonthWageFormal = new BigDecimal(permonthWageFormal_s);
                        if (BigDecimal.ZERO.compareTo(permonthWageFormal) >= 0){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("月薪(转正)不能输入负数或0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /**
                 * 月薪（当前）（元） 选填
                 */
                String permonthWagePresent_s = objects.get(24) == null || objects.get(24) == "" ? null : objects.get(24).toString();
                BigDecimal permonthWagePresent = null;
                if (StrUtil.isNotBlank(permonthWagePresent_s)){
                    if (!JudgeFormat.isValidDouble(permonthWagePresent_s,8,2)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("月薪(当前)格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        permonthWagePresent = new BigDecimal(permonthWagePresent_s);
                        if (BigDecimal.ZERO.compareTo(permonthWagePresent) >= 0){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("月薪(当前)不能输入负数或0，导入失败！");
                            errMsgList.add(errMsg);
                        }
                    }
                }
                /**
                 * 出生日期 必填
                 */
                String birthDate_s = objects.get(11) == null || objects.get(11) == "" ? null : objects.get(11).toString();
                Date birthDate = null;
                if (StrUtil.isNotBlank(birthDate_s)) {
                    if (!JudgeFormat.isValidDate(birthDate_s)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("出生日期格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        birthDate = new Date();
                        try {
                            birthDate = DateUtil.parse(birthDate_s);
                        } catch (Exception e){
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("出生日期格式错误，导入失败！");
                            errMsgList.add(errMsg);
                            birthDate = null;
                        }
                    }
                }
                String remark = objects.get(25) == null || objects.get(25) == "" ? null : objects.get(25).toString();
                if (StrUtil.isNotBlank(remark)) {
                    if (remark.length() > 600){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("备注不能超过600个字符，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }
                if (CollectionUtil.isEmpty(errMsgList)){
                    BasStaff basStaff = new BasStaff();
                    basStaff.setStaffCode(staffCode);
                    basStaff.setStaffName(staffName);
                    basStaff.setIsOnJob(ConstantsEms.IS_ON_JOB_ZZ)
                            .setGender(gender).setStaffType(staffType)
                            .setCheckInDate(checkInDate_s)
                            .setIdentityCard(identityCard)
                            .setDepartmentName(departmentName)
                            .setPositionName(positionName)
                            .setBrithday(birthDate)
                            .setEmailEnterprise(objects.get(12) == null || objects.get(12) == "" ? null : objects.get(12).toString())
                            .setEmailPersonal(objects.get(13) == null || objects.get(13) == "" ? null : objects.get(13).toString())
                            .setMobphone(objects.get(14) == null || objects.get(14) == "" ? null : objects.get(14).toString())
                            .setTelephone(objects.get(15) == null || objects.get(15) == "" ? null : objects.get(15).toString())
                            .setAddressHome(objects.get(16) == null || objects.get(16) == "" ? null : objects.get(16).toString())
                            .setAddressOffice(objects.get(17) == null || objects.get(17) == "" ? null : objects.get(17).toString())
                            .setEmergencyContactName1(objects.get(18) == null || objects.get(18) == "" ? null : objects.get(18).toString())
                            .setEmergencyContactTel1(objects.get(19) == null || objects.get(19) == "" ? null : objects.get(19).toString())
                            .setEmergencyContactName2(objects.get(20) == null || objects.get(20) == "" ? null : objects.get(20).toString())
                            .setEmergencyContactTel2(objects.get(21) == null || objects.get(21) == "" ? null : objects.get(21).toString())
                            .setPermonthWageProbation(permonthWageProbation)
                            .setPermonthWageFormal(permonthWageFormal)
                            .setPermonthWagePresent(permonthWagePresent)
                            .setRemark(remark).setWorkCenterSid(workCenterSid).setWorkCenterCode(workCenterCode)
                            .setStatus(Status.ENABLE.getCode())
                            .setHandleStatus(HandleStatus.SAVE.getCode());
                    basStaff.setDefaultCompanySid(companySid).setCompanyName(companyName);
                    basStaff.setDefaultDepartmentSid(departmentSid);
                    basStaff.setDefaultPosition(positionSid);
                    basStaff.setDefaultPlantSid(plantSid);
                    staffList.add(basStaff);
                }
            }
        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        if (CollectionUtil.isNotEmpty(errMsgList)){
            return EmsResultEntity.error(errMsgList);
        }
        if (CollectionUtil.isNotEmpty(warnMsgList)){
            return EmsResultEntity.warning(staffList, warnMsgList, null);
        }
        int row = basStaffMapper.inserts(staffList);
        staffList.forEach(staff->{
            //待办通知
            SysTodoTask sysTodoTask = new SysTodoTask();
            sysTodoTask.setTaskCategory(ConstantsEms.TODO_TASK_DB)
                    .setTableName("s_bas_staff")
                    .setDocumentSid(staff.getStaffSid());
            sysTodoTask.setTitle("员工档案: " + staff.getStaffCode() + " 当前是保存状态，请及时处理！")
                    .setDocumentCode(String.valueOf(staff.getStaffCode()))
                    .setNoticeDate(new Date())
                    .setUserId(ApiThreadLocalUtil.get().getUserid());
            sysTodoTaskMapper.insert(sysTodoTask);
            //插入日志
            List<OperMsg> msgList=new ArrayList<>();
            MongodbDeal.insert(Long.valueOf(staff.getStaffSid()), staff.getHandleStatus(), null, TITLE,null);
        });
        return EmsResultEntity.success(row);
    }

    private void copy(List<Object> objects, List<List<Object>> readAll) {
        //获取第一行的列数
        int size = readAll.get(0).size();
        //当前行的列数
        int lineSize = objects.size();
        for (int i = lineSize; i < size; i++) {
            Object o = null;
            objects.add(o);
        }
    }

    /**
     * 供应商-附件对象
     */
    private void addBasStaffAttachment(BasStaff basStaff) {
        basStaffAttachmentMapper.delete(
                new UpdateWrapper<BasStaffAttachment>()
                        .lambda()
                        .eq(BasStaffAttachment::getStaffSid, basStaff.getStaffSid())
        );
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(basStaff.getAttachmentList())) {
            basStaff.getAttachmentList().forEach(o -> {
                o.setStaffSid(basStaff.getStaffSid());
            });
            basStaffAttachmentMapper.inserts(basStaff.getAttachmentList());
        }
    }

    /**
     * 考勤信息/工资单添加员工
     */
    @Override
    public List<BasStaff> addStaff(BasStaff basStaff) {
        List<BasStaff> staffList = new ArrayList<>();
        //获取员工信息
        staffList = basStaffMapper.selectBasStaffList(basStaff);
        return staffList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setIsOnJob(BasStaff basStaff) {
        if (basStaff.getStaffSidList().length == 0) {
            throw new BaseException("请选择行！");
        }
        LambdaUpdateWrapper<BasStaff> updateWrapper = new LambdaUpdateWrapper<>();
        int row = 0;
        if (StrUtil.isBlank(basStaff.getIsOnJob())) {
            basStaff.setIsOnJob(null);
        }
        //在离职状态
        updateWrapper.in(BasStaff::getStaffSid, basStaff.getStaffSidList()).set(BasStaff::getIsOnJob, basStaff.getIsOnJob());
        row = basStaffMapper.update(null, updateWrapper);
        return row;
    }

    /**
     * 新建时校验名称是否存在
     * @param basStaff
     * @return
     */
    @Override
    public EmsResultEntity checkName(BasStaff basStaff) {
        //校验员工编码是否重复
        checkCodeUnique(basStaff);
        if(basStaff.getIdentityCard()!=null){
            if (basStaff.getStaffSid() != null){
                BasStaff staff = basStaffMapper.selectById(basStaff.getStaffSid());
                if (!basStaff.getIdentityCard().equals(staff.getIdentityCard())) {
                    //校验身份证号是否重复
                    checkIdentityCardUnique(basStaff);
                }
            } else {
                //校验身份证号是否重复
                checkIdentityCardUnique(basStaff);
            }
        }
        if (StrUtil.isNotBlank(basStaff.getStaffName())) {
            QueryWrapper<BasStaff> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(BasStaff::getStaffName, basStaff.getStaffName());
            if (basStaff.getStaffSid() != null) {
                queryWrapper.lambda().ne(BasStaff::getStaffSid, basStaff.getStaffSid());
            }
            List<BasStaff> staffList = basStaffMapper.selectList(queryWrapper);
            if (CollectionUtil.isNotEmpty(staffList)){
                CommonErrMsgResponse warnMsg = new CommonErrMsgResponse();
                List<CommonErrMsgResponse> warnMsgList = new ArrayList<>();
                warnMsg.setMsg("已存在" + basStaff.getStaffName() + "姓名的员工，是否继续操作？");
                warnMsgList.add(warnMsg);
                return EmsResultEntity.warning(warnMsgList, "已存在" + basStaff.getStaffName() + "姓名的员工，是否继续操作？");
            }
        }
        return EmsResultEntity.success();
    }

    /**
     * 员工工作状况报表
     *
     * @param basStaff 员工档案
     * @return 员工档案集合
     */
    @Override
    public List<BasStaffConditionForm> conditionBasStaffList(BasStaffConditionForm basStaff) {
        return basStaffMapper.conditionBasStaffList(basStaff);
    }
}
