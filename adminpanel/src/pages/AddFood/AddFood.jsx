import { useState } from "react"
import { assets } from "../../assets/assets"
import { addFood } from "../../services/foodService"
import { toast } from "react-toastify"

const AddFood = () => {
  const [image, setImage] = useState(false)
  const [loading,setLoading] = useState(false)
  const [data, setData] = useState({
    name : '',
    description: '',
    price: '',
    category: 'Cake'
  })

  const onChangeHandler = (event) => {
    const name = event.target.name
    const value = event.target.value
    setData(data => ({...data, [name] : value}))
  }

  const onSubmitHandler = async (event) =>{
    event.preventDefault()
    if(!image){
      toast.error('Please select an image')
      return
    }
    try {
      setLoading(true)
      await addFood({...data, price:Number(data.price)}, image)
      toast.success('Add food successfully')
      setData({name:'', description: '', category: 'Cake', price: ''})
      setImage(false)
    }catch (error) {
      console.log(error)
      toast.error('Error adding food')
    } finally{
      setLoading(false)
    }
  }

  return (
    <div className="mx-2 mt-2">
      <div className="row">
        <div className="card col-md-4">
          <div className="card-body">
            <h2 className="mb-4">Add Food</h2>
            <form onSubmit={onSubmitHandler}>
              <div className="mb-3">
                    <label htmlFor="image" className="form-label">
                      <img src={image ? URL.createObjectURL(image) : assets.upload} width={98} alt=""></img>
                    </label>
                    <input type="file" id="image" className="form-control" hidden onChange={(e) => setImage(e.target.files[0])} />
              </div>

              <div className="mb-3">
                    <label htmlFor="name" className="form-label">Tên món ăn</label>
                    <input type="text" placeholder="Cake" id="name" className="form-control" required name="name" onChange={onChangeHandler} value={data.name}/>
              </div>
            
            
              <div className="mb-3">
                    <label htmlFor="description" className="form-label">Chi tiết</label>
                    <textarea type="text" placeholder="Write descriptions about food" id="description" rows="5" className="form-control" required name="description" onChange={onChangeHandler} value={data.description}/>
              </div>

              <div className="mb-3">
                    <label htmlFor="category" className="form-label">Danh mục</label>
                    <select name="category" id="category" className="form-control" onChange={onChangeHandler} value={data.category}>
                      <option value="Bánh Ngọt">Bánh ngọt</option>
                      <option value="Humburger">Humburger</option>
                      <option value="Đồ ăn nhanh">Đồ ăn nhanh</option>
                      <option value="PIzza">PIzza</option>
                      <option value="Salad">Salad</option>
                      <option value="Kem tươi">Kem tươi</option>
                      <option value="Đồ uống">Đồ uống</option>
                      <option value="Hoa quả">Hoa quả</option>
                    </select>
              </div>

               <div className="mb-3">
                    <label htmlFor="price" className="form-label">Price</label>
                    <input type="number" placeholder="&#36;200" name="price" id="price" className='form-control' onChange={onChangeHandler} value={data.price}></input>
              </div>
               <button type="submit" disabled={loading} className="btn btn-primary">{loading ? "Đang thêm món ăn..." : "Thêm"}</button>
            </form>
          </div>
        </div>
      </div>
</div>
  )
}


export default AddFood