package com.mike.my.MyApp.repos;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.mike.my.MyApp.domain.Message;

public interface MessageRepo extends CrudRepository<Message, Long>{

	List<Message> findByTag(String tag);
	
}
