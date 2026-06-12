package br.com.danilo.fipevariation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class FipeVariationApplication {

    public static void main(String[] args) {
        SpringApplication.run(FipeVariationApplication.class, args);
    }
}
