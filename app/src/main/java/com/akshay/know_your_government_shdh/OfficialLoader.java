package com.akshay.know_your_government_shdh;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class OfficialLoader extends AsyncTask<String,Void, ArrayList<Official>>
{
    @SuppressLint("StaticFieldLeak")
    private MainActivity mainActivity;
    private static final String TAG = "OfficialLoader";


    OfficialLoader(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

    @Override
    protected ArrayList<Official> doInBackground(String... strings)
    {
        ArrayList<Official> finalData;
        String CT_ST_ZP = strings[0];
        String API_TOKEN = BuildConfig.API_KEY;
        String DATA_URL = "https://www.googleapis.com/civicinfo/v2/representatives?key="+API_TOKEN+"&address="+CT_ST_ZP;

        String data = getOfficialDatafromURL(DATA_URL);
        finalData = parseJSON(data);
        return finalData;
    }

    private String getOfficialDatafromURL(String URL)
    {
        Uri dataUri = Uri.parse(URL);
        String urlToUse = dataUri.toString();

        StringBuilder sb = new StringBuilder();
        try
        {
            java.net.URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null)
                sb.append(line).append('\n');
        }
        catch (Exception e)
        {
            Log.e(TAG, "EXCEPTION | OfficialLoader: getOfficialDatafromURL: bp:", e);
            return sb.toString();
        }
        return sb.toString();
    }

    private ArrayList<Official> parseJSON(String data)
    {
        ArrayList<Official> tempList = new ArrayList<>();
        setUpLocation(data);
        Official official;

        try
        {
            JSONObject temp = new JSONObject(data);
            JSONArray offices = (JSONArray) temp.get("offices");
            JSONArray officials = (JSONArray) temp.get("officials");
            Log.d(TAG, "parseJSON: bp: Length of Array: "+ offices.length());

            for(int i = 0; i<offices.length(); i++)
            {
                JSONObject office = (JSONObject) offices.get(i);
                JSONObject officialIndices = (JSONObject) offices.get(i);
                JSONArray index = officialIndices.getJSONArray("officialIndices");

                for(int j = 0; j< index.length(); j++)
                {
                    Official official_intermediate;
                    JSONObject officialData_JSON = (JSONObject) officials.get(index.getInt(j)) ;
                    official_intermediate = fetchOfficialDetails(officialData_JSON);
                    official = official_intermediate;
                    official.setTitle(office.getString("name"));                    // Setting Title here because the above statement would nake the title field of official object to null string
                    tempList.add(official);
                }
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | parseJSON: bp: " + e);
        }
        return  tempList;
    }

    private Official fetchOfficialDetails(JSONObject officialData_JSON)
    {
        Official temp = new Official();
        temp.setName(getNameFromData(officialData_JSON));
        temp.setParty(getPartyFromData(officialData_JSON));
        temp.setAddress(getAddressFromData(officialData_JSON));
        temp.setUrls(getURLFromData(officialData_JSON));
        temp.setEmails(getEmailFromData(officialData_JSON));
        temp.setPhones(getPhoneFromData(officialData_JSON));
        temp.setPhotoURL(getPhotoURLfromData(officialData_JSON));
        temp.setChannels(getChannelsFromData(officialData_JSON));
        return temp;
    }

    private String getNameFromData(JSONObject officialData_json)
    {
        String name = "";
        try
        {
            name = officialData_json.getString("name");
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | fetchOfficialDetails: bp: " + e);
        }

        return name;
    }

    private String getAddressFromData(JSONObject officialData_json)
    {
        String finalAddress = "";
        try
        {
            JSONArray addresses = (JSONArray) officialData_json.get("address");
            JSONObject address = (JSONObject) addresses.get(0);
            String line1 = getLine1FromData(address);
            String line2 = getLine2FromData(address);
            String city = getCityFromData(address);
            String state = getStateFromData(address);
            String zip = getZIPFromData(address);
            finalAddress = line1 + ", " + (line2.equals("")?line2 + "":line2 + ", ") + city + ", " + state + ", " +zip;
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | getAddressFromData: " + e);
        }

        return finalAddress;
    }

    private String getLine1FromData(JSONObject address)
    {
        String line1 = "";
        try
        {
            line1 = address.getString("line1");
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | getLine1FromData: " + e);
        }
        return line1;
    }

    private String getLine2FromData(JSONObject address)
    {
        String line2 = "";
        try
        {
            line2 = address.getString("line2");
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | getLine2FromData: " + e);
        }
        return line2;
    }

    private String getCityFromData(JSONObject address)
    {
        String city = "";
        try
        {
            city = address.getString("city");
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | getCityFromData: " + e);
        }
        return city;
    }

    private String getStateFromData(JSONObject address)
    {
        String state = "";
        try
        {
            state = address.getString("state");
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | getStateFromData: " + e);
        }
        return state;
    }

    private String getZIPFromData(JSONObject address)
    {
        String zip = "";
        try
        {
            zip = address.getString("zip");
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | getZIPFromData: " + e);
        }
        return zip;
    }

    private String getURLFromData(JSONObject officialData_json)
    {
        String URL = "";

        try
        {
            JSONArray urls = (JSONArray) officialData_json.get("urls");
            URL = urls.get(0).toString();
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | getURLFromData: " + e);
        }

        return URL;
    }

    private String getEmailFromData(JSONObject officialData_json)
    {
        String email = "";

        try
        {
            JSONArray urls = (JSONArray) officialData_json.get("emails");
            email = urls.get(0).toString();
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | getEmailFromData: " + e);
        }

        return email.toLowerCase();
    }

    private String getPhoneFromData(JSONObject officialData_json)
    {
        String phone = "";

        try
        {
            JSONArray urls = (JSONArray) officialData_json.get("phones");
            phone = urls.get(0).toString();
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | getPhoneFromData: " + e);
        }

        return phone;
    }

    private String getPartyFromData(JSONObject officialData_json)
    {
        String party = "";
        try
        {
            party = officialData_json.getString("party");
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | fetchOfficialDetails: bp: " + e);
        }

        return  "( "+party+" )";
    }

    private ArrayList<Channel> getChannelsFromData(JSONObject officialData_json)
    {
        ArrayList<Channel> tempList = new ArrayList<>();
        Channel temp;

        try
        {
            JSONArray channels = (JSONArray) officialData_json.get("channels");
            for(int i = 0; i < channels.length(); i++)
            {
                JSONObject channel = (JSONObject) channels.get(i);
                temp = new Channel(channel.getString("type"), channel.getString("id"));
                tempList.add(temp);
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | getAddressFromData: " + e);
        }
        return tempList;
    }

    private String getPhotoURLfromData(JSONObject officialData_json)
    {
        String photoURL = "";

        try
        {
            photoURL = officialData_json.getString("photoUrl");
//            Log.d(TAG, "getPhotoURLfromData: bp: PhotoURL: " + photoURL);
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | getEmailFromData: " + e);
        }

        return photoURL;
    }

    private void setUpLocation(String data)
    {
        TextView location = mainActivity.findViewById(R.id.location);
        try
        {
            JSONObject normalizedInput = new JSONObject(data);
            normalizedInput = normalizedInput.getJSONObject("normalizedInput");
            String city = normalizedInput.getString("city");
            String state = normalizedInput.getString("state");
            String zip = normalizedInput.getString("zip");

            String locationText = (city.equals("")?"":city+", ") + (zip.equals("")?state:state+", ") + (zip.equals("")?"":zip);
            location.setText(locationText);
        }
        catch (Exception e)
        {
            Log.d(TAG, "EXCEPTION | parseJSON: " + e);
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Official> officials)
    {
        mainActivity.updateOfficialData(officials);
        super.onPostExecute(officials);
    }
}
