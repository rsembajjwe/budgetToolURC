package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.RequisitionDataRepository;
import com.methaltech.application.data.bgtool.repository.RequisitionItemRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.RequisitionData;
import com.methaltech.application.data.entity.bgtool.RequisitionData.RequisitionStatus;
import com.methaltech.application.data.entity.bgtool.RequisitionItem;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.livedata.repository.SALFLDGRepository;
import com.methaltech.application.data.livedata.repository.UrcCSalFldgRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;

@Service
@Transactional
public class RequisitionDataService {

    @Autowired
    private RequisitionDataRepository requisitionDataRepository;

    @Autowired
    private RequisitionItemRepository requisitionItemRepository;
    @Autowired
    private SALFLDGRepository salfldgRepository;
    @Autowired
    private UrcCSalFldgRepository urcCSalFldgRepository;

    public RequisitionData createRequisition(RequisitionData requisitionData) {
        // Generate unique requisition number if not set
        if (requisitionData.getRequisitionNumber() == null || requisitionData.getRequisitionNumber().isEmpty()) {
            String requisitionNumber = generateRequisitionNumber(requisitionData.getRequisitionType());
            requisitionData.setRequisitionNumber(requisitionNumber);
        }

        // Set initial status
        if (requisitionData.getStatus() == null) {
            requisitionData.setStatus(RequisitionData.RequisitionStatus.DRAFT);
        }

        // Save requisition first
        RequisitionData savedRequisition = requisitionDataRepository.save(requisitionData);

        // Save items if any
        if (requisitionData.getItems() != null && !requisitionData.getItems().isEmpty()) {
            for (int i = 0; i < requisitionData.getItems().size(); i++) {
                RequisitionItem item = requisitionData.getItems().get(i);
                item.setRequisitionData(savedRequisition);
                item.setItemNumber(i + 1);
                item.calculateTotalCost();
                requisitionItemRepository.save(item);
            }

            // Update total amount
            savedRequisition.updateTotalAmount();
            savedRequisition = requisitionDataRepository.save(savedRequisition);
        }

        return savedRequisition;
    }

    public RequisitionData updateRequisition(RequisitionData requisitionData) {
        return requisitionDataRepository.save(requisitionData);
    }

    /**
     * Find a requisition by ID and fetch its items eagerly.
     *
     * @param id the requisition ID
     * @return the requisition with items
     * @throws EntityNotFoundException if not found
     */
    public RequisitionData getRequisitionWithItems(Long id) {
        return requisitionDataRepository.findByIdWithItems(id)
                .orElseThrow(() -> new EntityNotFoundException(
                "Requisition with ID " + id + " not found"));
    }

    public RequisitionItem updateRequisitionItem(RequisitionItem requisitionItem) {
        return requisitionItemRepository.save(requisitionItem);
    }

    public RequisitionData submitRequisition(Long requisitionId, User submittedBy) {
        Optional<RequisitionData> optionalRequisition = requisitionDataRepository.findById(requisitionId);
        if (!optionalRequisition.isPresent()) {
            throw new RuntimeException("Requisition not found");
        }

        RequisitionData requisition = optionalRequisition.get();

        if (!requisition.canBeSubmitted()) {
            throw new RuntimeException("Requisition cannot be submitted in current state");
        }

        requisition.setStatus(RequisitionData.RequisitionStatus.SUBMITTED);
        requisition.setSubmittedBy(submittedBy);
        requisition.setSubmittedDate(LocalDateTime.now());
        requisition.setLastUpdatedBy(submittedBy);

        return requisitionDataRepository.save(requisition);
    }

    public RequisitionData approveRequisition(Long requisitionId, User approvedBy, String message) {
        Optional<RequisitionData> optionalRequisition = requisitionDataRepository.findById(requisitionId);
        if (!optionalRequisition.isPresent()) {
            throw new RuntimeException("Requisition not found");
        }

        RequisitionData requisition = optionalRequisition.get();
        requisition.setStatus(RequisitionData.RequisitionStatus.APPROVED);
        requisition.setHodStatus(RequisitionData.RequisitionStatus.APPROVED);
        requisition.setApprovedBy(approvedBy);
        requisition.setApprovedDate(LocalDateTime.now());
        requisition.setLastUpdatedBy(approvedBy);
        requisition.setComment(message);

        return requisitionDataRepository.save(requisition);
    }

    public RequisitionData rejectRequisition(Long requisitionId, User rejectedBy, String reason) {
        Optional<RequisitionData> optionalRequisition = requisitionDataRepository.findById(requisitionId);
        if (!optionalRequisition.isPresent()) {
            throw new RuntimeException("Requisition not found");
        }

        RequisitionData requisition = optionalRequisition.get();
        requisition.setStatus(RequisitionData.RequisitionStatus.REJECTED);
        requisition.setRejectionReason(reason);
        requisition.setLastUpdatedBy(rejectedBy);

        return requisitionDataRepository.save(requisition);
    }

    public Optional<RequisitionData> getRequisitionById(Long id) {
        return requisitionDataRepository.findById(id);
    }

    public Optional<RequisitionData> getRequisitionByNumber(String requisitionNumber) {
        return requisitionDataRepository.findWithItemsAndCoaAndUserByRequisitionNumber(requisitionNumber);
    }

    @Transactional(readOnly = true)
    public RequisitionData getFullRequisitionByNumber(String requisitionNumber) {
        return requisitionDataRepository.findWithItemsAndCoaAndUserByRequisitionNumber(requisitionNumber)
                .orElseThrow(() -> new IllegalArgumentException(
                "Requisition not found with number: " + requisitionNumber));
    }

    public List<RequisitionData> getRequisitionsByBudget(Budget budget) {
        List<RequisitionData> result = requisitionDataRepository.findByBudgetOrderByCreatedDateDesc(budget);
        return result != null ? result : Collections.emptyList();
    }

    public List<RequisitionData> getByBudgetAndStatusOrderByCreatedDateDesc(Budget budget, RequisitionData.RequisitionStatus status) {
        List<RequisitionData> result = requisitionDataRepository.findByBudgetAndStatusOrderByCreatedDateDesc(budget, status);
        return result != null ? result : Collections.emptyList();
    }

    public List<RequisitionData> getByBudgetStatusesAndDeptSectionsOrderByCreatedDateDesc(
            Budget budget,
            Set<RequisitionData.RequisitionStatus> statuses,
            Set<UrcDeptSectionAnlDimbgt> sections) {

        if (statuses == null || statuses.isEmpty() || sections == null || sections.isEmpty()) {
            return Collections.emptyList();
        }

        return requisitionDataRepository
                .findByBudgetStatusesAndDeptSectionsOrderByCreatedDateDesc(budget, statuses, sections);
    }

    public List<RequisitionData> getByBudgetStatuseAndDeptSectionOrderByCreatedDateDesc(
            Budget budget,
            RequisitionData.RequisitionStatus statuses,
            UrcDeptSectionAnlDimbgt sections) {

        if (statuses == null || sections == null) {
            return Collections.emptyList();
        }

        return requisitionDataRepository
                .findByBudgetStatuseAndDeptSectionOrderByCreatedDateDesc(budget, statuses, sections);
    }

    public List<RequisitionData> findByBudgetAndDeptSectionOrderByCreatedDateDesc(Budget budget, UrcDeptSectionAnlDimbgt deptSection) {
        List<RequisitionData> result = requisitionDataRepository.findByBudgetAndDeptSectionOrderByCreatedDateDesc(budget, deptSection).stream()
                .distinct()
                .toList();
        return result != null ? result : Collections.emptyList();
    }

    public List<RequisitionData> findByBudgetAndDeptSectionInOrderByCreatedDateDesc(Budget budget, Set<UrcDeptSectionAnlDimbgt> deptSection) {
        List<RequisitionData> result = requisitionDataRepository.findByBudgetAndDeptSectionInOrderByCreatedDateDesc(budget, deptSection);
        return result != null ? result : Collections.emptyList();
    }

    public List<RequisitionData> getRequisitionsByActivity(Urc_Activities activity) {
        List<RequisitionData> result = requisitionDataRepository.findByActivityOrderByCreatedDateDesc(activity);
        return result != null ? result : Collections.emptyList();
    }

    public List<RequisitionData> findByBudget(Budget budget) {
        List<RequisitionData> result = requisitionDataRepository.findByBudget(budget);
        return result != null ? result : Collections.emptyList();
    }

    /*    public List<RequisitionData> getRequisitionsByCreator(String createdBy) {
    return requisitionDataRepository.findByCreatedByOrderByCreatedDateDesc(createdBy);
    }*/
    public List<RequisitionData> getRequisitionsByStatus(RequisitionData.RequisitionStatus status) {
        List<RequisitionData> result = requisitionDataRepository.findByStatusOrderByCreatedDateDesc(status);
        return result != null ? result : Collections.emptyList();
    }

    public List<RequisitionData> getRequisitionsByType(RequisitionData.RequisitionType type) {
        List<RequisitionData> result = requisitionDataRepository.findByRequisitionTypeOrderByCreatedDateDesc(type);
        return result != null ? result : Collections.emptyList();
    }

    public Page<RequisitionData> searchRequisitions(String searchTerm, Pageable pageable) {
        return requisitionDataRepository.searchRequisitions(searchTerm, pageable);

    }

    public List<RequisitionData> getPendingApprovals() {
        List<RequisitionData> result = requisitionDataRepository.findPendingApprovals();
        return result != null ? result : Collections.emptyList();
    }

    public List<RequisitionData> getOverBudgetRequisitions() {
        List<RequisitionData> result = requisitionDataRepository.findOverBudgetRequisitions();
        return result != null ? result : Collections.emptyList();
    }

    // Dashboard statistics
    public Long getTotalRequisitionsCount() {
        return requisitionDataRepository.count();
    }

    public Long getRequisitionsCountByStatus(RequisitionData.RequisitionStatus status) {
        return requisitionDataRepository.countByStatus(status);
    }

    public Long getRequisitionsCountByBudget(Budget budget) {
        return requisitionDataRepository.countByBudget(budget);
    }

    public Long getRequisitionsCountByBudgetAndDeptSection(Budget budget, UrcDeptSectionAnlDimbgt deptSection) {
        return requisitionDataRepository.countByBudgetAndDeptSection(budget, deptSection);
    }

    public Long getRequisitionsCountByBudgetAndStatusAndDeptSection(Budget budget, RequisitionData.RequisitionStatus status, UrcDeptSectionAnlDimbgt deptSection) {
        return requisitionDataRepository.countByBudgetAndStatusAndDeptSection(budget, status, deptSection);
    }

    public Long getRequisitionsCountByBudgetAndStatus(Budget budget, RequisitionData.RequisitionStatus status) {
        return requisitionDataRepository.countByBudgetAndStatus(budget, status);
    }

    public Double getTotalAmountByStatus(RequisitionData.RequisitionStatus status) {
        Double amount = requisitionDataRepository.sumAmountByStatus(status);
        return amount != null ? amount : 0.0;
    }

    public Double getTotalAmountByBudget(Budget budget) {
        Double amount = requisitionDataRepository.sumAmountByBudget(budget);
        return amount != null ? amount : 0.0;
    }

    public List<Object[]> getRequisitionsByType() {
        return requisitionDataRepository.countRequisitionsByType();
    }

    public List<Object[]> getRequisitionsByMonth() {
        return requisitionDataRepository.countRequisitionsByMonth();
    }

    public void deleteRequisition(Long requisitionId) {
        Optional<RequisitionData> requisition = requisitionDataRepository.findById(requisitionId);
        if (requisition.isPresent() && requisition.get().getStatus() == RequisitionData.RequisitionStatus.DRAFT) {
            requisitionDataRepository.deleteById(requisitionId);
        } else {
            throw new RuntimeException("Only draft requisitions can be deleted");
        }
    }

    // Item management methods
    public RequisitionItem addItemToRequisition(Long requisitionId, RequisitionItem item) {
        Optional<RequisitionData> optionalRequisition = requisitionDataRepository.findById(requisitionId);
        if (!optionalRequisition.isPresent()) {
            throw new RuntimeException("Requisition not found");
        }

        RequisitionData requisition = optionalRequisition.get();

        if (!requisition.canBeEdited()) {
            throw new RuntimeException("Cannot add items to submitted requisition");
        }

        item.setRequisitionData(requisition);
        item.setItemNumber(requisition.getItems().size() + 1);
        item.calculateTotalCost();

        RequisitionItem savedItem = requisitionItemRepository.save(item);

        // Update requisition total
        requisition.addItem(savedItem);
        requisitionDataRepository.save(requisition);

        return savedItem;
    }

    public void removeItemFromRequisition(Long requisitionId, Long itemId) {
        Optional<RequisitionData> optionalRequisition = requisitionDataRepository.findById(requisitionId);
        Optional<RequisitionItem> optionalItem = requisitionItemRepository.findById(itemId);

        if (!optionalRequisition.isPresent() || !optionalItem.isPresent()) {
            throw new RuntimeException("Requisition or item not found");
        }

        RequisitionData requisition = optionalRequisition.get();
        RequisitionItem item = optionalItem.get();

        if (!requisition.canBeEdited()) {
            throw new RuntimeException("Cannot remove items from submitted requisition");
        }

        requisition.removeItem(item);
        requisitionItemRepository.delete(item);

        // Renumber remaining items
        List<RequisitionItem> remainingItems = requisitionItemRepository.findByRequisitionDataOrderByItemNumberAsc(requisition);
        for (int i = 0; i < remainingItems.size(); i++) {
            remainingItems.get(i).setItemNumber(i + 1);
            requisitionItemRepository.save(remainingItems.get(i));
        }

        // Update requisition total
        requisitionDataRepository.save(requisition);
    }

    public List<RequisitionItem> getItemsByRequisition(Long requisitionId) {
        Optional<RequisitionData> optionalRequisition = requisitionDataRepository.findById(requisitionId);
        if (!optionalRequisition.isPresent()) {
            return List.of();
        }

        return requisitionItemRepository.findByRequisitionDataOrderByItemNumberAsc(optionalRequisition.get());
    }

    private String generateRequisitionNumber(RequisitionData.RequisitionType type) {
        String prefix;
        switch (type) {
            case CASH_REQUISITION:
                prefix = "CR";
                break;
            case FORM_5:
                prefix = "RFQ";
                break;
            case FORM_48:
                prefix = "LPO";
                break;
            default:
                prefix = "REQ";
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + "-" + timestamp + "-" + uuid;
    }

    /*    public List<RequisitionData> findLastRequisitionForFiscal(String fyStart, String fyEnd, Pageable pageable) {
    return requisitionDataRepository.findLastRequisitionForFiscal(fyStart, fyEnd, pageable);
    }*/
    public Optional<RequisitionData> getLastRequisitionForFiscal(Budget budget) {
        String startYear = String.format("/%02d/", budget.getStartDate().getYear() % 100);
        String endYear = String.format("/%02d/", budget.getCloseDate().getYear() % 100);

        List<RequisitionData> list = requisitionDataRepository.findLastRequisitionForFiscal(
                startYear, endYear, PageRequest.of(0, 1)
        );
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    /**
     * Count requisitions for a specific budget
     */
    public long countRequisitionsByBudget(Budget budget) {
        return requisitionDataRepository.countByBudget(budget);
    }

    /**
     * Count requisitions for a specific budget with a specific procurement
     * method
     */
    public long countRequisitionsByBudgetAndProcMethod(Budget budget, RequisitionData.ProcMethods procMethods) {
        return requisitionDataRepository.countByBudgetAndProcMethods(budget, procMethods);
    }

    // 👉 New method
    public long countRequisitionsByBudgetDeptSectionAndProcMethod(
            Budget budget,
            UrcDeptSectionAnlDimbgt deptSection,
            RequisitionData.ProcMethods procMethods
    ) {
        return requisitionDataRepository.countByBudgetAndDeptSectionAndProcMethods(budget, deptSection, procMethods);
    }

    public BigDecimal getTotalRequestedByBudget(Budget budget) {
        return requisitionDataRepository.sumRequestedAmountByBudget(budget);
    }

    public BigDecimal getTotalRequestedByDeptSection(UrcDeptSectionAnlDimbgt deptSection) {
        return requisitionDataRepository.sumRequestedAmountByDeptSection(deptSection);
    }

    public BigDecimal getTotalRequestedByBudgetAndStatus(Budget budget, RequisitionStatus status) {
        return requisitionDataRepository.sumRequestedAmountByBudgetAndStatus(budget, status);
    }

    public BigDecimal getTotalRequestedByBudgetDeptSectionAndStatus(Budget budget, UrcDeptSectionAnlDimbgt deptSection, RequisitionStatus status) {
        return requisitionDataRepository.sumRequestedAmountByBudgetDeptSectionAndStatus(budget, deptSection, status);
    }

    public Double getRequestedTotalByActivityAndCoa(String coaCode, Long activityId) {
        return requisitionDataRepository.getRequestedSumByActivityAndCoa(
                coaCode,
                activityId,
                RequisitionData.RequisitionStatus.REJECTED // always exclude rejected
        );
    }

    public Double getRequestedAmountForActivityExcludingRejected(Long activityId) {
        return requisitionDataRepository
                .findTotalRequestedAmountByActivityExcludingStatus(
                        activityId, RequisitionData.RequisitionStatus.REJECTED);
    }

    public Double getsumRequestedAmountByBudgetCOADeptSectionAndActivity(Budget budget, String coaCode, UrcDeptSectionAnlDimbgt deptSection, Long activityId, Long requisitionId) {
        return requisitionDataRepository.sumRequestedAmountByBudgetCOADeptSectionAndActivity(budget, coaCode, deptSection, activityId, RequisitionStatus.APPROVED, requisitionId).doubleValue();
    }

    public Double getsumRequestedAmountByBudgetCOADeptSection(Budget budget, String coaCode, UrcDeptSectionAnlDimbgt deptSection, Long requisitionId) {
        return requisitionDataRepository.sumRequestedAmountByBudgetCOADeptSection(budget, coaCode, deptSection, RequisitionStatus.APPROVED, requisitionId).doubleValue();
    }

    /**
     * Save or update a RequisitionData along with its items.
     */
    @Transactional
    public RequisitionData saveRequisition(RequisitionData requisitionData) {
        // If there are items
        if (requisitionData.getItems() != null && !requisitionData.getItems().isEmpty()) {
            // 🔗 Ensure bidirectional link & save items
            for (RequisitionItem item : requisitionData.getItems()) {
                item.setRequisitionData(requisitionData);
                requisitionItemRepository.save(item);
            } 

            // 💰 Calculate requested amount
            double requestedAmount = requisitionData.getItems().stream()
                    .mapToDouble(i
                            -> (i.getEstimatedUnitCost() != null ? i.getEstimatedUnitCost() : 0.0)
                    * (i.getQuantity() != null ? i.getQuantity() : 0.0)
                    )
                    .sum();
            requisitionData.setRequestedAmount(requestedAmount);
        } else {
            // No items → requestedAmount = 0
            requisitionData.setRequestedAmount(0.0);
        }

        // ✅ Save requisition data itself
        return requisitionDataRepository.save(requisitionData);
    }

    /**
     * Save a single RequisitionItem (optional helper)
     */
    @Transactional
    public RequisitionItem saveRequisitionItem(RequisitionItem item) {
        return requisitionItemRepository.save(item);
    }

    @Transactional
    public void deleteRequisitionItem(RequisitionItem item) {
        requisitionItemRepository.delete(item);
    }

    @Transactional
    public RequisitionData saveOrUpdateRequisition(RequisitionData requisitionData) {
        // If ID exists, merge; otherwise, insert
        if (requisitionData.getId() != null) {
            RequisitionData existing = requisitionDataRepository.findById(requisitionData.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Requisition not found"));
            existing = requisitionData;
            // Clear and set items
            existing.getItems().clear();
            for (RequisitionItem item : requisitionData.getItems()) {
                item.setRequisitionData(existing);
                existing.getItems().add(item);
            }

            return requisitionDataRepository.save(existing);
        } else {
            // New insert
            for (RequisitionItem item : requisitionData.getItems()) {
                item.setRequisitionData(requisitionData);
            }
            return requisitionDataRepository.save(requisitionData);
        }
    }

    public BigDecimal calculateTotalBalanceBudgetAndCoaAndSectionBalance(Budget budget, String coacode, String deptUnits) {
        BigDecimal actuals = salfldgRepository.calculateTotalByBudgetAndCoaAndSectionActuals(getFinancialYearPeriods(budget), coacode, deptUnits);

        return actuals;
    }

    public BigDecimal calculateTotalByBudgetAndCoaAndActivityAndSectionActuals(Budget budget, String coacode, String deptUnits, String activityCode) {
        BigDecimal actuals = salfldgRepository.calculateTotalByBudgetAndCoaAndActivityAndSectionActuals(getFinancialYearPeriods(budget), coacode, activityCode, deptUnits);

        return actuals;
    }

    public BigDecimal calculateTotalCommittedByCoaAndSectionBalance(Budget budget, String coacode, String deptUnits) {
        BigDecimal actuals = urcCSalFldgRepository.findTotalCommittedAmountByPeriodsCodeAndSection(getFinancialYearPeriods(budget), deptUnits, coacode);

        return actuals;
    }

    public BigDecimal calculateTotalCommittedByCoaAndSectionActivity(Budget budget, String coacode, String deptUnits, String activityCode) {
        BigDecimal actuals = urcCSalFldgRepository.findTotalCommittedAmountByPeriodsCodeAndSectionAndActivity(getFinancialYearPeriods(budget), deptUnits, coacode, activityCode);

        return actuals;
    }

    public Set<Integer> getFinancialYearPeriods(Budget budget) {
        Set<Integer> periods = new LinkedHashSet<>();

        if (budget.getStartDate() == null || budget.getCloseDate() == null) {
            return periods; // return empty if dates are not set
        }

        // Get the financial year end (YYYY part)
        int yearSuffix = budget.getCloseDate().getYear(); // e.g., 2025 for FY 2024/07/01 to 2025/06/30

        // Start from July of the start year
        LocalDate current = LocalDate.of(budget.getStartDate().getYear(), Month.JULY, 1);
        for (int i = 1; i <= 12; i++) {
            //String periodCode = String.format("%d%03d", yearSuffix, i); // e.g., 2025001
            int periodCode = yearSuffix * 1000 + i;
            periods.add(periodCode);
            current = current.plusMonths(1);
        }

        return periods;
    }

}
