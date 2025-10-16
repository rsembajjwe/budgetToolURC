package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.AcquisitionRequestRepository;
import com.methaltech.application.data.entity.bgtool.AcquisitionRequest;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.Organisation;
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
public class AcquisitionRequestService {

    @Autowired
    private AcquisitionRequestRepository acquisitionRepository;

    public AcquisitionRequest createAcquisition(AcquisitionRequest acquisition) {
        // Generate unique acquisition number
        String acquisitionNumber = generateAcquisitionNumber(
                acquisition.getAcquisitionType(),
                acquisition.getOrganisation().getCode()
        );
        acquisition.setAcquisitionNumber(acquisitionNumber);

        // Set initial status
        acquisition.setStatus(AcquisitionRequest.AcquisitionStatus.DRAFT);
        acquisition.setApprovalStatus(AcquisitionRequest.ApprovalStatus.PENDING);

        // Calculate budget information
        calculateBudgetInfo(acquisition);

        return acquisitionRepository.save(acquisition);
    }

    public AcquisitionRequest updateAcquisition(AcquisitionRequest acquisition) {
        calculateBudgetInfo(acquisition);
        return acquisitionRepository.save(acquisition);
    }

    public AcquisitionRequest submitAcquisition(Long acquisitionId, String submittedBy) {
        Optional<AcquisitionRequest> optionalAcquisition = acquisitionRepository.findById(acquisitionId);
        if (!optionalAcquisition.isPresent()) {
            throw new RuntimeException("Acquisition request not found");
        }

        AcquisitionRequest acquisition = optionalAcquisition.get();

        if (!acquisition.canBeSubmitted()) {
            throw new RuntimeException("Acquisition cannot be submitted in current state");
        }

        acquisition.setStatus(AcquisitionRequest.AcquisitionStatus.SUBMITTED);
        acquisition.setApprovalStatus(AcquisitionRequest.ApprovalStatus.PENDING);
        acquisition.setLastUpdatedBy(submittedBy);

        return acquisitionRepository.save(acquisition);
    }

    public AcquisitionRequest approveAcquisition(Long acquisitionId, String approvedBy, String comments) {
        Optional<AcquisitionRequest> optionalAcquisition = acquisitionRepository.findById(acquisitionId);
        if (!optionalAcquisition.isPresent()) {
            throw new RuntimeException("Acquisition request not found");
        }

        AcquisitionRequest acquisition = optionalAcquisition.get();
        acquisition.setStatus(AcquisitionRequest.AcquisitionStatus.APPROVED);
        acquisition.setApprovalStatus(AcquisitionRequest.ApprovalStatus.FINAL_APPROVED);
        acquisition.setApprovedBy(approvedBy);
        acquisition.setApprovedDate(LocalDateTime.now());
        acquisition.setNotes(comments);

        return acquisitionRepository.save(acquisition);
    }

    public AcquisitionRequest rejectAcquisition(Long acquisitionId, String rejectedBy, String reason) {
        Optional<AcquisitionRequest> optionalAcquisition = acquisitionRepository.findById(acquisitionId);
        if (!optionalAcquisition.isPresent()) {
            throw new RuntimeException("Acquisition request not found");
        }

        AcquisitionRequest acquisition = optionalAcquisition.get();
        acquisition.setStatus(AcquisitionRequest.AcquisitionStatus.REJECTED);
        acquisition.setApprovalStatus(AcquisitionRequest.ApprovalStatus.REJECTED);
        acquisition.setRejectedBy(rejectedBy);
        acquisition.setRejectedDate(LocalDateTime.now());
        acquisition.setRejectionReason(reason);

        return acquisitionRepository.save(acquisition);
    }

    public Optional<AcquisitionRequest> getAcquisitionById(Long id) {
        return acquisitionRepository.findById(id);
    }

    public Optional<AcquisitionRequest> getAcquisitionByNumber(String acquisitionNumber) {
        return acquisitionRepository.findByAcquisitionNumber(acquisitionNumber);
    }

    public List<AcquisitionRequest> getAcquisitionsByBudget(Budget budget) {
        return acquisitionRepository.findByBudgetOrderByCreatedDateDesc(budget);
    }

    public List<AcquisitionRequest> getAcquisitionsByOrganisation(Organisation organisation) {
        return acquisitionRepository.findByOrganisationOrderByCreatedDateDesc(organisation);
    }

    public List<AcquisitionRequest> getAcquisitionsByCOA(COA coa) {
        return acquisitionRepository.findByCoaOrderByCreatedDateDesc(coa);
    }

    public List<AcquisitionRequest> getAcquisitionsByCreator(String createdBy) {
        return acquisitionRepository.findByCreatedByOrderByCreatedDateDesc(createdBy);
    }

    public List<AcquisitionRequest> getAcquisitionsByStatus(AcquisitionRequest.AcquisitionStatus status) {
        return acquisitionRepository.findByStatusOrderByCreatedDateDesc(status);
    }

    public List<AcquisitionRequest> getPendingApprovals() {
        return acquisitionRepository.findByStatusOrderByPriorityLevelDescCreatedDateAsc(
                AcquisitionRequest.AcquisitionStatus.SUBMITTED
        );
    }

    public List<AcquisitionRequest> getHighPriorityAcquisitions() {
        return acquisitionRepository.findHighPriorityAcquisitions();
    }

    public Page<AcquisitionRequest> searchAcquisitions(String searchTerm, Pageable pageable) {
        return acquisitionRepository.searchAcquisitions(searchTerm, pageable);
    }

    // Dashboard statistics
    public Long getTotalAcquisitionsCount() {
        return acquisitionRepository.count();
    }

    public Long getPendingAcquisitionsCount() {
        return acquisitionRepository.countByStatus(AcquisitionRequest.AcquisitionStatus.SUBMITTED);
    }

    public Long getApprovedAcquisitionsCount() {
        return acquisitionRepository.countByStatus(AcquisitionRequest.AcquisitionStatus.APPROVED);
    }

    public Double getTotalRequestedAmount() {
        Double total = acquisitionRepository.sumTotalRequestedAmount();
        return total != null ? total : 0.0;
    }

    public Double getApprovedAmount() {
        Double total = acquisitionRepository.sumApprovedAmount();
        return total != null ? total : 0.0;
    }

    private String generateAcquisitionNumber(AcquisitionRequest.AcquisitionType type, String orgCode) {
        String prefix = type == AcquisitionRequest.AcquisitionType.CASH_REQUISITION ? "CR" : "LPO";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + "-" + orgCode + "-" + timestamp + "-" + uuid;
    }

    private void calculateBudgetInfo(AcquisitionRequest acquisition) {
        // This would integrate with your actual budget calculation system
        // For now, using mock calculations
        if (acquisition.getCoa() != null) {
            String coaCode = acquisition.getCoa().getCode();
            double mockBudget = 50_000_000 + (coaCode.hashCode() % 200_000_000);
            double mockCommitted = mockBudget * 0.15;
            double mockSpent = mockBudget * 0.25;

            acquisition.setAvailableBudget(mockBudget);
            acquisition.setCommittedAmount(mockCommitted);
            acquisition.setActualSpent(mockSpent);
            acquisition.setBalanceAmount(mockBudget - mockCommitted - mockSpent);
        }
    }

    public void deleteAcquisition(Long acquisitionId) {
        Optional<AcquisitionRequest> acquisition = acquisitionRepository.findById(acquisitionId);
        if (acquisition.isPresent() && acquisition.get().getStatus() == AcquisitionRequest.AcquisitionStatus.DRAFT) {
            acquisitionRepository.deleteById(acquisitionId);
        } else {
            throw new RuntimeException("Only draft acquisitions can be deleted");
        }
    }
}
