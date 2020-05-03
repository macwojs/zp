package edu.agh.zp;

import edu.agh.zp.repositories.CitizenRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories( basePackageClasses = CitizenRepository.class)
public class ZpApplication {


	public static void main( String[] args ) {
		SpringApplication.run( ZpApplication.class, args );
	}

}
