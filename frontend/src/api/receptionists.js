const API = "/api/receptionists";
import { headers, handleUnauthorized } from "./apiClient.js";

export const getAllReceptionists = async () => {
  const res = await fetch(API, { headers: headers() });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Qabulxona xodimlarini yuklashda xato");
  const data = await res.json();
  return data.data || [];
};

export const registerReceptionistUser = async (fullName, email, password) => {
  const res = await fetch("/api/auth/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ fullName, email, password, role: "RECEPTIONIST" }),
  });
  if (!res.ok) throw new Error("Foydalanuvchi yaratishda xato");
  return res.json();
};

export const deleteReceptionist = async (id) => {
  const res = await fetch(`${API}/${id}`, {
    method: "DELETE",
    headers: headers(),
  });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Xodimni o'chirishda xato");
};