
package com.methaltech.application.data.entity.livedata;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "URC_ANL_CODE")
@NoArgsConstructor
public @Data class UrcAnlCode {

    @EmbeddedId
    private UrcAnlCodeId id;

    @Column(name = "UPDATE_COUNT")
    private Long updateCount;

    @Column(name = "LAST_CHANGE_USER_ID")
    private String lastChangeUserId;

    @Column(name = "LAST_CHANGE_DATETIME")
    private Date lastChangeDatetime;

    @Column(name = "LOOKUP")
    private String lookup;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DAG_CODE")
    private String dagCode;

    @Column(name = "BDGT_CHECK")
    private Integer bdgtCheck = 0; // Default value

    @Column(name = "BDGT_STOP")
    private Integer bdgtStop = 0; // Default value

    @Column(name = "PROHIBIT_POSTING")
    private Integer prohibitPosting = 0; // Default value

    @Column(name = "NAVIGATION_OPTION")
    private Integer navigationOption = 99; // Default value

    @Column(name = "COMBINED_BDGT_CHCK")
    private Integer combinedBdgtCheck = 0; // Default value

    // Constructors, getters, and setters

    // Rest of the entity class

    // Primary key class
    @Embeddable
    public static @Data class UrcAnlCodeId implements Serializable {

        @Column(name = "ANL_CAT_ID")
        private String anlCatId;

        @Column(name = "ANL_CODE")
        private String anlCode;

        // Constructors, getters, and setters

        // Equals and hashCode methods
    }
}
