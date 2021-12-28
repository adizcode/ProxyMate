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
    System.setProperty("webdriver.chrome.driver", "/home/adizcode/Documents/WebDrivers/chromedriver")

    val options = ChromeOptions().apply {
        addArguments("use-fake-device-for-media-stream")
        addArguments("use-fake-ui-for-media-stream")
    }

    val driver = ChromeDriver(options).apply {

        manage().window().maximize()

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

                println("The ${onlineClassKey.time} $onlineClassVal class will start in ${diffMilitaryTime.time}")
                println("Sleeping...")
                Thread.sleep(diffMilitaryTime.toMillis())
            }

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
            findElements(byOnlineClassName)[0].click()

            // Try to access the sessions list dropdown
            try {
                val bySessionsList = By.id("sessions-list")
                explicitWait.until(ExpectedConditions.presenceOfElementLocated(bySessionsList))
                val sessionsList = findElement(bySessionsList)

                var index = 1

                val byFirstRoomLink = By.xpath("//*[@id='sessions-list']/li[$index]/a/span")
                explicitWait.until(ExpectedConditions.presenceOfElementLocated(byFirstRoomLink))
                val firstRoomText = findElement(byFirstRoomLink).getAttribute("innerText")
                println("The first room is $firstRoomText")

                if (firstRoomText == "Course Room") {
                    index = 2
                }

                val byDropdownButton = By.id("sessions-list-dropdown")
                explicitWait.until(ExpectedConditions.presenceOfElementLocated(byDropdownButton))
                explicitWait.until(ExpectedConditions.elementToBeClickable(byDropdownButton))
                findElement(byDropdownButton).click()

                val byClassLink = By.xpath("//*[@id='sessions-list']/li[$index]/a")
                explicitWait.until(ExpectedConditions.presenceOfElementLocated(byClassLink))
                val clickRoom = findElement(byClassLink)
                explicitWait.until(ExpectedConditions.elementToBeClickable(byClassLink))
                clickRoom.click()
                println("Join button clicked!")
                println("Joining: ${clickRoom.findElement(By.tagName("span")).getAttribute("innerText")}")
                val tabs = windowHandles.toList()
                switchTo().window(tabs[1])
            }

            // Error is thrown
            // Room is not yet available, retry after 1 minute
            catch (e: Exception) {
                println("Exception thrown: $e")
                println("Room for the ${onlineClassKey.time} $onlineClassVal class is not yet available.")
                println("Will try again after 1 minute...")
                navigate().back()
                navigate().refresh()
                Thread.sleep(60 * 1000)
                continue
            }

            try {
                val byAudioTestButton = By.xpath("/html/body/div[3]/div/div[2]/div/div/div[2]/button")
                explicitWait.until(ExpectedConditions.presenceOfElementLocated(byAudioTestButton))
                explicitWait.until(ExpectedConditions.elementToBeClickable(byAudioTestButton))
                findElement(byAudioTestButton).click()
            } catch (e: Exception) {
                println("Exception thrown: $e")
                println("Did not come across any audio test button. Moving on.")
            }

            try {
                val byVideoTestButton = By.id("techcheck-video-ok-button")
                explicitWait.until(ExpectedConditions.presenceOfElementLocated(byVideoTestButton))
                explicitWait.until(ExpectedConditions.elementToBeClickable(byVideoTestButton))
                findElement(byVideoTestButton).click()
            } catch (e: Exception) {
                println("Exception thrown: $e")
                println("Did not come across any video test button. Moving on.")
            }

            try {
                val byCloseTutorialButton = By.xpath("/html/body/div[1]/div[2]/div/div/button")
                explicitWait.until(ExpectedConditions.presenceOfElementLocated(byCloseTutorialButton))
                explicitWait.until(ExpectedConditions.elementToBeClickable(byCloseTutorialButton))
                findElement(byCloseTutorialButton).click()
            } catch (e: Exception) {
                println("Exception thrown: $e")
                println("Did not come across any close tutorial button. Moving on.")
            }

            // Open class and log entry
            println("Joining class: $onlineClassVal at ${getCurrentTime()}")

            // Sleep for the remaining class time
            val exitTime = onlineClassKey.add(MilitaryTime(0, 40))
            val remainingTime = exitTime.subtract(MilitaryTime(getCurrentTime()))
            Thread.sleep(remainingTime.toMillis())

            // Close class and log exit
            println("Exiting class: $onlineClassVal at ${getCurrentTime()}")
            close()
            val tabs = windowHandles.toList()
            switchTo().window(tabs[0])
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