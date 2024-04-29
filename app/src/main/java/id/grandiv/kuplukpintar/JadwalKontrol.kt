package id.grandiv.kuplukpintar

import com.google.firebase.Timestamp

class JadwalKontrol() {
    lateinit var tanggal: Timestamp
    lateinit var tempat: String
    lateinit var dokter: String
    lateinit var pesan: String

    constructor(tanggal: Timestamp, tempat: String, dokter: String, pesan: String) : this() {
        this.tanggal = tanggal
        this.tempat = tempat
        this.dokter = dokter
        this.pesan = pesan
    }
}