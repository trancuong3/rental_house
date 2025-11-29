package pkg210;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Test210 {

    public static void main(String[] args) throws InterruptedException, IOException {

        System.setProperty("webdriver.chrome.driver", "C:\\Users\\TechCare\\Desktop\\selenium\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get("file:///C:/Users/TechCare/Desktop/index.html"); 
        driver.manage().window().maximize();

        FileWriter writer = new FileWriter("TestCaseResults.txt");

        // Chạy 3 chức năng đầu
        for (int fn = 1; fn <= 3; fn++) {

            writer.write("=======================================\n");
            writer.write("KET QUA TEST CASE CHO CHUC NANG FN" + fn + "\n");
            writer.write("=======================================\n");
            writer.write("TestCase | A | B | C | D | Expected | Actual | PASS/FAIL\n");

            System.out.println("=======================================");
            System.out.println("KET QUA TEST CASE CHO CHUC NANG FN" + fn);
            System.out.println("TestCase | A | B | C | D | Expected | Actual | PASS/FAIL");

            // Chọn chức năng trong dropdown
            Select functionSelect = new Select(driver.findElement(By.id("fnSelect")));
            functionSelect.selectByValue(String.valueOf(fn));
            Thread.sleep(500);

            // bấm Generate 70 samples
            WebElement genBtn = driver.findElement(By.xpath("//button[text()='Generate 70 samples']"));
            genBtn.click();
            Thread.sleep(500);

            // bấm Run selected
            WebElement runBtn = driver.findElement(By.xpath("//button[text()='Run selected']"));
            runBtn.click();
            Thread.sleep(3000);

            // Lấy tất cả dòng trong bảng kết quả
            List<WebElement> rows = driver.findElements(By.cssSelector("#resultsTable tbody tr"));

            int testNo = 1;
            for (WebElement row : rows) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                String input = cells.get(1).getText(); // JSON input
                String expected = cells.get(2).getText();
                String actual = cells.get(3).getText();
                String status = cells.get(4).getText();

                // parse input JSON để lấy A,B,C,D
                input = input.replace("{","").replace("}","").replace("\"",""); // simple clean
                String a="",b="",c="",d="";
                for (String part : input.split(",")) {
                    String[] kv = part.split(":");
                    if(kv.length<2) continue;
                    String key = kv[0].trim();
                    String val = kv[1].trim();
                    switch(key){
                        case "a": a=val; break;
                        case "b": b=val; break;
                        case "c": c=val; break;
                        case "d": d=val; break;
                    }
                }

                String line = testNo + " | " + a + " | " + b + " | " + c + " | " + d + " | " + expected + " | " + actual + " | " + status;
                writer.write(line + "\n");
                System.out.println(line);
                testNo++;
            }

            writer.write("\n");
            System.out.println();
        }

        writer.close();
        driver.quit();
        System.out.println("Da luu ket qua vao file TestCaseResults.txt");
    }
}
