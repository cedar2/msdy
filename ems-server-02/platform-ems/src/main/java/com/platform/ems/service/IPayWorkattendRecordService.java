package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.common.core.domain.AjaxResult;
import com.platform.ems.domain.PayWorkattendRecord;
import com.platform.ems.domain.base.EmsResultEntity;
import com.platform.ems.domain.dto.response.PayWorkattendRecordItemResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 考勤信息-主Service接口
 *
 * @author linhongwei
 * @date 2021-09-14
 */
public interface IPayWorkattendRecordService extends IService<PayWorkattendRecord> {
    /**
     * 查询考勤信息-主
     *
     * @param workattendRecordSid 考勤信息-主ID
     * @return 考勤信息-主
     */
    public PayWorkattendRecord selectPayWorkattendRecordById(Long workattendRecordSid);

    /**
     * 查询考勤信息-主列表
     *
     * @param payWorkattendRecord 考勤信息-主
     * @return 考勤信息-主集合
     */
    public List<PayWorkattendRecord> selectPayWorkattendRecordList(PayWorkattendRecord payWorkattendRecord);
    /**
     * 查询考勤信息-明细报表
     */
    public List<PayWorkattendRecordItemResponse> report(PayWorkattendRecord payWorkattendRecord);
    /**
     * 新增考勤信息-主
     *
     * @param payWorkattendRecord 考勤信息-主
     * @return 结果
     */
    public int insertPayWorkattendRecord(PayWorkattendRecord payWorkattendRecord);

    /**
     * 修改考勤信息-主
     *
     * @param payWorkattendRecord 考勤信息-主
     * @return 结果
     */
    public int updatePayWorkattendRecord(PayWorkattendRecord payWorkattendRecord);

    /**
     * 变更考勤信息-主
     *
     * @param payWorkattendRecord 考勤信息-主
     * @return 结果
     */
    public int changePayWorkattendRecord(PayWorkattendRecord payWorkattendRecord);

    /**
     * 批量删除考勤信息-主
     *
     * @param workattendRecordSids 需要删除的考勤信息-主ID
     * @return 结果
     */
    public int deletePayWorkattendRecordByIds(List<Long> workattendRecordSids);

    /**
     * 更改确认状态
     *
     * @param payWorkattendRecord
     * @return
     */
    int check(PayWorkattendRecord payWorkattendRecord);

    /**
     * 单据提交校验
     */
    int verify(PayWorkattendRecord payWorkattendRecord);

    /**
     * 选择某一笔主表导出它的明细
     *
     * @param
     * @return
     */
    void exportItemByRecord(HttpServletResponse response, PayWorkattendRecord payWorkattendRecord);

    /**
     * 选择某一笔主表导入它的明细
     *
     * @param file
     * @return
     */
    Object importItemData(MultipartFile file, String workattendRecordCode);
    /**
     *考勤 导入
     */
    public EmsResultEntity importDataM(MultipartFile file);
}
