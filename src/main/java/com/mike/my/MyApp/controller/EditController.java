package com.mike.my.MyApp.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mike.my.MyApp.domain.Message;
import com.mike.my.MyApp.domain.User;
import com.mike.my.MyApp.repos.MessageRepo;
import com.mike.my.MyApp.service.UserService;

@Controller
public class EditController {

	@Autowired
	private MessageRepo messageRepo;

	@Autowired
	private UserService userService;

 	/*\
 	 * userMessages: Показывает посты другого пользователя
 	 * 
 	 * 
 	\*/
	@GetMapping("/user-messages/{user}")
	public String userMessages(
			@AuthenticationPrincipal User currentUser, 
			@PathVariable User user, 
			Model model,
			@RequestParam(required = false) Message message) {
		Set<Message> messages = user.getMessages();


		model.addAttribute("userChannel", user);
		model.addAttribute("subscriptionsCount", user.getSubscriptions().size());
		model.addAttribute("subscribersCount", user.getSubscribers().size());
		model.addAttribute("isSubscriber", user.getSubscribers().contains(currentUser));
		model.addAttribute("messages", messages);
		model.addAttribute("message", message);
		model.addAttribute("isCurrentUser", currentUser.equals(user));

		return "userMessages";
	}

 	/*\
 	 * updateMessage: Обрабатывает update поста
 	\*/
	@PostMapping("/user-messages/{user}")
	public String updateMessage(
			@AuthenticationPrincipal User currentUser, 
			@PathVariable Long  user,
			@RequestParam("id") Message message,
			@RequestParam("text") String text,
			@RequestParam("tag") String tag,
			@RequestParam("file") MultipartFile file) throws Exception {
		if(message.getAuthor().equals(currentUser)) {
			if(!StringUtils.isEmpty(text)) {
				message.setText(text);
			}

			if(!StringUtils.isEmpty(tag)) {
				message.setTag(tag);
			}
			userService.saveFile(message, file);
			messageRepo.save(message);
		}

		return "redirect:/user-messages/" + user;
	}




}
