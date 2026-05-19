import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class AutomationTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {

        // 🔕 CONFIGURACIÓN CHROME SIN POPUPS NI PASSWORD MANAGER
        ChromeOptions options = new ChromeOptions();

        // ❌ desactiva gestor de contraseñas de Google
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);

        options.setExperimentalOption("prefs", prefs);

        // ❌ notificaciones y popups
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--incognito");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-features=PasswordLeakDetection");

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
        driver.get("https://www.saucedemo.com");
    }

    @Test
    public void loginTest() {

        // LOGIN
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"));

        // AGREGAR PRODUCTO
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();


        driver.findElement(By.id("add-to-cart-sauce-labs-bike-light")).click();

        String cantidadCarrito =
                driver.findElement(By.className("shopping_cart_badge")).getText();

        Assert.assertEquals(cantidadCarrito, "2");

        driver.findElement(By.className("shopping_cart_link")).click();

        Assert.assertTrue(driver.findElement(By.className("inventory_item_name")).isDisplayed());

        // CHECKOUT
        driver.findElement(By.id("checkout")).click();

        driver.findElement(By.id("first-name")).sendKeys("Juan");
        driver.findElement(By.id("last-name")).sendKeys("Perez");
        driver.findElement(By.id("postal-code")).sendKeys("1000");

        driver.findElement(By.id("continue")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("finish")));


        driver.findElement(By.id("finish")).click();

        String mensaje =
                driver.findElement(By.className("complete-header")).getText();

        Assert.assertEquals(mensaje, "Thank you for your order!");


        System.out.println(driver.getCurrentUrl());

    }

    @Test
    public void loginIncorrectoTest() {

        driver.findElement(By.id("user-name")).sendKeys("usuario_falso");

        driver.findElement(By.id("password")).sendKeys("clave_falsa");

        driver.findElement(By.id("login-button")).click();

        String error =
                driver.findElement(By.className("error-message-container"))
                        .getText();

        Assert.assertTrue(error.contains("Username and password do not match"));

    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }
}