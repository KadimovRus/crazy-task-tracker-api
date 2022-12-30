package may.code.crazy.task.tracker.api.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import may.code.crazy.task.tracker.api.dto.AckDto;
import may.code.crazy.task.tracker.api.dto.ProjectDto;
import may.code.crazy.task.tracker.api.exceptions.BadRequestException;
import may.code.crazy.task.tracker.api.exceptions.NotFoundException;
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

    public static final String FETCH_PROJECT = "/api/projects";
    public static final String CREATE_PROJECT = "/api/projects";
    public static final String EDIT_PROJECT = "/api/projects/{project_id}";
    public static final String DELETE_PROJECT = "/api/projects/{project_id}";

    @GetMapping(FETCH_PROJECT)
    public List<ProjectDto> fetchProject(@RequestParam(value = "prefix_name", required = false)Optional<String> optionalPrefixName) {

        optionalPrefixName = optionalPrefixName.filter(prefixName -> !prefixName.trim().isEmpty());

        Stream<ProjectEntity> projectStream = optionalPrefixName
                .map(projectRepository::streamAllByNameIgnoreCaseAndNameStartsWith)
                .orElseGet(projectRepository::streamAll);

        return projectStream
                .map(projectDtoFactory::makeProjectDto)
                .collect(Collectors.toList());
    }

    @PostMapping(CREATE_PROJECT)
    public ProjectDto createProject(@RequestParam String name) {

        projectRepository.findByName(name)
                .ifPresent(project -> {
                    throw new BadRequestException(String.format("Project with \"%s\" already exists", name));
                });

        ProjectEntity project = projectRepository.saveAndFlush(
                ProjectEntity
                        .builder()
                        .name(name)
                        .build()
        );

        return projectDtoFactory.makeProjectDto(project);
    }

    @PostMapping(EDIT_PROJECT)
    public ProjectDto editProject(@PathVariable("project_id") Long project_id,
                                  @RequestParam String name) {

        if(name.trim().isEmpty()) {
            throw new BadRequestException("Name can't be empty");
        }

        ProjectEntity projectEntity = projectRepository
                .findById(project_id)
                .orElseThrow(() -> {
                    return new NotFoundException(String.format("Project with \"s%\" does not exists", project_id));
                });

        projectRepository.findByName(name)
                .filter(anotherProject -> !Objects.equals(anotherProject.getId(), project_id))
                .ifPresent(anotherProject -> {
                    throw new BadRequestException(String.format("Project with name \"%s\" already exists", name));
                });

        projectEntity.setName(name);

        projectEntity = projectRepository.saveAndFlush(projectEntity);

        return projectDtoFactory.makeProjectDto(projectEntity);
    }

    @DeleteMapping(DELETE_PROJECT)
    public AckDto editPatch(@PathVariable Long project_id) {

        ProjectEntity project = projectRepository
                .findById(project_id)
                .orElseThrow(() -> new NotFoundException(String.format("Project with id \"%s\" not found", project_id)));
        projectRepository.deleteById(project_id);

        return  AckDto.makeDefault(true);
    }
}
