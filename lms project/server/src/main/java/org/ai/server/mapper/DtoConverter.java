package org.ai.server.mapper;

import org.ai.server.dto.*;
import org.ai.server.model.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DtoConverter {

    public static UserDto convertTheUserToUserDto(UserEntity user){

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setImage(user.getImage());
        userDto.setRole(user.getRole());

        return userDto;
    }

    public static List<UserDto> convertTheUserListToUserDtoList(List<UserEntity> userList){
        return userList.stream().map(DtoConverter::convertTheUserToUserDto).collect(Collectors.toList());
    }


    public static LectureDto convertTheLectureToLectureDto(LectureEntity lecture){
        LectureDto lectureDto = new LectureDto();
        lectureDto.setLectureId(lecture.getId());
        lectureDto.setTitle(lecture.getTitle());
        lectureDto.setDuration(lecture.getDuration());
        lectureDto.setPreview(lecture.getIsPreview());
        lectureDto.setOrder(lecture.getOrder());
        return lectureDto;
    }

    public static List<LectureDto> convertTheLectureListToLectureDtoList(List<LectureEntity> lectureEntityList){
        return lectureEntityList.stream().map(DtoConverter::convertTheLectureToLectureDto).collect(Collectors.toList());
    }

    public static ChapterDto convertTheChapterToChapterDto(ChapterEntity chapterEntity){
        ChapterDto chapterDto = new ChapterDto();
        chapterDto.setChapterId(chapterEntity.getId());
        chapterDto.setTitle(chapterEntity.getTitle());
        chapterDto.setOrder(chapterEntity.getOrder());

        // Map lectures if not null
        if (chapterEntity.getLectures() != null) {
            List<LectureDto> lectureDtos = chapterEntity.getLectures().stream().map(lecture -> {
                LectureDto ld = new LectureDto();
                ld.setLectureId(lecture.getId());
                ld.setTitle(lecture.getTitle());
                ld.setDuration(lecture.getDuration());
                ld.setVideoUrl(lecture.getVideoUrl());
                ld.setPreview(lecture.getIsPreview());
                ld.setOrder(lecture.getOrder());
                return ld;
            }).collect(Collectors.toList());

            chapterDto.setLectures(lectureDtos);
        } else {
            chapterDto.setLectures(Collections.emptyList());
        }


        return chapterDto;
    }

    public static List<ChapterDto> convertTheChapterListToChapterDtoList(List<ChapterEntity> chapterEntityList){
        return chapterEntityList.stream().map(DtoConverter::convertTheChapterToChapterDto).collect(Collectors.toList());
    }

    public static CourseRatingDto convertTheCourseRatingToCourseRatingDto(CourseRatingEntity courseRatingEntity){
        CourseRatingDto courseRatingDto = new CourseRatingDto();
        courseRatingDto.setId(courseRatingEntity.getId());
        courseRatingDto.setRating(courseRatingEntity.getRating());
        courseRatingDto.setUserId(courseRatingEntity.getUser().getId());

        return courseRatingDto;
    }
    public static List<CourseRatingDto> convertTheCourseRatingListToCourseRatingDtoList(List<CourseRatingEntity> courseRatingEntityList){

        return courseRatingEntityList.stream().map(DtoConverter::convertTheCourseRatingToCourseRatingDto).collect(Collectors.toList());
    }


    public static CourseDto convertTheCourseToCourseDto(CourseEntity courseEntity) {
        CourseDto courseDto = new CourseDto();

        courseDto.setId(courseEntity.getId());
        courseDto.setCourseTitle(courseEntity.getTitle());
        courseDto.setCourseDescription(courseEntity.getDescription());
        courseDto.setCoursePrice(courseEntity.getPrice());
        courseDto.setThumbnailUrl(courseEntity.getThumbnailUrl());
        courseDto.setPublished(courseEntity.getIsPublished());
        courseDto.setCreatedAt(courseEntity.getCreatedAt());
        courseDto.setUpdatedAt(courseEntity.getUpdatedAt());


        // Set educator
        if (courseEntity.getEducator() != null) {
            courseDto.setEducator(convertTheUserToUserDto(courseEntity.getEducator()));
        }

        // Set chapters
        if (courseEntity.getChapters() != null) {
            List<ChapterDto> chapterDtos = courseEntity.getChapters().stream()
                    .map(DtoConverter::convertTheChapterToChapterDto) // Use the correct reference if static
                    .collect(Collectors.toList());
            courseDto.setChapters(chapterDtos);
        } else {
            courseDto.setChapters(Collections.emptyList());
        }

        // Set enrolled students
        if (courseEntity.getStudents() != null) {
            courseDto.setEnrolledStudents(convertTheUserListToUserDtoList(courseEntity.getStudents()));
        } else {
            courseDto.setEnrolledStudents(Collections.emptyList());
        }

        // Set ratings
        if (courseEntity.getRatings() != null) {
            courseDto.setCourseRatings(convertTheCourseRatingListToCourseRatingDtoList(courseEntity.getRatings()));
        } else {
            courseDto.setCourseRatings(Collections.emptyList());
        }

        return courseDto;
    }

    public static List<CourseDto> convertTheCourseListToCourseDtoList(List<CourseEntity> courseEntityList){
        return courseEntityList.stream().map(DtoConverter::convertTheCourseToCourseDto).collect(Collectors.toList());
    }

    public static PurchaseDto convertThePurchaseToPurchaseDto(PurchaseEntity purchaseEntity){
        PurchaseDto purchaseDto=new PurchaseDto();
        purchaseDto.setId(purchaseEntity.getId());
        purchaseDto.setAmount(purchaseEntity.getAmount());
        purchaseDto.setStatus(purchaseEntity.getStatus());
        purchaseDto.setUser(purchaseEntity.getUser());
        purchaseDto.setId(purchaseEntity.getId());
        return purchaseDto;

    }
    public static CourseProgressDto convertTheCourseProgressToCourseProgressDto(CourseProgressEntity courseProgressEntity){
        CourseProgressDto courseProgressDto=new CourseProgressDto();

        courseProgressDto.setId(courseProgressEntity.getId());
        courseProgressDto.setCompleted(courseProgressEntity.getCompleted());
        return courseProgressDto;
    }

}
