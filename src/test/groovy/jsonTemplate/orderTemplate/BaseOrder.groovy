package jsonTemplate.orderTemplate

import groovy.json.JsonBuilder

class BaseOrder {

    def orderId
    def priority
    def pickupStartDateTime
    def pickupEndDateTime
    def orderType
    def designatedShipVia
    def deliveryStartDateTime
    def deliveryEndDateTime
    def hotOrder
    def lastPrioritizedDateTime
    def singlesOrder
    def pipelineId
    def orderedDateTime
    def isCancelled
    def merchandizingDept
    def totalMonetaryValue
    def currencyCode
    def billingMethodId
    def businessPartnerId
    def mustReleaseByDateTime
    def isOriginalOrder
    def isPerishable
    def isHazmat
    def dangerousGoodsId
    def residentialDeliveryRequired
    def designatedModeId
    def designatedCarrierId
    def designatedServiceLevelId
    def designatedEquipmentId
    def designatedTractorId
    def designatedDriverType
    def isBackOrdered
    def customerId
    def customerName
    def federatedStoreNbr
    def originFacilityId
    def originAddressLine1
    def originAddressLine2
    def originAddressLine3
    def originCity
    def originCounty
    def originCountry
    def originStateOrProvince
    def originPostalCode
    def destinationFacilityId
    def destinationAddressLine1
    def destinationAddressLine2
    def destinationAddressLine3
    def destinationCity
    def destinationCounty
    def destinationCountry
    def destinationStateOrProvince
    def destinationPostalCode
    def shipThruFacilityId
    def packAndHoldFlag
    def refShipmentId
    def refShipmentStopSeqNumber
    def billToName
    def billToTitle
    def billToFacilityName
    def billToAddressLine1
    def billToAddressLine2
    def billToAddressLine3
    def billToCity
    def billToCounty
    def billToCountryCode
    def billToStateOrProv
    def billToPostalCode
    def routeTo
    def routingShipGroupNumber
    def acctReceivableAcctNbr
    def scheduledDeliveryEndDate
    def routingAttribute
    def scheduledPickupDate
    def designatedStaticRouteId
    def minStatus
    def maxStatus
    def orderConsolidationAttribute
    def holdFlag
    def transportationOrder
    def orderNote
    def orderlines=[]

    BaseOrder() {

        orderId = '1234'
        priority = null
        pickupStartDateTime = null
        pickupEndDateTime = null
        orderType
        designatedShipVia = null
        deliveryStartDateTime = null
        deliveryEndDateTime = null
        hotOrder = null
        lastPrioritizedDateTime = null
        singlesOrder = null
        pipelineId = null
        orderedDateTime = null
        isCancelled = false
        merchandizingDept = null
        totalMonetaryValue = null
        currencyCode = null
        billingMethodId = null
        businessPartnerId = null
        mustReleaseByDateTime = null
        isOriginalOrder = false
        isPerishable = false
        isHazmat = false
        dangerousGoodsId = null
        residentialDeliveryRequired = null
        designatedModeId = null
        designatedCarrierId = 'CARR1'
        designatedServiceLevelId = null
        designatedEquipmentId = null
        designatedTractorId = null
        designatedDriverType = null
        isBackOrdered = false
        customerId = null
        customerName = null
        federatedStoreNbr = null
        originFacilityId
        originAddressLine1 = null
        originAddressLine2 = null
        originAddressLine3 = null
        originCity = null
        originCounty = null
        originCountry = null
        originStateOrProvince = null
        originPostalCode = null
        destinationFacilityId
        destinationAddressLine1 = null
        destinationAddressLine2 = null
        destinationAddressLine3 = null
        destinationCity = null
        destinationCounty = null
        destinationCountry = null
        destinationStateOrProvince = null
        destinationPostalCode = null
        shipThruFacilityId = null
        packAndHoldFlag = null
        refShipmentId = null
        refShipmentStopSeqNumber = null
        billToName = null
        billToTitle = null
        billToFacilityName = null
        billToAddressLine1 = null
        billToAddressLine2 = null
        billToAddressLine3 = null
        billToCity = null
        billToCounty = null
        billToCountryCode = null
        billToStateOrProv = null
        billToPostalCode = null
        routeTo = null
        routingShipGroupNumber = null
        acctReceivableAcctNbr = null
        scheduledDeliveryEndDate = null
        routingAttribute = null
        scheduledPickupDate = null
        designatedStaticRouteId = null
        minStatus = null
        maxStatus = null
        orderConsolidationAttribute = null
        holdFlag = false
        transportationOrder = null
        orderNote = null
        orderlines=1.collect {new BaseOrderLine(orderId)}
    }

    def buildjson()
    {
        def json = new JsonBuilder()
        def root = json {
            OrderId this.orderId
            Priority this.priority
            PickupStartDateTime this.pickupStartDateTime
            PickupEndDateTime this.pickupEndDateTime
            OrderType this.orderType
            DesignatedShipVia this.designatedShipVia
            DeliveryStartDateTime this.deliveryStartDateTime
            DeliveryEndDateTime this.deliveryEndDateTime
            HotOrder this.hotOrder
            LastPrioritizedDateTime this.lastPrioritizedDateTime
            SinglesOrder this.singlesOrder
            PipelineId this.pipelineId
            OrderedDateTime this.orderedDateTime
            IsCancelled this.isCancelled
            MerchandizingDept this.merchandizingDept
            TotalMonetaryValue this.totalMonetaryValue
            CurrencyCode this.currencyCode
            BillingMethodId this.billingMethodId
            BusinessPartnerId this.businessPartnerId
            MustReleaseByDateTime this.mustReleaseByDateTime
            IsOriginalOrder this.isOriginalOrder
            IsPerishable this.isPerishable
            IsHazmat this.isHazmat
            DangerousGoodsId this.dangerousGoodsId
            ResidentialDeliveryRequired this.residentialDeliveryRequired
            DesignatedModeId this.designatedModeId
            DesignatedCarrierId this.designatedCarrierId
            DesignatedServiceLevelId this.designatedServiceLevelId
            DesignatedEquipmentId this.designatedEquipmentId
            DesignatedTractorId this.designatedTractorId
            DesignatedDriverType this.designatedDriverType
            IsBackOrdered this.isBackOrdered
            CustomerId this.customerId
            CustomerName this.customerName
            FederatedStoreNbr this.federatedStoreNbr
            OriginFacilityId this.originFacilityId
            OriginAddressLine1 this.originAddressLine1
            OriginAddressLine2 this.originAddressLine2
            OriginAddressLine3 this.originAddressLine3
            OriginCity this.originCity
            OriginCounty this.originCounty
            OriginCountry this.originCountry
            OriginStateOrProvince this.originStateOrProvince
            OriginPostalCode this.originPostalCode
            DestinationFacilityId this.destinationFacilityId
            DestinationAddressLine1 this.destinationAddressLine1
            DestinationAddressLine2 this.destinationAddressLine2
            DestinationAddressLine3 this.destinationAddressLine3
            DestinationCity this.destinationCity
            DestinationCounty this.destinationCounty
            DestinationCountry this.destinationCountry
            DestinationStateOrProvince this.destinationStateOrProvince
            DestinationPostalCode this.destinationPostalCode
            ShipThruFacilityId this.shipThruFacilityId
            PackAndHoldFlag this.packAndHoldFlag
            RefShipmentId this.refShipmentId
            RefShipmentStopSeqNumber this.refShipmentStopSeqNumber
            BillToName this.billToName
            BillToTitle this.billToTitle
            BillToFacilityName this.billToFacilityName
            BillToAddressLine1 this.billToAddressLine1
            BillToAddressLine2 this.billToAddressLine2
            BillToAddressLine3 this.billToAddressLine3
            BillToCity this.billToCity
            BillToCounty this.billToCounty
            BillToCountryCode this.billToCounty
            BillToStateOrProv this.billToStateOrProv
            BillToPostalCode this.billToPostalCode
            RouteTo this.routeTo
            RoutingShipGroupNumber this.routingShipGroupNumber
            AcctReceivableAcctNbr this.acctReceivableAcctNbr
            ScheduledDeliveryEndDate this.scheduledDeliveryEndDate
            RoutingAttribute this.routingAttribute
            ScheduledPickupDate this.scheduledPickupDate
            DesignatedStaticRouteId this.designatedStaticRouteId
            MinStatus this.minStatus
            MaxStatus this.maxStatus
            OrderConsolidationAttribute this.orderConsolidationAttribute
            HoldFlag this.holdFlag
            TransportationOrder this.transportationOrder
            OrderNote this.orderNote
            OrderLine(
                    this.orderlines.collect{it.buildjson(delegate)}
            )

        }
        return json.toPrettyString()
    }
}
