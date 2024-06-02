package com.vehicool.vehicool.business.service;

import com.querydsl.core.types.Predicate;
import com.vehicool.vehicool.business.querydsl.LenderFilter;
import com.vehicool.vehicool.business.querydsl.LenderQueryDsl;
import com.vehicool.vehicool.persistence.entity.ConfidentialFile;
import com.vehicool.vehicool.persistence.entity.Contract;
import com.vehicool.vehicool.persistence.entity.Lender;
import com.vehicool.vehicool.persistence.repository.DatabaseStorageRepository;
import com.vehicool.vehicool.persistence.repository.LenderRepository;
import com.vehicool.vehicool.util.fileconfigs.ImageUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class LenderService {
    private final LenderRepository lenderRepository;
    private final LenderQueryDsl lenderQueryDsl;
    private DatabaseStorageRepository databaseStorageRepository;

    public Lender getLenderById(Long id) {
        return lenderRepository.findById(id).orElse(null);
    }

    public Lender save(Lender lender) {
        return lenderRepository.save(lender);
    }

    public void delete(Long id) {
        lenderRepository.deleteById(id);
    }

    public Lender update(Lender lender, Long Id) {
        lender.setId(Id);
        return lenderRepository.saveAndFlush(lender);
    }


    public Page<Lender> findAll(LenderFilter lenderFilter, Pageable pageRequest) {
        Predicate filter = lenderQueryDsl.filter(lenderFilter);
        return lenderRepository.findAll(filter, pageRequest);
    }

    public List<Contract> contractRequests(Long lenderId, Long statusId) {
        return lenderRepository.contractRequests(lenderId, statusId);
    }



}
