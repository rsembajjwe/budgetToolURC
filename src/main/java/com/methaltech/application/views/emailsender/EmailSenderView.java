package com.methaltech.application.views.emailsender;

import com.methaltech.application.data.EmailSender;
import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.bgtool.service.Coalevel1Service;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.Coalevel1;
import com.methaltech.application.data.entity.bgtool.Currency;
import com.methaltech.application.views.MainLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Email Sender")
@Route(value = "send_email", layout = MainLayout.class)
//@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
//@AnonymousAllowed

public class EmailSenderView extends HorizontalLayout {
    
    private EmailField name;
    private Button sayHello;
    private Button sett;
    private Text label;
    @Autowired
    private EmailSender emailSender;
    private final BudgetItemsService budgetItemsService;
    private final Coalevel1Service sampleCoalevel1Service;
    
    public EmailSenderView(BudgetItemsService budgetItemsService,Coalevel1Service sampleCoalevel1Service) {
        this.budgetItemsService = budgetItemsService;
        this.sampleCoalevel1Service=sampleCoalevel1Service;
        //bug = setBudget();
        name = new EmailField("Your email");
        sayHello = new Button("Say hello");
        sett = new Button("Set bcat");
        label = new Text("Budget");
        sayHello.addClickListener(e -> {
            try {
                emailSender.sendEmail("Test Subject", "Test Message", new String[]{"methaltech77@gmail.com"});
                Notification.show("Email Sent " + name.getValue());
            } catch (Exception ex) {
                Notification.show("Email sending failed " + name.getValue() + " " + ex.getMessage());
            }
            
        });
        sett.addClickListener(e -> {
            changeRate();
        });
        
        sayHello.addClickShortcut(Key.ENTER);
        //label.setText(bug.getFinancial_year());

        setMargin(true);
        setVerticalComponentAlignment(Alignment.END, name, sayHello);
        this.setAlignItems(Alignment.BASELINE);
        add(name, sayHello, label, sett);
    }
    
    private void changeRate() {
        List<BudgetItems> findByDeptUnitAndBudget = budgetItemsService.findByAll();
        for (BudgetItems z : findByDeptUnitAndBudget) {
            z.setBcategory(z.getCoacode().getCode());
            int cat=Integer.parseInt(z.getCoacode().getCode().trim().substring(0,1));
            Coalevel1 coal = sampleCoalevel1Service.findByCode(cat);
            System.out.println(cat);
            z.setCoalevel1(coal);
            budgetItemsService.update(z);
        }
    }
}
