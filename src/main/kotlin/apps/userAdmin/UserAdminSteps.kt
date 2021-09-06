package apps.userAdmin

import common.Urumber
import io.ktor.http.*
import kotlin.test.assertEquals

@Urumber(
    defaultSteps = [
        "Create users \r\n |   user   | \r\n | testUser |",
        "Validate users \r\n |   user   | \r\n | testUser |",
        "Create locations \r\n | region | location | \r\n |  USA   | NewYork  |",
        "Create companies \r\n | company | \r\n |   DB    |",
        "Grant permissions for testUser by regions \r\n | region | \r\n |  USA   |",
        "Validate permissions for testUser  \r\n | region | location | company | \r\n |  USA   |  NewYork |   DB    |"
    ]
)
class UserAdminSteps {
    private val app = UserAdminApp()

    @Urumber(
        stepRegExp = "^Create users",
        stepDescription = "Create users from table",
        stepView = "Create users DataTable"
    )
    fun setUsers(users: List<Map<String, String>>) {
        app.users = users.map { it["user"]!! }
    }

    @Urumber(
        stepRegExp = "^Create locations",
        stepDescription = "Create locations from table",
        stepView = "Create locations DataTable"
    )
    fun setLocations(locations: List<Map<String, String>>) {
        app.locations = locations.map {
            UserAdminApp.BookingLocation(
                it["location"]!!,
                UserAdminApp.REGION.valueOf(it["region"]!!)
            )
        }
    }

    @Urumber(
        stepRegExp = "^Create companies",
        stepDescription = "Create locations from table",
        stepView = "Create locations DataTable"
    )
    fun setCompanies(companies: List<Map<String, String>>) {
        app.companies = companies.map { it["company"]!! }
    }

    @Urumber(
        stepRegExp = "^Validate users",
        stepDescription = "Validate users by table",
        stepView = "Validate users DataTable"
    )
    fun validateUsers(users: List<Map<String, String>>) {
        assertEquals(users.map { it["user"]!! }.toSortedSet().joinToString(), app.users.toSortedSet().joinToString())
    }

    @Urumber(
        stepRegExp = "^Validate locations",
        stepDescription = "Validate locations by table",
        stepView = "Validate locations DataTable"
    )
    fun validateLocations(locations: List<Map<String, String>>) {
        assertEquals(locations.map {
            UserAdminApp.BookingLocation(
                it["location"]!!,
                UserAdminApp.REGION.valueOf(it["region"]!!)
            )
        }.joinToString(), app.locations.joinToString())
    }

    @Urumber(
        stepRegExp = "^Validate companies",
        stepDescription = "Validate companies by table",
        stepView = "Validate companies DataTable"
    )
    fun validateCompanies(companies: List<Map<String, String>>) {
        assertEquals(
            companies.map { it["company"]!! }.toSortedSet().joinToString(),
            app.companies.toSortedSet().joinToString()
        )
    }

    @Urumber(
        stepRegExp = "^Grant permissions for (\\w+) by (regions|regionsAndLocations|regionsLocationsAndCompanies)",
        stepDescription = "Grant permissions for user by table",
        stepView = "Grant permissions for user by \r\n (regions|regionsAndLocations|regionsLocationsAndCompanies)"
    )
    fun grantPermissions(user: String, permissionsType: String, params: List<Map<String, String>>) {
        when (permissionsType) {
            "regions" -> app.setPermissionsByRegions(user, params.map { UserAdminApp.REGION.valueOf(it["region"]!!) })
            "regionsAndLocations" -> app.setPermissionsByRegionsAndLocations(
                user,
                params.map { UserAdminApp.REGION.valueOf(it["region"]!!.trim()) to it["location"]!! })
            "regionsLocationsAndCompanies" -> app.setPermissionsByRegionsLocationsAndCompanies(
                user,
                params.map {
                    it.mapValues { e ->
                        if (e.key == "region") {
                            UserAdminApp.REGION.valueOf(e.value)
                        } else {
                            e.value
                        }
                    }
                })
        }
    }

    @Urumber(
        stepRegExp = "^Validate permissions for (\\w+)",
        stepDescription = "Validate permissions by table",
        stepView = "Validate permissions DataTable"
    )
    fun validatePermissions(user: String, params: List<Map<String, String>>) {
        fun validatePermissionsForUser(user: String, regionsLocationsAndCompanies: List<Map<String, Any>>) {
            val actual = app.permissions.filter { it.user == user }
            val expected = app.convertToPermissions(user, regionsLocationsAndCompanies)
            assertEquals(
                actual.joinToString(),
                expected.joinToString()
            )
        }

        validatePermissionsForUser(user, params.map {
            it.mapValues { e ->
                if (e.key == "region") {
                    UserAdminApp.REGION.valueOf(e.value)
                } else {
                    e.value
                }
            }
        })
    }
}