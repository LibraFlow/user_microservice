package backend2.presentation;

import backend2.domain.SubscriptionDTO;
import backend2.domain.SubscriptionType;
import backend2.business.usecase.subscription.AddSubscriptionUseCase;
import backend2.business.usecase.subscription.GetUserSubscriptionsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final AddSubscriptionUseCase addSubscriptionUseCase;
    private final GetUserSubscriptionsUseCase getUserSubscriptionsUseCase;

    @PostMapping("/{userId}/subscriptions")
    public ResponseEntity<SubscriptionDTO> addSubscription(
            @PathVariable Integer userId,
            @RequestParam SubscriptionType type) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Integer authUserId = ((Number) jwt.getClaim("userId")).intValue();
        if (!authUserId.equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(addSubscriptionUseCase.addSubscription(userId, type));
    }

    @GetMapping("/{userId}/subscriptions")
    public ResponseEntity<List<SubscriptionDTO>> getUserSubscriptions(@PathVariable Integer userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Integer authUserId = ((Number) jwt.getClaim("userId")).intValue();
        if (!authUserId.equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(getUserSubscriptionsUseCase.getUserSubscriptions(userId));
    }

    @GetMapping("/{userId}/subscriptions/active")
    public ResponseEntity<List<SubscriptionDTO>> getActiveUserSubscriptions(@PathVariable Integer userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        Integer authUserId = ((Number) jwt.getClaim("userId")).intValue();
        if (!authUserId.equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(getUserSubscriptionsUseCase.getActiveUserSubscriptions(userId));
    }
} 