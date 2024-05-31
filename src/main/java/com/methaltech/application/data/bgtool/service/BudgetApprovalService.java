package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.BudgetApprovalMessagesRepository;
import com.methaltech.application.data.bgtool.repository.BudgetApprovalRepository;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetApproval;
import com.methaltech.application.data.entity.bgtool.BudgetApprovalMessages;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.User;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BudgetApprovalService {

    @Autowired
    private BudgetApprovalRepository budgetApprovalRepository;
    @Autowired
    private BudgetApprovalMessagesRepository budgetApprovalMessagesRepository;

    @Transactional
    public List<BudgetApproval> getBudgetApprovalsByBudgetAndSections(Budget budget, Set<UrcDeptSectionAnlDimbgt> sections) {
        List<BudgetApproval> budgetApprovals = budgetApprovalRepository.findByBudgetAndSections(budget, sections);
        return budgetApprovals;
    }

    @Transactional
    public Page<BudgetApproval> getBudgetApprovalsByBudgetAndSections(Budget budget, Set<UrcDeptSectionAnlDimbgt> sections, Pageable pageable) {

        return budgetApprovalRepository.findByBudgetAndSections(budget, sections, pageable);
    }

    @Transactional
    public BudgetApproval saveBudgetApproval(BudgetApproval budgetApproval) {

        return budgetApprovalRepository.save(budgetApproval);
    }

    public List<BudgetApproval> getBudgetApprovalsByBudget(Budget budget) {
        return budgetApprovalRepository.findByBudget(budget);
    }

    public BudgetApproval findTopByBudgetAndSectionOrderByBloSubmissionDateDesc(Budget budget, UrcDeptSectionAnlDimbgt section) {
        return budgetApprovalRepository.findTopByBudgetAndSectionOrderByBloSubmissionDateDesc(budget, section);
    }

    public boolean allHodSubmissionsTrue(Budget budget) {
        List<BudgetApproval> budgetApprovals = getBudgetApprovalsByBudget(budget);
        for (BudgetApproval approval : budgetApprovals) {
            if (approval.getHodSubmission() == null || !approval.getHodSubmission()) {
                return false;
            }
        }
        return true;
    }
        public boolean allCfoSubmissionsTrue(Budget budget) {
        List<BudgetApproval> budgetApprovals = getBudgetApprovalsByBudget(budget);
        for (BudgetApproval approval : budgetApprovals) {
            if (approval.getMaSubmission()== null || !approval.getMaSubmission()) {
                return false;
            }
        }
        return true;
    }
        
        public boolean allMdApproveTrue(Budget budget) {
        List<BudgetApproval> budgetApprovals = getBudgetApprovalsByBudget(budget);
        for (BudgetApproval approval : budgetApprovals) {
            if (approval.getCfoSubmission()== null || !approval.getCfoSubmission()) {
                return false;
            }
        }
        return true;
    }        

    public void sunmitToCFO(Budget budget, User user) {
        List<BudgetApproval> budgetApprovals = getBudgetApprovalsByBudget(budget);
        for (BudgetApproval approval : budgetApprovals) {
            approval.setMaSubmission(true);
            approval.setMaSubmissionDate(LocalDateTime.now());
            saveBudgetApproval(approval);
            BudgetApprovalMessages newMessage = new BudgetApprovalMessages();
            newMessage.setApprover(user);
            newMessage.setSentDate(LocalDateTime.now());
            newMessage.setMessage(approval.getSection().getNAME() + " Budget Submitted to HOD");
            newMessage.setBudgetApproval(approval);
            budgetApprovalMessagesRepository.save(newMessage);
        }

    }
        public void UnsunmitToCFO(Budget budget, User user) {
        List<BudgetApproval> budgetApprovals = getBudgetApprovalsByBudget(budget);
        for (BudgetApproval approval : budgetApprovals) {
            approval.setMaSubmission(false);
            approval.setMaSubmissionDate(LocalDateTime.now());
            saveBudgetApproval(approval);
            BudgetApprovalMessages newMessage = new BudgetApprovalMessages();
            newMessage.setApprover(user);
            newMessage.setSentDate(LocalDateTime.now());
            newMessage.setMessage(approval.getSection().getNAME() + " Budget Un Submitted to HOD");
            newMessage.setBudgetApproval(approval);
            budgetApprovalMessagesRepository.save(newMessage);
        }

    }
        public void sunmitToMD(Budget budget, User user) {
        List<BudgetApproval> budgetApprovals = getBudgetApprovalsByBudget(budget);
        for (BudgetApproval approval : budgetApprovals) {
            approval.setCfoSubmission(true);
            approval.setCfoSubmissionDate(LocalDateTime.now());
            saveBudgetApproval(approval);
            BudgetApprovalMessages newMessage = new BudgetApprovalMessages();
            newMessage.setApprover(user);
            newMessage.setSentDate(LocalDateTime.now());
            newMessage.setMessage(approval.getSection().getNAME() + " Budget Submitted to MD");
            newMessage.setBudgetApproval(approval);
            budgetApprovalMessagesRepository.save(newMessage);
        }

    }
        public void Approve(Budget budget, User user) {
        List<BudgetApproval> budgetApprovals = getBudgetApprovalsByBudget(budget);
        for (BudgetApproval approval : budgetApprovals) {
            approval.setMaApproval(true);
            approval.setMaApprovalDate(LocalDateTime.now());
            saveBudgetApproval(approval);
            BudgetApprovalMessages newMessage = new BudgetApprovalMessages();
            newMessage.setApprover(user);
            newMessage.setSentDate(LocalDateTime.now());
            newMessage.setMessage(approval.getSection().getNAME() + " Budget Approved");
            newMessage.setBudgetApproval(approval);
            budgetApprovalMessagesRepository.save(newMessage);
        }

    }   
        
        public void UnApprove(Budget budget, User user) {
        List<BudgetApproval> budgetApprovals = getBudgetApprovalsByBudget(budget);
        for (BudgetApproval approval : budgetApprovals) {
            approval.setMaApproval(false);
            approval.setMaApprovalDate(LocalDateTime.now());
            saveBudgetApproval(approval);
            BudgetApprovalMessages newMessage = new BudgetApprovalMessages();
            newMessage.setApprover(user);
            newMessage.setSentDate(LocalDateTime.now());
            newMessage.setMessage(approval.getSection().getNAME() + " Budget Un Approved By MD");
            newMessage.setBudgetApproval(approval);
            budgetApprovalMessagesRepository.save(newMessage);
        }

    } 
        
        public boolean findIfMDUnapproved(Budget budget) {
        List<BudgetApproval> budgetApprovals = getBudgetApprovalsByBudget(budget);
        for (BudgetApproval approval : budgetApprovals) {
            if (approval.getMaApproval()== null || !approval.getMaApproval()) {
                return true;
            }
        }
        return false;
    } 
        
        public void UnsunmitToMD(Budget budget, User user) {
        List<BudgetApproval> budgetApprovals = getBudgetApprovalsByBudget(budget);
        for (BudgetApproval approval : budgetApprovals) {
            approval.setCfoSubmission(false);
            approval.setCfoSubmissionDate(LocalDateTime.now());
            saveBudgetApproval(approval);
            BudgetApprovalMessages newMessage = new BudgetApprovalMessages();
            newMessage.setApprover(user);
            newMessage.setSentDate(LocalDateTime.now());
            newMessage.setMessage(approval.getSection().getNAME() + " Budget Withdrew by  CFO");
            newMessage.setBudgetApproval(approval);
            budgetApprovalMessagesRepository.save(newMessage);
        }

    }        
}
