package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.RepManufactureStatistic;

/**
 * 生产统计报Mapper接口
 *
 * @author chenkw
 * @date 2022-05-11
 */
public interface RepManufactureStatisticMapper  extends BaseMapper<RepManufactureStatistic> {


    RepManufactureStatistic selectRepManufactureStatisticById(Long dataRecordSid);

    List<RepManufactureStatistic> selectRepManufactureStatisticList(RepManufactureStatistic repManufactureStatistic);

    /**
     * 添加多个
     * @param list List RepManufactureStatistic
     * @return int
     */
    int inserts(@Param("list") List<RepManufactureStatistic> list);

}
