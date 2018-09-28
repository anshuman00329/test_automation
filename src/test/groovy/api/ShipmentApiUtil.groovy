package api

import com.jayway.restassured.response.Response
import common_libs.CommonUtils
import connection_factories.RestAssuredUtils

import db.DbConnectionFactory
import jsonTemplate.shipmentTemplate.BaseShipmentInvolvedParties
import jsonTemplate.shipmentTemplate.BaseShipmentOrderMovement
import jsonTemplate.shipmentTemplate.BaseShipmentStop
import jsonTemplate.shipmentTemplate.BaseShipmentNote
import jsonTemplate.tenderTemplate.BaseTender
import org.testng.Assert

class ShipmentApiUtil {

    RestAssuredUtils rest
    BaseTender tender
    String URL
    def config = new CommonUtils()
    def shipment_app_config
    def shipment_config
    def shipment_db_config
    DbConnectionFactory db

    ShipmentApiUtil() {
        rest = new RestAssuredUtils()
        tender = new BaseTender()
        db = new DbConnectionFactory()
    }

    def update_facilities_and_stops_on_tlm_shipment(index, shipment, minimum_days_from_now, stop_facilities, stop_actions) {
        BaseShipmentStop stop = new BaseShipmentStop(shipment.orgid, shipment.shipmentid)
        stop.setStopfacilityid(stop_facilities[index])
        stop.setStopfacilityname(stop_facilities[index])
        stop.setStopaction(stop_actions[index])
        stop.setStopseq(index + 1)
        config.update_shipment_stop_timestamp(stop, index, minimum_days_from_now)
        return stop
    }

    def update_order_movement(index, shipment, orders) {
        BaseShipmentOrderMovement orderMov = new BaseShipmentOrderMovement(shipment.orgid, shipment.shipmentid)
        orderMov.setOrderid(orders[index])
        def pick_delivery_seq = index * 2
        orderMov.setOrderpickupseq(pick_delivery_seq + 1)
        orderMov.setOrderdeliveryseq(pick_delivery_seq + 2)
        return orderMov
    }

    def update_shipment_note(index, noteType, noteValue, noteCode, noteVisibility, shipment) {
        BaseShipmentNote shipmentNote = new BaseShipmentNote(shipment.orgid, shipment.shipmentid)
        shipmentNote.setNoteseq(index + 1)
        shipmentNote.setNotetype(noteType[index])
        shipmentNote.setNotevalue(noteValue[index])
        shipmentNote.setNotecode(noteCode[index])
        shipmentNote.setNotevisibility(noteVisibility[index])
        return shipmentNote
    }

    def update_Involved_parties(shipment, involvedPatyId) {
        BaseShipmentInvolvedParties involvedParties = new BaseShipmentInvolvedParties(shipment.orgid, shipment.shipmentid, shipment.partyqualifierid)
        involvedParties.setInvolvedpartyid(involvedPatyId)
        involvedParties.setPartycontactcorp("DummyCorp")
        involvedParties.setPartycontactlanguage("English")
        return involvedParties
    }
}
