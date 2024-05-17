package jetbrains.org.db

import jetbrains.org.model.UserType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object UserTable : Table("users") {
    val id = long("id").autoIncrement()

    val name = varchar("name", 50).uniqueIndex()
    val email = text("email").uniqueIndex()
    val link = text("link").nullable()
    val aboutMe = text("about_me").nullable()
    val userType = enumeration<UserType>("user_type")

    override val primaryKey: Table.PrimaryKey = PrimaryKey(id)
}

object ContentTable : Table("content") {
    val contentId = long("contentId").uniqueIndex()

    val text = text("text")
    val author = reference("author_id", UserTable.id)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    override val primaryKey: Table.PrimaryKey = PrimaryKey(contentId)
}