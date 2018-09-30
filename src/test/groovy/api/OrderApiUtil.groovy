package api

import com.jayway.restassured.response.Response
import common_libs.CommonUtils
import connection_factories.RestAssuredUtils
import db.DbConnectionFactory
import jsonTemplate.orderTemplate.BaseOrderLine
import jsonTemplate.tenderTemplate.BaseTender

class OrderApiUtil {
    RestAssuredUtils rest
    BaseTender tender
    String URL
    def config = new CommonUtils()
    def order_app_config
    def order_config
    def order_db_config
    DbConnectionFactory db

    OrderApiUtil() {
        rest = new RestAssuredUtils()
        db = new DbConnectionFactory()
    }

    def update_order_Lines(index, order_line_item, order) {
        BaseOrderLine orderline = new BaseOrderLine(order.orderId)
        orderline.setOrderLineId(index + 1)
        orderline.setItemId(order_line_item[index])
        return orderline
    }
}