package com.driver;


import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {

    private Map<String,Order> orderMap = new HashMap<>();
    private Map<String,DeliveryPartner> deliveryPartnerMap = new HashMap<>();

    private Map<String , List<String>> partnerOrderMap = new HashMap<>();


    public void addOrder(Order order){
        orderMap.put(order.getId() , order);
    }

    public void addPartner(String partnerId){
        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        deliveryPartnerMap.put(partnerId , deliveryPartner);
    }


    public void addOrderPartnerPair(String orderId, String partnerId){
        //This is basically assigning that order to that partnerId
        if(partnerOrderMap.containsKey(partnerId)){
            List<String> orders = partnerOrderMap.get(partnerId);
            orders.add(orderId);
            DeliveryPartner partner = deliveryPartnerMap.get(partnerId);
            partner.setNumberOfOrders(partner.getNumberOfOrders() + 1);

            partnerOrderMap.put(partnerId , orders);
        }else{
            List<String> orders = new ArrayList<>();
            orders.add(orderId);

            DeliveryPartner partner = deliveryPartnerMap.get(partnerId);
            partner.setNumberOfOrders(1);

            partnerOrderMap.put(partnerId , orders);
        }
    }


    public Order getOrderById(String orderId){

        return orderMap.getOrDefault(orderId , null);
    }


    public DeliveryPartner getPartnerById(String partnerId){

        return deliveryPartnerMap.getOrDefault(partnerId , null);
    }


    public Integer getOrderCountByPartnerId(String partnerId){

        return partnerOrderMap.getOrDefault(partnerId , new ArrayList<>()).size();
    }


    public List<String> getOrdersByPartnerId(String partnerId){

        return partnerOrderMap.getOrDefault(partnerId , new ArrayList<>());
    }


    public List<String> getAllOrders(){

        return new ArrayList<>(orderMap.keySet());
    }


    public Integer getCountOfUnassignedOrders(){
        //Count of orders that have not been assigned to any DeliveryPartner

        Integer totalOrders = getAllOrders().size();
        Integer assignedOrders = 0;

        for(String partnerId : partnerOrderMap.keySet()){
            assignedOrders += partnerOrderMap.get(partnerId).size();
        }

        return totalOrders - assignedOrders;
    }


    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId){

        String[] timeComponents = time.split(":");
        int HH = Integer.parseInt(timeComponents[0]);
        int MM = Integer.parseInt(timeComponents[1]);
        int currentTime=HH*60+MM;

        Integer count = 0;
        List<String> orders = partnerOrderMap.get(partnerId);

        for(String orderId : orders){
            Order order = orderMap.get(orderId);
            if(order.getDeliveryTime() > currentTime){
                count++;
            }
        }
        return count;
    }


    public String getLastDeliveryTimeByPartnerId(String partnerId){

       List<String> orders = partnerOrderMap.get(partnerId);
       int deliveryTime = 0;
       if(orders.size() > 0){
           Order order = orderMap.get(orders.get(orders.size()-1));
           deliveryTime = order.getDeliveryTime();
       }

       StringBuilder sb = new StringBuilder();
       int HH = deliveryTime/60;
       if(HH < 10){
           sb.append("0");
       }
       sb.append(String.valueOf(HH) + ":");

        int MM = deliveryTime % 60;
        if(MM < 10){
            sb.append("0");
        }
        sb.append(String.valueOf(MM));
        return sb.toString();
    }


    public void deletePartnerById(String partnerId){
        //Delete the partnerId
        //And push all his assigned orders to unassigned orders.
        if(partnerOrderMap.containsKey(partnerId)){
            partnerOrderMap.remove(partnerId);
        }
        if(deliveryPartnerMap.containsKey(partnerId)){
            deliveryPartnerMap.remove(partnerId);
        }
    }

    public void deleteOrderById(String orderId){
        //Delete an order and also
        // remove it from the assigned order of that partnerId
        if(orderMap.containsKey(orderId)) orderMap.remove(orderId);

        for(String partnerId : partnerOrderMap.keySet()){
            List<String> orders = partnerOrderMap.get(partnerId);
            if(orders.contains(orderId)){
                orders.remove(orderId);

                DeliveryPartner partner = deliveryPartnerMap.get(partnerId);
                partner.setNumberOfOrders(partner.getNumberOfOrders()-1);
            }
        }
    }
}
