# clinic-system
# CLINIC — Klinika Boshqaruv Tizimi

Zamonaviy klinikalar uchun to'liq funksional boshqaruv tizimi.  
Spring Boot backend va React + Vite frontend asosida qurilgan.

---

## Texnologiyalar

**Backend**
- Java 17, Spring Boot 3
- Spring Security (JWT + OAuth2)
- Spring Data JPA, PostgreSQL
- Resend (email OTP)
- Lombok, MapStruct

**Frontend**
- React 18, Vite
- React Router v6
- Recharts
- CSS Variables (Dark/Light mode)

---

## Rollar

| Rol | Vakolatlar |
|-----|-----------|
| `SUPER_ADMIN` | Barcha tizimni boshqarish, foydalanuvchilarni qo'shish/o'chirish |
| `ADMIN` | Dashboard, navbatlar, to'lovlar, dorilar, xizmatlar |
| `RECEPTIONIST` | Navbat qo'shish va boshqarish |
| `DOCTOR` | O'z navbatlari va bemorlarini ko'rish |
| `PATIENT` | Navbat olish, o'z tarixini ko'rish |

---

## Xususiyatlar

- JWT asosida autentifikatsiya
- Google OAuth2 orqali kirish
- Email OTP tasdiqlash (Resend orqali)
- Rol asosida sahifalar va menyu
- Dark / Light rejim
- Dashboard: statistika, haftalik grafik, to'lovlar, navbatlar, dori zahirasi, shifokorlar holati
- Bemorlar, shifokorlar, xizmatlar, dori-darmonlar CRUD

---

## O'rnatish va ishga tushirish

### Talablar
- Java 17+
- Node.js 18+
- PostgreSQL 14+

### Backend

```bash
cd backend
# application.properties ni sozlang (DB, JWT, Resend)
./mvnw spring-boot:run