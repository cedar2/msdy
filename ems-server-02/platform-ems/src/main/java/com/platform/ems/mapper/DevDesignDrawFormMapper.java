package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.DevDesignDrawForm;

/**
 * 图稿批复单Mapper接口
 * 
 * @author qhq
 * @date 2021-11-05
 */
public interface DevDesignDrawFormMapper  extends BaseMapper<DevDesignDrawForm> {


    DevDesignDrawForm selectDevDesignDrawFormById (Long designDrawFormSid);

    List<DevDesignDrawForm> selectDevDesignDrawFormList (DevDesignDrawForm devDesignDrawForm);

    /**
     * 添加多个
     * @param list List DevDesignDrawForm
     * @return int
     */
    int inserts (@Param("list") List<DevDesignDrawForm> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity DevDesignDrawForm
    * @return int
    */
    int updateAllById (DevDesignDrawForm entity);

    /**
     * 更新多个
     * @param list List DevDesignDrawForm
     * @return int
     */
    int updatesAllById (@Param("list") List<DevDesignDrawForm> list);


}
