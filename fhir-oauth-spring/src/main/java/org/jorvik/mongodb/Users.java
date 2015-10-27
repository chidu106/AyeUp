package org.jorvik.mongodb;


import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

@Document(collection = "users")
public class Users {

	@Id
	private String id;
	
	private String username;
	
	private String password;
	
	private List<GrantedAuthority> roles;
	
	public String getUserName() {
		return username;
	}
	
	public void setUserName (String userName)
	{
		this.username = userName;
	}
	public void  setPassword(String Password)
	{
		this.password = Password;
	}
	
	public String getPassword() {
		return password;	
	}
	
	public String getId() {
		return id;
	}
	
	public List<GrantedAuthority> getRoles() {
		return roles;
	}
	
	public void setItems(List<GrantedAuthority> roles){
		this.roles = roles;
	}
}
