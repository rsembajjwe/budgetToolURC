package com.methaltech.application.views.currency;

import com.methaltech.application.data.bgtool.service.BudgetService;
import com.methaltech.application.data.bgtool.service.CurrencyDataService;
import com.methaltech.application.data.bgtool.service.CurrencyService;
import com.methaltech.application.data.entity.bgtool.Budget;
import com.methaltech.application.data.entity.bgtool.Currency;
import com.methaltech.application.data.entity.bgtool.CurrencyData;
import com.methaltech.application.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;

@PageTitle("Currency")
@Route(value = "currency", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "BLO","USER","HOD"})
public class currencyView extends Div {

    private Grid<Currency> gridCurrency = new Grid<>(Currency.class, false);
    private final CurrencyDataService sampleCurrencyDataService;
    private final CurrencyService sampleCurrencyService;
    private ComboBox<Budget> comboBoxBudget = new ComboBox<>("Budget");
    private final BudgetService chosenBudgetService;

    public currencyView(CurrencyDataService sampleCurrencyDataService, CurrencyService sampleCurrencyService,
            BudgetService chosenBudgetService) {
        this.sampleCurrencyDataService = sampleCurrencyDataService;
        this.sampleCurrencyService = sampleCurrencyService;
        this.chosenBudgetService = chosenBudgetService;
        //setSpacing(false);

        comboBoxBudget.setItems(query -> chosenBudgetService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        comboBoxBudget.setItemLabelGenerator(Budget::getFinancialYear);
        gridCurrency.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
        gridCurrency.addColumn(new ComponentRenderer<>(Currency -> {
            Text textField = new Text("");
            if (Optional.ofNullable(Currency.getBudget()).isPresent()) {

                Optional<CurrencyData> curr = sampleCurrencyDataService.get(Currency.getCurrencyid());
                if (curr.isPresent()) {
                    textField.setText(curr.get().getCurrency());
                } else {
                    textField.setText("Not Found");
                }

                return textField;
            } else {
                textField.setText("Empty");
                return textField;
            }
        })).setHeader("Currency");
        gridCurrency.addColumn(new ComponentRenderer<>(Currency -> {
            Text textField = new Text("");
            if (Optional.ofNullable(Currency.getBudget()).isPresent()) {

                Optional<CurrencyData> curr = sampleCurrencyDataService.get(Currency.getCurrencyid());
                if (curr.isPresent()) {
                    textField.setText(curr.get().getCurrencyShort());
                } else {
                    textField.setText("Not Found");
                }

                return textField;
            } else {
                textField.setText("Empty");
                return textField;
            }
        })).setHeader("ABR");        
        gridCurrency.addColumn("rate").setAutoWidth(true);
        LitRenderer<Currency> importantRenderer = LitRenderer.<Currency>of(
                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", important -> important.isEnabled() ? "check" : "minus").withProperty("color",
                important -> important.isEnabled()
                ? "var(--lumo-primary-text-color)"
                : "var(--lumo-disabled-text-color)");
        Image image2 = new Image("images/ugflagstrip.png", "Strip");
        image2.setWidthFull();
        image2.getStyle().set("margin", "0").set("padding", "0");
        add(image2);
        add(comboBoxBudget, gridCurrency);
        comboBoxBudget.addValueChangeListener(e -> {
            refreshGridCurrency();
        });

        setSizeFull();
        //setJustifyContentMode(JustifyContentMode.CENTER);
       // setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        //getStyle().set("text-align", "center");
    }

    private void refreshGridCurrency() {
        if (!comboBoxBudget.isEmpty()) {
            gridCurrency.select(null);
            gridCurrency.getDataProvider().refreshAll();
            gridCurrency.setItems(sampleCurrencyService.findCurrencyByBudget(comboBoxBudget.getValue()));
        }

    }
}
