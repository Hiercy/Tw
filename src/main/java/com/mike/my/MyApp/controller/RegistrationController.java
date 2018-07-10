package com.mike.my.MyApp.controller;

import java.util.Collections;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.mike.my.MyApp.domain.User;
import com.mike.my.MyApp.domain.dto.CaptchaResponseDto;
import com.mike.my.MyApp.service.UserService;

@Controller
public class RegistrationController {

		private final static String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";
	
	 	@Autowired
	    private UserService userService;

	 	// Для капчи
	 	@Value("${recaptcha.secret}")
	 	private String secret;
	 	
	 	@Autowired
	    private RestTemplate restTemplate;
	 	
	 	/*\
	 	 * registration: Показывает страницу регистрации
	 	\*/
	    @GetMapping("/registration")
	    public String registration() {
	        return "registration";
	    }

	 	/*\
	 	 * addUser: Метод добавления нового пользователя
	 	 * 
	 	\*/
	    @PostMapping("/registration")
	    public String addUser(
	    		@RequestParam("password2") String passwordConfirm,		
	    		@RequestParam("g-recaptcha-response") String captchaResponse,	
	    		@Valid User user,
	    		BindingResult bindingResult, 
	    		Model model) {
	    	
	    	// Это нужно для капчи
	    	String url = String.format(CAPTCHA_URL, secret, captchaResponse);
	    	
	    	
	    	CaptchaResponseDto response = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDto.class);
	    	
	    	// Если есть ошибки
	    	if(!response.isSuccess()) {
	    		// Попросить пользователя ткнуть на капчу
	    		model.addAttribute("errorCaptcha", "Fill captcha");
	    	}
	    	
	    	// StringUtils.isEmpty(passwordConfirm) - Проверяет, является ли строка пароля пустой
	    	boolean isConfirmEmpty = StringUtils.isEmpty(passwordConfirm);
	    	
	    	// Если есть ошибка, значит поле пустое
	    	if(isConfirmEmpty) {
	    		model.addAttribute("password2Error", "Password confirmation cannot be empty");
	    	}
	    	
	    	// Если пароль есть, но не совпадает со вторым паролем, попросить заполнить правильно
	    	if(user.getPassword() != null && !user.getPassword().equals(passwordConfirm)) {
	    		model.addAttribute("passwordError", "Password are different");
	    	}
	    	
	    	// Если пароль пустой, есть ошибки
	    	if(isConfirmEmpty || bindingResult.hasErrors() || !response.isSuccess()) {
	    		Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
	    		
	    		// Вывести ошибку
	    		model.mergeAttributes(errors);
	    		
	    		return "registration";
	    	}
	    	
	    	// Неправильное имя пользователя
	        if (!userService.addUser(user)) {
	            model.addAttribute("usernameError", "User exists!");
	            return "registration";
	        }

	       
	        return "redirect:/login";
	    }
	    
	 	/*\
	 	 * activate: Обрабатывает код активации
	 	 * 
	 	 * @PathVariable нужен для обратботки url запросов
	 	\*/
	    @GetMapping("/activate/{code}")
	    public String activate(Model model, @PathVariable String code) {
	    	boolean isActivated = userService.activateUser(code);
	    	
	    	if(isActivated) {
	    		model.addAttribute("messageType", "success");
	    		model.addAttribute("message", "User successfully activated");
	    	} else {
	    		model.addAttribute("messageType", "danger");
	    		model.addAttribute("message", "Activation code is not found");
	    	}
	    	
	    	return "login";
	    }
	
}
