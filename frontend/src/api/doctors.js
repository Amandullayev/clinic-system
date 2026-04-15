const API = "/api/doctors";
import { headers, handleUnauthorized } from "./apiClient.js";

export const getAllDoctors = async () => {
  const res = await fetch(API, { headers: headers() });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Shifokorlarni yuklashda xato");
  const data = await res.json();
  return data.data || [];
};

export const registerDoctorUser = async (fullName, email, password) => {
  const res = await fetch("/api/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      fullName,
      email,
      password,
      role: "DOCTOR",
    }),
  });
  if (!res.ok) throw new Error("Foydalanuvchi yaratishda xato");
  return res.json();
};

export const createDoctor = async (form) => {
  const res = await fetch(API, {
    method: "POST",
    headers: { "Content-Type": "application/json", ...headers() },
    body: JSON.stringify(form),
  });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Shifokor qo'shishda xato");
  return res.json();
};

export const updateDoctor = async (id, form) => {
  const res = await fetch(`${API}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json", ...headers() },
    body: JSON.stringify(form),
  });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Shifokorni yangilashda xato");
  return res.json();
};

export const deleteDoctor = async (id) => {
  const res = await fetch(`${API}/${id}`, {
    method: "DELETE",
    headers: headers(),
  });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Shifokorni o'chirishda xato");
};