package backend2.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    public static final String SUBSCRIPTION_CHECK_REQUESTED_TOPIC = "subscription.check.requested";
    public static final String SUBSCRIPTION_CHECK_RESULT_TOPIC = "subscription.check.result";

    @Bean
    public NewTopic subscriptionCheckRequestedTopic() {
        return new NewTopic(SUBSCRIPTION_CHECK_REQUESTED_TOPIC, 1, (short) 1);
    }

    @Bean
    public NewTopic subscriptionCheckResultTopic() {
        return new NewTopic(SUBSCRIPTION_CHECK_RESULT_TOPIC, 1, (short) 1);
    }
} 