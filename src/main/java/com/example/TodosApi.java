package com.example;

import java.io.IOException;

import com.example.model.ToDo;
import com.example.model.ToDoManager;
import com.example.model.ToDoManager.PostResult;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TodosApi extends HttpServlet {
	private final ToDoManager manager = new ToDoManager();
	private final Jsonb jsonb = JsonbBuilder.create();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		var getResult = manager.getTodos();
		JsonResponder.getInstance().sendJson(response, HttpServletResponse.SC_OK, getResult);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ToDo params;
		try {
			params = jsonb.fromJson(request.getInputStream(), ToDo.class);
		} catch (JsonbException e) {
			params = null;
		}		
		PostResult postResult;
		if (params == null) {
			postResult = new PostResult(null, ToDoManager.INVALID_JSON_ERROR);
		}
		else {
			postResult = manager.postTodo(params);
		}
		JsonResponder.getInstance().sendJson(response, HttpServletResponse.SC_CREATED, postResult);
	}
}
