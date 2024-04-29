package id.grandiv.kuplukpintar
import com.google.firebase.Timestamp

class RiwayatKontrol() {
    lateinit var tanggal: Timestamp
    lateinit var dokter: String
    lateinit var pesan: String

    constructor(tanggal: Timestamp, dokter: String, pesan: String) : this() {
        this.tanggal = tanggal
        this.dokter = dokter
        this.pesan = pesan
    }
}