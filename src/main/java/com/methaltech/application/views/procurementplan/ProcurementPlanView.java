package com.methaltech.application.views.procurementplan;

import com.methaltech.application.data.EmailSender;
import com.methaltech.application.data.EmailValidator;
import com.methaltech.application.data.ProcurementMethodList;
import com.methaltech.application.data.ProcuremntTypeList;
import com.methaltech.application.data.Role;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.D_Unit;
import com.methaltech.application.data.entity.bgtool.UnitsBudget;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.DataDuplicationService;
import com.methaltech.application.data.bgtool.service.ProcurementPlanService;
import com.methaltech.application.data.bgtool.service.UnitService;
import com.methaltech.application.data.bgtool.service.UnitsBudgetService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.entity.bgtool.ProcurementPlan;
import com.methaltech.application.data.oldbgtool.service.DepartmentUnitService;
import com.methaltech.application.data.oldbgtool.service.UrcUserService;
import com.methaltech.application.data.oldbgtool.service.UserUnitsService;
import com.methaltech.application.data.livedata.service.UrcDeptSectionAnlDimService;
import com.methaltech.application.security.AuthenticatedUser;
import com.methaltech.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import java.util.Random;

@PageTitle("Procurement Plan")
@Route(value = "procurementplan", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class ProcurementPlanView extends Div {

    private static final Random random = new Random();
    private static final char[] symbols;

    private final Grid<ProcurementPlan> grid = new Grid<>(ProcurementPlan.class, false);

    private TextField subject = new TextField("Subject of Procurement");
    private ComboBox currency = new ComboBox("Currency");
    private ComboBox fundsource = new ComboBox("Source of Funds");
    private ComboBox procMethod = new ComboBox("Procurement Method");
    private ComboBox contractType = new ComboBox("Procurement Type");
    private NumberField cost = new NumberField("Cost");

    private ComboBox<Budget> budget = new ComboBox("Budget");
    private Checkbox preq = new Checkbox("Prequalification");
    private Checkbox reserv = new Checkbox("Application of Reserve Scheme");
    DatePicker bid_inv = new DatePicker("Bid Invitation Date");
    DatePicker bid_open = new DatePicker("Bid Opening Date");
    DatePicker evaluation = new DatePicker("Evaluation Report Date");
    DatePicker award_not = new DatePicker("Award Notification Date");
    DatePicker contract_sign = new DatePicker("Contract Signing Date");
    DatePicker completion = new DatePicker("Completion Date");

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<ProcurementPlan> binder;

    private ProcurementPlan sampleProcurementPlan;
    private Budget sampleBudget;
    private UnitsBudget sampleUnitsBudget;
    private AuthenticatedUser authenticatedUser;

    private final UserService samplePersonService;
    private final BudgetService sampleBudgetService;
    private final UnitService sampleUnitService;
    private final UnitsBudgetService sampleUnitsBudgetService;
    private final DataDuplicationService sampleDataDuplicationService;
    private final UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService;
    private final UrcUserService urcUserService;
    private final DepartmentUnitService departmentUnitService;

    private final ProcurementPlanService ProcurementPlanService;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private final EmailValidator emailValidator;
    public MultiSelectComboBox<D_Unit> unitsList;
    private final UrcDeptSectionAnlDimService sampleSectionService;
    private final UserUnitsService UserUnitsService;

    @Autowired
    public ProcurementPlanView(UserService samplePersonService, EmailValidator emailValidator,
            BudgetService sampleBudgetService, UnitService sampleUnitService, UnitsBudgetService sampleUnitsBudgetService,
            UrcDeptSectionAnlDimService sampleSectionService, DataDuplicationService sampleDataDuplicationService,
            UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService, AuthenticatedUser authenticatedUser,
            UrcUserService urcUserService, UserUnitsService UserUnitsService, DepartmentUnitService departmentUnitService,
            ProcurementPlanService ProcurementPlanService) {
        this.samplePersonService = samplePersonService;
        this.emailValidator = emailValidator;
        this.sampleBudgetService = sampleBudgetService;
        this.sampleUnitService = sampleUnitService;
        this.sampleUnitsBudgetService = sampleUnitsBudgetService;
        this.sampleSectionService = sampleSectionService;
        this.sampleDataDuplicationService = sampleDataDuplicationService;
        this.sampleUrcDeptSectionAnlDimbgtService = sampleUrcDeptSectionAnlDimbgtService;
        this.urcUserService = urcUserService;
        this.UserUnitsService = UserUnitsService;
        this.departmentUnitService = departmentUnitService;
        this.authenticatedUser = authenticatedUser;
        this.ProcurementPlanService = ProcurementPlanService;
        addClassNames("user-view");
        this.setHeight("100%");
        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setHeight("100%");
        splitLayout.setSplitterPosition(65);
        Image image2 = new Image("images/ugflagstrip.png", "Strip");
        image2.setWidthFull();
        image2.getStyle().set("margin", "0").set("padding", "0");
        add(image2);
        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);
        add(createBudgetSelectLayout());
        add(splitLayout);

        // Configure Grid
        grid.addColumn("subject").setAutoWidth(true);
        grid.addColumn("currency").setAutoWidth(true);
        grid.addColumn("cost").setAutoWidth(true);
        grid.addColumn("fundsource").setAutoWidth(true);
        grid.addColumn("procurementmethod").setAutoWidth(true);
        grid.addColumn("procurementtype").setAutoWidth(true);
        grid.addColumn("prequal").setAutoWidth(true);
        grid.addColumn("reserve").setAutoWidth(true);
        grid.addColumn("invite").setAutoWidth(true);
        grid.addColumn("close").setAutoWidth(true);
        grid.addColumn("evaluation").setAutoWidth(true);
        grid.addColumn("notification").setAutoWidth(true);
        grid.addColumn("signing").setAutoWidth(true);
        grid.addColumn("completion").setAutoWidth(true);
        //grid.addColumn("roles").setAutoWidth(true);

        PersonContextMenu contextMenu = new PersonContextMenu(grid);

        refreshgridProcurementPlan();
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                fundsource.setReadOnly(true);
                // unitsList.setEnabled(true);
                sampleProcurementPlan = event.getValue();
                populateForm(event.getValue());
                UI.getCurrent().navigate(ProcurementPlanView.class);
                //UI.getCurrent().navigate(String.format(SAMPLEPERSON_EDIT_ROUTE_TEMPLATE, event.getValue().getUserId()));
            } else {
                fundsource.setReadOnly(false);
                clearForm();
                sampleProcurementPlan = new ProcurementPlan();
                UI.getCurrent().navigate(ProcurementPlanView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(ProcurementPlan.class);

        //binder.bindInstanceFields(this);
        // Bind form fields to User properties
        cancel.addClickListener(e -> {
            clearForm();
            // refreshGrid();
            refreshgridProcurementPlan();
        });

        save.addClickListener(e -> {
            CharSequence editorTextFields = validEditorTextFields();

            if (editorTextFields != null && editorTextFields.length() > 0) {
                //Notification.show("Fill in the Empty Field (s)");
                NotificationDialogue(" Either " + validEditorTextFields().toString() + "Or Select Budget");
                // Notification not = Notification.show("Fill in the Empty Field (s).");
                // not.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {
                int newdeterminant = 0;
                sampleBudget = budget.getValue();
                try {
                    if (this.sampleProcurementPlan == null) {
                        this.sampleProcurementPlan = new ProcurementPlan();

                    } else {
                        // this.sampleProcurementPlan.setDeptsection(preq.getValue());
                        //  samplePersonService.update(this.sampleProcurementPlan);

                        refreshgridProcurementPlan();
                        clearForm();

                    }

                    UI.getCurrent().navigate(ProcurementPlanView.class);
                } catch (Exception validationException) {
                    // Notification.show(" An exception happened while trying to store the User details. ");
                    Notificationerror(" An exception happened while trying to store the User details. " + validationException.getMessage());
                }
            }

        });

    }

    private Div createBudgetSelectLayout() {
        budget = new ComboBox("Select Budget");
        budget.setItemLabelGenerator(Budget::getFinancialYear);

        Div div = new Div();
        div.add(budget);

        budget.setItems(query -> sampleBudgetService.list(PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query))).stream());
        budget.addValueChangeListener(ev -> {

            refreshgridProcurementPlan();
            sampleBudget = ev.getValue();
        });
        return div;
    }

    private void refreshgridProcurementPlan() {
        if (budget != null && !budget.isEmpty()) {

        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("user-editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("user-editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();

        subject.setRequired(true);
        subject.setRequiredIndicatorVisible(true);
        subject.setErrorMessage("Subject is Required");
        subject.setClearButtonVisible(true);

        currency.setRequired(true);
        currency.setRequiredIndicatorVisible(true);
        currency.setErrorMessage("Currency is Required");
        currency.setClearButtonVisible(true);

        cost.setRequired(true);
        cost.setRequiredIndicatorVisible(true);
        cost.setErrorMessage("Fund Source is Required");
        cost.setClearButtonVisible(true);

        fundsource.setRequired(true);
        fundsource.setRequiredIndicatorVisible(true);
        fundsource.setErrorMessage("Fund Source is Required");
        fundsource.setClearButtonVisible(true);

        procMethod.setClearButtonVisible(true);
        procMethod.setRequired(true);
        procMethod.setRequiredIndicatorVisible(true);
        procMethod.setErrorMessage("Procurement Method is Required");
        procMethod.setItems(ProcurementMethodList.Micro_Procurement, ProcurementMethodList.Open_Bidding, ProcurementMethodList.Request_for_Quotations,
                ProcurementMethodList.Restricted_Bidding, ProcurementMethodList.Selection_Method_Threshold_in_UGX_Request_for_Proposals_with_Expression_of_Interest, ProcurementMethodList.Selection_Method_Threshold_in_UGX_Request_for_Proposals_without_Expression_of_Interest
        );

        contractType.setRequired(true);
        contractType.setRequiredIndicatorVisible(true);
        contractType.setErrorMessage("Contact Type is Required");

        // Populate combo with roles
        contractType.setItems(ProcuremntTypeList.Admeasurement_contracts, ProcuremntTypeList.Cost_reimbursable_contract, ProcuremntTypeList.Framework_contract,
                ProcuremntTypeList.Lump_sum_contract, ProcuremntTypeList.Percentage_based_contract, ProcuremntTypeList.Retainer_contract,
                ProcuremntTypeList.Success_fee_contract, ProcuremntTypeList.Target_price_contract, ProcuremntTypeList.Time_based_contract);
        formLayout.add(subject, currency, fundsource, procMethod, contractType, preq, reserv, bid_inv,
                bid_open, evaluation, award_not, contract_sign, completion);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0px", 1),
                new FormLayout.ResponsiveStep("600px", 2),
                new FormLayout.ResponsiveStep("900px", 3)
        );
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private StringBuilder validEditorTextFields() {
        StringBuilder build = new StringBuilder();
        if (subject.isEmpty()) {
            build.append("Subject field is empty \n");

        }
        if (currency.isEmpty()) {
            build.append("Currency field is empty \n");

        }
        if (fundsource.isEmpty()) {
            build.append("Fund source is empty \n");

        }
        if (contractType.isEmpty()) {
            build.append("Contract Type field is empty \n");

        }
        return build;
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buttonLayout.add(save, cancel);

        editorLayoutDiv.add(buttonLayout);

    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        preq.clear();
        populateForm(null);
        budget.setValue(sampleBudget);
        refreshgridProcurementPlan();
        //unitsList.setEnabled(false);
    }

    private void populateForm(ProcurementPlan value) {
        this.sampleProcurementPlan = value;
        binder.readBean(this.sampleProcurementPlan);

    }

    public static String prepareRandomString(int len) {
        char[] buf = new char[len];

        for (int idx = 0; idx < buf.length; ++idx) {
            buf[idx] = symbols[random.nextInt(symbols.length)];
        }

        return new String(buf);
    }

    static {
        StringBuilder tmp = new StringBuilder();

        for (char ch = '0'; ch <= '9'; ++ch) {
            tmp.append(ch);
        }

        for (char ch = 'a'; ch <= 'z'; ++ch) {
            tmp.append(ch);
        }

        symbols = tmp.toString().toCharArray();
    }

    private Component Notificationerror(String errorMessage) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        Div text = new Div(new Text(errorMessage));

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> {
            notification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(Alignment.CENTER);

        notification.add(layout);

        notification.open();
        return notification;
    }

    private Component NotificationDialogue(String errorMessage) {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Fill in the Empty Fields");
        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getHeader().add(closeButton);
        TextArea label = new TextArea();
        label.setEnabled(false);
        label.setValue(errorMessage);
        label.setHeightFull();
        label.setWidthFull();
        label.getStyle().set("background", "red");

        VerticalLayout dialogLayout = new VerticalLayout(label);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");
        //dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        dialog.add(dialogLayout);
        dialog.open();
        return dialog;
    }

    public Dialog unitListEditor(MultiSelectComboBox<D_Unit> unitsList) {

        Dialog dialog = new Dialog();
        VerticalLayout div = new VerticalLayout();

        dialog.setHeaderTitle("User Units");

        Button saveButton = new Button("Save", e -> {

            dialog.close();

        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);

        /*            // Center the button within the example
            dialog.getStyle().set("position", "fixed").set("top", "0").set("right", "0")
            .set("bottom", "0").set("left", "0").set("display", "flex")
            .set("align-items", "center").set("justify-content", "center");*/
        div.setPadding(false);
        div.setSpacing(false);
        div.setAlignItems(FlexComponent.Alignment.STRETCH);
        div.getStyle().set("width", "500px").set("max-width", "100%");

        div.add(unitsList);
        dialog.add(div);

        dialog.open();

        return dialog;
    }

    private class PersonContextMenu extends GridContextMenu<ProcurementPlan> {

        public PersonContextMenu(Grid<ProcurementPlan> target) {
            super(target);

            addItem("Edit", e -> e.getItem().ifPresent(person -> {
                // System.out.printf("Edit: %s%n", person.getFullName());
            }));
            addItem("Delete", e -> e.getItem().ifPresent(person -> {
                // System.out.printf("Delete: %s%n", person.getFullName());
            }));
            addItem("Add Department Units", e -> e.getItem().ifPresent(person -> {

            }));

            add(new Hr());

            GridMenuItem<ProcurementPlan> emailItem = addItem("Email",
                    e -> e.getItem().ifPresent(person -> {
                        // System.out.printf("Email: %s%n",
                        // person.getFullName());
                    }));
            GridMenuItem<ProcurementPlan> phoneItem = addItem("Call",
                    e -> e.getItem().ifPresent(person -> {
                        // System.out.printf("Phone: %s%n",
                        // person.getFullName());
                    }));

            setDynamicContentHandler(person -> {
                // Do not show context menu when header is clicked
                if (person == null) {
                    return false;
                }
                emailItem
                        .setText(String.format("Email: %s", person.getSubject()));
                phoneItem.setText(String.format("Call: %s",
                        person.getCurrency()));
                return true;
            });
        }

    }
}
