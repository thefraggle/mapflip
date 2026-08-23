package de.goork.mapflip.parser

interface MapUrlParser {
    val serviceName: String
    val supportedHosts: List<String>

    fun canParse(url: String): Boolean
    fun parse(url: String): ParsedLocation
    fun extractUrl(text: String?): String?
}
