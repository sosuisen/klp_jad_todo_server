package com.example;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.example.model.ToDo;
import com.example.model.ToDoManager;
import com.example.model.ToDoManager.DeleteResult;
import com.example.model.ToDoManager.PutResult;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TodosIdApi extends HttpServlet {
	private final ToDoManager manager = new ToDoManager();
	private final Jsonb jsonb = JsonbBuilder.create();
	private final Logger logger = Logger.getLogger(TodosIdApi.class.getName());

	// 「/api/todos」より後ろのパスとマッチ
	private static final Pattern PUT_PATTERN = Pattern.compile("^/(\\d+)/(title|date|priority|completed)?$");
	private static final Pattern DELETE_PATTERN = Pattern.compile("^/(\\d+)$");

	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getPathInfo();
		logger.info("PUT: " + path);

		ToDo params;
		try {
			params = jsonb.fromJson(new InputStreamReader(request.getInputStream()), ToDo.class);
		} catch (JsonbException e) {
			params = null;
		}
		PutResult putResult;		
		var mat = PUT_PATTERN.matcher(path);
		if (params == null) {
			putResult = new PutResult(null, ToDoManager.INVALID_JSON_ERROR);
		} else if (mat.matches()) {
			var id = Integer.parseInt(mat.group(1));
			var fieldName = mat.group(2);

			putResult = switch (fieldName) {
				case "title" -> manager.putTitle(id, params.title());
				case "date" -> manager.putDate(id, params.date());
				case "priority" -> manager.putPriority(id, params.priority());
				case "completed" -> manager.putCompleted(id, params.completed());
				default -> new PutResult(null, ToDoManager.NOT_FOUND_ERROR);
			};
		} else {
			putResult = new PutResult(null, ToDoManager.NOT_FOUND_ERROR);
		}
		JsonResponder.getInstance().sendJson(response, HttpServletResponse.SC_OK, putResult);
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getPathInfo();
		logger.info("DELETE: " + path);

		DeleteResult deleteResult;
		var mat = DELETE_PATTERN.matcher(path);
		if (mat.matches()) {
			var id = Integer.parseInt(mat.group(1));
			deleteResult = manager.delete(id);
		} else {
			deleteResult = new DeleteResult(-1, ToDoManager.NOT_FOUND_ERROR);
		}
		JsonResponder.getInstance().sendJson(response, HttpServletResponse.SC_OK, deleteResult);
	}
}
