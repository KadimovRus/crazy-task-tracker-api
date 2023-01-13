package may.code.crazy.task.tracker.api.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import may.code.crazy.task.tracker.api.controller.helpers.ControllerHelper;
import may.code.crazy.task.tracker.api.dto.TaskStateDto;
import may.code.crazy.task.tracker.api.exceptions.BadRequestException;
import may.code.crazy.task.tracker.api.factory.TaskStateDtoFactory;
import may.code.crazy.task.tracker.store.entities.ProjectEntity;
import may.code.crazy.task.tracker.store.entities.TaskStateEntity;
import may.code.crazy.task.tracker.store.repositories.TaskStateEntityRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@RestController
public class TaskStateController {

    TaskStateDtoFactory taskStateDtoFactory;
    TaskStateEntityRepository taskStateEntityRepository;

    ControllerHelper controllerHelper;

    public static final String GET_TASK_STATES = "/api/projects/{project_id}/task-states";
    public static final String CREATE_TASK_STATE = "/api/projects/{project_id}/task-states";
    public static final String DELETE = "";

    @GetMapping(GET_TASK_STATES)
    public List<TaskStateDto> getTaskStates(@PathVariable(name = "project_id") Long projectId) {

        ProjectEntity projectEntity = controllerHelper.getProjectOrThrowException(projectId);

        return projectEntity
                .getTaskStates()
                .stream()
                .map(taskStateDtoFactory::makeTaskStateDto)
                .collect(Collectors.toList());

    }

    @PostMapping(CREATE_TASK_STATE)
    public TaskStateDto createTaskState(@PathVariable Long project_id,
                                        @RequestParam(name = "task_state_name") String taskStateName) {

        if (taskStateName.isEmpty()) {
            throw  new BadRequestException("Task state name can't be empty.");
        }

        ProjectEntity project = controllerHelper.getProjectOrThrowException(project_id);
        project.getTaskStates()
                .stream()
                .map(TaskStateEntity::getName)
                .filter(anotherTaskStateName -> anotherTaskStateName.equalsIgnoreCase(taskStateName))
                .findAny()
                .ifPresent(it -> {
                    throw new BadRequestException(String.format("Task state with \"%s\" already exists.", taskStateName));
                });

        TaskStateEntity taskState = taskStateEntityRepository.saveAndFlush(TaskStateEntity.builder()
                .name(taskStateName).build());

        taskStateEntityRepository.findTaskStateEntityByRightTaskStateIdIsNullAndProjectId(project_id)
                .ifPresent(anotherTaskState -> {
                    taskState.setLeftTaskState(anotherTaskState);

                    anotherTaskState.setRightTaskState(taskState);
                    taskStateEntityRepository.saveAndFlush(anotherTaskState);
                });

        final TaskStateEntity savedTaskState = taskStateEntityRepository.saveAndFlush(taskState);

        return taskStateDtoFactory.makeTaskStateDto(savedTaskState);
    }

}
