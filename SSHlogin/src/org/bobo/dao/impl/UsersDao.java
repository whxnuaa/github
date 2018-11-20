package org.bobo.dao.impl;

import java.util.List;

import org.bobo.dao.IUsersDao;
import org.bobo.entity.Users;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class UsersDao implements IUsersDao {
	private HibernateTemplate hibernateTemplate;
	@Override
	public Users findByName(String name) throws Exception {
		String hql="from Users where name='"+name+"'";
		List<Users> users=hibernateTemplate.find(hql);
		if(users.size()!=0){
			return users.get(0);
		}
		return null;
	}
	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}
	@Override
	public Users findByNameAndPassword(String name, String password) throws Exception {
		String hql="from Users where name=? and password=?";
		List<Users> users=hibernateTemplate.find(hql,name,password);
		if(users.size()!=0){
			return users.get(0);
		}
		return null;
	}
	

}
