package com.platform.ems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.ems.domain.FinBookAccountPayableItem;
import com.platform.ems.domain.dto.request.form.FinBookAccountPayableFormRequest;

/**
 * 财务流水账-明细-应付Service接口
 *
 * @author linhongwei
 * @date 2021-06-03
 */
public interface IFinBookAccountPayableItemService extends IService<FinBookAccountPayableItem>{

    /**
     * 设置到期日前的校验
     * @param request
     * @return
     */
    void verifyValidDate(FinBookAccountPayableFormRequest request);

    /**
     * 设置到期日
     * @param request
     * @return
     */
    int setValidDate(FinBookAccountPayableFormRequest request);
}
