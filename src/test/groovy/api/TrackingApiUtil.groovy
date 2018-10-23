package api

import common_libs.CommonUtils
import connection_factories.RestAssuredUtils
import jsonTemplate.shipmentTemplate.BaseShipment
import jsonTemplate.tenderTemplate.BaseAccept
import jsonTemplate.tenderTemplate.BaseReset
import jsonTemplate.tenderTemplate.BaseTender
import jsonTemplate.tenderTemplate.BaseTenderConfig
import jsonTemplate.tenderTemplate.BaseTenderRecall
import jsonTemplate.tenderTemplate.BaseTenderReject
import jsonTemplate.trackingTemplate.BaseTrackingCreate
import org.testng.Assert
import org.testng.annotations.BeforeSuite
import org.testng.annotations.Test

class TrackingApiUtil {

    BaseShipment shipment
    def shipmentUtil
    def orgId
    def commonUtil
    BaseTender baseTender
    TenderApiUtil tenderApi
    BaseTrackingCreate baseTrackingCreate
    BaseAccept baseAccept
    BaseTenderReject baseReject
    BaseTenderRecall baseRecall
    BaseReset baseReset
    BaseTenderConfig baseTenderConfig
    def tenderJson, tenderAcceptJson, shipmentJson, tenderRejectJson, tenderRecallJson, tenderResetJson, tenderConfigJson, trackingJson
    def stop_facilities = ['FAC1', 'FAC2']
    def stop_actions = ['PU', 'DL']
    def orders = ['HAR_ORDER_41']
    def tenderautoaccept = 'false'
    RestAssuredUtils restAssuredUtils;
    String getTenderUrl
    def tenderStatus
    def response

    TrackingApiUtil() {
        restAssuredUtils = new RestAssuredUtils()
        shipment = new BaseShipment()
        shipmentUtil = new ShipmentApiUtil()
        tenderApi = new TenderApiUtil()
        baseTender = new BaseTender()
        commonUtil = new CommonUtils()
        baseTrackingCreate = new BaseTrackingCreate();
        // minimum_days_from_now  = 2
        baseAccept = new BaseAccept()
        baseReject = new BaseTenderReject()
        baseRecall = new BaseTenderRecall()
        baseReset = new BaseReset()
        baseTenderConfig = new BaseTenderConfig()
        orgId = '1'
    }
    //Create Shipment Json
    @Test(description = "create")
    public void createshipment(def shipmentId, def carrierId) {
        //   preSuite();
        shipment.setOrgid(orgId)
        shipment.setShipmentid(shipmentId)
        shipment.setAssignedcarrier(carrierId)
        shipment.shipmentstops = stop_facilities.collect {
            shipmentUtil.update_facilities_and_stops_on_tlm_shipment(stop_facilities.indexOf(it), shipment, 2, stop_facilities, stop_actions)
        }
        shipment.shipmentordermovements = orders.collect {
            shipmentUtil.update_order_movement(orders.indexOf(it), shipment, orders)
        }
        shipmentJson = shipment.buildShipmentjsonOrderMovement()
        println("Shipment Json =" + shipmentJson)
        println(commonUtil.getUrl("shipment", "create_endpoint"))

        response = restAssuredUtils.postRequest(commonUtil.getUrl("shipment", "create_endpoint"), shipmentJson)
        commonUtil.assertStatusCode(response)
    }

    //Create Tender Json

    void createTender(def shipmentId) {
        baseTender.setShipmentid(shipmentId)
        tenderJson = baseTender.buildjson()
        println("Tender Json =" + tenderJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "endpoint"), tenderJson)
        commonUtil.assertStatusCode(response)
    }

    //Create Tender Accept Json
    @Test(description = "Accept tender")
    void acceptTender(def shipmentId, def carrierId) {
        baseAccept.setShipmentid(shipmentId)
        baseAccept.setCarrierid(carrierId)
        tenderAcceptJson = baseAccept.buildjson()
        println("Accept Json =" + tenderAcceptJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "acceptendpoint"), tenderAcceptJson)
        commonUtil.assertStatusCode(response)
    }

    //create tracking message
    @Test(description = "Create Tracking message")
    void createtracking(def shipmentId, def carrierId, def trackingId) {
        baseTrackingCreate.setShipmentId(shipmentId)
        baseTrackingCreate.setCarrierId(carrierId)
        baseTrackingCreate.setTrackingId(trackingId)
        trackingJson = baseTrackingCreate.buildjson()
        println("Tracking Json = " + trackingJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tracking", "endpoint"), trackingJson)
        commonUtil.assertStatusCode(response)
    }

}



