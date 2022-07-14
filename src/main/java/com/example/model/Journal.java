package com.example.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Journal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne( cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_student_course", referencedColumnName = "id")
    private CourseStudent courseStudent;

    @Column(name = "mark_points", nullable = false)
    private int markPoints;

    @Column(name = "mark_code", nullable = false)
    private String markCode;

    @Column(name = "mark_explanation", nullable = false)
    private String markExplanation;
}
