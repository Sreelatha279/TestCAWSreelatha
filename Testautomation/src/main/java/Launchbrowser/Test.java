package Launchbrowser;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Test {

    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://testpages.herokuapp.com/styled/tag/dynamic-table.html");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        WebElement ele = driver.findElement(By.xpath("//details/summary"));
        ele.click();

        WebElement json = driver.findElement(By.id("jsondata"));
        json.clear();

        String jsonData = "[{\"name\" : \"Bob\", \"age\" : 20, \"gender\": \"male\"}, " +
                "{\"name\": \"George\", \"age\" : 42, \"gender\": \"male\"}, " +
                "{\"name\": \"Sara\", \"age\" : 42, \"gender\": \"female\"}, " +
                "{\"name\": \"Conor\", \"age\" : 40, \"gender\": \"male\"}, " +
                "{\"name\": \"Jennifer\", \"age\" : 42, \"gender\": \"female\"}]";

        json.sendKeys(jsonData);

        WebElement refresh = driver.findElement(By.id("refreshtable"));
        refresh.click();

        List<JsonNode> expectedData = parseJSONData(jsonData);

        WebElement table = driver.findElement(By.id("dynamictable"));
        String tableData = table.getText();

        boolean dataMatches = compareData(expectedData, tableData);

        if (dataMatches) {
            System.out.println("Data matches!");
        } else {
            System.out.println("Data does not match.");
        }

        driver.quit();
    }

    private static List<JsonNode> parseJSONData(String jsonData) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonData,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, JsonNode.class));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean compareData(List<JsonNode> expectedData, String tableData) {
        String[] tableRows = tableData.split("\n");
        for (String row : tableRows) {
            for (JsonNode node : expectedData) {
                String expectedRow = node.get("name").asText() + " " + node.get("age").asInt() + " "
                        + node.get("gender").asText();
                if (row.contains(expectedRow)) {
                    return true;
                }
            }
        }
        return false;
    }
}
