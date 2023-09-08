package com.ram.testforgithubandcyclic.utilities;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ProfileComponent {

    private final Environment environment;

    public ProfileComponent(Environment environment) {
        this.environment = environment;
    }

    public String[] getActiveProfiles() {
        return environment.getActiveProfiles();
    }
}
