package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.BudgetApprovalsRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetApprovals;
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
public class BudgetApprovalsService {

    @Autowired
    private BudgetApprovalsRepository approvalsRepository;

    @Autowired
    private BudgetService budgetService;

    public BudgetApprovals createApprovalRequest(BudgetApprovals approval) {
        // Generate unique request ID
        String requestId = generateRequestId(approval.getDepartmentCode(), approval.getRequestType());
        approval.setRequestId(requestId);

        // Ensure budget is set
        if (approval.getBudget() == null) {
            throw new RuntimeException("Budget must be specified for approval request");
        }

        // Set initial status and stage
        approval.setCurrentStage(BudgetApprovals.ApprovalStage.BLO_REVIEW);
        approval.setOverallStatus(BudgetApprovals.ApprovalStatus.IN_PROGRESS);

        // Set timestamps
        approval.setRequestedDate(LocalDateTime.now());

        return approvalsRepository.save(approval);
    }

    public BudgetApprovals approveRequest(Long approvalId, String approverRole, String approverName, String comments) {
        Optional<BudgetApprovals> optionalApproval = approvalsRepository.findById(approvalId);
        if (!optionalApproval.isPresent()) {
            throw new RuntimeException("Approval request not found");
        }

        BudgetApprovals approval = optionalApproval.get();

        if (!approval.canBeApprovedBy(approverRole)) {
            throw new RuntimeException("User does not have permission to approve at this stage");
        }

        // Update approval based on current stage
        LocalDateTime now = LocalDateTime.now();

        switch (approval.getCurrentStage()) {
            case BLO_REVIEW:
                approval.setBloApprovedBy(approverName);
                approval.setBloApprovedDate(now);
                approval.setBloComments(comments);
                break;
            case HOD_REVIEW:
                approval.setHodApprovedBy(approverName);
                approval.setHodApprovedDate(now);
                approval.setHodComments(comments);
                break;
            case ADMIN_REVIEW:
                approval.setAdminApprovedBy(approverName);
                approval.setAdminApprovedDate(now);
                approval.setAdminComments(comments);
                break;
            case CFO_REVIEW:
                approval.setCfoApprovedBy(approverName);
                approval.setCfoApprovedDate(now);
                approval.setCfoComments(comments);
                approval.setFinalApprovedAmount(approval.getRequestedAmount());
                approval.setOverallStatus(BudgetApprovals.ApprovalStatus.APPROVED);
                break;
        }

        // Move to next stage if not completed
        if (approval.getCurrentStage() != BudgetApprovals.ApprovalStage.CFO_REVIEW) {
            approval.setCurrentStage(approval.getNextStage());
        } else {
            approval.setCurrentStage(BudgetApprovals.ApprovalStage.COMPLETED);
        }

        return approvalsRepository.save(approval);
    }

    public BudgetApprovals rejectRequest(Long approvalId, String rejectorRole, String rejectorName, String rejectionReason) {
        Optional<BudgetApprovals> optionalApproval = approvalsRepository.findById(approvalId);
        if (!optionalApproval.isPresent()) {
            throw new RuntimeException("Approval request not found");
        }

        BudgetApprovals approval = optionalApproval.get();

        if (!approval.canBeApprovedBy(rejectorRole)) {
            throw new RuntimeException("User does not have permission to reject at this stage");
        }

        approval.setOverallStatus(BudgetApprovals.ApprovalStatus.REJECTED);
        approval.setRejectedBy(rejectorName);
        approval.setRejectedDate(LocalDateTime.now());
        approval.setRejectionReason(rejectionReason);

        return approvalsRepository.save(approval);
    }

    public List<BudgetApprovals> getPendingApprovalsByRole(String role) {
        BudgetApprovals.ApprovalStage stage;
        switch (role) {
            case "BLO":
                stage = BudgetApprovals.ApprovalStage.BLO_REVIEW;
                break;
            case "HOD":
                stage = BudgetApprovals.ApprovalStage.HOD_REVIEW;
                break;
            case "ADMIN":
                stage = BudgetApprovals.ApprovalStage.ADMIN_REVIEW;
                break;
            case "CFO":
                stage = BudgetApprovals.ApprovalStage.CFO_REVIEW;
                break;
            default:
                return List.of();
        }

        return approvalsRepository.findPendingApprovalsByStage(stage);
    }

    public List<BudgetApprovals> getApprovalsByDepartment(String departmentCode) {
        return approvalsRepository.findByDepartmentCodeOrderByRequestedDateDesc(departmentCode);
    }

    public List<BudgetApprovals> getApprovalsByRequester(String requester) {
        return approvalsRepository.findByRequestedByOrderByRequestedDateDesc(requester);
    }

    public List<BudgetApprovals> getApprovalsByBudget(Budget budget) {
        return approvalsRepository.findByBudgetOrderByRequestedDateDesc(budget);
    }

    public List<BudgetApprovals> getApprovalsByBudgetId(Long budgetId) {
        return approvalsRepository.findByBudget_IdOrderByRequestedDateDesc(budgetId);
    }

    public List<BudgetApprovals> getApprovalsByBudgetAndDepartment(Budget budget, String departmentCode) {
        return approvalsRepository.findByBudgetAndDepartment(budget, departmentCode);
    }

    public List<BudgetApprovals> getApprovalsByBudgetAndStatus(Budget budget, BudgetApprovals.ApprovalStatus status) {
        return approvalsRepository.findByBudgetAndStatus(budget, status);
    }

    public List<BudgetApprovals> getHighPriorityApprovals() {
        return approvalsRepository.findHighPriorityPendingApprovals();
    }

    public List<BudgetApprovals> getOverdueApprovals() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return approvalsRepository.findOverdueApprovals(sevenDaysAgo);
    }

    public Page<BudgetApprovals> searchApprovals(String searchTerm, Pageable pageable) {
        return approvalsRepository.searchApprovals(searchTerm, pageable);
    }

    public Optional<BudgetApprovals> getApprovalByRequestId(String requestId) {
        return approvalsRepository.findByRequestId(requestId);
    }

    public BudgetApprovals updateApproval(BudgetApprovals approval) {
        return approvalsRepository.save(approval);
    }

    public void deleteApproval(Long approvalId) {
        approvalsRepository.deleteById(approvalId);
    }

    // Dashboard statistics
    public Long getPendingApprovalsCount() {
        return approvalsRepository.countPendingApprovals();
    }

    public Long getApprovedThisMonthCount() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        return approvalsRepository.countApprovedThisMonth(startOfMonth);
    }

    public Long getRejectedThisMonthCount() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        return approvalsRepository.countRejectedThisMonth(startOfMonth);
    }

    public Double getApprovedAmountThisMonth() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        Double amount = approvalsRepository.sumApprovedAmountThisMonth(startOfMonth);
        return amount != null ? amount : 0.0;
    }

    private String generateRequestId(String departmentCode, BudgetApprovals.RequestType requestType) {
        String prefix = departmentCode + "-" + requestType.name().substring(0, 3);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefix + "-" + timestamp + "-" + uuid;
    }
}
