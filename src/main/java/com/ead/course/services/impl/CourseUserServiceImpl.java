package com.ead.course.services.impl;

import com.ead.course.clients.AuthUserClient;
import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;
import com.ead.course.repositories.CourseUserRepository;
import com.ead.course.services.CourseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
public class CourseUserServiceImpl implements CourseUserService {

    @Autowired
    private CourseUserRepository courseUserRepository;
    @Autowired
    private AuthUserClient authUserClient;

    @Override
    public boolean existsByCourseAndUserId(CourseModel courseModel, UUID userId) {
        return this.courseUserRepository.existsByCourseAndUserId(courseModel, userId);
    }

    @Override
    public CourseUserModel save(CourseUserModel courseUserModel) {
        return this.courseUserRepository.save(courseUserModel);
    }

    @Transactional
    @Override
    public CourseUserModel saveAndSendSubscriptionUserInCourse(CourseUserModel courseUserModel) {
        courseUserModel = save(courseUserModel);

        authUserClient.postSubscriptionUserInCourse(courseUserModel.getCourse().getCourseId(),
                courseUserModel.getUserId());

        return courseUserModel;
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return this.courseUserRepository.existsByUserId(userId);
    }

    @Transactional
    @Override
    public void deleteCourseUserByUser(UUID userId) {
        this.courseUserRepository.deleteAllByUserId(userId);
    }
}
