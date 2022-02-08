package plugin

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import infra.NpmRegistry
import npmmodules.DefinitelyTypedUtils

internal class DefinitelyTypedUtilsTest {
    private val moduleName = "express"
    private val npmRegistry = mockk<NpmRegistry>()
    private val definitelyTypedUtils = DefinitelyTypedUtils(npmRegistry)

    @Test
    fun `isDefinitelyTypedModule should return true when given url is definitely type repository url`() {
        val result =
            definitelyTypedUtils.isDefinitelyTypedModule("https://github.com/DefinitelyTyped/DefinitelyTyped.git")

        Assertions.assertTrue(result)
    }

    @Test
    fun `isDefinitelyTypedModule should return false when given url is not definitely type repository url`() {
        val result =
            definitelyTypedUtils.isDefinitelyTypedModule("https://github.com/kotlin/Kotlin.git")

        Assertions.assertFalse(result)
    }

    @Test
    fun `findActualRepositoryUrl should return actual repository url for given type`() {
        val repositoryUrl = "https://github.com/kotlin/Kotlin.git"
        every { npmRegistry.getRepositoryUrl(any()) } returns repositoryUrl

        val result = definitelyTypedUtils.findActualRepositoryUrl(moduleName)

        Assertions.assertEquals(result, repositoryUrl)
        verify { npmRegistry.getRepositoryUrl(moduleName) }
    }
}