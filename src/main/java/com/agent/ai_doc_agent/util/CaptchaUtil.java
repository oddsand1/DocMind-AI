package com.agent.ai_doc_agent.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CaptchaUtil {

    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz0123456789";

    public String generateCaptcha() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public String generateCaptchaKey() {
        return "captcha:" + System.currentTimeMillis() + ":" + new Random().nextInt(10000);
    }
}