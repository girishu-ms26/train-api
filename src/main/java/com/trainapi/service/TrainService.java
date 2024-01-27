package com.trainapi.service;

import com.trainapi.constants.TrainApiConstants;
import com.trainapi.exceptions.ResourceNotFoundException;
import com.trainapi.exceptions.SeatNotAvailableException;
import com.trainapi.model.Receipt;
import com.trainapi.model.Seat;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class TrainService {
    private final ReceiptService receiptService;
    private final SeatService seatService;
    private final UserService userService;

    @Transactional
    public Receipt bookTicketByReceipt(Receipt receipt) throws Exception {
        if (receipt.getSeat() == null) {
            return bookTicketWithAvailableSeat(receipt);
        } else {
            return bookTicketWithSpecifiedSeat(receipt);
        }
    }

    private Receipt bookTicketWithAvailableSeat(Receipt receipt) {
        List<Seat> availableSeats = seatService.getAllAvailableSeats();
        if (availableSeats.isEmpty()) {
            throw new SeatNotAvailableException("No available seats");
        }
        Seat seat = availableSeats.stream()
                .findAny().get();
        return bookTicket(receipt, seat);
    }

    private Receipt bookTicketWithSpecifiedSeat(Receipt receipt) {
        Seat seat = seatService.findSeatBySectionAndSeatNumber(
                receipt.getSeat().getSection(),
                receipt.getSeat().getSeatNumber());
        if(seat.getSeatStatus().equals(TrainApiConstants.BOOKED)) {
            throw new SeatNotAvailableException("Seat not available");
        }
        return bookTicket(receipt, seat);
    }

    private Receipt bookTicket(Receipt receipt, Seat seat) {
        seat.setSeatStatus(TrainApiConstants.BOOKED);
        receipt.setSeat(seat);
        receipt = receiptService.saveReceipt(receipt);
        receipt.getUser().setSeat(seat);
        userService.saveUser(receipt.getUser());
        return receipt;
    }

    public Receipt getReceiptById(int receiptId) {
        return receiptService.getReceiptById(receiptId);
    }

    @Transactional
    public List<Receipt> getUsersAndSeatsBySectionCode(Character sectionCode) {
        List<Seat> seats = seatService.findAllSeatsBySectionAndStatus(sectionCode, TrainApiConstants.BOOKED);
        if(seats.isEmpty()) {
            throw new ResourceNotFoundException("No seats to show");
        }
        List<Integer> seatIds = seats.parallelStream()
                .map(Seat::getSeatId)
                .collect(Collectors.toList());

        return receiptService.findAllReceiptsUsingSeatIds(seatIds);
    }

    @Transactional
    public void removeUserFromTrain(int userId) throws Exception {
        Receipt receipt = receiptService.findReceiptByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with userId "+userId+" not found"));
        try {
            updateSeatStatus(receipt.getSeat(), TrainApiConstants.AVAILABLE);
            userService.removeUser(userId);
            receiptService.deleteReceipt(receipt);
            seatService.saveSeat(receipt.getSeat());
        } catch (Exception e) {
            log.error("Error while removing user from train", e);
            throw new Exception("Error while removing user from train", e);
        }
    }

    @Transactional
    public Receipt updateAndBookTicket(Receipt updateReceipt) throws Exception {
        if (updateReceipt == null) {
            throw new IllegalArgumentException("updateReceipt cannot be null");
        }
        Receipt oldReceipt = receiptService.getReceiptById(updateReceipt.getReceiptId());
        if (oldReceipt == null) {
            throw new ResourceNotFoundException("Receipt not found");
        }
        if (updateReceipt.equals(oldReceipt)) {
            throw new ResourceNotFoundException("Nothing to update");
        }
        Receipt newReceipt = new Receipt(oldReceipt.getReceiptId(),
                oldReceipt.getStart(),
                oldReceipt.getDestination(),
                oldReceipt.getPricePaid(),
                oldReceipt.getUser(),
                oldReceipt.getSeat());
        Seat oldSeat = oldReceipt.getSeat();
        Seat newSeat = updateReceipt.getSeat();
        if (oldSeat.getSection() != newSeat.getSection() || !Objects.equals(oldSeat.getSeatNumber(), newSeat.getSeatNumber())) {
            if (oldSeat.getSection() != newSeat.getSection()) {
                newSeat.setSection(updateReceipt.getSeat().getSection());
            }
            if (!Objects.equals(oldSeat.getSeatNumber(), newSeat.getSeatNumber())) {
                newSeat.setSeatNumber(updateReceipt.getSeat().getSeatNumber());
            }
        }
        newReceipt.setSeat(newSeat);
        updateSeatStatus(oldSeat, TrainApiConstants.AVAILABLE);
        return bookTicketByReceipt(newReceipt);
    }

    private void updateSeatStatus(Seat seat, String status) {
        seat.setSeatStatus(status);
        seatService.saveSeat(seat);
    }
}