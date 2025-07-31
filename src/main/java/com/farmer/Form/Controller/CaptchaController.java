package com.farmer.Form.Controller;

import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
public class CaptchaController {

    private static final String CAPTCHA_ATTRIBUTE = "captcha";
    private static final Random RANDOM = new Random();

    @GetMapping("/api/captcha")
    public Map<String, String> getCaptcha(HttpSession session) {
        String captcha = generateRandomString(6); // 6-character captcha
        session.setAttribute(CAPTCHA_ATTRIBUTE, captcha);
        Map<String, String> response = new HashMap<>();
        response.put(CAPTCHA_ATTRIBUTE, captcha);
        return response;
    }

    @PostMapping("/api/captcha/verify")
    public Map<String, Object> verifyCaptcha(@RequestBody Map<String, String> body, HttpSession session) {
        String userInput = body.get(CAPTCHA_ATTRIBUTE);
        Object captchaObj = session.getAttribute(CAPTCHA_ATTRIBUTE);
        Map<String, Object> result = new HashMap<>();
        if (captchaObj != null && userInput != null && userInput.equalsIgnoreCase(captchaObj.toString())) {
            result.put("success", true);
        } else {
            result.put("success", false);
            result.put("message", "Invalid captcha");
        }
        return result;
    }

    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }
} 