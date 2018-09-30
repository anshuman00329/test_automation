package tests


import api.ShipmentApiUtil
import api.TenderApiUtil
import common_libs.CommonUtils
import connection_factories.RestAssuredUtils
import jsonTemplate.shipmentTemplate.BaseShipment
import jsonTemplate.tenderTemplate.BaseAccept
import jsonTemplate.tenderTemplate.BaseReset
import jsonTemplate.tenderTemplate.BaseTender
import jsonTemplate.tenderTemplate.BaseTenderConfig
import jsonTemplate.tenderTemplate.BaseTenderRecall
import jsonTemplate.tenderTemplate.BaseTenderReject
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeSuite
import org.testng.annotations.Test

import com.jayway.restassured.response.Response

class TestTenderApi {

    BaseShipment shipment
    def shipmentUtil
    def commonUtil
    def minimum_days_from_now
    def orgId
    BaseTender baseTender
    TenderApiUtil tenderApi
    BaseAccept baseAccept
    BaseTenderReject baseReject
    BaseTenderRecall baseRecall
    BaseReset baseReset
    BaseTenderConfig baseTenderConfig
    Response response
    def tenderJson, tenderAcceptJson, shipmentJson, tenderRejectJson, tenderRecallJson, tenderResetJson, tenderConfigJson
    def stop_facilities = ['FAC1', 'FAC2']
    def stop_actions = ['PU', 'DL']
    def orders = ['HAR_ORDER_41']
    def tenderautoaccept = 'false'
    RestAssuredUtils restAssuredUtils;

    TestTenderApi() {
        shipment = new BaseShipment()
        shipmentUtil = new ShipmentApiUtil()
        tenderApi = new TenderApiUtil()
        baseTender = new BaseTender()
        commonUtil = new CommonUtils()
        minimum_days_from_now = 2
        baseAccept = new BaseAccept()
        baseReject = new BaseTenderReject()
        baseRecall = new BaseTenderRecall()
        baseReset = new BaseReset()
        baseTenderConfig = new BaseTenderConfig()
        restAssuredUtils = new RestAssuredUtils()
        orgId = '1'
    }

    @BeforeClass()
    public void beforeClass() {
        //Set the TenderAutoAccept=fale
        baseTenderConfig.setTenderautoaccept(tenderautoaccept)
        tenderConfigJson = baseTenderConfig.buildjson()
        println("Tender Config Json =" + tenderConfigJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "configendpoint"), tenderConfigJson)
        commonUtil.assertStatusCode(response)
    }

    @Test(description = "Create a Shipment, Tender:Accept:Reject the Shipment")
    public void tenderAcceptRejectShipment() {
        def shipmentId = 'STARJ1_SHIPMENT_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'STARJ_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def reasonCode = 'REASON_CODE1'
        //Create Shipment Json
        shipment.setOrgid(orgId)
        shipment.setShipmentid(shipmentId)
        shipment.setAssignedcarrier(carrierId)
        shipment.shipmentstops = stop_facilities.collect {
            shipmentUtil.update_facilities_and_stops_on_tlm_shipment(stop_facilities.indexOf(it), shipment, minimum_days_from_now, stop_facilities, stop_actions)
        }
        shipment.shipmentordermovements = orders.collect {
            shipmentUtil.update_order_movement(orders.indexOf(it), shipment, orders)
        }
        shipmentJson = shipment.buildShipmentjsonOrderMovement()
        println("Shipment Json =" + shipmentJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("shipment", "create_endpoint"), shipmentJson)
        commonUtil.assertStatusCode(response)

        //Create Tender Json
        baseTender.setShipmentid(shipmentId)
        tenderJson = baseTender.buildjson()
        println("Tender Json =" + tenderJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "endpoint"), tenderJson)
        commonUtil.assertStatusCode(response)
        String getTenderUrl = commonUtil.getUrl("tender", "gettenderendpoint")
        getTenderUrl = getTenderUrl.replace('${shipmentId}', shipmentId)
        getTenderUrl = getTenderUrl.replace('${carrierId}', carrierId)
        response = restAssuredUtils.getRequest(getTenderUrl)
        commonUtil.assertStatusCode(response)
        def tenderStatus = response.getBody().jsonPath().get("data.TenderStatus")
        Assert.assertEquals(tenderStatus, "TENDERED", "The expected qualifier is TENDERED but found " + tenderStatus)

        //Create Tender Accept Json
        baseAccept.setShipmentid(shipmentId)
        baseAccept.setCarrierid(carrierId)
        tenderAcceptJson = baseAccept.buildjson()
        println("Accept Json =" + tenderAcceptJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "acceptendpoint"), tenderAcceptJson)
        commonUtil.assertStatusCode(response)
        getTenderUrl = commonUtil.getUrl("tender", "gettenderendpoint")
        getTenderUrl = getTenderUrl.replace('${shipmentId}', shipmentId)
        getTenderUrl = getTenderUrl.replace('${carrierId}', carrierId)
        response = restAssuredUtils.getRequest(getTenderUrl)
        commonUtil.assertStatusCode(response)
        tenderStatus = response.getBody().jsonPath().get("data.TenderStatus")
        Assert.assertEquals(tenderStatus, "ACCEPTED", "The expected qualifier is ACCEPTED but found " + tenderStatus)

        //Create Tender Reject Json
        baseReject.setShipmentid(shipmentId)
        baseReject.setCarrierid(carrierId)
        baseReject.setReasoncode(reasonCode)
        tenderRejectJson = baseReject.buildjson()
        println("Reject Json =" + tenderRejectJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "rejectendpoint"), tenderRejectJson)
        commonUtil.assertStatusCode(response)
        getTenderUrl = commonUtil.getUrl("tender", "gettenderendpoint")
        getTenderUrl = getTenderUrl.replace('${shipmentId}', shipmentId)
        getTenderUrl = getTenderUrl.replace('${carrierId}', carrierId)
        response = restAssuredUtils.getRequest(getTenderUrl)
        commonUtil.assertStatusCode(response)
        tenderStatus = response.getBody().jsonPath().get("data.TenderStatus")
        Assert.assertEquals(tenderStatus, "REJECTED", "The expected qualifier is REJECTED but found " + tenderStatus)

    }

    @Test(description = "Create a Shipment, Tender:Accept:Recall the Shipment")
    public void tenderAcceptRecallShipment() {

        def shipmentId = 'STARL_SHIPMENT_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'STARL_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def reasonCode = 'REASON_CODE2'

        //Create Shipment Json
        shipment.setOrgid(orgId)
        shipment.setShipmentid(shipmentId)
        shipment.setAssignedcarrier(carrierId)
        shipment.shipmentstops = stop_facilities.collect {
            shipmentUtil.update_facilities_and_stops_on_tlm_shipment(stop_facilities.indexOf(it), shipment, minimum_days_from_now, stop_facilities, stop_actions)
        }
        shipment.shipmentordermovements = orders.collect {
            shipmentUtil.update_order_movement(orders.indexOf(it), shipment, orders)
        }
        shipmentJson = shipment.buildShipmentjsonOrderMovement()
        println("Shipment Json =" + shipmentJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("shipment", "create_endpoint"), shipmentJson)
        commonUtil.assertStatusCode(response)

        //Create Tender Json
        baseTender.setShipmentid(shipmentId)
        tenderJson = baseTender.buildjson()
        println("Tender Json =" + tenderJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "endpoint"), tenderJson)
        commonUtil.assertStatusCode(response)
        String getTenderUrl = commonUtil.getUrl("tender", "gettenderendpoint")
        getTenderUrl = getTenderUrl.replace('${shipmentId}', shipmentId)
        getTenderUrl = getTenderUrl.replace('${carrierId}', carrierId)
        response = restAssuredUtils.getRequest(getTenderUrl)
        commonUtil.assertStatusCode(response)
        def tenderStatus = response.getBody().jsonPath().get("data.TenderStatus")
        Assert.assertEquals(tenderStatus, "TENDERED", "The expected qualifier is TENDERED but found " + tenderStatus)

        //Create Tender Accept Json
        baseAccept.setShipmentid(shipmentId)
        baseAccept.setCarrierid(carrierId)
        tenderAcceptJson = baseAccept.buildjson()
        println("Accept Json =" + tenderAcceptJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "acceptendpoint"), tenderAcceptJson)
        commonUtil.assertStatusCode(response)
        getTenderUrl = commonUtil.getUrl("tender", "gettenderendpoint")
        getTenderUrl = getTenderUrl.replace('${shipmentId}', shipmentId)
        getTenderUrl = getTenderUrl.replace('${carrierId}', carrierId)
        response = restAssuredUtils.getRequest(getTenderUrl)
        commonUtil.assertStatusCode(response)
        tenderStatus = response.getBody().jsonPath().get("data.TenderStatus")
        Assert.assertEquals(tenderStatus, "ACCEPTED", "The expected qualifier is ACCEPTED but found " + tenderStatus)

        //Create Tender Recall Json
        baseRecall.setShipmentid(shipmentId)
        baseRecall.setCarrierid(carrierId)
        baseRecall.setReasoncode(reasonCode)
        tenderRecallJson = baseRecall.buildjson()
        println("Recall Json =" + tenderRecallJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "recallendpoint"), tenderRecallJson)
        commonUtil.assertStatusCode(response)
        getTenderUrl = commonUtil.getUrl("tender", "gettenderendpoint")
        getTenderUrl = getTenderUrl.replace('${shipmentId}', shipmentId)
        getTenderUrl = getTenderUrl.replace('${carrierId}', carrierId)
        response = restAssuredUtils.getRequest(getTenderUrl)
        commonUtil.assertStatusCode(response)
        tenderStatus = response.getBody().jsonPath().get("data.TenderStatus")
        Assert.assertEquals(tenderStatus, "RECALLED", "The expected qualifier is RECALLED but found " + tenderStatus)
    }

    @Test(description = "Create a Shipment, Tender:Accept:Reset the Shipment")
    public void tenderAcceptResetShipment() {

        def shipmentId = 'START_SHIPMENT_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'START_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def reasonCode = 'REASON_CODE3'

        //Create Shipment Json
        shipment.setOrgid(orgId)
        shipment.setShipmentid(shipmentId)
        shipment.setAssignedcarrier(carrierId)
        shipment.shipmentstops = stop_facilities.collect {
            shipmentUtil.update_facilities_and_stops_on_tlm_shipment(stop_facilities.indexOf(it), shipment, minimum_days_from_now, stop_facilities, stop_actions)
        }
        shipment.shipmentordermovements = orders.collect {
            shipmentUtil.update_order_movement(orders.indexOf(it), shipment, orders)
        }
        shipmentJson = shipment.buildShipmentjsonOrderMovement()
        println("Shipment Json =" + shipmentJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("shipment", "create_endpoint"), shipmentJson)
        commonUtil.assertStatusCode(response)

        //Create Tender Json
        baseTender.setShipmentid(shipmentId)
        tenderJson = baseTender.buildjson()
        println("Tender Json =" + tenderJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "endpoint"), tenderJson)
        commonUtil.assertStatusCode(response)
        String getTenderUrl = commonUtil.getUrl("tender", "gettenderendpoint")
        getTenderUrl = getTenderUrl.replace('${shipmentId}', shipmentId)
        getTenderUrl = getTenderUrl.replace('${carrierId}', carrierId)
        response = restAssuredUtils.getRequest(getTenderUrl)
        commonUtil.assertStatusCode(response)
        def tenderStatus = response.getBody().jsonPath().get("data.TenderStatus")
        Assert.assertEquals(tenderStatus, "TENDERED", "The expected qualifier is TENDERED but found " + tenderStatus)

        //Create Tender Accept Json
        baseAccept.setShipmentid(shipmentId)
        baseAccept.setCarrierid(carrierId)
        tenderAcceptJson = baseAccept.buildjson()
        println("Accept Json =" + tenderAcceptJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "acceptendpoint"), tenderAcceptJson)
        commonUtil.assertStatusCode(response)
        getTenderUrl = commonUtil.getUrl("tender", "gettenderendpoint")
        getTenderUrl = getTenderUrl.replace('${shipmentId}', shipmentId)
        getTenderUrl = getTenderUrl.replace('${carrierId}', carrierId)
        response = restAssuredUtils.getRequest(getTenderUrl)
        commonUtil.assertStatusCode(response)
        tenderStatus = response.getBody().jsonPath().get("data.TenderStatus")
        Assert.assertEquals(tenderStatus, "ACCEPTED", "The expected qualifier is ACCEPTED but found " + tenderStatus)

        //Create Tender Reset Json
        baseReset.setShipmentid(shipmentId)
        baseReset.setReasoncode(reasonCode)
        tenderResetJson = baseReset.buildjson()
        println("Reset Json =" + tenderResetJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "resetendpoint"), tenderResetJson)
        commonUtil.assertStatusCode(response)
        getTenderUrl = commonUtil.getUrl("tender", "gettenderendpoint")
        getTenderUrl = getTenderUrl.replace('${shipmentId}', shipmentId)
        getTenderUrl = getTenderUrl.replace('${carrierId}', carrierId)
        response = restAssuredUtils.getRequest(getTenderUrl)
        commonUtil.assertStatusCode(response)
        tenderStatus = response.getBody().jsonPath().get("data.TenderStatus")
        Assert.assertEquals(tenderStatus, "RESET", "The expected qualifier is RESET but found " + tenderStatus)
    }

    @Test(description = "Create a Shipment, Tender:Reset:Tender:Recall the Shipment")
    public void tenderResetTenderRecall() {
        def shipmentId = 'STRTTRL_SHIPMENT_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'STRTTRL_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def reasonCode = 'REASON_CODE4'

        //Create Shipment Json
        shipment.setOrgid(orgId)
        shipment.setShipmentid(shipmentId)
        shipment.setAssignedcarrier(carrierId)
        shipment.shipmentstops = stop_facilities.collect {
            shipmentUtil.update_facilities_and_stops_on_tlm_shipment(stop_facilities.indexOf(it), shipment, minimum_days_from_now, stop_facilities, stop_actions)
        }
        shipment.shipmentordermovements = orders.collect {
            shipmentUtil.update_order_movement(orders.indexOf(it), shipment, orders)
        }
        shipmentJson = shipment.buildShipmentjsonOrderMovement()
        println("Shipment Json =" + shipmentJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("shipment", "create_endpoint"), shipmentJson)
        commonUtil.assertStatusCode(response)

        //Create Tender Json
        baseTender.setShipmentid(shipmentId)
        tenderJson = baseTender.buildjson()
        println("Tender Json =" + tenderJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "endpoint"), tenderJson)
        commonUtil.assertStatusCode(response)
        String getTenderUrl = commonUtil.getUrl("tender", "gettenderendpoint")
        getTenderUrl = getTenderUrl.replace('${shipmentId}', shipmentId)
        getTenderUrl = getTenderUrl.replace('${carrierId}', carrierId)
        response = restAssuredUtils.getRequest(getTenderUrl)
        commonUtil.assertStatusCode(response)
        def tenderStatus = response.getBody().jsonPath().get("data.TenderStatus")
        Assert.assertEquals(tenderStatus, "TENDERED", "The expected qualifier is TENDERED but found " + tenderStatus)

        //Create Tender Reset Json
        baseReset.setShipmentid(shipmentId)
        baseReset.setReasoncode(reasonCode)
        tenderResetJson = baseReset.buildjson()
        println("Reset Json =" + tenderResetJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "resetendpoint"), tenderResetJson)
        commonUtil.assertStatusCode(response)
        getTenderUrl = commonUtil.getUrl("tender", "gettenderendpoint")
        getTenderUrl = getTenderUrl.replace('${shipmentId}', shipmentId)
        getTenderUrl = getTenderUrl.replace('${carrierId}', carrierId)
        response = restAssuredUtils.getRequest(getTenderUrl)
        commonUtil.assertStatusCode(response)
        tenderStatus = response.getBody().jsonPath().get("data.TenderStatus")
        Assert.assertEquals(tenderStatus, "RESET", "The expected qualifier is RESET but found " + tenderStatus)

        //Create Tender Json
        baseTender.setShipmentid(shipmentId)
        tenderJson = baseTender.buildjson()
        println("Tender Json =" + tenderJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "endpoint"), tenderJson)
        commonUtil.assertStatusCode(response)
        getTenderUrl = commonUtil.getUrl("tender", "gettenderendpoint")
        getTenderUrl = getTenderUrl.replace('${shipmentId}', shipmentId)
        getTenderUrl = getTenderUrl.replace('${carrierId}', carrierId)
        response = restAssuredUtils.getRequest(getTenderUrl)
        commonUtil.assertStatusCode(response)
        tenderStatus = response.getBody().jsonPath().get("data.TenderStatus")
        Assert.assertEquals(tenderStatus, "TENDERED", "The expected qualifier is TENDERED but found " + tenderStatus)

        //Create Tender Recall Json
        baseRecall.setShipmentid(shipmentId)
        baseRecall.setCarrierid(carrierId)
        baseRecall.setReasoncode(reasonCode)
        tenderRecallJson = baseRecall.buildjson()
        println("Recall Json =" + tenderRecallJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "recallendpoint"), tenderRecallJson)
        commonUtil.assertStatusCode(response)
        getTenderUrl = commonUtil.getUrl("tender", "gettenderendpoint")
        getTenderUrl = getTenderUrl.replace('${shipmentId}', shipmentId)
        getTenderUrl = getTenderUrl.replace('${carrierId}', carrierId)
        response = restAssuredUtils.getRequest(getTenderUrl)
        commonUtil.assertStatusCode(response)
        tenderStatus = response.getBody().jsonPath().get("data.TenderStatus")
        Assert.assertEquals(tenderStatus, "RECALLED", "The expected qualifier is RECALLED but found " + tenderStatus)

    }

    @Test(description = "Create a Shipment, Tender:Reject the Shipment")
    public void tenderReject() {
        def shipmentId = 'STRJ_SHIPMENT_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'STRJ_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def reasonCode = 'REASON_CODE5'

        //Create Shipment Json
        shipment.setOrgid(orgId)
        shipment.setShipmentid(shipmentId)
        shipment.setAssignedcarrier(carrierId)
        shipment.shipmentstops = stop_facilities.collect {
            shipmentUtil.update_facilities_and_stops_on_tlm_shipment(stop_facilities.indexOf(it), shipment, minimum_days_from_now, stop_facilities, stop_actions)
        }
        shipment.shipmentordermovements = orders.collect {
            shipmentUtil.update_order_movement(orders.indexOf(it), shipment, orders)
        }
        shipmentJson = shipment.buildShipmentjsonOrderMovement()
        println("Shipment Json =" + shipmentJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("shipment", "create_endpoint"), shipmentJson)
        commonUtil.assertStatusCode(response)

        //Create Tender Json
        baseTender.setShipmentid(shipmentId)
        tenderJson = baseTender.buildjson()
        println("Tender Json =" + tenderJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "endpoint"), tenderJson)
        commonUtil.assertStatusCode(response)
        String getTenderUrl = commonUtil.getUrl("tender", "gettenderendpoint")
        getTenderUrl = getTenderUrl.replace('${shipmentId}', shipmentId)
        getTenderUrl = getTenderUrl.replace('${carrierId}', carrierId)
        response = restAssuredUtils.getRequest(getTenderUrl)
        commonUtil.assertStatusCode(response)
        def tenderStatus = response.getBody().jsonPath().get("data.TenderStatus")
        Assert.assertEquals(tenderStatus, "TENDERED", "The expected qualifier is TENDERED but found " + tenderStatus)

        //Create Tender Reject Json
        baseReject.setShipmentid(shipmentId)
        baseReject.setCarrierid(carrierId)
        baseReject.setReasoncode(reasonCode)
        tenderRejectJson = baseReject.buildjson()
        println("Reject Json =" + tenderRejectJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "rejectendpoint"), tenderRejectJson)
        commonUtil.assertStatusCode(response)
        getTenderUrl = commonUtil.getUrl("tender", "gettenderendpoint")
        getTenderUrl = getTenderUrl.replace('${shipmentId}', shipmentId)
        getTenderUrl = getTenderUrl.replace('${carrierId}', carrierId)
        response = restAssuredUtils.getRequest(getTenderUrl)
        commonUtil.assertStatusCode(response)
        tenderStatus = response.getBody().jsonPath().get("data.TenderStatus")
        Assert.assertEquals(tenderStatus, "REJECTED", "The expected qualifier is REJECTED but found " + tenderStatus)
    }

    @Test(description = "Try to Tender/Accept/Reject and invalid shipment and validate the error message")
    public void validationFlowsForInvalidShipmentTest() {

        Response response
        def shipmentId = 'InvalidShipment_' + CommonUtils.getFourDigitRandomNumber()

        String expectedErrorMessageForTenderAction = "Shipment not found for business keys: shipmentId: ${shipmentId}."
        String expectedErrorMessageForAcceptAction = "The shipment is not in an eligible status to perform this action."
        String expectedErrorMessageForRejectAction = ""
        String expectedErrorMessageForRecallAction = ""

        String actualErrorMessageForTenderAction, actualErrorMessageForAcceptAction, actualErrorMessageForRejectAction, actualErrorMessageForRecallAction

        //Create Tender Json
        baseTender.setShipmentid(shipmentId)
        tenderJson = baseTender.buildjson()
        println("Tender Json =" + tenderJson)

        //Tender Invalid shipment and validate error message
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "endpoint"), tenderJson)
        actualErrorMessageForTenderAction = response.getBody().jsonPath().get("exceptions.message")[0]
        println("Validating error message when Invalid shipment is Tendered")
        assert expectedErrorMessageForTenderAction.equals(actualErrorMessageForTenderAction): "actual and expected error message are not same when an invalid shipment is Tendered"
        println("Error message is successfully validated")

        //Accept Invalid shipment and validate error message
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "acceptendpoint"), tenderJson)
        actualErrorMessageForAcceptAction = response.getBody().jsonPath().get("messages.Message.Description")[0]
        println("Validating error message when Invalid shipment is Accepted")
        assert actualErrorMessageForAcceptAction.equals(actualErrorMessageForAcceptAction): "actual and expected error message are not same when an invalid shipment is Tendered"
        println("Error message is successfully validated")

        //Reject Invalid shipment and validate error message
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "rejectendpoint"), tenderJson)
        actualErrorMessageForRejectAction = response.getBody().jsonPath().get("messages.Message.Description")[0]
        println("Validating error message when Invalid shipment is Rejected")
        assert actualErrorMessageForRejectAction.equals(actualErrorMessageForRejectAction): "actual and expected error message are not same when an invalid shipment is Tendered"
        println("Error message is successfully validated")

        //Recall Invalid shipment and validate error message
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "recallendpoint"), tenderJson)
        actualErrorMessageForRecallAction = response.getBody().jsonPath().get("messages.Message.Description")[0]
        println("Validating error message when Invalid shipment is Recalled")
        assert actualErrorMessageForRecallAction.equals(actualErrorMessageForRecallAction): "actual and expected error message are not same when an invalid shipment is Tendered"
        println("Error message is successfully validated")
    }

    @Test(description = "Tender already tendered shipment and validate the response")
    public void tenderAlreadyTenderedShipmentTest() {

        Response response
        def shipmentId = 'STARL_SHIPMENT_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'STARL_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def expectedErrorMessageForTenderAction = "A tender request has already been sent to the carrier"
        def actualErrorMessageForTenderAction

        //Create Shipment Json
        shipment.setOrgid(orgId)
        shipment.setShipmentid(shipmentId)
        shipment.setAssignedcarrier(carrierId)
        shipment.shipmentstops = stop_facilities.collect {
            shipmentUtil.update_facilities_and_stops_on_tlm_shipment(stop_facilities.indexOf(it), shipment, minimum_days_from_now, stop_facilities, stop_actions)
        }
        shipment.shipmentordermovements = orders.collect {
            shipmentUtil.update_order_movement(orders.indexOf(it), shipment, orders)
        }
        shipmentJson = shipment.buildShipmentjsonOrderMovement()
        println("Shipment Json =" + shipmentJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("shipment", "create_endpoint"), shipmentJson)
        commonUtil.assertStatusCode(response)

        //Create Tender Json
        baseTender.setShipmentid(shipmentId)
        tenderJson = baseTender.buildjson()
        println("Tender Json =" + tenderJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "endpoint"), tenderJson)
        commonUtil.assertStatusCode(response)
        String getTenderUrl = commonUtil.getUrl("tender", "gettenderendpoint")
        getTenderUrl = getTenderUrl.replace('${shipmentId}', shipmentId)
        getTenderUrl = getTenderUrl.replace('${carrierId}', carrierId)
        response = restAssuredUtils.getRequest(getTenderUrl)
        commonUtil.assertStatusCode(response)
        def tenderStatus = response.getBody().jsonPath().get("data.TenderStatus")
        Assert.assertEquals(tenderStatus, "TENDERED", "The expected qualifier is TENDERED but found " + tenderStatus)

        //Tender the same shipment again
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "endpoint"), tenderJson)
        actualErrorMessageForTenderAction = response.getBody().jsonPath().get("messages.Message.Description")[0]
        assert actualErrorMessageForTenderAction.equals(expectedErrorMessageForTenderAction): "actual and expected error message are not same when an already Tendered shipment is Tendered"
    }
}

