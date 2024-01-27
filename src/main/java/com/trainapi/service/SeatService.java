package com.trainapi.service;

import com.trainapi.constants.TrainApiConstants;
import com.trainapi.model.Seat;
import com.trainapi.repo.SeatRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class SeatService {

    private final SeatRepository seatRepository;

    public List<Seat> getAllAvailableSeats() {
        return seatRepository.getSeatsBySeatStatusIs(TrainApiConstants.AVAILABLE);
    }

    public void saveSeat(Seat seatToSave) {
        seatRepository.save(seatToSave);
    }

    public List<Seat> findAllSeatsBySectionAndStatus(Character section, String seatStatus) {
        return seatRepository.findSeatsBySectionAndSeatStatus(section, seatStatus);
    }

    public Seat findSeatBySectionAndSeatNumber(Character section, int seatNumber) {
        return seatRepository.findSeatBySectionAndSeatNumber(section, seatNumber);
    }

}
