package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.PayWorkattendRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 考勤信息-主Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-14
 */
public interface PayWorkattendRecordMapper extends BaseMapper<PayWorkattendRecord> {


    PayWorkattendRecord selectPayWorkattendRecordById(Long workattendRecordSid);

    List<PayWorkattendRecord> selectPayWorkattendRecordList(PayWorkattendRecord payWorkattendRecord);

    /**
     * 添加多个
     *
     * @param list List PayWorkattendRecord
     * @return int
     */
    int inserts(@Param("list") List<PayWorkattendRecord> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PayWorkattendRecord
     * @return int
     */
    int updateAllById(PayWorkattendRecord entity);

    /**
     * 更新多个
     *
     * @param list List PayWorkattendRecord
     * @return int
     */
    int updatesAllById(@Param("list") List<PayWorkattendRecord> list);


}
