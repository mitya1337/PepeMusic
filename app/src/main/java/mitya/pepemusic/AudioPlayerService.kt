package mitya.pepemusic

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.MediaDescriptionAdapter
import com.google.android.exoplayer2.ui.PlayerNotificationManager.NotificationListener
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class AudioPlayerService : Service() {

    private lateinit var track: Track
    private val player: SimpleExoPlayer by lazy { ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector()) }
    private val playerNotificationManager by lazy {
        PlayerNotificationManager.createWithNotificationChannel(this,
                PLAYBACK_CHANNEL_ID, R.string.playback_channel_name, PLAYBACK_NOTIFICATION_ID,
                object : MediaDescriptionAdapter {
                    override fun createCurrentContentIntent(player: Player?): PendingIntent? {
                        return null
                    }

                    override fun getCurrentContentText(player: Player?) = "keke"

                    override fun getCurrentContentTitle(player: Player?) = track.title

                    override fun getCurrentLargeIcon(player: Player?, callback: PlayerNotificationManager.BitmapCallback?): Bitmap? {
                        return null
                    }
                })
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        track = intent.getParcelableExtra("track")
        val dataSourceFactory = DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)))
        //val concatenatingMediaSource = ConcatenatingMediaSource()
        val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(track.contentUri)
        player.prepare(mediaSource)
        player.playWhenReady = true

        playerNotificationManager.setNotificationListener(object : NotificationListener {

            override fun onNotificationStarted(notificationId: Int, notification: Notification?) = startForeground(notificationId, notification)

            override fun onNotificationCancelled(notificationId: Int) = stopSelf()

        })
        playerNotificationManager.setPlayer(player)
        return START_STICKY
    }

    override fun onDestroy() {
        player.release()
        playerNotificationManager.setPlayer(null)
        super.onDestroy()
    }

    override fun onBind(p0: Intent?) = null

}