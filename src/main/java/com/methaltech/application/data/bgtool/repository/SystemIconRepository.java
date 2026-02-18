
package com.methaltech.application.data.bgtool.repository;
import com.methaltech.application.data.entity.bgtool.SystemIcon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemIconRepository extends JpaRepository<SystemIcon, Long> {
    Optional<SystemIcon> findByIconKey(String iconKey);
    boolean existsByIconKey(String iconKey);
}

