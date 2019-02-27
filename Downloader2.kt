package downloadImage

import java.io.File
import java.net.URL
import javax.imageio.ImageIO

class Downloader2 {


    fun downloadUri(uri: String): String? {
        return try {
            URL(uri).readText()
        } catch (ex: Exception) {
            println("download $uri ")
            ex.printStackTrace()
            null
        }
    }

    fun downloadAndSaveImage(imageUri: String): Report? {
        val fileName = "${imageUri.replace("/", "").replace(".", "")}.jpg"
        return try {
            val image = ImageIO.read(URL(imageUri))
            val time = System.currentTimeMillis()
            val file = File(fileName)

            ImageIO.write(image, "jpg", file)
            println("download image $imageUri")

            Report(time, file.length(), image.width, image.height, imageUri, fileName)

        } catch (e: Exception) {
            println("not load image $imageUri")
            e.printStackTrace()
            null
        }
    }
}