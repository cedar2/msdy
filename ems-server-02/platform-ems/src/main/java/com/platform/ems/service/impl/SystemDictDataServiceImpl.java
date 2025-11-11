package com.platform.ems.service.impl;

import com.platform.common.core.domain.model.DictData;
import com.platform.common.core.redis.RedisCache;
import com.platform.common.redis.thread.ApiThreadLocalUtil;
import com.platform.ems.mapper.BasCompanyMapper;
import com.platform.common.core.domain.model.LoginUser;
import com.platform.ems.service.ISystemDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@SuppressWarnings("all")
public class SystemDictDataServiceImpl implements ISystemDictDataService {

    public static final String SYS_DICT_LIST_KEY = "dict_data:";

    @Autowired
    private BasCompanyMapper basCompanyMapper;
    @Autowired
    private RedisCache redisService;
/*
    @Override
    public Map<String,Object> getDictDataList(){
        Map<String,Object> dataMap;
        LoginUser user= ApiThreadLocalUtil.get();
        if(user==null){
            throw new CustomException("暂未登录或token已过期");
        }
        String key=SYS_DICT_LIST_KEY;
        if(user.getUserid() != null && 1L == user.getUserid()){
            key+="admin";
        }else {
            key+=user.getSysUser().getClientId();
        }
        dataMap =redisService.getCacheObject(key);
        if(dataMap!=null&&dataMap.size()>0){
            return dataMap;
        }
        dataMap=new HashMap<>();
        List<DictData> dataList=basCompanyMapper.getDictDataList();
        if(user.getUserid() != null && 1L == user.getUserid()){
            for(DictData data:dataList){
                List<DictData> valueList=data.getDataList();
                String keyName=data.getDictType();
                dataMap.put(keyName,valueList);
            }
        }else {
            List<DictData> privateList=basCompanyMapper.getDictDataPrtvateList(user.getSysUser().getClientId());
            if(privateList!=null&&privateList.size()>0){
                dataList.removeAll(privateList);
                dataList.addAll(privateList);
            }
            for(DictData data:dataList){
                List<DictData> valueList=data.getDataList();
                String keyName=data.getDictType();
                dataMap.put(keyName,valueList);
            }
        }
        //缓存一小时
        redisService.setCacheObject(key, dataMap, 1L, TimeUnit.HOURS);
        return dataMap;
    }*/

    @Override
    @Cacheable(value ="dict_data#3600"  ,keyGenerator = "myKeyGenerator")
    public Map<String,Object> getDictDataList(){
        LoginUser user= ApiThreadLocalUtil.get();
        Map<String,Object> dataMap=new HashMap<>();
        List<DictData> dataList=basCompanyMapper.getDictDataList();
        if(user.getUserid() != null && 1L == user.getUserid()){
            for(DictData data:dataList){
                List<DictData> valueList=data.getDataList();
                String keyName=data.getDictType();
                dataMap.put(keyName,valueList);
            }
        }else {
            List<DictData> privateList=basCompanyMapper.getDictDataPrtvateList(user.getSysUser().getClientId());
            if(privateList!=null&&privateList.size()>0){
                dataList.removeAll(privateList);
                dataList.addAll(privateList);
            }
            for(DictData data:dataList){
                List<DictData> valueList=data.getDataList();
                String keyName=data.getDictType();
                dataMap.put(keyName,valueList);
            }
        }
        return dataMap;
    }


    @Override
    public List<DictData> selectDictData(String dictType){
        LoginUser user= ApiThreadLocalUtil.get();
        String key="dictData:";
        if(user.getUserid() != null && 1L == user.getUserid()){
            key+="admin";
        }else {
            key+=user.getSysUser().getClientId();
        }
        key+=":"+dictType;
        List<DictData> dataList =redisService.getCacheObject(key);
        if(dataList!=null&&dataList.size()>0){
            return dataList;
        }
        dataList =basCompanyMapper.selectDictData(dictType);
        //缓存一小时
        redisService.setCacheObject(key, dataList, 1L, TimeUnit.HOURS);
        return dataList;
    }
    @Override
    public boolean deleteDictData(String dictType){
        LoginUser user= ApiThreadLocalUtil.get();
        String key="dictData:";
        if(user.getUserid() != null && 1L == user.getUserid()){
            key+="admin";
        }else {
            key+=user.getSysUser().getClientId();
        }
        key+=":"+dictType;
       return redisService.deleteObject(key);
    }
}
