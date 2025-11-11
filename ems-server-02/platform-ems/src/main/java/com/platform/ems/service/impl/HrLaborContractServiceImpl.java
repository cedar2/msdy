package com.platform.ems.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.common.core.domain.model.DictData;
import com.platform.common.exception.base.BaseException;
import com.platform.common.utils.bean.BeanUtils;
import com.platform.common.core.domain.document.OperMsg;
import com.platform.common.log.enums.BusinessType;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.utils.file.FileUtils;
import com.platform.ems.constant.ConstantsEms;
import com.platform.ems.domain.*;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.CommonErrMsgResponse;
import com.platform.ems.enums.HandleStatus;
import com.platform.ems.mapper.*;
import com.platform.ems.service.IHrLaborContractService;
import com.platform.ems.util.JudgeFormat;
import com.platform.ems.util.MongodbDeal;
import com.platform.ems.util.MongodbUtil;
import com.platform.system.domain.SysTodoTask;
import com.platform.system.mapper.SysTodoTaskMapper;
import com.platform.system.service.ISysDictDataService;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 劳动合同
 *
 * @author xfzz
 * @date 2024/5/8
 */
@Service
public class HrLaborContractServiceImpl extends ServiceImpl<HrLaborContractMapper, HrLaborContract> implements IHrLaborContractService {

    @Autowired
    private HrLaborContractMapper hrLaborContractMapper;
    @Autowired
    private HrLaborContractAttachMapper hrLaborContractAttachMapper;
    @Autowired
    private SysTodoTaskMapper sysTodoTaskMapper;

    @Autowired
    private ISysDictDataService sysDictDataService;

    @Autowired
    private BasCompanyMapper basCompanyMapper;

    @Autowired
    private BasStaffMapper basStaffMapper;


    private static final String TITLE = "劳动合同";


    /**
     * 查询劳动合同
     *
     * @param laborContractSid 劳动合同ID
     * @return 劳动合同
     */
    @Override
    public HrLaborContract selectHrLaborContractById(Long laborContractSid) {
        HrLaborContract hrLaborContract = hrLaborContractMapper.selectHrLaborContractById(laborContractSid);
        List<HrLaborContractAttach> attachList = hrLaborContractAttachMapper.selectHrLaborContractAttachList(new HrLaborContractAttach().setLaborContractSid(laborContractSid));
        hrLaborContract.setAttachmentList(attachList);
        com.platform.ems.util.MongodbUtil.find(hrLaborContract);
        return hrLaborContract;
    }

    /**
     * 查询劳动合同列表
     *
     * @param hrLaborContract 劳动合同
     * @return 劳动合同
     */
    @Override
    public List<HrLaborContract> selectHrLaborContractList(HrLaborContract hrLaborContract) {
        List<HrLaborContract> list = hrLaborContractMapper.selectHrLaborContractList(hrLaborContract);
        for (HrLaborContract item:list) {
            Long contractRemainingDate;
            // 计算剩余天数
            contractRemainingDate = calculateRemainingDays(LocalDate.now(), item.getEndDate());
            item.setContractRemainingDate(contractRemainingDate);
        }
        return list;
    }

    /**
     * 新增劳动合同
     * 需要注意编码重复校验
     *
     * @param hrLaborContract 劳动合同
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertHrLaborContract(HrLaborContract hrLaborContract) {
        // 写默认值
        hrLaborContract.setSignInStatus("WQS");
        hrLaborContract.setLvyueStatus("LYZ");
        BasCompany basCompany = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>().lambda().
                eq(BasCompany::getCompanySid,hrLaborContract.getCompanySid()));
        hrLaborContract.setCompanyCode(basCompany.getCompanyCode());
        hrLaborContract.setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
        // 写入确认人
        if (ConstantsEms.CHECK_STATUS.equals(hrLaborContract.getHandleStatus())) {
            hrLaborContract.setConfirmDate(new Date()).setConfirmerAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = hrLaborContractMapper.insert(hrLaborContract);
        if (row > 0) {
            if (CollectionUtil.isNotEmpty(hrLaborContract.getAttachmentList())){
                hrLaborContract.getAttachmentList().forEach(item->{
                    item.setLaborContractSid(hrLaborContract.getLaborContractSid());
                });
                hrLaborContractAttachMapper.inserts(hrLaborContract.getAttachmentList());
            }
            //插入日志
            List<OperMsg> msgList;
            msgList = BeanUtils.eq(new HrLaborContract(), hrLaborContract);
            MongodbDeal.insert(hrLaborContract.getLaborContractSid(), hrLaborContract.getHandleStatus(), msgList, TITLE, null, hrLaborContract.getImportType());
        }
        return row;
    }

    /**
     * 变更劳动合同
     *
     * @param hrLaborContract 劳动合同
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int changeHrLaborContract(HrLaborContract hrLaborContract) {
        // 写默认值LaborContractSid
        HrLaborContract response = hrLaborContractMapper.selectHrLaborContractById(hrLaborContract.getLaborContractSid());
        // 更新人更新日期
        List<OperMsg> msgList;
        msgList = BeanUtils.eq(response, hrLaborContract);
        if (CollectionUtil.isNotEmpty(msgList)) {
            hrLaborContract.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
        }
        int row = hrLaborContractMapper.updateAllById(hrLaborContract);
        if (row > 0) {
            addAttach(hrLaborContract);
            if (HandleStatus.SUBMIT.getCode().equals(hrLaborContract.getHandleStatus())){
                // 附件清单
                updateAttach(hrLaborContract);
                //插入日志
                MongodbUtil.insertUserLog(hrLaborContract.getLaborContractSid(), BusinessType.CHANGE.getValue(), response, hrLaborContract, TITLE);

            }
        }
        return row;
    }


    /**
     * 更改确认状态
     *
     * @param hrLaborContract
     * @return
     */
    @Override
    public int check(HrLaborContract hrLaborContract) {
        int row = 0;
        Long[] sids = hrLaborContract.getLaborContractSidList();
        if (sids != null && sids.length > 0) {
            row = hrLaborContractMapper.update(null, new UpdateWrapper<HrLaborContract>().lambda()
                    .set(HrLaborContract::getHandleStatus, hrLaborContract.getHandleStatus())
                    .set(HrLaborContract::getConfirmDate, new Date())
                    .set(HrLaborContract::getConfirmerAccount,ApiThreadLocalUtil.get().getUsername())
                    .set(HrLaborContract::getUpdateDate, new Date())
                    .set(HrLaborContract::getUpdaterAccount, ApiThreadLocalUtil.get().getUsername())
                    .in(HrLaborContract::getLaborContractSid, sids));
            //删除代办
            sysTodoTaskMapper.delete(new QueryWrapper<SysTodoTask>().lambda()
                    .in(SysTodoTask::getDocumentSid,sids)
                    .eq(SysTodoTask::getTaskCategory,ConstantsEms.TODO_TASK_DB));
            //插入日志
            for (Long id : sids) {
                //插入日志
                MongodbDeal.check(id, hrLaborContract.getHandleStatus(), null, TITLE, null);
            }
            //如果是审批通过
            if (ConstantsEms.CHECK_STATUS.equals(hrLaborContract.getHandleStatus())){
                List<HrLaborContract> recordList = hrLaborContractMapper.selectList(new QueryWrapper<HrLaborContract>().lambda()
                        .in(HrLaborContract::getLaborContractSid,sids));
                if (CollectionUtil.isNotEmpty(recordList)){
                    recordList.forEach(record->{

                    });
                }
            }
        }
        return row;
    }

    /**
     * 劳动合同签收
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int signHrLaborContractById(HrLaborContract hrLaborContract) {
        LambdaUpdateWrapper<HrLaborContract> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(HrLaborContract::getLaborContractSid,hrLaborContract.getLaborContractSids())
                .set(HrLaborContract::getSignInStatus, hrLaborContract.getSignInStatus());
        int row = hrLaborContractMapper.update(null, updateWrapper);
        return row;
    }

    /**
     * 设置履约状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setLvyueStatusById(HrLaborContract hrLaborContract) {
        LambdaUpdateWrapper<HrLaborContract> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(HrLaborContract::getLaborContractSid,hrLaborContract.getLaborContractSids())
                .set(HrLaborContract::getLvyueStatus, hrLaborContract.getLvyueStatus());
        int row = hrLaborContractMapper.update(null, updateWrapper);
        if(row > 0){
            for (Long id : hrLaborContract.getLaborContractSids()) {
                //插入日志
                MongodbUtil.insertUserLog(id, BusinessType.QITA.getValue(),
                        null, TITLE,"设置履约状态");
            }
        }
        return row;
    }

    /**
     * 设置履约状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int endLvyueStatusById(HrLaborContract hrLaborContract) {
        LambdaUpdateWrapper<HrLaborContract> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(HrLaborContract::getLaborContractSid,hrLaborContract.getLaborContractSids())
                .set(HrLaborContract::getLvyueStatus, "YZZ");
        int row = hrLaborContractMapper.update(null, updateWrapper);
        if(row > 0){
            //插入日志
            MongodbUtil.insertUserLog(hrLaborContract.getLaborContractSid(), BusinessType.OTHER.getValue(),
                    null, TITLE,"终止");
        }
        return row;
    }
    /**
     * 处理附件
     *
     * @param hrLaborContract
     * @return
     */
    public void addAttach(HrLaborContract hrLaborContract){
        hrLaborContractAttachMapper.delete(new QueryWrapper<HrLaborContractAttach>().lambda()
                .eq(HrLaborContractAttach::getLaborContractSid,hrLaborContract.getLaborContractSid()));
        if (CollectionUtil.isNotEmpty(hrLaborContract.getAttachmentList())){
            hrLaborContract.getAttachmentList().forEach(item->{
                item.setLaborContractSid(hrLaborContract.getLaborContractSid());
            });
            hrLaborContractAttachMapper.inserts(hrLaborContract.getAttachmentList());
        }
    }

    /**
     * 批量修改附件信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateAttach(HrLaborContract record) {
        // 先删后加
        hrLaborContractAttachMapper.delete(new QueryWrapper<HrLaborContractAttach>().lambda()
                .eq(HrLaborContractAttach::getLaborContractSid, record.getLaborContractSid()));
        if (CollectionUtil.isNotEmpty(record.getAttachmentList())) {
            record.getAttachmentList().forEach(att -> {
                // 如果是新的
                if (att.getLaborContractSid() == null) {
                    att.setClientId(ApiThreadLocalUtil.get().getSysUser().getClientId())
                            .setCreateDate(new Date()).setCreatorAccount(ApiThreadLocalUtil.get().getUsername());
                    att.setLaborContractSid(record.getLaborContractSid());
                }
                // 如果是旧的就写入更改日期
                else {
                    att.setUpdateDate(new Date()).setUpdaterAccount(ApiThreadLocalUtil.get().getUsername());
                }
            });
            hrLaborContractAttachMapper.inserts(record.getAttachmentList());
        }
    }

    public static long calculateRemainingDays(LocalDate currentDate, Date endDate) {
        LocalDate endDateDate = endDate.toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        // 计算两个 LocalDate 之间的天数差
        Long row;
        row = ChronoUnit.DAYS.between(currentDate, endDateDate);
        if(row<0){
            row = 0L;
        }
        return row;
    }


    /**
     * 导入
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmsResultEntity importRecord(MultipartFile file) {
        int num = 0;
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

            // 数据字典验货阶段
            List<DictData> inspectionStageList = sysDictDataService.selectDictData("s_inspection_stage");
            inspectionStageList = inspectionStageList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> inspectionStageMaps = inspectionStageList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典问题类型
            List<DictData> defectTypeList = sysDictDataService.selectDictData("s_defect_type");
            defectTypeList = defectTypeList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> defectTypeMaps = defectTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典解决状态
            List<DictData> resolveStatusList = sysDictDataService.selectDictData("s_resolve_status");
            resolveStatusList = resolveStatusList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> resolveStatusMaps = resolveStatusList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典验货方式
            List<DictData> inspectionMethodList = sysDictDataService.selectDictData("s_inspection_method");
            inspectionMethodList = inspectionMethodList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> inspectionMethodMaps = inspectionMethodList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典验货结果
            List<DictData> resultList = sysDictDataService.selectDictData("s_inspection_result");
            resultList = resultList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> resultMaps = resultList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));

            // 数据字典验货结果
            List<DictData> inspectionPartnerTypeList = sysDictDataService.selectDictData("s_inspection_partner_type");
            inspectionPartnerTypeList = inspectionPartnerTypeList.stream().filter(o -> o.getStatus().equals(ConstantsEms.SYS_COMMON_STATUS_Y)).collect(Collectors.toList());
            Map<String, String> inspectionPartnerTypeMaps = inspectionPartnerTypeList.stream().collect(Collectors.toMap(DictData::getDictLabel, DictData::getDictValue, (key1, key2) -> key2));


            // 错误信息
            CommonErrMsgResponse errMsg = null;
            List<CommonErrMsgResponse> errMsgList = new ArrayList<>();
            // 警告信息
            CommonErrMsgResponse warnMsg = null;
            List<CommonErrMsgResponse> warnMsgList = new ArrayList<>();
            // 提示信息
            CommonErrMsgResponse infoMsg = null;
            List<CommonErrMsgResponse> infoMsgList = new ArrayList<>();

            // 列表
            List<HrLaborContract> recordList = new ArrayList<>();

            if (readAll.size() > 100) {
                throw new BaseException("导入表格中数据请不要超过100行！");
            }

            // 循环文件
            for (int i = 0; i < readAll.size(); i++) {
                if (i < 2) {
                    //前两行跳过
                    continue;
                }
                List<Object> objects = readAll.get(i);
                //填充总列数
                copy(objects, readAll);
                num = i + 1;

                // 主表
                HrLaborContract record = new HrLaborContract();
                /*
                 * 有效期(起) 必填
                 */
                String startDateS = objects.get(0) == null || objects.get(0) == "" ? null : objects.get(0).toString().trim();
                /*
                 * 有效期(至) 必填
                 */
                String endDateS = objects.get(1) == null || objects.get(1) == "" ? null : objects.get(1).toString().trim();
                /*
                 * 签约日期 必填
                 */
                String contractSignDateS = objects.get(2) == null || objects.get(2) == "" ? null : objects.get(2).toString().trim();
                /*
                 * 公司简称 必填
                 */
                String companyShortName = objects.get(3) == null || objects.get(3) == "" ? null : objects.get(3).toString().trim();
                /*
                 * 公司邮箱 选填
                 */
                String emailCompany = objects.get(4) == null || objects.get(4) == "" ? null : objects.get(4).toString().trim();
                /*
                 * 员工编号 必填
                 */
                String staffCode = objects.get(5) == null || objects.get(5) == "" ? null : objects.get(5).toString().trim();
                /*
                 * 员工姓名 必填
                 */
                String staffName = objects.get(6) == null || objects.get(6) == "" ? null : objects.get(6).toString().trim();
                /*
                 * 身份证号 必填
                 */
                String identityCard = objects.get(7) == null || objects.get(7) == "" ? null : objects.get(7).toString().trim();
                /*
                 * 员工联系电话 必填
                 */
                String mobphone = objects.get(8) == null || objects.get(8) == "" ? null : objects.get(8).toString().trim();
                /*
                 * 员工邮箱 选填
                 */
                String emailStaff = objects.get(9) == null || objects.get(9) == "" ? null : objects.get(9).toString().trim();
                /*
                 * 员工居住地址 选填
                 */
                String homeAddr = objects.get(10) == null || objects.get(10) == "" ? null : objects.get(10).toString().trim();
                /*
                 * 备注 选填
                 */
                String remark = objects.get(11) == null || objects.get(11) == "" ? null : objects.get(11).toString().trim();


                // 有效期(起)
                Date startDate = null;
                if (StrUtil.isBlank(startDateS)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("有效期(起)不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDate(startDateS)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("有效期(起)格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        startDate = DateUtil.parse(startDateS);
                    }
                }

                // 有效期(至)
                Date endDate = null;
                if (StrUtil.isBlank(endDateS)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("有效期(至)不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDate(endDateS)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("有效期(至)格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        endDate = DateUtil.parse(endDateS);
                    }
                }

                if(startDate != null &&  endDate != null){
                    if(endDate.compareTo(startDate) < 0){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("有效期(起)不能大于有效期(至)，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 签约日期
                Date contractSignDate = null;
                if (StrUtil.isBlank(contractSignDateS)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("签约日期不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    if (!JudgeFormat.isValidDate(contractSignDateS)){
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("签约日期格式错误，导入失败！");
                        errMsgList.add(errMsg);
                    }else {
                        contractSignDate = DateUtil.parse(contractSignDateS);
                    }
                }

                // 公司 -- 其它信息
                Long companySid = null;
                String companyCode = null;
                String ownerName = null;
                String creditCode = null;
                String officeAddr = null;
                if (StrUtil.isBlank(companyShortName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("公司简称不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                        BasCompany company = basCompanyMapper.selectOne(new QueryWrapper<BasCompany>().lambda()
                                .eq(BasCompany::getShortName, companyShortName));
                        if (company == null) {
                            errMsg = new CommonErrMsgResponse();
                            errMsg.setItemNum(num);
                            errMsg.setMsg("公司简称不存在，导入失败！");
                            errMsgList.add(errMsg);
                        } else {
                            if(company.getStatus().equals(ConstantsEms.ENABLE_STATUS)
                                    && company.getHandleStatus().equals(ConstantsEms.CHECK_STATUS)){
                                companySid = company.getCompanySid();
                                companyCode = company.getCompanyCode();
                                ownerName =  company.getOwnerName();
                                creditCode = company.getCreditCode();
                                officeAddr = company.getRegisterAddr();
                            }else{
                                errMsg = new CommonErrMsgResponse();
                                errMsg.setItemNum(num);
                                errMsg.setMsg("公司简称必须为确认且启用的数据，导入失败！");
                                errMsgList.add(errMsg);
                            }
                        }
                    }

                // 员工编号
                if (StrUtil.isBlank(staffCode)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("员工编号不能为空，导入失败！");
                    errMsgList.add(errMsg);
                } else {
                    BasStaff staff = basStaffMapper.selectOne(new QueryWrapper<BasStaff>().lambda()
                            .eq(BasStaff::getStaffCode, staffCode));
                    if (staff == null) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("员工编号不存在，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 员工姓名
                if (StrUtil.isBlank(staffName)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("员工姓名不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }

                // 身份证号
                if (StrUtil.isBlank(identityCard)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("身份证号不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }

                // 员工联系电话
                if (StrUtil.isBlank(mobphone)) {
                    errMsg = new CommonErrMsgResponse();
                    errMsg.setItemNum(num);
                    errMsg.setMsg("员工联系电话不能为空，导入失败！");
                    errMsgList.add(errMsg);
                }

                //公司邮箱
                if (StrUtil.isNotBlank(emailCompany)) {
                    if (emailCompany.length() > 60) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("公司邮箱只能输入60位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                //员工邮箱
                if (StrUtil.isNotBlank(emailStaff)) {
                    if (emailStaff.length() > 60) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("员工邮箱只能输入60位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                //员工居住地址
                if (StrUtil.isNotBlank(homeAddr)) {
                    if (homeAddr.length() > 100) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("员工居住地址只能输入100位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                //备注
                if (StrUtil.isNotBlank(remark)) {
                    if (remark.length() > 600) {
                        errMsg = new CommonErrMsgResponse();
                        errMsg.setItemNum(num);
                        errMsg.setMsg("备注只能输入600位，导入失败！");
                        errMsgList.add(errMsg);
                    }
                }

                // 写入数据
                if (CollectionUtil.isEmpty(errMsgList)) {
                    record.setStartDate(startDate).setEndDate(endDate).setContractSignDate(contractSignDate)
                            .setCompanySid(companySid).setCompanyCode(companyCode).setOwnerName(ownerName)
                            .setCreditCode(creditCode).setOfficeAddr(officeAddr).setStaffCode(staffCode)
                            .setStaffName(staffName).setIdentityCard(identityCard).setMobphone(mobphone)
                            .setEmailCompany(emailCompany).setEmailStaff(emailStaff).setHomeAddr(homeAddr)
                            .setImportType(BusinessType.IMPORT.getValue())
                            .setHandleStatus(ConstantsEms.CHECK_STATUS).setRemark(remark);
                    recordList.add(record);
                }
            }

            // 报错
            if (CollectionUtil.isNotEmpty(errMsgList)) {
                return EmsResultEntity.error(errMsgList);
            }
            else if (CollectionUtil.isNotEmpty(warnMsgList)) {
                String message = null;
                if (CollectionUtil.isNotEmpty(infoMsgList)) {
                    message = "";
                }
                return EmsResultEntity.warning(recordList, warnMsgList, infoMsgList, message);
            }
            else if (CollectionUtil.isNotEmpty(recordList)) {
                for (HrLaborContract record : recordList) {
                    insertHrLaborContract(record);
                }
                String message = null;
                if (CollectionUtil.isNotEmpty(infoMsgList)) {
                    message = "";
                }
                return EmsResultEntity.success(recordList.size(), null, infoMsgList, message);
            }

        } catch (BaseException e) {
            throw new BaseException(e.getDefaultMessage());
        }
        return EmsResultEntity.success();
    }

    public void copy(List<Object> objects, List<List<Object>> readAll){
        //获取第一行的列数
        int size = readAll.get(0).size();
        //当前行的列数
        int lineSize = objects.size();
        ArrayList<Object> all = new ArrayList<>();
        for (int i=lineSize;i<size;i++){
            Object o = new Object();
            o=null;
            objects.add(o);
        }
    }
}
