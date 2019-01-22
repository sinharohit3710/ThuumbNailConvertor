/**
 * 
 */
package com.opentable.Listeners;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.opentable.clients.ClientFactory;

import net.coobird.thumbnailator.Thumbnails;

/**
 * @author rsinha1
 *
 */
public class MySQSListener implements MessageListener {
	
    // Used to listen for message silence
    private volatile long timeOfLastMessage = System.nanoTime();
    final String sourceBucket = "restaurantpics";
    final String thumbnailBucket = "thumbnailsopentable";
    AmazonS3 s3client;
    
    public void waitForOneHourOfSilence() throws InterruptedException {
        for(;;) {
            long timeSinceLastMessage = System.nanoTime() - timeOfLastMessage;
            long remainingTillOneMinuteOfSilence = 
                TimeUnit.HOURS.toNanos(1) - timeSinceLastMessage;
            if( remainingTillOneMinuteOfSilence < 0 ) {
                break;
            }
            TimeUnit.NANOSECONDS.sleep(remainingTillOneMinuteOfSilence);
        }
    }
     

   


	public void onMessage(Message message) {
		try {
            
            message.acknowledge();
           
            String messageText = ((TextMessage) message).getText();
            
            int pictureNameStartIndex = messageText.indexOf("mainPictures");
            int pictureNameEndIndex = messageText.indexOf("\\", pictureNameStartIndex);
            String pictureName = messageText.substring(pictureNameStartIndex+13, pictureNameEndIndex);
           
            try {
				ResizeImage(sourceBucket,"mainPictures/"+pictureName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            timeOfLastMessage = System.nanoTime();
        } catch (JMSException e) {
            System.err.println( "Error processing message: " + e.getMessage() );
            e.printStackTrace();
        }
		
	}
	public void ResizeImage(String srcBucket, String srcKey  ) throws IOException, InterruptedException {
		ClientFactory clientFactory = new ClientFactory();
		s3client = clientFactory.getS3Client();
		System.out.println(srcKey);
		S3Object s3Object = s3client.getObject(new GetObjectRequest(srcBucket, srcKey));
		InputStream objectData = s3Object.getObjectContent();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Thumbnails.of(objectData).size(640, 480).toOutputStream(os);
		uploadPic(os,srcKey);
	}
	
	public void uploadPic(ByteArrayOutputStream  byteArrayOutputStream,String srcKey  ) {
		ClientFactory clientFactory = new ClientFactory();
		s3client = clientFactory.getS3Client();
		InputStream is = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		String fileName = "thumbnails/" + "thumsbnail."+srcKey.substring(13);
		ObjectMetadata meta = new ObjectMetadata();
		meta.setContentLength(byteArrayOutputStream.size());
		 s3client.putObject(new PutObjectRequest(thumbnailBucket, fileName, 
				is,meta).withCannedAcl(CannedAccessControlList.PublicRead));
		
		
	}
}

