package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.BasCompanyAttach;

/**
 * 公司档案-附件Service接口
 *
 * @author chenkw
 * @date 2021-09-15
 */
public interface IBasCompanyAttachService extends IService<BasCompanyAttach>{
    /**
     * 查询公司档案-附件
     *
     * @param attachmentSid 公司档案-附件ID
     * @return 公司档案-附件
     */
    public BasCompanyAttach selectBasCompanyAttachById(Long attachmentSid);

    /**
     * 查询公司档案-附件列表
     *
     * @param basCompanyAttach 公司档案-附件
     * @return 公司档案-附件集合
     */
    public List<BasCompanyAttach> selectBasCompanyAttachList(BasCompanyAttach basCompanyAttach);

    /**
     * 新增公司档案-附件
     *
     * @param basCompanyAttach 公司档案-附件
     * @return 结果
     */
    public int insertBasCompanyAttach(BasCompanyAttach basCompanyAttach);

    /**
     * 修改公司档案-附件
     *
     * @param basCompanyAttach 公司档案-附件
     * @return 结果
     */
    public int updateBasCompanyAttach(BasCompanyAttach basCompanyAttach);

    /**
     * 变更公司档案-附件
     *
     * @param basCompanyAttach 公司档案-附件
     * @return 结果
     */
    public int changeBasCompanyAttach(BasCompanyAttach basCompanyAttach);

    /**
     * 批量删除公司档案-附件
     *
     * @param attachmentSids 需要删除的公司档案-附件ID
     * @return 结果
     */
    public int deleteBasCompanyAttachByIds(List<Long>  attachmentSids);

}