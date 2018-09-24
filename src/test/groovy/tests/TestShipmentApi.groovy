package tests

import api.OrderApiUtil
import api.ShipmentApiUtil
import api.TenderApiUtil
import com.jayway.restassured.response.Response
import common_libs.CommonUtils
import jsonTemplate.orderTemplate.BaseOrder
import jsonTemplate.shipmentTemplate.BaseShipment
import jsonTemplate.shipmentTemplate.BaseShipmentPartyQualiffier
import jsonTemplate.tenderTemplate.BaseAccept
import jsonTemplate.tenderTemplate.BaseTender
import jsonTemplate.tenderTemplate.BaseTenderRecall
import jsonTemplate.tenderTemplate.BaseTenderReject
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test
import jsonTemplate.orderTemplate.BaseOrder
import jsonTemplate.shipmentTemplate.BaseShipment

class TestShipmentApi {

    BaseShipment shipment
    BaseShipmentPartyQualiffier shipmentPartyQualifier
    TenderApiUtil tenderApi
    BaseOrder order
    BaseTender baseTender
    BaseAccept baseAccept
    BaseTenderReject baseReject
    BaseTenderRecall baseRecall
    def shipmentUtil
    def orderUtil
    def commonUtil
    def minimum_days_from_now
    def orgId
    def partyqualifierid
    def updateShipmentId
    def shipmentJson, shipmentNoteJson, partyQualifierJson, tenderJson, tenderAcceptJson, tenderRejectJson, orderJson

    TestShipmentApi() {
        shipment = new BaseShipment()
        shipmentPartyQualifier = new BaseShipmentPartyQualiffier()
        order = new BaseOrder()
        shipmentUtil = new ShipmentApiUtil()
        orderUtil = new OrderApiUtil()
        commonUtil = new CommonUtils()
        baseTender = new BaseTender()
        baseAccept = new BaseAccept()
        baseReject = new BaseTenderReject()
        tenderApi = new TenderApiUtil()
        minimum_days_from_now = 2
        orgId = "1"
    }

    @BeforeClass
    public void preConfig() {
        /*Create a Insurance Company Party Qualifier*/
        partyqualifierid = "Insurance Company"
        shipmentPartyQualifier.setPartyqualifierid(partyqualifierid)
        shipmentPartyQualifier.setQualifierdescription("Adding Party Qualifier for Automation testing.")
        partyQualifierJson = shipmentPartyQualifier.buildjson()
        println("Party Qualifier json = " + partyQualifierJson)
        shipmentUtil.createPartyQualifier(partyQualifierJson)
        /*Create a Insurance Party Qualifier*/
        partyqualifierid = "Insurance"
        shipmentPartyQualifier.setPartyqualifierid(partyqualifierid)
        shipmentPartyQualifier.setQualifierdescription("Adding Party Qualifier for Automation testing update.")
        partyQualifierJson = shipmentPartyQualifier.buildjson()
        println("Party Qualifier json = " + partyQualifierJson)
        shipmentUtil.createPartyQualifier(partyQualifierJson)
    }

    @Test(description = "To create a 2 stop Shipment with all the entities added.")
    public void create2StopShipment() {
        def shipmentId = 'PAR_SHIPMENT_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'PAR_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def stop_facilities = ['FAC1', 'FAC2']
        def stop_actions = ['PU', 'DL']
        def orders = ['HAR_ORDER_41', 'HAR_ORDER_42']
        def involvedPatyId = 'eb1c47a-fc08-48a1-9340-f441c4ddec' + CommonUtils.getFourDigitRandomNumber()

        //Create Shipment Json
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
        shipmentUtil.createShipment(shipmentJson)
    }

    @Test(description = "Create a 4 Stop shipment, ensure check in Stop Table")
    public void createShipment() {
        def shipmentId = 'HAR_SHIPMENT_01'
        def carrierId = 'HAR_CARRIER_01'
        def stop_facilities = ['FAC1', 'FAC2', 'FAC3', 'FAC4']
        def stop_actions = ['PU', 'DL', 'DL', 'DL']
        def involvedPatyId = 'eb1c47a-fc08-48a1-9340-f441c4ddec' + CommonUtils.getFourDigitRandomNumber()
        partyqualifierid = "Insurance Company"
        //Create Shipment Json
        shipment.setOrgid(orgId)
        shipment.setShipmentid(shipmentId)
        shipment.setPartyqualifierid(partyqualifierid)
        shipment.setAssignedcarrier(carrierId)
        shipment.shipmentstops = stop_facilities.collect {
            shipmentUtil.update_facilities_and_stops_on_tlm_shipment(stop_facilities.indexOf(it), shipment, minimum_days_from_now, stop_facilities, stop_actions)
        }
        shipmentJson = shipment.buildsimplejson()
        println("Shipment Json with 4 Stops =" + shipmentJson)

        //Hit POST /api/scshipment/shipment/Save API, and validate the Shipment creation in db
        shipmentUtil.createShipment(shipmentJson)
        shipmentUtil.assert_for_Shipment_Stop(shipmentId, carrierId, stop_facilities, stop_actions)
    }

    @Test(description = "To create Shipment with Order Note")
    public void createShipmentWithNotes() {
        def shipmentId = 'HAR_SHIPMENT_02'
        def carrierId = 'HAR_CARRIER_02'
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
        shipmentUtil.createShipment(shipmentNoteJson)
        shipmentUtil.assert_for_Shipment_Level_Note(shipmentId, noteType, noteValue, noteCode, noteVisibility)
    }

    @Test(description = "RESET functionality of shipment json. Reset both stop and Order Movement")
    public void createShipmentWithResetLogic() {
        def shipmentId = 'HAR_SHIPMENT_04'
        def carrierId = 'HAR_CARRIER_04'
        def orders = ['HAR_ORDER_41', 'HAR_ORDER_42']
        def stop_facilities = ['FAC1', 'FAC2', 'FAC3', 'FAC4']
        def stop_actions = ['PU', 'DL', 'DL', 'DL']
        def involvedPatyId = 'eb1c47a-fc08-48a1-9340-f441c4ddec' + CommonUtils.getFourDigitRandomNumber()
        partyqualifierid = "Insurance Company"

        //Create Shipment Json with Reset Logic
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
        shipmentUtil.createShipment(shipmentJson)
        shipmentUtil.assert_for_Shipment_Stop_OrdMov(shipmentId, carrierId, stop_facilities, stop_actions, orders)

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
        shipmentUtil.createShipment(shipmentJson)
        shipmentUtil.assert_for_Shipment_Stop_OrdMov(shipmentId, carrierId, reset_stop_facilities, reset_stop_actions, resetOrders)

    }

    @Test(description = "To Create a Shipment with invalid data and check the Response code.")
    public void invalidShipment() {
        def shipmentId = 'PAR_SHIPMENT_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'PAR_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def stop_facilities = ['FAC1', 'FAC2']
        def stop_actions = ['PU', 'DL']
        def orders = ['HAR_ORDER_41', 'HAR_ORDER_42']
        def involvedPatyId = '0eb1c47a-fc08-48a1-9340-f441c4ddec'
        def response
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
        response = shipmentUtil.createInvalidShipment(shipmentJson)
        shipmentUtil.assert_for_invalid_Shipment_Status_Code(response)
    }

    @Test(description = "To Delete a Shipment and check the Response code.")
    public void deleteShipment() {
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
        shipmentUtil.createShipment(shipmentJson)
        response = shipmentUtil.getShipment(shipmentId)
        println(response.getBody().jsonPath().get("data.PK"))
        Response deleteResponse = shipmentUtil.deleteShipment(response.getBody().jsonPath().get("data.PK"))
        shipmentUtil.assert_for_delete_Shipment_Status_Code(deleteResponse.getStatusCode())
    }

    @Test(description = "To update the existing Shipment entity and validate the response")
    public void updateShipment() {
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
        shipmentUtil.createShipment(shipmentJson)
        shipmentUtil.getShipment(shipmentId)
        /*Create a new Party Qualifier to update the Shipment*/
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
        shipmentUtil.updateShipment(shipmentJson, shipmentId)
        response = shipmentUtil.getShipment(shipmentId)
        def actualPartyQualifierId = response.getBody().jsonPath().get("data.InvolvedParties.PartyQualifier.PartyQualifierId")[0]
        shipmentUtil.assert_for_update_Shipment(actualPartyQualifierId)
    }

    @Test(description = "Create a Shipment and perform TENDER, ACCEPT, REJECT of Shipment and check the status")
    public void tenderAcceptRejectShipment() {
        def shipmentId = 'SHIP_TENDER_' + CommonUtils.getFourDigitRandomNumber()
        def carrierId = 'SHIP_CARRIER_' + CommonUtils.getFourDigitRandomNumber()
        def stop_facilities = ['FAC1', 'FAC2']
        def stop_actions = ['PU', 'DL']
        def orders = ['HAR_ORDER_41', 'HAR_ORDER_42']
        def reasonCode = 'REASON_CODE1'
        Response response
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
        shipmentUtil.createShipment(shipmentJson)

        /*Create Tender Json*/
        baseTender.setShipmentid(shipmentId)
        tenderJson = baseTender.buildjson()
        println("Tender Json =" + tenderJson)
        tenderApi.tenderMsg(tenderJson, "endpoint")
        response = tenderApi.getTender(shipmentId, carrierId)
        def tenderStatus = response.getBody().jsonPath().get("data.TenderStatus")
        shipmentUtil.assert_for_tender_Status("TENDERED", tenderStatus)

        /*Create Tender Accept Json*/
        baseAccept.setShipmentid(shipmentId)
        baseAccept.setCarrierid(carrierId)
        tenderAcceptJson = baseAccept.buildjson()
        println("Accept Json =" + tenderAcceptJson)
        tenderApi.tenderMsg(tenderAcceptJson, "acceptendpoint")
        response = tenderApi.getTender(shipmentId, carrierId)
        tenderStatus = response.getBody().jsonPath().get("data.TenderStatus")
        shipmentUtil.assert_for_tender_Status("ACCEPTED", tenderStatus)

        /*Create Tender Reject Json*/
        baseReject.setShipmentid(shipmentId)
        baseReject.setCarrierid(carrierId)
        baseReject.setReasoncode(reasonCode)
        tenderRejectJson = baseReject.buildjson()
        println("Reject Json =" + tenderRejectJson)
        tenderApi.tenderMsg(tenderRejectJson, "rejectendpoint")
        response = tenderApi.getTender(shipmentId, carrierId)
        tenderStatus = response.getBody().jsonPath().get("data.TenderStatus")
        shipmentUtil.assert_for_tender_Status("REJECTED", tenderStatus)
    }

    @Test(description = "To add an order to the Shipment and check whether the Order planned status is changed to Planned or not.")
    public void createDistributionOrder() {
        def orderId = 'Par_Order_' + CommonUtils.getFourDigitRandomNumber()
        def orderType = 'PAR_WAVE_' + CommonUtils.getFourDigitRandomNumber()
        def order_origin_facility = 'FAC1'
        def order_destination_facility = 'FAC2'
        def order_line_item = ['HAR_ITEM01']
        Response response
        /*Create Order Json*/
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
        orderUtil.createDistributionOrder(orderJson)
        /*Create a Shipment with the order which is created*/
        def shipmentId = 'SHIP_ORDER_' + CommonUtils.getFourDigitRandomNumber()
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
        shipmentUtil.createShipment(shipmentJson)
        response = orderUtil.getOrder(orderId)
        println(response.getBody().jsonPath().get("data.PlanningStatus"))

    }
}
