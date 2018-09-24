package connection_factories


import com.jayway.restassured.RestAssured
import com.jayway.restassured.config.SSLConfig
import com.jayway.restassured.response.Response

class RestAssuredUtils {

    Response response

    public def postRequest(String endURL, Object jsonObj, String contentType) throws UnknownHostException {
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
                    .post(endURL)
        }
        catch (UnknownHostException e) {
            assert false: "Please check the URL"
        }
    }

    public def tokenAuthentication() {
        def token
        Map<String, String> map = new HashMap<String, String>()
        map.put("username", "supplychainadmin@1")
        map.put("password", "password")
        map.put("grant_type", "password")
        Response response = RestAssured.given().header("Authorization", "Basic b21uaWNvbXBvbmVudC4xLjAuMDpiNHM4cmdUeWc1NVhZTnVu").parameters(map).when().post("https://authserver.sc2020devint.manhdev.com/oauth/token")
        //Response response = RestAssured.given().header("Authorization","Basic b21uaWNvbXBvbmVudC4xLjAuMDpiNHM4cmdUeWc1NVhZTnVu").parameter("username","systemadmin@system.com").parameter("password","password").parameter("grant_type","password").when().post("https://authserver.sc2020devint.manhdev.com/oauth/token")
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
