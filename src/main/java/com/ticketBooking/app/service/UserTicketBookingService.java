package com.ticketBooking.app.service;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.ticketBooking.app.entities.User;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class UserTicketBookingService {

    private List<User> userList;
    private User user;
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



}
