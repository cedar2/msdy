package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ReqRequireDoc;

import java.util.List;

/**
 * 需求单Service接口
 *
 * @author linhongwei
 * @date 2021-04-02
 */
public interface IReqRequireDocService extends IService<ReqRequireDoc> {
    /**
     * 查询需求单
     *
     * @param requireDocSid 需求单ID
     * @return 需求单
     */
    public ReqRequireDoc selectReqRequireDocById(Long requireDocSid);

    /**
     * 查询需求单列表
     *
     * @param reqRequireDoc 需求单
     * @return 需求单集合
     */
    public List<ReqRequireDoc> selectReqRequireDocList(ReqRequireDoc reqRequireDoc);

    /**
     * 新增需求单
     *
     * @param reqRequireDoc 需求单
     * @return 结果
     */
    public int insertReqRequireDoc(ReqRequireDoc reqRequireDoc);

    /**
     * 修改需求单
     *
     * @param reqRequireDoc 需求单
     * @return 结果
     */
    public int updateReqRequireDoc(ReqRequireDoc reqRequireDoc);

    /**
     * 批量删除需求单
     *
     * @param requireDocSids 需要删除的需求单ID
     * @return 结果
     */
    public int deleteReqRequireDocByIds(Long[] requireDocSids);

    /**
     * 需求单确认
     */
    int confirm(ReqRequireDoc reqRequireDoc);

    /**
     * 需求单变更
     */
    int change(ReqRequireDoc reqRequireDoc);
}
