package uz.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${frontend.url}")
    private String frontendUrl;

    public void sendOtp(String toEmail, String otp) {
        send(toEmail,
                "CLINIC - Tasdiqlash kodi",
                "Salom!\n\n" +
                        "Tasdiqlash kodingiz: " + otp + "\n\n" +
                        "Kod 5 daqiqa davomida amal qiladi.\n\n" +
                        "CLINIC tizimi");
    }

    // YANGI: bemor navbat olgandan keyin tasdiqlash emaili
    public void sendAppointmentConfirmationRequest(String toEmail,
                                                   String patientName,
                                                   String doctorName,
                                                   String appointmentTime,
                                                   String confirmToken) {
        String confirmUrl = frontendUrl + "/confirm-appointment?token=" + confirmToken;
        String cancelUrl  = frontendUrl + "/cancel-appointment?token=" + confirmToken;

        send(toEmail,
                "CLINIC - Navbatni tasdiqlang",
                "Hurmatli " + patientName + "!\n\n" +
                        "Siz " + doctorName + " shifokorga " + appointmentTime + " vaqtiga yozildingiz.\n\n" +
                        "Navbatni tasdiqlash uchun: " + confirmUrl + "\n\n" +
                        "Bekor qilish uchun: " + cancelUrl + "\n\n" +
                        "DIQQAT: 24 soat ichida tasdiqlanmasa, navbatingiz avtomatik bekor qilinadi.\n\n" +
                        "CLINIC tizimi");
    }

    // YANGI: qabuldan 2 soat oldin eslatma
    public void sendAppointmentReminder(String toEmail,
                                        String patientName,
                                        String doctorName,
                                        String appointmentTime) {
        send(toEmail,
                "CLINIC - Bugun navbatingiz bor",
                "Hurmatli " + patientName + "!\n\n" +
                        "Eslatma: bugun " + appointmentTime + " da " +
                        doctorName + " shifokorga navbatingiz bor.\n\n" +
                        "Agar kela olmasangiz, iltimos oldindan xabar bering.\n\n" +
                        "CLINIC tizimi");
    }

    // YANGI: slot bo'shaganda xabar (kutish ro'yxatidagi bemor uchun — kelajakda)
    public void sendSlotAvailableNotification(String toEmail,
                                              String patientName,
                                              String doctorName,
                                              String appointmentTime) {
        send(toEmail,
                "CLINIC - Bo'sh joy chiqdi",
                "Hurmatli " + patientName + "!\n\n" +
                        doctorName + " shifokorga " + appointmentTime +
                        " vaqtida bo'sh joy chiqdi.\n\n" +
                        "Tezroq yoziling — joy cheklangan!\n\n" +
                        "CLINIC tizimi");
    }

    // YANGI: no-show ogohlantirish
    public void sendNoShowWarning(String toEmail,
                                  String patientName,
                                  int noShowCount) {
        String message = noShowCount >= 3
                ? "Online navbat olish imkoniyatingiz vaqtincha cheklandi. " +
                  "Navbat olish uchun klinikaga murojaat qiling."
                : "Bu " + noShowCount + "-marta. 3 martadan keyin online navbat " +
                  "olish imkoniyati cheklanadi.";

        send(toEmail,
                "CLINIC - Kelmagan navbat haqida",
                "Hurmatli " + patientName + "!\n\n" +
                        "Siz belgilangan navbatga kelmadingiz.\n\n" +
                        message + "\n\n" +
                        "CLINIC tizimi");
    }

    private void send(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}