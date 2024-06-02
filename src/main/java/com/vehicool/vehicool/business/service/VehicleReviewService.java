package com.vehicool.vehicool.business.service;


import com.vehicool.vehicool.persistence.entity.VehicleReview;
import com.vehicool.vehicool.persistence.repository.VehicleReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VehicleReviewService {
    private final VehicleReviewRepository vehicleReviewRepository;

    public VehicleReview getVehicleReviewById(Long id) {
        return vehicleReviewRepository.findById(id).orElse(null);
    }

    public VehicleReview save(VehicleReview vehicleReview) {
        return vehicleReviewRepository.save(vehicleReview);
    }

    public void delete(Long id){
        vehicleReviewRepository.deleteById(id);
    }

    public VehicleReview update(VehicleReview vehicleReview,Long Id){
        vehicleReview.setId(Id);
        return vehicleReviewRepository.saveAndFlush(vehicleReview);
    }


}
