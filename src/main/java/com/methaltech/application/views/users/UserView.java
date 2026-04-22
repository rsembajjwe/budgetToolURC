package com.methaltech.application.views.users;

import com.methaltech.application.data.BCrypt;
import com.methaltech.application.data.EmailSender;
import com.methaltech.application.data.EmailValidator;
import com.methaltech.application.data.GlobalConstants;
import com.methaltech.application.data.Role;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.DataDuplicationService;
import com.methaltech.application.data.bgtool.service.DeptSectionMergerService;
import com.methaltech.application.data.bgtool.service.UnitService;
import com.methaltech.application.data.bgtool.service.UnitsBudgetService;
import com.methaltech.application.data.bgtool.service.UrDepartmentsAnlDimService2;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.D_Unit;
import com.methaltech.application.data.entity.bgtool.UnitsBudget;
import com.methaltech.application.data.entity.bgtool.UrDepartmentsAnlDim2;
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
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@PageTitle("System Users")
@Route(value = "user-detail/:samplePersonID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class UserView extends Div implements BeforeEnterObserver {

    private static final String SAMPLEPERSON_ID = "samplePersonID";
    private static final String SAMPLEPERSON_EDIT_ROUTE_TEMPLATE = "user-detail/%s/edit";

    private static final Random random = new Random();
    private static final char[] symbols;

    private final Grid<User> grid = new Grid<>(User.class, false);

    private TextField fname;
    private TextField lname;
    private TextField username;
    private TextField tel;
    private TextField searchField;

    private Checkbox active;

    private MultiSelectComboBox<Role> roles;
    private ComboBox<Budget> budget;
    private ComboBox<String> activeFilter;
    private MultiSelectComboBox<UrcDeptSectionAnlDimbgt> sections
            = new MultiSelectComboBox<>("Sections Responsible for:");
    private MultiSelectComboBox<UrDepartmentsAnlDim2> departments
            = new MultiSelectComboBox<>("Departments Responsible for:");

    public MultiSelectComboBox<D_Unit> unitsList;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<User> binder;

    private User samplePerson;
    private Budget sampleBudget;
    private UnitsBudget sampleUnitsBudget;

    private String searchTerm = "";
    private String currentActiveFilter = "ACTIVE";

    private final AuthenticatedUser authenticatedUser;
    private final UserService samplePersonService;
    private final BudgetService sampleBudgetService;
    private final UnitService sampleUnitService;
    private final UnitsBudgetService sampleUnitsBudgetService;
    private final DataDuplicationService sampleDataDuplicationService;
    private final UrcDeptSectionAnlDimService sampleSectionService;
    private final UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService;
    private final DeptSectionMergerService sampleDeptSectionMergerService;
    private final UrDepartmentsAnlDimService2 sampleUrDepartmentsAnlDimService2;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private final EmailValidator emailValidator;

    @Autowired
    public UserView(UserService samplePersonService,
            EmailValidator emailValidator,
            BudgetService sampleBudgetService,
            UnitService sampleUnitService,
            UnitsBudgetService sampleUnitsBudgetService,
            UrcDeptSectionAnlDimService sampleSectionService,
            DataDuplicationService sampleDataDuplicationService,
            UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService,
            AuthenticatedUser authenticatedUser,
            DeptSectionMergerService sampleDeptSectionMergerService,
            UrDepartmentsAnlDimService2 sampleUrDepartmentsAnlDimService2) {

        this.samplePersonService = samplePersonService;
        this.emailValidator = emailValidator;
        this.sampleBudgetService = sampleBudgetService;
        this.sampleUnitService = sampleUnitService;
        this.sampleUnitsBudgetService = sampleUnitsBudgetService;
        this.sampleSectionService = sampleSectionService;
        this.sampleDataDuplicationService = sampleDataDuplicationService;
        this.sampleUrcDeptSectionAnlDimbgtService = sampleUrcDeptSectionAnlDimbgtService;
        this.authenticatedUser = authenticatedUser;
        this.sampleDeptSectionMergerService = sampleDeptSectionMergerService;
        this.sampleUrDepartmentsAnlDimService2 = sampleUrDepartmentsAnlDimService2;

        addClassNames("user-view");
        setHeightFull();

        binder = new BeanValidationBinder<>(User.class);

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setHeightFull();
        splitLayout.setSplitterPosition(68);

        Image image2 = new Image("images/ugflagstrip.png", "Strip");
        image2.setWidthFull();
        image2.getStyle().set("margin", "0").set("padding", "0");

        add(image2);
        add(createBudgetSelectLayout());

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        configureGrid();
        configureForm();
        configureActions();
        refreshgridUser();
    }

    private void configureGrid() {
        grid.addColumn(User::getFirstName)
                .setHeader("First Name")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(User::getLastName)
                .setHeader("Last Name")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(User::getEmail)
                .setHeader("Email")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(User::getTel)
                .setHeader("Phone")
                .setAutoWidth(true);

        grid.addComponentColumn(this::buildActiveBadge)
                .setHeader("Status")
                .setAutoWidth(true);

        grid.addComponentColumn(user -> {
            Button toggle = new Button(user.isActive() ? "Deactivate" : "Activate");
            if (user.isActive()) {
                toggle.addThemeVariants(ButtonVariant.LUMO_ERROR);
            } else {
                toggle.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            }
            toggle.addClickListener(e -> toggleUserActive(user));
            return toggle;
        }).setHeader("Action").setAutoWidth(true);

        grid.addColumn(user -> user.getRoles() != null
                ? user.getRoles().stream().map(Enum::name).collect(Collectors.joining(", "))
                : "")
                .setHeader("Roles")
                .setAutoWidth(true);

        new PersonContextMenu(grid);

        grid.setHeightFull();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        grid.asSingleSelect().addValueChangeListener(event -> {
            User selected = event.getValue();
            if (selected != null) {
                username.setReadOnly(true);
                samplePerson = selected;
                populateForm(selected);

                if (selected.getDeptsection() != null) {
                    sections.setValue(selected.getDeptsection());
                } else {
                    sections.clear();
                }

                if (selected.getDepartment() != null) {
                    departments.setValue(selected.getDepartment());
                } else {
                    departments.clear();
                }

                UI.getCurrent().navigate(UserView.class);
            } else {
                username.setReadOnly(false);
                clearForm();
                samplePerson = new User();
                UI.getCurrent().navigate(UserView.class);
            }
        });
    }

    private Component buildActiveBadge(User user) {
        Span badge = new Span(user.isActive() ? "Active" : "Inactive");
        badge.getElement().getThemeList().add("badge");
        if (user.isActive()) {
            badge.getElement().getThemeList().add("success");
        } else {
            badge.getElement().getThemeList().add("error");
        }
        return badge;
    }

    private void toggleUserActive(User user) {
        try {
            user.setActive(!user.isActive());
            samplePersonService.update(user);

            Notification notification = Notification.show(
                    user.getEmail() + " is now " + (user.isActive() ? "Active" : "Inactive")
            );
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            refreshgridUser();

            if (samplePerson != null
                    && samplePerson.getUserId() != null
                    && samplePerson.getUserId().equals(user.getUserId())) {
                populateForm(user);
            }
        } catch (Exception ex) {
            Notification notification = Notification.show("Failed to update user status: " + ex.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void configureForm() {
        sections.setItemLabelGenerator(UrcDeptSectionAnlDimbgt::getNAME);
        sections.setItems(query
                -> sampleUrcDeptSectionAnlDimbgtService.findByANL_CODEStartingWithD2(
                        PageRequest.of(
                                query.getPage(),
                                query.getPageSize(),
                                VaadinSpringDataHelpers.toSpringDataSort(query)
                        )
                ).stream()
        );

        departments.setItemLabelGenerator(UrDepartmentsAnlDim2::getName);
        departments.setItems(sampleUrDepartmentsAnlDimService2.findAll());
        departments.setVisible(false);

        binder.bindInstanceFields(this);

        binder.forField(fname)
                .asRequired("First name is Required")
                .bind(User::getFirstName, User::setFirstName);

        binder.forField(lname)
                .asRequired("Last name is Required")
                .bind(User::getLastName, User::setLastName);

        binder.forField(username)
                .asRequired("Email is Required")
                .bind(User::getEmail, User::setEmail);

        binder.forField(tel)
                .bind(User::getTel, User::setTel);

        binder.forField(roles)
                .asRequired("Roles is Required")
                .bind(User::getRoles, User::setRoles);

        binder.forField(departments)
                .bind(User::getDepartment, User::setDepartment);

        binder.forField(active)
                .bind(User::isActive, User::setActive);

        roles.addSelectionListener(e -> {
            if (e.getValue() != null && e.getValue().contains(Role.HOD)) {
                departments.setVisible(true);
            } else {
                departments.setVisible(false);
                departments.clear();
            }
        });
    }

    private void configureActions() {
        cancel.addClickListener(e -> {
            clearForm();
            refreshgridUser();
        });

        save.addClickListener(e -> {
            StringBuilder validation = validEditorTextFields();

            if (validation.length() > 0 || budget == null || budget.isEmpty()) {
                String budgetMessage = (budget == null || budget.isEmpty()) ? "Select Budget" : "";
                NotificationDialogue(validation + budgetMessage);
                return;
            }

            int newdeterminant = 0;
            String pass2 = prepareRandomString(6);
            sampleBudget = budget.getValue();

            try {
                if (this.samplePerson == null || this.samplePerson.getUserId() == null) {
                    this.samplePerson = new User();
                    this.samplePerson.setEmailVerificationHash(
                            BCrypt.hashpw(prepareRandomString(30), GlobalConstants.SALT)
                    );
                    this.samplePerson.setEmailVerificationAttempts(0);
                    this.samplePerson.setStatus(GlobalConstants.NEW);
                    this.samplePerson.setHashedPassword(BCrypt.hashpw(pass2, GlobalConstants.SALT));
                    this.samplePerson.setActive(true);
                    newdeterminant++;
                }

                binder.writeBean(this.samplePerson);
                this.samplePerson.setDeptsection(sections.getValue());
                this.samplePerson.setDepartment(departments.getValue());

                if (samplePersonService.getUsername(this.samplePerson.getEmail()) && newdeterminant > 0) {
                    Notification not = Notification.show("Email Exists.");
                    not.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                if (!emailValidator.ValidateEmail(this.samplePerson.getEmail())) {
                    Notification not = Notification.show("Enter a Valid Email");
                    not.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                samplePersonService.update(this.samplePerson);

                if (newdeterminant > 0) {
                    String[] email = new String[1];
                    email[0] = this.samplePerson.getEmail();

                    String message = emailSender.sendEmail(
                            "URC Budget ToolLTS",
                            "URC BudgetTool Welcomes You, " + pass2 + " is your temporary password",
                            email
                    );

                    if (message.length() > 23) {
                        Notification not = Notification.show("User email notification failed Reason: " + message);
                        not.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    } else {
                        Notification not = Notification.show("User details registered and password sent to email");
                        not.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    }
                } else {
                    Notification not = Notification.show("User updated successfully");
                    not.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                }

                refreshgridUser();
                clearForm();
                UI.getCurrent().navigate(UserView.class);

            } catch (Exception validationException) {
                Notificationerror("An exception happened while trying to store the user details. "
                        + validationException.getMessage());
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> samplePersonId = event.getRouteParameters()
                .get(SAMPLEPERSON_ID)
                .map(Integer::parseInt);

        if (samplePersonId.isPresent()) {
            Optional<User> samplePersonFromBackend = samplePersonService.get(samplePersonId.get());
            if (samplePersonFromBackend.isPresent()) {
                populateForm(samplePersonFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested user was not found, ID = %d", samplePersonId.get()),
                        3000,
                        Notification.Position.BOTTOM_START
                );
                refreshgridUser();
                event.forwardTo(UserView.class);
            }
        }
    }

    private Div createBudgetSelectLayout() {
        budget = new ComboBox<>("Select Budget");
        budget.setItemLabelGenerator(Budget::getFinancialYear);

        Div div = new Div();
        div.add(budget);

        budget.setItems(query
                -> sampleBudgetService.list(
                        PageRequest.of(
                                query.getPage(),
                                query.getPageSize(),
                                VaadinSpringDataHelpers.toSpringDataSort(query)
                        )
                ).stream()
        );

        budget.addValueChangeListener(ev -> {
            sampleBudget = ev.getValue();
            refreshgridUser();

            if (ev.getValue() != null) {
                save.setEnabled(ev.getValue().isActive());
            } else {
                save.setEnabled(true);
            }
        });

        return div;
    }

    private void refreshgridUser() {

        if (budget != null && !budget.isEmpty()) {

            grid.setDataProvider(DataProvider.fromCallbacks(
                    query -> {
                        int offset = query.getOffset();
                        int limit = query.getLimit();
                        int page = offset / limit;

                        return samplePersonService.search(
                                searchTerm,
                                currentActiveFilter,
                                PageRequest.of(
                                        page,
                                        limit,
                                        VaadinSpringDataHelpers.toSpringDataSort(query)
                                )
                        ).stream();
                    },
                    query -> (int) samplePersonService.count(searchTerm, currentActiveFilter)
            ));

        } else {
            grid.setItems(java.util.Collections.emptyList());
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("user-editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("user-editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();

        fname = new TextField("First Name");
        fname.setHelperText("First name is Required");
        fname.setRequired(true);
        fname.setRequiredIndicatorVisible(true);
        fname.setErrorMessage("Required");
        fname.setClearButtonVisible(true);

        lname = new TextField("Last Name");
        lname.setHelperText("Last name is Required");
        lname.setRequired(true);
        lname.setRequiredIndicatorVisible(true);
        lname.setErrorMessage("Required");
        lname.setClearButtonVisible(true);

        username = new TextField("Email");
        username.setHelperText("Email is Required");
        username.setRequired(true);
        username.setRequiredIndicatorVisible(true);
        username.setErrorMessage("Required");
        username.setClearButtonVisible(true);

        tel = new TextField("Phone");
        tel.setClearButtonVisible(true);

        roles = new MultiSelectComboBox<>("Roles");
        roles.setHelperText("Role is Required");
        roles.setRequired(true);
        roles.setRequiredIndicatorVisible(true);
        roles.setErrorMessage("Required");
        roles.setItems(
                Role.ADMIN, Role.USER, Role.BLO, Role.HR, Role.FREIGHT,
                Role.HOD, Role.PROCUREMENT, Role.MD, Role.CFO
        );

        active = new Checkbox("Active");
        active.setValue(true);

        formLayout.add(fname, lname, username, tel, roles, active, departments, sections);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private StringBuilder validEditorTextFields() {
        StringBuilder build = new StringBuilder();

        if (fname.isEmpty()) {
            build.append("First name field is empty \n");
        }
        if (lname.isEmpty()) {
            build.append("Last name field is empty \n");
        }
        if (username.isEmpty()) {
            build.append("Email field is empty \n");
        }
        if (roles.isEmpty()) {
            build.append("Roles field is empty \n");
        }

        return build;
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        buttonLayout.addClassName("top-toolbar");

        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button but = new Button("Refresh Sections");
        but.addSingleClickListener(e -> {
            sampleDataDuplicationService.duplicateData();
            sections.setItems(query
                    -> sampleUrcDeptSectionAnlDimbgtService.findByANL_CODEStartingWithD2(
                            PageRequest.of(
                                    query.getPage(),
                                    query.getPageSize(),
                                    VaadinSpringDataHelpers.toSpringDataSort(query)
                            )
                    ).stream()
            );
        });

        buttonLayout.add(save, cancel);

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            Set<Role> userRoles = user.getRoles();
            if (userRoles.contains(Role.ADMIN)) {
                // buttonLayout.add(but);
            }
        }

        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        wrapper.setSizeFull();

        searchField = new TextField();
        searchField.setPlaceholder("Search by name, email, phone...");
        searchField.setClearButtonVisible(true);
        searchField.setPrefixComponent(new Icon("lumo", "search"));
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.setWidth("70%");
        searchField.addValueChangeListener(e -> {
            searchTerm = e.getValue() == null ? "" : e.getValue().trim();
            refreshgridUser();
        });

        activeFilter = new ComboBox<>("Show");
        activeFilter.setItems("ACTIVE", "INACTIVE", "ALL");
        activeFilter.setValue("ACTIVE");
        activeFilter.setWidth("30%");
        activeFilter.setItemLabelGenerator(value -> switch (value) {
            case "ACTIVE" ->
                "Active";
            case "INACTIVE" ->
                "Inactive";
            case "ALL" ->
                "Both";
            default ->
                value;
        });

        activeFilter.addValueChangeListener(e -> {
            currentActiveFilter = e.getValue() == null ? "ACTIVE" : e.getValue();
            refreshgridUser();
        });

        HorizontalLayout filters = new HorizontalLayout(searchField, activeFilter);
        filters.setWidthFull();
        filters.setSpacing(true);
        filters.setAlignItems(FlexComponent.Alignment.END);
        filters.addClassName("filters-bar");

        VerticalLayout content = new VerticalLayout(filters, grid);
        content.setSizeFull();
        content.setPadding(false);
        content.setSpacing(true);
        content.setFlexGrow(1, grid);

        wrapper.add(content);
        splitLayout.addToPrimary(wrapper);
    }

    private void clearForm() {
        sections.clear();
        departments.clear();
        populateForm(null);
        username.setReadOnly(false);

        if (active != null) {
            active.setValue(true);
        }

        if (budget != null) {
            budget.setValue(sampleBudget);
        }

        refreshgridUser();
    }

    private void populateForm(User value) {
        this.samplePerson = value;

        if (value == null) {
            User emptyUser = new User();
            emptyUser.setActive(true);
            binder.readBean(emptyUser);
            departments.setVisible(false);
        } else {
            binder.readBean(this.samplePerson);
            if (value.getRoles() != null && value.getRoles().contains(Role.HOD)) {
                departments.setVisible(true);
            } else {
                departments.setVisible(false);
            }
        }
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
        closeButton.addClickListener(event -> notification.close());

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(Alignment.CENTER);

        notification.add(layout);
        notification.open();
        return notification;
    }

    private Component NotificationDialogue(String errorMessage) {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Fill in the Empty Fields");

        Button closeButton = new Button(new Icon("lumo", "cross"), e -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getHeader().add(closeButton);

        TextArea label = new TextArea();
        label.setEnabled(false);
        label.setValue(errorMessage);
        label.setHeightFull();
        label.setWidthFull();

        VerticalLayout dialogLayout = new VerticalLayout(label);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        dialog.add(dialogLayout);
        dialog.open();
        return dialog;
    }

    public Dialog unitListEditor(MultiSelectComboBox<D_Unit> unitsList) {
        Dialog dialog = new Dialog();
        VerticalLayout div = new VerticalLayout();

        dialog.setHeaderTitle("User Units");

        Button saveButton = new Button("Save", e -> {
            samplePerson.setUnits(unitsList.getValue());
            sampleUnitsBudget = sampleUnitsBudgetService.listbyBudget(sampleBudget, samplePerson);

            if (!unitsList.getValue().isEmpty()) {
                if (sampleUnitsBudget == null) {
                    sampleUnitsBudget = new UnitsBudget();
                    sampleUnitsBudget.setBudget(sampleBudget);
                    sampleUnitsBudget.setUser(samplePerson);
                    sampleUnitsBudget.setUnits(unitsList.getValue());
                } else {
                    sampleUnitsBudget.setUnits(unitsList.getValue());
                }

                sampleUnitsBudgetService.update(sampleUnitsBudget);
            }

            samplePersonService.update(samplePerson);
            dialog.close();
        });

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);

        div.setPadding(false);
        div.setSpacing(false);
        div.setAlignItems(FlexComponent.Alignment.STRETCH);
        div.getStyle().set("width", "500px").set("max-width", "100%");

        div.add(unitsList);
        dialog.add(div);
        dialog.open();

        return dialog;
    }

    private class PersonContextMenu extends GridContextMenu<User> {

        public PersonContextMenu(Grid<User> target) {
            super(target);

            addItem("Add Department Units", e -> e.getItem().ifPresent(person -> {
                samplePerson = person;

                unitsList = new MultiSelectComboBox<>("Units");
                unitsList.setItemLabelGenerator(D_Unit::getUnit);
                unitsList.setItems(sampleUnitService.findByDepartmentBudget(sampleBudget));

                if (samplePerson.getUnits() != null) {
                    unitsList.setValue(samplePerson.getUnits());
                }

                unitListEditor(unitsList);
            }));
        }
    }
}
