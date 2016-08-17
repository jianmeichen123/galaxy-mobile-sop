package com.galaxyinternet.touhou.controller;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.galaxyinternet.bo.touhou.DeliveryBo;
import com.galaxyinternet.common.controller.BaseControllerImpl;
import com.galaxyinternet.exception.PlatformException;
import com.galaxyinternet.framework.cache.Cache;
import com.galaxyinternet.framework.core.model.Page;
import com.galaxyinternet.framework.core.model.PageRequest;
import com.galaxyinternet.framework.core.model.ResponseData;
import com.galaxyinternet.framework.core.model.Result;
import com.galaxyinternet.framework.core.model.Result.Status;
import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.model.touhou.Delivery;
import com.galaxyinternet.service.DeliveryService;

/**
 * 投后阶段    交割前事项
 */

@Controller
@RequestMapping("/galaxy/delivery")
public class DeliveryController extends BaseControllerImpl<Delivery, DeliveryBo> {

	final Logger logger = LoggerFactory.getLogger(DeliveryController.class);

	@Autowired
	private DeliveryService deliveryService;
	
	@Autowired
	Cache cache;

	@Override
	protected BaseService<Delivery> getBaseService() {
		return this.deliveryService;
	}
	
	/**
	 *查询 事项
	 */
	@ResponseBody
	@RequestMapping(value = "/selectdelivery/{deliverid}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<Delivery> selectDelivery(@PathVariable("deliverid") Long deliverid) {
		ResponseData<Delivery> responseBody = new ResponseData<Delivery>();
		try {
			Delivery deliver = deliveryService.selectDelivery(deliverid);
			responseBody.setEntity(deliver);
			responseBody.setResult(new Result(Status.OK, ""));
			responseBody.setUserData(null);
			responseBody.setPageList(null);
		} catch (Exception e) {
			responseBody.setResult(new Result(Status.ERROR,null, "查询失败"));
			logger.error("delDelivery 查询失败",e);
		}
		return responseBody;
	}
	
	/**
	 *查询 事项列表
	 */
	@ResponseBody
	@RequestMapping(value = "/queryprodeliverypage", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseData<DeliveryBo> queryProDeliveryPage(@RequestBody DeliveryBo query) {
		
		ResponseData<DeliveryBo> responseBody = new ResponseData<DeliveryBo>();
		
		try {
			Integer pageNum = query.getPageNum() != null ? query.getPageNum() : 0;
			Integer pageSize = query.getPageSize() != null ? query.getPageSize() : 10;
			
			Page<DeliveryBo> deliverypage =  deliveryService.queryDeliveryPageList(query, new PageRequest(pageNum,pageSize));
			responseBody.setPageList(deliverypage);
			responseBody.setResult(new Result(Status.OK, ""));
			return responseBody;
		} catch (PlatformException e) {
			responseBody.setResult(new Result(Status.ERROR, null,"列表查询失败"));
			logger.error("queryProDeliveryPage ", e);
		}
		return responseBody;
	}
	
}
