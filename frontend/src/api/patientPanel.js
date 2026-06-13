
import { headers, handleUnauthorized } from "./apiClient.js";
const API = "/api/patient";

export const getMyAppointments = async () => {
  const res = await fetch(`${API}/my-appointments`, { headers: headers() });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Navbatlarni yuklashda xato");
  const data = await res.json();
  return data.data || [];
};

export const bookAppointment = async (request) => {
  const res = await fetch(`${API}/appointments`, {
    method: "POST",
    headers: { ...headers(), "Content-Type": "application/json" },
    body: JSON.stringify(request),
  });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Navbat olishda xato");
  const data = await res.json();
  return data.data;
};

export const cancelAppointment = async (id) => {
  const res = await fetch(`${API}/appointments/${id}`, {
    method: "DELETE",
    headers: headers(),
  });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Navbatni bekor qilishda xato");
};

export const getAvailableSlots = async (doctorId, date) => {
  const res = await fetch(`/api/patient/doctors/${doctorId}/available-slots?date=${date}`, {
    headers: headers(),
  });
  if (res.status === 401) { handleUnauthorized(); return []; }
  const data = await res.json();
  return data.data || [];
};