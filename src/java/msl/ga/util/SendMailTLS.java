/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msl.ga.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author edgarloraariza
 */
public class SendMailTLS {
    
    public void sendEmail(String toEmail, String subject, String htmlMsg) throws Exception{
        Properties properties = new Properties();
        String MSLGAConfigPath = getClass().getResource("/").getPath() + "MSL.GA.Config.properties";
        InputStream inputStream = new FileInputStream(MSLGAConfigPath);
        if(inputStream == null){
            throw new Exception("No se encuentra archivo de configurcion de base de datos: " + MSLGAConfigPath);
        }
        properties.load(inputStream);
        boolean envioDeEmails = Boolean.valueOf(properties.getProperty("msl_ga_gmail_activate"));
        if(envioDeEmails){
            String username = properties.getProperty("msl_ga_gmail_account");
            String password = properties.getProperty("msl_ga_gmail_password");
            
            Properties props = new Properties();
            props.put("mail.smtp.auth", Boolean.valueOf(properties.getProperty("mail_smtp_auth")));
            props.put("mail.smtp.starttls.enable", Boolean.valueOf(properties.getProperty("mail_smtp_starttls_enable")));
            props.put("mail.smtp.host", properties.getProperty("mail_smtp_host"));
            props.put("mail.smtp.port", properties.getProperty("mail_smtp_port"));

            Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                    }
            });


            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setHeader("Content-Type", "text/html; charset=UTF-8");
            message.setContent(htmlMsg, "text/html");
            Transport.send(message);
        }
    }
}
