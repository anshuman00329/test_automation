package jsonTemplate.shipmentTemplate

import groovy.json.JsonBuilder

class BaseMassCreateNotes {
    def shipmentids
    BaseMassShipmentNote shipmentnotes =[]
    BaseMassCreateNotes(){
        shipmentids
    }

    def buildMassCreateNotes(){
        def json = new JsonBuilder()
        json{
            ShipmentIds this.shipmentids
            ShipmentNote(
                    this.shipmentnotes.buildjson(delegate)
            )
        }
        return json.toPrettyString()
    }
}
