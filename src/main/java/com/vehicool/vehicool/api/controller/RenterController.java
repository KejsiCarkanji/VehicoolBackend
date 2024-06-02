package com.vehicool.vehicool.api.controller;

import com.vehicool.vehicool.api.dto.ContractDataDTO;
import com.vehicool.vehicool.api.dto.LenderReviewDTO;
import com.vehicool.vehicool.api.dto.StatusDTO;
import com.vehicool.vehicool.api.dto.VehicleReviewDTO;
import com.vehicool.vehicool.business.service.*;
import com.vehicool.vehicool.persistence.entity.*;
import com.vehicool.vehicool.security.user.User;
import com.vehicool.vehicool.security.user.UserRepository;
import com.vehicool.vehicool.util.mappers.ResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.List;
import static com.vehicool.vehicool.util.constants.Messages.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/renter")
public class RenterController {
    private final ModelMapper modelMapper;
    private final RenterService renterService;
    private final DataPoolService dataPoolService;
    private final VehicleService vehicleService;
    private final ContractService contractService;
    private final LenderReviewService lenderReviewService;
    private final VehicleReviewService vehicleReviewService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final StorageService storageService;


    @Transactional
    @PostMapping("/rentApply/{vehicleId}")
    public ResponseEntity<Object> rent(@RequestBody ContractDataDTO contractDTO, @PathVariable Long vehicleId, Principal connectedUser) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            if (user.getRenterProfile() == null || !user.getRenterProfile().getStatus().getEnumLabel().matches("VerifiedRenter")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            if (vehicle == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Vehicle not found!");
            }
            if (!vehicle.getStatus().getEnumLabel().matches("VerifiedVehicle")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Vehicle not verified yet!");
            }
            if (!vehicle.getAvailable()) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Vehicle not available for renting !");
            }
            Renter renter = user.getRenterProfile();
            if (!renter.getStatus().getEnumLabel().matches("VerifiedRenter")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Renter is not verified");
            }
            Contract contract = new Contract();
            contract.setStartDate(contractDTO.getStartDate());
            contract.setEndDate(contractDTO.getEndDate());
            contract.setRenter(renter);
            contract.setLender(vehicle.getLender());
            contract.setVehicle(vehicle);
            contract.setPricePerDay(vehicle.getVehicleCommerce().getPricePerDay());
            Date startDateUtil = contractDTO.getStartDate();
            Date endDateUtil = contractDTO.getEndDate();
            LocalDate startDate = convertToLocalDateViaInstant(startDateUtil);
            LocalDate endDate = convertToLocalDateViaInstant(endDateUtil);
            long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
            contract.setTotal(daysBetween * vehicle.getVehicleCommerce().getPricePerDay());
            DataPool contractualStatus = dataPoolService.findByEnumLabel("Waiting");
            contract.setContractualStatus(contractualStatus);
            contractService.save(contract);
            //Create notification object for the vehicle lender
            Notification notification = new Notification();
            notification.setMessage(renter.getUser().getFirstname() + " " + renter.getUser().getLastname() + " has requested to rent your vehicle with license plate:" + vehicle.getPlateNo());
            notification.setCorresponingUser(vehicle.getLender().getUser());
            notification.setIsRead(false);
            LocalDateTime localDateTime = LocalDateTime.now();
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
            Instant instant = zonedDateTime.toInstant();
            notification.setDateReceived(Date.from(instant));
            notificationService.save(notification);
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, contract, RECORD_CREATED);
        } catch (Exception e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());

        }
    }

    @GetMapping("/active-contract-requests")
    public ResponseEntity<Object> getActiveContractRequests(Principal connectedUser) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            if (user.getRenterProfile() == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Renter renter = user.getRenterProfile();
            if (renter == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ENTITY_NOT_FOUND);
            }
            DataPool contractStatus = dataPoolService.findByEnumLabel("Waiting");
            List<Contract> contracts = renterService.contractRequests(renter.getId(), contractStatus.getId());
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, contracts, RECORD_CREATED);
        } catch (Exception e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());

        }
    }

    @GetMapping("/accepted-contract-requests-pending-for-reconfirmation")
    public ResponseEntity<Object> getContractRequestsPeningForReconfirmation(Principal connectedUser) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            if (user.getRenterProfile() == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Renter renter = user.getRenterProfile();
            if (renter == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ENTITY_NOT_FOUND);
            }
            DataPool contractStatus = dataPoolService.findByEnumLabel("Accepted");
            List<Contract> contracts = renterService.contractRequests(renter.getId(), contractStatus.getId());
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, contracts, RECORD_CREATED);
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
            if (user.getRenterProfile() == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Renter renter = user.getRenterProfile();
            if (renter == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ENTITY_NOT_FOUND);
            }
            List<Contract> contracts = renter.getContractSigned();
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, contracts, RECORDS_RECEIVED);
        } catch (Exception e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());

        }
    }

    @GetMapping("/accepted-contract-requests-pending-for-reconfirmation/{contractId}")
    public ResponseEntity<Object> getContractPendingForReconfirmation(Principal connectedUser, @PathVariable Long contractId) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            if (user.getRenterProfile() == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Renter renter = user.getRenterProfile();
            if (renter == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ENTITY_NOT_FOUND);
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

    @PostMapping("/accepted-contract-requests-pending-for-reconfirmation/{contractId}/procced")
    public ResponseEntity<Object> LastStepConfirmation(Principal connectedUser, @PathVariable Long contractId, StatusDTO statusDTO) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            if (user.getRenterProfile() == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Renter renter = user.getRenterProfile();
            if (renter == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ENTITY_NOT_FOUND);
            }
            Contract contract = contractService.getContractById(contractId);
            if (contract == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Contract not found!");
            }
            DataPool status = dataPoolService.getDataPoolById(statusDTO.getStatusId());
            if (!status.getEnumName().matches("ContractualStatus")) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            contract.setContractualStatus(status);
            contractService.update(contract, contractId);
            //Create notification object for the vehicle lender
            Notification notification = new Notification();
            notification.setCorresponingUser(contract.getLender().getUser());
            notification.setIsRead(false);
            LocalDateTime localDateTime = LocalDateTime.now();
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
            Instant instant = zonedDateTime.toInstant();
            notification.setDateReceived(Date.from(instant));
            if (status.getEnumLabel().matches("Active")) {
                notification.setMessage(renter.getUser().getFirstname() + " " + renter.getUser().getLastname() + " has initialized renting contract for your vehicle with license plate:" + contract.getVehicle().getPlateNo());
            } else {
                notification.setMessage(renter.getUser().getFirstname() + " " + renter.getUser().getLastname() + " has cancelled renting request for your vehicle with license plate:" + contract.getVehicle().getPlateNo());
            }
            notificationService.save(notification);
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, contract, "Contract cancelled successfully!");
        } catch (Exception e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());

        }
    }

    @GetMapping("/contract-history/{contractId}")
    public ResponseEntity<Object> getContract(Principal connectedUser, @PathVariable Long contractId) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            if (user.getRenterProfile() == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Renter renter = user.getRenterProfile();
            if (renter == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ENTITY_NOT_FOUND);
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

    @Transactional
    @GetMapping("/active-contract-requests/{contractId}/cancel-contract")
    public ResponseEntity<Object> getActiveContractRequest(Principal connectedUser, @PathVariable Long contractId) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            if (user.getRenterProfile() == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Renter renter = user.getRenterProfile();
            if (renter == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ENTITY_NOT_FOUND);
            }
            Contract contract = contractService.getContractById(contractId);
            if (contract == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Contract not found!");
            }
            DataPool status = dataPoolService.findByEnumLabel("Canceled");
            contract.setContractualStatus(dataPoolService.getDataPoolById(18l));
            contractService.update(contract, contractId);
            //Create notification object for the vehicle lender
            Notification notification = new Notification();
            notification.setMessage(renter.getUser().getFirstname() + " " + renter.getUser().getLastname() + " has cancelled his request to rent your vehicle with license plate:" + contract.getVehicle().getPlateNo());
            notification.setCorresponingUser(contract.getLender().getUser());
            notification.setIsRead(false);
            LocalDateTime localDateTime = LocalDateTime.now();
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
            Instant instant = zonedDateTime.toInstant();
            notification.setDateReceived(Date.from(instant));
            notificationService.save(notification);
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, contract, "Contract cancelled successfully!");
        } catch (Exception e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, e.getMessage());

        }
    }

    @GetMapping("/active-contract-requests/{contractId}")
    public ResponseEntity<Object> getContractRequest(Principal connectedUser, @PathVariable Long contractId) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            if (user.getRenterProfile() == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Renter renter = user.getRenterProfile();
            if (renter == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ENTITY_NOT_FOUND);
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

    @Transactional
    @PostMapping("/contract-history/{contractId}/review-lender")
    public ResponseEntity<Object> reviewRenter(Principal connectedUser, @PathVariable Long contractId, @RequestBody LenderReviewDTO lenderReviewDTO) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            if (user.getRenterProfile() == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Renter renter = user.getRenterProfile();
            if (renter == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ENTITY_NOT_FOUND);
            }
            Contract contract = contractService.getContractById(contractId);
            if (contract == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Contract not found!");
            }
            Lender lender = contract.getLender();
            LenderReview lenderReview = modelMapper.map(lenderReviewDTO, LenderReview.class);
            lenderReview.setRenter(renter);
            lenderReview.setLender(lender);
            lenderReviewService.save(lenderReview);
            //Create notification object for the vehicle lender
            Notification notification = new Notification();
            notification.setMessage(renter.getUser().getFirstname() + " " + renter.getUser().getLastname() + " has reviewed you of " + lenderReview.getRating() + "/5");
            notification.setCorresponingUser(contract.getLender().getUser());
            notification.setIsRead(false);
            LocalDateTime localDateTime = LocalDateTime.now();
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
            Instant instant = zonedDateTime.toInstant();
            notification.setDateReceived(Date.from(instant));
            notificationService.save(notification);
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, lenderReview, RECORD_CREATED);
        } catch (Exception e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.INTERNAL_SERVER_ERROR, null, e.getMessage());

        }
    }

    @Transactional
    @PostMapping("/contract-history/{contractId}/review-vehicle")
    public ResponseEntity<Object> reviewRenter(Principal connectedUser, @PathVariable Long contractId, @RequestBody VehicleReviewDTO vehicleReviewDTO) {
        try {
            User user = userRepository.findByUsername(connectedUser.getName()).orElse(null);
            if (user == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "USER NOT FOUND!");
            }
            if (user.getRenterProfile() == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ERROR_OCCURRED);
            }
            Renter renter = user.getRenterProfile();
            if (renter == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, ENTITY_NOT_FOUND);
            }
            Contract contract = contractService.getContractById(contractId);
            if (contract == null) {
                return ResponseMapper.map(FAIL, HttpStatus.BAD_REQUEST, null, "Contract not found!");
            }
            Vehicle vehicle = contract.getVehicle();
            VehicleReview vehicleReview = modelMapper.map(vehicleReviewDTO, VehicleReview.class);
            vehicleReview.setRenter(renter);
            vehicleReview.setVehicleReviewed(vehicle);
            vehicleReviewService.save(vehicleReview);
            //Create notification object for the vehicle lender
            Notification notification = new Notification();
            notification.setMessage(renter.getUser().getFirstname() + " " + renter.getUser().getLastname() + " has reviewed your vehicle with plateNo: " + vehicle.getPlateNo() + "of " + vehicleReview.getRating() + "/5");
            notification.setCorresponingUser(contract.getLender().getUser());
            notification.setIsRead(false);
            LocalDateTime localDateTime = LocalDateTime.now();
            ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
            Instant instant = zonedDateTime.toInstant();
            notification.setDateReceived(Date.from(instant));
            notificationService.save(notification);
            return ResponseMapper.map(SUCCESS, HttpStatus.OK, vehicleReview, RECORD_CREATED);
        } catch (Exception e) {
            log.error(ERROR_OCCURRED, e.getMessage());
            return ResponseMapper.map(FAIL, HttpStatus.INTERNAL_SERVER_ERROR, null, e.getMessage());

        }
    }
    private static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

}
