package com.oocl.ita.ivy.parkinglot.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ParkingBoyDTO {
    private String ParkingLotId;
    private String parkingBoyId;

}