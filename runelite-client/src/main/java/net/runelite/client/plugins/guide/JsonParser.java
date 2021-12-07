package net.runelite.client.plugins.guide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class JsonParser {

    File file = new File("");

    BufferedReader br;
    String line;
    StringBuilder sbuilderObj = new StringBuilder();
    JSONObject jsonObj;

    public JsonParser() throws JSONException, FileNotFoundException {
        //Reading JSON from file system
        //make sure to change path of the training location on your local setup
        // construct Json object in the constructor for the parser
        String filePath = file.getAbsolutePath();
        filePath = filePath.concat("/runelite-client/src/main/java/net/runelite/client/plugins/guide/guide.JSON");
        System.out.println("path: " + filePath);
        br = new BufferedReader(new FileReader(filePath));
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

    // get array of keys for info objects
    public ArrayList<String> getAllInfoKeys(String focus, String key) throws JSONException{
        JSONArray arrObj = jsonObj.getJSONObject(focus).getJSONArray(key);
        JSONObject obj = arrObj.getJSONObject(0);
        Iterator<?> iter = obj.keys();
        ArrayList<String> keylist = new ArrayList<String>();
        while(iter.hasNext()) {
            keylist.add((String)iter.next());
        }
        return keylist;
    }

    public ArrayList<String> getAllGuideKeys(String focus) throws JSONException {
        JSONObject obj = jsonObj.getJSONObject(focus);
        Iterator<?> iter = obj.keys();
        ArrayList<String> keyList = new ArrayList<String>();
        while (iter.hasNext()) {
            keyList.add((String)iter.next());
        }
        return keyList;
    }

    public String getSpell(String focus, String key) throws JSONException {
        JSONArray arrObj = jsonObj.getJSONObject(focus).getJSONArray(key);
        return arrObj.getJSONObject(0).getString("spellToUse");
    }

    //Fetching dictionary in Json using JSONArray
    public String getName(String focus, String key, String info) throws JSONException {
        JSONArray arrObj = jsonObj.getJSONObject(focus).getJSONArray(key);
//        System.out.println("Name : " + name);
        return arrObj.getJSONObject(0).getJSONObject(info).getString("name");
    }

    public String getLocation(String focus, String key, String info) throws JSONException {
        JSONArray arrObj = jsonObj.getJSONObject(focus).getJSONArray(key);
        return arrObj.getJSONObject(0).getJSONObject(info).getString("location");
    }

    public String getHitpoints(String focus, String key, String info) throws JSONException {
        JSONArray arrObj = jsonObj.getJSONObject(focus).getJSONArray(key);
        return arrObj.getJSONObject(0).getJSONObject(info).getString("hitpoints");
    }

    public String getLevel(String focus, String key, String info) throws JSONException {
        JSONArray arrObj = jsonObj.getJSONObject(focus).getJSONArray(key);
        return arrObj.getJSONObject(0).getJSONObject(info).getString("combatLevel");
    }
}
