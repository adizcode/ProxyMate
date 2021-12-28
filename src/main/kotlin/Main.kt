fun main() {

    // Specify chromedriver path
    System.setProperty("webdriver.chrome.driver", "/home/adizcode/Documents/WebDrivers/chromedriver")

    MyWebDriver.apply {

        maximizeWindow()

        goToBlackboard()

        acceptCookies()

        login()

        attendClassesAsPerTimeTable(TODAY_TIME_TABLE)
    }

    MyWebDriver.quit()
}