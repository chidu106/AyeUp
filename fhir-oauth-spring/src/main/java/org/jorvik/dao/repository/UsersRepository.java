package org.jorvik.dao.repository;

import org.jorvik.dao.Users;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UsersRepository extends MongoRepository<Users, String> {
	

}
