package connection_factories


import com.jayway.restassured.RestAssured
import com.jayway.restassured.config.SSLConfig
import com.jayway.restassured.response.Response

class RestAssuredUtils {

    Response response

   public def postRequest(String endURL, Object jsonObj, String contentType) throws UnknownHostException {
        try{

                 return RestAssured.given()
                         .config(RestAssured.config().sslConfig(new SSLConfig().allowAllHostnames()))
                         .header("Authorization", "Bearer " +tokenAuthentication())
                         .header("Organization","1")
                         .header("Location","01")
                         .body(jsonObj)
                         .relaxedHTTPSValidation("TLS")
                         .when()
                         .contentType(contentType)
                         .post(endURL)
        }
        catch (UnknownHostException e){
            assert false :"Please check the URL"
        }
    }

    public def tokenAuthentication() {
        def token
        Map<String, String> map = new HashMap<String, String>()
        map.put("username","supplychainadmin@1")
        map.put("password","password")
        map.put("grant_type","password")
        Response response = RestAssured
                .given()
                .config(RestAssured.config().sslConfig(new SSLConfig().allowAllHostnames()))
                .header("Authorization", "Basic b21uaWNvbXBvbmVudC4xLjAuMDpiNHM4cmdUeWc1NVhZTnVu")
                .parameters(map)
                .relaxedHTTPSValidation("TLS")
                .when()
                .post "https://authserver.sc2020devint.manhdev.com/oauth/token"
        token = response.getBody().jsonPath().get("access_token")
        println("Auth token https is : " + token)
        return token
    }

}
