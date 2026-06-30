import { useRef } from "react"
import { categories } from "../../assets/assets"
import './ExploreMenu.css'

const ExploreMenu = ({category, setCategory}) => {

    const menuRef = useRef(null)
    const scrollLeft = () => {
        if (menuRef.current) {
            menuRef.current.scrollBy({left: -200, behavior: 'smooth'})
        }
    }

    const scrollRight = () => {
        if (menuRef.current) {
            menuRef.current.scrollBy({left: 200, behavior: 'smooth'})
        }
    }


  return (
    <div className="explore-menu position-relative">
        <h1 className="d-flex align-items-center justify-content-between">
            Khám phá Menu
        <div className="d-flex">
            <i className="bi bi-arrow-left-circle scroll-icon" onClick={scrollLeft}></i>
            <i className="bi bi-arrow-right-circle scroll-icon" onClick={scrollRight}></i>
        </div>
        </h1>
        <p>Khám phá món ăn được đề xuất</p>
        <div className="d-flex justify-content-between gap-4 overflow-auto explore-menu-list" ref={menuRef}>
            {
                categories.map((item, index) => {
                    return(
                        <div key={index} className="text-center explore-menu-list-item" onClick={() => setCategory(prevItem => prevItem === prevItem.category ? 'All' : item.category)}>
                            <img src={item.icon} className={item.category === category ? 'rounded-circle active': 'rounded-circle'} height={128} width={128}></img>
                            <p className="mt-2 fw-bold">{item.category}</p>
                        </div>
                    )
                })
            }
        </div>
        <hr/>
    </div>
  )
}

export default ExploreMenu