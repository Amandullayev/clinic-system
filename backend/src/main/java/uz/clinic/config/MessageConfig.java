package uz.clinic.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Spring MessageSource konfiguratsiyasi.
 * messages_uz.properties, messages_en.properties, messages_ru.properties
 * fayllarini src/main/resources/messages/ papkasidan o'qiydi.
 */
@Configuration
public class MessageConfig {

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasename("classpath:messages/messages");
        source.setDefaultEncoding("UTF-8");
        source.setDefaultLocale(new java.util.Locale("uz")); // fallback: o'zbek
        source.setCacheSeconds(3600); // Production da 1 soat cache
        return source;
    }
}