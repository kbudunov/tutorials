import java.io._

import AmazonS3Example.bucketName
import com.amazonaws.auth._
import com.amazonaws.services.s3._
import com.amazonaws.services.s3.model._
import com.amazonaws.util.IOUtils

object AmazonS3Example extends App {

  val cephServer = StringConfigParameter(
    "ceph-server",
    "Host:Port of CEPH",
    Some("http://000.000.000.000:80")
  )

  val cephAccessKey = StringConfigParameter(
    "ceph-accessKey",
    "CEPH Access Key",
    Some("00000000000000000")
  )

  val cephSecretKey = StringConfigParameter(
    "ceph-secretKey",
    "CEPH Secret Key",
    Some("0000000000000000000000000000000000000")
  )

  val accessKey = //"0000000000000000000"
  val secretKey = //"0000000000000000000000000000000000000"
  val bucketName = "new-test-bucket"
  val credentials = new BasicAWSCredentials(accessKey, secretKey)

  val client = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey))
  client.withEndpoint("http://000.000.000.000:80")

  def downloadFromS3(uploadPath: String, downloadPath: String) {
    client.getObject(new GetObjectRequest(bucketName, uploadPath),
      new File(downloadPath))
  }

  def createBucket(bucketName: String) {
    client.createBucket(bucketName)
  }

  def deleteBucket(bucketName: String) {
    client.deleteBucket(bucketName)
  }

  def putObject(bucketName: String, key: String, input: InputStream, metadata: ObjectMetadata) {
    client.putObject(new PutObjectRequest(bucketName, key, input, metadata))
  }

  def getObject(bucketName: String, key: String): S3ObjectInputStream = {
    client.getObject(new GetObjectRequest(bucketName, key)).getObjectContent
  }

  def listObjects(request: ListObjectsRequest) {
    client.listObjects(request)
  }

  def deleteObject(bucketName: String, key: String) {
    client.deleteObject(bucketName, key)
  }

  def deleteObjects(request: DeleteObjectsRequest) {
    client.deleteObjects(request)
  }


  //downloadFromS3("", "hello.txt")
  //createBucket("test2")
  //println(getObject(bucketName, "hello.txt"))

 def readObject(bucketName: String, fileName: String): String = {
   IOUtils.toString(client.getObject(new GetObjectRequest(bucketName, fileName)).getObjectContent)
 }


println(readObject(bucketName, "brick.jpg"))
}

