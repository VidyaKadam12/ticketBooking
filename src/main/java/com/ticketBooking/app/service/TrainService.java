package com.ticketBooking.app.service;

import com.ticketBooking.app.entities.Train;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TrainService {

    private List<Train> trainList;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String TRAIN_DB_PATH = "src/main/java/com/ticketBooking/app/localDB/trains.json";

    public TrainService() throws IOException{
        File trains = new File(TRAIN_DB_PATH);
        trainList = objectMapper.readValue(trains, new TypeReference<List<Train>>() {});

    }

    public List<Train> searchTrains(String source, String destination){
        return trainList.stream().filter(train -> validateTrain(train, source, destination)).collect(Collectors.toList());
    }

    private boolean validateTrain(Train train, String source, String destination){
        List<String> stationsOrder = train.getStations();

        int sourceIndex = stationsOrder.indexOf(source.toLowerCase());
        int destinationIndex = stationsOrder.indexOf(destination.toLowerCase());

        return sourceIndex != -1 && destinationIndex != -1 && sourceIndex < destinationIndex;

    }

    public void addTrain(Train newTrain) throws IOException {
        Optional<Train> existingTrain = trainList.stream().filter(train -> newTrain.getTrainId().equalsIgnoreCase(train.getTrainId())).findFirst();

        if (existingTrain.isPresent()) {
            updateTrain(newTrain);
        }
        else{
            trainList.add(newTrain);
            saveTrainListToFile();
        }

    }

    public void updateTrain(Train uptatedTrain) throws IOException {
        OptionalInt index =IntStream.range(0,trainList.size()).filter(i -> trainList.get(i).getTrainId().equalsIgnoreCase(uptatedTrain.getTrainId())).findFirst();
        if(index.isPresent()){
            trainList.set(index.getAsInt(), uptatedTrain);
            saveTrainListToFile();
        }
        else{
            addTrain(uptatedTrain);
        }
    }

    private void saveTrainListToFile() throws IOException{
            objectMapper.writeValue(new File(TRAIN_DB_PATH), trainList);
    }

}
