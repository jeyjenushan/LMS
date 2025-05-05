import React, { useContext, useEffect, useRef, useState } from "react";
import uniqid from "uniqid";
import Quill from "quill";
import { assets } from "../../assets/assets";
import axios from "axios";
import { toast } from "react-toastify";
import { AppContext } from "../../context/AppContext";

const AddCourse = () => {
  const quillRef = useRef(null);
  const editorRef = useRef(null);
  const { currency, etoken, backendUrl } = useContext(AppContext);
  const [courseTitle, setCourseTitle] = useState("");
  const [coursePrice, setCoursePrice] = useState(0);
  const [discount, setDiscount] = useState(0);
  const [image, setImage] = useState(null);
  const [chapters, setChapters] = useState([]);
  const [showPopup, setShowPopup] = useState(false);
  const [currentChapterId, setCurrentChapterId] = useState(null);
  const [lectureDetails, setLectureDetails] = useState({
    title: "",
    videoUrl: "",
    isPreview: false,
  });

  const handleChapter = (action, chapterId) => {
    if (action == "add") {
      const title = prompt("Enter Chapter name");
      if (title) {
        const newChapter = {
          title: title,
          lectures: [],
          collapsed: false,
          order: chapters.length > 0 ? chapters.slice(-1)[0].order + 1 : 1,
        };
        setChapters([...chapters, newChapter]);
      }
    } else if (action == "remove") {
      setChapters(chapters.filter((chapter) => chapter.id !== chapterId));
    } else if (action == "toggle") {
      setChapters(
        chapters.map((chapter) =>
          chapter.id === chapterId
            ? { ...chapter, collapsed: !chapter.collapsed }
            : chapter
        )
      );
    }
  };

  const handleLecture = (action, chapterId, lectureIndex) => {
    if (action == "add") {
      setCurrentChapterId(chapterId);
      setShowPopup(true);
    } else if (action == "remove") {
      setChapters(
        chapters.map((chapter) => {
          if (chapter.id == chapterId) {
            chapter.lectures.splice(lectureIndex, 1);
          }
          return chapter;
        })
      );
    }
  };

  const addLecture = () => {
    setChapters(
      chapters.map((chapter) => {
        if (chapter.id === currentChapterId) {
          const newLecture = {
            ...lectureDetails,
            lectureOrder:
              chapter.lectures.length > 0
                ? chapter.lectures.slice(-1)[0].order + 1
                : 1,
          };
          chapter.lectures.push(newLecture);
        }
        return chapter;
      })
    );
    setShowPopup(false);
    setLectureDetails({
      title: "",
      videoUrl: "",
      isPreview: false,
    });
  };
  const handleSubmit = async (e) => {
    try {
      e.preventDefault();

      if (!image) {
        toast.error("Thumbnail Not Selected");
      }

      const courseData = {
        courseTitle,
        courseDescription: quillRef.current.root.innerHTML,
        coursePrice: Number(coursePrice),
        discount: Number(discount),
        chapters: chapters,
      };

      const formData = new FormData();
      formData.append("courseData", JSON.stringify(courseData));
      formData.append("image", image);

      const { data } = await axios.post(
        backendUrl + "/api/course/add-course",
        formData,
        { headers: { Authorization: `Bearer ${etoken}` } }
      );

      if (data.success) {
        toast.success(data.message);
        setCourseTitle("");
        setCoursePrice(0);
        setDiscount(0);
        setImage(null);
        setChapters([]);
        quillRef.current.root.innerHTML = "";
      } else toast.error(data.message);
    } catch (error) {
      toast.error(error.message);
    }
  };

  useEffect(() => {
    //Initiate Quil only once
    if (!quillRef.current && editorRef.current) {
      quillRef.current = new Quill(editorRef.current, {
        theme: "snow",
      });
    }
  }, []);

  return (
    <div className="h-screen overflow-scroll flex flex-col items-start justify-between md:p-8 md:pb-0 p-4 pt-8 pb-0">
      <form
        onSubmit={handleSubmit}
        className="flex flex-col gap-4 max-w-md w-full text-gray-500"
      >
        <div className="flex flex-col gap-1">
          <p>Course Title</p>
          <input
            type="text"
            value={courseTitle}
            onChange={(e) => setCourseTitle(e.target.value)}
            placeholder="Type Here"
            className="outline-none md:py-2.5 py-2 px-3 rounded border border-gray-500"
            required
          />
        </div>

        <div className="flex flex-col gap-1">
          <p>Course Description</p>
          <div ref={editorRef}></div>
        </div>

        <div className="flex items-center justify-between flex-wrap">
          <div className="flex flex-col gap-1">
            <p>Course Price</p>
            <input
              type="number"
              placeholder="0"
              className="outline-none md:py-2.5 py-2 w-28 px-3 rounded border border-gray-500"
              onChange={(e) => setCoursePrice(e.target.value)}
              value={coursePrice}
              required
            />
          </div>
          <div className="flex md:flex-row flex-col items-center gap-3">
            <p>Course Thumbnail</p>
            <label htmlFor="thumbnailImage" className="flex items-center gap-3">
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
              <img
                src={image ? URL.createObjectURL(image) : ""}
                className="max-h-10"
                alt=""
              />
            </label>
          </div>
        </div>

        <div className="flex flex-col gap-1">
          <p>Discount %</p>
          <input
            type="number"
            onChange={(e) => setDiscount(e.target.value)}
            value={discount}
            placeholder="0"
            min={0}
            max={100}
            className="outline-none md:py-2.5 py-2 w-28 px-3 rounded border border-gray-500"
            required
          />
        </div>

        {/*Adding chapters & lectures */}
        <div>
          {chapters.map((chapter, chapterIndex) => (
            <div key={chapterIndex} className="bg-white border rounded-lg mb-4">
              <div className="flex justify-between items-center p-4 border-b">
                <div className="flex items-center">
                  <img
                    onClick={() => handleChapter("toggle", chapter.chapterId)}
                    src={assets.dropdown_icon}
                    width={14}
                    alt=""
                    className={`mr-2 cursor-pointer transition-all ${
                      chapter.collapsed && "-rotate-90"
                    }`}
                  />
                  <span className="font-semibold">
                    {chapterIndex + 1}
                    {chapter.title}
                  </span>
                </div>
                <span className="text-gray-500">
                  {chapter.lectures.length} Lectures
                </span>
                <img
                  onClick={() => handleChapter("remove", chapter.id)}
                  src={assets.cross_icon}
                  alt=""
                  className="cursor-pointer"
                />
              </div>
              {!chapter.collapsed && (
                <div className="p-4">
                  {chapter.lectures.map((lecture, lectureIndex) => (
                    <div
                      key={lectureIndex}
                      className="flex justify-between items-center mb-2"
                    >
                      <span>
                        {lectureIndex + 1}
                        {lecture.title}-{lecture.duration}mins-
                        <a
                          href={lecture.videoUrl}
                          target="_blank"
                          className="text-blue-500"
                        >
                          Link
                        </a>
                        -{lecture.isPreview ? "Free Preview" : "Paid"}
                      </span>
                      <img
                        onClick={() =>
                          handleLecture("remove", chapter.id, lectureIndex)
                        }
                        src={assets.cross_icon}
                        alt=""
                        className="cursor-pointer"
                      />
                    </div>
                  ))}
                  <div
                    onClick={() => handleLecture("add", chapter.id)}
                    className="inline-flex bg-gray-100 p-2 rounded cursor-pointer mt-2"
                  >
                    +Add Lecture
                  </div>
                </div>
              )}
            </div>
          ))}
          <div
            className="flex justify-center items-center bg-blue-100 p-2 rounded-lg cursor-pointer"
            onClick={() => handleChapter("add")}
          >
            + Add Chapter
          </div>
          {showPopup && (
            <div className="fixed inset-0 flex items-center justify-center bg-gray-800 bg-opacity-50">
              <div className="bg-white text-gray-700 p-4 rounded relative w-full max-w-80">
                <h2 className="text-lg font-semibold mb-4">Add Lecture</h2>
                <div className="mb-2">
                  <p>Lecture Title</p>
                  <input
                    type="text"
                    className="mt-1 block w-full border rounded py-1 px-2"
                    value={lectureDetails.title}
                    onChange={(e) =>
                      setLectureDetails({
                        ...lectureDetails,
                        title: e.target.value,
                      })
                    }
                  />
                </div>
                <div className="mb-2">
                  <p>Duration (minutes)</p>
                  <input
                    type="text"
                    className="mt-1 block w-full border rounded py-1 px-2"
                    value={lectureDetails.duration}
                    onChange={(e) =>
                      setLectureDetails({
                        ...lectureDetails,
                        duration: e.target.value,
                      })
                    }
                  />
                </div>

                <div className="mb-2">
                  <p>Lecture URL</p>
                  <input
                    type="text"
                    className="mt-1 block w-full border rounded py-1 px-2"
                    value={lectureDetails.videoUrl}
                    onChange={(e) =>
                      setLectureDetails({
                        ...lectureDetails,
                        videoUrl: e.target.value,
                      })
                    }
                  />
                </div>

                <div className="mb-2">
                  <p>Is Preview FREE?</p>
                  <input
                    type="checkbox"
                    className="mt-1 scale-125"
                    value={lectureDetails.isPreview}
                    onChange={(e) =>
                      setLectureDetails({
                        ...lectureDetails,
                        isPreview: e.target.checked,
                      })
                    }
                  />
                </div>

                <button
                  onClick={addLecture}
                  type="button"
                  className="w-full bg-blue-400 text-white px-4 py-2 rounded"
                >
                  Add
                </button>
                <img
                  src={assets.cross_icon}
                  onClick={() => setShowPopup(false)}
                  className="absolute top-4 right-4 w-4 cursor-pointer"
                  alt=""
                />
              </div>
            </div>
          )}
        </div>
        <button
          type="submit"
          className="bg-black text-white w-max py-2.5 px-8 rounded my-4"
        >
          ADD
        </button>
      </form>
    </div>
  );
};

export default AddCourse;
