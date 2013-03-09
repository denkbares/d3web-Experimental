package com.denkbares.ciconnector;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

public class Uploader {

	public static void main(String[] args) throws IOException {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(
				"http://d3web.de/action/SaveCIInfoAction");
		MultipartEntity entity = new MultipartEntity();
		String example = "src/main/resources/example.xml";
		entity.addPart("file", new FileBody(new File(example)));
		post.setEntity(entity);

		// HttpResponse response =
		httpclient.execute(post);
	}

}