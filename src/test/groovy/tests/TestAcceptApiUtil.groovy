package tests

import api.AcceptApiUtil
import api.TenderApiUtil
import connection_factories.RestAssuredUtils
import db.DbConnectionFactory
import org.testng.annotations.Test
import jsonTemplate.tenderTemplate.BaseAccept
import jsonTemplate.tenderTemplate.BaseTender

class TestAcceptApiUtil {

    BaseAccept baseAccept
    AcceptApiUtil acceptApi
    DbConnectionFactory db
    def acceptJson
    def tenderJson
    RestAssuredUtils rest
    BaseTender baseTender
    TenderApiUtil tenderApi

    TestAcceptApiUtil()
    {
        baseAccept = new BaseAccept()
        acceptApi = new AcceptApiUtil()
        db = new DbConnectionFactory()
        baseTender = new BaseTender()
        tenderApi = new TenderApiUtil()
    }

    @Test(description = "Tender and Accept the given shipment")
    public void acceptTenderShipment()
    {
        def shipmentid='FusionAuto88912'
        baseTender.setShipmentid(shipmentid)
        tenderJson = baseTender.buildjson()
        tenderApi.send(tenderJson)
        tenderApi.assert_for_Tender_Status("NOT_TENDERED",shipmentid)
        baseAccept.setShipmentid(shipmentid)
        acceptJson = baseAccept.buildjson()
        acceptApi.send(acceptJson)
        tenderApi.assert_for_Tender_Status("ACCEPTED",shipmentid)
    }



}


