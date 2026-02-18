
package com.methaltech.application.data.bgtool.service;
import com.methaltech.application.data.bgtool.repository.SystemIconRepository;
import com.methaltech.application.data.entity.bgtool.SystemIcon;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SystemIconService {

    private final SystemIconRepository repo;

    public SystemIconService(SystemIconRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public SystemIcon saveOrUpdate(String iconKey,
                                   String fileName,
                                   String contentType,
                                   byte[] data) {

        if (iconKey == null || iconKey.isBlank()) {
            throw new IllegalArgumentException("iconKey is required");
        }
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Icon data is empty");
        }
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("contentType is required");
        }

        SystemIcon icon = repo.findByIconKey(iconKey.trim())
                .orElseGet(SystemIcon::new);

        icon.setIconKey(iconKey.trim());
        icon.setFileName(fileName == null ? "icon" : fileName);
        icon.setContentType(contentType);
        icon.setSizeBytes(data.length);
        icon.setData(data);
        icon.setUploadedAt(LocalDateTime.now());

        return repo.save(icon);
    }

    @Transactional(readOnly = true)
    public SystemIcon getRequired(String iconKey) {
        return repo.findByIconKey(iconKey)
                .orElseThrow(() -> new IllegalArgumentException("Icon not found: " + iconKey));
    }
}

