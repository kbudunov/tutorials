import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;


public class AmazonS3ClientBuilder {
    public static void main(String[] args) {
        String accessKey = "00000000000000000";
        String secretKey = "00000000000000000000000000000000000";
        try {

            ClientConfiguration clientConfig = new ClientConfiguration();
            clientConfig.setProtocol(Protocol.HTTP);
            System.setProperty(SDKGlobalConfiguration.DISABLE_CERT_CHECKING_SYSTEM_PROPERTY, "false");
            if (SDKGlobalConfiguration.isCertCheckingDisabled()) {
                System.out.println("Cert checking is disabled");
            }
            AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            AmazonS3 conn = new AmazonS3Client(credentials);
            conn.setEndpoint("http://000.000.000.000:80");

            //CREATING A BUCKET
            //conn.createBucket("test-bucket");

            //LISTING OWNED BUCKETS
            List<Bucket> buckets = getListOfBuckets(conn);

            Bucket bucket = buckets.get(1);

            //CREATING AN OBJECT
            //createAnnObject(conn, bucket, "test.txt", "Hello world!!!");

            //LISTING A BUCKET’S CONTENT
            getBuckedContent(conn, bucket);

            //DOWNLOAD AN OBJECT (TO A FILE)
            //downloadObject(conn, bucket, "hello.txt");

            //GENERATE OBJECT DOWNLOAD URLS (SIGNED AND UNSIGNED)¶
           // generateDownloadUrls(conn, bucket, "hello.txt");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //LISTING OWNED BUCKETS
    private static List<Bucket> getListOfBuckets(AmazonS3 conn) {
        List<Bucket> buckets = conn.listBuckets();
        for (Bucket bucket : buckets) {
            System.out.println(bucket.getName() + "\t" +
                    StringUtils.fromDate(bucket.getCreationDate()));
        }
        return buckets;
    }

    //CREATING AN OBJECT
    private static void createAnnObject(AmazonS3 conn, Bucket bucket, String fileName, String textMsg) {
        ByteArrayInputStream input = new ByteArrayInputStream(textMsg.getBytes());
        conn.putObject(bucket.getName(), fileName, input, new ObjectMetadata());
    }

    //LISTING A BUCKET’S CONTENT
    private static void getBuckedContent(AmazonS3 conn, Bucket bucket) {
        ObjectListing objects = conn.listObjects(bucket.getName());
        do {
            for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
                System.out.println(objectSummary.getKey() + "\t" +
                        objectSummary.getSize() + "\t" +
                        StringUtils.fromDate(objectSummary.getLastModified()));
            }
            objects = conn.listNextBatchOfObjects(objects);
        } while (objects.isTruncated());
    }

    //DOWNLOAD AN OBJECT (TO A FILE)
    private static void downloadObject(AmazonS3 conn, Bucket bucket, String fileName) {
        conn.getObject(
                new GetObjectRequest(bucket.getName(), fileName),
                new File(fileName)
        );
    }

    //GENERATE OBJECT DOWNLOAD URLS (SIGNED AND UNSIGNED)
    private static void generateDownloadUrls(AmazonS3 conn, Bucket bucket, String fileName) {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket.getName(), fileName);
        System.out.println(conn.generatePresignedUrl(request));
    }
}