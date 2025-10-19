package com.methaltech.application.views;

import com.methaltech.application.data.Role;
import com.methaltech.application.data.bgtool.service.NdpPlanService;
import com.methaltech.application.data.bgtool.service.PriorityAreaService;
import com.methaltech.application.data.bgtool.service.StrategicObjectiveService;
import com.methaltech.application.data.bgtool.service.URC_Priority_AreasService;
import com.methaltech.application.data.bgtool.service.UrcStrategicObjectivesService;
import com.methaltech.application.data.bgtool.service.UrcStrategicPlanService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.NdpPlan;
import com.methaltech.application.data.entity.bgtool.PriorityArea;
import com.methaltech.application.data.entity.bgtool.StrategicObjective;
import com.methaltech.application.data.entity.bgtool.URC_Priority_Areas;
import com.methaltech.application.data.entity.bgtool.UrcStrategicObjectives;
import com.methaltech.application.data.entity.bgtool.UrcStrategicPlan;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.security.AuthenticatedUser;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@PageTitle("NDP Plan Management")
@Route(value = "ndp-management", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "BLO", "USER", "HOD"})
@Uses(Icon.class)
public class NdpPlanManagementView extends VerticalLayout {

    private final NdpPlanService ndpPlanService;
    private final UrcStrategicPlanService urcStrategicPlanService;
    private final URC_Priority_AreasService uRC_Priority_AreasService;
    private final UrcStrategicObjectivesService urcStrategicObjectivesService;
    private final UserService userService;
    private final AuthenticatedUser authenticatedUser;
    private final PriorityAreaService priorityAreaService;
    private final StrategicObjectiveService strategicObjectiveService;

    private final Grid<NdpPlan> planGrid = new Grid<>(NdpPlan.class, false);
    private final Grid<PriorityArea> priorityGrid = new Grid<>(PriorityArea.class, false);
    private final Grid<StrategicObjective> objectiveGrid = new Grid<>(StrategicObjective.class, false);

    private final Grid<URC_Priority_Areas> priorityGridUrc = new Grid<>(URC_Priority_Areas.class, false);
    private final Grid<UrcStrategicObjectives> objectiveGridUrc = new Grid<>(UrcStrategicObjectives.class, false);

    private ComboBox<NdpPlan> comboBoxNdpPlan = new ComboBox<>();
    private ComboBox<UrcStrategicPlan> comboBoxurcPlan = new ComboBox<>();

    private NdpPlan selectedPlan;
    private UrcStrategicPlan selectedUrcPlan;
    private final TextField name = new TextField("Name");
    private final TextField theme = new TextField("Theme");
    private final TextField ultimateGoal = new TextField("Ultimate Goal");
    private final DatePicker startDate = new DatePicker("Start Date");
    private final DatePicker endDate = new DatePicker("End Date");
    private final Binder<NdpPlan> binder = new Binder<>(NdpPlan.class);

    private final TextField nameUrc = new TextField("Name");
    private final TextField themeUrc = new TextField("Theme");
    private final TextField ultimateGoalUrc = new TextField("Ultimate Goal");
    private final DatePicker startDateUrc = new DatePicker("Start Date");
    private final DatePicker endDateUrc = new DatePicker("End Date");
    private final Binder<UrcStrategicPlan> binderUrc = new Binder<>(UrcStrategicPlan.class);

    private final Span themeValue = new Span("");
    private final Span goalValue = new Span("");
    private final Span ndpTimeRange = new Span("");

    private final Span themeUrcValue = new Span("");
    private final Span goalUrcValue = new Span("");
    private final Span urcTimeRange = new Span("");
    private User user;

    public NdpPlanManagementView(NdpPlanService ndpPlanService, UrcStrategicPlanService urcStrategicPlanService,
            PriorityAreaService priorityAreaService, URC_Priority_AreasService uRC_Priority_AreasService,
            StrategicObjectiveService strategicObjectiveService, UrcStrategicObjectivesService urcStrategicObjectivesService, UserService userService, AuthenticatedUser authenticatedUser) {
        this.ndpPlanService = ndpPlanService;
        this.priorityAreaService = priorityAreaService;
        this.strategicObjectiveService = strategicObjectiveService;
        this.userService = userService;
        this.authenticatedUser = authenticatedUser;
        this.urcStrategicPlanService = urcStrategicPlanService;
        this.uRC_Priority_AreasService = uRC_Priority_AreasService;
        this.urcStrategicObjectivesService = urcStrategicObjectivesService;
        binder.bindInstanceFields(this);
        binderUrc.bindInstanceFields(this);

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setUser();

        add(createHeader());
        // Tabs
        Tabs tabs = new Tabs();
        Tab ndpTab = new Tab("National Development Plan");
        Tab urcTab = new Tab("URC Plan");
        tabs.add(ndpTab, urcTab);

        // Tab content containers
        Div ndpContent = new Div(createNdpContent());
        Div urcContent = new Div(createUrcContent());
        // urcContent.setText("URC Plan content will be added here.");
        urcContent.setVisible(false);

        Map<Tab, Component> tabsToPages = new HashMap<>();
        tabsToPages.put(ndpTab, ndpContent);
        tabsToPages.put(urcTab, urcContent);

        Div pages = new Div(ndpContent, urcContent);
        pages.setSizeFull();

        tabs.addSelectedChangeListener(event -> {
            tabsToPages.values().forEach(page -> page.setVisible(false));
            Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
            selectedPage.setVisible(true);
        });

        add(tabs, pages);
        updatePlans();
    }

    private void setUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        user = userService.getUserByEmail(username);
        authenticatedUser.get().ifPresent(u -> user = u);
    }

    private Component createHeader() {
        H2 title = new H2("URC Strategic Plan Alignment To Uganda’s National Development Plan");
        title.getStyle()
                .set("font-weight", "600") // slightly lighter
                .set("font-size", "1.2rem") // smaller font
                .set("color", "#1a237e")
                .set("margin", "0")
                .set("text-align", "center")
                .set("font-family", "'Segoe UI', Roboto, sans-serif")
                .set("line-height", "1.2");        // reduce line height

        HorizontalLayout headerLayout = new HorizontalLayout(title);
        headerLayout.setWidthFull();
        headerLayout.setHeight("40px");             // smaller height
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.getStyle()
                .set("background-color", "#e8eaf6")
                .set("padding", "0.4rem 0.8rem") // smaller padding
                .set("border-radius", "6px")
                .set("box-shadow", "0 1px 3px rgba(0,0,0,0.08)"); // lighter shadow

        return headerLayout;
    }

    // ---------------- NDP Tab Content ----------------
    private Component createNdpContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);

        layout.add(createBudgetHeader(), createMainHeader(), createMainLayout());
        return layout;
    }

    // ---------------- NDP Tab Content ----------------
    private Component createUrcContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);

        // layout.add(createUrcBudgetHeader(), createUrcMainHeader(), createUrcMainLayout());
        layout.add(createUrcBudgetHeader(), createUrcMainHeader(), createUrcMainLayout());
        return layout;
    }

    private Component createBudgetHeader() {
        comboBoxNdpPlan.setItems(ndpPlanService.findAllWithDetails());
        comboBoxNdpPlan.setItemLabelGenerator(NdpPlan::getName);
        comboBoxNdpPlan.setPlaceholder("Select NDP Plan");
        comboBoxNdpPlan.setWidth("280px");
        comboBoxNdpPlan.getStyle().set("font-size", "0.9rem");

        comboBoxNdpPlan.addValueChangeListener(e -> {
            selectedPlan = e.getValue();
            selectPlan(selectedPlan);
            if (selectedPlan != null) {
                themeValue.setText(selectedPlan.getTheme());
                goalValue.setText(selectedPlan.getUltimateGoal());
                ndpTimeRange.setText(selectedPlan.getStartDate() + " to " + selectedPlan.getEndDate());
            }
        });

        Button addNdpPlan = new Button("Add/Edit Plan", e -> {
            if (comboBoxNdpPlan.isEmpty()) {
                openPlanDialog(new NdpPlan());
            } else {
                openPlanDialog(comboBoxNdpPlan.getValue());
            }
        });
        addNdpPlan.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addNdpPlan.getStyle()
                .set("font-size", "0.85rem")
                .set("padding", "0.3rem 0.8rem")
                .set("border-radius", "6px");
        addNdpPlan.setVisible(user.getRoles().contains(Role.ADMIN));

        HorizontalLayout headerLayout = new HorizontalLayout(comboBoxNdpPlan, addNdpPlan);
        headerLayout.setWidthFull();
        headerLayout.setSpacing(true);
        headerLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        headerLayout.getStyle()
                .set("background-color", "#f5f6fa")
                .set("padding", "0.5rem 1rem")
                .set("border-radius", "6px")
                .set("box-shadow", "0 1px 3px rgba(0,0,0,0.05)");

        return headerLayout;
    }

    private Component createUrcBudgetHeader() {
        comboBoxurcPlan.setItems(urcStrategicPlanService.findAll());
        comboBoxurcPlan.setItemLabelGenerator(UrcStrategicPlan::getName);
        comboBoxurcPlan.setPlaceholder("Select URC Strategic Plan");
        comboBoxurcPlan.setWidth("280px");
        comboBoxurcPlan.getStyle().set("font-size", "0.9rem");

        comboBoxurcPlan.addValueChangeListener(e -> {
            selectedUrcPlan = e.getValue();
            selectUrcPlan(selectedUrcPlan);
            if (selectedUrcPlan != null) {
                themeUrcValue.setText(selectedUrcPlan.getTheme());
                goalUrcValue.setText(selectedUrcPlan.getUltimateGoal());
                urcTimeRange.setText(selectedUrcPlan.getStartDate() + " to " + selectedUrcPlan.getEndDate());
            }
        });

        Button addNdpPlan = new Button("Add/Edit Plan", e -> {
            if (comboBoxurcPlan.isEmpty()) {
                openUrcPlanDialog(new UrcStrategicPlan());
            } else {
                openUrcPlanDialog(comboBoxurcPlan.getValue());
            }
        });
        addNdpPlan.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addNdpPlan.getStyle()
                .set("font-size", "0.85rem")
                .set("padding", "0.3rem 0.8rem")
                .set("border-radius", "6px");
        addNdpPlan.setVisible(user.getRoles().contains(Role.ADMIN));

        HorizontalLayout headerLayout = new HorizontalLayout(comboBoxurcPlan, addNdpPlan);
        headerLayout.setWidthFull();
        headerLayout.setSpacing(true);
        headerLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        headerLayout.getStyle()
                .set("background-color", "#f5f6fa")
                .set("padding", "0.5rem 1rem")
                .set("border-radius", "6px")
                .set("box-shadow", "0 1px 3px rgba(0,0,0,0.05)");

        return headerLayout;
    }

    private Component createMainHeader() {
        // Labels and values
        H4 themeLabel = new H4("Theme: ");
        themeLabel.getStyle().set("margin", "0");
        themeValue.getStyle().set("margin", "0").set("font-weight", "300");

        H4 goalLabel = new H4("Ultimate Goal: ");
        goalLabel.getStyle().set("margin", "0");
        goalValue.getStyle().set("margin", "0").set("font-weight", "300");

        H4 ndpTimeRangeLabel = new H4("Duration: ");
        ndpTimeRangeLabel.getStyle().set("margin", "0");
        ndpTimeRange.getStyle().set("margin", "0").set("font-weight", "300");

        // HorizontalLayouts with very small spacing
        HorizontalLayout themeLayout = new HorizontalLayout(themeLabel, themeValue);
        themeLayout.setSpacing(true); // default spacing is fine, can use false if too wide
        themeLayout.setPadding(false);
        themeLayout.setMargin(false);

        HorizontalLayout goalLayout = new HorizontalLayout(goalLabel, goalValue);
        goalLayout.setSpacing(true);
        goalLayout.setPadding(false);
        goalLayout.setMargin(false);

        HorizontalLayout durationLayout = new HorizontalLayout(ndpTimeRangeLabel, ndpTimeRange);
        durationLayout.setSpacing(true);
        durationLayout.setPadding(false);
        durationLayout.setMargin(false);

        // FlexLayout for responsiveness
        FlexLayout headerLayout = new FlexLayout(themeLayout, goalLayout, durationLayout);
        headerLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP); // allow wrapping on small screens
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.setWidthFull();
        headerLayout.getStyle().set("gap", "1rem"); // smaller gap between items
        headerLayout.getStyle().set("row-gap", "0.2rem"); // minimal vertical spacing

        return headerLayout;
    }

    private Component createUrcMainHeader() {
        // Labels and values
        H4 themeLabel = new H4("Theme: ");
        themeLabel.getStyle().set("margin", "0");
        themeUrcValue.getStyle().set("margin", "0").set("font-weight", "300");

        H4 goalLabel = new H4("Ultimate Goal: ");
        goalLabel.getStyle().set("margin", "0");
        goalUrcValue.getStyle().set("margin", "0").set("font-weight", "300");

        H4 ndpTimeRangeLabel = new H4("Duration: ");
        ndpTimeRangeLabel.getStyle().set("margin", "0");
        urcTimeRange.getStyle().set("margin", "0").set("font-weight", "300");

        // HorizontalLayouts with very small spacing
        HorizontalLayout themeLayout = new HorizontalLayout(themeLabel, themeUrcValue);
        themeLayout.setSpacing(true); // default spacing is fine, can use false if too wide
        themeLayout.setPadding(false);
        themeLayout.setMargin(false);

        HorizontalLayout goalLayout = new HorizontalLayout(goalLabel, goalUrcValue);
        goalLayout.setSpacing(true);
        goalLayout.setPadding(false);
        goalLayout.setMargin(false);

        HorizontalLayout durationLayout = new HorizontalLayout(ndpTimeRangeLabel, urcTimeRange);
        durationLayout.setSpacing(true);
        durationLayout.setPadding(false);
        durationLayout.setMargin(false);

        // FlexLayout for responsiveness
        FlexLayout headerLayout = new FlexLayout(themeLayout, goalLayout, durationLayout);
        headerLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP); // allow wrapping on small screens
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.setWidthFull();
        headerLayout.getStyle().set("gap", "1rem"); // smaller gap between items
        headerLayout.getStyle().set("row-gap", "0.2rem"); // minimal vertical spacing

        return headerLayout;
    }

    private Component createMainLayout() {
        // Left: NDP Plan Grid
        planGrid.addColumn(NdpPlan::getName).setHeader("Plan Name").setAutoWidth(true);
        planGrid.addColumn(NdpPlan::getStartDate).setHeader("Start Date");
        planGrid.addColumn(NdpPlan::getEndDate).setHeader("End Date");
        planGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        /*        planGrid.addSelectionListener(e -> {
        
        e.getFirstSelectedItem().ifPresent(this::selectPlan);
        });*/
        planGrid.asSingleSelect().addValueChangeListener(e -> {

            selectedPlan = e.getValue();
            selectPlan(selectedPlan);
        });

        Button addPlanBtn = new Button("Add Plan", e -> openPlanDialog(new NdpPlan()));
        VerticalLayout planLayout = new VerticalLayout(new H3("NDP Plans"), planGrid, addPlanBtn);
        planLayout.setWidth("35%");
        planLayout.setSpacing(false);
        planLayout.setPadding(false);

        // Center: Strategic Objectives
        //objectiveGrid.addColumn(StrategicObjective::getTitle).setHeader("Code").setAutoWidth(true);
        objectiveGrid.addColumn(object
                -> objectiveGrid.getListDataView().getItems()
                        .collect(java.util.stream.Collectors.toList())
                        .indexOf(object) + 1
        ).setHeader("#").setAutoWidth(true).setFlexGrow(0);        
        objectiveGrid.addColumn(StrategicObjective::getDescription).setHeader("NDP Objectives").setAutoWidth(true);
        Button addObjectiveBtn = new Button("Add Objective", e -> openObjectiveDialog(new StrategicObjective()));
        VerticalLayout objectiveLayout = new VerticalLayout(new H3("Strategic Objectives"), objectiveGrid, addObjectiveBtn);
        objectiveLayout.setWidth("35%");

        // Right: Priority Areas
        // priorityGrid.addColumn(PriorityArea::getName).setHeader("Priority Area").setAutoWidth(true);
                priorityGrid.addColumn(object
                -> priorityGrid.getListDataView().getItems()
                        .collect(java.util.stream.Collectors.toList())
                        .indexOf(object) + 1
        ).setHeader("#").setAutoWidth(true).setFlexGrow(0);
        priorityGrid.addColumn(PriorityArea::getDescription).setHeader("NDP Programmes").setAutoWidth(true);
        Button addPriorityBtn = new Button("Add Priority Area", e -> openPriorityDialog(new PriorityArea()));
        VerticalLayout priorityLayout = new VerticalLayout(new H3("Priority Areas"), priorityGrid, addPriorityBtn);
        priorityLayout.setWidth("65%");

        // Combine in a HorizontalLayout
        // HorizontalLayout layout = new HorizontalLayout(planLayout, objectiveLayout, priorityLayout);
        HorizontalLayout layout = new HorizontalLayout(objectiveLayout, priorityLayout);
        layout.setSizeFull();
        layout.setSpacing(true);
        return layout;
    }

    private Component createUrcMainLayout() {
        objectiveGridUrc.addColumn(object
                -> objectiveGridUrc.getListDataView().getItems()
                        .collect(java.util.stream.Collectors.toList())
                        .indexOf(object) + 1
        ).setHeader("#").setAutoWidth(true).setFlexGrow(0);

        objectiveGridUrc.addColumn(UrcStrategicObjectives::getObjective).setHeader("Description").setAutoWidth(true);
        Button addObjectiveBtn = new Button("Add Objective", e -> openObjectiveDialog(new StrategicObjective()));
        VerticalLayout objectiveLayout = new VerticalLayout(new H3("Strategic Objectives"), objectiveGridUrc, addObjectiveBtn);
        objectiveLayout.setWidth("35%");

        // Right: Priority Areas
        priorityGridUrc.addColumn(object
                -> priorityGridUrc.getListDataView().getItems()
                        .collect(java.util.stream.Collectors.toList())
                        .indexOf(object) + 1
        ).setHeader("#").setAutoWidth(true).setFlexGrow(0);

        priorityGridUrc.addColumn(URC_Priority_Areas::getName).setHeader("Priority Area").setAutoWidth(true);
        Button addPriorityBtn = new Button("Add Priority Area", e -> openUrcPriorityDialog(new URC_Priority_Areas()));
        VerticalLayout priorityLayout = new VerticalLayout(new H3("Priority Areas"), priorityGridUrc, addPriorityBtn);
        priorityLayout.setWidth("65%");

        // Combine in a HorizontalLayout
        // HorizontalLayout layout = new HorizontalLayout(planLayout, objectiveLayout, priorityLayout);
        HorizontalLayout layout = new HorizontalLayout(objectiveLayout, priorityLayout);
        layout.setSizeFull();
        layout.setSpacing(true);
        return layout;
    }

    private void selectPlan(NdpPlan plan) {
        this.selectedPlan = plan;
        if (plan != null) {
            objectiveGrid.setItems(plan.getStrategicObjectives());
            priorityGrid.setItems(plan.getPriorityAreas());
        } else {
            objectiveGrid.setItems(Collections.emptyList());
            priorityGrid.setItems(Collections.emptyList());
        }
    }

    private void selectUrcPlan(UrcStrategicPlan plan) {
        this.selectedUrcPlan = plan;
        if (plan != null) {

            objectiveGridUrc.setItems(urcStrategicObjectivesService.findByStrategicPlan(plan));
            priorityGridUrc.setItems(uRC_Priority_AreasService.getAreasByDate(plan.getStartDate()));
        } else {
            objectiveGrid.setItems(Collections.emptyList());
            priorityGrid.setItems(Collections.emptyList());
        }
    }

    private void updatePlans() {
        comboBoxNdpPlan.setItems(ndpPlanService.findAllWithDetails());
    }

    private void updateUrcPlans() {
        //comboBoxurcPlan.setItems(urcStrategicPlanService.findAll());
    }

    // --------------------------- Dialogs ---------------------------
    private void openPlanDialog(NdpPlan plan) {
        Dialog dialog = new Dialog();

        binder.setBean(plan);
        FormLayout form = new FormLayout(name, startDate, endDate, theme, ultimateGoal);
        binder.bind(name, NdpPlan::getName, NdpPlan::setName);
        binder.bind(theme, NdpPlan::getTheme, NdpPlan::setTheme);
        binder.bind(ultimateGoal, NdpPlan::getUltimateGoal, NdpPlan::setUltimateGoal);
        binder.bind(startDate, NdpPlan::getStartDate, NdpPlan::setStartDate);
        binder.bind(endDate, NdpPlan::getEndDate, NdpPlan::setEndDate);

        Button save = new Button("Save", e -> {
            binder.writeBeanIfValid(plan);
            ndpPlanService.save(plan);
            dialog.close();
            updatePlans();
        });
        Button cancel = new Button("Cancel", e -> dialog.close());
        dialog.add(form, new HorizontalLayout(save, cancel));
        dialog.open();
    }

    private void openUrcPlanDialog(UrcStrategicPlan plan) {
        Dialog dialog = new Dialog();

        binderUrc.setBean(plan);
        FormLayout form = new FormLayout(nameUrc, startDateUrc, endDateUrc, themeUrc, ultimateGoalUrc);
        binderUrc.bind(nameUrc, UrcStrategicPlan::getName, UrcStrategicPlan::setName);
        binderUrc.bind(themeUrc, UrcStrategicPlan::getTheme, UrcStrategicPlan::setTheme);
        binderUrc.bind(ultimateGoalUrc, UrcStrategicPlan::getUltimateGoal, UrcStrategicPlan::setUltimateGoal);
        binderUrc.bind(startDateUrc, UrcStrategicPlan::getStartDate, UrcStrategicPlan::setStartDate);
        binderUrc.bind(endDateUrc, UrcStrategicPlan::getEndDate, UrcStrategicPlan::setEndDate);

        Button save = new Button("Save", e -> {
            binderUrc.writeBeanIfValid(plan);
            urcStrategicPlanService.save(plan);
            dialog.close();
            updateUrcPlans();
        });
        Button cancel = new Button("Cancel", e -> dialog.close());
        dialog.add(form, new HorizontalLayout(save, cancel));
        dialog.open();
    }

    private void openObjectiveDialog(StrategicObjective obj) {
        if (selectedPlan == null) {
            Notification.show("Select a plan first.");
            return;
        }

        Dialog dialog = new Dialog();
        TextField code = new TextField("Code");
        TextArea desc = new TextArea("Description");
        Binder<StrategicObjective> binder = new Binder<>(StrategicObjective.class);
        binder.bind(code, StrategicObjective::getTitle, StrategicObjective::setTitle);
        binder.bind(desc, StrategicObjective::getDescription, StrategicObjective::setDescription);
        binder.setBean(obj);

        Button save = new Button("Save", e -> {
            obj.setNdpPlan(selectedPlan);
            strategicObjectiveService.save(obj);
            dialog.close();
            selectPlan(selectedPlan);
        });
        Button cancel = new Button("Cancel", e -> dialog.close());
        dialog.add(new FormLayout(code, desc), new HorizontalLayout(save, cancel));
        dialog.open();
    }

    private void openUrcObjectiveDialog(UrcStrategicObjectives obj) {
        if (selectedUrcPlan == null) {
            Notification.show("Select a plan first.");
            return;
        }

        Dialog dialog = new Dialog();
        TextArea desc = new TextArea("URC Strategic Objective");
        Binder<UrcStrategicObjectives> binder = new Binder<>(UrcStrategicObjectives.class);
        binder.bind(desc, UrcStrategicObjectives::getObjective, UrcStrategicObjectives::setObjective);
        binder.setBean(obj);

        Button save = new Button("Save", e -> {
            // obj.setUrcStrategicPlan(selectedUrcPlan);
            urcStrategicObjectivesService.save(obj);
            dialog.close();
            selectUrcPlan(selectedUrcPlan);
        });
        Button cancel = new Button("Cancel", e -> dialog.close());
        dialog.add(new FormLayout(desc), new HorizontalLayout(save, cancel));
        dialog.open();
    }

    private void openPriorityDialog(PriorityArea area) {
        if (selectedPlan == null) {
            Notification.show("Select a plan first.");
            return;
        }

        Dialog dialog = new Dialog();
        TextField name = new TextField("Name");
        TextArea desc = new TextArea("Description");
        Binder<PriorityArea> binder = new Binder<>(PriorityArea.class);
        binder.bind(name, PriorityArea::getName, PriorityArea::setName);
        binder.bind(desc, PriorityArea::getDescription, PriorityArea::setDescription);
        binder.setBean(area);

        Button save = new Button("Save", e -> {
            area.setNdpPlan(selectedPlan);
            priorityAreaService.save(area);
            dialog.close();
            selectPlan(selectedPlan);
        });
        Button cancel = new Button("Cancel", e -> dialog.close());
        dialog.add(new FormLayout(name, desc), new HorizontalLayout(save, cancel));
        dialog.open();
    }

    private void openUrcPriorityDialog(URC_Priority_Areas area) {
        if (selectedUrcPlan == null) {
            Notification.show("Select a plan first.");
            return;
        }

        Dialog dialog = new Dialog();
        TextField name = new TextField("Name");
        ComboBox<PriorityArea> desc = new ComboBox("NPD Programme");
        desc.setItemLabelGenerator(PriorityArea::getName);
        desc.setItems(priorityAreaService.findAll());
        Binder<URC_Priority_Areas> binder = new Binder<>(URC_Priority_Areas.class);
        binder.bind(name, URC_Priority_Areas::getName, URC_Priority_Areas::setName);
        binder.bind(desc, URC_Priority_Areas::getPriorityArea, URC_Priority_Areas::setPriorityArea);
        binder.setBean(area);

        Button save = new Button("Save", e -> {
            area.setPriorityArea(desc.getValue());
            uRC_Priority_AreasService.update(area);
            dialog.close();
            selectUrcPlan(selectedUrcPlan);
        });
        Button cancel = new Button("Cancel", e -> dialog.close());
        dialog.add(new FormLayout(name, desc), new HorizontalLayout(save, cancel));
        dialog.open();
    }

}
