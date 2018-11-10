package pssg.poc.vph.jivtdsender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication
public class JivtdsenderApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(JivtdsenderApplication.class, args);
	}
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(JivtdsenderApplication.class);
    }
}
