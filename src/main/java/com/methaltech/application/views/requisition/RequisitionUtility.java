package com.methaltech.application.views.requisition;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.methaltech.application.data.bgtool.service.DeptSectionMergerService;
import com.methaltech.application.data.entity.bgtool.RequisitionData;
import static com.methaltech.application.data.entity.bgtool.RequisitionData.RequisitionType.CASH_REQUISITION;
import static com.methaltech.application.data.entity.bgtool.RequisitionData.RequisitionType.FORM_48;
import static com.methaltech.application.data.entity.bgtool.RequisitionData.RequisitionType.FORM_5;
import com.methaltech.application.data.entity.livedata.UrcDepartmentAnlDim;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinServletService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequisitionUtility {

    private DeptSectionMergerService deptSectionMergerService;

    public RequisitionUtility(DeptSectionMergerService deptSectionMergerService) {
        this.deptSectionMergerService = deptSectionMergerService;
    }

    public void generatePDFForm(RequisitionData requisition, VerticalLayout comp) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(PageSize.A4);
            Document document = new Document(pdfDoc);

            // Create fonts
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
            PdfFont itallicFont = PdfFontFactory.createFont(StandardFonts.TIMES_ITALIC);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);

            switch (requisition.getRequisitionType()) {
                case CASH_REQUISITION:
                    generateCashRequisitionForm(document, requisition, boldFont, itallicFont, normalFont);
                    break;
                case FORM_5:
                    generateForm5(document, requisition, boldFont, itallicFont, normalFont);
                    break;
                case FORM_48:
                    generateForm48(document, requisition, boldFont, itallicFont, normalFont);
                    break;
            }

            document.close();

            // Create download
            /*            String fileName = requisition.getRequisitionType().name() + "_"
            + requisition.getRequisitionNumber().replace("/", "-") + ".pdf";
            
            StreamResource resource = new StreamResource(fileName, () -> new ByteArrayInputStream(baos.toByteArray()));
            resource.setContentType("application/pdf");
            resource.setCacheTime(0);
            
            // Trigger download
            UI ui = UI.getCurrent();
            if (ui != null) {
            StreamRegistration registration = ui.getSession()
            .getResourceRegistry()
            .registerResource(resource);
            
            String url = registration.getResourceUri().toString();
            System.out.println("PDF URL: " + url);
            ui.getPage().open(url, "_blank");
            showNotification("Report generated successfully", NotificationVariant.LUMO_SUCCESS);
            }else{
            System.out.println("UI is null! Cannot open dynamic resource.");
            }*/
            // 2️⃣ Create StreamResource
            String fileName = requisition.getRequisitionType().name() + "_"
                    + requisition.getRequisitionNumber().replace("/", "-") + ".pdf";

            // Keep resource as a field to avoid garbage collection
            StreamResource pdfResource = new StreamResource(fileName, () -> new ByteArrayInputStream(baos.toByteArray()));
            pdfResource.setContentType("application/pdf");
            pdfResource.setCacheTime(0);

            // Attach resource to layout to keep reference alive
            //Anchor downloadLink = new Anchor(pdfResource, "Download PDF");
            //downloadLink.getElement().setAttribute("download", true);
            Anchor openLink = new Anchor(pdfResource, "");
            openLink.setTarget("_blank");  // open in new tab
            openLink.getElement().setAttribute("download", false); // make it inline
            openLink.getStyle().set("display", "none"); // hide the link
            // comp.add(downloadLink,openLink);
            comp.add(openLink);

            // 3️⃣ Open PDF in a new tab safely
            UI ui = UI.getCurrent();
            if (ui != null) {
                ui.access(() -> {
                    // downloadLink.getElement().callJsFunction("click");
                    openLink.getElement().callJsFunction("click");
                    StreamRegistration registration = ui.getSession()
                            .getResourceRegistry()
                            .registerResource(pdfResource);

                    String url = registration.getResourceUri().toString();
                    //String url = VaadinServletService.getCurrent().resolveResourceURL(registration.getResourceUri());
                    UI.getCurrent().getPage().open(url, "_blank");
                    Notification.show("Report generated successfully", 3000, Notification.Position.TOP_CENTER);
                });
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            showNotification("Error generating PDF: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_END);
        notification.addThemeVariants(variant);
    }

    public RequisitionData.ProcMethods setProcurementMethodByAmount(Double requestedAmount) {
        if (requestedAmount == null) {
            return RequisitionData.ProcMethods.NOT_DETERMINED;
        }

        if (requestedAmount < 10_000_000) {
            return RequisitionData.ProcMethods.MICRO_PROCUREMENT;
        } else if (requestedAmount >= 10_000_000 && requestedAmount < 200_000_000) {
            return RequisitionData.ProcMethods.RFQ;
        } else if (requestedAmount >= 200_000_000 && requestedAmount <= 500_000_000) {
            return RequisitionData.ProcMethods.RESTRICTED_BIDDING;
        } else {
            return RequisitionData.ProcMethods.OPEN_BIDDING;
        }
    }

    private RequisitionData.ProcMethods setProcMethods(double requestedAmount) {
        return setProcurementMethodByAmount(requestedAmount);
    }

    private void generateCashRequisitionForm(Document document, RequisitionData requisition,
            PdfFont boldFont, PdfFont itallicFont, PdfFont normalFont) throws Exception {

        // Official PPDA header
        document.add(new Paragraph("UGANDA RAILWAYS CORPORATION")
                .setFont(boldFont).setFontSize(13).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("CASH REQUISITION FORM")
                .setFont(normalFont).setFontSize(13).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("REQUEST FOR APPROVAL OF REQUISITION")
                .setFont(boldFont).setFontSize(12).setTextAlignment(TextAlignment.CENTER).setMarginBottom(0));

        // --- PART I: REQUEST BY USER DEPARTMENT ---
        document.add(new Paragraph("PART I: REQUEST BY USER DEPARTMENT FOR APPROVAL OF REQUISITION")
                .setFont(boldFont).setMarginTop(0)).setTextAlignment(TextAlignment.CENTER);

        Table detailsTable = new Table(UnitValue.createPercentArray(new float[]{3, 3, 3, 3}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(0);

// --- First row: Procurement Reference Number spans all 4 columns ---
        Cell refCell = new Cell(1, 4) // row span 1, column span 4
                .add(new Paragraph("Requisition Reference Number: "
                        + safe(requisition.getRequisitionNumber()))
                        .setFont(boldFont)).setFontSize(10);
        detailsTable.addCell(refCell);

// --- Second row: headers ---
        detailsTable.addCell(new Cell().add(new Paragraph("Code of Procuring and Disposing Entity").setFont(normalFont))).setFontSize(10);
        detailsTable.addCell(new Cell().add(new Paragraph("Supplies/Works/Non-Consultancy Services").setFont(normalFont))).setFontSize(10);
        detailsTable.addCell(new Cell().add(new Paragraph("Financial Year").setFont(normalFont))).setFontSize(10);
        detailsTable.addCell(new Cell().add(new Paragraph("Sequence Number").setFont(normalFont)));

// --- Third row: values ---
        detailsTable.addCell(safe(requisition.getPdeCode())).setFontSize(10);
        //detailsTable.addCell(safe(requisition.getProcType().name())).setFontSize(10);
        String procType = requisition.getProcType() != null
                ? requisition.getProcType().name()
                : "N/A";

        detailsTable.addCell(safe(procType)).setFontSize(10);

        detailsTable.addCell(safe(requisition.getBudget().getFinancialYear())).setFontSize(10);
        detailsTable.addCell(safe(requisition.getRequisitionNumber())).setFontSize(10);

        document.add(detailsTable).setFont(normalFont).setFontSize(10);

        // --- PARTICULARS OF PROCUREMENT ---
        Table particulars = new Table(UnitValue.createPercentArray(new float[]{3, 5}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(25);

// --- First row: Section title spans 2 columns ---
        Cell partHeader = new Cell(1, 2)
                .add(new Paragraph("Particulars of Requisition").setFont(boldFont));
        particulars.addCell(partHeader);

// --- Subject of Procurement ---
        particulars.addCell(new Paragraph("Subject of Requisition").setFont(normalFont)).setTextAlignment(TextAlignment.LEFT);
        particulars.addCell(new Paragraph(safe(requisition.getSubjectOfProcurement()))).setTextAlignment(TextAlignment.LEFT);

        particulars.addCell(new Paragraph("Justification:").setFont(boldFont)).setTextAlignment(TextAlignment.LEFT);
        particulars.addCell(new Paragraph(safe(requisition.getJustification()))).setTextAlignment(TextAlignment.LEFT);

        document.add(particulars).setBottomMargin(25);

        // --- DETAILS TABLE ---
        Table details = new Table(UnitValue.createPercentArray(new float[]{1, 3, 1, 2, 2, 2}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(15);

// --- First row: Section title spans all 6 columns ---
        Cell header = new Cell(1, 6)
                .add(new Paragraph("Details Relating to the Requisition").setFont(boldFont)).setTextAlignment(TextAlignment.LEFT);
        details.addCell(header);

// --- Second row: Column headers ---
        Paragraph descHeader = new Paragraph()
                .add(new Text("Description ").setFont(normalFont)).setTextAlignment(TextAlignment.LEFT);  // <- italic font
        details.addCell(new Paragraph("Item No.").setFont(normalFont).setTextAlignment(TextAlignment.LEFT));
        details.addCell(descHeader);
        details.addCell(new Paragraph("Quantity").setFont(normalFont)).setTextAlignment(TextAlignment.LEFT);
        details.addCell(new Paragraph("Unit of Measure").setFont(normalFont)).setTextAlignment(TextAlignment.LEFT);
        details.addCell(new Paragraph("Estimated Unit Cost").setFont(normalFont)).setTextAlignment(TextAlignment.LEFT);
        details.addCell(new Paragraph("Market Price of the Procurement").setFont(normalFont)).setTextAlignment(TextAlignment.LEFT);

// --- Next 4 rows: placeholders for items ---
        int maxItems = 4;
        if (requisition.getItems().size() > 4) {
            maxItems = requisition.getItems().size();
            for (int i = 1; i <= maxItems; i++) {
                details.addCell(String.valueOf(i));                                // Item No.
                details.addCell(safe(requisition.getItems().get(i - 1).getDescription()));                   // Description (replace with actual item list if available)
                details.addCell(safe(requisition.getItems().get(i - 1).getQuantity()));                                              // Quantity
                details.addCell(safe(requisition.getItems().get(i - 1).getUnitOfMeasure()));             // Unit of Measure
                details.addCell(formatCurrency(requisition.getItems().get(i - 1).getTotalCost())); // Est. Unit Cost
                details.addCell(formatCurrency(requisition.getItems().get(i - 1).getMarketPrice()));       // Market Price

            }
        } else {
            for (int i = 1; i <= requisition.getItems().size(); i++) {
                details.addCell(String.valueOf(i));                                // Item No.
                details.addCell(safe(requisition.getItems().get(i - 1).getDescription()));                   // Description (replace with actual item list if available)
                details.addCell(safe(requisition.getItems().get(i - 1).getQuantity()));                                              // Quantity
                details.addCell(safe(requisition.getItems().get(i - 1).getUnitOfMeasure()));             // Unit of Measure
                details.addCell(formatCurrency(requisition.getItems().get(i - 1).getTotalCost())); // Est. Unit Cost
                details.addCell(formatCurrency(requisition.getItems().get(i - 1).getMarketPrice()));       // Market Price
            }
            int size = 5 - requisition.getItems().size();

        }

// --- Last row: Currency + Estimated Total Cost ---
        Cell currencyCell = new Cell(1, 5)
                .add(new Paragraph("Currency: " + safe(requisition.getCurrency())).setFont(boldFont).setTextAlignment(TextAlignment.LEFT));
        details.addCell(currencyCell);

        details.addCell(new Paragraph("Estimated Total Cost: "
                + formatCurrency(requisition.getRequestedAmount())).setFont(boldFont).setTextAlignment(TextAlignment.LEFT));

        document.add(details);

        // --- SIGNATURES SECTION ---
        Table signatures = new Table(UnitValue.createPercentArray(new float[]{1, 2, 1, 2}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(10);

// Remove all borders
        signatures.setBorder(Border.NO_BORDER);

// --- First row: headers ---
        Paragraph reqHeader = new Paragraph()
                .add(new Text("(1) Request for Requisition ").setFont(boldFont).setTextAlignment(TextAlignment.LEFT))
                .add(new Text("(Member of user department)").setFont(itallicFont).setTextAlignment(TextAlignment.LEFT));

        Paragraph confHeader = new Paragraph()
                .add(new Text("(2) Confirmation of Request ").setFont(boldFont).setTextAlignment(TextAlignment.LEFT))
                .add(new Text("(Head of user department)").setFont(itallicFont).setTextAlignment(TextAlignment.LEFT));

        signatures.addCell(new Cell(1, 2).add(reqHeader).setBorder(Border.NO_BORDER));
        signatures.addCell(new Cell(1, 2).add(confHeader).setBorder(Border.NO_BORDER));

// --- Helper for dashed line ---
// --- Signature Row ---
        signatures.addCell(new Paragraph("Signature:").setFont(normalFont).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("Signature:").setFont(normalFont).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

// --- Name Row ---
        signatures.addCell(new Paragraph("Name:").setFont(normalFont).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        signatures.addCell(new Paragraph(requisition.getCreatedBy().getFirstName() + " " + requisition.getCreatedBy().getLastName()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        signatures.addCell(new Paragraph("Name:").setFont(normalFont).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        signatures.addCell(new Paragraph(requisition.getApprovedBy().getFirstName() + " " + requisition.getApprovedBy().getLastName()).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));

// --- Title Row ---
        signatures.addCell(new Paragraph("Title:").setFont(normalFont).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        signatures.addCell(new Paragraph("HOD").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        signatures.addCell(new Paragraph("Title:").setFont(normalFont).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        signatures.addCell(new Paragraph("BLO").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));

// --- Date Row ---
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        signatures.addCell(new Paragraph("Date:").setFont(normalFont).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        signatures.addCell(new Paragraph(requisition.getCreatedDate().format(formatter)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        signatures.addCell(new Paragraph("Date:").setFont(normalFont).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        signatures.addCell(new Paragraph(requisition.getApprovedDate().format(formatter)).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));

        document.add(signatures);
        // --- FUNDS AVAILABILITY ---
        document.add(new Paragraph("Availability of funds to be confirmed prior to approval by Accounting Officer:")
                .setFont(itallicFont).setMarginTop(10).setTextAlignment(TextAlignment.LEFT));

        Table funds = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 2, 2}))
                .setWidth(UnitValue.createPercentValue(100));

        funds.addHeaderCell("Department/Section")
                .addHeaderCell("Activity")
                .addHeaderCell("Chart Of Account")
                .addHeaderCell("Balance Remaining By Account Code")
                .addHeaderCell("Balance Remaining By Activity").setFont(boldFont);

        // Placeholder row
        String sectioncode = "(" + requisition.getDeptSection().getANL_CODE() + ")";
        UrcDepartmentAnlDim findDepartmentDetail = deptSectionMergerService.getDepartmentBySectionCode(requisition.getDeptSection().getANL_CODE().trim());
        String dept = findDepartmentDetail.getNAME() + " (" + findDepartmentDetail.getANL_CODE() + ") / ";
        String activities = requisition.getActivity().getName() + " (" + requisition.getActivity().getActivityCode() + ")";
        String coa = requisition.getCoa().getName() + " (" + requisition.getCoa().getCode().trim() + ")";
        funds.addCell(dept + requisition.getDeptSection().getNAME() + " " + sectioncode).setFont(normalFont)
                .addCell(activities).setFont(normalFont).setFont(normalFont)
                .addCell(coa).setFont(normalFont).setFont(normalFont)
                .addCell(formatCurrency(requisition.getAvailableBalanceByCOA().doubleValue())).setFont(normalFont)
                .addCell(formatCurrency(requisition.getAvailableBalanceByActivity().doubleValue())).setFont(normalFont);

        document.add(funds);
        Table fundingApproval = new Table(UnitValue.createPercentArray(new float[]{1, 3}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(20);

// Remove all borders
        fundingApproval.setBorder(Border.NO_BORDER);

// --- Header row ---
        Paragraph headercon = new Paragraph()
                .add(new Text("(3) Confirmation of Funding and Approval to Procure ").setFont(boldFont).setTextAlignment(TextAlignment.LEFT))
                .add(new Text("(Accounting Officer)").setFont(normalFont).setTextAlignment(TextAlignment.LEFT));

        fundingApproval.addCell(new Cell(1, 2).add(headercon).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));

// --- Helper line ---
// --- Signature Row ---
        fundingApproval.addCell(new Paragraph("Signature:").setFont(normalFont).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        fundingApproval.addCell(new Paragraph("").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));

// --- Name Row ---
        fundingApproval.addCell(new Paragraph("Name:").setFont(normalFont).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        fundingApproval.addCell(new Paragraph("").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));

// --- Title Row ---
        fundingApproval.addCell(new Paragraph("Title:").setFont(normalFont).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        fundingApproval.addCell(new Paragraph("").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));

// --- Date Row ---
        fundingApproval.addCell(new Paragraph("Date:").setFont(normalFont).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
        fundingApproval.addCell(new Paragraph("").setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));

        document.add(fundingApproval);

    }

    private String catOfProc(RequisitionData.CatOfProc catp, RequisitionData requisition) {
        String category = "";
        if (requisition != null) {

            if (catp == requisition.getCatOfProc()) {
                category = "Yes";
            } else {
                category = "No";
            }
        }

        return category;
    }

    private String multiYear(RequisitionData.IsMultiYearContract catp, RequisitionData requisition) {
        String category = "";
        if (requisition != null) {

            if (catp == requisition.getIsMultiYearContract()) {
                category = "Yes";
            } else {
                category = "No";
            }
        }

        return category;
    }

    private void generateForm5(Document document, RequisitionData requisition,
            PdfFont boldFont, PdfFont itallicFont, PdfFont normalFont) throws Exception {

        // Official PPDA header
        document.add(new Paragraph("SCHEDULES")
                .setFont(boldFont).setFontSize(11).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("SCHEDULES 1")
                .setFont(normalFont).setFontSize(11).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("Forms")
                .setFont(normalFont).setFontSize(11).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("FORM 5")
                .setFont(boldFont).setFontSize(11).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("Regulation 3(1), 13(3), 15(3), 17(3) 24(2), 53(6), 54(5)")
                .setFont(itallicFont).setFontSize(11).setTextAlignment(TextAlignment.RIGHT));

        document.add(new Paragraph("THE PUBLIC PROCUREMENT AND DISPOSAL OF PUBLIC ASSETS ACT, 2003")
                .setFont(normalFont).setFontSize(11).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("REQUEST FOR APPROVAL OF PROCUREMENT")
                .setFont(boldFont).setFontSize(11).setTextAlignment(TextAlignment.CENTER).setMarginBottom(0));

        // --- PART I: REQUEST BY USER DEPARTMENT ---
        document.add(new Paragraph("PART I: REQUEST BY USER DEPARTMENT FOR APPROVAL OF PROCUREMENT")
                .setFont(boldFont).setMarginTop(0)).setTextAlignment(TextAlignment.CENTER);

        Table detailsTable = new Table(UnitValue.createPercentArray(new float[]{3, 3, 3, 3}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(0);

// --- First row: Procurement Reference Number spans all 4 columns ---
        Cell refCell = new Cell(1, 4) // row span 1, column span 4
                .add(new Paragraph("Procurement Reference Number: "
                        + safe(requisition.getRequisitionNumber()))
                        .setFont(boldFont)).setFontSize(10);
        detailsTable.addCell(refCell);

// --- Second row: headers ---
        detailsTable.addCell(new Cell().add(new Paragraph("Code of Procuring and Disposing Entity").setFont(normalFont))).setFontSize(10).setTextAlignment(TextAlignment.LEFT);
        detailsTable.addCell(new Cell().add(new Paragraph("Supplies/Works/Non-Consultancy Services").setFont(normalFont))).setFontSize(10).setTextAlignment(TextAlignment.LEFT);
        detailsTable.addCell(new Cell().add(new Paragraph("Financial Year").setFont(normalFont))).setFontSize(10).setTextAlignment(TextAlignment.LEFT);
        detailsTable.addCell(new Cell().add(new Paragraph("Sequence Number").setFont(normalFont)).setTextAlignment(TextAlignment.LEFT));

// --- Third row: values ---
        detailsTable.addCell(safe(requisition.getPdeCode())).setFontSize(10);
        detailsTable.addCell(safe(requisition.getProcType())).setFontSize(10);
        detailsTable.addCell(safe(requisition.getBudget().getFinancialYear())).setFontSize(10);
        detailsTable.addCell(safe(requisition.getRequisitionNumber())).setFontSize(10);

        document.add(detailsTable).setFont(normalFont).setFontSize(10);

        document.add(new Paragraph("Category of procurement and budget")
                .setFont(normalFont).setFontSize(10).setTextAlignment(TextAlignment.LEFT).setMarginBottom(0));

        Table procCatTable = new Table(UnitValue.createPercentArray(new float[]{3, 3, 3, 3}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(0);

// --- Second row: headers ---
        procCatTable.addCell(new Cell().add(new Paragraph("Recurrent Budget").setFont(normalFont)).setTextAlignment(TextAlignment.LEFT));
        procCatTable.addCell(new Cell().add(new Paragraph("Development Budget").setFont(normalFont)).setTextAlignment(TextAlignment.LEFT));
        procCatTable.addCell(new Cell().add(new Paragraph("Project Code").setFont(normalFont)).setTextAlignment(TextAlignment.LEFT));
        procCatTable.addCell(new Cell().add(new Paragraph("Project Title").setFont(normalFont)).setTextAlignment(TextAlignment.LEFT));

// --- Third row: values ---
        procCatTable.addCell(safe(catOfProc(RequisitionData.CatOfProc.RECURRENT_BUDGET, requisition))).setTextAlignment(TextAlignment.LEFT);
        procCatTable.addCell(safe(catOfProc(RequisitionData.CatOfProc.DEVELOPMENT_BUDGET, requisition))).setTextAlignment(TextAlignment.LEFT);
        procCatTable.addCell(safe(requisition.getProjectCode())).setTextAlignment(TextAlignment.LEFT);
        procCatTable.addCell(safe(requisition.getProjectTitle())).setTextAlignment(TextAlignment.LEFT);

        document.add(procCatTable).setFontSize(10);

        // --- Multi-year contracting info ---
        Table multiYearTable = new Table(UnitValue.createPercentArray(new float[]{3, 3, 3, 3}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(0);

// --- Second row: headers ---
        multiYearTable.addCell(new Cell().add(new Paragraph("Required Resources (UGX Bn) Year One").setFont(normalFont)).setTextAlignment(TextAlignment.LEFT));
        multiYearTable.addCell(new Cell().add(new Paragraph("Required Resources (UGX Bn) Year Two").setFont(normalFont)).setTextAlignment(TextAlignment.LEFT));
        multiYearTable.addCell(new Cell().add(new Paragraph("Required Resources (UGX Bn) Year Three").setTextAlignment(TextAlignment.LEFT).setFont(normalFont)));
        multiYearTable.addCell(new Cell().add(new Paragraph("Required Resources (UGX Bn) Year Four").setTextAlignment(TextAlignment.LEFT).setFont(normalFont)));

// --- Third row: values ---
        multiYearTable.addCell(safe(multiYear(RequisitionData.IsMultiYearContract.YEAR_ONE, requisition))).setTextAlignment(TextAlignment.LEFT);
        multiYearTable.addCell(safe(multiYear(RequisitionData.IsMultiYearContract.YEAR_TWO, requisition))).setTextAlignment(TextAlignment.LEFT);
        multiYearTable.addCell(safe(multiYear(RequisitionData.IsMultiYearContract.YEAR_THREE, requisition))).setTextAlignment(TextAlignment.LEFT);
        multiYearTable.addCell(safe(multiYear(RequisitionData.IsMultiYearContract.YEAR_FOUR, requisition))).setTextAlignment(TextAlignment.LEFT);

        document.add(new Paragraph("Is procurement going to result into multiyear contracting?").setTextAlignment(TextAlignment.LEFT)
                .setFont(normalFont)).setFontSize(10).setTextAlignment(TextAlignment.LEFT);
        document.add(multiYearTable).setBottomMargin(25);
        document.add(new Paragraph(""));

        // --- PARTICULARS OF PROCUREMENT ---
        Table particulars = new Table(UnitValue.createPercentArray(new float[]{3, 5}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(25);

// --- First row: Section title spans 2 columns ---
        Cell partHeader = new Cell(1, 2)
                .add(new Paragraph("Particulars of Procurement").setFont(boldFont)).setTextAlignment(TextAlignment.LEFT);
        particulars.addCell(partHeader);

// --- Subject of Procurement ---
        particulars.addCell(new Paragraph("Subject of Procurement").setFont(normalFont)).setTextAlignment(TextAlignment.LEFT);
        particulars.addCell(new Paragraph(safe(requisition.getSubjectOfProcurement()))).setTextAlignment(TextAlignment.LEFT);

// --- Procurement Plan Reference ---
        particulars.addCell(new Paragraph("Procurement Plan Reference").setFont(normalFont)).setTextAlignment(TextAlignment.LEFT);
        particulars.addCell(new Paragraph(safe(requisition.getProcurementPlanRef()))).setTextAlignment(TextAlignment.LEFT);

// --- Location for Delivery ---
        particulars.addCell(new Paragraph("Location for Delivery").setFont(normalFont)).setTextAlignment(TextAlignment.LEFT);
        particulars.addCell(new Paragraph(safe(requisition.getDeliveryLocation()))).setTextAlignment(TextAlignment.LEFT);

// --- Date Required ---
        particulars.addCell(new Paragraph("Date Required").setFont(normalFont)).setTextAlignment(TextAlignment.LEFT);
        particulars.addCell(new Paragraph(safe(requisition.getDateRequired()))).setTextAlignment(TextAlignment.LEFT);

        document.add(particulars).setBottomMargin(25);

        // --- DETAILS TABLE ---
        Table details = new Table(UnitValue.createPercentArray(new float[]{1, 3, 1, 2, 2, 2}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(15);

// --- First row: Section title spans all 6 columns ---
        Cell header = new Cell(1, 6)
                .add(new Paragraph("Details Relating to the Procurement").setFont(boldFont)).setTextAlignment(TextAlignment.LEFT);
        details.addCell(header);

// --- Second row: Column headers ---
        Paragraph descHeader = new Paragraph()
                .add(new Text("Description ").setFont(normalFont))
                .add(new Text("(Attach specifications, terms of reference or scope of works)")
                        .setFont(itallicFont)).setTextAlignment(TextAlignment.LEFT);  // <- italic font
        details.addCell(new Paragraph("Item No.").setFont(normalFont)).setTextAlignment(TextAlignment.LEFT);
        details.addCell(descHeader);
        details.addCell(new Paragraph("Quantity").setFont(normalFont)).setTextAlignment(TextAlignment.LEFT);
        details.addCell(new Paragraph("Unit of Measure").setFont(normalFont)).setTextAlignment(TextAlignment.LEFT);
        details.addCell(new Paragraph("Estimated Unit Cost").setFont(normalFont)).setTextAlignment(TextAlignment.LEFT);
        details.addCell(new Paragraph("Market Price of the Procurement").setFont(normalFont)).setTextAlignment(TextAlignment.LEFT);

// --- Next 4 rows: placeholders for items ---
        int maxItems = 4;
        if (requisition.getItems().size() > 4) {
            maxItems = requisition.getItems().size();
            for (int i = 1; i <= maxItems; i++) {
                details.addCell(String.valueOf(i));                                // Item No.
                details.addCell(safe(requisition.getItems().get(i - 1).getDescription()));                   // Description (replace with actual item list if available)
                details.addCell(safe(requisition.getItems().get(i - 1).getQuantity()));                                              // Quantity
                details.addCell(safe(requisition.getItems().get(i - 1).getUnitOfMeasure()));             // Unit of Measure
                details.addCell(formatCurrency(requisition.getItems().get(i - 1).getTotalCost())); // Est. Unit Cost
                details.addCell(formatCurrency(requisition.getItems().get(i - 1).getMarketPrice()));
                // System.out.println(requisition.getItems().toString()+ ":Items 1");// Market Price

            }
        } else {
            for (int i = 1; i <= requisition.getItems().size(); i++) {
                details.addCell(String.valueOf(i));                                // Item No.
                details.addCell(safe(requisition.getItems().get(i - 1).getDescription()));                   // Description (replace with actual item list if available)
                details.addCell(safe(requisition.getItems().get(i - 1).getQuantity()));                                              // Quantity
                details.addCell(safe(requisition.getItems().get(i - 1).getUnitOfMeasure()));             // Unit of Measure
                details.addCell(formatCurrency(requisition.getItems().get(i - 1).getTotalCost())); // Est. Unit Cost
                details.addCell(formatCurrency(requisition.getItems().get(i - 1).getMarketPrice()));
                //System.out.println(requisition.getItems().toString()+ ":Items 2");// Market Price
            }

        }

// --- Last row: Currency + Estimated Total Cost ---
        Cell currencyCell = new Cell(1, 5)
                .add(new Paragraph("Currency: " + safe(requisition.getCurrency())).setTextAlignment(TextAlignment.LEFT).setFont(boldFont));
        details.addCell(currencyCell);

        details.addCell(new Paragraph("Estimated Total Cost: "
                + formatCurrency(requisition.getRequestedAmount())).setFont(boldFont)).setTextAlignment(TextAlignment.LEFT);

        document.add(details);

        // --- SIGNATURES SECTION ---
        Table signatures = new Table(UnitValue.createPercentArray(new float[]{1, 2, 1, 2}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(10);

// Remove all borders
        signatures.setBorder(Border.NO_BORDER);

// --- First row: headers ---
        Paragraph reqHeader = new Paragraph()
                .add(new Text("(1) Request for Procurement ").setFont(boldFont))
                .add(new Text("(Member of user department)").setFont(itallicFont)).setTextAlignment(TextAlignment.LEFT);

        Paragraph confHeader = new Paragraph()
                .add(new Text("(2) Confirmation of Request ").setFont(boldFont))
                .add(new Text("(Head of user department)").setFont(itallicFont)).setTextAlignment(TextAlignment.LEFT);

        signatures.addCell(new Cell(1, 2).add(reqHeader).setBorder(Border.NO_BORDER));
        signatures.addCell(new Cell(1, 2).add(confHeader).setBorder(Border.NO_BORDER));

// --- Helper for dashed line ---
// --- Signature Row ---
        signatures.addCell(new Paragraph("Signature:").setFont(normalFont).setBorder(Border.NO_BORDER)).setTextAlignment(TextAlignment.LEFT);
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("Signature:").setFont(normalFont).setBorder(Border.NO_BORDER)).setTextAlignment(TextAlignment.LEFT);
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

// --- Name Row ---
        signatures.addCell(new Paragraph("Name:").setFont(normalFont).setBorder(Border.NO_BORDER)).setTextAlignment(TextAlignment.LEFT);
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("Name:").setFont(normalFont).setBorder(Border.NO_BORDER)).setTextAlignment(TextAlignment.LEFT);
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

// --- Title Row ---
        signatures.addCell(new Paragraph("Title:").setFont(normalFont).setBorder(Border.NO_BORDER)).setTextAlignment(TextAlignment.LEFT);
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("Title:").setFont(normalFont).setBorder(Border.NO_BORDER)).setTextAlignment(TextAlignment.LEFT);
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

// --- Date Row ---
        signatures.addCell(new Paragraph("Date:").setFont(normalFont).setBorder(Border.NO_BORDER)).setTextAlignment(TextAlignment.LEFT);
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));
        signatures.addCell(new Paragraph("Date:").setFont(normalFont).setBorder(Border.NO_BORDER)).setTextAlignment(TextAlignment.LEFT);
        signatures.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

        document.add(signatures);
        // --- FUNDS AVAILABILITY ---
        document.add(new Paragraph("Availability of funds to be confirmed prior to approval by Accounting Officer:")
                .setFont(itallicFont).setMarginTop(10)).setTextAlignment(TextAlignment.LEFT);
        // Placeholder row
        String sectioncode = "(" + requisition.getDeptSection().getANL_CODE() + ")";
        UrcDepartmentAnlDim findDepartmentDetail = deptSectionMergerService.getDepartmentBySectionCode(requisition.getDeptSection().getANL_CODE().trim());
        String dept = findDepartmentDetail.getNAME() + " (" + findDepartmentDetail.getANL_CODE() + ") / ";
        String activities = requisition.getActivity().getName() + " (" + requisition.getActivity().getActivityCode() + ")";
        String coa = requisition.getCoa().getName() + " (" + requisition.getCoa().getCode().trim() + ")";

        Table funds = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 2, 2}))
                .setWidth(UnitValue.createPercentValue(100));

        funds.addHeaderCell("Vote/Head No")
                .addHeaderCell("Programme")
                .addHeaderCell("Sub-programme")
                .addHeaderCell("Item")
                .addHeaderCell("Balance Remaining").setFont(boldFont).setTextAlignment(TextAlignment.LEFT);

        // Placeholder row
        funds.addCell("1").addCell("").addCell("").addCell(requisition.getSubjectOfProcurement()).addCell("");

        document.add(funds);
        document.add(new Paragraph(""));
        Table funds2 = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 2, 2}))
                .setWidth(UnitValue.createPercentValue(100));

        funds2.addHeaderCell(new Paragraph("Department/Section").setFont(boldFont))
                .addHeaderCell(new Paragraph("Activity").setFont(boldFont))
                .addHeaderCell(new Paragraph("Chart Of Account").setFont(boldFont))
                .addHeaderCell(new Paragraph("Balance Remaining By Account Code").setFont(boldFont))
                .addHeaderCell(new Paragraph("Balance Remaining By Activity").setFont(boldFont)).setTextAlignment(TextAlignment.LEFT);

        funds2.addCell(dept + requisition.getDeptSection().getNAME() + " " + sectioncode).setFont(normalFont)
                .addCell(activities).setFont(normalFont).setFont(normalFont)
                .addCell(coa).setFont(normalFont).setFont(normalFont)
                .addCell(formatCurrency(requisition.getAvailableBalanceByCOA().doubleValue())).setFont(normalFont)
                .addCell(formatCurrency(requisition.getAvailableBalanceByActivity().doubleValue())).setFont(normalFont).setTextAlignment(TextAlignment.LEFT);

        document.add(funds2);
        Table fundingApproval = new Table(UnitValue.createPercentArray(new float[]{1, 3}))
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(20);

// Remove all borders
        fundingApproval.setBorder(Border.NO_BORDER);

// --- Header row ---
        Paragraph headercon = new Paragraph()
                .add(new Text("(3) Confirmation of Funding and Approval to Procure ").setFont(boldFont))
                .add(new Text("(Accounting Officer)").setFont(normalFont)).setTextAlignment(TextAlignment.LEFT);

        fundingApproval.addCell(new Cell(1, 2).add(headercon).setBorder(Border.NO_BORDER));

// --- Helper line ---
// --- Signature Row ---
        fundingApproval.addCell(new Paragraph("Signature:").setFont(normalFont).setBorder(Border.NO_BORDER)).setTextAlignment(TextAlignment.LEFT);
        fundingApproval.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

// --- Name Row ---
        fundingApproval.addCell(new Paragraph("Name:").setFont(normalFont).setBorder(Border.NO_BORDER)).setTextAlignment(TextAlignment.LEFT);
        fundingApproval.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

// --- Title Row ---
        fundingApproval.addCell(new Paragraph("Title:").setFont(normalFont).setBorder(Border.NO_BORDER)).setTextAlignment(TextAlignment.LEFT);
        fundingApproval.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

// --- Date Row ---
        fundingApproval.addCell(new Paragraph("Date:").setFont(normalFont).setBorder(Border.NO_BORDER));
        fundingApproval.addCell(new Paragraph("").setBorder(Border.NO_BORDER));

        document.add(fundingApproval);

    }

    private void generateForm48(Document document, RequisitionData requisition,
            PdfFont titleFont, PdfFont headerFont, PdfFont normalFont) throws Exception {
        // Official PPDA header
        document.add(new Paragraph("REPUBLIC OF UGANDA")
                .setFont(titleFont).setFontSize(16).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("PUBLIC PROCUREMENT AND DISPOSAL OF PUBLIC ASSETS AUTHORITY")
                .setFont(headerFont).setFontSize(12).setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("FORM 48")
                .setFont(titleFont).setFontSize(18).setTextAlignment(TextAlignment.CENTER).setMarginBottom(0));

        document.add(new Paragraph("LOCAL PURCHASE ORDER")
                .setFont(headerFont).setFontSize(14).setTextAlignment(TextAlignment.CENTER).setMarginBottom(0));

        // LPO details
        Table detailsTable = new Table(UnitValue.createPercentArray(new float[]{3, 2}))
                .setWidth(UnitValue.createPercentValue(100)).setMarginBottom(0);

        detailsTable.addCell("LPO Number:").addCell(requisition.getRequisitionNumber()).setTextAlignment(TextAlignment.LEFT);
        detailsTable.addCell("Date:").addCell(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).setTextAlignment(TextAlignment.LEFT);
        detailsTable.addCell("Account Code:").addCell(requisition.getActivity().getActivityCode()).setTextAlignment(TextAlignment.LEFT);
        detailsTable.addCell("Activity:").addCell(requisition.getActivity().getActivityCode() + " - " + requisition.getActivity().getName()).setTextAlignment(TextAlignment.LEFT);

        document.add(detailsTable);

        // Order items
        document.add(new Paragraph("ORDER DETAILS").setFont(headerFont).setMarginTop(20)).setTextAlignment(TextAlignment.LEFT);

        Table orderTable = new Table(UnitValue.createPercentArray(new float[]{1, 3, 1, 1, 1}))
                .setWidth(UnitValue.createPercentValue(100)).setMarginBottom(20);

        orderTable.addHeaderCell("Item No.").addHeaderCell("Description").addHeaderCell("Quantity")
                .addHeaderCell("Unit Price").addHeaderCell("Total Amount").setTextAlignment(TextAlignment.LEFT);

        orderTable.addCell("1").addCell(requisition.getSubjectOfProcurement())
                .addCell("1").addCell(formatCurrency(requisition.getRequestedAmount()))
                .addCell(formatCurrency(requisition.getRequestedAmount())).setTextAlignment(TextAlignment.LEFT);

        document.add(orderTable);

        // Delivery terms
        // LPO signature section
        document.add(new Paragraph("AUTHORIZATION").setFont(headerFont).setMarginTop(30)).setTextAlignment(TextAlignment.LEFT);

        Table lpoSignatureTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100)).setMarginTop(20);

        lpoSignatureTable.addCell("Issued By:\n\n\n_________________\nName & Signature\nDate: ___________");
        lpoSignatureTable.addCell("Supplier Acceptance:\n\n\n_________________\nName & Signature\nDate: ___________");

        document.add(lpoSignatureTable);

        // Footer
        document.add(new Paragraph("This Local Purchase Order is issued in accordance with the PPDA Act 2003 and Regulations 2014.")
                .setFont(normalFont).setFontSize(8).setTextAlignment(TextAlignment.CENTER).setMarginTop(30)).setTextAlignment(TextAlignment.LEFT);
    }

    private void addSignatureSection(Document document, PdfFont headerFont) {
        try {
            document.add(new Paragraph("AUTHORIZATION").setFont(headerFont).setMarginTop(30));

            Table signatureTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                    .setWidth(UnitValue.createPercentValue(100)).setMarginTop(20);

            signatureTable.addCell("Prepared By:\n\n\n_________________\nName & Signature\nDate: ___________");
            signatureTable.addCell("Reviewed By:\n\n\n_________________\nName & Signature\nDate: ___________");
            signatureTable.addCell("Approved By:\n\n\n_________________\nName & Signature\nDate: ___________");

            document.add(signatureTable);

            // Footer
            document.add(new Paragraph("This form is issued in accordance with the PPDA Regulations 2014.")
                    .setFont(PdfFontFactory.createFont()).setFontSize(8).setTextAlignment(TextAlignment.CENTER).setMarginTop(20));
        } catch (IOException ex) {
            Logger.getLogger(PPDARequisitionDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getLastTwoChars(String input) {
        if (input == null || input.length() < 2) {
            return input; // return as is if shorter than 2 chars
        }
        return input.substring(input.length() - 2);
    }

    private String formatCurrency(Double amount) {
        if (amount == null) {
            return "0.0";
        }
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(1);
        return formatter.format(amount);
    }

    private String safe(Object value) {
        return value == null ? "" : value.toString();
    }
}
