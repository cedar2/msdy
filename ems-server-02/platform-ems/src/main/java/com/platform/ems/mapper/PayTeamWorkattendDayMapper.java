package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.PayTeamWorkattendDay;

/**
 * 班组日出勤信息Mapper接口
 * 
 * @author linhongwei
 * @date 2022-07-27
 */
public interface PayTeamWorkattendDayMapper  extends BaseMapper<PayTeamWorkattendDay> {


    PayTeamWorkattendDay selectPayTeamWorkattendDayById(Long teamWorkattendDaySid);

    PayTeamWorkattendDay getPayTeamWorkattendByUserName(String userName);

    List<PayTeamWorkattendDay> selectPayTeamWorkattendDayList(PayTeamWorkattendDay payTeamWorkattendDay);

    /**
     * 添加多个
     * @param list List PayTeamWorkattendDay
     * @return int
     */
    int inserts(@Param("list") List<PayTeamWorkattendDay> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity PayTeamWorkattendDay
    * @return int
    */
    int updateAllById(PayTeamWorkattendDay entity);

    /**
     * 更新多个
     * @param list List PayTeamWorkattendDay
     * @return int
     */
    int updatesAllById(@Param("list") List<PayTeamWorkattendDay> list);


}
