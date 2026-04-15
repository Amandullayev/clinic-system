const API = "/api/patients";
import { headers, handleUnauthorized } from "./apiClient.js";

export const getAllPatients = async () => {
  const res = await fetch(API, { headers: headers() });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Bemorlarni yuklashda xato");
  const data = await res.json();
  return data.data || [];
};

export const getPatientById = async (id) => {
  const res = await fetch(`${API}/${id}`, { headers: headers() });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Bemor topilmadi");
  const data = await res.json();
  return data.data;
};

export const createPatient = async (form) => {
  const res = await fetch(API, {
    method: "POST",
    headers: { "Content-Type": "application/json", ...headers() },
    body: JSON.stringify(form),
  });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Bemor qo'shishda xato");
  return res.json();
};

export const updatePatient = async (id, form) => {
  const res = await fetch(`${API}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json", ...headers() },
    body: JSON.stringify(form),
  });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Bemorni yangilashda xato");
  return res.json();
};

export const deletePatient = async (id) => {
  const res = await fetch(`${API}/${id}`, {
    method: "DELETE",
    headers: headers(),
  });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Bemorni o'chirishda xato");
};