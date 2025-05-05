import axios from "axios";
import React, { useContext, useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { AppContext } from "../context/AppContext";
import { toast } from "react-toastify";

const Verify = () => {
  const [searchParams, setSearchParams] = useSearchParams();

  const success = searchParams.get("success");
  const purchaseId = searchParams.get("purchaseId");

  const { backendUrl, stoken, fetchUserEnrolledCourses } =
    useContext(AppContext);

  const navigate = useNavigate();

  // Function to verify stripe payment
  const verifyStripe = async () => {
    if (success == "true") {
      await axios
        .post(
          `http://localhost:8080/api/purchases/complete?purchaseId=${purchaseId}`
        )
        .then((res) => {
          navigate("/my-enrollments");
        })
        .catch((err) => {
          console.error("Error completing purchase", err);
        });
    } else {
      await axios.post(
        `http://localhost:8080/api/purchases/fail?purchaseId=${purchaseId}`
      );
    }
  };

  useEffect(() => {
    verifyStripe();
  }, [success, purchaseId, navigate]);

  return (
    <div className="min-h-[60vh] flex items-center justify-center">
      <div className="w-20 h-20 border-4 border-gray-300 border-t-4 border-t-primary rounded-full animate-spin"></div>
    </div>
  );
};

export default Verify;
