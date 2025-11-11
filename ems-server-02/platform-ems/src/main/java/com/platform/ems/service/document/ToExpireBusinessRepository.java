package com.platform.ems.service.document;

import com.platform.system.domain.SysToexpireBusiness;
import org.springframework.data.mongodb.repository.MongoRepository;


/**
 * 即将逾期预警Service实现类
 * @author c
 *  Created on 2021/6/29.
 */
public interface ToExpireBusinessRepository extends MongoRepository<SysToexpireBusiness,String> {
}
