package com.methaltech.application.data;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailMessage;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailConfig {

	@Bean
	public MailSender mailSender() {
		JavaMailSenderImpl senderemail = new JavaMailSenderImpl();

		senderemail.setHost("smtp.gmail.com");
		senderemail.setPort(587);
		senderemail.setUsername("urcbudgetapp@gmail.com");
		senderemail.setPassword("ethftdnshnuisvjc");

		Properties javaMailProperties = new Properties();
		javaMailProperties.put("mail.smtp.auth", true);
		javaMailProperties.put("mail.smtp.starttls.enable", true);
                

		senderemail.setJavaMailProperties(javaMailProperties);

		return senderemail;
	}

	@Bean
	public MailMessage mailMessage() {
		return new SimpleMailMessage();
	}

}
