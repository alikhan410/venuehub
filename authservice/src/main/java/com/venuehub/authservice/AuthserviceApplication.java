package com.venuehub.authservice;

import com.venuehub.authservice.model.Role;
import com.venuehub.authservice.model.User;
import com.venuehub.authservice.repository.RoleRepository;
import com.venuehub.authservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.venuehub.authservice","com.venuehub.broker"})
public class AuthserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthserviceApplication.class, args);
	}
	@Transactional
	@Bean
	CommandLineRunner run(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {

			if (userRepository.findByUsername("admin").isEmpty()) {


				Role adminRole = new Role("ADMIN");

				roleRepository.save(adminRole);

				Set<Role> roles = new HashSet<>();
				roles.add(adminRole);
				String password = passwordEncoder.encode("pass");

				User admin = new User();
				admin.setUsername("admin");
				admin.setFirstName("admin");
				admin.setLastName("01");
				admin.setEmail("admin@gmail.com");
				admin.setAuthorities(roles);
				admin.setPassword(password);

				userRepository.save(admin);
			}
			if (userRepository.findByUsername("user").isEmpty()) {

				
				Role userRole = new Role("USER");
				roleRepository.save(userRole);

				Set<Role> roles = new HashSet<>();
				roles.add(userRole);
				String password = passwordEncoder.encode("pass");

				User user = new User();
				user.setUsername("user");
				user.setFirstName("user");
				user.setLastName("01");
				user.setEmail("user@gmail.com");
				user.setAuthorities(roles);
				user.setPassword(password);

				userRepository.save(user);
			}
			if (userRepository.findByUsername("vendor").isEmpty()) {
				
				Role vendorRole = new Role("VENDOR");
				
				roleRepository.save(vendorRole);

				Set<Role> roles = new HashSet<>();
				roles.add(vendorRole);
				String password = passwordEncoder.encode("pass");

				User vendor = new User();
				vendor.setUsername("vendor");
				vendor.setFirstName("vendor");
				vendor.setLastName("01");
				vendor.setEmail("vendor@gmail.com");
				vendor.setAuthorities(roles);
				vendor.setPassword(password);

				userRepository.save(vendor);
			}

		};
	}
}
