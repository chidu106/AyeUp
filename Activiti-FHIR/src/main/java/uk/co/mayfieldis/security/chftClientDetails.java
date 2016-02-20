package uk.co.mayfieldis.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.provider.ClientDetails;

public class chftClientDetails implements ClientDetails {

	static final long serialVersionUID = 1;
	
	private User clientUser;
	
	private String resourceIds;
	
	private String scopes;
	
	private String grantTypes;
	
	public chftClientDetails(User user, String resourceIds, String scopes, String grantTypes)
	{
		this.setClientUser(user);
		this.setResourceIds(resourceIds);
		this.setScopes(scopes);
		this.setGrantTypes(grantTypes);
	}
	
	public void setGrantTypes(String grantTypes)
	{
		this.grantTypes=grantTypes;
	}
	
	public void setClientUser(User user)
	{
		this.clientUser = user;
	}
	
	public void setResourceIds(String resourceIds)
	{
		this.resourceIds = resourceIds;
	}
	
	public void setScopes(String scopes)
	{
		this.scopes = scopes;
	}
	
	@Override
	public Integer getAccessTokenValiditySeconds() {
		
		return 5184000;
	}

	@Override
	public Map<String, Object> getAdditionalInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {

		return this.clientUser.getAuthorities();
	}

	@Override
	public Set<String> getAuthorizedGrantTypes() {
		return new HashSet<String>(Arrays.asList(this.grantTypes.split(",")));
		
	}

	@Override
	public String getClientId() {
		return this.clientUser.getUsername();
	}

	@Override
	public String getClientSecret() {
		// 
		return "";
	}

	@Override
	public Integer getRefreshTokenValiditySeconds() {
		return 5184000;
	}

	@Override
	public Set<String> getRegisteredRedirectUri() {
		return null; 
	
	}

	@Override
	public Set<String> getResourceIds() {
		return new HashSet<String>(Arrays.asList(this.resourceIds.split(",")));
	}

	@Override
	public Set<String> getScope() {
		return new HashSet<String>(Arrays.asList(this.scopes.split(",")));
	}

	@Override
	public boolean isAutoApprove(String arg0) {
		// 
		return false;
	}

	@Override
	public boolean isScoped() {

		return true;
	}

	@Override
	public boolean isSecretRequired() {
		return false;
	}

}
