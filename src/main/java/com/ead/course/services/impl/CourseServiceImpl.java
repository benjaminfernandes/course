package com.ead.course.services.impl;

import com.ead.course.clients.AuthUserClient;
import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.repositories.CourseRepository;
import com.ead.course.repositories.CourseUserRepository;
import com.ead.course.repositories.LessonRepository;
import com.ead.course.repositories.ModuleRepository;
import com.ead.course.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private CourseUserRepository courseUserRepository;
    @Autowired
    private AuthUserClient authUserClient;

    @Transactional
    @Override
    public void delete(CourseModel courseModel) {
        boolean deleteCourseUserInAuthUser = false;
        List<ModuleModel> moduleModelList = this.moduleRepository.findAllModulesIntoCourse(courseModel.getCourseId());

        if(!moduleModelList.isEmpty()){
            /*for(ModuleModel module : moduleModelList){
               List<LessonModel> lessonModelList = this.lessonRepository.findAllLessonsIntoModule(module.getModuleId());
               if(!lessonModelList.isEmpty()){
                   this.lessonRepository.deleteAll(lessonModelList);
               }
            }*/
            //TODO VERIFICAR SE FUNCIONA E SE NÃO FAZ 2 SELECTS NESTA ABORDAGEM
            moduleModelList.stream().filter(module -> !findAllLessonsIntoModule(module.getModuleId()).isEmpty())
                    .forEach(module -> {
                    this.lessonRepository.deleteAll(findAllLessonsIntoModule(module.getModuleId()));
            });
            this.moduleRepository.deleteAll(moduleModelList);
        }

        List<CourseUserModel> courseUserModelList = this.courseUserRepository
                .findAllCourseUserIntoCourse(courseModel.getCourseId());
        if(!courseUserModelList.isEmpty()) {
            this.courseUserRepository.deleteAll(courseUserModelList);
            deleteCourseUserInAuthUser = true;
        }
        this.courseRepository.delete(courseModel);

        if(deleteCourseUserInAuthUser){
            this.authUserClient.deleteCourseInAuthUser(courseModel.getCourseId());
        }

    }

    @Override
    public CourseModel save(CourseModel courseModel) {
        return this.courseRepository.save(courseModel);
    }

    @Override
    public Optional<CourseModel> findById(UUID courseId) {
        return this.courseRepository.findById(courseId);
    }

    @Override
    public Page<CourseModel> findAll(Specification<CourseModel> spec, Pageable pageable) {
        return this.courseRepository.findAll(spec, pageable);
    }

    private List<LessonModel> findAllLessonsIntoModule(UUID moduleId){
        return this.lessonRepository.findAllLessonsIntoModule(moduleId);
    }
}
