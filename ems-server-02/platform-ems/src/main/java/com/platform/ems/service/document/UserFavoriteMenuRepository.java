package com.platform.ems.service.document;

import com.platform.ems.domain.SysUserFavoriteMenu;
import org.springframework.data.mongodb.repository.MongoRepository;


/**
 * 用户收藏菜单Service实现类
 * @author c
 *  Created on 2021/6/29.
 */
public interface UserFavoriteMenuRepository extends MongoRepository<SysUserFavoriteMenu,String> {
}
