package com.trainapi.service;

import com.trainapi.constants.TrainApiConstants;
import com.trainapi.exceptions.ResourceNotFoundException;
import com.trainapi.exceptions.SeatNotAvailableException;
import com.trainapi.model.Receipt;
import com.trainapi.model.Seat;
import com.trainapi.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class TrainServiceTest {

    @Mock
    private ReceiptService receiptService;

    @Mock
    private SeatService seatService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TrainService trainService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void bookTicketByReceiptWithAvailableSeat() throws Exception {
        Receipt receipt = new Receipt();
        receipt.setUser(new User());
        List<Seat> availableSeats = Collections.singletonList(new Seat());
        when(seatService.getAllAvailableSeats()).thenReturn(availableSeats);
        when(receiptService.saveReceipt(any())).thenReturn(receipt);
        Receipt result = trainService.bookTicketByReceipt(new Receipt());
        assertNotNull(result);
    }

    @Test
    void bookTicketByReceiptWithSpecifiedSeat() throws Exception {
        Receipt receipt = new Receipt();
        Seat seat = new Seat();
        receipt.setUser(new User());
        receipt.setSeat(seat);
        seat.setSeatStatus(TrainApiConstants.AVAILABLE);
        when(seatService.getAllAvailableSeats()).thenReturn(Collections.singletonList(seat));
        when(seatService.findSeatBySectionAndSeatNumber(any(), anyInt())).thenReturn(seat);
        when(receiptService.saveReceipt(any())).thenReturn(receipt);
        Receipt result = trainService.bookTicketByReceipt(receipt);
        assertNotNull(result);
    }

    @Test
    void getUsersAndSeatsBySectionCode() {
        char sectionCode = 'A';
        Seat seat1 = new Seat(1,1,'A',TrainApiConstants.BOOKED);
        Seat seat2 = new Seat(2,2,'A',TrainApiConstants.BOOKED);
        List<Seat> seats = Arrays.asList(seat1, seat2);
        List<Integer> seatIds = Arrays.asList(1, 2);
        Receipt receipt1 = new Receipt(1, "Start1", "Destination1", 100, new User(), seat1);
        Receipt receipt2 = new Receipt(2, "Start2", "Destination2", 150, new User(), seat2);
        List<Receipt> expectedReceipts = Arrays.asList(receipt1, receipt2);
        when(seatService.findAllSeatsBySectionAndStatus(sectionCode, TrainApiConstants.BOOKED)).thenReturn(seats);
        when(receiptService.findAllReceiptsUsingSeatIds(seatIds)).thenReturn(expectedReceipts);
        List<Receipt> result = trainService.getUsersAndSeatsBySectionCode(sectionCode);
        assertEquals(expectedReceipts, result);
    }
    @Test
    void removeUserFromTrain() throws Exception {
        int userId = 1;
        Receipt receipt = new Receipt();
        receipt.setSeat(new Seat(1, 1, 'A', TrainApiConstants.BOOKED));
        when(receiptService.findReceiptByUserId(userId)).thenReturn(Optional.of(receipt));
        trainService.removeUserFromTrain(userId);
        verify(seatService, times(2)).saveSeat(any());
        verify(receiptService, times(1)).deleteReceipt(receipt);
        verify(userService, times(1)).removeUser(userId);
    }

    @Test
    void removeUserFromTrainReceiptNotFound() {
        int userId = 1;
        when(receiptService.findReceiptByUserId(userId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> trainService.removeUserFromTrain(userId));
        verify(seatService, never()).saveSeat(any());
        verify(receiptService, never()).deleteReceipt(any());
        verify(userService, never()).removeUser(anyInt());
    }

    @Test
    void removeUserFromTrainExceptionThrown() {
        int userId = 1;
        Receipt receipt = new Receipt();
        receipt.setSeat(new Seat(1, 1, 'A', TrainApiConstants.BOOKED));
        when(receiptService.findReceiptByUserId(userId)).thenReturn(Optional.of(receipt));
        doThrow(new RuntimeException("Simulating an exception")).when(seatService).saveSeat(any());
        assertThrows(Exception.class, () -> trainService.removeUserFromTrain(userId));
        verify(seatService, times(1)).saveSeat(any());
        verify(receiptService, never()).deleteReceipt(any());
        verify(userService, never()).removeUser(anyInt());
    }

    @Test
    void updateAndBookTicket() {
        int receiptId = 1;
        Receipt updateReceipt = new Receipt();
        updateReceipt.setReceiptId(receiptId);
        updateReceipt.setSeat(new Seat(1, 1, 'A', TrainApiConstants.BOOKED));
        Receipt oldReceipt = new Receipt();
        oldReceipt.setReceiptId(receiptId);
        oldReceipt.setSeat(new Seat(1, 1, 'A', TrainApiConstants.BOOKED));
        when(receiptService.getReceiptById(receiptId)).thenReturn(Optional.of(oldReceipt).get());
        when(seatService.findSeatBySectionAndSeatNumber(any(),anyInt()))
                .thenReturn(new Seat(1, 1, 'A', TrainApiConstants.BOOKED));

        assertThrows(SeatNotAvailableException.class, () -> trainService.updateAndBookTicket(updateReceipt));

    }

    @Test
    void updateAndBookTicketWithNullUpdateReceipt() {
        Receipt updateReceipt = null;
        assertThrows(IllegalArgumentException.class, () -> trainService.updateAndBookTicket(updateReceipt));
        verifyNoInteractions(receiptService, seatService, userService);
    }
}
