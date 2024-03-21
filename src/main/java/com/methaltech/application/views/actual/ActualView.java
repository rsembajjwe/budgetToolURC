package com.methaltech.application.views.actual;

import com.methaltech.application.data.Role;
import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.BudgetItemsActuals;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import com.methaltech.application.data.livedata.service.UrcDeptSectionAnlDimService;
import com.methaltech.application.security.AuthenticatedUser;
import com.methaltech.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@PageTitle("Actuals")
@Route(value = "actuals", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "PROCUREMENT", "BLO", "HOD"})
@Uses(Icon.class)
public class ActualView extends Div {

    private Grid<BudgetItemsActuals> gridBudgetItems = new Grid<>(BudgetItemsActuals.class, false);
    private final SALFLDGService sampleSALFLDGService;
    private final BudgetItemsService budgetItemsService;
    private AuthenticatedUser authenticatedUser;
    private final UserService samplePersonService;
    private final BudgetService sampleBudgetService;
    private final UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService;
    private User user;
    private ComboBox<Budget> budget = new ComboBox("Budget");
    private MultiSelectComboBox<UrcDeptSectionAnlDimbgt> comboBoxD_Section = new MultiSelectComboBox<>("Cost Centres");
    private DecimalFormat decimalFormat = new DecimalFormat("#,###.##");

    @Autowired
    public ActualView(SALFLDGService sampleSALFLDGService, BudgetItemsService budgetItemsService, AuthenticatedUser authenticatedUser,
            UserService samplePersonService, BudgetService sampleBudgetService, UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService) {
        this.sampleSALFLDGService = sampleSALFLDGService;
        this.budgetItemsService = budgetItemsService;
        this.authenticatedUser = authenticatedUser;
        this.samplePersonService = samplePersonService;
        this.sampleBudgetService = sampleBudgetService;
        this.sampleUrcDeptSectionAnlDimbgtService = sampleUrcDeptSectionAnlDimbgtService;

        addClassNames("actual-view");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        user = samplePersonService.getUserByEmail(username);
        if (authenticatedUser.get().isPresent()) {
            user = authenticatedUser.get().get();
        }

        Image image2 = new Image("images/ugflagstrip.png", "Strip");
        image2.setWidthFull();
        image2.getStyle().set("margin", "0").set("padding", "0");
        add(image2);

        budget.getStyle().set("marginRight", "10px");
        comboBoxD_Section.getStyle().set("marginRight", "10px");
        comboBoxD_Section.setItemLabelGenerator(UrcDeptSectionAnlDimbgt::getNAME);
        if (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT)) {
            comboBoxD_Section.setItems(sampleUrcDeptSectionAnlDimbgtService.getAllUrcSectionsAnlDims());
        } else {
            comboBoxD_Section.setItems(user.getDeptsection());
        }
        budget.setItemLabelGenerator(Budget::getFinancialYear);
        budget.setItems(query -> sampleBudgetService.list(PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query))).stream());
        Div div = new Div();
        div.add(budget, comboBoxD_Section);
        add(div);

        TabSheet tabSheet = new TabSheet();
        tabSheet.add("Monthly Actuals", budgetCoaView());
        tabSheet.add("Quarterly Actuals", new Div(new Text("A")));
        tabSheet.add("Annual Actuals", new Div(new Text("B")));

        tabSheet.addSelectedChangeListener(event -> {
            Component selectedTab = tabSheet.getSelectedTab();
            if (selectedTab != null) {
                // Do something with the selected tab
                System.out.println("Tab selected: " + selectedTab.getElement().getText());
                if (selectedTab.getElement().getText().equals("Monthly Actuals")) {
                    System.out.println("Grid for monthly");
                } else if (selectedTab.getElement().getText().equals("Quarterly Actuals")) {
                    System.out.println("Grid for Quarterly");
                } else if (selectedTab.getElement().getText().equals("Annual Actuals")) {
                    System.out.println("Grid for Annual");
                }
            }
        });
        setSizeFull();
        tabSheet.setSizeFull();
        tabSheet.setHeightFull();
        add(tabSheet);
        budget.addValueChangeListener(e -> {
            if (!comboBoxD_Section.isEmpty()) {
                gridBudgetItems.setItems(budgetItemsService.findDistinctBudgetItemses(budget.getValue(), comboBoxD_Section.getSelectedItems(), "FY2022-2023"));
            }
        });

        comboBoxD_Section.addValueChangeListener(e -> {
            if (!budget.isEmpty()) {
                gridBudgetItems.setItems(budgetItemsService.findDistinctBudgetItemses(budget.getValue(), comboBoxD_Section.getSelectedItems(), "FY2022-2023"));
            }
        });
    }

    private Div budgetCoaView() {
        Div div = new Div();
        div.setSizeFull();
        div.setSizeFull();
        gridBudgetItems.addColumn(budgetItem -> {
            COA coacode = budgetItem.getCoacode();
            Text label = new Text(coacode != null ? coacode.getCode() : "");
            return label.getText(); // Get the text content
        })
                .setHeader("Code").setWidth("80px").setFlexGrow(0)
                .setSortable(true) // Make the column sortable
                .setComparator((budgetItem1, budgetItem2) -> {
                    // Implement your custom comparator logic here
                    String name1 = budgetItem1.getCoacode() != null ? budgetItem1.getCoacode().getName() : "";
                    String name2 = budgetItem2.getCoacode() != null ? budgetItem2.getCoacode().getName() : "";
                    return name1.compareTo(name2);
                });
        gridBudgetItems.addColumn(BudgetItemsActuals::getItem).setHeader("Description");
        //gridBudgetItems.addColumn(BudgetItemsActuals::getJul).setHeader("July");
        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getJul();
            Span span = createSpan(value);

            return span;

        })).setHeader("Jul");
        Column<BudgetItemsActuals> julAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getJulA();
            Span span = createSpan(value);

            return span;

        })).setHeader("Jul Actuals");
// Apply the orange-column class to the cells of the "Jul Actuals" column
        julAColumn.setClassNameGenerator(item -> {
            // Check if the value is not null and greater than zero to apply the class
            if (item != null && item.getJulA() != null && item.getJulA().compareTo(BigDecimal.ZERO) > 0) {
                return "orange-column";
            }
            return "orange-column"; // No class applied if condition is not met
        });

        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getAug();
            Span span = createSpan(value);

            return span;

        })).setHeader("Aug");

        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getAugA();
            Span span = createSpan(value);

            return span;

        })).setHeader("Aug Actuals");
        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getSep();
            Span span = createSpan(value);

            return span;

        })).setHeader("Sep");
        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getSepA();
            Span span = createSpan(value);

            return span;

        })).setHeader("Sep Actuals");
        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getOct();
            Span span = createSpan(value);

            return span;

        })).setHeader("Oct");
        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getOctA();
            Span span = createSpan(value);

            return span;

        })).setHeader("Oct Actuals");
        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getNov();
            Span span = createSpan(value);

            return span;

        })).setHeader("Nov");

        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getNovA();
            Span span = createSpan(value);

            return span;

        })).setHeader("Nov Actuals");
        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getDec();
            Span span = createSpan(value);

            return span;

        })).setHeader("Dec");

        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getDecA();
            Span span = createSpan(value);

            return span;

        })).setHeader("Dec Actuals");
        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getJan();
            Span span = createSpan(value);

            return span;

        })).setHeader("Jan");

        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getJanA();
            Span span = createSpan(value);

            return span;

        })).setHeader("Jan Actuals");
        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getFeb();
            Span span = createSpan(value);

            return span;

        })).setHeader("Feb");

        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getFebA();
            Span span = createSpan(value);

            return span;

        })).setHeader("Feb Actuals");
        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getMar();
            Span span = createSpan(value);

            return span;

        })).setHeader("Mar");

        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getMarA();
            Span span = createSpan(value);

            return span;

        })).setHeader("Mar Actuals");
        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getApr();
            Span span = createSpan(value);

            return span;

        })).setHeader("Apr");

        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getAprA();
            Span span = createSpan(value);

            return span;

        })).setHeader("Apr Actuals");
        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getMay();
            Span span = createSpan(value);

            return span;

        })).setHeader("May");

        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getMayA();
            Span span = createSpan(value);

            return span;

        })).setHeader("May Actuals");
        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getJun();
            Span span = createSpan(value);

            return span;

        })).setHeader("Jun");

        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getJunA();
            Span span = createSpan(value);

            return span;

        })).setHeader("Jun Actuals");
        gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getTotal();
            Span span = createSpan(value);
            span.getElement().getThemeList().add("badge success");

            return span;

        })).setHeader("Total").setFlexGrow(0).setWidth("150px");

        gridBudgetItems.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);
        //gridBudgetItems.setHeight("900px");
        div.add(gridBudgetItems);
        return div;
    }

    private Span createSpan(BigDecimal value) {
        Span span;
        if (value != null) {
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            span = new Span(decimalFormat.format(value));
        } else {
            span = new Span("-");
        }
        return span;
    }
}
