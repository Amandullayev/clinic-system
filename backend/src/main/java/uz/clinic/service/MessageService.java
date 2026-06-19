package uz.clinic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Locale;

/**
 * Markaziy xabar tarjima xizmati.
 * Accept-Language header ga qarab mos tildagi xabarni qaytaradi.
 *
 * Ishlatish:
 *   messageService.get("auth.login.success")
 *   messageService.get("patient.not_found")
 */
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageSource messageSource;

    /**
     * Joriy so'rovdagi Accept-Language asosida tarjima qaytaradi.
     */
    public String get(String key) {
        return messageSource.getMessage(key, null, getCurrentLocale());
    }

    /**
     * Parametrli xabarlar uchun.
     * Misol: messages.properties da "user.greeting=Assalomu alaykum, {0}!"
     *        get("user.greeting", "Alisher") → "Assalomu alaykum, Alisher!"
     */
    public String get(String key, Object... args) {
        return messageSource.getMessage(key, args, getCurrentLocale());
    }

    /**
     * Request context dan Accept-Language header ni o'qiydi.
     * Agar header yo'q bo'lsa yoki aniqlanmasa — default Locale.uz ishlatiladi.
     */
    public Locale getCurrentLocale() {
        try {
            var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return new Locale("uz");

            String lang = attrs.getRequest().getHeader("Accept-Language");
            if (lang == null || lang.isBlank()) return new Locale("uz");

            // "uz", "en", "ru" yoki "en-US,en;q=0.9" formatlarini qabul qiladi
            String code = lang.split("[,;]")[0].trim().toLowerCase();
            return switch (code) {
                case "en", "en-us", "en-gb" -> Locale.ENGLISH;
                case "ru"                    -> new Locale("ru");
                default                      -> new Locale("uz");
            };
        } catch (Exception e) {
            return new Locale("uz");
        }
    }
}