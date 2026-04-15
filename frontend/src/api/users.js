import { headers, handleUnauthorized } from "./apiClient.js";

const API = "/api/users";

export const getAllUsers = async () => {
  const res = await fetch(API, { headers: headers() });
  handleUnauthorized(res);
  const data = await res.json();
  return data.data || [];
};

export const createUser = async (form) => {
  const res = await fetch(API, {
    method: "POST",
    headers: { "Content-Type": "application/json", ...headers() },
    body: JSON.stringify(form),
  });
  handleUnauthorized(res);
  if (!res.ok) { const e = await res.json(); throw new Error(e.message || "Xato"); }
  return res.json();
};

export const updateUser = async (id, form) => {
  const res = await fetch(`${API}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json", ...headers() },
    body: JSON.stringify(form),
  });
  handleUnauthorized(res);
  if (!res.ok) { const e = await res.json(); throw new Error(e.message || "Xato"); }
  return res.json();
};

export const toggleUserActive = async (id) => {
  const res = await fetch(`${API}/${id}/toggle`, {
    method: "PATCH",
    headers: headers(),
  });
  handleUnauthorized(res);
  return res.json();
};

export const deleteUser = async (id) => {
  await fetch(`${API}/${id}`, { method: "DELETE", headers: headers() });
};