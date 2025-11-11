package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.DelOutsourceMaterialIssueNote;

/**
 * 外发加工发料单Service接口
 * 
 * @author linhongwei
 * @date 2021-05-17
 */
public interface IDelOutsourceMaterialIssueNoteService extends IService<DelOutsourceMaterialIssueNote>{
    /**
     * 查询外发加工发料单
     * 
     * @param issueNoteSid 外发加工发料单ID
     * @return 外发加工发料单
     */
    public DelOutsourceMaterialIssueNote selectDelOutsourceMaterialIssueNoteById(Long issueNoteSid);

    /**
     * 查询外发加工发料单列表
     * 
     * @param delOutsourceMaterialIssueNote 外发加工发料单
     * @return 外发加工发料单集合
     */
    public List<DelOutsourceMaterialIssueNote> selectDelOutsourceMaterialIssueNoteList(DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote);

    /**
     * 新增外发加工发料单
     * 
     * @param delOutsourceMaterialIssueNote 外发加工发料单
     * @return 结果
     */
    public int insertDelOutsourceMaterialIssueNote(DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote);

    /**
     * 修改外发加工发料单
     * 
     * @param delOutsourceMaterialIssueNote 外发加工发料单
     * @return 结果
     */
    public int updateDelOutsourceMaterialIssueNote(DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote);

    /**
     * 变更外发加工发料单
     *
     * @param delOutsourceMaterialIssueNote 外发加工发料单
     * @return 结果
     */
    public int changeDelOutsourceMaterialIssueNote(DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote);

    /**
     * 批量删除外发加工发料单
     * 
     * @param issueNoteSids 需要删除的外发加工发料单ID
     * @return 结果
     */
    public int deleteDelOutsourceMaterialIssueNoteByIds(List<Long> issueNoteSids);

    /**
     * 更改确认状态
     * @param delOutsourceMaterialIssueNote
     * @return
     */
    int check(DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote);

    int verify(Long issueNoteSid, String handleStatus);
}
