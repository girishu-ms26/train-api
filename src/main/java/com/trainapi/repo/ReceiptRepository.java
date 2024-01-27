package com.trainapi.repo;

import com.trainapi.model.Receipt;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends CrudRepository<Receipt, Integer> {

    Optional<Receipt> findReceiptByUser_UserId(int userId);
    List<Receipt> findReceiptsBySeat_SeatIdIn(List<Integer> seatId);
}
