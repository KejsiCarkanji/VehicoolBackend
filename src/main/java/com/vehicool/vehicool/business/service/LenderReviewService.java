package com.vehicool.vehicool.business.service;

import com.vehicool.vehicool.persistence.entity.LenderReview;
import com.vehicool.vehicool.persistence.repository.LenderReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LenderReviewService {
    private final LenderReviewRepository lenderReviewRepository;

    public LenderReview getLenderReviewById(Long id) {
        return lenderReviewRepository.findById(id).orElse(null);
    }

    public LenderReview save(LenderReview lenderReview) {
        return lenderReviewRepository.save(lenderReview);
    }

    public void delete(Long id){
        lenderReviewRepository.deleteById(id);
    }

    public LenderReview update(LenderReview lenderReview,Long Id){
        lenderReview.setId(Id);
        return lenderReviewRepository.saveAndFlush(lenderReview);
    }


}
