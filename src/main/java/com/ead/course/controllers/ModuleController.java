package com.ead.course.controllers;

import com.ead.course.dtos.CourseDto;
import com.ead.course.dtos.ModuleDto;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.ModuleService;
import com.ead.course.specifications.SpecificationTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping
@CrossOrigin(origins = "*", maxAge = 3600)
public class ModuleController {

    @Autowired
    private ModuleService moduleService;
    @Autowired
    private CourseService courseService;

    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR')")
    @PostMapping("/courses/{courseId}/modules")
    public ResponseEntity<?> saveModule(@PathVariable(value = "courseId") UUID courseId,
                                        @RequestBody @Valid ModuleDto moduleDto){

        Optional<CourseModel> courseModelOptional = this.courseService.findById(courseId);
        //TODO após implementar o exceptionhandler fazer o ajusta orElseThrow do Optional
        if(!courseModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }

        var moduleModel = new ModuleModel();
        BeanUtils.copyProperties(moduleDto, moduleModel);
        moduleModel.setCourse(courseModelOptional.get());
        moduleModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));//Verificar como deixar como UTC padrão da aplicação
        moduleModel = this.moduleService.save(moduleModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(moduleModel);
    }

    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR')")
    @DeleteMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<?> deleteModule(@PathVariable(value = "courseId") UUID courseId,
                                          @PathVariable(value = "moduleId") UUID moduleId){

        Optional<ModuleModel> moduleModelOptional = this.moduleService.findModuleIntoCourse(courseId, moduleId);
        //TODO após implementar o exceptionhandler fazer o ajusta orElseThrow do Optional
        if(!moduleModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course or module not found");
        }

        this.moduleService.delete(moduleModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Module deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR')")
    @PutMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<?> updateModule(@PathVariable(value = "courseId") UUID courseId,
                                          @PathVariable(value = "moduleId") UUID moduleId,
                                          @RequestBody @Valid ModuleDto moduleDto){
        Optional<ModuleModel> moduleModelOptional = this.moduleService.findModuleIntoCourse(courseId, moduleId);
        //TODO após implementar o exceptionhandler fazer o ajusta orElseThrow do Optional no próprio service
        if(!moduleModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }
        var moduleModel = moduleModelOptional.get();
        moduleModel.setTitle(moduleDto.getTitle());
        moduleModel.setDescription(moduleDto.getDescription());
        moduleModel = this.moduleService.save(moduleModel);
        return ResponseEntity.status(HttpStatus.OK).body(moduleModel);
    }

    @PreAuthorize("hasAnyRole('ROLE_STUDENT')")
    @GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<Page<ModuleModel>> getAllModules(@PathVariable(value = "courseId") UUID courseId,
                                                           SpecificationTemplate.ModuleSpec spec,
                                                           @PageableDefault(page = 0, size = 10, sort = "moduleId",
                                                                   direction = Sort.Direction.ASC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(this.moduleService
                .findAllByCourse(SpecificationTemplate.moduleCourseId(courseId).and(spec), pageable));
    }

    @PreAuthorize("hasAnyRole('ROLE_STUDENT')")
    @GetMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<?> getOneModule(@PathVariable(value = "courseId") UUID courseId,
                                          @PathVariable(value = "moduleId") UUID moduleId){
        Optional<ModuleModel> moduleModelOptional = this.moduleService.findModuleIntoCourse(courseId, moduleId);
        //TODO após implementar o exceptionhandler fazer o ajusta orElseThrow do Optional no próprio service
        if(!moduleModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found for this course");
        }

        return ResponseEntity.status(HttpStatus.OK).body(moduleModelOptional.get());
    }
}
