package com.platform.ems.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.platform.ems.domain.TecProductZipper;

/**
 * 商品所用拉链Mapper接口
 * 
 * @author c
 * @date 2021-08-03
 */
public interface TecProductZipperMapper  extends BaseMapper<TecProductZipper> {


    TecProductZipper selectTecProductZipperById(Long productZipperSid);

    List<TecProductZipper> selectTecProductZipperList(TecProductZipper tecProductZipper);

    /**
     * 添加多个
     * @param list List TecProductZipper
     * @return int
     */
    int inserts(@Param("list") List<TecProductZipper> list);

    /**
    * 全量更新
    * null字段也会进行更新，慎用
    * @param entity TecProductZipper
    * @return int
    */
    int updateAllById(TecProductZipper entity);

    /**
     * 更新多个
     * @param list List TecProductZipper
     * @return int
     */
    int updatesAllById(@Param("list") List<TecProductZipper> list);


}
