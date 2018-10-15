package jsonTemplate.shipmentTemplate

import groovy.json.JsonBuilder

class BaseShipmentExtended {
    def buildJson(parent){
        parent."Extended"{}
    }
}
