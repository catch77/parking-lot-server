package com.oocl.ita.ivy.parkinglot.controller;

import com.oocl.ita.ivy.parkinglot.entity.ParkingBoy;
import com.oocl.ita.ivy.parkinglot.entity.ParkingBoyVo;
import com.oocl.ita.ivy.parkinglot.entity.ParkingLot;
import com.oocl.ita.ivy.parkinglot.entity.ParkingOrder;
import com.oocl.ita.ivy.parkinglot.entity.enums.BusinessExceptionType;
import com.oocl.ita.ivy.parkinglot.exception.BusinessException;
import com.oocl.ita.ivy.parkinglot.repository.UserRepository;
import com.oocl.ita.ivy.parkinglot.service.ParkingBoyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parking-boys")
public class ParkingBoyController implements BaseController<ParkingBoy, String> {

    @Autowired
    private ParkingBoyService parkingBoyService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ParkingBoy save(ParkingBoy parkingBoy) {
        if(userRepository.findByUsername(parkingBoy.getUser().getUsername()).isPresent()){
            throw new BusinessException(BusinessExceptionType.USERNAME_EXISTS);
        }
        return parkingBoyService.save(parkingBoy);
    }

    @Override
    public List<ParkingBoy> findAll() {
        return parkingBoyService.findAll();
    }

    @Override
    public Page<ParkingBoy> findAll(Pageable pageable) {
        return parkingBoyService.findAll(pageable);
    }

    @Override
    public ParkingBoy findById(String s) throws Exception {
        return parkingBoyService.findById(s);
    }

    @Override
    public void deleteById(String s) {
        parkingBoyService.deleteById(s);
    }

    @Override
    public ParkingBoy update(ParkingBoy parkingBoy) {
        return parkingBoyService.update(parkingBoy);
    }

    @PutMapping("/{id}/parking-lots")
    public ParkingBoy setParkingLots(@PathVariable String id, @RequestBody  List<ParkingLot> parkingLots){
        return parkingBoyService.setParkingLotsByID(id,parkingLots);
    }

    @GetMapping("/{id}/parking-lots")
    public List<ParkingLot> getParkingLotsById(@PathVariable String id){
        return parkingBoyService.getParkingLotsByID(id);
    }

    @GetMapping("/me")
    public ParkingBoy getCurrentParkingBoy(){
        return parkingBoyService.getCurrentParkingBoy();
    }

    @PutMapping("/{id}/parking-status")
    public ParkingBoy changeParkingBoyStatus(@PathVariable String id){
        return parkingBoyService.changeParkingBoyStatus(id);
    }

    @PutMapping("/{id}/upgrade")
    public ParkingBoy upgradeToManager(@PathVariable String id) {
        return parkingBoyService.upgradeToManager(id);
    }

    @PutMapping("/{id}/degrade")
    public ParkingBoy degradeToParkingBoy(@PathVariable String id) {
        return parkingBoyService.degradeToParkingBoy(id);
    }

    @PutMapping("/{id}/parking-boys")
    public ParkingBoy setParkingBoys(@PathVariable String id, @RequestBody List<ParkingBoy> parkingBoys) {
        return parkingBoyService.addParkingBoyForManager(id, parkingBoys);
    }

    @GetMapping("/{id}/subordinates")
    public List<ParkingBoy> getSubordinatesByManagerId(@PathVariable String id) {
        return parkingBoyService.getSubordinatesByManagerId(id);
    }

    @GetMapping("/user/{id}/subordinates")
    public List<ParkingBoy> getSubordinatesByUserId(@PathVariable Integer id) {
        return parkingBoyService.getSubordinatesByUserId(id);
    }


    @GetMapping("/lower-parking-boys")
    public List<ParkingBoy> parkingBoys(){
        return parkingBoyService.findLowerParkingBoy();
    }

    @PutMapping("/salary-raise/{sum}")
    public void raiseSalary (@PathVariable double sum) {
        parkingBoyService.raiseSalary(sum);
    }
}
