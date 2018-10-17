package tests

import api.OrderApiUtil
import api.ShipmentApiUtil
import api.TenderApiUtil
import com.jayway.restassured.response.Response
import common_libs.CommonUtils
import connection_factories.RestAssuredUtils
import jsonTemplate.orderTemplate.BaseOrder
import jsonTemplate.shipmentTemplate.BaseMassCreateInvolvedParties
import jsonTemplate.shipmentTemplate.BaseShipment
import jsonTemplate.shipmentTemplate.BaseShipmentPartyQualiffier
import jsonTemplate.shipmentTemplate.BaseShipmentReceivedStatus
import jsonTemplate.tenderTemplate.BaseAccept
import jsonTemplate.tenderTemplate.BaseTender
import jsonTemplate.tenderTemplate.BaseTenderRecall
import jsonTemplate.tenderTemplate.BaseTenderReject
import org.apache.log4j.PropertyConfigurator
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeSuite
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test
import jsonTemplate.orderTemplate.BaseOrder
import jsonTemplate.shipmentTemplate.BaseShipment

import java.util.logging.Logger

class TestShipmentApi {

    BaseShipment shipment
    BaseShipmentPartyQualiffier shipmentPartyQualifier
    BaseShipmentReceivedStatus baseShipmentReceivedStatus
    BaseMassCreateInvolvedParties baseMassCreateInvolvedParties
    TenderApiUtil tenderApi
    BaseOrder order
    BaseTender baseTender
    BaseAccept baseAccept
    BaseTenderReject baseReject
    BaseTenderRecall baseRecall
    RestAssuredUtils restAssuredUtils = new RestAssuredUtils();
    Response response
    def shipmentUtil
    def orderUtil
    def commonUtil
    def minimum_days_from_now
    def orgId
    def partyqualifierid
    def updateShipmentId
    def shipmentJson, shipmentNoteJson, partyQualifierJson, tenderJson, tenderAcceptJson, tenderRejectJson, orderJson, validationKey, involvedPatyJson
    static Logger logger=Logger.getLogger(this.getClass().getName());

    TestShipmentApi() {
        shipment = new BaseShipment()
        baseMassCreateInvolvedParties = new BaseMassCreateInvolvedParties()
        shipmentPartyQualifier = new BaseShipmentPartyQualiffier()
        baseShipmentReceivedStatus = new BaseShipmentReceivedStatus()
        order = new BaseOrder()
        shipmentUtil = new ShipmentApiUtil()
        orderUtil = new OrderApiUtil()
        commonUtil = new CommonUtils()
        baseTender = new BaseTender()
        baseAccept = new BaseAccept()
        baseReject = new BaseTenderReject()
        tenderApi = new TenderApiUtil()
        validationKey = commonUtil.read_properties("shipment")
        minimum_days_from_now = 2
        orgId = "1"
    }

    @BeforeSuite
    public preSuite( ){
        RestAssuredUtils.token = restAssuredUtils.tokenAuthentication()
        println("Global token is: "+RestAssuredUtils.token)
    }

    @BeforeTest
    public void loggerConfiguration()
    {
        String log4jConfigFile = System.getProperty("user.dir")+ File.separator + "log4j.properties";
        PropertyConfigurator.configure(log4jConfigFile);
    }

    @BeforeClass(enabled = true)
    public void preConfig() {
        //Create a Insurance Company Party Qualifier
        partyqualifierid = "Insurance Company"
        shipmentPartyQualifier.setPartyqualifierid(partyqualifierid)
        shipmentPartyQualifier.setQualifierdescription("Adding Party Qualifier for Automation testing.")
        partyQualifierJson = shipmentPartyQualifier.buildjson()
        println("Party Qualifier json = " + partyQualifierJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("shipment", "party_qualifier"), partyQualifierJson)
        //Create a Insurance Party Qualifier
        partyqualifierid = "Insurance"
        shipmentPartyQualifier.setPartyqualifierid(partyqualifierid)
        shipmentPartyQualifier.setQualifierdescription("Adding Party Qualifier for Automation testing update.")
        partyQualifierJson = shipmentPartyQualifier.buildjson()
        println("Party Qualifier json = " + partyQualifierJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("shipment", "party_qualifier"), partyQualifierJson)
    }

    @Test(description = "To create a 2 stop Shipment with all the entities added.")
    public void create2StopShipment() {
        def shipmentId = 'PAR_SHIPMENT_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'PAR_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def stop_facilities = ['FAC1', 'FAC2']
        def stop_actions = ['PU', 'DL']
        def orders = ['HAR_ORDER_41', 'HAR_ORDER_42']
        def involvedPatyId = 'eb1c47a-fc08-48a1-9340-f441c4ddec' + CommonUtils.getFourDigitRandomNumber()
        partyqualifierid = "Insurance Company"
        shipment.setOrgid(orgId)
        shipment.setShipmentid(shipmentId)
        shipment.setAssignedcarrier(carrierId)
        shipment.setPartyqualifierid(partyqualifierid)
        shipment.shipmentstops = stop_facilities.collect {
            shipmentUtil.update_facilities_and_stops_on_tlm_shipment(stop_facilities.indexOf(it), shipment, minimum_days_from_now, stop_facilities, stop_actions)
        }
        shipment.shipmentordermovements = orders.collect {
            shipmentUtil.update_order_movement(orders.indexOf(it), shipment, orders)
        }
        shipment.shipmentinvolvedparties = shipmentUtil.update_Involved_parties(shipment, involvedPatyId)
        shipmentJson = shipment.buildsimplejson()
        println("Shipment Json with 2 Stops =" + shipmentJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("shipment", "create_endpoint"), shipmentJson)
        commonUtil.assertStatusCode(response)
    }

    @Test(description = "Create a 4 Stop shipment, ensure check in Stop Table")
    public void createShipment() {
        def shipmentId = 'HAR_SHIPMENT_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'HAR_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def stop_facilities = ['FAC1', 'FAC2', 'FAC3', 'FAC4']
        def stop_actions = ['PU', 'DL', 'DL', 'DL']
        def orders = ['HAR_ORDER_41', 'HAR_ORDER_42', 'HAR_ORDER_43', 'HAR_ORDER_45']
        def involvedPatyId = 'eb1c47a-fc08-48a1-9340-f441c4ddec' + CommonUtils.getFourDigitRandomNumber()
        partyqualifierid = "Insurance Company"
        //Create Shipment Json
        shipment.setOrgid(orgId)
        shipment.setShipmentid(shipmentId)
        shipment.setPartyqualifierid(partyqualifierid)
        shipment.setAssignedcarrier(carrierId)
        shipment.setPartyqualifierid(partyqualifierid)
        shipment.shipmentstops = stop_facilities.collect {
            shipmentUtil.update_facilities_and_stops_on_tlm_shipment(stop_facilities.indexOf(it), shipment, minimum_days_from_now, stop_facilities, stop_actions)
        }
        shipment.shipmentordermovements = orders.collect {
            shipmentUtil.update_order_movement(orders.indexOf(it), shipment, orders)
        }
        shipment.shipmentinvolvedparties = shipmentUtil.update_Involved_parties(shipment, involvedPatyId)
        shipmentJson = shipment.buildsimplejson()
        println("Shipment Json with 4 Stops =" + shipmentJson)
        //Hit POST /api/scshipment/shipment/Save API, and validate the Shipment creation in db
        response = restAssuredUtils.postRequest(commonUtil.getUrl("shipment", "create_endpoint"), shipmentJson)
        commonUtil.assertStatusCode(response)
        String getShipmentUrl = commonUtil.getUrl("shipment", "getshipment_endpoint")
        getShipmentUrl = getShipmentUrl.replace('${shipmentId}', shipmentId)
        response = restAssuredUtils.getRequest(getShipmentUrl)
        commonUtil.assertStatusCode(response)
        Assert.assertEquals(response.getBody().jsonPath().get("data.AssignedCarrier"), carrierId, "The expected carrier " + carrierId + " is not matching with the actual " + response.getBody().jsonPath().get("data.AssignedCarrier"))
        for (int i = 0; i < stop_facilities.size(); i++) {
            String getStopUrl = commonUtil.getUrl("shipment", "getstop_endpoint")
            getStopUrl = getStopUrl.replace('${shipmentId}', shipmentId)
            getStopUrl = getStopUrl.replace('${stopSeq}', "${i + 1}")
            println("URL = " + getStopUrl)
            response = restAssuredUtils.getRequest(getStopUrl)
            commonUtil.assertStatusCode(response)
            Assert.assertEquals(response.getBody().jsonPath().get("data.StopFacilityId"), stop_facilities[i], "The expected Facility ID " + stop_facilities[i] + " is not matching with the actual " + response.getBody().jsonPath().get("data.StopFacilityId"))
            Assert.assertEquals(response.getBody().jsonPath().get("data.StopAction"), stop_actions[i], "The expected Stop action: " + stop_facilities[i] + " is not matching with the actual " + response.getBody().jsonPath().get("data.StopFacilityId"))
        }
    }

    @Test(description = "To create Shipment with Order Note")
    public void createShipmentWithNotes() {
        def shipmentId = 'HAR_SHIPMENT_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'HAR_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def stop_facilities = ['FAC1', 'FAC2', 'FAC3', 'FAC4']
        def stop_actions = ['PU', 'DL', 'DL', 'DL']
        def noteType = ['BOL', 'Shipping Label', 'PRO']
        def noteValue = ['BOL ID: BOL123', 'CNTR NUMBERS: LPN1,LPN2', 'PRO ID: PRO ID']
        def noteCode = ['BOL1', 'LABEL', 'PRO1']
        def noteVisibility = ['All', 'Internal Only', 'Carrier']
        def involvedPatyId = 'eb1c47a-fc08-48a1-9340-f441c4ddec' + CommonUtils.getFourDigitRandomNumber()
        partyqualifierid = "Insurance Company"

        //Create Shipment Json with Shipment Level Notes
        shipment.setOrgid(orgId)
        shipment.setShipmentid(shipmentId)
        shipment.setPartyqualifierid(partyqualifierid)
        shipment.setAssignedcarrier(carrierId)
        shipment.shipmentstops = stop_facilities.collect {
            shipmentUtil.update_facilities_and_stops_on_tlm_shipment(stop_facilities.indexOf(it), shipment, minimum_days_from_now, stop_facilities, stop_actions)
        }
        shipment.shipmentnotes = noteVisibility.collect {
            shipmentUtil.update_shipment_note(noteVisibility.indexOf(it), noteType, noteValue, noteCode, noteVisibility, shipment)
        }
        shipmentNoteJson = shipment.buildShipmentNotejson()
        println("Shipment Json with Shipment Level Note is =" + shipmentNoteJson)
        //Create Note against a shipment
        response = restAssuredUtils.postRequest(commonUtil.getUrl("shipment", "create_endpoint"), shipmentNoteJson)
        commonUtil.assertStatusCode(response)
        String getShipmentUrl = commonUtil.getUrl("shipment", "getshipment_endpoint")
        getShipmentUrl = getShipmentUrl.replace('${shipmentId}', shipmentId)
        response = restAssuredUtils.getRequest(getShipmentUrl)
        commonUtil.assertStatusCode(response)
        for (int i = 0; i < noteType.size(); i++) {
            Assert.assertEquals(response.getBody().jsonPath().get("data.ShipmentNote.NoteVisibility")[i], noteVisibility[i], "The expected Note Visibility " + noteVisibility[i] + " is not matching with the actual " + response.getBody().jsonPath().get("data.ShipmentNote.NoteVisibility")[i])
            Assert.assertEquals(response.getBody().jsonPath().get("data.ShipmentNote.NoteValue")[i], noteValue[i], "The expected Note Visibility " + noteValue[i] + " is not matching with the actual " + response.getBody().jsonPath().get("data.ShipmentNote.NoteValue")[i])
            Assert.assertEquals(response.getBody().jsonPath().get("data.ShipmentNote.NoteCode")[i], noteCode[i], "The expected Note Visibility " + noteCode[i] + " is not matching with the actual " + response.getBody().jsonPath().get("data.ShipmentNote.NoteCode")[i])
            Assert.assertEquals(response.getBody().jsonPath().get("data.ShipmentNote.NoteType")[i], noteType[i], "The expected Note Visibility " + noteType[i] + " is not matching with the actual " + response.getBody().jsonPath().get("data.ShipmentNote.NoteType")[i])
        }
    }

    @Test(description = "RESET functionality of shipment json. Reset both stop and Order Movement")
    public void createShipmentWithResetLogic() {
        def shipmentId = 'HAR_SHIPMENT_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'HAR_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def orders = ['HAR_ORDER_41', 'HAR_ORDER_42']
        def stop_facilities = ['FAC1', 'FAC2', 'FAC3', 'FAC4']
        def stop_actions = ['PU', 'DL', 'DL', 'DL']
        def involvedPatyId = 'eb1c47a-fc08-48a1-9340-f441c4ddec' + CommonUtils.getFourDigitRandomNumber()
        partyqualifierid = "Insurance Company"
        shipment.setOrgid(orgId)
        shipment.setShipmentid(shipmentId)
        shipment.setPartyqualifierid(partyqualifierid)
        shipment.setAssignedcarrier(carrierId)
        shipment.shipmentstops = stop_facilities.collect {
            shipmentUtil.update_facilities_and_stops_on_tlm_shipment(stop_facilities.indexOf(it), shipment, minimum_days_from_now, stop_facilities, stop_actions)
        }
        shipment.shipmentordermovements = orders.collect {
            shipmentUtil.update_order_movement(orders.indexOf(it), shipment, orders)
        }
        shipmentJson = shipment.buildShipmentjsonOrderMovement()
        println("Shipment Json with 4 Stops and 2 Order Movement=" + shipmentJson)
        //Hit POST /api/scshipment/shipment/Save API, and validate the Shipment creation in db
        response = restAssuredUtils.postRequest(commonUtil.getUrl("shipment", "create_endpoint"), shipmentJson)
        commonUtil.assertStatusCode(response)
        String getShipmentUrl = commonUtil.getUrl("shipment", "getshipment_endpoint")
        getShipmentUrl = getShipmentUrl.replace('${shipmentId}', shipmentId)
        response = restAssuredUtils.getRequest(getShipmentUrl)
        commonUtil.assertStatusCode(response)
        for (int i = 0; i < orders.size(); i++) {
            Assert.assertEquals(response.getBody().jsonPath().get("data.OrderMovement.OrderId")[i], orders[i], "The expected Note Visibility " + orders[i] + " is not matching with the actual " + response.getBody().jsonPath().get("data.OrderMovement.OrderId")[i])
        }
        //shipment RESET LOGIC FOR STOP & ORDER MOVEMENT
        def resetOrders = ['HAR_ORDER_41']
        def reset_stop_facilities = ['FAC1', 'FAC2']
        def reset_stop_actions = ['PU', 'DL']
        shipment.shipmentstops = reset_stop_facilities.collect {
            shipmentUtil.update_facilities_and_stops_on_tlm_shipment(reset_stop_facilities.indexOf(it), shipment, minimum_days_from_now, reset_stop_facilities, reset_stop_actions)
        }
        shipment.shipmentordermovements = resetOrders.collect {
            shipmentUtil.update_order_movement(resetOrders.indexOf(it), shipment, orders)
        }
        shipmentJson = shipment.buildShipmentjsonReset()
        println("Shipment Reset Json with 2 Stops and 1 order movement =" + shipmentJson)

        //Hit POST /api/scshipment/shipment/Save API, and validate the Shipment creation in db
        response = restAssuredUtils.postRequest(commonUtil.getUrl("shipment", "create_endpoint"), shipmentJson)
        commonUtil.assertStatusCode(response)
        getShipmentUrl = commonUtil.getUrl("shipment", "getshipment_endpoint")
        getShipmentUrl = getShipmentUrl.replace('${shipmentId}', shipmentId)
        response = restAssuredUtils.getRequest(getShipmentUrl)
        commonUtil.assertStatusCode(response)
        for (int i = 0; i < resetOrders.size(); i++) {
            Assert.assertEquals(response.getBody().jsonPath().get("data.OrderMovement.OrderId")[i], resetOrders[i], "The expected Note Visibility " + resetOrders[i] + " is not matching with the actual " + response.getBody().jsonPath().get("data.OrderMovement.OrderId")[i])
        }
    }

    @Test(description = "To Create a Shipment with invalid data and check the Response code.")
    public void invalidShipment() {
        def shipmentId = 'PAR_SHIPMENT_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'PAR_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def stop_facilities = ['FAC1', 'FAC2']
        def stop_actions = ['PU', 'DL']
        def orders = ['HAR_ORDER_41', 'HAR_ORDER_42']
        def involvedPatyId = '0eb1c47a-fc08-48a1-9340-f441c4ddec' + CommonUtils.getFourDigitRandomNumber()
        /*Pass Invalid Party Qualifier ID*/
        partyqualifierid = 'Dummy'
        /*Create Shipment Json*/
        shipment.setOrgid(orgId)
        shipment.setShipmentid(shipmentId)
        shipment.setAssignedcarrier(carrierId)
        shipment.setPartyqualifierid(partyqualifierid)
        shipment.shipmentstops = stop_facilities.collect {
            shipmentUtil.update_facilities_and_stops_on_tlm_shipment(stop_facilities.indexOf(it), shipment, minimum_days_from_now, stop_facilities, stop_actions)
        }
        shipment.shipmentordermovements = orders.collect {
            shipmentUtil.update_order_movement(orders.indexOf(it), shipment, orders)
        }
        shipment.shipmentinvolvedparties = shipmentUtil.update_Involved_parties(shipment, involvedPatyId)
        shipmentJson = shipment.buildsimplejson()
        println("Shipment Json with 2 Stops =" + shipmentJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("shipment", "create_endpoint"), shipmentJson)
        Assert.assertEquals(response.getStatusCode(), 400, "The response code is not 400....please check the logs for more information.")
    }

    @Test(description = "To Delete a Shipment and check the Response code.")
    public void deleteShipment() {
        def shipmentId = 'PAR_SHIPMENT_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'PAR_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def stop_facilities = ['FAC1', 'FAC2']
        def stop_actions = ['PU', 'DL']
        def orders = ['HAR_ORDER_41', 'HAR_ORDER_42']
        def involvedPatyId = '0eb1c47a-fc08-48a1-9340-f441c4ddec' + CommonUtils.getFourDigitRandomNumber()
        partyqualifierid = "Insurance Company"
        shipment.setOrgid(orgId)
        shipment.setShipmentid(shipmentId)
        shipment.setAssignedcarrier(carrierId)
        shipment.setPartyqualifierid(partyqualifierid)
        shipment.shipmentstops = stop_facilities.collect {
            shipmentUtil.update_facilities_and_stops_on_tlm_shipment(stop_facilities.indexOf(it), shipment, minimum_days_from_now, stop_facilities, stop_actions)
        }
        shipment.shipmentordermovements = orders.collect {
            shipmentUtil.update_order_movement(orders.indexOf(it), shipment, orders)
        }
        shipment.shipmentinvolvedparties = shipmentUtil.update_Involved_parties(shipment, involvedPatyId)
        shipmentJson = shipment.buildsimplejson()
        println("Shipment Json with 2 Stops =" + shipmentJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("shipment", "create_endpoint"), shipmentJson)
        commonUtil.assertStatusCode(response)
        String getShipmentUrl = commonUtil.getUrl("shipment", "getshipment_endpoint")
        getShipmentUrl = getShipmentUrl.replace('${shipmentId}', shipmentId)
        response = restAssuredUtils.getRequest(getShipmentUrl)
        commonUtil.assertStatusCode(response)
        println("PK: " + response.getBody().jsonPath().get("data.PK"))
        String deleteShipmentUrl = commonUtil.getUrl("shipment", "deleteshipment_endpoint")
        deleteShipmentUrl = deleteShipmentUrl.replace('${pk}', response.getBody().jsonPath().get("data.PK"))
        Response deleteResponse = restAssuredUtils.deleteRequest(deleteShipmentUrl)
        Assert.assertEquals(deleteResponse.getStatusCode(), 200, "The response code is not 200....please check the logs for more information.")
    }

    @Test(description = "To update the existing Shipment entity and validate the response")
    public void updateShipment() {
        def shipmentId = 'PAR_SHIPMENT_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'PAR_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def stop_facilities = ['FAC1', 'FAC2']
        def stop_actions = ['PU', 'DL']
        def orders = ['HAR_ORDER_41', 'HAR_ORDER_42']
        def involvedPatyId = '0eb1c47a-fc08-48a1-9340-f441c4ddec' + CommonUtils.getFourDigitRandomNumber()
        partyqualifierid = "Insurance Company"
        /*Create Shipment Json*/
        shipment.setOrgid(orgId)
        shipment.setShipmentid(shipmentId)
        shipment.setAssignedcarrier(carrierId)
        shipment.setPartyqualifierid(partyqualifierid)
        shipment.shipmentstops = stop_facilities.collect {
            shipmentUtil.update_facilities_and_stops_on_tlm_shipment(stop_facilities.indexOf(it), shipment, minimum_days_from_now, stop_facilities, stop_actions)
        }
        shipment.shipmentordermovements = orders.collect {
            shipmentUtil.update_order_movement(orders.indexOf(it), shipment, orders)
        }
        shipment.shipmentinvolvedparties = shipmentUtil.update_Involved_parties(shipment, involvedPatyId)
        shipmentJson = shipment.buildsimplejson()
        println("Shipment Json with 2 Stops =" + shipmentJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("shipment", "create_endpoint"), shipmentJson)
        commonUtil.assertStatusCode(response)
        /*Update Party Qualifier to update the Shipment*/
        partyqualifierid = "Insurance"
        shipment.setOrgid(orgId)
        shipment.setShipmentid(shipmentId)
        shipment.setAssignedcarrier(carrierId)
        shipment.setPartyqualifierid(partyqualifierid)
        shipment.shipmentstops = stop_facilities.collect {
            shipmentUtil.update_facilities_and_stops_on_tlm_shipment(stop_facilities.indexOf(it), shipment, minimum_days_from_now, stop_facilities, stop_actions)
        }
        shipment.shipmentordermovements = orders.collect {
            shipmentUtil.update_order_movement(orders.indexOf(it), shipment, orders)
        }
        shipment.shipmentinvolvedparties = shipmentUtil.update_Involved_parties(shipment, involvedPatyId)
        shipmentJson = shipment.buildsimplejson()
        println("Shipment Json with 2 Stops =" + shipmentJson)
        String updateShipmentUrl = commonUtil.getUrl("shipment", "udpateshipment_endpoint")
        updateShipmentUrl = updateShipmentUrl.replace('${shipmentId}', shipmentId)
        response = restAssuredUtils.updateRequest(updateShipmentUrl, shipmentJson)
        commonUtil.assertStatusCode(response)
        String getShipmentUrl = commonUtil.getUrl("shipment", "getshipment_endpoint")
        getShipmentUrl = getShipmentUrl.replace('${shipmentId}', shipmentId)
        response = restAssuredUtils.getRequest(getShipmentUrl)
        commonUtil.assertStatusCode(response)
        def actualPartyQualifierId = response.getBody().jsonPath().get("data.InvolvedParties.PartyQualifier.PartyQualifierId")[0]
        Assert.assertEquals(actualPartyQualifierId, partyqualifierid, "The expected qualifier is " + partyqualifierid + " but found " + actualPartyQualifierId)
    }

    @Test(description = "Create a Shipment and perform TENDER, ACCEPT, REJECT of Shipment and check the status")
    public void tenderAcceptRejectShipment() {
        def shipmentId = 'SHIP_TENDER_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'SHIP_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def stop_facilities = ['FAC1', 'FAC2']
        def stop_actions = ['PU', 'DL']
        def orders = ['HAR_ORDER_41', 'HAR_ORDER_42']
        def reasonCode = 'REASON_CODE1'
        /*Create Shipment Json*/
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
        /*Create Tender Json*/
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

        /*Create Tender Accept Json*/
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

        /*Create Tender Reject Json*/
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

    @Test(description = "To add an order to the Shipment and check whether the Order planned status is changed to Planned or not.")
    public void createDistributionOrder() {
        def orderId = 'PAR_ORDER_' + CommonUtils.getFourDigitRandomNumber()
        def orderType = 'PAR_WAVE_' + CommonUtils.getFourDigitRandomNumber()
        def order_origin_facility = 'FAC1'
        def order_destination_facility = 'FAC2'
        def order_line_item = ['HAR_ITEM01']
        order.setOrderId(orderId)
        order.setOrderType(orderType)
        order.setOriginFacilityId(order_origin_facility)
        order.setDestinationFacilityId(order_destination_facility)
        commonUtil.update_order_timestamp(order, minimum_days_from_now)
        order.orderlines = order_line_item.collect {
            orderUtil.update_order_Lines(order_line_item.indexOf(it), order_line_item, order)
        }
        orderJson = order.buildjson()
        println("Distribution Order json -> " + orderJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("order", "create_endpoint"), orderJson)
        commonUtil.assertStatusCode(response)
        def shipmentId = 'SHIP_ORDER_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'SHIP_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def stop_facilities = ['FAC1', 'FAC2']
        def stop_actions = ['PU', 'DL']
        def orders = [orderId]
        def reasonCode = 'REASON_CODE1'
        /*Create Shipment Json*/
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
        String getOrderUrl = commonUtil.getUrl("order", "get_endpoint")
        getOrderUrl = getOrderUrl.replace('${orderId}', orderId)
        response = restAssuredUtils.getRequest(getOrderUrl)
        commonUtil.assertStatusCode(response)
        Assert.assertEquals(response.getBody().jsonPath().get("data.PlanningStatus"), "Planned", "The expected Status is Planned" + " but found " + response.getBody().jsonPath().get("data.PlanningStatus"));
    }

    @Test(description = 'Create Mass involved parties for 3 Shipments')
    public void massInvolvedParty() {
        List<String> shipmentIds = new ArrayList<String>()
        for (int i = 0; i < 3; i++) {
            def shipmenttempId = 'MASS_SHIPMENT_' + CommonUtils.getFourDigitRandomNumber()
            shipmentIds.add(shipmenttempId)
            def carrierId = 'MASS_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
            def orders = ['MASS_ORDER_41', 'HAR_ORDER_42']
            def stop_facilities = ['FAC1', 'FAC2']
            def stop_actions = ['PU', 'DL']
            def involvedPatyId = 'eb1c47a-fc08-48a1-9340-f441c4ddec' + CommonUtils.getFourDigitRandomNumber()
            partyqualifierid = "Insurance Company"
            shipment.setOrgid(orgId)
            shipment.setShipmentid(shipmenttempId)
            //shipment.setPartyqualifierid(partyqualifierid)
            shipment.setAssignedcarrier(carrierId)
            shipment.shipmentstops = stop_facilities.collect {
                shipmentUtil.update_facilities_and_stops_on_tlm_shipment(stop_facilities.indexOf(it), shipment, minimum_days_from_now, stop_facilities, stop_actions)
            }
            shipment.shipmentordermovements = orders.collect {
                shipmentUtil.update_order_movement(orders.indexOf(it), shipment, orders)
            }
            shipmentJson = shipment.buildShipmentjsonOrderMovement()
            //println(shipmentJson)
            Response response = restAssuredUtils.postRequest(commonUtil.getUrl("shipment", "create_endpoint"), shipmentJson)
            commonUtil.assertStatusCode(response)
        }
        /*Iterator itr = shipmentId.iterator()
        while (itr.hasNext()){
            println("Shipmentid-> "+ itr.next())
        }*/
        def involvedPatyId = 'eb1c47a-fc08-48a1-9340-f441c4ddec' + CommonUtils.getFourDigitRandomNumber()
        baseMassCreateInvolvedParties.setOrgid(orgId)
        baseMassCreateInvolvedParties.setShipmentIds(shipmentIds)
        baseMassCreateInvolvedParties.setPartyqualifierid(partyqualifierid)
        baseMassCreateInvolvedParties.involvedParties = shipmentUtil.update_mass_Involved_parties(baseMassCreateInvolvedParties)
        involvedPatyJson = baseMassCreateInvolvedParties.buildJson()
        println("Mass Involved Parties Json: " + involvedPatyJson)
        Response response = restAssuredUtils.postRequest(commonUtil.getUrl('involvedparties', 'masscreate_endpoint'), involvedPatyJson)
        commonUtil.assertStatusCode(response)
        def getPartyqualifierUrl = commonUtil.getUrl("partyqualifier", "get_endpoint")
        getPartyqualifierUrl = getPartyqualifierUrl.replace('${partyQualifierId}', "Insurance Company")
        response = restAssuredUtils.getRequest(getPartyqualifierUrl)
        String responseString = response.getBody().jsonPath().get("data.InvolvedParties")
        println(responseString)
        Iterator itr = shipmentIds.iterator()
        while(itr.hasNext()){
            println(responseString.contains("ShipmentId=${itr.next()}"))
        }
        commonUtil.assertStatusCode(response)
    }

    @Test(description = "Create a received status config")
    public void createReceivedStatusConfig() {
        def configId = "VConfig-"+CommonUtils.getFourDigitRandomNumber()
        baseShipmentReceivedStatus.setEnableReceivedStatusFlow(false)
        baseShipmentReceivedStatus.setEvaluationCriteria("VCrit-02")
        baseShipmentReceivedStatus.setMonitorOrderLineQuantity(false)
        baseShipmentReceivedStatus.setMonitorOrderLineQuantity(false)
        baseShipmentReceivedStatus.setMonitorOrderLineSize1(false)
        baseShipmentReceivedStatus.setMonitorOrderLineSize2(false)
        baseShipmentReceivedStatus.setMonitorOrderLineSizeValue(false)
        baseShipmentReceivedStatus.setMonitorOrderLineVolume(false)
        baseShipmentReceivedStatus.setMonitorOrderLineWeight(false)
        baseShipmentReceivedStatus.setReceivedStatusConfigId(configId)
        def receivedStatusJson =baseShipmentReceivedStatus.buildJson()
        println("Received Status Json-> "+receivedStatusJson)
        Response response = restAssuredUtils.postRequest(commonUtil.getUrl("receivedstatusconfig", "create_endpoint"),receivedStatusJson)
        commonUtil.assertStatusCode(response)
        String getShipmentReceivedStatusUrl = commonUtil.getUrl("receivedstatusconfig","get_endpoint")
        getShipmentReceivedStatusUrl = getShipmentReceivedStatusUrl.replace('${receivedStatusConfigId}',configId )
        response = restAssuredUtils.getRequest(getShipmentReceivedStatusUrl)
        commonUtil.assertStatusCode(response)
        println(response.getBody().jsonPath().get("data"))
    }

    @Test(description = 'Example of fetching the expected data from the yaml file', enabled = false)
    public void temp() {
        def shipmentId = 'PAR_SHIPMENT_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'PAR_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def stop_facilities = ['FAC1', 'FAC2']
        def stop_actions = ['PU', 'DL']
        def orders = ['HAR_ORDER_41', 'HAR_ORDER_42']
        def involvedPatyId = '0eb1c47a-fc08-48a1-9340-f441c4ddec' + CommonUtils.getFourDigitRandomNumber()
        Response response
        partyqualifierid = "Insurance Company"
        /*Create Shipment Json*/
        shipment.setOrgid(orgId)
        shipment.setShipmentid(shipmentId)
        shipment.setAssignedcarrier(carrierId)
        shipment.setPartyqualifierid(partyqualifierid)
        shipment.shipmentstops = stop_facilities.collect {
            shipmentUtil.update_facilities_and_stops_on_tlm_shipment(stop_facilities.indexOf(it), shipment, minimum_days_from_now, stop_facilities, stop_actions)
        }
        shipment.shipmentordermovements = orders.collect {
            shipmentUtil.update_order_movement(orders.indexOf(it), shipment, orders)
        }
        shipment.shipmentinvolvedparties = shipmentUtil.update_Involved_parties(shipment, involvedPatyId)
        shipmentJson = shipment.buildsimplejson()
        println("Shipment Json with 2 Stops =" + shipmentJson)
        response = restAssuredUtils.postRequest(commonUtil.getUrl("shipment", "endpoint"), shipmentJson)
        println("Status = " + response.getStatusCode())
        println(validationKey[commonUtil.currentMethodName()]["orders"][1])
        //shipmentUtil.getShipment(shipmentId)
    }
}
