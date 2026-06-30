import { useEffect } from 'react';
import "../../pages/Contact/Contact.css";

const Contact = () => {
  useEffect(() => {
    // 1. Kiểm tra xem thư viện Leaflet (L) đã được tải từ CDN chưa
    if (typeof window !== 'undefined' && window.L) {
      const L = window.L;

      // Khởi tạo map (Sử dụng id="map")
      const map = L.map('map').setView([21.028511, 105.804817], 12);

      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      }).addTo(map);

      L.marker([21.028511, 105.804817])
        .addTo(map)
        .bindPopup('Your location')
        .openPopup();

      // Clean up: Hủy map khi component bị unmount để tránh rò rỉ bộ nhớ
      return () => {
        map.remove();
      };
    }
  }, []);

  const handleSubmit = (e) => {
    e.preventDefault();
    alert("Form submitted!");
  };

  return (
    <div className="container my-5">
      <h1 className="text-center mb-4">Liên hệ</h1>
      <div className="row">
        {/* Cột chứa Bản đồ */}
        <div className="col-md-6">
          <div className="map-container" id="map" style={{ height: '400px', minHeight: '100%' }}></div>
        </div>
        <div className="col-md-6">
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label htmlFor="name" className="form-label">Họ và Tên</label>
              <input type="text" className="form-control" id="name" required />
            </div>
            <div className="mb-3">
              <label htmlFor="email" className="form-label">Email</label>
              <input type="email" className="form-control" id="email" required />
            </div>
            <div className="mb-3">
              <label htmlFor="message" className="form-label">Lời nhắn</label>
              <textarea className="form-control" id="message" rows={5} required></textarea>
            </div>
            <button type="submit" className="btn btn-primary">Gửi</button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Contact;
