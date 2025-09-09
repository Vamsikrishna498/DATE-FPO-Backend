package com.farmer.Form.Config;
 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.core.env.Environment;
 
import java.util.Properties;
 
@Configuration
public class MailConfig {
 
    @Bean
    public JavaMailSender javaMailSender(Environment env) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
 
        String host = env.getProperty("spring.mail.host");
        int port = Integer.parseInt(env.getProperty("spring.mail.port", "465"));
        String username = env.getProperty("spring.mail.username");
        String password = env.getProperty("spring.mail.password");
 
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
 
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", env.getProperty("spring.mail.properties.mail.smtp.auth", "true"));
        props.put("mail.smtp.ssl.enable", env.getProperty("spring.mail.properties.mail.smtp.ssl.enable", "true"));
        props.put("mail.smtp.starttls.enable", env.getProperty("spring.mail.properties.mail.smtp.starttls.enable", "true"));
        props.put("mail.smtp.ssl.trust", env.getProperty("spring.mail.properties.mail.smtp.ssl.trust", host != null ? host : ""));
        props.put("mail.debug", "true");
 
        return mailSender;
    }
}