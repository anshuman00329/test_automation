package api

import common_libs.CommonUtils
import connection_factories.RestAssuredUtils
import jsonTemplate.tenderTemplate.BaseAccept

class AcceptApiUtil {

    RestAssuredUtils rest
    BaseAccept accept
    String URL
    def config  = new CommonUtils()
    def accept_app_config
    def accept_config
    def accept_db_config

    AcceptApiUtil()
    {
        rest = new RestAssuredUtils()
        accept = new BaseAccept()
    }

    def send(msg)
    {
        try {
            accept_config = config.read_properties()
            accept_app_config = accept_config['app_config']['accept']
            /*accept_db_config= config.add_mysql_url(accept_config['db_config']['accept'])*/
            URL =accept_app_config['endpoint']
            URL = URL.replace('${envTag}',config.getEnv_tag())
            def status = rest.postRequest(URL, msg, "application/json")
            if(status.getStatusCode()!=200)
            {
                throw  new Exception("Unable to post Request to "+ URL)
            }
        }
        catch (Exception e)
        {
            assert false:"Exception occured ${e.printStackTrace()}"
        }

    }

}
