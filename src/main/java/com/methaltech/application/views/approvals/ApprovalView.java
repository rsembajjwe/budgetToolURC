package com.methaltech.application.views.approvals;

import com.methaltech.application.data.Role;
import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.Coalevel1Service;
import com.methaltech.application.data.bgtool.service.DeptSectionMergerService;
import com.methaltech.application.data.bgtool.service.OrganisationService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetApproval;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.Coalevel1;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.Urc_Activities;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.entity.livedata.UrcDepartmentAnlDim;
import com.methaltech.application.security.AuthenticatedUser;
import com.methaltech.application.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.splitlayout.SplitLayoutVariant;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.PageRequest;

@PageTitle("Approval")
@Route(value = "approval", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "BLO", "HOD", "MA", "MD"})
public final class ApprovalView extends Div {

    private final UserService userService;

    private User user;
    private AuthenticatedUser authenticatedUser;
    private ComboBox<Budget> comboBoxBudget = new ComboBox<>("Budget");
    private MultiSelectComboBox<UrcDeptSectionAnlDimbgt> comboBoxD_Section = new MultiSelectComboBox<>("Cost Centre");
    private MultiSelectComboBox<UrcDepartmentAnlDim> comboBoxDepartment = new MultiSelectComboBox<>("Department");
    private final BudgetService chosenBudgetService;
    private final DeptSectionMergerService sampleDeptSectionMergerService;
    private ComboBox<Coalevel1> comboBoxCoalevel1;
    private final Coalevel1Service coalevel1Service;
    private Grid<COA> gridCOATT = new Grid<>(COA.class, false);
    Grid<BudgetItems> gridBudgetCoa = new Grid<>(BudgetItems.class, false);
    private final CoaService coaService;
    private MultiSelectComboBox<Organisation> comboBoxOrganisation = new MultiSelectComboBox<>("Budget Type");
    private final OrganisationService organisationService;
    private DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
    private final BudgetItemsService budgetItemsService;
    private Span footerTotal = new Span("");
    Footer footer = new Footer();
    private Grid<BudgetItems> gridBudgetItems = new Grid<>(BudgetItems.class, false);
    private Grid<BudgetApproval> gridBudgetApproval = new Grid<>(BudgetApproval.class, false);

    public ApprovalView(UserService userService, AuthenticatedUser authenticatedUser, BudgetService chosenBudgetService,
            DeptSectionMergerService sampleDeptSectionMergerService, Coalevel1Service coalevel1Service,
            CoaService coaService, OrganisationService organisationService, BudgetItemsService budgetItemsService) {
        this.userService = userService;
        this.authenticatedUser = authenticatedUser;
        this.chosenBudgetService = chosenBudgetService;
        this.sampleDeptSectionMergerService = sampleDeptSectionMergerService;
        this.coalevel1Service = coalevel1Service;
        this.coaService = coaService;
        this.organisationService = organisationService;
        this.budgetItemsService = budgetItemsService;
        this.setHeightFull();
        footerTotal.getElement().getThemeList().add("badge success");

        if (authenticatedUser.get().isPresent()) {
            user = authenticatedUser.get().get();
        }
        Image image2 = new Image("images/ugflagstrip.png", "Strip");
        image2.setWidthFull();
        image2.getStyle().set("margin", "0").set("padding", "0");

        TabSheet tabSheet = new TabSheet();
        //tabSheet.add("Budget", coaDiv());
        tabSheet.add("Budget", coaDiv());
        //tabSheet.add("Approvals", new Div(new Text("This is the Payment tab content")));
        tabSheet.add("Approvals", gridApprovals());
        tabSheet.setHeightFull();

        // add( HeaderMenu(),coaDiv());
        Div divs = new Div();
        divs.add(HeaderMenu(), tabSheet);
        divs.setHeightFull();
        divs.getStyle().set("flex-grow", "1");
        add(image2, divs);

    }

    public Div HeaderMenu() {
        Div heads = new Div();
        heads.getStyle().set("background-color", "lightgray");
        heads.getStyle().set("padding-bottom", "10px");

        comboBoxD_Section.setItemLabelGenerator(UrcDeptSectionAnlDimbgt::getNAME);

        comboBoxDepartment.setItemLabelGenerator(UrcDepartmentAnlDim::getNAME);
        comboBoxDepartment.addValueChangeListener(e -> {
            comboBoxD_Section.clear();
            comboBoxDepartment.setValue(e.getValue());
            refreshgridBudgetItemCOA();
        });
        if (user.getDeptsection() != null) {
            comboBoxD_Section.setItems(user.getDeptsection());
            if (user.getRoles().contains(Role.ADMIN) || user.getRoles().contains(Role.CFO) || user.getRoles().contains(Role.MD)) {
                comboBoxDepartment.setItems(sampleDeptSectionMergerService.getDeptCodes(user.getDeptsection().stream().toList()));
            }

        }

        comboBoxBudget.setItems(query -> chosenBudgetService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        comboBoxBudget.setItemLabelGenerator(Budget::getFinancialYear);

        // Add some spacing between components using CSS
        comboBoxBudget.getElement().getStyle().set("margin-right", "10px");
        comboBoxD_Section.getElement().getStyle().set("margin-right", "10px");

        comboBoxBudget.addValueChangeListener(e -> {
            comboBoxCoalevel1.setItems(coalevel1Service.findByBudget());
            comboBoxOrganisation.setItems(organisationService.findByBudgetList(e.getValue()));
            refreshgridBudgetItemCOA();
        });

        comboBoxD_Section.addValueChangeListener(e -> {
            comboBoxDepartment.clear();
            comboBoxD_Section.setValue(e.getValue());
            refreshgridBudgetItemCOA();
        });
        comboBoxOrganisation.setItemLabelGenerator(Organisation::getName);
        comboBoxOrganisation.addValueChangeListener(e -> {
            refreshgridBudgetItemCOA();
        });
        heads.add(comboBoxBudget, comboBoxD_Section, comboBoxDepartment);
        return heads;
    }

    public Div BudgetView() {
        Div splitLayout = new Div(ApprovalsView());
        splitLayout.setHeightFull();
        //splitLayout.setOrientation(SplitLayout.Orientation.HORIZONTAL);
        // splitLayout.addThemeVariants(SplitLayoutVariant.LUMO_SMALL);
        if (user.getRoles().contains(Role.BLO)) {

        }
        //coaDiv().setSizeFull();
        // splitLayout.setSizeFull();
        return splitLayout;
    }

    public Div DeatilsGrid() {
        Div gridDetails = new Div();
        Grid.Column<BudgetItems> codeColumn = gridBudgetItems
                .addColumn(budgetItem -> {
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
        Grid.Column<BudgetItems> itemColumn = gridBudgetItems.addColumn(BudgetItems::getItem).setHeader("Description").setKey("Item").setFlexGrow(2);
        Grid.Column<BudgetItems> actcodeColumn = gridBudgetItems
                .addColumn(budgetItem -> {
                    //COA coacode = budgetItem.getCoacode();
                    Urc_Activities act = budgetItem.getActivity();
                    Text label = new Text(act != null ? act.getActivityCode() : "");
                    return label.getText(); // Get the text content
                })
                .setHeader("Activity Code").setWidth("150px").setFlexGrow(0)
                .setSortable(true) // Make the column sortable
                .setComparator((budgetItem1, budgetItem2) -> {
                    // Implement your custom comparator logic here
                    String name1 = budgetItem1.getActivity() != null ? budgetItem1.getActivity().getActivityCode() : "";
                    String name2 = budgetItem2.getActivity() != null ? budgetItem2.getActivity().getActivityCode() : "";
                    return name1.compareTo(name2);
                });

        Grid.Column<BudgetItems> totalColumn = gridBudgetItems.addColumn(budgetItem -> {
            BigDecimal total = generatesumofMonths(budgetItem);
            // Pattern to remove extra zeros
            return decimalFormat.format(total);
        }).setHeader("Total")
                .setKey("totalz")
                .setSortable(true)
                .setComparator((budgetItem1, budgetItem2) -> {
                    BigDecimal total1 = generatesumofMonths(budgetItem1);
                    BigDecimal total2 = generatesumofMonths(budgetItem2);
                    return total1.compareTo(total2);
                }).setWidth("170px").setFlexGrow(0);
        gridBudgetItems.addColumn(budgetItem -> {
            return decimalFormat.format(budgetItem.getJul());
        }).setHeader("Jul")
                .setKey("jul")
                .setSortable(true)
                .setComparator((budgetItem1, budgetItem2) -> {
                    BigDecimal total1 = budgetItem1.getJul();
                    BigDecimal total2 = budgetItem2.getJul();
                    return total1.compareTo(total2);
                }).setWidth("170px").setFlexGrow(0);

        gridBudgetItems.addColumn(budgetItem -> {
            return decimalFormat.format(budgetItem.getAug());
        }).setHeader("Aug")
                .setKey("aug")
                .setSortable(true)
                .setComparator((budgetItem1, budgetItem2) -> {
                    BigDecimal total1 = budgetItem1.getAug();
                    BigDecimal total2 = budgetItem2.getAug();
                    return total1.compareTo(total2);
                }).setWidth("170px").setFlexGrow(0);
        gridBudgetItems.addColumn(budgetItem -> {
            return decimalFormat.format(budgetItem.getSep());
        }).setHeader("Sep")
                .setKey("sep")
                .setSortable(true)
                .setComparator((budgetItem1, budgetItem2) -> {
                    BigDecimal total1 = budgetItem1.getSep();
                    BigDecimal total2 = budgetItem2.getSep();
                    return total1.compareTo(total2);
                }).setWidth("170px").setFlexGrow(0);
        gridBudgetItems.addColumn(budgetItem -> {
            return decimalFormat.format(budgetItem.getOct());
        }).setHeader("Oct")
                .setKey("oct")
                .setSortable(true)
                .setComparator((budgetItem1, budgetItem2) -> {
                    BigDecimal total1 = budgetItem1.getOct();
                    BigDecimal total2 = budgetItem2.getOct();
                    return total1.compareTo(total2);
                }).setWidth("170px").setFlexGrow(0);
        gridBudgetItems.addColumn(budgetItem -> {
            return decimalFormat.format(budgetItem.getNov());
        }).setHeader("Nov")
                .setKey("nov")
                .setSortable(true)
                .setComparator((budgetItem1, budgetItem2) -> {
                    BigDecimal total1 = budgetItem1.getNov();
                    BigDecimal total2 = budgetItem2.getNov();
                    return total1.compareTo(total2);
                }).setWidth("170px").setFlexGrow(0);
        gridBudgetItems.addColumn(budgetItem -> {
            return decimalFormat.format(budgetItem.getDec());
        }).setHeader("Dec")
                .setKey("dec")
                .setSortable(true)
                .setComparator((budgetItem1, budgetItem2) -> {
                    BigDecimal total1 = budgetItem1.getDec();
                    BigDecimal total2 = budgetItem2.getDec();
                    return total1.compareTo(total2);
                }).setWidth("170px").setFlexGrow(0);
        gridBudgetItems.addColumn(budgetItem -> {
            return decimalFormat.format(budgetItem.getJan());
        }).setHeader("Jan")
                .setKey("jan")
                .setSortable(true)
                .setComparator((budgetItem1, budgetItem2) -> {
                    BigDecimal total1 = budgetItem1.getJan();
                    BigDecimal total2 = budgetItem2.getJan();
                    return total1.compareTo(total2);
                }).setWidth("170px").setFlexGrow(0);
        gridBudgetItems.addColumn(budgetItem -> {
            return decimalFormat.format(budgetItem.getFeb());
        }).setHeader("Feb")
                .setKey("feb")
                .setSortable(true)
                .setComparator((budgetItem1, budgetItem2) -> {
                    BigDecimal total1 = budgetItem1.getFeb();
                    BigDecimal total2 = budgetItem2.getFeb();
                    return total1.compareTo(total2);
                }).setWidth("170px").setFlexGrow(0);
        gridBudgetItems.addColumn(budgetItem -> {
            return decimalFormat.format(budgetItem.getMar());
        }).setHeader("Mar")
                .setKey("mar")
                .setSortable(true)
                .setComparator((budgetItem1, budgetItem2) -> {
                    BigDecimal total1 = budgetItem1.getMar();
                    BigDecimal total2 = budgetItem2.getMar();
                    return total1.compareTo(total2);
                }).setWidth("170px").setFlexGrow(0);
        gridBudgetItems.addColumn(budgetItem -> {
            return decimalFormat.format(budgetItem.getApr());
        }).setHeader("Apr")
                .setKey("apr")
                .setSortable(true)
                .setComparator((budgetItem1, budgetItem2) -> {
                    BigDecimal total1 = budgetItem1.getApr();
                    BigDecimal total2 = budgetItem2.getApr();
                    return total1.compareTo(total2);
                }).setWidth("170px").setFlexGrow(0);
        gridBudgetItems.addColumn(budgetItem -> {
            return decimalFormat.format(budgetItem.getMay());
        }).setHeader("May")
                .setKey("may")
                .setSortable(true)
                .setComparator((budgetItem1, budgetItem2) -> {
                    BigDecimal total1 = budgetItem1.getMay();
                    BigDecimal total2 = budgetItem2.getMay();
                    return total1.compareTo(total2);
                }).setWidth("170px").setFlexGrow(0);
        gridBudgetItems.addColumn(budgetItem -> {
            return decimalFormat.format(budgetItem.getJun());
        }).setHeader("Jun")
                .setKey("jun")
                .setSortable(true)
                .setComparator((budgetItem1, budgetItem2) -> {
                    BigDecimal total1 = budgetItem1.getJun();
                    BigDecimal total2 = budgetItem2.getJun();
                    return total1.compareTo(total2);
                }).setWidth("170px").setFlexGrow(0);

        gridBudgetItems.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);
        gridBudgetItems.setWidthFull();
        gridDetails.add(gridBudgetItems);
        return gridDetails;
    }

    public SplitLayout coaDiv() {
        Div coadiv = new Div();

        comboBoxCoalevel1 = new ComboBox<>("Chart Of Accounts");
        comboBoxCoalevel1.setPlaceholder("Select COA Category");
        comboBoxCoalevel1.setItemLabelGenerator(Coalevel1::getName);
        comboBoxCoalevel1.getElement().getStyle().set("margin-top", "10px");
        if (!comboBoxBudget.isEmpty()) {
            comboBoxCoalevel1.setItems(coalevel1Service.findByBudget());
        }

        comboBoxCoalevel1.addValueChangeListener(ev -> {
            refreshgridBudgetItemCOA();

        });

        // Configure Grid
        gridBudgetCoa.addColumn(budgetItem -> {
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
        gridBudgetCoa.addColumn(BudgetItems::getItem).setHeader("Description").setFlexGrow(2);
        gridBudgetCoa.addColumn(new ComponentRenderer<>(urcActivity -> {

            Span span = new Span(decimalFormat.format(urcActivity.getTotal()));
            span.getElement().getThemeList().add("badge success");

            return span;

        })).setHeader("Total").setFlexGrow(0).setWidth("150px");

        gridBudgetCoa.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);
        gridBudgetCoa.setHeightFull(); // Make sure the layout occupies all available width
        gridBudgetCoa.asSingleSelect().addValueChangeListener(e -> {
            if (e.getValue() != null) {
                gridBudgetItems.setItems(budgetItemsService.findByOrgAndBudgetAndSection3(comboBoxOrganisation.getValue(), comboBoxBudget.getValue(), comboBoxD_Section.getValue(), comboBoxCoalevel1.getValue(), e.getValue().getCoacode()));
            }

        });
        // Set flex-grow to 1 for the grid to occupy the remaining space
        // gridBudgetCoa.getStyle().set("flex-grow", "4");
        //SplitLayout splitLayout = new SplitLayout(new Div(comboBoxCoalevel1, gridCOATT), new Div());
        SplitLayout splitLayout = new SplitLayout();

        splitLayout.setHeightFull();
        splitLayout.setOrientation(SplitLayout.Orientation.HORIZONTAL);
        splitLayout.setSplitterPosition(25);
        splitLayout.addThemeVariants(SplitLayoutVariant.LUMO_SMALL);
        HorizontalLayout lay = new HorizontalLayout();
        lay.getStyle().set("background-color", "lightgray");
        lay.getStyle().set("padding-bottom", "10px");
        lay.add(comboBoxCoalevel1, comboBoxOrganisation);
        coadiv.add(lay, gridBudgetCoa, footer); // Add the grid to the coadiv
        footer.add(footerTotal);
        splitLayout.addToPrimary(coadiv);
        splitLayout.addToSecondary(DeatilsGrid());

        return splitLayout;
    }

    public Div ApprovalsView() {
        Div budgetview = new Div();
        if (user.getRoles().contains(Role.BLO)) {

        }
        return budgetview;
    }

    private void refreshgridCOA(String search) {
        if (!comboBoxCoalevel1.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxBudget.isEmpty()) {
            Set<UrcDeptSectionAnlDimbgt> sectionSet = new HashSet<>();
            // sectionSet.add(comboBoxD_Section.getValue());
            //gridCOATT.setItems(coaService.findByDeptsectionAndCoalevel1AndBudgetAndSearchTerm(sectionSet, comboBoxCoalevel1.getValue(), comboBoxBudget.getValue(), search));
        }

    }

    public Notification Notificationwarning(String warning) {

        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);

        Div text = new Div(
                // new Text("Your session will expire in 5 minutes due to inactivity."),
                // new HtmlComponent("br"),
                new Text(warning)
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

        notification.setPosition(Notification.Position.TOP_CENTER);

        return notification;
    }

    private void refreshgridBudgetItemCOA() {
        Set<UrcDeptSectionAnlDimbgt> sections = new HashSet<>();
        if (!comboBoxD_Section.isEmpty()) {
            sections = comboBoxD_Section.getValue();
        } else if (!comboBoxDepartment.isEmpty()) {
            sections = sampleDeptSectionMergerService.getSections(comboBoxDepartment.getValue());
            System.out.println(sections.size());
        }
        if (!comboBoxBudget.isEmpty() && !sections.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxCoalevel1.isEmpty()) {
            List<BudgetItems> lists = budgetItemsService.findDistictCodeAndNames(comboBoxOrganisation.getValue(), comboBoxBudget.getValue(), sections, comboBoxCoalevel1.getValue());
            gridBudgetCoa.setItems(lists);
            if (!lists.isEmpty()) {
                footerTotal.setText("Total " + comboBoxCoalevel1.getValue().getName() + ":    " + decimalFormat.format(budgetItemsService.calculateTotalSum(lists)));
                footerTotal.getStyle().set("text-align", "right");

            }

        }
    }

    private static BigDecimal generatesumofMonths(BudgetItems budget) {
        BigDecimal sumofMonths = BigDecimal.ZERO;

        if (budget.getJan() != null) {
            sumofMonths = sumofMonths.add(budget.getJan());
        }
        if (budget.getFeb() != null) {
            sumofMonths = sumofMonths.add(budget.getFeb());
        }
        if (budget.getMar() != null) {
            sumofMonths = sumofMonths.add(budget.getMar());
        }
        if (budget.getApr() != null) {
            sumofMonths = sumofMonths.add(budget.getApr());
        }
        if (budget.getMay() != null) {
            sumofMonths = sumofMonths.add(budget.getMay());
        }
        if (budget.getJun() != null) {
            sumofMonths = sumofMonths.add(budget.getJun());
        }
        if (budget.getJul() != null) {
            sumofMonths = sumofMonths.add(budget.getJul());
        }
        if (budget.getAug() != null) {
            sumofMonths = sumofMonths.add(budget.getAug());
        }
        if (budget.getSep() != null) {
            sumofMonths = sumofMonths.add(budget.getSep());
        }
        if (budget.getOct() != null) {
            sumofMonths = sumofMonths.add(budget.getOct());
        }
        if (budget.getNov() != null) {
            sumofMonths = sumofMonths.add(budget.getNov());
        }
        if (budget.getDec() != null) {
            sumofMonths = sumofMonths.add(budget.getDec());
        }

        return sumofMonths;
    }

    private Div gridApprovals() {
        Div divApprovals = new Div();

        gridBudgetApproval.addColumn(new ComponentRenderer<>(urcActivity -> {

            Span span = new Span(urcActivity.getSection().getNAME());
            span.getElement().getThemeList().add("badge success");

            return span;

        })).setHeader("Cost Centre").setFlexGrow(0).setWidth("150px");
        gridBudgetApproval.addColumn(BudgetApproval::getBloSubmission).setHeader("BLO Submission");
        gridBudgetApproval.addColumn(BudgetApproval::getBloSubmissionDate).setHeader("BLO Submission Date");
        gridBudgetApproval.addColumn(BudgetApproval::getHodSubmission).setHeader("HOD Submission");
        gridBudgetApproval.addColumn(BudgetApproval::getHodSubmissionDate).setHeader("HOD Submission Date");

        gridBudgetApproval.addColumn(BudgetApproval::getMaSubmission).setHeader("MA Submission");
        gridBudgetApproval.addColumn(BudgetApproval::getMaSubmissionDate).setHeader("MA Submission Date");
        gridBudgetApproval.addColumn(BudgetApproval::getCfoSubmission).setHeader("CFO Submission");
        gridBudgetApproval.addColumn(BudgetApproval::getCfoSubmissionDate).setHeader("CFO Submission Date");

        gridBudgetApproval.addColumn(BudgetApproval::getMaApproval).setHeader("MD Approval");
        gridBudgetApproval.addColumn(BudgetApproval::getMaApprovalDate).setHeader("MD Approval Date");
        gridBudgetApproval.setHeight("150px");
        divApprovals.add(gridBudgetApproval);
        return divApprovals;
    }
}
