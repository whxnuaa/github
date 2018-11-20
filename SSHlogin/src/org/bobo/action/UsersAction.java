package org.bobo.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.bobo.entity.Users;
import org.bobo.service.IUsersService;

import com.opensymphony.xwork2.ActionSupport;

public class UsersAction extends ActionSupport implements ServletRequestAware{
	private Users user;
	private HttpServletRequest request;
	private HttpSession session;
	private ServletContext application;
	private IUsersService usersService;
	
	public String login()throws Exception{
		System.out.println("name:"+user.getName()+"\tpassword:"+user.getPassword());
		Users us=usersService.login(user.getName(), user.getPassword());
		if(us==null){
			return INPUT;
		}
		
		return SUCCESS;
	}
 
	
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
		session=request.getSession();
		application=request.getServletContext();
	}
	
	
	public Users getUser() {
		return user;
	}

	
	public void setUser(Users user) {
		this.user = user;
	}



	public IUsersService getUsersService() {
		return usersService;
	}



	public void setUsersService(IUsersService usersService) {
		this.usersService = usersService;
	}

	
	
	
}
