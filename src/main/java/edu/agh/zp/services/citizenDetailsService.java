package edu.agh.zp.services;
import edu.agh.zp.CitizenDetails;
import edu.agh.zp.objects.CitizenEntity;
import edu.agh.zp.repositories.CitizenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class citizenDetailsService implements UserDetailsService {
    @Autowired
    CitizenRepository cR;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<CitizenEntity> citizen =  cR.findByEmail(email);
        citizen.orElseThrow(() -> new UsernameNotFoundException("Not found: " + email));

        return citizen.map(CitizenDetails::new).get();
    }

}
