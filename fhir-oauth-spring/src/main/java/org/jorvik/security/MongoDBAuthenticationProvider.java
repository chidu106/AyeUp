package org.jorvik.security;

import org.jorvik.dao.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;

public class MongoDBAuthenticationProvider extends
		AbstractUserDetailsAuthenticationProvider {

	@Autowired
	MongoOperations mongo;
	
	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		// TODO Auto-generated method stub

	}

	@Override
	protected UserDetails retrieveUser(String userName,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		UserDetails loadedUser;

        try {
            Users client = mongo.findOne(Query.query(Criteria.where("username").is(userName)), Users.class);
            
            loadedUser = new User(client.getUserName(), client.getPassword(), client.getRoles());
        } catch (Exception repositoryProblem) {
            throw new InternalAuthenticationServiceException(repositoryProblem.getMessage(), repositoryProblem);
        }

        return loadedUser;
	}

}
