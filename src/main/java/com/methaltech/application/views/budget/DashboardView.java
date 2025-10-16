package com.methaltech.application.views.budget;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.DeptSectionMergerService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetSummary;
import com.methaltech.application.data.entity.bgtool.BudgetSummaryCards;
import com.methaltech.application.data.entity.bgtool.DepartmentBudget;
import com.methaltech.application.data.entity.bgtool.RevenueBreakdown;
import com.methaltech.application.data.entity.bgtool.RevenueSource;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import com.methaltech.application.security.AuthenticatedUser;
import com.methaltech.application.views.MainLayout;
import com.methaltech.application.views.structure.DepartmentOverview;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Budget Management Dashboard")
@SpringComponent
@UIScope
@Route(value = "dashboard", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
@Uses(Icon.class)
public class DashboardView extends VerticalLayout {

    private final BudgetService budgetService;
    private final UserService userService;
    private AuthenticatedUser authenticatedUser;
    private VerticalLayout contentContainer;
    private ComboBox<Budget> fiscalYear = new ComboBox<>();
    private Budget selectedBudget;
    private Span statusIndicator;
    private Span lastUpdated;
    private User user;
        private final CoaService sampleCoaService;
    private final UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService;
    private final SALFLDGService sampleSALFLDGService;
    private final DeptSectionMergerService sampleDeptSectionMergerService;

    @Autowired
    public DashboardView(BudgetService budgetService,AuthenticatedUser authenticatedUser,UserService userService,CoaService sampleCoaService,
            UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService,SALFLDGService sampleSALFLDGService,DeptSectionMergerService sampleDeptSectionMergerService) {
        this.budgetService = budgetService;
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
                this.sampleDeptSectionMergerService = sampleDeptSectionMergerService;
        this.sampleCoaService = sampleCoaService;
        this.sampleUrcDeptSectionAnlDimbgtService = sampleUrcDeptSectionAnlDimbgtService;
        this.sampleSALFLDGService = sampleSALFLDGService;
        setWidthFull();
        setHeightFull();
        setPadding(false);
        setSpacing(false);
        setMargin(false);
        addClassName("dashboard-view");

        loadFiscalYearData();
        createHeader();
        createMainContent();
    }

    private void createHeader() {
        Div header = new Div();
        header.addClassName("dashboard-header");
        header.setWidthFull();

        HorizontalLayout headerContent = new HorizontalLayout();
        headerContent.setWidthFull();
        headerContent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerContent.setAlignItems(FlexComponent.Alignment.CENTER);
        headerContent.setPadding(false);
        headerContent.setSpacing(false);
        headerContent.setMargin(false);
        headerContent.setHeight("72px"); // Professional height

        // Left side - Logo and Title
        HorizontalLayout leftSide = new HorizontalLayout();
        leftSide.setAlignItems(FlexComponent.Alignment.CENTER);
        leftSide.setSpacing(true);
        leftSide.setPadding(false);
        leftSide.setMargin(false);
        leftSide.setFlexGrow(1);

        // Logo section
        HorizontalLayout logoSection = new HorizontalLayout();
        logoSection.setAlignItems(FlexComponent.Alignment.CENTER);
        logoSection.setSpacing(true);
        logoSection.setPadding(false);
        logoSection.setMargin(false);
        logoSection.addClassName("logo-section");

        Div logo = new Div();
        logo.addClassName("app-logo");
        Span logoText = new Span("B");
        logoText.addClassName("logo-text");
        logo.add(logoText);

        VerticalLayout titleGroup = new VerticalLayout();
        titleGroup.setSpacing(false);
        titleGroup.setPadding(false);
        titleGroup.setMargin(false);

        H1 title = new H1("Dashboard");
        title.addClassName("app-title");

        Span subtitle = new Span("Comprehensive Budget");
        subtitle.addClassName("app-subtitle");

        titleGroup.add(title, subtitle);
        logoSection.add(logo, titleGroup);

        // Fiscal year layout
        HorizontalLayout fiscalYearLayout = new HorizontalLayout();
        fiscalYearLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        fiscalYearLayout.addClassName("fiscal-year");
        fiscalYearLayout.setSpacing(true);
        fiscalYearLayout.setPadding(false);
        fiscalYearLayout.setMargin(false);

        Icon calendarIcon = new Icon(VaadinIcon.CALENDAR);
        calendarIcon.addClassName("calendar-icon");

        VerticalLayout fiscalInfo = new VerticalLayout();
        fiscalInfo.setSpacing(false);
        fiscalInfo.setPadding(false);
        fiscalInfo.setMargin(false);

        Span fiscalLabel = new Span("Fiscal Year");
        fiscalLabel.addClassName("fiscal-label");

        fiscalYear.addClassName("fiscal-year-selector");
        fiscalYear.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                selectedBudget = e.getValue();
                loadDashboardData();
                updateStatusIndicator();
            }
        });

        fiscalInfo.add(fiscalLabel, fiscalYear);
        fiscalYearLayout.add(calendarIcon, fiscalInfo);

        leftSide.add(logoSection, fiscalYearLayout);

        // Right side - Status and Action buttons
        HorizontalLayout rightSide = new HorizontalLayout();
        rightSide.setAlignItems(FlexComponent.Alignment.CENTER);
        rightSide.setSpacing(true);
        rightSide.setPadding(false);
        rightSide.setMargin(false);
        rightSide.addClassName("header-actions");


        Div statusDot = new Div();
        statusDot.addClassName("status-dot");

        VerticalLayout statusInfo = new VerticalLayout();
        statusInfo.setSpacing(false);
        statusInfo.setPadding(false);
        statusInfo.setMargin(false);

        statusIndicator = new Span("System Online");
        statusIndicator.addClassName("status-indicator");

        lastUpdated = new Span("Last updated: Just now");
        lastUpdated.addClassName("last-updated");

        //statusInfo.add(statusIndicator, lastUpdated);
        //statusLayout.add(statusDot, statusInfo);

        // Notification button
        Button notificationButton = new Button(new Icon(VaadinIcon.BELL));
        notificationButton.addClassName("notification-button");
        notificationButton.addClickListener(e -> {
            showNotification("No new notifications", NotificationVariant.LUMO_CONTRAST);
        });

        Div notificationBadge = new Div();
        notificationBadge.addClassName("notification-badge");
        notificationBadge.setText("3");
        notificationButton.getElement().appendChild(notificationBadge.getElement());

        // Refresh button with enhanced styling
        Button refreshButton = new Button("Refresh", new Icon(VaadinIcon.REFRESH));
        refreshButton.addClassName("primary-action");
        refreshButton.addClickListener(e -> {
            refreshDashboard();
            showNotification("Dashboard refreshed successfully!", NotificationVariant.LUMO_SUCCESS);
        });

        // Export button with enhanced styling
        Button exportButton = new Button("Export", new Icon(VaadinIcon.DOWNLOAD));
        exportButton.addClassName("secondary-action");
        exportButton.addClickListener(e -> {
            generatePDFReport();
            //showNotification("Export functionality coming soon!", NotificationVariant.LUMO_PRIMARY);
        });

        // Settings button
        Button settingsButton = new Button(new Icon(VaadinIcon.COG));
        settingsButton.addClassName("tertiary-action");
        settingsButton.addClickListener(e -> {
            showNotification("Settings panel coming soon!", NotificationVariant.LUMO_CONTRAST);
        });

        // User profile button
        Button userButton = new Button(new Icon(VaadinIcon.USER));
        userButton.addClassName("tertiary-action");
        userButton.addClickListener(e -> {
            showNotification("User profile coming soon!", NotificationVariant.LUMO_CONTRAST);
        });

        rightSide.add(notificationButton, refreshButton, exportButton, settingsButton, userButton);

        headerContent.add(leftSide, rightSide);
        header.add(headerContent);
        add(header);
    }

    private void loadFiscalYearData() {
        fiscalYear.setItems(query -> budgetService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        fiscalYear.setItemLabelGenerator(Budget::getFinancialYear);
        Optional<Budget> recentBudget = budgetService.getMostRecurrentBudget();
        if (recentBudget.isPresent()) {
            fiscalYear.setValue(recentBudget.get());
            selectedBudget = recentBudget.get();
        }
    }

    private void createMainContent() {
        Div mainContent = new Div();
        mainContent.addClassName("main-content");

        contentContainer = new VerticalLayout();
        contentContainer.setWidthFull();
        contentContainer.setHeightFull();
        contentContainer.setSpacing(true);
        contentContainer.setPadding(false);
        contentContainer.setMargin(false);
        contentContainer.addClassName("content-container");

        loadDashboardData();

        mainContent.add(contentContainer);
        add(mainContent);
    }

    private void loadDashboardData() {
        try {
            Budget currentBudget = selectedBudget != null ? selectedBudget : fiscalYear.getValue();
            if (currentBudget == null) {
                showEmptyState();
                return;
            }

            BudgetSummary budgetSummary = budgetService.getBudgetSummary(currentBudget);

            // Summary cards with enhanced styling
            BudgetSummaryCards summaryCards = new BudgetSummaryCards(budgetSummary,sampleCoaService,sampleUrcDeptSectionAnlDimbgtService,sampleSALFLDGService,sampleDeptSectionMergerService, selectedBudget);
            summaryCards.addClassName("budget-summary-cards");

            // Main content grid
            HorizontalLayout mainGrid = new HorizontalLayout();
            mainGrid.setWidthFull();
            mainGrid.setSpacing(true);
            mainGrid.setPadding(false);
            mainGrid.setMargin(false);
            mainGrid.addClassName("dashboard-grid");

            // Department overview with enhanced design
            DepartmentOverview departmentOverview = new DepartmentOverview(budgetSummary.getDepartmentBudgets(),sampleCoaService,sampleUrcDeptSectionAnlDimbgtService,sampleSALFLDGService,sampleDeptSectionMergerService,selectedBudget);
            departmentOverview.addClassName("department-overview");

            mainGrid.add(departmentOverview);

            // Revenue breakdown with premium styling
            RevenueBreakdown revenueBreakdown = new RevenueBreakdown(budgetSummary.getRevenueSources());
            revenueBreakdown.addClassName("revenue-breakdown");

            contentContainer.removeAll();
            contentContainer.add(summaryCards, mainGrid, revenueBreakdown);

            updateStatusIndicator();
        } catch (Exception e) {
            showNotification("Error loading dashboard data: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void showEmptyState() {
        contentContainer.removeAll();

        VerticalLayout emptyState = new VerticalLayout();
        emptyState.setAlignItems(FlexComponent.Alignment.CENTER);
        emptyState.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        emptyState.addClassName("empty-state");
        emptyState.setHeightFull();

        Icon emptyIcon = new Icon(VaadinIcon.CHART);
        emptyIcon.addClassName("empty-state-icon");

        H2 emptyTitle = new H2("No Budget Data Available");
        emptyTitle.addClassName("empty-state-title");

        Span emptyMessage = new Span("Please select a fiscal year to view budget dashboard data.");
        emptyMessage.addClassName("empty-state-message");

        emptyState.add(emptyIcon, emptyTitle, emptyMessage);
        contentContainer.add(emptyState);
    }

    private void updateStatusIndicator() {
        if (statusIndicator != null && lastUpdated != null) {
            statusIndicator.setText("System Online");
            lastUpdated.setText("Last updated: " + java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("MMM dd, HH:mm")));
        }
    }

    private void refreshDashboard() {
        // Add loading state
        contentContainer.removeAll();

        Div loadingIndicator = new Div();
        loadingIndicator.addClassName("loading-indicator");
        loadingIndicator.setText("Refreshing dashboard data...");

        contentContainer.add(loadingIndicator);

        // Simulate loading delay for better UX
        getUI().ifPresent(ui -> ui.access(() -> {
            try {
                Thread.sleep(500); // Brief delay for visual feedback
                loadDashboardData();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                showNotification("Refresh interrupted", NotificationVariant.LUMO_ERROR);
            }
        }));
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_END);
        notification.addThemeVariants(variant);

        // Enhanced notification styling
        notification.getElement().getStyle()
                .set("border-radius", "var(--lumo-border-radius-l)")
                .set("box-shadow", "var(--lumo-box-shadow-l)")
                .set("backdrop-filter", "blur(10px)");
    }

    private void generatePDFReport() {
        try {
            Budget currentBudget = selectedBudget != null ? selectedBudget : fiscalYear.getValue();
            if (currentBudget == null) {
                showNotification("Please select a fiscal year first", NotificationVariant.LUMO_ERROR);
                return;
            }

            BudgetSummary budgetSummary = budgetService.getBudgetSummary(currentBudget);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(PageSize.A4);
            Document document = new Document(pdfDoc);

            // Create fonts
            PdfFont titleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont headerFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            //PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            // Currency formatter
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
            currencyFormat.setCurrency(java.util.Currency.getInstance("UGX"));

            // Title Section
            Paragraph title = new Paragraph("BUDGET MANAGEMENT DASHBOARD REPORT")
                    .setFont(titleFont) // use bold font
                    .setFontSize(15)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(title);

            // Subtitle with fiscal year
            Paragraph subtitle = new Paragraph("Fiscal Year: " + currentBudget.getFinancialYear())
                    .setFont(headerFont)
                    .setFontSize(13)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(subtitle);

            // Report generation info
            Paragraph reportInfo = new Paragraph("Generated on: "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm")))
                    .setFont(normalFont)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(20);
            document.add(reportInfo);

            // Executive Summary Section
            Paragraph summaryHeader = new Paragraph("EXECUTIVE SUMMARY")
                    .setFont(headerFont)
                    .setFontSize(13)
                    .setMarginBottom(10);
            document.add(summaryHeader);

            // Summary Table
            Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{3, 2}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(20);

            // Summary table headers
            summaryTable.addHeaderCell(new Cell().add(new Paragraph("Metric").setFont(titleFont))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            summaryTable.addHeaderCell(new Cell().add(new Paragraph("Amount").setFont(titleFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // Summary data
            summaryTable.addCell("Total Budget").addCell(formatCurrency(budgetSummary.getTotalBudget()));
            summaryTable.addCell("Total Spent").addCell(formatCurrency(budgetSummary.getTotalSpent()));
            summaryTable.addCell("Remaining Budget").addCell(formatCurrency(budgetSummary.getRemainingBudget()));
            summaryTable.addCell("Total Revenue").addCell(formatCurrency(budgetSummary.getTotalRevenue()));
            summaryTable.addCell("Projected Revenue").addCell(formatCurrency(budgetSummary.getProjectedRevenue()));
            summaryTable.addCell("Budget Utilization").addCell(String.format("%.1f%%", budgetSummary.getSpentPercentage()));
            summaryTable.addCell("Revenue Achievement").addCell(String.format("%.1f%%", budgetSummary.getRevenuePercentage()));

            document.add(summaryTable);

            // Department Budget Analysis Section
            document.add(new Paragraph("DEPARTMENT BUDGET ANALYSIS")
                    .setFont(headerFont)
                    .setFontSize(13)
                    .setMarginTop(20)
                    .setMarginBottom(10));

            // Department Table
            Table deptTable = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 2, 1, 1}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(20);

            // Department table headers
            deptTable.addHeaderCell(new Cell().add(new Paragraph("Department").setFont(titleFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            deptTable.addHeaderCell(new Cell().add(new Paragraph("Total Budget").setFont(titleFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            deptTable.addHeaderCell(new Cell().add(new Paragraph("Total Spent").setFont(titleFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            deptTable.addHeaderCell(new Cell().add(new Paragraph("Available").setFont(titleFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            deptTable.addHeaderCell(new Cell().add(new Paragraph("Utilization").setFont(titleFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            deptTable.addHeaderCell(new Cell().add(new Paragraph("Status").setFont(titleFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // Department data
            for (DepartmentBudget dept : budgetSummary.getDepartmentBudgets()) {
                deptTable.addCell(dept.getDepartmentName());
                deptTable.addCell(formatCurrency(dept.getTotalBudget()));
                deptTable.addCell(formatCurrency(dept.getTotalSpent()));
                deptTable.addCell(formatCurrency(dept.getAvailableBudget()));
                deptTable.addCell(String.format("%.1f%%", dept.getSpentPercentage()));

                Cell statusCell = new Cell().add(new Paragraph(dept.getStatusText()));
                if (dept.getSpentPercentage() > 90) {
                    statusCell.setBackgroundColor(ColorConstants.PINK);
                } else if (dept.getSpentPercentage() > 75) {
                    statusCell.setBackgroundColor(ColorConstants.YELLOW);
                } else {
                    statusCell.setBackgroundColor(ColorConstants.GREEN);
                }
                deptTable.addCell(statusCell);
            }

            document.add(deptTable);

            // Revenue Sources Analysis Section
            document.add(new Paragraph("REVENUE SOURCES ANALYSIS")
                    .setFont(headerFont)
                    .setFontSize(13)
                    .setMarginTop(20)
                    .setMarginBottom(10));

            // Revenue Table
            Table revenueTable = new Table(UnitValue.createPercentArray(new float[]{2, 1, 2, 2, 1}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(20);

            // Revenue table headers
            revenueTable.addHeaderCell(new Cell().add(new Paragraph("Revenue Source").setFont(titleFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            revenueTable.addHeaderCell(new Cell().add(new Paragraph("Category").setFont(titleFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            revenueTable.addHeaderCell(new Cell().add(new Paragraph("Collected").setFont(titleFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            revenueTable.addHeaderCell(new Cell().add(new Paragraph("Projected").setFont(titleFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            revenueTable.addHeaderCell(new Cell().add(new Paragraph("Achievement").setFont(titleFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // Revenue data
            for (RevenueSource revenue : budgetSummary.getRevenueSources()) {
                revenueTable.addCell(revenue.getName());
                revenueTable.addCell(revenue.getCategory().toUpperCase());
                revenueTable.addCell(formatCurrency(revenue.getAmount()));
                revenueTable.addCell(formatCurrency(revenue.getProjected()));

                Cell achievementCell = new Cell().add(new Paragraph(String.format("%.1f%%", revenue.getProjectedPercentage())));
                if (revenue.getProjectedPercentage() >= 100) {
                    achievementCell.setBackgroundColor(ColorConstants.GREEN);
                } else if (revenue.getProjectedPercentage() >= 80) {
                    achievementCell.setBackgroundColor(ColorConstants.YELLOW);
                } else {
                    achievementCell.setBackgroundColor(ColorConstants.PINK);
                }
                revenueTable.addCell(achievementCell);
            }

            document.add(revenueTable);

            // Key Performance Indicators Section
            document.add(new Paragraph("KEY PERFORMANCE INDICATORS")
                    .setFont(headerFont)
                    .setFontSize(13)
                    .setMarginTop(20)
                    .setMarginBottom(10));

            // KPI Table
            Table kpiTable = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(20);

            kpiTable.addHeaderCell(new Cell().add(new Paragraph("Indicator").setFont(titleFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            kpiTable.addHeaderCell(new Cell().add(new Paragraph("Value").setFont(titleFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            kpiTable.addHeaderCell(new Cell().add(new Paragraph("Status").setFont(titleFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // Calculate KPIs
            double budgetEfficiency = budgetSummary.getTotalBudget() > 0
                    ? (budgetSummary.getTotalRevenue() / budgetSummary.getTotalBudget()) * 100 : 0;
            double revenueGrowth = budgetSummary.getProjectedRevenue() > 0
                    ? ((budgetSummary.getTotalRevenue() - budgetSummary.getProjectedRevenue()) / budgetSummary.getProjectedRevenue()) * 100 : 0;

            kpiTable.addCell("Budget Utilization Rate");
            kpiTable.addCell(String.format("%.1f%%", budgetSummary.getSpentPercentage()));
            kpiTable.addCell(budgetSummary.getSpentPercentage() <= 85 ? "Good" : budgetSummary.getSpentPercentage() <= 95 ? "Caution" : "Critical");

            kpiTable.addCell("Revenue Collection Rate");
            kpiTable.addCell(String.format("%.1f%%", budgetSummary.getRevenuePercentage()));
            kpiTable.addCell(budgetSummary.getRevenuePercentage() >= 90 ? "Excellent" : budgetSummary.getRevenuePercentage() >= 75 ? "Good" : "Needs Improvement");

            kpiTable.addCell("Budget Efficiency Ratio");
            kpiTable.addCell(String.format("%.1f%%", budgetEfficiency));
            kpiTable.addCell(budgetEfficiency >= 100 ? "Excellent" : budgetEfficiency >= 80 ? "Good" : "Below Target");

            kpiTable.addCell("Remaining Budget");
            kpiTable.addCell(formatCurrency(budgetSummary.getRemainingBudget()));
            kpiTable.addCell(budgetSummary.getRemainingBudget() > 0 ? "Available" : "Exceeded");

            document.add(kpiTable);

            // Recommendations Section
            document.add(new Paragraph("RECOMMENDATIONS & INSIGHTS")
                    .setFont(headerFont)
                    .setFontSize(13)
                    .setMarginTop(20)
                    .setMarginBottom(10));

            // Generate recommendations based on data
            StringBuilder recommendations = new StringBuilder();

            if (budgetSummary.getSpentPercentage() > 95) {
                recommendations.append("• URGENT: Budget utilization exceeds 95%. Implement immediate spending controls.\n");
            } else if (budgetSummary.getSpentPercentage() > 85) {
                recommendations.append("• CAUTION: Budget utilization above 85%. Monitor spending closely.\n");
            } else {
                recommendations.append("• Budget utilization is within acceptable range.\n");
            }

            if (budgetSummary.getRevenuePercentage() < 75) {
                recommendations.append("• Revenue collection below target. Review collection strategies.\n");
            } else if (budgetSummary.getRevenuePercentage() >= 100) {
                recommendations.append("• Excellent revenue performance. Consider expanding revenue streams.\n");
            }

            // Check for departments over budget
            long overBudgetDepts = budgetSummary.getDepartmentBudgets().stream()
                    .filter(dept -> dept.getSpentPercentage() > 100)
                    .count();

            if (overBudgetDepts > 0) {
                recommendations.append("• ").append(overBudgetDepts)
                        .append(" department(s) have exceeded their budget allocation. Immediate review required.\n");
            }

            // Check for underutilized departments
            long underutilizedDepts = budgetSummary.getDepartmentBudgets().stream()
                    .filter(dept -> dept.getSpentPercentage() < 40)
                    .count();

            if (underutilizedDepts > 0) {
                recommendations.append("• ").append(underutilizedDepts)
                        .append(" department(s) have low budget utilization. Consider reallocation.\n");
            }

            if (recommendations.length() == 0) {
                recommendations.append("• Overall budget performance is satisfactory. Continue monitoring.");
            }

            Paragraph recommendationsText = new Paragraph(recommendations.toString())
                    .setFont(normalFont)
                    .setFontSize(11)
                    .setMarginBottom(20);
            document.add(recommendationsText);

            // Footer
            Paragraph footer = new Paragraph("This report was generated automatically by the Budget Management System.")
                    .setFont(normalFont)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(30);
            document.add(footer);

            document.close();

            // Create download
            String fileName = "Budget_Dashboard_Report_" + currentBudget.getFinancialYear().replace("/", "-")
                    + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".pdf";

            StreamResource resource = new StreamResource(fileName, () -> new ByteArrayInputStream(baos.toByteArray()));
            resource.setContentType("application/pdf");
            resource.setCacheTime(0);

            // Trigger download
            UI ui = UI.getCurrent();
            if (ui != null) {
                StreamRegistration registration = ui.getSession()
                        .getResourceRegistry()
                        .registerResource(resource);

                String url = registration.getResourceUri().toString();
                ui.getPage().open(url, "_blank");   // pass URL, not the StreamResource
            } else {
                // If you're on a background thread, get a reference to UI and use ui.access(...)
                // uiRef.access(() -> { ... open url ... });
            }

            showNotification("PDF report generated successfully!", NotificationVariant.LUMO_SUCCESS);

        } catch (Exception e) {
            showNotification("Error generating PDF report: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }

    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(1);
        return "UGX " + formatter.format(amount);
    }
    /*    private String formatCurrency(double amount, NumberFormat formatter) {
    if (Math.abs(amount) >= 1_000_000_000) {
    return String.format("UGX %.1fB", amount / 1_000_000_000);
    } else if (Math.abs(amount) >= 1_000_000) {
    return String.format("UGX %.1fM", amount / 1_000_000);
    } else if (Math.abs(amount) >= 1_000) {
    return String.format("UGX %.0fK", amount / 1_000);
    } else {
    return String.format("UGX %.0f", amount);
    }
    } */
}
