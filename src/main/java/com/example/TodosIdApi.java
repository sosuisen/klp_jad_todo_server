package com.example;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.example.model.ToDo;
import com.example.model.ToDoManager;
import com.example.model.ToDoManager.PutResult;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TodosIdApi extends HttpServlet {
	private final Jsonb jsonb = JsonbBuilder.create();
	private final Logger logger = Logger.getLogger(TodosIdApi.class.getName());
	private ToDoManager manager;

	@Override
	public void init() throws ServletException {
		super.init();
		var dbPath = getServletContext().getInitParameter("dbPath");
		manager = ToDoManager.getInstance(dbPath);
	}

	// 「/api/todos」より後ろのパスとマッチ
	private final Pattern PUT_PATTERN = Pattern.compile("^/(\\d+)/(title|date|priority|completed)$");

	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getPathInfo();
		logger.info("PUT: " + path);

		PutResult putResult;				
		try {
			var params = jsonb.fromJson(new InputStreamReader(request.getInputStream()), ToDo.class);
			var mat = PUT_PATTERN.matcher(path);
			if (mat.matches()) {
				var id = Integer.parseInt(mat.group(1));
				var fieldName = mat.group(2);
				putResult = manager.putToDoField(id, fieldName, params);
			} else {
				putResult = new PutResult(null, ToDoManager.NOT_FOUND_ERROR);
			}
		} catch (JsonbException e) {
			putResult = new PutResult(null, ToDoManager.INVALID_JSON_ERROR);
		}
		JsonResponder.getInstance().sendJson(response, HttpServletResponse.SC_OK, putResult);
	}
}
