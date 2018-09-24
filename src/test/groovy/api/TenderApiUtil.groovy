package api

import com.jayway.restassured.response.Response
import common_libs.CommonUtils
import connection_factories.RestAssuredUtils
import db.DbConnectionFactory
import jsonTemplate.tenderTemplate.BaseTender


class TenderApiUtil {

    RestAssuredUtils rest
    BaseTender tender
    String URL
    def config = new CommonUtils()
    def dynamicendpoint
    def tender_app_config, accept_app_config, reject_app_config
    def tender_config, accept_config, reject_config
    def tender_db_config, accept_db_config, reject_db_config
    DbConnectionFactory db

    TenderApiUtil() {
        rest = new RestAssuredUtils()
        tender = new BaseTender()
        db = new DbConnectionFactory()
    }

    def tenderMsg(tenderJson, apiEndPoint) {
        Response response
        try {
            tender_config = config.read_properties()
            tender_app_config = tender_config['app_config']['tender']
            /*tender_db_config = config.add_mysql_url(tender_config['db_config']['tender'])*/
            URL = tender_app_config[apiEndPoint]
            URL = URL.replace('${envTag}', config.getEnv_tag())
            println("URL = " + URL)
            response = rest.postRequest(URL, tenderJson, "application/json")
            println("Status =" + response.getStatusCode())
            if (response.getStatusCode() != 200) {
                throw new Exception("Unable to post Request to " + URL)
            }
        }
        catch (Exception e) {
            assert false: "Exception occured ${e.printStackTrace()}"
        }
        return Response
    }

    def getTender(def shipmentId, def carrierId) {
        Response response
        try {
            tender_config = config.read_properties()
            tender_app_config = tender_config['app_config']['tender']
            /*tender_db_config = config.add_mysql_url(tender_config[config.getEnv_tag()]['db_config']['tender'])*/
            URL = tender_app_config["gettenderendpoint"]
            URL = URL.replace('${envTag}', config.getEnv_tag())
            URL = URL.replace('${shipmentId}', shipmentId)
            URL = URL.replace('${carrierId}', carrierId)
            println("URL = " + URL)
            response = rest.getRequest(URL, "application/json")
            println("Get response status =" + response.getStatusCode())
            if (response.getStatusCode() != 200) {
                throw new Exception("Unable to post Request to " + URL)
            }
        }
        catch (Exception e) {
            assert false: "Exception occured ${e.printStackTrace()}"
        }
        return response
    }

    def assert_for_Tender_Status(status, shipmentid) {
        def shipment_sql = db.shipmentDbProperties()
        def tender_sql = db.tenderDbProperties()
        def tenderResult = tender_sql.rows("select * from TND_TENDER  where SHIPMENT_ID =${shipmentid} limit 1")
        assert tenderResult.TENDER_STATUS == [status]
    }

}

