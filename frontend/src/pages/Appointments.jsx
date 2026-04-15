import { useState, useEffect } from "react";
import Layout from "../components/layout/Layout";
import {
  getAllAppointments,
  createAppointment,
  updateAppointment,
  deleteAppointment,
  fetchPatientOptions,
  fetchDoctorOptions,
  fetchServiceOptions,
} from "../api/appointments";
import "../styles/appointments.css";

const emptyForm = {
  patientId: "",
  doctorId: "",
  serviceId: "",
  appointmentTime: "",
  notes: "",
  status: "PENDING",
};

const STATUS_OPTIONS = [
  { value: "PENDING",   label: "Kutilmoqda",    cls: "badge-pending"   },
  { value: "CONFIRMED", label: "Tasdiqlangan",  cls: "badge-confirmed" },
  { value: "COMPLETED", label: "Yakunlangan",   cls: "badge-completed" },
  { value: "CANCELLED", label: "Bekor qilingan", cls: "badge-cancelled" },
];

function statusInfo(status) {
  return STATUS_OPTIONS.find(s => s.value === status)
    || { label: status, cls: "badge-pending" };
}

function getInitials(name) {
  if (!name) return "?";
  return name.split(" ").map(n => n[0]).join("").slice(0, 2).toUpperCase();
}

function formatDateTime(str) {
  if (!str) return "—";
  return str.replace("T", " ").slice(0, 16);
}

export default function Appointments() {
  const [appointments, setAppointments] = useState([]);
  const [filtered, setFiltered]         = useState([]);
  const [patients, setPatients]         = useState([]);
  const [doctors, setDoctors]           = useState([]);
  const [services, setServices]         = useState([]);
  const [search, setSearch]             = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [loading, setLoading]           = useState(true);
  const [showModal, setShowModal]       = useState(false);
  const [editId, setEditId]             = useState(null);
  const [form, setForm]                 = useState(emptyForm);
  const [error, setError]               = useState("");

  useEffect(() => {
    fetchAll();
    loadOptions();
  }, []);

  useEffect(() => {
    let result = appointments;
    if (statusFilter !== "ALL") {
      result = result.filter(a => a.status === statusFilter);
    }
    if (search.trim()) {
      const q = search.toLowerCase();
      result = result.filter(a =>
        a.patientName?.toLowerCase().includes(q) ||
        a.doctorName?.toLowerCase().includes(q) ||
        a.serviceName?.toLowerCase().includes(q)
      );
    }
    setFiltered(result);
  }, [search, statusFilter, appointments]);

  const fetchAll = async () => {
    setLoading(true);
    try {
      const data = await getAllAppointments();
      setAppointments(data);
      setFiltered(data);
    } finally {
      setLoading(false);
    }
  };

  const loadOptions = async () => {
    const [p, d, s] = await Promise.all([
      fetchPatientOptions(),
      fetchDoctorOptions(),
      fetchServiceOptions(),
    ]);
    setPatients(p);
    setDoctors(d);
    setServices(s);
  };

  const openAdd = () => {
    setForm(emptyForm);
    setEditId(null);
    setError("");
    setShowModal(true);
  };

  const openEdit = (a) => {
    setForm({
      patientId: a.patientId || "",
      doctorId: a.doctorId || "",
      serviceId: a.serviceId || "",
      appointmentTime: a.appointmentTime ? a.appointmentTime.slice(0, 16) : "",
      notes: a.notes || "",
      status: a.status || "PENDING",
    });
    setEditId(a.id);
    setError("");
    setShowModal(true);
  };

  const handleSave = async () => {
    if (!form.patientId || !form.doctorId || !form.appointmentTime) {
      setError("Bemor, shifokor va vaqt majburiy");
      return;
    }
    const res = editId
      ? await updateAppointment(editId, form)
      : await createAppointment(form);

    if (res.success) {
      setShowModal(false);
      fetchAll();
    } else {
      setError(res.message || "Xato yuz berdi");
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Qabulni o'chirasizmi?")) return;
    await deleteAppointment(id);
    fetchAll();
  };

  return (
    <Layout title="Qabullar">

      <div className="page-header">
        <div>
          <h2>Qabullar</h2>
          <p className="page-subtitle">Jami {appointments.length} ta qabul</p>
        </div>
        <button className="btn-add" onClick={openAdd}>+ Qabul qo'shish</button>
      </div>

      <div className="appt-toolbar">
        <input
          placeholder="Bemor, shifokor yoki xizmat bo'yicha qidirish..."
          value={search}
          onChange={e => setSearch(e.target.value)}
        />
        <select value={statusFilter} onChange={e => setStatusFilter(e.target.value)}>
          <option value="ALL">Barcha holat</option>
          {STATUS_OPTIONS.map(s => (
            <option key={s.value} value={s.value}>{s.label}</option>
          ))}
        </select>
      </div>

      <div className="table-wrapper">
        {loading ? (
          <p className="loading">Yuklanmoqda...</p>
        ) : filtered.length === 0 ? (
          <p className="empty-text">Qabul topilmadi</p>
        ) : (
          <>
            <table>
              <thead>
                <tr>
                  <th>#</th>
                  <th>Bemor</th>
                  <th>Shifokor</th>
                  <th>Xizmat</th>
                  <th>Vaqt</th>
                  <th>Holat</th>
                  <th>Amallar</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((a, i) => {
                  const s = statusInfo(a.status);
                  return (
                    <tr key={a.id}>
                      <td>{i + 1}</td>
                      <td>
                        <div className="patient-cell">
                          <div className="patient-avatar-sm">
                            {getInitials(a.patientName)}
                          </div>
                          <span>{a.patientName}</span>
                        </div>
                      </td>
                      <td>{a.doctorName}</td>
                      <td>{a.serviceName || "—"}</td>
                      <td>{formatDateTime(a.appointmentTime)}</td>
                      <td>
                        <span className={`status-badge ${s.cls}`}>
                          {s.label}
                        </span>
                      </td>
                      <td>
                        <button className="btn-edit" onClick={() => openEdit(a)}>
                          Tahrirlash
                        </button>
                        <button className="btn-delete" onClick={() => handleDelete(a.id)}>
                          O'chirish
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
            <div className="pagination">
              <span>1–{filtered.length} / {filtered.length}</span>
              <span>1</span>
            </div>
          </>
        )}
      </div>

      {showModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>{editId ? "Qabulni tahrirlash" : "Yangi qabul"}</h3>
            {error && <p className="error-msg">{error}</p>}

            <div className="form-group">
              <label>Bemor</label>
              <select
                value={form.patientId}
                onChange={e => setForm({ ...form, patientId: e.target.value })}
              >
                <option value="">Tanlang...</option>
                {patients.map(p => (
                  <option key={p.id} value={p.id}>{p.fullName}</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Shifokor</label>
              <select
                value={form.doctorId}
                onChange={e => setForm({ ...form, doctorId: e.target.value })}
              >
                <option value="">Tanlang...</option>
                {doctors.map(d => (
                  <option key={d.id} value={d.id}>
                    {d.fullName} — {d.specialization}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Xizmat</label>
              <select
                value={form.serviceId}
                onChange={e => setForm({ ...form, serviceId: e.target.value })}
              >
                <option value="">Tanlang...</option>
                {services.map(s => (
                  <option key={s.id} value={s.id}>{s.name}</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Qabul vaqti</label>
              <input
                type="datetime-local"
                value={form.appointmentTime}
                onChange={e => setForm({ ...form, appointmentTime: e.target.value })}
              />
            </div>

            {editId && (
              <div className="form-group">
                <label>Holat</label>
                <select
                  value={form.status}
                  onChange={e => setForm({ ...form, status: e.target.value })}
                >
                  {STATUS_OPTIONS.map(s => (
                    <option key={s.value} value={s.value}>{s.label}</option>
                  ))}
                </select>
              </div>
            )}

            <div className="form-group">
              <label>Izoh (ixtiyoriy)</label>
              <textarea
                value={form.notes}
                onChange={e => setForm({ ...form, notes: e.target.value })}
                placeholder="Qo'shimcha ma'lumot..."
              />
            </div>

            <div className="modal-actions">
              <button className="btn-cancel" onClick={() => setShowModal(false)}>
                Bekor qilish
              </button>
              <button className="btn-save" onClick={handleSave}>
                Saqlash
              </button>
            </div>
          </div>
        </div>
      )}

    </Layout>
  );
}