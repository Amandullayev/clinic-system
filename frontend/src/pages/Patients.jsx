import { useState, useEffect } from "react";
import Layout from "../components/layout/Layout";
import { getAllPatients, createPatient, updatePatient, deletePatient } from "../api/patients";
import "../styles/patients.css";

const emptyForm = {
  fullName: "", phone: "", email: "",
  birthDate: "", address: "", gender: "",
};

function getInitials(name) {
  if (!name) return "?";
  return name.split(" ").map(n => n[0]).join("").slice(0, 2).toUpperCase();
}

function formatId(id) {
  return "#" + String(id).padStart(3, "0");
}

function formatDate(dateStr) {
  if (!dateStr) return "—";
  return new Date(dateStr).toLocaleDateString("uz-UZ");
}

export default function Patients() {
  const [patients, setPatients] = useState([]);
  const [filtered, setFiltered] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editId, setEditId] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState("");

  useEffect(() => { fetchAll(); }, []);

  useEffect(() => {
    const q = search.toLowerCase();
    setFiltered(
      patients.filter(p =>
        p.fullName?.toLowerCase().includes(q) ||
        p.phone?.includes(q) ||
        String(p.id).includes(q)
      )
    );
  }, [search, patients]);

  const fetchAll = async () => {
    setLoading(true);
    try {
      const data = await getAllPatients();
      setPatients(data);
      setFiltered(data);
    } finally {
      setLoading(false);
    }
  };

  const openAdd = () => {
    setForm(emptyForm);
    setEditId(null);
    setError("");
    setShowModal(true);
  };

  const openEdit = (p) => {
    setForm({
      fullName: p.fullName || "",
      phone: p.phone || "",
      email: p.email || "",
      birthDate: p.birthDate || "",
      address: p.address || "",
      gender: p.gender || "",
    });
    setEditId(p.id);
    setError("");
    setShowModal(true);
  };

  const handleSave = async () => {
    if (!form.fullName || !form.phone) {
      setError("Ism va telefon raqam majburiy");
      return;
    }
    const res = editId
      ? await updatePatient(editId, form)
      : await createPatient(form);

    if (res.success) {
      setShowModal(false);
      fetchAll();
    } else {
      setError(res.message || "Xato yuz berdi");
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Rostdan ham o'chirasizmi?")) return;
    await deletePatient(id);
    fetchAll();
  };

  return (
    <Layout title="Bemorlar">

      <div className="page-header">
        <div>
          <h2>Bemorlar</h2>
          <p className="page-subtitle">Jami {patients.length} ta bemor ro'yxatdan o'tgan</p>
        </div>
        <button className="btn-add" onClick={openAdd}>+ Yangi bemor</button>
      </div>

      <div className="search-bar">
        <input
          placeholder="ID, ism yoki telefon raqam bo'yicha qidirish..."
          value={search}
          onChange={e => setSearch(e.target.value)}
        />
      </div>

      <div className="table-wrapper">
        {loading ? (
          <p className="loading">Yuklanmoqda...</p>
        ) : (
          <>
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>F.I.O</th>
                  <th>Tug'ilgan sana</th>
                  <th>Telefon</th>
                  <th>So'nggi tashrif</th>
                  <th>Jami tashrif</th>
                  <th>Holat</th>
                  <th>Amallar</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map(p => (
                  <tr key={p.id}>
                    <td className="patient-id">{formatId(p.id)}</td>
                    <td>
                      <div className="patient-name-cell">
                        <div className="patient-avatar">{getInitials(p.fullName)}</div>
                        <span>{p.fullName}</span>
                      </div>
                    </td>
                    <td>{formatDate(p.birthDate)}</td>
                    <td>{p.phone}</td>
                    <td>{formatDate(p.lastVisitDate)}</td>
                    <td>{p.totalVisits ?? 0}</td>
                    <td>
                      <span className={p.active ? "status-active" : "status-inactive"}>
                        {p.active ? "Faol" : "Nofaol"}
                      </span>
                    </td>
                    <td>
                      <button className="btn-edit" onClick={() => openEdit(p)}>Tahrirlash</button>
                      <button className="btn-delete" onClick={() => handleDelete(p.id)}>O'chirish</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <div className="pagination">
              <span>1-{filtered.length} / {filtered.length}</span>
              <span>1</span>
            </div>
          </>
        )}
      </div>

      {showModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>{editId ? "Bemor tahrirlash" : "Bemor qo'shish"}</h3>
            {error && <p className="error-msg">{error}</p>}
            <div className="form-group">
              <label>Ism Familiya</label>
              <input
                value={form.fullName}
                onChange={e => setForm({ ...form, fullName: e.target.value })}
                placeholder="Abdullayev Jamshid"
              />
            </div>
            <div className="form-group">
              <label>Telefon raqam</label>
              <input
                value={form.phone}
                onChange={e => setForm({ ...form, phone: e.target.value })}
                placeholder="+998 90 123 45 67"
              />
            </div>
            <div className="form-group">
              <label>Email</label>
              <input
                value={form.email}
                onChange={e => setForm({ ...form, email: e.target.value })}
                placeholder="example@mail.com"
              />
            </div>
            <div className="form-group">
              <label>Tug'ilgan sana</label>
              <input
                type="date"
                value={form.birthDate}
                onChange={e => setForm({ ...form, birthDate: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label>Jinsi</label>
              <select value={form.gender} onChange={e => setForm({ ...form, gender: e.target.value })}>
                <option value="">Tanlang</option>
                <option value="MALE">Erkak</option>
                <option value="FEMALE">Ayol</option>
              </select>
            </div>
            <div className="form-group">
              <label>Manzil</label>
              <input
                value={form.address}
                onChange={e => setForm({ ...form, address: e.target.value })}
                placeholder="Toshkent, Chilonzor"
              />
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