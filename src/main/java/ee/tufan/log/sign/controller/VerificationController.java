package ee.tufan.log.sign.controller;

import ee.tufan.log.sign.service.VerificationService;
import ee.tufan.log.sign.service.VerificationServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

@Controller
public class VerificationController {

	private final VerificationService verificationService;

	@GetMapping("/verify")
	public String upload() {
		return "verify";
	}

	@PostMapping("/verify")
	public String verify(String logLine, Model model) {
		try {
			List<Map<String, String>> logVerificationList = verificationService.verify(logLine);
			model.addAttribute("logVerificationList", logVerificationList);
		} catch (VerificationServiceException ex) {
			model.addAttribute("error", ex.getMessage());
		}
		return "verify";
	}

	@Autowired
	public VerificationController(VerificationService verificationService) {
		this.verificationService = verificationService;
	}

}
