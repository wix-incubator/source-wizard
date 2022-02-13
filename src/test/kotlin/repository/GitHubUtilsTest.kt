package repository

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class GitHubUtilsTest {
    private val httpsUrl  = "https://github.com/expressjs/express.git"
    private val sshUrl = "git@github.com:expressjs/express.git"


    @Test
    fun `convertToSshUrl should convert github repositories url to ssh url when https url is passed`() {
        Assertions.assertEquals(sshUrl, GitHubUtils.convertToSshUrl(httpsUrl))
    }

    @Test
    fun `convertToSshUrl should not change github repositories url when ssh url is passed`() {
        Assertions.assertEquals(sshUrl, GitHubUtils.convertToSshUrl(sshUrl))
    }
}