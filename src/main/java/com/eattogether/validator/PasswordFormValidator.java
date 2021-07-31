package com.eattogether.validator;

import com.eattogether.dto.PasswordForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PasswordFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        PasswordForm passwordForm = (PasswordForm) target;

        if(!passwordForm.getNewPassword().equals(passwordForm.getNewPasswordRepeat())){
            errors.rejectValue("newPassword","invalid newPassword",
                    "패스워드가 일치하지 않습니다.");
        }

    }
}
