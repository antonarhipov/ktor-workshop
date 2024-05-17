package jetbrains.org.db.fake

import jetbrains.org.db.UsersRepository
import jetbrains.org.model.User

class FakeUsersRepository : UsersRepository {

    var storage = mutableListOf<User>()

    override suspend fun findAll(): List<User> = storage

    override suspend fun save(user: User): Int {
        storage.add(user)
        return 1
    }

    override suspend fun find(id: Long): User? = storage.find {
        it.userId == id
    }

    override suspend fun delete(user: User): Int {
        storage.remove(user)
        return 1
    }

}