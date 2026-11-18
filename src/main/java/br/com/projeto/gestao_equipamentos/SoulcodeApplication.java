package br.com.projeto.gestao_equipamentos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class SoulcodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SoulcodeApplication.class, args);
	}

}
