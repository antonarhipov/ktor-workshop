package jetbrains.org.db

import org.jetbrains.exposed.sql.Table

object UserTable : Table("users") {
    val id = long("id").autoIncrement()

    val name = varchar("name", 50).uniqueIndex()
    val email = text("email").uniqueIndex()
    val link = text("link").nullable()
    val aboutMe = text("about_me").nullable()

    override val primaryKey: Table.PrimaryKey = PrimaryKey(id)
}