package com.vehicool.vehicool.business.querydsl;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.vehicool.vehicool.persistence.entity.QVehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleQueryDsl implements QueryDsl<VehicleFilter> {

    QVehicle qVehicle = QVehicle.vehicle;

    @Override
    public Predicate filter(VehicleFilter filter) {
        BooleanBuilder query = new BooleanBuilder();


        if (filter.getTransmissionTypeId() != null) {
            query.and(qVehicle.transmissionType.id.eq(filter.getTransmissionTypeId()));
        }
        if (filter.getEngineTypeId() != null) {
            query.and(qVehicle.engineType.id.eq(filter.getEngineTypeId()));
        }
        if (filter.getVehicleTypeId() != null) {
            query.and(qVehicle.engineType.id.eq(filter.getVehicleTypeId()));
        }
        if (filter.getLocationId() != null) {
            query.and(qVehicle.location.id.eq(filter.getLocationId()));
        }
        if (filter.getMinPrice() != null) {
            query.and(qVehicle.vehicleCommerce.pricePerDay.goe(filter.getMinPrice()));
        }
        if (filter.getMaxPrice() != null) {
            query.and(qVehicle.vehicleCommerce.pricePerDay.loe(filter.getMaxPrice()));
        }
        if (filter.getStartingDateAvailable() != null) {
            query.and(qVehicle.vehicleCommerce.dateAvailable.loe(filter.getStartingDateAvailable()));
        }
        if (filter.getStartingDateAvailable() != null) {
            query.and(qVehicle.vehicleCommerce.dateAvailable.loe(filter.getStartingDateAvailable()));
        }
//        query.and(qVehicle.status.enumLabel.matches("VerifiedVehicle"));

        return query;
    }
}

