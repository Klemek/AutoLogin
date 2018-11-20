package fr.klemek.autologin;

import fr.klemek.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

final class Utils {

    private Utils() {

    }

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("config");

    private static Properties props = null;

    static void createScreenshot(WebDriver driver, String filename) {
        try {
            File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            ImageIO.write(ImageIO.read(screenshot), "PNG", new File(filename));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    static void loadProperties(String name){
        props = new Properties();
        InputStream propIS = null;
        try{
            propIS = new FileInputStream(name);
        }catch(IOException ex){
            propIS = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        }
        if(propIS == null){
            Logger.log(Level.SEVERE,"File '"+name+"' not found");
        }else{
            try{
                props.load(propIS);
                propIS.close();
            }catch(IOException e){
                Logger.log(e);
            }
        }
    }

    static String getString(String key){
        if(props == null)
            loadProperties("config.properties");
        if(props.containsKey(key) && props.getProperty(key).length() > 0)
            return props.getProperty(key);
        //Logger.log(Level.WARNING,"Missing string '"+key+"'");
        return null;
    }

    static void pause(int time){
        try {
            Logger.log("Waiting "+time+" ms ...");
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Logger.log(Level.WARNING, e);
        }
    }

    static String hideString(int len){
        StringBuilder out = new StringBuilder();
        for(int i = 0; i < len; i++)
            out.append('*');
        return out.toString();
    }

    static OS detectOS(){
        String name = System.getProperty("os.name").toLowerCase();
        for(OS os:OS.values())
            for(String match:os.matches)
                if(name.contains(match))
                    return os;
        return OS.UNKOWN;
    }

    static boolean is64bit(){
        return System.getProperty("os.arch").contains("64");
    }

}
