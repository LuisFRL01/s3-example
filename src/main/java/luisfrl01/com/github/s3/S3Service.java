package luisfrl01.com.github.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class S3Service {

    @Value("${aws.region}")
    private String awsRegion;

    @Autowired
    private AmazonS3 amazonS3;

    public void createS3Bucket(String bucketName) {
        if(amazonS3.doesBucketExistV2(bucketName)) {
            log.info("Nome do bucket ja esta sendo usado.");
            return;
        }
        amazonS3.createBucket(new CreateBucketRequest(bucketName, awsRegion));
    }

    public List<Bucket> listBuckets(){
        return amazonS3.listBuckets();
    }

    public void deleteBucket(String bucketName){
        try {
            amazonS3.deleteBucket(bucketName);
        } catch (AmazonServiceException e) {
            log.error(e.getErrorMessage());
        }
    }

    public void putObject(String bucketName, BucketObject bucketObject) throws IOException {
        File file = new File(bucketObject.getObjectName());

        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(bucketObject.getText());
        fileWriter.close();

        amazonS3.putObject(bucketName, bucketObject.getObjectName(), file);
    }

    public void downloadObject(String bucketName, String objectName){
        S3Object s3object = amazonS3.getObject(bucketName, objectName);
        S3ObjectInputStream inputStream = s3object.getObjectContent();
        try {
            FileUtils.copyInputStreamToFile(inputStream, new File("." + File.separator + objectName));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void deleteObject(String bucketName, String objectName){
        amazonS3.deleteObject(bucketName, objectName);
    }

    public void moveObject(String bucketSourceName, String objectName, String bucketTargetName){
        amazonS3.copyObject(bucketSourceName, objectName, bucketTargetName, objectName);
    }
}
