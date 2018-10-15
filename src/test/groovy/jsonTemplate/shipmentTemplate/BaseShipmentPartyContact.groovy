package jsonTemplate.shipmentTemplate

class BaseShipmentPartyContact {
    def firstname
    def lastname
    def email
    def phone
    def address1
    def address2
    def address3
    def city
    def state
    def postalcode
    def county
    def country

    BaseShipmentPartyContact(){
        firstname = 'Automation'
        lastname = ''
        email = 'at@mail.com'
        phone = '111-222-3334'
        address1 = '2300 Windy Ridge Pkwy'
        address2 = 'None'
        address3 = 'None'
        city = 'Atlanta'
        state = 'GA'
        postalcode = '30339'
        county = 'Cobb'
        country = 'USA'
    }
    def buildjson(parent)
    {
        parent."PartyContact"{
            FirstName this.firstname
            LastName this.lastname
            Email this.email
            Phone this.phone
            Address1 this.address1
            Address2 this.address2
            Address3 this.address3
            City this.city
            State this.state
            PostalCode this.postalcode
            County this.county
            Country this.country
        }
    }

}
