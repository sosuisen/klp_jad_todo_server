package com.example.model;

import java.util.List;
import java.util.logging.Logger;

public class ToDoManager {
	private final Logger logger = Logger.getLogger(ToDoManager.class.getName());
	private final DAO dao;

	public static final String NOT_FOUND_ERROR = "Not Found";
	public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
	public static final String INVALID_JSON_ERROR = "Invalid JSON";

	// 応答用のレコード
	public interface Result {
		public String error();
	}
	public record GetResult(List<ToDo> todos, String error) implements Result {}
	public record PostResult(ToDo todo, String error) implements Result {}

	private ToDoManager(String dbPath) {
		dao = new DAO("jdbc:sqlite:" + dbPath);
	}

	private static class SingletonHolder {
		private static ToDoManager singleton;
	}

	public static ToDoManager getInstance(String dbPath) {
		if (SingletonHolder.singleton == null) {
			SingletonHolder.singleton = new ToDoManager(dbPath);
		}
		return SingletonHolder.singleton;
	}

	public GetResult getAll() {
		try {
			return new GetResult(dao.getAll(), null);
		} catch (Exception e) {
			logger.severe(e.getMessage());
			return new GetResult(null, INTERNAL_SERVER_ERROR);
		}
	}

	public PostResult post(ToDo todoParams) {
		try {
			return new PostResult(
					dao.create(
							todoParams.title(),
							todoParams.date(),
							todoParams.priority(),
							todoParams.completed()),
					null);
		} catch (Exception e) {
			logger.severe(e.getMessage());
			return new PostResult(null, INTERNAL_SERVER_ERROR);
		}
	}
}
