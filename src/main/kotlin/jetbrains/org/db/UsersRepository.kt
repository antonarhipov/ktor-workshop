package jetbrains.org.db

import jetbrains.org.model.User
import jetbrains.org.model.UserType
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.dsl.module

val usersDataModule = module {
    single<UsersRepository> { UsersRepositoryImpl() }
}

interface UsersRepository {
    suspend fun findAll(): List<User>
    suspend fun save(user: User): Int
    suspend fun find(id: Long): User?
    suspend fun delete(user: User): Int
}

class UsersRepositoryImpl : UsersRepository {

    override suspend fun findAll(): List<User> = suspendTransaction {
        UserTable.selectAll().map { row: ResultRow ->
            User(
                userId = row[UserTable.id],
                userType = UserType.REGISTERED,
                name = row[UserTable.name],
                email = row[UserTable.email],
                link = row[UserTable.link],
                aboutMe = row[UserTable.aboutMe]
            )
        }
    }

    override suspend fun save(user: User): Int = suspendTransaction {
        UserTable.insert { row ->
            row[UserTable.id] = user.userId
            row[UserTable.name] = user.name
            row[UserTable.link] = user.link
            row[UserTable.email] = user.email
            row[UserTable.aboutMe] = user.aboutMe
        }
    }.insertedCount

    override suspend fun find(id: Long): User? = suspendTransaction {
        UserTable.selectAll().where { UserTable.id eq id }.singleOrNull()?.let { row ->
            User(
                userId = row[UserTable.id],
                userType = UserType.REGISTERED,
                name = row[UserTable.name],
                link = row[UserTable.link],
                email = row[UserTable.email],
                aboutMe = row[UserTable.aboutMe]
            )
        }
    }

    override suspend fun delete(user: User): Int = suspendTransaction {
        UserTable.deleteWhere { UserTable.id eq user.userId }
    }

}

suspend fun <T> suspendTransaction(block: suspend () -> T): T =
    newSuspendedTransaction(Dispatchers.IO) { block() }


