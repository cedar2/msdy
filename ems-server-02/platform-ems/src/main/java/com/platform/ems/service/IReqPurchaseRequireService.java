package com.platform.ems.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.ReqPurchaseRequire;
import com.platform.ems.domain.base.EmsResultEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 申购单Service接口
 *
 * @author linhongwei
 * @date 2021-04-06
 */
public interface IReqPurchaseRequireService extends IService<ReqPurchaseRequire>{
    /**
     * 查询申购单
     *
     * @param purchaseRequireSid 申购单ID
     * @return 申购单
     */
    public ReqPurchaseRequire selectReqPurchaseRequireById(Long purchaseRequireSid);

    /**
     * 查询申购单列表
     *
     * @param reqPurchaseRequire 申购单
     * @return 申购单集合
     */
    public List<ReqPurchaseRequire> selectReqPurchaseRequireList(ReqPurchaseRequire reqPurchaseRequire);

    /**
     * 新增申购单
     *
     * @param reqPurchaseRequire 申购单
     * @return 结果
     */
    public int insertReqPurchaseRequire(ReqPurchaseRequire reqPurchaseRequire);

    /**
     * 修改申购单
     *
     * @param reqPurchaseRequire 申购单
     * @return 结果
     */
    public int updateReqPurchaseRequire(ReqPurchaseRequire reqPurchaseRequire);

    /**
     * 批量删除申购单
     *
     * @param purchaseRequireSids 需要删除的申购单ID
     * @return 结果
     */
    public int deleteReqPurchaseRequireByIds(Long[] purchaseRequireSids);

    /**
     * 作废
     */
    public int cancellationByIds(ReqPurchaseRequire reqPurchaseRequire);

    /**
     * 关闭
     */
    public int closeByIds(ReqPurchaseRequire reqPurchaseRequire);

    /**
     * 申购单提交
     */
    void submit(ReqPurchaseRequire reqPurchaseRequire);

    /**
     * 申购单确认
     */
    int check(ReqPurchaseRequire reqPurchaseRequire);

    /**
     * 申购单变更
     */
    int change(ReqPurchaseRequire reqPurchaseRequire);

    /**
     * 导入申购单
     */
    EmsResultEntity importData(MultipartFile file);
}
