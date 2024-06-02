package com.vehicool.vehicool.business.service;

import com.vehicool.vehicool.persistence.entity.RenterReview;
import com.vehicool.vehicool.persistence.repository.RenterReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RenterReviewService {
    private final RenterReviewRepository renterReviewRepository;

    public RenterReview getRenterReviewById(Long id) {
        return renterReviewRepository.findById(id).orElse(null);
    }

    public RenterReview save(RenterReview renterReview) {
        return renterReviewRepository.save(renterReview);
    }

    public void delete(Long id){
        renterReviewRepository.deleteById(id);
    }

    public RenterReview update(RenterReview renterReview,Long Id){
        renterReview.setId(Id);
        return renterReviewRepository.saveAndFlush(renterReview);
    }


}
