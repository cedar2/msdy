package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.DelOutsourceMaterialIssueNote;

/**
 * 外发加工发料单Mapper接口
 * 
 * @author linhongwei
 * @date 2021-05-17
 */
public interface DelOutsourceMaterialIssueNoteMapper  extends BaseMapper<DelOutsourceMaterialIssueNote> {


    DelOutsourceMaterialIssueNote selectDelOutsourceMaterialIssueNoteById(Long issueNoteSid);

    List<DelOutsourceMaterialIssueNote> selectDelOutsourceMaterialIssueNoteList(DelOutsourceMaterialIssueNote delOutsourceMaterialIssueNote);

    /**
     * 添加多个
     * @param list List DelOutsourceMaterialIssueNote
     * @return int
     */
    int inserts(@Param("list") List<DelOutsourceMaterialIssueNote> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity DelOutsourceMaterialIssueNote
    * @return int
    */
    int updateAllById(DelOutsourceMaterialIssueNote entity);

    /**
     * 更新多个
     * @param list List DelOutsourceMaterialIssueNote
     * @return int
     */
    int updatesAllById(@Param("list") List<DelOutsourceMaterialIssueNote> list);

}
