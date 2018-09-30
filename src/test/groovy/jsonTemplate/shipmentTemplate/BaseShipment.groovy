package jsonTemplate.shipmentTemplate

import groovy.json.*

class BaseShipment {

    def orgid
    def shipmentid
    def assignedcarrier
    def transportationstatus
    def protectionlevel
    def hazardousmaterial
    def productclass
    def billingmethod
    def actualtransportmode
    def actualequipment
    def actualservicelevel
    def designatedmodeid
    def designatedequipmentid
    def designatedservicelevelid
    def designatedshipvia
    def productclassid
    def totalcost
    def totalcostcurrencyuom
    def trailerid
    def trailernumber
    def tractornumber
    def billofladingnumber
    def pronumber
    def ontimestatus
    def dynamiccarrieropteligible
    def criteriaid
    def partyqualifierid
    def shipmentactions = []
    def shipmentstops = []
    def shipmentordermovements = []
    def shipmentinvolvedparties = []
    def shipmentnotes =[]

    BaseShipment() {

        orgid = '1'
        shipmentid = '1234'
        assignedcarrier = 'CARR1'
        transportationstatus = 'Planned'
        protectionlevel = 'PL1'
        hazardousmaterial = true
        productclass = 'PRod-01'
        billingmethod = 'Prepaid'
        actualtransportmode = 'LTL'
        actualequipment = 'TRACTOR-01'
        actualservicelevel = 'SL-LTL'
        designatedmodeid = 'LTL'
        designatedequipmentid = 'TRACTOR-01'
        designatedservicelevelid = 'SL-LTL'
        designatedshipvia = null
        productclassid = '30'
        totalcost = 50.0
        totalcostcurrencyuom = 'USD'
        trailerid = "PB08"
        trailernumber = "PB08C1234"
        tractornumber = "PB08C5678"
        billofladingnumber = "abcd1234efgh5678"
        pronumber = "1234567890"
        ontimestatus = "Early"
        dynamiccarrieropteligible = true
        criteriaid = "56872354"
        partyqualifierid = ''
        shipmentactions =new BaseShipmentActionReset()
        shipmentstops = (1..2).collect{new BaseShipmentStop(orgid,shipmentid)}
        shipmentordermovements = 1.collect {new BaseShipmentOrderMovement(orgid,shipmentid)}
        shipmentinvolvedparties = 1.collect{new BaseShipmentInvolvedParties(orgid,shipmentid,partyqualifierid)}
        shipmentnotes=1.collect {new BaseShipmentNote(orgid,shipmentid)}
    }

    def buildsimplejson()
    {
       def json = new JsonBuilder()
        def root = json {
            Orgid this.orgid
            AssignedCarrier this.assignedcarrier
            ShipmentId this.shipmentid
            TransportationStatus this.transportationstatus
            ProtectionLevel this.protectionlevel
            HazardousMaterial this.hazardousmaterial
            ProductClass this.productclass
            BillingMethod this.billingmethod
            ActualTransportMode this.actualtransportmode
            ActualEquipment this.actualequipment
            ActualServiceLevel this.actualservicelevel
            DesignatedModeId this.designatedmodeid
            DesignatedEquipmentId this.designatedequipmentid
            DesignatedServiceLevelId this.designatedservicelevelid
            DesignatedShipVia this.designatedshipvia
            ProductClassId this.productclassid
            TotalCost this.totalcost
            TotalCostCurrencyUOM this.totalcostcurrencyuom
            TrailerId this.trailerid
            TrailerNumber this.trailernumber
            TractorNumber this.tractornumber
            BillOfLadingNumber this.billofladingnumber
            ProNumber this.pronumber
            OnTimeStatus this.ontimestatus
            DynamicCarrierOptEligible this.dynamiccarrieropteligible
            CriteriaId this.criteriaid
            Stop(
                    this.shipmentstops.collect{it.buildjson(delegate)}
            )
            OrderMovement(
                    this.shipmentordermovements.collect{it.buildjson(delegate)}
            )
            InvolvedParties(
                    this.shipmentinvolvedparties.collect{it.buildjson(delegate)}
            )
        }
        return json.toPrettyString()
    }

    def buildShipmentjsonOrderMovement()
    {
        def json = new JsonBuilder()
        def root = json {
            Orgid this.orgid
            AssignedCarrier this.assignedcarrier
            ShipmentId this.shipmentid
            TransportationStatus this.transportationstatus
            TotalCost this.totalcost
            TotalCostCurrencyUOM this.totalcostcurrencyuom
            Stop(
                    this.shipmentstops.collect{it.buildjson(delegate)}
            )
            OrderMovement(
                    this.shipmentordermovements.collect {it.buildjson(delegate)}
            )
        }
        return json.toPrettyString()
    }

    def buildShipmentjsonReset()
    {
        def json = new JsonBuilder()
        def root = json {
            Orgid this.orgid
            Actions this.shipmentactions.each {it.buildjson(delegate)}
            AssignedCarrier this.assignedcarrier
            ShipmentId this.shipmentid
            TransportationStatus this.transportationstatus
            TotalCost this.totalcost
            TotalCostCurrencyUOM this.totalcostcurrencyuom
            Stop(
                    this.shipmentstops.collect{it.buildjson(delegate)}
            )
            OrderMovement(
                    this.shipmentordermovements.collect {it.buildjson(delegate)}
            )
        }
        return json.toPrettyString()
    }

    def buildShipmentNotejson()
    {
        def json = new JsonBuilder()
        def root = json {
            Orgid this.orgid
            AssignedCarrier this.assignedcarrier
            ShipmentId this.shipmentid
            TransportationStatus this.transportationstatus
            TotalCost this.totalcost
            TotalCostCurrencyUOM this.totalcostcurrencyuom
            Stop(
                    this.shipmentstops.collect{it.buildjson(delegate)}
            )

            ShipmentNote(
                    this.shipmentnotes.collect {it.buildjson(delegate)}
            )
        }
        return json.toPrettyString()
    }

}