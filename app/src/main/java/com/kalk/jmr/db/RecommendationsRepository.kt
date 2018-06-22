package com.kalk.jmr.db

class RecommendationsRepository() {

    companion object {
        private var sInstance: RecommendationsRepository? = null
        fun getInstance() = sInstance ?:
        synchronized(this) {
            sInstance ?: buildRepository().also { sInstance = it }
        }

        private fun buildRepository(): RecommendationsRepository {
            return RecommendationsRepository()
        }
    }
}