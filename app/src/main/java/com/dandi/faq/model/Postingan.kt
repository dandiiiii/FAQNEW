package com.example.faq

 class Postingan(
    var idUser: String,
    var pertanyaan: String,
    var jenisPertanyaan:String,
    var commentAdmin: String,
    var fotopostingan: String
)
 {
     constructor():this("","","","","")
 }