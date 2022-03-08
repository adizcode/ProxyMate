import model.ProcessedConfig
import webdriver.MyWebDriver

// 2. Create an example.config.yaml file for the user to copy
// 3. Tell the user to create a config.yaml with their own settings
// 4. Add all .yaml files to gitignore (except example.config.yaml)

fun main() {

    val processedConfig = ProcessedConfig.readAndProcessConfig()

    // Specify chromedriver path
    System.setProperty("webdriver.chrome.driver", "/home/adizcode/Documents/WebDrivers/chromedriver")

    val webDriver = MyWebDriver(processedConfig)

    webDriver.apply {

        maximizeWindow()

        goToBlackboard()

        acceptCookies()

        login()

        attendClassesAsPerTimeTable()
    }

    webDriver.quit()
}