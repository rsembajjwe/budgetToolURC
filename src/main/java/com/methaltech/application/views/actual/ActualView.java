package com.methaltech.application.views.actual;

import com.methaltech.application.data.Display;
import com.methaltech.application.data.PeriodExtractor;
import com.methaltech.application.data.Role;
import com.methaltech.application.data.Styles;
import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.BudgetItemsActuals;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.livedata.repository.SALFLDGProjection;
import com.methaltech.application.data.livedata.service.SALFLDGService;
import com.methaltech.application.security.AuthenticatedUser;
import com.methaltech.application.views.MainLayout;
import com.methaltech.application.views.budgetReport.BudgetReportsView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.DialogVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@PageTitle("Actuals")
@Route(value = "actuals", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "PROCUREMENT", "BLO", "HOD", "USER"})
@Uses(Icon.class)
public class ActualView extends Div {

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
    Budget chosenBudget = null;
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
    Span balanceSpan = new Span();

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
    Column<BudgetItemsActuals> balanceColumn;

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

    Button downloadWorkplan = new Button("Download Annual", new Icon(VaadinIcon.DOWNLOAD));
    Button downloadWorkplan2 = new Button("Download Qtr", new Icon(VaadinIcon.DOWNLOAD));
    PeriodExtractor gen = new PeriodExtractor();
    private final CoaService sampleCoaService;
    private Span footerTotal = new Span("");
    Button view = new Button("View", new Icon(VaadinIcon.MENU));

    @Autowired

    public ActualView(SALFLDGService sampleSALFLDGService, BudgetItemsService budgetItemsService, AuthenticatedUser authenticatedUser,
            UserService samplePersonService, BudgetService sampleBudgetService, UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService,
            CoaService sampleCoaService) {
        this.sampleSALFLDGService = sampleSALFLDGService;
        this.budgetItemsService = budgetItemsService;
        this.authenticatedUser = authenticatedUser;
        this.samplePersonService = samplePersonService;
        this.sampleBudgetService = sampleBudgetService;
        this.sampleUrcDeptSectionAnlDimbgtService = sampleUrcDeptSectionAnlDimbgtService;
        this.sampleCoaService = sampleCoaService;

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
        if (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.PROCUREMENT) || user.getRoles().contains(Role.USER)) {
            comboBoxD_Section.setItems(sampleUrcDeptSectionAnlDimbgtService.getAllUrcSectionsAnlDims2());
        } else {
            UrcDeptSectionAnlDimbgt notAnalysed = sampleUrcDeptSectionAnlDimbgtService.findByANL_CODE("#");
            Set<UrcDeptSectionAnlDimbgt> itemsss = new HashSet<>();
            itemsss.add(notAnalysed);
            itemsss.addAll(user.getDeptsection());
            comboBoxD_Section.setItems(itemsss);
        }
        budget.setItemLabelGenerator(Budget::getFinancialYear);
        budget.setItems(query -> sampleBudgetService.list(PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query))).stream());
        Div div = new Div();
        div.add(budget, comboBoxD_Section, view, downloadWorkplan, downloadWorkplan2);
        downloadWorkplan.addThemeVariants(ButtonVariant.LUMO_ICON);
        downloadWorkplan.setEnabled(false);

        downloadWorkplan2.addThemeVariants(ButtonVariant.LUMO_ICON);
        downloadWorkplan2.setEnabled(false);
        add(div);

        TabSheet tabSheet = new TabSheet();
        tabSheet.add("Monthly Budget Vs Actuals", budgetCoaView());
        tabSheet.add("Quarterly Budget Vs Actuals", budgetCoaQtrView());
        setSpanValues(budget.getValue());
        setSpanQtrValues(budget.getValue());

        tabSheet.addSelectedChangeListener(event -> {
            Component selectedTab = tabSheet.getSelectedTab();
            if (selectedTab != null) {

                if (selectedTab.getElement().getText().equals("Monthly Budget Vs Actuals")) {

                } else if (selectedTab.getElement().getText().equals("Quarterly Budget Vs Actuals")) {
                    if (!budget.isEmpty()) {

                    }
                } else if (selectedTab.getElement().getText().equals("Annual Budget Vs Actuals")) {

                }
            }
        });
        setSizeFull();
        tabSheet.setSizeFull();
        tabSheet.setHeightFull();
        add(tabSheet);

        downloadWorkplan.addClickListener(e -> {
            if (comboBoxD_Section.isEmpty() || budget.isEmpty()) {
                warningNotification("Ensure that You have filled the form well");
            } else {
                exportAndDownloadExcelWorkplan(budget.getValue());
            }

        });

        downloadWorkplan2.addClickListener(e -> {
            if (comboBoxD_Section.isEmpty() || budget.isEmpty()) {
                warningNotification("Ensure that You have filled the form well");
            } else {
                exportAndDownloadExcelWorkplanQtr(budget.getValue());
            }

        });
        budget.addValueChangeListener(e -> {
            chosenBudget = e.getValue();
            setSpanValues(e.getValue());
            setSpanQtrValues(e.getValue());

            if (!comboBoxD_Section.isEmpty() && !budget.isEmpty()) {
                downloadWorkplan.setEnabled(true);
                downloadWorkplan2.setEnabled(true);

            } else {
                downloadWorkplan.setEnabled(false);
                downloadWorkplan2.setEnabled(false);
                chosenBudget = null;
            }
        });
        view.addSingleClickListener(v -> {
            if (!comboBoxD_Section.isEmpty() || !budget.isEmpty()) {
                gridBudgetItems.setItems(budgetItemsService.findDistinctBudgetItemses(budget.getValue(), comboBoxD_Section.getSelectedItems()));
                gridBudgetItemsQuarterlyGrid.setItems(budgetItemsService.findDistinctBudgetItemses(budget.getValue(), comboBoxD_Section.getSelectedItems()));
            }
        });

        comboBoxD_Section.addValueChangeListener(e -> {
            if (!budget.isEmpty() && !comboBoxD_Section.isEmpty()) {
                downloadWorkplan.setEnabled(true);
                downloadWorkplan2.setEnabled(true);

            } else {
                downloadWorkplan.setEnabled(false);
                downloadWorkplan2.setEnabled(false);
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
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("Jul " + " Budget", e -> {
                createBudgetDialog(urcActivity, "July", "Jul");
            });
            return span;

        })).setHeader(julSpan).setWidth("150px");
        julAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {
      
            BigDecimal value = urcActivity.getJulA();
            Span span = createSpan(value);
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value);
            }
            span.getElement().getThemeList().add("badge");
            ContextMenu contextMenu = new ContextMenu(span);
            /*contextMenu.addItem(yearString2(setFY(urcActivity.getBudget()), "Jul Actual ") + "Transactions", e -> {*/
            contextMenu.addItem("Jul Actual (" + urcActivity.getBudget().getStartDate().getYear() + ") Transactions", e -> {
                createTransactionsDialog(urcActivity, "July",Month.JULY);
            });
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
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("Aug " + " Budget", e -> {
                createBudgetDialog(urcActivity, "August", "Aug");
            });
            return span;

        })).setHeader(augSpan).setWidth("150px");

        augAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getAugA();
            Span span = createSpan(value);
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value);
            }
            span.getElement().getThemeList().add("badge");
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("Aug Actual (" + urcActivity.getBudget().getStartDate().getYear() + ") Transactions", e -> {
                createTransactionsDialog(urcActivity, "August",Month.AUGUST);
            });
            return span;

        })).setHeader(augActualSpan).setWidth("150px");
        sepColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getSep();
            Span span = createSpan(value);
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("Sep " + " Budget", e -> {
                createBudgetDialog(urcActivity, "September", "Sep");
            });
            return span;

        })).setHeader(sepSpan).setWidth("150px");
        sepAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getSepA();
            Span span = createSpan(value);
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value);
            }
            span.getElement().getThemeList().add("badge");
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("Sep Actual (" + urcActivity.getBudget().getStartDate().getYear() + ") Transactions", e -> {
                createTransactionsDialog(urcActivity, "September",Month.SEPTEMBER);
            });
            return span;

        })).setHeader(sepActualSpan).setWidth("150px");
        octColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getOct();
            Span span = createSpan(value);
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("Oct " + " Budget", e -> {
                createBudgetDialog(urcActivity, "October", "Oct");
            });
            return span;

        })).setHeader(octSpan).setWidth("150px");
        octAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getOctA();
            Span span = createSpan(value);
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value);
            }
            span.getElement().getThemeList().add("badge");
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("Oct Actual (" + urcActivity.getBudget().getStartDate().getYear() + ") Transactions", e -> {
                createTransactionsDialog(urcActivity, "October",Month.OCTOBER);
            });
            return span;

        })).setHeader(octActualSpan).setWidth("150px");
        novColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getNov();
            Span span = createSpan(value);
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("Nov " + " Budget", e -> {
                createBudgetDialog(urcActivity, "November", "Nov");
            });
            return span;

        })).setHeader(novSpan).setWidth("150px");

        novAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getNovA();
            Span span = createSpan(value);
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value);
            }
            span.getElement().getThemeList().add("badge");
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("Nov Actual (" + urcActivity.getBudget().getStartDate().getYear() + ") Transactions", e -> {
                createTransactionsDialog(urcActivity, "November",Month.NOVEMBER);
            });
            return span;

        })).setHeader(novActualSpan).setWidth("150px");
        decColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getDec();
            Span span = createSpan(value);
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("Dec " + " Budget", e -> {
                createBudgetDialog(urcActivity, "December", "Dec");
            });
            return span;

        })).setHeader(decSpan).setWidth("150px");

        decAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getDecA();
            Span span = createSpan(value);
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value);
            }
            span.getElement().getThemeList().add("badge");
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("Dec Actual (" + urcActivity.getBudget().getStartDate().getYear() + ") Transactions", e -> {
                createTransactionsDialog(urcActivity, "December",Month.DECEMBER);
            });
            return span;

        })).setHeader(decActualSpan).setWidth("150px");
        janColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getJan();
            Span span = createSpan(value);
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("Jan " + " Budget", e -> {
                createBudgetDialog(urcActivity, "January", "Jan");
            });
            return span;

        })).setHeader(janSpan).setWidth("150px");

        janAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getJanA();
            Span span = createSpan(value);
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value);
            }
            span.getElement().getThemeList().add("badge");
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("Jan Actual (" + urcActivity.getBudget().getCloseDate().getYear() + ") Transactions", e -> {
                createTransactionsDialog(urcActivity, "January",Month.JANUARY);
            });
            return span;

        })).setHeader(janActualSpan).setWidth("150px");
        febColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getFeb();
            Span span = createSpan(value);
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("Feb " + " Budget", e -> {
                createBudgetDialog(urcActivity, "February", "Feb");
            });
            return span;

        })).setHeader(febSpan).setWidth("150px");

        febAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getFebA();
            Span span = createSpan(value);
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value);
            }
            span.getElement().getThemeList().add("badge");
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("Feb Actual (" + urcActivity.getBudget().getCloseDate().getYear() + ") Transactions", e -> {
                createTransactionsDialog(urcActivity, "Febuary",Month.FEBRUARY);
            });
            return span;

        })).setHeader(febActualSpan).setWidth("150px");
        marColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getMar();
            Span span = createSpan(value);
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("Mar " + " Budget", e -> {
                createBudgetDialog(urcActivity, "March", "Mar");
            });
            return span;

        })).setHeader(marSpan).setWidth("150px");

        marAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getMarA();
            Span span = createSpan(value);
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value);
            }
            span.getElement().getThemeList().add("badge");
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("Mar Actual (" + urcActivity.getBudget().getCloseDate().getYear() + ") Transactions", e -> {
                createTransactionsDialog(urcActivity, "March", Month.MARCH);
            });
            return span;

        })).setHeader(marActualSpan).setWidth("150px");
        aprColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getApr();
            Span span = createSpan(value);
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("Apr " + " Budget", e -> {
                createBudgetDialog(urcActivity, "April", "Apr");
            });
            return span;

        })).setHeader(aprSpan).setWidth("150px");

        aprAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getAprA();
            Span span = createSpan(value);
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value);
            }
            span.getElement().getThemeList().add("badge");
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("Apr Actual (" + urcActivity.getBudget().getCloseDate().getYear() + ") Transactions", e -> {
                createTransactionsDialog(urcActivity, "April", Month.APRIL);
            });
            return span;

        })).setHeader(aprActualSpan).setWidth("150px");
        mayColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getMay();
            Span span = createSpan(value);
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("May " + " Budget", e -> {
                createBudgetDialog(urcActivity, "May", "May");
            });
            return span;

        })).setHeader(maySpan).setWidth("150px");

        mayAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getMayA();
            Span span = createSpan(value);
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value);
            }
            span.getElement().getThemeList().add("badge");
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("May Actual (" + urcActivity.getBudget().getCloseDate().getYear() + ") Transactions", e -> {
                createTransactionsDialog(urcActivity, "May",Month.MAY);
            });
            return span;

        })).setHeader(mayActualSpan).setWidth("150px");
        junColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getJun();
            Span span = createSpan(value);
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("Jun " + " Budget", e -> {
                createBudgetDialog(urcActivity, "June", "Jun");
            });
            return span;

        })).setHeader(junSpan).setWidth("150px");

        junAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getJunA();
            Span span = createSpan(value);
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value);
            }
            span.getElement().getThemeList().add("badge");
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("Jun Actual (" + urcActivity.getBudget().getCloseDate().getYear() + ") Transactions", e -> {
                createTransactionsDialog(urcActivity, "June",Month.JUNE);
            });
            return span;

        })).setHeader(junActualSpan).setWidth("150px");
        totalColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getTotal();
            Span span = createSpan(value);
            span.getElement().getThemeList().add("badge success");
            ContextMenu contextMenu = new ContextMenu(span);
            contextMenu.addItem("Total " + urcActivity.getBudget().getFinancialYear(), e -> {
                createBudgetDialogTotal(urcActivity, "Total", "Total");
            });
            return span;

        })).setHeader("Total").setFlexGrow(0).setWidth("150px");

        totalAColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getTotalA();
            Span span = createSpan(value);
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value.negate());
            }
            span.getElement().getThemeList().add("badge success");
            ContextMenu contextMenu = new ContextMenu(span);

            contextMenu.addItem("Total Transactions " + urcActivity.getBudget().getFinancialYear(), e -> {
                createTransactionsDialog2(urcActivity, "Total");
            });
            return span;

        })).setHeader("Total Actual").setFlexGrow(0).setWidth("150px");

        balanceColumn = gridBudgetItems.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getTotalA();
            BigDecimal value2 = urcActivity.getTotal();
            Span span = createSpan(value2.subtract(value));
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value2.add(value));
            }
            span.getElement().getThemeList().add("badge success");

            return span;

        })).setHeader("Balance").setFlexGrow(0).setWidth("150px");

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

        })).setHeader("Qtr1").setWidth("150px");
        qtr1AColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getQtr1A();
            Span span = createSpan(value);
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value);
            }
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader("Qtr1 Actual").setWidth("150px");
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

        })).setHeader("Qtr2").setWidth("150px");

        qtr2AColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getQtr2A();
            Span span = createSpan(value);
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value);
            }
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader("Qtr2 Actual").setWidth("150px");
        qtr3Column = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getQtr3();
            Span span = createSpan(value);

            return span;

        })).setHeader("Qtr3").setWidth("150px");
        qtr3AColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getQtr3A();
            Span span = createSpan(value);
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value);
            }
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader("Qtr3 Actual").setWidth("150px");
        qtr4Column = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getQtr4();
            Span span = createSpan(value);

            return span;

        })).setHeader("Qtr4").setWidth("150px");
        qtr4AColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getQtr4A();
            Span span = createSpan(value);
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value);
            }
            span.getElement().getThemeList().add("badge");

            return span;

        })).setHeader("Qtr4 Actual").setWidth("150px");
        totalQtrColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getTotal();
            Span span = createSpan(value);

            return span;

        })).setHeader("Total").setWidth("150px");

        totalAQtrColumn = gridBudgetItemsQuarterlyGrid.addColumn(new ComponentRenderer<>(urcActivity -> {

            BigDecimal value = urcActivity.getTotalA();
            Span span = createSpan(value);
            if (urcActivity.getCoacode().getCode().startsWith("2") || urcActivity.getCoacode().getCode().startsWith("3")) {
                span = createSpan(value.negate());
            }
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
            int y = extra.generateYear(fy, month.substring(0, 3));
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

    /*    private void setSpanValues() {
    julSpan.setText(yearString(setFY(budget.getValue()), "Jul"));
    julActualSpan.setText(yearString(setFY(budget.getValue()), "Jul Actual"));
    augSpan.setText(yearString(setFY(budget.getValue()), "Aug"));
    augActualSpan.setText(yearString(setFY(budget.getValue()), "Aug Actual"));
    sepSpan.setText(yearString(setFY(budget.getValue()), "Sep"));
    sepActualSpan.setText(yearString(setFY(budget.getValue()), "Sep Actual"));
    octSpan.setText(yearString(setFY(budget.getValue()), "Oct"));
    octActualSpan.setText(yearString(setFY(budget.getValue()), "Oct Actual"));
    novSpan.setText(yearString(setFY(budget.getValue()), "Nov"));
    novActualSpan.setText(yearString(setFY(budget.getValue()), "Nov Actual"));
    decSpan.setText(yearString(setFY(budget.getValue()), "Dec"));
    decActualSpan.setText(yearString(setFY(budget.getValue()), "Dec Actual"));
    janSpan.setText(yearString(setFY(budget.getValue()), "Jan"));
    janActualSpan.setText(yearString(setFY(budget.getValue()), "Jan Actual"));
    febSpan.setText(yearString(setFY(budget.getValue()), "Feb"));
    febActualSpan.setText(yearString(setFY(budget.getValue()), "Feb Actual"));
    marSpan.setText(yearString(setFY(budget.getValue()), "Mar"));
    marActualSpan.setText(yearString(setFY(budget.getValue()), "Mar Actual"));
    aprSpan.setText(yearString(setFY(budget.getValue()), "Apr"));
    aprActualSpan.setText(yearString(setFY(budget.getValue()), "Apr Actual"));
    maySpan.setText(yearString(setFY(budget.getValue()), "May"));
    mayActualSpan.setText(yearString(setFY(budget.getValue()), "May Actual"));
    junSpan.setText(yearString(setFY(budget.getValue()), "Jun"));
    junActualSpan.setText(yearString(setFY(budget.getValue()), "Jun Actual"));
    // totalSpan.setText(yearString(setFY(budget.getValue()), "Total"));
    //totalActualSpan.setText(yearString2(setFY(budget.getValue()), "Total Actual"));
    }*/
    private void setSpanValues(Budget budget) {
        if (budget != null) {
            julSpan.setText("Jul (" + budget.getStartDate().getYear() + ")");
            julActualSpan.setText("Jul Actual (" + budget.getStartDate().getYear() + ")");
            augSpan.setText("Aug (" + budget.getStartDate().getYear() + ")");
            augActualSpan.setText("Aug Actual (" + budget.getStartDate().getYear() + ")");
            sepSpan.setText("Sep (" + budget.getStartDate().getYear() + ")");
            sepActualSpan.setText("Sep Actual (" + budget.getStartDate().getYear() + ")");
            octSpan.setText("Oct (" + budget.getStartDate().getYear() + ")");
            octActualSpan.setText("Oct Actual (" + budget.getStartDate().getYear() + ")");
            novSpan.setText("Nov (" + budget.getStartDate().getYear() + ")");
            novActualSpan.setText("Nov Actual (" + budget.getStartDate().getYear() + ")");
            decSpan.setText("Dec (" + budget.getStartDate().getYear() + ")");
            decActualSpan.setText("Dec Actual (" + budget.getStartDate().getYear() + ")");
            janSpan.setText("Jan (" + budget.getCloseDate().getYear() + ")");
            janActualSpan.setText("Jan Actual (" + budget.getCloseDate().getYear() + ")");
            febSpan.setText("Feb (" + budget.getCloseDate().getYear() + ")");
            febActualSpan.setText("Feb Actual (" + budget.getCloseDate().getYear() + ")");
            marSpan.setText("Mar (" + budget.getCloseDate().getYear() + ")");
            marActualSpan.setText("Mar Actual (" + budget.getCloseDate().getYear() + ")");
            aprSpan.setText("Apr (" + budget.getCloseDate().getYear() + ")");
            aprActualSpan.setText("Apr Actual (" + budget.getCloseDate().getYear() + ")");
            maySpan.setText("May (" + budget.getCloseDate().getYear() + ")");
            mayActualSpan.setText("May Actual (" + budget.getCloseDate().getYear() + ")");
            junSpan.setText("Jun (" + budget.getCloseDate().getYear() + ")");
            junActualSpan.setText("Jun Actual (" + budget.getCloseDate().getYear() + ")");
            // totalSpan.setText(yearString(setFY(budget.getValue()), "Total"));
            //totalActualSpan.setText(yearString2(setFY(budget.getValue()), "Total Actual"));                
        }

    }

    /*    private void setSpanQtrValues() {
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
    }*/
    private void setSpanQtrValues(Budget budget) {
        if (budget != null) {
            qtr1Span.setText("Qr1 (" + budget.getStartDate().getYear() + ")");
            qtr1ActualSpan.setText("Qr1 Actual(" + budget.getStartDate().getYear() + ")");
            qtr2Span.setText("Qr2 (" + budget.getStartDate().getYear() + ")");
            qtr2ActualSpan.setText("Qr2 Actual(" + budget.getStartDate().getYear() + ")");
            qtr3Span.setText("Qr3 (" + budget.getCloseDate().getYear() + ")");
            qtr3ActualSpan.setText("Qr3 Actual(" + budget.getCloseDate().getYear() + ")");
            qtr4Span.setText("Qr4 (" + budget.getCloseDate().getYear() + ")");
            qtr4ActualSpan.setText("Qr4 Actual(" + budget.getCloseDate().getYear() + ")");
            //totalQtrSpan.setText(yearString(setFY(budget.getValue()), "Total"));
            //totalQtrActualSpan.setText(yearString2(setFY(budget.getValue()), "Total Actual"));                
        }

    }

    public Notification warningNotification(String warning) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);

        Div text = new Div(
                new Text(warning),
                new HtmlComponent("br"),
                new Text("Close this warning to continue working.")
        );

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> {
            notification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.open();
        return notification;
    }

    private void exportAndDownloadExcelWorkplan(Budget budget) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Actuals " + budget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            //createHeaderRowWorkplan(workbook, sheet);
            createBudgetActualsSheet(workbook, sheet);
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource(budget.getFinancialYear() + " Budget Actuals.xlsx", ()
                    -> new ByteArrayInputStream(outputStream.toByteArray()));

            // Create an Anchor component with the StreamResource
            Anchor downloadLink = new Anchor(resource, "");
            downloadLink.getElement().setAttribute("download", true);
            add(downloadLink);
            // Programmatically click the download link to initiate the download
            downloadLink.getElement().callJsFunction("click");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportAndDownloadExcelWorkplanQtr(Budget budget) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Quarterly Actuals " + budget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            createHeaderRowWorkplanQtr(workbook, sheet);
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource(budget.getFinancialYear() + "Quarterly Budget Actuals.xlsx", ()
                    -> new ByteArrayInputStream(outputStream.toByteArray()));

            // Create an Anchor component with the StreamResource
            Anchor downloadLink = new Anchor(resource, "");
            downloadLink.getElement().setAttribute("download", true);
            add(downloadLink);
            // Programmatically click the download link to initiate the download
            downloadLink.getElement().callJsFunction("click");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createHeaderRowWorkplan(Workbook workbook, Sheet sheet) {
        short rowHeight = 500;
        short tr = 0;
        Font fontBold2 = workbook.createFont();
        fontBold2.setFontName("Arial");
        fontBold2.setFontHeightInPoints((short) 10);
        fontBold2.setBold(false);

        Font fontBold = workbook.createFont();
        fontBold.setFontName("Arial");
        fontBold.setFontHeightInPoints((short) 10);
        fontBold.setBold(true);

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.RED.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(true);
        style.setFont(fontBold);

        CellStyle styleq = workbook.createCellStyle();
        styleq.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
        styleq.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq.setAlignment(HorizontalAlignment.CENTER);
        styleq.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq.setWrapText(true);
        styleq.setFont(fontBold);

        CellStyle styleq2 = workbook.createCellStyle();
        styleq2.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.index);
        styleq2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq2.setAlignment(HorizontalAlignment.CENTER);
        styleq2.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq2.setWrapText(true);
        styleq2.setFont(fontBold);

        CellStyle styleq31 = workbook.createCellStyle();
        styleq31.setAlignment(HorizontalAlignment.CENTER);
        styleq31.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq31.setWrapText(true);
        styleq31.setFont(fontBold);

        CellStyle styleq3 = workbook.createCellStyle();
        styleq3.setFillForegroundColor(IndexedColors.VIOLET.index);
        styleq3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq3.setAlignment(HorizontalAlignment.CENTER);
        styleq3.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq3.setWrapText(true);
        styleq3.setFont(fontBold);

        CellStyle styleq4 = workbook.createCellStyle();
        styleq4.setFillForegroundColor(IndexedColors.TAN.index);
        styleq4.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq4.setAlignment(HorizontalAlignment.CENTER);
        styleq4.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq4.setWrapText(true);
        styleq4.setFont(fontBold);

        CellStyle styley = workbook.createCellStyle();
        styley.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
        styley.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styley.setAlignment(HorizontalAlignment.LEFT);
        styley.setVerticalAlignment(VerticalAlignment.CENTER);
        styley.setWrapText(true);//styley.setFont(fontBold);

        CellStyle stylegreen = workbook.createCellStyle();
        stylegreen.setFillForegroundColor(IndexedColors.GREEN.index);
        stylegreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        stylegreen.setAlignment(HorizontalAlignment.CENTER);
        stylegreen.setVerticalAlignment(VerticalAlignment.CENTER);
        stylegreen.setFont(fontBold);
        stylegreen.setWrapText(true);
        stylegreen.setFont(fontBold);

        CellStyle stylec = workbook.createCellStyle();
        stylec.setAlignment(HorizontalAlignment.CENTER);
        stylec.setVerticalAlignment(VerticalAlignment.CENTER);
        stylec.setWrapText(true);//stylec.setFont(fontBold);
        stylec.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));

        CellStyle stylec2 = workbook.createCellStyle();
        stylec2.setAlignment(HorizontalAlignment.CENTER);
        stylec2.setVerticalAlignment(VerticalAlignment.CENTER);
        stylec2.setWrapText(true);
        stylec2.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
        stylec2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        stylec2.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));

        CellStyle stylec1 = workbook.createCellStyle();
        stylec1.setAlignment(HorizontalAlignment.CENTER);
        stylec1.setVerticalAlignment(VerticalAlignment.CENTER);
        stylec1.setWrapText(true);
        stylec1.setFont(fontBold);

        CellStyle borderedStyle = workbook.createCellStyle();
        borderedStyle.setBorderTop(BorderStyle.THIN);
        borderedStyle.setBorderBottom(BorderStyle.THIN);
        borderedStyle.setBorderLeft(BorderStyle.THIN);
        borderedStyle.setBorderRight(BorderStyle.THIN);
        borderedStyle.setAlignment(HorizontalAlignment.CENTER);
        borderedStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle borderedStyleWithColor = workbook.createCellStyle();
        borderedStyleWithColor.cloneStyleFrom(borderedStyle); // Copy styles from the borderedStyle
        borderedStyleWithColor.setFillForegroundColor(IndexedColors.RED.index);
        borderedStyleWithColor.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle style11 = workbook.createCellStyle();
        style11.setAlignment(HorizontalAlignment.LEFT);
        style11.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style11.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));
        List<Integer> rowBoldcount = new ArrayList();
        Row headerRow = sheet.createRow(tr);

        try {

            headerRow.setHeight(rowHeight);

            addImageToHeader(sheet, "/META-INF/resources/images/urclogo.png");

        } catch (IOException ex) {
            Logger.getLogger(BudgetReportsView.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Create a cell for the header
        // Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(1);
        headerCell.setCellValue("UGANDA RAILWAYS CORPORATION");
        headerCell.setCellStyle(styleq31);
        CellRangeAddress cellRange3 = new CellRangeAddress(tr, tr, 1, 25);
        sheet.addMergedRegion(cellRange3);
        setBottomBorderForRegion(sheet, cellRange3);
        tr++;
        Row row0 = sheet.createRow(tr);
        Cell cellq = row0.createCell((short) 0);
        row0.getCell(0).setCellStyle(styleq31);
        cellq.setCellValue("FINANCIAL PERFORMANCE REPORT");
        CellRangeAddress cellRange4 = new CellRangeAddress(tr, tr, 0, 26);
        sheet.addMergedRegion(cellRange4);
        rowBoldcount.add((int) 0);
        tr++;

        Row row = sheet.createRow(tr);
        Cell cell = row.createCell((short) 0);
        row.getCell(0).setCellStyle(styleq31);
        cell.setCellValue("Code");
        Cell cell2 = row.createCell((short) 1);
        row.getCell(1).setCellStyle(styleq31);
        cell2.setCellValue("Description");
        Cell cell3 = row.createCell((short) 2);
        row.getCell(2).setCellStyle(styleq31);
        cell3.setCellValue(yearString(setFY(budget.getValue()), "Jul"));
        Cell cell4 = row.createCell((short) 3);
        row.getCell(3).setCellStyle(styleq31);
        cell4.setCellValue(yearString(setFY(budget.getValue()), "Jul Actual"));
        Cell cell5 = row.createCell((short) 4);
        row.getCell(4).setCellStyle(styleq31);
        cell5.setCellValue(yearString(setFY(budget.getValue()), "Aug"));

        Cell cell6 = row.createCell((short) 5);
        row.getCell(5).setCellStyle(styleq31);
        cell6.setCellValue(yearString(setFY(budget.getValue()), "Aug Actual"));
        Cell cell7 = row.createCell((short) 6);
        row.getCell(6).setCellStyle(styleq31);
        cell7.setCellValue(yearString(setFY(budget.getValue()), "Sep"));
        Cell cell8 = row.createCell((short) 7);
        row.getCell(7).setCellStyle(styleq31);
        cell8.setCellValue(yearString(setFY(budget.getValue()), "Sep Actual"));
        Cell cell9 = row.createCell((short) 8);
        row.getCell(8).setCellStyle(styleq31);
        cell9.setCellValue(yearString(setFY(budget.getValue()), "Oct"));
        Cell cell10 = row.createCell((short) 9);
        row.getCell(9).setCellStyle(styleq31);
        cell10.setCellValue(yearString(setFY(budget.getValue()), "Oct Actual"));

        Cell cella = row.createCell((short) 10);
        row.getCell(10).setCellStyle(styleq31);
        cella.setCellValue(yearString(setFY(budget.getValue()), "Nov"));
        Cell cellb = row.createCell((short) 11);
        row.getCell(11).setCellStyle(styleq31);
        cellb.setCellValue(yearString(setFY(budget.getValue()), "Nov Actual"));
        Cell cellc = row.createCell((short) 12);
        row.getCell(12).setCellStyle(styleq31);
        cellc.setCellValue(yearString(setFY(budget.getValue()), "Dec"));
        Cell celld = row.createCell((short) 13);
        row.getCell(13).setCellStyle(styleq31);
        celld.setCellValue(yearString(setFY(budget.getValue()), "Dec Actual"));

        Cell cella1 = row.createCell((short) 14);
        row.getCell(14).setCellStyle(styleq31);
        cella1.setCellValue(yearString(setFY(budget.getValue()), "Jan"));
        Cell cellb1 = row.createCell((short) 15);
        row.getCell(15).setCellStyle(styleq31);
        cellb1.setCellValue(yearString(setFY(budget.getValue()), "Jan Actual"));
        Cell cellc1 = row.createCell((short) 16);
        row.getCell(16).setCellStyle(styleq31);
        cellc1.setCellValue(yearString(setFY(budget.getValue()), "Feb"));
        Cell celld1 = row.createCell((short) 17);
        row.getCell(17).setCellStyle(styleq31);
        celld1.setCellValue(yearString(setFY(budget.getValue()), "Feb Actual"));

        Cell celle1 = row.createCell((short) 18);
        row.getCell(18).setCellStyle(styleq31);
        celle1.setCellValue(yearString(setFY(budget.getValue()), "Mar"));
        Cell cellf1 = row.createCell((short) 19);
        row.getCell(19).setCellStyle(styleq31);
        cellf1.setCellValue(yearString(setFY(budget.getValue()), "Mar Actual"));
        Cell cellg1 = row.createCell((short) 20);
        row.getCell(20).setCellStyle(styleq31);
        cellg1.setCellValue(yearString(setFY(budget.getValue()), "Apr"));
        Cell cellh1 = row.createCell((short) 21);
        row.getCell(21).setCellStyle(styleq31);
        cellh1.setCellValue(yearString(setFY(budget.getValue()), "Apr Actual"));
        Cell celli1 = row.createCell((short) 22);
        row.getCell(22).setCellStyle(styleq31);
        celli1.setCellValue(yearString(setFY(budget.getValue()), "May"));
        Cell cellj1 = row.createCell((short) 23);
        row.getCell(23).setCellStyle(styleq31);
        cellj1.setCellValue(yearString(setFY(budget.getValue()), "May Actual"));
        Cell cellk1 = row.createCell((short) 24);
        row.getCell(24).setCellStyle(styleq31);
        cellk1.setCellValue(yearString(setFY(budget.getValue()), "Jun"));
        Cell celll1 = row.createCell((short) 25);
        row.getCell(25).setCellStyle(styleq31);
        celll1.setCellValue(yearString(setFY(budget.getValue()), "Jun Actual"));
        rowBoldcount.add((int) 1);

        int rowend = 0;
        int rowstart2 = 0;

        List<BudgetItemsActuals> findDistinctBudgetItemses = budgetItemsService.findDistinctBudgetItemses(budget.getValue(), comboBoxD_Section.getSelectedItems());
        for (BudgetItemsActuals h : findDistinctBudgetItemses) {
            tr++;
            short tc = 0;
            Row rowx1 = sheet.createRow(tr);
            rowx1.createCell((short) tc).setCellValue(h.getCoacode().getCode());
            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getItem());

            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getJul().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec2);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getJulA().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getAug().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec2);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getAugA().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getSep().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec2);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getSepA().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getOct().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec2);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getOctA().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getNov().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec2);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getNovA().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getDec().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec2);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getDecA().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getJan().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec2);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getJanA().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getFeb().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec2);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getFebA().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getMar().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec2);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getMarA().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getApr().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec2);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getAprA().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getMay().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec2);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getMayA().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getJun().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec2);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getJunA().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec);

        }

        for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }
        int y = 0;
        for (Row currentRow : sheet) {
            y++;
            if (currentRow == null) {
                continue;
            }

            for (Cell currentCell : currentRow) {
                if (currentCell == null) {
                    continue;
                }
                if (y > 1) {
// Get the existing cell style
                    CellStyle existingStyle = currentCell.getCellStyle();

// Create a new style that combines the existing style with the border style
                    CellStyle newStyle = sheet.getWorkbook().createCellStyle();
                    newStyle.cloneStyleFrom(existingStyle);
                    newStyle.setBorderTop(BorderStyle.THIN);
                    newStyle.setBorderBottom(BorderStyle.THIN);
                    newStyle.setBorderLeft(BorderStyle.THIN);
                    newStyle.setBorderRight(BorderStyle.THIN);
                    newStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setWrapText(true);

                    currentCell.setCellStyle(newStyle);
                }

            }
        }

    }

    private void createHeaderRowWorkplanQtr(Workbook workbook, Sheet sheet) {

        // ===== Styles (cached) =====
        Map<SKey, CellStyle> st = buildQtrStyles(workbook);

        // Columns:
        // 0 Code, 1 Description,
        // 2 Qtr1 Budget, 3 Qtr1 Actual,
        // 4 Qtr2 Budget, 5 Qtr2 Actual,
        // 6 Qtr3 Budget, 7 Qtr3 Actual,
        // 8 Qtr4 Budget, 9 Qtr4 Actual,
        // 10 Total Budget, 11 Total Actual, 12 Variance
        final int COL_CODE = 0;
        final int COL_DESC = 1;
        final int COL_Q1_B = 2, COL_Q1_A = 3;
        final int COL_Q2_B = 4, COL_Q2_A = 5;
        final int COL_Q3_B = 6, COL_Q3_A = 7;
        final int COL_Q4_B = 8, COL_Q4_A = 9;
        final int COL_TB = 10, COL_TA = 11, COL_VAR = 12;
        final int LAST_COL = 12;

        int r = 0;

        // ===== Row 0: Logo + Title =====
        Row titleRow = sheet.createRow(r);
        titleRow.setHeightInPoints(26);

        try {
            addImageToHeader(sheet, "/META-INF/resources/images/urclogo.png");
        } catch (IOException ex) {
            Logger.getLogger(BudgetReportsView.class.getName()).log(Level.SEVERE, null, ex);
        }

        Cell titleCell = titleRow.createCell(1);
        titleCell.setCellValue("UGANDA RAILWAYS CORPORATION");
        titleCell.setCellStyle(st.get(SKey.TITLE));
        sheet.addMergedRegion(new CellRangeAddress(r, r, 1, LAST_COL));
        r++;

        // ===== Row 1: Subtitle band =====
        String selectedNames = comboBoxD_Section.getSelectedItems().isEmpty()
                ? "All Cost Centres"
                : comboBoxD_Section.getSelectedItems().stream()
                        .map(UrcDeptSectionAnlDimbgt::getNAME)
                        .filter(Objects::nonNull)
                        .sorted()
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("All Cost Centres");

        String subtitle = "BUDGET " + budget.getValue().getFinancialYear()
                + " VS ACTUALS " + gen.getPreviousFy(budget.getValue().getFinancialYear())
                + " | Cost Centres: " + selectedNames;

        Row subRow = sheet.createRow(r);
        subRow.setHeightInPoints(18);

        Cell subCell = subRow.createCell(0);
        subCell.setCellValue(subtitle);
        subCell.setCellStyle(st.get(SKey.SUBTITLE));
        sheet.addMergedRegion(new CellRangeAddress(r, r, 0, LAST_COL));

        // style merged area to keep band fill consistent
        for (int c = 1; c <= LAST_COL; c++) {
            Cell cc = subRow.createCell(c);
            cc.setCellStyle(st.get(SKey.SUBTITLE));
        }
        r++;

        // ===== Row 2: Header row =====
        Row hdr = sheet.createRow(r);
        hdr.setHeightInPoints(34);

        createTextCell(hdr, COL_CODE, "Code", st.get(SKey.HEADER));
        createTextCell(hdr, COL_DESC, "Description", st.get(SKey.HEADER));
        createTextCell(hdr, COL_Q1_B, "Qtr1", st.get(SKey.HEADER));
        createTextCell(hdr, COL_Q1_A, "Qtr1 Actual", st.get(SKey.HEADER));
        createTextCell(hdr, COL_Q2_B, "Qtr2", st.get(SKey.HEADER));
        createTextCell(hdr, COL_Q2_A, "Qtr2 Actual", st.get(SKey.HEADER));
        createTextCell(hdr, COL_Q3_B, "Qtr3", st.get(SKey.HEADER));
        createTextCell(hdr, COL_Q3_A, "Qtr3 Actual", st.get(SKey.HEADER));
        createTextCell(hdr, COL_Q4_B, "Qtr4", st.get(SKey.HEADER));
        createTextCell(hdr, COL_Q4_A, "Qtr4 Actual", st.get(SKey.HEADER));
        createTextCell(hdr, COL_TB, "Total Budget", st.get(SKey.HEADER));
        createTextCell(hdr, COL_TA, "Total Actual", st.get(SKey.HEADER));
        createTextCell(hdr, COL_VAR, "Variance", st.get(SKey.HEADER));
        r++;

        // Freeze below header; lock first 2 columns
        sheet.createFreezePane(2, 3);

        // ===== Data (sorted by COA code so subtotals appear under groups) =====
        List<BudgetItemsActuals> items
                = budgetItemsService.findDistinctBudgetItemses(budget.getValue(), comboBoxD_Section.getSelectedItems());

        items.sort((a, b) -> {
            String ca = a.getCoacode() != null && a.getCoacode().getCode() != null ? a.getCoacode().getCode() : "";
            String cb = b.getCoacode() != null && b.getCoacode().getCode() != null ? b.getCoacode().getCode() : "";
            return ca.compareTo(cb);
        });

        // running totals per group (1) and (2/3)
        double[] run1 = new double[LAST_COL + 1];
        double[] run23 = new double[LAST_COL + 1];
        String currentGroup = null;
        int dataIndex = 0;

        for (BudgetItemsActuals h : items) {
            String code = safeCode(h);

            String group = code.startsWith("1") ? "1"
                    : (code.startsWith("2") || code.startsWith("3")) ? "23"
                    : null;

            // group change -> insert subtotal right here (below previous group)
            if (currentGroup != null && !Objects.equals(currentGroup, group)) {
                if ("1".equals(currentGroup)) {
                    r = insertQtrSubtotal(sheet, r, "TOTAL INCOME", run1, st, LAST_COL, COL_VAR);
                } else if ("23".equals(currentGroup)) {
                    r = insertQtrSubtotal(sheet, r, "TOTAL EXPENDITURE", run23, st, LAST_COL, COL_VAR);
                }
            }
            currentGroup = group;

            Row row = sheet.createRow(r++);
            // do not hard-set row height; let width reduce wrapping
            boolean zebra = (dataIndex++ % 2 == 1);
            CellStyle textStyle = zebra ? st.get(SKey.TEXT_ZEBRA) : st.get(SKey.TEXT);
            CellStyle moneyStyle = zebra ? st.get(SKey.MONEY_ZEBRA) : st.get(SKey.MONEY);

            // Code + Description
            createTextCell(row, COL_CODE, code, st.get(SKey.CODE));
            createTextCell(row, COL_DESC, safeStr(h.getItem()), textStyle);

            // Quarter sums (null-safe)
            double q1b = sum(bd(h.getJul()), bd(h.getAug()), bd(h.getSep()));
            double q1a = sum(bd(h.getJulA()), bd(h.getAugA()), bd(h.getSepA()));
            double q2b = sum(bd(h.getOct()), bd(h.getNov()), bd(h.getDec()));
            double q2a = sum(bd(h.getOctA()), bd(h.getNovA()), bd(h.getDecA()));
            double q3b = sum(bd(h.getJan()), bd(h.getFeb()), bd(h.getMar()));
            double q3a = sum(bd(h.getJanA()), bd(h.getFebA()), bd(h.getMarA()));
            double q4b = sum(bd(h.getApr()), bd(h.getMay()), bd(h.getJun()));
            double q4a = sum(bd(h.getAprA()), bd(h.getMayA()), bd(h.getJunA()));

            double tb = q1b + q2b + q3b + q4b;
            double ta = q1a + q2a + q3a + q4a;
            double var = ta - tb;

            createMoneyCell(row, COL_Q1_B, q1b, moneyStyle);
            createMoneyCell(row, COL_Q1_A, q1a, moneyStyle);
            createMoneyCell(row, COL_Q2_B, q2b, moneyStyle);
            createMoneyCell(row, COL_Q2_A, q2a, moneyStyle);
            createMoneyCell(row, COL_Q3_B, q3b, moneyStyle);
            createMoneyCell(row, COL_Q3_A, q3a, moneyStyle);
            createMoneyCell(row, COL_Q4_B, q4b, moneyStyle);
            createMoneyCell(row, COL_Q4_A, q4a, moneyStyle);
            createMoneyCell(row, COL_TB, tb, moneyStyle);
            createMoneyCell(row, COL_TA, ta, moneyStyle);

            // variance styling (overspend red, underspend/ok green)
            createMoneyCell(row, COL_VAR, var, var > 0 ? st.get(SKey.VAR_NEG) : st.get(SKey.VAR_POS));

            // accumulate group totals (sum each numeric column; variance separately is sum of variances)
            if ("1".equals(group)) {
                run1[COL_Q1_B] += q1b;
                run1[COL_Q1_A] += q1a;
                run1[COL_Q2_B] += q2b;
                run1[COL_Q2_A] += q2a;
                run1[COL_Q3_B] += q3b;
                run1[COL_Q3_A] += q3a;
                run1[COL_Q4_B] += q4b;
                run1[COL_Q4_A] += q4a;
                run1[COL_TB] += tb;
                run1[COL_TA] += ta;
                run1[COL_VAR] += var;
            } else if ("23".equals(group)) {
                run23[COL_Q1_B] += q1b;
                run23[COL_Q1_A] += q1a;
                run23[COL_Q2_B] += q2b;
                run23[COL_Q2_A] += q2a;
                run23[COL_Q3_B] += q3b;
                run23[COL_Q3_A] += q3a;
                run23[COL_Q4_B] += q4b;
                run23[COL_Q4_A] += q4a;
                run23[COL_TB] += tb;
                run23[COL_TA] += ta;
                run23[COL_VAR] += var;
            }
        }

        // insert last group subtotal
        if ("1".equals(currentGroup)) {
            r = insertQtrSubtotal(sheet, r, "TOTAL INCOME", run1, st, LAST_COL, COL_VAR);
        } else if ("23".equals(currentGroup)) {
            r = insertQtrSubtotal(sheet, r, "TOTAL EXPENDITURE", run23, st, LAST_COL, COL_VAR);
        }

        // ===== Column sizing (executive) =====
        applyQtrColumnSizing(sheet, LAST_COL);

        // Print setup
        PrintSetup ps = sheet.getPrintSetup();
        ps.setLandscape(true);
        ps.setFitWidth((short) 1);
        ps.setFitHeight((short) 0);
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);
        sheet.setRepeatingRows(CellRangeAddress.valueOf("1:3"));

        // Clean look
        sheet.setDisplayGridlines(false);
        sheet.setDefaultRowHeightInPoints(18);
    }

    private void createMoneyCell(Row row, int colIndex, double value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private String safeStr(String s) {
        return s == null ? "" : s;
    }

    private String safeCode(BudgetItemsActuals h) {
        if (h == null || h.getCoacode() == null || h.getCoacode().getCode() == null) {
            return "";
        }
        return h.getCoacode().getCode();
    }

    private double bd(BigDecimal v) {
        return v == null ? 0d : v.doubleValue();
    }

    private double sum(double a, double b, double c) {
        return a + b + c;
    }

// ----------------- Styles for QTR report -----------------
    private Map<SKey, CellStyle> buildQtrStyles(Workbook wb) {
        Map<SKey, CellStyle> s = new EnumMap<>(SKey.class);
        DataFormat df = wb.createDataFormat();

        Font title = wb.createFont();
        title.setFontName("Calibri");
        title.setFontHeightInPoints((short) 16);
        title.setBold(true);

        Font hdr = wb.createFont();
        hdr.setFontName("Calibri");
        hdr.setFontHeightInPoints((short) 10);
        hdr.setBold(true);
        hdr.setColor(IndexedColors.WHITE.getIndex());

        Font normal = wb.createFont();
        normal.setFontName("Calibri");
        normal.setFontHeightInPoints((short) 10);

        Font bold = wb.createFont();
        bold.setFontName("Calibri");
        bold.setFontHeightInPoints((short) 10);
        bold.setBold(true);

        java.util.function.Consumer<CellStyle> borders = st -> {
            st.setBorderTop(BorderStyle.THIN);
            st.setBorderBottom(BorderStyle.THIN);
            st.setBorderLeft(BorderStyle.THIN);
            st.setBorderRight(BorderStyle.THIN);
            st.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            st.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            st.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            st.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        };

        CellStyle titleSt = wb.createCellStyle();
        titleSt.setFont(title);
        titleSt.setAlignment(HorizontalAlignment.CENTER);
        titleSt.setVerticalAlignment(VerticalAlignment.CENTER);
        s.put(SKey.TITLE, titleSt);

        CellStyle sub = wb.createCellStyle();
        sub.setFont(bold);
        sub.setAlignment(HorizontalAlignment.LEFT);
        sub.setVerticalAlignment(VerticalAlignment.CENTER);
        sub.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        sub.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        borders.accept(sub);
        s.put(SKey.SUBTITLE, sub);

        CellStyle header = wb.createCellStyle();
        header.setFont(hdr);
        header.setAlignment(HorizontalAlignment.CENTER);
        header.setVerticalAlignment(VerticalAlignment.CENTER);
        header.setWrapText(true);
        header.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        borders.accept(header);
        s.put(SKey.HEADER, header);

        CellStyle code = wb.createCellStyle();
        code.setFont(normal);
        code.setAlignment(HorizontalAlignment.CENTER);
        code.setVerticalAlignment(VerticalAlignment.TOP);
        borders.accept(code);
        s.put(SKey.CODE, code);

        CellStyle text = wb.createCellStyle();
        text.setFont(normal);
        text.setAlignment(HorizontalAlignment.LEFT);
        text.setVerticalAlignment(VerticalAlignment.TOP); // looks cleaner with wrapping
        text.setWrapText(true);
        borders.accept(text);
        s.put(SKey.TEXT, text);

        CellStyle textZ = wb.createCellStyle();
        textZ.cloneStyleFrom(text);
        textZ.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        textZ.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.put(SKey.TEXT_ZEBRA, textZ);

        CellStyle money = wb.createCellStyle();
        money.cloneStyleFrom(text);
        money.setAlignment(HorizontalAlignment.RIGHT);
        money.setDataFormat(df.getFormat("#,##0.00"));
        s.put(SKey.MONEY, money);

        CellStyle moneyZ = wb.createCellStyle();
        moneyZ.cloneStyleFrom(money);
        moneyZ.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        moneyZ.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.put(SKey.MONEY_ZEBRA, moneyZ);

        CellStyle totalLabel = wb.createCellStyle();
        totalLabel.cloneStyleFrom(sub);
        totalLabel.setAlignment(HorizontalAlignment.LEFT);
        s.put(SKey.TOTAL_LABEL, totalLabel);

        CellStyle totalMoney = wb.createCellStyle();
        totalMoney.cloneStyleFrom(sub);
        totalMoney.setAlignment(HorizontalAlignment.RIGHT);
        totalMoney.setDataFormat(df.getFormat("#,##0.00"));
        s.put(SKey.TOTAL_MONEY, totalMoney);

        CellStyle varPos = wb.createCellStyle();
        varPos.cloneStyleFrom(money);
        varPos.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        varPos.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.put(SKey.VAR_POS, varPos);

        CellStyle varNeg = wb.createCellStyle();
        varNeg.cloneStyleFrom(money);
        varNeg.setFillForegroundColor(IndexedColors.ROSE.getIndex());
        varNeg.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.put(SKey.VAR_NEG, varNeg);

        return s;
    }
// ----------------- Subtotal insertion -----------------

    private int insertQtrSubtotal(Sheet sheet,
            int rowIndex,
            String label,
            double[] totals,
            Map<SKey, CellStyle> st,
            int lastCol,
            int varianceCol) {

        Row row = sheet.createRow(rowIndex++);
        row.setHeightInPoints(18);

        Cell c0 = row.createCell(0);
        c0.setCellStyle(st.get(SKey.TOTAL_LABEL));

        Cell c1 = row.createCell(1);
        c1.setCellValue(label);
        c1.setCellStyle(st.get(SKey.TOTAL_LABEL));

        for (int col = 2; col <= lastCol; col++) {
            Cell cell = row.createCell(col);
            cell.setCellValue(totals[col]);
            if (col == varianceCol) {
                cell.setCellStyle(totals[col] > 0 ? st.get(SKey.VAR_NEG) : st.get(SKey.VAR_POS));
            } else {
                cell.setCellStyle(st.get(SKey.TOTAL_MONEY));
            }
            totals[col] = 0d; // reset for next group
        }
        return rowIndex;
    }

// ----------------- Executive sizing -----------------
    private void applyQtrColumnSizing(Sheet sheet, int lastCol) {
        sheet.setColumnWidth(0, 14 * 256);  // Code
        sheet.setColumnWidth(1, 75 * 256);  // Description (wide => less wrapping => shorter rows)

        for (int i = 2; i <= lastCol; i++) {
            sheet.setColumnWidth(i, 16 * 256);
        }
        sheet.setColumnWidth(12, 18 * 256); // Variance a bit wider
    }
// ----------------- Cell helpers (null-safe) -----------------

    private void createTextCell(Row row, int colIndex, String value, CellStyle style) {
        Cell cell = row.createCell(colIndex);
        cell.setCellValue(value == null ? "" : value);
        cell.setCellStyle(style);
    }

    private static void addImageToHeader(Sheet sheet, String imagePath) throws IOException {
        // Load the image
        BufferedImage bufferedImage = ImageIO.read(BudgetReportsView.class.getResourceAsStream(imagePath));

        // Convert the image to a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        // Add the image to the header
        Drawing<?> drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 0, 1, 1);

        int pictureIndex = sheet.getWorkbook().addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
        drawing.createPicture(anchor, pictureIndex);
    }

    private void setBottomBorderForRegion(Sheet sheet, CellRangeAddress region) {
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
    }

    private static void createMergedRegion(Sheet sheet, int startRow, int endRow, int startColumn, int endColumn) {
        CellRangeAddress cellRangeAddress = new CellRangeAddress(startRow, endRow, startColumn, endColumn);
        sheet.addMergedRegion(cellRangeAddress);
    }

    private static boolean isOverlappingWithExistingRegions(Sheet sheet, CellRangeAddress newRegion) {
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress existingRegion = sheet.getMergedRegion(i);
            if (newRegion.isInRange(existingRegion.getFirstRow(), existingRegion.getFirstColumn())
                    || newRegion.isInRange(existingRegion.getLastRow(), existingRegion.getLastColumn())) {
                return true; // Overlapping
            }
        }
        return false; // Not overlapping
    }

// Method to add a merged region based on the sheet type
    private static void addMergedRegion(Sheet sheet, CellRangeAddress region) {
        if (sheet instanceof XSSFSheet) {
            ((XSSFSheet) sheet).addMergedRegion(region);
        } else if (sheet instanceof HSSFSheet) {
            ((HSSFSheet) sheet).addMergedRegion(region);
        }
    }

    public Dialog createTransactionsDialog(BudgetItemsActuals target, String month, Month month2) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(target.getCoacode().getCode() + " " + month + " Transactions " + budget.getValue().getFinancialYear());

        Grid<SALFLDGProjection> gridTransactions = new Grid<>(SALFLDGProjection.class, false);
        gridTransactions.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridTransactions.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);

        // Columns
        gridTransactions.addColumn(SALFLDGProjection::getAccntCode)
                .setHeader("Code")
                .setWidth("80px")
                .setFlexGrow(0)
                .setSortable(true).setTooltipGenerator(trans -> {
            COA coa = sampleCoaService.findByCodeAndBudget(trans.getAccntCode(), budget.getValue());
            return coa.getName();
        });

        gridTransactions.addColumn(SALFLDGProjection::getDescriptn).setHeader("Name").setFrozenToEnd(true);

        gridTransactions.addColumn(SALFLDGProjection::getJrnalNo)
                .setHeader("Journal No.")
                .setWidth("80px")
                .setFlexGrow(0);

        List<SALFLDGProjection> items = refreshgridTransactions(target.getBudget(), target.getCoacode().getCode(), month2);
        footerTotal.setText("Total: " + sumAmounts(items));
        footerTotal.getStyle().set("text-align", "right").set("font-weight", "bold");

        gridTransactions.addColumn(salfldg -> {
            BigDecimal amount = salfldg.getAmount();
            return amount != null ? formatAmount(amount) : "";
        }).setHeader("Amount").setAutoWidth(true).setFooter(footerTotal);
        gridTransactions.addColumn(SALFLDGProjection::getTransDatetime).setHeader("Trans Date");
        gridTransactions.addColumn(SALFLDGProjection::getAnalT1).setHeader("Section").setTooltipGenerator(trans -> {
            String sectcode = trans.getAnalT1();
            if (sectcode == null || sectcode.trim() == "") {
                return "_";
            } else {
                // return sampleUrcDeptSectionAnlDimbgtService.findByANL_CODE(trans.getAnalT1()).getNAME();

                return sectcode;
            }

        });
        gridTransactions.addColumn(SALFLDGProjection::getPeriod).setHeader("Period");

        gridTransactions.getStyle().set("width", "100%").set("max-width", "100%");
        gridTransactions.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridTransactions.setItems(items);

        // Buttons
        Button filterButton = new Button("Close", new Icon(VaadinIcon.CLOSE), ef -> dialog.close());
        filterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button downloadButton = new Button("Download", new Icon(VaadinIcon.ENVELOPE_O), ef
                -> {
            // List<SALFLDGProjection>  items2 = refreshgridTransactions(target.getBudget().getFinancialYear(), target.getCoacode().getCode(), month2);
            exportAndDownloadExcelTransactionDetails(budget.getValue(), target.getCoacode(), items, month);
            dialog.close();
        });
        dialog.getFooter().add(filterButton, downloadButton);

        dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        dialog.add(gridTransactions);
        dialog.setWidth("80%");
        dialog.open();

        return dialog;
    }

    public Dialog createTransactionsDialog2(BudgetItemsActuals target, String month) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(target.getCoacode().getCode() + " Total Transactions " + budget.getValue().getFinancialYear());

        Grid<SALFLDGProjection> gridTransactions = new Grid<>(SALFLDGProjection.class, false);
        gridTransactions.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridTransactions.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);

        // Columns
        gridTransactions.addColumn(SALFLDGProjection::getAccntCode)
                .setHeader("Code")
                .setWidth("80px")
                .setFlexGrow(0)
                .setSortable(true).setTooltipGenerator(trans -> {
            COA coa = sampleCoaService.findByCodeAndBudget(trans.getAccntCode(), budget.getValue());
            return coa.getName();
        });

        gridTransactions.addColumn(SALFLDGProjection::getDescriptn).setHeader("Name").setFrozenToEnd(true);

        gridTransactions.addColumn(SALFLDGProjection::getJrnalNo)
                .setHeader("Journal No.")
                .setWidth("80px")
                .setFlexGrow(0);

        List<SALFLDGProjection> items = refreshgridTransactions2(target.getBudget().getFinancialYear(), target.getCoacode().getCode());
        footerTotal.setText("Total: " + sumAmounts(items));
        footerTotal.getStyle().set("text-align", "right").set("font-weight", "bold");

        gridTransactions.addColumn(salfldg -> {
            BigDecimal amount = salfldg.getAmount();
            return amount != null ? formatAmount(amount) : "";
        }).setHeader("Amount").setAutoWidth(true).setFooter(footerTotal);
        gridTransactions.addColumn(SALFLDGProjection::getTransDatetime).setHeader("Trans Date");
        gridTransactions.addColumn(SALFLDGProjection::getAnalT1).setHeader("Section").setTooltipGenerator(trans -> {
            String sectcode = trans.getAnalT1();
            if (sectcode == null || sectcode.trim() == "") {
                return "_";
            } else {
                // return sampleUrcDeptSectionAnlDimbgtService.findByANL_CODE(trans.getAnalT1()).getNAME();

                return sectcode;
            }

        });
        gridTransactions.addColumn(SALFLDGProjection::getPeriod).setHeader("Period");

        gridTransactions.getStyle().set("width", "100%").set("max-width", "100%");
        gridTransactions.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridTransactions.setItems(items);

        // Buttons
        Button filterButton = new Button("Close", new Icon(VaadinIcon.CLOSE), ef -> dialog.close());
        filterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button downloadButton = new Button("Download", new Icon(VaadinIcon.ENVELOPE_O), ef
                -> {
            // List<SALFLDGProjection>  items2 = refreshgridTransactions(target.getBudget().getFinancialYear(), target.getCoacode().getCode(), month2);
            exportAndDownloadExcelTransactionDetails(budget.getValue(), target.getCoacode(), items, month);
            dialog.close();
        });
        dialog.getFooter().add(filterButton, downloadButton);

        dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        dialog.add(gridTransactions);
        dialog.setWidth("80%");
        dialog.open();

        return dialog;
    }

    // Define a method to format the amount
    public List<SALFLDGProjection> refreshgridTransactions(Budget bgt, String coacode, Month month) {
        UrcDeptSectionAnlDimbgt freightAnlDimbgt = sampleUrcDeptSectionAnlDimbgtService.findByANL_CODE("S020");
        UrcDeptSectionAnlDimbgt propertymgt = sampleUrcDeptSectionAnlDimbgtService.findByANL_CODE("S004");
        COA coa = sampleCoaService.findByCodeAndBudget(coacode, budget.getValue());
        List<String> selectedAnlCodes = comboBoxD_Section.getSelectedItems().stream()
                .map(UrcDeptSectionAnlDimbgt::getANL_CODE)
                .map(String::trim)
                .collect(Collectors.toList());

        //  period = extra.generateCurrentPeriod(month, month).generatePreviousPeriod(financialYear, month);
        List<SALFLDGProjection> lis = new ArrayList<>();

        if (comboBoxD_Section.getValue().contains(freightAnlDimbgt) && (coa.getDisplay() == Display.FREIGHT || coa.getCode().contains("111109") || coa.getCode().contains("111110"))) {
            lis = sampleSALFLDGService.findByPeriodAndAccntCode(extra.getFinancialYearPeriodByMonth(bgt,month), coacode.trim());
        } else if (comboBoxD_Section.getValue().contains(propertymgt) && (coa.getCode().contains("111401") || coa.getCode().contains("111402") || coa.getCode().contains("111403") || coa.getCode().contains("111404") || coa.getCode().contains("111406") || coa.getCode().contains("111407"))) {
            lis = sampleSALFLDGService.findByPeriodAndAccntCode(extra.getFinancialYearPeriodByMonth(bgt,month), coacode.trim());
        } else {
            if(selectedAnlCodes.contains("#")){
                System.out.println("contains # "+selectedAnlCodes);
                lis = sampleSALFLDGService.findByPeriodAndAccntCodeAndAnalT1InOrUnAnalyzed(extra.getFinancialYearPeriodByMonth(bgt,month), coacode.trim(), selectedAnlCodes);
            }else{
                System.out.println("Doesn`t contain # "+selectedAnlCodes);
               lis = sampleSALFLDGService.findByPeriodAndAccntCodeAndAnalT1InAllS(extra.getFinancialYearPeriodByMonth(bgt,month), coacode.trim(), selectedAnlCodes); 
            }
            
        }

        return lis;
    }

    public List<SALFLDGProjection> refreshgridTransactions2(String financialYear, String coacode) {
        UrcDeptSectionAnlDimbgt freightAnlDimbgt = sampleUrcDeptSectionAnlDimbgtService.findByANL_CODE("S020");
        UrcDeptSectionAnlDimbgt propertymgt = sampleUrcDeptSectionAnlDimbgtService.findByANL_CODE("S004");
        COA coa = sampleCoaService.findByCodeAndBudget(coacode, budget.getValue());
        List<String> selectedAnlCodes = comboBoxD_Section.getSelectedItems().stream()
                .map(UrcDeptSectionAnlDimbgt::getANL_CODE)
                .map(String::trim)
                .collect(Collectors.toList());

        // int period = extra.generateCurrentPeriod(financialYear, month);
        List<Integer> period = extra.getListOfCurrentPeriodByFY(financialYear);
        //  period = extra.generateCurrentPeriod(month, month).generatePreviousPeriod(financialYear, month);
        List<SALFLDGProjection> lis = new ArrayList<>();

        if (comboBoxD_Section.getValue().contains(freightAnlDimbgt) && (coa.getDisplay() == Display.FREIGHT || coa.getCode().contains("111109") || coa.getCode().contains("111110"))) {
            lis = sampleSALFLDGService.findByPeriodAndAccntCode2(period, coacode.trim());
        } else if (comboBoxD_Section.getValue().contains(propertymgt) && (coa.getCode().contains("111401") || coa.getCode().contains("111402") || coa.getCode().contains("111403") || coa.getCode().contains("111404") || coa.getCode().contains("111406") || coa.getCode().contains("111407"))) {
            lis = sampleSALFLDGService.findByPeriodAndAccntCode2(period, coacode.trim());
        } else {
            lis = sampleSALFLDGService.findByPeriodAndAccntCodeAndAnalT1InAllS2(period, coacode.trim(), selectedAnlCodes);
        }

        return lis;
    }

    private void exportAndDownloadExcelTransactionDetails(Budget budget, COA coa, List<SALFLDGProjection> list, String month) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(coa.getCode() + " " + month + " Transactions " + budget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            createTransactionDetails(workbook, sheet, coa, list);
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource(coa.getCode() + " " + month + " Transactions " + budget.getFinancialYear() + ".xlsx", ()
                    -> new ByteArrayInputStream(outputStream.toByteArray()));

            // Create an Anchor component with the StreamResource
            Anchor downloadLink = new Anchor(resource, "");
            downloadLink.getElement().setAttribute("download", true);
            add(downloadLink);
            // Programmatically click the download link to initiate the download
            downloadLink.getElement().callJsFunction("click");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createTransactionDetails(Workbook workbook, Sheet sheet, COA coa, List<SALFLDGProjection> list) {
        short rowHeight = 500;
        short tr = 0;
        Font fontBold2 = workbook.createFont();
        fontBold2.setFontName("Arial");
        fontBold2.setFontHeightInPoints((short) 10);
        fontBold2.setBold(false);

        Font fontBold = workbook.createFont();
        fontBold.setFontName("Arial");
        fontBold.setFontHeightInPoints((short) 10);
        fontBold.setBold(true);

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.RED.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(true);
        style.setFont(fontBold);

        CellStyle styleq = workbook.createCellStyle();
        styleq.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
        styleq.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq.setAlignment(HorizontalAlignment.CENTER);
        styleq.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq.setWrapText(true);
        styleq.setFont(fontBold);

        CellStyle styleq2 = workbook.createCellStyle();
        styleq2.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.index);
        styleq2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq2.setAlignment(HorizontalAlignment.CENTER);
        styleq2.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq2.setWrapText(true);
        styleq2.setFont(fontBold);

        CellStyle styleq31 = workbook.createCellStyle();
        styleq31.setAlignment(HorizontalAlignment.CENTER);
        styleq31.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq31.setWrapText(true);
        styleq31.setFont(fontBold);

        CellStyle styleq3 = workbook.createCellStyle();
        styleq3.setFillForegroundColor(IndexedColors.VIOLET.index);
        styleq3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq3.setAlignment(HorizontalAlignment.CENTER);
        styleq3.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq3.setWrapText(true);
        styleq3.setFont(fontBold);

        CellStyle styleq4 = workbook.createCellStyle();
        styleq4.setFillForegroundColor(IndexedColors.TAN.index);
        styleq4.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq4.setAlignment(HorizontalAlignment.CENTER);
        styleq4.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq4.setWrapText(true);
        styleq4.setFont(fontBold);

        CellStyle styley = workbook.createCellStyle();
        styley.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
        styley.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styley.setAlignment(HorizontalAlignment.LEFT);
        styley.setVerticalAlignment(VerticalAlignment.CENTER);
        styley.setWrapText(true);//styley.setFont(fontBold);

        CellStyle stylegreen = workbook.createCellStyle();
        stylegreen.setFillForegroundColor(IndexedColors.GREEN.index);
        stylegreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        stylegreen.setAlignment(HorizontalAlignment.CENTER);
        stylegreen.setVerticalAlignment(VerticalAlignment.CENTER);
        stylegreen.setFont(fontBold);
        stylegreen.setWrapText(true);
        stylegreen.setFont(fontBold);

        CellStyle stylec = workbook.createCellStyle();
        stylec.setAlignment(HorizontalAlignment.LEFT);
        stylec.setVerticalAlignment(VerticalAlignment.CENTER);
        stylec.setWrapText(true);//stylec.setFont(fontBold);
        stylec.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));

        CellStyle stylec2 = workbook.createCellStyle();
        stylec2.setAlignment(HorizontalAlignment.CENTER);
        stylec2.setVerticalAlignment(VerticalAlignment.CENTER);
        stylec2.setWrapText(true);
        stylec2.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
        stylec2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        stylec2.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));

        CellStyle stylec1 = workbook.createCellStyle();
        stylec1.setAlignment(HorizontalAlignment.CENTER);
        stylec1.setVerticalAlignment(VerticalAlignment.CENTER);
        stylec1.setWrapText(true);
        stylec1.setFont(fontBold);

        CellStyle borderedStyle = workbook.createCellStyle();
        borderedStyle.setBorderTop(BorderStyle.THIN);
        borderedStyle.setBorderBottom(BorderStyle.THIN);
        borderedStyle.setBorderLeft(BorderStyle.THIN);
        borderedStyle.setBorderRight(BorderStyle.THIN);
        borderedStyle.setAlignment(HorizontalAlignment.CENTER);
        borderedStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle borderedStyleWithColor = workbook.createCellStyle();
        borderedStyleWithColor.cloneStyleFrom(borderedStyle); // Copy styles from the borderedStyle
        borderedStyleWithColor.setFillForegroundColor(IndexedColors.RED.index);
        borderedStyleWithColor.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle style11 = workbook.createCellStyle();
        style11.setAlignment(HorizontalAlignment.LEFT);
        style11.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style11.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));
        List<Integer> rowBoldcount = new ArrayList();
        Row headerRow = sheet.createRow(tr);

        try {

            headerRow.setHeight(rowHeight);

            addImageToHeader(sheet, "/META-INF/resources/images/urclogo.png");

        } catch (IOException ex) {
            Logger.getLogger(BudgetReportsView.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Create a cell for the header
        // Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(1);
        headerCell.setCellValue("UGANDA RAILWAYS CORPORATION");
        headerCell.setCellStyle(styleq31);
        CellRangeAddress cellRange3 = new CellRangeAddress(tr, tr, 1, 6);
        sheet.addMergedRegion(cellRange3);
        setBottomBorderForRegion(sheet, cellRange3);
        tr++;
        Row row0 = sheet.createRow(tr);
        Cell cellq = row0.createCell((short) 0);
        row0.getCell(0).setCellStyle(styleq31);
        cellq.setCellValue("TRANSACTION DETAILS " + budget.getValue().getFinancialYear() + " " + coa.getCode().trim() + " " + coa.getName());
        CellRangeAddress cellRange4 = new CellRangeAddress(tr, tr, 0, 6);
        sheet.addMergedRegion(cellRange4);
        rowBoldcount.add((int) 0);
        tr++;

        Row row = sheet.createRow(tr);
        Cell cell = row.createCell((short) 0);
        row.getCell(0).setCellStyle(styleq31);
        cell.setCellValue("Code");
        Cell cell2 = row.createCell((short) 1);
        row.getCell(1).setCellStyle(styleq31);
        cell2.setCellValue("Description");
        Cell cell3 = row.createCell((short) 2);
        row.getCell(2).setCellStyle(styleq31);
        cell3.setCellValue("Journal No.");
        Cell cell4 = row.createCell((short) 3);
        row.getCell(3).setCellStyle(styleq31);
        cell4.setCellValue("Amount");
        Cell cell5 = row.createCell((short) 4);
        row.getCell(4).setCellStyle(styleq31);
        cell5.setCellValue("Date");

        Cell cell6 = row.createCell((short) 5);
        row.getCell(5).setCellStyle(styleq31);
        cell6.setCellValue("Section");

        Cell cell7 = row.createCell((short) 6);
        row.getCell(6).setCellStyle(styleq31);
        cell7.setCellValue("period");

        rowBoldcount.add((int) 1);
        CreationHelper createHelper = workbook.getCreationHelper();
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));

// Format for dates, e.g., "yyyy-MM-dd" or any other format you need
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (SALFLDGProjection h : list) {
            tr++;
            short tc = 0;
            Row rowx1 = sheet.createRow(tr);
            rowx1.createCell((short) tc).setCellValue(h.getAccntCode());
            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getDescriptn());

            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getJrnalNo() + " ");
            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getAmount().doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;

            if (h.getTransDatetime() != null) {
                rowx1.createCell(tc).setCellValue(h.getTransDatetime());
                rowx1.getCell(tc).setCellStyle(dateCellStyle);
            } else {
                rowx1.createCell(tc).setCellValue("");
                rowx1.getCell(tc).setCellStyle(stylec);
            }
            /*            rowx1.createCell((short) tc).setCellValue(h.getTransDatetime());
                rowx1.getCell(tc).setCellStyle(stylec);*/
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getAnalT1() + " ");
            rowx1.getCell(tc).setCellStyle(stylec);

            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getPeriod() + " ");
            rowx1.getCell(tc).setCellStyle(stylec);

        }
        // Add a row for the total amount using a formula
        Row totalRow = sheet.createRow(++tr);
        short tc = 0;
        totalRow.createCell(tc).setCellValue("Total");
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        totalRow.createCell(tc).setCellValue("");
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        totalRow.createCell(tc).setCellValue("");
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;

        // Set the formula for summing up the amount column
        String amountColumnRange = String.format("D2:D%d", tr); // Assuming the Amount column is the 4th column (index 3)
        totalRow.createCell(tc).setCellFormula(String.format("SUM(%s)", amountColumnRange));
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        totalRow.createCell(tc).setCellValue("");
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        totalRow.createCell(tc).setCellValue("");
        totalRow.getCell(tc).setCellStyle(stylec);

        for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }
        int y = 0;
        for (Row currentRow : sheet) {
            y++;
            if (currentRow == null) {
                continue;
            }

            for (Cell currentCell : currentRow) {
                if (currentCell == null) {
                    continue;
                }
                if (y > 1) {
// Get the existing cell style
                    CellStyle existingStyle = currentCell.getCellStyle();

// Create a new style that combines the existing style with the border style
                    CellStyle newStyle = sheet.getWorkbook().createCellStyle();
                    newStyle.cloneStyleFrom(existingStyle);
                    newStyle.setBorderTop(BorderStyle.THIN);
                    newStyle.setBorderBottom(BorderStyle.THIN);
                    newStyle.setBorderLeft(BorderStyle.THIN);
                    newStyle.setBorderRight(BorderStyle.THIN);
                    newStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setWrapText(true);

                    currentCell.setCellStyle(newStyle);
                }

            }
        }
    }

    public String sumAmounts(List<SALFLDGProjection> projections) {
        BigDecimal sum = BigDecimal.ZERO;
        for (SALFLDGProjection projection : projections) {
            sum = sum.add(projection.getAmount());
        }
        String sums = decimalFormat.format(sum);
        if (sum == null) {
            return "";
        }
        if (sum.compareTo(BigDecimal.ZERO) < 0) {
            sums = decimalFormat.format(sum.abs());
            return "(" + sums + ")";
        } else {
            sums = decimalFormat.format(sum);
            return "(" + sums + ")";
        }

    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return ""; // Return an empty string if amount is null
        }
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
        return df.format(amount.doubleValue());
    }

    public Dialog createBudgetDialogTotal(BudgetItemsActuals target, String month, String month2) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(target.getCoacode().getCode() + " " + month + " Budget " + extra.getCurrentFy(target.getBudget().getFinancialYear()));

        Grid<BudgetItems> gridTransactions = new Grid<>(BudgetItems.class, false);
        gridTransactions.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridTransactions.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);

        // Columns
        gridTransactions.addColumn(budgetItem -> {
            COA coacode = budgetItem.getCoacode();
            Text label = new Text(coacode != null ? coacode.getCode() : "");
            return label.getText(); // Get the text content
        })
                .setHeader("Code").setWidth("80px").setFlexGrow(0).setFrozen(true)
                .setSortable(true) // Make the column sortable
                .setComparator((budgetItem1, budgetItem2) -> {
                    // Implement your custom comparator logic here
                    String name1 = budgetItem1.getCoacode() != null ? budgetItem1.getCoacode().getName() : "";
                    String name2 = budgetItem2.getCoacode() != null ? budgetItem2.getCoacode().getName() : "";
                    return name1.compareTo(name2);
                }).setFrozen(true).setTooltipGenerator(trans -> {
            return trans.getCoacode().getName();
        });

        gridTransactions.addColumn(BudgetItems::getItem).setHeader("Name").setFrozen(true).setWidth("200px");

        List<BudgetItems> list = budgetItemsService.findBudgetItemsByBudgetAndCoaAndSectios(target.getBudget(), target.getCoacode(), target.getDeptUnit(), month2);

        gridTransactions.addColumn(salfldg -> {

            return formatAmount(salfldg.getJul()) + "";
        }).setHeader("Jul").setAutoWidth(true);

        gridTransactions.addColumn(salfldg -> {
            return formatAmount(salfldg.getAug()) + "";
        }).setHeader("Aug").setAutoWidth(true);

        gridTransactions.addColumn(salfldg -> {
            return formatAmount(salfldg.getSep()) + "";
        }).setHeader("Sep").setAutoWidth(true);

        gridTransactions.addColumn(salfldg -> {
            return formatAmount(salfldg.getOct()) + "";
        }).setHeader("Oct").setAutoWidth(true);

        gridTransactions.addColumn(salfldg -> {
            return formatAmount(salfldg.getNov()) + "";
        }).setHeader("Nov").setAutoWidth(true);

        gridTransactions.addColumn(salfldg -> {
            return formatAmount(salfldg.getDec()) + "";
        }).setHeader("Dec").setAutoWidth(true);

        gridTransactions.addColumn(salfldg -> {
            return formatAmount(salfldg.getJan()) + "";
        }).setHeader("Jan").setAutoWidth(true);

        gridTransactions.addColumn(salfldg -> {
            return formatAmount(salfldg.getFeb()) + "";
        }).setHeader("Feb").setAutoWidth(true);

        gridTransactions.addColumn(salfldg -> {
            return formatAmount(salfldg.getMar()) + "";
        }).setHeader("Mar").setAutoWidth(true);

        gridTransactions.addColumn(salfldg -> {
            return formatAmount(salfldg.getApr()) + "";
        }).setHeader("Apr").setAutoWidth(true);

        gridTransactions.addColumn(salfldg -> {
            return formatAmount(salfldg.getMay()) + "";
        }).setHeader("May").setAutoWidth(true);

        gridTransactions.addColumn(salfldg -> {
            return formatAmount(salfldg.getJun()) + "";
        }).setHeader("Jun").setAutoWidth(true);

        gridTransactions.addColumn(budgetItem -> {
            UrcDeptSectionAnlDimbgt coacode = budgetItem.getDeptUnit();
            Text label = new Text(coacode != null ? coacode.getNAME() : "");
            return label.getText(); // Get the text content
        })
                .setHeader("Section").setWidth("150px").setFlexGrow(0)
                .setSortable(true) // Make the column sortable
                .setComparator((budgetItem1, budgetItem2) -> {
                    // Implement your custom comparator logic here
                    String name1 = budgetItem1.getCoacode() != null ? budgetItem1.getCoacode().getName() : "";
                    String name2 = budgetItem2.getCoacode() != null ? budgetItem2.getCoacode().getName() : "";
                    return name1.compareTo(name2);
                }).setTooltipGenerator(trans -> {
            return trans.getCoacode().getCode();
        });

        gridTransactions.addColumn(budgetItem -> {
            Text label = new Text(budgetItem.getBudgetType() != null ? budgetItem.getBudgetType().getCode() : "");
            return label.getText(); // Get the text content
        })
                .setHeader("Budget Type").setWidth("150px").setFlexGrow(0)
                .setSortable(true) // Make the column sortable
                .setComparator((budgetItem1, budgetItem2) -> {
                    // Implement your custom comparator logic here
                    String name1 = budgetItem1.getBudgetType() != null ? budgetItem1.getBudgetType().getCode() : "";
                    String name2 = budgetItem2.getBudgetType() != null ? budgetItem2.getBudgetType().getCode() : "";
                    return name1.compareTo(name2);
                }).setTooltipGenerator(trans -> {
            String sectcode = trans.getBudgetType().getName();
            if (sectcode == null || sectcode.trim() == "") {
                return "_";
            } else {
                return sectcode;
            }
        });

        gridTransactions.addColumn(budgetItem -> {
            Text label = new Text(budgetItem.getFundsource() != null ? budgetItem.getFundsource().getCode() : "");
            return label.getText(); // Get the text content
        })
                .setHeader("Fund Source").setWidth("150px").setFlexGrow(0)
                .setSortable(true) // Make the column sortable
                .setComparator((budgetItem1, budgetItem2) -> {
                    // Implement your custom comparator logic here
                    String name1 = budgetItem1.getFundsource() != null ? budgetItem1.getFundsource().getCode() : "";
                    String name2 = budgetItem2.getFundsource() != null ? budgetItem2.getFundsource().getCode() : "";
                    return name1.compareTo(name2);
                })
                .setTooltipGenerator(trans -> {
                    if (trans.getFundsource() == null || trans.getFundsource().getFundsource() == null || trans.getFundsource().getFundsource().trim().isEmpty()) {
                        return "_";
                    } else {
                        return trans.getFundsource().getFundsource();
                    }
                });

        gridTransactions.addColumn(budgetItem -> {
            Text label = new Text(budgetItem.getActivity() != null ? budgetItem.getActivity().getActivityCode() : "");
            return label.getText(); // Get the text content
        })
                .setHeader("Activity Code").setWidth("150px").setFlexGrow(0)
                .setSortable(true) // Make the column sortable
                .setComparator((budgetItem1, budgetItem2) -> {
                    // Implement your custom comparator logic here
                    String name1 = budgetItem1.getActivity() != null ? budgetItem1.getActivity().getActivityCode() : "";
                    String name2 = budgetItem2.getActivity() != null ? budgetItem2.getActivity().getActivityCode() : "";
                    return name1.compareTo(name2);
                })
                .setTooltipGenerator(trans -> {
                    if (trans.getActivity() == null || trans.getActivity().getName() == null || trans.getActivity().getName().trim().isEmpty()) {
                        return "_";
                    } else {
                        return trans.getActivity().getName();
                    }
                });

        gridTransactions.getStyle().set("width", "100%").set("max-width", "100%");
        gridTransactions.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridTransactions.setItems(list);

        // Buttons
        Button filterButton = new Button("Close", new Icon(VaadinIcon.CLOSE), ef -> dialog.close());
        filterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button downloadButton = new Button("Download", new Icon(VaadinIcon.ENVELOPE_O), ef
                -> {
            List<BudgetItems> list2 = budgetItemsService.findBudgetItemsByBudgetAndCoaAndSectios(target.getBudget(), target.getCoacode(), target.getDeptUnit(), month2);

            exportAndDownloadExcelBudgetDetailsTotal(budget.getValue(), target.getCoacode(), list2, month);
            dialog.close();
        });
        dialog.getFooter().add(filterButton, downloadButton);

        dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        dialog.add(gridTransactions);
        dialog.setWidth("80%");
        dialog.open();

        return dialog;
    }

    private void exportAndDownloadExcelBudgetDetailsTotal(Budget budget, COA coa, List<BudgetItems> list, String month) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(coa.getCode() + " " + month + " Budget " + budget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            createBudgetDetailsTotal(workbook, sheet, coa, list, month);
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource(coa.getCode() + " " + month + " Budget " + budget.getFinancialYear() + ".xlsx", ()
                    -> new ByteArrayInputStream(outputStream.toByteArray()));

            // Create an Anchor component with the StreamResource
            Anchor downloadLink = new Anchor(resource, "");
            downloadLink.getElement().setAttribute("download", true);
            add(downloadLink);
            // Programmatically click the download link to initiate the download
            downloadLink.getElement().callJsFunction("click");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createBudgetDetailsTotal(Workbook workbook, Sheet sheet, COA coa, List<BudgetItems> list, String month) {

        short rowHeight = 500;
        short tr = 0;
        Font fontBold2 = workbook.createFont();
        fontBold2.setFontName("Arial");
        fontBold2.setFontHeightInPoints((short) 10);
        fontBold2.setBold(false);

        Font fontBold = workbook.createFont();
        fontBold.setFontName("Arial");
        fontBold.setFontHeightInPoints((short) 10);
        fontBold.setBold(true);

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.RED.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(true);
        style.setFont(fontBold);

        CellStyle styleq = workbook.createCellStyle();
        // styleq.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
        //styleq.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq.setAlignment(HorizontalAlignment.LEFT);
        styleq.setVerticalAlignment(VerticalAlignment.BOTTOM);
        styleq.setWrapText(true);
        styleq.setFont(fontBold2);

        CellStyle styleq2 = workbook.createCellStyle();
        styleq2.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.index);
        styleq2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq2.setAlignment(HorizontalAlignment.CENTER);
        styleq2.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq2.setWrapText(true);
        styleq2.setFont(fontBold);

        CellStyle styleq31 = workbook.createCellStyle();
        styleq31.setAlignment(HorizontalAlignment.CENTER);
        styleq31.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq31.setWrapText(true);
        styleq31.setFont(fontBold);

        CellStyle styleq3 = workbook.createCellStyle();
        styleq3.setFillForegroundColor(IndexedColors.VIOLET.index);
        styleq3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq3.setAlignment(HorizontalAlignment.CENTER);
        styleq3.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq3.setWrapText(true);
        styleq3.setFont(fontBold);

        CellStyle styleq4 = workbook.createCellStyle();
        styleq4.setFillForegroundColor(IndexedColors.TAN.index);
        styleq4.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq4.setAlignment(HorizontalAlignment.CENTER);
        styleq4.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq4.setWrapText(true);
        styleq4.setFont(fontBold);

        CellStyle styley = workbook.createCellStyle();
        styley.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
        styley.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styley.setAlignment(HorizontalAlignment.LEFT);
        styley.setVerticalAlignment(VerticalAlignment.CENTER);
        styley.setWrapText(true);//styley.setFont(fontBold);

        CellStyle stylegreen = workbook.createCellStyle();
        stylegreen.setFillForegroundColor(IndexedColors.GREEN.index);
        stylegreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        stylegreen.setAlignment(HorizontalAlignment.CENTER);
        stylegreen.setVerticalAlignment(VerticalAlignment.CENTER);
        stylegreen.setFont(fontBold);
        stylegreen.setWrapText(true);
        stylegreen.setFont(fontBold);

        CellStyle stylec = workbook.createCellStyle();
        stylec.setAlignment(HorizontalAlignment.LEFT);
        stylec.setVerticalAlignment(VerticalAlignment.CENTER);
        stylec.setWrapText(true);//stylec.setFont(fontBold);
        stylec.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        stylec.setFont(boldFont);
        stylec.setBorderTop(BorderStyle.THIN);
        stylec.setBorderBottom(BorderStyle.DOUBLE);

        CellStyle stylec2 = workbook.createCellStyle();
        stylec2.setAlignment(HorizontalAlignment.CENTER);
        stylec2.setVerticalAlignment(VerticalAlignment.CENTER);
        stylec2.setWrapText(true);
        stylec2.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
        stylec2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        stylec2.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));

        CellStyle stylec1 = workbook.createCellStyle();
        stylec1.setAlignment(HorizontalAlignment.CENTER);
        stylec1.setVerticalAlignment(VerticalAlignment.CENTER);
        stylec1.setWrapText(true);
        stylec1.setFont(fontBold);

        CellStyle borderedStyle = workbook.createCellStyle();
        borderedStyle.setBorderTop(BorderStyle.THIN);
        borderedStyle.setBorderBottom(BorderStyle.THIN);
        borderedStyle.setBorderLeft(BorderStyle.THIN);
        borderedStyle.setBorderRight(BorderStyle.THIN);
        borderedStyle.setAlignment(HorizontalAlignment.CENTER);
        borderedStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle borderedStyleWithColor = workbook.createCellStyle();
        borderedStyleWithColor.cloneStyleFrom(borderedStyle); // Copy styles from the borderedStyle
        borderedStyleWithColor.setFillForegroundColor(IndexedColors.RED.index);
        borderedStyleWithColor.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle style11 = workbook.createCellStyle();
        style11.setAlignment(HorizontalAlignment.LEFT);
        style11.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style11.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));
        List<Integer> rowBoldcount = new ArrayList();
        Row headerRow = sheet.createRow(tr);

        try {

            headerRow.setHeight(rowHeight);

            addImageToHeader(sheet, "/META-INF/resources/images/urclogo.png");

        } catch (IOException ex) {
            Logger.getLogger(BudgetReportsView.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Create a cell for the header
        // Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(1);
        headerCell.setCellValue("UGANDA RAILWAYS CORPORATION");
        headerCell.setCellStyle(styleq31);
        CellRangeAddress cellRange3 = new CellRangeAddress(tr, tr, 1, 5);
        sheet.addMergedRegion(cellRange3);
        setBottomBorderForRegion(sheet, cellRange3);
        tr++;
        Row row0 = sheet.createRow(tr);
        Cell cellq = row0.createCell((short) 0);
        row0.getCell(0).setCellStyle(styleq31);
        cellq.setCellValue("BUDGET DETAILS " + budget.getValue().getFinancialYear() + " " + coa.getCode().trim() + " " + coa.getName());
        CellRangeAddress cellRange4 = new CellRangeAddress(tr, tr, 0, 5);
        sheet.addMergedRegion(cellRange4);
        rowBoldcount.add((int) 0);
        tr++;

        Row row = sheet.createRow(tr);
        Cell cell = row.createCell((short) 0);
        row.getCell(0).setCellStyle(styleq31);
        cell.setCellValue("Code");
        Cell cell2 = row.createCell((short) 1);
        row.getCell(1).setCellStyle(styleq31);
        cell2.setCellValue("Description");
        Cell cell3 = row.createCell((short) 2);
        row.getCell(2).setCellStyle(styleq31);
        cell3.setCellValue("Jul");
        Cell cell4 = row.createCell((short) 3);
        row.getCell(3).setCellStyle(styleq31);
        cell4.setCellValue("Aug");
        Cell cell5 = row.createCell((short) 4);
        row.getCell(4).setCellStyle(styleq31);
        cell5.setCellValue("Sep");

        Cell cell6 = row.createCell((short) 5);
        row.getCell(5).setCellStyle(styleq31);
        cell6.setCellValue("Oct");

        Cell cell7 = row.createCell((short) 6);
        row.getCell(6).setCellStyle(styleq31);
        cell7.setCellValue("Nov");

        Cell cell8 = row.createCell((short) 7);
        row.getCell(7).setCellStyle(styleq31);
        cell8.setCellValue("Dec");

        Cell cell9 = row.createCell((short) 8);
        row.getCell(8).setCellStyle(styleq31);
        cell9.setCellValue("Jan");

        Cell cell10 = row.createCell((short) 9);
        row.getCell(9).setCellStyle(styleq31);
        cell10.setCellValue("Feb");

        Cell cell11 = row.createCell((short) 10);
        row.getCell(10).setCellStyle(styleq31);
        cell11.setCellValue("Mar");

        Cell cell12 = row.createCell((short) 11);
        row.getCell(11).setCellStyle(styleq31);
        cell12.setCellValue("Apr");

        Cell cell13 = row.createCell((short) 12);
        row.getCell(12).setCellStyle(styleq31);
        cell13.setCellValue("May");

        Cell cell14 = row.createCell((short) 13);
        row.getCell(13).setCellStyle(styleq31);
        cell14.setCellValue("Jun");

        Cell cell15 = row.createCell((short) 14);
        row.getCell(14).setCellStyle(styleq31);
        cell15.setCellValue("Section");

        rowBoldcount.add((int) 1);
        CreationHelper createHelper = workbook.getCreationHelper();
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));

// Format for dates, e.g., "yyyy-MM-dd" or any other format you need
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        BigDecimal sumD = BigDecimal.ZERO;
        for (BudgetItems h : list) {
            tr++;
            short tc = 0;
            Row rowx1 = sheet.createRow(tr);
            rowx1.createCell((short) tc).setCellValue(h.getCoacode().getCode());
            rowx1.getCell(tc).setCellStyle(styleq);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getItem());

            rowx1.getCell(tc).setCellStyle(styleq);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getJul().doubleValue());
            rowx1.getCell(tc).setCellStyle(styleq);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getAug().doubleValue());
            rowx1.getCell(tc).setCellStyle(styleq);
            tc++;

            rowx1.createCell(tc).setCellValue(h.getSep().doubleValue());
            rowx1.getCell(tc).setCellStyle(styleq);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getOct().doubleValue());
            rowx1.getCell(tc).setCellStyle(styleq);
            tc++;

            rowx1.createCell((short) tc).setCellValue(h.getNov().doubleValue());
            rowx1.getCell(tc).setCellStyle(styleq);
            tc++;

            rowx1.createCell((short) tc).setCellValue(h.getDec().doubleValue());
            rowx1.getCell(tc).setCellStyle(styleq);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getJan().doubleValue());
            rowx1.getCell(tc).setCellStyle(styleq);
            tc++;

            rowx1.createCell((short) tc).setCellValue(h.getFeb().doubleValue());
            rowx1.getCell(tc).setCellStyle(styleq);
            tc++;

            rowx1.createCell((short) tc).setCellValue(h.getMar().doubleValue());
            rowx1.getCell(tc).setCellStyle(styleq);
            tc++;

            rowx1.createCell((short) tc).setCellValue(h.getApr().doubleValue());
            rowx1.getCell(tc).setCellStyle(styleq);
            tc++;

            rowx1.createCell((short) tc).setCellValue(h.getMay().doubleValue());
            rowx1.getCell(tc).setCellStyle(styleq);
            tc++;

            rowx1.createCell((short) tc).setCellValue(h.getJun().doubleValue());
            rowx1.getCell(tc).setCellStyle(styleq);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getDeptUnit().getNAME() + " ");
            rowx1.getCell(tc).setCellStyle(styleq);
            sumD = sumD.add(Optional.ofNullable(h.getJul()).orElse(BigDecimal.ZERO))
                    .add(Optional.ofNullable(h.getAug()).orElse(BigDecimal.ZERO))
                    .add(Optional.ofNullable(h.getSep()).orElse(BigDecimal.ZERO))
                    .add(Optional.ofNullable(h.getOct()).orElse(BigDecimal.ZERO))
                    .add(Optional.ofNullable(h.getNov()).orElse(BigDecimal.ZERO))
                    .add(Optional.ofNullable(h.getDec()).orElse(BigDecimal.ZERO))
                    .add(Optional.ofNullable(h.getJan()).orElse(BigDecimal.ZERO))
                    .add(Optional.ofNullable(h.getFeb()).orElse(BigDecimal.ZERO))
                    .add(Optional.ofNullable(h.getMar()).orElse(BigDecimal.ZERO))
                    .add(Optional.ofNullable(h.getApr()).orElse(BigDecimal.ZERO))
                    .add(Optional.ofNullable(h.getMay()).orElse(BigDecimal.ZERO))
                    .add(Optional.ofNullable(h.getJun()).orElse(BigDecimal.ZERO));

        }
        // Add a row for the total amount using a formula
        Row totalRow = sheet.createRow(++tr);
        short tc = 0;
        totalRow.createCell(tc).setCellValue("Total");
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        totalRow.createCell(tc).setCellValue(formatAmount(sumD));
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        String amountColumnRange = "";
        amountColumnRange = String.format("C2:C%d", tr);
        totalRow.createCell(tc).setCellFormula(String.format("SUM(%s)", amountColumnRange));
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;

        // Set the formula for summing up the amount column
        amountColumnRange = String.format("D2:D%d", tr); // Assuming the Amount column is the 4th column (index 3)
        totalRow.createCell(tc).setCellFormula(String.format("SUM(%s)", amountColumnRange));
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        amountColumnRange = String.format("E2:E%d", tr);
        totalRow.createCell(tc).setCellFormula(String.format("SUM(%s)", amountColumnRange));
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        amountColumnRange = String.format("F2:F%d", tr);
        totalRow.createCell(tc).setCellFormula(String.format("SUM(%s)", amountColumnRange));
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        amountColumnRange = String.format("G2:G%d", tr);
        totalRow.createCell(tc).setCellFormula(String.format("SUM(%s)", amountColumnRange));
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        amountColumnRange = String.format("H2:H%d", tr);
        totalRow.createCell(tc).setCellFormula(String.format("SUM(%s)", amountColumnRange));
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        amountColumnRange = String.format("I2:I%d", tr);
        totalRow.createCell(tc).setCellFormula(String.format("SUM(%s)", amountColumnRange));
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        amountColumnRange = String.format("J2:J%d", tr);
        totalRow.createCell(tc).setCellFormula(String.format("SUM(%s)", amountColumnRange));
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        amountColumnRange = String.format("K2:K%d", tr);
        totalRow.createCell(tc).setCellFormula(String.format("SUM(%s)", amountColumnRange));
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        amountColumnRange = String.format("L2:L%d", tr);
        totalRow.createCell(tc).setCellFormula(String.format("SUM(%s)", amountColumnRange));
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        amountColumnRange = String.format("M2:M%d", tr);
        totalRow.createCell(tc).setCellFormula(String.format("SUM(%s)", amountColumnRange));
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        amountColumnRange = String.format("N2:N%d", tr);
        totalRow.createCell(tc).setCellFormula(String.format("SUM(%s)", amountColumnRange));
        totalRow.getCell(tc).setCellStyle(stylec);

        for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }
        int y = 0;
        for (Row currentRow : sheet) {
            y++;
            if (currentRow == null) {
                continue;
            }

            for (Cell currentCell : currentRow) {
                if (currentCell == null) {
                    continue;
                }
                if (y > 1) {
// Get the existing cell style
                    CellStyle existingStyle = currentCell.getCellStyle();

// Create a new style that combines the existing style with the border style
                    CellStyle newStyle = sheet.getWorkbook().createCellStyle();
                    newStyle.cloneStyleFrom(existingStyle);
                    newStyle.setBorderTop(BorderStyle.THIN);
                    newStyle.setBorderBottom(BorderStyle.THIN);
                    newStyle.setBorderLeft(BorderStyle.THIN);
                    newStyle.setBorderRight(BorderStyle.THIN);
                    newStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setWrapText(true);

                    currentCell.setCellStyle(newStyle);
                }

            }
        }
    }

    public Dialog createBudgetDialog(BudgetItemsActuals target, String month, String month2) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(target.getCoacode().getCode() + " " + month + " Budget " + extra.getCurrentFy(target.getBudget().getFinancialYear()));

        Grid<BudgetItems> gridTransactions = new Grid<>(BudgetItems.class, false);
        gridTransactions.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridTransactions.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);

        // Columns
        gridTransactions.addColumn(budgetItem -> {
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
                }).setFrozen(true).setTooltipGenerator(trans -> {
            return trans.getCoacode().getName();
        });

        gridTransactions.addColumn(BudgetItems::getItem).setHeader("Name").setFrozenToEnd(true);

        List<BudgetItems> list = budgetItemsService.findBudgetItemsByBudgetAndCoaAndSectios(target.getBudget(), target.getCoacode(), target.getDeptUnit(), month2);

        footerTotal.setText("Total: " + sumBudgetAmountsByMonth(list, month2));
        footerTotal.getStyle().set("text-align", "right").set("font-weight", "bold");

        gridTransactions.addColumn(salfldg -> {

            return sumBudgetAmountsByMonth(salfldg, month2);
            //return salfldg.getJul()+"";
        }).setHeader("Amount").setAutoWidth(true).setFooter(footerTotal);

        gridTransactions.addColumn(budgetItem -> {
            UrcDeptSectionAnlDimbgt coacode = budgetItem.getDeptUnit();
            Text label = new Text(coacode != null ? coacode.getNAME() : "");
            return label.getText(); // Get the text content
        })
                .setHeader("Code").setWidth("150px").setFlexGrow(0)
                .setSortable(true) // Make the column sortable
                .setComparator((budgetItem1, budgetItem2) -> {
                    // Implement your custom comparator logic here
                    String name1 = budgetItem1.getCoacode() != null ? budgetItem1.getCoacode().getName() : "";
                    String name2 = budgetItem2.getCoacode() != null ? budgetItem2.getCoacode().getName() : "";
                    return name1.compareTo(name2);
                }).setFrozen(true).setTooltipGenerator(trans -> {
            return trans.getCoacode().getCode();
        });

        gridTransactions.getStyle().set("width", "100%").set("max-width", "100%");
        gridTransactions.setSelectionMode(Grid.SelectionMode.SINGLE);
        gridTransactions.setItems(list);

        // Buttons
        Button filterButton = new Button("Close", new Icon(VaadinIcon.CLOSE), ef -> dialog.close());
        filterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button downloadButton = new Button("Download", new Icon(VaadinIcon.ENVELOPE_O), ef
                -> {
            List<BudgetItems> list2 = budgetItemsService.findBudgetItemsByBudgetAndCoaAndSectios(target.getBudget(), target.getCoacode(), target.getDeptUnit(), month2);

            exportAndDownloadExcelBudgetDetails(budget.getValue(), target.getCoacode(), list2, month2);
            dialog.close();
        });
        dialog.getFooter().add(filterButton, downloadButton);

        dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        dialog.add(gridTransactions);
        dialog.setWidth("80%");
        dialog.open();

        return dialog;
    }

    public String sumBudgetAmountsByMonth(List<BudgetItems> projections, String month) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BudgetItems projection : projections) {
            if (month.equals("Jul")) {
                sum = sum.add(projection.getJul());
            } else if (month.equals("Aug")) {
                sum = sum.add(projection.getAug());
            } else if (month.equals("Sep")) {
                sum = sum.add(projection.getSep());
            } else if (month.equals("Oct")) {
                sum = sum.add(projection.getOct());
            } else if (month.equals("Nov")) {
                sum = sum.add(projection.getNov());
            } else if (month.equals("Dec")) {
                sum = sum.add(projection.getDec());
            } else if (month.equals("Jan")) {
                sum = sum.add(projection.getJan());
            } else if (month.equals("Feb")) {
                sum = sum.add(projection.getFeb());
            } else if (month.equals("Mar")) {
                sum = sum.add(projection.getMar());
            } else if (month.equals("Apr")) {
                sum = sum.add(projection.getApr());
            } else if (month.equals("May")) {
                sum = sum.add(projection.getMay());
            } else if (month.equals("Jun")) {
                sum = sum.add(projection.getJun());
            } else if (month.equals("Total")) {
                sum = BigDecimal.ZERO;
                sum = sum.add(Optional.ofNullable(projection.getJul()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getAug()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getSep()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getOct()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getNov()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getDec()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getJan()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getFeb()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getMar()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getApr()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getMay()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getJun()).orElse(BigDecimal.ZERO));
            }

        }
        String sums = decimalFormat.format(sum);
        if (sum == null) {
            return "";
        }
        if (sum.compareTo(BigDecimal.ZERO) < 0) {
            sums = decimalFormat.format(sum.abs());
            return "(" + sums + ")";
        } else {
            sums = decimalFormat.format(sum);
            return "(" + sums + ")";
        }

    }

    public String sumBudgetAmountsByMonth2(List<BudgetItems> projections, String month) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BudgetItems projection : projections) {
            if (month.equals("Jul")) {
                sum = sum.add(projection.getJul());
            } else if (month.equals("Aug")) {
                sum = sum.add(projection.getAug());
            } else if (month.equals("Sep")) {
                sum = sum.add(projection.getSep());
            } else if (month.equals("Oct")) {
                sum = sum.add(projection.getOct());
            } else if (month.equals("Nov")) {
                sum = sum.add(projection.getNov());
            } else if (month.equals("Dec")) {
                sum = sum.add(projection.getDec());
            } else if (month.equals("Jan")) {
                sum = sum.add(projection.getJan());
            } else if (month.equals("Feb")) {
                sum = sum.add(projection.getFeb());
            } else if (month.equals("Mar")) {
                sum = sum.add(projection.getMar());
            } else if (month.equals("Apr")) {
                sum = sum.add(projection.getApr());
            } else if (month.equals("May")) {
                sum = sum.add(projection.getMay());
            } else if (month.equals("Jun")) {
                sum = sum.add(projection.getJun());
            } else if (month.equals("Total")) {
                sum = BigDecimal.ZERO;
                sum = sum.add(Optional.ofNullable(projection.getJul()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getAug()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getSep()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getOct()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getNov()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getDec()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getJan()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getFeb()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getMar()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getApr()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getMay()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getJun()).orElse(BigDecimal.ZERO));
            }

        }
        String sums = decimalFormat.format(sum);
        if (sum == null) {
            return "";
        }
        if (sum.compareTo(BigDecimal.ZERO) < 0) {
            sums = decimalFormat.format(sum.abs());
            return sums;
        } else {
            sums = decimalFormat.format(sum);
            return sums;
        }

    }

    public String sumBudgetAmountsByMonth(BudgetItems projection, String month) {
        BigDecimal sum = BigDecimal.ZERO;

        switch (month) {
            case "Jul":
                sum = projection.getJul();
                break;
            case "Aug":
                sum = projection.getAug();
                break;
            case "Sep":
                sum = projection.getSep();
                break;
            case "Oct":
                sum = projection.getOct();
                break;
            case "Nov":
                sum = projection.getNov();
                break;
            case "Dec":
                sum = projection.getDec();
                break;
            case "Jan":
                sum = projection.getJan();
                break;
            case "Feb":
                sum = projection.getFeb();
                break;
            case "Mar":
                sum = projection.getMar();
                break;
            case "Apr":
                sum = projection.getApr();
                break;
            case "May":
                sum = projection.getMay();
                break;
            case "Jun":
                sum = projection.getJun();
                break;
            case "Total":
                /*                sum = projection.getJul().add(projection.getAug()).add(projection.getSep()).add(projection.getOct())
                .add(projection.getNov()).add(projection.getDec()).add(projection.getJan()).add(projection.getFeb()).add(projection.getMar())
                .add(projection.getApr()).add(projection.getMay()).add(projection.getJun());*/

                sum = Optional.ofNullable(projection.getJul()).orElse(BigDecimal.ZERO)
                        .add(Optional.ofNullable(projection.getAug()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getSep()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getOct()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getNov()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getDec()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getJan()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getFeb()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getMar()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getApr()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getMay()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getJun()).orElse(BigDecimal.ZERO));
                break;
            default:
                break;
        }

        String sums = decimalFormat.format(sum);
        if (sum == null) {
            return "";
        }
        if (sum.compareTo(BigDecimal.ZERO) < 0) {
            sums = decimalFormat.format(sum.abs());
            return "(" + sums + ")";
        } else {
            sums = decimalFormat.format(sum);
            return "(" + sums + ")";
        }

    }

    public BigDecimal sumBudgetAmountsByMonth2(BudgetItems projection, String month) {
        BigDecimal sum = BigDecimal.ZERO;

        switch (month) {
            case "Jul":
                sum = projection.getJul();
                break;
            case "Aug":
                sum = projection.getAug();
                break;
            case "Sep":
                sum = projection.getSep();
                break;
            case "Oct":
                sum = projection.getOct();
                break;
            case "Nov":
                sum = projection.getNov();
                break;
            case "Dec":
                sum = projection.getDec();
                break;
            case "Jan":
                sum = projection.getJan();
                break;
            case "Feb":
                sum = projection.getFeb();
                break;
            case "Mar":
                sum = projection.getMar();
                break;
            case "Apr":
                sum = projection.getApr();
                break;
            case "May":
                sum = projection.getMay();
                break;
            case "Jun":
                sum = projection.getJun();
                break;
            case "Total":
                /*                sum = projection.getJul().add(projection.getAug()).add(projection.getSep()).add(projection.getOct())
                .add(projection.getNov()).add(projection.getDec()).add(projection.getJan()).add(projection.getFeb()).add(projection.getMar())
                .add(projection.getApr()).add(projection.getMay()).add(projection.getJun());*/

                sum = Optional.ofNullable(projection.getJul()).orElse(BigDecimal.ZERO)
                        .add(Optional.ofNullable(projection.getAug()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getSep()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getOct()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getNov()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getDec()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getJan()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getFeb()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getMar()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getApr()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getMay()).orElse(BigDecimal.ZERO))
                        .add(Optional.ofNullable(projection.getJun()).orElse(BigDecimal.ZERO));
                break;
            default:
                break;
        }

        String sums = decimalFormat.format(sum);
        if (sum == null) {
            return BigDecimal.ZERO;
        }
        if (sum.compareTo(BigDecimal.ZERO) < 0) {
            sums = decimalFormat.format(sum.abs());
            return sum;
        } else {
            sums = decimalFormat.format(sum);
            return sum;
        }

    }

    private void exportAndDownloadExcelBudgetDetails(Budget budget, COA coa, List<BudgetItems> list, String month) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(coa.getCode() + " " + month + " Budget " + budget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);
            createBudgetDetails(workbook, sheet, coa, list, month);
            //createDataRows(sheet, people);

            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource(coa.getCode() + " " + month + " Budget " + budget.getFinancialYear() + ".xlsx", ()
                    -> new ByteArrayInputStream(outputStream.toByteArray()));

            // Create an Anchor component with the StreamResource
            Anchor downloadLink = new Anchor(resource, "");
            downloadLink.getElement().setAttribute("download", true);
            add(downloadLink);
            // Programmatically click the download link to initiate the download
            downloadLink.getElement().callJsFunction("click");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String[] MONTHS = {"Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan", "Feb", "Mar", "Apr", "May", "Jun"};

    private enum SKey {
        TITLE, SUBTITLE, HEADER,
        CODE, TEXT, TEXT_ZEBRA,
        MONEY, MONEY_ZEBRA,
        TOTAL_LABEL, TOTAL_MONEY,
        VAR_POS, VAR_NEG
    }

    private void createBudgetActualsSheet(Workbook workbook, Sheet sheet) {

        Map<SKey, CellStyle> st = buildStyles(workbook);

        final int FIRST_NUMERIC_COL = 2;
        final int LAST_MONTH_COL = 25;
        final int TOTAL_BUDGET_COL = 26;
        final int TOTAL_ACTUAL_COL = 27;
        final int VARIANCE_COL = 28;
        final int LAST_COL = 28;

        int r = 0;

        // ================= HEADER =================
        Row titleRow = sheet.createRow(r++);
        titleRow.setHeightInPoints(26);

        try {
            addImageToHeader(sheet, "/META-INF/resources/images/urclogo.png");
        } catch (IOException ex) {
            Logger.getLogger(BudgetReportsView.class.getName()).log(Level.SEVERE, null, ex);
        }

        Cell titleCell = titleRow.createCell(1);
        titleCell.setCellValue("UGANDA RAILWAYS CORPORATION");
        titleCell.setCellStyle(st.get(SKey.TITLE));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, LAST_COL));

        Row subRow = sheet.createRow(r++);
        subRow.setHeightInPoints(18);
        Cell subCell = subRow.createCell(0);

        String selectedNames = comboBoxD_Section.getSelectedItems().stream().map(UrcDeptSectionAnlDimbgt::getNAME)
                .filter(Objects::nonNull)
                .sorted() // optional: alphabetical
                .collect(Collectors.joining(", "));
        subCell.setCellValue(selectedNames.toUpperCase() + " SECTIONS FINANCIAL PERFORMANCE REPORT");
        subCell.setCellStyle(st.get(SKey.SUBTITLE));
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, LAST_COL));

        Row header = sheet.createRow(r++);
        int c = 0;
        createCell(header, c++, "Code", st.get(SKey.HEADER));
        createCell(header, c++, "Description", st.get(SKey.HEADER));

        for (String m : MONTHS) {
            createCell(header, c++, m, st.get(SKey.HEADER));
            createCell(header, c++, m + " Actual", st.get(SKey.HEADER));
        }
        createCell(header, c++, "Total Budget", st.get(SKey.HEADER));
        createCell(header, c++, "Total Actual", st.get(SKey.HEADER));
        createCell(header, c++, "Variance", st.get(SKey.HEADER));

        sheet.createFreezePane(2, 3);

        // ================= DATA =================
        List<BudgetItemsActuals> items
                = budgetItemsService.findDistinctBudgetItemses(
                        budget.getValue(),
                        comboBoxD_Section.getSelectedItems());

        // IMPORTANT: Ensure sorted by COA code
        items.sort((a, b) -> {
            String ca = a.getCoacode() != null ? a.getCoacode().getCode() : "";
            String cb = b.getCoacode() != null ? b.getCoacode().getCode() : "";
            return ca.compareTo(cb);
        });

        double[] runningTotals1 = new double[LAST_COL + 1];
        double[] runningTotals23 = new double[LAST_COL + 1];

        String currentGroup = null;

        for (BudgetItemsActuals h : items) {

            String code = h.getCoacode() != null ? h.getCoacode().getCode() : "";
            String group = code.startsWith("1") ? "1"
                    : (code.startsWith("2") || code.startsWith("3")) ? "23"
                    : null;

            // If group changes, insert subtotal before writing next group
            if (currentGroup != null && !currentGroup.equals(group)) {
                if (currentGroup.equals("1")) {
                    r = insertSubtotalRow(sheet, r, "TOTAL INCOME", runningTotals1, st, LAST_COL);
                } else if (currentGroup.equals("23")) {
                    r = insertSubtotalRow(sheet, r, "TOTAL EXPENDITURE", runningTotals23, st, LAST_COL);
                }
            }

            currentGroup = group;

            Row row = sheet.createRow(r++);
            int col = 0;

            createCell(row, col++, code, st.get(SKey.CODE));
            createCell(row, col++, h.getItem(), st.get(SKey.TEXT));

            double totalBudget = 0;
            double totalActual = 0;

            for (BigDecimal bd : new BigDecimal[]{
                h.getJul(), h.getJulA().abs(),
                h.getAug(), h.getAugA().abs(),
                h.getSep(), h.getSepA().abs(),
                h.getOct(), h.getOctA().abs(),
                h.getNov(), h.getNovA().abs(),
                h.getDec(), h.getDecA().abs(),
                h.getJan(), h.getJanA().abs(),
                h.getFeb(), h.getFebA().abs(),
                h.getMar(), h.getMarA().abs(),
                h.getApr(), h.getAprA().abs(),
                h.getMay(), h.getMayA().abs(),
                h.getJun(), h.getJunA().abs()
            }) {
                double val = bd != null ? bd.doubleValue() : 0;
                createMoneyCell(row, col++, val, st.get(SKey.MONEY));
                if ((col - FIRST_NUMERIC_COL) % 2 == 1) {
                    totalBudget += val;
                } else {
                    totalActual += val;
                }
            }

            double variance = totalActual - totalBudget;

            createMoneyCell(row, TOTAL_BUDGET_COL, totalBudget, st.get(SKey.MONEY));
            createMoneyCell(row, TOTAL_ACTUAL_COL, totalActual, st.get(SKey.MONEY));
            createMoneyCell(row, VARIANCE_COL, variance,
                    variance > 0 ? st.get(SKey.VAR_NEG) : st.get(SKey.VAR_POS));

            // accumulate running totals
            if ("1".equals(group)) {
                accumulate(runningTotals1, row, FIRST_NUMERIC_COL, LAST_COL);
            } else if ("23".equals(group)) {
                accumulate(runningTotals23, row, FIRST_NUMERIC_COL, LAST_COL);
            }
        }

        // insert last group subtotal
        if ("1".equals(currentGroup)) {
            r = insertSubtotalRow(sheet, r, "TOTAL INCOME", runningTotals1, st, LAST_COL);
        } else if ("23".equals(currentGroup)) {
            r = insertSubtotalRow(sheet, r, "TOTAL EXPENDITURE", runningTotals23, st, LAST_COL);
        }

        sheet.setDisplayGridlines(false);
        applyColumnSizing(sheet, LAST_COL);
    }

    private void accumulate(double[] totals, Row row, int startCol, int endCol) {
        for (int i = startCol; i <= endCol; i++) {
            totals[i] += getNumericCellValueSafe(row.getCell(i));
        }
    }

    private int insertSubtotalRow(Sheet sheet,
            int rowIndex,
            String label,
            double[] totals,
            Map<SKey, CellStyle> st,
            int lastCol) {

        Row row = sheet.createRow(rowIndex++);
        Cell c0 = row.createCell(0);
        c0.setCellStyle(st.get(SKey.TOTAL_LABEL));

        Cell c1 = row.createCell(1);
        c1.setCellValue(label);
        c1.setCellStyle(st.get(SKey.TOTAL_LABEL));

        for (int i = 2; i <= lastCol; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(totals[i]);
            cell.setCellStyle(i == 28
                    ? (totals[i] > 0 ? st.get(SKey.VAR_NEG) : st.get(SKey.VAR_POS))
                    : st.get(SKey.TOTAL_MONEY));
            totals[i] = 0; // reset for next group
        }

        return rowIndex;
    }

// ---------------- helpers ----------------
    private Map<SKey, CellStyle> buildStyles(Workbook wb) {
        Map<SKey, CellStyle> s = new EnumMap<>(SKey.class);
        DataFormat df = wb.createDataFormat();

        Font title = wb.createFont();
        title.setFontName("Calibri");
        title.setFontHeightInPoints((short) 16);
        title.setBold(true);

        Font hdr = wb.createFont();
        hdr.setFontName("Calibri");
        hdr.setFontHeightInPoints((short) 10);
        hdr.setBold(true);
        hdr.setColor(IndexedColors.WHITE.getIndex());

        Font normal = wb.createFont();
        normal.setFontName("Calibri");
        normal.setFontHeightInPoints((short) 10);

        Font bold = wb.createFont();
        bold.setFontName("Calibri");
        bold.setFontHeightInPoints((short) 10);
        bold.setBold(true);

        java.util.function.Consumer<CellStyle> borders = st -> {
            st.setBorderTop(BorderStyle.THIN);
            st.setBorderBottom(BorderStyle.THIN);
            st.setBorderLeft(BorderStyle.THIN);
            st.setBorderRight(BorderStyle.THIN);
            st.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            st.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            st.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            st.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        };

        CellStyle titleSt = wb.createCellStyle();
        titleSt.setFont(title);
        titleSt.setAlignment(HorizontalAlignment.CENTER);
        titleSt.setVerticalAlignment(VerticalAlignment.CENTER);
        s.put(SKey.TITLE, titleSt);

        CellStyle sub = wb.createCellStyle();
        sub.setFont(bold);
        sub.setAlignment(HorizontalAlignment.CENTER);
        sub.setVerticalAlignment(VerticalAlignment.CENTER);
        sub.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        sub.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        borders.accept(sub);
        s.put(SKey.SUBTITLE, sub);

        CellStyle header = wb.createCellStyle();
        header.setFont(hdr);
        header.setAlignment(HorizontalAlignment.CENTER);
        header.setVerticalAlignment(VerticalAlignment.CENTER);
        header.setWrapText(true);
        header.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        borders.accept(header);
        s.put(SKey.HEADER, header);

        CellStyle code = wb.createCellStyle();
        code.setFont(normal);
        code.setAlignment(HorizontalAlignment.CENTER);
        code.setVerticalAlignment(VerticalAlignment.CENTER);
        borders.accept(code);
        s.put(SKey.CODE, code);

        CellStyle text = wb.createCellStyle();
        text.setFont(normal);
        text.setAlignment(HorizontalAlignment.LEFT);
        text.setVerticalAlignment(VerticalAlignment.CENTER);
        text.setWrapText(true);
        borders.accept(text);
        s.put(SKey.TEXT, text);

        CellStyle textZ = wb.createCellStyle();
        textZ.cloneStyleFrom(text);
        textZ.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        textZ.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.put(SKey.TEXT_ZEBRA, textZ);

        CellStyle money = wb.createCellStyle();
        money.cloneStyleFrom(text);
        money.setAlignment(HorizontalAlignment.RIGHT);
        money.setDataFormat(df.getFormat("#,##0.00"));
        s.put(SKey.MONEY, money);

        CellStyle moneyZ = wb.createCellStyle();
        moneyZ.cloneStyleFrom(money);
        moneyZ.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        moneyZ.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.put(SKey.MONEY_ZEBRA, moneyZ);

        CellStyle totalLabel = wb.createCellStyle();
        totalLabel.cloneStyleFrom(sub);
        totalLabel.setAlignment(HorizontalAlignment.LEFT);
        s.put(SKey.TOTAL_LABEL, totalLabel);

        CellStyle totalMoney = wb.createCellStyle();
        totalMoney.cloneStyleFrom(sub);
        totalMoney.setAlignment(HorizontalAlignment.RIGHT);
        totalMoney.setDataFormat(df.getFormat("#,##0.00"));
        s.put(SKey.TOTAL_MONEY, totalMoney);

        // Variance styles (no custom colors; uses indexed greens/reds)
        CellStyle varPos = wb.createCellStyle();
        varPos.cloneStyleFrom(money);
        varPos.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        varPos.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.put(SKey.VAR_POS, varPos);

        CellStyle varNeg = wb.createCellStyle();
        varNeg.cloneStyleFrom(money);
        varNeg.setFillForegroundColor(IndexedColors.ROSE.getIndex());
        varNeg.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.put(SKey.VAR_NEG, varNeg);

        return s;
    }

    private void createCell(Row row, int col, String val, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(val == null ? "" : val);
        cell.setCellStyle(style);
    }

    private void writeMoney(Row row, int col, double val, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(val);
        cell.setCellStyle(style);
    }

    private double sumNullable(BigDecimal... values) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal v : values) {
            if (v != null) {
                sum = sum.add(v.abs());
            }
        }
        return sum.doubleValue();
    }

    private CellStyle styleVariance(Map<SKey, CellStyle> st, double variance) {
        return variance > 0 ? st.get(SKey.VAR_NEG) : st.get(SKey.VAR_POS);
    }

    private String getStringCell(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }
        if (cell.getCellType() == CellType.FORMULA) {
            if (cell.getCachedFormulaResultType() == CellType.STRING) {
                return cell.getStringCellValue();
            }
            if (cell.getCachedFormulaResultType() == CellType.NUMERIC) {
                return String.valueOf((long) cell.getNumericCellValue());
            }
        }
        return cell.toString();
    }

    private double getNumericCellValueSafe(Cell cell) {
        if (cell == null) {
            return 0d;
        }
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            }
            if (cell.getCellType() == CellType.FORMULA) {
                return cell.getNumericCellValue();
            }
            if (cell.getCellType() == CellType.STRING) {
                String s = cell.getStringCellValue();
                if (s == null || s.trim().isEmpty()) {
                    return 0d;
                }
                return Double.parseDouble(s.replace(",", "").trim());
            }
        } catch (Exception ignored) {
        }
        return 0d;
    }

    /*private void createCell(Row row, int col, String val, CellStyle style) {
Cell cell = row.createCell(col);
cell.setCellValue(val);
cell.setCellStyle(style);
}

private void writeMoney(Row row, int col, double val, CellStyle style) {
Cell cell = row.createCell(col);
cell.setCellValue(val);
cell.setCellStyle(style);
}

private double bd(BigDecimal v) {
return v == null ? 0d : v.doubleValue();
}*/
    private String cellRef(int row1Based, int col0Based) {
        return CellReference.convertNumToColString(col0Based) + row1Based;
    }

    private String sumEveryOther(int row1Based, int startCol, int endColInclusive, int step) {
        StringBuilder sb = new StringBuilder("SUM(");
        boolean first = true;
        for (int c = startCol; c <= endColInclusive; c += step) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            sb.append(cellRef(row1Based, c));
        }
        sb.append(")");
        return sb.toString();
    }

    private void createHeaderCell(Row row, int col, String text, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(text);
        cell.setCellStyle(style);
    }

    private void writeMoney(Row row, int col, Number val, boolean zebra, Map<Styles.Key, CellStyle> st) {
        Cell cell = row.createCell(col);
        cell.setCellValue(val == null ? 0d : val.doubleValue());
        cell.setCellStyle(zebra ? st.get(Styles.Key.ZEBRA_MONEY) : st.get(Styles.Key.MONEY));
    }

    private void applyColumnSizing(Sheet sheet, int lastCol) {
        sheet.setColumnWidth(0, 14 * 256);  // Code
        sheet.setColumnWidth(1, 75 * 256);  // Description

        for (int i = 2; i <= lastCol; i++) {
            sheet.setColumnWidth(i, 15 * 256);
        }

        sheet.setDefaultRowHeightInPoints(18);
    }

    private void configurePrint(Sheet sheet) {
        PrintSetup ps = sheet.getPrintSetup();
        ps.setLandscape(true);
        ps.setFitWidth((short) 1);
        ps.setFitHeight((short) 0);

        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);
        sheet.setRepeatingRows(CellRangeAddress.valueOf("1:3")); // title/sub/header repeat
    }

    private void createBudgetDetails(Workbook workbook, Sheet sheet, COA coa, List<BudgetItems> list, String month) {

        short rowHeight = 500;
        short tr = 0;
        Font fontBold2 = workbook.createFont();
        fontBold2.setFontName("Arial");
        fontBold2.setFontHeightInPoints((short) 10);
        fontBold2.setBold(false);

        Font fontBold = workbook.createFont();
        fontBold.setFontName("Arial");
        fontBold.setFontHeightInPoints((short) 10);
        fontBold.setBold(true);

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.RED.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(true);
        style.setFont(fontBold);

        CellStyle styleq = workbook.createCellStyle();
        styleq.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
        styleq.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq.setAlignment(HorizontalAlignment.CENTER);
        styleq.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq.setWrapText(true);
        styleq.setFont(fontBold);

        CellStyle styleq2 = workbook.createCellStyle();
        styleq2.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.index);
        styleq2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq2.setAlignment(HorizontalAlignment.CENTER);
        styleq2.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq2.setWrapText(true);
        styleq2.setFont(fontBold);

        CellStyle styleq31 = workbook.createCellStyle();
        styleq31.setAlignment(HorizontalAlignment.CENTER);
        styleq31.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq31.setWrapText(true);
        styleq31.setFont(fontBold);

        CellStyle styleq3 = workbook.createCellStyle();
        styleq3.setFillForegroundColor(IndexedColors.VIOLET.index);
        styleq3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq3.setAlignment(HorizontalAlignment.CENTER);
        styleq3.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq3.setWrapText(true);
        styleq3.setFont(fontBold);

        CellStyle styleq4 = workbook.createCellStyle();
        styleq4.setFillForegroundColor(IndexedColors.TAN.index);
        styleq4.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styleq4.setAlignment(HorizontalAlignment.CENTER);
        styleq4.setVerticalAlignment(VerticalAlignment.CENTER);
        styleq4.setWrapText(true);
        styleq4.setFont(fontBold);

        CellStyle styley = workbook.createCellStyle();
        styley.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
        styley.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styley.setAlignment(HorizontalAlignment.LEFT);
        styley.setVerticalAlignment(VerticalAlignment.CENTER);
        styley.setWrapText(true);//styley.setFont(fontBold);

        CellStyle stylegreen = workbook.createCellStyle();
        stylegreen.setFillForegroundColor(IndexedColors.GREEN.index);
        stylegreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        stylegreen.setAlignment(HorizontalAlignment.CENTER);
        stylegreen.setVerticalAlignment(VerticalAlignment.CENTER);
        stylegreen.setFont(fontBold);
        stylegreen.setWrapText(true);
        stylegreen.setFont(fontBold);

        CellStyle stylec = workbook.createCellStyle();
        stylec.setAlignment(HorizontalAlignment.LEFT);
        stylec.setVerticalAlignment(VerticalAlignment.CENTER);
        stylec.setWrapText(true);//stylec.setFont(fontBold);
        stylec.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));

        CellStyle stylec2 = workbook.createCellStyle();
        stylec2.setAlignment(HorizontalAlignment.CENTER);
        stylec2.setVerticalAlignment(VerticalAlignment.CENTER);
        stylec2.setWrapText(true);
        stylec2.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
        stylec2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        stylec2.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));

        CellStyle stylec1 = workbook.createCellStyle();
        stylec1.setAlignment(HorizontalAlignment.CENTER);
        stylec1.setVerticalAlignment(VerticalAlignment.CENTER);
        stylec1.setWrapText(true);
        stylec1.setFont(fontBold);

        CellStyle borderedStyle = workbook.createCellStyle();
        borderedStyle.setBorderTop(BorderStyle.THIN);
        borderedStyle.setBorderBottom(BorderStyle.THIN);
        borderedStyle.setBorderLeft(BorderStyle.THIN);
        borderedStyle.setBorderRight(BorderStyle.THIN);
        borderedStyle.setAlignment(HorizontalAlignment.CENTER);
        borderedStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle borderedStyleWithColor = workbook.createCellStyle();
        borderedStyleWithColor.cloneStyleFrom(borderedStyle); // Copy styles from the borderedStyle
        borderedStyleWithColor.setFillForegroundColor(IndexedColors.RED.index);
        borderedStyleWithColor.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle style11 = workbook.createCellStyle();
        style11.setAlignment(HorizontalAlignment.LEFT);
        style11.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style11.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("###,###.##"));
        List<Integer> rowBoldcount = new ArrayList();
        Row headerRow = sheet.createRow(tr);

        try {

            headerRow.setHeight(rowHeight);

            addImageToHeader(sheet, "/META-INF/resources/images/urclogo.png");

        } catch (IOException ex) {
            Logger.getLogger(BudgetReportsView.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Create a cell for the header
        // Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(1);
        headerCell.setCellValue("UGANDA RAILWAYS CORPORATION");
        headerCell.setCellStyle(styleq31);
        CellRangeAddress cellRange3 = new CellRangeAddress(tr, tr, 1, 5);
        sheet.addMergedRegion(cellRange3);
        setBottomBorderForRegion(sheet, cellRange3);
        tr++;
        Row row0 = sheet.createRow(tr);
        Cell cellq = row0.createCell((short) 0);
        row0.getCell(0).setCellStyle(styleq31);
        cellq.setCellValue("BUDGET DETAILS " + budget.getValue().getFinancialYear() + " " + coa.getCode().trim() + " " + coa.getName());
        CellRangeAddress cellRange4 = new CellRangeAddress(tr, tr, 0, 5);
        sheet.addMergedRegion(cellRange4);
        rowBoldcount.add((int) 0);
        tr++;

        Row row = sheet.createRow(tr);
        Cell cell = row.createCell((short) 0);
        row.getCell(0).setCellStyle(styleq31);
        cell.setCellValue("Code");
        Cell cell2 = row.createCell((short) 1);
        row.getCell(1).setCellStyle(styleq31);
        cell2.setCellValue("Description");
        Cell cell3 = row.createCell((short) 2);
        row.getCell(2).setCellStyle(styleq31);
        cell3.setCellValue("Journal No.");
        Cell cell4 = row.createCell((short) 3);
        row.getCell(3).setCellStyle(styleq31);
        cell4.setCellValue("Amount");
        Cell cell5 = row.createCell((short) 4);
        row.getCell(4).setCellStyle(styleq31);
        cell5.setCellValue("Date");

        Cell cell6 = row.createCell((short) 5);
        row.getCell(5).setCellStyle(styleq31);
        cell6.setCellValue("Section");

        rowBoldcount.add((int) 1);
        CreationHelper createHelper = workbook.getCreationHelper();
        CellStyle dateCellStyle = workbook.createCellStyle();
        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));

// Format for dates, e.g., "yyyy-MM-dd" or any other format you need
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (BudgetItems h : list) {
            tr++;
            short tc = 0;
            Row rowx1 = sheet.createRow(tr);
            rowx1.createCell((short) tc).setCellValue(h.getCoacode().getCode());
            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getItem());

            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;
            rowx1.createCell((short) tc).setCellValue(" ");
            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;
            rowx1.createCell((short) tc).setCellValue(sumBudgetAmountsByMonth2(h, month).doubleValue());
            rowx1.getCell(tc).setCellStyle(stylec);
            tc++;

            rowx1.createCell(tc).setCellValue("");
            rowx1.getCell(tc).setCellStyle(stylec);

            /*            rowx1.createCell((short) tc).setCellValue(h.getTransDatetime());
                rowx1.getCell(tc).setCellStyle(stylec);*/
            tc++;
            rowx1.createCell((short) tc).setCellValue(h.getDeptUnit().getNAME() + " ");
            rowx1.getCell(tc).setCellStyle(stylec);

        }
        // Add a row for the total amount using a formula
        Row totalRow = sheet.createRow(++tr);
        short tc = 0;
        totalRow.createCell(tc).setCellValue("Total");
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        totalRow.createCell(tc).setCellValue("");
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        totalRow.createCell(tc).setCellValue("");
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;

        // Set the formula for summing up the amount column
        String amountColumnRange = String.format("D2:D%d", tr); // Assuming the Amount column is the 4th column (index 3)
        totalRow.createCell(tc).setCellFormula(String.format("SUM(%s)", amountColumnRange));
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        totalRow.createCell(tc).setCellValue("");
        totalRow.getCell(tc).setCellStyle(stylec);
        tc++;
        totalRow.createCell(tc).setCellValue("");
        totalRow.getCell(tc).setCellStyle(stylec);

        for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }
        int y = 0;
        for (Row currentRow : sheet) {
            y++;
            if (currentRow == null) {
                continue;
            }

            for (Cell currentCell : currentRow) {
                if (currentCell == null) {
                    continue;
                }
                if (y > 1) {
// Get the existing cell style
                    CellStyle existingStyle = currentCell.getCellStyle();

// Create a new style that combines the existing style with the border style
                    CellStyle newStyle = sheet.getWorkbook().createCellStyle();
                    newStyle.cloneStyleFrom(existingStyle);
                    newStyle.setBorderTop(BorderStyle.THIN);
                    newStyle.setBorderBottom(BorderStyle.THIN);
                    newStyle.setBorderLeft(BorderStyle.THIN);
                    newStyle.setBorderRight(BorderStyle.THIN);
                    newStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
                    newStyle.setWrapText(true);

                    currentCell.setCellStyle(newStyle);
                }

            }
        }
    }

    public Set<Integer> getFinancialYearPeriods(Budget budget) {
        Set<Integer> periods = new LinkedHashSet<>();

        if (budget.getStartDate() == null || budget.getCloseDate() == null) {
            return periods; // return empty if dates are not set
        }

        // Get the financial year end (YYYY part)
        int yearSuffix = budget.getCloseDate().getYear(); // e.g., 2025 for FY 2024/07/01 to 2025/06/30

        // Start from July of the start year
        LocalDate current = LocalDate.of(budget.getStartDate().getYear(), Month.JULY, 1);
        for (int i = 1; i <= 12; i++) {
            //String periodCode = String.format("%d%03d", yearSuffix, i); // e.g., 2025001
            int periodCode = yearSuffix * 1000 + i;
            periods.add(periodCode);
            current = current.plusMonths(1);
        }

        return periods;
    }

    public Set<Integer> getFinancialYearPeriodByMonth(Budget budget, Month month) {
        Set<Integer> periods = new LinkedHashSet<>();

        if (budget == null || month == null
                || budget.getStartDate() == null || budget.getCloseDate() == null) {
            return periods;
        }

        // Normalize to month boundaries
        LocalDate start = budget.getStartDate().withDayOfMonth(1);
        LocalDate end = budget.getCloseDate().withDayOfMonth(1);

        // Find the financial-year "period index" for the given month: July=1 ... June=12
        int periodIndex = ((month.getValue() - Month.JULY.getValue() + 12) % 12) + 1;

        // Financial year suffix = close year (e.g., FY 2025/07 -> 2026/06 => 2026)
        int fyEndYear = budget.getCloseDate().getYear();
        int periodCode = fyEndYear * 1000 + periodIndex;

        // Validate that the requested month actually lies within the budget range
        // We need to determine which calendar year that month falls in within this FY window.
        // Months Jul-Dec belong to start year, Jan-Jun belong to close year.
        int monthYear = (month.getValue() >= Month.JULY.getValue())
                ? budget.getStartDate().getYear()
                : budget.getCloseDate().getYear();

        LocalDate requestedMonth = LocalDate.of(monthYear, month, 1);

        if (!requestedMonth.isBefore(start) && !requestedMonth.isAfter(end)) {
            periods.add(periodCode);
        }

        return periods;
    }

    public Set<Integer> getFinancialYearPeriodsByQuarter(Budget budget, int quarter) {
        Set<Integer> periods = new LinkedHashSet<>();
        if (budget == null || budget.getStartDate() == null || budget.getCloseDate() == null) {
            return periods;
        }
        if (quarter < 1 || quarter > 4) {
            throw new IllegalArgumentException("Quarter must be 1..4");
        }

        LocalDate start = budget.getStartDate().withDayOfMonth(1);
        LocalDate end = budget.getCloseDate().withDayOfMonth(1);

        int fyEndYear = budget.getCloseDate().getYear();

        Month[] quarterMonths = switch (quarter) {
            case 1 ->
                new Month[]{Month.JULY, Month.AUGUST, Month.SEPTEMBER};
            case 2 ->
                new Month[]{Month.OCTOBER, Month.NOVEMBER, Month.DECEMBER};
            case 3 ->
                new Month[]{Month.JANUARY, Month.FEBRUARY, Month.MARCH};
            case 4 ->
                new Month[]{Month.APRIL, Month.MAY, Month.JUNE};
            default ->
                throw new IllegalArgumentException("Quarter must be 1..4");
        };

        for (Month m : quarterMonths) {
            // July=1 ... June=12
            int periodIndex = ((m.getValue() - Month.JULY.getValue() + 12) % 12) + 1;
            int periodCode = fyEndYear * 1000 + periodIndex;

            int monthYear = (m.getValue() >= Month.JULY.getValue())
                    ? budget.getStartDate().getYear()
                    : budget.getCloseDate().getYear();

            LocalDate requestedMonth = LocalDate.of(monthYear, m, 1);

            if (!requestedMonth.isBefore(start) && !requestedMonth.isAfter(end)) {
                periods.add(periodCode);
            }
        }

        return periods;
    }

}
