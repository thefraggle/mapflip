import Foundation

/// Converts Apple Maps URLs to compatible navigation / map URIs.
public enum AppleMapsParser {

    private static let urlRegex: NSRegularExpression = {
        let pattern = #"(?:https?://|applemaps://)(?:maps\.apple\.com|bing\.com/maps|maps\.bing\.com|openstreetmap\.org|osm\.org|wego\.here\.com|share\.here\.com|here\.com|waze\.com|yandex\.[a-z.]+/maps)[^\s<>"'()]+"#
        return try! NSRegularExpression(pattern: pattern, options: [.caseInsensitive])
    }()

    /// Extracts a supported map URL from arbitrary text (e.g. shared messenger messages or clipboard snippets).
    public static func extractMapUrl(from text: String?) -> String? {
        guard let text = text, !text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else {
            return nil
        }
        let range = NSRange(text.startIndex..<text.endIndex, in: text)
        if let match = urlRegex.firstMatch(in: text, options: [], range: range),
           let matchRange = Range(match.range, in: text) {
            var url = String(text[matchRange])
            url = url.trimmingCharacters(in: CharacterSet(charactersIn: ".,;!?)]>"))
            return url
        }
        return nil
    }

    /// Converts a map URL into a normalized ParsedMapLocation.
    public static func parse(appleUrl: String?) -> ParsedMapLocation {
        guard let rawUrl = appleUrl?.trimmingCharacters(in: .whitespacesAndNewlines), !rawUrl.isEmpty else {
            return ParsedMapLocation(type: .home)
        }

        let extracted = extractMapUrl(from: rawUrl) ?? rawUrl
        let normalized = normalizeUrl(extracted)

        guard let components = URLComponents(string: normalized) else {
            return ParsedMapLocation(type: .home)
        }

        var params: [String: String] = [:]
        for item in components.queryItems ?? [] {
            let key = item.name.lowercased().trimmingCharacters(in: .whitespaces)
            let value = item.value?.replacingOccurrences(of: "+", with: " ").trimmingCharacters(in: .whitespaces)
            params[key] = value
        }

        let host = components.host?.lowercased() ?? ""
        if host.contains("bing.com") {
            return parseBing(components: components, params: params, rawUrl: normalized)
        } else if host.contains("openstreetmap.org") || host.contains("osm.org") {
            return parseOsm(components: components, params: params, rawUrl: normalized)
        } else if host.contains("here.com") {
            return parseHere(components: components, params: params, rawUrl: normalized)
        } else if host.contains("waze.com") {
            return parseWaze(components: components, params: params, rawUrl: normalized)
        } else if host.contains("yandex.") {
            return parseYandex(components: components, params: params, rawUrl: normalized)
        }

        let saddr = params["saddr"]
        let daddr = params["daddr"]
        let dirflg = params["dirflg"]?.lowercased()

        let travelMode: TravelMode? = {
            switch dirflg {
            case "w": return .walking
            case "r": return .transit
            case "b": return .bicycling
            case "d": return .driving
            default: return nil
            }
        }()

        // 1. Directions with both origin and destination
        if let saddr = saddr, !saddr.isEmpty, let daddr = daddr, !daddr.isEmpty {
            return ParsedMapLocation(
                type: .directions(origin: saddr, destination: daddr, mode: travelMode)
            )
        }

        // 2. Navigation / Destination only
        if let daddr = daddr, !daddr.isEmpty {
            return ParsedMapLocation(
                type: .navigation(destination: daddr, mode: travelMode)
            )
        }

        // 3. Start address only
        if let saddr = saddr, !saddr.isEmpty {
            return ParsedMapLocation(type: .query(saddr))
        }

        // 4. Coordinates (ll, pt, coordinate, center)
        let rawCoords = params["ll"] ?? params["pt"] ?? params["coordinate"] ?? params["center"]
        if let coords = rawCoords?.replacingOccurrences(of: " ", with: ""), !coords.isEmpty {
            let searchQuery = params["q"] ?? params["address"] ?? params["near"] ?? params["name"]
            return ParsedMapLocation(
                type: .coordinates(coords: coords, query: searchQuery)
            )
        }

        // 5. Search query / address / auid / place name
        if let searchQuery = params["q"] ?? params["address"] ?? params["near"] ?? params["name"] ?? params["auid"],
           !searchQuery.isEmpty {
            return ParsedMapLocation(type: .query(searchQuery))
        }

        // 6. Short links or Place paths (without query)
        let path = components.path
        if path.contains("/p/") || path.contains("/place") || path.contains("/directions") {
            return ParsedMapLocation(type: .searchFallback(normalized))
        }

        // 7. Fallback: raw query or home
        if let query = components.percentEncodedQuery, !query.isEmpty {
            return ParsedMapLocation(type: .searchFallback(query))
        } else {
            return ParsedMapLocation(type: .home)
        }
    }

    private static func parseBing(components: URLComponents, params: [String: String], rawUrl: String) -> ParsedMapLocation {
        if let cp = params["cp"], cp.contains("~") {
            let coords = cp.replacingOccurrences(of: "~", with: ",")
            let query = params["where1"] ?? params["q"]
            return ParsedMapLocation(type: .coordinates(coords: coords, query: query))
        }
        if let rtp = params["rtp"] {
            let legs = rtp.components(separatedBy: "~")
            if legs.count >= 2 {
                let origin = legs.first?.replacingOccurrences(of: "pos.", with: "").replacingOccurrences(of: "adr.", with: "").replacingOccurrences(of: "_", with: ", ") ?? ""
                let dest = legs.last?.replacingOccurrences(of: "pos.", with: "").replacingOccurrences(of: "adr.", with: "").replacingOccurrences(of: "_", with: ", ") ?? ""
                return ParsedMapLocation(type: .directions(origin: origin, destination: dest, mode: nil))
            }
        }
        if let query = params["where1"] ?? params["q"] ?? params["q1"], !query.isEmpty {
            return ParsedMapLocation(type: .query(query))
        }
        return ParsedMapLocation(type: .searchFallback(rawUrl))
    }

    private static func parseOsm(components: URLComponents, params: [String: String], rawUrl: String) -> ParsedMapLocation {
        if let mlat = params["mlat"], let mlon = params["mlon"] {
            return ParsedMapLocation(type: .coordinates(coords: "\(mlat),\(mlon)", query: nil))
        }
        if let query = params["query"], !query.isEmpty {
            return ParsedMapLocation(type: .query(query))
        }
        if let frag = components.fragment, frag.contains("map=") {
            let parts = frag.replacingOccurrences(of: "map=", with: "").components(separatedBy: "/")
            if parts.count >= 3 {
                return ParsedMapLocation(type: .coordinates(coords: "\(parts[1]),\(parts[2])", query: nil))
            }
        }
        return ParsedMapLocation(type: .searchFallback(rawUrl))
    }

    private static func parseHere(components: URLComponents, params: [String: String], rawUrl: String) -> ParsedMapLocation {
        if let map = params["map"] {
            let parts = map.components(separatedBy: ",")
            if parts.count >= 2 {
                return ParsedMapLocation(type: .coordinates(coords: "\(parts[0]),\(parts[1])", query: nil))
            }
        }
        let path = components.path
        if path.contains("/l/") {
            let sub = path.components(separatedBy: "/l/").last ?? ""
            let parts = sub.components(separatedBy: ",")
            if parts.count >= 2 {
                let lat = parts[0]
                let lon = parts[1].components(separatedBy: "/").first ?? parts[1]
                let msg = params["msg"]
                return ParsedMapLocation(type: .coordinates(coords: "\(lat),\(lon)", query: msg))
            }
        }
        if path.contains("/search/") {
            let q = path.components(separatedBy: "/search/").last?.removingPercentEncoding ?? ""
            if !q.isEmpty {
                return ParsedMapLocation(type: .query(q))
            }
        }
        if let query = params["q"] ?? params["msg"], !query.isEmpty {
            return ParsedMapLocation(type: .query(query))
        }
        return ParsedMapLocation(type: .searchFallback(rawUrl))
    }

    private static func parseWaze(components: URLComponents, params: [String: String], rawUrl: String) -> ParsedMapLocation {
        if let ll = params["ll"] {
            let query = params["q"]
            return ParsedMapLocation(type: .coordinates(coords: ll, query: query))
        }
        if let q = params["q"], !q.isEmpty {
            return ParsedMapLocation(type: .query(q))
        }
        return ParsedMapLocation(type: .searchFallback(rawUrl))
    }

    private static func parseYandex(components: URLComponents, params: [String: String], rawUrl: String) -> ParsedMapLocation {
        if let rtext = params["rtext"] {
            let legs = rtext.components(separatedBy: "~")
            if legs.count >= 2 {
                return ParsedMapLocation(type: .directions(origin: legs.first ?? "", destination: legs.last ?? "", mode: nil))
            }
        }
        if let ll = params["ll"] {
            let parts = ll.components(separatedBy: ",")
            if parts.count == 2 {
                let lon = parts[0]
                let lat = parts[1]
                let text = params["text"]
                return ParsedMapLocation(type: .coordinates(coords: "\(lat),\(lon)", query: text))
            }
        }
        if let text = params["text"], !text.isEmpty {
            return ParsedMapLocation(type: .query(text))
        }
        return ParsedMapLocation(type: .searchFallback(rawUrl))
    }

    private static func normalizeUrl(_ url: String) -> String {
        var trimmed = url.trimmingCharacters(in: .whitespacesAndNewlines)
        if trimmed.lowercased().hasPrefix("applemaps://") {
            trimmed = "https://" + trimmed.dropFirst("applemaps://".count)
        }
        if !trimmed.lowercased().hasPrefix("http://") && !trimmed.lowercased().hasPrefix("https://") {
            trimmed = "https://\(trimmed)"
        }
        return trimmed.replacingOccurrences(of: " ", with: "%20")
    }
}

public enum TravelMode: String, Codable {
    case driving
    case walking
    case bicycling
    case transit
}

public struct ParsedMapLocation: Equatable {
    public enum LocationType: Equatable {
        case home
        case query(String)
        case coordinates(coords: String, query: String?)
        case navigation(destination: String, mode: TravelMode?)
        case directions(origin: String, destination: String, mode: TravelMode?)
        case searchFallback(String)
    }

    public let type: LocationType

    public init(type: LocationType) {
        self.type = type
    }
}
