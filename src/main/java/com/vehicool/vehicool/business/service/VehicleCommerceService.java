package com.vehicool.vehicool.business.service;

import com.vehicool.vehicool.persistence.entity.Vehicle;
import com.vehicool.vehicool.persistence.entity.VehicleCommerce;
import com.vehicool.vehicool.persistence.repository.VehicleCommerceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VehicleCommerceService {
    private final VehicleCommerceRepository vehicleCommerceRepository;
    public VehicleCommerce getVehicleById(Long id) {
        return vehicleCommerceRepository.findById(id).orElse(null);
    }

    public VehicleCommerce save(VehicleCommerce vehicleCommerce) {
        return vehicleCommerceRepository.save(vehicleCommerce);
    }

    public void delete(Long id) {
        vehicleCommerceRepository.deleteById(id);
    }

    public VehicleCommerce update(VehicleCommerce vehicleCommerce, Long Id) {
        vehicleCommerce.setId(Id);
        return vehicleCommerceRepository.saveAndFlush(vehicleCommerce);
    }
}
