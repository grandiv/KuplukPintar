package id.grandiv.kuplukpintar.models

class PatientRequest() {
    lateinit var name: String
    lateinit var email: String
    lateinit var address: String
    lateinit var phoneNumber: String
    lateinit var nomorSip: String

    constructor(name: String, email: String, address: String, phoneNumber: String, nomorSip: String) : this() {
        this.name = name
        this.email = email
        this.address = address
        this.phoneNumber = phoneNumber
        this.nomorSip = nomorSip
    }
}