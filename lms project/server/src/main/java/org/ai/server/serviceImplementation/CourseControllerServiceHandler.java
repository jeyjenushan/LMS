package org.ai.server.serviceImplementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.ai.server.dto.ChapterDto;
import org.ai.server.dto.CourseDto;
import org.ai.server.dto.LectureDto;
import org.ai.server.dto.Response;
import org.ai.server.exception.CourseOperationException;
import org.ai.server.mapper.DtoConverter;
import org.ai.server.model.*;
import org.ai.server.repository.ChapterRepository;
import org.ai.server.repository.CourseRepository;
import org.ai.server.repository.LectureRepository;
import org.ai.server.repository.UserRepository;
import org.ai.server.service.CloudinaryService;
import org.ai.server.service.CourseControllerService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class CourseControllerServiceHandler implements CourseControllerService {

    private final CourseRepository courseRepository;
    private  final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final ObjectMapper objectMapper;
    private final LectureRepository lectureRepository;
    private final ChapterRepository chapterRepository;


    @Override
    public Response getAllCourses() {
        Response response = new Response();
        try {
            List<CourseEntity> courses = courseRepository.findByIsPublished(true);
            if (courses.isEmpty()) {
                response.setMessage("No published courses available at this time");
                response.setStatusCode(200);
                response.setCourseDtoList(Collections.emptyList());
                response.setSuccess(true);
                return response;
            }


            List<CourseDto> courseDtoList = courses.stream()
                    .map(course -> {
                        CourseDto dto = DtoConverter.convertTheCourseToCourseDto(course);
                        dto.getEducator().setImage(null);
                        dto.setChapters(null);
                        dto.setEnrolledStudents(null);
                        return dto;
                    })
                    .collect(Collectors.toList());

            response.setMessage("All courses successfully retrieved");
            response.setStatusCode(200);
            response.setCourseDtoList(courseDtoList);
            response.setSuccess(true);

        } catch (DataAccessException e) {
            throw new CourseOperationException(
                    "We encountered a problem while retrieving courses.",
                    "Data access error: " + e.getMessage()
            );
        } catch (Exception e) {
            throw new CourseOperationException(
                    "An unexpected error occurred while loading courses.",
                    "Unexpected error: " + e.getMessage()
            );
        }
        return response;
    }

    @Override
    public Response getCourseById(Long id) {
        Response response=new Response();
     try{
         CourseEntity course = courseRepository.findById(id)
                 .orElseThrow(() -> new CourseOperationException(
                         "The requested course doesn't exist",
                         "Course not found with ID: " + id
                 ));

         processLectureUrls(course);
         
         CourseDto courseDto=DtoConverter.convertTheCourseToCourseDto(course);
         response.setCourseDto(courseDto);
         response.setStatusCode(200);
         response.setSuccess(true);
         response.setMessage("Course successfully retrieved");



     }catch (CourseOperationException e) {

            response.setMessage(e.getUserMessage());
            response.setStatusCode(404); // Not Found
            response.setSuccess(false);
            response.setCourseDto(null);

        }catch (DataAccessException e) {

         response.setMessage("We're having trouble accessing course data. Please try again later.");
         response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value());
         response.setSuccess(false);
         response.setCourseDto(null);



     }


     catch (Exception e) {

         response.setMessage("We couldn't load the course details. Please try again later.");
         response.setStatusCode(500);
         response.setSuccess(false);
         response.setCourseDto(null);
     }
     return response;
    }

    @Override
    @Transactional
    public Response addCourse(String courseDataJson, MultipartFile imageFile, String email) {
        Response response = new Response();
        try {

            if (imageFile == null || imageFile.isEmpty()) {
               response.setMessage("Please provide an image file");
               return response;
            }

            CourseDto courseDto = objectMapper.readValue(courseDataJson, CourseDto.class);


            String thumbnailUrl = cloudinaryService.uploadFile(imageFile);

            UserEntity user = userRepository.findByEmail(email);


            CourseEntity course = new CourseEntity();
            course.setTitle(courseDto.getCourseTitle());
            course.setDescription(courseDto.getCourseDescription());
            course.setThumbnailUrl(thumbnailUrl);

            course.setPrice(courseDto.getCoursePrice());
            course.setDiscount(courseDto.getDiscount());

            course.setEducator(user);
            user.getCreatedCourses().add(course);


            course.setIsPublished(true);
            CourseEntity savedCourse = courseRepository.save(course);



            // 5. Process chapters and lectures
            if (courseDto.getChapters() != null) {
                List<ChapterEntity> chapters = new ArrayList<>();

                for (ChapterDto chapterDto : courseDto.getChapters()) {
                    ChapterEntity chapter = new ChapterEntity();
                    chapter.setTitle(chapterDto.getTitle());
                    chapter.setOrder(chapterDto.getOrder());
                    chapter.setCourse(savedCourse);
                    // Save chapter to get an ID
                    ChapterEntity savedChapter = chapterRepository.save(chapter);

                    // Process lectures for this chapter
                    if (chapterDto.getLectures() != null) {
                        List<LectureEntity> lectures = new ArrayList<>();

                        for (LectureDto lectureDto : chapterDto.getLectures()) {
                            LectureEntity lecture = new LectureEntity();
                            lecture.setTitle(lectureDto.getTitle());
                            lecture.setDuration(lectureDto.getDuration());
                            lecture.setVideoUrl(lectureDto.getVideoUrl());
                            lecture.setIsPreview(lectureDto.isPreview());
                            lecture.setOrder(lectureDto.getOrder());
                            lecture.setChapter(chapter);
                            lectures.add(lecture);
                        }
                        // Save lectures for this chapter
                        lectureRepository.saveAll(lectures);
                        savedChapter.getLectures().addAll(lectures);
                        response.setLectureDtoList(DtoConverter.convertTheLectureListToLectureDtoList(lectures));

                    }

                    chapters.add(chapter);
                }

                response.setChapterDtoList(DtoConverter.convertTheChapterListToChapterDtoList(chapters));
                savedCourse.getChapters().addAll(chapters);
                courseRepository.save(savedCourse);

            }





            CourseDto savedCourseDto = DtoConverter.convertTheCourseToCourseDto(savedCourse);
            response.setMessage("Course successfully added with all chapters and lectures");
            response.setStatusCode(200);
            response.setCourseDto(savedCourseDto);

            response.setSuccess(true);


        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setStatusCode(500);
            response.setSuccess(false);
        }
        return response;
    }

    private void processLectureUrls(CourseEntity course) {
        course.getChapters().forEach(chapter -> {
            chapter.getLectures().forEach(lecture -> {
                if (!lecture.getIsPreview()) {
                    lecture.setVideoUrl("");
                }
            });
        });
    }
}
