package es.uv.adiez;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import es.uv.adiez.domain.User;
import es.uv.adiez.domain.UserType;
import es.uv.adiez.services.UserService;



@SpringBootApplication
//@EnableMongoRepositories(basePackages = {"es.uv.adiez.repositories"})
public class DataProducerApplication implements ApplicationRunner{

	@Autowired
	UserService us;
	
	
	public static void main(String[] args) {
		SpringApplication.run(DataProducerApplication.class, args);
	}
	
	@Override
	public void run(ApplicationArguments args) throws JsonMappingException, JsonProcessingException {
		
	    //Resource resource = new ClassPathResource("data.txt");
        //is.doImport(resource);
                
        /*us.deleteAll();
        
        User u = us.create(new User("admin", "1234", "123456J", "Anabel", UserType.physical, new String[] {"ROLE_USER", "ROLE_ADMIN"}));
        System.out.println(u);
        u = us.create(new User("user", "1234", "123456J", "Otro", UserType.physical, new String[] {"ROLE_USER"}));
        System.out.println(u);*/
	}

}