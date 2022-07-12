package com.example.dto;

import com.example.model.CourseStudent;
import com.example.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class CourseDTO {

    private String name;

    private String theme;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String courseStatus;

    private Integer lecturerId;
}
