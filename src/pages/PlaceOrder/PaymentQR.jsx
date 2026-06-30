import { useContext, useEffect, useRef, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import { toast } from "react-toastify";
import { StoreContext } from "../../context/StoreContext";

const API_URL = "http://localhost:8080";
const POLL_INTERVAL_MS = 5000;
const MAX_POLL_ATTEMPTS = 36; // 3 phút - 36 lan

const PaymentPage = () => {
    const { state } = useLocation();
    const navigate = useNavigate();
    const {setQuantities} = useContext(StoreContext)

    const [status, setStatus] = useState("PENDING"); // PENDING | CONFIRMED | TIMEOUT
    const attemptRef = useRef(0)
    const intervalRef = useRef(null);


    // Nếu vào thẳng URL này mà không có state → về trang chủ
    useEffect(() => {
        if (!state?.orderId) {
            navigate("/");
        }
    }, [state, navigate]);

    useEffect(() => {
        if (!state?.orderId) return;

        const pollStatus = async () => {
            try {
                const res = await axios.get(
                    `${API_URL}/api/orders/${state.orderId}/status`
                );
                const orderStatus = res.data.orderStatus;

                if (orderStatus === "CONFIRMED") {
                    clearInterval(intervalRef.current);
                    setQuantities({}) //clear items in cart out memory
                    setStatus("CONFIRMED");
                    toast.success("Thanh toán thành công!");
                    setTimeout(() => navigate("/", { state: { orderId: state.orderId } }), 1500);
                }
            } catch {
                // Bỏ qua lỗi mạng nhất thời, tiếp tục poll
            }

            attemptRef.current += 1
            if(attemptRef.current >= MAX_POLL_ATTEMPTS){
                clearInterval(intervalRef.current)
                setStatus("TIMEOUT");
            }
            // setAttempts((prev) => {
            //     const next = prev + 1;
            //     if (next >= MAX_POLL_ATTEMPTS) {
            //         clearInterval(intervalRef.current);
            //         setStatus("TIMEOUT");
            //     }
            //     return next;
            // });
        };

        intervalRef.current = setInterval(pollStatus, POLL_INTERVAL_MS);
        return () => clearInterval(intervalRef.current);
    }, [state, navigate, setQuantities]);

    if (!state?.orderId) return null;

    const { qrUrl, amount, paymentCode } = state;

    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-6 text-center">

                    {status === "TIMEOUT" ? (
                        <div className="alert alert-warning">
                            <h5>Hết thời gian chờ thanh toán</h5>
                            <p>Nếu đã chuyển khoản, vui lòng liên hệ hỗ trợ với mã: <strong>{paymentCode}</strong></p>
                            <button className="btn btn-primary" onClick={() => navigate("/")}>
                                Về trang chủ
                            </button>
                        </div>
                    ) : status === "CONFIRMED" ? (
                        <div className="alert alert-success">
                            <h5>✓ Thanh toán thành công</h5>
                            <p>Đang chuyển hướng...</p>
                        </div>
                    ) : (
                        <>
                            <h4 className="mb-3">Quét mã QR để thanh toán</h4>

                            <img
                                src={qrUrl}
                                alt="QR thanh toán SePay"
                                className="img-fluid border rounded"
                                style={{ maxWidth: "280px" }}
                            />

                            <div className="mt-3">
                                <p className="mb-1">
                                    Số tiền: <strong>{amount?.toLocaleString("vi-VN")} VND</strong>
                                </p>
                                <p className="mb-1">
                                    Nội dung chuyển khoản:{" "}
                                    <strong className="text-primary">{paymentCode}</strong>
                                </p>
                                <small className="text-muted">
                                    ⚠ Nhập đúng nội dung chuyển khoản để hệ thống xác nhận tự động
                                </small>
                            </div>

                            <div className="mt-4 d-flex align-items-center justify-content-center gap-2 text-muted">
                                <div className="spinner-border spinner-border-sm" role="status" />
                                <span>Đang chờ xác nhận thanh toán...</span>
                            </div>
                        </>
                    )}
                </div>
            </div>
        </div>
    );
};

export default PaymentPage;