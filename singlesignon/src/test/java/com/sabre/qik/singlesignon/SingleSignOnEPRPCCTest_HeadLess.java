// ;*****************************************************************************************
// ;* Script       : SingleSignOnEPRPCC
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
import java.util.Collection;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Clock;
import java.io.FileWriter;
import java.io.IOException;
public class SingleSignOnEPRPCCTest_HeadLess 
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

    	String iEnv = "CERT";
    	String iBrowser ="Safari";
    	String iURL = "https://accounts.cert.havail.sabre.com/login/srw?";
    	String iEPR = "100";
    	String iPassword = "xdrcft12";
    	String iPCC = "0FJG";
    	String iFileName   = "/Users/sameerbasha/Desktop/SabreSingleSignOn/ATKKey.txt";
    	
		System.out.println("Initiating Workflow");
    	
    	if ( iFileName.equals("") || iFileName.isEmpty() ){
    		iFileName = "ATKKey.txt";
    	}
    	
    	String SabreATKCookieName = "";
    	if ( iEnv.equalsIgnoreCase("PROD") ){
    		SabreATKCookieName = "SabrePRODATK";
    	}else{
    		SabreATKCookieName = "SabreCERTATK"; 
    	}
    	
    	try {
    		System.out.println("Execution Started..." );
    		
	    	// WebDriver and Browser Initiation
    		WebDriver driver = null;
    		ChromeOptions chromeOptions = null;
    		switch (iBrowser.toLowerCase()) {
    			case "safari":
    				WebDriverManager.safaridriver().setup();
    				driver = new SafariDriver();
    				break;
    			case "chrome":
    				WebDriverManager.chromedriver().setup();
    			    chromeOptions = new ChromeOptions();
    			    chromeOptions.addArguments("--headless");
    				driver = new ChromeDriver(chromeOptions);
    				break;
    			case "google chrome":
    				WebDriverManager.chromedriver().setup();
    			    chromeOptions = new ChromeOptions();
    			    chromeOptions.addArguments("--headless");
    				driver = new ChromeDriver(chromeOptions);
    				break;
    			case "edge":
    				WebDriverManager.edgedriver().setup();
    				driver = new EdgeDriver();
    				break;
    			case "microsoft edge":
    				WebDriverManager.edgedriver().setup();
    				break;
    			case "firefox":
    				WebDriverManager.firefoxdriver().setup();
    				driver = new FirefoxDriver();
    				break;
    			default:
    				System.out.println("Default case entered..." );
    				WebDriverManager.chromedriver().setup();
    				driver = new ChromeDriver();
    				break;
    		}
    	
	    	//driver.get("https://accounts.cert.havail.sabre.com/login/srw?");
    		System.out.println("Launching Browser..." );
	    	driver.get(iURL);
	    	driver.manage().window().maximize();
	    	
	    	System.out.println("Waiting for Page to load..." );
	    	
	    	// Wait for the page to load - Max wait 20secs
	    	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"submitButton\"]"))).click();
	    	
	    	System.out.println("Entering Data..." );
	    	// Go ahead and enter the values now
	    	driver.findElement(By.xpath("//*[@id=\"username\"]")).sendKeys(iEPR);
	    	driver.findElement(By.xpath("//*[@id=\"password\"]")).sendKeys(iPassword);
	    	driver.findElement(By.xpath("//*[@id=\"pcc\"]")).sendKeys(iPCC);
	    	
	    	// Submit the Sign In button
	    	//driver.findElement(By.xpath("//*[@id=\"submitButton\"]")).click();
	    	driver.findElement(By.xpath("//*[@id=\"submitButton\"]")).sendKeys(Keys.ENTER);
	    	
	    	System.out.println("Waiting for Page to Load...for Cookie Parsing..." );
	    	//Wait for the page to load by verifying if one element is visible. Max wait time is 20Secs
	    	wait = new WebDriverWait(driver, Duration.ofSeconds(120));
	    	//wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"content\"]/div/div/div/div[1]/span")));
	    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"content\"]/div/div/div/h4")));

	    	System.out.println("Page Loaded..." );
	        //Parse the Cookie
	    	String ATKToken = driver.manage().getCookieNamed(SabreATKCookieName).getValue();
	    	System.out.println("Cookie Parsed Succesfully...!" );
	    	System.out.println("ATK Token: " + ATKToken );
	    	
	        FileWriter myWriter = new FileWriter(iFileName);
	        myWriter.write(ATKToken);
	        myWriter.close();
	    	
	    	// End the Browser and Driver Session
	    	driver.quit();
	    	
    	}catch(Exception e){
    		String error = e.getMessage().toString();
    		System.out.println("Error:" + error );
    	}
    }
}
