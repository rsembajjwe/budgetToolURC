package com.methaltech.application.views.structure;

import com.methaltech.application.data.Role;
import com.methaltech.application.data.bgtool.service.DeptSectionMergerService;
import com.methaltech.application.data.bgtool.service.SectionDeptUnitMergerService;
import com.methaltech.application.data.entity.bgtool.DeptSectionMerger;
import com.methaltech.application.data.entity.bgtool.SectionDeptUnitMerger;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.entity.livedata.UrcDepartmentAnlDim;
import com.methaltech.application.data.entity.livedata.UrcDeptSectionAnlDim;
import com.methaltech.application.data.entity.livedata.UrcDeptUnitAnlDimView;
import com.methaltech.application.data.livedata.service.UrcDepartmentAnlDimService;
import com.methaltech.application.data.livedata.service.UrcDeptSectionAnlDimService;
import com.methaltech.application.data.livedata.service.UrcDeptUnitAnlDimViewService;
import com.methaltech.application.security.AuthenticatedUser;
import com.methaltech.application.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@PageTitle("Structure")
@Route(value = "structure", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "BLO","USER","HOD"})
public class structureView extends Div {

    private Grid<UrcDeptSectionAnlDim> gridSections = new Grid<>(UrcDeptSectionAnlDim.class, false);
    private Grid<UrcDepartmentAnlDim> gridDepartments = new Grid<>(UrcDepartmentAnlDim.class, false);
    private Grid<UrcDeptUnitAnlDimView> gridDept_Unit = new Grid<>(UrcDeptUnitAnlDimView.class, false);

    private final UrcDepartmentAnlDimService sampleUrcAnlCodeService;
    private final UrcDeptSectionAnlDimService sampleSectionService;
    private final DeptSectionMergerService sampleDeptSectionMergerService;
    private final UrcDeptUnitAnlDimViewService sampleUrcDeptUnitAnlDimViewService;
    private final SectionDeptUnitMergerService sampleSectionDeptUnitMergerService;
    private final SplitLayout master;
    private AuthenticatedUser authenticatedUser;

    public structureView(UrcDepartmentAnlDimService sampleUrcAnlCodeService, UrcDeptSectionAnlDimService sampleSectionService,
            DeptSectionMergerService sampleDeptSectionMergerService, AuthenticatedUser authenticatedUser,
            UrcDeptUnitAnlDimViewService sampleUrcDeptUnitAnlDimViewService, SectionDeptUnitMergerService sampleSectionDeptUnitMergerService) {
        this.sampleUrcAnlCodeService = sampleUrcAnlCodeService;
        this.sampleSectionService = sampleSectionService;
        this.sampleDeptSectionMergerService = sampleDeptSectionMergerService;
        this.authenticatedUser = authenticatedUser;
        this.sampleUrcDeptUnitAnlDimViewService = sampleUrcDeptUnitAnlDimViewService;
        this.sampleSectionDeptUnitMergerService = sampleSectionDeptUnitMergerService;
        this.setHeight("100%");
        gridDepartments.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
        gridSections.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        gridDepartments.addColumn("ANL_CODE").setHeader("CODE").setAutoWidth(true);
        gridSections.addColumn("ANL_CODE").setHeader("CODE").setAutoWidth(true);
        gridDepartments.addColumn("NAME").setHeader("DEPARTMENT NAME").setAutoWidth(true);
        gridSections.addColumn("NAME").setHeader("SECTION NAME").setAutoWidth(true);

        gridDept_Unit.addColumn("ANLCODE").setHeader("CODE").setAutoWidth(true);
        gridDept_Unit.addColumn("NAME").setHeader("DEPARTMENT UNIT NAME").setAutoWidth(true);
        gridDept_Unit.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
        gridDept_Unit.setSelectionMode(Grid.SelectionMode.MULTI);
        setDepartment_Units();
        gridDepartments.asSingleSelect().addValueChangeListener(vl -> {
            gridSections.deselectAll();
            UrcDepartmentAnlDim selectedDepartment = vl.getValue();

            if (selectedDepartment != null) {
                String anlCode = selectedDepartment.getANL_CODE();
                if (anlCode != null && !anlCode.isEmpty()) {
                    Optional<DeptSectionMerger> merger = sampleDeptSectionMergerService.findByDeptcodeCustom(anlCode);
                    if (merger.isPresent()) {
                        DeptSectionMerger deptSectionMerger = merger.get();
                        Set<String> sects = deptSectionMerger.getSectioncodes();
                        for (String result : sects) {
                            UrcDeptSectionAnlDim section = sampleSectionService.findByANL_CODE(result);
                            if (section != null) {
                                gridSections.select(section);
                            }
                        }
                    }
                }
            }
        });
        gridSections.setSelectionMode(SelectionMode.MULTI);
        gridSections.asMultiSelect().addValueChangeListener(event -> {
            gridDept_Unit.deselectAll();

            Set<UrcDeptSectionAnlDim> selectedDeptSections = event.getValue(); // Get selected items directly

            for (UrcDeptSectionAnlDim q : selectedDeptSections) {
                String anlCode = q.getANL_CODE();
                if (anlCode != null) {
                    Optional<SectionDeptUnitMerger> merger = sampleSectionDeptUnitMergerService.getMergerBySectioncode(anlCode);
                    merger.ifPresent(mergerData -> {
                        Set<String> sects = mergerData.getDeptUnitcodes();
                        for (String result : sects) {
                            Optional<UrcDeptUnitAnlDimView> section = sampleUrcDeptUnitAnlDimViewService.findByANL_CODE(result);
                            section.ifPresent(sectionData -> {
                                gridDept_Unit.select(sectionData);
                            });
                        }
                    });
                }
            }
        });

        /*        gridSections.asMultiSelect().addValueChangeListener(vl -> {
        gridDept_Unit.deselectAll();
        Set<UrcDeptSectionAnlDim> selectedDept_Units =gridSections.asMultiSelect().getSelectedItems();
        for(UrcDeptSectionAnlDim q:selectedDept_Units){
        Set<String> sects = sampleSectionDeptUnitMergerService.getMergerBySectioncode(q.getANL_CODE()).get().getDeptUnitcodes();
        for (String result : sects) {
        UrcDeptUnitAnlDimView section = sampleUrcDeptUnitAnlDimViewService.findByANL_CODE(result).get();
        if (section != null) {
        gridDept_Unit.select(section);
        Notification.show(result);
        }
        }
        }
        
        });*/
        gridDepartments.setHeight("100%");
        gridSections.setHeight("100%");
        gridSections.setSelectionMode(Grid.SelectionMode.MULTI);
        gridDept_Unit.setSelectionMode(Grid.SelectionMode.MULTI);
        setDepartment();
        setSections();
        SplitLayout lay = new SplitLayout(gridDepartments, gridDept_Unit);
        lay.setOrientation(SplitLayout.Orientation.VERTICAL);
        lay.setSplitterPosition(35);
        lay.setHeight("100%");
        lay.setWidth("100%");
        master = new SplitLayout(lay, new Div(gridSections));
        master.setHeight("100%"); // Set height to 100% to cover the whole view
        master.setWidth("100%");  // Set width to 100% to cover the whole view
        master.setSplitterPosition(50);
        //master.addToPrimary(new Div(gridDepartments));
        // master.addToSecondary(new Div(gridSections));
        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            Set<Role> roles = user.getRoles();
            if (roles.contains(Role.ADMIN)) {
                PersonContextMenu contextMenu = new PersonContextMenu(gridDepartments);
                PersonContextMenu2 contextMenu2 = new PersonContextMenu2(gridSections);
            }
        }
        Image image2 = new Image("images/ugflagstrip.png", "Strip");
        image2.setWidthFull();
        image2.getStyle().set("margin", "0").set("padding", "0");
        add(image2);
        add(master);
    }

    private void setDepartment() {
        gridDepartments.setItems(sampleUrcAnlCodeService.getAllUrcDepartmentAnlDims());
    }

    private void setDepartment_Units() {
        gridDept_Unit.setItems(sampleUrcDeptUnitAnlDimViewService.getAllViews());
    }

    private void setSections() {
        gridSections.setItems(sampleSectionService.getAllUrcSectionsAnlDims());
    }

    public Notification NotificationError(String error) {
        // When creating a notification using the constructor,
        // the duration is 0-sec by default which means that
        // the notification does not close automatically.
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        Div text = new Div(new Text(error));

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

        notification.setPosition(Notification.Position.MIDDLE);

        return notification;
    }

    public Notification Notificationwarning(String warning) {
        // When creating a notification using the constructor,
        // the duration is 0-sec by default which means that
        // the notification does not close automatically.
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

    private class PersonContextMenu extends GridContextMenu<UrcDepartmentAnlDim> {

        public PersonContextMenu(Grid<UrcDepartmentAnlDim> target) {
            super(target);

            addItem("Save", e -> e.getItem().ifPresent(person -> {
                Set<UrcDeptSectionAnlDim> section = gridSections.asMultiSelect().getSelectedItems();
                Set<String> mySet = new HashSet<>();
                for (UrcDeptSectionAnlDim sets : section) {
                    mySet.add(sets.getANL_CODE());
                }
                if (!gridDepartments.asSingleSelect().isEmpty()) {
                    UrcDepartmentAnlDim dept = gridDepartments.asSingleSelect().getValue();

                    // Try to retrieve the DeptSectionMerger using findByDeptcode
                    Optional<DeptSectionMerger> optionalDeptcode = sampleDeptSectionMergerService.findByDeptcode(dept.getANL_CODE());

                    if (optionalDeptcode.isPresent()) {
                        // DeptSectionMerger found, update its values
                        DeptSectionMerger deptcode = optionalDeptcode.get();
                        deptcode.setDeptcode(dept.getANL_CODE());
                        deptcode.setSectioncodes(mySet);
                        sampleDeptSectionMergerService.update(deptcode);
                    } else {
                        // DeptSectionMerger not found, create a new one
                        DeptSectionMerger deptcode = new DeptSectionMerger();
                        deptcode.setDeptcode(dept.getANL_CODE());
                        deptcode.setSectioncodes(mySet);
                        sampleDeptSectionMergerService.update(deptcode); // Assuming you have a save method
                    }
                } else {
                    Notificationwarning("Please select a Department");
                }
            }));

        }
    }

    private class PersonContextMenu2 extends GridContextMenu<UrcDeptSectionAnlDim> {

        public PersonContextMenu2(Grid<UrcDeptSectionAnlDim> target) {
            super(target);

            addItem("Save", e -> e.getItem().ifPresent(person -> {
                Set<UrcDeptUnitAnlDimView> section = gridDept_Unit.asMultiSelect().getSelectedItems();
                Set<String> mySet = new HashSet<>();
                for (UrcDeptUnitAnlDimView sets : section) {
                    mySet.add(sets.getANLCODE());
                }
                if (gridSections.asMultiSelect().getSelectedItems().size() > 1) {
                    Notificationwarning("Please select one section " + gridSections.asMultiSelect().getSelectedItems().size());

                } else {
                    Set<UrcDeptSectionAnlDim> dept = gridSections.asMultiSelect().getValue();
                    for (UrcDeptSectionAnlDim c : dept) {
                        Optional<SectionDeptUnitMerger> optionalDeptcode = sampleSectionDeptUnitMergerService.getMergerBySectioncode(c.getANL_CODE());
                        if (optionalDeptcode.isPresent()) {
                            // DeptSectionMerger found, update its values
                            SectionDeptUnitMerger deptcode = optionalDeptcode.get();
                            deptcode.setSectioncode(c.getANL_CODE());
                            deptcode.setDeptUnitcodes(mySet);
                            sampleSectionDeptUnitMergerService.createMerger(deptcode);
                        } else {
                            // DeptSectionMerger not found, create a new one
                            SectionDeptUnitMerger deptcode = new SectionDeptUnitMerger();
                            deptcode.setSectioncode(c.getANL_CODE());
                            deptcode.setDeptUnitcodes(mySet);
                            sampleSectionDeptUnitMergerService.createMerger(deptcode);
                        }
                    }
                }
            }));

        }
    }
}
