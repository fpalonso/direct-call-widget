package dev.ferp.dcw.data.phones

import com.google.common.truth.Truth.assertThat
import dev.ferp.dcw.data.phones.source.PhoneDataSource
import dev.ferp.dcw.data.phones.source.device.TestPhoneProvider
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DevicePhoneRepositoryTest {

    private lateinit var dataSource: PhoneDataSource
    private lateinit var repo: PhoneRepository

    @Before
    fun setUp() {
        dataSource = mockk()
        repo = DevicePhoneRepository(dataSource)
    }

    @Test
    fun `getPhoneListByLookUpKey returns phone list`() = runTest {
        // Given
        coEvery {
            dataSource.getPhoneList(any())
        } returns TestPhoneProvider.nonBlankNumbersPhoneList()

        // When
        val phones = repo.getPhoneListByLookUpKey("lookUpKey")

        // Then
        assertThat(phones).isEqualTo(TestPhoneProvider.nonBlankNumbersPhoneList())
    }

    @Test
    fun `getPhoneListByLookUpKey returns empty list if data source throws an exception`() = runTest {
        // Given
        coEvery {
            dataSource.getPhoneList(any())
        } throws Exception("Something went wrong")

        // When
        val phones = repo.getPhoneListByLookUpKey("lookUpKey")

        // Then
        assertThat(phones).isEmpty()
    }
}