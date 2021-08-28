package com.eattogether.domain;

import com.eattogether.dto.EventType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter @EqualsAndHashCode(of="id")
public class Event {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Study study;

    @ManyToOne
    private Account createdBy;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column
    private Integer limitOfEnrollments; // int로 할시에 null로 초기화 불가능

    @OneToMany(mappedBy = "event")
    private List<Enrollment> enrollments; // 하나의 이벤트에 여러개의 신청서

    @Enumerated(EnumType.STRING)
    private EventType eventType; // 이벤트 타입은 2개
}