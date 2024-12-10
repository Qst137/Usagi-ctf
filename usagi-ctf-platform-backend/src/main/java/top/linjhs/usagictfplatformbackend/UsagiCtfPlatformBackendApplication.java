package top.linjhs.usagictfplatformbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class UsagiCtfPlatformBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsagiCtfPlatformBackendApplication.class, args);
	}

}
