package com.spring.api.service;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.spring.api.code.AuthError;
import com.spring.api.exception.CustomException;
import com.spring.api.util.RedisUtil;
import com.spring.api.util.UserCheckUtil;

@Service("authService")
public class AuthServiceImpl implements AuthService{
	private final UserCheckUtil userCheckUtil;
    private final RedisUtil redisUtil;
    private final String serviceId;
    private final String accessKey;
    private final String secretKey;
    private final String phoneNumber;
    private final int authcodeDigit;
    private final long authcodeTime;
    
	@Autowired
	AuthServiceImpl(
		UserCheckUtil checkUtil,
		RedisUtil redisUtil,
		@Value("${sms.serviceId}") String serviceId,
		@Value("${sms.accesskey}") String accessKey,
		@Value("${sms.secretKey}") String secretKey,
		@Value("${sms.phoneNumber}") String phoneNumber,
		@Value("${sms.authcodeDigit}") int authcodeDigit,
		@Value("${sms.authcodeTime}") long authcodeTime
	){
		this.userCheckUtil = checkUtil;
		this.redisUtil = redisUtil;
		this.serviceId = serviceId;
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.phoneNumber = phoneNumber;
		this.authcodeDigit = authcodeDigit;
		this.authcodeTime = authcodeTime*1000;
	}
	
	@Override
	public void getAuthcode(HashMap<String,String> param) throws Exception{
		String user_phone = param.get("user_phone");
		System.out.println(createRandomAuthcode(this.authcodeDigit));
		userCheckUtil.checkUserPhoneRegex(user_phone);
		userCheckUtil.isUserPhoneDuplicate(user_phone);
		
		String apiUrl = "https://sens.apigw.ntruss.com/sms/v2/services/"+serviceId+"/messages";
		String method = "POST";
		String contentType = "application/json; charset=UTF-8";
		String timestamp = Long.toString(System.currentTimeMillis());
		String signature = getSignature(timestamp);
		String authcode = createRandomAuthcode(this.authcodeDigit);
		
		JSONObject message = new JSONObject();
		message.put("to", user_phone.replaceAll("-", ""));
		
		JSONArray messages = new JSONArray();
		messages.add(message);
		
		JSONObject body = new JSONObject();
		body.put("type", "SMS");
		body.put("contentType", "COMM");
		body.put("countryCode", "82");
		body.put("from", this.phoneNumber);
		body.put("content", "[거래상어] 휴대폰 인증 번호는 "+authcode+" 입니다.");
		body.put("messages", messages);
		
		URL url = new URL(apiUrl);
		
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setUseCaches(false);
		con.setDoOutput(true);
		con.setRequestProperty("Content-Type", contentType);
		con.setRequestProperty("x-ncp-iam-access-key", this.accessKey);
		con.setRequestProperty("x-ncp-apigw-timestamp", timestamp);
		con.setRequestProperty("x-ncp-apigw-signature-v2", signature);
		con.setRequestMethod(method);
		
		DataOutputStream dataOutputStream = new DataOutputStream(con.getOutputStream());
		dataOutputStream.write(body.toJSONString().getBytes());
		dataOutputStream.flush();
		dataOutputStream.close();

		if(con.getResponseCode() == HttpStatus.ACCEPTED.value()) {
			redisUtil.setData(user_phone, authcode, this.authcodeTime);
		}else {
			throw new CustomException(AuthError.USER_AUTHCODE_NOT_SENT);
		}
	}
	
	private String createRandomAuthcode(int n) {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<n; i++) {
			sb.append((int)Math.floor(Math.random()*10)+"");
		}
		return sb.toString();
	}
	
	private String getSignature(String timestamp) throws Exception {
		String url = "/sms/v2/services/"+this.serviceId+"/messages";
		String method = "POST";
		String newLine = "\n";
		String space = " ";
		String message = new StringBuilder()
				.append(method)
				.append(space)
				.append(url)
				.append(newLine)
				.append(timestamp)
				.append(newLine)
				.append(this.accessKey)
				.toString();
		
		SecretKeySpec signingKey = new SecretKeySpec(this.secretKey.getBytes("UTF-8"),"HmacSHA256");
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(signingKey);
		
		byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
		String encodeBase64String = Base64.encodeBase64String(rawHmac);
		
		return encodeBase64String;
	}
}