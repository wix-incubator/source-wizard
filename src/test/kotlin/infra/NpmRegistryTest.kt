package infra

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class NpmRegistryTest {

    @Test
    fun `getRepositoryUrl should return repository url for given npm module`() {
        val url = NpmRegistry().getRepositoryUrl("express")

        assertEquals(
            "https://github.com/expressjs/express.git",
            url,
            "integration with https://api.npms.io/ registry failed"
        )
    }
}