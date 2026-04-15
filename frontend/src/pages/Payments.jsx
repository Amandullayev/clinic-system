import { useState, useEffect } from "react";
import Layout from "../components/layout/Layout";
import { getAllPayments, createPayment, refundPayment } from "../api/payments";
import { getAllAppointments } from "../api/appointments";
import "../styles/payment.css";

const emptyForm = { appointmentId: "", amount: "", paymentMethod: "CASH" };

const methodLabel = { CASH: "Naqd", CARD: "Karta", TRANSFER: "O'tkazma" };
const statusLabel = { PAID: "To'langan", REFUNDED: "Qaytarilgan", PENDING: "Kutilmoqda" };
const statusColor = { PAID: "#16A34A", REFUNDED: "#EF4444", PENDING: "#F59E0B" };

export default function Payments() {
  const [payments, setPayments] = useState([]);
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState("");
  useEffect(() => {
    fetchAll();
    loadAppointments();
  }, []);

  const fetchAll = async () => {
    setLoading(true);
    try {
      const data = await getAllPayments();
      setPayments(data);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const loadAppointments = async () => {
    try {
      const data = await getAllAppointments();
      setAppointments(data);
    } catch {
      setAppointments([]);
    }
  };

  const openAdd = () => {
    setForm(emptyForm);
    setError("");
    setShowModal(true);
  };

  const handleSave = async () => {
    if (!form.appointmentId || !form.amount) {
      setError("Qabul va summa majburiy");
      return;
    }
    try {
      await createPayment({
        appointmentId: Number(form.appointmentId),
        amount: Number(form.amount),
        paymentMethod: form.paymentMethod,
      });
      setShowModal(false);
      fetchAll();
    } catch (e) {
      setError(e.message || "Xato yuz berdi");
    }
  };

  const handleRefund = async (id) => {
    if (!window.confirm("To'lovni qaytarasizmi?")) return;
    try {
      await refundPayment(id);
      fetchAll();
    } catch (e) {
      alert(e.message || "Xato yuz berdi");
    }
  };

  return (
    <Layout title="To'lovlar">
      <div className="page-header">
        <div>
          <h2>To'lovlar</h2>
          <p className="page-subtitle">Jami {payments.length} ta to'lov</p>
        </div>
        <button className="btn-add" onClick={openAdd}>+ To'lov qo'shish</button>
      </div>

      <div className="table-wrapper">
        {loading ? (
          <p className="loading">Yuklanmoqda...</p>
        ) : payments.length === 0 ? (
          <p className="empty-text">To'lovlar mavjud emas</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Bemor</th>
                <th>Xizmat</th>
                <th>Summa</th>
                <th>To'lov usuli</th>
                <th>Status</th>
                <th>Sana</th>
                <th>Amallar</th>
              </tr>
            </thead>
            <tbody>
              {payments.map((p, i) => (
                <tr key={p.id}>
                  <td>{i + 1}</td>
                  <td>{p.patientName}</td>
                  <td>{p.serviceName}</td>
                  <td style={{ fontWeight: 600, color: "#16A34A" }}>{p.amount?.toLocaleString()} so'm</td>
                  <td>{methodLabel[p.paymentMethod] || p.paymentMethod}</td>
                  <td style={{ color: statusColor[p.status], fontWeight: 600 }}>
                    {statusLabel[p.status] || p.status}
                  </td>
                  <td>{p.paidAt ? p.paidAt.slice(0, 10) : "—"}</td>
                  <td>
                    {p.status === "PAID" && (
                      <button className="btn-delete" onClick={() => handleRefund(p.id)}>
                        Qaytarish
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {showModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>To'lov qo'shish</h3>
            {error && <p className="error-msg">{error}</p>}
            <div className="form-group">
              <label>Qabul</label>
              <select
                value={form.appointmentId}
                onChange={e => setForm({ ...form, appointmentId: e.target.value })}
              >
                <option value="">— Tanlang —</option>
                {appointments.map(a => (
                  <option key={a.id} value={a.id}>
                    {a.patientName} — {a.doctorName} ({a.appointmentTime?.slice(0, 10)})
                  </option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Summa (so'm)</label>
              <input
                type="number"
                placeholder="Masalan: 150000"
                value={form.amount}
                onChange={e => setForm({ ...form, amount: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label>To'lov usuli</label>
              <select
                value={form.paymentMethod}
                onChange={e => setForm({ ...form, paymentMethod: e.target.value })}
              >
                <option value="CASH">Naqd</option>
                <option value="CARD">Karta</option>
                <option value="TRANSFER">O'tkazma</option>
              </select>
            </div>
            <div className="modal-actions">
              <button className="btn-cancel" onClick={() => setShowModal(false)}>Bekor qilish</button>
              <button className="btn-save" onClick={handleSave}>Saqlash</button>
            </div>
          </div>
        </div>
      )}
    </Layout>
  );
}