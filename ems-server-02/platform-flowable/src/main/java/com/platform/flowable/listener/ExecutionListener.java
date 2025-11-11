package com.platform.flowable.listener;

import org.flowable.engine.delegate.DelegateExecution;

import java.io.Serializable;

/**
 * 全局监听
 * @author qhq
 */
public interface  ExecutionListener extends Serializable {
	String EVENTNAME_START = "start";
	String EVENTNAME_END = "end";
	String EVENTNAME_TAKE = "take";

	void notify(DelegateExecution execution) throws Exception;
}
