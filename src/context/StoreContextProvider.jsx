
import { useEffect, useState } from "react";
import { fetchFoodList } from "../service/foodService";
import { StoreContext } from "./StoreContext";
import { addToCart, getCartData, removeQuantitiesFromCart, removeItemFromCart } from "../service/cartService";

export const StoreContextProvider = (props) =>{

    const [foodList, setFoodList] = useState([])
    const [quantities, setQuantities] = useState({})
    const [token, setToken] = useState("")

    const increaseQuantity = async(foodId) => {
        setQuantities((prev) => ({...prev, [foodId]: (prev[foodId] || 0)+1}))
        await addToCart(foodId, token)
    }

    const decreaseQuantity = async (foodId) => {
        setQuantities((prev) => ({...prev, [foodId]: prev[foodId] > 0 ? prev[foodId] - 1 : 0}))
        await removeQuantitiesFromCart(foodId, token)
    }


    const removeFromCart = async (foodId) => {
        setQuantities((prevQuantities) => {
            const updateQuantities = {...prevQuantities}
            delete updateQuantities[foodId]
            return updateQuantities
            
        })
        await removeItemFromCart(foodId, token)
    }

    const loadCartData = async (token) =>{
        const items = await getCartData(token)
        setQuantities(items)
    }

    const contextValue = {
        foodList,
        setFoodList,
        increaseQuantity,
        decreaseQuantity,
        quantities,
        removeFromCart,
        token,
        setToken,
        setQuantities,
        loadCartData
    }

    useEffect(() => {
        async function loadData() {
            const data = await fetchFoodList()
            setFoodList(data)
            if(localStorage.getItem("token")){
                setToken(localStorage.getItem("token"))
                await loadCartData(localStorage.getItem("token"))
            }
        }
        loadData()
    }, [])

    return(
        <StoreContext.Provider value={contextValue}>
            {props.children}
        </StoreContext.Provider>
    )
}