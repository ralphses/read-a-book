package online.contactraphael.readabook;

import online.contactraphael.readabook.configuration.security.RsaKeyProperties;
import online.contactraphael.readabook.model.user.AppUser;
import online.contactraphael.readabook.model.user.UserRole;
import online.contactraphael.readabook.repository.AppUserRepository;
import online.contactraphael.readabook.utility.monnify.MonnifyCredential;
import online.contactraphael.readabook.utility.uploads.FileStorage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableConfigurationProperties({RsaKeyProperties.class, FileStorage.class, MonnifyCredential.class})
public class ReadABookApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReadABookApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			String email = "eze.raph@gmail.com";
				AppUser appUser = AppUser.builder()
						.password(passwordEncoder.encode("password"))
						.isEnabled(true)
						.userRole(UserRole.CUSTOMER)
						.isAccountNonLocked(true)
						.email(email)
						.fullName("Eze Raphael")
						.build();
			if(appUserRepository.findByEmail(email).isEmpty()) {
				appUserRepository.save(appUser);
			}
		};
	}

}
