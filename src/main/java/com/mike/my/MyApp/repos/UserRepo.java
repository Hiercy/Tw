package com.mike.my.MyApp.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mike.my.MyApp.domain.User;

// JpaRepository будет иметь все функции CrudRepository и PagingAndSortingRepository
public interface UserRepo extends JpaRepository<User, Long>{

	 User findByUsername(String username);

	 User findByActivationCode(String code);
	
}
