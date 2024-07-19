package com.methaltech.application.data.livedata.repository;

import com.methaltech.application.data.entity.livedata.UrcAnlCode;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UrcAnlCodeRepository extends JpaRepository<UrcAnlCode, String> {

    //List<UrcAnlCode> findByAnlCatIdAndAnlCodeStartingWith(String anlCatId, String anlCode);
    // Custom query method using @Query
    @Query("SELECT u FROM UrcAnlCode u WHERE u.id.anlCatId = :anlCatId AND u.id.anlCode LIKE :prefix%")
    List<UrcAnlCode> findByAnlCatIdAndAnlCodeStartingWith(@Param("anlCatId") String anlCatId, @Param("prefix") String prefix);


}
