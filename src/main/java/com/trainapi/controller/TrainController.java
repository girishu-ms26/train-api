package com.trainapi.controller;

import com.trainapi.model.Receipt;
import com.trainapi.service.TrainService;
import com.trainapi.constants.TrainApiConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(TrainApiConstants.BASE_PATH)
public class TrainController {

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    @PostMapping(value = TrainApiConstants.POST_BOOK_TRAIN_PATH)
    public ResponseEntity<Receipt> bookTicketByReceipt(@RequestBody Receipt receipt) throws Exception {
        final Receipt savedReceipt = trainService.bookTicketByReceipt(receipt);
        return new ResponseEntity<>(savedReceipt,HttpStatus.CREATED);
    }

    @GetMapping(value = TrainApiConstants.GET_RECEIPT_PATH)
    public ResponseEntity<Receipt> getReceiptByReceiptId(@PathVariable int receiptId) {
        final Receipt receipt = trainService.getReceiptById(receiptId);
        return new ResponseEntity<>(receipt,HttpStatus.OK);
    }

    @GetMapping(value = TrainApiConstants.GET_SECTION_PATH)
    public ResponseEntity<List<Receipt>> getUsersAndSeatsBySectionCode(@PathVariable Character sectionCode) {
        final List<Receipt> receipts = trainService.getUsersAndSeatsBySectionCode(sectionCode);
        return new ResponseEntity<>(receipts,HttpStatus.OK);
    }

    @DeleteMapping(value = TrainApiConstants.REMOVE_USER_PATH)
    public ResponseEntity<HttpStatus> removeUserFromTrainByUserId(@PathVariable int userId) throws Exception {
        trainService.removeUserFromTrain(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = TrainApiConstants.UPDATE_SEAT_PATH)
    public ResponseEntity<Receipt> updateUserSeat(@RequestBody Receipt receipt) throws Exception {
        Receipt updatedReceipt = trainService.updateAndBookTicket(receipt);
        return new ResponseEntity<>(updatedReceipt,HttpStatus.OK);
    }

}

