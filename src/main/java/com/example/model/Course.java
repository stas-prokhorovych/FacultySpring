package com.example.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotEmpty(message = "name can't be empty")
    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @NotEmpty(message = "theme can't be empty")
    @Column(name = "theme", nullable = false)
    private String theme;

    @Column(name = "start_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;

    @Column(name = "course_status", nullable = false)
    private String courseStatus;

    @ManyToOne
    @JoinColumn(name="id_lecturer")
    private User idLecturer;

    @OneToMany(mappedBy = "course")
    private Set<CourseStudent> courseStudent = new HashSet<>();
}
