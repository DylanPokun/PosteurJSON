package dap.PosteurJSON;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class App {
	public static void main(String[] args) {
		// JSON parser object to parse read file
		JSONParser jsonParser = new JSONParser();

		try (FileReader reader = new FileReader("sendMe.json")) {
			// Read JSON file
			Object obj = jsonParser.parse(reader);

			// Parse Array to JSON
			JSONArray jsonArray = (JSONArray) obj;
			// System.out.println(jsonArray);

			// Iterate over json array
			for (Object jsonObject : jsonArray) {
				try {
					// Initial casting
					JSONObject currentJson = (JSONObject) jsonObject;

					// Get "Headers" object
					JSONObject headersJson = (JSONObject) currentJson.get("Headers");
					// System.out.println(headersJson);

					// Get "Headers"'s content
					String accept = (String) headersJson.get("Accept");
					String contentType = (String) headersJson.get("Content-Type");
					String authorization = (String) headersJson.get("Authorization");

					// Get other conf
					String url = (String) currentJson.get("URL");
					// System.out.println(url);
					String method = (String) currentJson.get("Method");
					// System.out.println(method);

					// Get Body
					JSONObject bodyJson = (JSONObject) currentJson.get("Body");
					// System.out.println(headersJson);

					// HTTP-Post
					CloseableHttpClient httpclient = HttpClients.createDefault();
					// Init
					if (method.trim().toLowerCase().equals("post")) {
						HttpPost post = new HttpPost(url);

						// Headers
						post.setHeader("Content-Type", contentType);
						post.setHeader("Accept", accept);
						post.setHeader("Authorization", authorization);

						// Body
						String jsonRequestBody = bodyJson.toString();
						HttpEntity entity = new ByteArrayEntity(jsonRequestBody.getBytes("UTF-8"));
						post.setEntity(entity);

						// Exec
						CloseableHttpResponse response = httpclient.execute(post);
						System.out.println(response.getStatusLine());
						
						// Closing
						response.close();
					} else if (method.trim().toLowerCase().equals("get")) {
						HttpGet get = new HttpGet(url);
						// Headers
						get.setHeader("Content-Type", contentType);
						get.setHeader("Accept", accept);
						get.setHeader("Authorization", authorization);
						// Exec
						CloseableHttpResponse response = httpclient.execute(get);

						// Output
						System.out.println(response.getStatusLine());
						HttpEntity entity = response.getEntity();
						String responseBody = EntityUtils.toString(entity, "UTF-8");
						System.out.println(responseBody.toString());

						// Closing
						response.close();
					}

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					// Interruption du traitement
					System.out.println(HttpStatus.SC_METHOD_FAILURE);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
