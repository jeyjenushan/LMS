import  { useContext } from "react";
import { assets } from "../../assets/assets";
import { Link, useNavigate } from "react-router-dom";
import { AppContext } from "../../context/AppContext";

const Navbar = () => {
  const { educatorData, etoken, handleEducatorLogout } = useContext(AppContext);
  const navigate = useNavigate();

  return (
    <div className="flex items-center justify-between px-4 md:px-8 border-b border-gray-500 py-3">
      <Link to="/">
        <img src={assets.logo} className="w-28 lg:w-32" alt="Website Logo" />
      </Link>

      <div className="flex items-center gap-5 text-gray-500 relative">
        <p>Hi! {educatorData?.name || "Guest"}</p>

        {etoken ? (
          <div className="flex items-center gap-2 cursor-pointer group relative">
            {educatorData?.image ? (
              <img
                className="w-8 h-8 rounded-full object-cover"
                src={educatorData.image}
                alt="User profile"
              />
            ) : (
              <div className="w-8 h-8 rounded-full bg-gray-300 flex items-center justify-center">
                <span className="text-sm font-medium">
                  {educatorData?.name?.charAt(0) || "U"}
                </span>
              </div>
            )}

            <img className="w-2.5" src={assets.dropdown_icon} alt="" />
            <div className="absolute top-0 right-0 pt-14 text-base font-medium text-gray-600 z-20 hidden group-hover:block">
              <div className="min-w-48 bg-stone-100 rounded flex flex-col gap-4 p-4">
                <p
                  onClick={handleEducatorLogout}
                  className="hover:text-black cursor-pointer"
                >
                  Logout
                </p>
              </div>
            </div>
          </div>
        ) : (
          <button onClick={() => navigate("/login")}>
            <img className="max-w-8" src={assets.profile_img} alt="Login" />
          </button>
        )}
      </div>
    </div>
  );
};

export default Navbar;
