package com.ead.course.services.impl;

import com.ead.course.repositories.LessonRepository;
import com.ead.course.services.LessonService;
import org.springframework.beans.factory.annotation.Autowired;

public class LessonServiceImpl implements LessonService {

    @Autowired
    private LessonRepository lessonRepository;

}
