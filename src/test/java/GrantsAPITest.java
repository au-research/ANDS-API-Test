import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.parsing.Parser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import com.jayway.restassured.response.Response;

/**
 * Created by lwoods on 5/05/2016.
 */

public class GrantsAPITest {

    private static Properties props;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        APIProperties localProperties = new APIProperties();
        props = localProperties.getProp();

        System.out.println("Getting and verifying configuration");
        System.out.println("Testing...");

        RestAssured.useRelaxedHTTPSValidation();
        //RestAssured.registerParser("Content-type: application/json", Parser.JSON);
    }

    @Test
    public void getBaseUrl() {
        when().get(props.getProperty("grant_api_url")).then().statusCode(200);
    }

    @Test
    public void getDefaultResponse() {
        when().get(props.getProperty("grant_api_url"))
                .then().statusCode(200)
                .body("status", equalTo("OK"))
                .contentType(ContentType.JSON)
        ;
    }

    @Test
    public void testSearch() {
        given().queryParam("q",  "fish").
                when().get(props.getProperty("grant_api_url"))
                .then().statusCode(200)
                .body("data.totalFound", greaterThanOrEqualTo(410))
        ;
    }

    @Test
    public void testSearchTitle() {
        given().queryParam("title",  "cancer").queryParam("fl",  "titles").
                when().get(props.getProperty("grant_api_url"))
                .then().statusCode(200)
                .body("data.recordData.title",
                        everyItem(
                                anyOf(
                                        containsString("cancer"),
                                        containsString("Cancer")
                                )
                        )
                );

    }
}
