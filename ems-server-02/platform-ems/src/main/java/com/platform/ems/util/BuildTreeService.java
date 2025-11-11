package com.platform.ems.util;

import com.platform.common.utils.bean.BeanUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 树形结构处理工具
 * @param <T>
 * @author c
 */
public class BuildTreeService<T> {

    /**
     * 节点字段名称
     */
    private String nodeFieldName;
    /**
     * 父节点字段名称
     */
    private String parentNodeFieldName;
    /**
     * 子集字段名称
     */
    private String childrenFieldName;

    public BuildTreeService(String nodeFieldName,String parentNodeFieldName,String childrenFieldName) {
        this.childrenFieldName=childrenFieldName;
        this.nodeFieldName=nodeFieldName;
        this.parentNodeFieldName=parentNodeFieldName;
    }

    public List<T> buildTreeSelect(List<T> list) {
        List<T> Trees = buildTree(list);
        return Trees;
    }

    public List<T> buildTree(List<T> list) {
        List<T> returnList = new ArrayList<>();
        List<Long> tempList = new ArrayList<>();
        for (T dept : list) {
            Object value= BeanUtils.getValue(dept, nodeFieldName);
            tempList.add((Long)value);
        }
        for (Iterator<T> iterator = list.iterator(); iterator.hasNext();) {
            T infor = iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            Object value= BeanUtils.getValue(infor, parentNodeFieldName);
            Long parentId=(Long)value;
            if (!tempList.contains(parentId)) {
                recursionFn(list, infor,nodeFieldName,parentNodeFieldName,childrenFieldName);
                returnList.add(infor);
            }
        }
        if (returnList.isEmpty()) {
            returnList = list;
        }
        return returnList;
    }



    /**
     * 递归列表
     *
     * @param list
     * @param t
     */
    private void recursionFn(List<T> list, T t,String nodeFieldName,String parentFieldName,String childrenFieldName) {
        // 得到子节点列表
        List<T> childList = getChildList(list, t,nodeFieldName,parentFieldName);
        BeanUtils.setValue(t, childrenFieldName, childList);
        for (T tChild : childList) {
            if (hasChild(list, tChild,nodeFieldName,parentFieldName)) {
                recursionFn(list, tChild,nodeFieldName,parentFieldName,childrenFieldName);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<T> getChildList(List<T> list, T t,String nodeFieldName,String parentFieldName) {
        List<T> tlist = new ArrayList<>();
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            T n =  it.next();
            Long parentNodeId= (Long)BeanUtils.getValue(n, parentFieldName);
            if(parentNodeId!=null){
                Long nodeId= (Long)BeanUtils.getValue(t, nodeFieldName);
                if (nodeId.longValue() == parentNodeId.longValue()) {
                    tlist.add(n);
                }
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<T> list, T t,String nodeFieldName,String parentFieldName) {
        return getChildList(list, t,nodeFieldName,parentFieldName).size() > 0 ? true : false;
    }
}
