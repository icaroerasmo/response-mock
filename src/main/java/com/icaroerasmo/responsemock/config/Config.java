package com.icaroerasmo.responsemock.config;

import com.icaroerasmo.responsemock.models.Endpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.UUID;

@Configuration
//@EnableWebSecurity
public class Config {

    @Bean
    public RedisTemplate<UUID, Endpoint> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<UUID, Endpoint> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf((csrf) -> csrf.disable())
//                .cors(cors -> {
//                    cors.configurationSource(corsConfigurationSource());
//                })
//                .authorizeHttpRequests((auth) -> auth
//                        .anyRequest()
//                        .permitAll()
//                );
//        return http.build();
//    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:3000", "https://mock.icaroerasmo.com");
            }
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new
                UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
