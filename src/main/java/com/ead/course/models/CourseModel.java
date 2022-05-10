package com.ead.course.models;

import com.ead.course.enums.CourseLevel;
import com.ead.course.enums.CourseStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "TB_COURSES")
public class CourseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID courseId;
    @Column(nullable = false, length = 150)
    private String name;
    @Column(nullable = false, length = 250)
    private String description;
    @Column
    private String imageUrl;
    @CreationTimestamp
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime creationDate;
    @UpdateTimestamp
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime lastUpdateDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus courseStatus;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseLevel courseLevel;
    @Column(nullable = false)
    private UUID userInstructor;
    //Neste caso o JsonProperty será usado somente o set
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) //o jackson deserealiza apenas o campo se persistido, nas consultas é ignorado
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    @Fetch(FetchMode.SUBSELECT) // faz uma consulta separada para trazer os módulos
    //@OnDelete(action = OnDeleteAction.CASCADE) //DELEGA PARA O BD EXCLUIR OS FILHOS - MELHOR DESEMPENHO DO QUE O ORPHALREMOVE, PORÉM NÃO TEMOS CONTROLE DO QUE ESTÁ EXCLUINDO SE CASO DÊ ERRO
    private Set<ModuleModel> modules;


}
