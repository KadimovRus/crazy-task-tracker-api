package may.code.crazy.task.tracker.api.controller.helpers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import may.code.crazy.task.tracker.api.exceptions.NotFoundException;
import may.code.crazy.task.tracker.store.entities.ProjectEntity;
import may.code.crazy.task.tracker.store.repositories.ProjectRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
@Transactional
public class ControllerHelper {

    ProjectRepository projectRepository;

    public ProjectEntity getProjectOrThrowException(Long project_id) {
        return projectRepository
                .findById(project_id)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format("Project with id \"%s\" not found",
                                        project_id
                                )
                        )
                );
    }
}
