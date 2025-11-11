package com.platform.ems.service.document;

import com.platform.system.domain.SysTodoTask;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 代办事项Service实现类
 * @author c
 *  Created on 2021/6/29.
 */
public interface TodoTaskListRepository  extends MongoRepository<SysTodoTask,String> {

}
