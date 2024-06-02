package com.vehicool.vehicool.security.user;

import com.vehicool.vehicool.api.dto.AppealDTO;
import com.vehicool.vehicool.business.service.*;
import com.vehicool.vehicool.persistence.entity.*;
import com.vehicool.vehicool.persistence.repository.DatabaseStorageRepository;
import com.vehicool.vehicool.util.fileconfigs.ImageUtils;
import com.vehicool.vehicool.util.mappers.ResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static com.vehicool.vehicool.util.constants.Messages.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final DataPoolService dataPoolService;
    private final UserRepository userRepository;
    private final RenterService renterService;
    private final LenderService lenderService;
    private final NotificationService notificationService;
    private final StorageService storageService;
    private final DatabaseStorageRepository databaseStorageRepository;
    private final BannedUsersAppealingService bannedUsersAppealingService;

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request, Principal connectedUser) {
        service.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-details")
    public ResponseEntity<?> getDetails(Principal connectedUser) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, user, RECORDS_RECEIVED);
        } catch (Exception e) {
            return ResponseMapper.map(FAIL, HttpStatus.INTERNAL_SERVER_ERROR, null, ERROR_OCCURRED);
        }
    }

    @PostMapping(value = "/ban-appeal", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<?> banAppeal(Principal connectedUser, @Valid AppealDTO appealDTO) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            if (user.getBannedUsersAppealing() != null && user.getBannedUsersAppealing().getStatus().getEnumLabel().matches("BanAppealingUser")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "YOU HAVE AN ACTIVE APPEAL!");
            }
            BannedUsersAppealing appeal = new BannedUsersAppealing();
            appeal.setUser(user);
            appeal.setDescription(appealDTO.getDescription());
            List<ConfidentialFile> supportFiles = new ArrayList<>();
            for (MultipartFile file : appealDTO.getSupportFiles()) {
                supportFiles.add(ConfidentialFile.builder()
                        .name(file.getOriginalFilename())
                        .userBanAppeal(appeal)
                        .type(file.getContentType())
                        .imageData(ImageUtils.compressImage(file.getBytes())).build());
            }
            if (user.getBannedUsersAppealing() != null && user.getBannedUsersAppealing().getStatus().getEnumLabel().matches("BannedUser")) {
                appeal.setStatus(dataPoolService.findByEnumLabel("Pending-repetition"));
                bannedUsersAppealingService.updateAppeal(appeal, user.getBannedUsersAppealing().getId());
            } else {
                appeal.setStatus(dataPoolService.findByEnumLabel("Pending-active"));
                bannedUsersAppealingService.saveAppeal(appeal);
            }
            databaseStorageRepository.saveAll(supportFiles);
            user.setUserStatus(dataPoolService.findByEnumLabel("BanAppealingUser"));
            service.updateUser(user, user.getUsername());
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, user, RECORDS_RECEIVED);
        } catch (Exception e) {
            return ResponseMapper.map(FAIL, HttpStatus.INTERNAL_SERVER_ERROR, null, ERROR_OCCURRED);
        }
    }

    @PostMapping("/user-renter-profile-application")
    public ResponseEntity<?> renterProfileApplication(Principal connectedUser) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            if (user.getRenterProfile()==null) {
                Renter renter = new Renter();
                renter.setUser(user);
                renter.setStatus(dataPoolService.findByEnumLabel("unconfirmedRenter"));
                renterService.save(renter);
                user.setRenterProfile(renter);
                userRepository.save(user);
                return ResponseMapper.map(SUCCESS, HttpStatus.OK, renter, RECORDS_RECEIVED);
            }
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "User have already a renter profile!");
        } catch (Exception e) {
            return ResponseMapper.map(FAIL, HttpStatus.INTERNAL_SERVER_ERROR, null, SERVER_ERROR);
        }
    }

    @PostMapping("/user-lender-profile-application")
    public ResponseEntity<?> lenderProfileApplication(Principal connectedUser) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            if (!user.getUserStatus().getEnumLabel().matches("VerifiedUser")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT VERIFIED!");
            }
            if (user.getLenderProfile()==null) {
                Lender lender = new Lender();
                lender.setUser(user);
                lender.setStatus(dataPoolService.findByEnumLabel("unconfirmedLender"));
                lenderService.save(lender);
                user.setLenderProfile(lender);
                userRepository.save(user);
                return ResponseMapper.map(SUCCESS, HttpStatus.OK, lender, RECORDS_RECEIVED);
            }
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "User have already a lender profile!");
        } catch (Exception e) {
            return ResponseMapper.map(FAIL, HttpStatus.INTERNAL_SERVER_ERROR, null, ERROR_OCCURRED);
        }
    }

    @PostMapping(value = "/user-verification-application", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> userVerificationApplication(Principal connectedUser, @RequestParam("image") List<MultipartFile> files) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null || user.getUserStatus().getEnumLabel().matches("BannedUser")|| user.getUserStatus().getEnumLabel().matches("BanAppealingUser")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND OR IS BANNED!");
            }
            List<ConfidentialFile> confidentialFiles = new ArrayList<>();
            for (MultipartFile file : files) {
                confidentialFiles.add(ConfidentialFile.builder()
                        .name(file.getOriginalFilename())
                        .user(user)
                        .type(file.getContentType())
                        .imageData(ImageUtils.compressImage(file.getBytes())).build());
            }
            databaseStorageRepository.saveAll(confidentialFiles);
            String message = service.uploadConfidentialFile(files, user.getUsername());
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, message, RECORDS_RECEIVED);
        } catch (Exception e) {
            return ResponseMapper.map(FAIL, HttpStatus.INTERNAL_SERVER_ERROR, null, ERROR_OCCURRED);
        }
    }

    @GetMapping("/notifications")
    public ResponseEntity<?> notifications(Principal connectedUser) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, user.getNotifications(), RECORDS_RECEIVED);
        } catch (Exception e) {
            return ResponseMapper.map(FAIL, HttpStatus.INTERNAL_SERVER_ERROR, null, ERROR_OCCURRED);
        }
    }

    @GetMapping("/notifications/{notificationId}")
    public ResponseEntity<?> notificationById(Principal connectedUser, @PathVariable Long notificationId) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            Notification notification = notificationService.getById(notificationId);
            notification.setIsRead(true);
            notificationService.update(notification, notificationId);
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, notification, RECORDS_RECEIVED);
        } catch (Exception e) {
            return ResponseMapper.map(FAIL, HttpStatus.INTERNAL_SERVER_ERROR, null, ERROR_OCCURRED);
        }
    }
}
