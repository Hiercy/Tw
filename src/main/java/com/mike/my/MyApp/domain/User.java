package com.mike.my.MyApp.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// Класс User отвечает за пользователя
// @Table - таблица в которую будут заноситься пользователи
@Entity
@Table(name = "usr")
public class User implements UserDetails {
	private static final long serialVersionUID = 1L;

	// Id пользователя
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	// Имя
	@NotBlank(message = "Username cannot be empty")
	private String username;
	
	// Пароль
	@NotBlank(message = "Password cannot be empty")
	private String password;

	// В сети ли пользователь
	private boolean active;
	
	// Email
	@Email(message = "Email is not correct")
	@NotBlank(message = "Email cannot be empty")
	private String email;
	
	// Код активации при регистрации
	private String activationCode;

	/*
	 * @CollectionTable - определяет имя таблицы и @JoinColumn (Задает таблицу, 
	 * которая используется для сопоставления наборов базовых или встраиваемых типов. 
	 * Используется для поля или свойства коллекции.).
	 * 
	 * @Enumerated - Указывает, что постоянное свойство или поле должно сохраняться как перечислимый тип.
	 */
	@ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
	@Enumerated(EnumType.STRING)
	private Set<Role> roles;

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Message> messages;

	@ManyToMany
	@JoinTable(
				name = "user_subsciptions",
				joinColumns = { @JoinColumn(name = "channel_id") },
				inverseJoinColumns = { @JoinColumn(name = "subscriber_id") }
			)
	private Set<User> subscribers = new HashSet<>();
	
	@ManyToMany
	@JoinTable(
				name = "user_subsciptions",
				joinColumns = { @JoinColumn(name = "subscriber_id") },
				inverseJoinColumns = { @JoinColumn(name = "channel_id") }
			)
	private Set<User> subscriptions = new HashSet<>();
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return Objects.equals(id, user.id);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id);
	}
	public boolean isAdmin() {
		return roles.contains(Role.ADMIN);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return getRoles();
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return isActive();
	}

	public Set<Message> getMessages() {
		return messages;
	}

	public void setMessages(Set<Message> messages) {
		this.messages = messages;
	}

	public Set<User> getSubscribers() {
		return subscribers;
	}

	public void setSubscribers(Set<User> subscribers) {
		this.subscribers = subscribers;
	}

	public Set<User> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(Set<User> subscriptions) {
		this.subscriptions = subscriptions;
	}

	

}
