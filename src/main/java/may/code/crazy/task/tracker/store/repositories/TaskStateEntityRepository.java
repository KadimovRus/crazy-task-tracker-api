package may.code.crazy.task.tracker.store.repositories;

import may.code.crazy.task.tracker.store.entities.TaskStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskStateEntityRepository extends JpaRepository<TaskStateEntity, Long> {

    Optional<TaskStateEntity> findTaskStateEntityByRightTaskStateIdIsNullAndProjectId(Long projectId);
}
