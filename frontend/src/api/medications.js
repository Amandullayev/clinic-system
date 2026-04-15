const API = "/api/medications";
import { headers, handleUnauthorized } from "./apiClient.js";

export const getAllMedications = async () => {
  const res = await fetch(API, { headers: headers() });
  handleUnauthorized(res);
  const data = await res.json();
  return data.data || [];
};

export const getLowStockMedications = async () => {
  const res = await fetch(`${API}/low-stock`, { headers: headers() });
  handleUnauthorized(res);
  const data = await res.json();
  return data.data || [];
};

export const createMedication = async (form) => {
  const res = await fetch(API, {
    method: "POST",
    headers: { "Content-Type": "application/json", ...headers() },
    body: JSON.stringify(form),
  });
  return res.json();
};

export const updateMedication = async (id, form) => {
  const res = await fetch(`${API}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json", ...headers() },
    body: JSON.stringify(form),
  });
  return res.json();
};

export const deleteMedication = async (id) => {
  await fetch(`${API}/${id}`, {
    method: "DELETE",
    headers: headers(),
  });
};