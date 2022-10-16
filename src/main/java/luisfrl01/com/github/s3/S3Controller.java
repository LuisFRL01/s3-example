package luisfrl01.com.github.s3;

import com.amazonaws.services.s3.model.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/s3")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    @PostMapping("/{bucketName}")
    public void createBucket(@PathVariable String bucketName){
        s3Service.createS3Bucket(bucketName);
    }

    @GetMapping
    public List<String> listBuckets(){
        var buckets = s3Service.listBuckets();
        return buckets.stream().map(Bucket::getName).collect(Collectors.toList());
    }

    @DeleteMapping("/{bucketName}")
    public void deleteBucket(@PathVariable String bucketName){
        s3Service.deleteBucket(bucketName);
    }

    @DeleteMapping("/{bucketName}/object/{objectName}")
    public void deleteBucketObkect(@PathVariable String bucketName,
                                   @PathVariable String objectName){
        s3Service.deleteObject(bucketName, objectName);
    }

    @PostMapping("/{bucketName}/objects")
    public void createObject(@PathVariable String bucketName,
                             @RequestBody BucketObject bucketObject) throws IOException {
        s3Service.putObject(bucketName, bucketObject);
    }

    @GetMapping("/{bucketName}/objects/{objectName}")
    public File downloadObject(@PathVariable String bucketName, @PathVariable String objectName) throws IOException {
        s3Service.downloadObject(bucketName, objectName);
        return new File("./" + objectName);
    }

    @PatchMapping("/{bucketName}/objects/{objectName}/{bucketTarget}")
    public void moveObject(@PathVariable String bucketName, @PathVariable String objectName,
                           @PathVariable String bucketTarget) throws IOException {
        s3Service.moveObject(bucketName, objectName, bucketTarget);
    }
}
