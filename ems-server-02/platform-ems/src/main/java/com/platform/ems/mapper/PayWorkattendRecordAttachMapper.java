package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.PayWorkattendRecordAttach;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 考勤信息-附件Mapper接口
 *
 * @author linhongwei
 * @date 2021-09-14
 */
public interface PayWorkattendRecordAttachMapper extends BaseMapper<PayWorkattendRecordAttach> {


    PayWorkattendRecordAttach selectPayWorkattendRecordAttachById(Long attachmentSid);

    List<PayWorkattendRecordAttach> selectPayWorkattendRecordAttachList(PayWorkattendRecordAttach payWorkattendRecordAttach);

    /**
     * 添加多个
     *
     * @param list List PayWorkattendRecordAttach
     * @return int
     */
    int inserts(@Param("list") List<PayWorkattendRecordAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity PayWorkattendRecordAttach
     * @return int
     */
    int updateAllById(PayWorkattendRecordAttach entity);

    /**
     * 更新多个
     *
     * @param list List PayWorkattendRecordAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<PayWorkattendRecordAttach> list);


}
