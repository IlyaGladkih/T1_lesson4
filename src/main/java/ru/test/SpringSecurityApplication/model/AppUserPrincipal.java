package ru.test.SpringSecurityApplication.model;

import lombok.*;

import java.security.Principal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AppUserPrincipal implements Principal {

    private String id;

    private String name;

    private List<String> role;

    @Override
    public String getName() {
        return name;
    }
}
