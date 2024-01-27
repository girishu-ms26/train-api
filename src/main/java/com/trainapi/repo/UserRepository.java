package com.trainapi.repo;

import com.trainapi.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    List<User> findUsersBySeat_SeatIdIn(List<Integer> seatId);
}