package com.methaltech.application.views.login;

import com.methaltech.application.data.BCrypt;
import com.methaltech.application.data.EmailSender;
import com.methaltech.application.data.EmailValidator;
import com.methaltech.application.data.GlobalConstants;
import com.methaltech.application.data.bgtool.service.SystemIconService;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.SystemIcon;
import com.methaltech.application.security.AuthenticatedUser;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Random;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
public class LoginView extends LoginOverlay implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;
    @Autowired
    private final EmailValidator emailValidator;

    @Autowired
    private EmailSender emailSender;
    private final UserService samplePersonService;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();
    private final SystemIconService systemIconService;

    public LoginView(AuthenticatedUser authenticatedUser, EmailValidator emailValidator, UserService samplePersonService, SystemIconService systemIconService) {
        this.authenticatedUser = authenticatedUser;
        this.emailValidator = emailValidator;
        this.samplePersonService = samplePersonService;
        this.systemIconService = systemIconService;

        setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));
        addClassNames("login-view");

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header()); // important to avoid NPE
        i18n.getHeader().setDescription("Uganda Railways Corporation");
        i18n.setAdditionalInformation(null);
        setI18n(i18n);

// Put logo ABOVE title (centered)
        setTitle(buildLoginTitle());

        setForgotPasswordButtonVisible(true);
        addForgotPasswordListener(er -> {
            Dialog dialog = new Dialog();
            EmailField email = new EmailField("Email");

            dialog.setHeaderTitle("Enter your Email");

            VerticalLayout dialogLayout = createDialogLayout(email);
            dialog.add(dialogLayout);

            Button saveButton = createSubmitButton(dialog);
            saveButton.addClickListener(ee -> {
                if (email.isEmpty()) {
                    Notification not = Notification.show("EmailField is empty");
                    not.addThemeVariants(NotificationVariant.LUMO_ERROR);
                } else if (emailValidator.ValidateEmail(email.getValue()) == false) {
                    Notification not = Notification.show("Wrong email format");
                    not.addThemeVariants(NotificationVariant.LUMO_ERROR);
                } else {
                    String pass2 = prepareRandomString(6);

                    User user = samplePersonService.getUserByEmail(email.getValue());
                    user.setHashedPassword(BCrypt.hashpw(pass2, GlobalConstants.SALT));
                    samplePersonService.update(user);
                    String[] emailto = new String[1];
                    emailto[0] = email.getValue();
                    String message = emailSender.sendEmail("URC Budget ToolLTS", "Hello User, " + pass2 + " is your temporary password", emailto);
                    if (message.length() > 23) {
                        Notification not = Notification.show("User email notification failed. Reason: " + message);
                        not.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    } else {
                        Notification not = Notification.show("Temporary password sent");
                        not.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    }
                }

            });
            Button cancelButton = new Button("Cancel", e -> dialog.close());
            dialog.getFooter().add(cancelButton);
            dialog.getFooter().add(saveButton);

            dialog.open();

        });

        setOpened(true);
        //<theme-editor-local-classname>
        addClassName("login-view-login-overlay-1");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            // Already logged in
            setOpened(false);
            event.forwardTo("");
        }

        setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }

    private Component NotificationDialogue() {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Enter your Email");
        Button closeButton = new Button(new Icon("lumo", "cross"),
                (e) -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getHeader().add(closeButton);
        EmailField label = new EmailField();
        label.setHeightFull();
        label.setWidthFull();

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

    private static Button createSubmitButton(Dialog dialog) {
        Button submitButton = new Button("Submit", e -> dialog.close());
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        return submitButton;
    }

    private static VerticalLayout createDialogLayout(EmailField email) {

        email.setLabel("Email address");
        email.getElement().setAttribute("name", "email");
        email.setErrorMessage("Enter a valid email address");
        email.setClearButtonVisible(true);
        email.setPattern("[A-Za-z0-9._%+-]{3,}@[a-zA-Z]{3,}([.]{1}[a-zA-Z]{2,}|[.]{1}[a-zA-Z]{2,}[.]{1}[a-zA-Z]{2,})");

        VerticalLayout dialogLayout = new VerticalLayout(email);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }

    public static String prepareRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    private void applySystemLogoToBrand() {
        try {
            SystemIcon icon = systemIconService.getRequired("URC_LOGO");

            StreamResource res = new StreamResource(
                    icon.getFileName(),
                    () -> new ByteArrayInputStream(icon.getData())
            );
            res.setContentType(icon.getContentType());

            StreamRegistration reg = UI.getCurrent()
                    .getSession()
                    .getResourceRegistry()
                    .registerResource(res);

            String url = reg.getResourceUri().toString();

            String css = """
            vaadin-login-overlay.login-view-login-overlay-1::part(brand) {
              background-image: url('%s');
              background-repeat: no-repeat;
              background-position: center;
              background-size: contain;
            }
            """.formatted(url.replace("'", "\\'"));

            UI.getCurrent().getPage().executeJs("""
            const id = 'urc-login-brand-style';
            let s = document.getElementById(id);
            if (!s) { s = document.createElement('style'); s.id = id; document.head.appendChild(s); }
            s.textContent = $0;
        """, css);

            System.out.println("URC_LOGO applied to brand.");

        } catch (Exception ex) {
            System.out.println("URC_LOGO failed: " + ex.getMessage());
        }
    }

    private Component buildLoginTitle() {
        VerticalLayout box = new VerticalLayout();
        box.setPadding(false);
        box.setSpacing(false);
        box.setAlignItems(FlexComponent.Alignment.CENTER);

        // Default title text
        H2 title = new H2("Budget Tool");
        title.getStyle().set("margin", "6px 0 0 0").set("text-align", "center").set("color", "lightgreen");

        // Try DB logo
        try {
            SystemIcon icon = systemIconService.getRequired("URC_LOGO");

            StreamResource res = new StreamResource(
                    icon.getFileName(),
                    () -> new ByteArrayInputStream(icon.getData())
            );
            res.setContentType(icon.getContentType());

            Image logo = new Image(res, "URC Logo");
            logo.setWidth("120px");     // adjust
            logo.getStyle().set("margin", "0").set("padding", "0");

            box.add(logo, title);

        } catch (Exception ex) {
            // If no logo in DB, show just title
            box.add(title);
        }

        return box;
    }

}
