package org.smart.admin.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.smart.admin.common.util.SecurityUtils;
import org.smart.admin.model.entity.Commune;
import org.smart.admin.model.entity.Notification;
import org.smart.admin.model.entity.User;
import org.smart.admin.model.entity.UserCommuneComment;
import org.smart.admin.repository.DepartmentRepository;
import org.smart.admin.repository.NotificationRepository;
import org.smart.admin.repository.UserCommuneCommentRepository;
import org.smart.admin.service.CommuneService;
import org.smart.admin.service.MailService;
import org.smart.admin.service.NotificationService;
import org.smart.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/steps")
public class StepsController {

	@Autowired
	private NotificationRepository notificationRepo;

	@Autowired
	public MailService mailService;

	@Autowired
	private UserService userService;

	@Autowired
	private CommuneService communeService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private UserCommuneCommentRepository userCommentRepository;

	@GetMapping("/{departmentCode}/{inseeCommune}")
	public String steps(@PathVariable String departmentCode, @PathVariable String inseeCommune, Model model) {
		model.addAttribute("commune", DepartmentRepository.getTownship(departmentCode, inseeCommune));
		model.addAttribute("comments", userCommentRepository.findByInseeCommune(inseeCommune));
		model.addAttribute("communeInfo", communeService.findByInsee(departmentCode));
		return "steps";
	}

	@PostMapping(path = "/{departmentCode}/{inseeCommune}", name = "changeNotification")
	public String changeNotification(@PathVariable String departmentCode, @PathVariable String inseeCommune,
			Model model) {
		Commune commune = communeService.findByInsee(inseeCommune);
		model.addAttribute("commune", DepartmentRepository.getTownship(departmentCode, inseeCommune));
		model.addAttribute("comments", userCommentRepository.findByInseeCommune(inseeCommune));
		commune.setNbEdit(commune.getNbEdit() + 1);
		communeService.edit(commune);
		return "steps";
	}

	@ResponseBody
	@PostMapping("/{inseeCommune}/comment")
	public void comment(@PathVariable String inseeCommune, @RequestParam("comment") String comment, Model model) {
		UserCommuneComment userComment = new UserCommuneComment();
		userComment.setContent(comment);
		userComment.setInseeCommune(inseeCommune);
		userComment.setUser(userService.findByUsername(SecurityUtils.findLoggedInUsername()));
		userCommentRepository.save(userComment);

		List<UserCommuneComment> userCommuneComments = userCommentRepository.findByInseeCommune(inseeCommune);
		Set<User> users = userCommuneComments.stream().map(UserCommuneComment::getUser)
				.filter(user -> !user.getUsername().equals(SecurityUtils.findLoggedInUsername()))
				.collect(Collectors.toSet());

		for (User user : users) {
			Notification notification = new Notification();
			notification.setUser(user);
			String text = "L'utilisateur " + SecurityUtils.findLoggedInUsername()
					+ " a ajouté un commentaire à votre préfecture ";
			notification.setText(text);
			notificationService.save(notification);
		}

	}

	@ModelAttribute
	public void addAttributes(Model model) {
		model.addAttribute("notifs", notificationRepo.findByUser_Username(SecurityUtils.findLoggedInUsername()));
	}

}
