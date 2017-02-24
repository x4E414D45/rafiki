package edu.cpp.Rafikie.controller;
//import edu.cpp.Rafikie.model.Customer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.cpp.Rafikie.data.Login;
import edu.cpp.Rafikie.data.Register;
import edu.cpp.Rafikie.data.User;
import edu.cpp.Rafikie.data.UserDetails;
import edu.cpp.Rafikie.data.provider.MongoDBConnection;
import edu.cpp.Rafikie.data.provider.UserManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import recommender.Interests;
import recommender.Recommender;

/**
 * This is the controller used by Spring framework.
 * <p>
 * The basic function of this controller is to map each HTTP API Path to the
 * correspondent method.
 *
 */
@RestController
public class WebController extends WebMvcConfigurerAdapter {

	User user = new User();
	UserDetails userDetail = new UserDetails();

	@Autowired
	private UserManager userManager;

	@Autowired
	MongoDBConnection connection;

	@Autowired
	Interests interests;

	@Autowired 
	Recommender recommender;

	/*	 @Override
	    public void addViewControllers(ViewControllerRegistry registry) {
	        registry.addViewController("/index").setViewName("index");
	        registry.addViewController("/login").setViewName("login");
	        
	 }*/
	@RequestMapping(value = "/getEmail", method = RequestMethod.GET)
	public User getEmail() {
            return user;
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST, consumes = "application/json")
	public boolean login(@RequestBody String login) {
            Gson gson = new GsonBuilder().create();
            Login loginDetails = gson.fromJson(login, Login.class);
            loginDetails.setPassword(userManager.passwordEncryption(loginDetails.getPassword()));
            List<String> list = userManager.isUserExist(loginDetails.getEmail());
            return list.contains(loginDetails.getPassword());
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes = "application/json")
	public boolean register(@RequestBody String registerDetails) throws JsonParseException, JsonMappingException, IOException {
            Gson gson = new GsonBuilder().create();
            Register register = gson.fromJson(registerDetails, Register.class);
            register.setPassword(userManager.passwordEncryption(register.getPassword()));
            List<String> list = new ArrayList<>();
            list = userManager.isUserExist(register.getEmail());

            if (list.isEmpty()) {
                userManager.register(register);
                return true;
            } 
            else 
            {
                return false;
            }
	}

	@RequestMapping(value = "/userDetails", method = RequestMethod.POST, consumes = "application/json")
	public boolean insertUserDetails(@RequestBody String userDetails) throws JsonParseException, JsonMappingException, IOException {
		userManager.insertUserDetails(userDetails);
		return false;

	}

	@RequestMapping(value = "/fetchUserDetails", method = RequestMethod.POST, consumes = "application/json")
	public UserDetails fetchUserDetails(@RequestBody String userEmail)  {
		userDetail = userManager.fetchUserDetails(userEmail);                         
		return userDetail;
	}

}
