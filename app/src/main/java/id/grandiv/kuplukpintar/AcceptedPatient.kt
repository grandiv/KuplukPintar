package id.grandiv.kuplukpintar

class AcceptedPatient() {
    lateinit var name: String
    lateinit var email: String
    lateinit var address: String
    lateinit var phoneNumber: String
    lateinit var doctorId: String

    constructor(name: String, email: String, address: String, phoneNumber: String, doctorId: String) : this() {
        this.name = name
        this.email = email
        this.address = address
        this.phoneNumber = phoneNumber
        this.doctorId = doctorId
    }
}