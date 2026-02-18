
package com.methaltech.application.data.entity.bgtool;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "system_icon",
    indexes = {
        @Index(name = "ix_system_icon_key", columnList = "icon_key", unique = true)
    }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SystemIcon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** A stable key e.g. "URC_LOGO", "PDF_HEADER_LOGO", "APP_FAVICON" */
    @Column(name = "icon_key", nullable = false, unique = true, length = 80)
    private String iconKey;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "content_type", nullable = false, length = 120)
    private String contentType; // image/png, image/jpeg, image/svg+xml

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    /** Store the icon binary in DB */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "data", nullable = false)
    private byte[] data;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    void prePersist() {
        if (uploadedAt == null) uploadedAt = LocalDateTime.now();
    }
}

