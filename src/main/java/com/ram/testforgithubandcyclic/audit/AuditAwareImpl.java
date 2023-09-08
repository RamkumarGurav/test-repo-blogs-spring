package com.ram.testforgithubandcyclic.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditAwareImpl")
public class AuditAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        String uname = "system";
        if (authentication != null) {
            uname = authentication
                    .getName();
        }
        return Optional.of(uname);

//        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getName());
    }

}