package com.mike.my.MyApp.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

// @Entity, указывает, что это объект JPA(Java Persistence API нужно для работы с базой данных).
// Класс Message отвечает за сообщения
@Entity
public class Message {

	// @Id - Говорит о том, что это поле id
	// @GeneratedValue нужно для создания Primary Key(автоматическое присваивание индитификатора объекта)
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	// @NotBlank - не пустое поле
	// @Length - максимльная длина
	@NotBlank(message = "Please fill field")
	@Length(max = 2048, message = "Message so long (more than 2kB)")
	private String text;
	
	@Length(max = 255, message = "Message so long (more than 2kB)")
	private String tag;
	
	// @ManyToOne - эта аннотация означает, что у автора отношение от многим к одному
	// @JoinColumn
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User author;
	
	private String filename;

	public Message() {}
	
	public Message(String text, String tag, User user) {
		this.author = user;
		this.text = text;
		this.tag = tag;
	}
	
	public String getAuthorName() {
		return author != null ? author.getUsername() : "<none>";
	}
	
	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
}
