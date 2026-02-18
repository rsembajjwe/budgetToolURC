package com.methaltech.application.views.users;

import com.methaltech.application.data.BCrypt;
import com.methaltech.application.data.EmailSender;
import com.methaltech.application.data.EmailValidator;
import com.methaltech.application.data.GlobalConstants;
import com.methaltech.application.data.Role;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.DataDuplicationService;
import com.methaltech.application.data.bgtool.service.DeptSectionMergerService;
import com.methaltech.application.data.bgtool.service.SystemIconService;
import com.methaltech.application.data.bgtool.service.UnitService;
import com.methaltech.application.data.bgtool.service.UnitsBudgetService;
import com.methaltech.application.data.bgtool.service.UrDepartmentsAnlDimService2;
import com.methaltech.application.data.bgtool.service.UrcDeptSectionAnlDimbgtService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.D_Unit;
import com.methaltech.application.data.entity.bgtool.SystemIcon;
import com.methaltech.application.data.entity.bgtool.UrDepartmentsAnlDim2;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.livedata.service.UrcDeptSectionAnlDimService;
import com.methaltech.application.security.AuthenticatedUser;
import com.methaltech.application.views.MainLayout;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.*;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

/**
 * System Settings View: - Users management (grid + editor) - Search users
 * (name/email/tel) - System icons upload (store in DB via SystemIconService)
 *
 * Notes: - This view assumes UserService exposes list(Pageable) and
 * update(User) and get(id). - For scalable search, add
 * UserService.search(String, Pageable) that hits the DB. A fallback in-memory
 * filter is included. - Icons section assumes SystemIconService + entity exist
 * as previously provided.
 */
@PageTitle("System Settings")
@Route(value = "system-settings", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class SystemSettingsView extends Div implements BeforeEnterObserver {

    // ---------------- Routes (optional if you still want deep links) ----------------
    private static final String USER_ID_PARAM = "userId";

    // ---------------- UI: Tabs & Panels ----------------
    private Tabs tabs;
    private VerticalLayout usersPanel;
    private VerticalLayout iconsPanel;

    // ---------------- UI: Users (Grid + Editor) ----------------
    private final Grid<User> grid = new Grid<>(User.class, false);

    private TextField userSearch;

    private TextField fname;
    private TextField lname;
    private TextField username; // email
    private TextField tel;
    private MultiSelectComboBox<Role> roles;

    private MultiSelectComboBox<UrcDeptSectionAnlDimbgt> sections = new MultiSelectComboBox<>("Sections Responsible for:");
    private MultiSelectComboBox<UrDepartmentsAnlDim2> departments = new MultiSelectComboBox<>("Departments Responsible for:");
    private MultiSelectComboBox<D_Unit> unitsList = new MultiSelectComboBox<>("Units Responsible for:");

    private ComboBox<Budget> budget;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);

    private User selectedUser;
    private Budget selectedBudget;

    // ---------------- Services ----------------
    private final UserService userService;
    private final EmailValidator emailValidator;
    private final BudgetService budgetService;
    private final UnitService unitService;
    private final UnitsBudgetService unitsBudgetService;
    private final DataDuplicationService dataDuplicationService;
    private final UrcDeptSectionAnlDimbgtService urcDeptSectionService;
    private final UrDepartmentsAnlDimService2 departmentsService;
    private final AuthenticatedUser authenticatedUser;
    private final DeptSectionMergerService deptSectionMergerService;

    // Icons
    private final SystemIconService systemIconService;

    @Autowired
    private EmailSender emailSender;

    // ---------------- Constructor ----------------
    @Autowired
    public SystemSettingsView(UserService userService,
            EmailValidator emailValidator,
            BudgetService budgetService,
            UnitService unitService,
            UnitsBudgetService unitsBudgetService,
            UrcDeptSectionAnlDimService sectionService, // not used directly but kept to match your injection style
            DataDuplicationService dataDuplicationService,
            UrcDeptSectionAnlDimbgtService urcDeptSectionService,
            AuthenticatedUser authenticatedUser,
            DeptSectionMergerService deptSectionMergerService,
            UrDepartmentsAnlDimService2 departmentsService,
            SystemIconService systemIconService) {

        this.userService = userService;
        this.emailValidator = emailValidator;
        this.budgetService = budgetService;
        this.unitService = unitService;
        this.unitsBudgetService = unitsBudgetService;
        this.dataDuplicationService = dataDuplicationService;
        this.urcDeptSectionService = urcDeptSectionService;
        this.authenticatedUser = authenticatedUser;
        this.deptSectionMergerService = deptSectionMergerService;
        this.departmentsService = departmentsService;
        this.systemIconService = systemIconService;

        addClassNames("system-settings-view");
        setHeightFull();

        buildShell();
        buildUsersPanel();
        buildIconsPanel();

        // Default selection
        showUsersPanel();

        configureGrid();
        configureEditorBindings();
        refreshGridUsers();
    }

    // ---------------- Shell ----------------
    private void buildShell() {
        VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        root.setPadding(false);
        root.setSpacing(false);

        tabs = new Tabs(new Tab("Users"), new Tab("System Icons"));
        tabs.setWidthFull();
        tabs.addSelectedChangeListener(e -> {
            if (tabs.getSelectedIndex() == 0) {
                showUsersPanel();
            } else {
                showIconsPanel();
            }
        });

        usersPanel = new VerticalLayout();
        usersPanel.setSizeFull();
        usersPanel.setPadding(true);
        usersPanel.setSpacing(true);

        iconsPanel = new VerticalLayout();
        iconsPanel.setSizeFull();
        iconsPanel.setPadding(true);
        iconsPanel.setSpacing(true);
        iconsPanel.setVisible(false);

        root.add(tabs, usersPanel, iconsPanel);
        root.expand(usersPanel, iconsPanel);
        add(root);
    }

    private void showUsersPanel() {
        usersPanel.setVisible(true);
        iconsPanel.setVisible(false);
    }

    private void showIconsPanel() {
        usersPanel.setVisible(false);
        iconsPanel.setVisible(true);
    }

    // ---------------- Users Panel ----------------
    private void buildUsersPanel() {
        usersPanel.removeAll();

        // Optional top strip
        Image strip = new Image("images/ugflagstrip.png", "Strip");
        strip.setWidthFull();
        strip.getStyle().set("margin", "0").set("padding", "0");

        // Search
        userSearch = new TextField();
        userSearch.setPlaceholder("Search by first name, last name, email, phone...");
        userSearch.setPrefixComponent(new Icon("lumo", "search"));
        userSearch.setClearButtonVisible(true);
        userSearch.setWidth("420px");
        userSearch.addValueChangeListener(e -> refreshGridUsers()); // live search

        Button refreshBtn = new Button("Refresh", e -> refreshGridUsers());
        refreshBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout searchBar = new HorizontalLayout(userSearch, refreshBtn);
        searchBar.setAlignItems(FlexComponent.Alignment.END);
        searchBar.setWidthFull();

        // Budget selector
        Component budgetSelect = createBudgetSelectLayout();

        // Split layout
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();
        splitLayout.setSplitterPosition(65);

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        usersPanel.add(strip, searchBar, budgetSelect, splitLayout);
        usersPanel.expand(splitLayout);
    }

    private Component createBudgetSelectLayout() {
        budget = new ComboBox<>("Select Budget");
        budget.setItemLabelGenerator(Budget::getFinancialYear);
        budget.setWidth("320px");

        budget.setItems(query
                -> budgetService.list(PageRequest.of(
                        query.getPage(),
                        query.getPageSize(),
                        VaadinSpringDataHelpers.toSpringDataSort(query)
                )).stream()
        );

        budget.addValueChangeListener(ev -> {
            selectedBudget = ev.getValue();
            refreshGridUsers();
            // Optional: disable save if inactive budget
            if (selectedBudget != null && !selectedBudget.isActive()) {
                save.setEnabled(false);
                Notification.show("Selected budget is inactive. Saving disabled.", 2500, Notification.Position.TOP_END);
            } else {
                save.setEnabled(true);
            }
        });

        return new HorizontalLayout(budget);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        wrapper.setSizeFull();

        grid.setSizeFull();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        wrapper.add(grid);
        splitLayout.addToPrimary(wrapper);
    }

    private void configureGrid() {
        grid.removeAllColumns();
        grid.addColumn(User::getFirstName).setHeader("First Name").setAutoWidth(true);
        grid.addColumn(User::getLastName).setHeader("Last Name").setAutoWidth(true);
        grid.addColumn(User::getEmail).setHeader("Email").setAutoWidth(true);
        grid.addColumn(User::getTel).setHeader("Phone").setAutoWidth(true);
        grid.addColumn(user -> user.getRoles() == null ? "" : user.getRoles().toString())
                .setHeader("Roles").setAutoWidth(true);

        grid.asSingleSelect().addValueChangeListener(event -> {
            User u = event.getValue();
            if (u != null) {
                selectedUser = u;
                username.setReadOnly(true);
                populateForm(u);
                safeSetMulti(sections, u.getDeptsection());
                safeSetMulti(departments, u.getDepartment());
                safeSetMulti(unitsList, u.getUnits());
            } else {
                username.setReadOnly(false);
                clearForm();
                selectedUser = null;
            }
        });

        new UserContextMenu(grid);
    }

    private void refreshGridUsers() {
        if (budget == null || budget.isEmpty()) {
            grid.setItems(List.of());
            return;
        }

        final String q = userSearch == null ? "" : Optional.ofNullable(userSearch.getValue()).orElse("").trim().toLowerCase(Locale.ROOT);

        // Best practice: server-side search. If your service doesn't have it, fallback to filtering current page.
        grid.setItems(query -> fetchUsers(query, q));
    }

    private Stream<User> fetchUsers(Query<User, Void> query, String q) {
        var pageReq = PageRequest.of(
                query.getPage(),
                query.getPageSize(),
                VaadinSpringDataHelpers.toSpringDataSort(query)
        );

        // If you implement a proper DB search:
        // if (!q.isBlank()) return userService.search(q, pageReq).stream();
        // Fallback: fetch page then filter in-memory (works but not perfect for huge data)
        Stream<User> base = userService.list(pageReq).stream();
        if (q.isBlank()) {
            return base;
        }

        return base.filter(u
                -> contains(u.getFirstName(), q)
                || contains(u.getLastName(), q)
                || contains(u.getEmail(), q)
                || contains(u.getTel(), q)
        );
    }

    private boolean contains(String value, String q) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(q);
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("user-editor-layout");
        editorLayoutDiv.setSizeFull();

        Div editorDiv = new Div();
        editorDiv.setClassName("user-editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();

        fname = new TextField("First Name");
        fname.setHelperText("Required");
        fname.setRequired(true);
        fname.setRequiredIndicatorVisible(true);
        fname.setClearButtonVisible(true);

        lname = new TextField("Last Name");
        lname.setHelperText("Required");
        lname.setRequired(true);
        lname.setRequiredIndicatorVisible(true);
        lname.setClearButtonVisible(true);

        username = new TextField("Email");
        username.setHelperText("Required");
        username.setRequired(true);
        username.setRequiredIndicatorVisible(true);
        username.setClearButtonVisible(true);

        tel = new TextField("Phone");
        tel.setClearButtonVisible(true);

        roles = new MultiSelectComboBox<>("Roles");
        roles.setItems(Role.ADMIN, Role.USER, Role.BLO, Role.HR, Role.FREIGHT, Role.HOD, Role.PROCUREMENT, Role.MD, Role.CFO);
        roles.setHelperText("Required");
        roles.setRequired(true);
        roles.setRequiredIndicatorVisible(true);

        // Sections
        sections.setItemLabelGenerator(UrcDeptSectionAnlDimbgt::getNAME);
        sections.setItems(query
                -> urcDeptSectionService.findByANL_CODEStartingWithD2(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query))
                ).stream()
        );

        // Departments
        departments.setItemLabelGenerator(UrDepartmentsAnlDim2::getName);
        departments.setItems(departmentsService.findAll());

        // Units
        unitsList.setItemLabelGenerator(D_Unit::getUnit);
        unitsList.setItems(unitService.findByDepartmentBudget(selectedBudget));

        roles.addSelectionListener(e -> departments.setVisible(e.getValue().contains(Role.HOD)));

        formLayout.add(fname, lname, username, tel, roles, departments, sections, unitsList);

        editorDiv.add(formLayout);
        createUserButtons(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void configureEditorBindings() {
        binder.bindInstanceFields(this);

        binder.forField(fname)
                .asRequired("First name is required")
                .bind(User::getFirstName, User::setFirstName);

        binder.forField(lname)
                .asRequired("Last name is required")
                .bind(User::getLastName, User::setLastName);

        binder.forField(username)
                .asRequired("Email is required")
                .bind(User::getEmail, User::setEmail);

        binder.forField(tel)
                .bind(User::getTel, User::setTel);

        binder.forField(roles)
                .asRequired("Roles are required")
                .bind(User::getRoles, User::setRoles);

        binder.forField(sections)
                .bind(User::getDeptsection, User::setDeptsection);

        binder.forField(departments)
                .bind(User::getDepartment, User::setDepartment);

        binder.forField(unitsList)
                .bind(User::getUnits, User::setUnits);
    }

    private void createUserButtons(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");

        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGridUsers();
        });

        save.addClickListener(e -> onSaveUser());

        Button refreshSections = new Button("Refresh Sections");
        refreshSections.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        refreshSections.addClickListener(e -> {
            dataDuplicationService.duplicateData();
            Notification.show("Sections refreshed", 2000, Notification.Position.TOP_END);
        });

        // Only show admin tools to admins (you already restrict view to ADMIN, but kept here)
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent() && maybeUser.get().getRoles() != null && maybeUser.get().getRoles().contains(Role.ADMIN)) {
            buttonLayout.add(refreshSections);
        }

        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void onSaveUser() {
        if (budget == null || budget.isEmpty()) {
            notificationDialog("Select Budget", "Please select a budget before saving.");
            return;
        }

        StringBuilder missing = validateUserInputs();
        if (missing.length() > 0) {
            notificationDialog("Missing Fields", missing.toString());
            return;
        }

        int newUser = 0;
        String tempPassword = prepareRandomString(6);

        try {
            if (selectedUser == null) {
                selectedUser = new User();
                selectedUser.setEmailVerificationHash(BCrypt.hashpw(prepareRandomString(30), GlobalConstants.SALT));
                selectedUser.setEmailVerificationAttempts(0);
                selectedUser.setStatus(GlobalConstants.NEW);
                selectedUser.setHashedPassword(BCrypt.hashpw(tempPassword, GlobalConstants.SALT));
                newUser = 1;
            }

            binder.writeBean(selectedUser);

            // Basic checks
            if (newUser == 1 && userService.getUsername(selectedUser.getEmail())) {
                notifyError("Email exists.");
                return;
            }

            if (!emailValidator.ValidateEmail(selectedUser.getEmail())) {
                notifyError("Enter a valid Email.");
                return;
            }

            // Persist relationships
            selectedUser.setDeptsection(sections.getValue());
            selectedUser.setDepartment(departments.getValue());
            selectedUser.setUnits(unitsList.getValue());

            userService.update(selectedUser);

            if (newUser == 1) {
                String[] email = new String[]{selectedUser.getEmail()};
                String message = emailSender.sendEmail(
                        "URC Budget ToolLTS",
                        "URC BudgetTool Welcomes You, " + tempPassword + " is your temporary password",
                        email
                );

                if (message != null && message.length() > 23) {
                    notifyError("User email notification failed: " + message);
                } else {
                    notifySuccess("User registered. Password sent to email.");
                }
            } else {
                notifySuccess("User updated.");
            }

            refreshGridUsers();
            clearForm();

        } catch (Exception ex) {
            notifyError("Failed to save: " + ex.getMessage());
        }
    }

    private StringBuilder validateUserInputs() {
        StringBuilder sb = new StringBuilder();
        if (fname.isEmpty()) {
            sb.append("First name is empty\n");
        }
        if (lname.isEmpty()) {
            sb.append("Last name is empty\n");
        }
        if (username.isEmpty()) {
            sb.append("Email is empty\n");
        }
        if (roles.isEmpty()) {
            sb.append("Roles is empty\n");
        }
        return sb;
    }

    private void clearForm() {
        sections.clear();
        departments.clear();
        unitsList.clear();
        populateForm(null);
        refreshGridUsers();
    }

    private void populateForm(User value) {
        selectedUser = value;
        binder.readBean(selectedUser);
        username.setReadOnly(selectedUser != null && selectedUser.getUserId() != null);
    }

    private <T> void safeSetMulti(MultiSelectComboBox<T> box, Set<T> value) {
        if (value == null) {
            box.clear();
        } else {
            box.setValue(value);
        }
    }

    // ---------------- Icons Panel ----------------
    private void buildIconsPanel() {
        iconsPanel.removeAll();

        H3 title = new H3("System Icons");
        title.getStyle().set("margin", "0");

        Paragraph help = new Paragraph("Upload and store system icons (logo, favicon, PDF header logo) in the database.");
        help.getStyle().set("margin", "0 0 10px 0");
        iconsPanel.add(help);

        ComboBox<String> iconKey = new ComboBox<>("Icon Key");
        iconKey.setItems("URC_LOGO", "APP_LOGO", "PDF_HEADER_LOGO", "FAVICON");
        iconKey.setAllowCustomValue(true);
        iconKey.setWidth("320px");

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/png", "image/jpeg", "image/svg+xml");
        upload.setMaxFiles(1);

        Image preview = new Image();
        preview.setAlt("Preview");
        preview.setWidth("240px");

        Button load = new Button("Load Existing", e -> {
            try {
                String key = iconKey.getValue();
                if (key == null || key.isBlank()) {
                    Notification.show("Select an icon key first");
                    return;
                }
                SystemIcon icon = systemIconService.getRequired(key.trim());
                preview.setSrc(toStreamResource(icon));
            } catch (Exception ex) {
                notifyError(ex.getMessage());
            }
        });

        upload.addSucceededListener(ev -> {
            try {
                String key = iconKey.getValue();
                if (key == null || key.isBlank()) {
                    Notification.show("Select an icon key first");
                    return;
                }

                byte[] bytes = buffer.getInputStream().readAllBytes();
                // hard limit 2MB (optional)
                if (bytes.length > 2_000_000) {
                    notifyError("File too large. Max 2MB.");
                    return;
                }

                SystemIcon saved = systemIconService.saveOrUpdate(
                        key.trim(),
                        ev.getFileName(),
                        ev.getMIMEType(),
                        bytes
                );

                preview.setSrc(toStreamResource(saved));
                notifySuccess("Saved icon: " + saved.getIconKey());

            } catch (Exception ex) {
                notifyError("Upload failed: " + ex.getMessage());
            }
        });

        HorizontalLayout top = new HorizontalLayout(iconKey, load);
        top.setAlignItems(FlexComponent.Alignment.END);

        iconsPanel.add(title, help, top, upload, preview);
    }

    private StreamResource toStreamResource(SystemIcon icon) {
        return new StreamResource(icon.getFileName(),
                () -> new ByteArrayInputStream(icon.getData())) {
            public String getContentType() {
                return icon.getContentType();
            }
        };
    }

    // ---------------- BeforeEnter ----------------
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Optional deep link support: /system-settings?userId=...
        Optional<String> idParam = event.getRouteParameters().get(USER_ID_PARAM);
        idParam.ifPresent(idStr -> {
            try {
                Integer id = Integer.parseInt(idStr);
                userService.get(id).ifPresent(this::populateForm);
            } catch (Exception ignored) {
            }
        });
    }

    // ---------------- Utilities: Notifications, Password ----------------
    private void notifySuccess(String msg) {
        Notification n = Notification.show(msg, 3500, Notification.Position.TOP_END);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void notifyError(String msg) {
        Notification n = Notification.show(msg, 5000, Notification.Position.MIDDLE);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private Component notificationDialog(String title, String body) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(title);

        Button close = new Button(new Icon("lumo", "cross"), e -> dialog.close());
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getHeader().add(close);

        TextArea area = new TextArea();
        area.setValue(body);
        area.setReadOnly(true);
        area.setWidthFull();
        area.setHeight("220px");

        dialog.add(new VerticalLayout(area));
        dialog.open();
        return dialog;
    }

    private static final Random random = new Random();
    private static final char[] symbols;

    public static String prepareRandomString(int len) {
        char[] buf = new char[len];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = symbols[random.nextInt(symbols.length)];
        }
        return new String(buf);
    }

    static {
        StringBuilder tmp = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ch++) {
            tmp.append(ch);
        }
        for (char ch = 'a'; ch <= 'z'; ch++) {
            tmp.append(ch);
        }
        symbols = tmp.toString().toCharArray();
    }

    // ---------------- Context Menu ----------------
    private class UserContextMenu extends GridContextMenu<User> {

        public UserContextMenu(Grid<User> target) {
            super(target);

            addItem("Edit", e -> e.getItem().ifPresent(user -> {
                selectedUser = user;
                populateForm(user);
            }));

            addItem("Delete", e -> e.getItem().ifPresent(user -> {
                ConfirmDialog dialog = new ConfirmDialog();
                dialog.setHeader("Delete User");
                dialog.setText("Are you sure you want to delete " + user.getEmail() + "?");
                dialog.setCancelable(true);
                dialog.setConfirmText("Delete");
                dialog.addConfirmListener(ev -> {
                    try {
                        userService.delete(user.getUserId());
                        notifySuccess("Deleted");
                        refreshGridUsers();
                        clearForm();
                    } catch (Exception ex) {
                        notifyError("Delete failed: " + ex.getMessage());
                    }
                });
                dialog.open();
            }));

            addItem("Add Department Units", e -> e.getItem().ifPresent(user -> {
                if (selectedBudget == null) {
                    notifyError("Select a budget first.");
                    return;
                }
                //openUnitsDialog(user);
            }));
        }
    }

    /*    private void openUnitsDialog(User user) {
    Dialog dialog = new Dialog();
    dialog.setHeaderTitle("User Units");
    
    MultiSelectComboBox<D_Unit> units = new MultiSelectComboBox<>("Units");
    units.setItemLabelGenerator(D_Unit::getUnit);
    units.setItems(unitService.findByDepartmentBudget(selectedBudget));
    
    if (user.getUnits() != null) units.setValue(user.getUnits());
    
    Button saveBtn = new Button("Save", e -> {
    try {
    user.setUnits(units.getValue());
    unitsBudgetService.updateOrCreateForUserAndBudget(selectedBudget, user, units.getValue()); // implement or keep your existing logic
    userService.update(user);
    notifySuccess("Units saved");
    dialog.close();
    } catch (Exception ex) {
    notifyError("Failed: " + ex.getMessage());
    }
    });
    saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    
    Button cancelBtn = new Button("Cancel", e -> dialog.close());
    
    dialog.getFooter().add(cancelBtn, saveBtn);
    dialog.add(new VerticalLayout(units));
    dialog.open();
    }*/
}
