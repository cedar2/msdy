package com.platform.ems.service.document;

import com.platform.system.domain.SysOverdueBusiness;
import org.springframework.data.mongodb.repository.MongoRepository;


/**
 * 已预期警示Service实现类
 * @author c
 *  Created on 2021/6/29.
 */
public interface OverdueBusinessRepository extends MongoRepository<SysOverdueBusiness,String> {
}
