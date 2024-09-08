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
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Clock;
import java.io.FileWriter;
import java.io.IOException;
public class SingleSignOnEPRPCCArgs 
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
    	
    	String iEnv        = iargs[0];
    	String iBrowser    = iargs[1];
    	String iURL        = iargs[2].toString();
    	String iEPR        = iargs[3];
    	String iPassword   = iargs[4];
    	String iPCC        = iargs[5];
    	String iFileName   = iargs[6];

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
    		switch (iBrowser.toLowerCase()) {
    			case "safari":
    				WebDriverManager.safaridriver().setup();
    				driver = new SafariDriver();
    				break;
    			case "chrome":
    				WebDriverManager.chromedriver().setup();
    				driver = new ChromeDriver();
    				break;
    			case "google chrome":
    				WebDriverManager.chromedriver().setup();
    				driver = new ChromeDriver();
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
	    	driver.get(iURL);
	    	//driver.manage().window().maximize();
	    	
	    	// Wait for the page to load - Max wait 20secs
	    	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"submitButton\"]"))).click();
	    	
	    	// Go ahead and enter the values now
	    	driver.findElement(By.xpath("//*[@id=\"username\"]")).sendKeys(iEPR);
	    	driver.findElement(By.xpath("//*[@id=\"password\"]")).sendKeys(iPassword);
	    	driver.findElement(By.xpath("//*[@id=\"pcc\"]")).sendKeys(iPCC);
	    	
	    	// Submit the Sign In button
	    	//driver.findElement(By.xpath("//*[@id=\"submitButton\"]")).click();
	    	driver.findElement(By.xpath("//*[@id=\"submitButton\"]")).sendKeys(Keys.ENTER);
	    	
	    	//Wait for the page to load by verifying if one element is visible. Max wait time is 20Secs
	    	wait = new WebDriverWait(driver, Duration.ofSeconds(30));
	    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"content\"]/div/div/div/div[1]/span")));
	
	        //Parse the Cookie
	    	String ATKToken = driver.manage().getCookieNamed(SabreATKCookieName).getValue();
	    	System.out.println("ATK Token: " + ATKToken );
	    	System.out.println("ATK Token: " + ATKToken );
    		String userDir = System.getProperty("user.dir");
    		System.out.println("Directory:" + userDir );
	    	
	        FileWriter myWriter = new FileWriter(iFileName);
	        myWriter.write(ATKToken);
	        myWriter.close();
	    	
	    	// End the Browser and Driver Session
	    	driver.quit();
	    	//driver.close();
	    	//return ATKToken;
	    	
    	}catch(Exception e){
    		String error = e.getMessage().toString();
    		//return error;
    	}
        
        //****************************************************************
        // Commented & retained the code if required for future usage. ***
        //****************************************************************
    	// Timestamp for testing and tracking purpose
    	//Clock systemClock = Clock.systemDefaultZone();
    	//System.out.println( systemClock.instant() + ": " +  "Initiating work flow" );
    	
    	// Parese Label values
    	//String userID = driver.findElement(By.xpath("//*[@id=\"content\"]/div/div/div/div[1]/span")).getText();
    	//String pcc = driver.findElement(By.xpath("//*[@id=\"content\"]/div/div/div/div[2]/span")).getText();
    	//String domain= driver.findElement(By.xpath("//*[@id=\"content\"]/div/div/div/div[3]/span")).getText();
    	
    	// Get all Cookies
    	//Collection<Cookie> cookiesSet = driver.manage().getCookies();
    	//System.out.println( systemClock.instant() + ": " +  "Cookies Set:" + cookiesSet );
    	
    	// Implicit Wait example
    	//driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    	//WebElement waitForElement = driver.findElement(By.xpath("//*[@id=\"username\"]"));
    	
    	
    }
}
