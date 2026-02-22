package com.sky.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;



public class JwtUtil {
    /**
     * 生成jwt
     * 使用Hs256算法,私钥使用固定密钥
     * @param secretKey jwt密钥
     * @param ttlMillis jwt有效期,单位毫秒
     * @param claims 设置的信息
     * return
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims){
        //指定签名的时候使用的签名算法，也就是header的部分
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        //生成JWT的时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);
        //设置jwt的body
        JwtBuilder builder = Jwts.builder()
                //如果有私有声明，则先设置这个私有声明，给builder的claim赋值，写在标准的声明赋值之后，覆盖标准的声明
               .setClaims(claims)
                //设置签名使用的签名算法和签名使用的密钥
               .signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8))
                //设置过期时间
        .setExpiration(exp);
        return builder.compact();
    }

    /**
     * Token解密
     * @param secretKey jwt密钥 此密钥一定要保留好在服务器，不能暴漏，否则sign就可以被伪造，对接多个客户端时可以造多个
     * @param token 加密后的token
     */
    public static Claims parseJWT(String secretKey, String token)
    {
        //得到DefaultJwtParser
        return Jwts.parser()
                //设置签名的秘钥
               .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                //设置需要解析的jwt
        .parseClaimsJws(token).getBody();
    }
}
