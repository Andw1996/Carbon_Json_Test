package com.example.carbon_json_test;

import android.graphics.Color;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


//fuelmix ID 20 = 5123.51
//fuelmix ID 49 = 4891.86
//Above are the total values of two different fuelmix inputs. Around 5000
//The values of the json data represent the MW generated due to the use of that particular fuel.


public class fetchData extends AsyncTask <Void, Void, Void> {

    String data = "";
    String dataParsed = "";
    String singleParsed ="";
    String eNumAsString = "";
    int erenew = 0;
    int ecoal = 0;
    int egas = 0;
    int enetImport = 0;
    int eotherFossil = 0;
    int result = 0;

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            URL url = new URL("https://carbondown.tk/fuelmix/all");

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";

            while(line != null){
                line = bufferedReader.readLine();
                data = data + line;
            }

            JSONArray JA = new JSONArray(data);
            for(final int[] i = {0}; i[0] < 1; i[0]++){
                JSONObject JO = (JSONObject) JA.get(JA.length()-1); // The length -1 fetches the latest array object

                int renew = JO.getInt("renew"); //makes string value integer
                int coal = JO.getInt("coal");
                int gas = JO.getInt("gas");
                int netImport = JO.getInt("netImport");
                int otherFossil = JO.getInt("otherFossil");

               // String fuelmixDateTime = (String) JO.get("fuelmixDateTime");

                singleParsed =  "Below is the value for the MW produced by each source of fuel and the time in which it was recorded." + "\n" + "\n" +
                                "Time of update: " + JO.get("fuelmixDateTime") + "\n" +
                                "Coal: " + JO.get("coal")+"MW" + "\n" +
                                "Gas: " + JO.get("gas")+"MW" + "\n" +
                                "Net Import of Resources: " + JO.get("netImport")+"MW" + "\n" +
                                "Other Fossil Fuels: " + JO.get("otherFossil")+"MW" + "\n" +
                                "Renewable Energy: " + JO.get("renew")+"MW"+ "\n";

                dataParsed = dataParsed + singleParsed + "\n";

                erenew = renew;
                ecoal = coal;
                egas = gas;
                enetImport = netImport;
                eotherFossil = otherFossil;

                result = ecoal + eotherFossil + enetImport + egas + erenew; // calculate total MW produced, display % of renewable for now

                String numAsString = Integer.toString(result);
                eNumAsString = numAsString + " MW";

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (erenew >= result/2){
            MainActivity.eData.setTextColor(Color.GREEN);
            MainActivity.eData.setText("It is a good time to use appliances");

        }else {
            MainActivity.eData.setTextColor(Color.RED);
            MainActivity.eData.setText("It is not a good time to use appliances");
        }
        MainActivity.data.setText(this.dataParsed);
        MainActivity.total.setText(this.eNumAsString);

    }
}
