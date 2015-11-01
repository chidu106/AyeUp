package org.jorvik.security;

import org.jorvik.dao.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


//https://codesilo.wordpress.com/2012/07/08/mongodb-spring-data-and-spring-security-with-custom-userdetailsservice/


public class MongoDBAuthenticationProvider implements UserDetailsService {

	//extends AbstractUserDetailsAuthenticationProvider
	
	private MongoTemplate mongoTemplate;
	
	private static final Logger log = LoggerFactory.getLogger(MongoDBAuthenticationProvider.class);
	
	public UserDetails loadUserByUsername(String username)
			 throws UsernameNotFoundException {
			 log.info("In UserDetails class Username="+username);
			 User user = getUserDetail(username);
			 log.info("Username="+username);
			 log.info("User="+user.toString());
			 //System.out.println(username);
			 //User userDetail = new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),true,true,true,true,getAuthorities(user. getRole()));
			 return user;
			 }
	
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		// TODO Auto-generated method stub

	}
	
	@Autowired
	 public void setMongoTemplate(MongoTemplate mongoTemplate) {
	 this.mongoTemplate = mongoTemplate;
	 }
	
	public User getUserDetail(String username){
		 
		 log.info("In getUserDetails class Username="+username);
		 MongoOperations mongo = (MongoOperations)mongoTemplate;
		 log.info("In getUserDetails-1 class Username="+username);
		 Users user = mongo.findOne(
		 new Query(Criteria.where("username").is(username)),
		 Users.class);
		 User sUser = null;
		 if(user == null){
	            throw new UsernameNotFoundException(username);
	        }
		 else
		 {
			 log.info("In getUserDetails-2 class Username="+username);
			 sUser = new User(user.getUserName(), user.getPassword(), user.getRoles());
			 log.info("User="+user.toString());
		 }
		 return sUser;
		 }
	
	protected UserDetails retrieveUser(String userName,
			UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		UserDetails loadedUser;

        try {
        	log.info("In retrieveUser class Username="+userName);
        	MongoOperations mongo = (MongoOperations)mongoTemplate;
            Users client = mongo.findOne(Query.query(Criteria.where("username").is(userName)), Users.class);
            
            loadedUser = new User(client.getUserName(), client.getPassword(), client.getRoles());
        } catch (Exception repositoryProblem) {
            throw new InternalAuthenticationServiceException(repositoryProblem.getMessage(), repositoryProblem);
        }

        return loadedUser;
	}

	/*
	Users yorkie = new Users();
	yorkie.setUserName("jim");
	yorkie.setPassword("r0binh00d");
	List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
	SimpleGrantedAuthority role = new SimpleGrantedAuthority("ADMIN");
	roles.add(role);
	role = new SimpleGrantedAuthority("USER");
	roles.add(role);
	yorkie.setItems(roles);
	
	mongo.insert(yorkie,"usersJorvik"); 
	*/
	
	/* Sample JSON file for collection usersJorvik
	 * 
	 * {
    "_id" : ObjectId("5636181e02d30e09340f79fa"),
    "_class" : "org.jorvik.dao.Users",
    "username" : "jim",
    "password" : "password",
    "roles" : [ 
        {
            "role" : "ROLE_USER",
            "_class" : "org.springframework.security.core.authority.SimpleGrantedAuthority"
        }, 
        {
            "role" : "ROLE_ADMIN",
            "_class" : "org.springframework.security.core.authority.SimpleGrantedAuthority"
        }
    ]
}
	 */
	
}
