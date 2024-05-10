package id.grandiv.kuplukpintar.models

class JadwalObat() {
    lateinit var id: String
    lateinit var namaobat: String
    lateinit var kadar: String
    lateinit var kuantitas: String
    lateinit var jam: String
    lateinit var kapan: String

    constructor(id:String, namaobat: String, kadar: String, kuantitas: String, jam: String, kapan: String) : this() {
        this.id = id
        this.namaobat = namaobat
        this.kadar = kadar
        this.kuantitas = kuantitas
        this.jam = jam
        this.kapan = kapan
    }
}