package khanghtse.com.projectmanagement.services;

import khanghtse.com.projectmanagement.dtos.*;

import java.util.UUID;

public interface ITaskService {
    BoardResponse getBoard(UUID projectId);
    BoardResponse getBoard(UUID projectId, String keyword, String priority, Boolean isMyTask, String currentUserEmail);
    TaskResponse createTask(UUID projectId, CreateTaskRequest request, String userEmail);
    TaskResponse moveTask(UUID taskId, MoveTaskRequest request);
    TaskResponse updateTask(UUID taskId, UpdateTaskRequest request);
    void deleteTask(UUID taskId);
    SubTaskResponse createSubTask(UUID parentTaskId, String title);
    SubTaskResponse toggleSubTask(UUID subTaskId);
    void deleteSubTask(UUID subTaskId);
    TaskResponse getTask(UUID taskId);
}
