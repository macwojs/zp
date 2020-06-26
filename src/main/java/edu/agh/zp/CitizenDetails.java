package edu.agh.zp;


import edu.agh.zp.objects.CitizenEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CitizenDetails  implements UserDetails {
    private String email;
    private String password;
    private boolean active;
    private String role;
    private List<GrantedAuthority> authorities;

    public CitizenDetails(CitizenEntity citizen) {
        this.email = citizen.getEmail();
        this.password = citizen.getPassword();
//        this.role = citizen.getRoles().toString();
//         this.active = citizen.isActive()
//        this.authorities = Arrays.stream(citizen.getRoles().split(",")).map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList())
    }

    public CitizenDetails() { }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<>( );

        list.add(new SimpleGrantedAuthority("ROLE_"+role));

        return list;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
