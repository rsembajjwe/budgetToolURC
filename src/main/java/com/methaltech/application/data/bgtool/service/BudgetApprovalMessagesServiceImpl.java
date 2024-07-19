package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.bgtool.repository.BudgetApprovalMessagesRepository;
import com.methaltech.application.data.bgtool.repository.BudgetApprovalMessagesService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetApprovalMessages;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class BudgetApprovalMessagesServiceImpl implements BudgetApprovalMessagesService {

    @Autowired
    private BudgetApprovalMessagesRepository repository;

    @Override
    public BudgetApprovalMessages saveMessage(BudgetApprovalMessages message) {
        return repository.save(message);
    }

    @Override
    public BudgetApprovalMessages getMessageById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<BudgetApprovalMessages> getAllMessages() {
        return repository.findAll();
    }

    @Override
    public void deleteMessage(Integer id) {
        repository.deleteById(id);
    }

    public List<BudgetApprovalMessages> getMessagesByBudgetAndSections(Budget budget, Set<UrcDeptSectionAnlDimbgt> sections) {
        return repository.findByBudgetAndSections(budget, sections);
    }
}
