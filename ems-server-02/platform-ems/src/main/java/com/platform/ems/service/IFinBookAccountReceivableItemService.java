package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinBookAccountReceivableItem;
import com.platform.ems.domain.dto.request.form.FinBookAccountReceivableFormRequest;

/**
 * 财务流水账-明细-应收Service接口
 *
 * @author linhongwei
 * @date 2021-06-11
 */
public interface IFinBookAccountReceivableItemService extends IService<FinBookAccountReceivableItem>{

    /**
     * 设置到期日前的校验
     * @param request
     * @return
     */
    void verifyValidDate(FinBookAccountReceivableFormRequest request);

    /**
     * 设置到期日
     * @param request
     * @return
     */
    int setValidDate(FinBookAccountReceivableFormRequest request);

}
