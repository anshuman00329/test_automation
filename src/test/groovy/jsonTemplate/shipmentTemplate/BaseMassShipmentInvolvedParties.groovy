package jsonTemplate.shipmentTemplate

class BaseMassShipmentInvolvedParties {
    def orgid
    def partyqualifierid
    def partycontactcorp
    def partycontactlanguage
    def partycontact = []

    BaseMassShipmentInvolvedParties(orgid, partyqualifierid) {
        this.orgid = orgid
        this.partyqualifierid = partyqualifierid
        partycontactcorp = ''
        partycontactlanguage = ''
        partycontact = new BaseShipmentPartyContact()
    }

    def buildjson(parent) {
        parent."InvolvedParties" {
            OrgId this.orgid
            PartyContact this.partycontact.each { it.buildjson(delegate) }
            PartyQualifierId this.partyqualifierid
            PartyContactCorp this.partycontactcorp
            PartyContactLanguage this.partycontactlanguage
        }
    }
}
