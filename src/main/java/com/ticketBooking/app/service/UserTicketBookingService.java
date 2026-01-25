package com.ticketBooking.app.service;


import com.ticketBooking.app.entities.Ticket;
import com.ticketBooking.app.entities.Train;
import com.ticketBooking.app.entities.User;
import com.ticketBooking.app.utils.UserServiceUtil;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserTicketBookingService {

    private List<User> userList;
    private User user;
    private TrainService trainService;
    private UserServiceUtil userServiceUtil;
    private ObjectMapper obj = new ObjectMapper();
    private static final String USER_DB_PATH = "src/main/java/com/ticketBooking/app/localDB/users.json";

    public UserTicketBookingService(User user) throws IOException {
        this.user = user;
        loadUserListFromFile();
    }

    public UserTicketBookingService() throws IOException {

        loadUserListFromFile();
    }

    private void loadUserListFromFile(){
        userList = obj.readValue(new File(USER_DB_PATH), new TypeReference<List<User>>() {});

    }

    public Boolean loginUser(){
        Optional<User> foundUser = userList.stream().filter(user1 -> user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword())).findFirst();
        return foundUser.isPresent();
    }

    public void signUpUser(User user) throws IOException{
        Optional<User> existsUser = userList.stream().filter(user1 -> user1.getName().equals(user.getName())).findFirst();
        if(!existsUser.isPresent()){
            userList.add(user);
            saveUserToFille();
        }
    }

    public void saveUserToFille() throws IOException{
        obj.writeValue(new File(USER_DB_PATH), userList);
    }

    public void fetchBookings(){
        if(user.getName().isEmpty() || user.getName() == null){
            System.out.println("please login first");
        }
        Optional<User> userFound = userList.stream().filter(user1 -> user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword())).findFirst();
        if(userFound.isPresent()){
            List<Ticket> ticketsBooked = userFound.get().getTicketsBooked();
            if(ticketsBooked.isEmpty()){
                System.out.println("No tickets Booked");
            }
            else{
                userFound.get().printTickets();
            }
        }


    }

    public Boolean cancelBooking(String ticketId){
//        Scanner s = new Scanner(System.in);
//        System.out.println("Enter the ticket id to cancel");
//        ticketId = s.next();

        if(ticketId == null || ticketId.isEmpty()){
            System.out.println("ticket ID can't be empty");
            return Boolean.FALSE;
        }
        boolean removed = user.getTicketsBooked().removeIf(ticket ->ticket.getTicketId().equals(ticketId));
        if(removed){
            //userList.remove(user.getTicketsBooked());
            System.out.println("Ticket with ID " + ticketId + " has been cancelled");
            return Boolean.TRUE;
        }
        else{
            System.out.println("Ticket with ID " + ticketId + " has not found");
            return Boolean.FALSE;
        }
    }

    public List<Train> getTrains(String source, String destination){
        try{
            System.out.println("Inside getTrains...");
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, destination);
        }catch(IOException ex){
            System.out.println("catched exmeption");
            return new ArrayList<>();
        }
    }

    public List<List<Integer>> fetchSeats(Train train){
        return train.getSeats();
    }

    public Boolean bookTrainSeat(Train train, int row, int seat) throws IOException {
        List<List<Integer>> seats = train.getSeats();
        if(row>=0 && row < seats.size() && seat >=0 && seat < seats.get(row).size()){
            if(seats.get(row).get(seat) == 0){
                seats.get(row).set(seat,1);
                train.setSeats(seats);
                trainService.addTrain(train);
                return true;
            }
            else{
                return false;
            }

        }
        else{
            return false;
        }
    }




}
