package jsonTemplate.trackingTemplate

import groovy.json.*

class BaseTrackingCreate {
    def trackingId
    def shipmentId
    def trackingStatus
    def carrierId
    def messageType
    def stopSeq
    def eventtimestamp
    def trailerNumber
    def tractorNumber
    def billOfLadingNumber
    def proNumber
    def notes
    def latitude
    def longitude
    def city
    def country
    def stateProvince
    def postalCode
    def address=[]

    BaseTrackingCreate() {
        trackingId = "1"
        shipmentId = "ShipB1"
        trackingStatus ="ACCEPTED"
        carrierId = "CARR2"
        messageType = "Arrival"
        stopSeq = 1
        eventtimestamp = "2018-08-06T06:00:00"
        trailerNumber = "TrailerNumber at stop2 Arrival"
        tractorNumber = "TractorNumber at stop2 Arrival"
        billOfLadingNumber ="BillOfLoading at stop2 Arrival"
        proNumber = "1234ProNumber at stop2 Arrival"
       address = new BaseAddress()
        notes = "To test shipment attributes"
        latitude = "58.5000"
        longitude = "-134.4967"
        city = "Jacksonville"
        country = "USA"
        postalCode = "32214"
        stateProvince = "FL"
    }
    def buildjson(){
        def json = new JsonBuilder()
        def root = json {
            TrackingId this.trackingId
            ShipmentId this.shipmentId
            TrackingStatus this.trackingStatus
            CarrierId this.carrierId
            MessageType this.messageType
            StopSeq this.stopSeq
            Eventtimestamp this.eventtimestamp
            TrailerNumber this.trailerNumber
            TractorNumber this.tractorNumber
            BillOfLadingNumber this.billOfLadingNumber
            ProNumber this.proNumber
            Notes this.notes
            Address this.address.each { it.buildjson(delegate)}



            Latitude this.latitude
            Longitude this.longitude
            City this.city
            Country this.country
            StateProvince this.stateProvince
            PostalCode this.postalCode

        }
        return json.toPrettyString()
    }

}
