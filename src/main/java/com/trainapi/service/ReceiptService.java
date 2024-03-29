package com.trainapi.service;

import com.trainapi.exceptions.ResourceNotFoundException;
import com.trainapi.model.Receipt;
import com.trainapi.repo.ReceiptRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ReceiptService {

    private final ReceiptRepository receiptRepository;

    public Receipt saveReceipt(Receipt receipt) {
        return receiptRepository.save(receipt);
    }
    public Receipt getReceiptById(int receiptId) {
        return receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt with ID " + receiptId + " not found"));
    }

    public List<Receipt> findAllReceiptsUsingSeatIds(List<Integer> seatIds) {
        return receiptRepository.findReceiptsBySeat_SeatIdIn(seatIds);
    }

    public Optional<Receipt> findReceiptByUserId(int userId) {
        return receiptRepository.findReceiptByUser_UserId(userId);
    }

    public void deleteReceipt(Receipt receipt) {
        receiptRepository.delete(receipt);
    }
}
