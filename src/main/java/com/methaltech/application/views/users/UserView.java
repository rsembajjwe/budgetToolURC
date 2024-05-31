package com.methaltech.application.views.users;

import com.methaltech.application.data.BCrypt;
import com.methaltech.application.data.EmailSender;
import com.methaltech.application.data.entity.bgtool.*;
import com.methaltech.application.data.EmailValidator;
import com.methaltech.application.data.GlobalConstants;
import com.methaltech.application.data.Role;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.D_Unit;
import com.methaltech.application.data.entity.bgtool.UnitsBudget;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.DataDuplicationService;
import com.methaltech.application.data.bgtool.service.DeptSectionMergerService;
import com.methaltech.application.data.bgtool.service.UnitService;
import com.methaltech.application.data.bgtool.service.UnitsBudgetService;
import com.methaltech.application.data.bgtool.service.UrDepartmentsAnlDimService2;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.entity.bgtool.DeptSectionMerger;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.livedata.UrcDepartmentAnlDim;
import com.methaltech.application.data.entity.oldbgtool.DepartmentUnit;
import com.methaltech.application.data.entity.oldbgtool.UrcUser;
import com.methaltech.application.data.entity.oldbgtool.UserUnits;
import com.methaltech.application.data.livedata.service.UrcDepartmentAnlDimService;
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
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
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
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.List;
import java.util.Optional;
import jakarta.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import java.util.Random;
import java.util.Set;

@PageTitle("System Users")
@Route(value = "user-detail/:samplePersonID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class UserView extends Div implements BeforeEnterObserver {

    private final String SAMPLEPERSON_ID = "samplePersonID";
    private final String SAMPLEPERSON_EDIT_ROUTE_TEMPLATE = "user-detail/%s/edit";

    private static final Random random = new Random();
    private static final char[] symbols;

    private final Grid<User> grid = new Grid<>(User.class, false);

    private TextField fname;
    private TextField lname;
    private TextField username;
    private TextField tel;
    private MultiSelectComboBox<Role> roles;

    private ComboBox<Budget> budget;
    private MultiSelectComboBox<UrcDeptSectionAnlDimbgt> sections = new MultiSelectComboBox("Sections Responsible for:");
    private MultiSelectComboBox<UrDepartmentsAnlDim2> departments = new MultiSelectComboBox("Departments Responsible for:");

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<User> binder;

    private User samplePerson;
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
    private final UrcDepartmentAnlDimService sampleUrcDepartmentAnlDimService;
    private final UrDepartmentsAnlDimService2 sampleUrDepartmentsAnlDimService2;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private final EmailValidator emailValidator;
    public MultiSelectComboBox<D_Unit> unitsList;
    private final UrcDeptSectionAnlDimService sampleSectionService;
    private final UserUnitsService UserUnitsService;
    private final DeptSectionMergerService sampleDeptSectionMergerService;

    @Autowired
    public UserView(UserService samplePersonService, EmailValidator emailValidator,
            BudgetService sampleBudgetService, UnitService sampleUnitService, UnitsBudgetService sampleUnitsBudgetService,
            UrcDeptSectionAnlDimService sampleSectionService, DataDuplicationService sampleDataDuplicationService,
            UrcDeptSectionAnlDimbgtService sampleUrcDeptSectionAnlDimbgtService, AuthenticatedUser authenticatedUser,
            UrcUserService urcUserService, UserUnitsService UserUnitsService, DepartmentUnitService departmentUnitService, UrcDepartmentAnlDimService sampleUrcDepartmentAnlDimService,
            DeptSectionMergerService sampleDeptSectionMergerService,UrDepartmentsAnlDimService2 sampleUrDepartmentsAnlDimService2) {
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
        this.sampleUrcDepartmentAnlDimService = sampleUrcDepartmentAnlDimService;
        this.sampleDeptSectionMergerService = sampleDeptSectionMergerService;
        this.sampleUrDepartmentsAnlDimService2=sampleUrDepartmentsAnlDimService2;
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
        grid.addColumn("firstName").setAutoWidth(true);
        grid.addColumn("lastName").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("tel").setAutoWidth(true);
        //grid.addColumn("roles").setAutoWidth(true);

        PersonContextMenu contextMenu = new PersonContextMenu(grid);

        refreshgridUser();
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Notification.show(event.getValue().getDeptsection().size() + "");
                username.setReadOnly(true);
                // unitsList.setEnabled(true);
                samplePerson = event.getValue();
                populateForm(event.getValue());
                sections.setValue(event.getValue().getDeptsection());
                departments.setValue(event.getValue().getDepartment());
                UI.getCurrent().navigate(UserView.class);
                //UI.getCurrent().navigate(String.format(SAMPLEPERSON_EDIT_ROUTE_TEMPLATE, event.getValue().getUserId()));
            } else {
                username.setReadOnly(false);
                clearForm();
                samplePerson = new User();
                UI.getCurrent().navigate(UserView.class);
            }
        });
        sections.setItemLabelGenerator(UrcDeptSectionAnlDimbgt::getNAME);
        sections.setItems(query -> sampleUrcDeptSectionAnlDimbgtService.findByANL_CODEStartingWithD2(PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query))).stream());

        departments.setItemLabelGenerator(UrDepartmentsAnlDim2::getName);
        /*        departments.setItemLabelGenerator(deptSectionMerger -> {
        String deptcode = deptSectionMerger.getDeptcode();
        return sampleDeptSectionMergerService.findDepartmentByDeptCode(deptcode);
        });*/
        departments.setItems(sampleUrDepartmentsAnlDimService2.findAll());

        // Configure Form
        binder = new BeanValidationBinder<>(User.class);
        /*        binder.forField(unitsList)
        .bind(User::getUnits, User::setUnits);*/
        //binder.forField(unitsList).bind("units");
        binder.bindInstanceFields(this);
        // Bind form fields to User properties
        binder.forField(fname)
                .asRequired("First name is Required") // Add required validation
                .bind(User::getFirstName, User::setFirstName);

        binder.forField(lname)
                .asRequired("Last name is Required") // Add required validation
                .bind(User::getLastName, User::setLastName);

        binder.forField(username)
                .asRequired("Email is Required") // Add required validation
                .bind(User::getEmail, User::setEmail);

        binder.forField(tel)
                .bind(User::getTel, User::setTel);

        binder.forField(roles)
                .asRequired("Roles is Required") // Add required validation
                .bind(User::getRoles, User::setRoles);

        binder.forField(departments)
                .bind(User::getDepartment, User::setDepartment);

        roles.addSelectionListener(e -> {
            if (e.getValue().contains(Role.HOD)) {
                departments.setVisible(true);
            } else {
                departments.setVisible(false);
            }
        });
        cancel.addClickListener(e -> {
            clearForm();
            // refreshGrid();
            refreshgridUser();
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
                String pass2 = prepareRandomString(6);
                sampleBudget = budget.getValue();
                try {
                    if (this.samplePerson == null) {
                        this.samplePerson = new User();

                        this.samplePerson.setEmailVerificationHash(BCrypt.hashpw(prepareRandomString(30), GlobalConstants.SALT));
                        this.samplePerson.setEmailVerificationAttempts(0);
                        this.samplePerson.setStatus(GlobalConstants.NEW);
                        this.samplePerson.setHashedPassword(BCrypt.hashpw(pass2, GlobalConstants.SALT));

                        // this.samplePerson.setBudget(budget.getValue());
                        newdeterminant++;
                    }
                    binder.writeBean(this.samplePerson);
                    if (samplePersonService.getUsername(this.samplePerson.getEmail()) == true && newdeterminant > 0) {

                        Notification not = Notification.show("Email Exists.");
                        not.addThemeVariants(NotificationVariant.LUMO_ERROR);

                    } else if (emailValidator.ValidateEmail(this.samplePerson.getEmail()) == false) {

                        Notification not = Notification.show("Enter a Valid Email");
                        not.addThemeVariants(NotificationVariant.LUMO_ERROR);

                    } else {
                        /*                        if (!unitsList.isEmpty()) {
                        this.samplePerson.setUnits(unitsList.getValue());
                        }*/

                        this.samplePerson.setDeptsection(sections.getValue());
                        samplePersonService.update(this.samplePerson);

                        String[] email = new String[1];
                        email[0] = this.samplePerson.getEmail();
                        if (newdeterminant > 0) {

                            String message = emailSender.sendEmail("URC Budget ToolLTS", "URC BudgetTool Welcomes You, " + pass2 + " is your temporary password", email);
                            if (message.length() > 23) {
                                Notification not = Notification.show("User email notification failed Reason: " + message);
                                not.addThemeVariants(NotificationVariant.LUMO_ERROR);
                            } else {
                                Notification not = Notification.show("User detailed Registered and password sent to his/her email");
                                not.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                            }
                            newdeterminant = 0;
                        }

                        refreshgridUser();
                        clearForm();
                        //refreshGrid();
                        // Notification not = Notification.show("User details stored.");
                        // not.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                    }

                    UI.getCurrent().navigate(UserView.class);
                } catch (Exception validationException) {
                    // Notification.show(" An exception happened while trying to store the User details. ");
                    Notificationerror(" An exception happened while trying to store the User details. " + validationException.getMessage());
                }
            }

        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> samplePersonId = event.getRouteParameters().get(SAMPLEPERSON_ID).map(Integer::parseInt);
        if (samplePersonId.isPresent()) {
            Optional<User> samplePersonFromBackend = samplePersonService.get(samplePersonId.get());
            if (samplePersonFromBackend.isPresent()) {
                populateForm(samplePersonFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested samplePerson was not found, ID = %d", samplePersonId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshgridUser();
                // refreshGrid();
                event.forwardTo(UserView.class);
            }
        }
    }

    private Div createBudgetSelectLayout() {
        budget = new ComboBox("Select Budget");
        budget.setItemLabelGenerator(Budget::getFinancialYear);

        Div div = new Div();
        div.add(budget);

        budget.setItems(query -> sampleBudgetService.list(PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query))).stream());
        budget.addValueChangeListener(ev -> {

            refreshgridUser();
            sampleBudget = ev.getValue();
            if (!ev.getValue().isActive()) {
                save.setEnabled(false);
            }
        });
        return div;
    }

    private void refreshgridUser() {
        if (budget != null && !budget.isEmpty()) {
            grid.setItems(query -> samplePersonService.list(
                    PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                    .stream());
            //grid.setItems(budget.getValue().getUsers());  
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

        roles = new MultiSelectComboBox("Roles");
        roles.setHelperText("Role is Required");
        roles.setRequired(true);
        roles.setRequiredIndicatorVisible(true);
        roles.setErrorMessage("Required");

        // Populate combo with roles
        roles.setItems(Role.ADMIN, Role.USER, Role.BLO, Role.HR, Role.FREIGHT, Role.HOD, Role.PROCUREMENT, Role.MD,Role.CFO);
        formLayout.add(fname, lname, username, tel, roles, departments, sections);

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
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button but = new Button("Refresh Sections");
        but.addSingleClickListener(e -> {
            sampleDataDuplicationService.duplicateData();
            sections.setItems(query -> sampleUrcDeptSectionAnlDimbgtService.findByANL_CODEStartingWithD2(PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query))).stream());
        });
        Button importUsers = new Button("Import Users");
        importUsers.addClickListener(e -> {
            List<UrcUser> userList = urcUserService.getAllUsers();
            for (UrcUser us : userList) {
                boolean userget = samplePersonService.getUsername(us.getEmail());
                if (userget == false) {
                    User user = new User();
                    user.setFirstName(us.getFirstName());
                    user.setLastName(us.getLastName());
                    user.setTel(us.getTel());
                    user.setEmailVerificationHash(us.getEmailVerificationHash());
                    user.setHashedPassword(us.getPassword());
                    user.setStatus(us.getStatus());
                    user.setEmail(us.getEmail());

                    Set<Role> role = new HashSet<>();
                    role.add(Role.USER);
                    user.setRoles(role);
                    List<UserUnits> list = UserUnitsService.findByUserId(us.getUserId());
                    // List<String> sects = new ArrayList<>();

                    Set<UrcDeptSectionAnlDimbgt> uniqueSections = new HashSet<>();
                    List<String> sects = new ArrayList<>();
                    for (UserUnits b : list) {
                        System.out.println(b.getUnitId() + " Unit Id");
                        Optional<DepartmentUnit> unitOptional = departmentUnitService.getUnitById2(b.getUnitId());

                        if (unitOptional.isPresent()) {
                            DepartmentUnit unit = unitOptional.get();
                            String sunCode = unit.getSunSectCode();
                            sects.add(sunCode);
                            System.out.println(sunCode + " Section codes");
                        } else {
                            System.out.println("Unit not found for ID: " + b.getUnitId());
                        }
                    }

                    Set<String> uniqueSects = new HashSet<>(sects);
                    if (!uniqueSects.isEmpty()) {
                        for (String c : uniqueSects) {
                            UrcDeptSectionAnlDimbgt sections = sampleUrcDeptSectionAnlDimbgtService.findByANL_CODE(c);
                            uniqueSections.add(sections);
                        }
                        user.setDeptsection(uniqueSections);
                        System.out.println(uniqueSections.size());
                    }

                    samplePersonService.update(user);
                    refreshgridUser();

                }

            }
        });
        buttonLayout.add(save, cancel);
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            Set<Role> roles = user.getRoles();
            if (roles.contains(Role.ADMIN)) {
                // buttonLayout.add(but);
                //buttonLayout.add(but, importUsers);
            }
        }
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
        sections.clear();
        populateForm(null);
        budget.setValue(sampleBudget);
        refreshgridUser();
        //unitsList.setEnabled(false);
    }

    private void populateForm(User value) {
        this.samplePerson = value;
        binder.readBean(this.samplePerson);

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

            // samplePerson.setUnitsbudget(unitsList.getValue());
            samplePerson.setUnits(unitsList.getValue());
            sampleUnitsBudget = sampleUnitsBudgetService.listbyBudget(sampleBudget, samplePerson);
            if (!unitsList.getValue().isEmpty()) {
                if (sampleUnitsBudget == null) {
                    sampleUnitsBudget = new UnitsBudget();
                    sampleUnitsBudget.setBudget(sampleBudget);
                    sampleUnitsBudget.setUser(samplePerson);
                    //bud.setUnits(samplePersonService.findUnitsByUserAndDepartmentBudget(samplePerson, sampleBudget));  
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

    private class PersonContextMenu extends GridContextMenu<User> {

        public PersonContextMenu(Grid<User> target) {
            super(target);

            addItem("Edit", e -> e.getItem().ifPresent(person -> {
                // System.out.printf("Edit: %s%n", person.getFullName());
            }));
            addItem("Delete", e -> e.getItem().ifPresent(person -> {
                // System.out.printf("Delete: %s%n", person.getFullName());
            }));
            addItem("Add Department Units", e -> e.getItem().ifPresent(person -> {
                // System.out.printf("Delete: %s%n", person.getFullName());
                List<D_Unit> bug = sampleUnitsBudgetService.listUnitsbyBudget(sampleBudget, samplePerson);
                unitsList = new MultiSelectComboBox("Units");
                //unitsList.setItemLabelGenerator(unitsBudget -> unitsBudget.getUnits().getUnit());
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
