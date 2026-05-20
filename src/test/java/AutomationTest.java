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

    @Test
    public void eliminarProductoDelCarritoTest() {

        // LOGIN
        driver.findElement(By.id("user-name")).sendKeys("standard_user");

        driver.findElement(By.id("password")).sendKeys("secret_sauce");

        driver.findElement(By.id("login-button")).click();


        // VALIDAR LOGIN
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"));

        // AGREGAR PRODUCTO
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();

        // VALIDAR QUE EL CARRITO TENGA 1 PRODUCTO
        String cantidadCarrito =
                driver.findElement(By.className("shopping_cart_badge")).getText();

        Assert.assertEquals(cantidadCarrito, "1");

        // IR AL CARRITO
        driver.findElement(By.className("shopping_cart_link")).click();

        // VALIDAR QUE EL PRODUCTO ESTÉ EN EL CARRITO
        Assert.assertTrue(
                driver.findElement(By.className("inventory_item_name"))
                        .isDisplayed()
        );

        // ELIMINAR PRODUCTO
        driver.findElement(By.id("remove-sauce-labs-backpack")).click();

        // VALIDAR QUE EL CARRITO QUEDE VACÍO
        int cantidadProductos =
                driver.findElements(By.className("cart_item")).size();

        Assert.assertEquals(cantidadProductos, 0);

        System.out.println("Producto eliminado correctamente");
    }

    @Test
    public void logoutTest() {

        // LOGIN
        driver.findElement(By.id("user-name")).sendKeys("standard_user");

        driver.findElement(By.id("password")).sendKeys("secret_sauce");

        driver.findElement(By.id("login-button")).click();

        // VALIDAR LOGIN
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"));

        // ABRIR MENÚ LATERAL
        driver.findElement(By.id("react-burger-menu-btn")).click();

        // ESPERAR QUE EL BOTÓN LOGOUT SEA CLICKEABLE
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.id("logout_sidebar_link")
                )
        );

        // CLICK EN LOGOUT
        driver.findElement(By.id("logout_sidebar_link")).click();

        // VALIDAR QUE VOLVIÓ AL LOGIN
        Assert.assertTrue(driver.getCurrentUrl().contains("saucedemo.com"));

        // VALIDAR BOTÓN LOGIN VISIBLE
        Assert.assertTrue(
                driver.findElement(By.id("login-button")).isDisplayed()
        );

        System.out.println("Logout realizado correctamente");
    }

    @Test
    public void checkoutVacioTest() {

        // LOGIN
        driver.findElement(By.id("user-name")).sendKeys("standard_user");

        driver.findElement(By.id("password")).sendKeys("secret_sauce");

        driver.findElement(By.id("login-button")).click();

        // VALIDAR LOGIN
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"));

        // AGREGAR PRODUCTO
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();

        // IR AL CARRITO
        driver.findElement(By.className("shopping_cart_link")).click();

        // IR AL CHECKOUT
        driver.findElement(By.id("checkout")).click();

        // CLICK EN CONTINUE SIN COMPLETAR DATOS
        driver.findElement(By.id("continue")).click();

        // OBTENER MENSAJE DE ERROR
        String error =
                driver.findElement(By.className("error-message-container"))
                        .getText();

        // VALIDAR ERROR
        Assert.assertTrue(error.contains("First Name is required"));

        System.out.println("Validación de checkout vacío correcta");
    }

    @Test
    public void usuarioBloqueadoTest() {

        // INGRESAR USUARIO BLOQUEADO
        driver.findElement(By.id("user-name")).sendKeys("locked_out_user");

        driver.findElement(By.id("password")).sendKeys("secret_sauce");

        driver.findElement(By.id("login-button")).click();

        // OBTENER MENSAJE DE ERROR
        String error =
                driver.findElement(By.className("error-message-container"))
                        .getText();

        // VALIDAR MENSAJE
        Assert.assertTrue(
                error.contains("Sorry, this user has been locked out")
        );

        System.out.println("Usuario bloqueado validado correctamente");
    }

    @Test
    public void validarTotalCheckoutTest() {

        // LOGIN
        driver.findElement(By.id("user-name")).sendKeys("standard_user");

        driver.findElement(By.id("password")).sendKeys("secret_sauce");

        driver.findElement(By.id("login-button")).click();

        // VALIDAR LOGIN
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"));

        // AGREGAR PRODUCTO
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();

        // IR AL CARRITO
        driver.findElement(By.className("shopping_cart_link")).click();

        // IR AL CHECKOUT
        driver.findElement(By.id("checkout")).click();

        // COMPLETAR DATOS
        driver.findElement(By.id("first-name")).sendKeys("Juan");

        driver.findElement(By.id("last-name")).sendKeys("Perez");

        driver.findElement(By.id("postal-code")).sendKeys("1000");

        driver.findElement(By.id("continue")).click();

        // OBTENER TOTAL
        String total =
                driver.findElement(By.className("summary_total_label"))
                        .getText();

        // VALIDAR TOTAL
        Assert.assertTrue(total.contains("Total"));

        System.out.println("Total validado correctamente");
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }
}