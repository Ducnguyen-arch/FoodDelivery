export function formatCurrencyUnit(value){
    return new Intl.NumberFormat('vi-VN').format(value);
}

export function calculateCartTotal(cartItems, quantities){

        //calculate
        const subTotal = cartItems.reduce((acc, food) => acc + food.price * quantities[food.id], 0)
        const shipping = subTotal === 0 ? 0.0 : 25000;
        const tax = subTotal * 0.05; //5% tax
        const total = subTotal + shipping + tax

        return {subTotal, shipping, tax, total}
}