import Foundation

/// Converts Apple Maps URLs to compatible navigation / map URIs.
public enum AppleMapsParser {

    private static let urlRegex: NSRegularExpression = {
        let pattern = #"(?:https?://|applemaps://|maps\.apple\.com)[^\s<>"'()]+"#
        return try! NSRegularExpression(pattern: pattern, options: [.caseInsensitive])
    }()

    /// Extracts an Apple Maps URL from arbitrary text (e.g. shared messenger messages or clipboard snippets).
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

    /// Converts an Apple Maps URL into a normalized ParsedMapLocation.
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
