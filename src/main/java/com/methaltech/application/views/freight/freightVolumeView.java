package com.methaltech.application.views.freight;

import com.methaltech.application.data.Display;
import com.methaltech.application.data.MonthlySumResponseFreight;
import com.methaltech.application.data.Report;
import com.methaltech.application.data.UploadExamplesI18N;
import com.methaltech.application.data.bgtool.service.BudgetItemsService;
import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.COAReconcileService;
import com.methaltech.application.data.bgtool.service.CoaService;
import com.methaltech.application.data.bgtool.service.Coalevel11Service;
import com.methaltech.application.data.bgtool.service.Coalevel1Service;
import com.methaltech.application.data.bgtool.service.CurrencyService;
import com.methaltech.application.data.bgtool.service.FreightVolumesService;
import com.methaltech.application.data.bgtool.service.OrganisationService;
import com.methaltech.application.data.bgtool.service.StockUnitMeasureService;
import com.methaltech.application.data.bgtool.service.UserService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.BudgetItems;
import com.methaltech.application.data.entity.bgtool.COA;
import com.methaltech.application.data.entity.bgtool.COAReconcile;
import com.methaltech.application.data.entity.bgtool.Coalevel1;
import com.methaltech.application.data.entity.bgtool.Coalevel11;
import com.methaltech.application.data.entity.bgtool.Currency;
import com.methaltech.application.data.entity.bgtool.FreightVolumes;
import com.methaltech.application.data.entity.bgtool.Organisation;
import com.methaltech.application.data.entity.bgtool.UrcDeptSectionAnlDimbgt;
import com.methaltech.application.data.entity.bgtool.User;
import com.methaltech.application.data.entity.oldbgtool.OldFreightRate;
import com.methaltech.application.data.errorMessages;
import com.methaltech.application.data.oldbgtool.service.OldFreightRateService;
import com.methaltech.application.security.AuthenticatedUser;
import com.methaltech.application.views.MainLayout;
import com.methaltech.application.views.budgetReport.BudgetReportsView;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.server.StreamResource;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;

@PageTitle("Freight Volumes")
@Route(value = "freight", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "FREIGHT", "USER"})
@Uses(Icon.class)
public class freightVolumeView extends Div {

    private final FreightVolumesService sampleFreightVolumesService;
    private final BudgetService sampleBudgetService;
    private final CoaService sampleCoaService;
    private ComboBox<Budget> comboBoxBudget = new ComboBox<>("Budget");
    private ComboBox<COA> comboBoxCOA = new ComboBox<>("Freight Route");
    private Grid<FreightVolumes> gridFreightVolume = new Grid<>(FreightVolumes.class, false);
    private final CurrencyService sampleCurrencyService;
    private final BudgetItemsService budgetItemsService;
    private final StockUnitMeasureService sampleStockUnitMeasureService;
    private final OrganisationService sampleOrganisationService;
    private final BudgetItemsService sampleBudgetItemsService;
    private final OldFreightRateService oldFreightRateService;

    private final Binder<FreightVolumes> binder = new BeanValidationBinder<>(FreightVolumes.class);
    private ComboBox<UrcDeptSectionAnlDimbgt> comboBoxD_Section;

    private FreightVolumes freightVolumes;

    private TextField name = new TextField("Name");
    private TextField cargotype = new TextField("Cargo Type");
    private TextField cargo_desc = new TextField("Cargo Description");
    private BigDecimalField rate = new BigDecimalField("Rate");
    private ComboBox<Currency> currencyComboBox = new ComboBox("Currency");
    private ComboBox<Organisation> comboBoxOrganisation = new ComboBox<>("Budget Type");
    private BigDecimalField jul = new BigDecimalField("Jul");
    private BigDecimalField aug = new BigDecimalField("Aug");
    private BigDecimalField sep = new BigDecimalField("Sep");
    private BigDecimalField oct = new BigDecimalField("Oct");
    private BigDecimalField nov = new BigDecimalField("Nov");
    private BigDecimalField dec = new BigDecimalField("Dec");
    private BigDecimalField jan = new BigDecimalField("Jan");
    private BigDecimalField feb = new BigDecimalField("Feb");
    private BigDecimalField mar = new BigDecimalField("Mar");
    private BigDecimalField apr = new BigDecimalField("Apr");
    private BigDecimalField may = new BigDecimalField("May");
    private BigDecimalField jun = new BigDecimalField("Jun");
    private BigDecimalField total = new BigDecimalField("Total");

    private Button save = new Button("Save");
    private Button cancel = new Button("Cancel");
    Span span = new Span();

    SplitLayout splitLayout = new SplitLayout();
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US); // Use US locale for consistent formatting

    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);
    private AuthenticatedUser authenticatedUser;
    private final UserService userService;
    private User user;
    private final COAReconcileService coaReconcileService;
    private final CoaService coaService;
    private final Coalevel1Service sampleCoalevel1Service;
    private final Coalevel11Service sampleCoalevel11Service;

    public freightVolumeView(AuthenticatedUser authenticatedUser, FreightVolumesService sampleFreightVolumesService, BudgetService sampleBudgetService, CoaService sampleCoaService,
            CurrencyService sampleCurrencyService, BudgetItemsService budgetItemsService, StockUnitMeasureService sampleStockUnitMeasureService,
            OrganisationService sampleOrganisationService, UserService userService, BudgetItemsService sampleBudgetItemsService,
            OldFreightRateService oldFreightRateService, COAReconcileService coaReconcileService, CoaService coaService, Coalevel1Service sampleCoalevel1Service,
            Coalevel11Service sampleCoalevel11Service) {
        this.sampleFreightVolumesService = sampleFreightVolumesService;
        this.sampleBudgetService = sampleBudgetService;
        this.sampleCoaService = sampleCoaService;
        this.sampleCurrencyService = sampleCurrencyService;
        this.budgetItemsService = budgetItemsService;
        this.sampleStockUnitMeasureService = sampleStockUnitMeasureService;
        this.sampleOrganisationService = sampleOrganisationService;
        this.userService = userService;
        this.sampleBudgetItemsService = sampleBudgetItemsService;
        this.oldFreightRateService = oldFreightRateService;
        this.coaReconcileService = coaReconcileService;
        this.coaService = coaService;
        this.sampleCoalevel1Service = sampleCoalevel1Service;
        this.sampleCoalevel11Service = sampleCoalevel11Service;
        setHeight("100%");
        Image image2 = new Image("images/ugflagstrip.png", "Strip");
        image2.setWidthFull();
        image2.getStyle().set("margin", "0").set("padding", "0");
        add(image2);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        user = userService.getUserByEmail(username);
        if (authenticatedUser.get().isPresent()) {
            user = authenticatedUser.get().get();
        }
        comboBoxD_Section = new ComboBox<>("Cost Centre");
        comboBoxD_Section.setItemLabelGenerator(UrcDeptSectionAnlDimbgt::getNAME);
        comboBoxD_Section.setItems(user.getDeptsection());

        comboBoxOrganisation.setItemLabelGenerator(Organisation::getName);
        TabSheet tabSheet = new TabSheet();
        tabSheet.add("View Freight Volume", detailsPanel());
        tabSheet.add("Freight Volume Reports", secondPanel());
        tabSheet.setHeight("100%");

        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(
                // Microsoft Excel (OpenXML, .xlsx)
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                ".xlsx");

        UploadExamplesI18N i18n = new UploadExamplesI18N();
        i18n.getAddFiles().setOne("Upload Spreadsheet...");
        i18n.getDropFiles().setOne("Drop spreadsheet here");
        i18n.getError().setIncorrectFileType(
                "Provide the file in one of the supported formats (.xls, .xlsx, .csv).");
        upload.setI18n(i18n);
        upload.setVisible(false);
        if (!comboBoxBudget.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxCOA.isEmpty()) {

        } else {
            upload.setVisible(false);
        }
        upload.addSucceededListener(event -> {
            if (!comboBoxBudget.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxCOA.isEmpty()) {
                String fileName = event.getFileName();
                InputStream inputStream = buffer.getInputStream(fileName);
                //System.out.println("Uploaded");
                extractFreightRateFYFromCell2(inputStream);
            } else {
                Notification.show("Select all the required parameters");
            }

        });
        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.add(comboBoxBudget, comboBoxCOA, comboBoxD_Section, comboBoxOrganisation, upload);
        add(formLayout);

        binder.forField(name).bind(FreightVolumes::getName, FreightVolumes::setName);
        binder.forField(cargotype).bind(FreightVolumes::getCargotype, FreightVolumes::setCargotype);
        binder.forField(cargo_desc).bind(FreightVolumes::getCargo_desc, FreightVolumes::setCargo_desc);
        binder.forField(rate).bind(FreightVolumes::getRate, FreightVolumes::setRate);
        binder.forField(currencyComboBox).bind(FreightVolumes::getCurrency, FreightVolumes::setCurrency);
        binder.forField(jul).bind(FreightVolumes::getJul, FreightVolumes::setJul);
        binder.forField(aug).bind(FreightVolumes::getAug, FreightVolumes::setAug);
        binder.forField(sep).bind(FreightVolumes::getSep, FreightVolumes::setSep);
        binder.forField(oct).bind(FreightVolumes::getOct, FreightVolumes::setOct);
        binder.forField(nov).bind(FreightVolumes::getNov, FreightVolumes::setNov);
        binder.forField(dec).bind(FreightVolumes::getDec, FreightVolumes::setDec);
        binder.forField(jan).bind(FreightVolumes::getJan, FreightVolumes::setJan);
        binder.forField(feb).bind(FreightVolumes::getFeb, FreightVolumes::setFeb);
        binder.forField(mar).bind(FreightVolumes::getMar, FreightVolumes::setMar);
        binder.forField(apr).bind(FreightVolumes::getApr, FreightVolumes::setApr);
        binder.forField(may).bind(FreightVolumes::getMay, FreightVolumes::setMay);
        binder.forField(jun).bind(FreightVolumes::getJun, FreightVolumes::setJun);
        // binder.forField(comboBoxBudget).bind(FreightVolumes::getBudget, FreightVolumes::setBudget);
        binder.forField(total).bind(FreightVolumes::getTotal, FreightVolumes::setTotal);
        // binder.forField(comboBoxCOA).bind(FreightVolumes::getCoacode, FreightVolumes::setCoacode);
        total.setEnabled(false);
        symbols.setGroupingSeparator(','); // Set the grouping separator to a comma

        comboBoxBudget.addValueChangeListener(e -> {
            if (!e.getValue().isActive()) {
                save.setEnabled(false);
                cancel.setEnabled(false);
                upload.setVisible(false);
            }
            if (!comboBoxBudget.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxCOA.isEmpty()) {
                upload.setVisible(true);
            } else {
                upload.setVisible(false);
            }
        });
        comboBoxD_Section.addValueChangeListener(e -> {
            if (!comboBoxBudget.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxCOA.isEmpty()) {
                upload.setVisible(true);
            } else {
                upload.setVisible(false);
            }
        });
        comboBoxCOA.addValueChangeListener(e -> {
            if (!comboBoxBudget.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxCOA.isEmpty()) {
                upload.setVisible(true);
            } else {
                upload.setVisible(false);
            }
        });
        comboBoxOrganisation.addValueChangeListener(e -> {
            if (!comboBoxBudget.isEmpty() && !comboBoxOrganisation.isEmpty() && !comboBoxD_Section.isEmpty() && !comboBoxCOA.isEmpty()) {
                upload.setVisible(true);
            } else {
                upload.setVisible(false);
            }
        });
        setBudgetCombo();

        setFreightVolumeGridDetails();
        add(tabSheet);

    }

    public void setBudgetCombo() {
        comboBoxBudget.setItems(query -> sampleBudgetService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        comboBoxBudget.setItemLabelGenerator(Budget::getFinancialYear);
        comboBoxBudget.addValueChangeListener(e -> {
            if (!comboBoxBudget.isEmpty()) {
                setCOACombo(comboBoxBudget.getValue());
            }
            if (!comboBoxCOA.isEmpty() && !comboBoxBudget.isEmpty()) {
                setFreightVolumeGrid(comboBoxBudget.getValue(), comboBoxCOA.getValue());
            }
            comboBoxOrganisation.setItems(sampleOrganisationService.findByBudgetList(comboBoxBudget.getValue()));
        });
    }

    public void setCOACombo(Budget budget) {
        comboBoxCOA.setItems(sampleCoaService.findByBudgetAndDisplayAndStateOpen(budget, Display.FREIGHT, true));
        comboBoxCOA.setItemLabelGenerator(COA::getName);
        comboBoxCOA.addValueChangeListener(e -> {
            if (!comboBoxCOA.isEmpty() && !comboBoxBudget.isEmpty()) {
                setFreightVolumeGrid(comboBoxBudget.getValue(), comboBoxCOA.getValue());
            }
        });
    }

    public void setFreightVolumeGrid(Budget budget, COA coacode) {

        gridFreightVolume.setItems(sampleFreightVolumesService.getAllFreightVolumesByBudgetAndCode(budget, coacode));
        span.setText("Total Tonns " + decimalFormat.format(sampleFreightVolumesService.calculateSumOfAllMonthsByBudgetAndCoacode(comboBoxBudget.getValue(), comboBoxCOA.getValue())));
    }

    public void setFreightVolumeGridDetails() {
        currencyComboBox.setItems(query -> sampleCurrencyService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        currencyComboBox.setItemLabelGenerator(item -> item.getData().getCurrencyShort());
        gridFreightVolume.addColumn("name").setHeader("Client").setAutoWidth(true);
        gridFreightVolume.addColumn("cargotype").setHeader("Cargo Type").setAutoWidth(true);
        gridFreightVolume.addColumn("cargo_desc").setHeader("Description").setAutoWidth(true);
        Column<FreightVolumes> rateColumn = gridFreightVolume.addColumn("rate").setHeader("Rate").setAutoWidth(true);
        rateColumn.setRenderer(new NumberRenderer<>(FreightVolumes::getRate, decimalFormat));
        // gridFreightVolume.addColumn("currency").setHeader("Currency").setAutoWidth(true);
        gridFreightVolume.addColumn(new ComponentRenderer<>(urcActivity -> {
            Span span = new Span("");
            if (urcActivity.getCurrency() != null) {
                span = new Span(urcActivity.getCurrency().getData().getCurrencyShort());
            } else {
                span = new Span("");
            }

            return span;
        })).setHeader("Currency").setFlexGrow(0).setWidth("150px");
        Column<FreightVolumes> totalColumn = gridFreightVolume.addColumn("total").setHeader("Total/Tons").setAutoWidth(true);
        totalColumn.setRenderer(new NumberRenderer<>(FreightVolumes::getTotal, decimalFormat));

        gridFreightVolume.asSingleSelect().addValueChangeListener(e -> {
            populateForm(e.getValue());
        });
    }

    public VerticalLayout volumesDetails() {
        VerticalLayout v = new VerticalLayout();
        FormLayout form = new FormLayout();
        form.add(name, cargotype, cargo_desc, rate, currencyComboBox, jul,
                aug, sep, oct, nov, dec, jan, feb, mar,
                apr, may, jun, total);
        HorizontalLayout lay = new HorizontalLayout();
        lay.setAlignItems(FlexComponent.Alignment.BASELINE);
        lay.add(save, cancel);

        // binder.bindInstanceFields(this);
        save.addClickListener(event -> {

            if (!comboBoxD_Section.isEmpty() && !comboBoxBudget.isEmpty() && !comboBoxCOA.isEmpty() && !comboBoxOrganisation.isEmpty()) {
                FreightVolumes freightVolume = gridFreightVolume.asSingleSelect().getValue();
                BudgetItems item;
                if (freightVolume != null) {

                    item = budgetItemsService.getBudgetItemsByAnalcode(freightVolume.getId());

                } else {
                    freightVolume = new FreightVolumes(); // Initialize a new object
                    item = new BudgetItems();
                }
// Update the fields of the freightVolume object with the form data
                binder.writeBeanIfValid(freightVolume);
                freightVolume.setCoacode(comboBoxCOA.getValue());
                freightVolume.setBudget(comboBoxBudget.getValue());
                freightVolume.setTotal(calculateTotalVolume());
                sampleFreightVolumesService.createOrUpdateFreightVolume(freightVolume);
                itemset(item, freightVolume, comboBoxOrganisation.getValue());
                sampleBudgetItemsService.update(item);
                //setCOACombo(comboBoxBudget.getValue());
                setFreightVolumeGrid(comboBoxBudget.getValue(), comboBoxCOA.getValue());
            }

        });
        cancel.addClickListener(event -> cancel());
        v.add(form, lay);
        return v;
    }

    public BudgetItems itemset(BudgetItems item, FreightVolumes freightVolume, Organisation org) {
        item.setItem(freightVolume.getName());
        item.setCost(freightVolume.getRate());
        item.setQty(freightVolume.getTotal());
        item.setUnitMeasure("TON");
        item.setCurrency(freightVolume.getCurrency());
        item.setNotes(freightVolume.getName());
        item.setBudget(freightVolume.getBudget());
        item.setBudgetType(org);
        item.setCoacode(freightVolume.getCoacode());
        item.setBcategory(freightVolume.getCoacode().getCode());
        Coalevel1 coal = sampleCoalevel1Service.findByCode(1);
        item.setCoalevel1(coal);
        item.setDeptUnit(comboBoxD_Section.getValue());
        item.setAnalcode(freightVolume.getId());

        item.setJan(calculateTotal(freightVolume, freightVolume.getJan(), freightVolume.getRate()));
        item.setFeb(calculateTotal(freightVolume, freightVolume.getFeb(), freightVolume.getRate()));
        item.setMar(calculateTotal(freightVolume, freightVolume.getMar(), freightVolume.getRate()));
        item.setApr(calculateTotal(freightVolume, freightVolume.getApr(), freightVolume.getRate()));
        item.setMay(calculateTotal(freightVolume, freightVolume.getMay(), freightVolume.getRate()));
        item.setJun(calculateTotal(freightVolume, freightVolume.getJun(), freightVolume.getRate()));
        item.setJul(calculateTotal(freightVolume, freightVolume.getJul(), freightVolume.getRate()));
        item.setAug(calculateTotal(freightVolume, freightVolume.getAug(), freightVolume.getRate()));
        item.setSep(calculateTotal(freightVolume, freightVolume.getSep(), freightVolume.getRate()));
        item.setOct(calculateTotal(freightVolume, freightVolume.getOct(), freightVolume.getRate()));
        item.setNov(calculateTotal(freightVolume, freightVolume.getNov(), freightVolume.getRate()));
        item.setDec(calculateTotal(freightVolume, freightVolume.getDec(), freightVolume.getRate()));
        return item;
    }

    public BigDecimal calculateTotal(FreightVolumes freightVolume, BigDecimal amount, BigDecimal rate) {
        BigDecimal total = BigDecimal.ZERO;
        if (freightVolume.getCurrency() != null) {
            total = amount.multiply(freightVolume.getCurrency().getRate().multiply(rate));
        }
        return total;
    }

    public void setFreightVolumes(FreightVolumes freightVolumes) {
        this.freightVolumes = freightVolumes;
        binder.setBean(freightVolumes);
    }

    private void cancel() {
        Notification.show("Operation canceled.");
    }

    private VerticalLayout detailsPanel() {
        VerticalLayout lay = new VerticalLayout();
        MenuBar menuBar = new MenuBar();
        menuBar.setOpenOnHover(true);
        MenuItem share = menuBar.addItem("Share");
        SubMenu shareSubMenu = share.getSubMenu();
        shareSubMenu.addItem("Upload Freight Volumes").addThemeNames("custom-theme");
        shareSubMenu.addItem("Download Freight Template");
        shareSubMenu.addItem("Download Freight Volume Details").addClickListener(e -> {
            if (!comboBoxBudget.isEmpty()) {
                exportAndDownloadFreightVolume(comboBoxBudget.getValue());
            }
        });
        shareSubMenu.addItem("Import Freight Volumes").addClickListener(e -> {
            /*            if (!comboBoxBudget.isEmpty()) {
            if (sampleFreightVolumesService.countByBudget(comboBoxBudget.getValue()) == 0) {
            List<OldFreightRate> getFreightRatesByFiscalYea = oldFreightRateService.getFreightRatesByFiscalYear(comboBoxBudget.getValue().getFinancialYear());
            for (OldFreightRate f : getFreightRatesByFiscalYea) {
            FreightVolumes fr = new FreightVolumes();
            fr.setBudget(comboBoxBudget.getValue());
            fr.setName(f.getName());
            fr.setCargo_desc(f.getCargoDescription());
            fr.setCargotype(f.getCargoType());
            fr.setCoacode(setCOA(f.getCode(), comboBoxBudget.getValue()));
            Currency cur = sampleCurrencyService.findByDataCurrencyAndBudget(f.getCurrency(), comboBoxBudget.getValue());
            if (cur != null) {
            fr.setCurrency(cur);
            }
            
            fr.setRate(f.getRate());
            fr.setJan(f.getJan());
            fr.setFeb(f.getFeb());
            fr.setMar(f.getMar());
            fr.setApr(f.getApr());
            fr.setMay(f.getMay());
            fr.setJun(f.getJun());
            fr.setJul(f.getJul());
            fr.setSep(f.getSep());
            fr.setAug(f.getAug());
            fr.setOct(f.getOct());
            fr.setNov(f.getNov());
            fr.setDec(f.getDec());
            sampleFreightVolumesService.createOrUpdateFreightVolume(fr);
            
            }
            } else {
            warningNotification("Items found");
            }
            
            } else {
            warningNotification("Select Budget");
            }*/
        });

        /*        formLayout.setResponsiveSteps(
        // Use one column by default
        new ResponsiveStep("0", 2),
        // Use two columns, if the layout's width exceeds 320px
        new ResponsiveStep("320px", 3),
        // Use three columns, if the layout's width exceeds 500px
        new ResponsiveStep("500px", 4));*/
        splitLayout.setSplitterPosition(50);
        lay.setHeight("100%");
        splitLayout.setHeight("100%");
        gridFreightVolume.setHeight("100%");
        Footer footer = new Footer();
        footer.getElement().getStyle().set("margin-left", "auto");
        span.getElement().getStyle().set("margin-left", "auto");
        span.setWidth("200px");
        footer.getElement().getThemeList().add("badge success");
        footer.add(span);

        // Make the SplitLayout components take up the remaining space
        splitLayout.addToPrimary(gridFreightVolume, footer);
        splitLayout.addToSecondary(volumesDetails());
        lay.add(menuBar, splitLayout);

        return lay;
    }

    private VerticalLayout secondPanel() {
        VerticalLayout lay = new VerticalLayout();
        lay.add(new H1(""));
        return lay;
    }

    private void populateForm(FreightVolumes value) {
        this.freightVolumes = value;
        binder.readBean(this.freightVolumes);

    }

    public BigDecimal calculateTotalVolume() {
        BigDecimal total = BigDecimal.ZERO;
        total = total.add(jan.getValue() != null ? jan.getValue() : BigDecimal.ZERO);
        total = total.add(feb.getValue() != null ? feb.getValue() : BigDecimal.ZERO);
        total = total.add(mar.getValue() != null ? mar.getValue() : BigDecimal.ZERO);
        total = total.add(apr.getValue() != null ? apr.getValue() : BigDecimal.ZERO);
        total = total.add(may.getValue() != null ? may.getValue() : BigDecimal.ZERO);
        total = total.add(jun.getValue() != null ? jun.getValue() : BigDecimal.ZERO);
        total = total.add(jul.getValue() != null ? jul.getValue() : BigDecimal.ZERO);
        total = total.add(aug.getValue() != null ? aug.getValue() : BigDecimal.ZERO);
        total = total.add(sep.getValue() != null ? sep.getValue() : BigDecimal.ZERO);
        total = total.add(oct.getValue() != null ? oct.getValue() : BigDecimal.ZERO);
        total = total.add(nov.getValue() != null ? nov.getValue() : BigDecimal.ZERO);
        total = total.add(dec.getValue() != null ? dec.getValue() : BigDecimal.ZERO);
        return total;
    }

    public BigDecimal calculateTotalTons() {
        BigDecimal total = BigDecimal.ZERO;

        return total;
    }

    public Notification warningNotification(String error) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);

        Div text = new Div(new Text(error), new HtmlComponent("br"), new Text("Close this warning to continue working."));

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.setAriaLabel("Close");
        closeButton.addClickListener(event -> {
            notification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.open();
        return notification;
    }

    public COA setCOA(String codez, Budget budget) {
        COA coa = null;
        List<COAReconcile> code = coaReconcileService.findCOAReconcileByOldcoa(codez);
        for (COAReconcile b : code) {
            //System.out.println(b.getNewcoa() + ": Item new " + b.getOldcoa() + ": Old Item");
            coa = coaService.findByCodeAndBudget(b.getNewcoa(), budget);
            // System.out.println(coa.getCode() + " Item new");
        }

        return coa;
    }

    public Notification warningNotification(List<errorMessages> messages) {
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
        Grid<errorMessages> errors = new Grid<>(errorMessages.class, false);
        errors.addColumn(errorMessages::getRow).setHeader("Row");
        errors.addColumn(errorMessages::getMessage).setHeader("Warning");
        errors.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT, GridVariant.LUMO_ROW_STRIPES);
        errors.getStyle().set("background-color", "#ffcc00");

        errors.setItems(messages);
        Div text = new Div(
                errors,
                new HtmlComponent("br"),
                new Text("Close this warning to continue working.")
        );

        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.setAriaLabel("Close");
        closeButton.addClickListener(event -> {
            notification.close();
        });

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.open();
        return notification;
    }

    public void extractFreightRateFYFromCell2(InputStream inputStream) {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            int i = 0;
            List<errorMessages> messages = new ArrayList<>();
            List<FreightVolumes> listFreightVolumes = new ArrayList();
            for (Row row : sheet) {
                i++;
                if (i > 1) {
                    FreightVolumes info = new FreightVolumes();
                    BudgetItems budget = new BudgetItems();

                    handleCell(row, messages, info, i, 0, "Null Client Value", (code) -> {
                        code.setCellType(CellType.STRING);
                        info.setName(code.getStringCellValue());
                        info.setBudget(comboBoxBudget.getValue());
                        info.setCoacode(comboBoxCOA.getValue());
                    });

                    handleCell(row, messages, info, i, 1, "Null Cargo Type Value", (cargot) -> {
                        info.setCargotype(cargot.getStringCellValue());
                    });

                    handleCell(row, messages, info, i, 2, "Null Cargo Description Value", (cargod) -> {
                        info.setCargo_desc(cargod.getStringCellValue());
                    });
                    //handle null rate cell
                    handleCell(row, messages, info, i, 3, "Null Rate Value", (rate) -> {
                    });
                    //handle null currency cell
                    handleCell(row, messages, info, i, 4, "Null Currency Value", (curr) -> {
                    });
                    handleCell(row, messages, info, i, 4, "Null Currency Value", (curr) -> {
                    });
                    handleCurrencyCell(row, messages, info, i, 4, "Invalid Currency Value");

                    handleNumericCell(row, messages, info, i, 3, "Use Numeric Value", (rate) -> {
                        Cell cell = row.getCell(3);
                        BigDecimal test = BigDecimal.ZERO;
                        if (cell != null) {
                            cell.setCellType(CellType.STRING);

                            try {
                                info.setRate(new BigDecimal(rate.getStringCellValue()));

                            } catch (NumberFormatException ex) {
                            }
                        } else {
                            info.setRate(test);
                        }

                    });

                    //handleCurrencyCell(row, messages, info, i, 4, "Use Right Currency");
                    handleNumericCell(row, messages, info, i, 5, "Use Numeric Value", (jul) -> {
                        Cell cell = row.getCell(3);
                        BigDecimal test = BigDecimal.ZERO;
                        if (cell != null) {
                            cell.setCellType(CellType.STRING);

                            try {
                                info.setJul(new BigDecimal(jul.getStringCellValue()));

                            } catch (NumberFormatException ex) {
                            }
                        } else {
                            info.setJul(test);
                        }

                    });
                    handleNumericCell(row, messages, info, i, 6, "Use Numeric Value", (aug) -> {
                        Cell cell = row.getCell(3);
                        BigDecimal test = BigDecimal.ZERO;
                        if (cell != null) {
                            cell.setCellType(CellType.STRING);

                            try {
                                info.setAug(new BigDecimal(aug.getStringCellValue()));

                            } catch (NumberFormatException ex) {
                            }
                        } else {
                            info.setAug(test);
                        }

                    });
                    handleNumericCell(row, messages, info, i, 7, "Use Numeric Value", (sep) -> {
                        Cell cell = row.getCell(3);
                        BigDecimal test = BigDecimal.ZERO;
                        if (cell != null) {
                            cell.setCellType(CellType.STRING);

                            try {
                                info.setSep(new BigDecimal(sep.getStringCellValue()));

                            } catch (NumberFormatException ex) {
                            }
                        } else {
                            info.setSep(test);
                        }

                    });
                    handleNumericCell(row, messages, info, i, 8, "Use Numeric Value", (oct) -> {
                        Cell cell = row.getCell(3);
                        BigDecimal test = BigDecimal.ZERO;
                        if (cell != null) {
                            cell.setCellType(CellType.STRING);

                            try {
                                info.setOct(new BigDecimal(oct.getStringCellValue()));

                            } catch (NumberFormatException ex) {
                            }
                        } else {
                            info.setOct(test);
                        }

                    });
                    handleNumericCell(row, messages, info, i, 9, "Use Numeric Value", (nov) -> {
                        Cell cell = row.getCell(3);
                        BigDecimal test = BigDecimal.ZERO;
                        if (cell != null) {
                            cell.setCellType(CellType.STRING);

                            try {
                                info.setNov(new BigDecimal(nov.getStringCellValue()));

                            } catch (NumberFormatException ex) {
                            }
                        } else {
                            info.setNov(test);
                        }

                    });
                    handleNumericCell(row, messages, info, i, 10, "Use Numeric Value", (dec) -> {
                        Cell cell = row.getCell(3);
                        BigDecimal test = BigDecimal.ZERO;
                        if (cell != null) {
                            cell.setCellType(CellType.STRING);

                            try {
                                info.setDec(new BigDecimal(dec.getStringCellValue()));

                            } catch (NumberFormatException ex) {
                            }
                        } else {
                            info.setDec(test);
                        }

                    });
                    handleNumericCell(row, messages, info, i, 11, "Use Numeric Value", (jan) -> {
                        Cell cell = row.getCell(3);
                        BigDecimal test = BigDecimal.ZERO;
                        if (cell != null) {
                            cell.setCellType(CellType.STRING);

                            try {
                                info.setJan(new BigDecimal(jan.getStringCellValue()));

                            } catch (NumberFormatException ex) {
                            }
                        } else {
                            info.setJan(test);
                        }

                    });
                    handleNumericCell(row, messages, info, i, 12, "Use Numeric Value", (feb) -> {
                        Cell cell = row.getCell(3);
                        BigDecimal test = BigDecimal.ZERO;
                        if (cell != null) {
                            cell.setCellType(CellType.STRING);

                            try {
                                info.setFeb(new BigDecimal(feb.getStringCellValue()));

                            } catch (NumberFormatException ex) {
                            }
                        } else {
                            info.setFeb(test);
                        }

                    });
                    handleNumericCell(row, messages, info, i, 13, "Use Numeric Value", (mar) -> {
                        Cell cell = row.getCell(3);
                        BigDecimal test = BigDecimal.ZERO;
                        if (cell != null) {
                            cell.setCellType(CellType.STRING);

                            try {
                                info.setMar(new BigDecimal(mar.getStringCellValue()));

                            } catch (NumberFormatException ex) {
                            }
                        } else {
                            info.setMar(test);
                        }

                    });
                    handleNumericCell(row, messages, info, i, 14, "Use Numeric Value", (apr) -> {
                        Cell cell = row.getCell(3);
                        BigDecimal test = BigDecimal.ZERO;
                        if (cell != null) {
                            cell.setCellType(CellType.STRING);

                            try {
                                info.setApr(new BigDecimal(apr.getStringCellValue()));

                            } catch (NumberFormatException ex) {
                            }
                        } else {
                            info.setApr(test);
                        }

                    });
                    handleNumericCell(row, messages, info, i, 15, "Use Numeric Value", (may) -> {
                        Cell cell = row.getCell(3);
                        BigDecimal test = BigDecimal.ZERO;
                        if (cell != null) {
                            cell.setCellType(CellType.STRING);

                            try {
                                info.setMay(new BigDecimal(may.getStringCellValue()));

                            } catch (NumberFormatException ex) {
                            }
                        } else {
                            info.setMay(test);
                        }

                    });
                    handleNumericCell(row, messages, info, i, 16, "Use Numeric Value", (jun) -> {
                        Cell cell = row.getCell(3);
                        BigDecimal test = BigDecimal.ZERO;
                        if (cell != null) {
                            cell.setCellType(CellType.STRING);

                            try {
                                info.setJun(new BigDecimal(jun.getStringCellValue()));

                            } catch (NumberFormatException ex) {
                            }
                        } else {
                            info.setJun(test);
                        }

                    });
                    listFreightVolumes.add(info);
                }
            }
            if (messages.isEmpty()) {
                for (FreightVolumes a : listFreightVolumes) {
                    BudgetItems budget = new BudgetItems();
                    sampleFreightVolumesService.createOrUpdateFreightVolume(a);
                    budget = itemset(budget, a, comboBoxOrganisation.getValue());
                    sampleBudgetItemsService.update(budget);
                }
                if (!comboBoxCOA.isEmpty() && !comboBoxBudget.isEmpty()) {
                    setFreightVolumeGrid(comboBoxBudget.getValue(), comboBoxCOA.getValue());
                }
            } else {
                warningNotification(messages);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleCell(Row row, List<errorMessages> messages, FreightVolumes info, int rowIndex, int columnIndex, String errorMessage, CellHandler handler) {
        Cell cell = row.getCell(columnIndex);
        if (cell != null) {
            handler.handle(cell);
        } else {
            handleNullCell(messages, rowIndex, columnIndex, errorMessage);
        }
    }

    private void handleNumericCell(Row row, List<errorMessages> messages, FreightVolumes info, int rowIndex, int columnIndex, String errorMessage, CellHandler handler) {
        Cell cell = row.getCell(columnIndex);
        BigDecimal test = BigDecimal.ZERO;
        if (cell != null) {
            cell.setCellType(CellType.STRING);

            try {
                test = new BigDecimal(cell.getStringCellValue());
                if (columnIndex == 3) {
                    info.setRate(test);
                } else if (columnIndex == 5) {
                    info.setJul(test);
                } else if (columnIndex == 6) {
                    info.setAug(test);
                } else if (columnIndex == 7) {
                    info.setSep(test);
                } else if (columnIndex == 8) {
                    info.setOct(test);
                } else if (columnIndex == 9) {
                    info.setNov(test);
                } else if (columnIndex == 10) {
                    info.setDec(test);
                } else if (columnIndex == 11) {
                    info.setJan(test);
                } else if (columnIndex == 12) {
                    info.setFeb(test);
                } else if (columnIndex == 13) {
                    info.setMar(test);
                } else if (columnIndex == 14) {
                    info.setApr(test);
                } else if (columnIndex == 15) {
                    info.setMay(test);
                } else if (columnIndex == 16) {
                    info.setJun(test);
                    info.setTotal(calculateTotalSum(info));
                }

            } catch (NumberFormatException ex) {
                handleNumericError(messages, rowIndex, columnIndex, errorMessage);
            }
        } else {
            if (columnIndex == 3) {
                info.setRate(test);
            } else if (columnIndex == 5) {
                info.setJul(test);
            } else if (columnIndex == 6) {
                info.setAug(test);
            } else if (columnIndex == 7) {
                info.setSep(test);
            } else if (columnIndex == 8) {
                info.setOct(test);
            } else if (columnIndex == 9) {
                info.setNov(test);
            } else if (columnIndex == 10) {
                info.setDec(test);
            } else if (columnIndex == 11) {
                info.setJan(test);
            } else if (columnIndex == 12) {
                info.setFeb(test);
            } else if (columnIndex == 13) {
                info.setMar(test);
            } else if (columnIndex == 14) {
                info.setApr(test);
            } else if (columnIndex == 15) {
                info.setMay(test);
            } else if (columnIndex == 16) {
                info.setJun(test);
                info.setTotal(calculateTotalSum(info));
            }
        }
    }

    private void handleCurrencyCell(Row row, List<errorMessages> messages, FreightVolumes info, int rowIndex, int columnIndex, String errorMessage) {
        Cell cell = row.getCell(columnIndex);
        if (cell != null) {
            cell.setCellType(CellType.STRING);
            Currency curr = sampleCurrencyService.findByDataCurrencyAndBudget(cell.getStringCellValue(), comboBoxBudget.getValue());
            if (curr == null) {
                handleCurrencyError(messages, rowIndex, columnIndex, errorMessage);
            } else {
                info.setCurrency(curr);
            }
        } else {
            handleNullCell(messages, rowIndex, columnIndex, errorMessage);
        }
    }

    private void handleNullCell(List<errorMessages> messages, int rowIndex, int columnIndex, String errorMessage) {
        errorMessages error = new errorMessages();
        error.setRow("Row: " + rowIndex + " Column: " + columnIndex);
        error.setMessage(":     " + errorMessage);
        messages.add(error);
    }

    private void handleNumericError(List<errorMessages> messages, int rowIndex, int columnIndex, String errorMessage) {
        errorMessages error = new errorMessages();
        error.setRow("Row: " + rowIndex + " Column: " + columnIndex);
        error.setMessage(":     " + errorMessage);
        messages.add(error);
    }

    private void handleCurrencyError(List<errorMessages> messages, int rowIndex, int columnIndex, String errorMessage) {
        errorMessages error = new errorMessages();
        error.setRow("Row: " + rowIndex + " Column: " + columnIndex);
        error.setMessage(":     " + errorMessage);
        messages.add(error);
    }

    @FunctionalInterface
    private interface CellHandler {

        void handle(Cell cell);
    }

    public BigDecimal calculateTotalSum(FreightVolumes freightVolumes) {
        return freightVolumes.getJul().add(freightVolumes.getAug())
                .add(freightVolumes.getSep())
                .add(freightVolumes.getOct())
                .add(freightVolumes.getNov())
                .add(freightVolumes.getDec())
                .add(freightVolumes.getJan())
                .add(freightVolumes.getFeb())
                .add(freightVolumes.getMar())
                .add(freightVolumes.getApr())
                .add(freightVolumes.getMay())
                .add(freightVolumes.getJun());
    }

    private void createFreightVolumesDetail(Workbook workbook, Sheet sheet) {
        // Set paper size and row height
        sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
        short rowHeight = 500;

        // Create font
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);

        // Create cell style with specified font
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);

        // Create a bold style for the entire row
        CellStyle boldRowStyle = workbook.createCellStyle();
        boldRowStyle.cloneStyleFrom(cellStyle);
        Font boldRowFont = workbook.createFont();
        boldRowFont.setBold(true);
        boldRowStyle.setFont(boldRowFont);

        CellStyle boldRowStyleTotalColumn = workbook.createCellStyle();
        boldRowStyleTotalColumn.cloneStyleFrom(cellStyle);
        Font boldRowFontTotalColumn = workbook.createFont();
        boldRowFontTotalColumn.setBold(true);
        boldRowStyleTotalColumn.setFont(boldRowFontTotalColumn);
        boldRowStyleTotalColumn.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        boldRowStyleTotalColumn.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Create a bold and centered style with a bottom border
        CellStyle boldCenteredStyle = workbook.createCellStyle();
        boldCenteredStyle.cloneStyleFrom(cellStyle);
        boldCenteredStyle.setAlignment(HorizontalAlignment.CENTER);
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldCenteredStyle.setFont(boldFont);
        boldCenteredStyle.setBorderBottom(BorderStyle.THIN);

        CellStyle boldLeftStyle = workbook.createCellStyle();
        boldLeftStyle.cloneStyleFrom(cellStyle);
        boldLeftStyle.setAlignment(HorizontalAlignment.LEFT);
        Font boldFont2 = workbook.createFont();
        boldFont2.setBold(true);
        boldLeftStyle.setFont(boldFont2);
        boldLeftStyle.setBorderBottom(BorderStyle.THIN);

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.setHeight(rowHeight);

        try {
            // Add image to header
            addImageToHeader2(sheet, "/META-INF/resources/images/urclogo.png");
        } catch (IOException ex) {
            Logger.getLogger(BudgetReportsView.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Ensure that the headerRow is not null before accessing cells
        if (headerRow != null) {
            Cell headerCell = headerRow.createCell(1);
            headerCell.setCellValue("UGANDA RAILWAYS CORPORATION");
            CellRangeAddress cellRange3 = new CellRangeAddress(0, 0, 1, 17);
            sheet.addMergedRegion(cellRange3);
            setBottomBorderForRegion(sheet, cellRange3);

            // Set the style for the first cell in the merged region to be bold
            Cell firstCellInMergedRegion = headerRow.getCell(1);
            if (firstCellInMergedRegion != null) {
                firstCellInMergedRegion.setCellStyle(boldCenteredStyle);
            }
        }

        // Create header2 row
        Row header2 = sheet.createRow(1);

        // Ensure that the header2 is not null before accessing cells
        if (header2 != null) {
            Cell header2Cell = header2.createCell(0);
            header2Cell.setCellValue("FREIGHT VOLUMES " + comboBoxBudget.getValue().getFinancialYear());
            CellRangeAddress cellRange2 = new CellRangeAddress(1, 1, 0, 17);
            sheet.addMergedRegion(cellRange2);
            setBottomBorderForRegion(sheet, cellRange2);

            // Set the style for the first cell in the merged region to be bold
            Cell firstCellInMergedRegion = header2.getCell(0);
            if (firstCellInMergedRegion != null) {
                firstCellInMergedRegion.setCellStyle(boldCenteredStyle);
            }
        }

        Coalevel11 getCoalevel11 = sampleCoalevel11Service.findByName("MAIN ROUTE");
        int row = 2;
        if (getCoalevel11 != null) {
            Row Q2 = sheet.createRow(2);
            Cell header2Cell = Q2.createCell(0);
            header2Cell.setCellValue(getCoalevel11.getName().toUpperCase());
            CellRangeAddress cellRange2 = new CellRangeAddress(2, 2, 0, 17);
            sheet.addMergedRegion(cellRange2);
            setBottomBorderForRegion(sheet, cellRange2);

            Cell firstCellInMergedRegion = Q2.getCell(0);
            if (firstCellInMergedRegion != null) {
                firstCellInMergedRegion.setCellStyle(boldLeftStyle);
            }

            List<COA> listCoa = sampleCoaService.findByBudgetAndCoalevel11(comboBoxBudget.getValue(), getCoalevel11);
            if (listCoa != null) {

                for (COA coa : listCoa) {
                    List<FreightVolumes> items = sampleFreightVolumesService.getAllFreightVolumesByBudgetAndCode(comboBoxBudget.getValue(), coa);
                    if (!items.isEmpty()) {
                        row++;
                        Row Q4 = sheet.createRow(row);
                        Q4.createCell((short) 0).setCellValue(coa.getCode());
                        Q4.createCell((short) 1).setCellValue(coa.getName());
                        CellRangeAddress cellRange3 = new CellRangeAddress(row, row, 1, 17);
                        sheet.addMergedRegion(cellRange3);
                        Cell firstCellInMergedRegion1 = Q4.getCell(0);
                        if (firstCellInMergedRegion1 != null) {
                            firstCellInMergedRegion1.setCellStyle(boldLeftStyle);
                        }
                        Cell firstCellInMergedRegion2 = Q4.getCell(1);
                        if (firstCellInMergedRegion2 != null) {
                            firstCellInMergedRegion2.setCellStyle(boldLeftStyle);
                        }
                        row++;
                        Row Q3 = sheet.createRow(row);

                        // Ensure that the Q2 is not null before accessing cells
                        if (Q3 != null && !items.isEmpty()) {
                            // Populate cells with data
                            Q3.createCell((short) 0).setCellValue("NAME");
                            Q3.createCell((short) 1).setCellValue("CARGO TYPE");
                            Q3.createCell((short) 2).setCellValue("DESCRIPTION");
                            Q3.createCell((short) 3).setCellValue("RATE");
                            Q3.createCell((short) 4).setCellValue("CURRENCY");
                            Q3.createCell((short) 5).setCellValue("JUL/TON");
                            Q3.createCell((short) 6).setCellValue("AUG/TON");
                            Q3.createCell((short) 7).setCellValue("SEP/TON");
                            Q3.createCell((short) 8).setCellValue("OCT/TON");
                            Q3.createCell((short) 9).setCellValue("NOV/TON");
                            Q3.createCell((short) 10).setCellValue("DEC/TON");
                            Q3.createCell((short) 11).setCellValue("JAN/TON");
                            Q3.createCell((short) 12).setCellValue("FEB/TON");
                            Q3.createCell((short) 13).setCellValue("MAR/TON");
                            Q3.createCell((short) 14).setCellValue("APR/TON");
                            Q3.createCell((short) 15).setCellValue("MAY/TON");
                            Q3.createCell((short) 16).setCellValue("JUN/TON");
                            Q3.createCell((short) 17).setCellValue("TOTAL/TON");
                            for (short i = 0; i <= 17; i++) {
                                Cell firstCellInMergedRegion3 = Q3.getCell(i);
                                if (firstCellInMergedRegion3 != null && i < 17) {
                                    firstCellInMergedRegion3.setCellStyle(boldRowStyle);
                                } else if (firstCellInMergedRegion3 != null && i == 17) {
                                    firstCellInMergedRegion3.setCellStyle(boldRowStyleTotalColumn);
                                }
                            }

                            for (FreightVolumes b : items) {
                                row++;
                                Row Q5 = sheet.createRow(row);
                                if (Q5 != null) {
                                    Cell cell0 = Q5.createCell((short) 0);
                                    if (b.getName() != null) {
                                        cell0.setCellValue(b.getName());
                                    } else {
                                        cell0.setCellValue("-");
                                    }

                                    Cell cell1 = Q5.createCell((short) 1);
                                    if (b.getCargotype() != null) {
                                        cell1.setCellValue(b.getCargotype());
                                    } else {
                                        cell1.setCellValue("-");
                                    }

                                    Cell cell2 = Q5.createCell((short) 2);
                                    if (b.getCargo_desc() != null) {
                                        cell2.setCellValue(b.getCargo_desc());
                                    } else {
                                        cell2.setCellValue("-");
                                    }

                                    Cell cell3 = Q5.createCell((short) 3);
                                    if (b.getRate() != null) {
                                        cell3.setCellValue(b.getRate().doubleValue());
                                    } else {
                                        cell3.setCellValue("-");
                                    }

                                    Cell cell4 = Q5.createCell((short) 4);
                                    if (b.getCurrency() != null) {
                                        cell4.setCellValue(b.getCurrency().getData().getCurrencyShort());
                                    } else {
                                        cell4.setCellValue("-");
                                    }

                                    Cell cell5 = Q5.createCell((short) 5);
                                    if (b.getJul() != null) {
                                        cell5.setCellValue(b.getJul().doubleValue());
                                    } else {
                                        cell5.setCellValue("-");
                                    }

                                    Cell cell6 = Q5.createCell((short) 6);
                                    if (b.getAug() != null) {
                                        cell6.setCellValue(b.getAug().doubleValue());
                                    } else {
                                        cell6.setCellValue("-");
                                    }

                                    Cell cell7 = Q5.createCell((short) 7);
                                    if (b.getSep() != null) {
                                        cell7.setCellValue(b.getSep().doubleValue());
                                    } else {
                                        cell7.setCellValue("-");
                                    }

                                    Cell cell8 = Q5.createCell((short) 8);
                                    if (b.getOct() != null) {
                                        cell8.setCellValue(b.getOct().doubleValue());
                                    } else {
                                        cell8.setCellValue("-");
                                    }

                                    Cell cell9 = Q5.createCell((short) 9);
                                    if (b.getNov() != null) {
                                        cell9.setCellValue(b.getNov().doubleValue());
                                    } else {
                                        cell9.setCellValue("-");
                                    }

                                    Cell cell10 = Q5.createCell((short) 10);
                                    if (b.getDec() != null) {
                                        cell10.setCellValue(b.getDec().doubleValue());
                                    } else {
                                        cell10.setCellValue("-");
                                    }

                                    Cell cell11 = Q5.createCell((short) 11);
                                    if (b.getJan() != null) {
                                        cell11.setCellValue(b.getJan().doubleValue());
                                    } else {
                                        cell11.setCellValue("-");
                                    }

                                    Cell cell12 = Q5.createCell((short) 12);
                                    if (b.getFeb() != null) {
                                        cell12.setCellValue(b.getFeb().doubleValue());
                                    } else {
                                        cell12.setCellValue("-");
                                    }

                                    Cell cell13 = Q5.createCell((short) 13);
                                    if (b.getMar() != null) {
                                        cell13.setCellValue(b.getMar().doubleValue());
                                    } else {
                                        cell13.setCellValue("-");
                                    }

                                    Cell cell14 = Q5.createCell((short) 14);
                                    if (b.getApr() != null) {
                                        cell14.setCellValue(b.getApr().doubleValue());
                                    } else {
                                        cell14.setCellValue("-");
                                    }

                                    Cell cell15 = Q5.createCell((short) 15);
                                    if (b.getMay() != null) {
                                        cell15.setCellValue(b.getMay().doubleValue());
                                    } else {
                                        cell15.setCellValue("-");
                                    }

                                    Cell cell16 = Q5.createCell((short) 16);
                                    if (b.getJun() != null) {
                                        cell16.setCellValue(b.getJun().doubleValue());
                                    } else {
                                        cell16.setCellValue("-");
                                    }

                                    Cell cell17 = Q5.createCell((short) 17);
                                    if (b.getTotal() != null) {
                                        cell17.setCellValue(b.getTotal().doubleValue());
                                    } else {
                                        cell17.setCellValue("-");
                                    }
                                    cell17.setCellStyle(boldRowStyleTotalColumn);

                                }
                            }
                            row++;
                            Row Q5 = sheet.createRow(row);
                            if (Q5 != null) {
                                Cell cell0 = Q5.createCell((short) 0);
                                cell0.setCellValue("TOTAL:");

                                MonthlySumResponseFreight total = sampleFreightVolumesService.getTotals(comboBoxBudget.getValue(), coa);

                                Cell cell5 = Q5.createCell((short) 5);
                                if (total.getJul() != null) {
                                    cell5.setCellValue(total.getJul().doubleValue());
                                } else {
                                    cell5.setCellValue("-");
                                }
                                cell5.setCellStyle(boldRowStyleTotalColumn);

                                Cell cell6 = Q5.createCell((short) 6);
                                if (total.getAug() != null) {
                                    cell6.setCellValue(total.getAug().doubleValue());
                                } else {
                                    cell6.setCellValue("-");
                                }
                                cell6.setCellStyle(boldRowStyleTotalColumn);

                                Cell cell7 = Q5.createCell((short) 7);
                                if (total.getSep() != null) {
                                    cell7.setCellValue(total.getSep().doubleValue());
                                } else {
                                    cell7.setCellValue("-");
                                }
                                cell7.setCellStyle(boldRowStyleTotalColumn);

                                Cell cell8 = Q5.createCell((short) 8);
                                if (total.getOct() != null) {
                                    cell8.setCellValue(total.getOct().doubleValue());
                                } else {
                                    cell8.setCellValue("-");
                                }
                                cell8.setCellStyle(boldRowStyleTotalColumn);

                                Cell cell9 = Q5.createCell((short) 9);
                                if (total.getNov() != null) {
                                    cell9.setCellValue(total.getNov().doubleValue());
                                } else {
                                    cell9.setCellValue("-");
                                }
                                cell9.setCellStyle(boldRowStyleTotalColumn);

                                Cell cell10 = Q5.createCell((short) 10);
                                if (total.getDec() != null) {
                                    cell10.setCellValue(total.getDec().doubleValue());
                                } else {
                                    cell10.setCellValue("-");
                                }
                                cell10.setCellStyle(boldRowStyleTotalColumn);

                                Cell cell11 = Q5.createCell((short) 11);
                                if (total.getJan() != null) {
                                    cell11.setCellValue(total.getJan().doubleValue());
                                } else {
                                    cell11.setCellValue("-");
                                }
                                cell11.setCellStyle(boldRowStyleTotalColumn);

                                Cell cell12 = Q5.createCell((short) 12);
                                if (total.getFeb() != null) {
                                    cell12.setCellValue(total.getFeb().doubleValue());
                                } else {
                                    cell12.setCellValue("-");
                                }
                                cell12.setCellStyle(boldRowStyleTotalColumn);

                                Cell cell13 = Q5.createCell((short) 13);
                                if (total.getMar() != null) {
                                    cell13.setCellValue(total.getMar().doubleValue());
                                } else {
                                    cell13.setCellValue("-");
                                }

                                cell13.setCellStyle(boldRowStyleTotalColumn);

                                Cell cell14 = Q5.createCell((short) 14);
                                if (total.getApr() != null) {
                                    cell14.setCellValue(total.getApr().doubleValue());
                                } else {
                                    cell14.setCellValue("-");
                                }
                                cell14.setCellStyle(boldRowStyleTotalColumn);

                                Cell cell15 = Q5.createCell((short) 15);
                                if (total.getMay() != null) {
                                    cell15.setCellValue(total.getMay().doubleValue());
                                } else {
                                    cell15.setCellValue("-");
                                }
                                cell15.setCellStyle(boldRowStyleTotalColumn);

                                Cell cell16 = Q5.createCell((short) 16);
                                if (total.getJun() != null) {
                                    cell16.setCellValue(total.getJun().doubleValue());
                                } else {
                                    cell16.setCellValue("-");
                                }
                                cell16.setCellStyle(boldRowStyleTotalColumn);

                                Cell cell17 = Q5.createCell((short) 17);
                                if (total.getTotal() != null) {
                                    cell17.setCellValue(total.getTotal().doubleValue());
                                } else {
                                    cell17.setCellValue("-");
                                }
                                cell17.setCellStyle(boldRowStyleTotalColumn);

                            }

                        }

                    }

                }

                // Create Q2 data row
                // Set the style for the first cell in the merged region to be bold
                // Set the entire Q2 row to be bold
                /*            */
            }
            getCoalevel11 = sampleCoalevel11Service.findByName("SOUTHERN ROUTE");
            row++;
            if (getCoalevel11 != null) {
                Row Q21 = sheet.createRow(row);
                Cell header2Cell2 = Q21.createCell(0);
                header2Cell2.setCellValue(getCoalevel11.getName().toUpperCase());
                CellRangeAddress cellRange21 = new CellRangeAddress(row, row, 0, 17);
                sheet.addMergedRegion(cellRange21);
                setBottomBorderForRegion(sheet, cellRange21);

                Cell firstCellInMergedRegion1 = Q21.getCell(0);
                if (firstCellInMergedRegion1 != null) {
                    firstCellInMergedRegion1.setCellStyle(boldLeftStyle);
                }

                List<COA> listCoa1 = sampleCoaService.findByBudgetAndCoalevel11(comboBoxBudget.getValue(), getCoalevel11);
                if (listCoa1 != null) {

                    for (COA coa : listCoa1) {

                        List<FreightVolumes> items = sampleFreightVolumesService.getAllFreightVolumesByBudgetAndCode(comboBoxBudget.getValue(), coa);

                        if (!items.isEmpty()) {
                            row++;
                            Row Q4 = sheet.createRow(row);
                            Q4.createCell((short) 0).setCellValue(coa.getCode());
                            Q4.createCell((short) 1).setCellValue(coa.getName());
                            CellRangeAddress cellRange3 = new CellRangeAddress(row, row, 1, 17);
                            sheet.addMergedRegion(cellRange3);
                            Cell firstCellInMergedRegion12 = Q4.getCell(0);
                            if (firstCellInMergedRegion12 != null) {
                                firstCellInMergedRegion12.setCellStyle(boldLeftStyle);
                            }
                            Cell firstCellInMergedRegion2 = Q4.getCell(1);
                            if (firstCellInMergedRegion2 != null) {
                                firstCellInMergedRegion2.setCellStyle(boldLeftStyle);
                            }

                            row++;
                            Row Q3 = sheet.createRow(row);

                            // Ensure that the Q2 is not null before accessing cells
                            if (Q3 != null && !items.isEmpty()) {
                                // Populate cells with data
                                Q3.createCell((short) 0).setCellValue("NAME");
                                Q3.createCell((short) 1).setCellValue("CARGO TYPE");
                                Q3.createCell((short) 2).setCellValue("DESCRIPTION");
                                Q3.createCell((short) 3).setCellValue("RATE");
                                Q3.createCell((short) 4).setCellValue("CURRENCY");
                                Q3.createCell((short) 5).setCellValue("JUL/TON");
                                Q3.createCell((short) 6).setCellValue("AUG/TON");
                                Q3.createCell((short) 7).setCellValue("SEP/TON");
                                Q3.createCell((short) 8).setCellValue("OCT/TON");
                                Q3.createCell((short) 9).setCellValue("NOV/TON");
                                Q3.createCell((short) 10).setCellValue("DEC/TON");
                                Q3.createCell((short) 11).setCellValue("JAN/TON");
                                Q3.createCell((short) 12).setCellValue("FEB/TON");
                                Q3.createCell((short) 13).setCellValue("MAR/TON");
                                Q3.createCell((short) 14).setCellValue("APR/TON");
                                Q3.createCell((short) 15).setCellValue("MAY/TON");
                                Q3.createCell((short) 16).setCellValue("JUN/TON");
                                Q3.createCell((short) 17).setCellValue("TOTAL/TON");
                                for (short i = 0; i <= 17; i++) {
                                    Cell firstCellInMergedRegion3 = Q3.getCell(i);
                                    if (firstCellInMergedRegion3 != null && i < 17) {
                                        firstCellInMergedRegion3.setCellStyle(boldRowStyle);
                                    } else if (firstCellInMergedRegion3 != null && i == 17) {
                                        firstCellInMergedRegion3.setCellStyle(boldRowStyleTotalColumn);
                                    }
                                }

                                for (FreightVolumes b : items) {
                                    row++;
                                    Row Q5 = sheet.createRow(row);
                                    if (Q5 != null) {
                                        Cell cell0 = Q5.createCell((short) 0);
                                        if (b.getName() != null) {
                                            cell0.setCellValue(b.getName());
                                        } else {
                                            cell0.setCellValue("-");
                                        }

                                        Cell cell1 = Q5.createCell((short) 1);
                                        if (b.getCargotype() != null) {
                                            cell1.setCellValue(b.getCargotype());
                                        } else {
                                            cell1.setCellValue("-");
                                        }

                                        Cell cell2 = Q5.createCell((short) 2);
                                        if (b.getCargo_desc() != null) {
                                            cell2.setCellValue(b.getCargo_desc());
                                        } else {
                                            cell2.setCellValue("-");
                                        }

                                        Cell cell3 = Q5.createCell((short) 3);
                                        if (b.getRate() != null) {
                                            cell3.setCellValue(b.getRate().doubleValue());
                                        } else {
                                            cell3.setCellValue("-");
                                        }

                                        Cell cell4 = Q5.createCell((short) 4);
                                        if (b.getCurrency() != null) {
                                            cell4.setCellValue(b.getCurrency().getData().getCurrencyShort());
                                        } else {
                                            cell4.setCellValue("-");
                                        }

                                        Cell cell5 = Q5.createCell((short) 5);
                                        if (b.getJul() != null) {
                                            cell5.setCellValue(b.getJul().doubleValue());
                                        } else {
                                            cell5.setCellValue("-");
                                        }

                                        Cell cell6 = Q5.createCell((short) 6);
                                        if (b.getAug() != null) {
                                            cell6.setCellValue(b.getAug().doubleValue());
                                        } else {
                                            cell6.setCellValue("-");
                                        }

                                        Cell cell7 = Q5.createCell((short) 7);
                                        if (b.getSep() != null) {
                                            cell7.setCellValue(b.getSep().doubleValue());
                                        } else {
                                            cell7.setCellValue("-");
                                        }

                                        Cell cell8 = Q5.createCell((short) 8);
                                        if (b.getOct() != null) {
                                            cell8.setCellValue(b.getOct().doubleValue());
                                        } else {
                                            cell8.setCellValue("-");
                                        }

                                        Cell cell9 = Q5.createCell((short) 9);
                                        if (b.getNov() != null) {
                                            cell9.setCellValue(b.getNov().doubleValue());
                                        } else {
                                            cell9.setCellValue("-");
                                        }

                                        Cell cell10 = Q5.createCell((short) 10);
                                        if (b.getDec() != null) {
                                            cell10.setCellValue(b.getDec().doubleValue());
                                        } else {
                                            cell10.setCellValue("-");
                                        }

                                        Cell cell11 = Q5.createCell((short) 11);
                                        if (b.getJan() != null) {
                                            cell11.setCellValue(b.getJan().doubleValue());
                                        } else {
                                            cell11.setCellValue("-");
                                        }

                                        Cell cell12 = Q5.createCell((short) 12);
                                        if (b.getFeb() != null) {
                                            cell12.setCellValue(b.getFeb().doubleValue());
                                        } else {
                                            cell12.setCellValue("-");
                                        }

                                        Cell cell13 = Q5.createCell((short) 13);
                                        if (b.getMar() != null) {
                                            cell13.setCellValue(b.getMar().doubleValue());
                                        } else {
                                            cell13.setCellValue("-");
                                        }

                                        Cell cell14 = Q5.createCell((short) 14);
                                        if (b.getApr() != null) {
                                            cell14.setCellValue(b.getApr().doubleValue());
                                        } else {
                                            cell14.setCellValue("-");
                                        }

                                        Cell cell15 = Q5.createCell((short) 15);
                                        if (b.getMay() != null) {
                                            cell15.setCellValue(b.getMay().doubleValue());
                                        } else {
                                            cell15.setCellValue("-");
                                        }

                                        Cell cell16 = Q5.createCell((short) 16);
                                        if (b.getJun() != null) {
                                            cell16.setCellValue(b.getJun().doubleValue());
                                        } else {
                                            cell16.setCellValue("-");
                                        }

                                        Cell cell17 = Q5.createCell((short) 17);
                                        if (b.getTotal() != null) {
                                            cell17.setCellValue(b.getTotal().doubleValue());
                                        } else {
                                            cell17.setCellValue("-");
                                        }
                                        cell17.setCellStyle(boldRowStyleTotalColumn);

                                    }
                                }

                            }
                        }

                    }

                }

                // Create Q2 data row
                // Set the style for the first cell in the merged region to be bold
                // Set the entire Q2 row to be bold
                /*            for (short i = 0; i <= 17; i++) {
            Cell firstCellInMergedRegion = Q2.getCell(i);
            if (firstCellInMergedRegion != null) {
            firstCellInMergedRegion.setCellStyle(boldRowStyle);
            }
            }*/
            }

        }
    }

    private void exportAndDownloadFreightVolume(Budget budget) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("FREIGHT VOLUMES " + budget.getFinancialYear());
            // Set the paper size to A3 Landscape
            sheet.getPrintSetup().setPaperSize(PrintSetup.A3_PAPERSIZE);
            sheet.getPrintSetup().setLandscape(true);

            createFreightVolumesDetail(workbook, sheet);

            //createDataRows(sheet, people);
            // Write the workbook to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // Create a StreamResource with the Excel data
            StreamResource resource = new StreamResource("FREIGHT VOLUMES " + budget.getFinancialYear() + ".xlsx", ()
                    -> new ByteArrayInputStream(outputStream.toByteArray()));

            // Create an Anchor component with the StreamResource
            Anchor downloadLink2 = new Anchor(resource, "");
            downloadLink2.getElement().setAttribute("download", true);
            add(downloadLink2);
            // Programmatically click the download link to initiate the download
            downloadLink2.getElement().callJsFunction("click");
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private static void addImageToHeader2(Sheet sheet, String imagePath) throws IOException {
        // Load the image
        BufferedImage bufferedImage = ImageIO.read(BudgetReportsView.class.getResourceAsStream(imagePath));

        // Convert the image to a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        // Add the image to the header
        Drawing<?> drawing = sheet.createDrawingPatriarch();

        // Set the position and size of the image
        int row = 0; // Modify this to the desired row number
        int col = 0; // Modify this to the desired column number
        int dx1 = 0; // The x-coordinate in EMU units (1/1024th of a point)
        int dy1 = 0; // The y-coordinate in EMU units
        int dx2 = 1023; // The width of the image in EMU units
        int dy2 = 255; // The height of the image in EMU units

        ClientAnchor anchor = drawing.createAnchor(dx1, dy1, dx2, dy2, col, row, col + 1, row + 1);

        int pictureIndex = sheet.getWorkbook().addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
        drawing.createPicture(anchor, pictureIndex);
    }

    private void setBottomBorderForRegion(Sheet sheet, CellRangeAddress region) {
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
    }

    private void setBottomTopBorderForRegion(Sheet sheet, CellRangeAddress region) {
        RegionUtil.setBorderBottom(BorderStyle.DOUBLE, region, sheet);
        RegionUtil.setBorderTop(BorderStyle.THICK, region, sheet);
    }

}
