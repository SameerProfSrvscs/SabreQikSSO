// ;*****************************************************************************************
// ;* Script       : SingleSignOn
// ;*
// ;* Called by    : Qik Application
// ;*
// ;* Function     : Function to automate Agent Login using EPR details and parse the ATK Token that can be used 
// ;*                across various places like Sabre Web Services, native host commands etc. to simulate SingleSignOn
// ;*
// ;* Created by   : Sameer/04Mar2024
// ;*
// ;* Modified     :
// ;*
// ;* Parameters   :
// ;*
//;* Σ ¥ @
//;*******************************************************************************************    

package com.sabre.qik.singlesignon;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Clock;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
public class SingleSignOn 
{

    public static void main( String[] iargs )
    {
        
    	//*************************************************************
    	//                        STEPS FOLLOWED                    ***
    	// 1. Initiate Browser                                      ***
    	// 2. Wait till Page Loads and Sign In Button appears.      ***
    	// 3. Fill in the values of EPR, Password & PCC             ***
    	// 4. Sign In                                               ***
    	// 5. Wait till the page loads and EPR values appear        ***
    	// 6. Parse the Cookie ATH Token and Close the Driver       ***
    	//*************************************************************
    	
    	String iEnv           = iargs[0];
    	String iBrowser       = iargs[1];
    	String iURL           = iargs[2].toString();
    	String iFileDirectory = iargs[3];
		String iInvisibleMode = iargs[4];
		
		String iFileName = "";
    	if ( iFileDirectory.equals("") || iFileDirectory.isEmpty() ){
    		iFileName = "ATKKey.txt";
    	}else{
    		iFileName = iFileDirectory + File.separator + "ATKKey.txt";
    	}
    	
    	String logFile = iFileDirectory + File.separator + "SSOProcess.log";
    	
    	String SabreATKCookieName = "";
    	if ( iEnv.equalsIgnoreCase("PROD") ){
    		SabreATKCookieName = "SabrePRODATK";
    	}else{
    		SabreATKCookieName = "SabreCERTATK"; 
    	}
    	
    	try {
    		
    		System.out.println("Execution Started..." );
        	SSOUpdateLogFile(logFile,"Browser Execution Started...");
        	SSOUpdateLogFile(logFile,"Setting Up Driver for Browser...it may take some time...");
        	
	    	// WebDriver and Browser Initiation
    		WebDriver driver = null;
    		switch (iBrowser.toLowerCase()) {
    			case "safari":
    				WebDriverManager.safaridriver().setup();
    				driver = new SafariDriver();
    				break;
    			case "chrome":
    				if(iInvisibleMode.toLowerCase().equals("true")){
        				WebDriverManager.chromedriver().setup();
    					ChromeOptions chromeOptions = new ChromeOptions();
        				chromeOptions.addArguments("--headless");
        				driver = new ChromeDriver(chromeOptions);
        				SSOUpdateLogFile(logFile,"Running in Headless Mode...");
    				}else{
        				WebDriverManager.chromedriver().setup();
        				driver = new ChromeDriver();
    				}
    				break;
    			case "google chrome":
    				if(iInvisibleMode.toLowerCase().equals("true")){
        				WebDriverManager.chromedriver().setup();
    					ChromeOptions chromeOptions = new ChromeOptions();
        				chromeOptions.addArguments("--headless");
        				driver = new ChromeDriver(chromeOptions);
        				SSOUpdateLogFile(logFile,"Running in Headless Mode...");
    				}else{
        				WebDriverManager.chromedriver().setup();
        				driver = new ChromeDriver();
    				}
    				break;
    			case "edge":
    				if(iInvisibleMode.toLowerCase().equals("true")){
    					WebDriverManager.edgedriver().setup();
    					EdgeOptions edgeOptions = new EdgeOptions();
    					edgeOptions.addArguments("--headless");
    					driver = new EdgeDriver(edgeOptions);
    					SSOUpdateLogFile(logFile,"Running in Headless Mode...");
    				}else{
        				WebDriverManager.edgedriver().setup();
        				driver = new EdgeDriver();
    				}
    				break;
    			case "microsoft edge":
    				if(iInvisibleMode.toLowerCase().equals("true")){
    					WebDriverManager.edgedriver().setup();
    					EdgeOptions edgeOptions = new EdgeOptions();
    					edgeOptions.addArguments("--headless");
    					driver = new EdgeDriver(edgeOptions);
    					SSOUpdateLogFile(logFile,"Running in Headless Mode...");
    				}else{
        				WebDriverManager.edgedriver().setup();
        				driver = new EdgeDriver();
    				}
    				break;
    			case "firefox":
    				if(iInvisibleMode.toLowerCase().equals("true")){
    					WebDriverManager.firefoxdriver().setup();
    					FirefoxOptions firefoxOptions = new FirefoxOptions();
    					firefoxOptions.addArguments("--headless");
    					driver = new FirefoxDriver(firefoxOptions);
    					SSOUpdateLogFile(logFile,"Running in Headless Mode...");
    				}else{
    					WebDriverManager.firefoxdriver().setup();
        				driver = new FirefoxDriver();
    				}
    				break;
    			default:
    				System.out.println("Default case entered..." );
    				WebDriverManager.chromedriver().setup();
    				driver = new ChromeDriver();
    				break;
    		}
    	
        	SSOUpdateLogFile(logFile,"Browser Setup Completed...");
    		
	    	driver.get(iURL);
	    	//driver.manage().window().maximize();
        	SSOUpdateLogFile(logFile,"Browser Launched...");
        	
        	SSOUpdateLogFile(logFile,"Waiting for Page to be loaded...Wait time: 2mins");
	    	//Wait for the page to load by verifying if one element (You are logged in as) is visible. 
	    	//Max wait time is 120Secs
	    	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));
	    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"content\"]/div/div/div/h4")));
	        
        	SSOUpdateLogFile(logFile,"Page Loaded...");
        	SSOUpdateLogFile(logFile,"Parsing ATK Cookie...");
	        //Parse the Cookie
	    	String ATKToken = driver.manage().getCookieNamed(SabreATKCookieName).getValue();
	    	System.out.println("ATK Key: " + ATKToken );
        	SSOUpdateLogFile(logFile,"Parsing ATK Cookie...Succesful!");
	    	
	        FileWriter myWriter = new FileWriter(iFileName);
	        myWriter.write(ATKToken);
	        myWriter.close();
	    	
        	SSOUpdateLogFile(logFile,"Process Completed...");
        	SSOUpdateLogFile(logFile,"Closing the Browser!");
	    	// End the Browser and Driver Session
	    	driver.quit();
	    	
    	}catch(Exception e){
    		String error = e.getMessage().toString();
        	SSOUpdateLogFile(logFile,"Exception occurred...");
        	SSOUpdateLogFile(logFile,error); 
    		//return error;
    	}
    	
    }
    
	public static void SSOUpdateLogFile(String iFileName, String iString){
		
    	if ( iFileName.equals("") || iFileName.isEmpty() ){
    		// Do Nothing
    	}else{
    		try {
	            // Open given file in append mode by creating an
	            // object of BufferedWriter class
    			
    			String timeStamp = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm:ss").format(LocalDateTime.now());
    			String logString = timeStamp + " - [SSO.QIK.DEBUG.LOG]:" + iString;
    			
    			File file = new File(iFileName);
    			FileWriter fr = new FileWriter(file, true);
    			BufferedWriter br = new BufferedWriter(fr);
    			
    			br.write(logString);
    			br.newLine();
    			br.close();
    			fr.close();
    			
    		}catch (IOException e) {
	            // Display message when exception occurs
    			SSOUpdateLogFile(iFileName,"Exception occurred..." + e);
    		}
    	}
	}	
	
}
