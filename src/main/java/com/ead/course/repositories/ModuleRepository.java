package com.ead.course.repositories;

import com.ead.course.models.ModuleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ModuleRepository extends JpaRepository<ModuleModel, UUID>, JpaSpecificationExecutor<ModuleModel> {

    @Query(value = "SELECT * FROM TB_MODULES WHERE COURSE_ID = :courseId", nativeQuery = true)
    public List<ModuleModel> findAllModulesIntoCourse(@Param("courseId") UUID courseId);
    @Query(value = "SELECT * FROM TB_MODULES WHERE COURSE_ID = :courseId AND MODULE_ID = :moduleId",
            nativeQuery = true)
    Optional<ModuleModel> findModuleIntoCourse(@Param("courseId") UUID courseId, @Param("moduleId") UUID moduleId);

    //@Modifying usado se quiser utilizar o @query para atualizar ou deletar algum registro
}
