package edu.agh.zp.repositories;

import edu.agh.zp.objects.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository  extends JpaRepository<Role, Long> {
    Optional<Role> findById(long roleid);
    Optional<Role> findByName(String name);
}
