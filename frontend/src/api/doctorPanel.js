const API = "/api/doctor";
import { headers, handleUnauthorized } from "./apiClient.js";

export const getMyAppointments = async () => {
  const res = await fetch(`${API}/my-appointments`, { headers: headers() });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Navbatlarni yuklashda xato");
  const data = await res.json();
  return data.data || [];
};

export const getMyPatients = async () => {
  const res = await fetch(`${API}/my-patients`, { headers: headers() });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Bemorlarni yuklashda xato");
  const data = await res.json();
  return data.data || [];
};

export const updateAppointmentStatus = async (id, status) => {
  const res = await fetch(`${API}/appointments/${id}/status?status=${status}`, {
    method: "PATCH",
    headers: headers(),
  });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Statusni yangilashda xato");
  const data = await res.json();
  return data.data;
};

export const writeDiagnosis = async (id, data) => {
  const res = await fetch(`${API}/appointments/${id}/diagnose`, {
    method: "PATCH",
    headers: { ...headers(), "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Tashxis saqlashda xato");
  const json = await res.json();
  return json.data;
};