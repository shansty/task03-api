import by.itechartgroup.anastasiya.shirochina.pojos.Book;
import by.itechartgroup.anastasiya.shirochina.pojos.Root;
import by.itechartgroup.anastasiya.shirochina.pojos.UserId;
import by.itechartgroup.anastasiya.shirochina.utils.Cookies;
import by.itechartgroup.anastasiya.shirochina.utils.Randomizer;
import by.itechartgroup.anastasiya.shirochina.utils.Reader;
import by.itechartgroup.anastasiya.shirochina.utils.Screenshot;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.RequestOptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginTest {
    Playwright playwright;
    Browser browser;
    Page page;
    BrowserContext context;
    String url = "https://demoqa.com/login";
    String token;
    String userID;
    String userName;
    String expires;

    @BeforeEach
    public void warmUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = context.newPage();
        page.navigate(url);
    }

    @AfterEach
    public void tearsDown() {
        playwright.close();
    }

    @Test
    public void loginFormTest() throws IOException {
        page.getByPlaceholder("UserName").fill(Reader.readPropertyUserName());
        page.getByPlaceholder("Password").fill(Reader.readPropertyPassword());
        page.locator("//button[@id='login']").click();
        page.waitForURL("**/profile");
        List<Cookie> cookies = context.cookies();
        userID = Cookies.getCookieByName("userID", cookies);
        Assertions.assertNotNull(userID);
        token = Cookies.getCookieByName("token", cookies);
        Assertions.assertNotNull(token);
        userName = Cookies.getCookieByName("userName", cookies);
        Assertions.assertNotNull(userName);
        expires = Cookies.getCookieByName("expires", cookies);
        Assertions.assertNotNull(expires);

        page.route("**/*.{png,jpg,jpeg}", route -> route.abort());
        Response response = page.waitForResponse("https://demoqa.com/BookStore/v1/Books", () -> {
            page.locator("//span[text() ='Book Store']").click();
        });
        Screenshot.getScreenshot(page, "screenshots/screenshot.png");
        Assertions.assertEquals(200, response.status());
        String jsonResponse = response.text();
        Gson gson = new Gson();
        Root booksResponse = gson.fromJson(jsonResponse, Root.class);
        List<Book> books = booksResponse.getBooks();
        int quantityOfBooksApi = books.size();
        assertThat(page.locator("//div[@role='rowgroup' and @class='rt-tr-group']//img[contains(@src, '.jpg')]"))
                .hasCount(quantityOfBooksApi);

        String quantityOfPageApi = String.valueOf(Randomizer.randomNumber(1000, 1002));
        page.route("https://demoqa.com/BookStore/v1/Book?ISBN=*", route -> {
            APIResponse newResponse = route.fetch();
            Gson newGson = new Gson();
            JsonObject json = newGson.fromJson(newResponse.text(), JsonObject.class);
            json.remove("pages");
            json.addProperty("pages", quantityOfPageApi);
            route.fulfill(new Route.FulfillOptions().setBody(json.toString()));
        });
        List<Locator> listOfBooks =  page.locator("//div[@role='rowgroup' and @class='rt-tr-group']//div[@class='action-buttons']").all();
        listOfBooks.get(Randomizer.randomNumber(0, listOfBooks.size())).click();
        assertThat(page.locator("//div[@id = 'pages-wrapper']//label[@id = 'userName-value']")).hasText(quantityOfPageApi);

        APIResponse responseNew = playwright.request().newContext().get("https://demoqa.com/Account/v1/User/" + userID,
                RequestOptions.create().setHeader("Authorization", "Bearer " + token));
        String responseNewText = responseNew.text();
        Assertions.assertEquals(Reader.readPropertyUserName(), gson.fromJson(responseNewText, UserId.class).getUsername());
        Assertions.assertEquals(new ArrayList<>(), gson.fromJson(responseNewText, UserId.class).getBooks());
    }
}
