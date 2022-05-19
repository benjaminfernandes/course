package com.ead.course.validation;

import com.ead.course.dtos.CourseDto;
import com.ead.course.enums.UserType;
import com.ead.course.models.UserModel;
import com.ead.course.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;
import java.util.UUID;

@Component
public class CourseValidator implements Validator {

    @Autowired
    @Qualifier("defaultValidator")
    private Validator validator;
    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        CourseDto courseDto = (CourseDto) target;
        validator.validate(courseDto, errors);

        if(!errors.hasErrors()){
            validateUserInstructor(courseDto.getUserInstructor(), errors);
        }
    }

    private void validateUserInstructor(UUID userInstructor, Errors errors){
        Optional<UserModel> userModelOptional = this.userService.findById(userInstructor);

        if(userModelOptional.isEmpty()){
            errors.rejectValue("userInstructor", "UserInstructorError",
                    "Instructor not found.");
        }

        if(verifyTypeUser(userModelOptional.get())){
            errors.rejectValue("userInstructor", "UserInstructorError",
                    "User must be INSTRUCTOR or ADMIN");
        }
    }

    private boolean verifyTypeUser(UserModel userModel){
        if(userModel.getUserType().equals(UserType.STUDENT.toString()))
            return true;
        else
            return false;
    }
}
