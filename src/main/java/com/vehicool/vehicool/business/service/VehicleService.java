package com.vehicool.vehicool.business.service;

import com.querydsl.core.types.Predicate;
import com.vehicool.vehicool.business.querydsl.VehicleFilter;
import com.vehicool.vehicool.business.querydsl.VehicleQueryDsl;
import com.vehicool.vehicool.persistence.entity.ConfidentialFile;
import com.vehicool.vehicool.persistence.entity.FileData;
import com.vehicool.vehicool.persistence.entity.Vehicle;
import com.vehicool.vehicool.persistence.repository.DatabaseStorageRepository;
import com.vehicool.vehicool.persistence.repository.SystemStorageRepository;
import com.vehicool.vehicool.persistence.repository.VehicleRepository;
import com.vehicool.vehicool.security.user.User;
import com.vehicool.vehicool.util.fileconfigs.ImageUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final SystemStorageRepository systemStorageRepository;
    private final VehicleQueryDsl vehicleQueryDsl;
    private final DatabaseStorageRepository databaseStorageRepository;
    private final String FOLDER_PATH = "C:\\Users\\ergih\\Desktop\\fileStorage\\";

    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id).orElse(null);
    }

    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public void delete(Long id) {
        vehicleRepository.deleteById(id);
    }

    public Vehicle update(Vehicle vehicle, Long Id) {
        vehicle.setId(Id);
        return vehicleRepository.saveAndFlush(vehicle);
    }

    public Page<Vehicle> findAll(VehicleFilter vehicleFilter, Pageable pageRequest) {
        Predicate filter = vehicleQueryDsl.filter(vehicleFilter);
        return vehicleRepository.findAll(filter, pageRequest);
    }

    public String uploadVehicleImages(List<MultipartFile> files, Long vehicleId) throws IOException {
        Vehicle vehicle = getVehicleById(vehicleId);
        if (vehicle == null) {
            return "Vehicle not found for the given id !";
        }
        List<FileData> fileDataList = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file == null || file.getContentType().equals("image")) {
                continue;
            }
            String filePath = FOLDER_PATH + file.getOriginalFilename();
            fileDataList.add(FileData.builder().name(file.getOriginalFilename()).vehicle(vehicle).type(file.getContentType()).filePath(filePath).isProfileImage(false).build());
            file.transferTo(new File(filePath));
        }
        systemStorageRepository.saveAll(fileDataList);


        if (!fileDataList.isEmpty()) {
            return "file uploaded successfully !";
        }
        return "Upload error!";
    }

    public List<byte[]> downloadImageFromFileSystem(Long vehicleId) throws IOException {
        Vehicle vehicle = getVehicleById(vehicleId);
        if (vehicle == null) {
            return null;
        }
        List<FileData> filesData = vehicle.getImages();
        List<byte[]> images = new ArrayList<>();
        for (FileData fileData : filesData) {
            String filePath = fileData.getFilePath();
            byte[] image = Files.readAllBytes(new File(filePath).toPath());
            images.add(image);
        }
        return images;
    }

    public List<byte[]> getVehicleConfidentialFiles(Long vehicleId) {
        Vehicle vehicle = getVehicleById(vehicleId);
        if (vehicle == null) {
            return null;
        }
        List<byte[]> images = new ArrayList<>();
        List<ConfidentialFile> vehicleRegistrations = vehicle.getVehicleRegistrations();
        for(ConfidentialFile currentFile:vehicleRegistrations){
            byte[] image = ImageUtils.decompressImage(currentFile.getImageData());
            images.add(image);
        }
        return images;
    }

    @Transactional
    public String uploadRenterConfidentialFile(List<MultipartFile> file, Long vehicleId) throws IOException {

        Vehicle vehicle = getVehicleById(vehicleId);
        if (vehicle == null) {
            return "Vehicle not found !";
        }
        List<ConfidentialFile> list = new ArrayList<>();
        for(MultipartFile current:file){
            ConfidentialFile confidentialFile =ConfidentialFile.builder().name(current.getOriginalFilename()).type(current.getContentType()).vehicle(vehicle).imageData(ImageUtils.compressImage(current.getBytes())).build();
            list.add(confidentialFile);
        }
        databaseStorageRepository.saveAll(list);
        if (!list.isEmpty()) {
            return "file uploaded successfully !";
        }
        return null;
    }


}
