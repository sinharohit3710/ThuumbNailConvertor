/**
 * 
 */
package com.opentable.clients;



import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

/**
 * @author rsinha1
 *
 */

public class ClientFactory {
	
	AWSCredentials credentials;
	String accessId;
	String secretKey;
	
	public ClientFactory() {
		this.accessId = "AKIAIIDLILOGPXCDKALA";
		this.secretKey = "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";
		this.credentials =  new BasicAWSCredentials(accessId, secretKey);
	}
	
	public AmazonS3 getS3Client() {
		
		AmazonS3 s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.US_EAST_1).build();
		return s3client;
		
	}
	
	public AmazonSQS getSQSClient( ) {
		return AmazonSQSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.US_EAST_1).build();
	}

}
