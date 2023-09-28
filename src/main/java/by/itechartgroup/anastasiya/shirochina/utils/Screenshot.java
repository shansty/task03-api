package by.itechartgroup.anastasiya.shirochina.utils;
import com.microsoft.playwright.Page;
import java.nio.file.Paths;

public class Screenshot {
    public static void getScreenshot(Page page, String path) {
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(path)));
    }
}
