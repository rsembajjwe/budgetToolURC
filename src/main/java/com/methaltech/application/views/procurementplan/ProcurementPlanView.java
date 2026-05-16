// ================= PART 1 OF 3 =================
package com.methaltech.application.views.procurementplan;

import com.methaltech.application.data.ProcClass;
import com.methaltech.application.data.Role;
import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.CurrencyService;
import com.methaltech.application.data.bgtool.service.FundsourceService;
import com.methaltech.application.data.bgtool.service.ProcurementBudgetItemGroupService;
import com.methaltech.application.data.bgtool.service.ProcurementMethodService;
import com.methaltech.application.data.bgtool.service.ProcurementPlanService;
import com.methaltech.application.data.bgtool.service.ProcurementTypeService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.Currency;
import com.methaltech.application.data.entity.bgtool.Fundsource;
import com.methaltech.application.data.entity.bgtool.ProcurementBudgetItemGroup;
import com.methaltech.application.data.entity.bgtool.ProcurementMethod;
import com.methaltech.application.data.entity.bgtool.ProcurementPlan;
import com.methaltech.application.data.entity.bgtool.ProcurementType;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.security.AuthenticatedUser;
import com.methaltech.application.views.MainLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

import jakarta.annotation.security.RolesAllowed;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Procurement Plan")
@Route(value = "procurementplan", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "PROCUREMENT", "BLO", "HOD"})
@Uses(Icon.class)
public class ProcurementPlanView extends Div {

    private static final List<ProcClass> OTHER_CLASSES = List.of(
            ProcClass.Supplies,
            ProcClass.Works,
            ProcClass.Non_Consultancy,
            ProcClass.Disposal,
            ProcClass.Other
    );

    private final UserService userService;
    private final BudgetService budgetService;
    private final UrcDeptSectionAnlDimbgtService deptSectionService;
    private final ProcurementPlanService procurementPlanService;
    private final ProcurementMethodService procurementMethodService;
    private final ProcurementTypeService procurementTypeService;
    private final BudgetItemsService budgetItemsService;
    private final CoaService coaService;
    private final CurrencyService currencyService;
    private final FundsourceService fundsourceService;
    private final AuthenticatedUser authenticatedUser;
    private final ProcurementBudgetItemGroupService procurementBudgetItemGroupService;

    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");
    private final Map<CellStyle, CellStyle> blackStyleCache = new HashMap<>();

    private User currentUser;

    private final PlanSection consultancySection = new PlanSection(
            "Consultancy",
            List.of(ProcClass.Consultancy),
            ProcClass.Consultancy
    );

    private final PlanSection otherSection = new PlanSection(
            "Supplies, Works, Non-Consultancy, Disposal",
            OTHER_CLASSES,
            ProcClass.Supplies
    );

    @Autowired
    public ProcurementPlanView(
            UserService userService,
            BudgetService budgetService,
            UrcDeptSectionAnlDimbgtService deptSectionService,
            ProcurementPlanService procurementPlanService,
            ProcurementMethodService procurementMethodService,
            ProcurementTypeService procurementTypeService,
            BudgetItemsService budgetItemsService,
            CoaService coaService,
            CurrencyService currencyService,
            FundsourceService fundsourceService,
            AuthenticatedUser authenticatedUser,
            ProcurementBudgetItemGroupService procurementBudgetItemGroupService
    ) {
        this.userService = userService;
        this.budgetService = budgetService;
        this.deptSectionService = deptSectionService;
        this.procurementPlanService = procurementPlanService;
        this.procurementMethodService = procurementMethodService;
        this.procurementTypeService = procurementTypeService;
        this.budgetItemsService = budgetItemsService;
        this.coaService = coaService;
        this.currencyService = currencyService;
        this.fundsourceService = fundsourceService;
        this.authenticatedUser = authenticatedUser;
        this.procurementBudgetItemGroupService = procurementBudgetItemGroupService;

        addClassName("procurement-plan-view");
        setSizeFull();

        currentUser = authenticatedUser.get().orElse(null);
        if (currentUser == null) {
            throw new IllegalStateException("Authenticated user not found.");
        }

        buildView();
    }

    private void buildView() {
        Image strip = new Image("images/ugflagstrip.png", "Uganda flag strip");
        strip.setWidthFull();
        strip.getStyle().set("margin", "0").set("padding", "0");

        configureSection(consultancySection);
        configureSection(otherSection);

        TabSheet tabs = new TabSheet();
        tabs.setSizeFull();
        tabs.add(consultancySection.title, consultancySection.root);
        tabs.add(otherSection.title, otherSection.root);

        add(strip, tabs);
    }

    private void configureSection(PlanSection section) {
        section.root.setSizeFull();
        section.root.addClassName("procurement-section");

        configureFilters(section);
        configurePlanGrid(section);
        configureBudgetItemGrid(section);
        configureContextMenus(section);

        SplitLayout split = new SplitLayout();
        split.setSizeFull();
        split.setSplitterPosition(40);

        VerticalLayout primary = new VerticalLayout(section.planGrid);
        primary.setPadding(false);
        primary.setSpacing(false);
        primary.setSizeFull();

        VerticalLayout secondary = createBudgetItemsPanel(section);
        secondary.setPadding(false);
        secondary.setSpacing(false);
        secondary.setSizeFull();

        split.addToPrimary(primary);
        split.addToSecondary(secondary);

        section.root.add(createToolbar(section), split);
    }

    private void configureFilters(PlanSection section) {
        section.budget.setLabel("Budget");
        section.budget.setItemLabelGenerator(Budget::getFinancialYear);
        section.budget.setItems(query -> budgetService.list(
                PageRequest.of(
                        query.getPage(),
                        query.getPageSize(),
                        VaadinSpringDataHelpers.toSpringDataSort(query)
                )
        ).stream());

        section.procClass.setLabel("Procurement Class");
        section.procClass.setItems(section.allowedClasses);
        section.procClass.setValue(section.defaultClass);

        section.costCentres.setLabel("Cost Centres");
        section.costCentres.setItemLabelGenerator(UrcDeptSectionAnlDimbgt::getNAME);
        section.costCentres.setItems(loadAllowedCostCentres());
        section.costCentres.setClearButtonVisible(true);

        section.funds.setLabel("Source of Funds");
        section.funds.setItemLabelGenerator(Fundsource::getFundsource);
        section.funds.setClearButtonVisible(true);

        section.budget.addValueChangeListener(e -> {
            Budget selected = e.getValue();
            boolean active = selected != null && selected.isActive();

            setActionsEnabled(section, active);

            if (selected != null) {
                section.funds.setItems(fundsourceService.findFundsourcesByBudget(selected));
            } else {
                section.funds.clear();
                section.funds.setItems(Collections.emptyList());
            }

            refreshSection(section);
        });

        section.procClass.addValueChangeListener(e -> refreshSection(section));
        section.costCentres.addValueChangeListener(e -> refreshSection(section));
        section.funds.addValueChangeListener(e -> refreshSection(section));
    }

    private Collection<UrcDeptSectionAnlDimbgt> loadAllowedCostCentres() {
        if (isAdminOrProcurement()) {
            return deptSectionService.getAllUrcSectionsAnlDims();
        }

        if (currentUser.getDeptsection() == null) {
            return Collections.emptyList();
        }

        return currentUser.getDeptsection();
    }

    private HorizontalLayout createToolbar(PlanSection section) {
        Button regenerate = new Button("Refresh Procurement Plan", new Icon(VaadinIcon.REFRESH));
        regenerate.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        regenerate.addClickListener(e -> confirmAction(
                "Refresh Procurement Plan",
                "This will update existing procurement plan records while preserving manual details such as procurement method, contract type and dates.",
                "Refresh",
                () -> regenerateProcurementPlan(section)
        ));

        Button refresh = new Button("Regenerate Procurement Plan", new Icon(VaadinIcon.REFRESH));
        refresh.addClickListener(e -> confirmAction(
                "Regenerate Procurement Plan",
                "This will delete existing procurement plan records and recreate them from budget items. Manual plan changes may be lost.",
                "Regenerate",
                () -> regenerateProcurementPlan2(section)
        ));

        Button downloadPlan = new Button("Download Procurement Plan", new Icon(VaadinIcon.DOWNLOAD));
        downloadPlan.addClickListener(e -> {
            if (!validateBudgetAndClass(section)) {
                return;
            }

            confirmAction(
                    "Download Procurement Plan",
                    "Generate and download the full procurement plan workbook?",
                    "Download",
                    () -> exportAndDownloadExcelProcurementPlanSheets(section.budget.getValue())
            );
        });

        HorizontalLayout toolbar = new HorizontalLayout(
                section.budget,
                section.procClass,
                section.costCentres,
                section.funds,
                regenerate,
                refresh,
                downloadPlan
        );

        toolbar.setWidthFull();
        toolbar.setAlignItems(FlexComponent.Alignment.END);
        toolbar.setPadding(true);
        toolbar.setSpacing(true);
        toolbar.addClassName("procurement-toolbar");

        return toolbar;
    }

    private void confirmAction(
            String title,
            String message,
            String confirmText,
            Runnable action
    ) {
        ConfirmDialog dialog = new ConfirmDialog();

        dialog.setHeader(title);
        dialog.setText(message);

        dialog.setCancelable(true);
        dialog.setCancelText("Cancel");

        dialog.setConfirmText(confirmText);
        dialog.setConfirmButtonTheme("primary");

        dialog.addConfirmListener(e -> action.run());

        dialog.open();
    }

// ================= PART 2 OF 3 =================
    private void configurePlanGrid(PlanSection section) {
        Grid<ProcurementPlan> grid = section.planGrid;
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        grid.addThemeVariants(
                GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_WRAP_CELL_CONTENT,
                GridVariant.LUMO_COLUMN_BORDERS
        );

        grid.addColumn(ProcurementPlan::getSubject)
                .setHeader("Subject of Procurement")
                .setFrozen(true)
                .setWidth("200px")
                .setResizable(true);

        grid.addColumn(new ComponentRenderer<>(plan -> badgeText(safeCoaCode(plan))))
                .setHeader("Code")
                .setFrozen(true)
                .setWidth("30px")
                .setResizable(true)
                .setTooltipGenerator(this::safeCoaName);

        grid.addColumn(plan -> money(plan.getCost()))
                .setHeader("Estimated Cost")
                .setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.END)
                .setWidth("70px")
                .setFooter(section.total)
                .setResizable(true);

        grid.addColumn(this::getFundSources)
                .setHeader("Fund Source")
                .setWidth("90px")
                .setResizable(true);
        grid.asMultiSelect().addValueChangeListener(e -> loadBudgetItemsForSelection(section));
    }

    private void configureBudgetItemGrid(PlanSection section) {
        Grid<BudgetItems> grid = section.budgetItemsGrid;
        grid.setSizeFull();
        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        grid.addThemeVariants(
                GridVariant.LUMO_NO_BORDER,
                GridVariant.LUMO_ROW_STRIPES,
                GridVariant.LUMO_WRAP_CELL_CONTENT,
                GridVariant.LUMO_COLUMN_BORDERS
        );

        Grid.Column<BudgetItems> itemColumn = grid.addColumn(BudgetItems::getItem)
                .setHeader("Budget Item")
                .setFrozen(true)
                .setWidth("300px")
                .setResizable(true);

        grid.addColumn(item -> item.getCoacode() == null ? "" : item.getCoacode().getCode())
                .setHeader("Code")
                .setWidth("100px")
                .setResizable(true);

        Grid.Column<BudgetItems> totalColumn = grid.addColumn(item -> money(sumBudgetItem(item)))
                .setHeader("Total")
                .setAutoWidth(true)
                .setResizable(true)
                .setTextAlign(ColumnTextAlign.END)
                .setSortable(true);

        grid.addColumn(item -> item.getFundsource() == null ? "" : item.getFundsource().getFundsource())
                .setHeader("Fund Source")
                .setWidth("180px")
                .setResizable(true);

        grid.addColumn(this::costCentreText)
                .setHeader("Cost Centre")
                .setWidth("260px")
                .setResizable(true);

        grid.addColumn(BudgetItems::getProcurementMethodName)
                .setHeader("Procurement Method")
                .setWidth("200px")
                .setResizable(true);

        grid.addColumn(BudgetItems::getProcurementTypeName)
                .setHeader("Contract Type")
                .setWidth("180px")
                .setResizable(true);

        grid.addColumn(BudgetItems::getPrequalificationText)
                .setHeader("Prequalification")
                .setWidth("150px")
                .setResizable(true);

        grid.addColumn(BudgetItems::getReserveSchemeText)
                .setHeader("Reserve Scheme")
                .setWidth("150px")
                .setResizable(true);

        if (section == consultancySection) {
            addConsultancyBudgetItemDateColumns(grid);
        } else {
            addOtherBudgetItemDateColumns(grid);
        }

        grid.addItemDoubleClickListener(e
                -> openBudgetItemEditDialog(section, e.getItem())
        );

        addMonthColumns(grid);

        FooterRow footer = grid.appendFooterRow();

        Span totalLabel = new Span("TOTAL");
        totalLabel.getStyle()
                .set("font-weight", "700")
                .set("color", "#0f172a")
                .set("background", "#f8fafc")
                .set("padding", "6px")
                .set("display", "block");

        section.budgetItemsTotalFooter = new Span("UGX 0.00");

        Div totalContainer = new Div(section.budgetItemsTotalFooter);
        totalContainer.getStyle()
                .set("background", "#f8fafc")
                .set("border-top", "2px solid #d1d5db")
                .set("padding", "6px 8px")
                .set("text-align", "right")
                .set("display", "block");

        footer.getCell(itemColumn).setComponent(totalLabel);
        footer.getCell(totalColumn).setComponent(totalContainer);

        grid.getDataProvider().addDataProviderListener(event -> {
            List<BudgetItems> items = new ArrayList<>();

            grid.getDataProvider()
                    .fetch(new Query<>())
                    .forEach(items::add);

            BigDecimal total = sumBudgetItemsMonths(items);

            section.budgetItemsTotalFooter.setText("UGX " + money(total));
        });
    }

    private void updateBudgetItemsFooter(
            PlanSection section,
            List<BudgetItems> items
    ) {
        if (section == null || section.budgetItemsTotalFooter == null) {
            return;
        }

        BigDecimal total = sumBudgetItemsMonths(items);
        section.budgetItemsTotalFooter.setText("UGX " + money(total));
    }

    private String costCentreText(BudgetItems item) {
        if (item == null) {
            return "";
        }

        if (isSyntheticGroupRow(item)) {
            Long groupId = Math.abs(item.getId());

            return procurementBudgetItemGroupService.findByIdWithItems(groupId)
                    .map(group -> group.getItems() == null
                    ? ""
                    : group.getItems().stream()
                            .filter(Objects::nonNull)
                            .map(BudgetItems::getDeptUnit)
                            .filter(Objects::nonNull)
                            .map(UrcDeptSectionAnlDimbgt::getNAME)
                            .filter(Objects::nonNull)
                            .filter(name -> !name.isBlank())
                            .distinct()
                            .collect(Collectors.joining(", ")))
                    .orElse("");
        }

        return item.getDeptUnit() == null ? "" : nvl(item.getDeptUnit().getNAME());
    }

    private void addMonthColumns(Grid<BudgetItems> grid) {
        Grid.Column<BudgetItems> jul = addMoneyMonthColumn(grid, "Jul", BudgetItems::getJul);
        Grid.Column<BudgetItems> aug = addMoneyMonthColumn(grid, "Aug", BudgetItems::getAug);
        Grid.Column<BudgetItems> sep = addMoneyMonthColumn(grid, "Sep", BudgetItems::getSep);

        Grid.Column<BudgetItems> oct = addMoneyMonthColumn(grid, "Oct", BudgetItems::getOct);
        Grid.Column<BudgetItems> nov = addMoneyMonthColumn(grid, "Nov", BudgetItems::getNov);
        Grid.Column<BudgetItems> dec = addMoneyMonthColumn(grid, "Dec", BudgetItems::getDec);

        Grid.Column<BudgetItems> jan = addMoneyMonthColumn(grid, "Jan", BudgetItems::getJan);
        Grid.Column<BudgetItems> feb = addMoneyMonthColumn(grid, "Feb", BudgetItems::getFeb);
        Grid.Column<BudgetItems> mar = addMoneyMonthColumn(grid, "Mar", BudgetItems::getMar);

        Grid.Column<BudgetItems> apr = addMoneyMonthColumn(grid, "Apr", BudgetItems::getApr);
        Grid.Column<BudgetItems> may = addMoneyMonthColumn(grid, "May", BudgetItems::getMay);
        Grid.Column<BudgetItems> jun = addMoneyMonthColumn(grid, "Jun", BudgetItems::getJun);

        HeaderRow quarterHeader = grid.prependHeaderRow();
        quarterHeader.join(jul, aug, sep).setText("QTR 1");
        quarterHeader.join(oct, nov, dec).setText("QTR 2");
        quarterHeader.join(jan, feb, mar).setText("QTR 3");
        quarterHeader.join(apr, may, jun).setText("QTR 4");
    }

    private Grid.Column<BudgetItems> addMoneyMonthColumn(
            Grid<BudgetItems> grid,
            String header,
            Function<BudgetItems, BigDecimal> getter
    ) {
        return grid.addColumn(new ComponentRenderer<>(item -> {
            BigDecimal value = nz(getter.apply(item));
            Span span = new Span(money(value));
            span.getElement().getThemeList().add(
                    value.compareTo(BigDecimal.ZERO) > 0
                    ? "badge success"
                    : "badge contrast"
            );
            return span;
        }))
                .setHeader(header)
                .setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.END)
                .setWidth("110px")
                .setResizable(true);
    }

    private VerticalLayout createBudgetItemsPanel(PlanSection section) {
        H3 title = new H3("Selected Budget Items");
        title.getStyle().set("margin", "var(--lumo-space-m)");

        Button download = new Button("Download Excel Report", new Icon(VaadinIcon.DOWNLOAD));
        download.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Anchor hiddenAnchor = new Anchor();
        hiddenAnchor.getStyle().set("display", "none");
        hiddenAnchor.getElement().setAttribute("download", true);

        download.addClickListener(e -> downloadSelectedBudgetItems(section, hiddenAnchor));

        HorizontalLayout header = new HorizontalLayout(title, download, hiddenAnchor);
        header.setWidthFull();
        header.setPadding(true);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        VerticalLayout layout = new VerticalLayout(header, section.budgetItemsGrid);
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);

        return layout;
    }

    private void configureContextMenus(PlanSection section) {
        if (!isAdminOrProcurement()) {
            return;
        }

        // =========================
        // PROCUREMENT PLAN MENU
        // =========================
        section.planContextMenu = new GridContextMenu<>(section.planGrid);

        section.planContextMenu.addItem("Edit Procurement Plan", e
                -> editSelectedPlan(section)
        );

        section.planContextMenu.addItem("Combine Selected Plans", e
                -> openCombineDialog(section)
        );

        section.planContextMenu.addItem("Ungroup Combined Plan", e
                -> ungroupSelectedPlans(section)
        );

        section.planContextMenu.addItem("Change Procurement Class", e
                -> openChangeClassDialog(section)
        );

        // =========================
        // BUDGET ITEM MENU
        // =========================
        section.itemContextMenu = new GridContextMenu<>(section.budgetItemsGrid);

        section.itemContextMenu.addItem("Update COA", e
                -> openUpdateCoaDialog(section)
        );

        addTransferMenus(section);

        section.itemContextMenu.addItem("Group Selected Budget Items", e
                -> openGroupBudgetItemsDialog(section)
        );

        section.itemContextMenu.addItem("Add Selected Items to Existing Group", e
                -> openAddItemsToGroupDialog(section)
        );

        section.itemContextMenu.addItem("Ungroup Selected Budget Item Group", e -> {
            Set<BudgetItems> selected = section.budgetItemsGrid.getSelectedItems();

            if (selected == null || selected.isEmpty()) {
                warn("Select a grouped budget item row to ungroup.");
                return;
            }

            boolean hasGroupedRow = selected.stream()
                    .anyMatch(this::isSyntheticGroupRow);

            if (!hasGroupedRow) {
                warn("Only grouped budget item rows can be ungrouped.");
                return;
            }

            ungroupSelectedBudgetItemGroup(section);
        });

        section.itemContextMenu.addItem("View Group Items", e
                -> openViewGroupItemsDialog(section)
        );
    }

    private void openViewGroupItemsDialog(PlanSection section) {
        Set<BudgetItems> selectedItems = section.budgetItemsGrid.getSelectedItems();

        List<Long> groupIds = selectedItems.stream()
                .filter(this::isSyntheticGroupRow)
                .map(BudgetItems::getSyntheticGroupId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (groupIds.size() != 1) {
            warn("Select exactly one grouped row.");
            return;
        }

        ProcurementBudgetItemGroup group = procurementBudgetItemGroupService
                .findByIdWithItems(groupIds.get(0))
                .orElse(null);

        if (group == null) {
            warn("Group not found.");
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Group Items - " + group.getGroupName());
        dialog.setWidth("850px");

        Grid<BudgetItems> grid = new Grid<>(BudgetItems.class, false);
        grid.setWidthFull();
        grid.setHeight("320px");

        grid.addColumn(item -> item.getCoacode() == null ? "" : item.getCoacode().getCode())
                .setHeader("Code")
                .setWidth("90px");

        grid.addColumn(BudgetItems::getItem)
                .setHeader("Budget Item")
                .setFlexGrow(1);

        grid.addColumn(item -> money(sumBudgetItem(item)))
                .setHeader("Total")
                .setTextAlign(ColumnTextAlign.END)
                .setResizable(true);

        grid.addColumn(item -> item.getDeptUnit() == null ? "" : item.getDeptUnit().getNAME())
                .setHeader("Section / Cost Centre")
                .setWidth("220px")
                .setResizable(true);

        grid.setItems(group.getItems() == null ? Collections.emptySet() : group.getItems());

        Button close = new Button("Close", e -> dialog.close());

        dialog.add(grid);
        dialog.getFooter().add(close);
        dialog.open();
    }

    private void ungroupSelectedBudgetItemGroup(PlanSection section) {
        Set<BudgetItems> selected = section.budgetItemsGrid.getSelectedItems();

        if (selected == null || selected.isEmpty()) {
            warn("Select at least one grouped budget item row.");
            return;
        }

        List<BudgetItems> groupedRows = selected.stream()
                .filter(Objects::nonNull)
                .filter(this::isSyntheticGroupRow)
                .collect(Collectors.toList());

        if (groupedRows.isEmpty()) {
            warn("Only grouped budget item rows can be ungrouped.");
            return;
        }

        int deletedGroups = 0;
        int restoredItems = 0;
        int skipped = 0;

        for (BudgetItems synthetic : groupedRows) {
            try {
                Long groupId = synthetic.getSyntheticGroupId();

                if (groupId == null) {
                    skipped++;
                    continue;
                }

                ProcurementBudgetItemGroup group = procurementBudgetItemGroupService
                        .findByIdWithItems(groupId)
                        .orElse(null);

                if (group == null) {
                    skipped++;
                    continue;
                }

                int itemCount = group.getItems() == null ? 0 : group.getItems().size();

                procurementBudgetItemGroupService.delete(group);

                deletedGroups++;
                restoredItems += itemCount;

            } catch (Exception ex) {
                skipped++;
                Logger.getLogger(getClass().getName())
                        .log(Level.WARNING, "Failed to ungroup budget item group", ex);
            }
        }

        refreshAllSectionsAfterTransfer();
        clearAllSelections();

        success("Ungroup completed. Removed "
                + deletedGroups
                + " group(s), restored "
                + restoredItems
                + " budget item(s), skipped "
                + skipped
                + ".");
    }

    private void openAddItemsToGroupDialog(PlanSection section) {
        Set<BudgetItems> selectedItems = section.budgetItemsGrid.getSelectedItems();

        List<BudgetItems> normalItems = selectedItems.stream()
                .filter(item -> item != null && item.getId() != null && item.getId() > 0)
                .collect(Collectors.toList());

        if (normalItems.isEmpty()) {
            warn("Select at least one normal budget item to add.");
            return;
        }

        if (section.budget.isEmpty() || section.procClass.isEmpty()) {
            warn("Select a financial year and procurement class first.");
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Add Items to Group");
        dialog.setWidth("650px");

        ComboBox<ProcurementBudgetItemGroup> groupCombo = new ComboBox<>("Select Group");
        groupCombo.setWidthFull();
        groupCombo.setRequiredIndicatorVisible(true);

        List<ProcurementBudgetItemGroup> groups
                = procurementBudgetItemGroupService.findByBudgetAndProcClassWithItems(
                        section.budget.getValue(),
                        section.procClass.getValue()
                );

        groupCombo.setItems(groups);
        groupCombo.setItemLabelGenerator(group
                -> group == null ? "" : group.getGroupName()
        );

        Button add = new Button("Add to Group", e -> {
            ProcurementBudgetItemGroup group = groupCombo.getValue();

            if (group == null) {
                warn("Select a group.");
                return;
            }

            Set<BudgetItems> groupItems = new HashSet<>(
                    group.getItems() == null ? Collections.emptySet() : group.getItems()
            );

            groupItems.addAll(normalItems);
            group.setItems(groupItems);

            procurementBudgetItemGroupService.save(group);

            refreshSection(section);
            section.planGrid.deselectAll();
            section.budgetItemsGrid.setItems(Collections.emptyList());

            dialog.close();
            success("Added " + normalItems.size() + " item(s) to group.");
        });

        add.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancel = new Button("Cancel", e -> dialog.close());

        dialog.add(new VerticalLayout(groupCombo));
        dialog.getFooter().add(cancel, add);
        dialog.open();
    }

    private void ungroupSelectedPlans(PlanSection section) {

        Set<ProcurementPlan> selectedPlans = section.planGrid.getSelectedItems();

        if (selectedPlans == null || selectedPlans.isEmpty()) {
            warn("Select at least one combined procurement plan to ungroup.");
            return;
        }

        int createdPlans = 0;
        int deletedPlans = 0;
        int skipped = 0;

        for (ProcurementPlan plan : selectedPlans) {

            if (plan == null) {
                skipped++;
                continue;
            }

            List<BudgetItems> items = safeBudgetItems(plan).stream()
                    .filter(Objects::nonNull)
                    .filter(item -> item.getId() != null && item.getId() > 0)
                    .filter(item -> item.getCoacode() != null)
                    .collect(Collectors.toList());

            if (items.isEmpty()) {
                skipped++;
                continue;
            }

            Map<COA, List<BudgetItems>> itemsByCoa = items.stream()
                    .collect(Collectors.groupingBy(
                            BudgetItems::getCoacode,
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));

            for (Map.Entry<COA, List<BudgetItems>> entry : itemsByCoa.entrySet()) {

                COA coa = entry.getKey();
                List<BudgetItems> coaItems = entry.getValue();

                if (coa == null || coaItems == null || coaItems.isEmpty()) {
                    skipped++;
                    continue;
                }

                BudgetItems firstItem = coaItems.get(0);

                ProcurementPlan newPlan = new ProcurementPlan();
                newPlan.setSubject(nvl(coa.getName()));
                newPlan.setBudget(firstItem.getBudget() != null ? firstItem.getBudget() : plan.getBudget());
                newPlan.setCoa(coa);
                newPlan.setCurrency(firstItem.getCurrency() != null ? firstItem.getCurrency() : plan.getCurrency());
                newPlan.setProcClass(firstItem.getProcClass() != null ? firstItem.getProcClass() : plan.getProcClass());
                newPlan.setProcPlanBudgetItems(new HashSet<>(coaItems));
                newPlan.setCost(sumBudgetItems(coaItems));

                newPlan.setProcurementMethod(plan.getProcurementMethod());
                newPlan.setProcurementtype(plan.getProcurementtype());
                newPlan.setPrequal(plan.getPrequal());
                newPlan.setReserve(plan.getReserve());

                newPlan.setBinvite(plan.getBinvite());
                newPlan.setReqInviofExpofInterestdate(plan.getReqInviofExpofInterestdate());
                newPlan.setReqClosingOpeningdate(plan.getReqClosingOpeningdate());
                newPlan.setApprovaloffinalevaluationreport(plan.getApprovaloffinalevaluationreport());
                newPlan.setReqApprovalOfShortlist(plan.getReqApprovalOfShortlist());
                newPlan.setAwardnotificationdate(plan.getAwardnotificationdate());
                newPlan.setReqNotificationdate(plan.getReqNotificationdate());
                newPlan.setInvitationofProposalsdate(plan.getInvitationofProposalsdate());
                newPlan.setSubmissionOpeningdate(plan.getSubmissionOpeningdate());
                newPlan.setInvNotificationdate(plan.getInvNotificationdate());
                newPlan.setContractsigningdate(plan.getContractsigningdate());
                newPlan.setBcompletion(plan.getBcompletion());

                procurementPlanService.save(newPlan);
                createdPlans++;
            }

            procurementPlanService.deleteProcurementPlan(plan);
            deletedPlans++;
        }

        refreshAllSectionsAfterTransfer();
        clearAllSelections();

        success("Ungroup completed. Created "
                + createdPlans
                + " COA-based procurement plan(s), deleted "
                + deletedPlans
                + " combined plan(s), skipped "
                + skipped
                + ".");
    }

    private void addTransferMenus(PlanSection section) {
        List<ProcClass> targets = section == consultancySection
                ? OTHER_CLASSES
                : List.of(
                        ProcClass.Consultancy,
                        ProcClass.Supplies,
                        ProcClass.Works,
                        ProcClass.Non_Consultancy,
                        ProcClass.Other
                );

        for (ProcClass targetClass : targets) {
            section.itemContextMenu.addItem(
                    "Transfer to " + label(targetClass),
                    e -> transferSelectedBudgetItems(section, targetClass)
            );
        }
    }

    private boolean isSyntheticGroupRow(BudgetItems item) {
        return item != null
                && Boolean.TRUE.equals(item.getSyntheticGroupedRow())
                && item.getSyntheticGroupId() != null;
    }

    private void openUpdateCoaDialog(PlanSection section) {
        Set<BudgetItems> selectedItems = section.budgetItemsGrid.getSelectedItems();

        if (selectedItems.isEmpty()) {
            warn("Select at least one budget item.");
            return;
        }

        boolean hasGroupedRow = selectedItems.stream()
                .anyMatch(this::isSyntheticGroupRow);

        if (hasGroupedRow) {
            warn("Grouped rows cannot have COA updated directly. Ungroup first or update the original budget items.");
            return;
        }

        if (section.budget.isEmpty()) {
            warn("Select a financial year first.");
            return;
        }

        Budget budget = section.budget.getValue();

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Update Account Code");
        dialog.setWidth("820px");
        dialog.setModal(true);
        dialog.setDraggable(true);
        dialog.setResizable(true);

        ComboBox<COA> coaCombo = new ComboBox<>("New COA / Account Code");
        coaCombo.setWidthFull();
        coaCombo.setRequiredIndicatorVisible(true);
        coaCombo.setClearButtonVisible(true);
        coaCombo.setPlaceholder("Search by code, name, operating expense or capital expenditure");

        List<COA> coas = coaService.findByBudget(budget).stream()
                .filter(Objects::nonNull)
                .filter(coa -> coa.getCode() != null)
                .filter(coa -> coa.getCode().startsWith("2") || coa.getCode().startsWith("3"))
                .sorted(Comparator.comparing(COA::getCode))
                .collect(Collectors.toList());

        coaCombo.setItems(coas);
        coaCombo.setItemLabelGenerator(this::coaLabel);

        Grid<BudgetItems> previewGrid = new Grid<>(BudgetItems.class, false);
        previewGrid.setHeight("260px");
        previewGrid.setWidthFull();

        previewGrid.addColumn(item -> item.getCoacode() == null ? "" : item.getCoacode().getCode())
                .setHeader("Current COA")
                .setWidth("130px")
                .setResizable(true);

        previewGrid.addColumn(BudgetItems::getItem)
                .setHeader("Budget Item")
                .setFlexGrow(1)
                .setResizable(true);

        previewGrid.addColumn(item -> money(sumBudgetItem(item)))
                .setHeader("Amount")
                .setWidth("150px")
                .setTextAlign(ColumnTextAlign.END)
                .setResizable(true);

        previewGrid.setItems(selectedItems);

        Div info = new Div();
        info.setText("Selected budget items: " + selectedItems.size());
        info.getStyle()
                .set("background", "#f3f6fb")
                .set("border", "1px solid #d9e2ef")
                .set("border-radius", "10px")
                .set("padding", "10px 14px")
                .set("font-size", "13px")
                .set("font-weight", "600")
                .set("color", "#1f2937");

        Button update = new Button("Update COA", event -> {
            COA selectedCoa = coaCombo.getValue();

            if (selectedCoa == null) {
                warn("Select a COA.");
                return;
            }

            for (BudgetItems item : selectedItems) {
                item.setCoacode(selectedCoa);
                budgetItemsService.saveBudgetItem(item);
            }

            refreshAllSectionsAfterTransfer();
            clearAllSelections();

            dialog.close();
            success("COA updated for " + selectedItems.size() + " budget item(s).");
        });

        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancel = new Button("Cancel", e -> dialog.close());

        HorizontalLayout actions = new HorizontalLayout(cancel, update);
        actions.setWidthFull();
        actions.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout layout = new VerticalLayout(info, coaCombo, previewGrid);
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setWidthFull();

        dialog.add(layout);
        dialog.getFooter().add(actions);
        dialog.open();
    }

    private String coaLabel(COA coa) {
        if (coa == null || coa.getCode() == null) {
            return "";
        }

        String category = coa.getCode().startsWith("2")
                ? "Operating Expense"
                : "Capital Expenditure";

        return category + " | " + coa.getCode() + " - " + nvl(coa.getName());
    }

    private void refreshSection(PlanSection section) {
        if (section.budget.isEmpty() || section.procClass.isEmpty()) {
            section.planGrid.setItems(Collections.emptyList());
            section.budgetItemsGrid.setItems(Collections.emptyList());
            section.total.setText("0.00/=");
            return;
        }

        List<ProcurementPlan> plans = procurementPlanService.findByBudgetAndProcClassWithItems(
                section.budget.getValue(),
                section.procClass.getValue()
        );

        section.planGrid.setItems(plans);
        section.total.setText(money(sumPlans(plans)) + "/=");
        section.budgetItemsGrid.setItems(Collections.emptyList());
    }

    private void resetBudgetItemsFooter(PlanSection section) {
        if (section != null && section.budgetItemsTotalFooter != null) {
            section.budgetItemsTotalFooter.setText("UGX 0.00");
        }
    }

    private void loadBudgetItemsForSelection(PlanSection section) {
        Set<ProcurementPlan> selectedPlans = section.planGrid.getSelectedItems();

        if (selectedPlans.isEmpty()) {
            section.budgetItemsGrid.setItems(Collections.emptyList());
            resetBudgetItemsFooter(section);
            return;
        }

        List<BudgetItems> displayItems = new ArrayList<>();

        for (ProcurementPlan plan : selectedPlans) {
            List<BudgetItems> planItems = safeBudgetItems(plan);

            List<ProcurementBudgetItemGroup> groups
                    = plan.getCoa() == null
                    ? Collections.emptyList()
                    : procurementBudgetItemGroupService.findByBudgetAndProcClassAndCoaWithItems(
                            plan.getBudget(),
                            plan.getProcClass(),
                            plan.getCoa()
                    ).stream()
                            .filter(group -> sameCoa(group.getCoa(), plan.getCoa()))
                            .collect(Collectors.toList());

            Set<Long> groupedItemIds = groups.stream()
                    .filter(group -> group.getItems() != null)
                    .flatMap(group -> group.getItems().stream())
                    .map(BudgetItems::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            groups.forEach(group
                    -> displayItems.add(toSyntheticGroupedBudgetItem(group))
            );

            planItems.stream()
                    .filter(Objects::nonNull)
                    .filter(item -> plan.getCoa() == null || sameCoa(item.getCoacode(), plan.getCoa()))
                    .filter(item -> item.getId() == null || !groupedItemIds.contains(item.getId()))
                    .forEach(displayItems::add);
        }

        section.budgetItemsGrid.setItems(displayItems);
        updateBudgetItemsFooter(section, displayItems);
    }

    private boolean sameCoa(COA a, COA b) {
        if (a == null || b == null) {
            return false;
        }

        return Objects.equals(a.getId(), b.getId());
    }

    private BudgetItems toSyntheticGroupedBudgetItem(ProcurementBudgetItemGroup group) {
        BudgetItems item = new BudgetItems();

        if (group == null) {
            return item;
        }

        item.setSyntheticGroupedRow(true);
        item.setSyntheticGroupId(group.getId());

        item.setId(group.getId() == null ? null : -group.getId());
        item.setItem("[GROUP] " + nvl(group.getGroupName()));
        item.setBudget(group.getBudget());
        item.setCoacode(group.getCoa());
        item.setDeptUnit(group.getDeptUnit());
        item.setProcClass(group.getProcClass());

        item.setJul(sumGroupMonth(group, BudgetItems::getJul));
        item.setAug(sumGroupMonth(group, BudgetItems::getAug));
        item.setSep(sumGroupMonth(group, BudgetItems::getSep));
        item.setOct(sumGroupMonth(group, BudgetItems::getOct));
        item.setNov(sumGroupMonth(group, BudgetItems::getNov));
        item.setDec(sumGroupMonth(group, BudgetItems::getDec));
        item.setJan(sumGroupMonth(group, BudgetItems::getJan));
        item.setFeb(sumGroupMonth(group, BudgetItems::getFeb));
        item.setMar(sumGroupMonth(group, BudgetItems::getMar));
        item.setApr(sumGroupMonth(group, BudgetItems::getApr));
        item.setMay(sumGroupMonth(group, BudgetItems::getMay));
        item.setJun(sumGroupMonth(group, BudgetItems::getJun));

        return item;
    }

    private BigDecimal sumGroupMonth(
            ProcurementBudgetItemGroup group,
            Function<BudgetItems, BigDecimal> getter
    ) {
        if (group == null || group.getItems() == null) {
            return BigDecimal.ZERO;
        }

        return group.getItems().stream()
                .map(getter)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void editSelectedPlan(PlanSection section) {
        Set<ProcurementPlan> selected = section.planGrid.getSelectedItems();

        if (selected.size() != 1) {
            warn("Select exactly one procurement plan item to edit.");
            return;
        }

        openEditDialog(section, selected.iterator().next());
    }

    private void openEditDialog(PlanSection section, ProcurementPlan plan) {

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit Procurement Plan");
        dialog.setModal(true);
        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.setWidth("600px");

        TextField subject = new TextField("Subject of Procurement");
        subject.setValue(nvl(plan.getSubject()));
        subject.setWidthFull();
        subject.setRequiredIndicatorVisible(true);

        BigDecimalField estimatedCost = new BigDecimalField("Estimated Cost");
        estimatedCost.setValue(nz(plan.getCost()));
        estimatedCost.setReadOnly(true);
        estimatedCost.setWidthFull();

        TextField procClass = new TextField("Procurement Class");
        procClass.setValue(label(plan.getProcClass()));
        procClass.setReadOnly(true);
        procClass.setWidthFull();

        TextField coa = new TextField("Account Code");
        coa.setValue(safeCoaCode(plan));
        coa.setReadOnly(true);
        coa.setWidthFull();

        FormLayout form = new FormLayout(
                subject,
                estimatedCost,
                procClass,
                coa
        );

        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0px", 1),
                new FormLayout.ResponsiveStep("700px", 2)
        );

        Grid<BudgetItems> preview = createBudgetItemPreviewGrid();
        preview.setItems(safeBudgetItems(plan));

        Button save = primaryButton("Save");

        save.addClickListener(e -> {

            if (subject.isEmpty()) {
                warn("Subject is required.");
                return;
            }

            plan.setSubject(subject.getValue());

            procurementPlanService.save(plan);

            refreshSection(section);

            dialog.close();

            success("Procurement plan updated.");
        });

        dialog.add(new VerticalLayout(form, preview));

        dialog.getFooter().add(
                new Button("Cancel", e -> dialog.close()),
                save
        );

        dialog.open();
    }

    private Grid<BudgetItems> createBudgetItemPreviewGrid() {
        Grid<BudgetItems> preview = new Grid<>(BudgetItems.class, false);
        preview.setHeight("180px");

        preview.addColumn(BudgetItems::getItem)
                .setHeader("Budget Item")
                .setAutoWidth(true);

        addMonthColumns(preview);

        return preview;
    }

    private void savePlanAndRefresh(PlanSection section, ProcurementPlan plan, Dialog dialog) {
        procurementPlanService.save(plan);

        refreshSection(section);
        section.planGrid.deselectAll();

        success("Procurement plan item saved.");
        dialog.close();
    }

// ================= PART 3 OF 3 =================
    private void openCombineDialog(PlanSection section) {

        Set<ProcurementPlan> selectedPlans = section.planGrid.getSelectedItems();

        if (selectedPlans == null || selectedPlans.size() < 2) {
            warn("Select more than one procurement plan item to combine.");
            return;
        }

        if (section.procClass.isEmpty()) {
            warn("Select procurement class first.");
            return;
        }

        ProcurementPlan firstPlan = selectedPlans.iterator().next();

        List<BudgetItems> items = selectedPlans.stream()
                .filter(Objects::nonNull)
                .flatMap(plan -> safeBudgetItems(plan).stream())
                .filter(Objects::nonNull)
                .filter(item -> item.getId() != null && item.getId() > 0)
                .distinct()
                .collect(Collectors.toList());

        if (items.isEmpty()) {
            warn("No valid budget items found to combine.");
            return;
        }

        BigDecimal totalCost = sumBudgetItems(items);

        boolean singleCoa = items.stream()
                .map(BudgetItems::getCoacode)
                .filter(Objects::nonNull)
                .map(COA::getId)
                .filter(Objects::nonNull)
                .distinct()
                .count() == 1;

        COA representativeCoa = singleCoa
                ? items.stream()
                        .map(BudgetItems::getCoacode)
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(null)
                : null;

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Combine Procurement Items");
        dialog.setModal(true);
        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.setWidth("850px");

        TextField subject = new TextField("Subject of Procurement");
        subject.setValue(singleCoa && representativeCoa != null
                ? nvl(representativeCoa.getName())
                : "Combined Procurement Item");
        subject.setRequiredIndicatorVisible(true);
        subject.setWidthFull();

        BigDecimalField cost = new BigDecimalField("Estimated Cost");
        cost.setValue(totalCost);
        cost.setReadOnly(true);
        cost.setWidthFull();

        TextField procClass = new TextField("Procurement Class");
        procClass.setValue(label(section.procClass.getValue()));
        procClass.setReadOnly(true);
        procClass.setWidthFull();

        FormLayout form = new FormLayout(subject, cost, procClass);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0px", 1),
                new FormLayout.ResponsiveStep("700px", 2)
        );

        Grid<BudgetItems> preview = createBudgetItemPreviewGrid();
        preview.setItems(items);

        Button combine = primaryButton("Combine");

        combine.addClickListener(e -> {

            if (subject.isEmpty() || subject.getValue().trim().isEmpty()) {
                warn("Subject is required.");
                return;
            }

            ProcurementPlan combinedPlan = new ProcurementPlan();
            combinedPlan.setSubject(subject.getValue().trim());
            combinedPlan.setBudget(firstPlan.getBudget());
            combinedPlan.setCurrency(firstPlan.getCurrency());
            combinedPlan.setProcClass(section.procClass.getValue());
            combinedPlan.setCost(totalCost);
            combinedPlan.setProcPlanBudgetItems(new HashSet<>(items));

            // If all items have same COA, keep it.
            // If mixed COAs, leave plan COA null so BudgetItems retain their own COA.
            combinedPlan.setCoa(representativeCoa);

            selectedPlans.forEach(procurementPlanService::deleteProcurementPlan);

            procurementPlanService.save(combinedPlan);

            refreshAllSectionsAfterTransfer();
            clearAllSelections();

            dialog.close();
            success("Selected procurement plans combined successfully.");
        });

        dialog.add(new VerticalLayout(form, preview));

        dialog.getFooter().add(
                new Button("Cancel", e -> dialog.close()),
                combine
        );

        dialog.open();
    }

    private void openChangeClassDialog(PlanSection section) {
        Set<ProcurementPlan> selectedPlans = section.planGrid.getSelectedItems();

        if (selectedPlans == null || selectedPlans.isEmpty()) {
            warn("Select at least one procurement plan item.");
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Change Procurement Class");
        dialog.setModal(true);
        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.setWidth("420px");

        ComboBox<ProcClass> targetClass = new ComboBox<>("Procurement Class");
        targetClass.setItems(
                ProcClass.Supplies,
                ProcClass.Works,
                ProcClass.Non_Consultancy,
                ProcClass.Consultancy,
                ProcClass.Disposal,
                ProcClass.Other
        );
        targetClass.setValue(section.procClass.getValue());
        targetClass.setRequiredIndicatorVisible(true);
        targetClass.setWidthFull();

        Button save = primaryButton("Change");

        save.addClickListener(e -> {
            ProcClass newClass = targetClass.getValue();

            if (newClass == null) {
                warn("Select procurement class.");
                return;
            }

            int updatedPlans = 0;
            int updatedItems = 0;
            int skipped = 0;

            for (ProcurementPlan plan : selectedPlans) {
                if (plan == null) {
                    skipped++;
                    continue;
                }

                List<BudgetItems> realItems = safeBudgetItems(plan).stream()
                        .filter(Objects::nonNull)
                        .filter(item -> item.getId() != null && item.getId() > 0)
                        .collect(Collectors.toList());

                if (realItems.isEmpty()) {
                    plan.setProcClass(newClass);
                    procurementPlanService.save(plan);
                    updatedPlans++;
                    continue;
                }

                for (BudgetItems item : realItems) {
                    item.setProcClass(newClass);
                    budgetItemsService.saveBudgetItem(item);
                    updatedItems++;
                }

                plan.setProcClass(newClass);
                plan.setProcPlanBudgetItems(new HashSet<>(realItems));
                plan.setCost(sumBudgetItems(realItems));

                procurementPlanService.save(plan);
                updatedPlans++;
            }

            refreshAllSectionsAfterTransfer();
            clearAllSelections();

            dialog.close();

            success("Procurement class updated. Plans: "
                    + updatedPlans
                    + ", Budget items: "
                    + updatedItems
                    + ", Skipped: "
                    + skipped
                    + ".");
        });

        dialog.add(new VerticalLayout(targetClass));

        dialog.getFooter().add(
                new Button("Cancel", e -> dialog.close()),
                save
        );

        dialog.open();
    }

    private void transferSelectedBudgetItems(PlanSection section, ProcClass targetClass) {
        Set<BudgetItems> selectedItems = new HashSet<>(section.budgetItemsGrid.getSelectedItems());
        Set<ProcurementPlan> selectedPlans = section.planGrid.getSelectedItems();

        if (selectedItems.isEmpty()) {
            warn("Select at least one budget item to transfer.");
            return;
        }

        if (selectedPlans.size() != 1) {
            warn("Select exactly one procurement plan item as the source.");
            return;
        }

        if (targetClass == null) {
            warn("Select a valid target procurement class.");
            return;
        }

        boolean hasGroupedRow = selectedItems.stream()
                .anyMatch(this::isSyntheticGroupRow);

        if (hasGroupedRow) {
            warn("Grouped rows cannot be transferred directly. Ungroup first or transfer the original budget items.");
            return;
        }

        ProcurementPlan sourcePlan = selectedPlans.iterator().next();

        if (Objects.equals(sourcePlan.getProcClass(), targetClass)) {
            warn("The selected budget item(s) are already under " + label(targetClass) + ".");
            return;
        }

        Set<BudgetItems> sourceItems = new HashSet<>(safeBudgetItems(sourcePlan));

        if (!sourceItems.containsAll(selectedItems)) {
            warn("Some selected budget items do not belong to the selected procurement plan.");
            return;
        }

        // Remove selected items from source plan
        Set<BudgetItems> remainingItems = new HashSet<>(sourceItems);
        remainingItems.removeAll(selectedItems);

        // Update selected budget items class first
        for (BudgetItems item : selectedItems) {
            item.setProcClass(targetClass);
            budgetItemsService.saveBudgetItem(item);
        }

        // Delete source plan if empty, otherwise update it
        if (remainingItems.isEmpty()) {
            procurementPlanService.deleteProcurementPlan(sourcePlan);
        } else {
            sourcePlan.setProcPlanBudgetItems(remainingItems);
            sourcePlan.setCost(sumBudgetItems(remainingItems));
            procurementPlanService.save(sourcePlan);
        }

        // Add selected items to compatible target plan, or create a new one
        ProcurementPlan targetPlan = findCompatibleTargetPlan(section, sourcePlan, targetClass)
                .orElseGet(() -> createTargetPlanSkeleton(sourcePlan, targetClass));

        Set<BudgetItems> targetItems = new HashSet<>(safeBudgetItems(targetPlan));
        targetItems.addAll(selectedItems);

        targetPlan.setProcPlanBudgetItems(targetItems);
        targetPlan.setCost(sumBudgetItems(targetItems));
        targetPlan.setProcClass(targetClass);

        procurementPlanService.save(targetPlan);

        // Refresh both grids and clear selection
        refreshAllSectionsAfterTransfer();
        clearAllSelections();

        // Force current section grids to reload cleanly
        refreshSection(section);
        section.planGrid.getDataProvider().refreshAll();
        section.budgetItemsGrid.setItems(Collections.emptyList());
        resetBudgetItemsFooter(section);

        success("Transferred " + selectedItems.size() + " budget item(s) to " + label(targetClass) + ".");
    }

    private void clearAllSelections() {
        consultancySection.planGrid.deselectAll();
        consultancySection.budgetItemsGrid.deselectAll();
        consultancySection.budgetItemsGrid.setItems(Collections.emptyList());

        otherSection.planGrid.deselectAll();
        otherSection.budgetItemsGrid.deselectAll();
        otherSection.budgetItemsGrid.setItems(Collections.emptyList());
    }

    private void refreshAllSectionsAfterTransfer() {
        syncSectionBudgetIfEmpty(consultancySection, otherSection);
        syncSectionBudgetIfEmpty(otherSection, consultancySection);

        refreshSection(consultancySection);
        refreshSection(otherSection);
    }

    private void syncSectionBudgetIfEmpty(PlanSection target, PlanSection source) {
        if (target.budget.isEmpty() && !source.budget.isEmpty()) {
            target.budget.setValue(source.budget.getValue());
        }
    }

    private Optional<ProcurementPlan> findCompatibleTargetPlan(
            PlanSection section,
            ProcurementPlan sourcePlan,
            ProcClass targetClass
    ) {
        if (section.budget.isEmpty() || sourcePlan == null || sourcePlan.getCoa() == null) {
            return Optional.empty();
        }

        List<ProcurementPlan> targetPlans = procurementPlanService.findByBudgetAndProcClass(
                section.budget.getValue(),
                targetClass
        );

        return targetPlans.stream()
                .filter(plan -> Objects.equals(plan.getCoa(), sourcePlan.getCoa()))
                .findFirst();
    }

    private ProcurementPlan createTargetPlanSkeleton(ProcurementPlan sourcePlan, ProcClass targetClass) {
        ProcurementPlan targetPlan = new ProcurementPlan();

        targetPlan.setBudget(sourcePlan.getBudget());
        targetPlan.setCoa(sourcePlan.getCoa());
        targetPlan.setCurrency(sourcePlan.getCurrency());
        targetPlan.setProcClass(targetClass);

        targetPlan.setSubject(
                sourcePlan.getCoa() == null
                ? sourcePlan.getSubject()
                : sourcePlan.getCoa().getName()
        );

        targetPlan.setCost(BigDecimal.ZERO);
        targetPlan.setProcPlanBudgetItems(new HashSet<>());

        return targetPlan;
    }

    private void regenerateProcurementPlan(PlanSection section) {
        if (section.budget.isEmpty()) {
            warn("Select a financial year first.");
            return;
        }

        Budget selectedBudget = section.budget.getValue();

        List<BudgetItems> procurementItems
                = budgetItemsService.findProcurementBudgetItemsByBudget(selectedBudget);

        Map<String, List<BudgetItems>> groupedItems = procurementItems.stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getId() != null)
                .filter(item -> item.getCoacode() != null)
                .filter(item -> item.getCoacode().getId() != null)
                .filter(item -> item.getCoacode().getCode() != null)
                .filter(item -> item.getCoacode().getCode().startsWith("2")
                || item.getCoacode().getCode().startsWith("3"))
                .filter(item -> item.getProcClass() != null)
                .collect(Collectors.groupingBy(
                        item -> planKey(item.getProcClass(), item.getCoacode()),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        Currency ugx = currencyService.findCurrenciesByCurrencyShortAndBudget(
                "UGX",
                selectedBudget
        );

        List<ProcurementPlan> existingPlans
                = procurementPlanService.findProcurementPlansForExport(selectedBudget);

        Map<String, ProcurementPlan> existingPlanMap = existingPlans.stream()
                .filter(Objects::nonNull)
                .filter(plan -> plan.getCoa() != null)
                .filter(plan -> plan.getProcClass() != null)
                .collect(Collectors.toMap(
                        plan -> planKey(plan.getProcClass(), plan.getCoa()),
                        Function.identity(),
                        (existing, duplicate) -> existing,
                        LinkedHashMap::new
                ));

        int created = 0;
        int updated = 0;
        int skipped = 0;

        for (Map.Entry<String, List<BudgetItems>> entry : groupedItems.entrySet()) {
            List<BudgetItems> budgetItems = entry.getValue();

            if (budgetItems == null || budgetItems.isEmpty()) {
                skipped++;
                continue;
            }

            BudgetItems first = budgetItems.get(0);
            COA coa = first.getCoacode();
            ProcClass procClass = first.getProcClass();

            BigDecimal total = sumBudgetItemsMonths(budgetItems);

            if (nz(total).compareTo(BigDecimal.ZERO) <= 0) {
                skipped++;
                continue;
            }

            ProcurementPlan plan = existingPlanMap.get(entry.getKey());

            if (plan == null) {
                plan = new ProcurementPlan();
                plan.setBudget(selectedBudget);
                plan.setCurrency(ugx);
                plan.setProcClass(procClass);
                plan.setCoa(coa);
                plan.setSubject(coa.getName());
                created++;
            } else {
                updated++;

                if (plan.getCurrency() == null) {
                    plan.setCurrency(ugx);
                }

                if (plan.getSubject() == null || plan.getSubject().isBlank()) {
                    plan.setSubject(coa.getName());
                }
            }

            // Only update generated fields
            plan.setCost(total);
            plan.setProcPlanBudgetItems(new HashSet<>(budgetItems));

            // Do NOT reset manual fields here:
            // procurementMethod
            // procurementtype
            // prequal
            // reserve
            // dates
            // subject if user edited it
            procurementPlanService.save(plan);
        }

        refreshAllSectionsAfterTransfer();

        success("Regeneration completed. Created "
                + created
                + ", updated "
                + updated
                + ", skipped "
                + skipped
                + ".");
    }

    private void regenerateProcurementPlan2(PlanSection section) {
        if (section.budget.isEmpty()) {
            warn("Select a financial year first.");
            return;
        }

        Budget selectedBudget = section.budget.getValue();
        procurementPlanService.deleteProcurementPlanByBudget(selectedBudget);

        List<BudgetItems> procurementItems
                = budgetItemsService.findProcurementBudgetItemsByBudget(selectedBudget);

        Map<String, List<BudgetItems>> groupedItems = procurementItems.stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getCoacode() != null)
                .filter(item -> item.getCoacode().getCode() != null)
                .filter(item -> item.getCoacode().getCode().startsWith("2")
                || item.getCoacode().getCode().startsWith("3"))
                .filter(item -> item.getProcClass() != null)
                .collect(Collectors.groupingBy(
                        item -> item.getCoacode().getId() + "::" + item.getProcClass().name(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        Currency ugx = currencyService.findCurrenciesByCurrencyShortAndBudget(
                "UGX",
                selectedBudget
        );

        int created = 0;
        int skipped = 0;

        for (List<BudgetItems> budgetItems : groupedItems.values()) {
            if (budgetItems.isEmpty()) {
                skipped++;
                continue;
            }

            BudgetItems first = budgetItems.get(0);
            COA coa = first.getCoacode();
            ProcClass procClass = first.getProcClass();

            BigDecimal total = sumBudgetItemsMonths(budgetItems);

            if (nz(total).compareTo(BigDecimal.ZERO) <= 0) {
                skipped++;
                continue;
            }

            ProcurementPlan plan = new ProcurementPlan();
            plan.setBudget(selectedBudget);
            plan.setCurrency(ugx);
            plan.setSubject(coa.getName());
            plan.setProcClass(procClass);
            plan.setCoa(coa);
            plan.setCost(total);
            plan.setProcPlanBudgetItems(new HashSet<>(budgetItems));

            procurementPlanService.save(plan);
            created++;
        }

        refreshAllSectionsAfterTransfer();

        success("Regeneration completed. Created "
                + created
                + " procurement plan item(s), skipped "
                + skipped
                + " item(s).");
    }

    private BigDecimal sumBudgetItemsMonths(List<BudgetItems> items) {

        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return items.stream()
                .filter(Objects::nonNull)
                .map(item
                        -> nz(item.getJul())
                        .add(nz(item.getAug()))
                        .add(nz(item.getSep()))
                        .add(nz(item.getOct()))
                        .add(nz(item.getNov()))
                        .add(nz(item.getDec()))
                        .add(nz(item.getJan()))
                        .add(nz(item.getFeb()))
                        .add(nz(item.getMar()))
                        .add(nz(item.getApr()))
                        .add(nz(item.getMay()))
                        .add(nz(item.getJun()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String planKey(ProcClass procClass, COA coa) {
        return procClass.name() + "::" + coa.getId();
    }

    private boolean isProcurementCoaCode(COA coa) {
        if (coa == null || coa.getCode() == null) {
            return false;
        }

        String code = coa.getCode().trim();
        return code.startsWith("2") || code.startsWith("3");
    }

    private boolean validateBudgetAndClass(PlanSection section) {
        if (section.budget.isEmpty() || section.procClass.isEmpty()) {
            warn("Select a financial year and procurement class.");
            return false;
        }

        return true;
    }

    private boolean validateRequired(TextField subject, ComboBox<?> method) {
        if (subject.isEmpty()) {
            warn("Subject of procurement is required.");
            return false;
        }

        if (method.isEmpty()) {
            warn("Procurement method is required.");
            return false;
        }

        return true;
    }

    private void setActionsEnabled(PlanSection section, boolean enabled) {
        if (section.planContextMenu != null) {
            section.planContextMenu.setEnabled(enabled);
        }

        if (section.itemContextMenu != null) {
            section.itemContextMenu.setEnabled(enabled);
        }
    }

    private void downloadSelectedBudgetItems(PlanSection section, Anchor anchor) {
        List<BudgetItems> items = new ArrayList<>();
        section.budgetItemsGrid.getDataProvider().fetch(new Query<>()).forEach(items::add);

        if (items.isEmpty()) {
            warn("No budget items to export.");
            return;
        }

        StreamResource resource = new StreamResource(
                generateFileName(),
                () -> new BudgetItemExcelExporter().exportBudgetItemsToExcel(items)
        );

        anchor.setHref(resource);
        anchor.getElement().executeJs("this.click()");
    }

    private String generateFileName() {
        return "Budget_Items_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                + ".xlsx";
    }

    private DatePicker datePicker(String label, LocalDate value) {
        DatePicker picker = new DatePicker(label);
        picker.setValue(value);
        picker.setWidthFull();
        return picker;
    }

    private Component sectionTitle(String title) {
        Div div = new Div(new Text(title));

        div.getStyle()
                .set("font-weight", "700")
                .set("font-size", "var(--lumo-font-size-m)")
                .set("padding-top", "var(--lumo-space-m)")
                .set("border-top", "1px solid var(--lumo-contrast-20pct)");

        return div;
    }

    private Button primaryButton(String text) {
        Button button = new Button(text);
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return button;
    }

    private Span badgeText(String text) {
        Span span = new Span(nvl(text));
        span.getElement().getThemeList().add("badge success");
        return span;
    }

    private String safeCoaCode(ProcurementPlan plan) {
        return plan == null || plan.getCoa() == null
                ? ""
                : nvl(plan.getCoa().getCode());
    }

    private String safeCoaName(ProcurementPlan plan) {
        if (plan == null) {
            return "";
        }

        if (plan.getCoa() != null) {
            return nvl(plan.getCoa().getName());
        }

        return safeBudgetItems(plan).stream()
                .map(BudgetItems::getCoacode)
                .filter(Objects::nonNull)
                .map(COA::getName)
                .filter(Objects::nonNull)
                .filter(name -> !name.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.joining(", "));
    }

    private String getFundSources(ProcurementPlan plan) {
        return safeBudgetItems(plan).stream()
                .map(BudgetItems::getFundsource)
                .filter(Objects::nonNull)
                .map(Fundsource::getFundsource)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.joining(", "));
    }

    private List<BudgetItems> safeBudgetItems(ProcurementPlan plan) {
        if (plan == null || plan.getProcPlanBudgetItems() == null) {
            return Collections.emptyList();
        }

        return new ArrayList<>(plan.getProcPlanBudgetItems());
    }

    private boolean allHaveSameCoa(Set<ProcurementPlan> plans) {
        if (plans == null || plans.isEmpty()) {
            return false;
        }

        COA first = plans.iterator().next().getCoa();

        return plans.stream()
                .allMatch(plan -> Objects.equals(first, plan.getCoa()));
    }

    private BigDecimal sumPlans(Collection<ProcurementPlan> plans) {
        if (plans == null) {
            return BigDecimal.ZERO;
        }

        return plans.stream()
                .map(ProcurementPlan::getCost)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumBudgetItems(Collection<BudgetItems> items) {
        if (items == null) {
            return BigDecimal.ZERO;
        }

        return items.stream()
                .map(this::sumBudgetItem)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumBudgetItem(BudgetItems item) {
        if (item == null) {
            return BigDecimal.ZERO;
        }

        return nz(item.getJul())
                .add(nz(item.getAug()))
                .add(nz(item.getSep()))
                .add(nz(item.getOct()))
                .add(nz(item.getNov()))
                .add(nz(item.getDec()))
                .add(nz(item.getJan()))
                .add(nz(item.getFeb()))
                .add(nz(item.getMar()))
                .add(nz(item.getApr()))
                .add(nz(item.getMay()))
                .add(nz(item.getJun()));
    }

    private BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String nvl(String value) {
        return value == null ? "" : value;
    }

    private String money(BigDecimal value) {
        return moneyFormat.format(nz(value));
    }

    private String boolText(Boolean value) {
        return Boolean.TRUE.equals(value) ? "Yes" : "No";
    }

    private String label(ProcClass procClass) {
        return procClass == null ? "" : procClass.name().replace('_', ' ');
    }

    private boolean isAdminOrProcurement() {
        return currentUser != null
                && currentUser.getRoles() != null
                && (currentUser.getRoles().contains(Role.ADMIN)
                || currentUser.getRoles().contains(Role.PROCUREMENT));
    }

    private void warn(String message) {
        Notification notification = Notification.show(
                message,
                4000,
                Notification.Position.TOP_CENTER
        );
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
    }

    private void success(String message) {
        Notification notification = Notification.show(
                message,
                3000,
                Notification.Position.TOP_CENTER
        );
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void exportAndDownloadExcelWorkplan(ProcClass procClass) {
        PlanSection section = findSectionByProcClass(procClass);

        if (section == null || section.budget.isEmpty()) {
            warn("Select a financial year and procurement class first.");
            return;
        }

        List<ProcurementPlan> plans = procurementPlanService.findByBudgetAndProcClass(
                section.budget.getValue(),
                procClass
        );

        if (plans.isEmpty()) {
            warn("No procurement plan records to export.");
            return;
        }

        String fileName = "Procurement_Workplan_"
                + procClass.name()
                + "_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                + ".xlsx";

        downloadStream(
                fileName,
                () -> buildProcurementPlanWorkbook(
                        plans,
                        "Procurement Workplan - " + procClass.name()
                )
        );
    }

    private void exportAndDownloadExcelProcurementPlanSheets(Budget budget) {

        if (budget == null) {
            warn("Select a financial year first.");
            return;
        }

        String fileName = "Procurement_Plan_"
                + safeBudgetName(budget)
                + "_"
                + LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
                )
                + ".xlsx";

        downloadStream(
                fileName,
                () -> buildProcurementPlanTemplateWorkbook(budget)
        );
    }

    private InputStream buildProcurementPlanTemplateWorkbook(Budget budget) {
        try (
                InputStream template = getClass()
                        .getResourceAsStream("/templates/procurement-plan-template.xlsx"); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    if (template == null) {
                        throw new IllegalStateException(
                                "Excel template not found: /templates/procurement-plan-template.xlsx"
                        );
                    }

                    try (Workbook workbook = WorkbookFactory.create(template)) {

                        Sheet otherTemplate = workbook.getSheet("SUPS-WRKS-NCONS");
                        Sheet consultancySheet = workbook.getSheet("Consultancy");

                        if (otherTemplate == null) {
                            throw new IllegalStateException("Template sheet SUPS-WRKS-NCONS not found.");
                        }

                        if (consultancySheet == null) {
                            throw new IllegalStateException("Template sheet Consultancy not found.");
                        }

                        List<ProcurementPlan> plans
                                = procurementPlanService.findProcurementPlansForExport(budget);

                        List<BudgetItems> allItems = plans.stream()
                                .flatMap(plan -> safeBudgetItems(plan).stream())
                                .filter(Objects::nonNull)
                                .filter(item -> item.getId() != null)
                                .filter(item -> item.getCoacode() != null)
                                .filter(item -> isProcurementCoaCode(item.getCoacode()))
                                .collect(Collectors.toMap(
                                        BudgetItems::getId,
                                        Function.identity(),
                                        (existing, duplicate) -> existing,
                                        LinkedHashMap::new
                                ))
                                .values()
                                .stream()
                                .collect(Collectors.toList());

                        List<ProcurementBudgetItemGroup> groups
                                = procurementBudgetItemGroupService.findByBudgetWithItems(budget)
                                        .stream()
                                        .filter(Objects::nonNull)
                                        .filter(group -> group.getId() != null)
                                        .collect(Collectors.toMap(
                                                ProcurementBudgetItemGroup::getId,
                                                Function.identity(),
                                                (existing, duplicate) -> existing,
                                                LinkedHashMap::new
                                        ))
                                        .values()
                                        .stream()
                                        .collect(Collectors.toList());

                        Set<Long> groupedItemIds = groups.stream()
                                .filter(group -> group.getItems() != null)
                                .flatMap(group -> group.getItems().stream())
                                .filter(Objects::nonNull)
                                .map(BudgetItems::getId)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toSet());

                        List<BudgetItems> ungroupedItems = allItems.stream()
                                .filter(item -> !groupedItemIds.contains(item.getId()))
                                .collect(Collectors.toList());

                        List<BudgetItems> exportItems = new ArrayList<>();

                        groups.stream()
                                .map(this::toSyntheticGroupedBudgetItem)
                                .filter(Objects::nonNull)
                                .forEach(exportItems::add);

                        exportItems.addAll(ungroupedItems);

                        exportItems = exportItems.stream()
                                .filter(Objects::nonNull)
                                .filter(item -> item.getId() != null)
                                .collect(Collectors.toMap(
                                        BudgetItems::getId,
                                        Function.identity(),
                                        (existing, duplicate) -> existing,
                                        LinkedHashMap::new
                                ))
                                .values()
                                .stream()
                                .collect(Collectors.toList());

                        List<BudgetItems> suppliesItems
                                = filterByProcClass(exportItems, ProcClass.Supplies);

                        List<BudgetItems> worksItems
                                = filterByProcClass(exportItems, ProcClass.Works);

                        List<BudgetItems> nonConsultancyItems
                                = filterByProcClass(exportItems, ProcClass.Non_Consultancy);

                        List<BudgetItems> consultancyItems
                                = filterByProcClass(exportItems, ProcClass.Consultancy);

                        Sheet suppliesSheet = otherTemplate;
                        workbook.setSheetName(
                                workbook.getSheetIndex(suppliesSheet),
                                "Supplies"
                        );

                        Sheet worksSheet = cloneSheet(workbook, otherTemplate, "Works");

                        Sheet nonConsultancySheet = cloneSheet(
                                workbook,
                                otherTemplate,
                                "Non Consultancy"
                        );

                        workbook.setSheetName(
                                workbook.getSheetIndex(consultancySheet),
                                "Consultancy"
                        );

                        setFinancialYear(suppliesSheet, budget);
                        setFinancialYear(worksSheet, budget);
                        setFinancialYear(nonConsultancySheet, budget);
                        setFinancialYear(consultancySheet, budget);

                        populateOtherTemplateSheet(suppliesSheet, suppliesItems);
                        populateOtherTemplateSheet(worksSheet, worksItems);
                        populateOtherTemplateSheet(nonConsultancySheet, nonConsultancyItems);
                        populateConsultancyTemplateSheet(consultancySheet, consultancyItems);

                        Sheet notes = workbook.getSheet("User guiding notes");
                        if (notes != null) {
                            workbook.removeSheetAt(workbook.getSheetIndex(notes));
                        }
                        applyWorkbookFormatting(workbook);
                        workbook.write(out);
                    }

                    return new ByteArrayInputStream(out.toByteArray());

                } catch (Exception ex) {
                    throw new RuntimeException(
                            "Failed to generate procurement plan workbook",
                            ex
                    );
                }
    }

    private List<BudgetItems> filterByProcClass(List<BudgetItems> items, ProcClass procClass) {
        return items.stream()
                .filter(item -> item.getProcClass() == procClass)
                .collect(Collectors.toList());
    }

    private Sheet cloneSheet(Workbook workbook, Sheet templateSheet, String newName) {
        int templateIndex = workbook.getSheetIndex(templateSheet);
        Sheet cloned = workbook.cloneSheet(templateIndex);

        int clonedIndex = workbook.getSheetIndex(cloned);
        workbook.setSheetName(clonedIndex, newName);

        return cloned;
    }

    private void populateOtherTemplateSheet(Sheet sheet, List<BudgetItems> items) {
        if (sheet == null || items == null || items.isEmpty()) {
            return;
        }

        int startRow = 10;          // Excel row 11
        int templateRowIndex = 10;
        int rowsToInsert = items.size() + 1; // +1 for Section row

        preserveRowsBelow(sheet, startRow, rowsToInsert);

        Row templateRow = sheet.getRow(templateRowIndex + rowsToInsert);

        int count = 1;
        int rowIndex = startRow;

        for (BudgetItems item : items) {
            Row row = sheet.createRow(rowIndex++);
            copyRowStyle(templateRow, row);
            applyBlackFontAndLeftAlignment(row, sheet.getWorkbook());

            BigDecimal itemTotal = nz(sumBudgetItem(item));

            createCell(row, 0, count++);
            createCell(row, 1, nvl(item.getItem()));
            createCell(row, 2, "UGX");
            createCell(row, 3, itemTotal.doubleValue());

            createCell(row, 4, nz(item.getCurrentYearEstimatedCost()).doubleValue());
            createCell(row, 5, item.getProjectedCompletionTimeYears());
            createCell(row, 6, nz(item.getPaidUpSum()).doubleValue());
            createCell(row, 7, nz(item.getPendingSum()).doubleValue());
            createCell(row, 8, nvl(item.getPendingTimeToCompletion()));

            createCell(row, 9, item.getFundsource() == null ? "" : nvl(item.getFundsource().getFundsource()));
            createCell(row, 10, item.getProcurementMethod() == null ? "" : nvl(item.getProcurementMethod().getProcuremntMethod()));
            createCell(row, 11, item.getProcClass() == null ? "" : label(item.getProcClass()));
            createCell(row, 12, item.getProcurementType() == null ? "" : nvl(item.getProcurementType().getProcuremntType()));

            createCell(row, 13, boolText(item.getPrequalification()));
            createCell(row, 14, boolText(item.getReserveScheme()));
            createCell(row, 15, nvl(item.getReservationSchemeDetails()));

            createCell(row, 16, dateText(item.getBidInvitationDate()));
            createCell(row, 17, dateText(item.getBidClosingOpeningDate()));
            createCell(row, 18, dateText(item.getEvaluationApprovalDate()));
            createCell(row, 19, dateText(item.getAwardNotificationDate()));
            createCell(row, 20, dateText(item.getContractSigningDate()));
            createCell(row, 21, dateText(item.getCompletionDate()));
            createCell(row, 23, sectionName(item));
        }

    }

    private void populateConsultancyTemplateSheet(Sheet sheet, List<BudgetItems> items) {
        if (sheet == null || items == null || items.isEmpty()) {
            return;
        }

        int startRow = 9;           // Excel row 10
        int templateRowIndex = 9;
        int rowsToInsert = items.size() + 1; // +1 for Section row

        preserveRowsBelow(sheet, startRow, rowsToInsert);

        Row templateRow = sheet.getRow(templateRowIndex + rowsToInsert);

        int count = 1;
        int rowIndex = startRow;

        for (BudgetItems item : items) {
            Row row = sheet.createRow(rowIndex++);
            copyRowStyle(templateRow, row);
            applyBlackFontAndLeftAlignment(row, sheet.getWorkbook());

            BigDecimal itemTotal = nz(sumBudgetItem(item));

            createCell(row, 0, count++);
            createCell(row, 1, nvl(item.getItem()));
            createCell(row, 2, "UGX");
            createCell(row, 3, itemTotal.doubleValue());

            createCell(row, 4, nz(item.getCurrentYearEstimatedCost()).doubleValue());
            createCell(row, 5, item.getProjectedCompletionTimeYears());
            createCell(row, 6, nz(item.getPaidUpSum()).doubleValue());
            createCell(row, 7, nz(item.getPendingSum()).doubleValue());
            createCell(row, 8, nvl(item.getPendingTimeToCompletion()));

            createCell(row, 9, item.getFundsource() == null ? "" : nvl(item.getFundsource().getFundsource()));
            createCell(row, 10, item.getProcurementMethod() == null ? "" : nvl(item.getProcurementMethod().getProcuremntMethod()));
            createCell(row, 11, item.getProcClass() == null ? "" : label(item.getProcClass()));

            createCell(row, 12, boolText(item.getReserveScheme()));
            createCell(row, 13, nvl(item.getReservationSchemeDetails()));
            createCell(row, 14, item.getProcurementType() == null ? "" : nvl(item.getProcurementType().getProcuremntType()));

            createCell(row, 15, dateText(item.getBidInvitationDate()));
            createCell(row, 16, dateText(item.getBidClosingOpeningDate()));
            createCell(row, 17, dateText(item.getEvaluationApprovalDate()));
            createCell(row, 18, dateText(item.getAwardNotificationDate()));

            createCell(row, 19, dateText(item.getProposalInvitationDate()));
            createCell(row, 20, dateText(item.getProposalSubmissionOpeningDate()));
            createCell(row, 21, dateText(item.getFinalEvaluationApprovalDate()));
            createCell(row, 22, dateText(item.getFinalNotificationDate()));
            createCell(row, 23, dateText(item.getContractSigningDate()));
            createCell(row, 24, dateText(item.getCompletionDate()));
            createCell(row, 25, sectionName(item));
        }

    }

    private void preserveRowsBelow(Sheet sheet, int startRow, int rowsToInsert) {

        if (rowsToInsert <= 1) {
            return;
        }

        int lastRow = sheet.getLastRowNum();

        // shift everything below the template row
        sheet.shiftRows(
                startRow + 1,
                lastRow,
                rowsToInsert - 1,
                true,
                false
        );

        // clone merged regions that were shifted
        List<CellRangeAddress> mergedRegions = new ArrayList<>();

        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {

            CellRangeAddress region = sheet.getMergedRegion(i);

            if (region.getFirstRow() >= startRow + 1) {

                mergedRegions.add(
                        new CellRangeAddress(
                                region.getFirstRow() + rowsToInsert - 1,
                                region.getLastRow() + rowsToInsert - 1,
                                region.getFirstColumn(),
                                region.getLastColumn()
                        )
                );
            }
        }

        // remove old merged regions below insertion point
        for (int i = sheet.getNumMergedRegions() - 1; i >= 0; i--) {

            CellRangeAddress region = sheet.getMergedRegion(i);

            if (region.getFirstRow() >= startRow + 1) {
                sheet.removeMergedRegion(i);
            }
        }

        // add shifted merged regions back
        for (CellRangeAddress region : mergedRegions) {
            sheet.addMergedRegion(region);
        }
    }

    private void applyBlackFontAndLeftAlignment(Row row, Workbook workbook) {

        if (row == null || workbook == null) {
            return;
        }

        for (Cell cell : row) {

            if (cell == null) {
                continue;
            }

            CellStyle originalStyle = cell.getCellStyle();

            if (originalStyle == null) {
                continue;
            }

            CellStyle cachedStyle = blackStyleCache.get(originalStyle);

            if (cachedStyle == null) {

                Font originalFont = workbook.getFontAt(
                        originalStyle.getFontIndex()
                );

                Font blackFont = workbook.createFont();

                blackFont.setFontName(originalFont.getFontName());
                blackFont.setFontHeight(originalFont.getFontHeight());
                blackFont.setBold(originalFont.getBold());
                blackFont.setItalic(originalFont.getItalic());

                blackFont.setColor(IndexedColors.BLACK.getIndex());

                CellStyle newStyle = workbook.createCellStyle();
                newStyle.cloneStyleFrom(originalStyle);

                newStyle.setFont(blackFont);

                cachedStyle = newStyle;

                blackStyleCache.put(originalStyle, cachedStyle);
            }

            cell.setCellStyle(cachedStyle);
        }

        Cell subjectCell = row.getCell(1);

        if (subjectCell != null) {

            CellStyle subjectOriginal = subjectCell.getCellStyle();

            CellStyle leftStyle = workbook.createCellStyle();
            leftStyle.cloneStyleFrom(subjectOriginal);

            leftStyle.setAlignment(HorizontalAlignment.LEFT);

            Font originalFont = workbook.getFontAt(
                    subjectOriginal.getFontIndex()
            );

            Font blackFont = workbook.createFont();

            blackFont.setFontName(originalFont.getFontName());
            blackFont.setFontHeight(originalFont.getFontHeight());
            blackFont.setBold(originalFont.getBold());

            blackFont.setColor(IndexedColors.BLACK.getIndex());

            leftStyle.setFont(blackFont);

            subjectCell.setCellStyle(leftStyle);
        }
    }

    private String sectionName(BudgetItems item) {
        return item == null || item.getDeptUnit() == null
                ? ""
                : nvl(item.getDeptUnit().getNAME());
    }

    private String getDistinctSectionNames(List<BudgetItems> items) {
        if (items == null || items.isEmpty()) {
            return "";
        }

        return items.stream()
                .map(this::sectionName)
                .filter(s -> s != null && !s.isBlank())
                .distinct()
                .collect(Collectors.joining(", "));
    }

    private void createCell(Row row, int column, Object value) {
        Cell cell = row.getCell(column);

        if (cell == null) {
            cell = row.createCell(column);
        }

        if (value == null) {
            cell.setCellValue("");
            return;
        }

        if (value instanceof BigDecimal bd) {
            cell.setCellValue(bd.doubleValue());
        } else if (value instanceof Number number) {
            cell.setCellValue(number.doubleValue());
        } else if (value instanceof Boolean bool) {
            cell.setCellValue(Boolean.TRUE.equals(bool) ? "Yes" : "No");
        } else if (value instanceof LocalDate date) {
            cell.setCellValue(date.toString());
        } else {
            cell.setCellValue(String.valueOf(value));
        }
    }

    private void copyRowStyle(Row sourceRow, Row targetRow) {

        if (sourceRow == null || targetRow == null) {
            return;
        }

        targetRow.setHeight(sourceRow.getHeight());

        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {

            Cell sourceCell = sourceRow.getCell(i);

            if (sourceCell == null) {
                continue;
            }

            Cell targetCell = targetRow.createCell(i);

            // REUSE EXISTING STYLE
            targetCell.setCellStyle(sourceCell.getCellStyle());

            // copy cell type if needed
            switch (sourceCell.getCellType()) {
                case STRING ->
                    targetCell.setCellValue(sourceCell.getStringCellValue());

                case NUMERIC ->
                    targetCell.setCellValue(sourceCell.getNumericCellValue());

                case BOOLEAN ->
                    targetCell.setCellValue(sourceCell.getBooleanCellValue());

                case FORMULA ->
                    targetCell.setCellFormula(sourceCell.getCellFormula());

                default -> {
                }
            }
        }
    }

    private void setFinancialYear(Sheet sheet, Budget budget) {

        if (sheet == null || budget == null) {
            return;
        }

        // Excel row 6 = POI row index 5
        Row row = sheet.getRow(5);

        if (row == null) {
            row = sheet.createRow(5);
        }

        // Adjust column index depending on your template
        // Example uses column D = index 3
        Cell cell = row.getCell(3);

        if (cell == null) {
            cell = row.createCell(3);
        }

        cell.setCellValue(nvl(budget.getFinancialYear()));
    }

    private PlanSection findSectionByProcClass(ProcClass procClass) {
        if (procClass == null) {
            return null;
        }

        if (consultancySection.allowedClasses.contains(procClass)) {
            return consultancySection;
        }

        if (otherSection.allowedClasses.contains(procClass)) {
            return otherSection;
        }

        return null;
    }

    private String safeBudgetName(Budget budget) {
        if (budget == null || budget.getFinancialYear() == null) {
            return "Budget";
        }

        return budget.getFinancialYear().replaceAll("[^a-zA-Z0-9_-]", "_");
    }

    private interface ExcelStreamFactory {

        InputStream create();
    }

    private void downloadStream(String fileName, ExcelStreamFactory factory) {
        StreamResource resource = new StreamResource(fileName, factory::create);

        resource.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );

        Anchor anchor = new Anchor(resource, "");
        anchor.getElement().setAttribute("download", fileName);
        anchor.getStyle().set("display", "none");

        add(anchor);

        anchor.getElement().executeJs(
                "this.click(); setTimeout(() => this.remove(), 1000);"
        );
    }

    private InputStream buildProcurementPlanWorkbook(
            List<ProcurementPlan> plans,
            String title
    ) {

        try (
                Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Procurement Plan");

            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            int rowNum = 0;

            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(title);
            titleCell.setCellStyle(titleStyle);

            rowNum++;

            Row header = sheet.createRow(rowNum++);

            String[] headers = {
                "No.",
                "Procurement Class",
                "COA Code",
                "Subject of Procurement",
                "Budget Item",
                "Fund Source",
                "Estimated Cost",
                "Procurement Method",
                "Contract Type",
                "Prequalification",
                "Reserve Scheme",
                "Bid / EOI Invitation",
                "Closing / Opening",
                "Evaluation Approval",
                "Award Notification",
                "Contract Signing",
                "Completion"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int count = 1;

            for (ProcurementPlan plan : plans) {

                List<BudgetItems> items = safeBudgetItems(plan);

                if (items.isEmpty()) {

                    Row row = sheet.createRow(rowNum++);

                    row.createCell(0).setCellValue(count++);
                    row.createCell(1).setCellValue(
                            plan.getProcClass() == null
                            ? ""
                            : plan.getProcClass().name()
                    );

                    row.createCell(2).setCellValue(safeCoaCode(plan));
                    row.createCell(3).setCellValue(nvl(plan.getSubject()));
                    row.createCell(4).setCellValue("");
                    row.createCell(5).setCellValue("");
                    row.createCell(6).setCellValue(nz(plan.getCost()).doubleValue());

                    continue;
                }

                for (BudgetItems item : items) {

                    Row row = sheet.createRow(rowNum++);

                    row.createCell(0).setCellValue(count++);

                    row.createCell(1).setCellValue(
                            item.getProcClass() == null
                            ? ""
                            : item.getProcClass().name()
                    );

                    row.createCell(2).setCellValue(
                            item.getCoacode() == null
                            ? ""
                            : nvl(item.getCoacode().getCode())
                    );

                    row.createCell(3).setCellValue(nvl(plan.getSubject()));

                    row.createCell(4).setCellValue(nvl(item.getItem()));

                    row.createCell(5).setCellValue(
                            item.getFundsource() == null
                            ? ""
                            : nvl(item.getFundsource().getFundsource())
                    );

                    row.createCell(6).setCellValue(
                            nz(sumBudgetItem(item)).doubleValue()
                    );

                    row.createCell(7).setCellValue(
                            item.getProcurementMethod() == null
                            ? ""
                            : item.getProcurementMethod().getProcuremntMethod()
                    );

                    row.createCell(8).setCellValue(
                            item.getProcurementType() == null
                            ? ""
                            : item.getProcurementType().getProcuremntType()
                    );

                    row.createCell(9).setCellValue(
                            boolText(item.getPrequalification())
                    );

                    row.createCell(10).setCellValue(
                            boolText(item.getReserveScheme())
                    );

                    row.createCell(11).setCellValue(
                            dateText(item.getBidInvitationDate())
                    );

                    row.createCell(12).setCellValue(
                            dateText(item.getBidClosingOpeningDate())
                    );

                    row.createCell(13).setCellValue(
                            dateText(item.getEvaluationApprovalDate())
                    );

                    row.createCell(14).setCellValue(
                            dateText(item.getAwardNotificationDate())
                    );

                    row.createCell(15).setCellValue(
                            dateText(item.getContractSigningDate())
                    );

                    row.createCell(16).setCellValue(
                            dateText(item.getCompletionDate())
                    );
                }
            }

            Row totalRow = sheet.createRow(rowNum + 1);

            totalRow.createCell(5).setCellValue("TOTAL");
            totalRow.createCell(6).setCellValue(sumPlans(plans).doubleValue());

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            applyWorkbookFormatting(workbook);
            workbook.write(out);

            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException ex) {

            warn("Failed to generate Excel file: " + ex.getMessage());

            return new ByteArrayInputStream(new byte[0]);
        }
    }

    private void applyWorkbookFormatting(Workbook workbook) {
        if (workbook == null) {
            return;
        }

        DataFormat dataFormat = workbook.createDataFormat();

        Font cgTimes10 = workbook.createFont();
        cgTimes10.setFontName("CG Times");
        cgTimes10.setFontHeightInPoints((short) 10);
        cgTimes10.setColor(IndexedColors.BLACK.getIndex());

        Map<CellStyle, CellStyle> cache = new HashMap<>();

        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                for (Cell cell : row) {
                    CellStyle original = cell.getCellStyle();

                    CellStyle updated = cache.computeIfAbsent(original, style -> {
                        CellStyle s = workbook.createCellStyle();
                        s.cloneStyleFrom(style);
                        s.setFont(cgTimes10);
                        return s;
                    });

                    cell.setCellStyle(updated);
                }
            }
        }

        formatEstimatedCostColumn(workbook.getSheet("Consultancy"), dataFormat);
        formatEstimatedCostColumn(workbook.getSheet("Supplies"), dataFormat);
        formatEstimatedCostColumn(workbook.getSheet("Works"), dataFormat);
        formatEstimatedCostColumn(workbook.getSheet("Non Consultancy"), dataFormat);
    }

    private void formatEstimatedCostColumn(Sheet sheet, DataFormat dataFormat) {
        if (sheet == null) {
            return;
        }

        int estimatedCostColumn = 3; // Column D

        for (Row row : sheet) {
            Cell cell = row.getCell(estimatedCostColumn);

            if (cell == null) {
                continue;
            }

            CellStyle style = cell.getCellStyle();
            style.setDataFormat(dataFormat.getFormat("#,##0.00"));
        }
    }

    private String dateText(LocalDate date) {
        return date == null ? "" : date.toString();
    }

    private static final class PlanSection {

        private final String title;
        private final List<ProcClass> allowedClasses;
        private final ProcClass defaultClass;
        Span budgetItemsTotalFooter;

        private final VerticalLayout root = new VerticalLayout();

        private final Grid<ProcurementPlan> planGrid
                = new Grid<>(ProcurementPlan.class, false);

        private final Grid<BudgetItems> budgetItemsGrid
                = new Grid<>(BudgetItems.class, false);

        private final ComboBox<Budget> budget = new ComboBox<>();
        private final ComboBox<ProcClass> procClass = new ComboBox<>();

        private final MultiSelectComboBox<UrcDeptSectionAnlDimbgt> costCentres
                = new MultiSelectComboBox<>();

        private final MultiSelectComboBox<Fundsource> funds
                = new MultiSelectComboBox<>();

        private final Span total = new Span("0.00/=");

        private GridContextMenu<ProcurementPlan> planContextMenu;
        private GridContextMenu<BudgetItems> itemContextMenu;

        private PlanSection(
                String title,
                List<ProcClass> allowedClasses,
                ProcClass defaultClass
        ) {
            this.title = title;
            this.allowedClasses = allowedClasses;
            this.defaultClass = defaultClass;

            this.total.getStyle().set("font-weight", "700");
            this.total.getElement().getThemeList().add("badge success");
        }
    }

    private void addConsultancyBudgetItemDateColumns(Grid<BudgetItems> grid) {
        grid.addColumn(BudgetItems::getBidInvitationDate)
                .setHeader("EOI Invitation")
                .setWidth("140px")
                .setResizable(true);

        grid.addColumn(BudgetItems::getBidClosingOpeningDate)
                .setHeader("EOI Closing/Opening")
                .setWidth("160px")
                .setResizable(true);

        grid.addColumn(BudgetItems::getEvaluationApprovalDate)
                .setHeader("Shortlist Approval")
                .setWidth("170px")
                .setResizable(true);

        grid.addColumn(BudgetItems::getAwardNotificationDate)
                .setHeader("EOI Notification / Award Notification")
                .setWidth("210px")
                .setResizable(true);

        grid.addColumn(BudgetItems::getContractSigningDate)
                .setHeader("Contract Signing")
                .setWidth("160px")
                .setResizable(true);

        grid.addColumn(BudgetItems::getCompletionDate)
                .setHeader("Completion")
                .setWidth("140px")
                .setResizable(true);
    }

    private void addOtherBudgetItemDateColumns(Grid<BudgetItems> grid) {
        grid.addColumn(BudgetItems::getBidInvitationDate)
                .setHeader("Bid Invitation")
                .setWidth("140px")
                .setResizable(true);

        grid.addColumn(BudgetItems::getBidClosingOpeningDate)
                .setHeader("Bid Closing/Opening")
                .setWidth("170px")
                .setResizable(true);

        grid.addColumn(BudgetItems::getEvaluationApprovalDate)
                .setHeader("Evaluation Approval")
                .setWidth("170px")
                .setResizable(true);

        grid.addColumn(BudgetItems::getAwardNotificationDate)
                .setHeader("Award Notification")
                .setWidth("160px")
                .setResizable(true);

        grid.addColumn(BudgetItems::getContractSigningDate)
                .setHeader("Contract Signing")
                .setWidth("160px")
                .setResizable(true);

        grid.addColumn(BudgetItems::getCompletionDate)
                .setHeader("Completion")
                .setWidth("140px")
                .setResizable(true);
    }

    private void openBudgetItemEditDialog(PlanSection section, BudgetItems item) {
        if (item == null) {
            warn("No budget item selected.");
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit Budget Item Procurement Details");
        dialog.setModal(true);
        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.setWidth("900px");

        TextField itemName = new TextField("Budget Item");
        itemName.setValue(nvl(item.getItem()));
        itemName.setReadOnly(true);
        itemName.setWidthFull();

        TextField accountCode = new TextField("Account Code");
        accountCode.setValue(item.getCoacode() == null ? "" : nvl(item.getCoacode().getCode()));
        accountCode.setReadOnly(true);
        accountCode.setWidthFull();

        TextField costCentre = new TextField("Cost Centre");
        costCentre.setValue(item.getDeptUnit() == null ? "" : nvl(item.getDeptUnit().getNAME()));
        costCentre.setReadOnly(true);
        costCentre.setWidthFull();

        TextField fundSource = new TextField("Fund Source");
        fundSource.setValue(item.getFundsource() == null ? "" : nvl(item.getFundsource().getFundsource()));
        fundSource.setReadOnly(true);
        fundSource.setWidthFull();

        ComboBox<ProcurementMethod> procurementMethod = new ComboBox<>("Procurement Method");
        procurementMethod.setItems(procurementMethodService.getAllProcurementMethods());
        procurementMethod.setItemLabelGenerator(ProcurementMethod::getProcuremntMethod);
        procurementMethod.setValue(item.getProcurementMethod());
        procurementMethod.setRequiredIndicatorVisible(true);
        procurementMethod.setWidthFull();

        ComboBox<ProcurementType> procurementType = new ComboBox<>("Contract Type");
        procurementType.setItems(procurementTypeService.getAllProcurementTypes());
        procurementType.setItemLabelGenerator(ProcurementType::getProcuremntType);
        procurementType.setValue(item.getProcurementType());
        procurementType.setWidthFull();

        Checkbox prequalification = new Checkbox(
                "Prequalification",
                Boolean.TRUE.equals(item.getPrequalification())
        );

        Checkbox reserveScheme = new Checkbox(
                "Application of Reserve Scheme",
                Boolean.TRUE.equals(item.getReserveScheme())
        );

        DatePicker bidInvitationDate = datePicker("Bid / EOI Invitation", item.getBidInvitationDate());
        DatePicker bidClosingOpeningDate = datePicker("Closing / Opening", item.getBidClosingOpeningDate());
        DatePicker evaluationApprovalDate = datePicker("Evaluation / Shortlist Approval", item.getEvaluationApprovalDate());
        DatePicker awardNotificationDate = datePicker("Award Notification", item.getAwardNotificationDate());
        DatePicker contractSigningDate = datePicker("Contract Signing", item.getContractSigningDate());
        DatePicker completionDate = datePicker("Completion", item.getCompletionDate());

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0px", 1),
                new FormLayout.ResponsiveStep("700px", 2),
                new FormLayout.ResponsiveStep("1000px", 3)
        );

        form.add(
                itemName,
                accountCode,
                costCentre,
                fundSource,
                procurementMethod,
                procurementType,
                prequalification,
                reserveScheme,
                sectionTitle(section == consultancySection
                        ? "Consultancy Procurement Schedule"
                        : "Invitation and Award of Bids"),
                bidInvitationDate,
                bidClosingOpeningDate,
                evaluationApprovalDate,
                awardNotificationDate,
                contractSigningDate,
                completionDate
        );

        Button save = primaryButton("Save");
        save.addClickListener(e -> {
            if (procurementMethod.isEmpty()) {
                warn("Procurement method is required.");
                return;
            }

            item.setProcurementMethod(procurementMethod.getValue());
            item.setProcurementType(procurementType.getValue());
            item.setPrequalification(prequalification.getValue());
            item.setReserveScheme(reserveScheme.getValue());
            item.setBidInvitationDate(bidInvitationDate.getValue());
            item.setBidClosingOpeningDate(bidClosingOpeningDate.getValue());
            item.setEvaluationApprovalDate(evaluationApprovalDate.getValue());
            item.setAwardNotificationDate(awardNotificationDate.getValue());
            item.setContractSigningDate(contractSigningDate.getValue());
            item.setCompletionDate(completionDate.getValue());

            budgetItemsService.saveBudgetItem(item);

            refreshSection(section);
            section.budgetItemsGrid.getDataProvider().refreshAll();

            dialog.close();
            success("Budget item procurement details saved.");
        });

        dialog.add(form);
        dialog.getFooter().add(
                new Button("Cancel", e -> dialog.close()),
                save
        );

        dialog.open();
    }

    private void openGroupBudgetItemsDialog(PlanSection section) {
        Set<BudgetItems> selectedItems = section.budgetItemsGrid.getSelectedItems();

        if (selectedItems.size() < 2) {
            warn("Select at least two budget items to group.");
            return;
        }

        BudgetItems first = selectedItems.iterator().next();

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Group Budget Items");
        dialog.setWidth("650px");

        TextField groupName = new TextField("Group Name");
        groupName.setWidthFull();
        groupName.setRequiredIndicatorVisible(true);
        groupName.setValue(first.getCoacode() == null
                ? "Grouped Procurement Item"
                : first.getCoacode().getName());

        BigDecimal total = selectedItems.stream()
                .map(BudgetItems::getYearTotalFromQuarters)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimalField totalField = new BigDecimalField("Grouped Total");
        totalField.setValue(total);
        totalField.setReadOnly(true);
        totalField.setWidthFull();

        Button save = new Button("Group Items", e -> {
            if (groupName.isEmpty()) {
                warn("Enter group name.");
                return;
            }

            ProcurementBudgetItemGroup group = new ProcurementBudgetItemGroup();
            group.setGroupName(groupName.getValue());
            group.setBudget(first.getBudget());
            group.setCoa(first.getCoacode());
            group.setDeptUnit(first.getDeptUnit());
            group.setProcClass(first.getProcClass());
            group.setItems(new HashSet<>(selectedItems));

            procurementBudgetItemGroupService.save(group);

            refreshAllSectionsAfterTransfer();
            clearAllSelections();

            dialog.close();
            success("Grouped " + selectedItems.size() + " budget item(s).");
        });

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancel = new Button("Cancel", e -> dialog.close());

        dialog.add(new VerticalLayout(groupName, totalField));
        dialog.getFooter().add(cancel, save);
        dialog.open();
    }

}
