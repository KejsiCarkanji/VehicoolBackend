package com.vehicool.vehicool;

import com.vehicool.vehicool.business.service.*;
import com.vehicool.vehicool.persistence.entity.Lender;
import com.vehicool.vehicool.persistence.entity.Renter;
import com.vehicool.vehicool.persistence.entity.Vehicle;
import com.vehicool.vehicool.security.auth.AuthenticationService;
import com.vehicool.vehicool.security.auth.RegisterRequest;
import com.vehicool.vehicool.security.user.Role;
import com.vehicool.vehicool.security.user.User;
import com.vehicool.vehicool.security.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
@RequiredArgsConstructor
public class VehicoolApplication {
    private final AuthenticationService authenticationService;
    private final LenderService lenderService;
    private final RenterService renterService;
    private final UserService userService;
    private final AdministratorService administratorService;
    private final JdbcTemplate jdbcTemplate;
    private final DataPoolService dataPoolService;
    private final VehicleService vehicleService;

    public static void main(String[] args) {
        SpringApplication.run(VehicoolApplication.class, args);
    }

    @Bean
    public String createOwnerProfile() throws IOException {
        if (dataPoolService.totalElements() == 0) {
            ClassPathResource resource = new ClassPathResource("inserts.sql");
            String script = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            jdbcTemplate.execute(script);
            System.out.println("SQL script executed successfully!");
        }
        if (userService.getUserByUsername("owner") == null) {
            authenticationService.register(new RegisterRequest("owner", "vehicool", "project",21, "vehicool@gmail.com","0682011111", "12345678"));
            User user = userService.getUserByUsername("owner");
            Set<Role> roles = new HashSet<>();
            roles.add(Role.ADMIN);
            roles.add(Role.LENDER);
            roles.add(Role.RENTER);
            roles.add(Role.USER);
            user.setRoles(roles);
            user.setUserStatus(dataPoolService.findByEnumLabel("VerifiedUser"));
            administratorService.saverUser(user, user.getUsername());
        }
        if (userService.getUserByUsername("lender") == null) {
            authenticationService.register(new RegisterRequest("lender", "lender", "lender",22, "lender@lender.com","0672011111", "12345678"));
            User user = userService.getUserByUsername("lender");
            Set<Role> roles = new HashSet<>();
            roles.add(Role.LENDER);
            roles.add(Role.USER);
            user.setRoles(roles);
            user.setUserStatus(dataPoolService.findByEnumLabel("VerifiedUser"));
            Lender lender = new Lender();
            lender.setUser(user);
            lender.setStatus(dataPoolService.findByEnumLabel("VerifiedLender"));
            lenderService.save(lender);
            user.setLenderProfile(lender);
            administratorService.saverUser(user, user.getUsername());
            Vehicle vehicle = new Vehicle();
            vehicle.setLender(lender);
            vehicle.setBrand("Rolls-Royce");
            vehicle.setModel("Phantom");
            vehicle.setEngineSize(6.8);
            vehicle.setAvailable(true);
            vehicle.setPlateNo("AA666EH");
            vehicle.setLocation(dataPoolService.getDataPoolById(24L));
            vehicle.setVehicleType(dataPoolService.findByEnumLabel("Cars"));
            vehicle.setEngineType(dataPoolService.findByEnumLabel("Gasoline"));
            vehicle.setColor("Black");
            vehicle.setTransmissionType(dataPoolService.findByEnumLabel("Automatic"));
            vehicle.setStatus(dataPoolService.findByEnumLabel("VerifiedVehicle"));
            vehicle.setNoOfSeats(4L);
            vehicle.setProductionYear(2024L);
            vehicle.setVin("SC22301232902DK");
            vehicleService.save(vehicle);
            Vehicle vehicle2 = new Vehicle();
            vehicle2.setLender(lender);
            vehicle2.setBrand("Mercedes-Benz");
            vehicle2.setModel("S-class");
            vehicle2.setEngineSize(4.0);
            vehicle2.setAvailable(true);
            vehicle2.setPlateNo("AA001EH");
            vehicle2.setLocation(dataPoolService.getDataPoolById(24L));
            vehicle2.setVehicleType(dataPoolService.findByEnumLabel("Cars"));
            vehicle2.setEngineType(dataPoolService.findByEnumLabel("Gasoline"));
            vehicle2.setColor("White");
            vehicle2.setTransmissionType(dataPoolService.findByEnumLabel("Automatic"));
            vehicle2.setStatus(dataPoolService.findByEnumLabel("VerifiedVehicle"));
            vehicle2.setNoOfSeats(4L);
            vehicle2.setProductionYear(2024L);
            vehicle2.setVin("AK22336332902DK");
            vehicleService.save(vehicle2);
            Vehicle vehicle3 = new Vehicle();
            vehicle3.setLender(lender);
            vehicle3.setBrand("Fiat");
            vehicle3.setModel("Punto");
            vehicle3.setEngineSize(1.2);
            vehicle3.setAvailable(true);
            vehicle3.setPlateNo("AA283VL");
            vehicle3.setLocation(dataPoolService.getDataPoolById(25L));
            vehicle3.setVehicleType(dataPoolService.findByEnumLabel("Cars"));
            vehicle3.setEngineType(dataPoolService.findByEnumLabel("Gasoline"));
            vehicle3.setColor("Pink");
            vehicle3.setTransmissionType(dataPoolService.findByEnumLabel("Automatic"));
            vehicle3.setStatus(dataPoolService.findByEnumLabel("VerifiedVehicle"));
            vehicle3.setNoOfSeats(5L);
            vehicle3.setProductionYear(2024L);
            vehicle3.setVin("GT12336332902DK");
            vehicleService.save(vehicle3);
        }
        if (userService.getUserByUsername("renter") == null) {
            authenticationService.register(new RegisterRequest("renter", "renter", "renter",33, "renter@renter.com","0692011111", "12345678"));
            User user = userService.getUserByUsername("renter");
            Set<Role> roles = new HashSet<>();
            roles.add(Role.RENTER);
            roles.add(Role.USER);
            user.setRoles(roles);
            user.setUserStatus(dataPoolService.findByEnumLabel("VerifiedUser"));
            Renter renter = new Renter();
            renter.setStatus(dataPoolService.findByEnumLabel("VerifiedRenter"));
            renter.setUser(user);
            renterService.save(renter);
            user.setRenterProfile(renter);
            administratorService.saverUser(user, user.getUsername());
        }
        return "ACCOUNTS CREATED";
    }

}
