package com.blueduck.annotator.encryption

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.util.Assertions
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSourceInputStream
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.TransferListener
import java.io.IOException
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.spec.SecretKeySpec

@UnstableApi
class EncryptedDataSource(upstream: DataSource, val password: String) : DataSource {

    private var upstream: DataSource? = upstream
    private var cipherInputStream: CipherInputStream? = null

    override fun open(dataSpec: DataSpec): Long {
        val key = EncryptionAndDecryption.passwordTo256ByteArray(password)
        val cipher = Cipher.getInstance(EncryptionAndDecryption.TRANSFORMATION);
        val skeySpec = SecretKeySpec(key, EncryptionAndDecryption.ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, skeySpec)
        val inputStream = DataSourceInputStream(upstream!!, dataSpec)
        cipherInputStream = CipherInputStream(inputStream, cipher)
        inputStream.open()
        return C.LENGTH_UNSET.toLong()

    }

    override fun read(buffer: ByteArray, offset: Int, readLength: Int): Int {
        Assertions.checkNotNull<Any>(cipherInputStream)
        val bytesRead = cipherInputStream!!.read(buffer, offset, readLength)
        return if (bytesRead < 0) {
            C.RESULT_END_OF_INPUT
        } else bytesRead
    }

    override fun addTransferListener(transferListener: TransferListener) {

    }

    override fun getUri(): Uri? {
        return upstream!!.uri
    }

    @Throws(IOException::class)
    override fun close() {
        if (cipherInputStream != null) {
            cipherInputStream = null
            upstream!!.close()
        }
    }
}