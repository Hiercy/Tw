package com.mike.my.MyApp.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mike.my.MyApp.domain.Role;
import com.mike.my.MyApp.domain.User;
import com.mike.my.MyApp.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	
	/*\
	 * userList: метод для просмотра существующих пользователей
	 * 
	 * @PreAuthorize("hasAuthority('ADMIN')") - разрешает смотреть список существующих пользователей только админу
	\*/
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping
	public String userList(Model model) {
		// Показать всех пользователей
		model.addAttribute("users", userService.findAll());
		return "userList";
	}
	
	/*\
	 * userEditForm: метод для изменения ролей пользователей. Доступен только админу
	\*/
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("{user}")
	public String userEditForm(@PathVariable User user, Model model) {
		model.addAttribute("user", user);
		model.addAttribute("roles", Role.values());
		return "userEdit";
	}
	
	/*\
	 * userSave: обработка сохранения ролей пользователя. Доступен только админу
	\*/
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping
	public String userSave(
			@RequestParam String username, 
			@RequestParam Map<String, String> form, 
			@RequestParam("userId") User user) {
		
		userService.saveUser(user, username, form);
		
		return "redirect:/user";
	}
	
	
	/*\
	 * getProfile: Метод отвечающий замену пароля
	\*/
	@GetMapping("profile")
	public String getProfile(Model model, @AuthenticationPrincipal User user)  {
		model.addAttribute("username", user.getUsername());
		model.addAttribute("email", user.getEmail());
		
		return "profile";
	}
	
	/*\
	 * getProfile: Метод отвечающий за обработку замены пароля
	\*/
	@PostMapping("profile")
	public String updateProfile(@AuthenticationPrincipal User user, @RequestParam String password, String email) {
		userService.updateProfile(user, password, email);
		
		return "redirect:/user/profile";
	}
	
	/*\
	 * subscribe: Метод отвечающий за подписку
	\*/
	@GetMapping("subscribe/{user}")
	public String subscribe(
			@AuthenticationPrincipal User currentUser,
			@PathVariable User user) {
		
		// Кто и на кого подписывается
		userService.subscribe(currentUser, user);
		
		return "redirect:/user-messages/" + user.getId();
	}
	
	/*\
	 * unsubscribe: Метод отвечающий за отписку
	\*/
	@GetMapping("unsubscribe/{user}")
	public String unsubscribe(
			@AuthenticationPrincipal User currentUser,
			@PathVariable User user) {
		userService.unsubscribe(currentUser, user);
		
		return "redirect:/user-messages/" + user.getId();
	}
	
	/*\
	 * userList: Показывает все подписки
	 * 
	 * @PathVariable нужен для обратботки url запросов
	\*/
	@GetMapping("{type}/{user}/list")
	public String userList(
			Model model,
			@PathVariable User user,
			@PathVariable String type) {
		
		model.addAttribute("userChannel", user);
		model.addAttribute("type", type);
		
		if("subscriptions".equals(type)) {
			model.addAttribute("users",user.getSubscriptions());
		} else {
			model.addAttribute("users", user.getSubscribers());
		}
		
		return "subscriptions";
	}
}
