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

            val onlineClassKey: MilitaryTime = timeTable.keys.elementAt(i)
            val onlineClassVal = timeTable[onlineClassKey]

            val currentMilitaryTime = MilitaryTime(getCurrentTime())
            val lastEntryTime = onlineClassKey.add(MilitaryTime(0, 10))

            println("Current Time: ${currentMilitaryTime.time}")
            println("Class Time: ${onlineClassKey.time}")
            println("Last Entry Time: ${lastEntryTime.time}")

            // Class missed
            if (currentMilitaryTime.isGreaterThan(lastEntryTime)) {
                println("Missed the ${onlineClassKey.time} $onlineClassVal class")
                i++
                continue
            }

            // Current class has not yet started, wait it out
            if (currentMilitaryTime.isLessThan(onlineClassKey)) {

                val diffMilitaryTime = onlineClassKey.subtract(currentMilitaryTime)

                println("The $onlineClassKey $onlineClassVal class will start in ${diffMilitaryTime.time}")
                println("Sleeping...")
                Thread.sleep(diffMilitaryTime.toMillis())
            }

            // Wait for the search icon to appear and then click on it
            val bySearchIcon = By.cssSelector("#main-content-inner > div > header > div > div > button > bb-svg-icon")
            explicitWait.until(ExpectedConditions.presenceOfElementLocated(bySearchIcon))
            findElement(bySearchIcon).click()

            // Type subject name in the search box
            val bySearchBox = By.cssSelector("input[aria-label='Search your courses']")
            explicitWait.until(ExpectedConditions.presenceOfElementLocated(bySearchBox))
            findElement(bySearchBox).apply {
                clear()
                sendKeys(onlineClassVal)
            }

            // Wait for the desired course to be visible and click on it
            val byOnlineClassName = By.linkText(onlineClassVal)
            explicitWait.until(ExpectedConditions.presenceOfElementLocated(byOnlineClassName))
            findElement(byOnlineClassName).click()

            // Room is not yet available, retry after 1 minute
            // TODO: Place a proper condition
            if (true) {
                println("Room for the ${onlineClassKey.time} $onlineClassVal class is not yet available.")
                println("Will try again after 1 minute...")
                navigate().back()
                navigate().refresh()
                Thread.sleep(60 * 1000)
                continue
            }

            // Open class and log entry
            println("Joining class: $onlineClassVal at ${getCurrentTime()}")

            // TODO: Sleep for the remaining class time
            Thread.sleep(5000)

            // Close class and log exit
            println("Exiting class: $onlineClassVal at ${getCurrentTime()}")
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