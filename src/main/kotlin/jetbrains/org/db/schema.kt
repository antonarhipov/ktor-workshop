package jetbrains.org.db

import jetbrains.org.model.UserType
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UserTable : LongIdTable("users") {
    val name = varchar("name", 50).uniqueIndex()
    val email = text("email").uniqueIndex()
    val link = text("link").nullable()
    val aboutMe = text("about_me").nullable()
    val userType = enumeration<UserType>("user_type")
}

object ContentTable : IdTable<Long>("content") {
    override val id = long("contentId").uniqueIndex().entityId()

    val text = text("text")
    val author = reference("author_id", UserTable)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id)
}