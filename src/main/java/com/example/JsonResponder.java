package com.example;

import java.io.IOException;

import com.example.model.ToDoManager;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.http.HttpServletResponse;

public class JsonResponder {
	public static Jsonb jsonb = JsonbBuilder.create();
	
	private static JsonResponder instance = new JsonResponder();
	private JsonResponder() {}
	public static JsonResponder getInstance() {
		return instance;
	}	
	
	public void sendJson(HttpServletResponse response, int successCode, ToDoManager.Result result) throws JsonbException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		response.setStatus(getStatusCode(successCode, result.error()));
		response.getWriter().write(jsonb.toJson(result));
	}
	
	private int getStatusCode(int successCode, String error) {
		return switch (error) {
			case null -> successCode;
			case ToDoManager.INVALID_JSON_ERROR -> HttpServletResponse.SC_BAD_REQUEST;
			case ToDoManager.NOT_FOUND_ERROR -> HttpServletResponse.SC_NOT_FOUND;
			default -> HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		};
	}
}
