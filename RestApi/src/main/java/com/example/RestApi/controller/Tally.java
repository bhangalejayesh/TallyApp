package com.example.RestApi.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cglib.beans.BeanCopier.Generator;
import org.springframework.cglib.beans.BeanGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.RestApi.entities.AppProperties;
import com.example.RestApi.entities.ManageProperties;
import com.example.RestApi.entity.CLParameters;
import com.example.RestApi.utility.Licence;
//import com.softage.extend.easi.entities.AppProperties;
import com.example.RestApi.utility.XMLFile;
import java.sql.*;  
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@CrossOrigin
@Service
public class Tally extends Generator {
	private static final Logger logger = LogManager.getLogger();
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	private final com.example.RestApi.entities.AppProperties appProperties = ManageProperties.getInstance().getAppProperties();
//	private Licence licence;
	
	private enum DATA_TYPE { SALES_INVOICE("SI"), PURCHASE_INVOICE("PI");
		private final String condition = "where tran.docstatus=1 and tran.company='{company}' and tran.posting_date between '{fdate}' and '{tdate}'\n";
		private final String mquery="select distinct upper(party.name) name, tran.gst_category, '{pg}' as parent_account, party.gstin, address.state " +
				"from tab{party} party " +
				"inner join `tab{ttype} Invoice` tran on(tran.customer=party.name) " +
				"inner join tabAddress address on(address.name=tran.{party}_address)\n" + condition;
		
//		private final String masterQuery = "select distinct trim(LEFT(account, CHAR_LENGTH(account) - LOCATE('-', REVERSE(account)))) account, acc.root_type, acc.report_type, acc.account_type,\n" +
//				"trim(LEFT(acc.parent_account, CHAR_LENGTH(acc.parent_account) - LOCATE('-', REVERSE(acc.parent_account)))) parent_account\n" +
//				"from `tabGL Entry` tran\n" +
//				"inner join tabAccount acc on(acc.name=tran.account)\n" +
//				condition +
//				"and party>'' order by tran.creation";
		
		private final String tlist = "select tran.name, upper({party}) as party, "
				+ "nvl(tran.payment_terms_template, '45 Days') as credit_period "
				+ "from `tab{ttype} Invoice` tran\n" + condition;
		
		private final String tquery="select ((debit*-1)+credit) amount, DATE_FORMAT(ge.posting_date, '%Y%m%d') posting_date, concat(voucher_no, '-', DATE_FORMAT(ge.posting_date, '%d.%m.%Y')) reference,\n"
				+ "case when party_type>'' then upper(party) else trim(LEFT(account, CHAR_LENGTH(account) - LOCATE('-', REVERSE(account)))) end account,\n"
				+ "party_type, remarks from `tabGL Entry` ge\n"
				+ "where ge.docstatus=1 and voucher_no=?\n"
				+ "order by abs(amount) desc";
//		private final String tquery="select si.name, account, party_type, party, debit, credit, si.remarks " +
//				"from `tab{ttype} Invoice` si " +
//				"inner join `tabGL Entry` ge on(ge.voucher_no=si.name)\n" + condition +
//				" and voucher_no=?" +
//				"order by ge.voucher_no, ge.creation";
		
		private final String itemquery="select item_name, rate, uom, amount  from `tabSales Invoice Item` where parent ='{name}'";
		
		private final String consignee="select NVL(shipping_address,address_display) as consigneeAddress from `tabSales Invoice` where name  ='{name}'";

		private String value;
		private DATA_TYPE(String s) {value=s;}
		
		private String getVchTypeName() {return value.equals("SI") ?  "Sales" : "Purchase";}
		private String getMasterQuery() {
			boolean si = value.equals("SI");
			return mquery
					.replace("{pg}", "Sundry " + (si ? "Debtors" : "Creditors"))
					.replaceAll("\\{party\\}", (si ? "Customer" : "Supplier"))
					.replace("{ttype}", getVchTypeName());
		}
		
		private String getTransactionListQuery(String company, String fromdate, String todate) {
			return tlist
					.replaceAll("\\{party\\}", (value.equals("SI") ? "Customer" : "Supplier"))
					.replace("{ttype}", getVchTypeName())
//					.replace("{company}", company)
//					.replace("{fdate}", fromdate)
//					.replace("{tdate}", todate);
					.replace("{company}", "Satpuda Engineering Pvt. Ltd.")
					.replace("{fdate}", "2023-07-06")
					.replace("{tdate}", "2023-07-07");
		}
		
		private String getTransactionQuery() {
			return tquery
					.replaceAll("\\{party\\}", (value.equals("SI") ? "Customer" : "Supplier"))
					.replace("{ttype}", getVchTypeName());
		}
		
		private String getTransactionItemListQuery(String vname) {
			return itemquery
					.replace("{name}", vname);
		}
		
		private String getTransactionConsigneeQuery(String vname) {
			return consignee
					.replace("{name}", vname);
		}
		
		public static DATA_TYPE getEnum(String value) {
			for(DATA_TYPE v : values())
				if(v.value.equalsIgnoreCase(value)) return v;
			
			throw new IllegalArgumentException();
		}
	}
	
//	public Tally(Licence licence) {
////		super(licence);
//	}
	
//	 @Autowired
//	 JdbcTemplate jdbcTemplate;
//	 AppProperties appProperties1;
	 
	@PostMapping("/")
	public void exportTransactions(CLParameters clParameters) throws Exception {
		logger.trace("tally export transactions");
//		Licence licence =new Licence();
		
//		if (licence==null)
//			throw new Exception("invalid licence file!");
		
		File output_dir = new File(appProperties.getOutputFolder());
		if (!output_dir.exists())
    		output_dir.mkdirs();
    	
    	else {
    		if (!output_dir.isDirectory())
    			throw new Exception("output destination set is not a directory: " + output_dir.getAbsolutePath());
    	}
		
		String company=clParameters.getCompany();
		String fromdate=clParameters.getFromDate();
		String todate=clParameters.getToDate();
//		boolean updateMasters=clParameters.isUpdateMasters();
		boolean updateMasters=true;
		String[] dataTypes= {"SI"};//clParameters.getDataTypes();
		
//		try (com.softage.extend.easi.entities.ResultSet r=new com.softage.extend.easi.entities.ResultSet(); Connection conn = r.getConnection();) {
//		Connection conn;
		try (Connection conn = DriverManager.getConnection(
			    "jdbc:mariadb://localhost:3306/satpuda",
			    "root", "jayesh");) {
			for (String dataType : dataTypes) {
				DATA_TYPE exportDataType = DATA_TYPE.getEnum(dataType);
				
				XMLFile xmlFile = new XMLFile();
				xmlFile.writeXMLOpen("ENVELOPE");
					xmlFile.writeXMLOpen("HEADER");
						xmlFile.writeXML("TALLYREQUEST", "Import Data");
					xmlFile.writeXMLClose("HEADER");
					
					xmlFile.writeXMLOpen("BODY");
						xmlFile.writeXMLOpen("IMPORTDATA");
							xmlFile.writeXMLOpen("REQUESTDESC");
								xmlFile.writeXML("REPORTNAME", "Vouchers");
							xmlFile.writeXMLClose("REQUESTDESC");
							
							xmlFile.writeXMLOpen("REQUESTDATA");
								xmlFile.writeXMLOpen("TALLYMESSAGE"); xmlFile.writeXMLAttr("xmlns:UDF", "TallyUDF");
									Statement stmt = conn.createStatement();
									ResultSet rs = null;
									
									if (updateMasters) {
										String mq = exportDataType.getMasterQuery()// masterQuery
												.replace("{company}", "Satpuda Engineering Pvt. Ltd.")
												.replace("{fdate}", "2023-07-06")
												.replace("{tdate}", "2023-07-07");
//												.replace("{company}", company)
//												.replace("{fdate}", fromdate)
//												.replace("{tdate}", todate);
										
//										logger.debug(mq);
										rs = stmt.executeQuery(mq);
										System.out.println("rs Master "+rs);
										while (rs.next()) {
//											party.name, tran.gst_category, '{pg}' as parent_account, party.gstin, address.state
											String account = rs.getString("name");//account
											
											xmlFile.writeXMLOpen("LEDGER"); xmlFile.writeXMLAttr("NAME", account); xmlFile.writeXMLAttr("RESERVEDNAME", "");
												xmlFile.writeXMLOpen("MAILINGNAME.LIST"); xmlFile.writeXMLAttr("TYPE", "String");
													xmlFile.writeXML("MAILINGNAME", account);
												xmlFile.writeXMLClose("MAILINGNAME.LIST");
												
												xmlFile.writeXML("GSTREGISTRATIONTYPE", rs.getString("gstin")!=null ? "Regular" : "");
												xmlFile.writeXML("PARENT", rs.getString("parent_account"));
												xmlFile.writeXML("PARTYGSTIN", rs.getString("gstin"));
												xmlFile.writeXML("LEDSTATENAME", rs.getString("state"));
												
												xmlFile.writeXMLOpen("LANGUAGENAME.LIST");
													xmlFile.writeXMLOpen("NAME.LIST"); xmlFile.writeXMLAttr("TYPE", "String");
														xmlFile.writeXML("NAME", account);
													xmlFile.writeXMLClose("NAME.LIST");
													xmlFile.writeXML("LANGUAGEID", "1033");
												xmlFile.writeXMLClose("LANGUAGENAME.LIST");
											xmlFile.writeXMLClose("LEDGER");
										}
										
										rs.close();
										
										createItems(xmlFile);
									}
									
									ResultSet rsList = stmt.executeQuery(exportDataType.getTransactionListQuery(company, fromdate, todate));
									PreparedStatement psTran = conn.prepareStatement(exportDataType.getTransactionQuery());
									
									System.out.println("rsList"+rsList+" psTran "+psTran);
									while (rsList.next()) {
										String vname = rsList.getString("name");
										
										psTran.setString(1, vname);
										rs = psTran.executeQuery();
										
										PreparedStatement pslist = conn.prepareStatement(exportDataType.getTransactionItemListQuery(vname));
										PreparedStatement consingneeAdd = conn.prepareStatement(exportDataType.getTransactionConsigneeQuery(vname));
//										
										ResultSet consingneeDetails= consingneeAdd.executeQuery();
										ResultSet itemlist=null;
										itemlist=pslist.executeQuery();
										xmlFile.writeXMLOpen("VOUCHER"); xmlFile.writeXMLAttr("REMOTEID", vname); xmlFile.writeXMLAttr("VCHTYPE", "GST Sales"); xmlFile.writeXMLAttr("Action", "Create"); xmlFile.writeXMLAttr("OBJVIEW", "Invoice Voucher View"); 
										
										while (rs.next()) {
											double amount = rs.getDouble("amount");//(rs.getDouble("debit") * -1) + rs.getDouble("credit");											
											String account = rs.getString("account");
											// account = account.substring(0, account.lastIndexOf("-")).trim();
											
											if (rs.getString("party_type")!=null) {
												String dateStr = rs.getString("posting_date");
												
												if (clParameters.isDummyMode())
													dateStr = dateStr.substring(0, 6) + "01";
												
												xmlFile.writeXML("DATE", dateStr);
												xmlFile.writeXML("VCHSTATUSDATE", dateStr);
												xmlFile.writeXML("GUID", vname);
												xmlFile.writeXML("NARRATION", rs.getString("remarks"));
												xmlFile.writeXML("PARTYNAME", account);
												xmlFile.writeXML("VOUCHERTYPENAME", "GST Sales");//exportDataType.getVchTypeName());
												xmlFile.writeXML("REFERENCE", rs.getString("reference"));
												xmlFile.writeXML("VOUCHERNUMBER", vname);
												xmlFile.writeXML("PARTYLEDGERNAME", account);
												xmlFile.writeXML("BASICBUYERNAME", account);
//												
												xmlFile.writeXMLOpen("CONSIGNEEADDRESS.LIST");
												while (consingneeDetails.next()) {
													String strAdd=consingneeDetails.getString("consigneeAddress");
													String shippingAdd=strAdd.replaceAll("<[^>]*>", "");
													System.out.println("inside consingneeDetails loop"+shippingAdd);
													xmlFile.writeXML("CONSIGNEEADDRESS",shippingAdd );
												}
												xmlFile.writeXMLClose("CONSIGNEEADDRESS.LIST");
												
												xmlFile.writeXMLOpen("LEDGERENTRIES.LIST");
													xmlFile.writeXML("LEDGERNAME", account);
													xmlFile.writeXML("AMOUNT", String.valueOf(amount));
													xmlFile.writeXMLOpen("BILLALLOCATIONS.LIST");
														xmlFile.writeXML("NAME", vname);
														xmlFile.writeXMLOpen("BILLCREDITPERIOD", rsList.getString("credit_period"));
															xmlFile.writeXMLAttr("JD", "43444");
															xmlFile.writeXMLAttr("P", rsList.getString("credit_period"));
														xmlFile.writeXMLClose("BILLCREDITPERIOD");
														xmlFile.writeXML("BILLTYPE", "New Ref");
														xmlFile.writeXML("TDSDEDUCTEEISSPECIALRATE", "No");
														xmlFile.writeXML("AMOUNT", String.valueOf(amount));
													xmlFile.writeXMLClose("BILLALLOCATIONS.LIST");
												xmlFile.writeXMLClose("LEDGERENTRIES.LIST");
												
												continue;
											}
											
											
											xmlFile.writeXMLOpen("LEDGERENTRIES.LIST");
												xmlFile.writeXML("LEDGERNAME", account.equalsIgnoreCase("Sales") ? "GST SALES" : account);
												xmlFile.writeXML("AMOUNT", String.valueOf(amount));
												xmlFile.writeXML("VATEXPAMOUNT", String.valueOf(amount));
											xmlFile.writeXMLClose("LEDGERENTRIES.LIST");
										}
										
//										String itemqueryList="select item_name, rate, uom, amount  from `tabSales Invoice Item` where parent = '{vname}'";
//										 ResultSet itemlist=stmt.executeQuery(itemqueryList);
										xmlFile.writeXMLOpen("ALLINVENTORYENTRIES.LIST");
										while (itemlist.next()) {
											System.out.println("inside itemlist loop");
											double itemAmount = itemlist.getDouble("amount");
											double rate=itemlist.getDouble("rate");
											xmlFile.writeXML("STOCKITEMNAME", itemlist.getString("item_name"));
											xmlFile.writeXML("RATE",String.valueOf(rate)+"/"+itemlist.getString("uom"));
											xmlFile.writeXML("AMOUNT",String.valueOf(itemAmount));
//											xmlFile.writeXML("STORE","Stores");
											continue;
										}
										xmlFile.writeXMLClose("ALLINVENTORYENTRIES.LIST");
										
										xmlFile.writeXMLClose("VOUCHER");
										rs.close();
									}
									
									rsList.close();
									
								xmlFile.writeXMLClose("TALLYMESSAGE");
							xmlFile.writeXMLClose("REQUESTDATA");
						xmlFile.writeXMLClose("IMPORTDATA");
					xmlFile.writeXMLClose("BODY");
				xmlFile.writeXMLClose("ENVELOPE");
				
				File oFile = new File(output_dir.getAbsolutePath(), "TallyExport (" + exportDataType.value + ") " + sdf.format(new Date()) + ".xml");
				try (FileOutputStream outputFile = new FileOutputStream(oFile)) {
					outputFile.write(xmlFile.toString().getBytes());
					outputFile.close();
				}
				
				System.out.println("generated files:\n" + oFile.getAbsolutePath());
			}
		}
	}
	
	private void createItems(XMLFile xmlFile) {
		String[] accounts= {"AST-718-ASSLY (B&C Type)", "AMBULANCE STRETCHER TROLLY","EECO Ambulance","Paramount Powder Pebble Gray Satin-H6R07032S6","Ramp for Eeco Stretcher Trolley"};
		for (String account : accounts) {
			xmlFile.writeXMLOpen("LEDGER"); xmlFile.writeXMLAttr("NAME", account); xmlFile.writeXMLAttr("RESERVEDNAME", "");
				xmlFile.writeXMLOpen("MAILINGNAME.LIST"); xmlFile.writeXMLAttr("TYPE", "String");
					xmlFile.writeXML("MAILINGNAME", account);
				xmlFile.writeXMLClose("MAILINGNAME.LIST");
				
	//			xmlFile.writeXML("GSTREGISTRATIONTYPE", rs.getString("gstin")!=null ? "Regular" : "");
//				xmlFile.writeXML("PARENT", rs.getString("parent_account"));
				xmlFile.writeXML("PARENT", "Sundry Debtors");
	//			xmlFile.writeXML("PARTYGSTIN", rs.getString("gstin"));
	//			xmlFile.writeXML("LEDSTATENAME", rs.getString("state"));
				
				xmlFile.writeXMLOpen("LANGUAGENAME.LIST");
					xmlFile.writeXMLOpen("NAME.LIST"); xmlFile.writeXMLAttr("TYPE", "String");
						xmlFile.writeXML("NAME", account);
					xmlFile.writeXMLClose("NAME.LIST");
					xmlFile.writeXML("LANGUAGEID", "1033");
				xmlFile.writeXMLClose("LANGUAGENAME.LIST");
			xmlFile.writeXMLClose("LEDGER");
		}
	}
}
