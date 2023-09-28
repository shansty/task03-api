package by.itechartgroup.anastasiya.shirochina.utils;
import java.io.IOException;

public class Reader {
    public static String readPropertyUserName() throws IOException {
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("application.properties"));
        return System.getProperty("userName");
    }
    public static String readPropertyPassword() throws IOException {
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("application.properties"));
        return System.getProperty("password");
    }
}
