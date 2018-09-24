package jsonTemplate.orderTemplate


import groovy.json.JsonBuilder

class BaseOrderLine {

    def orderId
    def orderLineId
    def itemId
    def orderedQuantity
    def allocatedQuantity
    def originalOrderedQuantity
    def inventoryTypeId
    def productStatusId
    def batchNumber
    def countryOfOrigin
    def requiredPackQuantity
    def shelfDays
    def orderPlanningRunId
    def isPendingReplenishment
    def pipelineStatus
    def isAllocatable
    def parentOrderLineId
    def adjustedOrderedQuantity
    def nbrOfAllocationAttempts
    def projectedCompletionTime
    def isShortageLine
    def requiredBundleQuantity
    def requiredSubPackQuantity
    def requiredLpnQuantity
    def requiredPalletQuantity
    def vasProcessType
    def unitWeight
    def unitVolume
    def description
    def isCancelled
    def customerItemNbr
    def monetaryValue
    def pickupStartDateTime
    def pickupEndDateTime
    def deliveryStartDateTime
    def deliveryEndDateTime
    def protectionLevelId
    def isHazmat
    def dangerousGoodsId
    def merchandizingDepartment
    def itemGTIN
    def purchaseOrderId
    def purchaseOrderLineId
    def externalPONumber
    def externalPOLineNumber
    def storeDepartment
    def merchandizeGroup
    def merchandizeType
    def priority
    def qtyUOM
    def weight
    def weightUOM
    def volume
    def volumeUOM
    def order
    def orderLineShortage
    def transportationOrderLine
    def orderLineNote

    BaseOrderLine(orderid){
        this.orderId=orderid
        orderLineId = '1'
        itemId= 'HAR_ITEM1'
        orderedQuantity = '100'
        allocatedQuantity = '0'
        originalOrderedQuantity = '100'
        inventoryTypeId = null
        productStatusId = null
        batchNumber = null
        countryOfOrigin = null
        requiredPackQuantity = null
        shelfDays = null
        orderPlanningRunId = null
        isPendingReplenishment = false
        pipelineStatus = null
        isAllocatable = false
        parentOrderLineId = null
        adjustedOrderedQuantity = null
        nbrOfAllocationAttempts = '0'
        projectedCompletionTime = null
        isShortageLine = false
        requiredBundleQuantity = null
        requiredSubPackQuantity = null
        requiredLpnQuantity = null
        requiredPalletQuantity = null
        vasProcessType = null
        unitWeight = '1'
        unitVolume = '1'
        description = null
        isCancelled = false
        customerItemNbr = null
        monetaryValue = null
        pickupStartDateTime = null
        pickupEndDateTime = null
        deliveryStartDateTime = null
        deliveryEndDateTime = null
        protectionLevelId = null
        isHazmat = false
        dangerousGoodsId = null
        merchandizingDepartment = null
        itemGTIN = null
        purchaseOrderId = null
        purchaseOrderLineId = null
        externalPONumber = null
        externalPOLineNumber = null
        storeDepartment = null
        merchandizeGroup = null
        merchandizeType = null
        priority = null
        qtyUOM = null
        weight = '100'
        weightUOM = 'lb'
        volume = '100'
        volumeUOM = 'cuft'
        order = null
        orderLineShortage = null
        transportationOrderLine = null
        orderLineNote = null
    }

    def buildjson(parent) {

        parent."OrderLine" {
            OrderId this.orderId
            OrderLineId this.orderLineId
            ItemId this.itemId
            OrderedQuantity this.orderedQuantity
            AllocatedQuantity this.allocatedQuantity
            OriginalOrderedQuantity this.originalOrderedQuantity
            InventoryTypeId this.inventoryTypeId
            ProductStatusId this.productStatusId
            BatchNumber this.batchNumber
            CountryOfOrigin this.countryOfOrigin
            RequiredPackQuantity this.requiredPackQuantity
            ShelfDays this.shelfDays
            OrderPlanningRunId this.orderPlanningRunId
            IsPendingReplenishment this.isPendingReplenishment
            PipelineStatus this.pipelineStatus
            IsAllocatable this.isAllocatable
            ParentOrderLineId this.parentOrderLineId
            AdjustedOrderedQuantity this.adjustedOrderedQuantity
            NbrOfAllocationAttempts this.nbrOfAllocationAttempts
            ProjectedCompletionTime this.projectedCompletionTime
            IsShortageLine this.isShortageLine
            RequiredBundleQuantity this.requiredBundleQuantity
            RequiredSubPackQuantity this.requiredSubPackQuantity
            RequiredLpnQuantity this.requiredLpnQuantity
            RequiredPalletQuantity this.requiredPalletQuantity
            VasProcessType this.vasProcessType
            UnitWeight this.unitWeight
            UnitVolume this.unitVolume
            Description this.description
            IsCancelled this.isCancelled
            CustomerItemNbr this.customerItemNbr
            MonetaryValue this.monetaryValue
            PickupStartDateTime this.pickupStartDateTime
            PickupEndDateTime this.pickupEndDateTime
            DeliveryStartDateTime this.deliveryStartDateTime
            DeliveryEndDateTime this.deliveryEndDateTime
            ProtectionLevelId this.protectionLevelId
            IsHazmat this.isHazmat
            DangerousGoodsId this.dangerousGoodsId
            MerchandizingDepartment this.merchandizingDepartment
            ItemGTIN this.itemGTIN
            PurchaseOrderId this.purchaseOrderId
            PurchaseOrderLineId this.purchaseOrderLineId
            ExternalPONumber this.externalPONumber
            ExternalPOLineNumber this.externalPOLineNumber
            StoreDepartment this.storeDepartment
            MerchandizeGroup this.merchandizeGroup
            MerchandizeType this.merchandizeType
            Priority this.priority
            QtyUOM this.qtyUOM
            Weight this.weight
            WeightUOM this.weightUOM
            Volume this.volume
            VolumeUOM this.volumeUOM
            Order this.order
            OrderLineShortage this.orderLineShortage
            TransportationOrderLine this.transportationOrderLine
            OrderLineNote this.orderLineNote
        }
    }

}
