import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;

public class ActivitiesAPITest {

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
            .body("data.totalFound", greaterThanOrEqualTo(100))
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
            .body("data.recordData.type",
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
            .body("data.recordData[0].identifier",
                everyItem(
                    containsString(testIdentifier)
                )
            )
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
            System.out.println(titles.toString());
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
        final String testDescription = "unique biology";
        given().queryParam("institution", testDescription)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.recordData.description",
                everyItem(
                    containsString(testDescription)
                )
            );
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
            );
    }

    @Test
    public void testParamFunder() {
        final String testFunder = "Australian Research Council";
        Response response = given().queryParam("funder", '"'+testFunder+'"')
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .extract().response();

        ArrayList<ArrayList<String>> allItems =
            response.path("data.recordData.funder");

        for (ArrayList<String> items : allItems) {
            Assert.assertThat(
                items.toString().toLowerCase(),
                containsString(testFunder.toLowerCase())
            );
        }
    }

    @Test
    public void testParamPrincipalInvestigator() {
        final String testPrincipalInvestigator = "Jacob George";
        Response response =
            given()
                .queryParam("principalInvestigator", testPrincipalInvestigator)
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
            given()
                .queryParam("researcher", testResearcher)
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
        given()
            .queryParam("fundingScheme", testFundingScheme)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.recordData.fundingScheme",
                everyItem(
                    containsString(testFundingScheme)
                )
            );
    }

    @Test
    public void testParamAddedSince() throws ParseException {
        // TODO check and implement addedSince
        final String testAddedSince = "20151208";
        Response response = given()
            .queryParam("addedSince", testAddedSince)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200).extract().response();

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
        DateFormat returnFormat = new SimpleDateFormat("Y", Locale.ENGLISH);

        Date start = format.parse(testAddedSince);

        ArrayList<String> allCreatedWhen =
            response.path("data.recordData.dateTimeCreated");

        // assert that the return date is after the start date
        for (String date : allCreatedWhen) {
            Date compare = returnFormat.parse(date);
            Assert.assertTrue(compare.compareTo(start) >= 0);
        }

    }

    @Test
    public void testParamModifiedSince() throws ParseException {
        // TODO check and implement addedSince
        final String testModifiedSince = "20151208";
        Response response = given()
            .queryParam("modifiedSince", testModifiedSince)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200).extract().response();

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
        DateFormat returnFormat = new SimpleDateFormat("Y", Locale.ENGLISH);

        Date start = format.parse(testModifiedSince);

        ArrayList<String> allModifiedWhen =
            response.path("data.recordData.dateTimeModified");

        // assert that the return date is after the start date
        for (String date : allModifiedWhen) {
            Date compare = returnFormat.parse(date);
            Assert.assertTrue(compare.compareTo(start) >= 0);
        }
    }

    @Test
    public void testCombination() {
        Response response = given()
            .queryParam("description", "unique")
            .queryParam("type", "grant")
            .queryParam("title", "caves climate")
            .queryParam("subject", "Earth Sciences")
            .queryParam("funder", "Australian Research Council")
            .queryParam("fl", "title")
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
                .body("data.recordData.description", everyItem(containsString("unique")))
                .body("data.recordData.type", everyItem(equalTo("grant")))
            .extract().response()
        ;

        //funders
        ArrayList<ArrayList<String>> allFunders =
            response.path("data.recordData.funder");

        for (ArrayList<String> funders : allFunders) {
            Assert.assertThat(
                funders.toString(),
                containsString("Australian Research Council")
            );
        }

        ArrayList<ArrayList<String>> allSubjects =
            response.path("data.recordData.subjects");

        for (ArrayList<String> subjects : allSubjects) {
            Assert.assertThat(
                subjects.toString().toLowerCase(),
                anyOf(
                    containsString("earth"),
                    containsString("science")
                )
            );
        }

        ArrayList<ArrayList<String>> allTitles =
            response.path("data.recordData.titles");

        for (ArrayList<String> titles : allTitles) {
            Assert.assertThat(
                titles.toString().toLowerCase(),
                anyOf(
                    containsString("cave"),
                    containsString("climate"),
                    containsString("climat")
                )
            );
        }
    }
}
