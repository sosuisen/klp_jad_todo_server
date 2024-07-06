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
	private final Jsonb jsonb = JsonbBuilder.create();
	private ToDoManager manager;

	@Override
	public void init() throws ServletException {
		super.init();
		var dbPath = getServletContext().getInitParameter("dbPath");
		manager = ToDoManager.getInstance(dbPath);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		var getResult = manager.getTodos();
		JsonResponder.getInstance().sendJson(response, HttpServletResponse.SC_OK, getResult);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PostResult postResult = null;
		try {
			var params = jsonb.fromJson(request.getInputStream(), ToDo.class);
			postResult = manager.postTodo(params);
		} catch (JsonbException e) {
			postResult = new PostResult(null, ToDoManager.INVALID_JSON_ERROR);
		}
		JsonResponder.getInstance().sendJson(response, HttpServletResponse.SC_CREATED, postResult);
	}
}
