package tests

import api.TenderApiUtil
import api.TrackingApiUtil
import common_libs.CommonUtils
import connection_factories.RestAssuredUtils
import jsonTemplate.shipmentTemplate.BaseShipment
import jsonTemplate.tenderTemplate.BaseTender
import jsonTemplate.trackingTemplate.BaseTrackingCreate
import org.testng.Assert
import org.testng.annotations.BeforeSuite
import org.testng.annotations.Test

class TrackingApi {

    BaseShipment shipment
    def shipmentUtil
    def orgId

    BaseTender baseTender
    TenderApiUtil tenderApi
    TrackingApiUtil trackingApi
    BaseTrackingCreate baseTrackingCreate
    def response
    RestAssuredUtils restAssuredUtils;
    CommonUtils commonUtil

    TrackingApi() {
        trackingApi = new TrackingApiUtil()
        commonUtil = new CommonUtils()
        restAssuredUtils = new RestAssuredUtils()
        baseTrackingCreate = new BaseTrackingCreate();

    }

    @BeforeSuite(enabled = false)
    public preSuite() {
        RestAssuredUtils.token = restAssuredUtils.tokenAuthentication()
        println("Global token is: " + RestAssuredUtils.token)
    }

    @Test(description = "Validation of MessageType:Loading,Unloading,Loaded,Unloaded,Other")

    public void valiadtionOfMessageTypeTest() {
        def shipmentId = 'ShipB' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'CARR' + CommonUtils.getFourDigitRandomNumber()
        def trackingId = 'TrackB' + CommonUtils.getFourDigitRandomNumber()
        def reasonCode = 'REASON_CODE1'


        trackingApi.createshipment(shipmentId, carrierId)
        trackingApi.createTender(shipmentId)
        trackingApi.acceptTender(shipmentId, carrierId)
        trackingApi.createtracking(shipmentId, carrierId, trackingId)

        // Setting message type as Loading ...
        println("loading")
        def trackingId_loading = 'TrackB' + CommonUtils.getFourDigitRandomNumber();
        baseTrackingCreate.setShipmentId(shipmentId)
        baseTrackingCreate.setCarrierId(carrierId)
        baseTrackingCreate.setTrackingId(trackingId_loading)
        baseTrackingCreate.setMessageType('Loading')
        String TrackingUrl
        TrackingUrl = baseTrackingCreate.buildjson()
        println("Tracking Json = " + TrackingUrl)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tracking", "endpoint"), TrackingUrl)
        commonUtil.assertStatusCode(response)
        String getTrackingUrl
        getTrackingUrl = commonUtil.getUrl("tracking", "gettrackingendpoint")
        getTrackingUrl = getTrackingUrl.replace('${trackingId}', trackingId_loading)
        println(getTrackingUrl)
        response = restAssuredUtils.getRequest(getTrackingUrl)
        commonUtil.assertStatusCode(response)
        def tracking_status
        tracking_status = response.getBody().jsonPath().get("data.MessageType")
        Assert.assertEquals(tracking_status, "Loading", "The expected qualifier is Loading but found " + tracking_status)

        //Setting MessageType as Unloading
        println("Unloading")
        def trackingId_unloading = 'TrackB' + CommonUtils.getFourDigitRandomNumber();
        baseTrackingCreate.setShipmentId(shipmentId)
        baseTrackingCreate.setCarrierId(carrierId)
        baseTrackingCreate.setTrackingId(trackingId_unloading)
        baseTrackingCreate.setMessageType('Unloading')
        TrackingUrl = baseTrackingCreate.buildjson()
        println("Tracking Json = " + TrackingUrl)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tracking", "endpoint"), TrackingUrl)
        commonUtil.assertStatusCode(response)
        getTrackingUrl = commonUtil.getUrl("tracking", "gettrackingendpoint")
        getTrackingUrl = getTrackingUrl.replace('${trackingId}', trackingId_unloading)
        println(getTrackingUrl)
        response = restAssuredUtils.getRequest(getTrackingUrl)
        commonUtil.assertStatusCode(response)
        tracking_status = response.getBody().jsonPath().get("data.MessageType")
        Assert.assertEquals(tracking_status, "Unloading", "The expected qualifier is Unloading but found " + tracking_status)

        //Setting MessageType as Loaded
        println("Loaded")
        def trackingId_loaded = 'TrackB' + CommonUtils.getFourDigitRandomNumber();
        baseTrackingCreate.setShipmentId(shipmentId)
        baseTrackingCreate.setCarrierId(carrierId)
        baseTrackingCreate.setTrackingId(trackingId_loaded)
        baseTrackingCreate.setMessageType('Loaded')
        TrackingUrl = baseTrackingCreate.buildjson()
        println("Tracking Json = " + TrackingUrl)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tracking", "endpoint"), TrackingUrl)
        commonUtil.assertStatusCode(response)
        getTrackingUrl = commonUtil.getUrl("tracking", "gettrackingendpoint")
        getTrackingUrl = getTrackingUrl.replace('${trackingId}', trackingId_loaded)
        println(getTrackingUrl)
        response = restAssuredUtils.getRequest(getTrackingUrl)
        commonUtil.assertStatusCode(response)
        tracking_status = response.getBody().jsonPath().get("data.MessageType")
        Assert.assertEquals(tracking_status, "Loaded", "The expected qualifier is Loaded but found " + tracking_status)

        //Setting MessageType as Unloaded
        println("Unloaded")
        def trackingId_unloaded = 'TrackB' + CommonUtils.getFourDigitRandomNumber();
        baseTrackingCreate.setShipmentId(shipmentId)
        baseTrackingCreate.setCarrierId(carrierId)
        baseTrackingCreate.setTrackingId(trackingId_unloaded)
        baseTrackingCreate.setMessageType('Unloaded')
        TrackingUrl = baseTrackingCreate.buildjson()
        println("Tracking Json = " + TrackingUrl)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tracking", "endpoint"), TrackingUrl)
        commonUtil.assertStatusCode(response)
        getTrackingUrl = commonUtil.getUrl("tracking", "gettrackingendpoint")
        getTrackingUrl = getTrackingUrl.replace('${trackingId}', trackingId_unloaded)
        println(getTrackingUrl)
        response = restAssuredUtils.getRequest(getTrackingUrl)
        commonUtil.assertStatusCode(response)
        tracking_status = response.getBody().jsonPath().get("data.MessageType")
        Assert.assertEquals(tracking_status, "Unloaded", "The expected qualifier is Unloaded but found " + tracking_status)

        //Setting MessageType as Other
        println("Other")
        def trackingId_other = 'TrackB' + CommonUtils.getFourDigitRandomNumber();
        baseTrackingCreate.setShipmentId(shipmentId)
        baseTrackingCreate.setCarrierId(carrierId)
        baseTrackingCreate.setTrackingId(trackingId_other)
        baseTrackingCreate.setMessageType('Other')
        TrackingUrl = baseTrackingCreate.buildjson()
        println("Tracking Json = " + TrackingUrl)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tracking", "endpoint"), TrackingUrl)
        commonUtil.assertStatusCode(response)
        getTrackingUrl = commonUtil.getUrl("tracking", "gettrackingendpoint")
        getTrackingUrl = getTrackingUrl.replace('${trackingId}', trackingId_other)
        println(getTrackingUrl)
        response = restAssuredUtils.getRequest(getTrackingUrl)
        commonUtil.assertStatusCode(response)
        tracking_status = response.getBody().jsonPath().get("data.MessageType")
        Assert.assertEquals(tracking_status, "Other", "The expected qualifier is Other but found " + tracking_status)


    }

}

