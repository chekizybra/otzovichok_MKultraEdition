package chekizybra.otzovichok;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "chekizybra.otzovichok.repository")
@EntityScan(basePackages = "chekizybra.otzovichok.model")
public class OtzovichokApplication {
	public static void main(String[] args) {
		SpringApplication.run(OtzovichokApplication.class, args);
	}
}