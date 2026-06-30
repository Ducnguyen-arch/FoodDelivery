import { useState } from "react"
import "./Register.css"
import {Link, useNavigate} from "react-router-dom"
import {toast} from 'react-toastify'
import { registerUser } from "../../service/authService"

const Register = () => {
  const navigate = useNavigate()
  const [data, setData] = useState({
    name: '',
    email: '',
    password: ''
  })

  const onChangeHandler = (event) => {
    const name = event.target.name
    const value = event.target.value;
    setData(data => ({...data, [name]: value}));
  }

  const onSubmitHandler = async (event) => {
    event.preventDefault()
    try {
      const response = await registerUser(data)
      if(response.status === 201){
        toast.success("Đăng ký tài khoản thành công")
        navigate('/login')
      }else{
        toast.error("Vui lòng thử lại")
      }
    }catch (error) {
      toast.error("Thử lại", error)
    }
  }

  return (
    
    <div className="register-container">
    <div className="row">
      <div className="col-sm-9 col-md-7 col-lg-5 mx-auto">
        <div className="card border-0 shadow rounded-3 my-5">
          <div className="card-body p-4 p-sm-5">
            <h5 className="card-title text-center mb-5 fw-light fs-5">Đăng ký tài khoản</h5>
            <form onSubmit={onSubmitHandler}>
             <div className="form-floating mb-3">
                <input type="text" className="form-control" id="floatingName" placeholder="Nguyen Duc"
                  name="name" onChange={onChangeHandler} value={data.name} required/>
                <label htmlFor="floatingName">Tên đầy đủ</label>
              </div>
              <div className="form-floating mb-3">
                <input type="email" className="form-control" id="floatingInput" placeholder="name@example.com"
                  name="email" onChange={onChangeHandler} value={data.email} required
                />
                <label htmlFor="floatingInput">Địa chỉ email</label>
              </div>
              <div className="form-floating mb-3">
                <input type="password" className="form-control" id="floatingPassword" placeholder="Password"
                  name="password" onChange={onChangeHandler} value={data.password} required/>
                <label htmlFor="floatingPassword">Mật khẩu</label>
              </div>

              <div className="d-grid">
                <button className="btn btn-outline-primary btn-login text-uppercase" type="submit">Đăng ký</button>
              </div>
              <div className="mt-4">
                    Đã có tài khoản? <Link to="/login">Đăng nhập</Link>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
    </div>
  )
}

export default Register