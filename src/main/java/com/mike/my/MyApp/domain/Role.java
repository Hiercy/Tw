package com.mike.my.MyApp.domain;

import org.springframework.security.core.GrantedAuthority;

// Перечисление ролей
// GrantedAuthority - интерфейс, дающий полномочия объекту аутентификации
public enum Role implements GrantedAuthority{
	USER, ADMIN;
	
	
	@Override
	public String getAuthority() {
		return name();
	}
}
