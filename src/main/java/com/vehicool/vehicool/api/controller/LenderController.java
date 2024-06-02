package com.vehicool.vehicool.api.controller;

import com.vehicool.vehicool.api.dto.RenterReviewDTO;
import com.vehicool.vehicool.api.dto.StatusDTO;
import com.vehicool.vehicool.api.dto.VehicleCommercialDTO;
import com.vehicool.vehicool.api.dto.VehicleDTO;
import com.vehicool.vehicool.business.service.*;
import com.vehicool.vehicool.persistence.entity.*;
import com.vehicool.vehicool.persistence.repository.DatabaseStorageRepository;
import com.vehicool.vehicool.security.user.User;
import com.vehicool.vehicool.security.user.UserRepository;
import com.vehicool.vehicool.util.fileconfigs.ImageUtils;
import com.vehicool.vehicool.util.mappers.ResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.vehicool.vehicool.util.constants.Messages.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/lender")
public class LenderController {
    private final ModelMapper modelMapper;
    private final LenderService lenderService;
    private final DataPoolService dataPoolService;
    private final VehicleService vehicleService;
    private final VehicleCommerceService vehicleCommerceService;
    private final ContractService contractService;
    private final RenterReviewService renterReviewService;
    private final StorageService storageService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final DatabaseStorageRepository databaseStorageRepository;

    @PostMapping("/lender-vehicles/{vehicleId}/set-commercial-data")
    @Transactional
    public ResponseEntity<Object> changeVehicleCommercialDetails(Principal connectedUser, @PathVariable Long vehicleId, @RequestBody VehicleCommercialDTO vehicleCommercialDTO) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            Lender lender = user.getLenderProfile();
            if (lender == null || !lender.getStatus().getEnumLabel().matches("VerifiedLender")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Lender not found !");
            }
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            if (vehicle == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Vehicle not found !");
            }
            if (vehicle.getVehicleCommerce() == null) {
                VehicleCommerce vehicleCommerce = modelMapper.map(vehicleCommercialDTO, VehicleCommerce.class);
                vehicleCommerce.setVehicle(vehicle);
                vehicleCommerceService.save(vehicleCommerce);
                vehicle.setAvailable(vehicleCommercialDTO.getIsAvailable());
                vehicleService.update(vehicle, vehicleId);
                return ResponseMapper.map(SUCCESS, HttpStatus.OK, vehicleCommerce, RECORDS_RECEIVED);
            } else {
                VehicleCommerce vehicleCommerce = vehicle.getVehicleCommerce();
                vehicleCommerce.setVehicle(vehicle);
                vehicleCommerce.setPricePerDay(vehicleCommerce.getPricePerDay());
                vehicleCommerce.setDateAvailable(vehicleCommercialDTO.getDateAvailable());
                vehicleCommerce.setMaxDateAvailable(vehicleCommercialDTO.getMaxDateAvailable());
                vehicleCommerceService.update(vehicleCommerce, vehicleCommerce.getId());
                vehicle.setAvailable(vehicleCommercialDTO.getIsAvailable());
                vehicleService.update(vehicle, vehicleId);
                return ResponseMapper.map(SUCCESS, HttpStatus.OK, vehicleCommerce, RECORDS_RECEIVED);
            }

        } catch (PropertyReferenceException e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());
        } catch (Exception ex) {
            log.error(ERROR_OCCURRED, ex.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.INTERNAL_SERVER_ERROR, null, SERVER_ERROR);
        }
    }

    @GetMapping("/active-contract-requests")
    @Transactional
    public ResponseEntity<Object> listContractRequests(Principal connectedUser) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            Lender lender = user.getLenderProfile();
            if (lender == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Lender not found !");
            }
            DataPool contractStatus = dataPoolService.findByEnumLabel("Waiting");
            List<Contract> contracts = lenderService.contractRequests(lender.getId(), contractStatus.getId());
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, contracts, RECORDS_RECEIVED);
        } catch (PropertyReferenceException e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());
        } catch (Exception ex) {
            log.error(ERROR_OCCURRED, ex.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.INTERNAL_SERVER_ERROR, null, SERVER_ERROR);
        }
    }

    @PostMapping("/active-contract-requests/{contractId}/proceed")
    @Transactional
    public ResponseEntity<Object> proceedContractRequests(Principal connectedUser, @PathVariable Long contractId, @RequestBody StatusDTO statusDTO) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null || !user.getUserStatus().getEnumLabel().matches("VerifiedUser")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            Lender lender = user.getLenderProfile();
            if (lender == null || !lender.getStatus().getEnumLabel().matches("VerifiedLender")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Contract contract = contractService.getContractById(contractId);
            if (contract == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Contract request not found !");
            }
            DataPool status = dataPoolService.getDataPoolById(statusDTO.getStatusId());
            if (status == null || !status.getEnumName().matches("ContractualStatus")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Status not found !");
            }
            if (!lender.getStatus().getEnumLabel().matches("VerifiedLender")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Lender is not verified");
            }
            contract.setContractualStatus(status);
            contractService.update(contract, contractId);

            //Create notification object for the renter
            Notification notification = new Notification();
            notification.setCorresponingUser(contract.getRenter().getUser());
            notification.setIsRead(false);
            LocalDateTime localDateTime = LocalDateTime.now();
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
            Instant instant = zonedDateTime.toInstant();
            notification.setDateReceived(Date.from(instant));
            if (status.getEnumLabel().matches("Accepted")) {
                notification.setMessage(lender.getUser().getFirstname() + " " + lender.getUser().getLastname() + " has accepted your renting request for his " + contract.getVehicle().getBrand() + " " + contract.getVehicle().getModel());
            } else {
                notification.setMessage(lender.getUser().getFirstname() + " " + lender.getUser().getLastname() + " has refused your renting request for his " + contract.getVehicle().getBrand() + " " + contract.getVehicle().getModel());
            }
            notificationService.save(notification);

            return ResponseMapper.map(SUCCESS, HttpStatus.OK, contract, "Contract status updated !");
        } catch (PropertyReferenceException e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());
        } catch (Exception ex) {
            log.error(ERROR_OCCURRED, ex.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.INTERNAL_SERVER_ERROR, null, SERVER_ERROR);
        }
    }

    @GetMapping("/active-contract-requests/{contractId}")
    @Transactional
    public ResponseEntity<Object> viewContractRequest(Principal connectedUser, @PathVariable Long contractId, @RequestBody StatusDTO statusDTO) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null || !user.getUserStatus().getEnumLabel().matches("VerifiedUser")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            Lender lender = user.getLenderProfile();
            if (lender == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Contract contract = contractService.getContractById(contractId);
            if (contract == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Contract request not found !");
            }
            contract = contractService.getContractById(contractId);
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, contract, RECORDS_RECEIVED);
        } catch (PropertyReferenceException e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());
        } catch (Exception ex) {
            log.error(ERROR_OCCURRED, ex.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.INTERNAL_SERVER_ERROR, null, SERVER_ERROR);
        }
    }

    @GetMapping("/lender-vehicles")
    @Transactional
    public ResponseEntity<Object> listVehicles(Principal connectedUser) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null || !user.getUserStatus().getEnumLabel().matches("VerifiedUser")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            Lender lender = user.getLenderProfile();
            if (lender == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, lender.getVehicles(), RECORDS_RECEIVED);
        } catch (PropertyReferenceException e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());
        } catch (Exception ex) {
            log.error(ERROR_OCCURRED, ex.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.INTERNAL_SERVER_ERROR, null, SERVER_ERROR);
        }
    }

    @PostMapping("/post-a-vehicle")
    @Transactional
    public ResponseEntity<Object> createVehicle(Principal connectedUser, @RequestBody @Valid VehicleDTO vehicleDTO) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null || !user.getUserStatus().getEnumLabel().matches("VerifiedUser")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            Lender lender = user.getLenderProfile();
            if (lender == null || !lender.getStatus().getEnumLabel().matches("VerifiedLender")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED + " RELATED TO LENDER PROFILE!");
            }
            DataPool locaton = dataPoolService.getDataPoolById(vehicleDTO.getCityId());
            if (locaton == null || !locaton.getEnumName().matches("location")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Location not found/exists !");
            }
            DataPool transmission = dataPoolService.getDataPoolById(vehicleDTO.getTransmissionTypeId());
            if (transmission == null || !transmission.getEnumName().matches("transmissionType")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Transmission type not found/exists !");
            }
            DataPool vehicleType = dataPoolService.getDataPoolById(vehicleDTO.getVehicleTypeId());
            if (vehicleType == null || !vehicleType.getEnumName().matches("vehicleType")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Vehicle type type not found/exists !");
            }

            DataPool engineType = dataPoolService.getDataPoolById(vehicleDTO.getEngineTypeId());
            if (engineType == null || !engineType.getEnumName().matches("engineType")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Engine type type not found/exists !");
            }
            Vehicle vehicle = modelMapper.map(vehicleDTO, Vehicle.class);
            vehicle.setLender(lender);
            vehicle.setPlateNo(vehicleDTO.getPlateNo());
            vehicle.setLocation(locaton);
            vehicle.setEngineType(engineType);
            vehicle.setVehicleType(vehicleType);
            vehicle.setTransmissionType(transmission);
            vehicle.setAvailable(false);
            vehicle.setStatus(dataPoolService.findByEnumLabel("unconfirmedVehicle"));
            vehicleService.save(vehicle);
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, vehicle, RECORD_CREATED);
        } catch (Exception e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());

        }
    }

    @PostMapping(value = "/lender-vehicles/{vehicleId}/verify-vehicle", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Object> verifyVehicle(@RequestParam("documentations") List<MultipartFile> files, Principal connectedUser, @PathVariable Long vehicleId) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null || !user.getUserStatus().getEnumLabel().matches("VerifiedUser")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            Lender lender = user.getLenderProfile();
            if (lender == null || !lender.getStatus().getEnumLabel().matches("VerifiedLender")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED + " RELATED TO LENDER PROFILE!");
            }
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            if (vehicle == null || lender.getStatus().getEnumLabel().matches("BannedVehicle")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED + " RELATED TO VEHICLE!");
            }
            List<ConfidentialFile> confidentialFiles = new ArrayList<>();
            for (MultipartFile file : files) {
                confidentialFiles.add(ConfidentialFile.builder()
                        .name(file.getOriginalFilename())
                        .vehicle(vehicle)
                        .type(file.getContentType())
                        .imageData(ImageUtils.compressImage(file.getBytes())).build());
            }
            databaseStorageRepository.saveAll(confidentialFiles);
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, "Vehicle FIles uploaded successfuly!", RECORDS_RECEIVED);
        } catch (PropertyReferenceException e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());
        } catch (Exception ex) {
            log.error(ERROR_OCCURRED, ex.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.INTERNAL_SERVER_ERROR, null, SERVER_ERROR);
        }
    }

    @DeleteMapping("/lender-vehicles/{vehicleId}/delete-vehicle")
    @Transactional
    public ResponseEntity<Object> deleteVehicle(Principal connectedUser, @PathVariable Long vehicleId) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null || !user.getUserStatus().getEnumLabel().matches("VerifiedUser")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            Lender lender = user.getLenderProfile();
            if (lender == null || !lender.getStatus().getEnumLabel().matches("VerifiedLender")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            if (vehicle != null) {
                DataPool status = dataPoolService.findByEnumLabel("UserRemovedVehicle");
                vehicle.setStatus(status);
                vehicleService.update(vehicle, vehicleId);
            }
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, vehicle, "Vehicle deleted successfuly !");
        } catch (Exception e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());


        }
    }

    @PutMapping("/lender-vehicles/{vehicleId}/update-vehicle")
    @Transactional
    public ResponseEntity<Object> updateVehicle(Principal connectedUser, @PathVariable Long
            vehicleId, @RequestBody @Valid VehicleDTO vehicleDTO) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null || !user.getUserStatus().getEnumLabel().matches("VerifiedUser")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            Lender lender = user.getLenderProfile();
            if (lender == null || !lender.getStatus().getEnumLabel().matches("VerifiedLender")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            if (vehicle == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Vehicle not found !");
            }
            modelMapper.map(vehicleDTO, vehicle);
            vehicleService.update(vehicle, vehicleId);
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, vehicle, "Vehicle updated successfuly !");
        } catch (Exception e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());

        }
    }

    @GetMapping("/contract-history")
    public ResponseEntity<Object> getContractsHistory(Principal connectedUser) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            Lender lender = user.getLenderProfile();
            if (lender == null || !lender.getStatus().getEnumLabel().matches("VerifiedLender")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            List<Contract> contracts = lender.getContractSigned();
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, contracts, RECORDS_RECEIVED);
        } catch (Exception e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());

        }
    }

    @GetMapping("/contract-history/{contractId}")
    public ResponseEntity<Object> getContract(Principal connectedUser, @PathVariable Long contractId) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null || !user.getUserStatus().getEnumLabel().matches("VerifiedUser")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            Lender lender = user.getLenderProfile();
            if (lender == null || !lender.getStatus().getEnumLabel().matches("VerifiedLender")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Contract contract = contractService.getContractById(contractId);
            if (contract == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Contract not found!");
            }
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, contract, RECORDS_RECEIVED);
        } catch (Exception e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());

        }
    }

    @PostMapping("/contract-history/{contractId}/review-renter")
    public ResponseEntity<Object> reviewRenter(Principal connectedUser, @PathVariable Long
            contractId, @RequestBody RenterReviewDTO renterReviewDTO) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null || !user.getUserStatus().getEnumLabel().matches("VerifiedUser")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            Lender lender = user.getLenderProfile();
            if (lender == null || !lender.getStatus().getEnumLabel().matches("VerifiedLender")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Contract contract = contractService.getContractById(contractId);
            if (contract == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Contract not found!");
            }
            Renter renter = contract.getRenter();
            RenterReview renterReview = modelMapper.map(renterReviewDTO, RenterReview.class);
            renterReview.setRenter(renter);
            renterReview.setLender(lender);
            renterReviewService.save(renterReview);
            //Create notification object for the renter
            Notification notification = new Notification();
            notification.setMessage("A user has reviewed you of " + renterReview.getRating() + "/5");
            notification.setCorresponingUser(contract.getLender().getUser());
            notification.setIsRead(false);
            LocalDateTime localDateTime = LocalDateTime.now();
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
            Instant instant = zonedDateTime.toInstant();
            notification.setDateReceived(Date.from(instant));
            notificationService.save(notification);
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, renterReview, RECORDS_RECEIVED);
        } catch (Exception e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());

        }
    }

    @PostMapping(value = "/lender-vehicles/{vehicleId}/upload-vehicle-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Object> uploadVehicleImages
            (@RequestParam("image") List<MultipartFile> files, Principal connectedUser, @PathVariable Long vehicleId) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null || !user.getUserStatus().getEnumLabel().matches("VerifiedUser")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            Lender lender = user.getLenderProfile();
            if (lender == null || !lender.getStatus().getEnumLabel().matches("VerifiedLender")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            if (vehicle == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Vehicle not found !");
            }
            String result = vehicleService.uploadVehicleImages(files, vehicleId);
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, result, "Vehicle pictures uploaded successfuly !");
        } catch (Exception e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());


        }
    }

    @GetMapping(value = "/lender-vehicles/{vehicleId}/imagesIds")
    @Transactional
    public ResponseEntity<Object> vehicleImagesIds(Principal connectedUser, @PathVariable Long vehicleId) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null || !user.getUserStatus().getEnumLabel().matches("VerifiedUser")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            Lender lender = user.getLenderProfile();
            if (lender == null || !lender.getStatus().getEnumLabel().matches("VerifiedLender")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            if (vehicle == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Vehicle not found !");
            }
            List<Long> imgIds = vehicle.getImages().stream().map(img -> img.getId()).toList();
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, imgIds, "Ids retrieved!");
        } catch (Exception e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
    }

    @GetMapping(value = "/lender-vehicles/{vehicleId}")
    public ResponseEntity<Object> getVehicledetails(Principal connectedUser, @PathVariable Long vehicleId) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, vehicle, RECORD_CREATED);
        } catch (Exception e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());

        }
    }

    @DeleteMapping(value = "/lender-vehicles/{vehicleId}/deleteImage/{imageId}")
    @Transactional
    public ResponseEntity<Object> vehicleImagesIds(Principal connectedUser, @PathVariable Long
            vehicleId, @PathVariable Long imageId) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null || !user.getUserStatus().getEnumLabel().matches("VerifiedUser")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            Lender lender = user.getLenderProfile();
            if (lender == null || !lender.getStatus().getEnumLabel().matches("VerifiedLender")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            if (vehicle == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Vehicle not found !");
            }
            FileData fileData = storageService.getStorageFileById(imageId);
            if (fileData != null) {
                storageService.deleteStorageFile(imageId);
            }
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, null, "Image deleted successfully !");
        } catch (Exception e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
    }

    @PostMapping(value = "/lender-vehicles/{vehicleId}/set-profile-image/{imageId}")
    @Transactional
    public ResponseEntity<Object> setProfileImage(Principal connectedUser, @PathVariable Long
            vehicleId, @PathVariable Long imageId) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null || !user.getUserStatus().getEnumLabel().matches("VerifiedUser")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            Lender lender = user.getLenderProfile();
            if (lender == null || !lender.getStatus().getEnumLabel().matches("VerifiedLender")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            if (vehicle == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Vehicle not found !");
            }
            FileData fileData = storageService.getStorageFileById(imageId);
            if (fileData == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Image not found !");
            }
            List<FileData> images = vehicle.getImages();
            images.stream().forEach(elem -> elem.setIsProfileImage(false));
            images.stream().filter(elem -> elem.getId() == imageId).findFirst().get().setIsProfileImage(true);
            storageService.saveAllImages(images);
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, null, "Image deleted successfully !");
        } catch (Exception e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());
        }
    }
}
