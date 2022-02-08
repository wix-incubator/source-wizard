package repository

object GitHubUtils {

    private fun getSshUrl(organization: String, repositoryName: String) = "git@github.com:$organization/$repositoryName"

    fun convertToSshUrl(githubUrl: String): String {
        if (githubUrl.startsWith("git")) {
            return githubUrl
        }

        val (organization, repositoryName) =
            Regex(".*/(.*)/(.*)").find(githubUrl)!!.destructured
        return getSshUrl(organization, repositoryName)
    }
}