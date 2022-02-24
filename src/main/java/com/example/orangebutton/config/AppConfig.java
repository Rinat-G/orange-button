package com.example.orangebutton.config;

import com.example.orangebutton.domain.service.AuthService;
import com.example.orangebutton.web.filter.AuthFilter;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

import static java.util.Collections.singletonList;

@Configuration
public class AppConfig {

    @Value("${google.client.id}")
    private String clientId;

    @Bean
    public GoogleIdTokenVerifier verifier() {
        return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(singletonList(clientId))
                .build();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public FilterRegistrationBean<AuthFilter> logFilter(AuthService authService) {
        FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AuthFilter(authService));
        registrationBean.addUrlPatterns("/check", "/signup", "/session/*", "/session");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public S3Client yandexS3client() {
        return S3Client.builder()
                .endpointOverride(URI.create("https://storage.yandexcloud.net"))
                .build();
    }
}
