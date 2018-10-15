package jsonTemplate.shipmentTemplate

import groovy.json.JsonBuilder

class BaseMassCreateInvolvedParties {
    def shipmentIds = []
    def involvedParties
    def partyqualifierid
    def orgid

    BaseMassCreateInvolvedParties() {
        shipmentIds
        partyqualifierid = ''
        involvedParties = new BaseMassShipmentInvolvedParties(orgid,partyqualifierid)
    }

    def buildJson() {
        def json = new JsonBuilder()
        def root = json {
            ShipmentIds this.shipmentIds
            InvolvedParties this.involvedParties.buildjson(delegate)
        }
        return json.toPrettyString()
    }
}
