package org.bobo.service;

import org.bobo.entity.Users;

public interface IUsersService {
	public Users searchByName(String name)throws Exception;
	public Users login(String name,String password)throws Exception;

}
