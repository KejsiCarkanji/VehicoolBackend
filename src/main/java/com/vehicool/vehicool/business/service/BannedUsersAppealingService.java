package com.vehicool.vehicool.business.service;

import com.vehicool.vehicool.persistence.entity.BannedUsersAppealing;
import com.vehicool.vehicool.persistence.entity.ConfidentialFile;
import com.vehicool.vehicool.persistence.entity.DataPool;
import com.vehicool.vehicool.persistence.repository.BannedUsersAppealingRepository;
import com.vehicool.vehicool.security.user.User;
import com.vehicool.vehicool.util.fileconfigs.ImageUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class BannedUsersAppealingService {
   private final BannedUsersAppealingRepository repository;
   public List<BannedUsersAppealing> list(){
       return  repository.findAll();
   }
   public BannedUsersAppealing updateAppeal(BannedUsersAppealing bannedUsersAppealing,Long id){
       bannedUsersAppealing.setId(id);
       return repository.saveAndFlush(bannedUsersAppealing);
   }
    public BannedUsersAppealing saveAppeal(BannedUsersAppealing bannedUsersAppealing){
        return repository.save(bannedUsersAppealing);
    }
    public BannedUsersAppealing getById(Long id){
        return repository.findById(id).orElse(null);
    }
    public List<byte[]> getUserConfidentialFiles(Long id) {
        BannedUsersAppealing appeal = getById(id);
        if (appeal == null) {
            return null;
        }
        List<byte[]> responseImages = new ArrayList<>();
        List<ConfidentialFile> evidence = appeal.getConfidentialFiles();
        for (ConfidentialFile currentFile : evidence) {
            byte[] image = ImageUtils.decompressImage(currentFile.getImageData());
            responseImages.add(image);
        }
        return responseImages;
    }



}
