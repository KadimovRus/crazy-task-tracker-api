package may.code.crazy.task.tracker.store.repositories;

import may.code.crazy.task.tracker.store.entities.TaskStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskStateEntityRepository extends JpaRepository<TaskStateEntity, Long> {
}
