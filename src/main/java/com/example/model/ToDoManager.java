package com.example.model;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import com.example.exceptions.RecordNotFoundException;

public class ToDoManager {
	// DBの場所をフルパスで書きます
	private final String dbPath = "c:\\pleiades\\2024-03\\workspace\\todo-server\\jad.db";
	private final DAO dao = new DAO("jdbc:sqlite:" + dbPath);
	private final Logger logger = Logger.getLogger(ToDoManager.class.getName());

	public static final String NOT_FOUND_ERROR = "Not Found";
	public static final String INTERNAL_SERVER_ERROR = "Internal Server Error"; 
	public static final String INVALID_JSON_ERROR = "Invalid JSON";

	// 応答用のレコード
	public static interface Result { public String error(); }
	public record GetResult(List<ToDo> todos, String error) implements Result {}
	public record PostResult(ToDo todo, String error) implements Result  {}
	public record PutResult(ToDo todo, String error) implements Result  {}
	public record DeleteResult(int id, String error) implements Result {}

	public GetResult getTodos() {
		try {
			return new GetResult(dao.getAll(), null);
		} catch (Exception e) {
			logger.severe(e.getMessage());
			return new GetResult(null, INTERNAL_SERVER_ERROR);
		}
	}

	public PostResult postTodo(ToDo todoParams) {
		try {
			return new PostResult(
					dao.create(
							todoParams.title(),
							LocalDate.now(),
							todoParams.priority(),
							todoParams.completed()),
					null);
		} catch (Exception e) {
			logger.severe(e.getMessage());
			return new PostResult(null, INTERNAL_SERVER_ERROR);
		}
	}
	
	public PutResult putTitle(int id, String title) {
		try {
			return new PutResult(
					dao.updateTitle(id, title),
					null);
		} catch (RecordNotFoundException e) {
			logger.warning(e.getMessage());
			return new PutResult(null, NOT_FOUND_ERROR);
		} catch (Exception e) {
			logger.severe(e.getMessage());
			return new PutResult(null, INTERNAL_SERVER_ERROR);
		}
	}

	public PutResult putDate(int id, LocalDate date) {
		try {
			return new PutResult(
					dao.updateDate(id, date),
					null);
		} catch (RecordNotFoundException e) {
			logger.warning(e.getMessage());
			return new PutResult(null, NOT_FOUND_ERROR);
		} catch (Exception e) {
			logger.severe(e.getMessage());
			return new PutResult(null, INTERNAL_SERVER_ERROR);
		}
	}

	public PutResult putPriority(int id, int priority) {
		try {
			return new PutResult(
					dao.updatePriority(id, priority),
					null);
		} catch (RecordNotFoundException e) {
			logger.warning(e.getMessage());
			return new PutResult(null, NOT_FOUND_ERROR);
		} catch (Exception e) {
			logger.severe(e.getMessage());
			return new PutResult(null, INTERNAL_SERVER_ERROR);
		}
	}

	public PutResult putCompleted(int id, boolean completed) {
		try {
			return new PutResult(
					dao.updateCompleted(id, completed),
					null);
		} catch (RecordNotFoundException e) {
			logger.warning(e.getMessage());
			return new PutResult(null, NOT_FOUND_ERROR);
		} catch (Exception e) {
			logger.severe(e.getMessage());
			return new PutResult(null, INTERNAL_SERVER_ERROR);
		}
	}
	
	public DeleteResult delete(int id) {
		try {
			dao.delete(id);
			return new DeleteResult(id, null);
		} catch (RecordNotFoundException e) {
			logger.warning(e.getMessage());
			return new DeleteResult(id, NOT_FOUND_ERROR);
		} catch (Exception e) {
			logger.severe(e.getMessage());
			return new DeleteResult(id, INTERNAL_SERVER_ERROR);
		}
	}
}
