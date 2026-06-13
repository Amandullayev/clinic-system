const API = "/api/reports";
import { headers, handleUnauthorized } from "./apiClient.js";

export const getReports = async (from, to) => {
  const params = new URLSearchParams();
  if (from) params.append("from", from);
  if (to) params.append("to", to);
  const res = await fetch(`${API}?${params}`, { headers: headers() });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Hisobotlarni yuklashda xato");
  const data = await res.json();
  return data.data || {};
};