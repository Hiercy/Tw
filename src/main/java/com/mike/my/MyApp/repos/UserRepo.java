package com.mike.my.MyApp.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mike.my.MyApp.domain.User;

public interface UserRepo extends JpaRepository<User, Long>{

	 User findByUsername(String username);

	 User findByActivationCode(String code);
	
}
