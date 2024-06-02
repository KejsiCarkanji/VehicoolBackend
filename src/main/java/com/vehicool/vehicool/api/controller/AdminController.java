package com.vehicool.vehicool.api.controller;

import com.vehicool.vehicool.api.dto.StatusDTO;
import com.vehicool.vehicool.business.querydsl.UserFilter;
import com.vehicool.vehicool.business.querydsl.VehicleFilter;
import com.vehicool.vehicool.business.service.*;
import com.vehicool.vehicool.persistence.entity.*;
import com.vehicool.vehicool.security.user.Role;
import com.vehicool.vehicool.security.user.User;
import com.vehicool.vehicool.security.user.UserService;
import com.vehicool.vehicool.util.mappers.ResponseMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.vehicool.vehicool.util.constants.Messages.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin-panel")
public class AdminController {
    private final ModelMapper modelMapper;
    private final RenterService renterService;
    private final LenderService lenderService;
    private final DataPoolService dataPoolService;
    private final AdministratorService adminService;
    private final VehicleService vehicleService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final BannedUsersAppealingService bannedUsersAppealingService;

    @GetMapping("/list-all-vehicles")
    @Transactional
    public ResponseEntity<Object> listVehicles(@Valid @RequestParam Map<String, Object> vehicleFilterRequest,
                                               @RequestParam(defaultValue = "0") Integer page,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               @RequestParam(defaultValue = "id") String sort) {
        try {
            VehicleFilter vehicleFilter = modelMapper.map(vehicleFilterRequest, VehicleFilter.class);
            Pageable pageRequest = PageRequest.of(page, size, Sort.by(sort));
            Page<Vehicle> vehiclePage = vehicleService.findAll(vehicleFilter, pageRequest);

            return ResponseMapper.map(SUCCESS, HttpStatus.OK, vehiclePage, RECORDS_RECEIVED);
        } catch (PropertyReferenceException e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());
        } catch (Exception ex) {
            log.error(ERROR_OCCURRED, ex.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.INTERNAL_SERVER_ERROR, null, SERVER_ERROR);
        }
    }

    @GetMapping("/list-all-vehicles/{id}")
    @Transactional
    public ResponseEntity<Object> vehicleData(@PathVariable Long id) {
        try {
            Vehicle vehicle = vehicleService.getVehicleById(id);
            if (vehicle == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "VEHICLE NOT FOUND!");
            }
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, vehicle, RECORDS_RECEIVED);
        } catch (PropertyReferenceException e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());
        } catch (Exception ex) {
            log.error(ERROR_OCCURRED, ex.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.INTERNAL_SERVER_ERROR, null, SERVER_ERROR);
        }
    }

    @GetMapping("/list-all-vehicles/{vehicleId}/confidential-data")
    @Transactional
    public ResponseEntity<Object> vehicleConfidentialData(@PathVariable Long vehicleId) {
        try {
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            if (vehicle == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "VEHICLE NOT FOUND!");
            }
            List<byte[]> confidentialFiles = vehicleService.getVehicleConfidentialFiles(vehicleId);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);

            for (int i = 0; i < confidentialFiles.size(); i++) {
                ZipEntry entry = new ZipEntry("file" + (i + 1) + ".png");
                entry.setSize(confidentialFiles.get(i).length);
                zos.putNextEntry(entry);
                zos.write(confidentialFiles.get(i));
                zos.closeEntry();
            }
            zos.close();

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=lender_files.zip").body(baos.toByteArray());
        } catch (Exception e) {
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
    }

    @GetMapping("/list-all-users")
    @Transactional
    public ResponseEntity<Object> listUsers(@Valid @RequestParam Map<String, Object> userFilterRequest,
                                            @RequestParam(defaultValue = "0") Integer page,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestParam(defaultValue = "id") String sort) {
        try {
            UserFilter userFilter = modelMapper.map(userFilterRequest, UserFilter.class);
            Pageable pageRequest = PageRequest.of(page, size, Sort.by(sort));
            Page<User> usersePage = userService.listUsers(userFilter, pageRequest);

            return ResponseMapper.map(SUCCESS, HttpStatus.OK, usersePage, RECORDS_RECEIVED);
        } catch (Exception e) {
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
    }

    @GetMapping("/list-all-users/{userId}/user-data")
    @Transactional
    public ResponseEntity<Object> getUserData(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, user, RECORDS_RECEIVED);
        } catch (Exception e) {
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
    }

    @GetMapping("/list-all-users/{userId}/user-ban-appeal")
    @Transactional
    public ResponseEntity<Object> getUserBanAppeal(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            BannedUsersAppealing appeal = user.getBannedUsersAppealing();
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, appeal, RECORDS_RECEIVED);
        } catch (Exception e) {
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
    }

    @GetMapping("/list-all-users/{userId}/appeal-support-evidence")
    @Transactional
    public ResponseEntity<Object> getUserBanAppealEvidence(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            BannedUsersAppealing appeal = user.getBannedUsersAppealing();
            List<byte[]> supportEvidence = bannedUsersAppealingService.getUserConfidentialFiles(appeal.getId());
            if (supportEvidence.size() == 0) {
                return ResponseMapper.map(SUCCESS, HttpStatus.OK, null, "User has not uploaded support files!");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);

            for (int i = 0; i < supportEvidence.size(); i++) {
                ZipEntry entry = new ZipEntry("file" + (i + 1) + ".png");
                entry.setSize(supportEvidence.get(i).length);
                zos.putNextEntry(entry);
                zos.write(supportEvidence.get(i));
                zos.closeEntry();
            }
            zos.close();

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + user.getFirstname() + "_" + user.getLastname() + " appeal_files.zip").body(baos.toByteArray());
        } catch (Exception e) {
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
    }

    @GetMapping("/list-all-users/{username}/confidential-files")
    @Transactional
    public ResponseEntity<Object> getUserConfidentialFiles(@PathVariable String username) {
        try {
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            List<byte[]> confidentialFiles = userService.getUserConfidentialFiles(username);
            if (confidentialFiles.size() == 0) {
                return ResponseMapper.map(SUCCESS, HttpStatus.OK, null, "User has not uploaded confidential files!");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);
            for (int i = 0; i < confidentialFiles.size(); i++) {
                ZipEntry entry = new ZipEntry("file" + (i + 1) + ".png");
                entry.setSize(confidentialFiles.get(i).length);
                zos.putNextEntry(entry);
                zos.write(confidentialFiles.get(i));
                zos.closeEntry();
            }
            zos.close();

            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=lender_files.zip").body(baos.toByteArray());
        } catch (Exception e) {
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
    }

    @PostMapping("/list-all-users/{username}/user-status-management")
    @Transactional
    public ResponseEntity<Object> ManageUserStatuses(@PathVariable String username, @RequestBody StatusDTO StatusDTO) {
        try {
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            DataPool newStatus = dataPoolService.getDataPoolById(StatusDTO.getStatusId());
            if (newStatus == null || !newStatus.getEnumName().matches("UserStatus")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            if (newStatus.getEnumLabel().matches("BannedUser")) {
                Set<Role> roles = new HashSet<>();
                roles.add(Role.BANNED_USER);
                user.setRoles(roles);
                DataPool Lenderstatus = dataPoolService.findByEnumLabel("BannedLender");
                if (user.getLenderProfile() != null) {
                    Lender lenderProfile = user.getLenderProfile();
                    lenderProfile.setStatus(Lenderstatus);
                    lenderService.save(lenderProfile);
                }
                if (user.getRenterProfile() != null) {
                    DataPool renterStatus = dataPoolService.findByEnumLabel("BannedRenter");
                    Renter renterProfile = user.getRenterProfile();
                    renterProfile.setStatus(renterStatus);
                    renterService.save(renterProfile);
                }
                user.setUserStatus(newStatus);
                adminService.saverUser(user, username);
                return ResponseMapper.map(SUCCESS, HttpStatus.OK, user, RECORD_UPDATED);
            }
            if (newStatus.getEnumLabel().matches("VerifiedUser")) {
                Set<Role> userRoles = new HashSet<>();
                userRoles.add(Role.USER);
                user.setRoles(userRoles);
                user.setUserStatus(newStatus);
                if (user.getLenderProfile() != null && user.getUserStatus().getEnumLabel().matches("BannedUser")) {
                    DataPool Lenderstatus = dataPoolService.findByEnumLabel("unconfirmedLender");
                    Lender lenderProfile = user.getLenderProfile();
                    lenderProfile.setStatus(Lenderstatus);
                    lenderService.save(lenderProfile);
                }
                if (user.getRenterProfile() != null) {
                    DataPool renterStatus = dataPoolService.findByEnumLabel("unconfirmedRenter");
                    Renter renterProfile = user.getRenterProfile();
                    renterProfile.setStatus(renterStatus);
                    renterService.save(renterProfile);
                }
                user.setUserStatus(newStatus);
                adminService.saverUser(user, username);
                return ResponseMapper.map(SUCCESS, HttpStatus.OK, user, RECORD_UPDATED);
            }
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, null, "USER STATUS REMAINS THE SAME!");

        } catch (Exception e) {
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
    }

    @PostMapping("/list-all-users/{username}/lender-status-management")
    @Transactional
    public ResponseEntity<Object> ManageLenderStatuses(@PathVariable String username, @RequestBody @Valid StatusDTO StatusDTO) {
        try {
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            if (user.getUserStatus().getEnumLabel().matches("unconfirmedUser")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NEED TO BE VERIFIED FIRST!");
            }
            if (user.getUserStatus().getEnumLabel().matches("BannedUser") || user.getUserStatus().getEnumLabel().matches("BanAppealingUser")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER IS CURRENTLY BANNED!");
            }
            DataPool newStatus = dataPoolService.getDataPoolById(StatusDTO.getStatusId());
            if (newStatus == null || !newStatus.getEnumName().matches("LenderStatus")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Notification notification = new Notification();
            notification.setIsRead(false);
            LocalDateTime localDateTime = LocalDateTime.now();
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
            Instant instant = zonedDateTime.toInstant();
            notification.setDateReceived(Date.from(instant));
            notification.setCorresponingUser(user);
            Set<Role> activeRoles = user.getRoles();
            if (newStatus.getEnumLabel().matches("VerifiedLender")) {
                if (user.getLenderProfile() == null) {
                    Lender lender = new Lender();
                    lender.setStatus(newStatus);
                    lender.setUser(user);
                    lenderService.save(lender);
                } else {
                    Lender lender = user.getLenderProfile();
                    lender.setStatus(newStatus);
                    lender.setUser(user);
                    lenderService.update(lender, lender.getId());

                }
                if (!activeRoles.contains(Role.LENDER)) {
                    activeRoles.add(Role.LENDER);
                    user.setRoles(activeRoles);
                    adminService.saverUser(user, username);
                }
                notification.setMessage("Your lender profile application is approved!");
                notificationService.save(notification);
                return ResponseMapper.map(SUCCESS, HttpStatus.OK, user, "USER LENDER PROFILE IS IN CONFIRMED STATUS!");
            } else if (newStatus.getEnumLabel().matches("BannedLender")) {
                if (user.getLenderProfile() == null) {
                    Lender lender = new Lender();
                    lender.setStatus(newStatus);
                    lender.setUser(user);
                    lenderService.save(lender);
                } else {
                    Lender lender = user.getLenderProfile();
                    lender.setStatus(newStatus);
                    lender.setUser(user);
                    lenderService.update(lender, lender.getId());

                }
                if (activeRoles.contains(Role.LENDER)) {
                    activeRoles.remove(Role.LENDER);
                    user.setRoles(activeRoles);
                    adminService.saverUser(user, username);
                }
                notification.setMessage("Your lender profile is banned! If you think this was a mistake you can appeal your status!");
                notificationService.save(notification);
                return ResponseMapper.map(SUCCESS, HttpStatus.OK, user, "USER LENDER PROFILE IS IN BANNED STATUS!");
            } else if (newStatus.getEnumLabel().matches("unconfirmedLender")) {
                if (user.getLenderProfile() == null) {
                    Lender lender = new Lender();
                    lender.setStatus(newStatus);
                    lender.setUser(user);
                    lenderService.save(lender);
                } else {
                    Lender lender = user.getLenderProfile();
                    lender.setStatus(newStatus);
                    lender.setUser(user);
                    lenderService.update(lender, lender.getId());

                }
                if (activeRoles.contains(Role.LENDER)) {
                    activeRoles.remove(Role.LENDER);
                    user.setRoles(activeRoles);
                    adminService.saverUser(user, username);
                }
                if (user.getLenderProfile().getStatus().getEnumLabel().matches("BannedLender")) {
                    notification.setMessage("Your lender profile is no longer banned!Apply for confirmation in order to gain profile privileges!");
                } else {
                    notification.setMessage("Your lender profile application is not accepted!Contact us via email in order to get more information!");
                }
                notificationService.save(notification);
                return ResponseMapper.map(SUCCESS, HttpStatus.OK, user, "LENDER STATUS IS NO LONGER BANNED BUT NEED RECONFIRMATION!");
            } else {
                return ResponseMapper.map(SUCCESS, HttpStatus.OK, user, "LENDER STATUS REMAINS UNCHANGED!");
            }
        } catch (Exception e) {
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
    }

    @PostMapping("/list-all-vehicles/{vehicleId}/vehicle-status-management")
    @Transactional
    public ResponseEntity<Object> ManageVehicleStatuses(@PathVariable Long vehicleId, @RequestBody @Valid StatusDTO StatusDTO) {
        try {
            DataPool newStatus = dataPoolService.getDataPoolById(StatusDTO.getStatusId());
            if (newStatus == null || !newStatus.getEnumName().matches("VehicleStatus")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            if (vehicle == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Vehicle not found!");
            }
            Notification notification = new Notification();
            notification.setIsRead(false);
            LocalDateTime localDateTime = LocalDateTime.now();
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
            Instant instant = zonedDateTime.toInstant();
            notification.setDateReceived(Date.from(instant));
            notification.setCorresponingUser(vehicle.getLender().getUser());
            if (newStatus.getEnumLabel().matches("VerifiedVehicle")) {
                notification.setMessage("Your vehicle with plate no:" + vehicle.getPlateNo() + " is verified!");
            } else if (newStatus.getEnumLabel().matches("BannedVehicle") && vehicle.getStatus().getEnumLabel().matches("BanAppealingVehicle")) {
                notification.setMessage("Your ban appeal for vehicle with plate no:" + vehicle.getPlateNo() + " is refused!");
                vehicle.setAvailable(false);
            } else if (newStatus.getEnumLabel().matches("BannedVehicle") && !vehicle.getStatus().getEnumLabel().matches("BanAppealingVehicle")) {
                notification.setMessage("Your vehicle with plate no:" + vehicle.getPlateNo() + " is banned!");
            } else if (newStatus.getEnumLabel().matches("unconfirmedVehicle") && !(vehicle.getStatus().getEnumLabel().matches("BanAppealingVehicle") ||vehicle.getStatus().getEnumLabel().matches("BannedVehicle")) ) {
                notification.setMessage("Your vehicle with plate no:" + vehicle.getPlateNo() + " is not approved!Reupload correct vehicle documents!");
            } else if (newStatus.getEnumLabel().matches("unconfirmedVehicle") && (vehicle.getStatus().getEnumLabel().matches("BanAppealingVehicle")||vehicle.getStatus().getEnumLabel().matches("BannedVehicle"))) {
                notification.setMessage("Your vehicle with plate no:" + vehicle.getPlateNo() + " is no longer banned! Apply for verification to proceed further !");
            }
            notificationService.save(notification);
            vehicle.setStatus(newStatus);
            vehicleService.update(vehicle, vehicleId);
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, newStatus.getEnumLabel(), "Vehicle status set!");
        } catch (Exception e) {
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
    }


    @PostMapping("/list-all-users/{username}/renter-status-management")
    @Transactional
    public ResponseEntity<Object> ManageRenterStatuses(@PathVariable String username, @RequestBody @Valid StatusDTO StatusDTO) {
        try {
            User user = userService.getUserByUsername(username);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            if (user.getUserStatus().getEnumLabel().matches("unconfirmedUser")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NEED TO BE VERIFIED FIRST!");
            }
            if (user.getUserStatus().getEnumLabel().matches("BannedUser") || user.getUserStatus().getEnumLabel().matches("BanAppealingUser")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER IS CURRENTLY BANNED!");
            }
            DataPool newStatus = dataPoolService.getDataPoolById(StatusDTO.getStatusId());
            if (newStatus == null || !newStatus.getEnumName().matches("RenterStatus")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Set<Role> activeRoles = user.getRoles();
            Notification notification = new Notification();
            notification.setIsRead(false);
            LocalDateTime localDateTime = LocalDateTime.now();
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
            Instant instant = zonedDateTime.toInstant();
            notification.setDateReceived(Date.from(instant));
            notification.setCorresponingUser(user);
            if (newStatus.getEnumLabel().matches("VerifiedRenter")) {
                if (user.getRenterProfile() == null) {
                    Renter renter = new Renter();
                    renter.setStatus(newStatus);
                    renter.setUser(user);
                    renterService.save(renter);
                } else {
                    Renter renter = user.getRenterProfile();
                    renter.setStatus(newStatus);
                    renter.setUser(user);
                    renterService.update(renter, renter.getId());

                }
                if (!activeRoles.contains(Role.LENDER)) {
                    activeRoles.add(Role.LENDER);
                    user.setRoles(activeRoles);
                    adminService.saverUser(user, username);
                }
                notification.setMessage("Your renter profile application is approved!");
                notificationService.save(notification);
                return ResponseMapper.map(SUCCESS, HttpStatus.OK, user, "USER RENTER PROFILE IS IN CONFIRMED STATUS!");
            } else if (newStatus.getEnumLabel().matches("BannedRenter")) {
                if (user.getRenterProfile() == null) {
                    Renter renter = new Renter();
                    renter.setStatus(newStatus);
                    renter.setUser(user);
                    renterService.save(renter);
                } else {
                    Renter renter = user.getRenterProfile();
                    renter.setStatus(newStatus);
                    renter.setUser(user);
                    renterService.update(renter, renter.getId());

                }
                if (activeRoles.contains(Role.LENDER)) {
                    activeRoles.remove(Role.LENDER);
                    user.setRoles(activeRoles);
                    adminService.saverUser(user, username);
                }
                notification.setMessage("Your renter profile is banned! If you think this was a mistake you can appeal your status!");
                notificationService.save(notification);
                return ResponseMapper.map(SUCCESS, HttpStatus.OK, user, "USER RENTER PROFILE IS IN BANNED STATUS!");
            } else if (newStatus.getEnumLabel().matches("unconfirmedRenter")) {
                if (user.getRenterProfile() == null) {
                    Renter renter = new Renter();
                    renter.setStatus(newStatus);
                    renter.setUser(user);
                    renterService.save(renter);
                } else {
                    Renter renter = user.getRenterProfile();
                    renter.setStatus(newStatus);
                    renter.setUser(user);
                    renterService.update(renter, renter.getId());

                }
                if (activeRoles.contains(Role.LENDER)) {
                    activeRoles.remove(Role.LENDER);
                    user.setRoles(activeRoles);
                    adminService.saverUser(user, username);
                }
                if (user.getRenterProfile().getStatus().getEnumLabel().matches("BannedRenter")) {
                    notification.setMessage("Your renter profile is no longer banned!Apply for confirmation in order to gain profile privileges!");
                } else {
                    notification.setMessage("Your renter profile application is not accepted!Upload correct pictures front-back of your driver license!");
                }
                notificationService.save(notification);
                return ResponseMapper.map(SUCCESS, HttpStatus.OK, user, "RENTER STATUS IS NO LONGER BANNED BUT NEED RECONFIRMATION!");
            } else {
                return ResponseMapper.map(SUCCESS, HttpStatus.OK, user, "RENTER STATUS REMAINS UNCHANGED!");
            }
        } catch (Exception e) {
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
    }


}

