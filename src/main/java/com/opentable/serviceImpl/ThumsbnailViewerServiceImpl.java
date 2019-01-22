package com.opentable.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.opentable.clients.ClientFactory;

public class ThumsbnailViewerServiceImpl {
	
	AmazonS3 s3client;
	final String thumbnailBucket = "thumbnailsopentable";
	
	public List<String> getImageUrls() {
		List<String> urlList = new ArrayList<String>();
		ClientFactory clientFactory = new ClientFactory();
		s3client = clientFactory.getS3Client();
		ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(thumbnailBucket);
		ListObjectsV2Result result;

      
            result = s3client.listObjectsV2(req);

            for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                System.out.printf(" - %s (size: %d)\n", objectSummary.getKey(), objectSummary.getSize());
                urlList.add("https://s3.amazonaws.com/thumbnailsopentable/"+objectSummary.getKey());
            }
            
            return urlList;

      
	}

}
