import { createContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import humanizeDuration from "humanize-duration";
import axios from "axios";
import { toast } from "react-toastify";

export const AppContext = createContext();

export const AppContextProvider = (props) => {
  const currency = import.meta.env.VITE_CURRENCY;
  const backendUrl = import.meta.env.VITE_BACKEND_URL;
  const navigate = useNavigate();

  const [allCourses, setAllCourses] = useState([]);
  const [isEducator, setIsEducator] = useState(true);
  const [enrolledCourses, setEnrolledCourses] = useState([]);
  const [stoken, setStoken] = useState(localStorage.getItem("stoken") || null);
  const [etoken, setEtoken] = useState(localStorage.getItem("etoken") || null);
  const [studentData, setStudentData] = useState(null);
  const [educatorData, setEducatorData] = useState(null);

  const sendOtp = async (email) => {
    try {
      const { data } = await axios.post(
        `${backendUrl}/api/auth/forgotpassword/send-otp`,
        {},
        {
          params: { email },
        }
      );
      if (data.statusCode == 200) {
        toast.success(data.message);
        return true;
      } else {
        toast.error(data.message || "Failed to send otp your email"); // Display error if doctorDtos is missing
        return false;
      }
    } catch (error) {
      toast.error(
        error.response?.data?.message ||
          "An error occurred while sending email to the user"
      );
      return false;
    }
  };

  const verifyOtp = async (email, otp) => {
    try {
      const { data } = await axios.post(
        `${backendUrl}/api/auth/forgotpassword/verify-otp`,
        {},
        {
          params: { email, otp },
        }
      );
      if (data.statusCode == 200) {
        toast.success(data.message);
        return true;
      } else {
        toast.error(data.message || "Wrong OTP provided"); 
        return false;
      }
    } catch (error) {
      console.error(error);
      toast.error(
        error.response?.data?.message || "An error occurred verify  otp"
      );
      return false;
    }
  };

  const resetPassword = async (email, otp, newPassword) => {
    try {
      const { data } = await axios.post(
        `${backendUrl}/api/auth/forgotpassword/reset-password`,
        {},
        {
          params: { email, newPassword, otp },
        }
      );
      if (data.statusCode == 200) {
        toast.success(data.message);
        return true;
      } else {
        toast.error(data.message || "Wrong OTP provided");
        return false;
      }
    } catch (error) {
      console.error(error);
      toast.error(error.response?.data?.message || "Error resetting password");
      return false;
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("stoken");
    setStoken(null);
    navigate("/");
  };

  const handleEducatorLogout = () => {
    localStorage.removeItem("etoken");
    setStoken(null);
    navigate("/");
  };


  useEffect(() => {
    const loadData = async () => {
      if (stoken) {
        try {
          await loadUserProfileData();
        } catch (error) {
          console.error("Failed to load user profile:", error);
          if (error.response?.status === 401) {
            handleLogout();
          }
        }
      }
    };
    loadData();
  }, [stoken]);

  useEffect(() => {
    const loadData = async () => {
      if (etoken) {
        try {
          await loadEducatorProfileData();
        } catch (error) {
          console.error("Failed to load user profile:", error);
          if (error.response?.status === 401) {
            handleEducatorLogout();
          }
        }
      }
    };
    loadData();
  }, [etoken]);



  // Getting User Profile using API
  const loadUserProfileData = async () => {
    try {
      const { data } = await axios.get(backendUrl + `/api/user/getDetails`, {
        headers: {
          Authorization: `Bearer ${stoken}`,
        },
      });

      if (data.statusCode == 200) {
        setStudentData(data.userDto);
      } else {
        toast.error(data.message);
      }
    } catch (error) {
      console.log(error);
      toast.error(error.message);
    }
  };

  const loadEducatorProfileData = async () => {
    try {
      const { data } = await axios.get(backendUrl + `/api/user/getDetails`, {
        headers: {
          Authorization: `Bearer ${etoken}`,
        },
      });

      if (data.statusCode == 200) {
        setEducatorData(data.userDto);
      } else {
        toast.error(data.message);
      }
    } catch (error) {
      console.log(error);
      toast.error(error.message);
    }
  };



  //FetchAllCourses
  const fetchAllCourses = async () => {
    try {
      const { data } = await axios.get(backendUrl + "/api/course/all");

      if (data.success) {
        setAllCourses(data.courseDtoList);
      } else {
        toast.error(data.message);
      }
    } catch (error) {
      toast.error(error.message);
    }
  };


  //Function to calculate average rating of course
  const calculateRating = (course) => {
    if (course.courseRatings.length == 0) {
      return 0;
    }
    let totalRating = 0;
    course.courseRatings.forEach((rating) => {
      totalRating += rating.rating;
    });
    return totalRating / course.courseRatings.length;
  };


  //Function to calculate course chapter time
  const calculateChapterTime = (chapter) => {
    let time = 0;
    chapter.lectures.map((lecture) => (time += lecture.duration));
    return humanizeDuration(time * 60 * 1000, { units: ["h", "m"] });
  };

  //Function to calculate course duration
  const calculateCourseDuration = (course) => {
    let time = 0;
    course.chapters.map((chapter) =>
      chapter.lectures.map((lecture) => (time += lecture.duration))
    );

    return humanizeDuration(time * 60 * 1000, { units: ["h", "m"] });
  };


  //Function to calculate number of lectures
  const calculateTotalNumberLectures = (course) => {
    let totalLectures = 0;
    course.chapters.forEach((chapter) => {
      if (Array.isArray(chapter.lectures)) {
        totalLectures += chapter.lectures.length;
      }
    });
    return totalLectures;
  };

  const value = {
    currency,
    allCourses,
    navigate,
    calculateRating,
    isEducator,
    setIsEducator,
    stoken,
    setStoken,
    etoken,
    setEtoken,
    sendOtp,
    verifyOtp,
    resetPassword,
    backendUrl,
    handleLogout,
    studentData,
    educatorData,
    handleEducatorLogout,
    fetchAllCourses,
    calculateChapterTime,
    setEnrolledCourses,
    calculateCourseDuration,
    calculateTotalNumberLectures,
    enrolledCourses,
  };
  useEffect(() => {
    fetchAllCourses();
  }, []);

  return (
    <AppContext.Provider value={value}>
      {props.children}
    </AppContext.Provider>
  );
};
