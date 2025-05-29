package backend2.security;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class PasswordValidationService {
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 64;
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");

    public List<String> validatePassword(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.trim().isEmpty()) {
            errors.add("Password cannot be empty");
            return errors;
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            errors.add("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }

        if (password.length() > MAX_PASSWORD_LENGTH) {
            errors.add("Password cannot exceed " + MAX_PASSWORD_LENGTH + " characters");
        }

        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one uppercase letter");
        }

        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one lowercase letter");
        }

        if (!NUMBER_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one number");
        }

        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one special character (!@#$%^&*(),.?\":{}|<>)");
        }

        return errors;
    }
} 