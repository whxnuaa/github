package org.bobo.service.impl;

import org.bobo.dao.IUsersDao;
import org.bobo.entity.Users;
import org.bobo.service.IUsersService;

public class UsersService implements IUsersService {
	private IUsersDao usersDao;
	@Override
	public Users searchByName(String name) throws Exception {
		return usersDao.findByName(name);
	}
	public IUsersDao getUsersDao() {
		return usersDao;
	}
	public void setUsersDao(IUsersDao usersDao) {
		this.usersDao = usersDao;
	}
	@Override
	public Users login(String name, String password) throws Exception {
		
		return usersDao.findByNameAndPassword(name, password);
	}
	

}
