package com.jpaproject.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class StudyForm {

    @Length(min=5,max=30)
    @Pattern(regexp="^[a-zA-Z0-9_-]{5,20}$") // 사용가능한 패턴 종류
    private String url;

    @NotBlank
    private String title;

    @NotBlank
    @Length(max=50)
    private String short_note;

    private String long_note;

}
