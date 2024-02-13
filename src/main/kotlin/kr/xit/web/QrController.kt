package kr.xit.web

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import kr.xit.util.XingUtils
import kr.xit.util.convert
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

@RestController
class QrController {
    @PostMapping("/qr/file/upload")
    fun uploadQrCode(
        @RequestParam("uploadFiles")
        multipartFiles: List<MultipartFile>?,
    ): List<Map<String, Any>> {
        val rtn = mutableListOf<Map<String, Any>>()

        multipartFiles?.map { mf ->
            println(mf.originalFilename)
            println(mf.size)
            rtn.add(
                hashMapOf<String, Any>().apply {
                    put("qrcode", XingUtils.readQrcodeFromFile(mf.convert()))
                    put("name", mf.originalFilename!!)
                },
            )
        }
        return rtn
    }

    /**
     * qr code 생성
     * @param data : String
     * @param format : BarcodeFormat.QR_CODE|BarcodeFormat.CODE_128|BarcodeFormat.EAN_13
     * @param imgFormat : 생성할 qrcode image type PNG|JPG
     *
     * @return ResponseEntity<ByteArray> : 생성된 qrcode image
     */
    @GetMapping("/qr/create")
    @Throws(WriterException::class, IOException::class)
    fun createQrcode(
        data: String,
        format: BarcodeFormat = BarcodeFormat.QR_CODE,
        imgFormat: String = "PNG",
    ): ResponseEntity<ByteArray>? {
        // QR 정보
        val width: Int = 200
        val height: Int = 200

        // QR Code - BitMatrix: qr code 정보 생성
        val encode: BitMatrix =
            MultiFormatWriter()
                .encode(data, format, width, height)

        // QR Code - Image 생성. : 1회성으로 생성해야 하기 때문에
        // stream으로 Generate(1회성이 아니면 File로 작성 가능.)
        try {
            // output Stream
            val out: ByteArrayOutputStream = ByteArrayOutputStream()

            // Bitmatrix, file.format, outputStream
            MatrixToImageWriter.writeToStream(encode, "PNG", out)

            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(out.toByteArray())
        } catch (e: Exception) {
            e.printStackTrace()
            println("QR Code OutputStream 도중 Excpetion 발생, ${e.message}")
        }

        return null
    }

    // Image File Response
    @GetMapping("/qr/get/image")
    @Throws(IOException::class)
    fun name(): ResponseEntity<ByteArray> {
        // Image name - ImageFile to Byte[]
        val imageName = "img.png"
        val byteFile = getImageBye(imageName)

        // Header
        val headers: HttpHeaders = HttpHeaders()
        headers.add("Content-Type", "image/png")
        headers.add("Content-Length", byteFile!!.size.toString())

        // Return
        return ResponseEntity(byteFile, headers, HttpStatus.OK)
    }

    // Image to Byte[]
    @Throws(IOException::class)
    private fun getImageBye(imageName: String): ByteArray? {
        // image 경로 및 File
        val classPathResource =
            ClassPathResource(
                "static/images/$imageName",
            )
        val file: File = classPathResource.file

        var byteImage: ByteArray? = null

        var originalImage: BufferedImage? = null
        val out = ByteArrayOutputStream()

        originalImage = ImageIO.read(file)
        ImageIO.write(originalImage, "png", out)
        out.flush()

        byteImage = out.toByteArray()
        return byteImage
    }
}
