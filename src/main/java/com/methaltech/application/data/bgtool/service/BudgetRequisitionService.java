package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.BudgetRequisitionRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetRequisition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class BudgetRequisitionService {

    @Autowired
    private BudgetRequisitionRepository requisitionRepository;

    @Autowired
    private BudgetService budgetService;

    public BudgetRequisition createRequisition(BudgetRequisition requisition) {
        // Generate unique requisition number
        String requisitionNumber = generateRequisitionNumber(
                requisition.getDepartmentCode(),
                requisition.getRequisitionType()
        );
        requisition.setRequisitionNumber(requisitionNumber);

        // Set initial status
        requisition.setStatus(BudgetRequisition.RequisitionStatus.DRAFT);
        requisition.setApprovalStatus(BudgetRequisition.ApprovalStatus.PENDING);

        // Get available budget for the account
        Double availableBudget = getAvailableBudget(
                requisition.getBudget(),
                requisition.getDepartmentCode(),
                requisition.getSectionCode(),
                requisition.getAccountCode()
        );
        requisition.setAvailableBudget(availableBudget);

        return requisitionRepository.save(requisition);
    }

    public BudgetRequisition updateRequisition(BudgetRequisition requisition) {
        return requisitionRepository.save(requisition);
    }

    public BudgetRequisition submitRequisition(Long requisitionId, String submittedBy) {
        Optional<BudgetRequisition> optionalRequisition = requisitionRepository.findById(requisitionId);
        if (!optionalRequisition.isPresent()) {
            throw new RuntimeException("Requisition not found");
        }

        BudgetRequisition requisition = optionalRequisition.get();

        if (!requisition.canBeSubmitted()) {
            throw new RuntimeException("Requisition cannot be submitted in current state");
        }

        requisition.setStatus(BudgetRequisition.RequisitionStatus.SUBMITTED);
        requisition.setApprovalStatus(BudgetRequisition.ApprovalStatus.PENDING);
        requisition.setLastUpdatedBy(submittedBy);

        return requisitionRepository.save(requisition);
    }

    public BudgetRequisition approveRequisition(Long requisitionId, String approvedBy, String comments) {
        Optional<BudgetRequisition> optionalRequisition = requisitionRepository.findById(requisitionId);
        if (!optionalRequisition.isPresent()) {
            throw new RuntimeException("Requisition not found");
        }

        BudgetRequisition requisition = optionalRequisition.get();
        requisition.setStatus(BudgetRequisition.RequisitionStatus.APPROVED);
        requisition.setApprovalStatus(BudgetRequisition.ApprovalStatus.FINAL_APPROVED);
        requisition.setApprovedBy(approvedBy);
        requisition.setApprovedDate(LocalDateTime.now());
        requisition.setNotes(comments);

        return requisitionRepository.save(requisition);
    }

    public BudgetRequisition rejectRequisition(Long requisitionId, String rejectedBy, String reason) {
        Optional<BudgetRequisition> optionalRequisition = requisitionRepository.findById(requisitionId);
        if (!optionalRequisition.isPresent()) {
            throw new RuntimeException("Requisition not found");
        }

        BudgetRequisition requisition = optionalRequisition.get();
        requisition.setStatus(BudgetRequisition.RequisitionStatus.REJECTED);
        requisition.setApprovalStatus(BudgetRequisition.ApprovalStatus.REJECTED);
        requisition.setRejectedBy(rejectedBy);
        requisition.setRejectedDate(LocalDateTime.now());
        requisition.setRejectionReason(reason);

        return requisitionRepository.save(requisition);
    }

    public BudgetRequisition cancelRequisition(Long requisitionId, String cancelledBy, String reason) {
        Optional<BudgetRequisition> optionalRequisition = requisitionRepository.findById(requisitionId);
        if (!optionalRequisition.isPresent()) {
            throw new RuntimeException("Requisition not found");
        }

        BudgetRequisition requisition = optionalRequisition.get();

        if (!requisition.canBeCancelled()) {
            throw new RuntimeException("Requisition cannot be cancelled in current state");
        }

        requisition.setStatus(BudgetRequisition.RequisitionStatus.CANCELLED);
        requisition.setLastUpdatedBy(cancelledBy);
        requisition.setNotes(reason);

        return requisitionRepository.save(requisition);
    }

    public Optional<BudgetRequisition> getRequisitionById(Long id) {
        return requisitionRepository.findById(id);
    }

    public Optional<BudgetRequisition> getRequisitionByNumber(String requisitionNumber) {
        return requisitionRepository.findByRequisitionNumber(requisitionNumber);
    }

    public List<BudgetRequisition> getRequisitionsByBudget(Budget budget) {
        return requisitionRepository.findByBudgetOrderByCreatedDateDesc(budget);
    }

    public List<BudgetRequisition> getRequisitionsByDepartment(String departmentCode) {
        return requisitionRepository.findByDepartmentCodeOrderByCreatedDateDesc(departmentCode);
    }

    public List<BudgetRequisition> getRequisitionsBySection(String departmentCode, String sectionCode) {
        return requisitionRepository.findByDepartmentCodeAndSectionCodeOrderByCreatedDateDesc(departmentCode, sectionCode);
    }

    public List<BudgetRequisition> getRequisitionsByCreator(String createdBy) {
        return requisitionRepository.findByCreatedByOrderByCreatedDateDesc(createdBy);
    }

    public List<BudgetRequisition> getRequisitionsByStatus(BudgetRequisition.RequisitionStatus status) {
        return requisitionRepository.findByStatusOrderByCreatedDateDesc(status);
    }

    public List<BudgetRequisition> getPendingRequisitions() {
        return requisitionRepository.findByStatusOrderByPriorityLevelDescCreatedDateAsc(
                BudgetRequisition.RequisitionStatus.SUBMITTED
        );
    }

    public List<BudgetRequisition> getHighPriorityRequisitions() {
        return requisitionRepository.findHighPriorityRequisitions();
    }

    public List<BudgetRequisition> getOverBudgetRequisitions() {
        return requisitionRepository.findOverBudgetRequisitions();
    }

    public Page<BudgetRequisition> searchRequisitions(String searchTerm, Pageable pageable) {
        return requisitionRepository.searchRequisitions(searchTerm, pageable);
    }

    // Dashboard statistics
    public Long getTotalRequisitionsCount() {
        return requisitionRepository.count();
    }

    public Long getPendingRequisitionsCount() {
        return requisitionRepository.countByStatus(BudgetRequisition.RequisitionStatus.SUBMITTED);
    }

    public Long getApprovedRequisitionsCount() {
        return requisitionRepository.countByStatus(BudgetRequisition.RequisitionStatus.APPROVED);
    }

    public Double getTotalRequestedAmount() {
        Double total = requisitionRepository.sumTotalRequestedAmount();
        return total != null ? total : 0.0;
    }

    public Double getApprovedAmount() {
        Double total = requisitionRepository.sumApprovedAmount();
        return total != null ? total : 0.0;
    }

    public Long getRequisitionsCountByDepartment(String departmentCode) {
        return requisitionRepository.countByDepartmentCode(departmentCode);
    }

    public Double getRequestedAmountByDepartment(String departmentCode) {
        Double total = requisitionRepository.sumRequestedAmountByDepartment(departmentCode);
        return total != null ? total : 0.0;
    }

    // Helper methods
    private String generateRequisitionNumber(String departmentCode, BudgetRequisition.RequisitionType type) {
        String prefix = "REQ-" + departmentCode + "-" + type.name().substring(0, 3);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + "-" + timestamp + "-" + uuid;
    }

    private Double getAvailableBudget(Budget budget, String departmentCode, String sectionCode, String accountCode) {
        // This would integrate with your budget allocation system
        // For now, return a mock value based on account code
        return 50_000_000.0 + (accountCode.hashCode() % 100_000_000);
    }

    public void deleteRequisition(Long requisitionId) {
        Optional<BudgetRequisition> requisition = requisitionRepository.findById(requisitionId);
        if (requisition.isPresent() && requisition.get().getStatus() == BudgetRequisition.RequisitionStatus.DRAFT) {
            requisitionRepository.deleteById(requisitionId);
        } else {
            throw new RuntimeException("Only draft requisitions can be deleted");
        }
    }
}
