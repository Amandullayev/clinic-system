import { useEffect, useState } from "react";
import Layout from "../components/layout/Layout";
import { getAllUsers, createUser, updateUser, toggleUserActive, deleteUser } from "../api/users";
import "../styles/superadmin.css";

const ROLES = ["ADMIN", "RECEPTIONIST", "DOCTOR", "PATIENT"];
const roleLabel = { SUPER_ADMIN: "Super Admin", ADMIN: "Admin", RECEPTIONIST: "Qabulxona", DOCTOR: "Shifokor", PATIENT: "Bemor" };
const roleColor = { SUPER_ADMIN: "role-superadmin", ADMIN: "role-admin", RECEPTIONIST: "role-receptionist", DOCTOR: "role-doctor", PATIENT: "role-patient" };

const emptyForm = { fullName: "", email: "", password: "", role: "ADMIN" };

export default function SuperAdminDashboard() {
  const [users, setUsers]         = useState([]);
  const [loading, setLoading]     = useState(true);
  const [search, setSearch]       = useState("");
  const [roleFilter, setRoleFilter] = useState("ALL");
  const [showModal, setShowModal] = useState(false);
  const [editId, setEditId]       = useState(null);
  const [form, setForm]           = useState(emptyForm);
  const [error, setError]         = useState("");

  useEffect(() => { fetchAll(); }, []);

  const fetchAll = async () => {
    setLoading(true);
    try { setUsers(await getAllUsers()); }
    catch (e) { console.error(e); }
    finally { setLoading(false); }
  };

  const filtered = users.filter(u => {
    const matchSearch = u.fullName?.toLowerCase().includes(search.toLowerCase()) ||
                        u.email?.toLowerCase().includes(search.toLowerCase());
    const matchRole = roleFilter === "ALL" || u.role === roleFilter;
    return matchSearch && matchRole;
  });

  const openAdd = () => {
    setForm(emptyForm);
    setEditId(null);
    setError("");
    setShowModal(true);
  };

  const openEdit = (u) => {
    setForm({ fullName: u.fullName, email: u.email, password: "", role: u.role });
    setEditId(u.id);
    setError("");
    setShowModal(true);
  };

  const handleSave = async () => {
    if (!form.fullName || !form.email || (!editId && !form.password)) {
      setError("Ism, email va parol majburiy");
      return;
    }
    try {
      if (editId) {
        const payload = { fullName: form.fullName, email: form.email, role: form.role };
        await updateUser(editId, payload);
      } else {
        await createUser(form);
      }
      setShowModal(false);
      fetchAll();
    } catch (e) { setError(e.message || "Xato yuz berdi"); }
  };

  const handleToggle = async (id) => {
    try { await toggleUserActive(id); fetchAll(); }
    catch (e) { alert(e.message); }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Foydalanuvchini o'chirasizmi?")) return;
    try { await deleteUser(id); fetchAll(); }
    catch (e) { alert(e.message); }
  };

  const stats = {
    total:       users.length,
    active:      users.filter(u => u.active).length,
    admins:      users.filter(u => u.role === "ADMIN").length,
    doctors:     users.filter(u => u.role === "DOCTOR").length,
    receptionists: users.filter(u => u.role === "RECEPTIONIST").length,
    patients:    users.filter(u => u.role === "PATIENT").length,
  };

  return (
    <Layout>
      <div className="page-header">
        <div>
          <h2 className="dashboard-title">Super Admin paneli</h2>
          <p className="page-subtitle">Tizim foydalanuvchilari boshqaruvi</p>
        </div>
        <button className="btn-add" onClick={openAdd}>+ Foydalanuvchi qo'shish</button>
      </div>

      {/* Stats */}
      <div className="sa-stats-grid">
        <div className="sa-stat-card teal">
          <p className="stat-label">Jami foydalanuvchilar</p>
          <p className="stat-value">{stats.total}</p>
        </div>
        <div className="sa-stat-card green">
          <p className="stat-label">Adminlar</p>
          <p className="stat-value">{stats.admins}</p>
        </div>
        <div className="sa-stat-card blue">
          <p className="stat-label">Shifokorlar</p>
          <p className="stat-value">{stats.doctors}</p>
        </div>
        <div className="sa-stat-card orange">
          <p className="stat-label">Qabulxona xodimlari</p>
          <p className="stat-value">{stats.receptionists}</p>
        </div>
        <div className="sa-stat-card purple">
          <p className="stat-label">Bemorlar</p>
          <p className="stat-value">{stats.patients}</p>
        </div>
        <div className="sa-stat-card gray">
          <p className="stat-label">Faol foydalanuvchilar</p>
          <p className="stat-value">{stats.active}</p>
        </div>
      </div>

      {/* Filter */}
      <div className="sa-toolbar">
        <input
          placeholder="Ism yoki email bo'yicha qidiring..."
          value={search}
          onChange={e => setSearch(e.target.value)}
        />
        <select value={roleFilter} onChange={e => setRoleFilter(e.target.value)}>
          <option value="ALL">Barcha rollar</option>
          {["SUPER_ADMIN", "ADMIN", "RECEPTIONIST", "DOCTOR", "PATIENT"].map(r => (
            <option key={r} value={r}>{roleLabel[r]}</option>
          ))}
        </select>
      </div>

      {/* Table */}
      <div className="table-wrapper">
        {loading ? <p className="loading">Yuklanmoqda...</p> :
         filtered.length === 0 ? <p className="empty-text">Foydalanuvchilar topilmadi</p> : (
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Ism</th>
                <th>Email</th>
                <th>Rol</th>
                <th>Holat</th>
                <th>Qo'shilgan</th>
                <th>Amallar</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((u, i) => (
                <tr key={u.id}>
                  <td>{i + 1}</td>
                  <td>
                    <div className="user-cell">
                      <div className="user-avatar">{u.fullName?.charAt(0)}</div>
                      <span style={{ fontWeight: 500 }}>{u.fullName}</span>
                    </div>
                  </td>
                  <td style={{ color: "var(--text-secondary)" }}>{u.email}</td>
                  <td>
                    <span className={`role-badge ${roleColor[u.role] || ""}`}>
                      {roleLabel[u.role] || u.role}
                    </span>
                  </td>
                  <td>
                    <span className={u.active ? "badge-active-user" : "badge-inactive-user"}>
                      {u.active ? "Faol" : "Bloklangan"}
                    </span>
                  </td>
                  <td style={{ color: "var(--text-secondary)" }}>
                    {u.createdAt ? u.createdAt.slice(0, 10) : "—"}
                  </td>
                  <td>
                    <button className="btn-edit" onClick={() => openEdit(u)}>Tahrirlash</button>
                    <button
                      className={u.active ? "btn-block" : "btn-unblock"}
                      onClick={() => handleToggle(u.id)}
                    >
                      {u.active ? "Blok" : "Faollashtir"}
                    </button>
                    <button className="btn-delete" onClick={() => handleDelete(u.id)}>O'chirish</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Modal */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>{editId ? "Foydalanuvchini tahrirlash" : "Yangi foydalanuvchi"}</h3>
            {error && <p className="error-msg">{error}</p>}
            <div className="form-group">
              <label>To'liq ism *</label>
              <input value={form.fullName} onChange={e => setForm({ ...form, fullName: e.target.value })} placeholder="Ism Familiya" />
            </div>
            <div className="form-group">
              <label>Email *</label>
              <input type="email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} placeholder="email@example.com" />
            </div>
            {!editId && (
              <div className="form-group">
                <label>Parol *</label>
                <input type="password" value={form.password} onChange={e => setForm({ ...form, password: e.target.value })} placeholder="Kamida 6 ta belgi" />
              </div>
            )}
            <div className="form-group">
              <label>Rol *</label>
              <select value={form.role} onChange={e => setForm({ ...form, role: e.target.value })}>
                {ROLES.map(r => <option key={r} value={r}>{roleLabel[r]}</option>)}
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