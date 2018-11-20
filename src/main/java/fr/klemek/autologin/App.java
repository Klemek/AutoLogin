package fr.klemek.autologin;

import fr.klemek.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

public class App {

    private static HashMap<String, String> params;

    public static void main(String[] args) {
        Logger.init("logging.properties");

        if (verifyConnection()) {
            System.exit(0);
            return;
        }

        if (args.length > 0) {
            File driverLocation = new File(args[0]);
            if (!driverLocation.exists()) {
                Logger.log(Level.WARNING, "Could not find driver at argument 0 location");
                System.exit(1);
                return;
            }
            System.setProperty("phantomjs.binary.path", args[0]);
        } else {

            String path;
            switch (Utils.detectOS()) {
                case WINDOWS:
                    path = "phantomjs/windows/phantomjs.exe";
                    break;
                case MACOSX:
                    path = "phantomjs/macosx/phantomjs";
                    break;
                case UNIX:
                    if (Utils.is64bit()) {
                        path = "phantomjs/linux64/phantomjs";
                    } else {
                        path = "phantomjs/linux32/phantomjs";
                    }
                    break;
                default:
                    Logger.log(Level.SEVERE, "Unsupported OS '" + System.getProperty("os.name") + "'");
                    Logger.log(Level.SEVERE, "Please indicate phantomJS binary file as first argument");
                    System.exit(1);
                    return;
            }
            try {
                Logger.log("Extracting phantomJS binary into temp directory...");
                File f = new File(Extractor.getFile(path));
                f.setExecutable(true);
                System.setProperty("phantomjs.binary.path", f.getAbsolutePath());

            } catch (IOException | URISyntaxException e) {
                Logger.log(e);
                Logger.log(Level.SEVERE, "Cannot extract phantomJS binary");
            }
        }



        if (!loadParams()) {
            Logger.log(Level.SEVERE, "Cannot load parameters");
            System.exit(1);
            return;
        }

        HttpUtils.HttpResult hr = HttpUtils.executeRequest("GET", params.get("address"));
        if (hr.code != 200) {
            Logger.log(Level.SEVERE, "Destination address is not reachable");
            System.exit(1);
            return;
        }

        if (driverExecution()) return;

        verifyConnection();

        System.exit(0);
    }

    private static boolean driverExecution() {
        WebDriver driver = null;
        try {
            driver = new PhantomJSDriver();
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            driver.manage().window().setSize(new Dimension(1024, 768));

            Logger.log("Connecting " + params.get("address") + "...");

            driver.get(params.get("address"));

            if (params.containsKey("page_title") && !driver.getTitle().equals(params.get("page_title"))) {
                Logger.log(Level.SEVERE, "Invalid page title");
                System.exit(1);
                return true;
            }

            Logger.log("Connected");

            Utils.pause(2000);

            Utils.createScreenshot(driver, "screenshot_phantomJS.png");


            WebElement loginInput = driver.findElement(By.id(params.get("username_field_id")));
            WebElement passwordInput = driver.findElement(By.id(params.get("pass_field_id")));

            if (loginInput == null || passwordInput == null) {
                Logger.log(Level.SEVERE, "Cannot find field input");
                System.exit(1);
                return true;
            }

            Logger.log("Detected input fields");

            loginInput.click();
            loginInput.sendKeys(params.get("login"));
            passwordInput.click();
            passwordInput.sendKeys(params.get("pass"));
            Logger.log("Filled input fields");

            Utils.createScreenshot(driver, "screenshot_phantomJS.png");
            WebElement buttonLogin = driver.findElement(By.id(params.get("button_id")));

            if (buttonLogin == null) {
                Logger.log(Level.SEVERE, "Cannot find button");
                System.exit(1);
                return true;
            }

            Logger.log("Detected button");
            buttonLogin.click();

            Logger.log("Clicked button");

            Utils.pause(2000);
            Logger.log("Form filled successfully");

            driver.quit();
            return true;

        } catch (Exception e) {
            Logger.log(e);
            if (driver != null)
                driver.quit();
            return false;
        }
    }

    private static boolean loadParams() {
        params = new HashMap<>();
        if (Utils.getString("address") == null) {
            Logger.log(Level.SEVERE, "No address to connect");
            return false;
        }
        params.put("address", Utils.getString("address"));
        if (Utils.getString("page_title") != null) {
            params.put("page_title", Utils.getString("page_title"));
        } else {
            Logger.log(Level.WARNING, "No page title, cannot ensure correct page is connected");
        }
        for (String field : new String[]{"username_field_id", "pass_field_id", "button_id"}) {
            if (Utils.getString(field) == null) {
                Logger.log(Level.SEVERE, "Missing id : " + field);
                return false;
            }
            params.put(field, Utils.getString(field));
        }
        if (Utils.getString("login") != null) {
            params.put("login", Utils.getString("login"));
            Logger.log("Using login '" + params.get("login") + "'");
        } else {
            if (System.console() == null)
                return false;
            String login = System.console().readLine("Enter login:");
            if (login.length() == 0) {
                Logger.log("Login is empty");
                return false;
            }
            params.put("login", login);
        }
        if (Utils.getString("pass") != null) {
            params.put("pass", Utils.getString("pass"));
            Logger.log("Using pass '" + Utils.hideString(params.get("pass").length()) + "'");
        } else {
            if (System.console() == null)
                return false;
            String pass = new String(System.console().readPassword("Enter pass:"));
            if (pass.length() == 0) {
                Logger.log("Pass is empty");
                return false;
            }
            params.put("pass", pass);
        }
        return true;
    }

    private static boolean verifyConnection() {
        if (Utils.getString("test_ports") == null || Utils.getString("test_address") == null) {
            Logger.log(Level.WARNING, "Skipped connection test");
            return true;
        }

        Logger.log("Testing connection...");

        String[] ports = Utils.getString("test_ports").split(",");
        String address = Utils.getString("test_address");
        String head = Utils.getString("test_head");

        boolean connected = true;

        for (String sPort : ports) {
            try {
                int port = Integer.parseInt(sPort);
                Logger.log("Testing port : " + port);
                HttpUtils.HttpResult hr = HttpUtils.executeRequest("GET", address + ":" + port);
                if (hr.code != 200) {
                    Logger.log(Level.WARNING, "Got response " + hr.code);
                    connected = false;
                    break;
                }
                if (head != null && (hr.result == null || !hr.result.trim().startsWith(head))) {
                    if (hr.result != null)
                        Logger.log(Level.WARNING, "Invalid start of response : '" + hr.result.trim().substring(0, head.length()) + "'");
                    else
                        Logger.log(Level.WARNING, "Response is null");
                    connected = false;
                    break;
                }
            } catch (NumberFormatException e) {
                Logger.log(Level.WARNING, "Invalid port : '" + sPort + "'");
            }
        }
        if (!connected) {
            Logger.log(Level.WARNING, "You don't seem as connected as you want to the outside world");
            return false;
        } else {
            Logger.log("You seem connected to the outside world !");
            return true;
        }
    }

}
