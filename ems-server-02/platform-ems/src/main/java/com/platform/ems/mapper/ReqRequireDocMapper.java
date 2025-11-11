package com.platform.ems.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.ems.domain.ReqRequireDoc;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 需求单Mapper接口
 *
 * @author linhongwei
 * @date 2021-04-02
 */
public interface ReqRequireDocMapper extends BaseMapper<ReqRequireDoc> {


    ReqRequireDoc selectReqRequireDocById(Long requireDocSid);

    List<ReqRequireDoc> selectReqRequireDocList(ReqRequireDoc reqRequireDoc);

    /**
     * 添加多个
     *
     * @param list List ReqRequireDoc
     * @return int
     */
    int inserts(@Param("list") List<ReqRequireDoc> list);

    /**
     * 全量更新
     * null字段也会进行更新，慎用
     *
     * @param entity ReqRequireDoc
     * @return int
     */
    int updateAllById(ReqRequireDoc entity);

    /**
     * 更新多个
     *
     * @param list List ReqRequireDoc
     * @return int
     */
    int updatesAllById(@Param("list") List<ReqRequireDoc> list);


    int deleteReqRequireDocByIds(@Param("array") Long[] requireDocSids);

    int countByDomain(ReqRequireDoc params);

    int confirm(ReqRequireDoc reqRequireDoc);
}
