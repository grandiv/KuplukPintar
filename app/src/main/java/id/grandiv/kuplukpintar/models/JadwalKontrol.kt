package id.grandiv.kuplukpintar.models
import com.google.firebase.Timestamp
class JadwalKontrol() {
    lateinit var id: String
    lateinit var tanggal: Timestamp
    lateinit var tempat: String
    lateinit var dokter: String
    lateinit var pesan: String

    constructor(id:String, tanggal: Timestamp, tempat: String, dokter: String, pesan: String) : this() {
        this.id = id
        this.tanggal = tanggal
        this.tempat = tempat
        this.dokter = dokter
        this.pesan = pesan
    }
}