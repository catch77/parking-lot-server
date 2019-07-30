package com.oocl.ita.ivy.parkinglot.service;

import com.oocl.ita.ivy.parkinglot.entity.*;

import com.oocl.ita.ivy.parkinglot.entity.enums.BusinessExceptionType;
import com.oocl.ita.ivy.parkinglot.entity.enums.OrderStatus;
import com.oocl.ita.ivy.parkinglot.entity.enums.ParkingBoyStatus;
import com.oocl.ita.ivy.parkinglot.exception.BusinessException;
import com.oocl.ita.ivy.parkinglot.repository.CustomerRepository;
import com.oocl.ita.ivy.parkinglot.repository.ParkingBoyRepository;
import com.oocl.ita.ivy.parkinglot.repository.ParkingLotRepository;
import com.oocl.ita.ivy.parkinglot.repository.ParkingOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParkingOrderService {

    @Autowired
    private ParkingLotService parkingLotService;
    @Autowired
    private ParkingOrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ParkingBoyService parkingBoyService;
    @Autowired
    private ParkingLotRepository parkingLotRepository;
    @Autowired
    private ParkingBoyRepository parkingBoyRepository;
    @Autowired
    private CustomerService customerService;


    public ParkingOrder save(String customerUsername, String carNo) {
        Customer customer = customerRepository.findByUsername(customerUsername);
        if (customer == null) {
            throw new BusinessException(BusinessExceptionType.RECODE_NOT_FOUNT);
        }
        ParkingOrder parkingOrder = new ParkingOrder();
        parkingOrder.setCarNo(carNo);
        parkingOrder.setCustomer(customer);
        parkingOrder.setOrderStatus(OrderStatus.PROGRESSING);
        parkingOrder.setSubmitTime(new Date());
        return orderRepository.save(parkingOrder);
    }

    public ParkingOrder customerPark(String customerUsername, String carNo) {

        ParkingOrder parkingOrder = save(customerUsername, carNo);
        /*
         * 1.先把订单分配给有停车空位，并且是open的parkingboy
         * 2.如果没有open的，就把订单给stop的parkingboy
         * 3.没有就返回
         **/
        ParkingBoy parkingBoy = parkingBoyService.getParkingBoyInSomeStatus(ParkingBoyStatus.OPEN.getStatus());
//        List<ParkingBoy> OpenedAndHasFreePlaceParkingBoyList = parkingBoyRepository.findAllByStatus(ParkingBoyStatus.OPEN)
//                .stream()
//                .filter(ParkingBoy::hasFreeParkingLot).collect(Collectors.toList());

        if (parkingBoy == null)
            parkingBoyService.getParkingBoyInSomeStatus(ParkingBoyStatus.STOP.getStatus());
        //找到第一个为有位置的parking_lot
        ParkingLot validParkingLot = null;
        ParkingLot temp = null;
        for (int i = 0; i < parkingBoy.getParkingLotList().size(); i++) {
            temp = parkingBoy.getParkingLotList().get(i);
            if (temp.getCapacity() > temp.getUsedCapacity()) {
                validParkingLot = temp;
                break;
            }
        }
        //将parking_lot的used_capacity+1
        System.out.println(validParkingLot);
        parkingLotService.addUsedCapacity(validParkingLot.getId());

        validParkingLot = parkingLotService.findById(validParkingLot.getId());
        parkingBoy = parkingBoyService.findById(parkingBoy.getId());
        parkingOrder.setParkingLot(validParkingLot);
        parkingOrder.setParkParkingBoy(parkingBoy);
        if (parkingBoy == null) {
            parkingOrder.setOrderStatus(OrderStatus.PROGRESSING);
        } else {
            parkingOrder.setOrderStatus(OrderStatus.PARK);
        }

        parkingOrder.setParkParkingBoy(parkingBoy);
        parkingOrder.setParkingLot(parkingBoy.getParkingLotList().get(0));

        Integer userId = customerService.findById(parkingOrder.getCustomer().getId()).getUser().getId();
        String number = new Date().getTime() + Math.random() * 10000 + "" + userId;
        parkingOrder.setNumber(number);

        parkingOrder.setStartTime(new Date());

        return orderRepository.save(parkingOrder);
    }


    public ParkingOrder customerFetch(String fetchId) throws Exception {
        ParkingOrder parkingOrder = orderRepository.findById(fetchId).orElseThrow(() -> new BusinessException(BusinessExceptionType.RECODE_NOT_FOUNT));
        ParkingLot parkingLot = parkingOrder.getParkingLot();
        List<ParkingBoy> parkingBoyList = parkingBoyService.getParkingBoyByParkingLot(parkingLot.getId(), String.valueOf(ParkingBoyStatus.OPEN));
        if (parkingBoyList.size() == 0) {
            parkingBoyList = parkingBoyService.getParkingBoyByParkingLot(parkingLot.getId(), String.valueOf(ParkingBoyStatus.STOP));
        }
        if (parkingBoyList.size() == 0) {
            return null;
        }

        parkingOrder.setOrderStatus(OrderStatus.PAID);
        parkingOrder.setFetchParkingBoy(parkingBoyList.get(0));
        parkingOrder.setEndTime(new Date());

        return orderRepository.save(parkingOrder);
    }

    public List<ParkingBoyVo> getMySelfParkOrders(){
        ParkingBoy me = parkingBoyService.getCurrentParkingBoy();
        List<ParkingOrder> parkingOrders = orderRepository.findAll();
        List<ParkingBoyVo> result = new ArrayList<>();
        ParkingBoyVo parkingBoyVo = null;
        for(int i = 0; i < parkingOrders.size(); i++){
            ParkingBoy parkParkingBoy = parkingOrders.get(i).getParkParkingBoy();
            if(me.getId() == parkParkingBoy.getId()){
                ParkingOrder parkingOrder = parkingOrders.get(i);
                Customer customer = parkingOrder.getCustomer();
                User user = customer.getUser();
                ParkingBoy fetchParkingBoy = parkingOrder.getFetchParkingBoy();
                ParkingLot parkingLot = parkingOrder.getParkingLot();

                parkingBoyVo = new ParkingBoyVo();

                parkingBoyVo.setOrderId(parkingOrder.getId());
                parkingBoyVo.setUsername(user.getUsername());
                parkingBoyVo.setPhone(customer.getPhone());
                parkingBoyVo.setCarNo(parkingOrder.getCarNo());
                parkingBoyVo.setPrice(parkingOrder.getPrice());
                parkingBoyVo.setSubmitTime(parkingOrder.getSubmitTime());
                parkingBoyVo.setParkingLotName(parkingLot.getName());
                parkingBoyVo.setFetchTime(parkingOrder.getFetchTime());
                parkingBoyVo.setParkParkingBoyName(parkParkingBoy.getName());
                parkingBoyVo.setFetchParkingBoyName(fetchParkingBoy.getName());
                parkingBoyVo.setOrderStatus(parkingOrder.getOrderStatus());
            }
            result.add(parkingBoyVo);
        }
        return result;
    }

    public List<ParkingBoyVo> getMySelfFetchOrder(){
        ParkingBoy me = parkingBoyService.getCurrentParkingBoy();
        List<ParkingOrder> parkingOrders = orderRepository.findAll();
        List<ParkingBoyVo> result = new ArrayList<>();
        ParkingBoyVo parkingBoyVo = null;
        for(int i = 0; i < parkingOrders.size(); i++){
            ParkingBoy parkParkingBoy = parkingOrders.get(i).getFetchParkingBoy();
            if(me.getId() == parkParkingBoy.getId()){
                ParkingOrder parkingOrder = parkingOrders.get(i);
                Customer customer = parkingOrder.getCustomer();
                User user = customer.getUser();
                ParkingBoy fetchParkingBoy = parkingOrder.getFetchParkingBoy();
                ParkingLot parkingLot = parkingOrder.getParkingLot();

                parkingBoyVo = new ParkingBoyVo();

                parkingBoyVo.setOrderId(parkingOrder.getId());
                parkingBoyVo.setUsername(user.getUsername());
                parkingBoyVo.setPhone(customer.getPhone());
                parkingBoyVo.setCarNo(parkingOrder.getCarNo());
                parkingBoyVo.setPrice(parkingOrder.getPrice());
                parkingBoyVo.setSubmitTime(parkingOrder.getSubmitTime());
                parkingBoyVo.setParkingLotName(parkingLot.getName());
                parkingBoyVo.setFetchTime(parkingOrder.getFetchTime());
                parkingBoyVo.setParkParkingBoyName(parkParkingBoy.getName());
                parkingBoyVo.setFetchParkingBoyName(fetchParkingBoy.getName());
                parkingBoyVo.setOrderStatus(parkingOrder.getOrderStatus());
            }
            result.add(parkingBoyVo);
        }
        return result;
    }

    public List<ParkingBoyVo> getMySelfAllOrders(){
        List<ParkingBoyVo> parkOrders = getMySelfParkOrders();
        List<ParkingBoyVo> fetchOrders = getMySelfFetchOrder();

        List<ParkingBoyVo> results = new ArrayList<>(parkOrders.size() + fetchOrders.size());
        int i = 0;
        int j = 0;

        while(i <= parkOrders.size() && j <= fetchOrders.size()){
            results.add(parkOrders.get(i).getSubmitTime().before(fetchOrders.get(j).getSubmitTime()) ? parkOrders.get(i++) : fetchOrders.get(j));
        }

        while(i <= parkOrders.size()){
            results.add(parkOrders.get(i++));
        }
        while(j <= fetchOrders.size()){
            results.add(fetchOrders.get(j++));
        }
        return results;
    }



    public Page<ParkingOrder> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public ParkingOrder findById(String id) {
        return orderRepository.findById(id).orElseThrow(() -> new BusinessException(BusinessExceptionType.RECODE_NOT_FOUNT));
    }
}
