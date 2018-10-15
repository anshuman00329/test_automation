package jsonTemplate.shipmentTemplate

import groovy.json.JsonBuilder

class BaseShipmentReceivedStatus {
    def enableReceivedStatusFlow
    def evaluationCriteria
    def extended
    def monitorOrderLineQuantity
    def monitorOrderLineSize1
    def monitorOrderLineSize2
    def monitorOrderLineSizeValue
    def monitorOrderLineVolume
    def monitorOrderLineWeight
    def receivedStatusConfigId

    BaseShipmentReceivedStatus(){
        enableReceivedStatusFlow
        evaluationCriteria
        extended = new BaseShipmentExtended()
        monitorOrderLineQuantity
        monitorOrderLineSize1
        monitorOrderLineSize2
        monitorOrderLineSizeValue
        monitorOrderLineVolume
        monitorOrderLineWeight
        receivedStatusConfigId
    }

    def buildJson(){
        def json = new JsonBuilder()
        def root = json {
            EnableReceivedStatusFlow this.enableReceivedStatusFlow
            EvaluationCriteria this.evaluationCriteria
            Extended this.extended.buildJson(delegate)
            MonitorOrderLineQuantity this.monitorOrderLineQuantity
            MonitorOrderLineSize1 this.monitorOrderLineSize1
            MonitorOrderLineSize2 this.monitorOrderLineSize2
            MonitorOrderLineSizeValue this.monitorOrderLineSizeValue
            MonitorOrderLineVolume this.monitorOrderLineVolume
            MonitorOrderLineWeight this.monitorOrderLineWeight
            ReceivedStatusConfigId this.receivedStatusConfigId
        }
        return json.toPrettyString()
    }
}
