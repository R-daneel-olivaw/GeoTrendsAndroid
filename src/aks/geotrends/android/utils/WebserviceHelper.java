package aks.geotrends.android.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import aks.geotrends.android.json.JsonRegionalTrending;

public class WebserviceHelper {

	private static final String BASE_WEBSERVICE = "http://ec2-52-88-201-34.us-west-2.compute.amazonaws.com:43094/geotrendws/trending/";

	public JsonRegionalTrending fetchKeyowrdForRegion(RegionsEnum reg) throws JsonParseException, JsonMappingException, IOException {

			HttpClient httpClient = new DefaultHttpClient();

			HttpGet getReq = prepareKeywordRequestForRegion(reg);
			HttpResponse httpResponse = httpClient.execute(getReq);

			HttpEntity httpEntity = httpResponse.getEntity();
			// Read content & Log
			InputStream inputStream = httpEntity.getContent();

			ObjectMapper mapper = new ObjectMapper();
			JsonRegionalTrending regionalTrending = mapper.readValue(inputStream, JsonRegionalTrending.class);

			return regionalTrending;
	}

	private HttpGet prepareKeywordRequestForRegion(RegionsEnum reg) {
		HttpGet getReq = new HttpGet(BASE_WEBSERVICE + reg.getRegion());
		getReq.setHeader("Content-Type", "application/json; charset=utf-8");

		return getReq;
	}

}
