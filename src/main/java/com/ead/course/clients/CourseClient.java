package com.ead.course.clients;

import com.ead.course.dtos.ResponsePageDto;
import com.ead.course.dtos.UserDto;
import com.ead.course.services.UtilsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Log4j2
@Component
public class CourseClient {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UtilsService utilsService;

    //Utilizando API Composition Pattern
    public Page<UserDto> getAllUsersByCourse(UUID courseId, Pageable pageable){
        //List<CourseDto> searchResult = null;
        ResponseEntity<ResponsePageDto<UserDto>> result = null;
        String url = this.utilsService.createUrl(courseId, pageable);

        log.debug("Request URL: {}", url);
        log.info("Request URL: {}", url);
        try{
            ParameterizedTypeReference<ResponsePageDto<UserDto>> responseType =
                    new ParameterizedTypeReference<ResponsePageDto<UserDto>>() {};

            result = this.restTemplate.exchange(url, HttpMethod.GET,null, responseType);
            //searchResult = result.getBody().getContent();
            log.debug("Response Number of Elements: {}", result.getBody().getTotalElements());
        }catch (HttpStatusCodeException e){
            log.error("Error request /courses {} ", e);
        }
        log.info("Ending request /users courseID {}", courseId);
        return result.getBody();
    }

}
