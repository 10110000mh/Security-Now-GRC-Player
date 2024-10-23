package am.h10110000.securitynow.data

import am.h10110000.securitynow.highQulity

class EpisodeManager {
    companion object {
        fun formatEpisodeNumber(episodeNumber: Int): String {
            return String.format("%04d", episodeNumber)
        }

        fun generateEpisodeUrl(episodeNumber: Int): String {
            val formattedNumber = formatEpisodeNumber(episodeNumber)
            if (highQulity)
                return "https://twit.cachefly.net/audio/sn/sn$formattedNumber/sn$formattedNumber.mp3"
            return "https://media.grc.com/SN/sn-$episodeNumber-lq.mp3"

        }
    }
}