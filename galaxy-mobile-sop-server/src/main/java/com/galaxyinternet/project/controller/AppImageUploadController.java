package com.galaxyinternet.project.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.galaxyinternet.common.controller.BaseControllerImpl;
import com.galaxyinternet.framework.core.model.ResponseData;
import com.galaxyinternet.framework.core.model.Result;
import com.galaxyinternet.framework.core.model.Result.Status;
import com.galaxyinternet.framework.core.service.BaseService;
import com.galaxyinternet.model.project.PersonPool;
import com.galaxyinternet.model.project.Project;
import com.galaxyinternet.model.sopfile.AppSopFile;
import com.galaxyinternet.model.sopfile.SopFile;
import com.galaxyinternet.service.PersonPoolService;
import com.galaxyinternet.service.ProjectService;
import com.galaxyinternet.service.SopFileService;
import com.galaxyinternet.service.UserService;
/**
 * APP端文件上传管理
 * @author 
 * @ClassName  : AppImageUploadController  
 * @Version  版本   
 * @ModifiedBy
 * @Copyright  Galaxyinternet  
 * @date  2016年6月8日 上午10:28:40
 */
@Controller
@RequestMapping("/galaxy/mobile")
public class AppImageUploadController extends BaseControllerImpl<SopFile, AppSopFile> {
	
	final Logger logger = LoggerFactory.getLogger(AppImageUploadController.class);		
	
	@Autowired
	private SopFileService sopFileService;
	
	@Autowired
	private ProjectService proJectService;
	
	@Autowired
	private PersonPoolService personPoolService;
/*	@Autowired
	private UserRoleService userRoleService;*/
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ProjectService projectService;

	private String tempfilePath;

	public String getTempfilePath() {
		return tempfilePath;
	}
	@Value("${galaxy.project.sop.endpoint}")
	public void setTempfilePath(String tempfilePath) {
		this.tempfilePath = tempfilePath;
	}
	
	@Override
	protected BaseService<SopFile> getBaseService() {
		return this.sopFileService;
	}
	
	/**
	 * 上传录音文件-系统自动上传
	 * @param file
	 * @param request
	 * @param model
	 * @return   10.9.11.161
	 */	
	 @ResponseBody
	 @RequestMapping(value = "/uploadFile/{pid}", produces = MediaType.APPLICATION_JSON_VALUE) 	 
	 public ResponseData<PersonPool> uploadFile(@PathVariable("pid") String pid, @RequestParam(value = "fileList") MultipartFile fileList,HttpServletRequest request) {
		 ResponseData<PersonPool> responseBody = new ResponseData<PersonPool>();
				 	ServletContext s1 = request.getServletContext();
				 //	String path = s1.getRealPath("/")+"/image";				 	
				 	String strDirPath = request.getSession().getServletContext().getRealPath("/")+"/image";
			 		File uploadDirectory = new File(strDirPath); 
			 		if(!uploadDirectory.exists()){
			 			uploadDirectory.mkdirs();
			 		}			 	

		 			if(fileList!=null && !fileList.isEmpty()){
		 				String fileName = fileList.getOriginalFilename();
		 				String contentType  = fileList.getContentType();
		 				Long size = fileList.getSize();
				        File targetFile = new File(strDirPath, fileName); 				        
				        if(!targetFile.exists()){  
				            targetFile.mkdirs();  
				        }else{
				        	targetFile.delete();
				        }
				        
				        try {  
				        	fileList.transferTo(targetFile); 	
				        	PersonPool  personPool = personPoolService.queryById(Long.parseLong(pid));
					        personPool.setFileName(fileName);
					        personPool.setFileSuffix(contentType);
					        personPool.setFilePath(tempfilePath+"image/"+fileName);
					        personPool.setFileLength(size.toString());
					        int num =  personPoolService.updateById(personPool);
				            //properties.replace("app."+appName+".version", appVersion);
				        } catch (Exception e) {  
				            e.printStackTrace(); 
				            responseBody.setResult(new Result(Status.ERROR, null, "上传失败"));			 	
				 			return responseBody;		 	
				        }  
				        //model.addAttribute("fileUrl", path + File.separator + fileName);  
		 			}		 			
		 			responseBody.setResult(new Result(Status.OK, null, "上传成功"));			 	
		 			return responseBody;		 	
	 }
	 
	 @RequestMapping(value = "/showFile/{pid}", method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE) 
	 public void showFile(@PathVariable("pid") String pid,HttpServletRequest request,HttpServletResponse response)
		{		 	 
		 		if(pid!=null){
		 			PersonPool pl = personPoolService.queryById(Long.parseLong(pid));
		 		
		 	//	String path = tempfilePath + "/12333333333333333.jpg";
				if(pl!=null && pl.getFilePath()!=null){
					/*response.setContentType("image/jpeg");*/
					response.setContentType(pl.getFileSuffix());
					response.setHeader("If-Modified-Since", "0");
					response.setHeader("Cache-Control", "no-cache");
					FileInputStream fis = null;
					OutputStream os = null;
					try
					{
						if(pl.getFilePath()!=null){
							fis = new FileInputStream(pl.getFilePath());
							os = response.getOutputStream();
							int count = 0;
							byte[] buffer = new byte[1024 * 1024];
							while ((count = fis.read(buffer)) != -1)
							{
								os.write(buffer, 0, count);
							}
							os.flush();
						}
					}
					catch (Exception e)
					{
						logger.error("显示文件失败",e);
					}
					finally
					{
						try
						{
							if (os != null) os.close();
							if (fis != null) fis.close();
						}
						catch (IOException e)
						{
							logger.error("显示文件失败",e);
						}
							
					}
				}
			}
				
		
		}
	 
}
