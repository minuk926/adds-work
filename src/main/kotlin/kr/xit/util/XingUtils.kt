package kr.xit.util

import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.MultiFormatWriter
import com.google.zxing.NotFoundException
import com.google.zxing.Result
import com.google.zxing.WriterException
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO

// https://www.geeksforgeeks.org/how-to-generate-and-read-qr-code-with-java-using-zxing-library/

/**
 * MultipartFile -> File로 변환
 */
fun MultipartFile.convert(): File {
    val name = this.originalFilename.toString()

    val ext = name.substring(name.lastIndexOf("."))
    val filename = name.substring(0, name.lastIndexOf("."))
    val path = Files.createTempFile(filename, ext)

    this.transferTo(path)
    return path.toFile()
}

object XingUtils {
    const val QR_CODE_PATH = "d:/temp/qr/"
    private val hashMap: MutableMap<EncodeHintType, ErrorCorrectionLevel> =
        EnumMap(com.google.zxing.EncodeHintType::class.java)

    init {
        hashMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L
    }

    /**
     *     //BarcodeFormat.QR_CODE,
     *     //BarcodeFormat.CODE_128,
     *     //BarcodeFormat.EAN_13,
     */
    @Throws(WriterException::class, IOException::class)
    fun createQR(
        format: BarcodeFormat,
        data: String,
        path: String,
        charset: String? = StandardCharsets.UTF_8.name(),
        height: Int,
        width: Int,
    ) {
        val matrix =
            MultiFormatWriter().encode(
                String(data.toByteArray(charset(charset!!)), charset(charset!!)),
                format,
                width,
                height,
            )

        val filePath = Paths.get("$QR_CODE_PATH/$path")

        MatrixToImageWriter.writeToPath(
            matrix,
            path.substring(path.lastIndexOf('.') + 1),
            filePath,
        )
    }

    @Throws(FileNotFoundException::class, IOException::class, NotFoundException::class)
    fun readQrcodeFromPath(
        path: String?, // charset: String? = StandardCharsets.UTF_8.name(),
        // map: Map<*, *>? = hashMap,
    ): String {
        val binaryBitmap =
            BinaryBitmap(
                HybridBinarizer(
                    BufferedImageLuminanceSource(
                        ImageIO.read(
                            FileInputStream(path),
                        ),
                    ),
                ),
            )

        val result: Result =
            MultiFormatReader().decode(binaryBitmap)

        return result.text
    }

    @Throws(FileNotFoundException::class, IOException::class, NotFoundException::class)
    fun readQrcodeFromFile(file: File?): String {
        val binaryBitmap =
            BinaryBitmap(
                HybridBinarizer(
                    BufferedImageLuminanceSource(
                        ImageIO.read(
                            file,
                        ),
                    ),
                ),
            )

        val result: Result =
            MultiFormatReader().decode(binaryBitmap)

        return result.text
    }
}

@Throws(WriterException::class, IOException::class, NotFoundException::class)
fun main() {
    // The data that the QR code will contain

    var data = "qrcode_1234"

    // The path where the image will get saved
    var path: String = "qr_code.png"

    // Encoding charset
    val charset = "UTF-8"

    val hashMap: MutableMap<EncodeHintType, ErrorCorrectionLevel> =
        HashMap()

    hashMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L

    // BarcodeFormat.QR_CODE,
    // BarcodeFormat.CODE_128,
    // BarcodeFormat.EAN_13,
    XingUtils.createQR(BarcodeFormat.QR_CODE, data, path, charset, 200, 200)
    println("QR Code Generated!!! ")

    // Path where the QR code is saved
    val path2 = "${XingUtils.QR_CODE_PATH}/$path"

    println(
        "QRCode output: " +
            XingUtils.readQrcodeFromPath(path2),
    )
}
