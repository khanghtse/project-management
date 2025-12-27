package khanghtse.com.projectmanagement.services;

import khanghtse.com.projectmanagement.dtos.*;

import java.util.List;
import java.util.UUID;

public interface ITaskService {
    BoardResponse getBoard(UUID projectId);
    BoardResponse getBoard(UUID projectId, String keyword, String priority, Boolean isMyTask, String currentUserEmail);
    TaskResponse createTask(UUID projectId, CreateTaskRequest request, String userEmail);
    TaskResponse moveTask(UUID taskId, MoveTaskRequest request, String userEmail);
    TaskResponse updateTask(UUID taskId, UpdateTaskRequest request, String userEmail);
    void deleteTask(UUID taskId);
    SubTaskResponse createSubTask(UUID parentTaskId, String title);
    SubTaskResponse toggleSubTask(UUID subTaskId);
    void deleteSubTask(UUID subTaskId);
    TaskResponse getTask(UUID taskId);
    List<ActivityLogDto.Response> getActivityLogs(UUID taskId);
}
