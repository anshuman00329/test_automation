package api

import common_libs.CommonUtils
import connection_factories.RestAssuredUtils
import db.DbConnectionFactory
import jsonTemplate.tenderTemplate.BaseTender


class TenderApi {

    RestAssuredUtils rest
    BaseTender tender
    String URL
    def config  = new CommonUtils()
    def tender_app_config
    def tender_config
    def tender_db_config
    DbConnectionFactory db

    TenderApi()
    {
        rest = new RestAssuredUtils()
        tender = new BaseTender()
        db = new DbConnectionFactory()
    }

    def send(msg)
    {
        try {
            tender_config = config.read_properties()
            tender_app_config = tender_config['app_config']['tender']
            tender_db_config= config.add_mysql_url(tender_config['tlmdevint']['db_config']['tender'])
            URL = tender_app_config['url']+ tender_app_config['endpoint']
            URL = URL.replace('${envTag}',config.getEnv_tag())
            println ("URL = " +URL)
            def status = rest.postRequest(URL, msg, "application/json")
            println("Status =" + status)
            /*if(status.getStatusCode()!=200)
            {
                throw  new Exception("Unable to post Request to "+ URL)
            }*/
        }
        catch (Exception e)
        {
            assert false:"Exception occured ${e.printStackTrace()}"
        }

    }

    def assert_for_Tender_Status(status,shipmentid)
    {
        def sql = db.shipmentDbProperties()
        def result = sql.rows("select * from SCS_SHIPMENT where SHIPMENT_ID =${shipmentid} limit 1")
        assert result.TENDER_STATUS == [status]
    }

}

