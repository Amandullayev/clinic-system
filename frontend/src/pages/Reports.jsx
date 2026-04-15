import { useState, useEffect } from "react";
import Layout from "../components/layout/Layout";
import { getReports } from "../api/reports";
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from "recharts";
import "../styles/reports.css";

const COLORS_APPOINTMENT = ["#0D7377", "#2563EB", "#EF4444"];
const COLORS_PAYMENT = ["#16A34A", "#EF4444"];

export default function Reports() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getReports().then(d => {
      setData(d);
      setLoading(false);
    });
  }, []);

  if (loading) return <Layout title="Hisobotlar"><p>Yuklanmoqda...</p></Layout>;

  return (
    <Layout title="Hisobotlar">
      <h2>Umumiy hisobot</h2>

      <div>
        <p>Bemorlar: {data?.totalPatients}</p>
        <p>Shifokorlar: {data?.totalDoctors}</p>
      </div>
    </Layout>
  );
}