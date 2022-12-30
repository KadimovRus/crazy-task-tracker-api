package may.code.crazy.task.tracker.api.factory;

import may.code.crazy.task.tracker.api.dto.TaskStateDto;
import may.code.crazy.task.tracker.store.entities.TaskStateEntity;

public class TaskStateFactory {

    public TaskStateDto makeTaskStateDto(TaskStateEntity entity) {
        return TaskStateDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .ordinal(entity.getOrdinal())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
