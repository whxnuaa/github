package org.bobo.entity;

import java.io.Serializable;

public class Users implements Serializable{
	private Integer id;
	private String name;
	private String password;
	public Users() {
		super();
	}
	public Users(String name, String password) {
		super();
		this.name = name;
		this.password = password;
	}
	@Override
	public String toString() {
		return "User [name=" + name + ", password=" + password + "]";
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

}
