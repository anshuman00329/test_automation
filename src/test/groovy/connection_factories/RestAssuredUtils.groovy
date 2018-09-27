package connection_factories


import com.jayway.restassured.RestAssured
import com.jayway.restassured.config.SSLConfig
import com.jayway.restassured.response.Response
import common_libs.CommonUtils

class RestAssuredUtils {

    Response response
    CommonUtils commonUtils = new CommonUtils();

    public def postRequest(String endURL, Object jsonObj) throws UnknownHostException {
        try {
            Map<String, String> map = new HashMap<String, String>()
            map.put("Authorization", "Bearer" + tokenAuthentication())
            map.put("Organization", "1")
            map.put("Location", "01")
            return RestAssured.given()
                    .config(RestAssured.config().sslConfig(new SSLConfig().allowAllHostnames()))
                    .headers(map)
                    .body(jsonObj)
                    .relaxedHTTPSValidation("TLS")
                    .when()
                    .contentType("application/json")
                    .post(endURL)
        }
        catch (UnknownHostException e) {
            assert false: "Please check the URL"
        }
    }

    public def tokenAuthentication() {
        def token
        Map<String, String> map = new HashMap<String, String>()
        map.put("username","supplychainadmin@1")
        map.put("password","password")
        map.put("grant_type","password")
        String url = 'https://authserver.${envTag}.manhdev.com/oauth/token'
        url = url.replace('${envTag}',commonUtils.getEnv_tag())
        Response response = RestAssured
                .given()
                .config(RestAssured.config().sslConfig(new SSLConfig().allowAllHostnames()))
                .header("Authorization", "Basic b21uaWNvbXBvbmVudC4xLjAuMDpiNHM4cmdUeWc1NVhZTnVu")
                .parameters(map)
                .relaxedHTTPSValidation("TLS")
                .when()
                .post(url)
        token = response.getBody().jsonPath().get("access_token")
        println("Auth token https is : " + token)
        return token
        
    }

    public Response getRequest(String endURL, String contentType) {
        try {
            Map<String, String> map = new HashMap<String, String>()
            map.put("Authorization", "Bearer" + tokenAuthentication())
            map.put("Organization", "1")
            map.put("Location", "01")
            return RestAssured.given()
                    .config(RestAssured.config().sslConfig(new SSLConfig().allowAllHostnames()))
                    .headers(map)
                    .relaxedHTTPSValidation("TLS")
                    .when()
                    .contentType(contentType)
                    .get(endURL)
        } catch (Exception e) {
            println("Get request failed with following exception" + e.printStackTrace())
        }

    }

    public Response deleteRequest(String endURL, String contentType) {
        try {
            Map<String, String> map = new HashMap<String, String>()
            map.put("Authorization", "Bearer" + tokenAuthentication())
            map.put("Organization", "1")
            map.put("Location", "01")
            return RestAssured.given()
                    .config(RestAssured.config().sslConfig(new SSLConfig().allowAllHostnames()))
                    .headers(map)
                    .relaxedHTTPSValidation("TLS")
                    .when()
                    .contentType(contentType)
                    .delete(endURL)
        } catch (Exception e) {
            println("Delete request failed with following exception" + e.printStackTrace())
        }
    }

    public Response updateRequest(String endURL, Object jsonObj, String contentType) {
        try {
            Map<String, String> map = new HashMap<String, String>()
            map.put("Authorization", "Bearer" + tokenAuthentication())
            map.put("Organization", "1")
            map.put("Location", "01")
            return RestAssured.given()
                    .config(RestAssured.config().sslConfig(new SSLConfig().allowAllHostnames()))
                    .headers(map)
                    .body(jsonObj)
                    .relaxedHTTPSValidation("TLS")
                    .when()
                    .contentType(contentType)
                    .put(endURL)
        } catch (Exception e) {
            println("Update request failed with following exception" + e.printStackTrace())
        }
    }

}
