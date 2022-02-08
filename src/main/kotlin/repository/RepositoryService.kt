package repository

interface RepositoryService {
    fun cloneRepository(repositoryUrl: String): String
    fun pullRepository(repositoryUrl: String)
}