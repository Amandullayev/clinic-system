import { headers, handleUnauthorized } from "./apiClient.js";

const API = "/api/appointments";

export const getAllAppointments = async () => {
  const res = await fetch(API, { headers: headers() });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Qabullarni yuklashda xato");
  const data = await res.json();
  return data.data || [];
};

export const createAppointment = async (form) => {
  const res = await fetch(API, {
    method: "POST",
    headers: { "Content-Type": "application/json", ...headers() },
    body: JSON.stringify(form),
  });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Qabul qo'shishda xato");
  return res.json();
};

export const updateAppointment = async (id, form) => {
  const res = await fetch(`${API}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json", ...headers() },
    body: JSON.stringify(form),
  });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Qabulni yangilashda xato");
  return res.json();
};

export const deleteAppointment = async (id) => {
  const res = await fetch(`${API}/${id}`, {
    method: "DELETE",
    headers: headers(),
  });
  handleUnauthorized(res);
};

export const fetchPatientOptions = async () => {
  const res = await fetch("/api/patients", { headers: headers() });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Bemorlarni yuklashda xato");
  const data = await res.json();
  return data.data || [];
};

export const fetchDoctorOptions = async () => {
  const res = await fetch("/api/doctors", { headers: headers() });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Shifokorlarni yuklashda xato");
  const data = await res.json();
  return data.data || [];
};

export const fetchServiceOptions = async () => {
  const res = await fetch("/api/services", { headers: headers() });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Xizmatlarni yuklashda xato");
  const data = await res.json();
  return data.data || [];
};

export const fetchAppointmentOptions = async () => {
  const res = await fetch(API, { headers: headers() });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Qabullarni yuklashda xato");
  const data = await res.json();
  return data.data || [];
};