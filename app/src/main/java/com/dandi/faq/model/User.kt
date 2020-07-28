package com.dandi.faq.model

class User(
    var fotoProfil: String,
    var nama: String,
    var noTelp: String,
    var uid: String
) {
    constructor() : this("", "", "", "")
}
