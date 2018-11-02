package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

import entity.Item;
import external.YelpAPI;

public class MySQLConnection {
	private Connection connection;
	
	public  MySQLConnection() {
		try{
			
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			connection = DriverManager.getConnection(MySQLDBUtil.URL);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	public void close() {
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<Item> searchItems(double lat, double lon, String term) {
		YelpAPI api = new YelpAPI();
		List<Item> items = api.search(lat, lon, term);
		
		for (Item item : items) {
			saveItem(item);
		}
		
		return items;
	}
	
	public void saveItem(Item item) {
		if (connection == null) {
			System.err.println("DB connection failed");
			return;
		}
		
		try {
			String sql = "INSERT IGNORE INTO items VALUES (?,?,?,?,?,?,?)";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, item.getItemId());
			preparedStatement.setString(2, item.getName());
			preparedStatement.setDouble(3, item.getRating());
			preparedStatement.setString(4, item.getAddress());
			preparedStatement.setString(5, item.getUrl());
			preparedStatement.setString(6, item.getImageUrl());
			preparedStatement.setDouble(7, item.getDistance());
			
			preparedStatement.execute();
			
			sql = "INSERT IGNORE INTO catergories VALUES (?, ?)";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, item.getItemId());
			for (String category : item.getCategories()) {
				preparedStatement.setString(2, category);
				preparedStatement.execute();
			}
				
		} catch (Exception e) {
			System.err.println("saveItem() exception!");
			e.printStackTrace();
		}
	}
	public void setFavoriteItems(String userId, List<String> itemIds) {
        if (connection == null) {
            System.err.println("DB connection failed");
            return;
        }

        try {
            String sql = "INSERT IGNORE INTO history(user_id, item_id) VALUES (?, ?)";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userId);
            for (String itemId : itemIds) {
                ps.setString(2, itemId);
                boolean flag = ps.execute();
                System.out.println("setFavoriteItems() flag = " + flag);
            }
        } catch (Exception e) {
        	System.out.println("setFavoriteItems() exception catch");
            e.printStackTrace();
        }
    }

	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		if (connection == null) {
	        System.err.println("DB connectionection failed");
	        return;
        }
    
		try {
	        String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
	        PreparedStatement ps = connection.prepareStatement(sql);
	        ps.setString(1, userId);
	        for (String itemId : itemIds) {
	            ps.setString(2, itemId);
	            ps.execute();
	        }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
	
}
