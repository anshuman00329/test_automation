package tests

import api.ShipmentApiUtil
import api.TenderApiUtil
import com.jayway.restassured.response.Response
import common_libs.CommonUtils
import connection_factories.RestAssuredUtils
import jsonTemplate.shipmentTemplate.BaseShipment
import jsonTemplate.tenderTemplate.*
import org.apache.log4j.PropertyConfigurator
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

import java.util.logging.Logger

class TestTenderApi {

    BaseShipment shipment
    def shipmentUtil, commonUtil
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
    RestAssuredUtils restAssuredUtils
    static Logger logger=Logger.getLogger(this.getClass().getName());

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

    @BeforeTest
    public void loggerConfiguration()
    {
        String log4jConfigFile = System.getProperty("user.dir")+ File.separator + "log4j.properties";
        PropertyConfigurator.configure(log4jConfigFile);
    }

    @BeforeClass()
    public void beforeClass() {

        //Set the TenderAutoAccept=false
        baseTenderConfig.setTenderautoaccept(tenderautoaccept)
        tenderConfigJson = baseTenderConfig.buildjson()
        println("Tender Config Json =" + tenderConfigJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "configendpoint"), tenderConfigJson)
        commonUtil.assertStatusCode(response)
    }

    @Test(description = "Create a Shipment, Tender:Accept:Reject the Shipment")
    public void tenderAcceptRejectShipmentTest() {

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
        assert tenderStatus.equals("TENDERED") : "The expected qualifier is TENDERED but found " + tenderStatus

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
        assert tenderStatus.equals("ACCEPTED") : "The expected qualifier is ACCEPTED but found " + tenderStatus

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
        assert tenderStatus.equals("REJECTED") : "The expected qualifier is REJECTED but found "+ tenderStatus
    }

    @Test(description = "Create a Shipment, Tender:Accept:Recall the Shipment")
    public void tenderAcceptRecallShipmentTest() {

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
        assert tenderStatus.equals("TENDERED") : "The expected qualifier is TENDERED but found " + tenderStatus

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
        assert tenderStatus.equals("ACCEPTED") : "The expected qualifier is ACCEPTED but found " + tenderStatus

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
        assert tenderStatus.equals("RECALLED") : "The expected qualifier is RECALLED but found " + tenderStatus
    }

    @Test(description = "Create a Shipment, Tender:Accept:Reset the Shipment")
    public void tenderAcceptResetShipmentTest() {

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
        assert tenderStatus.equals("TENDERED") : "The expected qualifier is TENDERED but found " + tenderStatus

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
        assert tenderStatus.equals("ACCEPTED") : "The expected qualifier is ACCEPTED but found " + tenderStatus

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
    public void tenderResetTenderRecallTest() {

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
        assert tenderStatus.equals("TENDERED") : "The expected qualifier is TENDERED but found " + tenderStatus

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
        assert tenderStatus.equals("TENDERED") : "The expected qualifier is TENDERED but found " + tenderStatus

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
        assert tenderStatus.equals("RECALLED") : "The expected qualifier is RECALLED but found " + tenderStatus
    }

    @Test(description = "Create a Shipment, Tender:Reject the Shipment")
    public void tenderRejectTest() {

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
        assert tenderStatus.equals("TENDERED") : "The expected qualifier is TENDERED but found " + tenderStatus

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
        assert tenderStatus.equals("REJECTED") : "The expected qualifier is REJECTED but found "+ tenderStatus
    }

    @Test(description = "Try to Tender/Accept/Reject/Recall an invalid shipment and validate the error message")
    public void validationFlowsForInvalidShipmentTest() {

        def shipmentId = 'InvalidShipment_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'STARL_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def reasonCode = 'REASON_CODE1'
        String expectedErrorMessageForTenderAction = "Shipment not found for business keys: shipmentId: ${shipmentId}."
        String expectedErrorMessageForAcceptAction = "The shipment is not in an eligible status to perform this action."
        String expectedErrorMessageForRejectAction = "The shipment is not in an eligible status to perform this action."
        String expectedErrorMessageForRecallAction = "The shipment is not in an eligible status to perform this action."

        String actualErrorMessageForTenderAction, actualErrorMessageForAcceptAction, actualErrorMessageForRejectAction, actualErrorMessageForRecallAction

        //Tender Invalid shipment and validate error message
        baseTender.setShipmentid(shipmentId)
        tenderJson = baseTender.buildjson()
        println("Tender Json =" + tenderJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "endpoint"), tenderJson)
        actualErrorMessageForTenderAction = response.getBody().jsonPath().get("exceptions.message")[0]
        assert actualErrorMessageForTenderAction.equals(expectedErrorMessageForTenderAction): "actual and expected error message are not same when an invalid shipment is Tendered"

        //Accept Invalid shipment and validate error message
        baseAccept.setShipmentid(shipmentId)
        baseAccept.setCarrierid(carrierId)
        tenderAcceptJson = baseAccept.buildjson()
        println("Accept Json =" + tenderAcceptJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "acceptendpoint"), tenderAcceptJson)
        actualErrorMessageForAcceptAction = response.getBody().jsonPath().get("messages.Message.Description")[0]
        assert actualErrorMessageForAcceptAction.equals(expectedErrorMessageForAcceptAction): "actual and expected error message are not same when an invalid shipment is Tendered"

        //Reject Invalid shipment and validate error message
        baseReject.setShipmentid(shipmentId)
        baseReject.setCarrierid(carrierId)
        baseReject.setReasoncode(reasonCode)
        tenderRejectJson = baseReject.buildjson()
        println("Reject Json =" + tenderRejectJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "rejectendpoint"), tenderRejectJson)
        actualErrorMessageForRejectAction = response.getBody().jsonPath().get("messages.Message.Description")[0]
        assert actualErrorMessageForRejectAction.equals(expectedErrorMessageForRejectAction): "actual and expected error message are not same when an invalid shipment is Tendered"

        //Recall Invalid shipment and validate error message
        baseRecall.setShipmentid(shipmentId)
        baseRecall.setCarrierid(carrierId)
        baseRecall.setReasoncode(reasonCode)
        tenderRecallJson = baseRecall.buildjson()
        println("Recall Json =" + tenderRecallJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "recallendpoint"), tenderRecallJson)
        actualErrorMessageForRecallAction = response.getBody().jsonPath().get("messages.Message.Description")[0]
        assert actualErrorMessageForRecallAction.equals(expectedErrorMessageForRecallAction): "actual and expected error message are not same when an invalid shipment is Tendered"
    }

    @Test(description = "Tender already Tendered shipment and validate the response")
    public void tenderAlreadyTenderedShipmentTest() {

        def shipmentId = 'STARL_SHIPMENT_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'STARL_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def expectedErrorMessageForTenderAction = "A tender request has already been sent to the carrier"
        def actualErrorMessageForTenderAction

        //Create the Shipment
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

        //Tender the shipment
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
        assert tenderStatus.equals("TENDERED") : "The expected qualifier is TENDERED but found " + tenderStatus

        //Tender the same shipment again and validate the error message
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "endpoint"), tenderJson)
        actualErrorMessageForTenderAction = response.getBody().jsonPath().get("messages.Message.Description")[0]
        assert actualErrorMessageForTenderAction.equals(expectedErrorMessageForTenderAction): "actual and expected error message are not same when an already Tendered shipment is Tendered"
    }

    @Test(description = "Accept/Tender already Accepted shipment and validate the response")
    public void acceptAlreadyAcceptedShipmentTest() {

        def shipmentId = 'STARL_SHIPMENT_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'STARL_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def expectedErrorMessageForAcceptAction = "The shipment is not in an eligible status to perform this action."
        def expectedErrorMessageForTenderAction = "The shipment has already been Accepted."
        def actualErrorMessageForAcceptAction, actualErrorMessageForTenderAction, actualErrorMessageForResetAction

        //Create the Shipment
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

        //Tender the shipment
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
        assert tenderStatus.equals("TENDERED") : "The expected qualifier is TENDERED but found " + tenderStatus

        //Accept the shipment
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
        assert tenderStatus.equals("ACCEPTED") : "The expected qualifier is ACCEPTED but found " + tenderStatus

        //Accept the same shipment again and validate the error message
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "acceptendpoint"), tenderAcceptJson)
        actualErrorMessageForAcceptAction = response.getBody().jsonPath().get("messages.Message.Description")[0]
        assert actualErrorMessageForAcceptAction.equals(expectedErrorMessageForAcceptAction): "actual and expected error message are not same when an already Accepted shipment is Accepted"

        //Tender already Accepted shipment and validate the error message
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "endpoint"), tenderJson)
        actualErrorMessageForTenderAction = response.getBody().jsonPath().get("messages.Message.Description")[0]
        assert actualErrorMessageForTenderAction.equals(expectedErrorMessageForTenderAction): "actual and expected error message are not same when an already Tendered shipment is Tendered"
    }

    @Test(description = "Accept/Reject/Recall already Rejected shipment and validate the response")
    public void rejectAlreadyRejectedShipmentTest() {

        def shipmentId = 'STARL_SHIPMENT_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'STARL_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def reasonCode = 'REASON_CODE1'
        def expectedErrorMessage = "The shipment is not in an eligible status to perform this action."
        def actualErrorMessageForAcceptAction, actualErrorMessageForRejectAction, actualErrorMessageForRecallAction

        //Create the Shipment
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

        //Tender the shipment
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
        assert tenderStatus.equals("TENDERED") : "The expected qualifier is TENDERED but found " + tenderStatus

        //Accept the shipment
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
        assert tenderStatus.equals("ACCEPTED") : "The expected qualifier is ACCEPTED but found " + tenderStatus

        //Reject the Shipment
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
        assert tenderStatus.equals("REJECTED") : "The expected qualifier is REJECTED but found "+ tenderStatus

        //Accept the same shipment again and validate the error message
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "acceptendpoint"), tenderAcceptJson)
        actualErrorMessageForAcceptAction = response.getBody().jsonPath().get("messages.Message.Description")[0]
        assert actualErrorMessageForAcceptAction.equals(expectedErrorMessage): "actual and expected error message are not same when an already Accepted shipment is Accepted"

        //Reject the same shipment again and validate the error message
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "rejectendpoint"), tenderRejectJson)
        actualErrorMessageForRejectAction = response.getBody().jsonPath().get("messages.Message.Description")[0]
        assert actualErrorMessageForRejectAction.equals(expectedErrorMessage): "actual and expected error message are not same when an already Rejected shipment is Rejected"

        //Recall Invalid shipment and validate error message
        baseRecall.setShipmentid(shipmentId)
        baseRecall.setCarrierid(carrierId)
        baseRecall.setReasoncode(reasonCode)
        tenderRecallJson = baseRecall.buildjson()
        println("Recall Json =" + tenderRecallJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "recallendpoint"), tenderRecallJson)
        actualErrorMessageForRecallAction = response.getBody().jsonPath().get("messages.Message.Description")[0]
        println("Validating error message when Invalid shipment is Recalled")
        assert actualErrorMessageForRecallAction.equals(expectedErrorMessage): "actual and expected error message are not same when an invalid shipment is Tendered"
    }

    @Test(description = "Accept/Reject/Recall already Recalled shipment and validate the response")
    public void recallAlreadyRecalledShipmentTest() {

        def shipmentId = 'STARL_SHIPMENT_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'STARL_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def reasonCode = 'REASON_CODE1'
        def expectedErrorMessage = "The shipment is not in an eligible status to perform this action."
        def actualErrorMessageForAcceptAction, actualErrorMessageForRejectAction, actualErrorMessageForRecallAction

        //Create the Shipment
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

        //Tender the shipment
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
        assert tenderStatus.equals("TENDERED") : "The expected qualifier is TENDERED but found " + tenderStatus

        //Accept the shipment
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
        assert tenderStatus.equals("ACCEPTED") : "The expected qualifier is ACCEPTED but found " + tenderStatus
        //Recall the Shipment
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
        assert tenderStatus.equals("RECALLED") : "The expected qualifier is RECALLED but found " + tenderStatus

        //Accept the same shipment again and validate the error message
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "acceptendpoint"), tenderAcceptJson)
        actualErrorMessageForAcceptAction = response.getBody().jsonPath().get("messages.Message.Description")[0]
        assert actualErrorMessageForAcceptAction.equals(expectedErrorMessage): "actual and expected error message are not same when an already Accepted shipment is Accepted"

        //Reject Invalid shipment and validate error message
        baseReject.setShipmentid(shipmentId)
        baseReject.setCarrierid(carrierId)
        baseReject.setReasoncode(reasonCode)
        tenderRejectJson = baseReject.buildjson()
        println("Reject Json =" + tenderRejectJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "rejectendpoint"), tenderRejectJson)
        actualErrorMessageForRejectAction = response.getBody().jsonPath().get("messages.Message.Description")[0]
        println("Validating error message when Invalid shipment is Rejected")
        assert actualErrorMessageForRejectAction.equals(expectedErrorMessage): "actual and expected error message are not same when an invalid shipment is Tendered"

        //Recall the same shipment again and validate the error message
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "rejectendpoint"), tenderRecallJson)
        actualErrorMessageForRecallAction = response.getBody().jsonPath().get("messages.Message.Description")[0]
        assert actualErrorMessageForRecallAction.equals(expectedErrorMessage): "actual and expected error message are not same when an already Recalled shipment is Recalled"
    }

    @Test(description = "Tender a shipment with null carrier and validate the response")
    public void tenderShipmentWithNullCarrierTest() {

        def shipmentId = 'STARL_SHIPMENT_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = null
        def expectedErrorMessage = "Carrier is not assigned to shipment, Please assign and then tender it"
        def actualErrorMessageForTenderAction

        //Create the Shipment
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

        //Tender the shipment
        baseTender.setShipmentid(shipmentId)
        tenderJson = baseTender.buildjson()
        println("Tender Json =" + tenderJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "endpoint"), tenderJson)
        actualErrorMessageForTenderAction = response.getBody().jsonPath().get("messages.Message.Description")[0]
        assert actualErrorMessageForTenderAction.equals(expectedErrorMessage): "actual and expected error message are not same when an already Recalled shipment is Recalled"
    }

    @Test(description = "")
    public void tenderAutoAcceptTest(){

        def shipmentId = 'STARL_SHIPMENT_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'STARL_CARRIER_' + CommonUtils.getFourDigitRandomNumber()

        //Set the TenderAutoAccept=true
        baseTenderConfig.setTenderautoaccept("true")
        tenderConfigJson = baseTenderConfig.buildjson()
        println("Tender Config Json =" + tenderConfigJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "configendpoint"), tenderConfigJson)
        commonUtil.assertStatusCode(response)

        //Create the Shipment
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

        //Tender the shipment
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
        println(tenderStatus)
        assert tenderStatus.equals("ACCEPTED") : "The expected qualifier is TENDERED but found " + tenderStatus

        //Set the TenderAutoAccept=false
        baseTenderConfig.setTenderautoaccept(tenderautoaccept)
        tenderConfigJson = baseTenderConfig.buildjson()
        println("Tender Config Json =" + tenderConfigJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("tender", "configendpoint"), tenderConfigJson)
        commonUtil.assertStatusCode(response)
    }
}

