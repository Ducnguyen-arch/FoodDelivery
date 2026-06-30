import axios from "axios";

const API_URL = "http://localhost:8080/api/cart"

export const addToCart = async (foodId, token) => {
     try {
            await axios.post(API_URL, {foodId},
            { headers: {Authorization: `Bearer ${token}`}})
     } catch (error) {
        console.error("Lỗi thêm số lượng items trong giỏ hàng", error)
        
     }
}

export const removeQuantitiesFromCart = async (foodId, token) => {
     try {
        axios.post(API_URL+"/remove",
        {foodId},
        { headers: {Authorization: `Bearer ${token}`}})
     } catch (error) {
        console.error("Lỗi xóa số lượng items trong giỏ hàng", error)

     }
}

export const getCartData = async(token) => {
    try {
        const response = await axios.get(API_URL,
        { headers: {Authorization: `Bearer ${token}`}})
        return response.data.items
    } catch (error) {
        console.error("Lỗi hiển thị số lượng items trong giỏ hàng", error)
    }
}

export const removeItemFromCart = async (foodId, token) => {
     try {
         await axios.post(API_URL + "/removeItem", { foodId }, { 
             headers: { Authorization: `Bearer ${token}` } 
         });
     } catch (error) {
         console.error("Lỗi xóa item trong giỏ hàng", error);
     }
}
