import { useState } from "react"
import FoodDisplay from "../../components/FoodDisplay/FoodDisplay"

const ExploreFood = () => {
  const [category, setCategory] = useState('All')
  const [searchText, setSearchText] = useState('')

  return (
    <>
    <div className="container">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <form onSubmit={(e) => e.preventDefault()}>
            <div className="input-group mb-3">
              <select className="form-select mt-2" style={{"maxWidth" :"150px"}} onChange={(e) => setCategory(e.target.value)}>
                <option value="All">All</option>
                <option value="Bánh ngọt">Bánh Ngọt</option>
                <option value="Hamburger">Hamburger</option>
                <option value="Đồ ăn nhanh">Đồ ăn nhanh</option>
                <option value="Pizza">Pizza</option>
                <option value="Salad">Salad</option>
                <option value="Kem tươi">Kem tươi</option>
                <option value="Đồ uống">Đồ uống</option>
                <option value="Hoa quả">Hoa quả</option>
              </select>
              <input type="text" className="form-control mt-2" placeholder="Tìm kiếm món ăn ưa thích..." 
               onChange={(e) => setSearchText(e.target.value)} value={searchText} />
              <button className=" btn btn-primary mt-2" type="submit">
                <i className=" bi bi-search"></i>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
    <FoodDisplay category={category} searchText={searchText} />
  </>
  )
}
export default ExploreFood