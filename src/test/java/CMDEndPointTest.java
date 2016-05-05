import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import org.junit.*;

import java.util.Properties;

public class CMDEndPointTest {
    private static Properties props;

    // TODO: 8/12/2015 resource for tests, sample xml kernel-2.2 kernel-3
    // TODO: 8/12/2015 xml, json and text response

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        APIProperties localProperties = new APIProperties();
        props = localProperties.getProp();

        System.out.println("Getting and verifying configuration");
        System.out.println("Testing...");

        RestAssured.useRelaxedHTTPSValidation();
    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        System.out.println("Done!");
    }

    @Test
    public void getBaseUrl() {
        when().get(props.getProperty("base_url")).then().statusCode(200);
    }

    @Test
    public void getServiceUrl() {
        when().get(props.getProperty("service_url")).then().statusCode(200);
    }

    // TODO: 8/12/2015 status.json works but wrong contentType
    @Test
    public void getServiceStatus() {
        when().get(props.getProperty("service_url") + "status.xml/")
                .then()
                .contentType(ContentType.XML)
                .statusCode(200)
                .body("response.@type", equalTo("success"))
                .body("response.responsecode", equalTo("MT090"))
        ;

        /*when().get(props.getProperty("service_url") + "status.json/")
                .then()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("response.@type", equalTo("success"))
                .body("response.responsecode", equalTo("MT090"))
        ;*/
    }

    // TODO: 8/12/2015 Update documentation service point .JSON does not work
    @Test
    public void getMetadataTest() {
        given().queryParam("doi", props.getProperty("test_doi1")).
                when().get(props.getProperty("service_url") + "xml.xml")
                .then().contentType(ContentType.XML).statusCode(200)
                .body("resource.identifier", equalTo(props.getProperty("test_doi1")))
        ;
    }

    // TODO: 8/12/2015 Update getMetadataFail for contentType.XML
    @Test
    public void getMetadataFail() {
        given().queryParam("doi", props.getProperty("test_doi1_fail")).
                when().get(props.getProperty("service_url") + "xml.xml")
                .then().statusCode(200)
//                .body("response.@type", equalTo("failure"))
//                .body("response.responsecode", equalTo("MT011"))
        ;
    }

    // TODO: 8/12/2015 MINT
    @Test
    public void postMint() {
        Response response =
                given()
                        .header("Authorization", props.getProperty("auth_header"))
                        .queryParam("url", props.getProperty("test_url1"))
                        .queryParam("app_id", props.getProperty("app_id"))
                        .param("xml", APIProperties.getFileContent("sample.xml"))
                        .body(APIProperties.getFileContent("sample.xml"))
                        .when().get(props.getProperty("service_url") + "mint.xml/")
                        .then()
                        .statusCode(200)
                        .contentType(ContentType.XML)
//                        .body("response.@type", equalTo("success"))
//                        .body("response.responsecode", equalTo("MT003"))
//                        .body("response.doi", equalTo(props.getProperty("test_doi2")))
//                        .body("response.app_id", equalTo(props.getProperty("app_id")))
                        .extract().response();
        System.out.println(response.getBody().asString());
    }

    // TODO: 8/12/2015 UPDATE URL
    @Test
    public void postUpdateUrl() {
        Response response = given()
                .header("Authorization", props.getProperty("auth_header"))
                .queryParam("url", props.getProperty("test_url2"))
                .queryParam("doi", props.getProperty("test_doi2"))
                .queryParam("app_id", props.getProperty("app_id"))
                .when().get(props.getProperty("service_url") + "update.xml/")
                .then()
                .contentType(ContentType.XML)
                .body("response.@type", equalTo("success"))
                .body("response.responsecode", equalTo("MT002"))
                .body("response.doi", equalTo(props.getProperty("test_doi2")))
                .body("response.app_id", equalTo(props.getProperty("app_id")))
                .extract().response();
        System.out.println(response.getBody().asString());
    }

    // TODO: 8/12/2015 UPDATE XML
    // TODO: 8/12/2015 UPDATE URL AND XML

    @Test
    public void getDeactivate() {
        Response response = given()
                .header("Authorization", props.getProperty("auth_header"))
                .queryParam("doi", props.getProperty("test_doi2"))
                .queryParam("app_id", props.getProperty("app_id"))
                .when().get(props.getProperty("service_url") + "deactivate.xml/")
                .then()
                .contentType(ContentType.XML)
                .body("response.@type", equalTo("success"))
                .body("response.responsecode", equalTo("MT003"))
                .body("response.doi", equalTo(props.getProperty("test_doi2")))
                .body("response.app_id", equalTo(props.getProperty("app_id")))
                .extract().response();
//        System.out.println(response.getBody().asString());

    }

    @Test
    public void getActivate() {
        Response response = given()
                .header("Authorization", props.getProperty("auth_header"))
                .queryParam("doi", props.getProperty("test_doi2"))
                .queryParam("app_id", props.getProperty("app_id"))
                .param("xml", APIProperties.getFileContent("sample.xml"))
                .when().get(props.getProperty("service_url") + "activate.xml/")
                .then()
                .statusCode(200)
                .contentType(ContentType.XML)
                .body("response.@type", equalTo("success"))
                .body("response.responsecode", equalTo("MT004"))
                .body("response.doi", equalTo(props.getProperty("test_doi2")))
                .body("response.app_id", equalTo(props.getProperty("app_id")))
                .extract().response();
//        System.out.println(response.getBody().asString());

    }
}
