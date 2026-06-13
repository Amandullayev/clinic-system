import { useState, useEffect } from "react";
import Layout from "../components/layout/Layout";
import {
  getAllMedications,
  createMedication,
  updateMedication,
  deleteMedication,
} from "../api/medications";
import "../styles/medications.css";

const UNITS = [
  { value: "MG",       label: "mg (milligram)" },
  { value: "ML",       label: "ml (millilitr)"  },
  { value: "G",        label: "g (gram)"         },
  { value: "DONA",     label: "dona"             },
  { value: "KAPSULA",  label: "kapsula"          },
  { value: "TABLETKA", label: "tabletka"         },
  { value: "AMPULA",   label: "ampula"           },
];

const CATEGORIES = [
  { value: "ANTIBIOTIK",    label: "Antibiotiklar" },
  { value: "OGHRIQ",        label: "Og'riq qoldiruvchilar" },
  { value: "YURAK_TOMIR",   label: "Yurak-tomir" },
  { value: "VITAMIN",       label: "Vitaminlar va minerallar" },
  { value: "ALLERGIYA",     label: "Allergiya dorilar" },
  { value: "OSHQOZON",      label: "Oshqozon-ichak" },
  { value: "ASAB",          label: "Asab tizimi" },
  { value: "QON_BOSIMI",    label: "Qon bosimi" },
  { value: "ANTIFUNGAL",    label: "Antifungal" },
  { value: "BOSHQA",        label: "Boshqa" },
];

const CATEGORY_LABELS = {
  ANTIBIOTIK: "Antibiotiklar",
  OGHRIQ: "Og'riq qoldiruvchilar",
  YURAK_TOMIR: "Yurak-tomir",
  VITAMIN: "Vitaminlar va minerallar",
  ALLERGIYA: "Allergiya dorilar",
  OSHQOZON: "Oshqozon-ichak",
  ASAB: "Asab tizimi",
  QON_BOSIMI: "Qon bosimi",
  ANTIFUNGAL: "Antifungal",
  BOSHQA: "Boshqa",
};

const UNIT_LABELS = {
  MG: "mg", ML: "ml", G: "g",
  DONA: "dona", KAPSULA: "kapsula",
  TABLETKA: "tabletka", AMPULA: "ampula",
};

const emptyForm = {
  name: "", category: "", quantity: "",
  minQuantity: "", unit: "", price: "",
};

export default function Medications() {
  const [meds, setMeds] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [editId, setEditId] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [error, setError] = useState("");
  const [activeCategory, setActiveCategory] = useState("");

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

  const filtered = meds.filter(m => {
  const matchSearch =
    m.name?.toLowerCase().includes(search.toLowerCase()) ||
    m.category?.toLowerCase().includes(search.toLowerCase());
  const matchCategory = activeCategory ? m.category === activeCategory : true;
  return matchSearch && matchCategory;
});
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
    if (!form.name.trim()) { setError("Dori nomi majburiy"); return; }
    if (!form.quantity)     { setError("Miqdor majburiy"); return; }
    if (!form.minQuantity)  { setError("Minimal miqdor majburiy"); return; }
    if (!form.unit)         { setError("Birlikni tanlang"); return; }
    if (!form.price || Number(form.price) <= 0) {
      setError("Narx majburiy va 0 dan katta bo'lishi kerak");
      return;
    }

    try {
      const payload = {
        name: form.name.trim(),
        category: form.category.trim() || null,
        quantity: Number(form.quantity),
        minQuantity: Number(form.minQuantity),
        unit: form.unit,
        price: form.price ? Number(form.price) : null,
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

  const lowCount = meds.filter(isLow).length;

  return (
    <Layout title="Dorilar zahirasi">

      <div className="page-header">
        <div>
          <h2>Dorilar zahirasi</h2>
          <p className="page-subtitle">
            Jami {meds.length} ta dori
            {lowCount > 0 && (
              <span style={{ color: "#EF4444", marginLeft: "12px", fontWeight: 600 }}>
                ⚠ {lowCount} ta kam qolgan
              </span>
            )}
          </p>
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

      {/* Kategoriya filtrlari */}
<div style={{ display: "flex", gap: "8px", flexWrap: "wrap", marginBottom: "16px" }}>
  <button
    onClick={() => setActiveCategory("")}
    style={{
      padding: "6px 14px", borderRadius: "20px", fontSize: "13px",
      border: "1px solid var(--border)", cursor: "pointer",
      backgroundColor: activeCategory === "" ? "#0D7377" : "var(--input-bg)",
      color: activeCategory === "" ? "white" : "var(--text-secondary)",
      fontWeight: activeCategory === "" ? 600 : 400,
    }}
  >
    Barchasi ({meds.length})
  </button>
  {CATEGORIES.map(cat => {
    const count = meds.filter(m => m.category === cat.value).length;
    if (count === 0) return null;
    return (
      <button
        key={cat.value}
        onClick={() => setActiveCategory(cat.value)}
        style={{
          padding: "6px 14px", borderRadius: "20px", fontSize: "13px",
          border: "1px solid var(--border)", cursor: "pointer",
          backgroundColor: activeCategory === cat.value ? "#0D7377" : "var(--input-bg)",
          color: activeCategory === cat.value ? "white" : "var(--text-secondary)",
          fontWeight: activeCategory === cat.value ? 600 : 400,
        }}
      >
        {cat.label} ({count})
      </button>
    );
  })}
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
                  <td>
                      {m.category ? (
                        <span style={{
                          backgroundColor: "var(--bg)", padding: "2px 10px",
                          borderRadius: "20px", fontSize: "12px", color: "var(--text-secondary)",
                          border: "1px solid var(--border)",
                        }}>
                          {CATEGORY_LABELS[m.category] || m.category}
                        </span>
                      ) : "—"}
                    </td>
                  <td>
                    <span style={{
                      fontWeight: 700,
                      color: isLow(m) ? "#EF4444" : "#16A34A",
                      fontSize: "15px",
                    }}>
                      {m.quantity}
                    </span>
                  </td>
                  <td style={{ color: "var(--text-secondary)" }}>{m.minQuantity}</td>
                  <td>
                    <span style={{
                      backgroundColor: "#0D737715", color: "#0D7377",
                      padding: "2px 10px", borderRadius: "20px",
                      fontSize: "12px", fontWeight: 600,
                    }}>
                      {UNIT_LABELS[m.unit] || m.unit || "—"}
                    </span>
                  </td>
                  <td style={{ color: "#16A34A", fontWeight: 600 }}>
                    {m.price ? `${Number(m.price).toLocaleString("uz-UZ")} so'm` : "—"}
                  </td>
                  <td>
                    {isLow(m) ? (
                      <span className="badge-low">⚠ Kam qoldi</span>
                    ) : (
                      <span className="badge-ok">✓ Yetarli</span>
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
                <input
                  value={form.name}
                  onChange={e => setForm({ ...form, name: e.target.value })}
                  placeholder="Dori nomini kiriting yoki tanlang"
                  list="medication-names"
                />
                <datalist id="medication-names">
                  <option value="Paracetamol" />
                  <option value="Amoxicillin" />
                  <option value="Ibuprofen" />
                  <option value="Aspirin" />
                  <option value="Ciprofloxacin" />
                  <option value="Metformin" />
                  <option value="Omeprazol" />
                  <option value="Losartan" />
                  <option value="Atorvastatin" />
                  <option value="Amlodipin" />
                  <option value="Diazepam" />
                  <option value="Drotaverin" />
                  <option value="Analgin" />
                  <option value="No'shpa" />
                  <option value="Validol" />
                  <option value="Pentalgin" />
                  <option value="Suprastin" />
                  <option value="Furosemid" />
                  <option value="Biseptol" />
                  <option value="Eritromitsin" />
                  <option value="Prednizolon" />
                  <option value="Vitamin C" />
                  <option value="Vitamin D3" />
                  <option value="Magniy B6" />
                </datalist>
              </div>
               <div className="form-group">
                  <label>Kategoriya</label>
                  <input
                    value={form.category}
                    onChange={e => setForm({ ...form, category: e.target.value })}
                    placeholder="Kategoriyani kiriting yoki tanlang"
                    list="category-list"
                  />
                  <datalist id="category-list">
                    <option value="ANTIBIOTIK" />
                    <option value="OGHRIQ" />
                    <option value="YURAK_TOMIR" />
                    <option value="VITAMIN" />
                    <option value="ALLERGIYA" />
                    <option value="OSHQOZON" />
                    <option value="ASAB" />
                    <option value="QON_BOSIMI" />
                    <option value="ANTIFUNGAL" />
                    <option value="BOSHQA" />
                  </datalist>
                </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Miqdori *</label>
                <input
                  type="number"
                  min="0"
                  value={form.quantity}
                  onChange={e => setForm({ ...form, quantity: e.target.value })}
                  placeholder="0"
                />
              </div>
              <div className="form-group">
                <label>Min. miqdor *</label>
                <input
                  type="number"
                  min="1"
                  value={form.minQuantity}
                  onChange={e => setForm({ ...form, minQuantity: e.target.value })}
                  placeholder="10"
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Birlik *</label>
                <select
                  value={form.unit}
                  onChange={e => setForm({ ...form, unit: e.target.value })}
                >
                  <option value="">— Tanlang —</option>
                  {UNITS.map(u => (
                    <option key={u.value} value={u.value}>{u.label}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Narxi (so'm)</label>
                <input
                  type="number"
                  min="0"
                  value={form.price}
                  onChange={e => setForm({ ...form, price: e.target.value })}
                  placeholder="15000"
                />
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