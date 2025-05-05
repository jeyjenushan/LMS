import React, { useContext, useEffect } from "react";
import { Link } from "react-router-dom";
import { AppContext } from "../../context/AppContext";
import CourseCard from "./CourseCard";

const CourseSection = () => {
  const { allCourses, fetchAllCourses } = useContext(AppContext);

  useEffect(() => {
    fetchAllCourses();
  }, [fetchAllCourses]);

  return (
    <div className="py-16  md:px-40  px-8 text-center">
      <h2 className="text-3xl  font-medium text-gray-800">
        Learn from the best
      </h2>
      <p className="text-sm md:text-base text-gray-500 mt-4 max-w-2xl mx-auto">
        Discover our top-rated courses across various categories.From coding and
        design to <br />
        business and wellness,our courses are crafted to deliver results.
      </p>

      <div className="grid auto-fit-grid px-4 md:px-0 md:py-16 my-10 gap-4 ">
        {allCourses.slice(0, 4).map((course, index) => (
          <CourseCard key={index} course={course} />
        ))}
      </div>

      <div className="flex justify-center">
        <Link
          to="/course-list"
          onClick={() => scrollTo(0, 0)}
          className="text-gray-500 border border-gray-500/30 px-10 py-3 rounded inline-block" /* Added inline-block */
        >
          Show all courses
        </Link>
      </div>
    </div>
  );
};

export default CourseSection;
