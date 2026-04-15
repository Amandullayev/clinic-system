import { headers, handleUnauthorized } from "./apiClient.js";

export const getDashboardData = async () => {
  const res = await fetch("/api/dashboard", { headers: headers() });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Dashboard ma'lumotlarini yuklashda xato");
  const data = await res.json();
  return data.data || {};
};