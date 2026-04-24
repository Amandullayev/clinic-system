package uz.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;



    public void sendOtp(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("amandullayevmuhammadali8@gmail.com");
        message.setTo(toEmail);
        message.setSubject("CLINIC - Tasdiqlash kodi");
        message.setText(
                "Assalomu alaykum!\n\n" +
                        "Sizning tasdiqlash kodingiz: " + otp + "\n\n" +
                        "Kod 5 daqiqa davomida amal qiladi.\n\n" +
                        "CLINIC tizimi"
        );
        mailSender.send(message);
    }
}