package com.example.pocecdh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class PocEcdhApplication {

    public static void main(String[] args) {
        SpringApplication.run(PocEcdhApplication.class, args);
    }

}
