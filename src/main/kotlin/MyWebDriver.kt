import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

object MyWebDriver {

    private val driver: ChromeDriver
    private val explicitWait: WebDriverWait

    init {

        // Setup fake audio / video
        val options = ChromeOptions().apply {
            addArguments("use-fake-device-for-media-stream")
            addArguments("use-fake-ui-for-media-stream")
        }

        driver = ChromeDriver(options)

        explicitWait = initDefaultExplicitWait()
    }

    fun maximizeWindow() {
        driver.manage().window().maximize()
    }

    fun goToBlackboard() {
        driver.get(BLACKBOARD_URL)
    }

    fun acceptCookies() {
        try {
            val byAgreeButton = By.id("agree_button")
            explicitWait.until(ExpectedConditions.presenceOfElementLocated(byAgreeButton))
            driver.findElement(byAgreeButton).click()
        } catch (e: Exception) {
            println("Exception thrown: $e")
            println("Did not come across any cookies dialog box. Moving on.")
        }
    }

    fun login() {
        driver.findElement(By.id("user_id")).sendKeys(USERNAME)
        driver.findElement(By.id("password")).sendKeys(PASSWORD + Keys.ENTER)
    }

    fun attendClassesAsPerTimeTable(timeTable: LinkedHashMap<MilitaryTime, String>) {
        var classIndex = 0

        while (classIndex < timeTable.size) {

            val onlineClassTime: MilitaryTime = timeTable.keys.elementAt(classIndex)
            val onlineClassName = timeTable[onlineClassTime]

            val result = attendClass(onlineClassTime, onlineClassName)

            // Result = false = "Class Missed"
            // Result = true = "Class Attended"
            // Result = null = "Error: Retrying"

            if (result != null) {
                classIndex++
            }
        }
    }

    private fun attendClass(onlineClassTime: MilitaryTime, onlineClassName: String?): Boolean? {

        val currentTime = MilitaryTime(getCurrentTimeString())

        // Entering class after this time gets you marked "Absent"
        val lastEntryTime = onlineClassTime.add(ENTRY_RELAXATION.subtract(MilitaryTime(0, 1)))

        println("Current Time: ${currentTime.time}")
        println("Class Time: ${onlineClassTime.time}")
        println("Last Entry Time: ${lastEntryTime.time}")

        // Class missed
        if (currentTime.isGreaterThan(lastEntryTime)) {
            return false
        }

        if (currentTime.isLessThan(onlineClassTime)) {
            waitForClassToStart(onlineClassTime, currentTime, onlineClassName)
        }

        openClassCoursePage(onlineClassName)

        // Error opening room
        if (!openClassRoom(onlineClassTime, onlineClassName)) {
            return null
        }

        handleAudioTest()

        handleVideoTest()

        handleTutorialOverlay()

        // Open class and log entry
        println("Joining class: $onlineClassName at ${getCurrentTimeString()}")

        waitForClassToFinish(onlineClassTime)

        exitClass(onlineClassName)

        return true
    }

    private fun waitForClassToStart(
        onlineClassTime: MilitaryTime,
        currentTime: MilitaryTime,
        onlineClassName: String?
    ) {
        val diffTime = onlineClassTime.subtract(currentTime)

        println("The ${onlineClassTime.time} $onlineClassName class will start in ${diffTime.time}")
        println("Sleeping...")
        Thread.sleep(diffTime.toMillis())
    }

    private fun openClassCoursePage(onlineClassName: String?) {
        searchByClassName(onlineClassName)
        clickOnClassCourse(onlineClassName)
    }

    private fun searchByClassName(onlineClassName: String?) {
        val bySearchBox = By.cssSelector("input[aria-label='Search your courses']")
        explicitWait.until(ExpectedConditions.presenceOfElementLocated(bySearchBox))
        driver.findElement(bySearchBox).apply {
            clear()
            sendKeys(onlineClassName)
        }
    }

    private fun clickOnClassCourse(onlineClassName: String?) {
        val byOnlineClassName = By.linkText(onlineClassName!!.uppercase())
        explicitWait.until(ExpectedConditions.presenceOfElementLocated(byOnlineClassName))
        driver.findElements(byOnlineClassName)[0].click()
    }

    private fun openClassRoom(onlineClassTime: MilitaryTime, onlineClassName: String?): Boolean {
        // Try to access the sessions list dropdown
        try {
            val bySessionsList = By.id("sessions-list")
            explicitWait.until(ExpectedConditions.presenceOfElementLocated(bySessionsList))
            val sessionsList = driver.findElement(bySessionsList)

            val roomIndex = if (isFirstRoomCalledCourseRoom()) 2 else 1

            openSessionsListDropdown()

            clickOnClassRoomLink(roomIndex)

            switchToNewTab()

            return true
        }

        // Error is thrown
        // Room is not yet available, retry after 1 minute
        catch (e: Exception) {
            println("Exception thrown: $e")
            println("Room for the ${onlineClassTime.time} $onlineClassName class is not yet available.")
            println("Will try again after 1 minute...")
            driver.navigate().back()
            driver.navigate().refresh()
            Thread.sleep(60 * 1000)
            return false
        }
    }

    private fun isFirstRoomCalledCourseRoom(): Boolean {
        val byFirstRoomLink = By.xpath("//*[@id='sessions-list']/li[1]/a/span")
        explicitWait.until(ExpectedConditions.presenceOfElementLocated(byFirstRoomLink))
        val firstRoomText = driver.findElement(byFirstRoomLink).getAttribute("innerText")

        println("The first room is $firstRoomText")

        return firstRoomText == "Course Room"
    }

    private fun openSessionsListDropdown() {
        val byDropdownButton = By.id("sessions-list-dropdown")
        explicitWait.until(ExpectedConditions.presenceOfElementLocated(byDropdownButton))
        explicitWait.until(ExpectedConditions.elementToBeClickable(byDropdownButton))
        driver.findElement(byDropdownButton).click()
    }

    private fun clickOnClassRoomLink(roomIndex: Int) {
        val byClassLink = By.xpath("//*[@id='sessions-list']/li[$roomIndex]/a")
        explicitWait.until(ExpectedConditions.presenceOfElementLocated(byClassLink))
        val clickRoom = driver.findElement(byClassLink)
        explicitWait.until(ExpectedConditions.elementToBeClickable(byClassLink))
        clickRoom.click()
        println("Join button clicked!")
        println("Joining: ${clickRoom.findElement(By.tagName("span")).getAttribute("innerText")}")
    }

    private fun switchToNewTab() {
        val tabs = driver.windowHandles.toList()
        driver.switchTo().window(tabs[1])
    }

    private fun handleAudioTest() {
        try {
            val byAudioTestButton = By.xpath("/html/body/div[3]/div/div[2]/div/div/div[2]/button")
            explicitWait.until(ExpectedConditions.presenceOfElementLocated(byAudioTestButton))
            explicitWait.until(ExpectedConditions.elementToBeClickable(byAudioTestButton))
            driver.findElement(byAudioTestButton).click()
        } catch (e: Exception) {
            println("Exception thrown: $e")
            println("Did not come across any audio test button. Moving on.")
        }
    }

    private fun handleVideoTest() {
        try {
            val byVideoTestButton = By.id("techcheck-video-ok-button")
            explicitWait.until(ExpectedConditions.presenceOfElementLocated(byVideoTestButton))
            explicitWait.until(ExpectedConditions.elementToBeClickable(byVideoTestButton))
            driver.findElement(byVideoTestButton).click()
        } catch (e: Exception) {
            println("Exception thrown: $e")
            println("Did not come across any video test button. Moving on.")
        }
    }

    private fun handleTutorialOverlay() {
        try {
            val byCloseTutorialButton = By.xpath("/html/body/div[1]/div[2]/div/div/button")
            explicitWait.until(ExpectedConditions.presenceOfElementLocated(byCloseTutorialButton))
            explicitWait.until(ExpectedConditions.elementToBeClickable(byCloseTutorialButton))
            driver.findElement(byCloseTutorialButton).click()
        } catch (e: Exception) {
            println("Exception thrown: $e")
            println("Did not come across any close tutorial button. Moving on.")
        }
    }

    private fun waitForClassToFinish(onlineClassTime: MilitaryTime) {
        val exitTime = onlineClassTime.add(CLASS_DURATION)
        val remainingTime = exitTime.subtract(MilitaryTime(getCurrentTimeString()))
        Thread.sleep(remainingTime.toMillis())
    }

    private fun exitClass(onlineClassName: String?) {
        println("Exiting class: $onlineClassName at ${getCurrentTimeString()}")
        closeCurrentTab()

        switchToPreviousTab()

        goBackAndRefresh()
    }

    private fun closeCurrentTab() {
        driver.close()
    }

    private fun switchToPreviousTab() {
        val tabs = driver.windowHandles.toList()
        driver.switchTo().window(tabs[0])
    }

    private fun goBackAndRefresh() {
        driver.navigate().back()
        driver.navigate().refresh()
    }

    fun quit() {
        driver.close()
        driver.quit()
    }

    private fun initDefaultExplicitWait(): WebDriverWait {
        return WebDriverWait(driver, Duration.ofSeconds(15))
    }
}