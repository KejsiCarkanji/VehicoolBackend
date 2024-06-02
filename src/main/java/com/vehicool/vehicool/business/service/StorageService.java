package com.vehicool.vehicool.business.service;


import com.vehicool.vehicool.persistence.entity.ConfidentialFile;
import com.vehicool.vehicool.persistence.entity.FileData;
import com.vehicool.vehicool.persistence.repository.DatabaseStorageRepository;
import com.vehicool.vehicool.persistence.repository.SystemStorageRepository;
import com.vehicool.vehicool.util.fileconfigs.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

@Service
public class StorageService {

    @Autowired
    private DatabaseStorageRepository databaseStorageRepository;

    @Autowired
    private SystemStorageRepository systemStorageRepository;

    public FileData getStorageFileById(Long id) {
        return systemStorageRepository.findById(id).orElse(null);
    }
    public void saveAllImages(List<FileData> images){
        systemStorageRepository.saveAll(images);
    }

    public FileData saveStorageFile(FileData fileData) {
        return systemStorageRepository.save(fileData);
    }

    public void deleteStorageFile(Long id){
        systemStorageRepository.deleteById(id);
    }

    public FileData updateStorageFile(FileData fileData,Long Id){
        fileData.setId(Id);
        return systemStorageRepository.saveAndFlush(fileData);
    }

    private final String FOLDER_PATH="src/main/resources/vehiclesImages/";

    public String uploadImageToDatabase(MultipartFile file) throws IOException {
        ConfidentialFile confidentialFile = databaseStorageRepository.save(ConfidentialFile.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(ImageUtils.compressImage(file.getBytes())).build());
        if (confidentialFile != null) {
            return "file uploaded successfully : " + file.getOriginalFilename();
        }
        return null;
    }



    public byte[] downloadImageToDatabase(String fileName) {
        Optional<ConfidentialFile> dbImageData = databaseStorageRepository.findByName(fileName);
        byte[] images = ImageUtils.decompressImage(dbImageData.get().getImageData());
        return images;
    }


    public String uploadImageToFileSystem(MultipartFile file) throws IOException {
        String filePath=FOLDER_PATH+file.getOriginalFilename();

        FileData fileData=systemStorageRepository.save(FileData.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .filePath(filePath).build());


        file.transferTo(new File(filePath));

        if (fileData != null) {
            return "file uploaded successfully : " + filePath;
        }
        return null;
    }

    public byte[] downloadImageFromFileSystem(Long id) throws IOException {
        Optional<FileData> fileData = systemStorageRepository.findById(id);
        String filePath=fileData.get().getFilePath();
        return Files.readAllBytes(new File(filePath).toPath());
    }

//    public List<String> encodeImages(List<Path> imagePaths) throws IOException {
//        List<String> encodedImages = new ArrayList<>();
//        for (Path path : imagePaths) {
//            byte[] imageBytes = Files.readAllBytes(path);
//            String encodedImage = Base64Utils.encodeToString(imageBytes);
//            encodedImages.add(encodedImage);
//        }
//        return encodedImages;
//    }



}
