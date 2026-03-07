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
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;

import java.io.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;

import java.io.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
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

    private static final PageSize REPORT_PAGE_SIZE = PageSize.A4;
    private static final float PAGE_MARGIN_TOP = 48f;
    private static final float PAGE_MARGIN_RIGHT = 36f;
    private static final float PAGE_MARGIN_BOTTOM = 48f;
    private static final float PAGE_MARGIN_LEFT = 36f;

    private static final Color HEADER_BG = new com.itextpdf.kernel.colors.DeviceRgb(245, 245, 245);
    private static final Color GRID = new com.itextpdf.kernel.colors.DeviceRgb(220, 220, 220);
    private static final Color ROW_ALT = new com.itextpdf.kernel.colors.DeviceRgb(250, 250, 250);

    private static final DateTimeFormatter FILE_TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
    private static final DateTimeFormatter PRINT_TS = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm");

    @Autowired
    public DashboardView(BudgetService budgetService, AuthenticatedUser authenticatedUser, UserService userService, CoaService sampleCoaService,
            UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService, SALFLDGService sampleSALFLDGService, DeptSectionMergerService sampleDeptSectionMergerService) {
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
        Button exportButton = new Button("Export Pdf", new Icon(VaadinIcon.DOWNLOAD));
        exportButton.addClassName("secondary-action");
        exportButton.addClickListener(e -> {
            generatePDFReport();
            //showNotification("Export functionality coming soon!", NotificationVariant.LUMO_PRIMARY);
        });
        Button exportwordButton = new Button("Export Word", new Icon(VaadinIcon.DOWNLOAD));
        exportwordButton.addClassName("secondary-action");
        exportwordButton.addClickListener(e -> {
            generateWordReport();
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

        rightSide.add(notificationButton, refreshButton, exportButton, exportwordButton,settingsButton, userButton);

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
            BudgetSummaryCards summaryCards = new BudgetSummaryCards(budgetSummary, sampleCoaService, sampleUrcDeptSectionAnlDimbgtService, sampleSALFLDGService, sampleDeptSectionMergerService, selectedBudget);
            summaryCards.addClassName("budget-summary-cards");
            summaryCards.setJustifyContentMode(JustifyContentMode.CENTER);

            // Main content grid
            HorizontalLayout mainGrid = new HorizontalLayout();
            mainGrid.setWidthFull();
            mainGrid.setSpacing(true);
            mainGrid.setPadding(false);
            mainGrid.setMargin(false);
            mainGrid.addClassName("dashboard-grid");

            // Department overview with enhanced design
            DepartmentOverview departmentOverview = new DepartmentOverview(budgetSummary.getDepartmentBudgets(), sampleCoaService, sampleUrcDeptSectionAnlDimbgtService, sampleSALFLDGService, sampleDeptSectionMergerService, selectedBudget);
            departmentOverview.addClassName("department-overview");

            

            // Revenue breakdown with premium styling
            RevenueBreakdown revenueBreakdown = new RevenueBreakdown(budgetSummary.getRevenueSources());
            revenueBreakdown.addClassName("revenue-breakdown");

            contentContainer.removeAll();
            mainGrid.add(departmentOverview, revenueBreakdown);
            contentContainer.add(summaryCards, mainGrid);

            updateStatusIndicator();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            showNotification("Error loading dashboard data2: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
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
// Body margins (header/footer are drawn by event handler)
    private static final float MARGIN_TOP = 90f;     // enough room for logo + title + divider
    private static final float MARGIN_RIGHT = 36f;
    private static final float MARGIN_BOTTOM = 55f;
    private static final float MARGIN_LEFT = 36f;

// Style colors (soft, executive)
    private static final Color COLOR_HEADER_BG = new com.itextpdf.kernel.colors.DeviceRgb(245, 245, 245);
    private static final Color COLOR_GRID = new com.itextpdf.kernel.colors.DeviceRgb(220, 220, 220);
    private static final Color COLOR_ALT_ROW = new com.itextpdf.kernel.colors.DeviceRgb(250, 250, 250);
    private static final Color COLOR_NOTE_BG = new com.itextpdf.kernel.colors.DeviceRgb(248, 248, 248);

    private void generatePDFReport() {
        try {
            Budget currentBudget = (selectedBudget != null) ? selectedBudget : fiscalYear.getValue();
            if (currentBudget == null) {
                showNotification("Please select a fiscal year first", NotificationVariant.LUMO_ERROR);
                return;
            }

            BudgetSummary summary = budgetService.getBudgetSummary(currentBudget);
            if (summary == null) {
                showNotification("No data found for the selected fiscal year.", NotificationVariant.LUMO_ERROR);
                return;
            }

            byte[] pdf = buildBudgetDashboardPdf(currentBudget, summary);

            String fileName = "Budget_Dashboard_Report_"
                    + safe(currentBudget.getFinancialYear()).replace("/", "-")
                    + "_" + LocalDateTime.now().format(FILE_TS)
                    + ".pdf";

            StreamResource resource = new StreamResource(fileName, () -> new ByteArrayInputStream(pdf));
            resource.setContentType("application/pdf");
            resource.setCacheTime(0);

            UI ui = UI.getCurrent();
            if (ui != null) {
                StreamRegistration reg = ui.getSession().getResourceRegistry().registerResource(resource);
                ui.getPage().open(reg.getResourceUri().toString(), "_blank");
                showNotification("PDF report generated successfully!", NotificationVariant.LUMO_SUCCESS);
            } else {
                showNotification("Unable to open PDF (no UI context).", NotificationVariant.LUMO_ERROR);
            }

        } catch (Exception e) {
            showNotification("Error generating PDF report: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }

    private byte[] buildBudgetDashboardPdf(Budget budget, BudgetSummary summary) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(baos, new WriterProperties().setFullCompressionMode(true));
            PdfDocument pdf = new PdfDocument(writer);
            pdf.setDefaultPageSize(REPORT_PAGE_SIZE);

            PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            ImageData logo = loadLogo("/META-INF/resources/images/urclogo.png"); // from src/main/resources
            PdfFormXObject totalPagesPlaceholder = new PdfFormXObject(new Rectangle(0, 0, 50, 12));

            String reportTitle = "BUDGET MANAGEMENT DASHBOARD REPORT";
            String fiscalYearLine = "Fiscal Year: " + safe(budget.getFinancialYear());

            pdf.addEventHandler(PdfDocumentEvent.END_PAGE,
                    new ReportHeaderFooterHandler(reportTitle, fiscalYearLine, fontRegular, fontBold, logo, totalPagesPlaceholder));

            try (Document doc = new Document(pdf)) {
                // doc.setMargins(MARGIN_TOP, MARGIN_RIGHT, MARGIN_BOTTOM, MARGIN_LEFT);
                doc.setMargins(100f, 36f, 55f, 36f);
                pdf.addEventHandler(PdfDocumentEvent.START_PAGE, new IEventHandler() {
                    @Override
                    public void handleEvent(Event event) {
                        PdfDocumentEvent ev = (PdfDocumentEvent) event;
                        PdfDocument pdfDoc = ev.getDocument();
                        if (pdfDoc.getNumberOfPages() == 2) { // when page 2 starts
                            doc.setMargins(55f, 36f, 55f, 36f);
                        }
                    }
                });

                // Meta line
                doc.add(new Paragraph("Generated on: " + LocalDateTime.now().format(PRINT_TS))
                        .setFont(fontRegular)
                        .setFontSize(9)
                        .setFontColor(ColorConstants.DARK_GRAY)
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setMarginBottom(14));

                // EXECUTIVE SUMMARY
                doc.add(sectionTitle("EXECUTIVE SUMMARY", fontBold));

                Table summaryTable = baseTable(new float[]{3, 2});
                addHeader(summaryTable, fontBold, "Metric", "Amount");

                addRow(summaryTable, fontRegular, false, "Total Budget", formatUGX(summary.getTotalBudget()), true);
                addRow(summaryTable, fontRegular, true, "Total Spent", formatUGX(summary.getTotalSpent()), true);
                addRow(summaryTable, fontRegular, false, "Remaining Budget", formatUGX(summary.getRemainingBudget()), true);
                addRow(summaryTable, fontRegular, true, "Total Revenue", formatUGX(summary.getTotalRevenue()), true);
                addRow(summaryTable, fontRegular, false, "Projected Revenue", formatUGX(summary.getProjectedRevenue()), true);
                addRow(summaryTable, fontRegular, true, "Budget Utilization", pct(summary.getSpentPercentage()), false);
                addRow(summaryTable, fontRegular, false, "Revenue Achievement", pct(summary.getRevenuePercentage()), false);

                doc.add(summaryTable);

                // DEPARTMENT ANALYSIS
                doc.add(sectionTitle("DEPARTMENT BUDGET ANALYSIS", fontBold));

                Table deptTable = baseTable(new float[]{2.8f, 1.5f, 1.5f, 1.5f, 1.0f, 0.9f});
                addHeader(deptTable, fontBold,
                        "Department", "Total Budget", "Total Spent", "Available", "Utilization", "Status");

                boolean alt = false;
                for (DepartmentBudget d : safeList(summary.getDepartmentBudgets())) {
                    Color bg = alt ? COLOR_ALT_ROW : ColorConstants.WHITE;
                    alt = !alt;

                    addBodyCell(deptTable, fontRegular, safe(d.getDepartmentName()), bg, TextAlignment.LEFT);
                    addBodyCell(deptTable, fontRegular, formatUGX(d.getTotalBudget()), bg, TextAlignment.RIGHT);
                    addBodyCell(deptTable, fontRegular, formatUGX(d.getTotalSpent()), bg, TextAlignment.RIGHT);
                    addBodyCell(deptTable, fontRegular, formatUGX(d.getAvailableBudget()), bg, TextAlignment.RIGHT);
                    addBodyCell(deptTable, fontRegular, pct(d.getSpentPercentage()), bg, TextAlignment.RIGHT);

                    deptTable.addCell(statusCell(fontRegular, safe(d.getStatusText()), statusColor(d.getSpentPercentage())));
                }
                doc.add(deptTable);

                // REVENUE SOURCES
                doc.add(sectionTitle("REVENUE SOURCES ANALYSIS", fontBold));

                Table revenueTable = baseTable(new float[]{2.8f, 1.1f, 1.6f, 1.6f, 0.9f});
                addHeader(revenueTable, fontBold, "Revenue Source", "Category", "Collected", "Projected", "Achievement");

                alt = false;
                for (RevenueSource r : safeList(summary.getRevenueSources())) {
                    Color bg = alt ? COLOR_ALT_ROW : ColorConstants.WHITE;
                    alt = !alt;

                    addBodyCell(revenueTable, fontRegular, safe(r.getName()), bg, TextAlignment.LEFT);
                    addBodyCell(revenueTable, fontRegular, safeUpper(r.getCategory()), bg, TextAlignment.CENTER);
                    addBodyCell(revenueTable, fontRegular, formatUGX(r.getAmount()), bg, TextAlignment.RIGHT);
                    addBodyCell(revenueTable, fontRegular, formatUGX(r.getProjected()), bg, TextAlignment.RIGHT);

                    revenueTable.addCell(statusCell(
                            fontRegular,
                            pct(r.getProjectedPercentage()),
                            achievementColor(r.getProjectedPercentage())
                    ));
                }
                doc.add(revenueTable);

                // KPI
                doc.add(sectionTitle("KEY PERFORMANCE INDICATORS", fontBold));

                Table kpiTable = baseTable(new float[]{3.2f, 1.6f, 1.2f});
                addHeader(kpiTable, fontBold, "Indicator", "Value", "Status");

                double budgetEfficiency = ratioPct(summary.getTotalRevenue(), summary.getTotalBudget());

                addKpi(kpiTable, fontRegular, "Budget Utilization Rate", pct(summary.getSpentPercentage()),
                        statusTextLowerIsBetter(safe(summary.getSpentPercentage()).doubleValue(), 85, 95));

                addKpi(kpiTable, fontRegular, "Revenue Collection Rate", pct(summary.getRevenuePercentage()),
                        statusTextHigherIsBetter(safe(summary.getRevenuePercentage()).doubleValue(), 75, 90));

                addKpi(kpiTable, fontRegular, "Budget Efficiency Ratio", String.format(Locale.US, "%.1f%%", budgetEfficiency),
                        budgetEfficiency >= 100 ? "Excellent" : budgetEfficiency >= 80 ? "Good" : "Below Target");

                addKpi(kpiTable, fontRegular, "Remaining Budget", formatUGX(summary.getRemainingBudget()),
                        safe(summary.getRemainingBudget()).compareTo(BigDecimal.ZERO) > 0 ? "Available" : "Exceeded");

                doc.add(kpiTable);

                // RECOMMENDATIONS
                doc.add(sectionTitle("RECOMMENDATIONS & INSIGHTS", fontBold));
                doc.add(recommendationsBlock(summary, fontRegular));

                // Footer note (content)
                doc.add(new Paragraph("This report was generated automatically by the Budget Management System.")
                        .setFont(fontRegular)
                        .setFontSize(8)
                        .setFontColor(ColorConstants.DARK_GRAY)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginTop(12));

                // Fill total pages placeholder
                writeTotalPages(pdf, totalPagesPlaceholder, fontRegular);
            }

            return baos.toByteArray();
        }
    }

    /* =========================
   Header / Footer Handler
   ========================= */
    private static class ReportHeaderFooterHandler implements IEventHandler {

        private final String title;
        private final String subtitle;
        private final PdfFont regular;
        private final PdfFont bold;
        private final ImageData logo;
        private final PdfFormXObject totalPages;

        ReportHeaderFooterHandler(String title, String subtitle,
                PdfFont regular, PdfFont bold,
                ImageData logo,
                PdfFormXObject totalPages) {
            this.title = title;
            this.subtitle = subtitle;
            this.regular = regular;
            this.bold = bold;
            this.logo = logo;
            this.totalPages = totalPages;
        }

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent ev = (PdfDocumentEvent) event;
            PdfDocument pdf = ev.getDocument();

            PdfPage page = ev.getPage();
            int pageNumber = pdf.getPageNumber(page);

            Rectangle ps = page.getPageSize();
            float left = ps.getLeft() + MARGIN_LEFT;
            float right = ps.getRight() - MARGIN_RIGHT;

            PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdf);

            try (Canvas canvas = new Canvas(pdfCanvas, ps)) {

                /* =========================
           HEADER (ONLY PAGE 1)
           ========================= */
                if (pageNumber == 1) {

                    float headerTopY = ps.getTop() - 26f;
                    float dividerY = ps.getTop() - 70f;

                    // LOGO
                    if (logo != null) {
                        Image img = new Image(logo);
                        img.scaleToFit(75, 40);
                        img.setFixedPosition(left, ps.getTop() - 58f);
                        canvas.add(img);
                    }

                    // TITLE
                    canvas.showTextAligned(
                            new Paragraph(title).setFont(bold).setFontSize(12),
                            (left + right) / 2f,
                            headerTopY,
                            TextAlignment.CENTER
                    );

                    // SUBTITLE
                    canvas.showTextAligned(
                            new Paragraph(subtitle)
                                    .setFont(regular)
                                    .setFontSize(9)
                                    .setFontColor(ColorConstants.DARK_GRAY),
                            (left + right) / 2f,
                            headerTopY - 14f,
                            TextAlignment.CENTER
                    );

                    // Divider line
                    pdfCanvas.setStrokeColor(COLOR_GRID).setLineWidth(0.8f);
                    pdfCanvas.moveTo(left, dividerY).lineTo(right, dividerY).stroke();
                }

                /* =========================
           FOOTER (ALL PAGES)
           ========================= */
                float footerY = ps.getBottom() + 28f;

                pdfCanvas.setStrokeColor(COLOR_GRID).setLineWidth(0.8f);
                pdfCanvas.moveTo(left, footerY + 12f).lineTo(right, footerY + 12f).stroke();

                canvas.showTextAligned(
                        new Paragraph("Page " + pageNumber + " of ")
                                .setFont(regular)
                                .setFontSize(8)
                                .setFontColor(ColorConstants.DARK_GRAY),
                        left,
                        footerY,
                        TextAlignment.LEFT
                );

                Image totalImg = new Image(totalPages).scaleToFit(50, 12);
                totalImg.setFixedPosition(left + 52f, footerY - 3f);
                canvas.add(totalImg);

                canvas.showTextAligned(
                        new Paragraph("Confidential")
                                .setFont(regular)
                                .setFontSize(8)
                                .setFontColor(ColorConstants.DARK_GRAY),
                        right,
                        footerY,
                        TextAlignment.RIGHT
                );
            }
        }
    }

    /* =========================
   Styling helpers
   ========================= */
    private Paragraph sectionTitle(String text, PdfFont bold) {
        return new Paragraph(text)
                .setFont(bold)
                .setFontSize(12)
                .setMarginTop(10)
                .setMarginBottom(8);
    }

    private Table baseTable(float[] weights) {
        return new Table(UnitValue.createPercentArray(weights))
                .useAllAvailableWidth()
                .setFixedLayout() // ✅ important: prevents width rebalancing warnings
                .setMarginBottom(14);
    }

    private void addHeader(Table table, PdfFont bold, String... headers) {
        for (String h : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(h).setFont(bold).setFontSize(9))
                    .setBackgroundColor(COLOR_HEADER_BG)
                    .setBorder(new SolidBorder(COLOR_GRID, 0.6f))
                    .setPadding(4) // ✅ was 7
                    .setTextAlignment(TextAlignment.CENTER));
        }
    }

    private void addRow(Table table, PdfFont font, boolean altRow, String key, String value, boolean rightAlignValue) {
        Color bg = altRow ? COLOR_ALT_ROW : ColorConstants.WHITE;
        addBodyCell(table, font, key, bg, TextAlignment.LEFT);
        addBodyCell(table, font, value, bg, rightAlignValue ? TextAlignment.RIGHT : TextAlignment.LEFT);
    }

    private void addKpi(Table t, PdfFont font, String indicator, String value, String status) {
        addBodyCell(t, font, indicator, ColorConstants.WHITE, TextAlignment.LEFT);
        addBodyCell(t, font, value, ColorConstants.WHITE, TextAlignment.RIGHT);
        addBodyCell(t, font, status, ColorConstants.WHITE, TextAlignment.CENTER);
    }

    private void addBodyCell(Table t, PdfFont font, String text, Color bg, TextAlignment align) {
        t.addCell(new Cell()
                .add(new Paragraph(safe(text)).setFont(font).setFontSize(9))
                .setBackgroundColor(bg)
                .setBorder(new SolidBorder(COLOR_GRID, 0.6f))
                .setPadding(4) // ✅ was 7
                .setTextAlignment(align)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));
    }

    private Cell statusCell(PdfFont font, String text, Color bg) {
        return new Cell()
                .add(new Paragraph(safe(text)).setFont(font).setFontSize(9))
                .setBackgroundColor(bg)
                .setBorder(new SolidBorder(COLOR_GRID, 0.6f))
                .setPadding(6)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    /* =========================
   Business formatting helpers
   ========================= */
    private String formatUGX(BigDecimal amount) {
        BigDecimal a = safe(amount);
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(0);
        nf.setMinimumFractionDigits(0);
        return "UGX " + nf.format(a);
    }

    private String pct(BigDecimal v) {
        return String.format(Locale.US, "%.1f%%", safe(v).doubleValue());
    }

    private double ratioPct(BigDecimal num, BigDecimal den) {
        BigDecimal n = safe(num), d = safe(den);
        if (d.compareTo(BigDecimal.ZERO) <= 0) {
            return 0.0;
        }
        return (n.doubleValue() / d.doubleValue()) * 100.0;
    }

    private Color statusColor(BigDecimal spentPct) {
        double v = safe(spentPct).doubleValue();
        if (v > 90) {
            return new com.itextpdf.kernel.colors.DeviceRgb(255, 220, 230); // soft pink
        }
        if (v > 75) {
            return new com.itextpdf.kernel.colors.DeviceRgb(255, 245, 210); // soft yellow
        }
        return new com.itextpdf.kernel.colors.DeviceRgb(220, 245, 230);            // soft green
    }

    private Color achievementColor(BigDecimal pct) {
        double v = safe(pct).doubleValue();
        if (v >= 100) {
            return new com.itextpdf.kernel.colors.DeviceRgb(220, 245, 230);
        }
        if (v >= 80) {
            return new com.itextpdf.kernel.colors.DeviceRgb(255, 245, 210);
        }
        return new com.itextpdf.kernel.colors.DeviceRgb(255, 220, 230);
    }

    private String statusTextLowerIsBetter(double value, double goodLimit, double cautionLimit) {
        return value <= goodLimit ? "Good" : value <= cautionLimit ? "Caution" : "Critical";
    }

    private String statusTextHigherIsBetter(double value, double goodLimit, double excellentLimit) {
        return value >= excellentLimit ? "Excellent" : value >= goodLimit ? "Good" : "Needs Improvement";
    }

    private Paragraph recommendationsBlock(BudgetSummary s, PdfFont font) {
        StringBuilder rec = new StringBuilder();

        double spent = safe(s.getSpentPercentage()).doubleValue();
        double rev = safe(s.getRevenuePercentage()).doubleValue();

        if (spent > 95) {
            rec.append("• URGENT: Budget utilization exceeds 95%. Implement immediate spending controls.\n");
        } else if (spent > 85) {
            rec.append("• CAUTION: Budget utilization is above 85%. Monitor spending closely and review commitments.\n");
        } else {
            rec.append("• Budget utilization is within an acceptable range.\n");
        }

        if (rev < 75) {
            rec.append("• Revenue collection is below target. Review billing, enforcement, and collection strategies.\n");
        } else if (rev >= 100) {
            rec.append("• Excellent revenue performance. Consider expanding high-performing revenue streams.\n");
        }

        long overBudget = safeList(s.getDepartmentBudgets()).stream()
                .filter(d -> safe(d.getSpentPercentage()).doubleValue() > 100)
                .count();

        if (overBudget > 0) {
            rec.append("• ").append(overBudget)
                    .append(" department(s) have exceeded their budget allocation. Immediate review is required.\n");
        }

        long underUtilized = safeList(s.getDepartmentBudgets()).stream()
                .filter(d -> safe(d.getSpentPercentage()).doubleValue() < 40)
                .count();

        if (underUtilized > 0) {
            rec.append("• ").append(underUtilized)
                    .append(" department(s) show low budget utilization. Consider phased reallocation or targeted execution support.\n");
        }

        if (rec.length() == 0) {
            rec.append("• Overall budget performance is satisfactory. Continue monitoring.");
        }

        return new Paragraph(rec.toString())
                .setFont(font)
                .setFontSize(10)
                .setPadding(10)
                .setMarginBottom(12)
                .setBackgroundColor(COLOR_NOTE_BG)
                .setBorder(new SolidBorder(COLOR_GRID, 0.6f));
    }

    /* =========================
   Total pages placeholder writing
   ========================= */
    private void writeTotalPages(PdfDocument pdf, PdfFormXObject placeholder, PdfFont font) {
        try (Canvas canvas = new Canvas(placeholder, pdf)) {
            canvas.add(new Paragraph(String.valueOf(pdf.getNumberOfPages()))
                    .setFont(font)
                    .setFontSize(8)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setMargin(0)
                    .setBorder(Border.NO_BORDER));
        }
    }

    /* =========================
   Logo loader (classpath resource)
   ========================= */
    private ImageData loadLogo(String classpathResource) {
        try (InputStream is = getClass().getResourceAsStream(classpathResource)) {
            if (is == null) {
                return null;
            }
            return ImageDataFactory.create(is.readAllBytes());
        } catch (Exception e) {
            // If logo fails to load, we still generate report without it.
            return null;
        }
    }

    /* =========================
   Null-safe helpers
   ========================= */
    private String safe(String s) {
        return s == null ? "" : s;
    }

    private String safeUpper(Object o) {
        return o == null ? "" : String.valueOf(o).toUpperCase(Locale.ROOT);
    }

    private BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private <T> java.util.List<T> safeList(java.util.List<T> list) {
        return list == null ? java.util.Collections.emptyList() : list;
    }

    private String formatCurrency(BigDecimal amount) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(1);
        return "UGX " + formatter.format(amount);
    }

    private void generateWordReport() {
        try {
            Budget currentBudget = selectedBudget != null ? selectedBudget : fiscalYear.getValue();
            if (currentBudget == null) {
                showNotification("Please select a fiscal year first", NotificationVariant.LUMO_ERROR);
                return;
            }

            BudgetSummary summary = budgetService.getBudgetSummary(currentBudget);
            if (summary == null) {
                showNotification("No data found for the selected fiscal year.", NotificationVariant.LUMO_ERROR);
                return;
            }

            byte[] docxBytes = buildBudgetDashboardDocx(currentBudget, summary);

            String fileName = "Budget_Dashboard_Report_"
                    + safe(currentBudget.getFinancialYear()).replace("/", "-")
                    + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"))
                    + ".docx";

            StreamResource resource = new StreamResource(fileName, () -> new ByteArrayInputStream(docxBytes));
            resource.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            resource.setCacheTime(0);

            UI ui = UI.getCurrent();
            if (ui != null) {
                StreamRegistration reg = ui.getSession().getResourceRegistry().registerResource(resource);
                ui.getPage().open(reg.getResourceUri().toString(), "_blank");
                showNotification("Word report generated successfully!", NotificationVariant.LUMO_SUCCESS);
            } else {
                showNotification("Unable to open Word report (no UI context).", NotificationVariant.LUMO_ERROR);
            }

        } catch (Exception e) {
            showNotification("Error generating Word report: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }

    private byte[] buildBudgetDashboardDocx(Budget budget, BudgetSummary summary)
            throws IOException, InvalidFormatException {

        try (XWPFDocument doc = new XWPFDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            // Page margins (twips: 1440 = 1 inch)
            CTSectPr sectPr = doc.getDocument().getBody().addNewSectPr();
            CTPageMar mar = sectPr.addNewPgMar();
            mar.setTop(1100);    // ~0.76"
            mar.setBottom(900);  // ~0.63"
            mar.setLeft(900);    // ~0.63"
            mar.setRight(900);   // ~0.63"

            // Optional: add logo (classpath resource)
            // Put logo at: src/main/resources/images/logo.png
            addLogoIfPresent(doc, "/images/logo.png", 110, 55);

            // Title block
            addCenteredTitle(doc, "BUDGET MANAGEMENT DASHBOARD REPORT", 16, true);
            addCenteredTitle(doc, "Fiscal Year: " + safe(budget.getFinancialYear()), 12, true);

            addRightMeta(doc, "Generated on: "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm")));

            addSpacer(doc, 1);

            // Executive Summary
            addSectionHeader(doc, "EXECUTIVE SUMMARY");
            XWPFTable summaryTable = doc.createTable(1, 2);
            styleTable(summaryTable);
            setHeaderRow(summaryTable.getRow(0), "Metric", "Amount");

            addRow(summaryTable, "Total Budget", formatUGX(summary.getTotalBudget()));
            addRow(summaryTable, "Total Spent", formatUGX(summary.getTotalSpent()));
            addRow(summaryTable, "Remaining Budget", formatUGX(summary.getRemainingBudget()));
            addRow(summaryTable, "Total Revenue", formatUGX(summary.getTotalRevenue()));
            addRow(summaryTable, "Projected Revenue", formatUGX(summary.getProjectedRevenue()));
            addRow(summaryTable, "Budget Utilization", pct(summary.getSpentPercentage()));
            addRow(summaryTable, "Revenue Achievement", pct(summary.getRevenuePercentage()));

            addSpacer(doc, 1);

            // Department Budget Analysis
            addSectionHeader(doc, "DEPARTMENT BUDGET ANALYSIS");
            XWPFTable deptTable = doc.createTable(1, 6);
            styleTable(deptTable);
            setHeaderRow(deptTable.getRow(0),
                    "Department", "Total Budget", "Total Spent", "Available", "Utilization", "Status");

            for (DepartmentBudget d : safeList(summary.getDepartmentBudgets())) {
                XWPFTableRow r = deptTable.createRow();
                setCell(r.getCell(0), safe(d.getDepartmentName()), false, ParagraphAlignment.LEFT);
                setCell(r.getCell(1), formatUGX(d.getTotalBudget()), false, ParagraphAlignment.RIGHT);
                setCell(r.getCell(2), formatUGX(d.getTotalSpent()), false, ParagraphAlignment.RIGHT);
                setCell(r.getCell(3), formatUGX(d.getAvailableBudget()), false, ParagraphAlignment.RIGHT);
                setCell(r.getCell(4), pct(d.getSpentPercentage()), false, ParagraphAlignment.RIGHT);

                String status = safe(d.getStatusText());
                setCell(r.getCell(5), status, false, ParagraphAlignment.CENTER);

                // Soft status shading
                shadeCell(r.getCell(5), statusColorHex(d.getSpentPercentage()));
            }

            addSpacer(doc, 1);

            // Revenue Sources
            addSectionHeader(doc, "REVENUE SOURCES ANALYSIS");
            XWPFTable revTable = doc.createTable(1, 5);
            styleTable(revTable);
            setHeaderRow(revTable.getRow(0),
                    "Revenue Source", "Category", "Collected", "Projected", "Achievement");

            for (RevenueSource rs : safeList(summary.getRevenueSources())) {
                XWPFTableRow r = revTable.createRow();
                setCell(r.getCell(0), safe(rs.getName()), false, ParagraphAlignment.LEFT);
                setCell(r.getCell(1), safeUpper(rs.getCategory()), false, ParagraphAlignment.CENTER);
                setCell(r.getCell(2), formatUGX(rs.getAmount()), false, ParagraphAlignment.RIGHT);
                setCell(r.getCell(3), formatUGX(rs.getProjected()), false, ParagraphAlignment.RIGHT);
                setCell(r.getCell(4), pct(rs.getProjectedPercentage()), false, ParagraphAlignment.CENTER);

                shadeCell(r.getCell(4), achievementColorHex(rs.getProjectedPercentage()));
            }

            addSpacer(doc, 1);

            // KPI
            addSectionHeader(doc, "KEY PERFORMANCE INDICATORS");
            XWPFTable kpiTable = doc.createTable(1, 3);
            styleTable(kpiTable);
            setHeaderRow(kpiTable.getRow(0), "Indicator", "Value", "Status");

            double efficiency = ratioPct(summary.getTotalRevenue(), summary.getTotalBudget());

            addKpiRow(kpiTable, "Budget Utilization Rate", pct(summary.getSpentPercentage()),
                    statusTextLowerIsBetter(safe(summary.getSpentPercentage()).doubleValue(), 85, 95));

            addKpiRow(kpiTable, "Revenue Collection Rate", pct(summary.getRevenuePercentage()),
                    statusTextHigherIsBetter(safe(summary.getRevenuePercentage()).doubleValue(), 75, 90));

            addKpiRow(kpiTable, "Budget Efficiency Ratio",
                    String.format(Locale.US, "%.1f%%", efficiency),
                    efficiency >= 100 ? "Excellent" : efficiency >= 80 ? "Good" : "Below Target");

            addKpiRow(kpiTable, "Remaining Budget", formatUGX(summary.getRemainingBudget()),
                    safe(summary.getRemainingBudget()).compareTo(BigDecimal.ZERO) > 0 ? "Available" : "Exceeded");

            addSpacer(doc, 1);

            // Recommendations
            addSectionHeader(doc, "RECOMMENDATIONS & INSIGHTS");
            XWPFParagraph rec = doc.createParagraph();
            rec.setSpacingBefore(120);
            rec.setSpacingAfter(120);
            XWPFRun rr = rec.createRun();
            rr.setFontFamily("Calibri");
            rr.setFontSize(11);
            rr.setText(buildRecommendations(summary));

            addSpacer(doc, 1);

            // Footer note
            XWPFParagraph foot = doc.createParagraph();
            foot.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun fr = foot.createRun();
            fr.setFontFamily("Calibri");
            fr.setFontSize(9);
            fr.setColor("666666");
            fr.setText("This report was generated automatically by the Budget Management System.");

            doc.write(baos);
            return baos.toByteArray();
        }
    }
    // ===== Paragraph helpers =====

    private void addCenteredTitle(XWPFDocument doc, String text, int size, boolean bold) {
        XWPFParagraph p = doc.createParagraph();
        p.setAlignment(ParagraphAlignment.CENTER);
        p.setSpacingAfter(80);
        XWPFRun r = p.createRun();
        r.setFontFamily("Calibri");
        r.setFontSize(size);
        r.setBold(bold);
        r.setText(text);
    }

    private void addRightMeta(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        p.setAlignment(ParagraphAlignment.RIGHT);
        p.setSpacingAfter(160);
        XWPFRun r = p.createRun();
        r.setFontFamily("Calibri");
        r.setFontSize(9);
        r.setColor("666666");
        r.setText(text);
    }

    private void addSectionHeader(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingBefore(220);
        p.setSpacingAfter(120);
        XWPFRun r = p.createRun();
        r.setFontFamily("Calibri");
        r.setFontSize(12);
        r.setBold(true);
        r.setText(text);
    }

    private void addSpacer(XWPFDocument doc, int lines) {
        for (int i = 0; i < lines; i++) {
            XWPFParagraph p = doc.createParagraph();
            p.setSpacingAfter(80);
        }
    }

// ===== Table helpers =====
    private void styleTable(XWPFTable table) {
        table.setWidth("100%");
        table.setTableAlignment(TableRowAlign.CENTER);
    }

    private void setHeaderRow(XWPFTableRow row, String... headers) {
        for (int i = 0; i < headers.length; i++) {
            XWPFTableCell cell = row.getCell(i);
            setCell(cell, headers[i], true, ParagraphAlignment.CENTER);
            shadeCell(cell, "F2F2F2");
        }
    }

    private void addRow(XWPFTable table, String c1, String c2) {
        XWPFTableRow r = table.createRow();
        setCell(r.getCell(0), c1, false, ParagraphAlignment.LEFT);
        setCell(r.getCell(1), c2, false, ParagraphAlignment.RIGHT);
    }

    private void addKpiRow(XWPFTable table, String indicator, String value, String status) {
        XWPFTableRow r = table.createRow();
        setCell(r.getCell(0), indicator, false, ParagraphAlignment.LEFT);
        setCell(r.getCell(1), value, false, ParagraphAlignment.RIGHT);
        setCell(r.getCell(2), status, false, ParagraphAlignment.CENTER);
    }

    private void setCell(XWPFTableCell cell, String text, boolean bold, ParagraphAlignment align) {
        cell.removeParagraph(0);
        XWPFParagraph p = cell.addParagraph();
        p.setAlignment(align);
        p.setSpacingBefore(60);
        p.setSpacingAfter(60);

        XWPFRun r = p.createRun();
        r.setFontFamily("Calibri");
        r.setFontSize(10);
        r.setBold(bold);
        r.setText(text == null ? "" : text);
    }

    private void shadeCell(XWPFTableCell cell, String hexColor) {
        // hexColor like "FFFFFF" or "F2F2F2"
        cell.setColor(hexColor);
    }

// ===== Logo =====
    private void addLogoIfPresent(XWPFDocument doc, String classpath, int widthPx, int heightPx)
            throws IOException, InvalidFormatException {
        try (InputStream is = getClass().getResourceAsStream(classpath)) {
            if (is == null) {
                return;
            }

            XWPFParagraph p = doc.createParagraph();
            p.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun r = p.createRun();

            r.addPicture(is,
                    XWPFDocument.PICTURE_TYPE_PNG,
                    "logo.png",
                    Units.toEMU(widthPx),
                    Units.toEMU(heightPx));

            p.setSpacingAfter(60);
        }
    }


    private String buildRecommendations(BudgetSummary s) {
        StringBuilder rec = new StringBuilder();

        double spent = safe(s.getSpentPercentage()).doubleValue();
        double rev = safe(s.getRevenuePercentage()).doubleValue();

        if (spent > 95) {
            rec.append("• URGENT: Budget utilization exceeds 95%. Implement immediate spending controls.\n");
        } else if (spent > 85) {
            rec.append("• CAUTION: Budget utilization is above 85%. Monitor spending closely.\n");
        } else {
            rec.append("• Budget utilization is within an acceptable range.\n");
        }

        if (rev < 75) {
            rec.append("• Revenue collection is below target. Review collection strategies.\n");
        } else if (rev >= 100) {
            rec.append("• Excellent revenue performance. Consider expanding revenue streams.\n");
        }

        long overBudget = safeList(s.getDepartmentBudgets()).stream()
                .filter(d -> safe(d.getSpentPercentage()).doubleValue() > 100)
                .count();
        if (overBudget > 0) {
            rec.append("• ").append(overBudget)
                    .append(" department(s) have exceeded their budget allocation. Immediate review required.\n");
        }

        long underUtilized = safeList(s.getDepartmentBudgets()).stream()
                .filter(d -> safe(d.getSpentPercentage()).doubleValue() < 40)
                .count();
        if (underUtilized > 0) {
            rec.append("• ").append(underUtilized)
                    .append(" department(s) have low utilization. Consider reallocation.\n");
        }

        if (rec.length() == 0) {
            rec.append("• Overall budget performance is satisfactory. Continue monitoring.\n");
        }
        return rec.toString();
    }


// Status shading (soft)
    private String statusColorHex(BigDecimal spentPct) {
        double v = safe(spentPct).doubleValue();
        if (v > 90) {
            return "FFDCE6"; // soft pink
        }
        if (v > 75) {
            return "FFF5D2"; // soft yellow
        }
        return "DCF5E6";            // soft green
    }

    private String achievementColorHex(BigDecimal pct) {
        double v = safe(pct).doubleValue();
        if (v >= 100) {
            return "DCF5E6";
        }
        if (v >= 80) {
            return "FFF5D2";
        }
        return "FFDCE6";
    }


}
