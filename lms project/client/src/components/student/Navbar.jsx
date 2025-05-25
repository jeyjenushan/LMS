import React, { useContext, useEffect } from "react";
import { assets } from "../../assets/assets";
import { Link, useNavigate } from "react-router-dom";
import { AppContext } from "../../context/AppContext";

const Navbar = () => {
  const { studentData, setStoken, stoken, handleLogout } =
    useContext(AppContext);
  const isCourseListPage = location.pathname.includes("/course-list");
  const navigate = useNavigate();


  return (
    <div
      className={`flex items-center justify-between px-4 sm:px-10 md:px-14 lg:px-36 border-b border-gray-500 py-4
     ${isCourseListPage ? "bg-white" : "bg-cyan-100/70"}
    `}
    >
      <img
        src={assets.logo}
        alt="Logo"
        className="w-28 lg:w-32 cursor-pointer"
        onClick={() => navigate("/")}
      />

      <div className="hidden md:flex items-center gap-5 text-gray-500">
        <div className="flex items-center gap-5">
          {stoken && (
            <>
              <Link to="/my-enrollments">My Enrollments</Link>
            </>
          )}
        </div>
        {stoken ? (
          <div className="flex items-center gap-2 cursor-pointer group relative">
            {studentData && (
              <img
                className="w-8 rounded-full"
                src={studentData.image}
                alt="User profile"
              />
            )}
            <img className="w-2.5" src={assets.dropdown_icon} alt="" />
            <div className="absolute top-0 right-0 pt-14 text-base font-medium text-gray-600 z-20 hidden group-hover:block">
              <div className="min-w-48 bg-stone-100 rounded flex flex-col gap-4 p-4">
             
                <p
                  onClick={() => navigate("/my-enrollments")}
                  className="hover:text-black cursor-pointer"
                >
                  My Enrollements
                </p>
                <p
                  onClick={handleLogout}
                  className="hover:text-black cursor-pointer"
                >
                  Logout
                </p>
              </div>
            </div>
          </div>
        ) : (
          <button
            onClick={() => navigate("/login")}
            className="bg-blue-600 text-white px-5 py-2 rounded-full"
          >
            Create Account
          </button>
        )}
      </div>

      {/* Mobile View */}
      <div className="md:hidden flex items-center gap-2 sm:gap-5 text-gray-500">
        <div className="flex items-center gap-1 sm:gap-2 max-sm:text">
          {stoken && (
            <>
              <Link to="/my-enrollments">Enrollments</Link>
            </>
          )}
        </div>
        {stoken ? (
          <div className="relative">
            {studentData?.image && (
              <img
                src={studentData.image}
                className="w-8 rounded-full"
                alt="Profile"
              />
            )}
          </div>
        ) : (
          <button onClick={() => navigate("/login")}>
            <img src={assets.user_icon} alt="Log in" />
          </button>
        )}
      </div>
    </div>
  );
};

export default Navbar;
