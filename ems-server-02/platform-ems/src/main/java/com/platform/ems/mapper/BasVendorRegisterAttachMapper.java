package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.BasVendorRegisterAttach;

/**
 * 供应商注册-附件Mapper接口
 * 
 * @author chenkw
 * @date 2022-02-21
 */
public interface BasVendorRegisterAttachMapper  extends BaseMapper<BasVendorRegisterAttach> {


    BasVendorRegisterAttach selectBasVendorRegisterAttachById(Long vendorRegisterAttachSid);

    List<BasVendorRegisterAttach> selectBasVendorRegisterAttachList(BasVendorRegisterAttach basVendorRegisterAttach);

    /**
     * 添加多个
     * @param list List BasVendorRegisterAttach
     * @return int
     */
    int inserts(@Param("list") List<BasVendorRegisterAttach> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity BasVendorRegisterAttach
    * @return int
    */
    int updateAllById(BasVendorRegisterAttach entity);

    /**
     * 更新多个
     * @param list List BasVendorRegisterAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<BasVendorRegisterAttach> list);


}
