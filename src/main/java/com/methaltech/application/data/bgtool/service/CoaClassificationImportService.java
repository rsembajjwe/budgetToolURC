package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.Classification1;
import com.methaltech.application.data.Classification2;
import com.methaltech.application.data.Classification3;
import com.methaltech.application.data.CoaEnumMapper;
import com.methaltech.application.data.bgtool.repository.CoaRepository;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.dto.CoaClassificationRow;
import com.methaltech.application.views.budget.CoaExcelParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CoaClassificationImportService {

    private final CoaRepository coaRepository;

    public CoaClassificationImportService(CoaRepository coaRepository) {
        this.coaRepository = coaRepository;
    }

    public record ImportResult(
            int rowsRead,
            int updated,
            int missingCodes,
            int failedRows,
            List<String> messages) {

    }

    @Transactional
    public ImportResult importAndUpdate(InputStream excelStream) throws Exception {

        List<CoaClassificationRow> rows = CoaExcelParser.parse(excelStream);
        if (rows == null || rows.isEmpty()) {
            return new ImportResult(0, 0, 0, 0, List.of("No rows found in Excel."));
        }

        // Load all COA records for the codes in the Excel (single DB hit)
        Set<String> codes = rows.stream()
                .map(CoaClassificationRow::code)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());

        if (codes.isEmpty()) {
            return new ImportResult(rows.size(), 0, 0, 0, List.of("Excel had no valid COA codes."));
        }

        List<COA> existing = coaRepository.findByCodeIn(codes);

        // OPTION C: group by code -> update ALL duplicates
        Map<String, List<COA>> coaByCode = existing.stream()
                .filter(c -> c.getCode() != null && !c.getCode().trim().isBlank())
                .collect(Collectors.groupingBy(c -> c.getCode().trim()));

        int updatedRows = 0;     // counts actual COA rows updated (includes duplicates)
        int missingCodes = 0;    // Excel codes not found in DB
        int failedRows = 0;      // Excel rows that failed due to enum mismatch etc.

        List<String> messages = new ArrayList<>();

        // (Optional) log duplicates found in DB
        coaByCode.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .forEach(e -> messages.add("DB DUPLICATE: code=" + e.getKey() + " count=" + e.getValue().size()));

        for (CoaClassificationRow r : rows) {

            if (r.code() == null || r.code().trim().isBlank()) {
                failedRows++;
                messages.add("FAILED: Blank code for row name=" + r.name());
                continue;
            }

            String code = r.code().trim();
            List<COA> targets = coaByCode.get(code);

            if (targets == null || targets.isEmpty()) {
                missingCodes++;
                messages.add("MISSING COA code: " + code + (r.name() != null && !r.name().isBlank() ? " (" + r.name() + ")" : ""));
                continue;
            }

            try {
                // Empty cells -> normalize() returns null -> parseEnumOrNull returns null (so null is stored)
                Classification1 c1 = CoaEnumMapper.parseEnumOrNull(Classification1.class, r.class1());
                Classification2 c2 = CoaEnumMapper.parseEnumOrNull(Classification2.class, r.class2());
                Classification3 c3 = CoaEnumMapper.parseEnumOrNull(Classification3.class, r.class3());

                for (COA coa : targets) {
                    coa.setClass1(c1);
                    coa.setClass2(c2);
                    coa.setClass3(c3);
                }

                updatedRows += targets.size();

                if (targets.size() > 1) {
                    messages.add("UPDATED DUPLICATE CODE: " + code + " updatedRows=" + targets.size());
                }

            } catch (IllegalArgumentException ex) {
                failedRows++;
                messages.add("FAILED " + code
                        + " | class1='" + r.class1()
                        + "', class2='" + r.class2()
                        + "', class3='" + r.class3()
                        + "' | " + ex.getMessage());
            } catch (Exception ex) {
                failedRows++;
                messages.add("FAILED " + code + " | " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            }
        }

        // Persist updates (existing contains all entities we modified; JPA dirty checking will handle it)
        coaRepository.saveAll(existing);

        // Summary on top
        messages.add(0, "DONE: excelRows=" + rows.size()
                + ", updatedRows=" + updatedRows
                + ", missingCodes=" + missingCodes
                + ", failedExcelRows=" + failedRows);

        return new ImportResult(rows.size(), updatedRows, missingCodes, failedRows, messages);
    }

}
