package com.ead.course.controllers;

import com.ead.course.dtos.SubscriptionDto;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.UserService;
import com.ead.course.specifications.SpecificationTemplate;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.status;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseUserController {

    @Autowired
    private CourseService courseService;
    @Autowired
    private UserService userService;

    @GetMapping("/courses/{courseId}/users")
    public ResponseEntity<?> getAllUsersByCourse(SpecificationTemplate.UserSpec spec,
            @PageableDefault(page = 0, size = 10, sort = "userId",
                    direction = Sort.Direction.ASC) Pageable pageable, @PathVariable(value = "courseId") UUID courseId){
        Optional<CourseModel> courseModelOptional = this.courseService.findById(courseId);
        if(courseModelOptional.isEmpty()){
            return status(HttpStatus.NOT_FOUND).body("Course not found");
        }
        return status(HttpStatus.OK).body(this.userService.findAll(SpecificationTemplate.userCourseId(courseId).and(spec), pageable));
    }

    @PostMapping("/courses/{courseId}/users/subscription")
    public ResponseEntity<?> saveSubscriptionUserInCourse(@PathVariable(value = "courseId") UUID courseId,
                                                          @RequestBody @Valid SubscriptionDto subscriptionDto){
        Optional<CourseModel> courseModelOptional = this.courseService.findById(courseId);
        if(courseModelOptional.isEmpty()){
            return status(HttpStatus.NOT_FOUND).body("Course not found");
        }
        //verificações utilizando state transfer
        return status(HttpStatus.CREATED).body("");
    }
}
