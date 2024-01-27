package com.trainapi.repo;

import com.trainapi.model.Seat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends CrudRepository<Seat, Integer> {
    List<Seat> getSeatsBySeatStatusIs(String seatStatus);
    List<Seat> findSeatsBySectionAndSeatStatus(Character section, String seatStatus);
    Seat findSeatBySectionAndSeatNumber(Character section, int seatNumber);
}
