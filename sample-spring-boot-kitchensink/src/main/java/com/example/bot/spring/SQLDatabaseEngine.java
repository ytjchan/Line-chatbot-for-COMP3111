package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	@Override
	String search(String text) throws Exception {
		//Write your code here
		String result = null;
		ResultSet rs = null;
		Connection connection = getConnection();
		PreparedStatement stmt = connection.prepareStatement("select response from keyresponse where keyword='"+text+"';");
		//stmt.setString(1, text);
		try {
			rs = stmt.executeQuery();
			log.info("Executing query...");
			if (rs.first()) { // check if there is any result
				 result = rs.getString(1);
			}
		} catch (SQLException e) { 
			log.info("KILL ME");
			log.info(e.toString()); 
		} finally {
			try {
				if (rs!=null)
					rs.close();
				if (stmt!=null)
					stmt.close();
				if (connection!=null)
					connection.close();
			} catch (SQLException e) {
				log.info("KILL ME");
				log.info(e.toString());
			}
		}
		if (result!=null)
			return result;
		throw new Exception("NOT FOUND");
	}
	
	
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}
