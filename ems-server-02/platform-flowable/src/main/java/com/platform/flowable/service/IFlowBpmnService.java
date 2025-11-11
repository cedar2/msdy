package com.platform.flowable.service;

import java.io.InputStream;

public interface IFlowBpmnService {

	public InputStream readImage(String businesskey,String definitionKey);
}
