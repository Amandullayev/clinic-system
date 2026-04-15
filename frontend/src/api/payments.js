const API = "/api/payments";
import { headers, handleUnauthorized } from "./apiClient.js";

export const getAllPayments = async () => {
  const res = await fetch(API, { headers: headers() });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("To'lovlarni yuklashda xato");
  const data = await res.json();
  return data.data || [];
};

export const getPaymentById = async (id) => {
  const res = await fetch(`${API}/${id}`, { headers: headers() });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("To'lov topilmadi");
  const data = await res.json();
  return data.data;
};

export const createPayment = async (form) => {
  const res = await fetch(API, {
    method: "POST",
    headers: { "Content-Type": "application/json", ...headers() },
    body: JSON.stringify(form),
  });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("To'lov qo'shishda xato");
  return res.json();
};

export const refundPayment = async (id) => {
  const res = await fetch(`${API}/${id}/refund`, {
    method: "PATCH",
    headers: headers(),
  });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("To'lovni qaytarishda xato");
  return res.json();
};

export const getPaymentsByMethod = async (method) => {
  const res = await fetch(`${API}/method/${method}`, { headers: headers() });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("To'lovlarni filtirlashda xato");
  const data = await res.json();
  return data.data || [];
};