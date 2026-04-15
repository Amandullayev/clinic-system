import { useState, useEffect } from "react";
import Layout from "../components/layout/Layout";
import {
  getAllMedications,
  createMedication,
  updateMedication,
  deleteMedication,
} from "../api/medications";
import "../styles/medications.css";

const emptyForm = { name: "", category: "", quantity: "", minQuantity: "", unit: "", price: "" };

export default function Medications() {
  const [meds, setMeds] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [editId, setEditId] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState("");

  useEffect(() => { fetchAll(); }, []);

  const fetchAll = async () => {
    setLoading(true);
    try {
      const data = await getAllMedications();
      setMeds(data);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const filtered = meds.filter(m =>
    m.name?.toLowerCase().includes(search.toLowerCase()) ||
    m.category?.toLowerCase().includes(search.toLowerCase())
  );

  const openAdd = () => {
    setForm(emptyForm);
    setEditId(null);
    setError("");
    setShowModal(true);
  };

  const openEdit = (m) => {
    setForm({
      name: m.name || "",
      category: m.category || "",
      quantity: m.quantity ?? "",
      minQuantity: m.minQuantity ?? "",
      unit: m.unit || "",
      price: m.price ?? "",
    });
    setEditId(m.id);
    setError("");
    setShowModal(true);
  };

  const handleSave = async () => {
    if (!form.name || !form.quantity) {
      setError("Nomi va miqdori majburiy");
      return;
    }
    try {
      const payload = {
        name: form.name,
        category: form.category,
        quantity: Number(form.quantity),
        minQuantity: Number(form.minQuantity),
        unit: form.unit,
        price: Number(form.price),
      };
      if (editId) {
        await updateMedication(editId, payload);
      } else {
        await createMedication(payload);
      }
      setShowModal(false);
      fetchAll();
    } catch (e) {
      setError(e.message || "Xato yuz berdi");
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("O'chirishni tasdiqlaysizmi?")) return;
    try {
      await deleteMedication(id);
      fetchAll();
    } catch (e) {
      alert(e.message || "Xato yuz berdi");
    }
  };

  const isLow = (m) => m.quantity <= m.minQuantity;

  return (
    <Layout title="Dorilar">
      <div className="page-header">
        <div>
          <h2>Dorilar</h2>
          <p className="page-subtitle">Jami {meds.length} ta dori</p>
        </div>
        <button className="btn-add" onClick={openAdd}>+ Dori qo'shish</button>
      </div>

      <div className="search-bar">
        <input
          placeholder="Nomi yoki kategoriya bo'yicha qidiring..."
          value={search}
          onChange={e => setSearch(e.target.value)}
        />
      </div>

      <div className="table-wrapper">
        {loading ? (
          <p className="loading">Yuklanmoqda...</p>
        ) : filtered.length === 0 ? (
          <p className="empty-text">Dorilar mavjud emas</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Nomi</th>
                <th>Kategoriya</th>
                <th>Miqdori</th>
                <th>Min. miqdor</th>
                <th>Birlik</th>
                <th>Narxi</th>
                <th>Holat</th>
                <th>Amallar</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((m, i) => (
                <tr key={m.id}>
                  <td>{i + 1}</td>
                  <td style={{ fontWeight: 500 }}>{m.name}</td>
                  <td>{m.category || "—"}</td>
                  <td style={{ fontWeight: 600, color: isLow(m) ? "#EF4444" : "inherit" }}>
                    {m.quantity}
                  </td>
                  <td>{m.minQuantity}</td>
                  <td>{m.unit || "—"}</td>
                  <td style={{ color: "#16A34A", fontWeight: 600 }}>
                    {m.price?.toLocaleString()} so'm
                  </td>
                  <td>
                    {isLow(m) ? (
                      <span className="badge-low">Kam qoldi</span>
                    ) : (
                      <span className="badge-ok">Yetarli</span>
                    )}
                  </td>
                  <td>
                    <button className="btn-edit" onClick={() => openEdit(m)}>Tahrirlash</button>
                    <button className="btn-delete" onClick={() => handleDelete(m.id)}>O'chirish</button>
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
            <h3>{editId ? "Dorini tahrirlash" : "Dori qo'shish"}</h3>
            {error && <p className="error-msg">{error}</p>}
            <div className="form-row">
              <div className="form-group">
                <label>Nomi *</label>
                <input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} placeholder="Masalan: Amoxicillin" />
              </div>
              <div className="form-group">
                <label>Kategoriya</label>
                <input value={form.category} onChange={e => setForm({ ...form, category: e.target.value })} placeholder="Masalan: Antibiotik" />
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label>Miqdori *</label>
                <input type="number" value={form.quantity} onChange={e => setForm({ ...form, quantity: e.target.value })} />
              </div>
              <div className="form-group">
                <label>Min. miqdor</label>
                <input type="number" value={form.minQuantity} onChange={e => setForm({ ...form, minQuantity: e.target.value })} />
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label>Birlik</label>
                <input value={form.unit} onChange={e => setForm({ ...form, unit: e.target.value })} placeholder="dona, ml, mg..." />
              </div>
              <div className="form-group">
                <label>Narxi (so'm)</label>
                <input type="number" value={form.price} onChange={e => setForm({ ...form, price: e.target.value })} />
              </div>
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