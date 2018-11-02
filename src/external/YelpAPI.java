package external;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item.ItemBuilder;
import entity.Item;

public class YelpAPI {
	private static final String HOST = "https://api.yelp.com";
	private static final String ENDPOINT = "/v3/businesses/search";
	private static final String DEFAULT_TERM = "";
	private static final int SEARCH_LIMIT = 20;

	private static final String TOKEN_TYPE = "Bearer";
	private static final String API_KEY = "ZNI18LqvE5WclThhkUgh-xC8yRUdEncoBHlR7d-P8iv9DfmBQTQECxlYZK5jl975xr9f4oi5noUMY8a_GNSvLaUCZVqHvJ0fvKyiMC4PizjMSQLYC7qrhDMmYsuyW3Yx";

	
	 public List<Item> search(double lat, double lon, String term) {
	 		if (term == null || term.isEmpty()) {
	 			term = DEFAULT_TERM;
	 		}
	 		
	 		try {
				term = URLEncoder.encode(term, "UTF-8"); // Rick Sun => Rick20%Sun
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
	 		
	 		String query = String.format("term=%s&latitude=%s&longitude=%s&limit=%s", term, lat, lon, SEARCH_LIMIT);
	 		String url = HOST + ENDPOINT + "?" + query;
	 		
	 		try {
				HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Authorization", TOKEN_TYPE + " " + API_KEY);
				
				int responseCode = connection.getResponseCode();
				System.out.println("Sending request to URL: " + url);
				System.out.println("Response code: " + responseCode);
				
				if (responseCode != 200) {
					return new ArrayList<>();
				}
						
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine = "";
				StringBuilder response = new StringBuilder();
				
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				
				JSONObject obj = new JSONObject(response.toString());
				if (!obj.isNull("businesses")) {
					return getItemList(obj.getJSONArray("businesses"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
	 			
			return new ArrayList<>();
		
	    }

 	
		/**
		 * Helper methods
		 */
		// Convert JSONArray to a list of item objects.
		private List<Item> getItemList(JSONArray restaurants) throws JSONException {
			List<Item> list = new ArrayList<>();

			for (int i = 0; i < restaurants.length(); ++i) {
				JSONObject restaurant = restaurants.getJSONObject(i);
				ItemBuilder builder = new ItemBuilder();
				
				if (!restaurant.isNull("id")) {
				    builder.setItemId(restaurant.getString("id"));
				}
				if (!restaurant.isNull("name")) {
				    builder.setName(restaurant.getString("name"));
				}
				if (!restaurant.isNull("url")) {
				    builder.setUrl(restaurant.getString("url"));
				}
				if (!restaurant.isNull("image_url")) {
				    builder.setImageUrl(restaurant.getString("image_url"));
				}
				if (!restaurant.isNull("rating")) {
				    builder.setRating(restaurant.getDouble("rating"));
				}
				if (!restaurant.isNull("distance")) {
				    builder.setDistance(restaurant.getDouble("distance"));
				}
				
				builder.setAddress(getAddress(restaurant));
				builder.setCategories(getCategories(restaurant));
				
				list.add(builder.build());
			}

			return list;
		}

		private Set<String> getCategories(JSONObject restaurant) throws JSONException {
			Set<String> categories = new HashSet<>();

			if (!restaurant.isNull("categories")) {
				JSONArray array = restaurant.getJSONArray("categories");
				for (int i = 0; i < array.length(); ++i) {
					JSONObject category = array.getJSONObject(i);
					if (!category.isNull("alias")) {
						categories.add(category.getString("alias"));
					}
				}
			}

			return categories;
		}

		private String getAddress(JSONObject restaurant) throws JSONException {
			String address = "";

			if (!restaurant.isNull("location")) {
				JSONObject location = restaurant.getJSONObject("location");
				if (!location.isNull("display_address")) {
					JSONArray displayAddress = location.getJSONArray("display_address");
					address = displayAddress.join(",");
				}
			}

			return address;
		}



		private void queryAPI(double lat, double lon) {
			List<Item> items = search(lat, lon, null);
			for (Item item : items) {
				JSONObject jObject = item.toJSONObject();
				System.out.println(jObject);
			}
		}

		/**
		 * Main entry for sample Yelp API requests.
		 */
		public static void main(String[] args) {
			YelpAPI tmApi = new YelpAPI();
			tmApi.queryAPI(37.38, -122.08);
		}
  

}
