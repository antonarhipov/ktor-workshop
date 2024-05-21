package jetbrains.org.db


import jetbrains.org.model.Content
import jetbrains.org.model.User
import jetbrains.org.model.UserType
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

typealias LongId = EntityID<Long>

class UserDAO(id: LongId) : LongEntity(id) {
    companion object : LongEntityClass<UserDAO>(UserTable)

    var name by UserTable.name
    var email by UserTable.email
    var link by UserTable.link
    var aboutMe by UserTable.aboutMe
    var userType by UserTable.userType

    val contentItems by ContentDAO referrersOn ContentTable.author

    override fun toString(): String =
        "User(name='$name', email='$email')"
}

class ContentDAO(id: LongId) : LongEntity(id) {
    companion object : LongEntityClass<ContentDAO>(ContentTable)

    var text by ContentTable.text
    var createdAt by ContentTable.createdAt

    var author by UserDAO referencedOn ContentTable.author

    override fun toString(): String = "Content(text='$text', author=$author, createdAt=$createdAt)"
}

fun UserDAO.toUser() =
    User(
        userId = id.value,
        userType = UserType.REGISTERED,
        name = name,
        email = email,
        link = link,
        aboutMe = aboutMe,
        content = this.contentItems.map {
            Content(
                contentId = it.id.value,
                text = it.text,
                createdAt = it.createdAt
            )
        }.toMutableList()
    )
