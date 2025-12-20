package khanghtse.com.projectmanagement.services;

import khanghtse.com.projectmanagement.dtos.TaskColumnDto;

import java.util.List;
import java.util.UUID;

public interface ITaskColumnService {

    List<TaskColumnDto.Response> getColumns(UUID projectId);

    TaskColumnDto.Response createColumn(UUID projectId, TaskColumnDto.CreateColumnRequest request);

    TaskColumnDto.Response updateColumn(UUID columnId, TaskColumnDto.UpdateColumnRequest request);

    TaskColumnDto.Response moveColumn(UUID columnId, TaskColumnDto.MoveColumnRequest request);

    void deleteColumn(UUID columnId);
}
