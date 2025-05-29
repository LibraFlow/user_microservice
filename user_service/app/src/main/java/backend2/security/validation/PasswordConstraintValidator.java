package backend2.security.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import backend2.security.PasswordValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Autowired
    private PasswordValidationService passwordValidationService;

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        List<String> errors = passwordValidationService.validatePassword(password);
        
        if (!errors.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.join(", ", errors))
                   .addConstraintViolation();
            return false;
        }

        return true;
    }
} 