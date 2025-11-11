package com.platform.ems.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.QuaProductCheckRecord;
import com.platform.ems.domain.base.EmsResultEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商品检测问题台账Service接口
 *
 * @author admin
 * @date 2024-03-06
 */
public interface IQuaProductCheckRecordService extends IService<QuaProductCheckRecord> {
    /**
     * 查询商品检测问题台账
     *
     * @param productCheckRecordSid 商品检测问题台账ID
     * @return 商品检测问题台账
     */
    public QuaProductCheckRecord selectQuaProductCheckRecordById(Long productCheckRecordSid);

    /**
     * 查询商品检测问题台账列表
     *
     * @param quaProductCheckRecord 商品检测问题台账
     * @return 商品检测问题台账集合
     */
    public List<QuaProductCheckRecord> selectQuaProductCheckRecordList(QuaProductCheckRecord quaProductCheckRecord);

    /**
     * 新增商品检测问题台账
     *
     * @param quaProductCheckRecord 商品检测问题台账
     * @return 结果
     */
    public int insertQuaProductCheckRecord(QuaProductCheckRecord quaProductCheckRecord);

    /**
     * 修改商品检测问题台账
     *
     * @param quaProductCheckRecord 商品检测问题台账
     * @return 结果
     */
    public int updateQuaProductCheckRecord(QuaProductCheckRecord quaProductCheckRecord);

    /**
     * 变更商品检测问题台账
     *
     * @param quaProductCheckRecord 商品检测问题台账
     * @return 结果
     */
    public int changeQuaProductCheckRecord(QuaProductCheckRecord quaProductCheckRecord);

    /**
     * 批量删除商品检测问题台账
     *
     * @param productCheckRecordSids 需要删除的商品检测问题台账ID
     * @return 结果
     */
    public int deleteQuaProductCheckRecordByIds(List<Long> productCheckRecordSids);

    /**
     * 更改确认状态
     *
     * @param quaProductCheckRecord
     * @return
     */
    int check(QuaProductCheckRecord quaProductCheckRecord);

    /**
     * 导入
     */
    EmsResultEntity importRecord(MultipartFile file);
}
