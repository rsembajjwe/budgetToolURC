package com.methaltech.application.data.bgtool.service;

import com.methaltech.application.data.ProcClass;
import com.methaltech.application.data.ProcurementMethodList;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class procurementMethodProcessorService {

    private final ProcurementTypeService sampleProcurementTypeService;

    public procurementMethodProcessorService( ProcurementTypeService sampleProcurementTypeService) {
        this.sampleProcurementTypeService = sampleProcurementTypeService;
    }

    public ProcurementMethodList setProcurementMethod(BigDecimal budget) {
        ProcurementMethodList procMethod = null;
        List<ProcClass> list = new ArrayList();
        list.add(ProcClass.Supplies);
        list.add(ProcClass.Non_Consultancy);
        list.add(ProcClass.Works);
        list.add(ProcClass.Consultancy);
        list.add(ProcClass.Other);

        List<ProcurementMethodList> listMethod = new ArrayList();
        listMethod.add(ProcurementMethodList.Open_Bidding);
        listMethod.add(ProcurementMethodList.Restricted_Bidding);
        listMethod.add(ProcurementMethodList.Request_for_Quotations);
        listMethod.add(ProcurementMethodList.Micro_Procurement);
        listMethod.add(ProcurementMethodList.Selection_Method_Threshold_in_UGX_Request_for_Proposals_with_Expression_of_Interest);
        listMethod.add(ProcurementMethodList.Selection_Method_Threshold_in_UGX_Request_for_Proposals_without_Expression_of_Interest);

        for (ProcClass tp : list) {
            if (tp.equals(ProcClass.Supplies) || tp.equals(ProcClass.Non_Consultancy)) {
                if (budget.doubleValue() > 200000000) {
                    procMethod = ProcurementMethodList.Open_Bidding;
                }
                if (budget.doubleValue() >= 100000000 && budget.doubleValue() < 200000000) {
                    procMethod = ProcurementMethodList.Restricted_Bidding;
                }
                if (budget.doubleValue() >= 5000000 && budget.doubleValue() < 100000000) {
                    procMethod = ProcurementMethodList.Request_for_Quotations;
                }

                if (budget.doubleValue() < 5000000) {
                    procMethod = ProcurementMethodList.Micro_Procurement;
                }
            } else if (tp.equals(ProcClass.Works)) {
                if (budget.doubleValue() > 500000000) {
                    procMethod = ProcurementMethodList.Open_Bidding;
                }
                if (budget.doubleValue() >= 200000000 && budget.doubleValue() < 500000000) {
                    procMethod = ProcurementMethodList.Restricted_Bidding;
                }
                if (budget.doubleValue() >= 10000000 && budget.doubleValue() < 200000000) {
                    procMethod = ProcurementMethodList.Request_for_Quotations;
                }

                if (budget.doubleValue() < 10000000) {
                    procMethod = ProcurementMethodList.Micro_Procurement;
                }
            } else if (tp.equals(ProcClass.Consultancy)) {
                if (budget.doubleValue() >= 200000000) {
                    procMethod = ProcurementMethodList.Selection_Method_Threshold_in_UGX_Request_for_Proposals_with_Expression_of_Interest;
                }
                if (budget.doubleValue() >= 500000000 && budget.doubleValue() < 200000000) {
                    procMethod = ProcurementMethodList.Selection_Method_Threshold_in_UGX_Request_for_Proposals_without_Expression_of_Interest;
                }

            }
        }
return procMethod;
    }
}
