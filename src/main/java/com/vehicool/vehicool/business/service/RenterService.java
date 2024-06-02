package com.vehicool.vehicool.business.service;


import com.querydsl.core.types.Predicate;
import com.vehicool.vehicool.business.querydsl.RenterFilter;
import com.vehicool.vehicool.business.querydsl.RenterQueryDsl;
import com.vehicool.vehicool.persistence.entity.ConfidentialFile;
import com.vehicool.vehicool.persistence.entity.Contract;
import com.vehicool.vehicool.persistence.entity.Renter;
import com.vehicool.vehicool.persistence.repository.DatabaseStorageRepository;
import com.vehicool.vehicool.persistence.repository.RenterRepository;
import com.vehicool.vehicool.util.fileconfigs.ImageUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class RenterService {
    private final RenterRepository renterRepository;
    private final DatabaseStorageRepository databaseStorageRepository;
    private final RenterQueryDsl renterQueryDsl;

    public Renter getRenterById(Long id) {
        return renterRepository.findById(id).orElse(null);
    }

    public Renter save(Renter renter) {
        return renterRepository.save(renter);
    }

    public void delete(Long id) {
        renterRepository.deleteById(id);
    }

    public Renter update(Renter renter, Long Id) {
        renter.setId(Id);
        return renterRepository.saveAndFlush(renter);
    }


    public Page<Renter> findAll(RenterFilter renterFilter, Pageable pageRequest) {
        Predicate filter = renterQueryDsl.filter(renterFilter);
        return renterRepository.findAll(filter, pageRequest);
    }

    public List<Contract> contractRequests(Long renterId, Long statusId) {
        return renterRepository.contractRequests(renterId, statusId);
    }


//    public String uploadRenterConfidentialFile(List<MultipartFile> file, Long renterId) throws IOException {
//        Renter renter = getRenterById(renterId);
//        if (renter == null) {
//            return "Failed to upload! Wrong renter ID";
//        }
//        ConfidentialFile confidentialFile =ConfidentialFile.builder()
//                .name(file.getOriginalFilename())
//                .type(file.getContentType())
//                .renter(renter)
//                .imageData(ImageUtils.compressImage(file.getBytes())).build());
//        if (confidentialFile != null) {
//            return "file uploaded successfully : " + file.getOriginalFilename();
//        }
//        return null;
//    }

}
