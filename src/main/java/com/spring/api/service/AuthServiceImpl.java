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
import org.springframework.transaction.annotation.Transactional;

import com.spring.api.code.AuthError;
import com.spring.api.exception.CustomException;
import com.spring.api.util.RedisUtil;
import com.spring.api.util.UserCheckUtil;

@Transactional
@Service("authService")
public class AuthServiceImpl implements AuthService{
	private final UserCheckUtil userCheckUtil;
    private final RedisUtil redisUtil;
    private final String serviceId;
    private final String accessKey;
    private final String secretKey;
    private final String phoneNumber;
    private final String PREFIX_AUTH_CODE = "auth_code:";
    private final String PREFIX_VERIFICATION_CODE = "verification_code:";
    
    private final int authDigit;
    private final long authTime;
    private final int verificationDigit;
    private final long verificationTime;
    
	@Autowired
	AuthServiceImpl(
		UserCheckUtil checkUtil,
		RedisUtil redisUtil,
		@Value("${sms.serviceId}") String serviceId,
		@Value("${sms.accesskey}") String accessKey,
		@Value("${sms.secretKey}") String secretKey,
		@Value("${sms.phoneNumber}") String phoneNumber,
		@Value("${sms.auth.digit}") int authDigit,
		@Value("${sms.auth.time}") long authTime,
		@Value("${sms.verification.digit}") int verificationDigit,
		@Value("${sms.verification.time}") long verificationTime
	){
		this.userCheckUtil = checkUtil;
		this.redisUtil = redisUtil;
		this.serviceId = serviceId;
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.phoneNumber = phoneNumber;
		this.authDigit = authDigit;
		this.authTime = authTime*1000;
		this.verificationDigit = verificationDigit;
		this.verificationTime = verificationTime*1000;
	}
	
	@Override
	public void getAuthcode(HashMap<String,String> param) throws Exception{
		String user_phone = param.get("user_phone");
		
		userCheckUtil.checkUserPhoneRegex(user_phone);
		
		String apiUrl = "https://sens.apigw.ntruss.com/sms/v2/services/"+serviceId+"/messages";
		String method = "POST";
		String contentType = "application/json; charset=UTF-8";
		String timestamp = Long.toString(System.currentTimeMillis());
		String signature = getSignature(timestamp);
		String auth_code = createRandomAuthcode(this.authDigit);
		
		JSONObject message = new JSONObject();
		message.put("to", user_phone.replaceAll("-", ""));
		
		JSONArray messages = new JSONArray();
		messages.add(message);
		
		JSONObject body = new JSONObject();
		body.put("type", "SMS");
		body.put("contentType", "COMM");
		body.put("countryCode", "82");
		body.put("from", this.phoneNumber);
		body.put("content", "[거래상어] 휴대폰 인증 번호는 "+auth_code+" 이며, "+(authTime/1000)+"초 동안 유효합니다.");
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
			redisUtil.setData(PREFIX_AUTH_CODE+user_phone, auth_code, this.authTime);
		}else {
			throw new CustomException(AuthError.AUTH_CODE_NOT_SENT);
		}
	}
	
	@Override
	public String getVerficationcode(HashMap<String,String> param) {
		String user_phone = param.get("user_phone");
		String auth_code = param.get("auth_code");
		String stored_auth_code = redisUtil.getData(PREFIX_AUTH_CODE+user_phone);
		
		userCheckUtil.checkUserPhoneRegex(user_phone);
		userCheckUtil.checkUserAuthcodeRegex(auth_code);
		
		if(stored_auth_code == null) {
			throw new CustomException(AuthError.NOT_FOUND_AUTH_CODE);
		}
		
		userCheckUtil.checkAuthcodeAndStoredAuthcode(stored_auth_code, auth_code);
		
		String verification_code = createRandomAuthcode(this.verificationDigit);
		redisUtil.delete(PREFIX_AUTH_CODE+""+user_phone);
		redisUtil.setData(PREFIX_VERIFICATION_CODE+user_phone, verification_code, this.verificationTime);
		
		return verification_code;
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