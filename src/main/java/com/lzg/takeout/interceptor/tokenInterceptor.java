package com.lzg.takeout.interceptor;

import com.lzg.takeout.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
@Slf4j
public class tokenInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;
    private final StringRedisTemplate redisTemplate;

    // TTL in hours for token/info refresh - keep consistent with LoginServiceImpl
    private static final long TTL_HOURS = 12L;
    Logger logger = Logger.getLogger(tokenInterceptor.class.getName());
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        logger.info("前端请求：" + request.getRequestURI());
        logger.info("前端token: " + request.getHeader("Authorization"));
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return true; // nothing to do
        }

        String token = header.substring(7);
        if (!jwtUtils.isTokenValid(token)) {
            return true; // invalid token, let filter/security handle it
        }

        String username = jwtUtils.getUsername(token);
        if (username == null) {
            return true;
        }

        // store parsed info in redis: key = auth:info:{token} -> username
        String infoKey = "auth:info:" + token;
        redisTemplate.opsForValue().set(infoKey, username, Duration.ofHours(TTL_HOURS));

        logger.info("通过拦截器，存入redis key=" + infoKey + ", value=" + username);
        return true;
    }
}
