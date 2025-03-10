package com.example.com.domain.infraestructura


import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.imageio.ImageIO


class Utils {
    companion object{
        fun createBase64ToImg(img: String, nombre: String): String? {
            val groupExtension = listOf("jpg", "jpeg", "gif", "png")
            val regex = "data:image/([^;]+);base64,(.+)".toRegex()
            val result = regex.find(img)

            return if (result != null) {
                val type = result.groupValues[1]
                var ext: String = type.split("/")[1]
                val body = result.groupValues[2]

                // Verificar si la extensión es válida
                if (ext !in groupExtension) return null

                try {
                    if (ext == "jpg") ext = "jpeg"

                    val imgBytes = Base64.getDecoder().decode(body)
                    val inputStream = ByteArrayInputStream(imgBytes)
                    val bufferImage: BufferedImage = ImageIO.read(inputStream)
                    val path: String = ApplicationContext.context.environment.config.property("ktor.path.images").getString() + "/$nombre"
                    val dir = File(path)

                    if (dir.isDirectory) {
                        val nFile: String = path + "/"
                        val nameFile = nombre + "_" + SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) + ".$ext"
                        val fileImag = File(nFile + nameFile)
                        ImageIO.write(bufferImage, ext, fileImag)
                        return nameFile
                    } else {
                        return null
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    return null
                }
            } else {
                return null
            }
        }



        fun deleteImage(nombre: String, name: String):Boolean{
            try{
                val path = "${ApplicationContext.context.environment.config.property("ktor.path.images").getString()}/$nombre"
                val img = File(path, name)
                return if (img.exists()){
                    img.delete()
                    true
                }
                else
                    false
            }catch (e: Exception){
                e.printStackTrace()
                return false
            }
        }

        fun createDir(nombre: String) :Boolean{
            try{
                val path = ApplicationContext.context.environment.config.property("ktor.path.images").getString()
                val dir = File(path, nombre)
                return if (!dir.exists()){
                    val created = dir.mkdirs()
                    if (created)
                        true
                    else
                        false
                }
                else
                    false
            }catch (e:Exception){
                e.printStackTrace()
                return false
            }
        }

        fun deleteDirectory(nombre: String) : Boolean{
            try{
                val path = ApplicationContext.context.environment.config.property("ktor.path.images").getString()+"/$nombre"
                val dir = File(path)
                if (dir.exists()){
                    return dir.deleteRecursively()
                }

            }catch (e:Exception){
                e.printStackTrace()
                return false
            }
            return false
        }
    }

}