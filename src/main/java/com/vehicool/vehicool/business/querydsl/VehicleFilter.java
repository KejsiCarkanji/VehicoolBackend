
package com.vehicool.vehicool.business.querydsl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleFilter {

    private Long transmissionTypeId;
    private Long vehicleTypeId;
    private Long engineTypeId;
    private Long locationId;
    private Double minPrice;
    private Double maxPrice;
    private Date startingDateAvailable;
    private Date lastDateAvailable;

}
