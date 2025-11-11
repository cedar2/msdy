package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.InvGoodReceiptNote;

/**
 * 收货单Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-01
 */
public interface InvGoodReceiptNoteMapper  extends BaseMapper<InvGoodReceiptNote> {


    InvGoodReceiptNote selectInvGoodReceiptNoteById(Long goodReceiptNoteSid);

    List<InvGoodReceiptNote> selectInvGoodReceiptNoteList(InvGoodReceiptNote invGoodReceiptNote);

    /**
     * 添加多个
     * @param list List InvGoodReceiptNote
     * @return int
     */
    int inserts(@Param("list") List<InvGoodReceiptNote> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity InvGoodReceiptNote
    * @return int
    */
    int updateAllById(InvGoodReceiptNote entity);

    /**
     * 更新多个
     * @param list List InvGoodReceiptNote
     * @return int
     */
    int updatesAllById(@Param("list") List<InvGoodReceiptNote> list);


}
