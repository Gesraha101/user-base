package com.spark.userbase.feature.add_user.data.source.local

import com.google.common.truth.Truth.assertThat
import com.spark.userbase.feature.add_user.data.model.UserEntity
import com.spark.userbase.feature.add_user.data.source.local.room.UserDao
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AddUserLocalDataSourceImplTest {

    @MockK lateinit var dao: UserDao

    private lateinit var dataSource: AddUserLocalDataSourceImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dataSource = AddUserLocalDataSourceImpl(dao)
    }

    // region insertUser

    @Test
    fun `insertUser delegates to dao insertUser`() = runTest {
        val entity = UserEntity(id = 0, name = "John", age = 25, jobTitle = "Dev", gender = "Male")
        coEvery { dao.insertUser(entity) } just runs

        dataSource.insertUser(entity)

        coVerify { dao.insertUser(entity) }
    }

    // endregion

    // region getAllUsers

    @Test
    fun `getAllUsers delegates to dao getAllUsers`() = runTest {
        val entities = listOf(
            UserEntity(id = 1, name = "Alice", age = 28, jobTitle = "Designer", gender = "Female")
        )
        every { dao.getAllUsers() } returns flowOf(entities)

        val result = dataSource.getAllUsers().first()

        assertThat(result).isEqualTo(entities)
    }

    @Test
    fun `getAllUsers returns empty list when dao has no users`() = runTest {
        every { dao.getAllUsers() } returns flowOf(emptyList())

        val result = dataSource.getAllUsers().first()

        assertThat(result).isEmpty()
    }

    // endregion
}
