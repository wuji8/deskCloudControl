package com.cloud.cc.tools;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JwtTokenUtil {

	/**
	 * 加密秘钥
	 */
	private static final String SECRET = "30ddc86d107c4d558db7186b3ce0a3e5";

	/**
	 * jackson
	 */
	private static ObjectMapper mapper = new ObjectMapper();

	/**
	 * header数据
	 * 
	 * @return
	 */
	private static Map<String, Object> createHead() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("typ", "JWT");
		map.put("alg", "HS256");
		return map;
	}

	/**
	 * 生成Token
	 * 
	 * @param obj
	 *            放入要生成token的�??
	 * @param maxAge
	 *            token的有效期(为毫�?)
	 * @return
	 * @throws JsonProcessingException
	 * @throws IllegalArgumentException
	 * @throws JWTCreationException
	 * @throws UnsupportedEncodingException
	 */
	public static <T> String sign(T obj, long maxAge) throws Exception {
		JWTCreator.Builder bulider = JWT.create(); // header-头部信息
		bulider.withHeader(createHead()).withSubject(mapper.writeValueAsString(obj)); // payload-载荷
		if (maxAge >= 0) {
			long expMillis = System.currentTimeMillis() + maxAge;
			Date exp = new Date(expMillis);
			bulider.withExpiresAt(exp);
		}
		return bulider.sign(Algorithm.HMAC256(SECRET));
	}

	/**
	 * 解密
	 * 
	 * @param token
	 *            token字符�?
	 * @param classT
	 *            返回值的类型
	 * @return 返回的是未加密前的token
	 * @throws Exception
	 */
	public static <T> T unsign(String token, Class<T> classT) throws Exception {
		DecodedJWT jwt = getJwt(token);
		Date exp = jwt.getExpiresAt();
		if (exp != null && exp.after(new Date())) {
			String subject = jwt.getSubject();
			return mapper.readValue(subject, classT);
		}
		return null;
	}

	/**
	 * 获取DecodedJWT对象 解析JWT包含的东�?
	 * 
	 * @param token
	 * @return
	 * @throws IllegalArgumentException
	 * @throws UnsupportedEncodingException
	 */
	public static DecodedJWT getJwt(String token) throws IllegalArgumentException, UnsupportedEncodingException {
		JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
		DecodedJWT jwt = verifier.verify(token);
		return jwt;
	}

	public static int oddSum(int[] arr) {
		int sum = 0;
		for (int i = 0; i < arr.length; i++) {
			if (i % 2 != 0) {
				sum += arr[i];
			}
		}
		return sum;
	}

	public static void main(String[] args) {
		int[] arr = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

		int sum = oddSum(arr);

		System.out.println(sum);
	}

}
