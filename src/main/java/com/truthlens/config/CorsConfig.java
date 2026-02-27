//package com.truthlens.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class CorsConfig implements WebMvcConfigurer {
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/api/**")
//                .allowedOrigins("http://localhost:5174") 
//                .allowedMethods("*");
//    }
//}

package com.truthlens.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Saari APIs ke liye (jaise /api/v1/verify)
                .allowedOriginPatterns("*") // Har jagah se request aane do (including chrome-extension://)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // OPTIONS jaruri hai extension ke liye
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}