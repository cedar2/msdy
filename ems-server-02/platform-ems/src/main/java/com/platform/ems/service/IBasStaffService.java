package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.BasStaff;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.form.BasStaffConditionForm;
import org.springframework.web.multipart.MultipartFile;

/**
 * 员工档案Service接口
 *
 * @author linhongwei
 * @date 2021-03-17
 */
public interface IBasStaffService extends IService<BasStaff>{
    /**
     * 查询员工档案
     *
     * @param staffSid 员工档案ID
     * @return 员工档案
     */
    BasStaff selectBasStaffById(Long staffSid);

    /**
     * 查询员工档案的编码和名称
     *
     * @param staffSid 员工档案ID
     * @return 员工档案
     */
    BasStaff selectCodeNameById(Long staffSid);

    /**
     * 查询员工档案列表
     *
     * @param basStaff 员工档案
     * @return 员工档案集合
     */
    List<BasStaff> selectBasStaffList(BasStaff basStaff);

    /**
     * 新增员工档案
     *
     * @param basStaff 员工档案
     * @return 结果
     */
    int insertBasStaff(BasStaff basStaff);

    /**
     * 批量直接新增员工档案 (无校验)
     *
     * @param basStaffList 员工档案
     * @return 结果
     */
    int insertBasStaff(List<BasStaff> basStaffList);

    /**
     * 修改员工档案
     *
     * @param basStaff 员工档案
     * @return 结果
     */
    int updateBasStaff(BasStaff basStaff);

    /**
     * 新增离职证明校验
     *
     * @param staffSid 员工ID
     * @return 结果
     */
    AjaxResult cheackHrDimissionCertificateById(Long staffSid);

    /**
     * 新增收入证明校验
     *
     * @param staffSid 员工ID
     * @return 结果
     */
    AjaxResult cheackHrIncomeCertificateById(Long staffSid);

    /**
     * 新增其它人事证明校验
     *
     * @param staffSid 员工ID
     * @return 结果
     */
    AjaxResult cheackHrOtherPersonnelCertificateById(Long staffSid);

    /**
     * 批量删除员工档案
     *
     * @param staffSids 需要删除的员工档案ID
     * @return 结果
     */
    int deleteBasStaffByIds(List<Long> staffSids);

    /**
     * 启用停用
     */
    int changeStatus(BasStaff basStaff);

    /**
     * 批量确认
     */
    int check(BasStaff basStaff);

    /**
     * 员工下拉框
     */
    List<BasStaff> getStaffList(BasStaff basStaff);

    /**
     * 员工下拉框  适用于件薪那边 取并集
     */
    List<BasStaff> getStaffAndWorkList(BasStaff basStaff);

    /**
     * 根据公司查询公司下面的员工
     */
    List<BasStaff> getCompanyStaff(Long companySid);

    /**
     * 导入员工档案
     */
    EmsResultEntity importData(MultipartFile file);

    /**
     * 考勤信息/工资单添加员工
     */
    List<BasStaff> addStaff(BasStaff basStaff);

    /**
     * 设置在离职状态
     * @param basStaff
     * @return
     */
    int setIsOnJob(BasStaff basStaff);

    /**
     * 新建时校验名称是否存在
     * @param basStaff
     * @return
     */
    EmsResultEntity checkName(BasStaff basStaff);

    /**
     * 员工工作状况报表
     *
     * @param basStaff 员工档案
     * @return 员工档案集合
     */
    List<BasStaffConditionForm> conditionBasStaffList(BasStaffConditionForm basStaff);
}
