package com.fouri.commonexcelmodule.utils;

import javax.faces.event.ActionEvent;

import oracle.adf.share.logging.ADFLogger;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.binding.OperationBinding;

public class PrismUtils {
    public static final String CREATE_MODE = "create";
    public static final String MODE = "mode";
    public static final String EDIT_MODE = "edit";
    public static final ADFLogger _log = ADFLogger.createADFLogger(PrismUtils.class);
    public static final String DELETE_MODE = "deleteMode";

    public PrismUtils() {
        super();
    }

    /**
     * method to create a blank row in vo
     * @param actionEvent
     * @param createInsertOp
     * @param richPopup
     */
    public static void createInsert(ActionEvent actionEvent, String createInsertOp, RichPopup richPopup) {
        _log.fine("Enter into createInsert method");
        ADFUtils.getPageFlowScope().put(MODE, CREATE_MODE);
        ADFUtils.findOperation(createInsertOp).execute();
        if (richPopup != null && richPopup.getChildren() != null) {
            JSFUtils.showPopup(richPopup);
        }
        _log.fine("Exit from  createInsert method");
    }

    /**
     * method to delete a record
     * @param actionEvent
     * @param deleteOp
     * @param localBundle
     * @param localeMsg
     */
    public static void delete(ActionEvent actionEvent, String deleteOp, String localBundle, String localeMsg) {
        _log.fine("Enter into delete method");
        OperationBinding bindings = ADFUtils.findOperation(deleteOp);
        bindings.execute();
        //TODO
        //        if (bindings.getErrors().isEmpty()) {
        //            msg = JSFUtils.getResourceBundleString(localBundle, localeMsg); //deleted, please click on save button
        //            JSFUtils.addFacesInformationMessage(msg);
        //        } else {
        //            msg = JSFUtils.getResourceBundleString(localBundle, localeMsg);
        //            JSFUtils.addFacesErrorMessage(msg);
        //        }
        _log.fine("Exit from  delete method");
    }

    /**
     * method to edit a row
     * @param actionEvent
     * @param richPopup
     */
    public static void editRow(ActionEvent actionEvent, RichPopup richPopup) {
        _log.fine("Enter into editRow method");
        ADFUtils.getPageFlowScope().put(MODE, EDIT_MODE);
        if (richPopup != null && richPopup.getChildren() != null) {
            JSFUtils.showPopup(richPopup);
            AdfFacesContext.getCurrentInstance().addPartialTarget(richPopup);
        }
        _log.fine("Exit from  editRow method");
    }

    public static void deleteConfirmation(ActionEvent actionEvent, RichPopup richPopup) {
        String deleteOpertion = null;
        if (ADFUtils.getPageFlowScope().get(DELETE_MODE) != null) {
            deleteOpertion = (String) ADFUtils.getPageFlowScope().get(DELETE_MODE);
            ADFUtils.findOperation(deleteOpertion).execute();
            JSFUtils.hidePopup(richPopup);
        }
    }
}
