package com.MyFunApp.NewFun.Net;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.LoggingMXBean;
import java.util.Set;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.util.Log;

import com.baidu.location.e;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class CONS {
	private static OkHttpClient client = null;
	
	public static final String BASE_URL = "http://goodoak.cn";
	public static String token;
	public static String SESSIONID;
	
	public synchronized static OkHttpClient getInstance() {
		if(client == null){
			client = new OkHttpClient();
		}
		return client;
	}

	// 对应/user/show的查询,id为0，返回个人信息，id为其它，返回相应的人的信息
	public static IUser get_user(int id) {
		IUser user = null;
		OkHttpClient client = getInstance();
		String url = BASE_URL + "/user/show";
		if (id != 0) {
			url = url + "?uid=" + id;
		}
		Request request = new Request.Builder().url(url)
				.header("Authorization", CONS.token).build();
		Response response;
		Gson gson = new Gson();
		try {
			response = client.newCall(request).execute();
			user = gson.fromJson(response.body().string(), IUser.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return user;
	}

	// 批量获取用户信息
	public static ArrayList<IUser> get_user_batch(ArrayList<Integer> ids) {
		StringBuilder ids_str = new StringBuilder();
		boolean flag = false;
		for (Integer i : ids) {
			if (flag) {
				ids_str.append(",");
			} else {
				flag = true;
			}
			ids_str.append(i);
		}
		String url = BASE_URL + "/user/show_batch?ids_str=" + ids_str;
		Request request = new Request.Builder().url(url)
				.header("Authorization", CONS.token).build();
		Response response;
		Gson gson = new Gson();
		OkHttpClient client = getInstance();
		try {
			response = client.newCall(request).execute();
			ArrayList<IUser> list = gson.fromJson(response.body().string(),
					new TypeToken<ArrayList<IUser>>() {
					}.getType());
			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 对应/user/send_verify_code
	public static HashMap<String,String> get_verify_code(String phone_num) {
		OkHttpClient client = getInstance();
		RequestBody formBody = new FormEncodingBuilder().add("num",
				phone_num).build();
		Request request = new Request.Builder()
				.url(BASE_URL + "/myfun/api/get_verify_code").post(formBody)
				.build();
		Response response;
		Gson gson = new Gson();
		try {
			response = client.newCall(request).execute();
			HashMap<String, String> map = gson.fromJson(response.body()
					.string(), new TypeToken<HashMap<String, String>>() {
			}.getType());
			return map;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 对应/user/create
	public static HashMap<String, String> create_user( String phone,String password,String code) {
		HashMap<String, String> map = new HashMap<String, String>();
		OkHttpClient client = getInstance();
		RequestBody formBody = new FormEncodingBuilder()
				.add("phone", phone).add("code", code)
				.add("password", password).build();
		Request request = new Request.Builder().url(BASE_URL + "/myfun/api/create_user")
				.post(formBody).build();
		Response response;
		Gson gson = new Gson();
		try {
			response = client.newCall(request).execute();
			map = gson.fromJson(response.body().string(),
					new TypeToken<HashMap<String, String>>() {
					}.getType());
			token = map.get("token");
			return map;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 对应/user/update
	public static HashMap<String, String> update_user(String nickname, String account, String female, String image,String token)
	{
		HashMap<String, String> map = new HashMap<String, String>();
		OkHttpClient client = getInstance();
		RequestBody formBody = new FormEncodingBuilder()
		.add("phone", account).add("female", female)
		.add("token", token)
		.add("name", nickname)
		.add("image", image).build();
		Request request = new Request.Builder().url(BASE_URL + "/myfun/api-token/update_user")
				.post(formBody).build();
		Response response;
		Gson gson = new Gson();
		try {
			response = client.newCall(request).execute();
			map = gson.fromJson(response.body().string(),
					new TypeToken<HashMap<String, String>>() {
					}.getType());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return map;
	}
	
	private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("file");
	// 对应/user/profile_image
	@SuppressLint("DefaultLocale")
	public static HashMap<String, String> upload_profile_image(String phone,String path, String imageType,String token) {
		MediaType MEDIA_TYPE = MediaType.parse("image/"
				+ imageType.toLowerCase());
		File file = new File(path);
		RequestBody requestBody = new MultipartBuilder()
	      .type(MultipartBuilder.FORM)
	      .addPart(
	          Headers.of("Content-Disposition", "form-data; name=\"token\";"),
	          RequestBody.create(null, token))
	      .addPart(
	          Headers.of("Content-Disposition", "form-data; name=\"phone\";"),
	          RequestBody.create(null, phone))   
	      .addPart(
	          Headers.of("Content-Disposition", "form-data; name=\"filetype\";"),
	          RequestBody.create(null, imageType))
	      .addPart(
	          Headers.of("Content-Disposition", "form-data; name=\"filepurpose\";"),
	          RequestBody.create(null, "profile"))
	      .addPart(
	          Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"image\";"),
	          RequestBody.create(MEDIA_TYPE, file))
	      .build();
		Request request = new Request.Builder()
				.url(BASE_URL + "/myfun/api-token/upload_profile_image")
				.header("Authorization", CONS.token).post(requestBody).build();
		OkHttpClient client = getInstance();
		Gson gson = new Gson();
		try {
			Response response = client.newCall(request).execute();
			String re = response.body()
					.string();
			HashMap<String, String> map = gson.fromJson(re, new TypeToken<HashMap<String, String>>() {
			}.getType());
			return map;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( Exception e){
			e.printStackTrace();
		}
		return null;
	}

	// 对应/user/login
	public static HashMap<String, String> login(String phone_num, String password) {
		HashMap<String, String> map = null;
		OkHttpClient client = getInstance();
		RequestBody formBody = new FormEncodingBuilder()
				.add("phone", phone_num).add("password", password).build();
		Request request = new Request.Builder().url(BASE_URL + "/myfun/api/user_login")
				.post(formBody).build();
		Response response;
		Gson gson = new Gson();
		try {
			response = client.newCall(request).execute();
			String re = response.body()
						.string();
			Log.i("re   re ", re);
			map = gson.fromJson(re, new TypeToken<HashMap<String, String>>() {
			}.getType());
			CONS.token = map.get("token");
			return map;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}


	// 对应/food_category
	public static ArrayList<HashMap<String, String>> food_categories() {
		OkHttpClient client = getInstance();
		Request request = new Request.Builder()
				.url(BASE_URL + "/food_category").build();
		Response response;
		Gson gson = new Gson();
		try {
			response = client.newCall(request).execute();
			ArrayList<HashMap<String, String>> list = gson.fromJson(response
					.body().string(),
					new TypeToken<List<HashMap<String, String>>>() {
					}.getType());
			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 对应/food_style
	public static ArrayList<HashMap<String, String>> food_styles() {
		OkHttpClient client = getInstance();
		Request request = new Request.Builder().url(BASE_URL + "/food_style")
				.build();
		Response response;
		Gson gson = new Gson();
		try {
			response = client.newCall(request).execute();
			ArrayList<HashMap<String, String>> list = gson.fromJson(response
					.body().string(),
					new TypeToken<List<HashMap<String, String>>>() {
					}.getType());
			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 对应/subject
	public static ArrayList<HashMap<String, String>> subjects() {
		OkHttpClient client = getInstance();
		Request request = new Request.Builder().url(BASE_URL + "/subject")
				.build();
		Response response;
		Gson gson = new Gson();
		try {
			response = client.newCall(request).execute();
			ArrayList<HashMap<String, String>> list = gson.fromJson(response
					.body().string(),
					new TypeToken<List<HashMap<String, String>>>() {
					}.getType());
			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 对应/wealth/options
	public static HashMap<String, Object> get_wealth_options() {
		OkHttpClient client = getInstance();
		Request request = new Request.Builder().url(
				BASE_URL + "/wealth/options").build();
		Response response;
		Gson gson = new Gson();
		try {
			response = client.newCall(request).execute();
			HashMap<String, Object> list = gson.fromJson(response.body()
					.string(), new TypeToken<HashMap<String, Object>>() {
			}.getType());
			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

//	对应发表喜欢
	public static boolean plus_like(int id){
		OkHttpClient client = getInstance();
		RequestBody formBody = new FormEncodingBuilder().add("you",
				id + "").build();
		Request request = new Request.Builder()
				.url(BASE_URL + "/likes/plus")
				.header("Authorization", CONS.token)
				.post(formBody)
				.build();
		Response response;
		Gson gson = new Gson();
		try {
			response = client.newCall(request).execute();
			HashMap<String, String> map = gson.fromJson(response.body()
					.string(), new TypeToken<HashMap<String, String>>() {
			}.getType());
			if(map.containsKey("you")){
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
		
	}
	
//	添加好友关系
	public static boolean plus_relationship(int id){
		OkHttpClient client = getInstance();
		RequestBody formBody = new FormEncodingBuilder().add("you",
				id + "").build();
		Request request = new Request.Builder()
				.url(BASE_URL + "/relationship/add")
				.header("Authorization", CONS.token)
				.post(formBody)
				.build();
		Response response;
		Gson gson = new Gson();
		try {
			response = client.newCall(request).execute();
			HashMap<String, String> map = gson.fromJson(response.body()
					.string(), new TypeToken<HashMap<String, String>>() {
			}.getType());
			if(map.containsKey("you")){
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
//	查询好友关系
	public static boolean get_relationship(int id){
		OkHttpClient client = getInstance();
		Request request = new Request.Builder()
				.url(BASE_URL + "/relationship/" + id)
				.header("Authorization", CONS.token)
				.build();
		Response response;
		Gson gson = new Gson();
		try {
			response = client.newCall(request).execute();
			HashMap<String, String> map = gson.fromJson(response.body()
					.string(), new TypeToken<HashMap<String, String>>() {
			}.getType());
			if(map.containsKey("result") && map.get("result").equals("1") ){
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
//	更新位置,现只有经纬度，后面可以添加更多的位置信息
	public static boolean update_position(HashMap<String,Double> param){
		OkHttpClient client = getInstance();
		Double lat = param.get("lat");
		Double lng = param.get("lng");
		RequestBody formBody = new FormEncodingBuilder()
				.add("lat",lat + "")
				.add("lng", lng + "").build();
		Request request = new Request.Builder()
				.url(BASE_URL + "/position/update")
				.header("Authorization", CONS.token)
				.post(formBody)
				.build();
		Response response;
		Gson gson = new Gson();
		try {
			response = client.newCall(request).execute();
			HashMap<String, String> map = gson.fromJson(response.body()
					.string(), new TypeToken<HashMap<String, String>>() {
			}.getType());
			if(map.containsKey("result") && map.get("result").equals("1")){
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
