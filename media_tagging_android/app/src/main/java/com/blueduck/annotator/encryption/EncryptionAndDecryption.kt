package com.blueduck.annotator.encryption

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.core.content.FileProvider
import com.blueduck.annotator.BuildConfig
import com.blueduck.annotator.util.Constant
import com.blueduck.annotator.util.createImageFile
import com.blueduck.annotator.util.createVideoFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.spec.SecretKeySpec


class EncryptionAndDecryption {
    companion object {
         const val ALGORITHM = "AES"
         const val TRANSFORMATION = "AES/ECB/PKCS5Padding"


        fun encrypt(password: String, imageUri: Uri, file: File, context: Context) {

            val key = passwordTo256ByteArray(password)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val secretKey = SecretKeySpec(key, ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val inputStream = context.contentResolver.openInputStream(imageUri)
            val outputStream = FileOutputStream(file)

            val buffer = ByteArray(1024)
            var count = inputStream!!.read(buffer)
            while (count != -1) {
                outputStream.write(cipher.update(buffer, 0, count))
                count = inputStream.read(buffer)
            }
            outputStream.write(cipher.doFinal())

            inputStream.close()
            outputStream.close()
        }

        fun encryptPassword(password: String): String {
            val key = passwordTo256ByteArray(BuildConfig.AES_TOKEN)
            val secretKey = SecretKeySpec(key, ALGORITHM)

            val cipher: Cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val encryptedBytes = cipher.doFinal(password.toByteArray())

            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        }

        fun decryptPassword(password: String): String {
            val key = passwordTo256ByteArray(BuildConfig.AES_TOKEN)
            val secretKey = SecretKeySpec(key, ALGORITHM)

            val cipher: Cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            val decodedBytes = Base64.decode(password, Base64.DEFAULT)

            val decryptedBytes = cipher.doFinal(decodedBytes)

            return String(decryptedBytes, Charsets.UTF_8)
        }


        fun decrypt(password: String, encryptedImageUri: Uri, context: Context): ByteArray {
            val key = passwordTo256ByteArray(password)

            val inputStream = context.contentResolver.openInputStream(encryptedImageUri)
            val encryptedImage = inputStream?.readBytes()
            inputStream?.close()

            val secretKey = SecretKeySpec(key, ALGORITHM)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKey)

            return cipher.doFinal(encryptedImage)
        }


        fun passwordTo256ByteArray(password: String): ByteArray {
            val digest = MessageDigest.getInstance("SHA-256")
            return digest.digest(password.toByteArray(Charsets.UTF_8))
        }


        fun encryptVideoFile(videoUri: Uri, outputFile: File, password: String, context: Context) {
            val key = passwordTo256ByteArray(password)

            val inputStream = context.contentResolver.openInputStream(videoUri)
            val output = FileOutputStream(outputFile)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val secretKey = SecretKeySpec(key, ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val cos = CipherOutputStream(output, cipher)
            val buffer = ByteArray(1024)

            var length: Int

            while (inputStream!!.read(buffer).also { length = it } != -1) {
                cos.write(buffer, 0, length)
            }

            cos.flush()
            cos.close()
            inputStream.close()
        }

        fun decryptVideoFile(videoUri: Uri, outputFile: File, password: String, context: Context) {
            val key = passwordTo256ByteArray(password)

            val inputStream = context.contentResolver.openInputStream(videoUri)
            val output = FileOutputStream(outputFile)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val secretKey = SecretKeySpec(key, ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secretKey)

            val cos = CipherOutputStream(output, cipher)
            val buffer = ByteArray(1024)

            var length: Int

            while (inputStream!!.read(buffer).also { length = it } != -1) {
                cos.write(buffer, 0, length)
            }

            cos.flush()
            cos.close()
            inputStream.close()
        }


        fun encryptImage(thumbImageUri: Uri, fileEncryptionPassword: String, context: Context): Uri {
            val thumbImageEncryptedFile = createImageFile(context)
            encrypt(
                decryptPassword(fileEncryptionPassword),
                thumbImageUri,
                thumbImageEncryptedFile,
                context
            )
            return FileProvider.getUriForFile(
                context,
                "${context.applicationContext.packageName}${Constant.PROVIDER}",
                thumbImageEncryptedFile
            )
        }

        fun encryptVideo(videoUri: Uri, fileEncryptionPassword: String, context: Context) : Uri{
            val encryptedFile = createVideoFile(context = context)
            val password = decryptPassword(fileEncryptionPassword)
            encryptVideoFile( videoUri, encryptedFile, password, context)
            return FileProvider.getUriForFile(context, "${context.applicationContext.packageName}${Constant.PROVIDER}", encryptedFile)
        }


        fun decryptPdfFile(password: String, encryptedPdfFile: File, outputFile: File) {
            val key = passwordTo256ByteArray(password)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val secretKey = SecretKeySpec(key, ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secretKey)

            val inputStream = FileInputStream(encryptedPdfFile)
            val outputStream = FileOutputStream(outputFile)

            val buffer = ByteArray(1024)
            var count = inputStream.read(buffer)

            while (count != -1) {
                outputStream.write(cipher.update(buffer, 0, count))
                count = inputStream.read(buffer)
            }

            outputStream.write(cipher.doFinal())

            inputStream.close()
            outputStream.close()
        }


    }




}
