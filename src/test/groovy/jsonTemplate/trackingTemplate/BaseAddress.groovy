package jsonTemplate.trackingTemplate

import groovy.json.*

class BaseAddress {
    def address1
    def address2
    def city
    def county
    def email

    BaseAddress()
    {
        address1="State Road 46"
        address2=''
        city="Jacksonville"
        county=''
        email="bserrao@manh.com"

    }
    def buildjson(parent) {

       parent."Address" {

           Address1 this.address1
           Address2 this.address2
           City this.city
           County this.county
           Email this.email
       }



    }
}
