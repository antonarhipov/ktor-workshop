package jetbrains.org.db

import jetbrains.org.model.User
import org.koin.dsl.module

val usersDataModule = module {
    single<UsersRepository> { UsersRepositoryImpl() }
}

interface UsersRepository {
    fun findAll(): List<User>
    fun save(user: User): Boolean
    fun find(id: Int): User?
    fun delete(user: User): Boolean
}

class UsersRepositoryImpl : UsersRepository {
    override fun findAll(): List<User> {
        TODO("Not yet implemented")
    }

    override fun save(user: User): Boolean {
        TODO("Not yet implemented")
    }

    override fun find(id: Int): User? {
        TODO("Not yet implemented")
    }

    override fun delete(user: User): Boolean {
        TODO("Not yet implemented")
    }
}