package com.ead.course.controllers;

import com.ead.course.dtos.LessonDto;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.LessonService;
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
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @Autowired
    private ModuleService moduleService;

    @PostMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<?> saveLesson(@PathVariable(value = "moduleId") UUID moduleId,
                                        @RequestBody @Valid LessonDto lessonDto){

        Optional<ModuleModel> moduleModelOptional = this.moduleService.findById(moduleId);
        //TODO após implementar o exceptionhandler fazer o ajusta orElseThrow do Optional
        if(!moduleModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found");
        }

        var lessonModel = new LessonModel();
        BeanUtils.copyProperties(lessonDto, lessonModel);
        lessonModel.setModule(moduleModelOptional.get());
        lessonModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));//Verificar como deixar como UTC padrão da aplicação
        lessonModel = this.lessonService.save(lessonModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(lessonModel);
    }

    @DeleteMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<?> deleteLesson(@PathVariable(value = "moduleId") UUID moduleId,
                                          @PathVariable(value = "lessonId") UUID lessonId){

        Optional<LessonModel> lessonModelOptional = this.lessonService.findLessonIntoModule(moduleId, lessonId);
        //TODO após implementar o exceptionhandler fazer o ajusta orElseThrow do Optional
        if(!lessonModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found in this module.");
        }

        this.lessonService.delete(lessonModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Module deleted successfully");
    }

    @PutMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<?> updateLesson(@PathVariable(value = "moduleId") UUID moduleId,
                                          @PathVariable(value = "lessonId") UUID lessonId,
                                          @RequestBody @Valid LessonDto lessonDto){
        Optional<LessonModel> lessonModelOptional = this.lessonService.findLessonIntoModule(moduleId, lessonId);
        //TODO após implementar o exceptionhandler fazer o ajusta orElseThrow do Optional no próprio service
        if(!lessonModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found in this module.");
        }
        var lessonModel = lessonModelOptional.get();
        lessonModel.setTitle(lessonDto.getTitle());
        lessonModel.setDescription(lessonDto.getDescription());
        lessonModel.setVideoUrl(lessonDto.getVideoUrl());
        lessonModel = this.lessonService.save(lessonModel);
        return ResponseEntity.status(HttpStatus.OK).body(lessonModel);
    }

    @GetMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<Page<LessonModel>> getAllLesson(@PathVariable(value = "moduleId") UUID moduleId,
                                                          SpecificationTemplate.LessonSpec spec,
                                                          @PageableDefault(page = 0, size = 10, sort = "lessonId",
                                                                  direction = Sort.Direction.ASC) Pageable pageable){

        return ResponseEntity.status(HttpStatus.OK).body(this.lessonService
                .findAllByModule(SpecificationTemplate.lessonModuleId2(moduleId).and(spec), pageable));
    }

    @GetMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<?> getOneLesson(@PathVariable(value = "moduleId") UUID moduleId,
                                          @PathVariable(value = "lessonId") UUID lessonId){
        Optional<LessonModel> lessonModelOptional = this.lessonService.findLessonIntoModule(moduleId,lessonId);
        //TODO após implementar o exceptionhandler fazer o ajusta orElseThrow do Optional no próprio service
        if(!lessonModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this module");
        }

        return ResponseEntity.status(HttpStatus.OK).body(lessonModelOptional.get());
    }
}
