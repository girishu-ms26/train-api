package com.cloudbees.trainapi.controller;

import com.cloudbees.trainapi.model.Receipt;
import com.cloudbees.trainapi.service.TrainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cloudbees.trainapi.constants.TrainApiConstants.BASE_PATH;
import static com.cloudbees.trainapi.constants.TrainApiConstants.GET_RECEIPT_PATH;
import static com.cloudbees.trainapi.constants.TrainApiConstants.GET_SECTION_PATH;
import static com.cloudbees.trainapi.constants.TrainApiConstants.POST_BOOK_TRAIN_PATH;
import static com.cloudbees.trainapi.constants.TrainApiConstants.REMOVE_USER_PATH;
import static com.cloudbees.trainapi.constants.TrainApiConstants.UPDATE_SEAT_PATH;


@RestController
@RequestMapping(BASE_PATH)
public class TrainController {

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    @PostMapping(value = POST_BOOK_TRAIN_PATH)
    public ResponseEntity<Receipt> bookTicketByReceipt(@RequestBody Receipt receipt) throws Exception {
        final Receipt savedReceipt = trainService.bookTicketByReceipt(receipt);
        return new ResponseEntity<>(savedReceipt,HttpStatus.CREATED);
    }

    @GetMapping(value = GET_RECEIPT_PATH)
    public ResponseEntity<Receipt> getReceiptByReceiptId(@PathVariable int receiptId) {
        final Receipt receipt = trainService.getReceiptById(receiptId);
        return new ResponseEntity<>(receipt,HttpStatus.OK);
    }

    @GetMapping(value = GET_SECTION_PATH)
    public ResponseEntity<List<Receipt>> getUsersAndSeatsBySectionCode(@PathVariable Character sectionCode) {
        final List<Receipt> receipts = trainService.getUsersAndSeatsBySectionCode(sectionCode);
        return new ResponseEntity<>(receipts,HttpStatus.OK);
    }

    @DeleteMapping(value = REMOVE_USER_PATH)
    public ResponseEntity<HttpStatus> removeUserFromTrainByUserId(@PathVariable int userId) throws Exception {
        trainService.removeUserFromTrain(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = UPDATE_SEAT_PATH)
    public ResponseEntity<Receipt> updateUserSeat(@RequestBody Receipt receipt) throws Exception {
        Receipt updatedReceipt = trainService.updateAndBookTicket(receipt);
        return new ResponseEntity<>(updatedReceipt,HttpStatus.OK);
    }

}

