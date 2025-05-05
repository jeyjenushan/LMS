import React, { useContext, useState } from "react";
import { AppContext } from "../context/AppContext";
import axios from "axios";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";
import { Oval } from "react-loader-spinner";
import { assets } from "../assets/assets";

const Register = () => {
  const navigate = useNavigate();
  const { backendUrl } = useContext(AppContext);

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
  });
  const [image, setImage] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleFileChange = (e) => {
    setImage(e.target.files[0]);
  };

  const onSubmitHandler = async (e) => {
    e.preventDefault();
    setLoading(true);

    const studentObj = {
      name: formData.name,
      email: formData.email,
      password: formData.password,
    };

    const formDataToSend = new FormData();
    formDataToSend.append("user", JSON.stringify(studentObj));
    if (image) {
      formDataToSend.append("image", image);
    }

    try {
      const response = await axios.post(
        backendUrl + "/api/auth/register",
        formDataToSend,
        { headers: { "Content-Type": "multipart/form-data" } }
      );

      if (response.data.statusCode === 200) {
        toast.success("Registration successful! Please login.");
        navigate("/login");
      } else {
        throw new Error(response.data.message || "Registration failed");
      }
    } catch (err) {
      const errorMessage =
        err.response?.data?.message || err.message || "Registration failed";
      toast.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="w-full max-w-2xl mx-auto p-4">
      <div className="bg-white rounded-lg shadow-md p-6">
        <h2 className="text-2xl font-semibold text-center mb-6">
          Student Registration
        </h2>

        <form
          onSubmit={onSubmitHandler}
          className="bg-white p-8 rounded-lg shadow-md"
        >
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Left Column */}
            <div className="space-y-4">
              <div>
                <label className="block text-gray-700 mb-1">Full Name</label>
                <input
                  name="name"
                  onChange={handleChange}
                  value={formData.name}
                  className="w-full p-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                  type="text"
                  required
                />
              </div>

              <div>
                <label className="block text-gray-700 mb-1">Email</label>
                <input
                  name="email"
                  onChange={handleChange}
                  value={formData.email}
                  className="w-full p-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                  type="email"
                  required
                />
              </div>

              <div>
                <label className="block text-gray-700 mb-1">Password</label>
                <input
                  name="password"
                  onChange={handleChange}
                  value={formData.password}
                  className="w-full p-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                  type="password"
                  required
                />
              </div>
            </div>
          </div>

          {/* Profile Image Upload */}
          <div className="mt-6">
            <label className="block text-gray-700 mb-1">Profile Image</label>
            <div className="flex items-center">
              <label className="cursor-pointer bg-gray-100 hover:bg-gray-200 px-4 py-2 rounded border border-gray-300">
                <img
                  src={assets.file_upload_icon}
                  alt=""
                  className="p-3 bg-blue-500 rounded"
                />
                <input
                  type="file"
                  id="thumbnailImage"
                  onChange={(e) => setImage(e.target.files[0])}
                  accept="image/"
                  hidden
                />
              </label>
              <span className="ml-2 text-gray-500">
                {image ? image.name : "No file chosen"}
              </span>
            </div>
          </div>

          {/* Register Button */}
          <div className="mt-8">
            <button
              type="submit"
              className="bg-blue-500 text-white w-full py-2 rounded-md text-base hover:bg-blue-700 transition-colors flex justify-center items-center"
              disabled={loading}
            >
              {loading ? (
                <Oval
                  height={20}
                  width={20}
                  color="white"
                  visible={true}
                  ariaLabel="oval-loading"
                />
              ) : (
                "Register"
              )}
            </button>
          </div>

          {/* Login Link */}
          <div className="mt-4 text-center">
            <p className="text-gray-600">
              Already have an account?{" "}
              <a href="/login" className="text-blue-600 hover:underline">
                Login here
              </a>
            </p>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Register;
