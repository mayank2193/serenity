package com.browserstack;

import java.net.URL;
import java.util.Iterator;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.SystemEnvironmentVariables;
import net.thucydides.core.webdriver.DriverSource;

public class BrowserStackSerenityDriver implements DriverSource {

    public WebDriver newDriver() {
        EnvironmentVariables environmentVariables = SystemEnvironmentVariables.createEnvironmentVariables();

        String username = System.getenv("LT_USERNAME");
        if (username == null) {
            username = (String) environmentVariables.getProperty("LT.user");
        }

        String accessKey = System.getenv("LT_ACCESS_KEY");
        if (accessKey == null) {
            accessKey = (String) environmentVariables.getProperty("LT.key");
        }

        String environment = System.getProperty("environment");
        DesiredCapabilities capabilities = new DesiredCapabilities();

        Iterator it = environmentVariables.getKeys().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();

            if (key.equals("LT.user") || key.equals("LT.key")
                    || key.equals("LT.server")) {
                continue;
            } else if (key.startsWith("LT_")) {
                capabilities.setCapability(key.replace("LT_", ""), environmentVariables.getProperty(key));
                
            } else if (environment != null && key.startsWith("environment." + environment)) {
                capabilities.setCapability(key.replace("environment." + environment + ".", ""),
                        environmentVariables.getProperty(key));
               
            }
        }

        try {
            return new RemoteWebDriver(new URL("https://" + username + ":" + accessKey + "@"
                    + environmentVariables.getProperty("LT.server") + "/wd/hub"), capabilities);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public boolean takesScreenshots() {
        return true;
    }
}
