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

    def createShipment(msg) {
        Response response
        try {
            shipment_config = config.read_properties()
            shipment_app_config = shipment_config['app_config']['shipment']
            /*shipment_db_config= config.add_mysql_url(shipment_config['db_config']['shipment'])*/
            URL = shipment_app_config['create_endpoint']
            URL = URL.replace('${envTag}', config.getEnv_tag())
            println("URL = " + URL)
            response = rest.postRequest(URL, msg, "application/json")
            println("Status =" + response.getStatusCode())
            if (response.getStatusCode() != 200) {
                throw new Exception("Unable to post Request to " + URL)
            }
        } catch (Exception e) {
            assert false: "Exception occured ${e.printStackTrace()}"
        }
        return response
    }

    def getShipment(String shipmentId) {
        Response response
        try {
            shipment_config = config.read_properties()
            shipment_app_config = shipment_config['app_config']['shipment']
            /*shipment_db_config= config.add_mysql_url(shipment_config[config.getEnv_tag()]['db_config']['shipment'])*/
            URL = shipment_app_config['getshipment_endpoint']
            URL = URL.replace('${envTag}', config.getEnv_tag())
            URL = URL.replace('${shipmentId}', shipmentId)
            println("URL = " + URL)
            response = rest.getRequest(URL, "application/json")
            println("Status =" + response.getStatusCode())
        } catch (Exception e) {
            assert false: "Exception occured ${e.printStackTrace()}"
        }
        return response
    }

    def deleteShipment(String pk) {
        def response
        try {
            shipment_config = config.read_properties()
            shipment_app_config = shipment_config['app_config']['shipment']
            /*shipment_db_config= config.add_mysql_url(shipment_config[config.getEnv_tag()]['db_config']['shipment'])*/
            URL = shipment_app_config['deleteshipment_endpoint']
            URL = URL.replace('${envTag}', config.getEnv_tag())
            URL = URL.replace('${pk}', pk)
            println("URL = " + URL)
            response = rest.deleteRequest(URL, "application/json")
            println("Status =" + response.getStatusCode())
            /*if(status.getStatusCode()!=200)
            {
                throw  new Exception("Unable to post Request to "+ URL)
            }*/
        } catch (Exception e) {
            assert false: "Exception occured ${e.printStackTrace()}"
        }
        return response
    }

    def updateShipment(msg, String shipmentId) {
        Response response
        try {
            shipment_config = config.read_properties()
            shipment_app_config = shipment_config['app_config']['shipment']
            /*shipment_db_config= config.add_mysql_url(shipment_config[config.getEnv_tag()]['db_config']['shipment'])*/
            URL = shipment_app_config['udpateshipment_endpoint']
            URL = URL.replace('${envTag}', config.getEnv_tag())
            URL = URL.replace('${shipmentId}', shipmentId)
            println("URL = " + URL)
            response = rest.updateRequest(URL, msg, "application/json")
            println("Status =" + response.getStatusCode())
            if (response.getStatusCode() != 200) {
                throw new Exception("Unable to put Request to " + URL)
            }
        } catch (Exception e) {
            assert false: "Exception occured ${e.printStackTrace()}"
        }
        return response.getBody()
    }

    def createInvalidShipment(msg) {
        try {
            shipment_config = config.read_properties()
            shipment_app_config = shipment_config['app_config']['shipment']
            /*shipment_db_config= config.add_mysql_url(shipment_config[config.getEnv_tag()]['db_config']['shipment'])*/
            URL = shipment_app_config['create_endpoint']
            URL = URL.replace('${envTag}', config.getEnv_tag())
            println("URL = " + URL)
            def response = rest.postRequest(URL, msg, "application/json")
            println("Status =" + response.getStatusCode())
            return response.getStatusCode()
            /*if(status.getStatusCode()!=200)
            {
                throw  new Exception("Unable to post Request to "+ URL)
            }*/
        } catch (Exception e) {
            assert false: "Exception occured ${e.printStackTrace()}"
        }
    }

    def createPartyQualifier(msg) {
        try {
            shipment_config = config.read_properties()
            shipment_app_config = shipment_config['app_config']['shipment']
            /*shipment_db_config= config.add_mysql_url(shipment_config['db_config']['shipment'])*/
            URL = shipment_app_config['party_qualifier']
            URL = URL.replace('${envTag}', config.getEnv_tag())
            println("URL = " + URL)
            def response = rest.postRequest(URL, msg)
            println("Status =" + response.getStatusCode())
            /*if(status.getStatusCode()!=200)
            {
                throw  new Exception("Unable to post Request to "+ URL)
            }*/
        } catch (Exception e) {
            assert false: "Exception occured ${e.printStackTrace()}"
        }
    }

    def assert_for_Shipment_Stop(shipmentId, carrierId, stop_facilities, stop_actions) {
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
    }

    def assert_for_invalid_Shipment_Status_Code(def responseCode) {
        boolean result = false
        if (400.equals(responseCode))
            result = true
        Assert.assertTrue(result, "The response code is not 400....please check the logs for more information.")
    }

    def assert_for_delete_Shipment_Status_Code(def responseCode) {
        boolean result = false
        if (200.equals(responseCode))
            result = true
        Assert.assertTrue(result, "The response code is not 200....please check the logs for more information.")
    }

    def assert_for_update_Shipment(def shipmentPartyQualifier) {
        boolean result = false
        def expected = "Insurance"
        if (expected.equals(shipmentPartyQualifier))
            result = true
        Assert.assertTrue(result, "The expected qualifier is " + expected + " but found " + shipmentPartyQualifier)
    }

    def assert_for_tender_Status(def expectedStatus, def actualStatus) {
        boolean result = false
        if (expectedStatus.equals(actualStatus))
            result = true
        Assert.assertTrue(result, "The expected Tender status is " + expectedStatus + " but found " + actualStatus)
    }
}
