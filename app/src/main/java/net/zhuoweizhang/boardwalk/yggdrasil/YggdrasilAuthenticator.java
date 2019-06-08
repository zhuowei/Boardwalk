package net.zhuoweizhang.boardwalk.yggdrasil;

import java.util.*;
import java.net.*;
import java.io.*;
import net.zhuoweizhang.boardwalk.downloader.*;
import net.zhuoweizhang.boardwalk.util.*;

import com.google.gson.Gson;

public class YggdrasilAuthenticator {

	private static final String API_URL = "https://authserver.mojang.com";
	//private static final String API_URL = "http://localhost:8000";

	private String clientName = "Minecraft";
	private int clientVersion = 1;
	private Gson gson = new Gson();

	private <T> T makeRequest(String endpoint, Object inputObject, Class<T> responseClass) throws IOException {
		InputStream is = null;
		HttpURLConnection conn;
		byte[] buf = new byte[0x4000];
		int statusCode = -1;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		String requestJson = gson.toJson(inputObject);
		URL url = null;

		try {
			url = new URL(API_URL + "/" + endpoint);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("User-Agent", DownloadUtils.USER_AGENT);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.connect();
			OutputStream os = null;
			try {
				os = conn.getOutputStream();
				os.write(requestJson.getBytes(DownloadUtils.utf8));
			} finally {
				if (os != null) os.close();
			}
			statusCode = conn.getResponseCode();
			if (statusCode != 200) {
				is = conn.getErrorStream();
			} else {
				is = conn.getInputStream();
			}

			IoUtil.pipe(is, bos, buf);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
				}
			}
		}

		String outString = new String(bos.toByteArray(), DownloadUtils.utf8);

		if (statusCode != 200) {
			throw new RuntimeException("Status: " + statusCode + ":" + outString);
		} else {
			T outResult = gson.fromJson(outString, responseClass);
			return outResult;
		}
	}

	public AuthenticateResponse authenticate(String username, String password, UUID clientId) throws IOException {
		AuthenticateRequest request = new AuthenticateRequest(username, password, clientId, clientName, clientVersion);
		return makeRequest("authenticate", request, AuthenticateResponse.class);
	}

	public RefreshResponse refresh(String authToken, UUID clientId/*, Profile activeProfile*/) throws IOException {
		RefreshRequest request = new RefreshRequest(authToken, clientId/*, activeProfile*/);
		return makeRequest("refresh", request, RefreshResponse.class);
	}
}
