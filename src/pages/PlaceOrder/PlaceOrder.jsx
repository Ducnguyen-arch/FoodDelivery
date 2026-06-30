import { useContext, useState } from "react";
import {assets} from "../../assets/assets"
import { StoreContext } from "../../context/StoreContext";
import {calculateCartTotal, formatCurrencyUnit} from "../../util/CartUtils"
import axios from 'axios'
import {toast} from 'react-toastify'
// import { SEPAY_KEY } from "../../util/constants";
import {useNavigate} from "react-router-dom";


const API_URL = "http://localhost:8080"

const PlaceOrder = () => {
   const {foodList, quantities, token} = useContext(StoreContext)

   const [submitting, setSubmitting] = useState(false)

   const navigate = useNavigate()

   const [formData, setFormData] = useState({
      firstName: "",
      lastName: "",
      email: "",
      phoneNumber: "",
      address: "",
      state: "",
      city: ""
   })

   //cart items
   const cartItems = foodList.filter((food) => quantities[food.id] > 0)
   const {subTotal, shipping, tax, total} = calculateCartTotal(cartItems, quantities)
   const formatPrice = formatCurrencyUnit;


   const onChangeHandler = (event) => {
      const {name, value} = event.target
      setFormData((prev) => ({...prev, [name]: value }))
   }

   const onSubmitHandler = async(event) => {
      event.preventDefault()
      if(cartItems.length === 0) return;

      setSubmitting(true);

      try {
         const orderPayload = {
            userAddress: `${formData.firstName} ${formData.lastName}, ${formData.address}, ${formData.city}, ${formData.state}`,
            userPhoneNumber: formData.phoneNumber,
                email: formData.email,
                orderItems: cartItems.map((item) => ({
                    foodId: item.id,
                    quantity: quantities[item.id],
                    price: item.price * quantities[item.id],
                    category: item.category,
                    imageUrl: item.imageUrl,
                    name: item.name,
                })),
                amount: total,
         }
         const response = await axios.post(`${API_URL}/api/orders/create`, orderPayload,
            {headers: {Authorization: `Bearer ${token}`}}
         )

         //BE return: {id, paymentCode, qrUrl, orderStatus}
         const {id: orderId, paymentCode, qrUrl} = response.data;
         navigate("/payment/webhook/sepay", {
            state: {orderId, paymentCode, qrUrl, amount: total}
         })
      } catch (error) {
         const message = error.response?.data.message || "Lỗi tạo đơn hàng";
         toast.error(message)
      }
   }

  return (
    <div className="container mt-2">
        <main>
            <div className="py-5 text-center">
               <img className="d-block mx-auto" src={assets.logo} alt="" width="98" height="98" /> 
            </div>

            <div className="row g-5">
               <div className="col-md-5 col-lg-4 order-md-last">
                  <h4 className="d-flex justify-content-between align-items-center mb-3">
                   <span className="text-primary">Giỏ hàng</span> <span className="badge bg-primary rounded-pill">{cartItems.length}</span> 
                  </h4>
                  <ul className="list-group mb-3">
                    {cartItems.map(item => (
                      <li key={item.id} className="list-group-item d-flex justify-content-between lh-sm">
                        <div>
                           <h6 className="my-0">{item.name}</h6>
                           <small className="text-body-secondary">Số lượng: {quantities[item.id]}</small> 
                        </div>
                        <span className="text-body-secondary">{formatPrice(item.price * quantities[item.id])} VND</span> 
                     </li>
                    ))}
                     <li className="list-group-item d-flex justify-content-between ">
                           <span>Chi phí vận chuyển</span> 
                        <span className="text-body-secondary">{formatPrice(subTotal === 0 ? 0 : shipping)} VND</span> 
                     </li>
                     <li className="list-group-item d-flex justify-content-between">
                        <span>Thuế <i>(5%)</i></span> 
                        <span className="text-body-secondary">{formatPrice(tax)} VND</span>
                     </li>
                     <li className="list-group-item d-flex justify-content-between"> 
                     <span>Tổng thanh toán</span>
                      <strong>{formatPrice(total)} VND</strong> 
                     </li>
                  </ul>
               </div>

               {/* Billing form */}
               <div className="col-md-7 col-lg-8">
                  <h4 className="mb-3">Địa chỉ giao hàng</h4>
                  <form className="needs-validation" onSubmit={onSubmitHandler}>
                     <div className="row g-3">
                        <div className="col-sm-6">
                           <label htmlFor="firstName" className="form-label">Họ</label> 
                           <input type="text" className="form-control" id="firstName" placeholder="Duc" required name="firstName" onChange={onChangeHandler} value={formData.firstName} /> 
                           
                        </div>
                        <div className="col-sm-6">
                           <label htmlFor="lastName" className="form-label">Tên</label> 
                           <input type="text" className="form-control" id="lastName" placeholder="Nguyen" required name="lastName" onChange={onChangeHandler} value={formData.lastName}/> 
                           
                        </div>
                        <div className="col-12">
                           <label htmlFor="email" className="form-label">Email</label> 
                           <div className="input-group has-validation">
                              <span className="input-group-text">@</span> 
                              <input type="email" className="form-control" id="email" placeholder="Email" required name="email" onChange={onChangeHandler} value={formData.email}/>                              
                           </div>
                        </div>
                        <div className="col-12">
                           <label htmlFor="phone" className="form-label">Số điện thoại</label>
                            <input type="number" className="form-control" id="phone" placeholder="9839994490" required name="phoneNumber" onChange={onChangeHandler} value={formData.phoneNumber}/>                         
                        </div>
                        <div className="col-12">
                           <label htmlFor="address" className="form-label">Địa chỉ</label> 
                           <input type="text" className="form-control" id="address" placeholder="1234 Main St" required name="address" onChange={onChangeHandler} value={formData.address} />                         
                        </div>
                        <div className="col-md-4">
                           <label htmlFor="state" className="form-label">Tỉnh/Thành</label> 
                           <select className="form-select" id="state" required name="state" onChange={onChangeHandler} value={formData.state} >
                              <option value="">Chọn...</option>
                              <option>Hà Nội</option>
                              <option>Thái Bình</option>
                           </select>
                        </div>

                         <div className="col-md-4">
                           <label htmlFor="city" className="form-label">Xã/Phường</label> 
                           <select className="form-select" id="city" required name="city" onChange={onChangeHandler} value={formData.city} >
                              <option value="">Choose...</option>
                              <option>Ô Diên</option>
                              <option>Đan Phượng</option>
                           </select>
                        </div>
                     </div>
                     <hr className="my-4"/>
                     <button className="w-100 btn btn-primary btn-lg" type="submit" disabled={cartItems.length === 0 || submitting}>{submitting ? "Đang xử lý ... " : "Tiếp tục thanh toán"}</button> 
                  </form>
               </div>
            </div>
        </main>
      </div>
  );
};

export default PlaceOrder
