package am.h10110000.securitynow.data

class EpisodeManager {
    companion object {
        fun formatEpisodeNumber(episodeNumber: Int): String {
            return String.format("%04d", episodeNumber)
        }

        fun generateEpisodeUrl(episodeNumber: Int): String {
            val formattedNumber = formatEpisodeNumber(episodeNumber)
            return "https://twit.cachefly.net/audio/sn/sn$formattedNumber/sn$formattedNumber.mp3"
        }
    }
}