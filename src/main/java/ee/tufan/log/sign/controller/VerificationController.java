package ee.tufan.log.sign.controller;

import ee.tufan.log.sign.service.SignService;
import ee.tufan.log.sign.service.SignServiceException;
import ee.tufan.log.sign.service.VerificationService;
import ee.tufan.log.sign.service.VerificationServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class VerificationController {

	private final VerificationService verificationService;

	@Autowired
	public VerificationController(VerificationService verificationService) {
		this.verificationService = verificationService;
	}

	@GetMapping("/verify")
	public String upload(Model model) throws IOException {
		return "verify";
	}

	@PostMapping("/verify")
	public String verify(String logLine, Model model) {
		List<Map<String, String>> logVerificationList = null;
		try {
			logVerificationList = verificationService.verify(logLine);
		} catch (VerificationServiceException ex) {
			// TODO: handle
			ex.printStackTrace();
		}
		model.addAttribute("logVerificationList", logVerificationList);
		return "verify";
	}

}
