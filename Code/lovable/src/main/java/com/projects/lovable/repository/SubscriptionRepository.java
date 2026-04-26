package com.projects.lovable.repository;

import com.projects.lovable.entity.Subscription;
import com.projects.lovable.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    //Get the current active subscription
    Optional<Subscription> findByUserIdAndStatusIn(Long userId, Set<SubscriptionStatus> statusSet);

    boolean existsByStripeSubscription(String subscriptionId);

    Optional<Subscription> findByStripeSubscription(String gatewaySubscriptionId);
}
