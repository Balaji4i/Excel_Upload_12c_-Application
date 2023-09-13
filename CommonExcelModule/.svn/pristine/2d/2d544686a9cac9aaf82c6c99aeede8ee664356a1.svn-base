package com.fouri.commonexcelmodule.view;

import com.fouri.commonexcelmodule.model.am.AppModuleImpl;
import com.fouri.commonexcelmodule.utils.ADFUtils;

import com.fouri.commonexcelmodule.utils.JSFUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.math.BigDecimal;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.sql.Struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import java.util.TreeMap;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.faces.event.ValueChangeEvent;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCDefBase;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.model.binding.DefinitionFactory;
import oracle.adf.view.rich.component.rich.input.RichInputFile;

import oracle.adf.view.rich.component.rich.layout.RichPanelBox;
import oracle.adf.view.rich.component.rich.layout.RichPanelGroupLayout;

import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.binding.OperationBinding;

import oracle.jbo.AttributeDef;
import oracle.jbo.Row;
import oracle.jbo.ViewCriteria;
import oracle.jbo.ViewCriteriaRow;
import oracle.jbo.ViewObject;

import oracle.jbo.domain.BlobDomain;

import oracle.jbo.uicli.binding.JUCtrlHierBinding;
import oracle.jbo.uicli.binding.JUCtrlHierDef;
import oracle.jbo.uicli.binding.JUCtrlHierTypeBinding;
import oracle.jbo.uicli.binding.JUCtrlValueBinding;
import oracle.jbo.uicli.binding.JUCtrlValueDef;
import oracle.jbo.uicli.binding.JUIteratorDef;
import oracle.jbo.uicli.mom.JUMetaObjectManager;

import oracle.jbo.uicli.mom.JUTags;

import oracle.jdbc.OracleTypes;

import oracle.sql.NUMBER;
import oracle.sql.StructDescriptor;

import org.apache.myfaces.trinidad.model.UploadedFile;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.IOUtils;
import org.apache.poi.ss.usermodel.Cell;   
import org.apache.poi.ss.usermodel.FillPatternType;  
import org.apache.poi.ss.usermodel.IndexedColors;  

import java.util.HashSet;
import java.util.Set;

public class CommonExcelModule {
    private RichInputFile fileUploadBinding;
    private RichPanelGroupLayout interfacePGBinding;
    private RichPanelBox interfacePBBinding;
    private RichPanelGroupLayout exportPGL;
    private static final String DATA_CONTROL = "AppModuleDataControl";
    private static final String DYNAMIC_TABLE_ROVO = "DynamicTableROVO1";
    private JUCtrlHierBinding treeBinding;
    private RichPanelBox interfaceDataPBBinding;
    private ArrayList<String> promptName;
    private ArrayList<String> mandatory;
    private String hiddenOutput; 

    public CommonExcelModule() {
        super();
    }
    
    private Map downloadMap = new HashMap<String,ArrayList<String>>();
    private ArrayList sheetName;
    private String lookupType;
    private String defaultTabAll;
    private InputStream inputstream;
    private UploadedFile file;
    private String fileName;
    private BlobDomain blobObj;
    private String exportedSelectedType;

    public void actionListener(ActionEvent actionEvent) {
        // Add event code here...
        String parentIfaceId = ADFUtils.evaluateEL("#{bindings.ifaceId1.inputValue}") != null ? ADFUtils.evaluateEL("#{bindings.ifaceId1.inputValue}").toString() : null;
        String ifaceId = ADFUtils.evaluateEL("#{bindings.childIfaceId.inputValue}") != null ? ADFUtils.evaluateEL("#{bindings.childIfaceId.inputValue}").toString() : null;
        String action = ADFUtils.evaluateEL("#{bindings.action.inputValue}") != null ? ADFUtils.evaluateEL("#{bindings.action.inputValue}").toString() : null; 
        if(action!=null && action.equalsIgnoreCase("U")){
            if(inputstream==null && file==null){
                fileUploadBinding.setValid(false);
                FacesContext.getCurrentInstance().addMessage( fileUploadBinding.getClientId(FacesContext.getCurrentInstance())
                                                                        , new FacesMessage(FacesMessage.SEVERITY_ERROR
                                                                                           , "File Missing"
                                                                                           , "Please Upload the file"
                                                                                           )
                                                                        );
            }
            else{
                try{
                    Map processedData = processExcel(getDefaultTabCount(parentIfaceId,ifaceId));
                    OperationBinding operationBinding = ADFUtils.findOperation("uploadData");
                    operationBinding.getParamsMap().put("ifaceId", ifaceId);
                    operationBinding.getParamsMap().put("parentIfaceId", parentIfaceId);
                    operationBinding.getParamsMap().put("processedData", processedData);
                    String batchId = (String)operationBinding.execute();
                    AdfFacesContext adfFacesContext = AdfFacesContext.getCurrentInstance();
                    if(batchId.equalsIgnoreCase("sheetError")){
                        file = null;
                        inputstream = null;
                        fileUploadBinding.resetValue();
                        fileUploadBinding.setValid(true);
                        adfFacesContext.addPartialTarget(fileUploadBinding);
                        String expMsg =
                                    "<html><body>" + "The upload excel does not contain the sheets according to the child interface name selected!!" +
                                    "<br/><br/>" + "Please upload the correct excel or choose the appropriate child interface name!!" +
                                    "<br/><br/>" + "<b>Note: </b>Don't Remove or Add any sheet from the downloaded template!!" +
                                    "</body></html>";
                        JSFUtils.addFacesErrorMessage(expMsg); 
                    }
                    else if(batchId.equalsIgnoreCase("issueinupload")){
                        file = null;
                        inputstream = null;
                        fileUploadBinding.resetValue();
                        fileUploadBinding.setValid(true);
                        adfFacesContext.addPartialTarget(fileUploadBinding);
                        String expMsg =
                                    "<html><body>" + "There is some issue while uploading the file!! Please contact the support team!!" +
                                    "</body></html>";
                        JSFUtils.addFacesErrorMessage(expMsg); 
                    }
                    else{
                            operationBinding = ADFUtils.findOperation("CreateInsert");
                            operationBinding.execute();
                            ViewObject vo = ADFUtils.findIterator("xxdmInterfaceVO1Iterator").getViewObject();
                            vo.getCurrentRow().setAttribute("BatchId", batchId);
                            vo.getCurrentRow().setAttribute("FileName", fileName);
                            vo.getCurrentRow().setAttribute("IfaceId", ifaceId!=null ? ifaceId : parentIfaceId);
                            vo.getCurrentRow().setAttribute("Status", "U");
                            vo.getCurrentRow().setAttribute("UploadedFile", blobObj);
                            operationBinding = ADFUtils.findOperation("Commit");
                            operationBinding.execute();
                            vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("ByBatchId"));
                            vo.setNamedWhereClauseParam("b_batchId", batchId);
                            vo.executeQuery();
                            interfacePBBinding.setRendered(true);
                            adfFacesContext.addPartialTarget(interfacePGBinding);
                            file = null;
                            inputstream = null;
                            fileUploadBinding.resetValue();
                            fileUploadBinding.setValid(true);
                            JSFUtils.addFacesInformationMessage("Uploaded Successfully");
                            ADFUtils.setEL("#{bindings.uploadedBatchId.inputValue}", batchId);
                            ADFUtils.setEL("#{bindings.validateProcessIfaceId.inputValue}",ifaceId!=null ? ifaceId : parentIfaceId);
                            ADFUtils.setEL("#{bindings.validatedBatchId.inputValue}", batchId);
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                    System.out.println("Exception in upload excel:"+e.getMessage());
                    JSFUtils.addFacesErrorMessage("There is an issue in the Upload");
                }
            }
        }
    }
    
    public Map<String,ArrayList<String>> prepareDownload(String ifaceId,String parentIfaceId,boolean tempData){
        Map returnMap = new HashMap<String,ArrayList<String>>();
        sheetName = new ArrayList<String>();
        ViewObject vo = ADFUtils.findIterator("InterfaceROVO1Iterator").getViewObject();
        if(!tempData){
            if(ifaceId!=null){
                vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("byIfaceId"),false);
                vo.setNamedWhereClauseParam("b_ifaceId", ifaceId);
            }
            else{
                vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("byParentIfaceId"),false);
                vo.setNamedWhereClauseParam("b_parentIfaceId", parentIfaceId);
            }
        }
        else{
            if(parentIfaceId!=null){
                vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("byIfaceId"),false);
                vo.setNamedWhereClauseParam("b_ifaceId", ifaceId);
            }
            else{
                vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("byParentIfaceId"),false);
                vo.setNamedWhereClauseParam("b_parentIfaceId", ifaceId);
            }
        }
        vo.setRangeSize(-1);
        vo.executeQuery();
        Row[] rows = vo.getAllRowsInRange();
        ViewObject mappingvo;
        Row[] innerRows;
        Row row;
        Row innerRow;
        ArrayList listObj;
        ArrayList columnList;
        ArrayList mandatoryList;
        String lookupTypes;
        String defaultTabLookup;
        Object[] defaultTab = null;
        ArrayList lookupTypesArr = new ArrayList<String>();
        Set defaultTabLookupSet = new HashSet<String>();
        for(int i=0;i<rows.length;i++){
            listObj = new ArrayList<String>();
            columnList = new ArrayList<String>();
            mandatoryList = new ArrayList<String>();
            row = rows[i];
            lookupTypes = row.getAttribute("LookupType")!=null ? row.getAttribute("LookupType").toString() : null;
            defaultTabLookup = row.getAttribute("DefaultTab")!=null ? row.getAttribute("DefaultTab").toString() : null;
            String[] arr;
            if(lookupTypes!=null){
                    arr = lookupTypes.split(",");
                    for(int j=0;j<arr.length;j++){
                        lookupTypesArr.add(arr[j]);                    
                    }
            }
            if(defaultTabLookup!=null){
                    arr = defaultTabLookup.split(",");
                    for(int j=0;j<arr.length;j++){
                        defaultTabLookupSet.add(arr[j]);                    
                    }
                defaultTab = defaultTabLookupSet.toArray();
            }
            mappingvo = ADFUtils.findIterator("xxdmexcelstgmappingROVO1Iterator").getViewObject();
            mappingvo.applyViewCriteria(mappingvo.getViewCriteriaManager().getViewCriteria("forDownload"));
            mappingvo.setNamedWhereClauseParam("b_ifaceId", row.getAttribute("IfaceId"));
            mappingvo.setRangeSize(-1);
            mappingvo.executeQuery();
            innerRows = mappingvo.getAllRowsInRange();
            for(int count=0;count<innerRows.length;count++){
                innerRow = innerRows[count];
                listObj.add((String)innerRow.getAttribute("PromptName")); 
                columnList.add((String)innerRow.getAttribute("StgColumnName")); 
                mandatoryList.add((String)innerRow.getAttribute("Mandatory"));
            }
            returnMap.put((String)row.getAttribute("StagingTable"), listObj);
            returnMap.put((String)row.getAttribute("StagingTable")+"_Col", columnList);
            returnMap.put((String)row.getAttribute("StagingTable")+"_Mandatory", mandatoryList);
            returnMap.put((String)row.getAttribute("StagingTable")+"_DEP", (String)row.getAttribute("DataExtractionProc"));
            returnMap.put((String)row.getAttribute("StagingTable")+"_DEOT", (String)row.getAttribute("DataExtractionObjType"));
            returnMap.put((String)row.getAttribute("StagingTable")+"_DETT", (String)row.getAttribute("DataExtractionTableType"));
            sheetName.add((String)row.getAttribute("StagingTable"));
        }
        lookupType = "";
        for(int i=0;i<lookupTypesArr.size();i++){
            if(i==0){
                lookupType = lookupType.concat("'"+lookupTypesArr.get(i).toString()+"'");
            }
            else {
                lookupType = lookupType.concat(",'"+lookupTypesArr.get(i).toString()+"'");
            }
        }
        defaultTabAll = "";
        if(defaultTab!=null){
            for(int i=0;i<defaultTab.length;i++){
                if(i==0){
                    defaultTabAll = defaultTabAll.concat("'"+defaultTab[i].toString()+"'");
                }
                else {
                    defaultTabAll = defaultTabAll.concat(",'"+defaultTab[i].toString()+"'");
                }
            }
        }
        return returnMap;
    }
    
    public int getDefaultTabCount(String parentIfaceId,String ifaceId){
        ViewObject vo = ADFUtils.findIterator("InterfaceROVO1Iterator").getViewObject();
            if(ifaceId!=null){
                vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("byIfaceId"),false);
                vo.setNamedWhereClauseParam("b_ifaceId", ifaceId);
            }
            else{
                vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("byParentIfaceId"),false);
                vo.setNamedWhereClauseParam("b_parentIfaceId", parentIfaceId);
            }
        vo.setRangeSize(-1);
        vo.executeQuery();
        Row[] rows = vo.getAllRowsInRange();
        Row row;
        String defaultTabLookup;
        Set defaultTabLookupSet = new HashSet<String>();
        for(int i=0;i<rows.length;i++){
            row = rows[i];
            String[] arr;
            defaultTabLookup = row.getAttribute("DefaultTab")!=null ? row.getAttribute("DefaultTab").toString() : null;
            if(defaultTabLookup!=null){
                    arr = defaultTabLookup.split(",");
                    for(int j=0;j<arr.length;j++){
                        defaultTabLookupSet.add(arr[j]);                    
                    }
            }
        }
        if(defaultTabLookupSet!=null){
             return defaultTabLookupSet.size();
        }
            return 0;
    }
    
    public void downLoadTemplate(FacesContext facesContext, OutputStream outputStream) throws Exception {
        String parentIfaceId = ADFUtils.evaluateEL("#{bindings.ifaceId1.inputValue}") != null ? ADFUtils.evaluateEL("#{bindings.ifaceId1.inputValue}").toString() : null;
        String ifaceId = ADFUtils.evaluateEL("#{bindings.childIfaceId.inputValue}") != null ? ADFUtils.evaluateEL("#{bindings.childIfaceId.inputValue}").toString() : null;
        String action = ADFUtils.evaluateEL("#{bindings.action.inputValue}") != null ? ADFUtils.evaluateEL("#{bindings.action.inputValue}").toString() : null;
        boolean withData = false;
        if(action.equals("W")){
            withData = true;
        }
        downloadMap = prepareDownload(ifaceId,parentIfaceId,false);
        constructWorkbook(facesContext,outputStream,false,null,withData);
    }
    
    public void constructWorkbook(FacesContext facesContext, OutputStream outputStream,boolean tempData,String batchId,boolean withData) throws  IOException{
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            ArrayList listObj = new ArrayList<String>();
            ArrayList colList = new ArrayList<String>();
            ArrayList mandatoryList = new ArrayList<String>();
            ArrayList queryList = new ArrayList<String>();
            ArrayList defaultSheetsName = new ArrayList<String>();
            AppModuleImpl amObj;
            //generating default sheets
            ViewObject vo;
            ViewCriteria vc;
            ViewCriteriaRow criteriaRow;
            Row[] rows;
            Row row;
            if(!defaultTabAll.equals("")){
                vo = ADFUtils.findIterator("xxfndLookupsView1Iterator").getViewObject();
                vc = vo.createViewCriteria();
                criteriaRow = vc.createViewCriteriaRow();
                criteriaRow.setAttribute("LookupTypeName", "EXCEL_DEFAULT_TAB_QUERY");
            String inClause = "IN ("+defaultTabAll+")";
            criteriaRow.setAttribute("LookupValueName", inClause);
            vc.addElement(criteriaRow);
            vo.applyViewCriteria(vc, false);
            vo.setRangeSize(-1);
            vo.executeQuery();
            rows = vo.getAllRowsInRange();
            for(int rowCount=0;rowCount<rows.length;rowCount++){
                row = rows[rowCount];
                queryList.add(row.getAttribute("LookupAddlValue"));
                defaultSheetsName.add(row.getAttribute("LookupValueNameDisp"));
            }
            for(int rowCount=0;rowCount<defaultSheetsName.size();rowCount++){
                vo = ADFUtils.getApplicationModuleForDataControl("AppModuleDataControl").findViewObject("DynamicROVO1");
                vo.remove();  
                vo = ADFUtils.getApplicationModuleForDataControl("AppModuleDataControl").createViewObjectFromQueryStmt("DynamicROVO1", queryList.get(rowCount).toString());  
                vo.setRangeSize(-1);
                vo.executeQuery();
                rows = vo.getAllRowsInRange();
                AttributeDef[] att = vo.getAttributeDefs();
                AttributeDef tempAtt;
                ArrayList attributeList = new ArrayList<String>();
                HSSFSheet sheet = workbook.createSheet(defaultSheetsName.get(rowCount).toString());
                sheet.createFreezePane(0, 1);
                for(int count=0;count<att.length;count++){
                    tempAtt = att[count];
                    attributeList.add(tempAtt.getName().toString());
                    sheet.setColumnWidth(count, 5500);
                }
                HSSFRow rowhead = sheet.createRow((short) 0);
//                CellStyle cellStyle = workbook.createCellStyle();
//                CreationHelper createHelper = workbook.getCreationHelper();
//                short dateFormat = createHelper.createDataFormat().getFormat("MM-dd-yyyy");
//                cellStyle.setDataFormat(dateFormat);
                for(int sheetHeader=0;sheetHeader<attributeList.size();sheetHeader++){
                    rowhead.createCell(sheetHeader).setCellValue(attributeList.get(sheetHeader).toString());
                }
                for(int sheetRow=0;sheetRow<rows.length;sheetRow++){
                    row = rows[sheetRow];
                    rowhead = sheet.createRow((short) sheetRow+1);
//                    cellStyle = workbook.createCellStyle();
//                    createHelper = workbook.getCreationHelper();
//                    dateFormat = createHelper.createDataFormat().getFormat("MM-dd-yyyy");
//                    cellStyle.setDataFormat(dateFormat);
                    for(int sheetHeader=0;sheetHeader<attributeList.size();sheetHeader++){
                        rowhead.createCell(sheetHeader).setCellValue(
                                row.getAttribute(attributeList.get(sheetHeader).toString())!=null ? 
                                row.getAttribute(attributeList.get(sheetHeader).toString()).toString() 
                                : null);
                    }
                }
                sheet.protectSheet("readOnly");
            }
            }
            //End of generating default sheets
            //Generating lookupData
            if(lookupType!=null && !lookupType.equalsIgnoreCase("")){
                vo = ADFUtils.findIterator("xxfndLookupsView1Iterator").getViewObject();
                vc = vo.createViewCriteria();
                String inClause = "IN ("+lookupType+")";
                criteriaRow = vc.createViewCriteriaRow();
                criteriaRow.setAttribute("LookupTypeName", inClause);
                vc.addElement(criteriaRow);
                vo.applyViewCriteria(vc, false);
                vo.setRangeSize(-1);
                vo.executeQuery();
                HSSFSheet sheet = workbook.createSheet("LookupData(READ-ONLY)");
                sheet.createFreezePane(0, 1);
                sheet.setColumnWidth(0, 5500);
                sheet.setColumnWidth(1, 5500);
                sheet.setColumnWidth(2, 5500);
                sheet.setColumnWidth(3, 5500);
                HSSFRow rowhead = sheet.createRow((short) 0);
//                CellStyle cellStyle = workbook.createCellStyle();
//                CreationHelper createHelper = workbook.getCreationHelper();
//                short dateFormat = createHelper.createDataFormat().getFormat("MM-dd-yyyy");
//                cellStyle.setDataFormat(dateFormat);
                rowhead.createCell(0).setCellValue("Lookup Type Name");
                rowhead.createCell(1).setCellValue("Lookup Type Name Disp");
                rowhead.createCell(2).setCellValue("Lookup Value Name");
                rowhead.createCell(3).setCellValue("Lookup Value Name Disp");
                rows=vo.getAllRowsInRange();
                for(int i=0;i<rows.length;i++){
                    row = rows[i];
                    rowhead = sheet.createRow((short) i+1);
//                    cellStyle = workbook.createCellStyle();
//                    createHelper = workbook.getCreationHelper();
//                    dateFormat = createHelper.createDataFormat().getFormat("MM-dd-yyyy");
//                    cellStyle.setDataFormat(dateFormat);
                    rowhead.createCell(0).setCellValue((String)row.getAttribute("LookupTypeName"));
                    rowhead.createCell(1).setCellValue((String)row.getAttribute("LookupTypeNameDisp"));
                    rowhead.createCell(2).setCellValue((String)row.getAttribute("LookupValueName"));
                    rowhead.createCell(3).setCellValue((String)row.getAttribute("LookupValueNameDisp"));
                }
                sheet.protectSheet("readOnly");
            }
            //End of Generating lookupData
            //Generating the template
            for(int i=0;i<sheetName.size();i++) {
                String sheetName = this.sheetName.get(i).toString();
                HSSFSheet sheet = workbook.createSheet(sheetName);
                sheet.createFreezePane(0, 1);
                listObj = (ArrayList<String>)downloadMap.get(sheetName);
                colList = (ArrayList<String>)downloadMap.get(sheetName+"_Col");
                mandatoryList = (ArrayList<String>)downloadMap.get(sheetName+"_Mandatory");
                for(int sheetColumn=0;sheetColumn<listObj.size();sheetColumn++){
                    sheet.setColumnWidth(sheetColumn, 5500);
                }
                if(tempData){
                    sheet.setColumnWidth(listObj.size(), 5500);
                    sheet.setColumnWidth(listObj.size()+1, 5500);
                }
                HSSFRow rowhead = sheet.createRow((short) 0);
//                CellStyle cellStyle = workbook.createCellStyle();
//                CreationHelper createHelper = workbook.getCreationHelper();
//                short dateFormat = createHelper.createDataFormat().getFormat("MM-dd-yyyy");
//                cellStyle.setDataFormat(dateFormat);
                String selectColumns = "";
                Cell cell;
                CellStyle mandatoryStyle = workbook.createCellStyle();  
                mandatoryStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());  
                mandatoryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); 
                String mandatory = null;
                for(int sheetHeader=0;sheetHeader<listObj.size();sheetHeader++){
                    mandatory = (String)mandatoryList.get(sheetHeader);
                    cell = rowhead.createCell(sheetHeader);
                    if(mandatory!=null && mandatory.equalsIgnoreCase("Y")){
                        cell.setCellValue("* "+listObj.get(sheetHeader).toString());
                        cell.setCellStyle(mandatoryStyle);
                    }else{
                        cell.setCellValue(listObj.get(sheetHeader).toString());
                    }
                    if(tempData){
                        if(sheetHeader==0){
                            selectColumns = selectColumns.concat(colList.get(sheetHeader).toString());
                        }else{
                            selectColumns = selectColumns.concat(","+colList.get(sheetHeader).toString());
                        }
                    }
                }
                //Writing data to template from the interface staging table
                if(tempData){
                    rowhead.createCell(listObj.size()).setCellValue("INTERFACE STATUS");
                    rowhead.createCell(listObj.size()+1).setCellValue("ERROR MESSAGE");
                    String Query = "SELECT ";
                    Query = Query.concat(selectColumns);
                    Query = Query.concat(",INTERFACE_STATUS_FLAG,ERR_DESCRIPTION");
                    Query = Query.concat(" FROM "+sheetName+" WHERE BATCH_ID="+batchId);
                    if(exportedSelectedType!=null){
                        Query = Query.concat(" AND INTERFACE_STATUS_FLAG='"+exportedSelectedType+"'");
                    }
                    vo = ADFUtils.getApplicationModuleForDataControl("AppModuleDataControl").findViewObject("DynamicROVO1");
                    vo.remove();  
                    vo = ADFUtils.getApplicationModuleForDataControl("AppModuleDataControl").createViewObjectFromQueryStmt("DynamicROVO1", Query);  
                    vo.setRangeSize(-1);
                    vo.executeQuery();
                    rows = vo.getAllRowsInRange();
                    for(int excelCount=0;excelCount<rows.length;excelCount++){
                        row=rows[excelCount];
                        rowhead = sheet.createRow((short) excelCount+1);
//                        cellStyle = workbook.createCellStyle();
//                        createHelper = workbook.getCreationHelper();
//                        dateFormat = createHelper.createDataFormat().getFormat("MM-dd-yyyy");
//                        cellStyle.setDataFormat(dateFormat); 
                        AttributeDef[] att = vo.getAttributeDefs();
                        for(int sheetHeader=0;sheetHeader<att.length;sheetHeader++){
                            rowhead.createCell(sheetHeader).setCellValue(row.getAttribute(sheetHeader)!=null ? row.getAttribute(sheetHeader).toString() : null);
                        }
                    }
                }
                //End of Writing data to template from the interface staging table
                
                //Writing table data to Excel
                if(withData)
                {
                CallableStatement cst = null;
                String objTypeName = (String)downloadMap.get(sheetName+"_DEOT");
                String tableTypeName = (String)downloadMap.get(sheetName+"_DETT");
                String dataExtractionProc = (String)downloadMap.get(sheetName+"_DEP"); 
                System.err.println("objTypeName-->"+objTypeName);
                if(objTypeName!=null && tableTypeName!=null && dataExtractionProc!=null){
                     amObj = (AppModuleImpl)ADFUtils.getApplicationModuleForDataControl(DATA_CONTROL);
                     Map<String,ArrayList<String>> inputMap = new HashMap<String,ArrayList<String>>();
                     System.err.println("inputMap==>"+inputMap);
                    inputMap = (Map<String,ArrayList<String>>)ADFUtils.evaluateEL("#{pageFlowScope.inputParamsMap}");
                    System.err.println("inputMap from scop==>"+inputMap);
                    if(inputMap!=null && !inputMap.isEmpty()){
                    ArrayList<String> dataType = inputMap.get("dataType");
                    ArrayList<String> dataValue = inputMap.get("dataValue");
                    System.err.println("dataType==>"+dataType);
                    System.err.println("dataValue==>"+dataValue);
                     if(amObj!=null){
                         try{
                             String paramString = "(";
                             for(int dataCount=0;dataCount<dataValue.size();dataCount++){
                                 if(dataCount==0){
                                     paramString = paramString.concat("?");
                                 }
                                 else{
                                     paramString = paramString.concat(",?"); 
                                 }
                             }
                             paramString = paramString.concat(",?");
                             paramString = paramString.concat(")");
                         cst = amObj.getDBTransaction().createCallableStatement("{call "+dataExtractionProc+""+paramString+"}", 1);
                         Connection con = cst.getConnection();
                         StructDescriptor structDescriptor = StructDescriptor.createDescriptor(objTypeName.toUpperCase(), con);
                         ResultSetMetaData metaData = structDescriptor.getMetaData();
                            for(int dataCount=0;dataCount<dataValue.size();dataCount++){
                                if(dataType.get(dataCount)!=null && dataType.get(dataCount).equals("VARCHAR")){
                                    cst.setObject(dataCount+1, dataValue.get(dataCount));
                                }
                                else if(dataType.get(dataCount)!=null && dataType.get(dataCount).equals("NUMBER")){
                                    cst.setObject(dataCount+1, new NUMBER(dataValue.get(dataCount)));
                                }
                            }
                         //cst.setObject(1, oracle_record);
                         cst.registerOutParameter(dataValue.size()+1, OracleTypes.ARRAY, tableTypeName.toUpperCase());
                         cst.execute();
                         if (cst.getObject(dataValue.size()+1) != null) {
                             Object[] data = (Object[]) ((Array) cst.getObject(dataValue.size()+1)).getArray();
                             for (int rowCount = 0 ; rowCount < data.length ; rowCount++) {
                                 Struct recRow = (Struct) data[rowCount];
                                 Map columnData = new HashMap<String,Object>();
                                 int idx = 1;
                                 for (Object attribute : recRow.getAttributes()) {
                                     System.err.println("attribute==>"+attribute);
                                     columnData.put(metaData.getColumnName(idx).toString(), attribute);
                                     ++idx;
                                 }
                                 rowhead = sheet.createRow((short) rowCount+1);
//                                 cellStyle = workbook.createCellStyle();
//                                 createHelper = workbook.getCreationHelper();
//                                 dateFormat = createHelper.createDataFormat().getFormat("MM-dd-yyyy");
//                                 cellStyle.setDataFormat(dateFormat); 
                                 for(int sheetHeader=0;sheetHeader<colList.size();sheetHeader++){
                                     rowhead.createCell(sheetHeader).setCellValue(columnData.get(colList.get(sheetHeader).toString())!=null ? columnData.get(colList.get(sheetHeader).toString()).toString() : null);
                                 }
                             }
                         }

                         } catch (SQLException e) {
                         System.err.println("Error in  invokeUploadDataUnitsAPI=" + e.getMessage());
                         } catch (Exception e) {
                         System.err.println("Error in  invokeUploadDataUnitsAPI=" + e.getMessage());
                         } finally {
                         try {
                             cst.close();
                         } catch (SQLException e) {
                             System.err.println("Error in closing statement");
                         }
                         }
                     }               
                }
                }
                }
                //End of Writing table data to Excel
            }
            workbook.write(outputStream);
            outputStream.flush();
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Exception in export:"+e.getMessage());
        }
    }

    public void fileuploadVCL(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        RichInputFile inputFileComponent = (RichInputFile)valueChangeEvent.getComponent();
        file = (UploadedFile)valueChangeEvent.getNewValue();
        fileName = file.getFilename();
        if (file.getContentType().equalsIgnoreCase("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                        file.getContentType().equalsIgnoreCase("application/xlsx") ||
                        file.getContentType().equalsIgnoreCase("application/kset")) {
            try {
                        inputstream = file.getInputStream();
                        blobObj = createBlobDomain(file);
                        inputFileComponent.setValid(true);
            } catch (IOException e) {
                        e.printStackTrace();
            }
        }else if (file.getContentType().equalsIgnoreCase("application/vnd.ms-excel")) {
                if (file.getFilename().toUpperCase().endsWith(".XLS")) {
                    try {
                                inputstream = file.getInputStream();
                                blobObj = createBlobDomain(file);
                                inputFileComponent.setValid(true);
                    } catch (IOException e) {
                                e.printStackTrace();
                    }
                }
        }
        else{
            file = null;
            inputstream = null;
            FacesContext.getCurrentInstance().addMessage( inputFileComponent.getClientId(FacesContext.getCurrentInstance())
                                                                    , new FacesMessage(FacesMessage.SEVERITY_ERROR
                                                                                       , "Incorrect File"
                                                                                       , "Upload the valid file"
                                                                                       )
                                                                    );
            inputFileComponent.resetValue();
            inputFileComponent.setValid(false);
        }
        interfacePBBinding.setRendered(false);
        AdfFacesContext adfFacesContext = AdfFacesContext.getCurrentInstance();
        adfFacesContext.addPartialTarget(interfacePGBinding);
    }
    
    public void setFile(UploadedFile file) {
            this.file = file;
        }

        public UploadedFile getFile() {
            return file;
        }

    public void setFileUploadBinding(RichInputFile fileUploadBinding) {
        this.fileUploadBinding = fileUploadBinding;
    }

    public RichInputFile getFileUploadBinding() {
        return fileUploadBinding;
    }
    
    public Map processExcel(int defaultSheetCount) throws IOException, InvalidFormatException {
        Map<String,Map> processedWorkBook = new HashMap<>();
        // Creating a Workbook from inputstream
        Workbook workbook = WorkbookFactory.create(inputstream);
        int defaultSheet = defaultSheetCount;
        String sheetName;
        for(int i=defaultSheet;i<workbook.getNumberOfSheets();i++){
            Sheet sheet = workbook.getSheetAt(i);
            sheetName = workbook.getSheetName(i);
            if(sheetName.equalsIgnoreCase("LookupData(READ-ONLY)")){
                continue;
            }
        // Create a DataFormatter to format and get each cell's value as String
        DataFormatter dataFormatter = new DataFormatter();
        Map<Integer, Map> excelRowValuesMap = new TreeMap<>();
        //Iterating over Rows and Columns using Java 8 forEach with lambda
        sheet.forEach(row -> { //get each Row
                      Map<Integer, String> excelColumnValuesMap = new HashMap<>();
                      row.forEach(cell -> { //get respective cloumns from each row
                        String cellValue = dataFormatter.formatCellValue(cell);
                if (row.getRowNum() != 0) {
                    excelColumnValuesMap.put(cell.getColumnIndex(), cellValue);
                } 
            });
            if (row.getRowNum() != 0) {
                excelRowValuesMap.put(row.getRowNum(), excelColumnValuesMap);
            }
        });
            processedWorkBook.put(sheet.getSheetName(),excelRowValuesMap);
        }
        // Closing the workbook
        workbook.close();
        //iterate map values for display
        return processedWorkBook;
    }
    
    private BlobDomain createBlobDomain(UploadedFile file) {
        InputStream in = null;
        BlobDomain blobDomain = null;
        OutputStream out = null;
    
        try {
        in = file.getInputStream();
        blobDomain = new BlobDomain();
        out = blobDomain.getBinaryOutputStream();
        IOUtils.copy(in, out);
        in.close();
    
        } catch (IOException e) {
        e.printStackTrace();
        } catch (SQLException e) {
        e.fillInStackTrace();
        }
    
        return blobDomain;
    }
    
    public void onFileDownload(FacesContext facesContext, OutputStream outputStream) throws IOException {
        ViewObject vc = ADFUtils.findIterator("xxdmInterfaceVO1Iterator").getViewObject();
        BlobDomain blob = (BlobDomain) vc.getCurrentRow().getAttribute("UploadedFile");
        try {
            IOUtils.copy(blob.getInputStream(), outputStream);
            blob.closeInputStream();
            outputStream.flush();
        } catch (Exception e) {

        }
    }

    public void setInterfacePGBinding(RichPanelGroupLayout interfacePGBinding) {
        this.interfacePGBinding = interfacePGBinding;
    }

    public RichPanelGroupLayout getInterfacePGBinding() {
        return interfacePGBinding;
    }

    public void ifaceIdVCL(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        interfacePBBinding.setRendered(false);
        AdfFacesContext adfFacesContext = AdfFacesContext.getCurrentInstance();
        adfFacesContext.addPartialTarget(interfacePGBinding);
    }
    
    public void childIfaceIdVCL(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        interfacePBBinding.setRendered(false);
        AdfFacesContext adfFacesContext = AdfFacesContext.getCurrentInstance();
        adfFacesContext.addPartialTarget(interfacePGBinding);
    }

    public void actionVCL(ValueChangeEvent valueChangeEvent) {
        // Add event code here...
        String action = valueChangeEvent.getNewValue()!=null ? valueChangeEvent.getNewValue().toString() : null;
        if((action!=null && action.equals("D")) || (action!=null && action.equals("U"))){
            interfacePBBinding.setRendered(false);
            exportPGL.setRendered(false);
            interfaceDataPBBinding.setRendered(false);
            AdfFacesContext adfFacesContext = AdfFacesContext.getCurrentInstance();
            adfFacesContext.addPartialTarget(interfacePGBinding);
        }
    }

    public void setInterfacePBBinding(RichPanelBox interfacePBBinding) {
        this.interfacePBBinding = interfacePBBinding;
    }

    public RichPanelBox getInterfacePBBinding() {
        return interfacePBBinding;
    }

    public void defaultACtionListener(ActionEvent actionEvent) {
        // Add event code here...
        String parentIfaceId = ADFUtils.evaluateEL("#{bindings.ifaceId1.inputValue}") != null ? ADFUtils.evaluateEL("#{bindings.ifaceId1.inputValue}").toString() : null;
        String action = ADFUtils.evaluateEL("#{bindings.action.inputValue}") != null ? ADFUtils.evaluateEL("#{bindings.action.inputValue}").toString() : null;
        String ErrorMsg="";
        int error = 0;
        if(parentIfaceId==null){
            ErrorMsg = ErrorMsg + "Please select the Parent Interface Name <br><br>";
            error=1;
        }
        if(action == null || action.equals("")){
            ErrorMsg = ErrorMsg + "Please select the Action <br><br>";
            error=1;
        }
        if(error==1){
            FacesContext.getCurrentInstance().addMessage(null
                                                                    , new FacesMessage(FacesMessage.SEVERITY_ERROR
                                                                                       , "Error"
                                                                                       , "<html><body>"+ErrorMsg+"</body></html>"
                                                                                       )
                                                                    );
        }
    }

    public void validateGoACL(ActionEvent actionEvent) {
        // Add event code here...
        String batchId = ADFUtils.evaluateEL("#{bindings.uploadedBatchId.inputValue}") !=null ?  ADFUtils.evaluateEL("#{bindings.uploadedBatchId.inputValue}").toString() : null;
        if(batchId!=null){
            ViewObject vo = ADFUtils.findIterator("xxdmInterfaceVO1Iterator").getViewObject();
            vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("ByBatchId"),false);
            vo.setNamedWhereClauseParam("b_batchId", batchId);
            vo.executeQuery();
            Row row = vo.first();
            String IfaceId = null;
            String status = null;
            if(row!=null){
                IfaceId = row.getAttribute("IfaceId")!=null ? row.getAttribute("IfaceId").toString() : null;
                status = row.getAttribute("Status")!=null ? row.getAttribute("Status").toString() : null;
                if(IfaceId!=null){
                    ADFUtils.setEL("#{bindings.ExportInterfaceId.inputValue}", IfaceId);
                }
            }
            if(status!=null && (status.equals("V") || status.equals("P"))){
                exportPGL.setRendered(true);
            }
            interfacePBBinding.setRendered(true);
            interfaceDataPBBinding.setRendered(false);
            AdfFacesContext adfFacesContext = AdfFacesContext.getCurrentInstance();
            adfFacesContext.addPartialTarget(interfacePGBinding);
            ADFUtils.setEL("#{bindings.validatedBatchId.inputValue}", batchId);
        }
        else{
            FacesContext.getCurrentInstance().addMessage(null
                                                                    , new FacesMessage(FacesMessage.SEVERITY_ERROR
                                                                                       , "Error"
                                                                                       , "Please select the Batch Id"
                                                                                       )
                                                                    );
        }
    }

    public void processGoACL(ActionEvent actionEvent) {
        // Add event code here...
        String batchId = ADFUtils.evaluateEL("#{bindings.validatedBatchId.inputValue}") !=null ?  ADFUtils.evaluateEL("#{bindings.validatedBatchId.inputValue}").toString() : null;
        if(batchId!=null){
            ViewObject vo = ADFUtils.findIterator("xxdmInterfaceVO1Iterator").getViewObject();
            vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("ByBatchId"),false);
            vo.setNamedWhereClauseParam("b_batchId", batchId);
            vo.executeQuery();
            Row row = vo.first();
            String IfaceId = null;
            String status = null;
            if(row!=null){
                IfaceId = row.getAttribute("IfaceId")!=null ? row.getAttribute("IfaceId").toString() : null;
                status = row.getAttribute("Status")!=null ? row.getAttribute("Status").toString() : null;
                if(IfaceId!=null){
                    ADFUtils.setEL("#{bindings.ExportInterfaceId.inputValue}", IfaceId);
                }
            }
            if(status!=null && (status.equals("V") || status.equals("P"))){
                exportPGL.setRendered(true);
            }
            interfacePBBinding.setRendered(true);
            interfaceDataPBBinding.setRendered(false);
            AdfFacesContext adfFacesContext = AdfFacesContext.getCurrentInstance();
            adfFacesContext.addPartialTarget(interfacePGBinding);
        }
        else{
            FacesContext.getCurrentInstance().addMessage(null
                                                                    , new FacesMessage(FacesMessage.SEVERITY_ERROR
                                                                                       , "Error"
                                                                                       , "Please select the Batch Id"
                                                                                       )
                                                                    );
        }
    }

    public void searchACL(ActionEvent actionEvent) {
        // Add event code here...
        String batchId = ADFUtils.evaluateEL("#{bindings.allBatchId.inputValue}") !=null ?  ADFUtils.evaluateEL("#{bindings.allBatchId.inputValue}").toString() : null;
        if(batchId!=null){
            ViewObject vo = ADFUtils.findIterator("xxdmInterfaceVO1Iterator").getViewObject();
            vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("ByBatchId"),false);
            vo.setNamedWhereClauseParam("b_batchId", batchId);
            vo.executeQuery();
            Row row = vo.first();
            String IfaceId = null;
            String status = null;
            if(row!=null){
                IfaceId = row.getAttribute("IfaceId")!=null ? row.getAttribute("IfaceId").toString() : null;
                status = row.getAttribute("Status")!=null ? row.getAttribute("Status").toString() : null;
                if(IfaceId!=null){
                    ADFUtils.setEL("#{bindings.ExportInterfaceId.inputValue}", IfaceId);
                }
            }
            if(status!=null && (status.equals("V") || status.equals("P"))){
                exportPGL.setRendered(true);
            }
            interfacePBBinding.setRendered(true);
            interfaceDataPBBinding.setRendered(false);
            AdfFacesContext adfFacesContext = AdfFacesContext.getCurrentInstance();
            adfFacesContext.addPartialTarget(interfacePGBinding);
            ADFUtils.setEL("#{bindings.uploadedBatchId.inputValue}", batchId);
            ADFUtils.setEL("#{bindings.validatedBatchId.inputValue}", batchId);
        }
        else{
            FacesContext.getCurrentInstance().addMessage(null
                                                                    , new FacesMessage(FacesMessage.SEVERITY_ERROR
                                                                                       , "Error"
                                                                                       , "Please select the Batch Id"
                                                                                       )
                                                                    );
        }
    }

    public void setExportedSelectedType(String exportedSelectedType) {
        this.exportedSelectedType = exportedSelectedType;
    }

    public String getExportedSelectedType() {
        return exportedSelectedType;
    }
    
    public void downLoadTempData(FacesContext facesContext, OutputStream outputStream) throws Exception {
        String parentIfaceId = ADFUtils.evaluateEL("#{bindings.ExportParentInterfaceId.inputValue}") != null ? ADFUtils.evaluateEL("#{bindings.ExportParentInterfaceId.inputValue}").toString() : null;
        String ifaceId = ADFUtils.evaluateEL("#{bindings.ExportInterface.inputValue}") != null ? ADFUtils.evaluateEL("#{bindings.ExportInterface.inputValue}").toString() : null;
        String batchId = ADFUtils.evaluateEL("#{bindings.BatchId.inputValue}") != null ? ADFUtils.evaluateEL("#{bindings.BatchId.inputValue}").toString() : null;
        downloadMap = prepareDownload(ifaceId,parentIfaceId,true);
        constructWorkbook(facesContext,outputStream,true,batchId,false);
    }

    public void setExportPGL(RichPanelGroupLayout exportPGL) {
        this.exportPGL = exportPGL;
    }

    public RichPanelGroupLayout getExportPGL() {
        return exportPGL;
    }

    public void viewTempDataTable(ActionEvent actionEvent) {
        // Add event code here...
        String parentIfaceId = ADFUtils.evaluateEL("#{bindings.ExportParentInterfaceId.inputValue}") != null ? ADFUtils.evaluateEL("#{bindings.ExportParentInterfaceId.inputValue}").toString() : null;
        String ifaceId = ADFUtils.evaluateEL("#{bindings.ExportInterface.inputValue}") != null ? ADFUtils.evaluateEL("#{bindings.ExportInterface.inputValue}").toString() : null;
        String batchId = ADFUtils.evaluateEL("#{bindings.BatchId.inputValue}") != null ? ADFUtils.evaluateEL("#{bindings.BatchId.inputValue}").toString() : null;
        String stagingTable = ADFUtils.evaluateEL("#{bindings.ExportStagingTable.inputValue}") != null ? ADFUtils.evaluateEL("#{bindings.ExportStagingTable.inputValue}").toString() : null;
        if(parentIfaceId!=null){
            getBindings().removeControlBinding(this.DYNAMIC_TABLE_ROVO+"_tree");
            getBindings().removeIteratorBinding(this.DYNAMIC_TABLE_ROVO+"_iterator");
            OperationBinding operationBinding = ADFUtils.findOperation("refreshAndExecuteVO");
            operationBinding.getParamsMap().put("ifaceId", ifaceId);
            operationBinding.getParamsMap().put("StagingTable", stagingTable);
            operationBinding.getParamsMap().put("batchId", batchId);
            operationBinding.getParamsMap().put("interfaceStatus", exportedSelectedType);
            Map<String,ArrayList<String>> returnValue = (Map<String,ArrayList<String>>)operationBinding.execute();
            promptName = returnValue.get("promptName");
            mandatory = returnValue.get("mandatory");
            createIterator();
            treeBinding = getTree();
            interfaceDataPBBinding.setRendered(true);
            AdfFacesContext adfFacesContext = AdfFacesContext.getCurrentInstance();
            adfFacesContext.addPartialTarget(interfacePGBinding);
        }
        else{
            FacesContext.getCurrentInstance().addMessage(null
                                                                    , new FacesMessage(FacesMessage.SEVERITY_ERROR
                                                                                       , "Error"
                                                                                       , "Please Individual Table to view the data"
                                                                                       )
                                                                    );
        }
    }
    
    private DCIteratorBinding createIterator()
        {
          DefinitionFactory defFactory = JUMetaObjectManager.getJUMom().getControlDefFactory();
          //Create and init an iterator binding definition
          JUIteratorDef iterDef = (JUIteratorDef) defFactory.createControlDef(DCDefBase.PNAME_Iterator);

          HashMap initValues = new HashMap();
          initValues.put(JUTags.ID, this.DYNAMIC_TABLE_ROVO+"_iterator");
          initValues.put(JUTags.DataControl, this.DATA_CONTROL);
          initValues.put(JUTags.PNAME_VOName, this.DYNAMIC_TABLE_ROVO);
          iterDef.init(initValues);

          //Create an iterator binding instance
          DCIteratorBinding iter = iterDef.createIterBinding(BindingContext.getCurrent(), getBindings());

          //Add the instance to the current binding container
          getBindings().addIteratorBinding(this.DYNAMIC_TABLE_ROVO+"_iterator", iter);

          return iter;
        }
    
    private JUCtrlHierBinding getTree()
        {

          DefinitionFactory defFactory = JUMetaObjectManager.getJUMom().getControlDefFactory();
          JUCtrlValueDef treeDef = (JUCtrlValueDef) defFactory.createControlDef(DCDefBase.PNAME_Tree);
            

          HashMap initValues = new HashMap();
          initValues.put(JUTags.ID, this.DYNAMIC_TABLE_ROVO+"_tree");
          initValues.put(JUCtrlHierDef.PNAME_IterBinding, this.DYNAMIC_TABLE_ROVO+"_iterator");


          JUCtrlHierTypeBinding typeBinding = new JUCtrlHierTypeBinding();
          initValues.put(JUCtrlHierDef.PNAME_TypeBindings, new JUCtrlHierTypeBinding[] { typeBinding });


          treeDef.init(initValues);

          JUCtrlValueBinding tree = (JUCtrlValueBinding) treeDef.createControlBinding(getBindings());

          getBindings().addControlBinding(treeDef.getName(), tree);

          return (JUCtrlHierBinding) tree;
        }
    
        public DCBindingContainer getBindings()
        {
          BindingContext bc = BindingContext.getCurrent();
          return (DCBindingContainer) bc.getCurrentBindingsEntry();
        }

    public void setTreeBinding(JUCtrlHierBinding treeBinding) {
        this.treeBinding = treeBinding;
    }

    public JUCtrlHierBinding getTreeBinding() {
        return treeBinding;
    }

    public void setInterfaceDataPBBinding(RichPanelBox interfaceDataPBBinding) {
        this.interfaceDataPBBinding = interfaceDataPBBinding;
    }

    public RichPanelBox getInterfaceDataPBBinding() {
        return interfaceDataPBBinding;
    }

    public void setPromptName(ArrayList<String> promptName) {
        this.promptName = promptName;
    }

    public ArrayList<String> getPromptName() {
        return promptName;
    }

    public void validateACL(ActionEvent actionEvent) {
        // Add event code here...
        String batchId = ADFUtils.evaluateEL("#{bindings.uploadedBatchId.inputValue}") != null ? ADFUtils.evaluateEL("#{bindings.uploadedBatchId.inputValue}").toString() : null;
        String IfaceId = ADFUtils.evaluateEL("#{bindings.validateProcessIfaceId.inputValue}") != null ? ADFUtils.evaluateEL("#{bindings.validateProcessIfaceId.inputValue}").toString() : null;
        if(IfaceId!=null){
            ViewObject vo = ADFUtils.findIterator("ExportInterfaceROVO1Iterator").getViewObject();
            vo.setNamedWhereClauseParam("iface_id", IfaceId);
            vo.setRangeSize(-1);
            vo.executeQuery();
            Row[] rows = vo.getAllRowsInRange();
            Row row;
            if(batchId!=null){
                for(int i=0;i<rows.length;i++){
                    row = rows[i];
                    String validationProc = (String)row.getAttribute("ValidationProc");
                    if(row.getAttribute("ValidationProc")!=null){
                        try{
                            OperationBinding operationBinding = ADFUtils.findOperation("invokeProcedure");
                            operationBinding.getParamsMap().put("procedureName", validationProc);
                            operationBinding.getParamsMap().put("batchId", batchId);
                            operationBinding.execute();
                        }
                        catch(Exception e){
                            FacesContext.getCurrentInstance().addMessage(null
                                                                                    , new FacesMessage(FacesMessage.SEVERITY_ERROR
                                                                                                       , "Error"
                                                                                                       , "Something went wrong!!!"
                                                                                                       )
                                                                                    );
                        }
                    }
                }
                vo = ADFUtils.findIterator("xxdmInterfaceVO1Iterator").getViewObject();
                vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("ByBatchId"),false);
                vo.setNamedWhereClauseParam("b_batchId", batchId);
                vo.executeQuery();
                row = vo.first();
                row.setAttribute("Status", "V");
                java.sql.Timestamp datetime = new java.sql.Timestamp(System.currentTimeMillis());
                oracle.jbo.domain.Date daTime = new  oracle.jbo.domain.Date(datetime); 
                row.setAttribute("ValidateDate", daTime);
                OperationBinding operationBinding = ADFUtils.findOperation("Commit");
                operationBinding.execute();
                if(IfaceId!=null){
                    ADFUtils.setEL("#{bindings.ExportInterfaceId.inputValue}", IfaceId);
                }
                exportPGL.setRendered(true);
                interfacePBBinding.setRendered(true);
                interfaceDataPBBinding.setRendered(false);
                AdfFacesContext adfFacesContext = AdfFacesContext.getCurrentInstance();
                adfFacesContext.addPartialTarget(interfacePGBinding);
                ADFUtils.setEL("#{bindings.validatedBatchId.inputValue}", batchId);
                FacesContext.getCurrentInstance().addMessage(null
                                                                        , new FacesMessage(FacesMessage.SEVERITY_INFO
                                                                                           , "Information"
                                                                                           , "Validation Completed!! Please check if there is any Error in the data uploaded!!"
                                                                                           )
                                                                        );
            }else{
                FacesContext.getCurrentInstance().addMessage(null
                                                                        , new FacesMessage(FacesMessage.SEVERITY_ERROR
                                                                                           , "Error"
                                                                                           , "Please select the Batch ID"
                                                                                           )
                                                                        );
            }
        }
        
    }
    
    public void processACL(ActionEvent actionEvent) {
        // Add event code here...
        String batchId = ADFUtils.evaluateEL("#{bindings.validatedBatchId.inputValue}") != null ? ADFUtils.evaluateEL("#{bindings.validatedBatchId.inputValue}").toString() : null;
        String IfaceId = ADFUtils.evaluateEL("#{bindings.validateProcessIfaceId.inputValue}") != null ? ADFUtils.evaluateEL("#{bindings.validateProcessIfaceId.inputValue}").toString() : null;
        if(IfaceId!=null){
            ViewObject vo = ADFUtils.findIterator("ExportInterfaceROVO1Iterator").getViewObject();
            vo.setNamedWhereClauseParam("iface_id", IfaceId);
            vo.setRangeSize(-1);
            vo.executeQuery();
            Row[] rows = vo.getAllRowsInRange();
            Row row;
            if(batchId!=null){
                for(int i=0;i<rows.length;i++){
                    row = rows[i];
                    String processProc = (String)row.getAttribute("ProcessProc");
                    if(row.getAttribute("ProcessProc")!=null){
                        try{
                            OperationBinding operationBinding = ADFUtils.findOperation("invokeProcedure");
                            operationBinding.getParamsMap().put("procedureName", processProc);
                            operationBinding.getParamsMap().put("batchId", batchId);
                            operationBinding.execute();
                        }
                        catch(Exception e){
                            FacesContext.getCurrentInstance().addMessage(null
                                                                                    , new FacesMessage(FacesMessage.SEVERITY_ERROR
                                                                                                       , "Error"
                                                                                                       , "Something went wrong!!!"
                                                                                                       )
                                                                                    );
                        }
                    }
                }
                vo = ADFUtils.findIterator("xxdmInterfaceVO1Iterator").getViewObject();
                vo.applyViewCriteria(vo.getViewCriteriaManager().getViewCriteria("ByBatchId"),false);
                vo.setNamedWhereClauseParam("b_batchId", batchId);
                vo.executeQuery();
                row = vo.first();
                row.setAttribute("Status", "P");
                java.sql.Timestamp datetime = new java.sql.Timestamp(System.currentTimeMillis());
                oracle.jbo.domain.Date daTime = new  oracle.jbo.domain.Date(datetime); 
                row.setAttribute("ProcessDate", daTime);
                OperationBinding operationBinding = ADFUtils.findOperation("Commit");
                operationBinding.execute();
                if(IfaceId!=null){
                    ADFUtils.setEL("#{bindings.ExportInterfaceId.inputValue}", IfaceId);
                }
                exportPGL.setRendered(true);
                interfacePBBinding.setRendered(true);
                interfaceDataPBBinding.setRendered(false);
                AdfFacesContext adfFacesContext = AdfFacesContext.getCurrentInstance();
                adfFacesContext.addPartialTarget(interfacePGBinding);
                FacesContext.getCurrentInstance().addMessage(null
                                                                        , new FacesMessage(FacesMessage.SEVERITY_INFO
                                                                                           , "Information"
                                                                                           , "Processed successfully!!"
                                                                                           )
                                                                        );
            }else{
                FacesContext.getCurrentInstance().addMessage(null
                                                                        , new FacesMessage(FacesMessage.SEVERITY_ERROR
                                                                                           , "Error"
                                                                                           , "Please select the Batch ID"
                                                                                           )
                                                                        );
            }
        }
        
    }

    public void setHiddenOutput(String hiddenOutput) {
        this.hiddenOutput = hiddenOutput;
    }

    public String getHiddenOutput() {
        String faceId = (String)ADFUtils.evaluateEL("#{pageFlowScope.parentInterfaceId}");
        if(faceId!=null  && !faceId.equals("")){
        BigDecimal faceIdBD = new BigDecimal(faceId);
        ADFUtils.setEL("#{bindings.ifaceId.inputValue}", faceIdBD);
        }
        return hiddenOutput;
    }

    public void setMandatory(ArrayList<String> mandatory) {
        this.mandatory = mandatory;
    }

    public ArrayList<String> getMandatory() {
        return mandatory;
    }
}
