package jetbrains.org.db.fake

import jetbrains.org.db.UsersRepository
import jetbrains.org.model.User

class FakeUsersRepository : UsersRepository {

    var storage = mutableListOf<User>()

    override suspend fun findAll(): List<User> = storage

    override suspend fun save(user: User) {
        storage.add(user)
    }

    override suspend fun find(id: Long): User? = storage.find {
        it.userId == id
    }

    override suspend fun update(user: User): Boolean {
        val u = find(user.userId)
        if (u != null) {
            storage.remove(u)
            storage.add(user)
            return true
        }
        return false
    }

}