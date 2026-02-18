package com.methaltech.application.views;

import com.methaltech.application.data.BCrypt;
import com.methaltech.application.data.EmailSender;
import com.methaltech.application.data.EmailValidator;
import com.methaltech.application.data.GlobalConstants;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.security.AuthenticatedUser;
import com.methaltech.application.views.BudgetCeiling.BudgetCeilingView;
import com.methaltech.application.views.UnitMeasures.UnitsMeasureView;
import com.methaltech.application.views.actual.ActualByActivityView;
import com.methaltech.application.views.actual.ActualView;
import com.methaltech.application.views.actual.PerformanceView;
import com.methaltech.application.views.approvals.ApprovalView;
import com.methaltech.application.views.budget.BLODashboardView;
import com.methaltech.application.views.budget.BudgetFormView;
import com.methaltech.application.views.budget.BudgetView;
import com.methaltech.application.views.budget.DashboardView;
import com.methaltech.application.views.budget.OrganisationCOAManagementView;
import com.methaltech.application.views.budgetReport.BudgetReportsView;
import com.methaltech.application.views.budgetReport.PhysicalFinancialPerformanceView;
import com.methaltech.application.views.budgetcontrol.BudgetControlView;
import com.methaltech.application.views.currency.currencyView;
import com.methaltech.application.views.freight.freightVolumeView;
import com.methaltech.application.views.procurementplan.ProcurementCOASettingView;
import com.methaltech.application.views.procurementplan.ProcurementPlanView;
import com.methaltech.application.views.requisition.BLORequisitionView;
import com.methaltech.application.views.salary.staffSalaryView;
import com.methaltech.application.views.structure.structureView;
import com.methaltech.application.views.users.SystemSettingsView;
import com.methaltech.application.views.users.UserView;
import com.methaltech.application.views.workplan.budgetWorkplanView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
@SpringComponent
@UIScope
public class MainLayout extends AppLayout {

    private H2 viewTitle;
    private Span viewSubtitle;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    private final PasswordField pass1 = new PasswordField();
    private final PasswordField pass2 = new PasswordField();
    private final PasswordField pass3 = new PasswordField();
    private Icon checkIcon;
    private Span passwordStrengthText;
    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final User user_current = new User();

    private final EmailValidator emailValidator;

    private EmailSender emailSender;

    private final UserService samplePersonService;
    Avatar avatar;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker, EmailValidator emailValidator, UserService samplePersonService) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;
        this.emailValidator = emailValidator;
        this.samplePersonService = samplePersonService;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
        addClassName("main-contentcss");
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");
        toggle.addClassName("professional-drawer-toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        viewTitle.addClassName("professional-view-title");

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        Image image = new Image("images/urcbg.png", "Logo");
        H1 appName = new H1("BUDGET");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        appName.addClassName("professional-app-name");
        Header header = new Header(image, appName);
        header.addClassName("professional-drawer-header");

        Image image2 = new Image("images/ugflagstrip2.png", "Strip");
        image2.setWidthFull();
        Header header2 = new Header(image2);
        header2.setWidthFull();

        Scroller scroller = new Scroller(createNavigation());
        scroller.addClassName("professional-navigation-scroller");

        addToDrawer(header, header2, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();
        nav.addClassName("professional-side-nav");

        if (accessChecker.hasAccess(DashboardView.class)) {
            SideNavItem item = new SideNavItem("Admin Dashboard", DashboardView.class, VaadinIcon.DASHBOARD.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }

        if (accessChecker.hasAccess(BLODashboardView.class)) {
            SideNavItem item = new SideNavItem("Dashboard", BLODashboardView.class, VaadinIcon.DASHBOARD.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }

        SideNavItem userManagementHeader = new SideNavItem("Budget Planning");
        userManagementHeader.setClassName("menu-header");
        nav.addItem(userManagementHeader);
        if (accessChecker.hasAccess(NdpPlanManagementView.class)) {
            SideNavItem item = new SideNavItem("NDP Alignment", NdpPlanManagementView.class, VaadinIcon.CLIPBOARD_CHECK.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }
        if (accessChecker.hasAccess(UrcProgrammesView.class)) {
            SideNavItem item = new SideNavItem("URC Programmes", UrcProgrammesView.class, VaadinIcon.CUBE.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }

        if (accessChecker.hasAccess(BudgetCeilingView.class)) {
            SideNavItem item = new SideNavItem("Budget Ceiling", BudgetCeilingView.class, VaadinIcon.TRENDING_UP.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }

        SideNavItem budgetingHeader = new SideNavItem("Budgeting");
        budgetingHeader.setClassName("menu-header");
        nav.addItem(budgetingHeader);

        if (accessChecker.hasAccess(BudgetFormView.class)) {
            SideNavItem item = new SideNavItem("Budget Form", BudgetFormView.class, VaadinIcon.EDIT.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }
        if (accessChecker.hasAccess(BudgetControlView.class)) {
            SideNavItem item = new SideNavItem("Budget Controls", BudgetControlView.class, VaadinIcon.SHIELD.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }

        if (accessChecker.hasAccess(freightVolumeView.class)) {
            SideNavItem item = new SideNavItem("Freight Volume Reports", freightVolumeView.class, VaadinIcon.TRUCK.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }

        if (accessChecker.hasAccess(staffSalaryView.class)) {
            SideNavItem item = new SideNavItem("Human Resource", staffSalaryView.class, VaadinIcon.USER_HEART.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }

        if (accessChecker.hasAccess(BLORequisitionView.class)) {
            SideNavItem item = new SideNavItem("Requisitions", BLORequisitionView.class, VaadinIcon.CART.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }

        SideNavItem budgetReportHeader = new SideNavItem("Budget Reports");
        budgetReportHeader.setClassName("menu-header");
        nav.addItem(budgetReportHeader);

        if (accessChecker.hasAccess(budgetWorkplanView.class)) {
            SideNavItem item = new SideNavItem("Work plans", budgetWorkplanView.class, VaadinIcon.CALENDAR.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }

        if (accessChecker.hasAccess(ProcurementPlanView.class)) {
            SideNavItem item = new SideNavItem("Procurement Plan", ProcurementPlanView.class, VaadinIcon.PACKAGE.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }

        if (accessChecker.hasAccess(BudgetReportsView.class)) {
            SideNavItem item = new SideNavItem("Budget Reports", BudgetReportsView.class, VaadinIcon.BAR_CHART.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }

        SideNavItem approvalReportHeader = new SideNavItem("Budget Approvals");
        approvalReportHeader.setClassName("menu-header");
        nav.addItem(approvalReportHeader);

        if (accessChecker.hasAccess(ApprovalView.class)) {
            SideNavItem item = new SideNavItem("Budget Approvals", ApprovalView.class, VaadinIcon.CHECK_CIRCLE.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }

        SideNavItem performanceReportHeader = new SideNavItem("Performance Reports");
        performanceReportHeader.setClassName("menu-header");
        nav.addItem(performanceReportHeader);
        if (accessChecker.hasAccess(PhysicalFinancialPerformanceView.class)) {
            SideNavItem item = new SideNavItem("Performance Report N", PhysicalFinancialPerformanceView.class, VaadinIcon.AREA_SELECT.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }
        if (accessChecker.hasAccess(ActualView.class)) {
            SideNavItem item = new SideNavItem("Performance Report O", ActualView.class, VaadinIcon.TRENDING_UP.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }

        if (accessChecker.hasAccess(PerformanceView.class)) {
            SideNavItem item = new SideNavItem("Analysis Report", PerformanceView.class, VaadinIcon.CHART.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }

        if (accessChecker.hasAccess(ActualByActivityView.class)) {
            SideNavItem item = new SideNavItem("Analysis-Activity Report", ActualByActivityView.class, VaadinIcon.CHART_GRID.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }

        SideNavItem userManagementHeader2 = new SideNavItem("Setting");
        userManagementHeader2.setClassName("menu-header");
        nav.addItem(userManagementHeader2);

        if (accessChecker.hasAccess(UserView.class)) {
            SideNavItem item = new SideNavItem("System Users", UserView.class, VaadinIcon.USERS.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }

        if (accessChecker.hasAccess(currencyView.class)) {
            SideNavItem item = new SideNavItem("Currency Exchange Rates", currencyView.class, VaadinIcon.MONEY_DEPOSIT.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }

        if (accessChecker.hasAccess(UnitsMeasureView.class)) {
            SideNavItem item = new SideNavItem("Units of Measure", UnitsMeasureView.class, VaadinIcon.SCALE.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }

        if (accessChecker.hasAccess(structureView.class)) {
            SideNavItem item = new SideNavItem("URC Cost Centres", structureView.class, VaadinIcon.BUILDING.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }

        if (accessChecker.hasAccess(ProcurementCOASettingView.class)) {
            SideNavItem item = new SideNavItem("Accounts Settings", ProcurementCOASettingView.class, VaadinIcon.COG.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }

        if (accessChecker.hasAccess(OrganisationCOAManagementView.class)) {
            SideNavItem item = new SideNavItem("Accounts-Revenue", OrganisationCOAManagementView.class, VaadinIcon.CONNECT.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }

        if (accessChecker.hasAccess(BudgetView.class)) {
            SideNavItem item = new SideNavItem("Set Budget", BudgetView.class, VaadinIcon.WALLET.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }
        if (accessChecker.hasAccess(SystemSettingsView.class)) {
            SideNavItem item = new SideNavItem("System Settings", SystemSettingsView.class, VaadinIcon.COG.create());
            item.addClassName("menu-item");
            nav.addItem(item);
        }

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            avatar = new Avatar(user.getFirstName());
            if (user.getProfilePicture() != null) {
                StreamResource resource = new StreamResource("profile-pic",
                        () -> new ByteArrayInputStream(user.getProfilePicture()));
                avatar.setImageResource(resource);
            }
            user.equals(user_current);

            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");

            Div div = new Div();
            div.addClassName("profileMenu");
            div.add(avatar);
            div.add(user.getFirstName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            SubMenu shareSubMenu = userName.getSubMenu();
            createIconItem(shareSubMenu, VaadinIcon.SIGN_OUT, "Sign out", null, true).addClickListener(e -> {
                authenticatedUser.logout();
            });

            userName.getSubMenu().add(new Hr());

            createIconItem(shareSubMenu, VaadinIcon.EDIT, "Change Password", null, true).addClickListener(e -> {
                NotificationDialogueChangePass(pass1, pass2, pass3);
            });
            userName.getSubMenu().add(new Hr());
            MultiFileMemoryBuffer buffer2 = new MultiFileMemoryBuffer();
            userName.getSubMenu().add(UploadProfilePic(buffer2));

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

    private MenuItem createIconItem(MenuBar menu, VaadinIcon iconName,
            String label, String ariaLabel) {
        return createIconItem(menu, iconName, label, ariaLabel, false);
    }

    private MenuItem createIconItem(MenuBar menu, VaadinIcon iconName,
            String label, String ariaLabel, boolean isChild) {
        Icon icon = new Icon(iconName);

        if (isChild) {
            icon.getStyle().set("width", "var(--lumo-icon-size-s)");
            icon.getStyle().set("height", "var(--lumo-icon-size-s)");
            icon.getStyle().set("marginRight", "var(--lumo-space-s)");
        }

        MenuItem item = menu.addItem(icon, e -> {
        });

        if (ariaLabel != null) {
            item.getElement().setAttribute("aria-label", ariaLabel);
        }

        if (label != null) {
            item.add(new Text(label));
        }

        return item;
    }

    private MenuItem createIconItem(SubMenu menu, VaadinIcon iconName,
            String label, String ariaLabel) {
        return createIconItem(menu, iconName, label, ariaLabel, false);
    }

    private MenuItem createIconItem(SubMenu menu, VaadinIcon iconName,
            String label, String ariaLabel, boolean isChild) {
        Icon icon = new Icon(iconName);

        if (isChild) {
            icon.getStyle().set("width", "var(--lumo-icon-size-s)");
            icon.getStyle().set("height", "var(--lumo-icon-size-s)");
            icon.getStyle().set("marginRight", "var(--lumo-space-s)");
        }

        MenuItem item = menu.addItem(icon, e -> {
        });

        if (ariaLabel != null) {
            item.getElement().setAttribute("aria-label", ariaLabel);
        }

        if (label != null) {
            item.add(new Text(label));
        }

        return item;
    }

    private Upload UploadProfilePic(MultiFileMemoryBuffer buffer) {
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".jpeg", ".jpg", ".png");
        upload.addSucceededListener(event -> {
            String fileName = event.getFileName();
            InputStream inputStream = buffer.getInputStream(fileName);
            try {
                byte[] bytes = IOUtils.toByteArray(inputStream);
                Optional<User> maybeUser = authenticatedUser.get();
                if (maybeUser.isPresent()) {
                    User user = maybeUser.get();
                    user.setProfilePicture(bytes);

                    samplePersonService.update(user);
                    StreamResource resource = new StreamResource("profile-pic",
                            () -> new ByteArrayInputStream(user.getProfilePicture()));
                    avatar.setImageResource(resource);
                }
            } catch (IOException ex) {
                //Logger.getLogger(MainLayout.class.getName()).log(Level.SEVERE, null, ex);
                Notification note = Notification.show("File upload failed Reason: " + ex.getMessage(),
                        5000, Notification.Position.MIDDLE);
            }

            // Do something with the file data
            // processFile(inputStream, fileName);
        });
        return upload;
    }

    private static Button createSubmitButton(Dialog dialog) {
        Button submitButton = new Button("Submit", e -> dialog.close());
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        return submitButton;
    }

    private static VerticalLayout createDialogLayout(PasswordField pass1, PasswordField pass2, PasswordField pass3) {

        pass1.setLabel("Current Password");
        pass1.setClearButtonVisible(true);

        pass2.setLabel("New Password");
        pass2.setClearButtonVisible(true);

        pass3.setLabel("Re-enter new Password");
        pass3.setClearButtonVisible(true);

        VerticalLayout dialogLayout = new VerticalLayout(pass1, pass2, pass3);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }

    private Component NotificationDialogueChangePass(PasswordField pass1, PasswordField pass2, PasswordField pass3) {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Change Password");
        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getHeader().add(closeButton);
        pass1.setLabel("Current Password");
        pass1.setClearButtonVisible(true);
        pass1.setWidthFull();
        pass1.setHelperText("Enter Current Password");
        pass1.setRequired(true);
        pass1.setRequiredIndicatorVisible(true);
        pass1.setErrorMessage("Required");
        pass1.setRevealButtonVisible(false);

        pass2.setLabel("New Password");
        pass2.setClearButtonVisible(true);
        pass2.setWidthFull();
        pass2.setHelperText("Enter New Password");
        pass2.setRequired(true);
        pass2.setRequiredIndicatorVisible(true);
        pass2.setErrorMessage("Required");

        checkIcon = VaadinIcon.CHECK.create();
        checkIcon.setVisible(false);
        checkIcon.getStyle().set("color", "var(--lumo-success-color)");
        pass2.setSuffixComponent(checkIcon);

        Div passwordStrength = new Div();
        passwordStrengthText = new Span();
        passwordStrength.add(new Text("Password strength: "),
                passwordStrengthText);
        pass2.setHelperComponent(passwordStrength);

        pass2.setValueChangeMode(ValueChangeMode.EAGER);
        pass2.addValueChangeListener(e -> {
            String password = e.getValue();
            updateHelper(password);
        });
        pass2.setRevealButtonVisible(false);

        pass3.setLabel("Re-enter new Password");
        pass3.setClearButtonVisible(true);
        pass3.setWidthFull();
        pass3.setHelperText("Re-Enter new Password");
        pass3.setRequired(true);
        pass3.setRequiredIndicatorVisible(true);
        pass3.setErrorMessage("Required");

        VerticalLayout dialogLayout = new VerticalLayout(pass1, pass2, pass3);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");
        //dialog.addThemeVariants(DialogVariant.LUMO_NO_PADDING);
        Div div = new Div();
        createButtonLayout(div, save, cancel);
        cancel.addClickListener(e -> {
            dialog.close();
        });
        save.addClickListener(e -> {
            Optional<User> maybeUser = authenticatedUser.get();
            User use = maybeUser.get();
            if (maybeUser.isPresent()) {
                if (pass1.getValue().isBlank() || pass1.isEmpty()) {

                    Notification not = Notification.show("Enter Current Password");
                    not.addThemeVariants(NotificationVariant.LUMO_ERROR);
                } else if (!BCrypt.hashpw(pass1.getValue(), GlobalConstants.SALT).equals(use.getHashedPassword())) {
                    //System.out.println(BCrypt.hashpw(pass1.getValue(), GlobalConstants.SALT) + " ----" + use.getHashedPassword());
                    Notification not = Notification.show("Wrong Password");
                    Notification not2 = Notification.show(BCrypt.hashpw(pass1.getValue(), GlobalConstants.SALT) + " ----" + use.getHashedPassword());
                    not.addThemeVariants(NotificationVariant.LUMO_ERROR);
                } else if (!pass2.getValue().equals(pass3.getValue())) {
                    Notification not = Notification.show("Passwords do not match");
                    not.addThemeVariants(NotificationVariant.LUMO_ERROR);
                } else {

                    use.setHashedPassword(BCrypt.hashpw(pass2.getValue(), GlobalConstants.SALT));
                    String[] email = new String[1];
                    email[0] = use.getEmail();
                    samplePersonService.update(use);
                    String message = emailSender.sendEmail("URC Budget ToolLTS", "You have changed your password", email);
                    if (message.length() > 23) {
                        Notification not = Notification.show("User email notification failed Reason: " + message);
                        not.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    } else {
                        Notification not = Notification.show("You have changed your password");
                        not.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    }
                    dialog.close();

                }
            }

        });
        dialog.add(dialogLayout, div);
        dialog.open();
        return dialog;
    }

    private void createButtonLayout(Div editorLayoutDiv, Button save, Button cancel) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void updateHelper(String password) {
        if (password.length() > 9 && emailValidator.isValidPassword(password) == true) {
            passwordStrengthText.setText("strong");
            passwordStrengthText.getStyle().set("color",
                    "var(--lumo-success-color)");
            checkIcon.setVisible(true);
            save.setEnabled(true);
        } else if (password.length() > 5 && emailValidator.isValidPassword(password) == true) {
            passwordStrengthText.setText("moderate");
            passwordStrengthText.getStyle().set("color", "#e7c200");
            checkIcon.setVisible(false);
            save.setEnabled(false);
        } else {
            passwordStrengthText.setText("weak");
            passwordStrengthText.getStyle().set("color",
                    "var(--lumo-error-color)");
            checkIcon.setVisible(false);
            save.setEnabled(false);
        }
    }
}
