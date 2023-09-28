package by.itechartgroup.anastasiya.shirochina.utils;
import com.microsoft.playwright.options.Cookie;
import java.util.List;

public class Cookies {
    public static String getCookieByName(String cookieName, List<Cookie> list) {
        for (Cookie cookie : list) {
            if (cookie.name.equals(cookieName)) {
                return cookie.value;
            }
        }
        return null;
    }
}
