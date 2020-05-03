package edu.agh.zp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories( basePackageClasses = edu.agh.zp.repositories.CitizenRepository.class)
public class ZpApplication {


	public static void main( String[] args ) {
		SpringApplication.run( ZpApplication.class, args );
	}

}
