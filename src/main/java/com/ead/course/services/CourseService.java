package com.ead.course.services;

import com.ead.course.models.CourseModel;
import org.springframework.stereotype.Service;

@Service
public interface CourseService {

    void delete(CourseModel courseModel);
}
