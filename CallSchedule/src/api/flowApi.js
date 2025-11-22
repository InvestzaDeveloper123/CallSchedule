import axios from "axios";

const API = "http://localhost:8081/api/flow";

export const startFlow = (fullName) => axios.post(`${API}/start`, { fullName });

export const sendOtp = (userId, mobile) => {
  console.log("BODY SENDING:", { userId, mobile });

  return axios.post(`${API}/send-otp`, { userId, mobile });
};

export const verifyOtp = (userId, otp) =>
  axios.post(`${API}/verify-otp`, { userId, otp });

export const selectInvestment = (userId, investmentRange) =>
  axios.post(`${API}/investment`, { userId, investmentRange });

export const checkSlot = (date, time) =>
  axios.get(`${API}/check-slot`, { params: { date, time } });

export const createBooking = (payload) =>
  axios.post(`${API}/create-booking`, payload);

export const getBookings = () =>
  axios.get(`${API}/bookings`);
