const API = "/api/reports";
import { headers, handleUnauthorized } from "./apiClient.js";

  export const getReports = async () => {
    const res = await fetch(API, { headers: headers() });
    handleUnauthorized(res);
    if (!res.ok) throw new Error("Hisobotlarni yuklashda xato");
    const data = await res.json();
    return data.data || {};
  };

export const getMonthlyRevenue = async () => {
  const res = await fetch(`${API}/monthly-revenue`, { headers: headers() });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Oylik daromadni yuklashda xato");
  const data = await res.json();
  return data.data || [];
};

export const getDoctorEfficiency = async () => {
  const res = await fetch(`${API}/doctor-efficiency`, { headers: headers() });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Shifokor samaradorligini yuklashda xato");
  const data = await res.json();
  return data.data || [];
};

export const getServiceStats = async () => {
  const res = await fetch(`${API}/service-stats`, { headers: headers() });
  handleUnauthorized(res);
  if (!res.ok) throw new Error("Xizmat statistikasini yuklashda xato");
  const data = await res.json();
  return data.data || [];
};