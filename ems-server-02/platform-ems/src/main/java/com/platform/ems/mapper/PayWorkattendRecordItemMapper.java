package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.PayWorkattendRecord;
import com.platform.ems.domain.PayWorkattendRecordItem;
import com.platform.ems.domain.dto.response.PayWorkattendRecordItemResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 考勤信息-明细Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-14
 */
public interface PayWorkattendRecordItemMapper extends BaseMapper<PayWorkattendRecordItem> {


    PayWorkattendRecordItem selectPayWorkattendRecordItemById(Long recordItemSid);
    List<PayWorkattendRecordItemResponse> getreport(PayWorkattendRecord payWorkattendRecord);
    List<PayWorkattendRecordItem> selectPayWorkattendRecordItemList(PayWorkattendRecordItem payWorkattendRecordItem);

    /**
     * 添加多个
     *
     * @param list List PayWorkattendRecordItem
     * @return int
     */
    int inserts(@Param("list") List<PayWorkattendRecordItem> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PayWorkattendRecordItem
     * @return int
     */
    int updateAllById(PayWorkattendRecordItem entity);

    /**
     * 更新多个
     *
     * @param list List PayWorkattendRecordItem
     * @return int
     */
    int updatesAllById(@Param("list") List<PayWorkattendRecordItem> list);


}
