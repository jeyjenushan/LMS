import React, { useContext } from "react";
import Home from "./pages/student/Home";
import CoursesList from "./pages/student/CoursesList";
import CourseDetails from "./pages/student/CourseDetails";
import MyEnrollments from "./pages/student/MyEnrollments";
import Player from "./pages/student/Player";
import Loading from "./components/student/Loading";
import Educator from "./pages/educator/Educator";
import Dashboard from "./pages/educator/Dashboard";
import AddCourse from "./pages/educator/AddCourse";
import MyCourses from "./pages/educator/MyCourses";
import StudentsEnrolled from "./pages/educator/StudentsEnrolled";
import Navbar from "./components/student/Navbar";
import {
  Navigate,
  Outlet,
  Route,
  Routes,
  useLocation,
  useMatch,
} from "react-router-dom";
import "quill/dist/quill.snow.css";
import Login from "./pages/Login";
import Register from "./pages/Register";
import ForgotPassword from "./pages/ForgotPassword";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { AppContext } from "./context/AppContext";
import Verify from "./pages/verify";

const App = () => {
  const location = useLocation();
  const isEducatorRoute = useMatch("/educator/*");
  const { stoken, etoken } = useContext(AppContext);

  // Protected Route Wrapper
  const ProtectedEducatorRoute = () => {
    return etoken ? (
      <Outlet />
    ) : (
      <Navigate to="/login" state={{ from: location }} replace />
    );
  };

  const shouldShowNavbar = () => {
    const authRoutes = ["/login", "/register", "/forgotPassword"];
    return !authRoutes.includes(location.pathname) && !isEducatorRoute;
  };
  const ProtectedAuthUserRoute = () => {
    return stoken ? (
      <Outlet />
    ) : (
      <Navigate to="/login" state={{ from: location }} replace />
    );
  };
  return (
    <div className="text-default min-h-screen bg-white">
      <ToastContainer
        position="top-right"
        autoClose={5000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="light"
      />
      {shouldShowNavbar() && <Navbar />}

      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/forgotPassword" element={<ForgotPassword />} />
        <Route path="/register" element={<Register />} />
        <Route path="/" element={<Home />} />
        <Route path="/course-list" element={<CoursesList />} />
        <Route path="/course-list/:input" element={<CoursesList />} />
        <Route path="/course/:id" element={<CourseDetails />} />
        <Route path="/loading/:path" element={<Loading />} />

        <Route element={<ProtectedAuthUserRoute />}>
          <Route path="/my-enrollments" element={<MyEnrollments />} />
          <Route path="/player/:courseId" element={<Player />} />
          <Route path="/verify" element={<Verify />} />
  
        </Route>

        <Route element={<ProtectedEducatorRoute />}>
          <Route path="/educator" element={<Educator />}>
            <Route path="/educator" element={<Dashboard />} />
            <Route path="add-course" element={<AddCourse />} />
            <Route path="my-courses" element={<MyCourses />} />
            <Route path="student-enrolled" element={<StudentsEnrolled />} />
          </Route>
        </Route>
      </Routes>
    </div>
  );
};

export default App;
