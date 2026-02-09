package com.employee_management_system.api_gateway.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered{
	
	private final JwtUtil jwtUtil;
	
	public JwtAuthenticationFilter(JwtUtil jwtUtil) {
		this.jwtUtil=jwtUtil;
	}
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// TODO Auto-generated method stub
		
		ServerHttpRequest request = exchange.getRequest();
		
		//ALLOW CORS PREFLIGHT
	    if (request.getMethod() == HttpMethod.OPTIONS) {
	        exchange.getResponse().setStatusCode(HttpStatus.OK);
	        return exchange.getResponse().setComplete();
	    }
	    
		String path = request.getURI().getPath();
		
		//public endpoints
		if(path.contains("/api/v1/auth") || path.contains("/employee/test")) {
		    return chain.filter(exchange);
		}
		
		//check authorization headers
		if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
			return unauthorized(exchange, "Missing Authorization Header");
		}
		
		String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		if(!authHeader.startsWith("Bearer ")) {
			return unauthorized(exchange, "Invalid Authorization Header");
		}
		
		String token = authHeader.substring(7);
		System.out.println(token);
		
		//validate jwt
		if(!jwtUtil.isTokenValid(token)) {
			System.out.println("not accesable jwt");
			return unauthorized(exchange, "Invalid or Token expired");
		}
		
		//extract role
		String roles= jwtUtil.extractRole(token);
        System.out.println("Role: "+roles);
        
        
		//role-based checks
		if(path.startsWith("/employee/add") && !roles.equals("ADMIN")) {
			return unauthorized(exchange, "Access denied for non-ADMIN");
		}
		
		if (path.startsWith("/employee/delete/") && !roles.equals("ADMIN")) {
	        return unauthorized(exchange, "Access denied for non-ADMIN");
	    }

	    if (path.startsWith("/employee/update/") && !roles.equals("ADMIN")) {
	        return unauthorized(exchange, "Access denied for non-ADMIN");
	    }
		
	    if (path.equals("/employee/getAll") && !(roles.equals("HR") || roles.equals("ADMIN"))) {
	        return unauthorized(exchange, "Access denied for non-HR/Admin");
	    }
	    
	    if ((path.startsWith("/employee/getEmployeeById/") 
	            || path.startsWith("/employee/profile/") 
	            || path.startsWith("/employee/user/")) 
	            && !roles.equals("EMPLOYEE")) {
	           return unauthorized(exchange, "Access denied for non-EMPLOYEE");
	       }
		
		//forward with headers
        String username = jwtUtil.extractEmail(token);
        Integer userId = jwtUtil.extractUserId(token);
		ServerHttpRequest mutedRequest = exchange.getRequest()
				.mutate()
				.header("X-User-Name", username)
				.header("X-User-Roles", roles)
				.header("X-User-Id", String.valueOf(userId))
				.build();
		
		return chain.filter(exchange.mutate().request(mutedRequest).build());
	}
	
	private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
		// TODO Auto-generated method stub
		exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
	    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

	    Map<String, String> response = new HashMap<>();
	    response.put("message", message);

	    try {
	        byte[] bytes = new ObjectMapper().writeValueAsBytes(response);
	        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
	                .bufferFactory().wrap(bytes)));
	    } catch (Exception e) {
	        return exchange.getResponse().setComplete();
	    }
	}

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return -1;	//high priority
	}

}

