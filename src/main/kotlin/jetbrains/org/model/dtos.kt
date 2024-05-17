package jetbrains.org.model

import kotlinx.serialization.Serializable


@Serializable
data class User(
    val userId: Int,
    val userType: UserType,
    val displayName: String,
    val link: String,
    val aboutMe: String? = null
)

enum class UserType {
    REGISTERED,
    MODERATOR,
}