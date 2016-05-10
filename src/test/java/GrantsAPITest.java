import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Properties;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;

// TODO rename to ActivitiesAPITest
public class GrantsAPITest {

    private static Properties props;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        APIProperties localProperties = new APIProperties();
        props = localProperties.getProp();

        System.out.println("Getting and verifying configuration");
        System.out.println("Testing...");

        RestAssured.useRelaxedHTTPSValidation();
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
        given().queryParam("q", "fish")
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.totalFound", greaterThanOrEqualTo(410))
        ;
    }

    @Test
    public void testParamQuery() {
        given().queryParam("q", "fish")
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.totalFound", greaterThanOrEqualTo(100));
    }

    @Test
    public void testParamType() {
        final String testType = "grant";
        given().queryParam("type", testType)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.recordData.status",
                everyItem(
                    equalTo(testType)
                )
            )
        ;
    }

    @Test
    public void testParamStatus() {
        final String testStatus = "active";
        given().queryParam("status", testStatus)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.recordData.status",
                everyItem(
                    equalTo(testStatus)
                )
            )
        ;
    }

    @Test
    public void testParamPurl() {
        final String testPurl = "http://purl.org/au-research/grants/arc/LP0776938";
        given().queryParam("purl", testPurl)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.totalFound", equalTo(1))
            .body("data.numFound", equalTo(1))
            .body("data.recordData.size()", equalTo(1))
            .body("data.recordData[0].purl", equalTo(testPurl))
        ;
    }

    @Test
    public void testParamIdentifier() {
        final String testIdentifier = "LP0776938";
        given().queryParam("purl", testIdentifier)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.totalFound", equalTo(1))
            .body("data.numFound", equalTo(1))
            .body("data.recordData.size()", equalTo(1))
            .body("data.recordData.size()", greaterThanOrEqualTo(1))
            .body("data.recordData[0].identifier", everyItem(containsString(testIdentifier)))
        ;
    }

    @Test
    public void testParamTitle() {
        final String testString = "cancer clustering";
        Response response = given()
            .queryParam("title", testString)
            .queryParam("fl", "title")
            .queryParam("rows", 15)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .extract().response();

        ArrayList<ArrayList<String>> allTitles =
            response.path("data.recordData.titles");

        for (ArrayList<String> titles : allTitles) {
            Assert.assertThat(
                titles.toString().toLowerCase(),
                anyOf(
                    containsString("cluster"),
                    containsString("cancer")
                )
            );
        }
    }

    @Test
    public void testParamSubject() {
        Response response = given()
            .queryParam("subject", "intelligent agents")
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .extract().response();

        ArrayList<ArrayList<String>> allSubjects =
            response.path("data.recordData.subjects");

        for (ArrayList<String> subjects : allSubjects) {
            Assert.assertThat(
                subjects.toString().toLowerCase(),
                anyOf(
                    containsString("intel"),
                    containsString("agent")
                )
            );
        }
    }

    @Test
    public void testParamDescription() {
        // TODO; fix description and test description
    }

    @Test
    public void testParamInstitution() {
        final String testInstitution = "University of Sydney";
        given().queryParam("institution", testInstitution)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.recordData.institutions",
                everyItem(
                    everyItem(
                        containsString(testInstitution)
                    )
                )
            )
        ;
    }

    @Test
    public void testParamFunder() {
        final String testFunder = "Australian Research Council";
        Response response = given().queryParam("funder", testFunder)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .extract().response();

        ArrayList<ArrayList<String>> allItems =
            response.path("data.recordData.funder");

        for (ArrayList<String> items : allItems) {
            Assert.assertThat(
                items.toString(),
                containsString(testFunder)
            );
        }
    }

    @Test
    public void testParamPrincipalInvestigator() {
        final String testPrincipalInvestigator = "Jacob George";
        Response response =
            given().queryParam("principalInvestigator", testPrincipalInvestigator)
                .when().get(props.getProperty("grant_api_url"))
                .then().statusCode(200)
                .extract().response();

        ArrayList<ArrayList<String>> allItems =
            response.path("data.recordData.principalInvestigator");

        for (ArrayList<String> items : allItems) {
            Assert.assertThat(
                items.toString(),
                containsString(testPrincipalInvestigator)
            );
        }
    }

    @Test
    public void testParamPrincipalResearcher() {
        final String testResearcher = "Jacob George";
        Response response =
            given().queryParam("researcher", testResearcher)
                .when().get(props.getProperty("grant_api_url"))
                .then().statusCode(200)
                .extract().response();

        ArrayList<ArrayList<String>> allItems =
            response.path("data.recordData.researchers");

        for (ArrayList<String> items : allItems) {
            Assert.assertThat(
                items.toString(),
                containsString(testResearcher)
            );
        }
    }

    @Test
    public void testParamFundingScheme() {
        final String testFundingScheme = "NHMRC Project Grants";
        given().queryParam("fundingScheme", testFundingScheme)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.recordData.fundingScheme",
                everyItem(
                    containsString(testFundingScheme)
                )
            );
    }

    @Test
    public void testParamAddedSince() {
        // TODO check and implement addedSince
        final String testAddedSince = "20151128";
        given().queryParam("addedSince", testAddedSince)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.recordData.dateTimeCreated",
                everyItem(
                    containsString(testAddedSince)
                )
            );
    }

    @Test
    public void testParamModifiedSince() {
        // TODO check and implement modifiedSince
        final String testModifiedSince = "20151128";
        given().queryParam("addedSince", testModifiedSince)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.recordData.dateTimeModified",
                everyItem(
                    containsString(testModifiedSince)
                )
            );
    }

    // TODO: combination test, test different params together




}
