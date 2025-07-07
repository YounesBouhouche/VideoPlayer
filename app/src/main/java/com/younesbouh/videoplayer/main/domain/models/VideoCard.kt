package com.younesbouh.videoplayer.main.domain.models

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.room.Entity
import androidx.room.PrimaryKey

@Immutable
@Entity
data class VideoCard(
    var contentUri: Uri,
    @PrimaryKey(autoGenerate = false)
    var id: Long,
    var title: String,
    var cover: Bitmap?,
    var coverByteArray: ByteArray,
    var artist: String,
    var album: String,
    var path: String,
    var date: Long,
    var size: Long,
    var duration: Long,
    var progress: Float,
    var favorite: Boolean,
    var lastPlayed: Long?
) {
    @Suppress("unused")
    class Builder {
        private var contentUri: Uri = Uri.EMPTY
        private var id: Long = -1
        private var title: String = ""
        private var cover: Bitmap? = null
        private var coverByteArray: ByteArray = byteArrayOf()
        private var artist: String = ""
        private var albumId: Long = -1
        private var album: String = ""
        private var genre: String = ""
        private var composer: String = ""
        private var lyrics: String = ""
        private var path: String = ""
        private var date: Long = 0
        private var size: Long = 0
        private var duration: Long = -1
        private var progress: Float = 0f
        private var favorite: Boolean = false
        private var lastPlayed: Long? = null

        fun setContentUri(contentUri: Uri) = apply { this.contentUri = contentUri }

        fun setId(id: Long) = apply { this.id = id }

        fun setTitle(title: String) = apply { this.title = title }

        fun setCover(cover: Bitmap?) = apply { this.cover = cover }

        fun setCoverByteArray(coverByteArray: ByteArray) = apply { this.coverByteArray = coverByteArray }

        fun setArtist(artist: String) = apply { this.artist = artist }

        fun setAlbumId(albumId: Long) = apply { this.albumId = albumId }

        fun setAlbum(album: String) = apply { this.album = album }

        fun setGenre(genre: String) = apply { this.genre = genre }

        fun setComposer(composer: String) = apply { this.composer = composer }

        fun setLyrics(lyrics: String) = apply { this.lyrics = lyrics }

        fun setPath(path: String) = apply { this.path = path }

        fun setDate(date: Long) = apply { this.date = date }

        fun setSize(size: Long) = apply { this.size = size }

        fun setDuration(duration: Long) = apply { this.duration = duration }

        fun setProgress(progress: Float) = apply { this.progress = progress }

        fun setFavorite(favorite: Boolean) = apply { this.favorite = favorite }

        fun setTimestamps(lastPlayed: Long?) = apply { this.lastPlayed = lastPlayed }

        fun build() =
            VideoCard(
                contentUri,
                id,
                title,
                cover,
                coverByteArray,
                artist,
                album,
                path,
                date,
                size,
                duration,
                progress,
                favorite,
                lastPlayed,
            )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VideoCard

        if (contentUri != other.contentUri) return false
        if (id != other.id) return false
        if (cover != other.cover) return false
        if (!coverByteArray.contentEquals(other.coverByteArray)) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (album != other.album) return false
        if (path != other.path) return false
        if (date != other.date) return false
        if (duration != other.duration) return false
        if (favorite != other.favorite) return false
        if (lastPlayed != other.lastPlayed) return false

        return true
    }

    override fun hashCode(): Int {
        var result = contentUri.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + (cover?.hashCode() ?: 0)
        result = 31 * result + coverByteArray.contentHashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + progress.hashCode()
        result = 31 * result + favorite.hashCode()
        result = 31 * result + lastPlayed.hashCode()
        return result
    }

    fun toMediaItem() =
        MediaItem
            .Builder()
            .setUri(contentUri)
            .setMediaId("$id")
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumTitle(album)
                    .setArtist(artist)
                    .setArtworkData(coverByteArray, MediaMetadata.PICTURE_TYPE_MEDIA)
                    .build(),
            )
            .build()
}
