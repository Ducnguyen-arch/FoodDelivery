import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import './ListFood.css'
import { deleteFood, getFoodList } from "../../services/foodService";


const ListFood = () => {
  const [listFood, setList] = useState([]);
  useEffect(() => {
    const fetchList = async () => {
       try {
        const data =  await getFoodList()
        setList(data)
       } catch (error) {
        toast.error("Lỗi không hiển thị danh sách Món ăn", error)
       }
    };
    fetchList();
  }, []);

  const removeFood = async (id) =>{
      try {
        const success = await deleteFood(id)
        if(success){
          toast.success("Xóa thành công!")
          setList(prevList => prevList.filter(food => food.id !== id))
        }else{
          toast.error("Lỗi khi xóa món ăn. Vui lòng thử lại!")
        }
      } catch (error) {
          toast.error("Lỗi khi xóa món ăn. Vui lòng thử lại!", error)
      }
  }

  return (
    <div className="py-5 row justify-content-center">
      <div className="col-11 card p-4 shadow-sm">
        <h2 className="mb-4">Danh Sách Món Ăn</h2>
        
        {listFood.length === 0 ? (
          <p className="no-data text-center text-muted">Hiện không có món ăn nào trong danh sách.</p>
        ) : (
          <table className="table table-striped align-middle">
            <thead>
              <tr>
                <th>Image</th>
                <th>Food Name</th>
                <th>Category</th>
                <th>Price</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {/* SỬA TẠI ĐÂY: Thay đổi dấu {} sang () để tự động return giao diện */}
              {listFood.map((food, index) => (
                <tr key={food.id || index}>
                  <td>
                    <img
                      src={food.imageUrl} // Sửa lỗi chính tả imageUR
                      height={48}
                      width={64}
                      alt={food.name}
                      style={{ objectFit: "cover", borderRadius: "4px" }}
                      onError={(e) => {
                        e.target.src = "https://via.placeholder.com/80";
                      }}
                    />
                  </td>
                  <td className="fw-bold">{food.name}</td>
                  <td>
                    <span className="badge bg-info text-dark">{food.category}</span>
                  </td>
                  <td className="text-danger fw-bold">
                    {new Intl.NumberFormat("vi-VN").format(food.price)} VND
                  </td>
                  <td>
                    <div className="d-flex gap-2">
                      <button className="btn btn-sm btn-primary">
                        Sửa
                      </button>
                      <button className="btn btn-sm btn-danger" onClick={() => removeFood(food.id)}>
                        Xóa
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};
export default ListFood;
