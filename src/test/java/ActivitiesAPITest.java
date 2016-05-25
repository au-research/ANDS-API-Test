import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
        RestAssured.useRelaxedHTTPSValidation();
    }

    /**
     * api/activities
     */
    @Test
    public void getBaseUrl() {
        when().get(props.getProperty("grant_api_url")).then().statusCode(200);
    }

    /**
     * api/activities
     */
    @Test
    public void getDefaultResponse() {
        when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("status", equalTo("OK"))
            .contentType(ContentType.JSON)
        ;
    }

    /**
     * api/activities/?q=fish
     */
    @Test
    public void testSearch() {
        given().queryParam("q", "fish")
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.numFound", greaterThanOrEqualTo(100))
        ;
    }

    /**
     * api/activities/?q=fish
     */
    @Test
    public void testParamQuery() {
        given().queryParam("q", "fish")
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.numFound", greaterThanOrEqualTo(100));
    }

    /**
     * api/activities/?type=grant
     */
    @Test
    public void testParamType() {
        final String testType = "grant";
        given().queryParam("type", testType)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.records.type",
                everyItem(
                    equalTo(testType)
                )
            )
        ;
    }

    /**
     * api/activities/?status=active
     */
    @Test
    public void testParamStatus() {
        final String testStatus = "active";
        given().queryParam("status", testStatus)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.records.status",
                everyItem(
                    equalTo(testStatus)
                )
            )
        ;
    }

    /**
     * api/activities/?purl=http://purl.org/au-research/grants/arc/LP0776938
     */
    @Test
    public void testParamPurl() {
        final String testPurl = "http://purl.org/au-research/grants/arc/LP0776938";
        given().queryParam("purl", testPurl)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.numFound", equalTo(1))
            .body("data.records.size()", equalTo(1))
            .body("data.records[0].purl", equalTo(testPurl))
        ;
    }

    /**
     * api/activities/?purl=LP0776938
     */
    @Test
    public void testParamIdentifier() {
        final String testIdentifier = "LP0776938";
        given().queryParam("purl", testIdentifier)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.numFound", equalTo(1))
            .body("data.records.size()", equalTo(1))
            .body("data.records[0].identifiers",
                everyItem(
                    containsString(testIdentifier)
                )
            )
        ;
    }

    /**
     * api/activities/?title=cancer clustering&flags=titles
     */
    @Test
    public void testParamTitle() {
        final String testString = "cancer clustering";
        Response response = given()
            .queryParam("title", testString)
            .queryParam("flags", "titles")
            .queryParam("rows", 15)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .extract().response();

        ArrayList<ArrayList<String>> allTitles =
            response.path("data.records.titles");

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

    /**
     * api/activities/?subject=intelligent agents
     */
    @Test
    public void testParamSubject() {
        Response response = given()
            .queryParam("subject", "intelligent agents")
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .extract().response();

        ArrayList<ArrayList<String>> allSubjects =
            response.path("data.records.subjects");

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

    /**
     * api/activities/?description=unique biology
     */
    @Test
    public void testParamDescription() {
        final String testDescription = "unique biology";
        given().queryParam("description", '"'+testDescription+'"')
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.records.description",
                everyItem(
                    containsString(testDescription)
                )
            );
    }

    /**
     * api/activities/?institution=University of Sydney
     */
    @Test
    public void testParamInstitution() {
        final String testInstitution = "University of Sydney";
        given().queryParam("institution", testInstitution)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.records.institutions",
                everyItem(
                    everyItem(
                        containsString(testInstitution)
                    )
                )
            );
    }

    /**
     * api/activities/?funder="Australian Research Council"
     */
    @Test
    public void testParamFunder() {
        final String testFunder = "Australian Research Council";
        Response response = given().queryParam("funder", '"'+testFunder+'"')
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .extract().response();

        ArrayList<ArrayList<String>> allItems =
            response.path("data.records.funder");

        for (ArrayList<String> items : allItems) {
            Assert.assertThat(
                items.toString().toLowerCase(),
                containsString(testFunder.toLowerCase())
            );
        }
    }

    /**
     * api/activities/?principalInvestigator=Jacob George
     */
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
            response.path("data.records.principalInvestigator");

        for (ArrayList<String> items : allItems) {
            Assert.assertThat(
                items.toString(),
                containsString(testPrincipalInvestigator)
            );
        }
    }

    /**
     * api/activities/?researcher=Jacob George
     */
    @Test
    public void testParamResearcher() {
        final String testResearcher = "Jacob George";
        Response response =
            given()
                .queryParam("researcher", testResearcher)
                .when().get(props.getProperty("grant_api_url"))
                .then().statusCode(200)
                .extract().response();

        ArrayList<ArrayList<String>> allItems =
            response.path("data.records.researchers");

        for (ArrayList<String> items : allItems) {
            Assert.assertThat(
                items.toString().toLowerCase(),
                anyOf(
                    containsString("george"),
                    containsString("jacob")
                )
            );
        }

        //make sure the first one matches Jacob George perfectly
        Assert.assertThat(
            allItems.get(0).toString(),
            containsString(testResearcher)
        );
    }

    /**
     * api/activities/?fundingScheme="NHMRC Project Grants"
     */
    @Test
    public void testParamFundingScheme() {
        final String testFundingScheme = "NHMRC Project Grants";
        given()
            .queryParam("fundingScheme", "\"" + testFundingScheme + "\"")
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.records.fundingScheme",
                everyItem(
                    containsString(testFundingScheme)
                )
            );
    }

    /**
     * api/activities/?addedSince=2015-11-28T13:15:30Z
     * @throws ParseException
     */
    @Test
    public void testParamAddedSince() throws ParseException {
        final String testAddedSince = "2015-11-28T13:15:30Z";
        Response response = given()
            .queryParam("addedSince", testAddedSince)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200).extract().response();

        DateFormat format =new SimpleDateFormat("YYYY-MM-DD", Locale.ENGLISH);
        DateFormat returnFormat = new SimpleDateFormat("Y", Locale.ENGLISH);

        Date start = format.parse(testAddedSince);

        ArrayList<String> allCreatedWhen =
            response.path("data.records.dateTimeCreated");

        // assert that the return date is after the start date
        for (String date : allCreatedWhen) {
            Date compare = returnFormat.parse(date);
            Assert.assertTrue(compare.compareTo(start) >= 0);
        }

    }

    /**
     * api/activities/?modifiedSince=2015-11-28T13:15:30Z
     * @throws ParseException
     */
    @Test
    public void testParamModifiedSince() throws ParseException {
        final String testModifiedSince = "2015-11-28T13:15:30Z";
        Response response = given()
            .queryParam("modifiedSince", testModifiedSince)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200).extract().response();

        DateFormat format = new SimpleDateFormat("YYYY-MM-DD", Locale.ENGLISH);
        DateFormat returnFormat = new SimpleDateFormat("Y", Locale.ENGLISH);

        Date start = format.parse(testModifiedSince);

        ArrayList<String> allModifiedWhen =
            response.path("data.records.dateTimeModified");

        // assert that the return date is after the start date
        for (String date : allModifiedWhen) {
            Date compare = returnFormat.parse(date);
            Assert.assertTrue(compare.compareTo(start) >= 0);
        }
    }

    /**
     * api/activities/
     * ?description=unique
     * &type=grant
     * &title=caves climate
     * &subject=Earth Sciences
     * &funder=Australian Research Council
     * &flags=titles
     */
    @Test
    public void testCombination() {
        Response response = given()
            .queryParam("description", "unique")
            .queryParam("type", "grant")
            .queryParam("title", "caves climate")
            .queryParam("subject", "Earth Sciences")
            .queryParam("funder", "Australian Research Council")
            .queryParam("flags", "titles")
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
                .body("data.records.description", everyItem(containsString("unique")))
                .body("data.records.type", everyItem(equalTo("grant")))
            .extract().response()
        ;

        // funders
        ArrayList<ArrayList<String>> allFunders =
            response.path("data.records.funder");

        for (ArrayList<String> funders : allFunders) {
            Assert.assertThat(
                funders.toString(),
                containsString("Australian Research Council")
            );
        }

        // subjects
        ArrayList<ArrayList<String>> allSubjects =
            response.path("data.records.subjects");

        for (ArrayList<String> subjects : allSubjects) {
            Assert.assertThat(
                subjects.toString().toLowerCase(),
                anyOf(
                    containsString("earth"),
                    containsString("science")
                )
            );
        }

        // titles
        ArrayList<ArrayList<String>> allTitles =
            response.path("data.records.titles");

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


    /**
     * activities/?identifier=chorizo risotto
     * @throws Exception
     */
    @Test
    public void testChorizoRisottoIdentifier() throws Exception {
        final String testIdentifier = "chorizo risotto";
        Response response = given().queryParam("identifier", testIdentifier)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.numFound", greaterThanOrEqualTo(1))
            .extract().response();

        // identifiers
        ArrayList<ArrayList<String>> allIdentifiers =
            response.path("data.records.identifiers");

        for (ArrayList<String> identifiers : allIdentifiers) {
            Assert.assertThat(
                identifiers.toString().toLowerCase(),
                anyOf(
                    containsString("chorizo"),
                    containsString("risotto")
                )
            );
        }
    }

    /**
     * activities/?q="chorizo risotto"
     * @throws Exception
     */
    @Test
    public void testExactQParamMatching() throws Exception {

        final String q = "chorizo risotto";
        Response response = given()
            .queryParam("q", '"'+q+'"')
            .queryParam("flags", "titles")
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.numFound", greaterThanOrEqualTo(1))
            .extract().response();

        // identifiers
        List allRecords = response.path("data.records");

        for (Object record : allRecords) {
            Assert.assertThat(
                record.toString().toLowerCase(),
                containsString("chorizo risotto")
            );
        }
    }

    /**
     * activities/?identifiers=http://AUT.org/au-research/grants/arc/LP100100422AUTx.Grant
     * @throws Exception
     */
    @Test
    public void testPURLinIdentifier() throws Exception {
        given()
            .queryParam("identifier", "http://AUT.org/au-research/grants/arc/LP100100422AUTx.Grant")
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.numFound", greaterThanOrEqualTo(1))
        ;
    }

    /**
     * activities/?offset=0
     * activities/?offset=5
     * @throws Exception
     */
    @Test
    public void testOffset() throws Exception {
        Response response = given()
            .queryParam("offset", 0)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.offset", equalTo(0))
            .extract().response();

        // store pointer for searching
        String pointer = response.path("data.records[5].id");

        // make sure that by moving 5 ahead, the first one is the pointer
        given()
            .queryParam("offset", 5)
            .when().get(props.getProperty("grant_api_url"))
            .then().statusCode(200)
            .body("data.offset", equalTo(5))
            .body("data.records[0].id", equalTo(pointer))
        ;
    }
}
