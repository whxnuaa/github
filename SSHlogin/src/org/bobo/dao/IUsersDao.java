package org.bobo.dao;

import org.bobo.entity.Users;

public interface IUsersDao {
	public Users findByName(String name) throws Exception;
	public Users findByNameAndPassword(String name,String password)throws Exception;
}
