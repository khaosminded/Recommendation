package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import db.MySQLConnection;
import entity.Item;

public class GeoRecommendation {
	public List<Item> recommendItems(String userId, double lat, double lon) {
		List<Item> recommendedItems = new ArrayList<>();
		MySQLConnection connection = new MySQLConnection();
		
		try {
			// Step 1, get all favorited itemids
			Set<String> favoriteItemIds = connection.getFavoriteItemIds(userId);
			// Step 2, get all categories, sort by count
			Map<String, Integer> allCategories = new HashMap<>();
			for (String itemId : favoriteItemIds) {
				Set<String> categories = connection.getCategories(itemId);
				for (String category : categories) {
					allCategories.put(category, 1 + allCategories.getOrDefault(category, 0));
				}
				
			}
			
			List<Entry<String, Integer>> categoryList= new ArrayList<>(allCategories.entrySet());
			Collections.sort(categoryList,(Entry<String, Integer> e1, Entry<String, Integer> e2) -> {
				return Integer.compare(e1.getValue(), e2.getValue());
			});
			// Step 3, search based on category, filter out favorite items
			
			Set<String> visited = new HashSet<>();
			for (Entry<String, Integer> entry : categoryList) {
				List<Item> items = connection.searchItems(lat, lon, entry.getKey());
				for (Item item : items) {
					if (!favoriteItemIds.contains(item.getItemId()) && !visited.contains(item.getItemId())) {
						recommendedItems.add(item);
						visited.add(item.getItemId());
					}
					//limit our maximum result
					if (recommendedItems.size() >= 20) {
						return recommendedItems;
					}
				}
				
			} 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}

		
		return recommendedItems;
  }

}
