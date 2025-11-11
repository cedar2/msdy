package com.platform.ems.service.document;

import com.platform.system.domain.SysBusinessBcst;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 业务动态Service实现类
 * @author c
 *  Created on 2021/6/30.
 */
public interface BusinessBcstRepository extends MongoRepository<SysBusinessBcst,String> {
}
