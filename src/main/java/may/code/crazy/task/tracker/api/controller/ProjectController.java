package may.code.crazy.task.tracker.api.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import may.code.crazy.task.tracker.api.controller.helpers.ControllerHelper;
import may.code.crazy.task.tracker.api.dto.AckDto;
import may.code.crazy.task.tracker.api.dto.ProjectDto;
import may.code.crazy.task.tracker.api.exceptions.BadRequestException;
import may.code.crazy.task.tracker.api.factory.ProjectDtoFactory;
import may.code.crazy.task.tracker.store.entities.ProjectEntity;
import may.code.crazy.task.tracker.store.repositories.ProjectRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class ProjectController {

    ProjectDtoFactory projectDtoFactory;
    ProjectRepository projectRepository;
    ControllerHelper controllerHelper;

    public static final String FETCH_PROJECT = "/api/projects";
    public static final String CREATE_PROJECT = "/api/projects";
    public static final String EDIT_PROJECT = "/api/projects/{project_id}";
    public static final String DELETE_PROJECT = "/api/projects/{project_id}";

    public static final String CREATE_OR_UPDATE_PROJECT = "/api/projects";

    @GetMapping(FETCH_PROJECT)
    public List<ProjectDto> fetchProject(@RequestParam(value = "prefix_name", required = false)Optional<String> optionalPrefixName) {

        optionalPrefixName = optionalPrefixName.filter(prefixName -> !prefixName.trim().isEmpty());

        Stream<ProjectEntity> projectStream = optionalPrefixName
                .map(projectRepository::streamAllByNameStartsWithIgnoreCase)
                .orElseGet(projectRepository::streamAllBy);

        return projectStream
                .map(projectDtoFactory::makeProjectDto)
                .collect(Collectors.toList());
    }

    @PutMapping(CREATE_OR_UPDATE_PROJECT)
    public ProjectDto createOrUpdateProject(@RequestParam(value = "project_id", required = false) Optional<Long> optionalId,
                                            @RequestParam(value = "project_name", required = false) Optional<String> optionalProjectName) {

        boolean isCreated = optionalId.isEmpty();

        optionalProjectName = optionalProjectName.filter(projectName -> !projectName.trim().isEmpty());

        if (isCreated && optionalProjectName.isEmpty()) {
            throw new BadRequestException("Project name can't be empty");
        }

        ProjectEntity projectEntity = optionalId
                .map(controllerHelper::getProjectOrThrowException)
                .orElseGet(() -> ProjectEntity.builder().build());

        optionalProjectName.ifPresent(projectName -> {
                projectRepository
                        .findByName(projectName)
                        .filter(anotherProject -> !Objects.equals(anotherProject.getId(), projectEntity.getId()))
                        .ifPresent(anotherProject -> {
                            throw new BadRequestException(String.format("Project \"%s\" already exists.", projectName));
                        });
                projectEntity.setName(projectName);
        });

        final ProjectEntity savedProject = projectRepository.saveAndFlush(projectEntity);

        return projectDtoFactory.makeProjectDto(savedProject);
    }


    @DeleteMapping(DELETE_PROJECT)
    public AckDto editPatch(@PathVariable Long project_id) {

        ProjectEntity project = controllerHelper.getProjectOrThrowException(project_id);
        projectRepository.deleteById(project_id);

        return  AckDto.makeDefault(true);
    }

}
