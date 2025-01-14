package com.oocl.ita.ivy.parkinglot.entity.enums;

import lombok.Getter;

@Getter
public enum BusinessExceptionType {
    RECODE_NOT_FOUNT(100, "RECORD_NOT_FOUNT", "Record not found"),
    USERNAME_EXISTS(101,"USERNAME_EXISTS","Username is exists"),
    PARKING_LOT_NOT_AVAILABLE(102, "PARKING_LOT_NOT_AVAILABLE", "No suitable parking lot."),
    PARKING_BOY_NOT_AVAILABLE(103, "PARKING_BOY_NOT_AVAILABLE", "Parking boy not available");

    private final int id;
    private final String name;
    private final String description;

    BusinessExceptionType(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

}
