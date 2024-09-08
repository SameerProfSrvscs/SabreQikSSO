// ;*****************************************************************************************
// ;* Script       : SingleSignOn
// ;*
// ;* Called by    : Qik Application
// ;*
// ;* Function     : Function to automate Agent Login using EPR details and parse the ATH Token that can be used 
// ;*                across various places like Sabre Web Services, native etc. to simulate SingleSignOn
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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Clock;
import java.io.FileWriter;
import java.io.IOException;

public class SingleSignOnTest 
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
    	
    	try {
	    	// Browser Initiation
	    	//WebDriver driver = new SafariDriver();
    		WebDriver driver = null;
    		String userDir = System.getProperty("user.dir");
    		System.out.println("Directory:" + userDir );
    		System.setProperty("webdriver.chrome.driver", "/Users/sameerbasha/Desktop/SabreSingleSignOn/Packages Downloaded/chromedriver-mac-arm64/chromedriver");
    		driver = new ChromeDriver();
	    	System.out.println("Opening up Browser" );
	    	driver.get("https://accounts.cert.havail.sabre.com/login/srw?");
	    	
	    	//driver.manage().window().maximize();
	    	System.out.println("Initiating work flow" );
	    	// Wait for the page to load - Max wait 20secs
	    	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"submitButton\"]"))).click();
	    	
	    	// Go ahead and enter the values now [Replace XXX with actual values below]
	    	driver.findElement(By.xpath("//*[@id=\"username\"]")).sendKeys("XXX");
	    	driver.findElement(By.xpath("//*[@id=\"password\"]")).sendKeys("XXXXXX");
	    	driver.findElement(By.xpath("//*[@id=\"pcc\"]")).sendKeys("XXXX");
	    	
	    	// Submit the Sign In button
	    	driver.findElement(By.xpath("//*[@id=\"submitButton\"]")).click();
	    	
	    	//Wait for the page to load by verifying if one element is visible. Max wait time is 20Secs
	    	wait = new WebDriverWait(driver, Duration.ofSeconds(20));
	    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"content\"]/div/div/div/div[1]/span")));
	
	        //Parse the Cookie
	    	String athToken = driver.manage().getCookieNamed("SabreCERTATK").getValue();
	    	System.out.println("ATH Token: " + athToken );
	    	
	    	// End the Browser and Driver Session
	    	driver.quit();
	    	//driver.close();
	    	
	        FileWriter myWriter = new FileWriter("./ATHKey.txt");
	        myWriter.write(athToken);
	        myWriter.close();
	    	
	    	//return athToken;
	    	
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
