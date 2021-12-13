import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

fun main() {

    val timeTable = TIME_TABLE.toMutableMap()

    // Specify chromedriver path
    System.setProperty("webdriver.chrome.driver", "/opt/WebDriver/bin/chromedriver")

    // TODO: Removing this causes Chrome to crash
    val options = ChromeOptions().apply {
        addArguments("--no-sandbox")
    }

    val driver = ChromeDriver(options).apply {

        val explicitWait = WebDriverWait(this, Duration.ofSeconds(15))

        // 1. Open Blackboard
        get("https://cuchd.blackboard.com/")

        // 2. Handle cookie dialog box
        val byAgreeButton = By.id("agree_button")
        explicitWait.until(ExpectedConditions.presenceOfElementLocated(byAgreeButton))
        findElement(byAgreeButton).click()

        // 3. Login
        findElement(By.id("user_id")).sendKeys(USERNAME)
        findElement(By.id("password")).sendKeys(PASSWORD + Keys.ENTER)

        var i = 0

        while (i < timeTable.size) {

            val lectureKey: MilitaryTime = timeTable.keys.elementAt(i)
            val lectureValue = timeTable[lectureKey]

            // Wait for the search icon to appear and then click on it
            val bySearchIcon = By.cssSelector("#main-content-inner > div > header > div > div > button > bb-svg-icon")
            explicitWait.until(ExpectedConditions.presenceOfElementLocated(bySearchIcon))
            findElement(bySearchIcon).click()

            // Type subject name in the search box
            val bySearchBox = By.cssSelector("input[aria-label='Search your courses']")
            explicitWait.until(ExpectedConditions.presenceOfElementLocated(bySearchBox))
            findElement(bySearchBox).apply {
                clear()
                sendKeys(lectureValue)
            }

            // Wait for the desired course to be visible
            val byLectureName = By.linkText(lectureValue)
            explicitWait.until(ExpectedConditions.presenceOfElementLocated(byLectureName))


            val currentMilitaryTime = MilitaryTime(getCurrentTime())

            // TODO: If current lecture has not yet started, wait it out

            // Open class and log entry
            findElement(byLectureName).click()
            println("Joining class: $lectureValue at ${getCurrentTime()}")

            // TODO: Sleep for the remaining lecture time
            Thread.sleep(5000)

            // Close class and log exit
            println("Exiting class: $lectureValue at ${getCurrentTime()}")
            navigate().back()
            navigate().refresh()

            i++
        }
    }

    driver.apply {
        close()
        quit()
    }
}