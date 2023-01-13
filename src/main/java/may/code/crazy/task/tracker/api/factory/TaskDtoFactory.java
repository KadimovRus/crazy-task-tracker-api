package may.code.crazy.task.tracker.api.factory;

import may.code.crazy.task.tracker.api.dto.TaskDto;
import may.code.crazy.task.tracker.store.entities.TaskEntity;
import org.springframework.scheduling.config.Task;

public class TaskDtoFactory {

    public TaskDto makeTaskDto(TaskEntity taskEntity) {
        return TaskDto.builder()
                .id(taskEntity.getId())
                .name(taskEntity.getName())
                .createdAt(taskEntity.getCreatedAt())
                .updatedAt(taskEntity.getUpdatedAt())
                .description(taskEntity.getDescription())
                .build();
    }
}
