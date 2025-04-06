package com.good.animalsgame.extern.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

/**
 * Конфигурация Swagger
 */
@Configuration
public class OpenAPIConfig {

    /**
     * Конфигурирует настройки Swagger
     */
    @Bean
    public OpenAPI myOpenAPI() {
        Contact contact = new Contact();
        contact.setName("Команда Хорошая");

        Info info = new Info()
                .title("AnimalsGame")
                .version("1.0.0")
                .contact(contact)
                .description("Игра по биоразнообразию");

        return new OpenAPI().info(info);
    }
}
