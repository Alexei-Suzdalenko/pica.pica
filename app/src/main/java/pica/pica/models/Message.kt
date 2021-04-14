package pica.pica.models

data class Message(
    var id: String = "",
    var text: String = "",
    var url: String = "",
    var sender: String = "",
    var receiver: String = ""
)