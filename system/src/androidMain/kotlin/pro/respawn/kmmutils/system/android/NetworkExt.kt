@file:Suppress("unused")

package pro.respawn.kmmutils.system.android

import android.net.Uri
import androidx.core.net.MailTo

public val Uri.asHttps: Uri get() = if (scheme == "http") buildUpon().scheme("https").build() else this

public val Uri.isHttp: Boolean get() = scheme?.startsWith("http", true) == true

public val Uri.linkType: LinkType
    get() = when (this.scheme) {
        null -> LinkType.Unknown
        "http", "https" -> LinkType.Web
        "mailto" -> LinkType.Mail
        "tel" -> LinkType.Tel
        "blob" -> LinkType.Blob
        "content" -> LinkType.ContentProvider
        "dns" -> LinkType.Dns
        "drm" -> LinkType.Drm
        "fax" -> LinkType.Fax
        "geo" -> LinkType.Geo
        "magnet" -> LinkType.Magnet
        "maps" -> LinkType.Map
        "market" -> LinkType.GooglePlay
        "messgage" -> LinkType.AppleMail
        "mms", "sms" -> LinkType.TextMessage
        "query" -> LinkType.FilesystemQuery
        "resource" -> LinkType.Resource
        "skype", "callto" -> LinkType.Skype
        "ssh" -> LinkType.Ssh
        "webcal" -> LinkType.Calendar
        "file" -> LinkType.File
        else -> LinkType.Other
    }

public enum class LinkType {
    Web,
    Mail,
    Tel,
    Other,
    Unknown,
    Blob,
    ContentProvider,
    Dns,
    Drm,
    File,
    Calendar,
    Ssh,
    Skype,
    Resource,
    FilesystemQuery,
    TextMessage,
    AppleMail,
    GooglePlay,
    Map,
    Magnet,
    Geo,
    Fax
}

public data class Email(
    val recipients: List<String>? = null,
    val subject: String? = null,
    val body: String? = null,
) {

    public companion object {

        public operator fun invoke(uri: Uri): Email {
            val mail = MailTo.parse(uri)
            return Email(
                mail.to?.split(", "), mail.subject, mail.body
            )
        }
    }
}
