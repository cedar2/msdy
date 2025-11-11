package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.ManDayManufactureProgressAttach;

/**
 * 生产进度日报-附件Mapper接口
 * 
 * @author linhongwei
 * @date 2021-06-09
 */
public interface ManDayManufactureProgressAttachMapper  extends BaseMapper<ManDayManufactureProgressAttach> {


    ManDayManufactureProgressAttach selectManDayManufactureProgressAttachById(Long dayManufactureProgressAttachSid);

    List<ManDayManufactureProgressAttach> selectManDayManufactureProgressAttachList(ManDayManufactureProgressAttach manDayManufactureProgressAttach);

    /**
     * 添加多个
     * @param list List ManDayManufactureProgressAttach
     * @return int
     */
    int inserts(@Param("list") List<ManDayManufactureProgressAttach> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity ManDayManufactureProgressAttach
    * @return int
    */
    int updateAllById(ManDayManufactureProgressAttach entity);

    /**
     * 更新多个
     * @param list List ManDayManufactureProgressAttach
     * @return int
     */
    int updatesAllById(@Param("list") List<ManDayManufactureProgressAttach> list);

}
