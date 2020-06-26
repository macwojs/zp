package edu.agh.zp.services;
import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.objects.Role;
import edu.agh.zp.repositories.CitizenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class citizenDetailsService implements UserDetailsService {
    @Autowired
    CitizenRepository cR;

//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        Optional<CitizenEntity> citizen =  cR.findByEmail(email);
//        citizen.orElseThrow(() -> new UsernameNotFoundException("Not found: " + email));
//        return new org.springframework.security.core.userdetails.User(citizen.get().getEmail(), citizen.get().getPassword(),
//                getAuthorities(citizen.get()));
////        return citizen.map(CitizenDetails::new).get();
//    }
//    private static Collection<? extends GrantedAuthority> getAuthorities(CitizenEntity user) {
//        String userRoles = user.getRole().name();
//        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(userRoles);
//        return authorities;
//    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        CitizenEntity user = cR.findByEmail(userName)
                .orElseThrow(() -> new UsernameNotFoundException("Email " + userName + " not found"));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                getAuthorities(user));
    }
    private static Collection<? extends GrantedAuthority> getAuthorities(CitizenEntity user) {
        String[] userRoles = user.getRoles().stream().map( Role::getName ).toArray(String[]::new);
        return AuthorityUtils.createAuthorityList(userRoles);
    }



}
