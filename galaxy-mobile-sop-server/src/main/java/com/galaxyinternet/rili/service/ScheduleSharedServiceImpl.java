package com.galaxyinternet.rili.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.galaxyinternet.framework.core.dao.BaseDao;
import com.galaxyinternet.framework.core.service.impl.BaseServiceImpl;
import com.galaxyinternet.model.user.User;
import com.galaxyinternet.rili.dao.ScheduleDepartUnoDao;
import com.galaxyinternet.rili.dao.ScheduleSharedDao;
import com.galaxyinternet.rili.model.ScheduleDepartUno;
import com.galaxyinternet.rili.model.ScheduleShared;
import com.galaxyinternet.rili.util.DeptNoUsers;
import com.galaxyinternet.rili.util.UtilService;
import com.galaxyinternet.rili.util.httpClientUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@Service("com.galaxyinternet.rili.service.ScheduleSharedService")
public class ScheduleSharedServiceImpl extends BaseServiceImpl<ScheduleShared> implements ScheduleSharedService{

	@Autowired
	private ScheduleSharedDao scheduleSharedDao;
	
	@Autowired
	private ScheduleDepartUnoDao scheduleDepartUnoDao;
	
	
	@Autowired
	private UtilService utilService;
	
	
	@Override
	protected BaseDao<ScheduleShared, Long> getBaseDao() {
		return this.scheduleSharedDao;
	}

	//httpClient 需要连接的路径
	private static String url="http://fx.qa.galaxyinternet.com/authority_service" ;
	/**
	 * 选择查看成员，成员包括：自己
	 * 共享给自己的成员（默认为自己）
	 * @return {[createUid:111, createUname:"TEST"]}
	 */
	public List<ScheduleShared> querySharedUsers(Object objUser) {
		User user = (User)objUser;
		
		List<ScheduleShared> cusers = new ArrayList<ScheduleShared>();
		
		ScheduleShared my = new ScheduleShared();
		my.setCreateUid(user.getId());
		//my.setCreateUname(user.getRealName());	
		my.setToUname("我的日历");
		cusers.add(my);
		
		ScheduleShared query = new ScheduleShared();
		//query.setToUid(16L);
		query.setToUid(user.getId());
		List<ScheduleShared> qList = scheduleSharedDao.selectList(query);
		
		if(qList!=null && !qList.isEmpty()){
			List<Long> uids = new ArrayList<Long>();
			for(ScheduleShared tempU : qList){
				uids.add(tempU.getCreateUid());
			}
			
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("userIds", object2JSONString(uids));
			String content = httpClientUtils.send(url+"/user/getUserByIds", map);
			Map<Long, String> name = new HashMap<Long, String>();
			 JsonParser parser=new JsonParser(); 
		   	 JsonObject object=(JsonObject) parser.parse(content);
		   	if(object.get("success").getAsBoolean()){
			   	 JsonArray array=object.get("value").getAsJsonArray();
			   	 for(int i=0;i<array.size();i++){
		             JsonObject subObject=array.get(i).getAsJsonObject();
		             
		             int isDel  = subObject.get("isDel").getAsInt();
		             int isShow   = subObject.get("isShow").getAsInt();
		             int isOuttage   = subObject.get("isOuttage").getAsInt();
		             
		             if(isDel==0 && isShow==0 && isOuttage==0){
			             String ids = subObject.get("userId").getAsString();
			             String userName = subObject.get("userName").getAsString();
			             Long userId = Long.valueOf(ids);
			             name.put(userId, userName);
		             }else{
		            	 String ids = subObject.get("userId").getAsString();
		            	 Long userId = Long.valueOf(ids);
		            	 ScheduleShared scheduleSharedApp = new ScheduleShared();
		            	 
		            	 scheduleSharedApp.setToUid(user.getId());
		            	 scheduleSharedApp.setCreateUid(userId);
		            	 
		            	 scheduleSharedDao.delete(scheduleSharedApp);
		            	 
		             }
			   	 }
		   	}
		   	
		   	List<ScheduleShared> qListt = scheduleSharedDao.selectList(query);
		   	for(ScheduleShared tempU : qListt){
//				/tempU.setToDeptName(deptIdNmaeMap.get(tempU.getDepartmentId()));
				tempU.setToUname(name.get(tempU.getCreateUid()));
			}
						
			cusers.addAll(qListt);
		}
		return cusers;
	}

	
	/**
	 * 自己共享的 共享人列表查询
	 * 共享人列表
	 * @param  toUname 查询
	 * @return {[toUid:111, toUname:"TEST",toDeptName:"DNAME"]}
	 */
	public List<ScheduleShared> queryMySharedUsers(Object objUser,String toUname) {
		User user = (User)objUser;
		List<ScheduleShared> cusers ;
		ScheduleShared query = new ScheduleShared();
		query.setCreateUid(user.getId());
		if(toUname==null){
			List<ScheduleShared> ss = new ArrayList<ScheduleShared>();
			cusers = scheduleSharedDao.selectList(query);		
			if(cusers!=null && !cusers.isEmpty()){
				List<Long> uids = new ArrayList<Long>();
				//List<Long> depts = new ArrayList<Long>();
				for(ScheduleShared tempU : cusers){
					uids.add(tempU.getToUid());
					//depts.add(tempU.getDepartmentId());
				}
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("userIds", object2JSONString(uids));
				String content = httpClientUtils.send(url+"/user/getUserByIds", map);
				Map<Long, String> name = new HashMap<Long, String>();
				 JsonParser parser=new JsonParser(); 
			   	 JsonObject object=(JsonObject) parser.parse(content);
			   	if(object.get("success").getAsBoolean()){
				   	 JsonArray array=object.get("value").getAsJsonArray();
				   	 for(int i=0;i<array.size();i++){
			             JsonObject subObject=array.get(i).getAsJsonObject();
			             int isDel  = subObject.get("isDel").getAsInt();
			             int isShow   = subObject.get("isShow").getAsInt();
			             int isOuttage   = subObject.get("isOuttage").getAsInt();
			             
			             if(isDel==0 && isShow==0 && isOuttage==0){
			            	 String ids = subObject.get("userId").getAsString();
				             String userName = subObject.get("userName").getAsString();
				             Long userId = Long.valueOf(ids);
				             name.put(userId, userName);		
			             }else{
			            	 
			            	  String ids = subObject.get("userId").getAsString();
			            	  Long userId = Long.valueOf(ids);
			            	 ScheduleShared scheduleSharedApp = new ScheduleShared();
			            	 
			            	 scheduleSharedApp.setToUid(userId);
			            	 scheduleSharedApp.setCreateUid(user.getId());
			            	 
			            	 scheduleSharedDao.delete(scheduleSharedApp);
			             }
				   	 }
			   	 }
				//Map<Long, String> uidNmaeMap = utilService.queryUidNmaeMap(uids);
			//	Map<Long, String> deptIdNmaeMap = utilService.queryDeptIdNmaeMap(uids);
			   	//删除禁用的   重新查一遍
			   	ss = scheduleSharedDao.selectList(query);
			   	if(ss!=null && !ss.isEmpty()){
					for(ScheduleShared tempU : ss){
		//				/tempU.setToDeptName(deptIdNmaeMap.get(tempU.getDepartmentId()));
						tempU.setToUname(name.get(tempU.getToUid()));
					}
			   	}			
			}
			return ss ;
		}else{
			cusers = new ArrayList<ScheduleShared>();
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("userName", toUname);
			String content = httpClientUtils.send(url+"/user/findUserByName", map);
//			/content.indexOf("\"value":"null"/");
			 JsonParser parser=new JsonParser(); 
		   	 JsonObject object=(JsonObject) parser.parse(content);
		   	if(object.get("success").getAsBoolean()){
			   	 JsonArray array=object.get("value").getAsJsonArray();
			   	for(int i=0;i<array.size();i++){
		             JsonObject subObject=array.get(i).getAsJsonObject();
		             String ids = subObject.get("userId").getAsString();
		             String userName = subObject.get("userName").getAsString();
		             Long userId = Long.valueOf(ids);
		             query.setToUid(userId);
		             ScheduleShared scheduleShared = scheduleSharedDao.selectOne(query);
		            
		             if(scheduleShared!=null){
		            	 scheduleShared.setToUname(userName);
		            	 cusers.add(scheduleShared);
		             }	             
			   		 
			   	 }
		   	 }
		    return cusers;

		}
		
	}	

	/**
	 * 添加共享人
	 * @param  {deptNoUsers : [{deptId:111,userCount:10,userIds:[222,333]},{deptId:111, userCount:10,userIds:[222,333]}]}
	 */
	@Transactional
	public void saveSharedUsers(Object objUser, ScheduleShared comShareQ, List<DeptNoUsers> deptNoUsers){
		User user = (User)objUser;
		
		//删除所有共享人
		scheduleSharedDao.delete(comShareQ);
		
		/*//删除所有部门共享人数量
		scheduleDepartUnoDao.delete(dun);*/
		
		List<ScheduleShared> toSaveShaerds = new ArrayList<ScheduleShared>();
		//List<ScheduleDepartUno> toSaveDeptUnos = new ArrayList<ScheduleDepartUno>();
		
		if(deptNoUsers!=null && !deptNoUsers.isEmpty()){
			
			for(DeptNoUsers temp : deptNoUsers){
				
				List<Long> userIds = temp.getUserIds();
				if(userIds == null || userIds.isEmpty()) continue;
				
				//Map<Long, String> uidNmaeMap = utilService.queryUidNmaeMap(userIds);
				
				
				// 部门-相关人数
			/*	dun = new ScheduleDepartUno();
				dun.setRemarkType((byte) 1);
				dun.setDepartmentId(temp.getDeptId());
				dun.setUserCount(temp.getUserCount());
				dun.setCreatedId(user.getId());
				toSaveDeptUnos.add(dun);*/
				
				for(Long uidTemp : userIds){
					// 日程共享
					comShareQ = new ScheduleShared();
					
					comShareQ.setToUid(uidTemp);
					//comShareQ.setToUname(uidNmaeMap.get(uidTemp));
					comShareQ.setDepartmentId(temp.getDeptId());
					comShareQ.setCreateUid(user.getId());
					//comShareQ.setCreateUname(user.getRealName());
					
					toSaveShaerds.add(comShareQ);
				}
			}
			
			if(!toSaveShaerds.isEmpty()) scheduleSharedDao.insertInBatch(toSaveShaerds);
		//	if(!toSaveDeptUnos.isEmpty()) scheduleDepartUnoDao.insertInBatch(toSaveDeptUnos);
		}
	}


	
	/**
	 * 删除共享人
	 */
	@Transactional
	public void delSharedUser(Long id, ScheduleDepartUno queryDun) {
		
		//scheduleDepartUnoDao.changeUno(queryDun);

		//删除共享人
		scheduleSharedDao.deleteById(id);
	}


	@Override
	public List<Long> selectByUserId(ScheduleShared query) {
		// TODO Auto-generated method stub
		return scheduleSharedDao.selectByUserId(query);
	}

	
	private String object2JSONString(Object object){
		String jsonString = null;
		try{
			jsonString = new ObjectMapper().writeValueAsString(object);
		}catch(Exception e){
			e.printStackTrace();
		}
		return jsonString;
	}
	
}
