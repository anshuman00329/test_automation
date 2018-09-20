package api

import common_libs.CommonUtils
import connection_factories.RestAssuredUtils
import db.DbConnectionFactory
import jsonTemplate.orderTemplate.BaseOrderLine
import jsonTemplate.tenderTemplate.BaseTender

class OrderApiUtil {
    RestAssuredUtils rest
    BaseTender tender
    String URL
    def config  = new CommonUtils()
    def order_app_config
    def order_config
    def order_db_config
    DbConnectionFactory db

    OrderApiUtil()
    {
        rest = new RestAssuredUtils()
        db = new DbConnectionFactory()
    }

    def update_order_Lines(index, order_line_item,order )
    {
        BaseOrderLine orderline = new BaseOrderLine(order.orgid,order.orderid)
        orderline.setOrderlineid(index+1)
        orderline.setItemid(order_line_item[index])
        return orderline
    }

    def createDistributionOrder(orderJson)
    {
        try {
            order_config = config.read_properties()
            order_app_config = order_config['app_config']['order']
            /*order_db_config= config.add_mysql_url(order_config['db_config']['order'])*/
            URL = order_app_config['create_endpoint']
            URL = URL.replace('${envTag}',config.getEnv_tag())
            println ("URL = " +URL)
            def status = rest.postRequest(URL, orderJson, "application/json","1")

            println("Status =" + status)
            if(status.getStatusCode()!=200)
            {
                throw  new Exception("Unable to post Request to "+ URL)
            }
        }catch(Exception e){
            assert false: "Exception occured ${e.printStackTrace()}"
        }
    }
}