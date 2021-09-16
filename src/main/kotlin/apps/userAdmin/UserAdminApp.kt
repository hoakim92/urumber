package apps.userAdmin

import common.Urumber
import java.lang.Exception

@Urumber(
    appDescription = [
        "UserAdmin allows create users, create booking locations, setup permissions and validate them",
        "Permissions table consists of columns: user, region, location, company",
        "Grant access to regions, locations, companies",
        "Grant access to region",
        "Grant access to regions, locations",
        "Grant access to regions, locations, companies",
        "Validate permissions"
    ]
)
class UserAdminApp {
    enum class REGION { APAC, EU, USA }

    var users: List<String> = emptyList()
        get() = field
        set(value) {
            field = value
                .filterNot { it.contains("@") }
        }
    var locations: List<BookingLocation> = emptyList()
        get() = field
        set(value) {
            field = value
        }
    var companies: List<String> = emptyList()
        get() = field
        set(value) {
            field = value
        }
    var permissions: List<Permission> = emptyList()

    fun findLocation(region: REGION, location: String): BookingLocation{
        val location = locations.findLast { it.region == region && it.location == location }
        if (location != null) {
            return location!!
        } else{
            throw Exception("CANNOT FIND SUCH LOCATION")
        }
    }

    fun setPermissionsByRegions(user: String, regions: List<REGION>) {
        permissions = permissions.filterNot { it.user == user }
        permissions = permissions.plus(regions.map { r ->
            locations.filter { l -> l.region == r }.map { l ->
                companies.map { c -> Permission(user, r, l, c) }
            }
        }.flatten().flatten())
    }

    fun setPermissionsByRegionsAndLocations(user: String, regionsAndLocations: List<Pair<REGION, String>>) {
        permissions = permissions.filterNot { it.user == user }
        permissions = permissions.plus(regionsAndLocations.map { ral ->
            companies.map { c ->
                Permission(
                    user,
                    ral.first,
                    findLocation(ral.first, ral.second),
                    c
                )
            }
        }.flatten())
    }

    fun setPermissionsByRegionsLocationsAndCompanies(
        user: String,
        regionsLocationsAndCompanies: List<Map<String, Any>>
    ) {
        permissions = permissions.filterNot { it.user == user }
        permissions = permissions.plus(
            convertToPermissions(user, regionsLocationsAndCompanies)
        )
    }

    fun convertToPermissions(user: String, rows: List<Map<String, Any>>) =
        rows.map { row ->
            Permission(
                user,
                row["region"]!! as REGION,
                findLocation(row["region"]!! as REGION, row["location"]!!.toString()),
                companies.findLast { c -> c == row["company"]!!.toString() }!!
            )
        }

    data class BookingLocation(val location: String, val region: REGION)
    data class Permission(val user: String, val region: REGION, val location: BookingLocation, val company: String)
}