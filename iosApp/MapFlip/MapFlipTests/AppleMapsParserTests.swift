import XCTest
@testable import MapFlip

final class AppleMapsParserTests: XCTestCase {

    func testConvertsSearchQuery() {
        let parsed = AppleMapsParser.parse(appleUrl: "https://maps.apple.com/?q=Eiffelturm")
        XCTAssertEqual(parsed.type, .query("Eiffelturm"))
        
        let url = UrlSchemeBuilder.buildUrl(for: parsed, target: .googleMaps)
        XCTAssertEqual(url.absoluteString, "comgooglemaps://?q=Eiffelturm")
    }

    func testConvertsCoordinates() {
        let parsed = AppleMapsParser.parse(appleUrl: "https://maps.apple.com/?ll=48.8584,2.2945")
        XCTAssertEqual(parsed.type, .coordinates(coords: "48.8584,2.2945", query: nil))
        
        let url = UrlSchemeBuilder.buildUrl(for: parsed, target: .googleMaps)
        XCTAssertEqual(url.absoluteString, "comgooglemaps://?q=48.8584,2.2945&center=48.8584,2.2945")
    }

    func testConvertsCoordinatesWithQuery() {
        let parsed = AppleMapsParser.parse(appleUrl: "https://maps.apple.com/?ll=48.8584,2.2945&q=Eiffelturm")
        XCTAssertEqual(parsed.type, .coordinates(coords: "48.8584,2.2945", query: "Eiffelturm"))
        
        let url = UrlSchemeBuilder.buildUrl(for: parsed, target: .googleMaps)
        XCTAssertEqual(url.absoluteString, "comgooglemaps://?q=Eiffelturm&center=48.8584,2.2945")
    }

    func testConvertsAddress() {
        let parsed = AppleMapsParser.parse(appleUrl: "https://maps.apple.com/?address=Berlin")
        XCTAssertEqual(parsed.type, .query("Berlin"))
    }

    func testConvertsAuidParameterAsSearchQuery() {
        let parsed = AppleMapsParser.parse(appleUrl: "https://maps.apple.com/?auid=1234567890")
        XCTAssertEqual(parsed.type, .query("1234567890"))
    }

    func testConvertsDirections() {
        let parsed = AppleMapsParser.parse(appleUrl: "https://maps.apple.com/?daddr=Munich")
        XCTAssertEqual(parsed.type, .navigation(destination: "Munich", mode: nil))
        
        let url = UrlSchemeBuilder.buildUrl(for: parsed, target: .googleMaps)
        XCTAssertEqual(url.absoluteString, "comgooglemaps://?daddr=Munich")
    }

    func testConvertsNearParameter() {
        let parsed = AppleMapsParser.parse(appleUrl: "https://maps.apple.com/?near=Hamburg")
        XCTAssertEqual(parsed.type, .query("Hamburg"))
    }

    func testFallbackForEmptyUrl() {
        let parsed = AppleMapsParser.parse(appleUrl: "https://maps.apple.com/")
        XCTAssertEqual(parsed.type, .home)
    }

    func testHandlesNullOrBlankUrl() {
        XCTAssertEqual(AppleMapsParser.parse(appleUrl: nil).type, .home)
        XCTAssertEqual(AppleMapsParser.parse(appleUrl: "").type, .home)
        XCTAssertEqual(AppleMapsParser.parse(appleUrl: "   ").type, .home)
    }

    func testHandlesUrlWithoutScheme() {
        let parsed = AppleMapsParser.parse(appleUrl: "maps.apple.com/?q=Hamburg")
        XCTAssertEqual(parsed.type, .query("Hamburg"))
    }

    func testConvertsDirectionsWithBothOriginAndDestination() {
        let parsed = AppleMapsParser.parse(appleUrl: "https://maps.apple.com/?saddr=Berlin&daddr=Munich")
        XCTAssertEqual(parsed.type, .directions(origin: "Berlin", destination: "Munich", mode: nil))
        
        let url = UrlSchemeBuilder.buildUrl(for: parsed, target: .googleMaps)
        XCTAssertEqual(url.absoluteString, "comgooglemaps://?saddr=Berlin&daddr=Munich")
    }

    func testConvertsStartAddressOnly() {
        let parsed = AppleMapsParser.parse(appleUrl: "https://maps.apple.com/?saddr=Frankfurt")
        XCTAssertEqual(parsed.type, .query("Frankfurt"))
    }

    func testConvertsPtCoordinateParameter() {
        let parsed = AppleMapsParser.parse(appleUrl: "https://maps.apple.com/?pt=52.5200,13.4050")
        XCTAssertEqual(parsed.type, .coordinates(coords: "52.5200,13.4050", query: nil))
    }

    func testConvertsPtCoordinateParameterWithQuery() {
        let parsed = AppleMapsParser.parse(appleUrl: "https://maps.apple.com/?pt=52.5200,13.4050&q=TV+Tower")
        XCTAssertEqual(parsed.type, .coordinates(coords: "52.5200,13.4050", query: "TV Tower"))
    }

    func testHandlesCaseInsensitiveQueryParameters() {
        let parsed1 = AppleMapsParser.parse(appleUrl: "https://maps.apple.com/?DADDR=Hamburg")
        XCTAssertEqual(parsed1.type, .navigation(destination: "Hamburg", mode: nil))
        
        let parsed2 = AppleMapsParser.parse(appleUrl: "https://maps.apple.com/?Q=Cologne")
        XCTAssertEqual(parsed2.type, .query("Cologne"))
    }

    func testConvertsShortLinkWithPlaceId() {
        let shortUrl = "https://maps.apple.com/p/dtcGHQZ--4bUSh"
        let parsed = AppleMapsParser.parse(appleUrl: shortUrl)
        XCTAssertEqual(parsed.type, .searchFallback(shortUrl))
    }

    func testConvertsPlacePathWithNameParameter() {
        let parsed = AppleMapsParser.parse(appleUrl: "https://maps.apple.com/place?name=Brandenburg+Gate")
        XCTAssertEqual(parsed.type, .query("Brandenburg Gate"))
    }

    func testCleansSpacesInsideCoordinates() {
        let parsed = AppleMapsParser.parse(appleUrl: "https://maps.apple.com/?ll=%2052.5200,%2013.4050%20")
        XCTAssertEqual(parsed.type, .coordinates(coords: "52.5200,13.4050", query: nil))
    }

    func testExtractsMapUrlFromTextSnippet() {
        let snippet = "Hey! Let's meet at https://maps.apple.com/?q=Brandenburg+Gate."
        let extracted = AppleMapsParser.extractMapUrl(from: snippet)
        XCTAssertEqual(extracted, "https://maps.apple.com/?q=Brandenburg+Gate")
        
        let parsed = AppleMapsParser.parse(appleUrl: snippet)
        XCTAssertEqual(parsed.type, .query("Brandenburg Gate"))
    }

    func testHandlesAppleMapsCustomScheme() {
        let parsed = AppleMapsParser.parse(appleUrl: "applemaps://maps.apple.com/?q=Berlin")
        XCTAssertEqual(parsed.type, .query("Berlin"))
    }

    func testConvertsDirectionsWithTravelMode() {
        let transit = AppleMapsParser.parse(appleUrl: "https://maps.apple.com/?saddr=Berlin&daddr=Potsdam&dirflg=r")
        XCTAssertEqual(transit.type, .directions(origin: "Berlin", destination: "Potsdam", mode: .transit))
        let transitUrl = UrlSchemeBuilder.buildUrl(for: transit, target: .googleMaps)
        XCTAssertEqual(transitUrl.absoluteString, "comgooglemaps://?saddr=Berlin&daddr=Potsdam&directionsmode=transit")

        let walking = AppleMapsParser.parse(appleUrl: "https://maps.apple.com/?saddr=Berlin&daddr=Potsdam&dirflg=w")
        XCTAssertEqual(walking.type, .directions(origin: "Berlin", destination: "Potsdam", mode: .walking))
        let walkingUrl = UrlSchemeBuilder.buildUrl(for: walking, target: .googleMaps)
        XCTAssertEqual(walkingUrl.absoluteString, "comgooglemaps://?saddr=Berlin&daddr=Potsdam&directionsmode=walking")
    }

    func testWazeUrlGeneration() {
        let coords = AppleMapsParser.parse(appleUrl: "https://maps.apple.com/?ll=48.8584,2.2945")
        let wazeUrl = UrlSchemeBuilder.buildUrl(for: coords, target: .waze)
        XCTAssertEqual(wazeUrl.absoluteString, "waze://?ll=48.8584,2.2945&navigate=yes")
    }

    func testBingMapsParsing() {
        let coords = AppleMapsParser.parse(appleUrl: "https://www.bing.com/maps?cp=52.5200~13.4050&where1=Berlin")
        XCTAssertEqual(coords.type, .coordinates(coords: "52.5200,13.4050", query: "Berlin"))

        let search = AppleMapsParser.parse(appleUrl: "https://www.bing.com/maps?q=Eiffelturm")
        XCTAssertEqual(search.type, .query("Eiffelturm"))
    }

    func testOpenStreetMapParsing() {
        let coords = AppleMapsParser.parse(appleUrl: "https://www.openstreetmap.org/?mlat=52.5200&mlon=13.4050")
        XCTAssertEqual(coords.type, .coordinates(coords: "52.5200,13.4050", query: nil))

        let search = AppleMapsParser.parse(appleUrl: "https://www.openstreetmap.org/search?query=Berlin")
        XCTAssertEqual(search.type, .query("Berlin"))
    }

    func testHereWeGoParsing() {
        let coords = AppleMapsParser.parse(appleUrl: "https://share.here.com/l/52.5200,13.4050?msg=Berlin")
        XCTAssertEqual(coords.type, .coordinates(coords: "52.5200,13.4050", query: "Berlin"))

        let search = AppleMapsParser.parse(appleUrl: "https://wego.here.com/search/Alexanderplatz")
        XCTAssertEqual(search.type, .query("Alexanderplatz"))
    }

    func testWazeParsing() {
        let coords = AppleMapsParser.parse(appleUrl: "https://www.waze.com/ul?ll=48.8584,2.2945&navigate=yes")
        XCTAssertEqual(coords.type, .coordinates(coords: "48.8584,2.2945", query: nil))

        let search = AppleMapsParser.parse(appleUrl: "https://www.waze.com/ul?q=Berlin")
        XCTAssertEqual(search.type, .query("Berlin"))
    }

    func testYandexMapsParsing() {
        let coords = AppleMapsParser.parse(appleUrl: "https://yandex.com/maps/?ll=13.4050,52.5200&text=Berlin")
        XCTAssertEqual(coords.type, .coordinates(coords: "52.5200,13.4050", query: "Berlin"))

        let search = AppleMapsParser.parse(appleUrl: "https://yandex.com/maps/?text=Kremlin")
        XCTAssertEqual(search.type, .query("Kremlin"))
    }

    func testHereAndYandexUrlBuilders() {
        let coords = ParsedMapLocation(type: .coordinates(coords: "52.5200,13.4050", query: "Berlin"))
        let hereUrl = UrlSchemeBuilder.buildUrl(for: coords, target: .hereWeGo)
        XCTAssertEqual(hereUrl.absoluteString, "https://share.here.com/l/52.5200,13.4050")

        let yandexUrl = UrlSchemeBuilder.buildUrl(for: coords, target: .yandexMaps)
        XCTAssertEqual(yandexUrl.absoluteString, "yandexmaps://maps.yandex.ru/?ll=13.4050,52.5200&z=16&text=Berlin")
    }
}
