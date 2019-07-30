package com.oocl.ita.ivy.parkinglot.entity;

import com.oocl.ita.ivy.parkinglot.entity.enums.Gender;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class ParkingBoy {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;

    @ManyToMany
    @JoinColumn(name="parking_lot_id")
    private List<ParkingLot> parkingLotList;

    @OneToOne
    @NotNull
    @JoinColumn(name="user_id")
    private User user;

    @NotNull
    private boolean free=true;

    @NotNull
    private String name;

    @NotNull
    private Gender gender;

    @Column
    @CreatedDate
    @NotNull
    private Date joinTime = new Date();

}
