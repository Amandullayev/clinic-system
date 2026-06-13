const API = "/api/settings";
import { headers, handleUnauthorized } from "./apiClient.js";

export const changePassword = async ({ oldPassword, newPassword }) => {
  const res = await fetch("/api/auth/change-password", {
    method: "POST",
    headers: { "Content-Type": "application/json", ...headers() },
    body: JSON.stringify({ oldPassword, newPassword }),
  });
  if (!res.ok) {
    const e = await res.json();
    throw new Error(e.message || "Parolni o'zgartirib bo'lmadi");
  }
  return res.json();
};

export const getSettings = async () => {
  const res = await fetch(API, { headers: headers() });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Sozlamalarni yuklashda xato");
  const data = await res.json();
  return data.data || {};
};

export const updateSettings = async (form) => {
  const res = await fetch(API, {
    method: "PUT",
    headers: { "Content-Type": "application/json", ...headers() },
    body: JSON.stringify(form),
  });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Sozlamalarni yangilashda xato");
  return res.json();
};