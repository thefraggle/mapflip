import Foundation

/// Supported navigation apps on iOS
public enum TargetApp: String, CaseIterable, Identifiable, Codable {
    case googleMaps = "google_maps"
    case waze = "waze"
    case organicMaps = "organic_maps"
    case osmAnd = "osmand"
    case appleMaps = "apple_maps"

    public var id: String { rawValue }

    public var displayName: String {
        switch self {
        case .googleMaps: return "Google Maps"
        case .waze: return "Waze"
        case .organicMaps: return "Organic Maps"
        case .osmAnd: return "OsmAnd Maps"
        case .appleMaps: return "Apple Maps"
        }
    }

    public var iconName: String {
        switch self {
        case .googleMaps: return "map.fill"
        case .waze: return "car.fill"
        case .organicMaps: return "leaf.fill"
        case .osmAnd: return "map"
        case .appleMaps: return "applelogo"
        }
    }

    public var appStoreUrl: URL? {
        switch self {
        case .googleMaps:
            return URL(string: "https://apps.apple.com/app/id585027354")
        case .waze:
            return URL(string: "https://apps.apple.com/app/id323229106")
        case .organicMaps:
            return URL(string: "https://apps.apple.com/app/id1567437057")
        case .osmAnd:
            return URL(string: "https://apps.apple.com/app/id934850377")
        case .appleMaps:
            return nil
        }
    }
}

/// Builds URL schemes for target navigation apps.
public enum UrlSchemeBuilder {

    public static func buildUrl(for location: ParsedMapLocation, target: TargetApp) -> URL {
        switch target {
        case .googleMaps:
            return buildGoogleMapsUrl(for: location)
        case .waze:
            return buildWazeUrl(for: location)
        case .organicMaps:
            return buildOrganicMapsUrl(for: location)
        case .osmAnd:
            return buildOsmAndUrl(for: location)
        case .appleMaps:
            return buildAppleMapsUrl(for: location)
        }
    }

    private static func buildGoogleMapsUrl(for location: ParsedMapLocation) -> URL {
        switch location.type {
        case .home:
            return URL(string: "comgooglemaps://") ?? URL(string: "https://www.google.com/maps")!

        case .query(let query):
            let encoded = query.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? query
            return URL(string: "comgooglemaps://?q=\(encoded)") ?? URL(string: "https://www.google.com/maps/search/?api=1&query=\(encoded)")!

        case .coordinates(let coords, let query):
            if let query = query, !query.isEmpty {
                let encodedQ = query.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? query
                return URL(string: "comgooglemaps://?q=\(encodedQ)&center=\(coords)") ?? URL(string: "https://www.google.com/maps/search/?api=1&query=\(encodedQ)")!
            } else {
                return URL(string: "comgooglemaps://?q=\(coords)&center=\(coords)") ?? URL(string: "https://www.google.com/maps/search/?api=1&query=\(coords)")!
            }

        case .navigation(let destination, let mode):
            let encodedDest = destination.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? destination
            var urlStr = "comgooglemaps://?daddr=\(encodedDest)"
            if let mode = mode {
                urlStr += "&directionsmode=\(mode.rawValue)"
            }
            return URL(string: urlStr) ?? URL(string: "https://www.google.com/maps/dir/?api=1&destination=\(encodedDest)")!

        case .directions(let origin, let destination, let mode):
            let encodedOrig = origin.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? origin
            let encodedDest = destination.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? destination
            var urlStr = "comgooglemaps://?saddr=\(encodedOrig)&daddr=\(encodedDest)"
            if let mode = mode {
                urlStr += "&directionsmode=\(mode.rawValue)"
            }
            return URL(string: urlStr) ?? URL(string: "https://www.google.com/maps/dir/?api=1&origin=\(encodedOrig)&destination=\(encodedDest)")!

        case .searchFallback(let fallback):
            let encoded = fallback.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? fallback
            return URL(string: "comgooglemaps://?q=\(encoded)") ?? URL(string: "https://www.google.com/maps/search/?api=1&query=\(encoded)")!
        }
    }

    private static func buildWazeUrl(for location: ParsedMapLocation) -> URL {
        switch location.type {
        case .home:
            return URL(string: "waze://") ?? URL(string: "https://www.waze.com")!
        case .coordinates(let coords, _):
            return URL(string: "waze://?ll=\(coords)&navigate=yes") ?? URL(string: "https://www.waze.com/ul?ll=\(coords)&navigate=yes")!
        case .query(let query):
            let encoded = query.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? query
            return URL(string: "waze://?q=\(encoded)&navigate=yes") ?? URL(string: "https://www.waze.com/ul?q=\(encoded)&navigate=yes")!
        case .navigation(let destination, _):
            let encoded = destination.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? destination
            return URL(string: "waze://?q=\(encoded)&navigate=yes") ?? URL(string: "https://www.waze.com/ul?q=\(encoded)&navigate=yes")!
        case .directions(_, let destination, _):
            let encoded = destination.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? destination
            return URL(string: "waze://?q=\(encoded)&navigate=yes") ?? URL(string: "https://www.waze.com/ul?q=\(encoded)&navigate=yes")!
        case .searchFallback(let fallback):
            let encoded = fallback.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? fallback
            return URL(string: "waze://?q=\(encoded)") ?? URL(string: "https://www.waze.com/ul?q=\(encoded)")!
        }
    }

    private static func buildOrganicMapsUrl(for location: ParsedMapLocation) -> URL {
        switch location.type {
        case .coordinates(let coords, let query):
            let parts = coords.split(separator: ",")
            if parts.count == 2 {
                let lat = parts[0]
                let lon = parts[1]
                let name = query?.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? ""
                return URL(string: "om://map?v=1&ll=\(lat),\(lon)&n=\(name)") ?? URL(string: "om://")!
            }
            return URL(string: "om://")!
        case .query(let query), .navigation(let query, _), .directions(_, let query, _):
            let encoded = query.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? query
            return URL(string: "om://search?query=\(encoded)") ?? URL(string: "om://")!
        default:
            return URL(string: "om://")!
        }
    }

    private static func buildOsmAndUrl(for location: ParsedMapLocation) -> URL {
        switch location.type {
        case .coordinates(let coords, _):
            let parts = coords.split(separator: ",")
            if parts.count == 2 {
                return URL(string: "osmandmaps://?lat=\(parts[0])&lon=\(parts[1])&z=16") ?? URL(string: "osmandmaps://")!
            }
            return URL(string: "osmandmaps://")!
        case .query(let query), .navigation(let query, _), .directions(_, let query, _):
            let encoded = query.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? query
            return URL(string: "osmandmaps://?q=\(encoded)") ?? URL(string: "osmandmaps://")!
        default:
            return URL(string: "osmandmaps://")!
        }
    }

    private static func buildAppleMapsUrl(for location: ParsedMapLocation) -> URL {
        switch location.type {
        case .home:
            return URL(string: "maps://")!
        case .query(let query):
            let encoded = query.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? query
            return URL(string: "maps://?q=\(encoded)")!
        case .coordinates(let coords, let query):
            if let query = query, !query.isEmpty {
                let encoded = query.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? query
                return URL(string: "maps://?ll=\(coords)&q=\(encoded)")!
            }
            return URL(string: "maps://?ll=\(coords)")!
        case .navigation(let destination, let mode):
            let encoded = destination.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? destination
            var urlStr = "maps://?daddr=\(encoded)"
            if let mode = mode {
                urlStr += "&dirflg=\(mode.rawValue.prefix(1))"
            }
            return URL(string: urlStr)!
        case .directions(let origin, let destination, let mode):
            let encodedOrig = origin.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? origin
            let encodedDest = destination.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? destination
            var urlStr = "maps://?saddr=\(encodedOrig)&daddr=\(encodedDest)"
            if let mode = mode {
                urlStr += "&dirflg=\(mode.rawValue.prefix(1))"
            }
            return URL(string: urlStr)!
        case .searchFallback(let fallback):
            let encoded = fallback.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? fallback
            return URL(string: "maps://?q=\(encoded)")!
        }
    }
}
