package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasStaffAttachment;

/**
 * 员工-附件Service接口
 *
 * @author chenkw
 * @date 2021-09-13
 */
public interface IBasStaffAttachmentService extends IService<BasStaffAttachment>{
    /**
     * 查询员工-附件
     *
     * @param staffAttachmentSid 员工-附件ID
     * @return 员工-附件
     */
    public BasStaffAttachment selectBasStaffAttachmentById(Long staffAttachmentSid);

    /**
     * 查询员工-附件列表
     *
     * @param basStaffAttachment 员工-附件
     * @return 员工-附件集合
     */
    public List<BasStaffAttachment> selectBasStaffAttachmentList(BasStaffAttachment basStaffAttachment);

    /**
     * 新增员工-附件
     *
     * @param basStaffAttachment 员工-附件
     * @return 结果
     */
    public int insertBasStaffAttachment(BasStaffAttachment basStaffAttachment);

    /**
     * 修改员工-附件
     *
     * @param basStaffAttachment 员工-附件
     * @return 结果
     */
    public int updateBasStaffAttachment(BasStaffAttachment basStaffAttachment);

    /**
     * 变更员工-附件
     *
     * @param basStaffAttachment 员工-附件
     * @return 结果
     */
    public int changeBasStaffAttachment(BasStaffAttachment basStaffAttachment);

    /**
     * 批量删除员工-附件
     *
     * @param staffAttachmentSids 需要删除的员工-附件ID
     * @return 结果
     */
    public int deleteBasStaffAttachmentByIds(List<Long>  staffAttachmentSids);


}
