package edu.cpp.Rafikie.data.provider;

import com.mongodb.DBCollection;

public interface MongoDBConnection {
	
	 public	DBCollection createConnection();
	 public DBCollection createConnectionforUserTable();
	 public	DBCollection createConnectionforUserImageTable();
	 public DBCollection  createConnectionforUserNotificationTable();
	 public DBCollection createConnectionforMessageTable();

}