package com.example.asmobile.git

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File

class GitManager(private val rootDir: File) {

    fun clone(url: String, destination: String): Result<Unit> {
        return try {
            val destDir = File(rootDir, destination)
            if (destDir.exists()) {
                return Result.failure(Exception("Destination already exists"))
            }
            Git.cloneRepository()
                .setURI(url)
                .setDirectory(destDir)
                .call()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun commit(repoPath: String, message: String): Result<Unit> {
        return try {
            val git = Git.open(File(rootDir, repoPath))
            git.add().addFilepattern(".").call()
            git.commit().setMessage(message).call()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun push(repoPath: String, credentials: Pair<String, String>? = null): Result<Unit> {
        return try {
            val git = Git.open(File(rootDir, repoPath))
            val pushCommand = git.push()
            credentials?.let {
                pushCommand.setCredentialsProvider(UsernamePasswordCredentialsProvider(it.first, it.second))
            }
            pushCommand.call()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun pull(repoPath: String, credentials: Pair<String, String>? = null): Result<Unit> {
        return try {
            val git = Git.open(File(rootDir, repoPath))
            val pullCommand = git.pull()
            credentials?.let {
                pullCommand.setCredentialsProvider(UsernamePasswordCredentialsProvider(it.first, it.second))
            }
            pullCommand.call()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
