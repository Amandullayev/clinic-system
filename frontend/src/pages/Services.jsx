import { useState, useEffect } from "react";
import Layout from "../components/layout/Layout";
import { getAllServices, createService, updateService, deleteService } from "../api/services";
import "../styles/services.css";

const CATEGORIES = [
  { value: "ALL",           label: "Barchasi"      },
  { value: "KORIK",         label: "Ko'rik"        },
  { value: "TERAPIYA",      label: "Terapiya"      },
  { value: "STOMATOLOGIYA", label: "Stomatologiya" },
  { value: "LABORATORIYA",  label: "Laboratoriya"  },
  { value: "FIZIOTERAPIYA", label: "Fizioterapiya" },
];

function categoryLabel(value) {
  return CATEGORIES.find(c => c.value === value)?.label || value;
}

function formatPrice(price) {
  if (!price && price !== 0) return "—";
  return Number(price).toLocaleString("uz-UZ") + " so'm";
}

const emptyForm = {
  name: "", description: "", price: "",
  category: "KORIK", durationMinutes: "",
};

export default function Services() {
  const [services, setServices]         = useState([]);
  const [filtered, setFiltered]         = useState([]);
  const [search, setSearch]             = useState("");
  const [activeCategory, setActiveCategory] = useState("ALL");
  const [loading, setLoading]           = useState(true);
  const [showModal, setShowModal]       = useState(false);
  const [editId, setEditId]             = useState(null);
  const [form, setForm]                 = useState(emptyForm);
  const [error, setError]               = useState("");

  useEffect(() => { fetchAll(); }, []);

  useEffect(() => {
    let result = services;
    if (activeCategory !== "ALL") {
      result = result.filter(s => s.category === activeCategory);
    }
    if (search.trim()) {
      const q = search.toLowerCase();
      result = result.filter(s =>
        s.name?.toLowerCase().includes(q) ||
        s.description?.toLowerCase().includes(q)
      );
    }
    setFiltered(result);
  }, [search, activeCategory, services]);

  const fetchAll = async () => {
    setLoading(true);
    try {
      const data = await getAllServices();
      setServices(data);
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

  
  const openEdit = (s) => {
    setForm({
      name: s.name || "",
      description: s.description || "",
      price: s.price || "",
      category: s.category || "KORIK",
      durationMinutes: s.durationMinutes || "",
    });
    setEditId(s.id);
    setError("");
    setShowModal(true);
  };

  const handleSave = async () => {
    if (!form.name || !form.price) {
      setError("Nomi va narxi majburiy");
      return;
    }
    const payload = {
      ...form,
      price: Number(form.price),
      durationMinutes: form.durationMinutes ? Number(form.durationMinutes) : null,
    };
    const res = editId
      ? await updateService(editId, payload)
      : await createService(payload);

    if (res.success) {
      setShowModal(false);
      fetchAll();
    } else {
      setError(res.message || "Xato yuz berdi");
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Rostdan ham o'chirasizmi?")) return;
    await deleteService(id);
    fetchAll();
  };

  return (
    <Layout title="Xizmatlar">

      <div className="page-header">
        <div>
          <h2>Xizmatlar</h2>
          <p className="page-subtitle">Jami {services.length} ta xizmat</p>
        </div>
        <button className="btn-add" onClick={openAdd}>+ Xizmat qo'shish</button>
      </div>

      {/* Kategoriya filtrlari */}
      <div className="category-tabs">
        {CATEGORIES.map(c => (
          <button
            key={c.value}
            className={`cat-tab ${activeCategory === c.value ? "cat-tab-active" : ""}`}
            onClick={() => setActiveCategory(c.value)}
          >
            {c.label}
          </button>
        ))}
      </div>

      {/* Qidiruv */}
      <div className="services-toolbar">
        <input
          placeholder="Xizmat nomi bo'yicha qidirish..."
          value={search}
          onChange={e => setSearch(e.target.value)}
        />
      </div>

      <div className="table-wrapper">
        {loading ? (
          <p className="loading">Yuklanmoqda...</p>
        ) : filtered.length === 0 ? (
          <p className="empty-text">Xizmat topilmadi</p>
        ) : (
          <>
            <table>
              <thead>
                <tr>
                  <th>#</th>
                  <th>Nomi</th>
                  <th>Kategoriya</th>
                  <th>Tavsif</th>
                  <th>Davomiyligi</th>
                  <th>Narxi</th>
                  <th>Amallar</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((s, i) => (
                  <tr key={s.id}>
                    <td>{i + 1}</td>
                    <td>{s.name}</td>
                    <td>
                      <span className="cat-badge">{categoryLabel(s.category)}</span>
                    </td>
                    <td>{s.description || "—"}</td>
                    <td>{s.durationMinutes ? `${s.durationMinutes} daqiqa` : "—"}</td>
                    <td className="service-price">{formatPrice(s.price)}</td>
                    <td>
                      <button className="btn-edit" onClick={() => openEdit(s)}>Tahrirlash</button>
                      <button className="btn-delete" onClick={() => handleDelete(s.id)}>O'chirish</button>
                    </td>
                  </tr>
                ))}
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
            <h3>{editId ? "Xizmatni tahrirlash" : "Xizmat qo'shish"}</h3>
            {error && <p className="error-msg">{error}</p>}

            <div className="form-group">
              <label>Nomi</label>
              <input
                value={form.name}
                onChange={e => setForm({ ...form, name: e.target.value })}
                placeholder="Konsultatsiya"
              />
            </div>

            <div className="form-group">
              <label>Kategoriya</label>
              <select
                value={form.category}
                onChange={e => setForm({ ...form, category: e.target.value })}
              >
                {CATEGORIES.filter(c => c.value !== "ALL").map(c => (
                  <option key={c.value} value={c.value}>{c.label}</option>
                ))}
              </select>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Narxi (so'm)</label>
                <input
                  type="number"
                  value={form.price}
                  onChange={e => setForm({ ...form, price: e.target.value })}
                  placeholder="50000"
                />
              </div>
              <div className="form-group">
                <label>Davomiyligi (daqiqa)</label>
                <input
                  type="number"
                  value={form.durationMinutes}
                  onChange={e => setForm({ ...form, durationMinutes: e.target.value })}
                  placeholder="30"
                />
              </div>
            </div>

            <div className="form-group">
              <label>Tavsif (ixtiyoriy)</label>
              <textarea
                value={form.description}
                onChange={e => setForm({ ...form, description: e.target.value })}
                placeholder="Xizmat haqida qisqacha..."
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