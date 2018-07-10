package com.mike.my.MyApp.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mike.my.MyApp.domain.Message;
import com.mike.my.MyApp.domain.User;
import com.mike.my.MyApp.repos.MessageRepo;
import com.mike.my.MyApp.service.UserService;

/*\
 * @GetMapping - это специализированная версия аннотации @RequestMapping, 
 * которая действует как ярлык для @RequestMapping (method = RequestMethod.GET). 
 * @GetMapping аннотированные методы обрабатывают HTTP-запросы GET, 
 * соответствующие заданному выражению URI.
 * 
 * @PostMapping - это специализированная версия аннотации @RequestMapping,
 * которая действует как ярлык для @RequestMapping (метод = RequestMethod.POST). 
 * @PostMapping аннотированные методы обрабатывают HTTP POST-запросы, 
 * согласованные с заданным выражением URI.
\*/

@Controller
public class MainController {
	@Autowired
	private MessageRepo messageRepo;

	@Autowired
	private UserService userService;

	/*\
	 * greeting: Отвечает за главную  страницу. Возвращает файл greeting
	\*/ 
	@GetMapping("/")
	public String greeting(Map<String, Object> model) {
		return "greeting";
	}
	
	/*\
	 * @RequestParam - используется для связи параметров запроса с параметрами метода(для параметров URL)
	 * Иногда мы получаем параметры в URL-адресе запроса, 
	 * в основном в запросах GET.
	 * Мы можем использовать @RequestParam для извлечения параметра URL и сопоставить его с аргументом метода.
	 * 
	 * main: Отвечает за страницу main ее и возвращает
	\*/
	@GetMapping("/main")
	public String main(@RequestParam(required = false, defaultValue="") String filter, Model model) {
		// Iterable<T> - это интерфейс , в моем случае, с сообщениями, которые написали пользователи
		// хранящимися в базе messageRepo. 
		// findAll() - Возвращает все экземпляры типа
		Iterable<Message> messages = messageRepo.findAll();

		// Если строка НЕ пустая
		if (filter != null && !filter.isEmpty()) {
			// То ищем сообщения по тегам
			messages = messageRepo.findByTag(filter);
		} else {
			// Если пустая, то показать все сообщения
			messages = messageRepo.findAll();
		}
		
		// Добавить на главную сообщения
		model.addAttribute("messages", messages);
		// Добавить Поиск на главную
		model.addAttribute("filter", filter);

		return "main";
	}

	/*\
	 * add: Отвечает за добавление пользователями сообщений с тешами и файлами
	 *  
	 *  @AuthenticationPrincipal - Без этой аннотации у пользователя не будет доступа к занесению сообщений и 
	 *  будет выкинута ошибка при добавлении нового сообщения 
	 *  (org.hibernate.TransientPropertyValueException: 
	 *  object references an unsaved transient instance - save the transient instance before flushing : com.mike.my.MyApp.domain.Message.author -> com.mike.my.MyApp.domain.User)
	 *  
	 *  @Valid - проверяет, действительны ли данные, которые вы отправляете методу, или нет
	 *  
	 *  BindingResult - возвращает результат "привязки", нужен для регистрации ошибок 
	\*/
	@PostMapping("/main")
	public String add(
			@AuthenticationPrincipal User user, 
			@Valid Message message,
			BindingResult bindingResult,
			Model model,
			@RequestParam("file") MultipartFile file) throws Exception {
		// К сообщению привязываем автора(user)
		message.setAuthor(user);

		// Если есть ошибки
		if (bindingResult.hasErrors()) {
			// Заносим это все в Map<String, String> и выводим
			Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
			
			// Вывод сообщения с ошибкой
			model.mergeAttributes(errorsMap);
			model.addAttribute("message", message);
		} else {
			// Если все хорошо
			// Сохранить файл, который хочет пользователь
			userService.saveFile(message, file);

			model.addAttribute("message", null);
			// Срхранить в базе
			messageRepo.save(message);
		}

		Iterable<Message> messages = messageRepo.findAll();

		model.addAttribute("messages", messages);

		return "main";
	}

}