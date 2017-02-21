package recommender;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import edu.cpp.Rafikie.data.UserDetails;
import edu.cpp.Rafikie.data.provider.MongoDBConnection;
import edu.cpp.Rafikie.data.provider.MongoDBConnectionImpl;
import edu.cpp.Rafikie.data.provider.UserManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("interests")
public class InterestsImpl implements Interests {

	MongoDBConnection connection;
	private ArrayList<String> allInterests;
	private Hashtable<String, Integer> interestIndex;

	@Autowired
	private UserManager userManager;

	public InterestsImpl() {
		connection = new MongoDBConnectionImpl();
	}

	private ArrayList<String> getInterestsFromDB() {
		DBCollection collection = connection.createConnectionforUserTable();
		DBCursor cursor = collection.find();
		HashSet<String> interestsSet = new HashSet();
		while (cursor.hasNext()) {
			try {
				String userEmail = cursor.next().get("email").toString();
				UserDetails userDetails = userManager.fetchUserDetails(userEmail);
				for (String interest : userDetails.getInterests()) {
					interestsSet.add(preprocessInterest(interest));
				}
			} catch (NullPointerException e) {
				System.out.println(e);
			}
		}
		ArrayList<String> interests = new ArrayList<>(interestsSet);
		Collections.sort(interests);
		return interests;
	}

	private Hashtable<String, Integer> createInterestIndex() {
		Hashtable<String, Integer> index = new Hashtable<>();
		int i = 0;
		for (String interest : allInterests) {
			index.put(interest, i);
			++i;
		}
		return index;
	}

	public void updateUserVector(String userEmail) {
		allInterests = getInterestsFromDB();
		interestIndex = createInterestIndex();
		UserDetails userDetails = userManager.fetchUserDetails(userEmail);
		String[] userInterests = userDetails.getInterests();
		if (userInterests != null) {
			Double[] d_v = new Double[interestIndex.size()];
			Arrays.fill(d_v, 0.0);
			for (String interest : userInterests) {
				interest = preprocessInterest(interest);
				try {
					d_v[interestIndex.get(interest)] = 1.0;
				}
				catch (Exception e) {
					System.out.println("Interest not in allInterests");
				}
			}
			RealVector vectorRepr = new ArrayRealVector(d_v);
			userDetails.setVectorRepr(vectorRepr);
			Gson gson = new GsonBuilder().create();
			try {
				String userDetailsStr = gson.toJson(userDetails);
				userManager.insertUserDetails(userDetailsStr);
			}
			catch (Exception e) {
				System.out.println(e);
				System.out.println("Could not convert UserDetails to string and add to DB.");
			}
		}
	}

	public void updateAllUserVectors() {
		// TODO
	}

	private String preprocessInterest(String interest) {
		return interest.trim().toLowerCase();
	}
}