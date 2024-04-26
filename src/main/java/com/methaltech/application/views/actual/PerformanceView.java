package com.methaltech.application.views.actual;

import com.methaltech.application.data.PeriodExtractor;
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
import com.vaadin.flow.component.notification.Notification;
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

@PageTitle("Performance")
@Route(value = "performance", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "PROCUREMENT", "BLO", "HOD"})
@Uses(Icon.class)
public class PerformanceView extends Div {

    private Grid<BudgetItemsActuals> gridBudgetItems = new Grid<>(BudgetItemsActuals.class, false);
    private Grid<BudgetItemsActuals> gridBudgetItemsQuarterlyGrid = new Grid<>(BudgetItemsActuals.class, false);
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
    PeriodExtractor extra = new PeriodExtractor();

    Span julSpan = new Span();
    Span julActualSpan = new Span();
    Span augSpan = new Span();
    Span augActualSpan = new Span();
    Span sepSpan = new Span();
    Span sepActualSpan = new Span();
    Span octSpan = new Span();
    Span octActualSpan = new Span();
    Span novSpan = new Span();
    Span novActualSpan = new Span();
    Span decSpan = new Span();
    Span decActualSpan = new Span();
    Span janSpan = new Span();
    Span janActualSpan = new Span();
    Span febSpan = new Span();
    Span febActualSpan = new Span();
    Span marSpan = new Span();
    Span marActualSpan = new Span();
    Span aprSpan = new Span();
    Span aprActualSpan = new Span();
    Span maySpan = new Span();
    Span mayActualSpan = new Span();
    Span junSpan = new Span();
    Span junActualSpan = new Span();
    Span totalSpan = new Span();
    Span totalActualSpan = new Span();    

    Span qtr1Span = new Span();
    Span qtr1ActualSpan = new Span();
    Span qtr2Span = new Span();
    Span qtr2ActualSpan = new Span();
    Span qtr3Span = new Span();
    Span qtr3ActualSpan = new Span();
    Span qtr4Span = new Span();
    Span qtr4ActualSpan = new Span();
    Span totalQtrSpan = new Span();
    Span totalQtrActualSpan = new Span();       

    Column<BudgetItemsActuals> julColumn;
    Column<BudgetItemsActuals> julAColumn;
    Column<BudgetItemsActuals> augColumn;
    Column<BudgetItemsActuals> augAColumn;
    Column<BudgetItemsActuals> sepColumn;
    Column<BudgetItemsActuals> sepAColumn;
    Column<BudgetItemsActuals> octColumn;
    Column<BudgetItemsActuals> octAColumn;
    Column<BudgetItemsActuals> novColumn;
    Column<BudgetItemsActuals> novAColumn;
    Column<BudgetItemsActuals> decColumn;
    Column<BudgetItemsActuals> decAColumn;
    Column<BudgetItemsActuals> janColumn;
    Column<BudgetItemsActuals> janAColumn;
    Column<BudgetItemsActuals> febColumn;
    Column<BudgetItemsActuals> febAColumn;
    Column<BudgetItemsActuals> marColumn;
    Column<BudgetItemsActuals> marAColumn;
    Column<BudgetItemsActuals> aprColumn;
    Column<BudgetItemsActuals> aprAColumn;
    Column<BudgetItemsActuals> mayColumn;
    Column<BudgetItemsActuals> mayAColumn;
    Column<BudgetItemsActuals> junColumn;
    Column<BudgetItemsActuals> junAColumn;
    Column<BudgetItemsActuals> totalColumn;
    Column<BudgetItemsActuals> totalAColumn;    

    Column<BudgetItemsActuals> qtr1Column;
    Column<BudgetItemsActuals> qtr1AColumn;
    Column<BudgetItemsActuals> qtr2Column;
    Column<BudgetItemsActuals> qtr2AColumn;
    Column<BudgetItemsActuals> qtr3Column;
    Column<BudgetItemsActuals> qtr3AColumn;
    Column<BudgetItemsActuals> qtr4Column;
    Column<BudgetItemsActuals> qtr4AColumn;
    Column<BudgetItemsActuals> totalQtrColumn;
    Column<BudgetItemsActuals> totalAQtrColumn;

    @Autowired

    public PerformanceView(SALFLDGService sampleSALFLDGService, BudgetItemsService budgetItemsService, AuthenticatedUser authenticatedUser,
            UserService samplePersonService, BudgetService sampleBudgetService, UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService) {
        this.sampleSALFLDGService = sampleSALFLDGService;
        this.budgetItemsService = budgetItemsService;
        this.authenticatedUser = authenticatedUser;
        this.samplePersonService = samplePersonService;
        this.sampleBudgetService = sampleBudgetService;
        this.sampleUrcDeptSectionAnlDimbgtService = sampleUrcDeptSectionAnlDimbgtService;
        //this.setSizeFull();
        gridBudgetItems.setHeightFull();

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
        tabSheet.add("Monthly Budget Vs Actuals", budgetCoaView());
        tabSheet.add("Quarterly Budget Vs Actuals", budgetCoaQtrView());
        setSpanValues();
        setSpanQtrValues();

        tabSheet.addSelectedChangeListener(event -> {
            Component selectedTab = tabSheet.getSelectedTab();
            if (selectedTab != null) {
                // Do something with the selected tab
                System.out.println("Tab selected: " + selectedTab.getElement().getText());
                if (selectedTab.getElement().getText().equals("Monthly Budget Vs Actuals")) {
                    /*                    selectedTab.getElement().setText("Monthly Budget Vs Actuals FY");
                    System.out.println("Grid for monthly");*/
                } else if (selectedTab.getElement().getText().equals("Quarterly Budget Vs Actuals")) {
                    if (!budget.isEmpty()) {
                        // gridBudgetItemsQuarterlyGrid.
                        //gridBudgetItemsQuarterlyGrid.setItems(budgetItemsService.findDistinctBudgetItemses(budget.getValue(), comboBoxD_Section.getSelectedItems()));
                    }
                } else if (selectedTab.getElement().getText().equals("Annual Budget Vs Actuals")) {
                    /*                    selectedTab.getElement().setText("Annual Budget Vs Actuals FY");
                    System.out.println("Grid for Annual");*/
                }
            }
        });
        setSizeFull();
        tabSheet.setSizeFull();
        tabSheet.setHeightFull();
        add(tabSheet);
        budget.addValueChangeListener(e -> {
            setSpanValues();
            setSpanQtrValues();

            if (!comboBoxD_Section.isEmpty()) {
                gridBudgetItems.setItems(budgetItemsService.findDistinctBudgetItemses2(budget.getValue(), comboBoxD_Section.getSelectedItems()));
                gridBudgetItemsQuarterlyGrid.setItems(budgetItemsService.findDistinctBudgetItemses2(budget.getValue(), comboBoxD_Section.getSelectedItems()));
            }
        });

        comboBoxD_Section.addValueChangeListener(e -> {
            if (!budget.isEmpty()) {
                gridBudgetItems.setItems(budgetItemsService.findDistinctBudgetItemses2(budget.getValue(), comboBoxD_Section.getSelectedItems()));
                gridBudgetItemsQuarterlyGrid.setItems(budgetItemsService.findDistinctBudgetItemses2(budget.getValue(), comboBoxD_Section.getSelectedItems()));
            }
        });
    }

    private Div budgetCoaView() {
        Div div = new Div();
        div.setSizeFull();
        div.setSizeFull();
        gridBudgetItems.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);
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

        julColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getJul();
            Span span = createSpan(value);

            return span;

        })).setHeader(julSpan).setWidth("150px");
        julAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getJulA();
            Span span = createSpan(value);
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader(julActualSpan).setWidth("150px");
// Apply the orange-column class to the cells of the "Jul Actuals" column
        julColumn.setClassNameGenerator(item -> {
            // Check if the value is not null and greater than zero to apply the class
            if (item != null && item.getJulA() != null && item.getJulA().compareTo(BigDecimal.ZERO) > 0) {
                return "orange-column";
            }
            return "orange-column"; // No class applied if condition is not met
        });

        augColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getAug();
            Span span = createSpan(value);

            return span;

        })).setHeader(augSpan).setWidth("150px");

        augAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getAugA();
            Span span = createSpan(value);
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader(augActualSpan).setWidth("150px");
        sepColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getSep();
            Span span = createSpan(value);

            return span;

        })).setHeader(sepSpan).setWidth("150px");
        sepAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getSepA();
            Span span = createSpan(value);
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader(sepActualSpan).setWidth("150px");
        octColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getOct();
            Span span = createSpan(value);

            return span;

        })).setHeader(octSpan).setWidth("150px");
        octAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getOctA();
            Span span = createSpan(value);
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader(octActualSpan).setWidth("150px");
        novColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getNov();
            Span span = createSpan(value);

            return span;

        })).setHeader(novSpan).setWidth("150px");

        novAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getNovA();
            Span span = createSpan(value);
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader(novActualSpan).setWidth("150px");
        decColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getDec();
            Span span = createSpan(value);

            return span;

        })).setHeader(decSpan).setWidth("150px");

        decAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getDecA();
            Span span = createSpan(value);
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader(decActualSpan).setWidth("150px");
        janColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getJan();
            Span span = createSpan(value);

            return span;

        })).setHeader(janSpan).setWidth("150px");

        janAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getJanA();
            Span span = createSpan(value);
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader(janActualSpan).setWidth("150px");
        febColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getFeb();
            Span span = createSpan(value);

            return span;

        })).setHeader(febSpan).setWidth("150px");

        febAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getFebA();
            Span span = createSpan(value);
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader(febActualSpan).setWidth("150px");
        marColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getMar();
            Span span = createSpan(value);

            return span;

        })).setHeader(marSpan).setWidth("150px");

        marAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getMarA();
            Span span = createSpan(value);
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader(marActualSpan).setWidth("150px");
        aprColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getApr();
            Span span = createSpan(value);

            return span;

        })).setHeader(aprSpan).setWidth("150px");

        aprAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getAprA();
            Span span = createSpan(value);
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader(aprActualSpan).setWidth("150px");
        mayColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getMay();
            Span span = createSpan(value);

            return span;

        })).setHeader(maySpan).setWidth("150px");

        mayAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getMayA();
            Span span = createSpan(value);
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader(mayActualSpan).setWidth("150px");
        julColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getJun();
            Span span = createSpan(value);

            return span;

        })).setHeader(junSpan).setWidth("150px");

        junAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getJunA();
            Span span = createSpan(value);
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader(junActualSpan).setWidth("150px");
       totalColumn= gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getTotal();
            Span span = createSpan(value);
            span.getElement().getThemeList().add("badge success");

            return span;

        })).setHeader("Total").setFlexGrow(0).setWidth("150px");

        totalAColumn=gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getTotalA();
            Span span = createSpan(value);
            span.getElement().getThemeList().add("badge success");

            return span;

        })).setHeader("Total Actual").setFlexGrow(0).setWidth("150px");

        gridBudgetItems.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);
        //gridBudgetItems.setHeight("900px");
        div.add(gridBudgetItems);
        return div;
    }

    private Div budgetCoaQtrView() {
        Div div = new Div();
        div.setSizeFull();
        div.setSizeFull();
        gridBudgetItemsQuarterlyGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);
        gridBudgetItemsQuarterlyGrid.addColumn(budgetItem -> {
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
        gridBudgetItemsQuarterlyGrid.addColumn(BudgetItemsActuals::getItem).setHeader("Description");
        //gridBudgetItems.addColumn(BudgetItemsActuals::getJul).setHeader("July");

        qtr1Column = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getQtr1();
            Span span = createSpan(value);

            return span;

        })).setHeader(qtr1Span).setWidth("150px");
        qtr1AColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getQtr1A();
            Span span = createSpan(value);
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader(qtr1ActualSpan).setWidth("150px");
// Apply the orange-column class to the cells of the "Jul Actuals" column
        qtr1AColumn.setClassNameGenerator(item -> {
            // Check if the value is not null and greater than zero to apply the class
            if (item != null && item.getJulA() != null && item.getJulA().compareTo(BigDecimal.ZERO) > 0) {
                return "orange-column";
            }
            return "orange-column"; // No class applied if condition is not met
        });

        qtr2Column = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getQtr2();
            Span span = createSpan(value);

            return span;

        })).setHeader(qtr2Span).setWidth("150px");

        qtr2AColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getQtr2A();
            Span span = createSpan(value);
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader(qtr2ActualSpan).setWidth("150px");
        qtr3Column = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getQtr3();
            Span span = createSpan(value);

            return span;

        })).setHeader(qtr3Span).setWidth("150px");
        qtr3AColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getQtr3A();
            Span span = createSpan(value);
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader(qtr3ActualSpan).setWidth("150px");
        qtr4Column = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getQtr4();
            Span span = createSpan(value);

            return span;

        })).setHeader(qtr4Span).setWidth("150px");
        qtr4AColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getQtr4A();
            Span span = createSpan(value);
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader(qtr4ActualSpan).setWidth("150px");
        totalQtrColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getTotal();
            Span span = createSpan(value);

            return span;

        })).setHeader("Total").setWidth("150px");

        totalAQtrColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getTotalA();
            Span span = createSpan(value);
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader("Total Actual").setWidth("150px");

        gridBudgetItemsQuarterlyGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);
        //gridBudgetItems.setHeight("900px");
        div.add(gridBudgetItemsQuarterlyGrid);
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

    private String yearString(String fy, String month) {
        if (budget.isEmpty()) {
            return month;
        } else {
            return month + " (" + extra.generateYear(fy, month.substring(0, 3)) + ")";
        }
    }
    private String yearString2(String fy, String month) {
        if (budget.isEmpty()) {
            return month;
        } else {
            int y=extra.generateYear(fy, month.substring(0, 3))-1;
            System.out.println(""+y);
            return month + " (" + y + ")";
        }
    }    

    private String setFY(Budget budget) {
        if (budget == null) {
            return "";
        } else {
            return budget.getFinancialYear();
        }
    }

    private void setSpanValues() {
        julSpan.setText(yearString(setFY(budget.getValue()), "Jul"));
        julActualSpan.setText(yearString2(setFY(budget.getValue()), "Jul Actual"));
        augSpan.setText(yearString(setFY(budget.getValue()), "Aug"));
        augActualSpan.setText(yearString2(setFY(budget.getValue()), "Aug Actual"));
        sepSpan.setText(yearString(setFY(budget.getValue()), "Sep"));
        sepActualSpan.setText(yearString2(setFY(budget.getValue()), "Sep Actual"));
        octSpan.setText(yearString(setFY(budget.getValue()), "Oct"));
        octActualSpan.setText(yearString2(setFY(budget.getValue()), "Oct Actual"));
        novSpan.setText(yearString(setFY(budget.getValue()), "Nov"));
        novActualSpan.setText(yearString2(setFY(budget.getValue()), "Nov Actual"));
        decSpan.setText(yearString(setFY(budget.getValue()), "Dec"));
        decActualSpan.setText(yearString2(setFY(budget.getValue()), "Dec Actual"));
        janSpan.setText(yearString(setFY(budget.getValue()), "Jan"));
        janActualSpan.setText(yearString2(setFY(budget.getValue()), "Jan Actual"));
        febSpan.setText(yearString(setFY(budget.getValue()), "Feb"));
        febActualSpan.setText(yearString2(setFY(budget.getValue()), "Feb Actual"));
        marSpan.setText(yearString(setFY(budget.getValue()), "Mar"));
        marActualSpan.setText(yearString2(setFY(budget.getValue()), "Mar Actual"));
        aprSpan.setText(yearString(setFY(budget.getValue()), "Apr"));
        aprActualSpan.setText(yearString2(setFY(budget.getValue()), "Apr Actual"));
        maySpan.setText(yearString(setFY(budget.getValue()), "May"));
        mayActualSpan.setText(yearString2(setFY(budget.getValue()), "May Actual"));
        junSpan.setText(yearString(setFY(budget.getValue()), "Jun"));
        junActualSpan.setText(yearString2(setFY(budget.getValue()), "Jun Actual"));
       // totalSpan.setText(yearString(setFY(budget.getValue()), "Total"));
       // totalActualSpan.setText(yearString2(setFY(budget.getValue()), "Total Actual"));        
    }

    private void setSpanQtrValues() {
        qtr1Span.setText(yearString(setFY(budget.getValue()), "Qr1"));
        qtr1ActualSpan.setText(yearString2(setFY(budget.getValue()), "Qr1 Actual"));
        qtr2Span.setText(yearString(setFY(budget.getValue()), "Qr2"));
        qtr2ActualSpan.setText(yearString2(setFY(budget.getValue()), "Qr2 Actual"));
        qtr3Span.setText(yearString(setFY(budget.getValue()), "Qr3"));
        qtr3ActualSpan.setText(yearString2(setFY(budget.getValue()), "Qr3 Actual"));
        qtr4Span.setText(yearString(setFY(budget.getValue()), "Qr4"));
        qtr4ActualSpan.setText(yearString2(setFY(budget.getValue()), "Qr4 Actual"));
        //totalQtrSpan.setText(yearString(setFY(budget.getValue()), "Total"));
        //totalQtrActualSpan.setText(yearString2(setFY(budget.getValue()), "Total Actual"));          
    }
}
