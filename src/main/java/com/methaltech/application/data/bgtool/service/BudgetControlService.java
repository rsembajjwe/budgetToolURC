
package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.BudgetControlRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BudgetControlService {

    @Autowired
    private BudgetControlRepository controlRepository;

    // ===================================================================
    // BUDGET LEVEL CONTROLS
    // ===================================================================

    @Transactional
    public BudgetControl setBudgetLevelControl(Budget budget, boolean budgetCheck, boolean budgetStop, 
                                              boolean postingProhibited, String reason, String createdBy) {
        Optional<BudgetControl> existingControl = controlRepository.findByBudgetAndControlLevel(
            budget, BudgetControl.ControlLevel.BUDGET);
        
        BudgetControl control;
        if (existingControl.isPresent()) {
            control = existingControl.get();
            control.setLastUpdatedBy(createdBy);
        } else {
            control = new BudgetControl();
            control.setBudget(budget);
            control.setControlLevel(BudgetControl.ControlLevel.BUDGET);
            control.setCreatedBy(createdBy);
        }
        
        control.setBudgetCheckEnabled(budgetCheck);
        control.setBudgetStopEnabled(budgetStop);
        control.setPostingProhibited(postingProhibited);
        control.setReason(reason);
        control.setControlStatus(BudgetControl.ControlStatus.ACTIVE);
        control.setEffectiveDate(LocalDateTime.now());
        
        return controlRepository.save(control);
    }

    public Optional<BudgetControl> getBudgetLevelControl(Budget budget) {
        return controlRepository.findByBudgetAndControlLevel(budget, BudgetControl.ControlLevel.BUDGET);
    }

    // ===================================================================
    // DEPARTMENT LEVEL CONTROLS
    // ===================================================================

    @Transactional
    public BudgetControl setDepartmentLevelControl(Budget budget, String departmentCode, String departmentName,
                                                  boolean budgetCheck, boolean budgetStop, boolean postingProhibited, 
                                                  String reason, String createdBy) {
        Optional<BudgetControl> existingControl = controlRepository.findByBudgetAndDepartmentCodeAndControlLevel(
            budget, departmentCode, BudgetControl.ControlLevel.DEPARTMENT);
        
        BudgetControl control;
        if (existingControl.isPresent()) {
            control = existingControl.get();
            control.setLastUpdatedBy(createdBy);
        } else {
            control = new BudgetControl();
            control.setBudget(budget);
            control.setControlLevel(BudgetControl.ControlLevel.DEPARTMENT);
            control.setDepartmentCode(departmentCode);
            control.setDepartmentName(departmentName);
            control.setCreatedBy(createdBy);
        }
        
        control.setBudgetCheckEnabled(budgetCheck);
        control.setBudgetStopEnabled(budgetStop);
        control.setPostingProhibited(postingProhibited);
        control.setReason(reason);
        control.setControlStatus(BudgetControl.ControlStatus.ACTIVE);
        control.setEffectiveDate(LocalDateTime.now());
        
        return controlRepository.save(control);
    }

    public List<BudgetControl> getDepartmentLevelControls(Budget budget) {
        return controlRepository.findByBudgetAndControlLevelOrderByDepartmentCodeAsc(
            budget, BudgetControl.ControlLevel.DEPARTMENT);
    }

    public Optional<BudgetControl> getDepartmentControl(Budget budget, String departmentCode) {
        return controlRepository.findByBudgetAndDepartmentCodeAndControlLevel(
            budget, departmentCode, BudgetControl.ControlLevel.DEPARTMENT);
    }

    // ===================================================================
    // SECTION LEVEL CONTROLS
    // ===================================================================

    @Transactional
    public BudgetControl setSectionLevelControl(Budget budget, String departmentCode, String departmentName,
                                               String sectionCode, String sectionName, boolean budgetCheck, 
                                               boolean budgetStop, boolean postingProhibited, String reason, String createdBy) {
        Optional<BudgetControl> existingControl = controlRepository.findByBudgetAndDepartmentCodeAndSectionCode(
            budget, departmentCode, sectionCode);
        
        BudgetControl control;
        if (existingControl.isPresent()) {
            control = existingControl.get();
            control.setLastUpdatedBy(createdBy);
        } else {
            control = new BudgetControl();
            control.setBudget(budget);
            control.setControlLevel(BudgetControl.ControlLevel.SECTION);
            control.setDepartmentCode(departmentCode);
            control.setDepartmentName(departmentName);
            control.setSectionCode(sectionCode);
            control.setSectionName(sectionName);
            control.setCreatedBy(createdBy);
        }
        
        control.setBudgetCheckEnabled(budgetCheck);
        control.setBudgetStopEnabled(budgetStop);
        control.setPostingProhibited(postingProhibited);
        control.setReason(reason);
        control.setControlStatus(BudgetControl.ControlStatus.ACTIVE);
        control.setEffectiveDate(LocalDateTime.now());
        
        return controlRepository.save(control);
    }

    public List<BudgetControl> getSectionLevelControls(Budget budget, String departmentCode) {
        return controlRepository.findByBudgetAndDepartmentCodeOrderByControlLevelAsc(budget, departmentCode);
    }

    public Optional<BudgetControl> getSectionControl(Budget budget, String departmentCode, String sectionCode) {
        return controlRepository.findByBudgetAndDepartmentCodeAndSectionCode(budget, departmentCode, sectionCode);
    }

    // ===================================================================
    // CONTROL VALIDATION METHODS
    // ===================================================================

    public boolean isBudgetCheckEnabled(Budget budget, String departmentCode, String sectionCode) {
        return controlRepository.isBudgetCheckEnabled(budget, departmentCode, sectionCode, LocalDateTime.now());
    }

    public boolean isBudgetStopEnabled(Budget budget, String departmentCode, String sectionCode) {
        return controlRepository.isBudgetStopEnabled(budget, departmentCode, sectionCode, LocalDateTime.now());
    }

    public boolean isPostingProhibited(Budget budget, String departmentCode, String sectionCode) {
        return controlRepository.isPostingProhibited(budget, departmentCode, sectionCode, LocalDateTime.now());
    }

    // ===================================================================
    // BULK OPERATIONS
    // ===================================================================

    @Transactional
    public int enableBudgetCheckForDepartments(Budget budget, List<String> departmentCodes, String updatedBy) {
        List<BudgetControl> controls = controlRepository.findByBudgetAndControlLevelOrderByDepartmentCodeAsc(
            budget, BudgetControl.ControlLevel.DEPARTMENT);
        
        List<Long> controlIds = controls.stream()
            .filter(control -> departmentCodes.contains(control.getDepartmentCode()))
            .map(BudgetControl::getId)
            .toList();
        
        return controlRepository.updateBudgetCheckForControls(true, updatedBy, LocalDateTime.now(), controlIds);
    }

    @Transactional
    public int enableBudgetStopForDepartments(Budget budget, List<String> departmentCodes, String updatedBy) {
        List<BudgetControl> controls = controlRepository.findByBudgetAndControlLevelOrderByDepartmentCodeAsc(
            budget, BudgetControl.ControlLevel.DEPARTMENT);
        
        List<Long> controlIds = controls.stream()
            .filter(control -> departmentCodes.contains(control.getDepartmentCode()))
            .map(BudgetControl::getId)
            .toList();
        
        return controlRepository.updateBudgetStopForControls(true, updatedBy, LocalDateTime.now(), controlIds);
    }

    @Transactional
    public int prohibitPostingForDepartments(Budget budget, List<String> departmentCodes, String updatedBy) {
        List<BudgetControl> controls = controlRepository.findByBudgetAndControlLevelOrderByDepartmentCodeAsc(
            budget, BudgetControl.ControlLevel.DEPARTMENT);
        
        List<Long> controlIds = controls.stream()
            .filter(control -> departmentCodes.contains(control.getDepartmentCode()))
            .map(BudgetControl::getId)
            .toList();
        
        return controlRepository.updatePostingProhibitedForControls(true, updatedBy, LocalDateTime.now(), controlIds);
    }

    // ===================================================================
    // CONTROL MANAGEMENT
    // ===================================================================

    @Transactional
    public BudgetControl activateControl(Long controlId, String activatedBy) {
        Optional<BudgetControl> optionalControl = controlRepository.findById(controlId);
        if (!optionalControl.isPresent()) {
            throw new RuntimeException("Budget control not found");
        }
        
        BudgetControl control = optionalControl.get();
        control.setControlStatus(BudgetControl.ControlStatus.ACTIVE);
        control.setLastUpdatedBy(activatedBy);
        control.setEffectiveDate(LocalDateTime.now());
        
        return controlRepository.save(control);
    }

    @Transactional
    public BudgetControl deactivateControl(Long controlId, String deactivatedBy) {
        Optional<BudgetControl> optionalControl = controlRepository.findById(controlId);
        if (!optionalControl.isPresent()) {
            throw new RuntimeException("Budget control not found");
        }
        
        BudgetControl control = optionalControl.get();
        control.setControlStatus(BudgetControl.ControlStatus.INACTIVE);
        control.setLastUpdatedBy(deactivatedBy);
        
        return controlRepository.save(control);
    }

    @Transactional
    public BudgetControl suspendControl(Long controlId, String suspendedBy, String reason) {
        Optional<BudgetControl> optionalControl = controlRepository.findById(controlId);
        if (!optionalControl.isPresent()) {
            throw new RuntimeException("Budget control not found");
        }
        
        BudgetControl control = optionalControl.get();
        control.setControlStatus(BudgetControl.ControlStatus.SUSPENDED);
        control.setLastUpdatedBy(suspendedBy);
        control.setReason(reason);
        
        return controlRepository.save(control);
    }

    // ===================================================================
    // DASHBOARD STATISTICS
    // ===================================================================

    public Long getTotalActiveControls(Budget budget) {
        return controlRepository.findActiveControls(budget, LocalDateTime.now()).stream().count();
    }

    public Long getBudgetCheckControlsCount(Budget budget) {
        return controlRepository.countBudgetCheckControls(budget);
    }

    public Long getBudgetStopControlsCount(Budget budget) {
        return controlRepository.countBudgetStopControls(budget);
    }

    public Long getPostingProhibitedControlsCount(Budget budget) {
        return controlRepository.countPostingProhibitedControls(budget);
    }

    // ===================================================================
    // MAINTENANCE OPERATIONS
    // ===================================================================

    @Transactional
    public int cleanupExpiredControls() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30); // Keep expired controls for 30 days
        return controlRepository.deleteExpiredControls(cutoffDate);
    }

    public List<BudgetControl> getExpiringControls(Budget budget, int daysAhead) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(daysAhead);
        return controlRepository.findExpiringControls(budget, startDate, endDate);
    }

    // ===================================================================
    // VALIDATION METHODS
    // ===================================================================

    public boolean validateTransactionAgainstControls(Budget budget, String departmentCode, String sectionCode, 
                                                     double transactionAmount, double currentSpent, double budgetLimit) {
        LocalDateTime now = LocalDateTime.now();
        
        // Check if budget check is enabled
        if (!isBudgetCheckEnabled(budget, departmentCode, sectionCode)) {
            return true; // No budget check required
        }
        
        // Check if posting is prohibited
        if (isPostingProhibited(budget, departmentCode, sectionCode)) {
            throw new RuntimeException("Posting is prohibited for this budget/department/section");
        }
        
        // Check budget limits
        double newTotal = currentSpent + transactionAmount;
        if (newTotal > budgetLimit) {
            if (isBudgetStopEnabled(budget, departmentCode, sectionCode)) {
                throw new RuntimeException("Transaction exceeds budget limit and budget stop is enabled");
            } else {
                // Budget check enabled but no stop - log warning
                return false; // Indicates budget exceeded but transaction can proceed
            }
        }
        
        return true; // Transaction is within budget
    }

    public String getControlStatusMessage(Budget budget, String departmentCode, String sectionCode) {
        StringBuilder message = new StringBuilder();
        
        if (isBudgetCheckEnabled(budget, departmentCode, sectionCode)) {
            message.append("Budget Check: Enabled");
        }
        
        if (isBudgetStopEnabled(budget, departmentCode, sectionCode)) {
            if (message.length() > 0) message.append(" | ");
            message.append("Budget Stop: Enabled");
        }
        
        if (isPostingProhibited(budget, departmentCode, sectionCode)) {
            if (message.length() > 0) message.append(" | ");
            message.append("Posting: Prohibited");
        }
        
        return message.length() > 0 ? message.toString() : "No Active Controls";
    }

    // ===================================================================
    // CRUD OPERATIONS
    // ===================================================================

    @Transactional
    public BudgetControl createControl(BudgetControl control) {
        return controlRepository.save(control);
    }

    @Transactional
    public BudgetControl updateControl(BudgetControl control) {
        return controlRepository.save(control);
    }

    @Transactional
    public void deleteControl(Long controlId) {
        controlRepository.deleteById(controlId);
    }

    public Optional<BudgetControl> getControlById(Long id) {
        return controlRepository.findById(id);
    }

    public List<BudgetControl> getControlsByBudget(Budget budget) {
        return controlRepository.findByBudgetOrderByControlLevelAscDepartmentCodeAsc(budget);
    }

    public List<BudgetControl> getActiveControls(Budget budget) {
        return controlRepository.findActiveControls(budget, LocalDateTime.now());
    }
}
