package employee_tests;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.exec.ExecuteException;
import org.testng.annotations.Test;

import com.arsin.ArsinSeleniumAPI;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import wrkday_pom.Emphomepg;
import wrkday_pom.Empprofilepg;
import wrkday_pom.Login;
import wrkday_pom.TestData;
import wrkday_pom.Utility;

public class Wrkday_EmployeeDetailsChange_Primaryaddress {

	ArsinSeleniumAPI oASelFW = null;
	@Parameters({ "prjName", "testEnvironment","instanceName","sauceUser","moduleName","testSetName"})
	
	@BeforeClass
	public void oneTimeSetUp(String prjName,String testEnvironment,String instanceName,String sauceUser,String moduleName,String testSetName) throws InterruptedException, ExecuteException, IOException 
	{
		    String[] environment=new ArsinSeleniumAPI().getEnvironment(testEnvironment,this.getClass().getName());	
			String os=environment[0];
		    String browser=environment[1];String testCasename=this.getClass().getSimpleName();	
			oASelFW = new ArsinSeleniumAPI(prjName,testCasename,browser,os,instanceName,sauceUser,moduleName,testSetName);
			oASelFW.startSelenium(oASelFW.getURL("WEB_URL_ADMIN",oASelFW.instanceName));
	}
		
    @Test
    public void primryAddrChng() throws Exception
    {
    	TestData tData 	= new TestData();
    	Utility util	= new Utility();
    	
		try{
			
			String filePath     = oASelFW.getConstValFrmPropertyFile("FilePath");
	        String sheet        = oASelFW.getConstValFrmPropertyFile("Sheet");
	        
			int totalRows		 = tData.getRowCount(filePath,sheet);
			System.out.println("totalRows"+totalRows);
			
			for(int rownum = 2; rownum<=totalRows; rownum++){
				
				System.out.println("data rowCount " + rownum);
				
				String emp_id		 = tData.getCellData(filePath, sheet, "Employee ID", rownum);
				String pswd			 = tData.getCellData(filePath, sheet, "Emp Password", rownum);
				String emp_FName	 = tData.getCellData(filePath, sheet, "Employee First Name", rownum);
				String emp_LName	 = tData.getCellData(filePath, sheet, "Employee Last Name", rownum);
				String newAddress	 = tData.getCellData(filePath, sheet, "new_Addr", rownum);
				String resetAddress	 = tData.getCellData(filePath, sheet, "Curr_Addr", rownum);
				String empName 		 = util.toCamelCase1(emp_FName)+" "+util.toCamelCase1(emp_LName);
				String formattedDate = util.getDaysBetweenDates(3);
				String date 		 = formattedDate.replace("/", "");
				
				String mgr_Name	 	 = tData.getCellData(filePath, sheet, "Salary Manager", rownum);
				String[] str 		 = util.splitString(mgr_Name," ");
				String[] str1 		 = util.splitString(str[0],",");
				String managerName 	 = str1[1]+" "+str1[0];
				System.out.println("Manger name--"+managerName);
				
				if(rownum == 10 || rownum == 11){
					System.out.println("Skipped " +rownum+"th row number record");
					oASelFW.effecta("sendReport", empName+" is Canada employee", "Date is already set some future date for Canada users", "Pass");
				}
				else{
					
					//Login to the application
			    	Login oUser = new Login (oASelFW);
			    	oUser.loginSubmit(emp_id,pswd);
			    	oASelFW.driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
			    	
			      	//Verify Employee is logged in
			    	Emphomepg oemp = new Emphomepg(oASelFW);
			    	oemp.vrfyEmp(empName);
			    	
			    	//Click profile picture and view profile
			    	oemp.empClk();

			    	//Verify employee name
			    	Empprofilepg oempprf = new Empprofilepg(oASelFW);
			    	oempprf.vrfyEmp(empName);
			    	
			    	//Verify manager name
			    	oempprf.vrfyMgr(managerName);
			    	
			    	//Click actions button
			    	oempprf.clkActions();
			    	
			    	//Click Change Contact Information
			    	oempprf.clkContctDtls("Personal Data");
			    	
			    	//Click Edit Primary Address Button
			    	oempprf.clickEditAddress();
			    	
			    	oASelFW.driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			    	
			    /*	oempprf.editDetails("Effective Date",date);
			    	
			    	oempprf.editDetails("Address Line 1",newAddress);*/
			    	//Update the address and date
			    
			    	oempprf.chgContctDtls(newAddress,date);
			    	
			    	//Click actions button
			    	oempprf.clkActions();
			    	
			    	//Click Change Contact Information
			    	oempprf.clkContctDtls("Personal Data");
			    	
			    	//Click Edit Primary Address Button
			    	oempprf.clickEditAddress();
			    	
			    	//Verify the address and date
			    	oempprf.vrfyContctDtls(newAddress,formattedDate);
			    	
			   /* 	oempprf.editDetails("Effective Date",date);
			    	
			    	oempprf.editDetails("Address Line 1",resetAddress);*/
			    	
			    	//Reset the details and verify
			    	oempprf.chgContctDtls(resetAddress,date);
			    	oempprf.clkActions();
			    	oempprf.clkContctDtls("Personal Data");
			    	oempprf.clickEditAddress();
			    	oempprf.vrfyContctDtls(resetAddress,formattedDate);
			    	
			    	//oASelFW.driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
			    	oemp.clkSgnout();
				}
			}
	    }
		catch(Exception e){
			
		}
    }
    
    @AfterClass
	public void oneTearDown() throws Exception
	{
    	try{
    		oASelFW.stopSelenium();
    	}catch(Exception e){
			
		}
	}
}
