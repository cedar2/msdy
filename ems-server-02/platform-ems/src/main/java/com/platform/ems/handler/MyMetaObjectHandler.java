package com.platform.ems.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.common.core.domain.model.LoginUser;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 自动注入器
 * @author cwp
 * @date 2021/03/10
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LoginUser user= ApiThreadLocalUtil.get();
        String clientId="";
        String username="";
        if(user==null||user.getSysUser()==null||user.getSysUser().getClientId()==null){
            clientId="10001";
        }else {
            clientId=user.getSysUser().getClientId();
            username=user.getSysUser().getUserName();
        }
        this.strictInsertFill(metaObject, "clientId", String.class,clientId);
        this.strictInsertFill(metaObject, "createDate", Date.class,  new Date());
        this.strictInsertFill(metaObject, "creatorAccount", String.class,  username);
        this.strictInsertFill(metaObject, "dataSourceSys", String.class, null);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LoginUser user= ApiThreadLocalUtil.get();
        this.strictUpdateFill(metaObject, "updaterAccount", String.class, user!=null?user.getUsername():null);
        this.strictUpdateFill(metaObject, "updateDate",Date.class,  new Date());
    }
}
