package controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import models.User;
import models.UserRepository;
import play.mvc.Result;
import util.Common;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static play.mvc.Results.ok;

/**
 * Created by dachengwen on 4/24/16.
 */
@Named
@Singleton
public class UsernameLoginController implements LoginAPI {
    private final UserRepository userRepository;
    @Inject
    public UsernameLoginController(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Result login(String username, String password) {
        List<User> users = userRepository.findByUserName(username);
        if (users.size()==0) {
            System.out.println("User is not existed");
            return Common.badRequestWrapper("Username is not existed");
        }
        User user = users.get(0);
        if (user.getPassword().equals(MD5Hashing(password))) {
            System.out.println("User is valid");
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", user.getId());
            jsonObject.addProperty("userName", user.getUserName());
            return ok(new Gson().toJson(jsonObject));
        } else {
            System.out.println("User is not valid");
            return Common.badRequestWrapper("User is not valid");
        }
    }

    private static String MD5Hashing(String password) {

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(password.getBytes());
        byte byteData[] = md.digest();

        //convert the byte to hex format method
        StringBuffer hexString = new StringBuffer();
        for (int i=0;i<byteData.length;i++) {
            String hex=Integer.toHexString(0xff & byteData[i]);
            if(hex.length()==1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

}
