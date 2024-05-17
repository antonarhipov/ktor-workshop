package jetbrains.org.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Content(
    val contentId: Long,
    val text: String,
    val createdAt: LocalDateTime,
)

@Serializable
data class User(
    val userId: Long,
    val userType: UserType,
    val name: String,
    val email: String,
    val link: String? = null,
    val aboutMe: String? = null,
    val content: MutableList<Content> = mutableListOf()
)

enum class UserType {
    REGISTERED,
    MODERATOR,
}