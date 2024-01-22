package com.methaltech.application.views.UnitMeasures;

import com.methaltech.application.data.BCrypt;
import com.methaltech.application.data.EmailSender;
import com.methaltech.application.data.EmailValidator;
import com.methaltech.application.data.GlobalConstants;
import com.methaltech.application.data.Role;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.DataDuplicationService;
import com.methaltech.application.data.bgtool.service.StockUnitMeasureService;
import com.methaltech.application.data.bgtool.service.UnitService;
import com.methaltech.application.data.bgtool.service.UnitsBudgetService;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.D_Unit;
import com.methaltech.application.data.entity.bgtool.StockUnitMeasure;
import com.methaltech.application.data.entity.bgtool.UnitsBudget;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.livedata.service.UrcDeptSectionAnlDimService;
import com.methaltech.application.security.AuthenticatedUser;
import com.methaltech.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
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
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Units of Measure")
@Route(value = "Units of Measure", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class UnitsMeasureView extends Div {

    private final Grid<StockUnitMeasure> grid = new Grid<>(StockUnitMeasure.class, false);

    private TextField code;
    private TextField unit;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button transfer = new Button("Import");
    private TextField searchField = new TextField();

    private final BeanValidationBinder<StockUnitMeasure> binder;

    private StockUnitMeasure samplePerson;
    private AuthenticatedUser authenticatedUser;

    private final UserService samplePersonService;
    private final BudgetService sampleBudgetService;
    private final UnitService sampleUnitService;
    private final UnitsBudgetService sampleUnitsBudgetService;
    private final DataDuplicationService sampleDataDuplicationService;
    private final UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService;
    private final StockUnitMeasureService sampleStockUnitMeasureService;
    @Autowired
    private EmailSender emailSender;

    @Autowired
    private final EmailValidator emailValidator;

    @Autowired
    public UnitsMeasureView(UserService samplePersonService, EmailValidator emailValidator,
            BudgetService sampleBudgetService, UnitService sampleUnitService, UnitsBudgetService sampleUnitsBudgetService,
            UrcDeptSectionAnlDimService sampleSectionService, DataDuplicationService sampleDataDuplicationService,
            UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService, AuthenticatedUser authenticatedUser, StockUnitMeasureService sampleStockUnitMeasureService) {
        this.samplePersonService = samplePersonService;
        this.emailValidator = emailValidator;
        this.sampleBudgetService = sampleBudgetService;
        this.sampleUnitService = sampleUnitService;
        this.sampleUnitsBudgetService = sampleUnitsBudgetService;
        this.sampleDataDuplicationService = sampleDataDuplicationService;
        this.sampleUrcDeptSectionAnlDimbgtService = sampleUrcDeptSectionAnlDimbgtService;
        this.authenticatedUser = authenticatedUser;
        this.sampleStockUnitMeasureService = sampleStockUnitMeasureService;
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
        //searchField.setLabel("Search");
        searchField.setClearButtonVisible(true);
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.setPlaceholder("Search");
        searchField.setWidthFull();

        add(searchField, splitLayout);

        // Configure Grid
        grid.addColumn("code").setHeader("Acronym").setAutoWidth(true);
        grid.addColumn("unit").setHeader("Description").setAutoWidth(true);
        grid.setHeight("100%");

        refreshgridUser();

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // Configure Form
        binder = new BeanValidationBinder<>(StockUnitMeasure.class);
        /*        binder.forField(unitsList)
        .bind(User::getUnits, User::setUnits);*/
        //binder.forField(unitsList).bind("units");
        binder.bindInstanceFields(this);
        // Bind form fields to User properties
        binder.forField(code)
                .asRequired("Acronym is Required") // Add required validation
                .bind(StockUnitMeasure::getCode, StockUnitMeasure::setCode);

        binder.forField(unit)
                .asRequired("Description is Required") // Add required validation
                .bind(StockUnitMeasure::getUnit, StockUnitMeasure::setUnit);

        cancel.addClickListener(e -> {
            clearForm();
            // refreshGrid();
            refreshgridUser();
        });
        transfer.addClickListener(e -> {
            if (sampleStockUnitMeasureService.count() < 1) {
                sampleStockUnitMeasureService.updateTransfer();
                refreshgridUser();
            } else {
                Notification notee = Notification.show("Already Imported");
                notee.addThemeVariants(NotificationVariant.LUMO_WARNING);
            }
        });
        searchField.addValueChangeListener(e -> {
            grid.setItems(sampleStockUnitMeasureService.search(e.getValue()));
        });
        save.addClickListener(e -> {

            CharSequence editorTextFields = validEditorTextFields();

            if (editorTextFields != null && editorTextFields.length() > 0) {
                //Notification.show("Fill in the Empty Field (s)");
                NotificationDialogue(" Either " + validEditorTextFields().toString());
                // Notification not = Notification.show("Fill in the Empty Field (s).");
                // not.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } else {

                try {
                    if (this.samplePerson == null) {
                        this.samplePerson = new StockUnitMeasure();

                        this.samplePerson.setCode(code.getValue());
                        this.samplePerson.setUnit(unit.getValue());
                    } else {
                        this.samplePerson.setCode(code.getValue());
                        this.samplePerson.setUnit(unit.getValue());

                        refreshgridUser();
                        clearForm();
                        //refreshGrid();
                        // Notification not = Notification.show("User details stored.");
                        // not.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                    }

                    UI.getCurrent().navigate(UnitsMeasureView.class);
                } catch (Exception validationException) {
                    // Notification.show(" An exception happened while trying to store the User details. ");
                    Notificationerror(" An exception happened while trying to store the User details. " + validationException.getMessage());
                }
            }

        });
        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                binder.setBean(event.getValue());
                UI.getCurrent().navigate(UnitsMeasureView.class);

                //UI.getCurrent().navigate(String.format(SAMPLEPERSON_EDIT_ROUTE_TEMPLATE, event.getValue().getUserId()));
            } else {

                clearForm();
                samplePerson = new StockUnitMeasure();
                UI.getCurrent().navigate(UnitsMeasureView.class);
            }
        });
    }

    private void refreshgridUser() {

        grid.setItems(query -> sampleStockUnitMeasureService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        //grid.setItems(budget.getValue().getUsers());  

    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("user-editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("user-editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        code = new TextField("Unit Code");
        //code.setHelperText("Unit Code is Required");
        code.setRequired(true);
        code.setRequiredIndicatorVisible(true);
        code.setErrorMessage("Required");
        code.setClearButtonVisible(true);

        unit = new TextField("Unit Of Measure");
        // unit.setHelperText("Unit Of Measure is Required");
        unit.setRequired(true);
        unit.setRequiredIndicatorVisible(true);
        unit.setErrorMessage("Required");
        unit.setClearButtonVisible(true);

        formLayout.add(code, unit);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private StringBuilder validEditorTextFields() {
        StringBuilder build = new StringBuilder();
        if (code.isEmpty()) {
            build.append("First name field is empty \n");

        }
        if (unit.isEmpty()) {
            build.append("Last name field is empty \n");

        }
        return build;
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        buttonLayout.add(save, cancel, transfer);

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
        populateForm(null);
        refreshgridUser();
        //unitsList.setEnabled(false);
    }

    private void populateForm(StockUnitMeasure value) {
        this.samplePerson = value;
        binder.readBean(this.samplePerson);

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
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

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

}
