package jetbrains.org.model

import kotlinx.serialization.Serializable


@Serializable
data class User(
    val userId: Long,
    val userType: UserType,
    val name: String,
    val email: String,
    val link: String? = null,
    val aboutMe: String? = null
)

enum class UserType {
    REGISTERED,
    MODERATOR,
}