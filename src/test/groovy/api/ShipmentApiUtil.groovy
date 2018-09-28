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

    /*Commenting the DB assertion methods*/

    /*def assert_for_Shipment_Stop(shipmentId, carrierId, stop_facilities, stop_actions) {
        def sql = db.shipmentDbProperties()
        //validate shipment created in SCS_SHIPMENT table <shipmentid, carrierid
        def shipmentResult = sql.rows("select SHIPMENT_ID, ASSIGNED_CARRIER from SCS_SHIPMENT WHERE SHIPMENT_ID = ${shipmentId}")
        assert shipmentResult.SHIPMENT_ID == [shipmentId]
        assert shipmentResult.ASSIGNED_CARRIER == [carrierId]

        //validate Stop info created in SCS_SHIPMENT_STOP table
        def stopResultList = sql.rows("SELECT STOP_FACILITY_ID,STOP_ACTION FROM SCS_STOP WHERE SHIPMENT_ID  = ${shipmentId} ORDER BY SEQ")
        def expectedStopFacility = stopResultList.STOP_FACILITY_ID
        def expectedStopAction = stopResultList.STOP_ACTION
        int i
        for (i = 0; i < stop_facilities.size(); i++) {
            assert expectedStopFacility[i] == stop_facilities[i]
            assert expectedStopAction[i] == stop_actions[i]
        }

    }

    def assert_for_Shipment_Stop_OrdMov(shipmentId, carrierId, stop_facilities, stop_actions, orders) {
        def sql = db.shipmentDbProperties()
        //validate Stop info created in SCS_SHIPMENT_STOP table
        def stopResultList = sql.rows("SELECT STOP_FACILITY_ID,STOP_ACTION FROM SCS_STOP WHERE SHIPMENT_ID  = ${shipmentId} ORDER BY SEQ")
        def expectedStopFacility = stopResultList.STOP_FACILITY_ID
        def expectedStopAction = stopResultList.STOP_ACTION
        int i
        for (i = 0; i < stop_facilities.size(); i++) {
            assert expectedStopFacility[i] == stop_facilities[i]
            assert expectedStopAction[i] == stop_actions[i]
        }

        //validate entries in OrderMovement Table
        def orderMovResult = sql.rows("SELECT ORDER_ID FROM SCS_ORDER_MOVEMENT WHERE SHIPMENT_ID = ${shipmentId} ORDER BY ORDER_PICKUP_SEQ")
        def expectedOrdersinOrdMov = orderMovResult.ORDER_ID
        for (i = 0; i < orders.size(); i++) {
            assert expectedOrdersinOrdMov[i] == orders[i]
        }
    }

    def assert_for_Shipment_Level_Note(shipmentId, noteType, noteValue, noteCode, noteVisibility) {
        def sql = db.shipmentDbProperties()

        //validate shipment NOTE created in SCS_SHIPMENT_NOTE table
        def shipmentNoteResultList = sql.rows("select NOTE_CODE,NOTE_TYPE,NOTE_VALUE,NOTE_VISIBILITY from SCS_SHIPMENT_NOTE WHERE SHIPMENT_PK IN (SELECT PK FROM SCS_SHIPMENT WHERE SHIPMENT_ID = ${shipmentId}) order by seq")

        def expectedNoteCode = shipmentNoteResultList.NOTE_CODE
        def expectedNoteType = shipmentNoteResultList.NOTE_TYPE
        def expectedNoteValue = shipmentNoteResultList.NOTE_VALUE
        def expectedNoteVisibility = shipmentNoteResultList.NOTE_VISIBILITY
        int i
        for (i = 0; i < noteVisibility.size(); i++) {
            assert expectedNoteCode[i] == noteCode[i]
            assert expectedNoteType[i] == noteType[i]
            assert expectedNoteValue[i] == noteValue[i]
            assert expectedNoteVisibility[i] == noteVisibility[i]
        }
    }*/
}
