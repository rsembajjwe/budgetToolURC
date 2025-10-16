
package com.methaltech.application.data.bgtool.repository;

import com.methaltech.application.data.entity.bgtool.BudgetApprovalMessages;
import java.util.List;

public interface BudgetApprovalMessagesService {
    BudgetApprovalMessages saveMessage(BudgetApprovalMessages message);
    BudgetApprovalMessages getMessageById(Integer id);
    List<BudgetApprovalMessages> getAllMessages();
    void deleteMessage(Integer id);
}
