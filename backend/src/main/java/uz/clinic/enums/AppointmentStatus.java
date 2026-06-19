package uz.clinic.enums;

public enum AppointmentStatus {
    PENDING,        // Yozildi, bemor tasdiqlash kutilmoqda (24 soat)
    CONFIRMED,      // Bemor tasdiqladi — slot qat'iy band
    ARRIVED,        // Bemor klinikaga keldi (receptionist belgiladi)
    COMPLETED,      // Shifokor ko'rdi, qabul tugadi
    CANCELLED,      // Bemor yoki admin tomonidan bekor qilindi
    AUTO_CANCELLED, // 24 soat ichida tasdiqlanmadi → avtomatik bekor
    NO_SHOW         // Bemor vaqtida kelmadi (15 daqiqa o'tdi)
}