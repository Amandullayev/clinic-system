const API = "/api/services";
import { headers, handleUnauthorized } from "./apiClient.js";

export const getAllServices = async () => {
  const res = await fetch(API, { headers: headers() });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Xizmatlarni yuklashda xato");
  const data = await res.json();
  return data.data || [];
};

export const getServiceById = async (id) => {
  const res = await fetch(`${API}/${id}`, { headers: headers() });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Xizmat topilmadi");
  const data = await res.json();
  return data.data;
};

export const createService = async (form) => {
  const res = await fetch(API, {
    method: "POST",
    headers: { "Content-Type": "application/json", ...headers() },
    body: JSON.stringify(form),
  });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Xizmat qo'shishda xato");
  return res.json();
};

export const updateService = async (id, form) => {
  const res = await fetch(`${API}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json", ...headers() },
    body: JSON.stringify(form),
  });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Xizmatni yangilashda xato");
  return res.json();
};

export const deleteService = async (id) => {
  const res = await fetch(`${API}/${id}`, {
    method: "DELETE",
    headers: headers(),
  });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Xizmatni o'chirishda xato");
};