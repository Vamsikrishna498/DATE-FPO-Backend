package com.farmer.Form.Service;


 
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.farmer.Form.DTO.EmailServiceDTO;
import com.farmer.Form.Entity.Template;
import com.farmer.Form.Repository.TemplateRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
 
@Builder
@Slf4j
@Service
public class EmailService {
 
    private final JavaMailSender mailSender;
    private final TemplateRepository templateRepository;
 
    @Autowired
    public EmailService(JavaMailSender mailSender, TemplateRepository templateRepository) {
        this.mailSender = mailSender;
        this.templateRepository = templateRepository;
    }

    // Resolve active template by name/type/module
    private Optional<Template> resolveTemplate(String name, Template.TemplateType type, Template.ModuleType module) {
        try {
            return templateRepository.findByTemplateNameAndTemplateTypeAndModuleType(name, type, module)
                    .filter(t -> Boolean.TRUE.equals(t.getIsActive()));
        } catch (Exception e) {
            log.warn("Template lookup failed for {} / {} / {}: {}", name, type, module, e.getMessage());
            return Optional.empty();
        }
    }

    private String render(String content, Map<String, String> vars) {
        if (content == null) return "";
        String rendered = content;
        if (vars != null) {
            for (Map.Entry<String, String> e : vars.entrySet()) {
                String key = e.getKey();
                String val = e.getValue() == null ? "" : e.getValue();
                rendered = rendered.replace("{" + key + "}", val);
            }
        }
        return rendered;
    }
 
    @Async
    public void sendEmail(EmailServiceDTO emailDto) {
        if (emailDto == null || emailDto.getTo() == null || emailDto.getTo().isEmpty()) {
            log.error("Email DTO or recipient address is null or empty");
            throw new IllegalArgumentException("Email DTO and recipient address must not be null or empty");
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("dateproject@hinfinitysolutions.com");
            message.setTo(emailDto.getTo());
            message.setSubject(emailDto.getSubject());
            message.setText(emailDto.getBody());
            mailSender.send(message);
            log.info("Email sent successfully to {}", emailDto.getTo());
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", emailDto.getTo(), e.getMessage(), e);
        }
    }
 
    @Async
    public void sendOtpEmail(String to, String otp) {
        if (to == null || to.isEmpty() || otp == null || otp.isEmpty()) {
            log.error("Recipient or OTP is null or empty");
            throw new IllegalArgumentException("Recipient and OTP must not be null or empty");
        }
        try {
            String subject = "Your OTP Code";
            String body = "Your OTP is: " + otp;
            // Try template override
            Map<String, String> vars = new HashMap<>();
            vars.put("otp", otp);
            vars.put("email", to);
            Optional<Template> tpl = resolveTemplate("OTP", Template.TemplateType.EMAIL, Template.ModuleType.SYSTEM);
            if (tpl.isPresent()) {
                if (tpl.get().getSubject() != null && !tpl.get().getSubject().isBlank()) subject = tpl.get().getSubject();
                body = render(tpl.get().getContent(), vars);
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("dateproject@hinfinitysolutions.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("OTP email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", to, e.getMessage(), e);
        }
    }
 
    @Async
    public void sendRegistrationEmail(String to, String name) {
        if (to == null || to.isEmpty()) {
            log.error("Recipient email is null or empty");
            throw new IllegalArgumentException("Recipient email must not be null or empty");
        }
        try {
            String subject = "Welcome to AgriStack!";
            String defaultBody = new StringBuilder()
                    .append("Welcome to AgriStack!\n\n")
                    .append("Hi ").append(name != null ? name : "User").append(",\n\n")
                    .append("Your registration is complete and currently pending admin approval.\n")
                    .append("You'll be notified once your account is activated.\n\n")
                    .append("Regards,\nAgriStack Team").toString();

            Map<String, String> vars = new HashMap<>();
            vars.put("name", name);
            vars.put("email", to);
            String body = defaultBody;
            Optional<Template> tpl = resolveTemplate("REGISTRATION", Template.TemplateType.EMAIL, Template.ModuleType.SYSTEM);
            if (tpl.isPresent()) {
                if (tpl.get().getSubject() != null && !tpl.get().getSubject().isBlank()) subject = tpl.get().getSubject();
                body = render(tpl.get().getContent(), vars);
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("dateproject@hinfinitysolutions.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Registration email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send registration email to {}: {}", to, e.getMessage(), e);
        }
    }

    // Notify employee when a farmer is assigned
    @Async
    public void sendFarmerAssignedToEmployee(String employeeEmail, String employeeName, String farmerName, Long farmerId) {
        if (employeeEmail == null || employeeEmail.isEmpty()) {
            log.error("Recipient email is null or empty");
            throw new IllegalArgumentException("Recipient email must not be null or empty");
        }
        try {
            String subject = "New farmer assigned to you";
            String defaultBody = new StringBuilder()
                    .append("Hi ").append(employeeName != null ? employeeName : "Employee").append(",\n\n")
                    .append("Farmer ").append(farmerName != null ? farmerName : String.valueOf(farmerId)).append(" (ID: ")
                    .append(farmerId != null ? farmerId : "-").append(") has been assigned to you.\n\n")
                    .append("Regards,\nAgriStack Team").toString();

            Map<String, String> vars = new HashMap<>();
            vars.put("employeeName", employeeName);
            vars.put("employeeEmail", employeeEmail);
            vars.put("farmerName", farmerName);
            vars.put("farmerId", farmerId != null ? String.valueOf(farmerId) : "");

            String body = defaultBody;
            Optional<Template> tpl = resolveTemplate("FARMER_ASSIGNED", Template.TemplateType.EMAIL, Template.ModuleType.EMPLOYEE);
            if (tpl.isPresent()) {
                if (tpl.get().getSubject() != null && !tpl.get().getSubject().isBlank()) subject = tpl.get().getSubject();
                body = render(tpl.get().getContent(), vars);
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("dateproject@hinfinitysolutions.com");
            message.setTo(employeeEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Assignment email sent successfully to {} for farmer {}", employeeEmail, farmerId);
        } catch (Exception e) {
            log.error("Failed to send assignment email to {}: {}", employeeEmail, e.getMessage(), e);
        }
    }
 
    @Async
    public void sendUserIdEmail(String to, String userId) {
        if (to == null || to.isEmpty() || userId == null || userId.isEmpty()) {
            log.error("Recipient email or user ID is null or empty");
            throw new IllegalArgumentException("Recipient email and user ID must not be null or empty");
        }
        try {
            String subject = "Your User ID - DigitalAgristack";
            String body = "Dear user,\n\nYour registered User ID is: " + userId +
                    "\n\nIf you did not request this, please ignore this email.\n\n" +
                    "Regards,\nDigitalAgristack Team";

            Map<String, String> vars = new HashMap<>();
            vars.put("userId", userId);
            vars.put("email", to);
            Optional<Template> tpl = resolveTemplate("USER_ID", Template.TemplateType.EMAIL, Template.ModuleType.SYSTEM);
            if (tpl.isPresent()) {
                if (tpl.get().getSubject() != null && !tpl.get().getSubject().isBlank()) subject = tpl.get().getSubject();
                body = render(tpl.get().getContent(), vars);
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("dateproject@hinfinitysolutions.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
 
            log.info("User ID email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send User ID email to {}: {}", to, e.getMessage(), e);
        }
    }
 
    // NEW method to send password reset confirmation email
    @Async
    public void sendPasswordResetConfirmationEmail(String to, String name) {
        if (to == null || to.isEmpty()) {
            log.error("Recipient email is null or empty");
            throw new IllegalArgumentException("Recipient email must not be null or empty");
        }
        try {
            String subject = "Password Reset Confirmation";
            String defaultBody = new StringBuilder()
                    .append("Dear ").append(name != null ? name : "User").append(",\n\n")
                    .append("Your password has been changed successfully.\n")
                    .append("If you did not perform this action, please contact support immediately.\n\n")
                    .append("Regards,\n")
                    .append("The Farmer Management Team").toString();

            Map<String, String> vars = new HashMap<>();
            vars.put("name", name);
            vars.put("email", to);
            String body = defaultBody;
            Optional<Template> tpl = resolveTemplate("PASSWORD_RESET_CONFIRMATION", Template.TemplateType.EMAIL, Template.ModuleType.SYSTEM);
            if (tpl.isPresent()) {
                if (tpl.get().getSubject() != null && !tpl.get().getSubject().isBlank()) subject = tpl.get().getSubject();
                body = render(tpl.get().getContent(), vars);
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("dateproject@hinfinitysolutions.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
 
            log.info("Password reset confirmation email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send password reset confirmation email to {}: {}", to, e.getMessage(), e);
        }
    }

    @Async
    public void sendAccountApprovedEmail(String to, String name, String tempPassword) {
        if (to == null || to.isEmpty()) {
            log.error("Recipient email is null or empty");
            throw new IllegalArgumentException("Recipient email must not be null or empty");
        }
        try {
            String subject = "Your AgriStack Account is Approved!";
            String defaultBody = new StringBuilder()
                    .append("Hi ").append(name != null ? name : "User").append(",\n\n")
                    .append("Your account has been approved and is now active!\n")
                    .append("You can log in using the following credentials:\n")
                    .append("Email: ").append(to).append("\n")
                    .append("Temporary Password: ").append(tempPassword).append("\n\n")
                    .append("Please change your password after your first login for security.\n\n")
                    .append("Regards,\nAgriStack Team").toString();

            Map<String, String> vars = new HashMap<>();
            vars.put("name", name);
            vars.put("email", to);
            vars.put("tempPassword", tempPassword);
            String body = defaultBody;
            Optional<Template> tpl = resolveTemplate("ACCOUNT_APPROVED", Template.TemplateType.EMAIL, Template.ModuleType.SYSTEM);
            if (tpl.isPresent()) {
                if (tpl.get().getSubject() != null && !tpl.get().getSubject().isBlank()) subject = tpl.get().getSubject();
                body = render(tpl.get().getContent(), vars);
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("dateproject@hinfinitysolutions.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Account approval email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send account approval email to {}: {}", to, e.getMessage(), e);
        }
    }

    @Async
    public void sendAccountRejectedEmail(String to, String name) {
        if (to == null || to.isEmpty()) {
            log.error("Recipient email is null or empty");
            throw new IllegalArgumentException("Recipient email must not be null or empty");
        }
        try {
            String subject = "Your AgriStack Account Registration Status";
            String defaultBody = new StringBuilder()
                    .append("Hi ").append(name != null ? name : "User").append(",\n\n")
                    .append("We regret to inform you that your registration request has been rejected after review.\n")
                    .append("If you believe this is a mistake or need more information, please contact support.\n\n")
                    .append("Regards,\nAgriStack Team").toString();

            Map<String, String> vars = new HashMap<>();
            vars.put("name", name);
            vars.put("email", to);
            String body = defaultBody;
            Optional<Template> tpl = resolveTemplate("ACCOUNT_REJECTED", Template.TemplateType.EMAIL, Template.ModuleType.SYSTEM);
            if (tpl.isPresent()) {
                if (tpl.get().getSubject() != null && !tpl.get().getSubject().isBlank()) subject = tpl.get().getSubject();
                body = render(tpl.get().getContent(), vars);
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("dateproject@hinfinitysolutions.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Account rejection email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send account rejection email to {}: {}", to, e.getMessage(), e);
        }
    }
}
