import { Link } from "react-router-dom"

const Header = () => {
  return (
    <div className="p-5 mb-4 bg-light rounded-3 mt-1"> 
        <div className="container-fluid py-5">
            <h1 className="display-5 fw-bold">Đặt món theo sở thích của bạn tại đây</h1>
            <p className="col-md-8 fs-4">Khám phá món ăn và đồ uống tại Hà Nội</p>
            <Link to= "/explore" className="btn btn-primary">Khám phá</Link>
        </div>
    </div>
  )
}

export default Header