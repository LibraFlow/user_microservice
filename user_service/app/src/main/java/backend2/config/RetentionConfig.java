package backend2.config;

import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class RetentionConfig {
    // Retention periods in days
    public static final int DELETED_USER_RETENTION_DAYS = 1095; // 3 years
    public static final int SUBSCRIPTION_HISTORY_RETENTION_DAYS = 365; // 1 year
} 