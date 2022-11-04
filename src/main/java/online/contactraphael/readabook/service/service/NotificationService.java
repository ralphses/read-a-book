package online.contactraphael.readabook.service.service;

import online.contactraphael.readabook.model.user.AppUser;

import java.util.List;

public interface NotificationService {

    void sendEmailNotification(List<String> emails, String sender, String message, String subject);

    void sendSMSNotification(List<String> phoneNumber, String sender, String message, String subject);
}
