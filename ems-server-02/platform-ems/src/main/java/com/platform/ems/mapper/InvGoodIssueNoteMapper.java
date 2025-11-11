package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvGoodIssueNote;

/**
 * 发货单Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-01
 */
public interface InvGoodIssueNoteMapper  extends BaseMapper<InvGoodIssueNote> {


    InvGoodIssueNote selectInvGoodIssueNoteById(@Param("noteSid")Long noteSid);

    List<InvGoodIssueNote> selectInvGoodIssueNoteList(InvGoodIssueNote invGoodIssueNote);

    /**
     * 添加多个
     * @param list List InvGoodIssueNote
     * @return int
     */
    int inserts(@Param("list") List<InvGoodIssueNote> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvGoodIssueNote
    * @return int
    */
    int updateAllById(InvGoodIssueNote entity);

    /**
     * 更新多个
     * @param list List InvGoodIssueNote
     * @return int
     */
    int updatesAllById(@Param("list") List<InvGoodIssueNote> list);


}
