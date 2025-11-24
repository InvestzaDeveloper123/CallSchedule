package com.example.demo.Services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Models.Booking;
import com.example.demo.Models.UserTemp;
import com.example.demo.Repositories.BookingRepository;
import com.example.demo.Repositories.UserTempRepository;
import com.example.demo.dto.CheckSlotResponse;
import com.example.demo.dto.CreateBookingRequest;
import com.example.demo.dto.InvestmentRequest;
import com.example.demo.dto.SendOtpRequest;
import com.example.demo.dto.StartRequest;
import com.example.demo.dto.VerifyOtpRequest;
import com.example.demo.util.OtpUtil;

@Service
public class UserFlowService {

    private final UserTempRepository userTempRepository;
    private final BookingRepository bookingRepository;
    private final SmsService smsService;

    @Autowired
    public UserFlowService(
            UserTempRepository userTempRepository,
            BookingRepository bookingRepository,
            SmsService smsService) {

        this.userTempRepository = userTempRepository;
        this.bookingRepository = bookingRepository;
        this.smsService = smsService;
    }

    // STEP 1
    public UserTemp start(StartRequest req) {
        UserTemp u = UserTemp.builder()
                .fullName(req.getFullName())
                .otpVerified(false)
                .createdAt(LocalDateTime.now())
                .build();

        return userTempRepository.save(u);
    }

    // STEP 2
    @Transactional
    public UserTemp sendOtp(SendOtpRequest req) {

        UserTemp u = null;

        if (req.getUserId() != null && !req.getUserId().isBlank()) {
            u = userTempRepository.findById(req.getUserId()).orElse(null);
        }

        if (u == null && req.getMobile() != null && !req.getMobile().isBlank()) {
            u = userTempRepository.findByMobile(req.getMobile()).orElse(null);
        }

        if (u == null) {
            u = UserTemp.builder()
                    .mobile(req.getMobile())
                    .otpVerified(false)
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        u.setMobile(req.getMobile());
        String otp = OtpUtil.generate4DigitOtp();

        u.setOtp(otp);
        u.setOtpVerified(false);
        u.setOtpGeneratedAt(LocalDateTime.now());
        userTempRepository.save(u);

        // Call the service with only mobile and otp
        smsService.sendSms(u.getMobile(), otp);

        return u;
    }

    // STEP 2b
    @Transactional
    public boolean verifyOtp(VerifyOtpRequest req) {
        // Fix: Prevent IllegalArgumentException if userId is null/empty
        if (req.getUserId() == null || req.getUserId().isBlank()) {
            return false;
        }
        
        Optional<UserTemp> optional = userTempRepository.findById(req.getUserId());
        if (optional.isEmpty()) return false;

        UserTemp u = optional.get();

        if (u.getOtp() == null) return false;

        if (u.getOtpGeneratedAt() != null &&
                u.getOtpGeneratedAt().plusMinutes(3).isBefore(LocalDateTime.now())) {
            return false; // OTP Expired
        }

        // Fix: Use trim() on the incoming OTP to prevent failures due to whitespace
        if (u.getOtp().equals(req.getOtp().trim())) {
            u.setOtpVerified(true);
            userTempRepository.save(u);
            return true; // OTP Matched
        }

        return false; // OTP Invalid
    }

    // STEP 3
    @Transactional
    public boolean setInvestment(InvestmentRequest req) {
        UserTemp u = userTempRepository.findById(req.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        u.setInvestmentRange(req.getInvestmentRange());
        userTempRepository.save(u);

        return !"BELOW_50L".equalsIgnoreCase(req.getInvestmentRange());
    }

    // STEP 4
    public CheckSlotResponse checkSlot(LocalDate date, LocalTime time) {
        long count = bookingRepository.countByDateAndTime(date, time);
        return new CheckSlotResponse(count < 5, count);
    }

    // STEP 5
    @Transactional
    public Booking createBooking(CreateBookingRequest req) {

        UserTemp u = userTempRepository.findById(req.getUserId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (!u.isOtpVerified()) {
            throw new IllegalStateException("OTP not verified");
        }

        if ("BELOW_50L".equalsIgnoreCase(u.getInvestmentRange())) {
            throw new IllegalStateException("User not eligible");
        }

        long count = bookingRepository.countByDateAndTime(req.getDate(), req.getTime());
        if (count >= 5) {
            throw new IllegalStateException("Slot full");
        }

        Booking b = Booking.builder()
                .fullName(u.getFullName())
                .mobile(u.getMobile())
                .email(req.getEmail())
                .guestEmail(req.getGuestEmail())
                .message(req.getMessage())
                .investmentRange(u.getInvestmentRange())
                .date(req.getDate())
                .time(req.getTime())
                .createdAt(LocalDateTime.now())
                .build();

        return bookingRepository.save(b);
    }

    // Fix: Add safety check to prevent IllegalArgumentException when ID is null
    public UserTemp getUser(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        return userTempRepository.findById(id).orElse(null);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}