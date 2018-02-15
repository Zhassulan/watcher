package kz.ugs.callisto.system.files.watcher.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;

import kz.ugs.callisto.system.files.watcher.model.FileModel;
import kz.ugs.callisto.system.files.watcher.system.FileManager;
import kz.ugs.callisto.system.files.watcher.system.MyLogger;

public class WebServiceClient {

	private Client client;
	private String url;
	private FileModel fileModel;

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public boolean createPostRequest(FileModel fileModel) {
		MyLogger.getLogger().info("Trying to create post request to url " + this.getUrl() + " for model {" + fileModel.getHostName() + ", " + fileModel.getFileName()
		+ ", " + fileModel.getbFile().getTotalSpace() + "}");
		this.fileModel = fileModel; 
		ClientConfig config = new ClientConfig();
		// config.getClasses().add(MultiPartWriter.class);
		config.register(MultiPartFeature.class);
		config.register(MultiPartWriter.class);
		Client client = ClientBuilder.newClient(config);

		// WebTarget webTarget = client.target(url);
		// WebTarget fileWebTarget = webTarget.path("file/upload");
		// Invocation.Builder invocationBuilder =
		// fileWebTarget.request(MediaType.APPLICATION_JSON);
		try {
			// MyLogger.getLogger().info(fileModel.getFileName());
			// Response response = invocationBuilder.post(Entity.entity(fileModel,
			// MediaType.APPLICATION_JSON));

			FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("data", fileModel.getbFile(), MediaType.APPLICATION_OCTET_STREAM_TYPE);
			fileDataBodyPart.setContentDisposition(FormDataContentDisposition.name("data").fileName(fileModel.getFileName()).build());

			MultiPart multiPart = new FormDataMultiPart().field("hostName", fileModel.getHostName())
					.field("fileName", fileModel.getFileName())
					// .field("data", fileModel.getData(), MediaType.APPLICATION_OCTET_STREAM_TYPE)
					.bodyPart(fileDataBodyPart);
			// Response response =
			// client.target(url).request(MediaType.APPLICATION_JSON).post(Entity.entity(fileModel,
			// MediaType.APPLICATION_JSON));
			Response response = client.target(url).request(MediaType.MULTIPART_FORM_DATA_TYPE)
					.post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA_TYPE));
			// Response response =
			// client.target(url).request(MediaType.MULTIPART_FORM_DATA_TYPE).post(ClientResponse.class,
			// multiPart);

			MyLogger.getLogger().info("HTTP status " + response.getStatus());
			if (response.getStatus() == 200)	{
				
				return true;
			}	else	{
				return false;
			}
			
		} catch (Exception e) {
			MyLogger.getLogger().error(e.getMessage(), e);
		}
		return true;
	}

	public FileModel getFileModel() {
		return fileModel;
	}

	public void setFileModel(FileModel fileModel) {
		this.fileModel = fileModel;
	}
	
	public boolean createPostRequestParams(String url, String hostName, String fileName, String data) {
		
		HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);
        
        //post.addHeader("Content-Type","application/x-www-form-urlencoded");
        
        // Create some NameValuePair for HttpPost parameters
        List<NameValuePair> arguments = new ArrayList<>(3);
        arguments.add(new BasicNameValuePair("hostName", hostName));
        arguments.add(new BasicNameValuePair("fileName", fileName));
        arguments.add(new BasicNameValuePair("data", data));

        try {
            post.setEntity(new UrlEncodedFormEntity(arguments));
            HttpResponse response = client.execute(post);
            // Print out the response message
            MyLogger.getLogger().info("Response: " + EntityUtils.toString(response.getEntity()));
            return true;
        } catch (OutOfMemoryError e)	{
        	MyLogger.getLogger().error(e.getMessage(), e);
        }	catch (IOException e) {
            MyLogger.getLogger().error(e.getMessage(), e);
        }
        return false;
	}

}
