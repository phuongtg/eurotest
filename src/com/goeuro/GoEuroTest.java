package com.goeuro;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 * @author Phuong Tran-Gia
 * @date November 19th, 2014
 * A simple online test from GoEuro
 */



public class GoEuroTest {

	public static final String URL_JSON_GOEURO="http://api.goeuro.com/api/v2/position/suggest/en/";
	public static final String cvsFile="GoEuro_Test.csv";
	
	public static void main(String[] args) throws IOException{
		try{
			if (args.length==0){
				System.out.print("You should run the program like this command line: java -jar GoEuroTest.jar 'STRING' !");
				System.exit(1);
			}
			//Getting the string from the command line
			String str=args[0];
			//System.out.print("Input parameter you have entered: "+str);
			String str2CSV="";
			str2CSV=parseData(str);
			//Check whether or not the result is null 
			if(str2CSV.length()>0){
				System.out.print("The result to write to the csv file: \n "+str2CSV);
				writeData2CSV(str2CSV);
			}else{
				System.out.print("There is no matching string from your input!");
			}
		}catch (IOException ex){
			ex.printStackTrace();
		}
		
		
		
	}
	
	/**
	 * This function is to collect and parse the data (JSON-based) from GoEuro's URL
	 * input data: the string from the command line
	 * output data: 
	 * @throws MalformedURLException 
	 * @throws ParseException 
	 */
	public static String parseData(String str) throws MalformedURLException,IOException{
		String strURL=URL_JSON_GOEURO+str;
		URL url= new URL(strURL);
		String str2CSV="";
		try{
			URLConnection strConn=url.openConnection();
			BufferedReader buff= new BufferedReader(new InputStreamReader(strConn.getInputStream()));
			String readLine;
			while ((readLine=buff.readLine())!=null){
				//System.out.println(readLine);
				//start parsing the collected data by using JSON lib
				JSONParser jParser= new JSONParser();
					Object obj= jParser.parse(readLine);
					//Read the JSON data into an array
					JSONArray jArray=(JSONArray)obj;
					//@SuppressWarnings("unchecked")
					Iterator<JSONObject> i = (Iterator<JSONObject>)jArray.iterator();
					//traverse the JSON array
					while (i.hasNext()){
						JSONObject innerObj=(JSONObject)i.next();
						long id=0;
						String name="", type="";
						id=(Long) innerObj.get("_id");
						name=(String)innerObj.get("name");
						type=(String)innerObj.get("type");
						
						//Get the latitude and longitude
						JSONObject geoObj=(JSONObject)innerObj.get("geo_position");
						double latitude=0, longitude=0;
						latitude=(Double)geoObj.get("latitude");
						longitude=(Double)geoObj.get("longitude");
						
						str2CSV+=id+","+name+","+type+","+latitude+","+longitude + "\n";
						
						//System.out.println(str2CSV);
					}
			}				
		}catch (ParseException pEx){
				System.out.print(pEx.toString());
				pEx.printStackTrace();

		}catch(IOException ex){
			System.out.println(ex.toString());
			ex.printStackTrace();
		}
		return str2CSV;
	}
	
	/**
	 * This function is to write the data to the CSV file
	 */
	public static void writeData2CSV(String strInput) throws IOException{
		String header="_id,name,type,latitude,longitude\n";
		try{
			File file= new File(cvsFile);
			if(file.exists()){
				FileUtils.writeStringToFile(file, strInput,true);
			}else if (!file.exists()){
				FileUtils.writeStringToFile(file, header);
				FileUtils.writeStringToFile(file, strInput,true);
			}
		}catch (IOException ex){
			ex.printStackTrace();
		}
	}

}
