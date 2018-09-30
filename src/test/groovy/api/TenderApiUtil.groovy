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



}

