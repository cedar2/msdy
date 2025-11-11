package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasCustomerAttach;

/**
 * 客户档案-附件Mapper接口
 *
 * @author chenkw
 * @date 2021-09-15
 */
public interface BasCustomerAttachMapper  extends BaseMapper<BasCustomerAttach> {


    BasCustomerAttach selectBasCustomerAttachById(Long attachmentSid);

    List<BasCustomerAttach> selectBasCustomerAttachList(BasCustomerAttach basCustomerAttach);

    /**
     * 添加多个
     * @param list List BasCustomerAttach
     * @return int
     */
    int inserts(@Param("list") List<BasCustomerAttach> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     * @param entity BasCustomerAttach
     * @return int
     */
    int updateAllById(BasCustomerAttach entity);

    /**
     * 更新多个
     * @param list List BasCustomerAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<BasCustomerAttach> list);


}