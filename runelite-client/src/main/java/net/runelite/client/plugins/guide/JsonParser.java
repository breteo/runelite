package net.runelite.client.plugins.guide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class JsonParser {

    BufferedReader br = new BufferedReader(new FileReader("src/main/java/TrainingLocation.json"));
    String line;
    StringBuilder sbuilderObj = new StringBuilder();
    JSONObject jsonObj;

    public JsonParser() throws JSONException, FileNotFoundException {
        //Reading JSON from file system
        //make sure to change path of the training location on your local setup
        System.out.println("Trying stuff Here");
        try {
            while ((line = br.readLine()) != null) {
                sbuilderObj.append(line);
            }
        } catch (IOException e) {
            System.out.println("IO Exception error in JSON Parser");
        }

        System.out.println("Original Json :: " + sbuilderObj.toString());
        jsonObj = new JSONObject(sbuilderObj.toString());

        //Using JSONObject Example

//            String name = jsonObj.getJSONObject("exampleInfo").getString("name");
//            String location = jsonObj.getJSONObject("exampleInfo").getString("location");
//            String combatLevel = jsonObj.getJSONObject("exampleInfo").getString("combatLevel");
//
//            System.out.println("###### Emp Info ############");
//            System.out.println("Name     : "+name);
//            System.out.println("Position : "+location);
//            System.out.println("Age      : "+combatLevel);

    }

    //Fetching dictionary in Json using JSONArray
    public String getName(String key, String info) throws JSONException {
        JSONArray arrObj = jsonObj.getJSONArray(key);
//        System.out.println("Name : " + name);
        return arrObj.getJSONObject(0).getJSONObject(info).getString("name");
    }

    public String getLocation(String key, String info) throws JSONException {
        JSONArray arrObj = jsonObj.getJSONArray(key);
        return arrObj.getJSONObject(0).getJSONObject(info).getString("location");
    }

    public String getHitpoints(String key, String info) throws JSONException {
        JSONArray arrObj = jsonObj.getJSONArray(key);
        return arrObj.getJSONObject(0).getJSONObject(info).getString("hitpoints");
    }

    public String getLevel(String key, String info) throws JSONException {
        JSONArray arrObj = jsonObj.getJSONArray(key);
        return arrObj.getJSONObject(0).getJSONObject(info).getString("combatLevel");
    }
}
